package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener
{
    GamePanel gp;

    //DIFFERENT ACTIONS
    public boolean upPressed = false;
    public boolean downPressed = false;
    public boolean leftPressed = false;
    public boolean rightPressed = false;
    public boolean debugPressed = false;
    public boolean pausePressed = false;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //nothing for now
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch(keyCode) {
            case KeyEvent.VK_W -> upPressed = true;
            case KeyEvent.VK_S -> downPressed = true;
            case KeyEvent.VK_D -> rightPressed = true;
            case KeyEvent.VK_A -> leftPressed = true;
            case KeyEvent.VK_F1 -> debugPressed = !debugPressed;
            case KeyEvent.VK_ESCAPE -> {
                pausePressed = !pausePressed;
                if (gp.gameState == GameState.DEAD && gp.player.lives <= 0) {
                    System.exit(0);
                }
            }
            case KeyEvent.VK_F2 -> gp.getWindow().dispose();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //nothing for now
    }
}
