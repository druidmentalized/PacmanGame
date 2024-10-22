package Object;
import Main.GamePanel;

public class Power_pellet_obj extends Booster {
    public Power_pellet_obj(GamePanel gp, int x, int y) {
        super(gp, x, y);
    }

    @Override
    protected void loadImages() {
        idle1 = getImage("/objects/power_pellet");
        idle2 = null;
    }

    @Override
    public void update() {
        //doesn't update
    }
}
