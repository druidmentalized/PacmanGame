package Object;
import Entity.Entity;
import Main.Direction;
import Main.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public abstract class Booster extends Entity {
    private int timeCounter = 0;
    private boolean consumed = false;
    protected int limit;

    public Booster(GamePanel gp, int x, int y)
    {
        super(gp);
        this.x = x;
        this.y = y;
        setDefaultValues();
        gp.getEatablesPanel().add(this);
    }

    @Override
    public void setDefaultValues() {
        loadImages();
        baseSpeed = 1;
        recalculateSpeed();
        baseCollisionStart = 0;
        baseCollisionEnd = 8;
        setCollisionAreaRectangle();
        setBounds(x, y, gp.getWidthTileSize(), gp.getHeightTileSize());
        direction = Direction.values()[new Random().nextInt(Direction.values().length - 1)];
        limit = 420;
        setAnimationThread(createAnimationThread(120));
        getAnimationThread().start();
    }

    @Override
    protected Thread createAnimationThread(int delay) {
        return new Thread(() -> {
            while (animationRunning) {
                if (spriteNum == 2) spriteNum = 0;
                spriteNum++;

                if (spriteNum == 1) currentImage = idle1;
                else if (spriteNum == 2) currentImage = idle2;

                //delay
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    //just to restart thread
                }
            }
        });
    }

    @Override
    public void update() {
        if (!consumed) {
            //for going left-right right-left border
            if (x + gp.getWidthTileSize() <= 0) {
                x = gp.getMaxScreenColumn() * gp.getWidthTileSize();
            }
            else if (x >= gp.getMaxScreenColumn() * gp.getWidthTileSize()) {
                x = 0;
            }

            if (tileChanged()) {
                //checking collision for current direction:
                gp.getCollisionChecker().checkIfCanMove(this, direction);

                //if touching a wall -> changing direction
                if (collision) {
                    collision = false;
                    //taking all possible directions
                    ArrayList<Direction> possibleDirections = determineDirections();

                    //randomizing next direction of movement of all possible
                    possibleDirections.remove(direction);
                    direction = possibleDirections.get(new Random().nextInt(possibleDirections.size()));
                }
            }

            switch (direction) {
                case UP -> y -= vspeed;
                case DOWN -> y += vspeed;
                case RIGHT -> x += hspeed;
                case LEFT -> x -= hspeed;
            }
        }
        timeCounter++;
    }

    private ArrayList<Direction> determineDirections() {
        ArrayList<Direction> possibleDirections = new ArrayList<>();
        for (Direction checkedDirection : Direction.values()) {
            if (checkedDirection != Direction.IDLE) {
                gp.getCollisionChecker().checkIfCanMove(this, checkedDirection);
                if (!collision) {
                    possibleDirections.add(checkedDirection);
                }
                else collision = false;
            }
        }
        return possibleDirections;
    }

    public void redraw() {
        if (currentImage == null) setIcon(null);
        else setIcon(getScaledIcon(currentImage));
        setLocation(x, y);
    }

    @Override
    public void resize() {
        super.resize();
        setBounds(x, y, gp.getWidthTileSize(), gp.getHeightTileSize());
    }

    @Override
    protected ImageIcon getScaledIcon(BufferedImage image) {
        Image scaledImage = image.getScaledInstance(gp.getWidthTileSize(), gp.getHeightTileSize(), Image.SCALE_SMOOTH);

        return new ImageIcon(scaledImage);
    }

    //GETTERS & SETTERS

    //getters
    public int getTimeCounter() {
        return timeCounter;
    }
    public boolean isConsumed() {
        return consumed;
    }
    public int getLimit() {
        return limit;
    }

    //------------------------------------------
    //setters
    public void setTimeCounter(int timeCounter) {
        this.timeCounter = timeCounter;
    }
    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }
}
