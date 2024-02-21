package src.worker;

import src.gpx.Session;
import src.gpx.SessionMetadata;
import src.utils.Utilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class Handler implements Runnable {
    private final Socket masterSocket;
    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;

    public Handler(Socket s) throws IOException {
        masterSocket = s;

        ois = new ObjectInputStream(masterSocket.getInputStream());
        oos = new ObjectOutputStream(masterSocket.getOutputStream());
    }

    @Override
    public void run() {
        System.err.println("Worker running job " + Thread.currentThread().getName());
        try (masterSocket) {
            // if we want to extend the functionality of the  worker, we can read an int from the master in order
            //  to know which directive to execute (as of writing, there is only one)
            Session subsession = (Session) ois.readObject();
            SessionMetadata metadata = Utilities.calculateMetrics(subsession);
            oos.writeObject(metadata);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
