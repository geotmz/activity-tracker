package src.master;

import src.utils.SimpleQueue;
import src.utils.Utilities;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

public class Master {

    private final int workerCount;
    private final InetAddress bCastAddress = InetAddress.getByName(Utilities.DISCOVERY_IP);
    private final InetAddress localhostAddress = InetAddress.getByName(Utilities.LOCAL_DISCOVERY_IP);
    private final ServerSocket userServerSocket;
    private final ServerSocket localServerSocket;

    public Master(int workerCount) throws IOException {
        this.workerCount = workerCount;

        userServerSocket = new ServerSocket(Utilities.DISCOVERY_PORT, 1000, bCastAddress);
        localServerSocket = new ServerSocket(Utilities.LOCAL_DISCOVERY_PORT, workerCount, localhostAddress);
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException {
        Master master = new Master(Integer.parseInt(args[0]));
        master.run();
    }

    public void run() throws IOException, ParserConfigurationException {
        while (true) {
            // this is blocking, which means workers will not be able to create a new connection
            //  without us having received a new job from a user first
            startJob(acceptUser(), acceptWorkers());
        }
    }

    private void startJob(final Socket user, final Queue<Socket> workers) throws IOException, ParserConfigurationException {
        (new Thread(new Handler(user, workers))).start();
    }

    private Socket acceptUser() throws IOException {
        System.err.println("Master waiting for users to serve...");
        return userServerSocket.accept();
    }

    private Queue<Socket> acceptWorkers() throws IOException {
        final Queue<Socket> workers = new SimpleQueue<>();

        System.err.println("Master waiting to accept " + workerCount + " workers...");
        for (int i = 0; i < workerCount; ++i) {
            workers.add(localServerSocket.accept());
            System.err.println("Master accepted worker on port " + localServerSocket.getLocalPort());
        }

        // after initializing all workers, do not close the server socket
        //  as it'll be needed to setup next queue of sessions
        return workers;
    }
}
