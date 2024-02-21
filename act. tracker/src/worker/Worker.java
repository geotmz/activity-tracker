package src.worker;

import src.utils.Utilities;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Worker {

    private final InetAddress localhostAddress = InetAddress.getByName(Utilities.LOCAL_DISCOVERY_IP);

    public Worker() throws UnknownHostException {

    }

    public static void main(String[] args) {
        try {
            Worker worker = new Worker();
            worker.run();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void run() throws IOException {
        while (true) {
            handleJob(connect());
        }
    }

    private Socket connect() throws IOException {
        // due to how the master works, this blocks until a new user job from the master is accepted
        System.err.println("Worker waiting for master to accept...");
        return new Socket(localhostAddress, Utilities.LOCAL_DISCOVERY_PORT);
    }

    private void handleJob(Socket s) throws IOException {
        System.err.println("Worker starting job...");
        (new Thread(new Handler(s))).start();
    }
}
