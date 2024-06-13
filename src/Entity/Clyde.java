package Entity;

import Main.GamePanel;

public class Clyde extends Ghost {

    public Clyde(GamePanel gp) {
        super(gp);
    }

    @Override
    protected void loadImages() {
        super.loadImages();

        up1 = getImage("res/ghosts/clyde_up_1");
        up2 = getImage("res/ghosts/clyde_up_2");

        down1 = getImage("res/ghosts/clyde_down_1");
        down2 = getImage("res/ghosts/clyde_down_2");

        right1 = getImage("res/ghosts/clyde_right_1");
        right2 = getImage("res/ghosts/clyde_right_2");

        left1 = getImage("res/ghosts/clyde_left_1");
        left2 = getImage("res/ghosts/clyde_left_2");

        idle1 = getImage("res/ghosts/clyde_idle_1");
        idle2 = getImage("res/ghosts/clyde_idle_2");

        frozenIdle1 = getImage("res/ghosts/clyde_frozen_idle_1");
        frozenIdle2 = getImage("res/ghosts/clyde_frozen_idle_2");
    }

    @Override
    protected int[] findInChaseMode() {
        int scareDistance = gp.tileSize * 8;
        if (calculateVectorDistance(x, y, gp.player.x, gp.player.y) <= scareDistance) {
            return scatterCoords;
        }
        else {
            return new int[]{gp.player.x, gp.player.y};
        }
    }
}
