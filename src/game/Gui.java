package game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;

import java.io.IOException;

public class Gui extends Application {

    public static final int size = 30;
    public static final int scene_height = size * 20 + 50;
    public static final int scene_width = size * 20 + 200;

    public static Image image_floor;
    public static Image image_wall;
    public static Image hero_right, hero_left, hero_up, hero_down;
    public static Image image_coin;

    private static volatile Label[][] fields;
    private static TextArea scoreList;

    // -------------------------------------------
    // | Maze: (0,0)              | Score: (1,0) |
    // |-----------------------------------------|
    // | boardGrid (0,1)          | scorelist    |
    // |                          | (1,1)        |
    // -------------------------------------------

    @Override
    public void start(Stage primaryStage) {
        try {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(0, 10, 0, 10));

            Text mazeLabel = new Text("Maze:");
            mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            Text scoreLabel = new Text("Score:");
            scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            scoreList = new TextArea();

            GridPane boardGrid = new GridPane();

            image_wall = new Image(getClass().getResourceAsStream("image/wall4.png"), size, size, false, false);
            image_floor = new Image(getClass().getResourceAsStream("image/floor1.png"), size, size, false, false);

            hero_right = new Image(getClass().getResourceAsStream("image/heroRight.png"), size, size, false, false);
            hero_left = new Image(getClass().getResourceAsStream("image/heroLeft.png"), size, size, false, false);
            hero_up = new Image(getClass().getResourceAsStream("image/heroUp.png"), size, size, false, false);
            hero_down = new Image(getClass().getResourceAsStream("image/heroDown.png"), size, size, false, false);

            image_coin = new Image(getClass().getResourceAsStream("image/coin1.png"), size, size, false, false);

            fields = new Label[20][20];
            for (int j = 0; j < 20; j++) {
                for (int i = 0; i < 20; i++) {
                    switch (Generel.board[j].charAt(i)) {
                        case 'w':
                            fields[i][j] = new Label("", new ImageView(image_wall));
                            break;
                        case ' ':
                            fields[i][j] = new Label("", new ImageView(image_floor));
                            break;
                        case 'c':
                            fields[i][j] = new Label("", new ImageView(image_coin));
                            break;
                        default:
                            throw new Exception("Illegal field value: " + Generel.board[j].charAt(i));
                    }
                    boardGrid.add(fields[i][j], i, j);
                }
            }
            scoreList.setEditable(false);

            grid.add(mazeLabel, 0, 0);
            grid.add(scoreLabel, 1, 0);
            grid.add(boardGrid, 0, 1);
            grid.add(scoreList, 1, 1);

            Scene scene = new Scene(grid, scene_width, scene_height);
            primaryStage.setScene(scene);
            primaryStage.show();

            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                switch (event.getCode()) {
                    case UP:
                        try {
                            playerMoved(0, -1, "up");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case DOWN:
                        try {
                            playerMoved(0, +1, "down");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case LEFT:
                        try {
                            playerMoved(-1, 0, "left");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case RIGHT:
                        try {
                            playerMoved(+1, 0, "right");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case ESCAPE:
                        System.exit(0);
                    default:
                        break;
                }
            });

            scoreList.setText(getScoreList());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void removePlayerOnScreen(Pair oldpos) {
        Platform.runLater(() -> {
            fields[oldpos.getX()][oldpos.getY()].setGraphic(new ImageView(image_floor));
        });
    }

    public static void placePlayerOnScreen(Pair newpos, String direction) {
//        if (!isReady || fields == null) return;
        Platform.runLater(() -> {
            int x = newpos.getX();
            int y = newpos.getY();
            ImageView view = new ImageView();
            switch (direction.toLowerCase()) {
                case "up":    view.setImage(hero_up); break;
                case "down":  view.setImage(hero_down); break;
                case "left":  view.setImage(hero_left); break;
                case "right": view.setImage(hero_right); break;
            }
            fields[x][y].setGraphic(view);
        });
    }

    public static void placeCoinOnScreen(Pair pair){
//        if (!isReady || fields == null) return;
        Platform.runLater(() -> {
            int x = pair.getX();
            int y = pair.getY();
            fields[x][y].setGraphic(new ImageView(image_coin));
        });

    }

    public static void updateScoreTable() {
        Platform.runLater(() -> {
            scoreList.setText(getScoreList());
        });
    }

    public static void playerMoved(int delta_x, int delta_y, String direction) throws IOException {
        if (direction.equals("up")) {
            App.clientThread.sendCommand("UP" + ":" + delta_x + ":" + delta_y);
        } else if (direction.equals("down")) {
            App.clientThread.sendCommand("DOWN" + ":" + delta_x + ":" + delta_y);
        } else if (direction.equals("left")) {
            App.clientThread.sendCommand("LEFT" + ":" + delta_x + ":" + delta_y);
        } else {
            App.clientThread.sendCommand("RIGHT" + ":" + delta_x + ":" + delta_y);
        }

        updateScoreTable();
    }

    public static String getScoreList() {
        StringBuffer b = new StringBuffer(100);
        for (Player p : App.clientThread.players) {
            b.append(p.getName()).append(": ").append(p.point).append("\r\n");
        }
        return b.toString();
    }
}
