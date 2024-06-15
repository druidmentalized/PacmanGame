package Main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Highscore implements Serializable
{
    private static final String highscoresFilePath = "res/highscores.txt";
    private final String playerName;
    private final int points;
    private final int time;

    public Highscore(String playerName, int points, int time) {
        this.points = points;
        this.playerName = playerName;
        this.time = time;
    }

    @Override
    public String toString() {
        return playerName + ": " + points + "pts " + time + " seconds";
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
}
