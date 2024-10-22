package Entity;

import Main.Direction;
import Main.GamePanel;
import Main.GameState;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static Entity.GhostState.*;

public abstract class Ghost extends Entity {
    protected Point scatterCoords;
    protected static Point eatenCoords;
    protected static Point outOfCageCoords;
    protected Point xy;
    protected GhostState state;
    private  boolean eaten = false;
    private  boolean goingOutOfCage = false;
    private boolean wasEatenDuringPellet = false; //variable responsible for not frightening again after being eaten
    protected BufferedImage frightened1, frightened2, frightenedSoonEnd1, frightenedSoonEnd2, eatenUp, eatenDown,
            eatenRight, eatenLeft, frozenIdle1, frozenIdle2, frozenFrightened1, frozenFrightened2,
            frozenFrightenedSoonEnd1, frozenFrightenedSoonEnd2;

    private  boolean canDropBoosters = false;
    private  boolean frozen = false;

    public Ghost(GamePanel gp) {
        super(gp);
        loadImages();
        setDefaultValues();
        gp.getCharactersPanel().add(this);
    }

    @Override
    public void setDefaultValues() {
        switch (this) {
            case Blinky blinky -> {
                x = (int) (gp.getMapConstrains()[1][0] * gp.getWidthTileSize());
                y = (int) (gp.getMapConstrains()[1][1] * gp.getHeightTileSize());
                canDropBoosters = true; //made because he's not in the cage from the very beginning -> can drop them

                scatterCoords = new Point((gp.getMaxScreenColumn() - 3) * gp.getWidthTileSize(), 0);
                outOfCageCoords = new Point(eatenCoords.x, (eatenCoords.y - 3 * gp.getHeightTileSize()));
            }
            case Pinky pinky -> {
                x = (int) (gp.getMapConstrains()[2][0] * gp.getWidthTileSize());
                y = (int) (gp.getMapConstrains()[2][1] * gp.getHeightTileSize());
                scatterCoords = new Point(3 * gp.getHeightTileSize(), 0);
            }
            case Inky inky -> {
                x = (int) (gp.getMapConstrains()[3][0] * gp.getWidthTileSize());
                y = (int) (gp.getMapConstrains()[3][1] * gp.getHeightTileSize());
                scatterCoords = new Point(((gp.getMaxScreenColumn() - 1) * gp.getWidthTileSize()), ((gp.getMaxScreenColumn() - 1) * gp.getHeightTileSize()));
            }
            case Clyde clyde -> {
                x = (int) (gp.getMapConstrains()[4][0] * gp.getWidthTileSize());
                y = (int) (gp.getMapConstrains()[4][1] * gp.getHeightTileSize());
                scatterCoords = new Point(0, ((gp.getMaxScreenColumn() - 1) * gp.getHeightTileSize()));
            }
            default -> {
                x = 0;
                y = 0;
                scatterCoords = new Point(0, 0);
            }
        }
        setBounds(x, y, (int)(1.5 * gp.getWidthTileSize()), (int)(1.5 * gp.getHeightTileSize()));
        baseSpeed = 1;
        recalculateSpeed();
        baseCollisionStart = 2.34;
        baseCollisionEnd = 7.67;
        setCollisionAreaRectangle();
        prevColumn = x / gp.getWidthTileSize();
        prevRow = y / gp.getHeightTileSize();
        setAnimationThread(createAnimationThread(200));
        getAnimationThread().start();
    }

    @Override
    protected void loadImages() {
        frightened1 = getImage("/ghosts/ghost_frightened_1");
        frightened2 = getImage("/ghosts/ghost_frightened_2");
        frightenedSoonEnd1 = getImage("/ghosts/ghost_frightened_soon_end_1");
        frightenedSoonEnd2 = getImage("/ghosts/ghost_frightened_soon_end_2");
        eatenUp = getImage("/ghosts/ghost_eaten_up");
        eatenDown = getImage("/ghosts/ghost_eaten_down");
        eatenRight = getImage("/ghosts/ghost_eaten_right");
        eatenLeft = getImage("/ghosts/ghost_eaten_left");
        frozenFrightened1 = getImage("/ghosts/ghost_frightened_frozen_1");
        frozenFrightened2 = getImage("/ghosts/ghost_frightened_frozen_2");
        frozenFrightenedSoonEnd1 = getImage("/ghosts/ghost_frightened_frozen_soon_end_1");
        frozenFrightenedSoonEnd2 = getImage("/ghosts/ghost_frightened_frozen_soon_end_2");
    }

