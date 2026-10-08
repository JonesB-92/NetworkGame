package game;

import java.io.*;
import java.net.Socket;

import javafx.application.Application;

public class App {

    static ClientThread clientThread;

    public static void main(String[] args) throws Exception {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Indtast spillernavn");
        String name = inFromUser.readLine();

//        Socket serverSocket = new Socket("10.10.139.15", 6789);
        Socket serverSocket = new Socket("localhost", 6789);
        clientThread = new ClientThread(serverSocket);
        clientThread.start();
        System.out.println("Client started");

        clientThread.sendCommand(name);

        Application.launch(Gui.class);

    }
}