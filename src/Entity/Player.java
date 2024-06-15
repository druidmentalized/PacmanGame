package Entity;

import Main.Direction;
import Main.GamePanel;
import Main.GameState;
import Object.*;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Entity {

    Direction nextDirection = Direction.IDLE;
    private int lives = 3;
    private BufferedImage invisibleUp1, invisibleUp2, invisibleUp3, invisibleDown1, invisibleDown2, invisibleDown3,
    invisibleRight1, invisibleRight2, invisibleRight3, invisibleLeft1, invisibleLeft2, invisibleLeft3, invisibleIdle;
    private BufferedImage death4, death5, death6;
    private int eatenGhosts = 0;
    private boolean ethereal;
    private boolean invisible;

    public Player(GamePanel gp) {
        super(gp);
        loadImages();
        setDefaultValues();
        gp.getCharactersPanel().add(this);
    }

    public void setDefaultValues() {
        baseSpeed = 1.34;
        recalculateSpeed();
        baseCollisionStart = 2.34;
        baseCollisionEnd = 7.67;
        setCollisionAreaRectangle();
        direction = Direction.IDLE;
        x = (int)(gp.getMapConstrains()[0][0] * gp.getWidthTileSize());
        y = (int)(gp.getMapConstrains()[0][1] * gp.getHeightTileSize());
        setBounds(x, y, (int)(1.5 * gp.getWidthTileSize()), (int)(1.5 * gp.getHeightTileSize()));
        collision = false;
        ethereal = false;
        invisible = false;
        if (getAnimationThread() == null) {
            setAnimationThread(createAnimationThread(30));
            getAnimationThread().start();
        }
    }

    @Override
    protected void loadImages() {
        up1 = getImage("res/player/pacman_up_1");
        up2 = getImage("res/player/pacman_up_2");
        up3 = getImage("res/player/pacman_up_3");

        down1 = getImage("res/player/pacman_down_1");
        down2 = getImage("res/player/pacman_down_2");
        down3 = getImage("res/player/pacman_down_3");

        right1 = getImage("res/player/pacman_right_1");
        right2 = getImage("res/player/pacman_right_2");
        right3 = getImage("res/player/pacman_right_3");

        left1 = getImage("res/player/pacman_left_1");
        left2 = getImage("res/player/pacman_left_2");
        left3 = getImage("res/player/pacman_left_3");

        idle1 = getImage("res/player/pacman_idle");

        death4 = getImage("res/player/pacman_death_4");
        death5 = getImage("res/player/pacman_death_5");
        death6 = getImage("res/player/pacman_death_6");

        invisibleIdle = getImage("res/player/pacman_invisible_idle");

        invisibleUp1 = getImage("res/player/pacman_invisible_up_1");
        invisibleUp2 = getImage("res/player/pacman_invisible_up_2");
        invisibleUp3 = getImage("res/player/pacman_invisible_up_3");

        invisibleDown1 = getImage("res/player/pacman_invisible_down_1");
        invisibleDown2 = getImage("res/player/pacman_invisible_down_2");
        invisibleDown3 = getImage("res/player/pacman_invisible_down_3");

        invisibleRight1 = getImage("res/player/pacman_invisible_right_1");
        invisibleRight2 = getImage("res/player/pacman_invisible_right_2");
        invisibleRight3 = getImage("res/player/pacman_invisible_right_3");

        invisibleLeft1 = getImage("res/player/pacman_invisible_left_1");
        invisibleLeft2 = getImage("res/player/pacman_invisible_left_2");
        invisibleLeft3 = getImage("res/player/pacman_invisible_left_3");
    }

    @Override
    protected Thread createAnimationThread(int delay) {
        return new Thread(() -> {
            while (animationRunning) {
                if (gp.getGameState() == GameState.PLAY) {
                    if (spriteNum == 5) spriteNum = 0;
                    spriteNum++;

                    if (!invisible) {
                        decideImage(idle1, up1, up2, up3, down1, down2, down3, right1, right2, right3, left1, left2, left3);
                    }
                    else {
                        decideImage(invisibleIdle, invisibleUp1, invisibleUp2, invisibleUp3, invisibleDown1, invisibleDown2, invisibleDown3, invisibleRight1, invisibleRight2, invisibleRight3, invisibleLeft1, invisibleLeft2, invisibleLeft3);
                    }

                    if (ethereal && currentImage != null && spriteNum % 2 == 1) {
                        currentImage = null;
                    }
                }
                else if (gp.getGameState() == GameState.DEAD){
                    try {
                        if (gp.getDeathAnimState() == gp.getDeathAnimGoes()) {
                            currentImage = idle1;
                            Thread.sleep(200);
                            currentImage = up1;
                            Thread.sleep(200);
                            currentImage = up2;
                            Thread.sleep(200);
                            currentImage = up3;
                            Thread.sleep(200);
                            currentImage = death4;
                            Thread.sleep(200);
                            currentImage = death5;
                            Thread.sleep(200);
                            currentImage = death6;
                            Thread.sleep(200);
                            currentImage = null;
                            Thread.sleep(200);
                            gp.setDeathAnimState(gp.getDeathAnimEnded());
                        }
                    } catch (InterruptedException e) {
                        //nothing
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

    private void decideImage(BufferedImage idle, BufferedImage up1, BufferedImage up2, BufferedImage up3, BufferedImage
            down1, BufferedImage down2, BufferedImage down3, BufferedImage right1, BufferedImage right2, BufferedImage
            right3, BufferedImage left1, BufferedImage left2, BufferedImage left3) {
        switch (direction) {
            case Direction.UP -> {
                if (spriteNum == 1) currentImage = idle;
                else if (spriteNum == 2) currentImage = up1;
                else if (spriteNum == 3) currentImage = up2;
                else if (spriteNum == 4) currentImage = up3;
            }

            case Direction.DOWN -> {
                if (spriteNum == 1) currentImage = idle;
                else if (spriteNum == 2) currentImage = down1;
                else if (spriteNum == 3) currentImage = down2;
                else if (spriteNum == 4) currentImage = down3;
            }

            case Direction.RIGHT -> {
                if (spriteNum == 1) currentImage = idle;
                else if (spriteNum == 2) currentImage = right1;
                else if (spriteNum == 3) currentImage = right2;
                else if (spriteNum == 4) currentImage = right3;
            }

            case Direction.LEFT -> {
                if (spriteNum == 1) currentImage = idle;
                else if (spriteNum == 2) currentImage = left1;
                else if (spriteNum == 3) currentImage = left2;
                else if (spriteNum == 4) currentImage = left3;
            }
            case Direction.IDLE -> currentImage = idle;
        }
    }

    @Override
    public void update() {
        //for going left-right right-left border
        if (x + gp.getWidthTileSize() <= 0){
            x = gp.getMaxScreenColumn() * gp.getWidthTileSize();
        }
        else if (x >= gp.getMaxScreenColumn() * gp.getWidthTileSize()) {
            x = 0;
        }

        //to change player's direction animation instantly
        if (isDirectionChanged()) {
            getAnimationThread().interrupt();
            setDirectionChanged(false);
        }

        //determining queued side
        if (gp.getKeyHandler().isUpPressed()) {
            nextDirection = Direction.UP;
            gp.getKeyHandler().setUpPressed(false);
        }
        else if (gp.getKeyHandler().isDownPressed()) {
            nextDirection = Direction.DOWN;
            gp.getKeyHandler().setDownPressed(false);
        }
        else if (gp.getKeyHandler().isRightPressed()) {
            nextDirection = Direction.RIGHT;
            gp.getKeyHandler().setRightPressed(false);
        }
        else if (gp.getKeyHandler().isLeftPressed()) {
            nextDirection = Direction.LEFT;
            gp.getKeyHandler().setLeftPressed(false);
        }

        //checking only if on the next tile or queued direction is something
        if (tileChanged() || direction == Direction.IDLE) {
            //checking would we be able to move if we change direction
            gp.getCollisionChecker().checkIfCanMove(this, nextDirection);
            if (!collision && nextDirection != Direction.IDLE) {
                direction = nextDirection;
                nextDirection = Direction.IDLE;
                setDirectionChanged(true);
            }
            else collision = false; //if no, then keeping this direction in memory and keeping moving as we wanted

            //checking collision for current direction:
            gp.getCollisionChecker().checkIfCanMove(this, direction);
        }

        //checking objects
        pickUpObject(gp.getCollisionChecker().checkEntities(this, gp.getObjects()), gp.getObjects());

        //checking boosters
        pickUpObject(gp.getCollisionChecker().checkEntities(this, gp.getBoosters()), gp.getBoosters());

        //interacting with ghosts
        interactWithGhost(gp.getCollisionChecker().checkEntities(this, gp.getGhosts()));

        //changing coords according to the direction
        if (!collision) {
            switch (direction) {
                case UP -> y -= vspeed;
                case DOWN -> y += vspeed;
                case RIGHT -> x += hspeed;
                case LEFT -> x -= hspeed;
            }
        }
        else direction = Direction.IDLE;
    }

    private void pickUpObject(int index, ArrayList<? extends Entity> passedObjects) {
        //checking if we actually interacted with something
        if (index != -1) {
            if (passedObjects.get(index) instanceof Point_obj) {
                gp.setScore(gp.getScore() + 10);
                gp.getEatablesPanel().remove(passedObjects.get(index));
                passedObjects.remove(index);
            }
            else if (passedObjects.get(index) instanceof Power_pellet_obj) {
                gp.setScore(gp.getScore() + 50);
                gp.getEatablesPanel().remove(passedObjects.get(index));
                passedObjects.remove(index);
                if (gp.isPelletEaten()) {
                    gp.getGhostsBehaviourThreadTask().setFrightenedCounter(gp.getGhostsBehaviourThreadTask().getFrightenedTiming());
                }
                else gp.setPelletEaten(true);

                gp.getGhostBehaviour().interrupt(); //in order to get them to frightened state instantly
            }
            else if (passedObjects.get(index) instanceof Booster) {
                gp.setScore(gp.getScore() + 50);
                ((Booster) passedObjects.get(index)).setConsumed(true);
                ((Booster) passedObjects.get(index)).setTimeCounter(0);
                if (passedObjects.get(index) instanceof Speed_booster_obj) {
                    passedObjects.get(index).x = 8 * gp.getWidthTileSize();
                    passedObjects.get(index).y = 1;
                    hspeed += (int)(0.67 * gp.getWidthScale());
                    vspeed += (int)(0.67 * gp.getHeightScale());
                }
                else if (passedObjects.get(index) instanceof Heart_booster_obj) {
                    gp.getEatablesPanel().remove(passedObjects.get(index));
                    passedObjects.remove(index);
                    lives++;
                    gp.getUi().addHealth();
                }
                else if (passedObjects.get(index) instanceof Wall_piercer_booster_obj) {
                    passedObjects.get(index).x = 10 * gp.getWidthTileSize();
                    passedObjects.get(index).y = 1;
                    ethereal = true;
                }
                else if (passedObjects.get(index) instanceof Invisibility_booster_obj) {
                    passedObjects.get(index).x = 10 * gp.getWidthTileSize();
                    passedObjects.get(index).y = 1;
                    invisible = true;
                }
                else if (passedObjects.get(index) instanceof Ghosts_freezer_obj) {
                    gp.getEatablesPanel().remove(passedObjects.get(index));
                    passedObjects.remove(index);
                    if (gp.isFreezerEaten()) {
                        gp.getGhostsBehaviourThreadTask().setGhostFreezerTimer(gp.getGhostsBehaviourThreadTask().getFrozenTiming());
                    }
                    else gp.setFreezerEaten(true);
                    gp.getGhostBehaviour().interrupt(); //in order to get them to frozen state instantly
                }
            }
        }
    }

    private void interactWithGhost(int index) {
        if (index != -1) {
            if (gp.isPelletEaten() && !gp.getGhosts().get(index).isEaten() && !gp.getGhosts().get(index).isWasEatenDuringPellet())  {
                eatenGhosts++;
                gp.getGhosts().get(index).setFrozen(false);
                gp.getGhosts().get(index).setEaten(true);
                gp.getGhosts().get(index).hspeed = (int)(2.67 * gp.getWidthScale());
                gp.getGhosts().get(index).vspeed = (int)(2.67 * gp.getHeightScale());
                gp.getGhosts().get(index).getAnimationThread().interrupt(); //to make ghost's appearance eaten instantly
                gp.getGhosts().get(index).state = GhostState.EATEN;

                //counting points for eaten ghosts and awarding player
                int awardedPoints = (int)Math.pow(2, eatenGhosts) * 100;
                gp.setScore(gp.getScore() + awardedPoints);

                //displaying these points(by adding them into messagesFlow in UI class)
                JLabel ghostDeadMessage = new JLabel(String.valueOf(awardedPoints));
                ghostDeadMessage.setBounds(x, y, gp.getWidthTileSize() * 2, gp.getHeightTileSize());
                gp.getUi().getMessagesFlow().add(ghostDeadMessage);
                gp.getUiPanel().add(ghostDeadMessage);

                //setting the counter for this message
                gp.getUi().getMessagesCounter().add(60); // for 60 updates -- exactly one second
            }
            else if (!gp.getGhosts().get(index).isEaten()) {
                gp.setGameState(GameState.DEAD);
                lives--;
                gp.getUi().deductHealth();
            }
        }
    }

    @Override
    public void resize() {
        super.resize();
        setBounds(x, y, (int)(1.5 * gp.getWidthTileSize()), (int)(1.5 * gp.getHeightTileSize()));
    }

    //GETTERS & SETTERS
    //getters
    public int getLives() {
        return lives;
    }
    public boolean isEthereal() {
        return ethereal;
    }
    public boolean isInvisible() {
        return invisible;
    }

    //------------------------------
    //setters
    public void setEatenGhosts(int eatenGhosts) {
        this.eatenGhosts = eatenGhosts;
    }
    public void setEthereal(boolean ethereal) {
        this.ethereal = ethereal;
    }
    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }
}