    @Override
    protected Thread createAnimationThread(int delay) {
        return new Thread(() -> {
            boolean blinkingWhite = false;

            while (animationRunning) {
                if (gp.getGameState() == GameState.PLAY) {
                    if (spriteNum == 2) spriteNum = 0;
                    spriteNum++;
                    if (state == CHASE || state == SCATTER) {
                        switch (direction) {
                            case UP -> {
                                if (spriteNum == 1) currentImage = up1;
                                else if (spriteNum == 2) currentImage = up2;
                            }

                            case DOWN -> {
                                if (spriteNum == 1) currentImage = down1;
                                else if (spriteNum == 2) currentImage = down2;
                            }

                            case RIGHT -> {
                                if (spriteNum == 1) currentImage = right1;
                                else if (spriteNum == 2) currentImage = right2;
                            }

                            case LEFT -> {
                                if (spriteNum == 1) currentImage = left1;
                                else if (spriteNum == 2) currentImage = left2;
                            }
                            case IDLE -> {
                                if (spriteNum == 1) currentImage = idle1;
                                else if (spriteNum == 2) currentImage = idle2;
                            }
                        }
                        if (frozen) {
                            if (spriteNum == 1) currentImage = frozenIdle1;
                            else if (spriteNum == 2) currentImage = frozenIdle2;
                        }
                    }
                    else if (state == FRIGHTENED) {
                        if (gp.getGhostsBehaviourThreadTask().getFrightenedCounter() < 2) {
                            //ghosts start blinking when frightened time comes to an end
                            if (spriteNum == 1 && blinkingWhite) {
                                currentImage = !frozen ? frightenedSoonEnd1 : frozenFrightenedSoonEnd1;
                            }
                            else if (spriteNum == 1){
                                currentImage = !frozen ? frightened1 : frozenFrightened1;
                            }
                            else if (spriteNum == 2 && blinkingWhite) {
                                currentImage = !frozen ? frightenedSoonEnd2 : frozenFrightenedSoonEnd2;
                                blinkingWhite = false;
                            }
                            else {
                                currentImage = !frozen ? frightened2 : frozenFrightened2;
                                blinkingWhite = true;
                            }
                        }
                        else {
                            if (spriteNum == 1) currentImage = !frozen ? frightened1 : frozenFrightened1;
                            else if (spriteNum == 2) currentImage = !frozen ? frightened2 : frozenFrightened2;
                        }
                    }
                    else if (state == EATEN) {
                        switch (direction) {
                            case UP -> currentImage = eatenUp;
                            case DOWN -> currentImage = eatenDown;
                            case RIGHT -> currentImage = eatenRight;
                            case LEFT -> currentImage = eatenLeft;
                        }
                    }
                }


                //delay
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    //just to change the icon
                }
            }
        });
    }

    protected abstract Point findInChaseMode();

    protected Point findTarget() {
        if (goingOutOfCage) {
            return outOfCageCoords;
        }
        else if (state == EATEN) return eatenCoords;
        else if (state == FRIGHTENED || gp.getPlayer().isInvisible()) {
            switch (new Random().nextInt(4)) {
                case 0 -> {
                    return new Point(currentColumn * gp.getWidthTileSize(), (currentRow - 1) * gp.getHeightTileSize()); //up
                }
                case 1 -> {
                    return new Point((currentColumn - 1) * gp.getWidthTileSize(), (currentRow) * gp.getHeightTileSize()); //left
                }
                case 2 -> {
                    return new Point ((currentColumn * gp.getWidthTileSize()), (currentRow + 1) * gp.getHeightTileSize()); //down
                }
                case 3 -> {
                    return new Point((currentColumn + 1) * gp.getWidthTileSize(), currentRow * gp.getHeightTileSize()); //right
                }
                default -> {
                    return new Point(0, 0);
                }
            }
        }
        else if (state == SCATTER) return scatterCoords;
        else return findInChaseMode();
    }

    protected void determineNextDirection(double targetX, double targetY) {
        //making all possible vectors to the target location and finding shortest of them
        //Possible are: not the previous direction + those, which do not meet collision
        HashMap<Direction, Double> vectors = new HashMap<>();
        Direction shortestVectorDirection = null;
        for (Direction checkedDirection : Arrays.copyOfRange(Direction.values(), 0,
                Direction.values().length - 1)) {
            if (Math.abs(direction.ordinal() - checkedDirection.ordinal()) != 2) {
                //gp.collisionChecker.checkIfCanMove(this, checkedDirection);
                gp.getCollisionChecker().checkIfCanMove(this, checkedDirection);
                if (!collision) {
                    switch (checkedDirection) {
                        case UP -> vectors.put(checkedDirection, calculateVectorDistance(x, y - gp.getHeightTileSize(),
                                    targetX, targetY));
                        case LEFT -> vectors.put(checkedDirection, calculateVectorDistance(x - gp.getWidthTileSize(), y,
                                targetX, targetY));
                        case DOWN -> vectors.put(checkedDirection, calculateVectorDistance(x, y + gp.getHeightTileSize(),
                                targetX, targetY));
                        case RIGHT -> vectors.put(checkedDirection, calculateVectorDistance(x + gp.getWidthTileSize(), y,
                                targetX, targetY));
                    }
                    if (shortestVectorDirection == null) shortestVectorDirection = checkedDirection;
                    else if (vectors.get(shortestVectorDirection) > vectors.get(checkedDirection))
                        shortestVectorDirection = checkedDirection;
                }
                else collision = false;
            }
        }
        direction = shortestVectorDirection;

    }

    protected double calculateVectorDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    @Override
    public void update() {
        //for going left-right right-left border
        if (x + gp.getWidthTileSize() <= 0) {
            x = gp.getMaxScreenColumn() * gp.getWidthTileSize();
        }
        else if (x >= gp.getMaxScreenColumn() * gp.getWidthTileSize()) {
            x = 0;
        }

        if (tileChanged()) {
            //to get back to normal states after being eaten
            if (eaten && currentColumn * gp.getWidthTileSize() == eatenCoords.x && currentRow * gp.getHeightTileSize() == eatenCoords.y) {
                eaten = false;
                wasEatenDuringPellet = true;
                goingOutOfCage = true;
                recalculateSpeed();
            }
            //getting to normal state after going out of cage
            else if (goingOutOfCage && (currentColumn == outOfCageCoords.x / gp.getWidthTileSize()
                    || currentColumn == outOfCageCoords.x / gp.getWidthTileSize() - 1) &&
                    (currentRow == outOfCageCoords.y / gp.getHeightTileSize())) {
                goingOutOfCage = false;
                canDropBoosters = true;
            }
            xy = findTarget();
            determineNextDirection(xy.x, xy.y);
            getAnimationThread().interrupt(); //to change ghost's direction animation instantly
        }

        //since we know that we can move to the new direction, then
        if (!frozen) {
            switch(direction) {
                case Direction.UP -> y -= vspeed;
                case Direction.DOWN -> y += vspeed;
                case Direction.RIGHT -> x += hspeed;
                case Direction.LEFT -> x -= hspeed;
            }
        }
    }
    @Override
    public void resize() {
        super.resize();
        setBounds(x, y, (int)(1.5 * gp.getWidthTileSize()), (int)(1.5 * gp.getHeightTileSize()));
        scatterCoords = new Point((int)(scatterCoords.x * gp.getWidthRatio()), (int)(scatterCoords.y * gp.getHeightRatio()));
        if (this instanceof Blinky) {
            eatenCoords = new Point((int)(eatenCoords.x * gp.getWidthRatio()), (int)(eatenCoords.y * gp.getHeightRatio()));
            outOfCageCoords = new Point((int)(outOfCageCoords.x * gp.getWidthRatio()), (int)(outOfCageCoords.y * gp.getHeightRatio()));
        }
    }

    //GETTERS & SETTERS

    //getters
    public boolean isEaten() {
        return eaten;
    }
    public boolean isGoingOutOfCage() {
        return goingOutOfCage;
    }
    public boolean isWasEatenDuringPellet() {
        return wasEatenDuringPellet;
    }
    public boolean isCanDropBoosters() {
        return canDropBoosters;
    }

    //-----------------------------------------------

    //setters
    public static void setEatenCoords(Point eatenCoords) {
        Ghost.eatenCoords = eatenCoords;
    }
    public void setGoingOutOfCage(boolean goingOutOfCage) {
        this.goingOutOfCage = goingOutOfCage;
    }
    public void setWasEatenDuringPellet(boolean wasEatenDuringPellet) {
        this.wasEatenDuringPellet = wasEatenDuringPellet;
    }
    public void setCanDropBoosters(boolean canDropBoosters) {
        this.canDropBoosters = canDropBoosters;
    }
    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }
    public void setEaten(boolean eaten) {
        this.eaten = eaten;
    }
}
