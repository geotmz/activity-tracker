package src;

import src.gpx.Session;
import src.gpx.SessionMetadata;
import src.utils.Utilities;
import src.utils.requests.Request;
import src.utils.requests.UserDataRequest;
import src.utils.requests.XMLProcessingRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MockClient {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        switch (Integer.parseInt(args[0])) {
            case 0 -> sendData(args[1]);
            case 1 -> requestData(args[1]);
            default -> throw new RuntimeException("Unknown command int");
        }
    }

    private static void sendData(String fname) throws IOException, ClassNotFoundException {
        var req = new XMLProcessingRequest(fname);
        System.err.println("Mock User read xml");

        Session[] ses = new Session[1];
        writeReadRequest(req, ses);
        System.err.println("Mock client received " + ses[0]);
    }

    private static void requestData(String username) throws IOException, ClassNotFoundException {
        SessionMetadata[] smd = new SessionMetadata[2];
        writeReadRequest(new UserDataRequest(username), smd);
        for (var sd : smd) {
            System.err.println("Mock client received " + sd);
        }
    }

    private static void writeReadRequest(Request req, Object[] toReturn) throws IOException, ClassNotFoundException {
        Socket s = new Socket(Utilities.DISCOVERY_IP, Utilities.DISCOVERY_PORT);
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

        System.err.println("Mock User writing " + req);

        oos.writeObject(req);
        System.err.println("Mock User sent");

        for (int i = 0; i < toReturn.length; i++) {
            toReturn[i] = ois.readObject();
        }
    }
}
