package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class Main
{

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameMenu());
    }

    static void createGameWindow(JFrame parentFrame, String mapName, GameMenu gm) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setBackground(Color.BLACK);
        window.setTitle("Pacman Game");

        GamePanel gp = new GamePanel(window, mapName);
        gp.addComponentListener(new Resizer(window));
        window.add(gp);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gp.setupGame();
        gp.startGameThread();

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                changeToMainMenu(gm, gp, parentFrame);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                changeToMainMenu(gm, gp, parentFrame);
            }
        });

    }

    private static void changeToMainMenu(GameMenu gm, GamePanel gp, Frame parentFrame) {
        gm.setLastScorePlayed(gp.getMaxScreenColumn());
        gm.setLastTimePlayed(gp.getUi().getGameTimeCounter());
        gm.changeWindow("ChooseNameHighscore");
        parentFrame.setState(JFrame.NORMAL);
        parentFrame.setVisible(true);
    }

}
