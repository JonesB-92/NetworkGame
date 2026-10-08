package serverskeleton;

import game.GameLogic;
import game.Player;
import java.net.*;
import java.io.*;

public class ServerThread extends Thread{
	Socket connSocket;
	
	public ServerThread(Socket connSocket) {
		this.connSocket = connSocket;
	}

	public void run() {
		try {
            //Client
            DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));

            System.out.println("Running");

            String name = inFromClient.readLine();
            System.out.println(name);

            System.out.println((name + " is connected\n"));

            Player player = GameLogic.makePlayers(name, outToClient);
            player.setDataOutputStream(outToClient);

            System.out.println("Player created: " + player);

            while (true) {
                String clientCommand = inFromClient.readLine();
                if (clientCommand == null) break;
                System.out.println(clientCommand + " received");
                handleCommand(clientCommand, player);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleCommand(String command, Player player) throws IOException {
        command = command.trim().toUpperCase();
        String[] commandData = command.split(":");

        switch (commandData[0]) {
            case "UP":
                System.out.println(player +  " walked up");
                GameLogic.updatePlayer(player, 0, -1, "up");
                break;
            case "DOWN":
                System.out.println(player +  " walked down");
                GameLogic.updatePlayer(player, 0, 1, "down");
                break;
            case "LEFT":
                System.out.println(player +  " walked left");
                GameLogic.updatePlayer(player, -1, 0, "left");
                break;
            case "RIGHT":
                System.out.println(player +  " walked right");
                GameLogic.updatePlayer(player, 1, 0, "right");
                break;
            case "COIN":
                System.out.println("Coin collected");
                // Update coin position on everyone's screen to the same new position
                GameLogic.sendCoin();

            default:
                System.out.println("Unknown command from client: " + command);
                break;
        }
    }
}
