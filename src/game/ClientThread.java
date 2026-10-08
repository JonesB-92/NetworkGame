package game;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientThread extends Thread {
    //Client
    public static List<Player> players;
    Socket clientSocket;

    /** COIN */
    public Pair currentCoin = null;

    //Server
    BufferedReader inFromServer;
    DataOutputStream outToServer;

    public ClientThread(Socket socket) throws IOException {
        this.clientSocket = socket;
        this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.outToServer = new DataOutputStream(clientSocket.getOutputStream());
    }

    @Override
    public void run() {
        players = new ArrayList<>();

        try {
            this.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                String line = inFromServer.readLine();
                if (line == null) break;

                if (line.startsWith("COIN:")) {
                    receiveCoin(line);
                } else {
                    receivePlayers(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void sendCommand(String command) {
        System.out.println("Client wants to: " + command);
        try {
            outToServer.writeBytes(command + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void receiveCoin(String line) {
        Platform.runLater(() -> {

            if (line == null) return;

            String[] parts = line.split(":");
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            Pair p = new Pair(x, y);

            currentCoin = p;
            Gui.placeCoinOnScreen(currentCoin);

        });
    }


    public static void receivePlayers(String string) throws IOException {
        Platform.runLater(() -> {
            String playersString = string;
            if (playersString == null) return;

            /** Normal gang */
            for (Player p : players) {
                Gui.removePlayerOnScreen(p.getLocation());
            }

            players.clear();
            String[] playersAsPlayers = playersString.split("#");

            for (String playerRaw : playersAsPlayers) {
                if (playerRaw.isEmpty()) continue;
                String[] playerData = playerRaw.split(":");

                if (playerData.length >= 5) {
                    String name = playerData[0];
                    int x = Integer.parseInt(playerData[1]);
                    int y = Integer.parseInt(playerData[2]);
                    String direction = playerData[3];
                    int point = Integer.parseInt(playerData[4]);

                    Player player = new Player(name, new Pair(x, y), direction);
                    player.addPoints(point);
                    players.add(player);
                }
            }

            for (Player p : players) {
                Gui.placePlayerOnScreen(p.getLocation(), p.getDirection());
            }

            Gui.updateScoreTable();
        });
    }
}

