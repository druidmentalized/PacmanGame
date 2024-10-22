package Object;

import Main.GamePanel;

public class Heart_booster_obj extends Booster {

    public Heart_booster_obj(GamePanel gp, int x, int y) {
        super(gp, x, y);
    }

    @Override
    protected Thread createAnimationThread(int delay) {
        return new Thread(() -> {
            while (animationRunning) {
                if (spriteNum == 3) spriteNum = 0;
                spriteNum++;

                if (spriteNum == 1) currentImage = up1;
                else if (spriteNum == 2) currentImage = up2;
                else if (spriteNum == 3) currentImage = up3;



                //delay
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    //nothing
                }
            }
        });
    }

    @Override
    protected void loadImages() {
        up1 = getImage("/objects/heart_booster_1");
        up2 = getImage("/objects/heart_booster_2");
        up3 = getImage("/objects/heart_booster_3");
    }
}
