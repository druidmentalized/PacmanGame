package Entity;

import Main.Direction;
import Main.GamePanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class Entity
{
    //SYSTEM
    public static GamePanel gp;
    public Thread animationThread;
    public boolean animationRunning = true;

    //INFO
    public Direction direction = Direction.IDLE;
    public int x;
    public int y;
    protected int[] startPoint;
    public int currentRow = 0;
    public int currentColumn = 0;
    protected int prevRow = 0;
    protected int prevColumn = 0;
    public int speed;
    public BufferedImage up1, up2, up3, down1, down2, down3, right1, right2, right3, left1, left2,
            left3, idle1, idle2;
    public BufferedImage currentImage;
    public boolean directionChanged = false;
    public int spriteNum = 0;

    //COLLISION
    public Rectangle collisionAreaRectangle;
    public int collisionAreaDefaultX, collisionAreaDefaultY;
    public int collisionAreaDefaultWidth, collisionAreaDefaultHeight;
    public boolean collision;

    //CHARACTERISTICS

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public BufferedImage getImage(String filePath) {
        try {
            return ImageIO.read(new File(filePath + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void setCollisionAreaRectangle(int x, int y, int width, int height) {
        collisionAreaRectangle = new Rectangle(x, y, width, height);
        collisionAreaDefaultX = x;
        collisionAreaDefaultY = y;
        collisionAreaDefaultWidth = width;
        collisionAreaDefaultHeight = height;
    }

    protected boolean tileChanged() {
        //points exactly at centre of entity
        currentColumn = (x + collisionAreaRectangle.x + (gp.tileSize / 2)) / gp.tileSize;
        currentRow = (y + collisionAreaRectangle.y + (gp.tileSize / 2)) / gp.tileSize;
        //adjusting point to its movement
        switch (direction) {
            case UP -> currentRow = (y + collisionAreaRectangle.y + collisionAreaRectangle.height - speed) / gp.tileSize;
            case LEFT -> currentColumn = (x + collisionAreaRectangle.x + collisionAreaRectangle.width - speed) / gp.tileSize;
            case DOWN -> currentRow = (y + collisionAreaRectangle.y) / gp.tileSize;
            case RIGHT -> currentColumn = (x + collisionAreaRectangle.x) / gp.tileSize;
        }
        if ((currentRow != prevRow) || (currentColumn != prevColumn)) {
            prevRow = currentRow;
            prevColumn = currentColumn;
            return true;
        }
        else return false;
    }

    public abstract void setDefaultValues();
    protected abstract void loadImages();
    protected abstract Thread createAnimationThread(int delay);
    public abstract void update();

    public void draw(Graphics2D g2) {
        g2.drawImage(currentImage, x, y, (int)(gp.tileSize * 1.5), (int)(gp.tileSize * 1.5), null);

        //DEBUG
        if (gp.keyHandler.debugPressed) {
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.setStroke(new BasicStroke(1));
            if (this instanceof Player){
                g2.setColor(Color.red);
            }
            else if (this instanceof Ghost) {
                g2.setColor(Color.GREEN);
            }
            collisionAreaRectangle.x = x + collisionAreaRectangle.x;
            collisionAreaRectangle.y = y + collisionAreaRectangle.y;
            g2.draw(collisionAreaRectangle);
            collisionAreaRectangle.x = collisionAreaDefaultX;
            collisionAreaRectangle.y = collisionAreaDefaultY;

            g2.setColor(Color.WHITE);
            String cords;
            if (this instanceof Player){
                cords = "X: " + (x + collisionAreaRectangle.x) + ", Y: " + (y + collisionAreaRectangle.y) +
                        ", column: " + ((x + collisionAreaRectangle.x)) / gp.tileSize + ", row: " +
                        ((y + collisionAreaRectangle.y) / gp.tileSize) + ", direction: " + direction;
                g2.drawString(cords, 20, gp.getMaxScreenHeight() - 20);
            }
            if (this instanceof Ghost) {
                if (this instanceof Blinky) {
                    g2.setColor(Color.RED);
                }
                else if (this instanceof Pinky){
                    g2.setColor(Color.PINK);
                }
                else if (this instanceof Inky) {
                    cords = " X: " + (x + collisionAreaRectangle.x) + ", Y: " + (y + collisionAreaRectangle.y) +
                            ", column: " + ((x + collisionAreaRectangle.x)) / gp.tileSize + ", row: " +
                            ((y + collisionAreaRectangle.y) / gp.tileSize) + ", direction: " + direction;
                    g2.drawString(cords, 15 * gp.tileSize, gp.getMaxScreenHeight() - 20);
                    g2.setColor(new Color(0, 255, 255));
                }
                else if (this instanceof Clyde) g2.setColor(new Color(255, 185, 81));
                g2.setStroke(new BasicStroke(3));
                Rectangle targetRect = new Rectangle(((Ghost) this).xy[0], ((Ghost) this).xy[1], 24, 24);
                g2.draw(targetRect);
            }
        }
    }

    public void setStartPoint(int[] startPoint) {
        this.startPoint = startPoint;
    }
}
