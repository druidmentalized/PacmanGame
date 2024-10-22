package Entity;

import Main.GamePanel;

public class Inky extends Ghost {

    public Inky(GamePanel gp) {
        super(gp);
    }

    @Override
    protected void loadImages() {
        super.loadImages();

        up1 = getImage("/ghosts/inky_up_1");
        up2 = getImage("/ghosts/inky_up_2");

        down1 = getImage("/ghosts/inky_down_1");
        down2 = getImage("/ghosts/inky_down_2");

        right1 = getImage("/ghosts/inky_right_1");
        right2 = getImage("/ghosts/inky_right_2");

        left1 = getImage("/ghosts/inky_left_1");
        left2 = getImage("/ghosts/inky_left_2");

        idle1 = getImage("/ghosts/inky_idle_1");
        idle2 = getImage("/ghosts/inky_idle_2");

        frozenIdle1 = getImage("/ghosts/inky_frozen_idle_1");
        frozenIdle2 = getImage("/ghosts/inky_frozen_idle_2");
    }

    @Override
    protected Point findInChaseMode() {
        //counting tile difference between pacman and blinky
        int xTileDifference = gp.getGhosts().getFirst().currentColumn - gp.getPlayer().currentColumn;
        int yTileDifference = gp.getGhosts().getFirst().currentRow - gp.getPlayer().currentRow;

        //target tile is doubled this difference
        int targetX = (gp.getGhosts().getFirst().currentColumn - (xTileDifference * 2)) * gp.getWidthTileSize();
        int targetY = (gp.getGhosts().getFirst().currentRow - (yTileDifference * 2)) * gp.getHeightTileSize();
        return new Point(targetX, targetY);
    }
}
