package Entity;

import Main.Direction;
import Main.GamePanel;
import Main.GameState;
import Object.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Entity {

    Direction nextDirection = Direction.IDLE;
    public int lives = 3;
    private BufferedImage invisibleUp1, invisibleUp2, invisibleUp3, invisibleDown1, invisibleDown2, invisibleDown3,
    invisibleRight1, invisibleRight2, invisibleRight3, invisibleLeft1, invisibleLeft2, invisibleLeft3, invisibleIdle;
    private BufferedImage death4, death5, death6;
    public int eatenGhosts = 0;
    public boolean ethereal;
    public boolean invisible;
    public Player(GamePanel gp) {
        super(gp);
        loadImages();
        setDefaultValues();
    }


    public void setDefaultValues() {
        speed = 4;
        direction = Direction.IDLE;
        x = (int)(gp.getMapConstrains()[0][0] * gp.tileSize);
        y = (int)(gp.getMapConstrains()[0][1] * gp.tileSize);
        setCollisionAreaRectangle((int)(2.34 * gp.scale), (int)(2.34 * gp.scale), (int)(7.67 * gp.scale),
                (int)(7.67 * gp.scale));
        collision = false;
        ethereal = false;
        invisible = false;
        if (animationThread == null) {
            animationThread = createAnimationThread(30);
            animationThread.start();
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
                if (gp.gameState == GameState.PLAY) {

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
                else if (gp.gameState == GameState.DEAD){
                    try {
                        if (gp.deathAnimState == gp.deathAnimGoes) {
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
                            gp.deathAnimState = gp.deathAnimEnded;
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
        if (x + gp.tileSize <= 0){
            x = gp.maxScreenColumn * gp.tileSize;
        }
        else if (x >= gp.maxScreenColumn * gp.tileSize) {
            x = 0;
        }

        if (directionChanged) {
            animationThread.interrupt(); //to change player's direction animation instantly
            directionChanged = false;
        }

        //determining queued side
        if (gp.keyHandler.upPressed) {
            nextDirection = Direction.UP;
            gp.keyHandler.upPressed = false;
        }
        else if (gp.keyHandler.downPressed) {
            nextDirection = Direction.DOWN;
            gp.keyHandler.downPressed = false;
        }
        else if (gp.keyHandler.rightPressed) {
            nextDirection = Direction.RIGHT;
            gp.keyHandler.rightPressed = false;
        }
        else if (gp.keyHandler.leftPressed) {
            nextDirection = Direction.LEFT;
            gp.keyHandler.leftPressed = false;
        }

        if (tileChanged() || direction == Direction.IDLE) {
            //checking would we be able to move if we change direction
            gp.collisionChecker.checkIfCanMove(this, nextDirection);
            if (!collision && nextDirection != Direction.IDLE) {
                direction = nextDirection;
                nextDirection = Direction.IDLE;
                directionChanged = true;
            }
            else collision = false; //if no, then keeping this direction in memory and keeping moving as we wanted

            //checking collision for current direction:
            gp.collisionChecker.checkIfCanMove(this, direction);
        }

        //checking objects
        pickUpObject(gp.collisionChecker.checkEntities(this, gp.objects), gp.objects);

        //checking boosters
        pickUpObject(gp.collisionChecker.checkEntities(this, gp.boosters), gp.boosters);

        //interacting with ghosts
        interactWithGhost(gp.collisionChecker.checkEntities(this, gp.ghosts));

        //changing coords according to the direction
        if (!collision) {
            switch (direction) {
                case UP -> y -= speed;
                case DOWN -> y += speed;
                case RIGHT -> x += speed;
                case LEFT -> x -= speed;
            }
        }
        else direction = Direction.IDLE;
    }

    private void pickUpObject(int index, ArrayList<? extends Entity> passedObjects) {
        if (index != -1) {
            if (passedObjects.get(index) instanceof Point_obj) {
                gp.score += 10;
                passedObjects.remove(index);
            }
            else if (passedObjects.get(index) instanceof Power_pellet_obj) {
                gp.score += 50;
                passedObjects.remove(index);
                if (gp.pelletEaten) {
                    gp.behaviourThreadTask.frightenedCounter = gp.behaviourThreadTask.getFrightenedTiming();
                }
                else gp.pelletEaten = true;

                gp.ghostBehaviour.interrupt(); //in order to get them to frightened state instantly
            }
            else if (passedObjects.get(index) instanceof Booster) {
                gp.score += 50;
                ((Booster) passedObjects.get(index)).consumed = true;
                ((Booster) passedObjects.get(index)).timeCounter = 0;
                if (passedObjects.get(index) instanceof Speed_booster_obj) {
                    passedObjects.get(index).x = 8 * gp.tileSize;
                    passedObjects.get(index).y = 1;
                    speed += 2;
                }
                else if (passedObjects.get(index) instanceof Heart_booster_obj) {
                    passedObjects.remove(index);
                    lives++;
                }
                else if (passedObjects.get(index) instanceof Wall_piercer_booster_obj) {
                    passedObjects.get(index).x = 10 * gp.tileSize;
                    passedObjects.get(index).y = 1;
                    ethereal = true;
                }
                else if (passedObjects.get(index) instanceof Invisibility_booster_obj) {
                    passedObjects.get(index).x = 10 * gp.tileSize;
                    passedObjects.get(index).y = 1;
                    invisible = true;
                }
                else if (passedObjects.get(index) instanceof Ghosts_freezer_obj) {
                    passedObjects.remove(index);
                    if (gp.freezerEaten) {
                        gp.behaviourThreadTask.ghostFreezerTimer = gp.behaviourThreadTask.getFrozenTiming();
                    }
                    else gp.freezerEaten = true;
                    gp.ghostBehaviour.interrupt(); //in order to get them to frozen state instantly
                }
            }
        }
    }

    private void interactWithGhost(int index) {
        if (index != -1) {
            if (gp.pelletEaten && !gp.ghosts.get(index).eaten) {
                eatenGhosts++;
                gp.ghosts.get(index).frozen = false;
                gp.ghosts.get(index).eaten = true;
                gp.ghosts.get(index).speed = 8;
                gp.ghosts.get(index).animationThread.interrupt(); //to make ghost's appearance eaten instantly
                gp.ghosts.get(index).state = GhostState.EATEN;

                //counting points for eaten ghosts and awarding player
                int awardedPoints = (int)Math.pow(2, eatenGhosts) * 100;
                gp.score += awardedPoints;
                //displaying these points(by adding them into messagesFlow in UI class)
                gp.ui.messagesFlow.add(awardedPoints);
                gp.ui.messagesFlow.add(x);
                gp.ui.messagesFlow.add(y);
                //setting the counter for this message
                gp.ui.messagesCounter.add(60); // for 60 updates -- exactly one second
            }
            else if (!gp.ghosts.get(index).eaten) {
                gp.gameState = GameState.DEAD;
                lives--;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
    }
}
