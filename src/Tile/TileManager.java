package Tile;


import Main.GamePanel;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.HashMap;
import java.util.Random;
import Main.GameState;
import Main.Redrawable;
import Main.Resizable;
import Object.Point_obj;
import Object.Power_pellet_obj;

import javax.swing.*;

public class TileManager implements Resizable, Redrawable {
    GamePanel gp;
    private Tile[][] tiles;
    private int textureChangeCounter = 0;
    private final HashMap<String, String> mapNamesToPaths = new HashMap<>() {{
       put("Wide", "/maps/wide_map.txt");
       put("Small", "/maps/small_map.txt");
       put("Humanlike", "/maps/humanlike_map.txt");
       put("Original", "/maps/original_map.txt");
       put("Squared", "/maps/squared_map.txt");
    }};

    public TileManager(GamePanel gp) {
        this.gp = gp;
    }

    public void loadMap(String mapName, JPanel mapPanel) {
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

        try {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(0, 0, 0, 0);

            //reading map from file
            InputStream inputStream = getClass().getResourceAsStream(mapNamesToPaths.get(mapName));
            assert inputStream != null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            //reading amount of rows and columns
            String[] mapLineInNumbers = bufferedReader.readLine().split(" ");
            int rows = Integer.parseInt(mapLineInNumbers[0]);
            int columns = Integer.parseInt(mapLineInNumbers[1]);
            mapPanel.setPreferredSize(new Dimension(columns * gp.getWidthTileSize(), rows * gp.getHeightTileSize()));
            mapPanel.setBounds(0, 0, columns * gp.getWidthTileSize(), rows * gp.getHeightTileSize());
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
                    //making tile
                    tiles[row][column] = new Tile(number, mapColor, gp);
                    tiles[row][column].setLocation(column * gp.getWidthTileSize(), row * gp.getHeightTileSize());

                    //setting constraints & adding to map
                    gbc.gridx = column;
                    gbc.gridy = row;
                    mapPanel.add(tiles[row][column], gbc);

                    //filling map with points
                    if (number == 11) {
                        gp.getObjects().add(new Point_obj(gp, column * gp.getWidthTileSize(), row * gp.getHeightTileSize()));
                    }
                    //filling map with power pellets
                    if (number == 12) {
                        gp.getObjects().add(new Power_pellet_obj(gp, column * gp.getWidthTileSize(), row * gp.getHeightTileSize()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    

    public void redraw() {
        for (int row = 0; row < gp.getMaxScreenRow(); row++) {
            for (int column = 0; column < gp.getMaxScreenColumn(); column++) {
                if ((textureChangeCounter / 15) % 2 == 1) tiles[row][column].setIcon(tiles[row][column].getScaledIcon(tiles[row][column].getImage()));
                else if ((textureChangeCounter / 15) % 2 == 0) tiles[row][column].setIcon(tiles[row][column].getScaledIcon(tiles[row][column].getWhiteImage()));
            }
        }
        textureChangeCounter++;
    }

    @Override
    public void resize() {
        for (int row = 0; row < gp.getMaxScreenRow(); row++) {
            for (int column = 0; column < gp.getMaxScreenColumn(); column++) {
                tiles[row][column].setIcon(tiles[row][column].getScaledIcon(tiles[row][column].getImage()));
                tiles[row][column].setLocation(column * gp.getWidthTileSize(), row * gp.getHeightTileSize());
            }
        }
    }

    //GETTERS & SETTERS

    //getters

    public GamePanel getGp() {
        return gp;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public int getTextureChangeCounter() {
        return textureChangeCounter;
    }

    public HashMap<String, String> getMapNamesToPaths() {
        return mapNamesToPaths;
    }


    //setters

    public void setGp(GamePanel gp) {
        this.gp = gp;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public void setTextureChangeCounter(int textureChangeCounter) {
        this.textureChangeCounter = textureChangeCounter;
    }
}
