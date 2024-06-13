package Object;
import Entity.Entity;
import Main.Direction;
import Main.GamePanel;
import com.sun.security.jgss.GSSUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public abstract class Booster extends Entity {
    public int timeCounter = 0;
    public boolean consumed = false;
    public int limit;

    public Booster(GamePanel gp, int x, int y)
    {
        super(gp);
        this.x = x;
        this.y = y;
        setDefaultValues();
    }

    @Override
    public void setDefaultValues() {
        loadImages();
        setCollisionAreaRectangle(0, 0, 8 * gp.scale, 8 * gp.scale);
        direction = Direction.values()[new Random().nextInt(Direction.values().length - 1)];
        speed = 3;
        limit = 420;
        animationThread = createAnimationThread(120);
        animationThread.start();
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

                }
            }
        });
    }

    @Override
    public void update() {
        if (!consumed) {
            //for going left-right right-left border
            if (x + gp.tileSize <= 0) {
                x = gp.maxScreenColumn * gp.tileSize;
            }
            else if (x >= gp.maxScreenColumn * gp.tileSize) {
                x = 0;
            }

            if (tileChanged()) {
                //checking collision for current direction:
                gp.collisionChecker.checkIfCanMove(this, direction);

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
                case UP -> y -= speed;
                case DOWN -> y += speed;
                case RIGHT -> x += speed;
                case LEFT -> x -= speed;
            }
        }
        timeCounter++;
    }

    private ArrayList<Direction> determineDirections() {
        ArrayList<Direction> possibleDirections = new ArrayList<>();
        for (Direction checkedDirection : Direction.values()) {
            if (checkedDirection != Direction.IDLE) {
                gp.collisionChecker.checkIfCanMove(this, checkedDirection);
                if (!collision) {
                    possibleDirections.add(checkedDirection);
                }
                else collision = false;
            }
        }
        return possibleDirections;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(currentImage, x, y, gp.tileSize, gp.tileSize, null);

        //DEBUG
        if (gp.keyHandler.debugPressed) {
            g2.setColor(Color.PINK);
            collisionAreaRectangle.x += x;
            collisionAreaRectangle.y += y;
            g2.draw(collisionAreaRectangle);
            collisionAreaRectangle.x = collisionAreaDefaultX;
            collisionAreaRectangle.y = collisionAreaDefaultY;
        }
    }
}
