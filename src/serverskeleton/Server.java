package serverskeleton;

import java.net.*;

public class Server {

    public static void main(String[] args) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(6789);

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("Player connected");
            (new ServerThread(connectionSocket)).start();
            System.out.println("Serverthread started");
        }
    }
}
