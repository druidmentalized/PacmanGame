package Tile;

import Main.GamePanel;
import Main.Resizable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tile extends JLabel {
    private static final String[] tileTypes = {
            "", "", "", "", "", "", "", "", "", "",
            //-----------------------------------BORDERS----------------------------------------------------//
            "void"/*10*/, "void"/*11(points)*/, "void"/*12(power pellets)*/, "corner_bottom_left_border"/*13*/,
            "corner_bottom_right_border"/*14*/, "corner_top_left_border"/*15*/, "corner_top_right_border"/*16*/,
            "horizontal_bottom_border"/*17*/, "horizontal_top_border"/*18*/, "vertical_left_border"/*19*/,
            "vertical_right_border"/*20*/,
            //------------------------------------WALLS-----------------------------------------------------//
            "corner_bottom_left_wall"/*21*/, "corner_bottom_right_wall"/*22*/, "corner_top_left_wall"/*23*/,
            "corner_top_right_wall"/*24*/, "horizontal_bottom_wall"/*25*/, "horizontal_top_wall"/*26*/,
            "vertical_left_wall"/*27*/, "vertical_right_wall"/*28*/,
            //------------------------------------ADDITIONAL---------------------------------------------------//
            "corner_bottom_left_strict_border"/*29*/, "corner_bottom_right_strict_border"/*30*/,
            "corner_top_left_strict_border"/*31*/, "corner_top_right_strict_border"/*32*/,
            "corner_top_bottom_left_border"/*33*/, "corner_top_bottom_right_border"/*34*/,
            "corner_left_right_top_border"/*35*/, "corner_left_right_bottom_border"/*36*/,
            "corner_right_left_top_border"/*37*/, "corner_right_left_bottom_border"/*38*/,
            "corner_bottom_top_left_border"/*39*/, "corner_bottom_top_right_border"/*40*/, "barrier"/*41*/,
            "filling_void"/*42(insides of structures)*/
    };
    private BufferedImage image;
    private BufferedImage whiteImage;
    private boolean collision;
    private int tileType;
    private final GamePanel gp;

    public Tile(int tileType, String color, GamePanel gp) {
        this.gp = gp;
        this.tileType = tileType;
        collision = tileType != 10 && tileType != 11 && tileType != 12;
        String pathName;
        String whitePathName;
        if (tileType == 10 || tileType == 11 || tileType == 12 || tileType == 42) {
            pathName = "/tiles/void.png";
            whitePathName = "/tiles/void.png";
        }
        else {
            pathName = "/tiles/" + color + "/" + tileTypes[tileType] + ".png";
            whitePathName = "/tiles/white/" + tileTypes[tileType] + ".png";
        }
        try {
            image = ImageIO.read(getClass().getResource(pathName));
            whiteImage = ImageIO.read(getClass().getResource(whitePathName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setIcon(getScaledIcon(image));
    }



    //GETTERS & SETTERS

    //getters
    public ImageIcon getScaledIcon(BufferedImage image) {
        Image scaledImage = image.getScaledInstance(gp.getWidthTileSize(), gp.getHeightTileSize(), Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
    public BufferedImage getImage() {
        return image;
    }
    public BufferedImage getWhiteImage() {
        return whiteImage;
    }
    public boolean isCollision() {
        return collision;
    }

    public int getTileType() {
        return tileType;
    }

    public GamePanel getGp() {
        return gp;
    }


    //setters

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setWhiteImage(BufferedImage whiteImage) {
        this.whiteImage = whiteImage;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public void setTileType(int tileType) {
        this.tileType = tileType;
    }
}
