package Object;
import Main.GamePanel;

public class Invisibility_booster_obj extends Booster {

    public Invisibility_booster_obj(GamePanel gp, int x, int y) {
        super(gp, x, y);
        limit = 600; //600 / 60 updates == 10 seconds
    }


    @Override
    protected void loadImages() {
        idle1 = getImage("res/objects/invisibility_booster_1");
        idle2 = getImage("res/objects/invisibility_booster_2");
    }
}
