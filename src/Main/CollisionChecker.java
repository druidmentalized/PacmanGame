package Main;
import Entity.Entity;
import java.util.ArrayList;
import java.util.Arrays;

import Entity.Ghost;
import Entity.Player;


public class CollisionChecker {
    private final GamePanel gp;
    private final ArrayList<Integer> neverPassableTileNums = new ArrayList<>(Arrays.asList(17, 18, 19, 20, 29, 30, 31, 32, 39));

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkIfCanMove(Entity entity, Direction direction) {
        //taking the opposite collision border to the direction of moving
        double entityCollisionX = entity.getDirection() == Direction.RIGHT ? entity.getX() + entity.getCollisionAreaRectangle().x :
                (entity.getX() + entity.getCollisionAreaRectangle().x + entity.getCollisionAreaRectangle().width - entity.getHspeed());
        double entityCollisionY = entity.getDirection() == Direction.DOWN ? entity.getY() + entity.getCollisionAreaRectangle().y :
                (entity.getY() + entity.getCollisionAreaRectangle().y + entity.getCollisionAreaRectangle().height - entity.getVspeed());

        //counting row and column
        int entityColumn = (int)(entityCollisionX / gp.getWidthTileSize());
        int entityRow = (int)(entityCollisionY / gp.getHeightTileSize());

        //for going to another side of the board(horizontally)
        if (entityColumn - 1 < 0 || entityColumn + 1 >= gp.getMaxScreenColumn()) {
            switch (direction) {
                case UP, DOWN -> {
                    entity.collision = true;
                    return;
                }
                case RIGHT, LEFT, IDLE -> {
                    return;
                }
            }
        }

        //in order to point at the next(by checked direction) tile
        switch (direction) {
            case UP ->
                entityRow--;
            case DOWN ->
                entityRow++;
            case RIGHT ->
                entityColumn++;
            case LEFT ->
                entityColumn--;
        }

        //for ethereal player to not go out of borders or inside the ghosts house
        if ((entity instanceof Player) && ((Player)entity).isEthereal()) {
            if (neverPassableTileNums.contains(gp.getTileManager().getTiles()[entityRow][entityColumn].getTileType())) {
                entity.collision = true;
            }
            return;
        }

        //checking tile for collision
        if (gp.getTileManager().getTiles()[entityRow][entityColumn].isCollision()) {
            //for ghosts to be able to go through the gate
            entity.collision = !((entity instanceof Ghost) && gp.getTileManager().getTiles()[entityRow][entityColumn].getTileType() == 41 &&
                    (((Ghost) entity).isEaten() || ((Ghost) entity).isGoingOutOfCage()));
        }
    }

    public int checkEntities(Entity player, ArrayList<? extends Entity> entities) {
        int returnIndex = -1;
        player.getCollisionAreaRectangle().x += player.getX();
        player.getCollisionAreaRectangle().y += player.getY();

        //adjusting player collision to his movement
        switch (player.getDirection()) {
            case UP -> player.getCollisionAreaRectangle().y -= player.getVspeed();
            case DOWN -> player.getCollisionAreaRectangle().y += player.getVspeed();
            case RIGHT -> player.getCollisionAreaRectangle().x += player.getHspeed();
            case LEFT -> player.getCollisionAreaRectangle().x -= player.getHspeed();
        }

        for (int i = 0; i < entities.size(); i++) {
            //adjusting object's collision also
            entities.get(i).getCollisionAreaRectangle().x += entities.get(i).getX();
            entities.get(i).getCollisionAreaRectangle().y += entities.get(i).getY();

            //checking whether they intersect
            if (player.getCollisionAreaRectangle().intersects(entities.get(i).getCollisionAreaRectangle())) {
                returnIndex = i;
            }

            //returning object's collision to normal
            entities.get(i).getCollisionAreaRectangle().x = entities.get(i).getCollisionAreaDefaultX();
            entities.get(i).getCollisionAreaRectangle().y = entities.get(i).getCollisionAreaDefaultY();
        }

        //getting player collision to normal state
        player.getCollisionAreaRectangle().x = player.getCollisionAreaDefaultX();
        player.getCollisionAreaRectangle().y = player.getCollisionAreaDefaultY();
        return returnIndex;
    }
}
