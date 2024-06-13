package Object;
import Entity.Entity;
import Main.GamePanel;

import java.awt.*;

public class Point_obj extends Booster {

    public Point_obj(GamePanel gp, int x, int y) {
        super(gp, x, y);
    }

    public void setDefaultValues() {
        loadImages();
        setCollisionAreaRectangle(3 * gp.scale, 3 * gp.scale, 2 * gp.scale, 2 * gp.scale);
    }

    @Override
    protected void loadImages() {
        currentImage = getImage("res/objects/point");
    }

    @Override
    protected Thread createAnimationThread(int delay) {
        //not needed for current class
        return null;
    }

    @Override
    public void update() {
        //doesn't update
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(currentImage, x, y, gp.tileSize, gp.tileSize, null);

        //debug
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
