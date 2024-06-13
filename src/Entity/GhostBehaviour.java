package Entity;

import Main.GamePanel;
import Main.GameState;
import Object.*;
import java.util.Random;

import static Entity.GhostState.*;

public class GhostBehaviour implements Runnable {
    GamePanel gp;
    private final static GhostState[] ghostStates = new GhostState[]{SCATTER, CHASE, SCATTER, CHASE, SCATTER, CHASE, SCATTER, CHASE};
    private static int[] stateTimings;
    private static int frightenedTiming;
    private static int frozenTiming;
    public int frightenedCounter = 0;
    public int ghostFreezerTimer = 0;
    public GhostBehaviour(GamePanel gp) {
        this.gp = gp;

        //setting timings of states
        switch(gp.level) {
            case 1 -> stateTimings = new int[]{7, 20, 7, 20, 5, 20, 5};
            case 2, 3, 4 -> stateTimings = new int[]{7, 20, 7, 20, 5, 1033, 1};
            default -> stateTimings = new int[]{5, 20, 5, 20, 5, 1033, 1};
        }

        //setting timings of fright
        switch (gp.level) {
            case 1, 2 -> frightenedTiming = 6;
            case 3, 4, 5 -> frightenedTiming = 5;
            case 6, 7, 8 -> frightenedTiming = 4;
            case 9, 10, 11 -> frightenedTiming = 3;
            case 12, 13, 14 -> frightenedTiming = 2;
            default -> frightenedTiming = 0;
        }

        //setting timings of frozen state
        switch (gp.level) {
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
        while (true) {
            if (gp.gameState == GameState.PLAY) {
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
                    gp.pelletEaten = false;
                    for (Ghost ghost : gp.ghosts) {
                        ghost.wasEatenDuringPellet = false;
                        gp.player.eatenGhosts = 0;
                        ghost.canDropBoosters = true;
                    }
                }
                else if (frightenedCounter <= 0 && gp.pelletEaten) {
                    frightenedCounter = frightenedTiming;
                }


                //ghost freezer timer
                ghostFreezerTimer--;
                if (ghostFreezerTimer == 0) {
                    gp.freezerEaten = false;
                    for (Ghost ghost : gp.ghosts) {
                        ghost.frozen = false;
                        ghost.canDropBoosters = true;
                    }
                }
                else if (ghostFreezerTimer < 0 && gp.freezerEaten) {
                    ghostFreezerTimer = frozenTiming;
                }

                //updating ghosts states
                for (Ghost ghost : gp.ghosts) {
                    if (totalTimeCounter == 5 && ghost instanceof Pinky) {
                        ghost.goingOutOfCage = true;
                    }
                    if (totalTimeCounter == 10 && ghost instanceof Inky) {
                        ghost.goingOutOfCage = true;
                    }
                    if (totalTimeCounter == 15 && ghost instanceof Clyde) {
                        ghost.goingOutOfCage = true;
                    }
                    ghost.state = ghostStates[currentState];
                    if (gp.pelletEaten && !ghost.wasEatenDuringPellet) {
                        ghost.state = FRIGHTENED;
                        ghost.animationThread.interrupt(); //to make his animation frightened instantly
                        ghost.canDropBoosters = false;
                    }
                    if (gp.freezerEaten && !ghost.wasEatenDuringPellet && !ghost.eaten) {
                        ghost.frozen = true;
                        ghost.animationThread.interrupt(); //to make his animation frozen instantly
                        ghost.canDropBoosters = false;
                    }
                    if (ghost.eaten) {
                        ghost.state = EATEN;
                        ghost.canDropBoosters = false;
                    }
                    if (totalTimeCounter % 5 == 0 && ghost.canDropBoosters) {
                        tryDropBooster(ghost);
                    }
                }
            }

            //updating every second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                if (gp.gameState == GameState.DEAD) break; //in order to reset the timer after player dies
            }
        }
    }

    private void tryDropBooster(Ghost ghost) {
        Random random = new Random();
        if (random.nextInt(100) <= 25) {
            switch (gp.boostersCollection.get(random.nextInt(gp.boostersCollection.size()))) {
                case "Speed booster" -> gp.boosters.add(new Speed_booster_obj(gp, ghost.x, ghost.y));
                case "Heart" -> gp.boosters.add(new Heart_booster_obj(gp, ghost.x, ghost.y));
                case "Wall piercer" -> gp.boosters.add(new Wall_piercer_booster_obj(gp, ghost.x, ghost.y));
                case "Invisibility mode" -> gp.boosters.add(new Invisibility_booster_obj(gp, ghost.x, ghost.y));
                case "Ghost freezer" -> gp.boosters.add(new Ghosts_freezer_obj(gp, ghost.x, ghost.y));
            }
        }
    }

    public int getFrightenedTiming() {
        return frightenedTiming;
    }

    public int getFrozenTiming() {
        return frozenTiming;
    }
}
