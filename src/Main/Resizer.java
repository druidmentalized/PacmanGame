package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class Resizer implements ComponentListener {
    private JFrame window;

    public Resizer(JFrame window) {
        this.window = window;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        //taking game window
        GamePanel gp = (GamePanel) e.getComponent();
        //pausing the whole game, while resizing
        gp.setGameState(GameState.PAUSE);
        gp.getGameThread().interrupt(); //to make it instant

        double newWidth = gp.getWidth();
        double newHeight = gp.getHeight();

        gp.setPrevWidthTileSize(gp.getWidthTileSize());
        gp.setPrevHeightTileSize(gp.getHeightTileSize());
        gp.setWidthTileSize((int)(Math.round(newWidth / gp.getMaxScreenColumn())));
        gp.setHeightTileSize((int)(Math.round(newHeight / gp.getMaxScreenRow())));

        gp.resize();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        //nothing
    }

    @Override
    public void componentShown(ComponentEvent e) {
        //nothing
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        //nothing
    }
}
