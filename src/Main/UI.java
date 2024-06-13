package Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    private Font emulogic;
    private BufferedImage healthImage;
    public ArrayList<Integer> messagesFlow = new ArrayList<>();
    public ArrayList<Integer> messagesCounter = new ArrayList<>();

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

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(emulogic);
        g2.setColor(Color.WHITE);
        drawHUD();
        drawGhostKillPoints();
    }

    private void drawGhostKillPoints() {
        if (!messagesFlow.isEmpty()) {
            g2.setColor(new Color(0, 255, 255));
            g2.setFont(g2.getFont().deriveFont(11f));
            //elements from the flow always go in triplets: amount of points, x and y coordinates
            for (int i = 0; i < messagesFlow.size(); i += 3) {
                g2.drawString(String.valueOf(messagesFlow.get(i)), messagesFlow.get(i + 1), messagesFlow.get(i + 2));
                //decreasing the counter and check whether the time of message showing have passed
                messagesCounter.set(i / 3, messagesCounter.get(i / 3) - 1);
                //removing messages if counter ended
                if (messagesCounter.get(i / 3) == 0) {
                    messagesCounter.remove(i / 3);
                    for (int j = 0; j < 3; j++) {
                        messagesFlow.remove(i);
                    }
                }
            }
        }
    }

    private void drawHUD() {
        if (gp.player.lives > 0) {
            drawScore();
            drawHealth();
        }
        else if (gp.player.lives == 0) {
            drawEndScreen();
        }
    }

    private void drawScore() {
        String message = String.valueOf(gp.score);
        g2.setFont(g2.getFont().deriveFont(20f));
        int x = gp.tileSize * 4 - (getMessageWidth(message) / 2);
        int y = (int)(gp.tileSize * 1.5) + (getMessageHeight(message) / 2);
        g2.drawString(message, x, y);
    }

    private void drawHealth() {
        int shift = ((gp.tileSize * 2) - (int)(healthImage.getWidth() * 1.5)) / 2;
        int x = shift;
        int y = (gp.tileSize * (gp.maxScreenRow - 2)) + shift;
        for (int i = 0; i < gp.player.lives; i++) {
            g2.drawImage(healthImage, x, y, (int)(gp.tileSize * 1.5), (int)(gp.tileSize * 1.5), null);
            x += gp.tileSize * 2;
        }
    }

    private void drawEndScreen() {
        //drawing semi transparent rectangle
        g2.setColor(new Color(0, 0, 0, 200));
        int rectWidth = 400;
        int rectHeight = 500;
        int rectX = (gp.getMaxScreenWidth() - rectWidth) / 2;
        int rectY= (gp.getMaxScreenWidth() - rectHeight) / 2;
        g2.fillRoundRect(rectX, rectY, rectWidth, rectHeight, 35, 35);
        //drawing stroke
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(rectX + 3, rectY + 3, rectWidth - 7, rectHeight - 7, 25, 25);
        g2.setStroke(new BasicStroke(5));

        //drawing score
        g2.setFont(g2.getFont().deriveFont(20f));
        String message = "Your score:";
        int x = rectX + (rectWidth - getMessageWidth(message)) / 2;
        rectY = rectY + getMessageHeight(message) + 30;
        g2.drawString(message, x, rectY);

        g2.setColor(Color.YELLOW);
        g2.setFont(g2.getFont().deriveFont(30f));
        message = String.valueOf(gp.score);
        x = rectX + (rectWidth - getMessageWidth(message)) / 2;
        rectY += getMessageHeight(message) + 10;
        g2.drawString(message, x, rectY);


        //drawing message to exit
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(20f));
        message = "Press \"ESC\" to Exit";
        x = rectX + (rectWidth - getMessageWidth(message)) / 2;
        rectY += 150;
        g2.drawString(message, x, rectY);
    }

    private int getMessageWidth(String message) {
        return (int)g2.getFontMetrics().getStringBounds(message, g2).getWidth();
    }

    private int getMessageHeight(String message) {
        return (int)g2.getFontMetrics().getStringBounds(message, g2).getHeight();
    }
}
