package Object;
import Main.Direction;
import Main.GamePanel;
import java.util.ArrayList;
import java.util.Random;

public class Speed_booster_obj extends Booster {

    public Speed_booster_obj(GamePanel gp, int x, int y) {
        super(gp, x, y);
        limit = 420; // 420 / 60 updates == 7 seconds
    }

    @Override
    protected void loadImages() {
        idle1 = getImage("res/objects/speed_booster_1");
        idle2 = getImage("res/objects/speed_booster_2");
    }

}
