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
    protected int[] scatterCoords;
    protected static int[] eatenCoords;
    protected static int[] outOfCageCoords;
    protected int[] xy;
    protected GhostState state;
    public boolean eaten = false;
    public boolean goingOutOfCage = false;
    public boolean wasEatenDuringPellet = false; //variable responsible for not frightening again after being eaten
    protected BufferedImage frightened1, frightened2, frightenedSoonEnd1, frightenedSoonEnd2, eatenUp, eatenDown,
            eatenRight, eatenLeft, frozenIdle1, frozenIdle2, frozenFrightened1, frozenFrightened2,
            frozenFrightenedSoonEnd1, frozenFrightenedSoonEnd2;

    public boolean canDropBoosters = false;
    public boolean frozen = false;

    public Ghost(GamePanel gp) {
        super(gp);
        loadImages();
        setDefaultValues();
    }

    @Override
    public void setDefaultValues() {
        switch (this) {
            case Blinky blinky -> {
                x = (int) (gp.getMapConstrains()[1][0] * gp.tileSize);
                y = (int) (gp.getMapConstrains()[1][1] * gp.tileSize);
                canDropBoosters = true; //made because he's not in the cage from the very beginning -> can drop them

                scatterCoords = new int[]{(gp.maxScreenColumn - 3) * gp.tileSize, 0};
            }
            case Pinky pinky -> {
                x = (int) (gp.getMapConstrains()[2][0] * gp.tileSize);
                y = (int) (gp.getMapConstrains()[2][1] * gp.tileSize);
                scatterCoords = new int[]{3 * gp.tileSize, 0};
            }
            case Inky inky -> {
                x = (int) (gp.getMapConstrains()[3][0] * gp.tileSize);
                y = (int) (gp.getMapConstrains()[3][1] * gp.tileSize);
                scatterCoords = new int[]{(gp.maxScreenColumn - 1) * gp.tileSize, (gp.maxScreenRow - 1) * gp.tileSize};
            }
            case Clyde clyde -> {
                x = (int) (gp.getMapConstrains()[4][0] * gp.tileSize);
                y = (int) (gp.getMapConstrains()[4][1] * gp.tileSize);
                scatterCoords = new int[]{0, (gp.maxScreenRow - 1) * gp.tileSize};
            }
            default -> {
                x = 0;
                y = 0;
                scatterCoords = new int[]{0, 0,};
            }
        }
        setCollisionAreaRectangle((int)(2.34 * gp.scale), (int)(2.34 * gp.scale), (int)(7.67 * gp.scale),
                (int)(7.67 * gp.scale));
        speed = 3;
        prevColumn = x / gp.tileSize;
        prevRow = y / gp.tileSize;
        animationThread = createAnimationThread(200);
        animationThread.start();
    }

    @Override
    protected void loadImages() {
        frightened1 = getImage("res/ghosts/ghost_frightened_1");
        frightened2 = getImage("res/ghosts/ghost_frightened_2");
        frightenedSoonEnd1 = getImage("res/ghosts/ghost_frightened_soon_end_1");
        frightenedSoonEnd2 = getImage("res/ghosts/ghost_frightened_soon_end_2");
        eatenUp = getImage("res/ghosts/ghost_eaten_up");
        eatenDown = getImage("res/ghosts/ghost_eaten_down");
        eatenRight = getImage("res/ghosts/ghost_eaten_right");
        eatenLeft = getImage("res/ghosts/ghost_eaten_left");
        frozenFrightened1 = getImage("res/ghosts/ghost_frightened_frozen_1");
        frozenFrightened2 = getImage("res/ghosts/ghost_frightened_frozen_2");
        frozenFrightenedSoonEnd1 = getImage("res/ghosts/ghost_frightened_frozen_soon_end_1");
        frozenFrightenedSoonEnd2 = getImage("res/ghosts/ghost_frightened_frozen_soon_end_2");
    }

    @Override
    protected Thread createAnimationThread(int delay) {
        return new Thread(() -> {
            boolean blinkingWhite = false;

            while (animationRunning) {
                if (gp.gameState == GameState.PLAY) {
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
                        if (gp.behaviourThreadTask.frightenedCounter < 2) {
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

    protected abstract int[] findInChaseMode();

    protected int[] findTarget() {
        if (goingOutOfCage) {
            return outOfCageCoords;
        }
        else if (state == EATEN) return eatenCoords;
        else if (state == FRIGHTENED || gp.player.invisible) {
            switch (new Random().nextInt(4)) {
                case 0 -> {
                    return new int[]{(currentColumn) * gp.tileSize, (currentRow - 1) * gp.tileSize}; //up
                }
                case 1 -> {
                    return new int[]{(currentColumn - 1) * gp.tileSize, (currentRow) * gp.tileSize}; //left
                }
                case 2 -> {
                    return new int[]{(currentColumn) * gp.tileSize, (currentRow + 1) * gp.tileSize}; //down
                }
                case 3 -> {
                    return new int[]{(currentColumn + 1) * gp.tileSize, (currentRow) * gp.tileSize}; //right
                }
                default -> {
                    return new int[0];
                }
            }
        }
        else if (state == SCATTER) return scatterCoords;
        else return findInChaseMode();
    }

    protected void determineNextDirection(int targetX, int targetY) {
        //making all possible vectors to the target location and finding shortest of them
        //Possible are: not the previous direction + those, which do not meet collision
        HashMap<Direction, Integer> vectors = new HashMap<>();
        Direction shortestVectorDirection = null;
        for (Direction checkedDirection : Arrays.copyOfRange(Direction.values(), 0,
                Direction.values().length - 1)) {
            if (Math.abs(direction.ordinal() - checkedDirection.ordinal()) != 2) {
                //gp.collisionChecker.checkIfCanMove(this, checkedDirection);
                gp.collisionChecker.checkIfCanMove(this, checkedDirection);
                if (!collision) {
                    switch (checkedDirection) {
                        case UP -> {
                            if (!((currentColumn == 9 || currentColumn == 12) && (currentRow == 14 || currentRow == 26)))
                                vectors.put(checkedDirection, calculateVectorDistance(x, y - gp.tileSize,
                                        targetX, targetY));
                            else vectors.put(checkedDirection, 99999);
                        }
                        case LEFT -> vectors.put(checkedDirection, calculateVectorDistance(x - gp.tileSize, y,
                                targetX, targetY));
                        case DOWN -> vectors.put(checkedDirection, calculateVectorDistance(x, y + gp.tileSize,
                                targetX, targetY));
                        case RIGHT -> vectors.put(checkedDirection, calculateVectorDistance(x + gp.tileSize, y,
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

    protected int calculateVectorDistance(int x1, int y1, int x2, int y2) {
        return (int)Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    @Override
    public void update() {
        //for going left-right right-left border
        if (x + gp.tileSize <= 0) {
            x = gp.maxScreenColumn * gp.tileSize;
        }
        else if (x >= gp.maxScreenColumn * gp.tileSize) {
            x = 0;
        }

        if (tileChanged()) {
            //to get back to normal states after being eaten
            if (eaten && currentColumn * gp.tileSize == eatenCoords[0] && currentRow * gp.tileSize == eatenCoords[1]) {
                eaten = false;
                wasEatenDuringPellet = true;
                goingOutOfCage = true;
                speed = 3;
            }
            else if (goingOutOfCage && (currentColumn == outOfCageCoords[0] / gp.tileSize
                    || currentColumn == outOfCageCoords[0] / gp.tileSize - 1) &&
                    (currentRow == outOfCageCoords[1] / gp.tileSize)) {
                goingOutOfCage = false;
                canDropBoosters = true;
            }
            xy = findTarget();
            determineNextDirection(xy[0], xy[1]);
            animationThread.interrupt(); //to change ghost's direction animation instantly
        }

        //since we know that we can move to the new direction, then
        if (!frozen) {
            switch(direction) {
                case Direction.UP -> y -= speed;
                case Direction.DOWN -> y += speed;
                case Direction.RIGHT -> x += speed;
                case Direction.LEFT -> x -= speed;
            }
        }
    }

    public static void setEatenCoords(int[] eatenXY) {
        eatenCoords = eatenXY;
        outOfCageCoords = new int[]{eatenCoords[0], eatenCoords[1] - 3 * 24};
    }
}
