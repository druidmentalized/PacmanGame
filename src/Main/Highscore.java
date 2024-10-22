package Main;

import java.io.*;
import java.util.ArrayList;

public class Highscore implements Serializable
{
    private static String highscoresFilePath;
    private final String playerName;
    private final int points;
    private final int time;
    private final int level;

    public Highscore(String playerName, int points, int time, int level) {
        this.points = points;
        this.playerName = playerName;
        this.time = time;
        this.level = level;
    }

    @Override
    public String toString() {
        return playerName + ": " + points + "pts, " + time + "s, " + level + "lvl";
    }
    public static void placeInFile(ArrayList<Highscore> highscoresList) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(highscoresFilePath));
            for (Highscore highscore : highscoresList) {
                oos.writeObject(highscore);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Highscore> readFromFile() {
        ArrayList<Highscore> highscoreList = new ArrayList<>();
        try {
            File file = new File(highscoresFilePath);
            if (file.exists() && file.canRead()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(highscoresFilePath));

                Highscore highscore;
                while (true) {
                    highscore = (Highscore) ois.readObject();
                    highscoreList.add(highscore);
                }
            }
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            //nothing, because it allows to get out from the infinite cycle meaning that we reached ending of the file
        }
        return highscoreList;
    }

    //GETTERS & SETTERS

    //setters
    public static void setHighscoresFilePath(String filePath) {
        highscoresFilePath = filePath;
    }
}
