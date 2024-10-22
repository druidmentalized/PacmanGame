package Entity;

import Main.GamePanel;

public class Blinky extends Ghost {

    public Blinky(GamePanel gp) {
        super(gp);
    }

    @Override
    protected void loadImages() {
        super.loadImages();
        up1 = getImage("/ghosts/blinky_up_1");
        up2 = getImage("/ghosts/blinky_up_2");

        down1 = getImage("/ghosts/blinky_down_1");
        down2 = getImage("/ghosts/blinky_down_2");

        right1 = getImage("/ghosts/blinky_right_1");
        right2 = getImage("/ghosts/blinky_right_2");

        left1 = getImage("/ghosts/blinky_left_1");
        left2 = getImage("/ghosts/blinky_left_2");

        idle1 = getImage("/ghosts/blinky_idle_1");
        idle2 = getImage("/ghosts/blinky_idle_2");

        frozenIdle1 = getImage("/ghosts/blinky_frozen_idle_1");
        frozenIdle2 = getImage("/ghosts/blinky_frozen_idle_2");
    }

    @Override
    protected Point findInChaseMode() {
        //taking exactly players position
        return new Point(gp.getPlayer().x, gp.getPlayer().y);
    }
}
