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

        up1 = getImage("res/ghosts/pinky_up_1");
        up2 = getImage("res/ghosts/pinky_up_2");

        down1 = getImage("res/ghosts/pinky_down_1");
        down2 = getImage("res/ghosts/pinky_down_2");

        right1 = getImage("res/ghosts/pinky_right_1");
        right2 = getImage("res/ghosts/pinky_right_2");

        left1 = getImage("res/ghosts/pinky_left_1");
        left2 = getImage("res/ghosts/pinky_left_2");

        idle1 = getImage("res/ghosts/pinky_idle_1");
        idle2 = getImage("res/ghosts/pinky_idle_2");

        frozenIdle1 = getImage("res/ghosts/pinky_frozen_idle_1");
        frozenIdle2 = getImage("res/ghosts/pinky_frozen_idle_2");
    }

    @Override
    protected int[] findInChaseMode() {
        switch (gp.player.direction) {
            case UP -> {
                return new int[]{gp.player.x, gp.player.y - (gp.tileSize * 4)};
            }
            case DOWN -> {
                return new int[]{gp.player.x ,gp.player.y + (gp.tileSize * 4)};
            }
            case RIGHT -> {
                return new int[]{gp.player.x + (gp.tileSize * 4), gp.player.y};
            }
            case LEFT -> {
                return new int[]{gp.player.x - (gp.tileSize * 4), gp.player.y};
            }
            default -> {
                return new int[]{gp.player.x, gp.player.y};
            }
        }
    }
}
