package Object;

import Main.GamePanel;

public class Wall_piercer_booster_obj extends Booster {

    public Wall_piercer_booster_obj(GamePanel gp, int x, int y) {
        super(gp, x, y);
        limit = 300; // 300 / 60 updates == 5 seconds
    }

    @Override
    protected void loadImages() {
        idle1 = getImage("res/objects/wall_piercer_booster");
        idle2 = getImage("res/objects/wall_piercer_booster");
    }
}
