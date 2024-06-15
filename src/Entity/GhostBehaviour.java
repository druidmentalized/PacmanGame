package Entity;

import Main.GamePanel;
import Main.GameState;
import Object.*;
import java.util.Random;

import static Entity.GhostState.*;

public class GhostBehaviour implements Runnable {
    GamePanel gp;
    private boolean threadRunning = true;
    private final static GhostState[] ghostStates = new GhostState[]{SCATTER, CHASE, SCATTER, CHASE, SCATTER, CHASE, SCATTER, CHASE};
    private static int[] stateTimings;
    private static int frightenedTiming;
    private static int frozenTiming;
    private int frightenedCounter = 0;
    private int ghostFreezerTimer = 0;
    public GhostBehaviour(GamePanel gp) {
        this.gp = gp;

        //setting timings of states
        switch(gp.getLevel()) {
            case 1 -> stateTimings = new int[]{7, 20, 7, 20, 5, 20, 5};
            case 2, 3, 4 -> stateTimings = new int[]{7, 20, 7, 20, 5, 1033, 1};
            default -> stateTimings = new int[]{5, 20, 5, 20, 5, 1033, 1};
        }

        //setting timings of fright
        switch (gp.getLevel()) {
            case 1, 2 -> frightenedTiming = 6;
            case 3, 4, 5 -> frightenedTiming = 5;
            case 6, 7, 8 -> frightenedTiming = 4;
            case 9, 10, 11 -> frightenedTiming = 3;
            case 12, 13, 14 -> frightenedTiming = 2;
            default -> frightenedTiming = 0;
        }

        //setting timings of frozen state
        switch (gp.getLevel()) {
            case 1, 2, 3 -> frozenTiming = 6;
            case 4, 5, 6 -> frozenTiming = 5;
            case 7, 8, 9 -> frozenTiming = 4;
            default -> frozenTiming = 3;
        }
    }

    @Override
    public void run() {
        int secCounter = 0;
        int totalTimeCounter = 0;
        int currentState = -1;
        while (threadRunning) {
            if (gp.getGameState() == GameState.PLAY) {
                totalTimeCounter++;

                //changing ghosts states depending on level
                if (secCounter == 0) {
                    currentState++;
                    if (currentState < stateTimings.length) secCounter = stateTimings[currentState];
                }
                secCounter--;

                frightenedCounter--;
                //power pellet timer
                if (frightenedCounter == 0) {
                    gp.setPelletEaten(false);
                    for (Ghost ghost : gp.getGhosts()) {
                        ghost.setWasEatenDuringPellet(false);
                        gp.getPlayer().setEatenGhosts(0);
                        ghost.setCanDropBoosters(true);
                    }
                }
                else if (frightenedCounter <= 0 && gp.isPelletEaten()) {
                    frightenedCounter = frightenedTiming;
                }


                //ghost freezer timer
                ghostFreezerTimer--;
                if (ghostFreezerTimer == 0) {
                    gp.setFreezerEaten(false);
                    for (Ghost ghost : gp.getGhosts()) {
                        ghost.setFrozen(false);
                        ghost.setCanDropBoosters(true);
                    }
                }
                else if (ghostFreezerTimer < 0 && gp.isFreezerEaten()) {
                    ghostFreezerTimer = frozenTiming;
                }

                //updating ghosts states
                for (Ghost ghost : gp.getGhosts()) {
                    if (totalTimeCounter == 5 && ghost instanceof Pinky) {
                        ghost.setGoingOutOfCage(true);
                    }
                    if (totalTimeCounter == 10 && ghost instanceof Inky) {
                        ghost.setGoingOutOfCage(true);
                    }
                    if (totalTimeCounter == 15 && ghost instanceof Clyde) {
                        ghost.setGoingOutOfCage(true);
                    }
                    ghost.state = ghostStates[currentState];
                    if (gp.isPelletEaten() && !ghost.isWasEatenDuringPellet()) {
                        ghost.state = FRIGHTENED;
                        ghost.getAnimationThread().interrupt(); //to make his animation frightened instantly
                        ghost.setCanDropBoosters(false);
                    }
                    if (gp.isFreezerEaten() && !ghost.isWasEatenDuringPellet() && !ghost.isEaten()) {
                        ghost.setFrozen(true);
                        ghost.getAnimationThread().interrupt(); //to make his animation frozen instantly
                        ghost.setCanDropBoosters(false);
                    }
                    if (ghost.isEaten()) {
                        ghost.state = EATEN;
                        ghost.setCanDropBoosters(false);
                    }
                    if (totalTimeCounter % 5 == 0 && ghost.isCanDropBoosters()) {
                        tryDropBooster(ghost);
                    }
                }
            }

            if (gp.getGameThread() == null) {
                threadRunning = false;
            }

            //updating every second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                if (gp.getGameState() == GameState.DEAD) break; //in order to reset the timer after player dies
            }
        }
    }

    private void tryDropBooster(Ghost ghost) {
        Random random = new Random();
        if (random.nextInt(100) <= 25) {
            switch (gp.getBoostersCollection().get(random.nextInt(gp.getBoostersCollection().size()))) {
                case "Speed booster" -> gp.getBoosters().add(new Speed_booster_obj(gp, ghost.x, ghost.y));
                case "Heart" -> gp.getBoosters().add(new Heart_booster_obj(gp, ghost.x, ghost.y));
                case "Wall piercer" -> gp.getBoosters().add(new Wall_piercer_booster_obj(gp, ghost.x, ghost.y));
                case "Invisibility mode" -> gp.getBoosters().add(new Invisibility_booster_obj(gp, ghost.x, ghost.y));
                case "Ghost freezer" -> gp.getBoosters().add(new Ghosts_freezer_obj(gp, ghost.x, ghost.y));
            }
        }
    }

    public int getFrightenedTiming() {
        return frightenedTiming;
    }

    public int getFrozenTiming() {
        return frozenTiming;
    }

    //GETTERS & SETTERS

    //getters
    public GamePanel getGp() {
        return gp;
    }
    public int getFrightenedCounter() {
        return frightenedCounter;
    }
    //----------------------------------------

    //setters
    public void setGp(GamePanel gp) {
        this.gp = gp;
    }
    public void setFrightenedCounter(int frightenedCounter) {
        this.frightenedCounter = frightenedCounter;
    }
    public void setGhostFreezerTimer(int ghostFreezerTimer) {
        this.ghostFreezerTimer = ghostFreezerTimer;
    }
}
