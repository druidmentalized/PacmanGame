package Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UI implements Resizable, Redrawable {
    GamePanel gp;
    private Font emulogic;
    private BufferedImage healthImage;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private JLabel pauseLabel;
    private final ArrayList<JLabel> healthLabels = new ArrayList<>();
    private final ArrayList<JLabel> messagesFlow = new ArrayList<>();
    private final ArrayList<Integer> messagesCounter = new ArrayList<>();
    private int updateCounter = 0;
    private int gameTimeCounter = 0;

    public UI(GamePanel gp) {
        this.gp = gp;

        try {
            healthImage = ImageIO.read(new File("res/player/pacman_left_2.png"));
            InputStream is = getClass().getResourceAsStream("/fonts/emulogic.ttf");
            emulogic = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

    }

    public void prepareUI() {
        //preparing scoreLabel
        scoreLabel = createLabel(String.valueOf(gp.getScore()));
        scoreLabel.setBounds(gp.getWidthTileSize(), gp.getHeightTileSize(), 5 * gp.getWidthTileSize(),
                2 * gp.getHeightTileSize());
        gp.getUiPanel().add(scoreLabel);

        //preparing timerLabel
        timerLabel = createLabel(String.valueOf(gameTimeCounter));
        timerLabel.setBounds(gp.getMaxScreenWidth() / 2, gp.getWidthTileSize(), 5 * gp.getHeightTileSize(),
                2 * gp.getHeightTileSize());
        gp.getUiPanel().add(timerLabel);

        //prepare pauseLabel
        pauseLabel = createLabel("PAUSE");
        pauseLabel.setFont(pauseLabel.getFont().deriveFont(50F));
        pauseLabel.setBounds(gp.getMaxScreenWidth() / 2 - 5 * gp.getWidthTileSize(), gp.getMaxScreenHeight() / 2 - 5 * gp.getHeightTileSize(),
                20 * gp.getWidthTileSize(), 5 * gp.getHeightTileSize());
        gp.getUiPanel().add(pauseLabel);

        //adding Health
        for (int i = 0; i < gp.getPlayer().getLives(); i++) {
            addHealth();
        }
    }

    @Override
    public void redraw() {
        redrawHUD();
        updateCounter++;

        //cycle, which counts time inside the UI class
        if (updateCounter == 60) {
            gameTimeCounter++;
            updateCounter = 0;
        }
    }

    private void redrawHUD() {
        //drawing score
        scoreLabel.setText(String.valueOf(gp.getScore()));
        timerLabel.setText(String.valueOf(gameTimeCounter));

        //making visible only when game is paused
        pauseLabel.setVisible(gp.getGameState() == GameState.PAUSE);

        //drawing score for killing ghost(if exists)
        drawGhostKillPoints();
    }

    private void drawGhostKillPoints() {
        if (!messagesFlow.isEmpty()) {
            for (int i = 0; i < messagesFlow.size(); i += 1) {
                //setting font for message
                messagesFlow.get(i).setFont(emulogic);
                messagesFlow.get(i).setFont(messagesFlow.get(i).getFont().deriveFont(11f));
                messagesFlow.get(i).setForeground(new Color(0, 255, 255));

                //deducting counter
                messagesCounter.set(i, messagesCounter.get(i) - 1);

                if (messagesCounter.get(i) == 0) //means that time to show the message ended
                {
                    gp.getUiPanel().remove(messagesFlow.get(i));
                    messagesFlow.remove(i);
                    messagesCounter.remove(i);
                    gp.getUiPanel().repaint();
                }
            }
        }
    }

    public void addHealth() {
        JLabel healthLabel = new JLabel(new ImageIcon(healthImage.getScaledInstance((int)(gp.getWidthTileSize() * 1.5),
                (int)(gp.getHeightTileSize() * 1.5), Image.SCALE_SMOOTH)));

        //painting relying on the position of the previous image
        if (!healthLabels.isEmpty()) {
            healthLabel.setBounds(healthLabels.getLast().getBounds().x + (2 * gp.getWidthTileSize()),
                    (int)(gp.getMaxScreenHeight() - (1.75 * gp.getHeightTileSize())),
                    healthLabel.getIcon().getIconWidth(), healthLabel.getIcon().getIconHeight());
        }
        //if no hearts were made before, then painting from a base position
        else {
            healthLabel.setBounds(gp.getWidthTileSize(), (int)(gp.getMaxScreenHeight() - (1.75 * gp.getHeightTileSize())),
                    healthLabel.getIcon().getIconWidth(), healthLabel.getIcon().getIconHeight());
        }
        healthLabels.add(healthLabel);
        gp.getUiPanel().add(healthLabel);
        gp.getUiPanel().repaint();
    }

    public void deductHealth() {
        gp.getUiPanel().remove(healthLabels.getLast());
        healthLabels.removeLast();
        gp.repaint();
    }

    private JLabel createLabel(String text) {
        JLabel returnLabel = new JLabel();
        returnLabel.setFont(emulogic);
        returnLabel.setFont(returnLabel.getFont().deriveFont(20f));
        returnLabel.setForeground(Color.WHITE);
        returnLabel.setText(text);
        return returnLabel;
    }

    public void resize() {
        //resizing score label
        scoreLabel.setBounds(gp.getWidthTileSize(), gp.getHeightTileSize(),
                (int)(scoreLabel.getWidth() * gp.getWidthRatio()), (int)(scoreLabel.getHeight() * gp.getHeightRatio()));
        float newFontSize = (float)(scoreLabel.getFont().getSize() * gp.getWidthRatio());
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(newFontSize));

        //resizing timer label
        timerLabel.setBounds((int)(timerLabel.getX() * gp.getWidthRatio()), gp.getHeightTileSize(),
                (int)(timerLabel.getWidth() * gp.getWidthRatio()), (int)(timerLabel.getHeight() * gp.getHeightRatio()));
        newFontSize = (float)(timerLabel.getFont().getSize() * gp.getWidthRatio());
        timerLabel.setFont(timerLabel.getFont().deriveFont(newFontSize));

        //resizing pause label
        pauseLabel.setBounds((int)(pauseLabel.getX() * gp.getWidthRatio()), (int)(pauseLabel.getY() * gp.getHeightRatio()),
                (int)(pauseLabel.getWidth() * gp.getWidthRatio()), (int)(pauseLabel.getHeight() * gp.getHeightRatio()));
        newFontSize = (float)(pauseLabel.getFont().getSize() * gp.getWidthRatio());
        pauseLabel.setFont(pauseLabel.getFont().deriveFont(newFontSize));

        //resizing health
        for (JLabel healthLabel : healthLabels) {
            healthLabel.setIcon(new ImageIcon(healthImage.getScaledInstance((int)(gp.getWidthTileSize() * 1.5),
                    (int)(gp.getHeightTileSize() * 1.5), Image.SCALE_SMOOTH)));
            healthLabel.setBounds((int)(healthLabel.getX() * gp.getWidthRatio()),
                    (int)(healthLabel.getY() * gp.getHeightRatio()), healthLabel.getIcon().getIconWidth(),
                    healthLabel.getIcon().getIconHeight()); {

            }
        }

        //resizing messages of death(if exist)
        for (JLabel deathLabel : messagesFlow) {
            newFontSize = (float)(deathLabel.getFont().getSize() * gp.getWidthRatio());
            deathLabel.setFont(deathLabel.getFont().deriveFont(newFontSize));
            deathLabel.setBounds((int)(deathLabel.getBounds().x * gp.getWidthRatio()),
                    (int)(deathLabel.getBounds().y * gp.getHeightRatio()),
                    (int)(deathLabel.getWidth() * gp.getWidthRatio()),
                    (int)(deathLabel.getHeight() * gp.getHeightRatio()));
        }
    }


    //GETTERS & SETTERS

    //getters
    public ArrayList<JLabel> getMessagesFlow() {
        return messagesFlow;
    }
    public ArrayList<Integer> getMessagesCounter() {
        return messagesCounter;
    }
    public int getGameTimeCounter() {
        return gameTimeCounter;
    }
}
