package Main;

import Entity.Entity;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener
{
    private GamePanel gp;

    //DIFFERENT ACTIONS
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean pausePressed = false;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch(keyCode) {
            case KeyEvent.VK_W -> upPressed = true;
            case KeyEvent.VK_S -> downPressed = true;
            case KeyEvent.VK_D -> rightPressed = true;
            case KeyEvent.VK_A -> leftPressed = true;
            case KeyEvent.VK_F1 -> gp.exit();
            case KeyEvent.VK_ESCAPE -> {
                pausePressed = !pausePressed;
                if (gp.getGameState() == GameState.DEAD && gp.getPlayer().getLives() <= 0) {
                    gp.exit();
                }
            }
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        //nothing
    }

    //GETTERS & SETTERS

    //getters
    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isPausePressed() {
        return pausePressed;
    }


    //setters
    public void setUpPressed(boolean upPressed) {
        this.upPressed = upPressed;
    }

    public void setDownPressed(boolean downPressed) {
        this.downPressed = downPressed;
    }

    public void setLeftPressed(boolean leftPressed) {
        this.leftPressed = leftPressed;
    }

    public void setRightPressed(boolean rightPressed) {
        this.rightPressed = rightPressed;
    }

    public void setPausePressed(boolean pausePressed) {
        this.pausePressed = pausePressed;
    }
}
