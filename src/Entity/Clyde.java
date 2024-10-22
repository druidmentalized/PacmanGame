package Entity;

import Main.GamePanel;

public class Clyde extends Ghost {

    public Clyde(GamePanel gp) {
        super(gp);
    }

    @Override
    protected void loadImages() {
        super.loadImages();

        up1 = getImage("/ghosts/clyde_up_1");
        up2 = getImage("/ghosts/clyde_up_2");

        down1 = getImage("/ghosts/clyde_down_1");
        down2 = getImage("/ghosts/clyde_down_2");

        right1 = getImage("/ghosts/clyde_right_1");
        right2 = getImage("/ghosts/clyde_right_2");

        left1 = getImage("/ghosts/clyde_left_1");
        left2 = getImage("/ghosts/clyde_left_2");

        idle1 = getImage("/ghosts/clyde_idle_1");
        idle2 = getImage("/ghosts/clyde_idle_2");

        frozenIdle1 = getImage("/ghosts/clyde_frozen_idle_1");
        frozenIdle2 = getImage("/ghosts/clyde_frozen_idle_2");
    }

    @Override
    protected Point findInChaseMode() {
        //going to players position. When in range of 8 tiles, going to scatter tile position
        int scareDistance = gp.getWidthTileSize() * 8;
        if (calculateVectorDistance(x, y, gp.getPlayer().x, gp.getPlayer().y) <= scareDistance) {
            return scatterCoords;
        }
        else {
            return new Point(gp.getPlayer().x, gp.getPlayer().y);
        }
    }
}
