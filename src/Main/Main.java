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

    static void createGameWindow(JFrame parentFrame, String mapName, ArrayList<String> boostersCollection) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setBackground(Color.BLACK);
        window.setTitle("Pacman Game");

        GamePanel gamePanel = new GamePanel(window, mapName, boostersCollection);
        window.add(gamePanel);

        window.pack();
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.setupGame();
        gamePanel.startGameThread();

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parentFrame.setState(JFrame.NORMAL);
                parentFrame.setVisible(true);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                parentFrame.setState(JFrame.NORMAL);
                parentFrame.setVisible(true);
            }
        });
    }

}
