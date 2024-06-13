package Main;
import Entity.Entity;
import java.util.ArrayList;
import java.util.Arrays;

import Entity.Ghost;
import Entity.Player;


public class CollisionChecker {
    GamePanel gp;
    private final ArrayList<Integer> neverPassableTileNums = new ArrayList<>(Arrays.asList(17, 18, 19, 20, 29, 30, 31, 32, 39));

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkIfCanMove(Entity entity, Direction direction) {
        int entityCollisionX = entity.direction == Direction.RIGHT ? entity.x + entity.collisionAreaRectangle.x :
                entity.x + entity.collisionAreaRectangle.x + entity.collisionAreaRectangle.width - entity.speed;
        int entityCollisionY = entity.direction == Direction.DOWN ? entity.y + entity.collisionAreaRectangle.y :
                entity.y + entity.collisionAreaRectangle.y + entity.collisionAreaRectangle.height - entity.speed;

        int entityColumn = entityCollisionX / gp.tileSize;
        int entityRow = entityCollisionY / gp.tileSize;

        //for going to another side of the board(horizontally)
        if (entityColumn - 1 < 0 || entityColumn + 1 >= gp.maxScreenColumn) {
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

        //for ethereal player to not go out of borders
        if ((entity instanceof Player) && ((Player)entity).ethereal) {
            if (neverPassableTileNums.contains(gp.tileManager.tiles[entityRow][entityColumn].tileType)) {
                entity.collision = true;
            }
            return;
        }

        if (gp.tileManager.tiles[entityRow][entityColumn] == null) {
            System.out.println("reached");
        }

        if (gp.tileManager.tiles[entityRow][entityColumn].collision) {
            //for ghosts to be able to go through the gate
            entity.collision = !((entity instanceof Ghost) && gp.tileManager.tiles[entityRow][entityColumn].tileType == 41 &&
                    (((Ghost) entity).eaten || ((Ghost) entity).goingOutOfCage));
        }
    }

    public int checkEntities(Entity player, ArrayList<? extends Entity> entities) {
        int returnIndex = -1;
        player.collisionAreaRectangle.x += player.x;
        player.collisionAreaRectangle.y += player.y;

        //adjusting player collision to his movement
        switch (player.direction) {
            case UP -> player.collisionAreaRectangle.y -= player.speed;
            case DOWN -> player.collisionAreaRectangle.y += player.speed;
            case RIGHT -> player.collisionAreaRectangle.x += player.speed;
            case LEFT -> player.collisionAreaRectangle.x -= player.speed;
        }

        for (int i = 0; i < entities.size(); i++) {
            //adjusting object's collision also
            entities.get(i).collisionAreaRectangle.x += entities.get(i).x;
            entities.get(i).collisionAreaRectangle.y += entities.get(i).y;

            //checking whether they intersect
            if (player.collisionAreaRectangle.intersects(entities.get(i).collisionAreaRectangle)) {
                returnIndex = i;
            }

            //returning object's collision to normal
            entities.get(i).collisionAreaRectangle.x = entities.get(i).collisionAreaDefaultX;
            entities.get(i).collisionAreaRectangle.y = entities.get(i).collisionAreaDefaultY;
        }

        //getting player collision to normal state
        player.collisionAreaRectangle.x = player.collisionAreaDefaultX;
        player.collisionAreaRectangle.y = player.collisionAreaDefaultY;
        return returnIndex;
    }
}
