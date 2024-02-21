package src.master;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import src.gpx.Session;
import src.gpx.SessionMetadata;
import src.gpx.Tags;
import src.gpx.Waypoint;
import src.utils.Utilities;
import src.utils.requests.Request;
import src.utils.requests.UserDataRequest;
import src.utils.requests.XMLProcessingRequest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

class Handler implements Runnable {
    private final Socket userSocket;
    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;
    private final Queue<Socket> availableWorkers;

    private final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    public Handler(final Socket userSocket, final Queue<Socket> availableWorkers) throws IOException, ParserConfigurationException {
        this.userSocket = userSocket;
        this.availableWorkers = availableWorkers;

        oos = new ObjectOutputStream(userSocket.getOutputStream());
        ois = new ObjectInputStream(userSocket.getInputStream());
    }

    @Override
    public void run() {
        System.err.println("Master running job " + Thread.currentThread().getName());
        try (userSocket) {
            Request request = (Request) ois.readObject();
            System.err.println("Master handling request with id " + request.getID());
            switch (request.getID()) {
                case Request.RequestID.REQUEST_XML_PROCESSING ->
                        handleXMLProcessingRequest((XMLProcessingRequest) request);
                case Request.RequestID.REQUEST_USER_DATA -> handleUserDataRequest((UserDataRequest) request);
                default -> handleUnknownRequest(request.getID());
            }
        } catch (IOException | SAXException | ParseException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleXMLProcessingRequest(XMLProcessingRequest request) throws IOException, SAXException, ParseException {
        System.err.println("Master handling xml processing request");
        System.err.println("Master parsing with document builder...");
        // https://stackoverflow.com/a/1706533
        Document d = db.parse(new InputSource(new StringReader(request.getBody())));

        Session userSession = processDocument(d);
        Session[] subsessions = userSession.splitSessionsEqually(availableWorkers.size());

        map(subsessions);
        SessionMetadata metadata = reduce(subsessions);

        System.err.println("Master reduced to metadata " + metadata);
        userSession.setMetadata(metadata);
        UserSingleton.getInstance().addUser(userSession.getUser(), userSession);

        // return data to user
        oos.writeObject(userSession);
    }

    private void map(Session[] subsessions) throws IOException {
        System.err.println("Master mapping to " + availableWorkers.size() + " available workers");
        for (var subsession : subsessions) {
            availableWorkers.add(dispatchSubsessionForHandling(subsession));
        }
    }

    private Socket dispatchSubsessionForHandling(Session session) throws IOException {
        Socket s = availableWorkers.poll();
        ObjectOutputStream workerOos = new ObjectOutputStream(Objects.requireNonNull(s).getOutputStream());
        workerOos.writeObject(session);
        return s;
    }

    private SessionMetadata reduce(Session[] subsessions) {
        SessionMetadata[] subsessions_metadata = new SessionMetadata[subsessions.length];

        int c = 0;
        while (availableWorkers.size() > 0) {
            try (Socket workerSocket = availableWorkers.poll()) {
                ObjectInputStream workerOis = new ObjectInputStream(workerSocket.getInputStream());
                subsessions_metadata[c++] = (SessionMetadata) workerOis.readObject();
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        return Utilities.averageSubsessionsMetadata(subsessions_metadata);
    }

    private Session processDocument(Document d) throws ParseException {
        System.err.println("Master processing document...");
        NodeList gpxs = d.getElementsByTagName(Tags.GPX_TAG);
        Session ac = new Session(
                // from the first gpx (there should be only one), get from the attributes the named item
                //  CREATOR_ATTR, and afterwards it's value (the gpx creator's name)
                gpxs.item(0).getAttributes().getNamedItem(Tags.CREATOR_ATTR).getNodeValue()
        );

        NodeList wpts = d.getElementsByTagName(Tags.WPT_TAG);

        for (int i = 0; i < wpts.getLength(); i++) {
            Node item = wpts.item(i);
            NamedNodeMap attributes = item.getAttributes();
            String ele = "";
            String time = "";

            NodeList childNodes = item.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                if (childNodes.item(j).getNodeName().equals(Tags.ELE_TAG)) ele = childNodes.item(j).getTextContent();
                if (childNodes.item(j).getNodeName().equals(Tags.TIME_TAG)) time = childNodes.item(j).getTextContent();
            }

            ac.addWaypoint(
                    new Waypoint(
                            Double.parseDouble(attributes.getNamedItem(Tags.LAT_ATTR).getNodeValue()), // lat
                            Double.parseDouble(attributes.getNamedItem(Tags.LON_ATTR).getNodeValue()), // lon
                            Float.parseFloat(ele), // ele
                            time // datetime
                    )
            );
        }

        return ac;
    }

    private void handleUserDataRequest(UserDataRequest request) throws IOException {
        System.err.println("Master handling user data request");
        UserSingleton instance = UserSingleton.getInstance();
        // this is the average total metadata of the requesting user
        SessionMetadata userTotalMetadata = Utilities.averageSubsessionsMetadata(getAllMetadataFromUser(request.getBody(), instance));

        // we just need to average out all the data from the existing sessions
        SessionMetadata allTotalMetadata = totalUserSessionMetadata(instance);

        oos.writeObject(userTotalMetadata);
        oos.writeObject(allTotalMetadata);

    }

    private SessionMetadata totalUserSessionMetadata(UserSingleton instance) {
        var keyList = instance.keyList();
        SessionMetadata[] mtd = new SessionMetadata[keyList.size()];
        for (int i = 0; i < keyList.size(); i++) {
            mtd[i] = Utilities.averageSubsessionsMetadata(getAllMetadataFromUser(keyList.get(i), instance));
        }

        return Utilities.averageSubsessionsMetadata(mtd);
    }

    private SessionMetadata[] getAllMetadataFromUser(String u, UserSingleton instance) {
        List<Session> sessionList = instance.getUserSessions(u);
        SessionMetadata[] sessionMetadata = new SessionMetadata[sessionList.size()];

        for (int i = 0; i < sessionList.size(); i++) {
            sessionMetadata[i] = sessionList.get(i).getMetadata();
        }
        return sessionMetadata;
    }

    private void handleUnknownRequest(byte id) throws IOException {
        System.err.println("Master unknown id " + id + " closing...");
        for (var s : availableWorkers) {
            s.close();
        }
        userSocket.close();
        throw new RuntimeException("Master unknown request id " + id);
    }
}
