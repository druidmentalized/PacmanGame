package Entity;

import Main.GamePanel;

public class Pinky extends Ghost
{
    public Pinky(GamePanel gp) {
        super(gp);
    }

    @Override
    protected void loadImages() {
        super.loadImages();

        up1 = getImage("/ghosts/pinky_up_1");
        up2 = getImage("/ghosts/pinky_up_2");

        down1 = getImage("/ghosts/pinky_down_1");
        down2 = getImage("/ghosts/pinky_down_2");

        right1 = getImage("/ghosts/pinky_right_1");
        right2 = getImage("/ghosts/pinky_right_2");

        left1 = getImage("/ghosts/pinky_left_1");
        left2 = getImage("/ghosts/pinky_left_2");

        idle1 = getImage("/ghosts/pinky_idle_1");
        idle2 = getImage("/ghosts/pinky_idle_2");

        frozenIdle1 = getImage("/ghosts/pinky_frozen_idle_1");
        frozenIdle2 = getImage("/ghosts/pinky_frozen_idle_2");
    }

    @Override
    protected Point findInChaseMode() {
        //counting 4 tiles ahead of pacman direction of movement
        switch (gp.getPlayer().direction) {
            case UP -> {
                return new Point(gp.getPlayer().x, gp.getPlayer().y - (gp.getHeightTileSize() * 4));
            }
            case DOWN -> {
                return new Point(gp.getPlayer().x ,gp.getPlayer().y + (gp.getHeightTileSize() * 4));
            }
            case RIGHT -> {
                return new Point(gp.getPlayer().x + (gp.getWidthTileSize() * 4), gp.getPlayer().y);
            }
            case LEFT -> {
                return new Point(gp.getPlayer().x - (gp.getWidthTileSize() * 4), gp.getPlayer().y);
            }
            default -> {
                return new Point(gp.getPlayer().x, gp.getPlayer().y);
            }
        }
    }
}
