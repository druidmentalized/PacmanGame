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
        GamePanel gp = (GamePanel) e.getComponent();
        gp.setGameState(GameState.PAUSE);
        gp.getGameThread().interrupt();

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
