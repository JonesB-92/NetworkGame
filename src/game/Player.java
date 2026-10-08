package game;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Player {
    String name;
    Pair location;
    int point;
    String direction;
    DataOutputStream dataOutputStream;

    public Player(String name, Pair location, String direction) {
        this.name = name;
        this.location = location;
        this.direction = direction;
        this.point = 0;
    }

    public Pair getLocation() {
        return this.location;
    }

    public void setLocation(Pair p) {
        this.location = p;
    }

    public int getXpos() {
        return location.x;
    }

    public int getYpos() {
        return location.y;
    }

    public String getDirection() {
        return direction;
    }

    public void addPoints(int p) {
        point += p;
    }

    public String toString() {
        return name + ":" + point;
    }

    public String getName() {
        return name;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public void setDataOutputStream(DataOutputStream dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
    }
}
