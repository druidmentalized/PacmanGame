package Tile;


import Main.GamePanel;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Random;
import java.util.HashMap;
import Main.GameState;
import Object.Point_obj;
import Object.Power_pellet_obj;

public class TileManager {
    GamePanel gp;
    public Tile[][] tiles;
    public int textureChangeCounter = 0;
    private final HashMap<String, String> mapNamesToPaths = new HashMap<>() {{
       put("Wide", "/maps/wide_map.txt");
       put("Small", "/maps/small_map.txt");
       put("Humanlike", "/maps/humanlike_map.txt");
       put("Original", "/maps/original_map.txt");
    }};

    public TileManager(GamePanel gp) {
        this.gp = gp;
    }

    public void loadMap(String mapName) {
        //choosing color
        Random random = new Random();
        String mapColor;
        switch (random.nextInt(6)) {
            case 0 -> mapColor = "lightgreen";
            case 1 -> mapColor = "blue";
            case 2 -> mapColor = "cherry";
            case 3 -> mapColor = "yellow";
            case 4 -> mapColor = "violet";
            case 5 -> mapColor = "pink";
            default -> mapColor = "white";
        }

        //mapColor = "white";

        try {
            //reading map from file
            InputStream inputStream = getClass().getResourceAsStream(mapNamesToPaths.get(mapName));
            assert inputStream != null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            //reading amount of rows and columns
            String[] mapLineInNumbers = bufferedReader.readLine().split(" ");
            int rows = Integer.parseInt(mapLineInNumbers[0]);
            int columns = Integer.parseInt(mapLineInNumbers[1]);
            gp.setPreferredSize(new Dimension(rows, columns));
            gp.setMaxScreenColumn(columns);
            gp.setMaxScreenRow(rows);
            tiles = new Tile[rows][columns];

            //reading map constraints
            mapLineInNumbers = bufferedReader.readLine().split(" ");
            double[][] mapConstraints = new double[mapLineInNumbers.length / 2][2];
            int entityNum = 0;
            for (int i = 0; i < mapLineInNumbers.length; i++) {
                mapConstraints[entityNum][i % 2] = Double.parseDouble(mapLineInNumbers[i]);
                if (i % 2 == 1) entityNum++;
            }
            gp.setMapConstrains(mapConstraints);

            //taking each tile and setting it
            int number;
            for (int row = 0; row < rows; row++) {
                mapLineInNumbers = bufferedReader.readLine().split(" ");
                for (int column = 0; column < columns; column++) {
                    number = Integer.parseInt(mapLineInNumbers[column]);
                    tiles[row][column] = new Tile(number, mapColor, gp);
                    tiles[row][column].collisionAreaRectangle = new Rectangle(column * gp.tileSize, row * gp.tileSize, 8 * gp.scale, 8 * gp.scale);

                    //filling map with points
                    if (number == 11) {
                        gp.objects.add(new Point_obj(gp, column * gp.tileSize, row * gp.tileSize));
                    }
                    //filling map with power pellets
                    if (number == 12) {
                        gp.objects.add(new Power_pellet_obj(gp, column * gp.tileSize, row * gp.tileSize));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void draw(Graphics2D g2) {
        int worldRow, worldColumn;
        int worldX, worldY;
        for (worldRow = 0; worldRow < tiles.length; worldRow++) {
            for (worldColumn = 0; worldColumn < tiles[worldRow].length; worldColumn++) {
                worldX = worldColumn * gp.tileSize;
                worldY = worldRow * gp.tileSize;
                if (gp.gameState == GameState.LEVELCHANGE) {
                    if ((textureChangeCounter / 15) % 2 == 1) {
                        g2.drawImage(tiles[worldRow][worldColumn].getImage(), worldX, worldY, gp.tileSize, gp.tileSize, null);
                    }
                    else if ((textureChangeCounter / 15) % 2 == 0) {
                        g2.drawImage(tiles[worldRow][worldColumn].getWhiteImage(), worldX, worldY, gp.tileSize, gp.tileSize, null);
                    }
                } //4 changes
                else g2.drawImage(tiles[worldRow][worldColumn].getImage(), worldX, worldY, gp.tileSize, gp.tileSize, null);

                //DEBUG
                if (gp.keyHandler.debugPressed) {
                    g2.setColor(Color.DARK_GRAY);
                    g2.draw(tiles[worldRow][worldColumn].collisionAreaRectangle);
                }
            }
        }
        if (gp.gameState == GameState.LEVELCHANGE) {
            textureChangeCounter++;
        }
    }
}
