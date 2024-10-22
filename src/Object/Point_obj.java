package Object;
import Main.GamePanel;

public class Point_obj extends Booster {

    public Point_obj(GamePanel gp, int x, int y) {
        super(gp, x, y);
    }

    public void setDefaultValues() {
        loadImages();
        setBounds(x, y, gp.getWidthTileSize(), gp.getHeightTileSize());
        baseCollisionStart = 3;
        baseCollisionEnd = 2;
        setCollisionAreaRectangle();
    }

    @Override
    protected void loadImages() {
        currentImage = getImage("/objects/point");
    }

    @Override
    protected Thread createAnimationThread(int delay) {
        //not needed for current class
        return null;
    }

    @Override
    public void update() {
        //doesn't update
    }
}
