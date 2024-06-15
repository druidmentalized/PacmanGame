package Entity;

import Main.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class Entity extends JLabel implements Resizable, Redrawable, Updatable
{
    //SYSTEM
    protected GamePanel gp;
    private Thread animationThread;
    protected static boolean animationRunning = true;

    //INFO
    protected Direction direction = Direction.IDLE;
    protected int x;
    protected int y;
    protected int currentRow = 0;
    protected int currentColumn = 0;
    protected int prevRow = 0;
    protected int prevColumn = 0;
    protected double baseSpeed;
    protected int hspeed;
    protected int vspeed;
    protected BufferedImage up1, up2, up3, down1, down2, down3, right1, right2, right3, left1, left2,
            left3, idle1, idle2;
    protected BufferedImage currentImage;
    private boolean directionChanged = false;
    protected int spriteNum = 0;

    //COLLISION
    protected Rectangle collisionAreaRectangle;
    private int collisionAreaDefaultX, collisionAreaDefaultY;
    protected double baseCollisionStart, baseCollisionEnd;
    public boolean collision;

    //CHARACTERISTICS

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    protected BufferedImage getImage(String filePath) {
        try {
            return ImageIO.read(new File(filePath + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    protected ImageIcon getScaledIcon(BufferedImage image) {
        if (image == null) return null;
        Image scaledImage = image.getScaledInstance((int)(gp.getWidthTileSize() * 1.5), (int)(gp.getHeightTileSize() * 1.5), Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    protected void setCollisionAreaRectangle() {
        collisionAreaRectangle = new Rectangle ((int)(baseCollisionStart * gp.getWidthScale())
                , (int)(baseCollisionStart * gp.getHeightScale()), (int)(baseCollisionEnd * gp.getWidthScale()),
                (int)(baseCollisionEnd * gp.getHeightScale()));
        collisionAreaDefaultX = collisionAreaRectangle.x;
        collisionAreaDefaultY = collisionAreaRectangle.y;
    }

    protected boolean tileChanged() {
        //points exactly at centre of entity
        currentColumn = (x + collisionAreaRectangle.x + (gp.getWidthTileSize() / 2)) / gp.getWidthTileSize();
        currentRow = (y + collisionAreaRectangle.y + (gp.getHeightTileSize() / 2)) / gp.getHeightTileSize();

        //adjusting point to its movement
        switch (direction) {
            case UP -> currentRow = (y + collisionAreaRectangle.y + collisionAreaRectangle.height - vspeed) / gp.getHeightTileSize();
            case LEFT -> currentColumn = (x + collisionAreaRectangle.x + collisionAreaRectangle.width - hspeed) / gp.getWidthTileSize();
            case DOWN -> currentRow = (y + collisionAreaRectangle.y) / gp.getHeightTileSize();
            case RIGHT -> currentColumn = (x + collisionAreaRectangle.x) / gp.getWidthTileSize();
        }

        //if changed, return true
        if ((currentRow != prevRow) || (currentColumn != prevColumn)) {
            prevRow = currentRow;
            prevColumn = currentColumn;
            return true;
        }
        else return false;
    }

    public void recalculateSpeed() {
        hspeed = (int)(baseSpeed * gp.getWidthScale());
        vspeed = (int)(baseSpeed * gp.getHeightScale());
    }

    private void recalculatePosition() {
        x = (int)Math.round((x * gp.getWidthRatio()));
        y = (int)Math.round((y * gp.getHeightRatio()));
    }

    public abstract void setDefaultValues();
    protected abstract void loadImages();
    protected abstract Thread createAnimationThread(int delay);
    @Override
    public abstract void update();

    @Override
    public void redraw() {
        setIcon(getScaledIcon(currentImage));
        setLocation(x, y);
    }

    @Override
    public void resize() {
        recalculatePosition();
        recalculateSpeed();
        setCollisionAreaRectangle();
    }

    // GETTERS & SETTERS
    //getters
    public Thread getAnimationThread() {
        return animationThread;
    }
    public Direction getDirection() {
        return direction;
    }
    @Override
    public int getX() {
        return x;
    }
    @Override
    public int getY() {
        return y;
    }
    public int getHspeed() {
        return hspeed;
    }
    public int getVspeed() {
        return vspeed;
    }
    public boolean isDirectionChanged() {
        return directionChanged;
    }
    public Rectangle getCollisionAreaRectangle() {
        return collisionAreaRectangle;
    }
    public int getCollisionAreaDefaultX() {
        return collisionAreaDefaultX;
    }
    public int getCollisionAreaDefaultY() {
        return collisionAreaDefaultY;
    }

    //-------------------------------
    //setters
    public static void setAnimationRunning(boolean animRun) {
        animationRunning = animRun;
    }
    public void setAnimationThread(Thread animationThread) {
        this.animationThread = animationThread;
    }

    public void setDirectionChanged(boolean directionChanged) {
        this.directionChanged = directionChanged;
    }
}
