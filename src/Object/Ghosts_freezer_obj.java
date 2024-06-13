package Object;

import Main.GamePanel;

public class Ghosts_freezer_obj extends Booster {

    public Ghosts_freezer_obj(GamePanel gp, int x, int y) {
        super(gp, x, y);
    }

    @Override
    protected void loadImages() {
        idle1 = getImage("res/objects/snowflake_booster");
        idle2 = getImage("res/objects/snowflake_booster");
    }
}
