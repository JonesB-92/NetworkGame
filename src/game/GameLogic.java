package game;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GameLogic {
    public static List<Player> players = java.util.Collections.synchronizedList(new ArrayList<>());

    public static Pair coinPos = null;

    public static Player makePlayers(String name, DataOutputStream dataOutputStream) throws IOException {
        Pair p = getRandomFreePosition();
        Player me = new Player(name, p, "up");
        players.add(me);
        me.setDataOutputStream(dataOutputStream);
        updatePlayer(me, 0, 0, "up");

        /** COIN */
        if (coinPos == null) {
            coinPos = getRandomFreePosition();
        }

        updateCoin(me);

        return me;
    }

    public static Pair getRandomFreePosition() {
        int x = 1;
        int y = 1;
        boolean foundfreepos = false;
        while (!foundfreepos) {
            Random r = new Random();
            x = Math.abs(r.nextInt() % 18) + 1;
            y = Math.abs(r.nextInt() % 18) + 1;
            if (y < Generel.board.length && x < Generel.board[y].length()) {
                if (Generel.board[y].charAt(x) == ' ') // er det gulv ?
                {
                    foundfreepos = true;
                    for (Player p : players) {
                        if (p.getXpos() == x && p.getYpos() == y) //pladsen optaget af en anden
                            foundfreepos = false;
                    }
                    if (
                        coinPos != null &&
                        coinPos.getX() == x &&
                        coinPos.getY() == y
                    ) {
                            foundfreepos = false;
                    }
                }
            }
        }
        Pair p = new Pair(x, y);
        return p;
    }

    public static void updatePlayer(Player me, int delta_x, int delta_y, String direction) throws IOException {
        me.direction = direction;
        int oldX = me.getXpos();
        int oldY = me.getYpos();
        int newX = oldX + delta_x;
        int newY = oldY + delta_y;

        if (Generel.board[newY].charAt(newX) == 'w') {
            me.addPoints(-1);
        } else {
            Player other = getPlayerAt(newX, newY);
            if (other != null && other != me) {
                me.addPoints(10);
                other.addPoints(-10);
                other.setLocation(getRandomFreePosition());
                me.setLocation(new Pair(newX, newY));
            } else {
                me.addPoints(1);
                me.setLocation(new Pair(newX, newY));
            }

            if (coinPos != null && me.getXpos() == coinPos.getX() && me.getYpos() == coinPos.getY()) {
                me.addPoints(100);
                coinPos = getRandomFreePosition();
                sendCoin();
            }
        }
        sendPlayers();
    }

    public static Player getPlayerAt(int x, int y) {
        for (Player p : players) {
            if (p.getXpos() == x && p.getYpos() == y) {
                return p;
            }
        }
        return null;
    }

    public static void sendCoin() throws IOException {
        synchronized (players) {
            for (Player player : players) {
                updateCoin(player);
            }
        }
    }

    // Hjælpemetode
    private static void updateCoin(Player p) throws IOException {
        if (coinPos != null && p.getDataOutputStream() != null) {
            p.getDataOutputStream().writeBytes("COIN:" + coinPos.getX() + ":" + coinPos.getY() + ":" + p.getDirection() + "\n");
            sendPlayers();
            p.getDataOutputStream().flush();
        }
    }

    public static void sendPlayers() throws IOException {
        synchronized (players) {
            String playersToString = "";
            for (Player player : players) {
                playersToString +=
                        player.getName() + ":" +
                                player.getLocation().getX() + ":" +
                                player.getLocation().getY() + ":" +
                                player.getDirection() + ":" +
                                player.point + "#";
            }

            System.out.println("Sendplayers: " + playersToString);

            for (Player player : players) {
                DataOutputStream outputStream = player.getDataOutputStream();
                outputStream.writeBytes(playersToString + "\n");
                outputStream.flush();
            }
        }
    }
}
