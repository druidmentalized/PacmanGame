package Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameMenu {
    JFrame frame = new JFrame("Pacman");
    private final JPanel panelsDeck = new JPanel();
    private Font emulogicFont;
    private final HashMap<String, JLabel> mapsNamesToPreviewPhotos = new HashMap<>();
    private final DefaultListModel<Highscore> listModel = new DefaultListModel<>();
    private JList<Highscore> highscoreList;
    private int lastTimePlayed;
    private int lastScorePlayed;
    private int lastMaxLevelPlayed;

    public GameMenu() {
        fillMapsMap();
        startMenu();
    }

    private void fillMapsMap() {
        ImageIcon imageIcon;
        Image resizedImage;

        //WIDE
        imageIcon = new ImageIcon("res/menu/wide_map_preview.png");
        resizedImage = imageIcon.getImage().getScaledInstance(imageIcon.getIconWidth() / 2, imageIcon.getIconHeight() / 2 + 100, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(resizedImage);
        mapsNamesToPreviewPhotos.put("Wide", new JLabel(imageIcon));

        //SMALL
        imageIcon = new ImageIcon("res/menu/small_map_preview.png");
        resizedImage = imageIcon.getImage().getScaledInstance(500, imageIcon.getIconHeight(), Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(resizedImage);
        mapsNamesToPreviewPhotos.put("Small", new JLabel(imageIcon));

        //SQUARED
        imageIcon = new ImageIcon("res/menu/squared_map_preview.png");
        resizedImage = imageIcon.getImage().getScaledInstance(imageIcon.getIconWidth(), imageIcon.getIconHeight(), Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(resizedImage);
        mapsNamesToPreviewPhotos.put("Squared", new JLabel(imageIcon));

        //HUMAN LIKE
        imageIcon = new ImageIcon("res/menu/humanlike_map_preview.png");
        resizedImage = imageIcon.getImage().getScaledInstance(imageIcon.getIconWidth() / 2, imageIcon.getIconHeight() / 2, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(resizedImage);
        mapsNamesToPreviewPhotos.put("Humanlike", new JLabel(imageIcon));


        //ORIGINAL
        imageIcon = new ImageIcon("res/menu/original_map_preview.png");
        resizedImage = imageIcon.getImage().getScaledInstance(imageIcon.getIconWidth() / 2, imageIcon.getIconHeight() / 2, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(resizedImage);
        mapsNamesToPreviewPhotos.put("Original", new JLabel(imageIcon));
    }

    public void startMenu() {
        //Load custom font
        try {
            emulogicFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/fonts/emulogic.ttf")).deriveFont(20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(emulogicFont);
        } catch (IOException | FontFormatException e) {
            emulogicFont = new Font("Arial", Font.PLAIN, 20);
        }

        //tune the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(688, 903);
        frame.setLayout(null);
        frame.setBackground(Color.BLACK);
        frame.setResizable(false);

        //creating gif background
        JLabel backgroundGIF = new JLabel(new ImageIcon("res/menu/menu_background.gif"), JLabel.CENTER);
        frame.setContentPane(backgroundGIF);

        //panel
        panelsDeck.setLayout(new CardLayout());
        panelsDeck.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        panelsDeck.setOpaque(false);


        //Main Menu Panel
        JPanel mainMenuPanel = createMainMenuPanel();

        //Play Panel
        JPanel playPanel = createPlayPanel();

        //HighscoresPanel
        JPanel highscoresPanel = createHighscoresPanel();

        //Options Panel
        JPanel controlsPanel = createColntorlsPanel();

        //Credits Panel
        JPanel creditsPanel = createCreditsPanel();

        //Choosing name Panel
        JPanel highscoreNameChooserPanel = createHighscoreNameChoosePanel();


        //Add panels to the frame
        panelsDeck.add(mainMenuPanel, "MainMenu");
        panelsDeck.add(playPanel, "Play");
        panelsDeck.add(highscoresPanel, "Highscores");
        panelsDeck.add(controlsPanel, "Controls");
        panelsDeck.add(creditsPanel, "Credits");
        panelsDeck.add(highscoreNameChooserPanel, "ChooseNameHighscore");

        frame.add(panelsDeck);

        //Display the frame
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //saving highscore when leaving the application
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveHighscores();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                saveHighscores();
            }
        });
    }

    private JPanel createMainMenuPanel() {
        // Main Menu Panel
        JPanel mainMenuPanel = new JPanel(null);
        mainMenuPanel.setOpaque(false);

        // Create the title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setBounds(100, 50, 500, 100);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));

        //first part of title
        JLabel titlePart1 = new JLabel("Pa");
        titlePart1.setFont(emulogicFont);
        titlePart1.setFont(titlePart1.getFont().deriveFont(70f));
        titlePart1.setForeground(Color.WHITE);
        titlePart1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        //getting and scaling title image
        JLabel titleImage = null;
        BufferedImage originalImage;
        Image scaledImage;
        try {
            originalImage = ImageIO.read(new File("res/player/pacman_right_3.png"));
            int imageDimension = titlePart1.getFontMetrics(titlePart1.getFont()).getHeight();
            scaledImage = originalImage.getScaledInstance(imageDimension, imageDimension, Image.SCALE_SMOOTH);
            titleImage = new JLabel(new ImageIcon(scaledImage));
        } catch (IOException e) {
            System.err.println("Scaling image wasn't successful");
        }

        //second part of title
        JLabel titlePart2 = new JLabel("man");
        titlePart2.setFont(emulogicFont);
        titlePart2.setFont(titlePart1.getFont().deriveFont(70f));
        titlePart2.setForeground(Color.WHITE);

        // Add components to title panel
        titlePanel.add(titlePart1);
        titlePanel.add(titleImage);
        titlePanel.add(titlePart2);
        mainMenuPanel.add(titlePanel);

        // Create buttons
        JButton playButton = createButton("PLAY");
        JButton highscoresButton = createButton("HIGHSCORES");
        highscoresButton.setFont(highscoresButton.getFont().deriveFont(18f));
        JButton optionsButton = createButton("CONTROLS");
        JButton creditsButton = createButton("CREDITS");
        JButton exitButton = createButton("EXIT");

        // Set button bounds (position and size)
        int width = 200;
        int positionX = calculatePosX(width) ;
        int height = 50;
        int indent = 40;
        int positionY = 200 + height + indent;

        //setting buttons' positions
        playButton.setBounds(positionX, positionY, width, height);
        positionY += height + indent;
        highscoresButton.setBounds(positionX, positionY, width, height);
        positionY += height + indent;
        optionsButton.setBounds(positionX, positionY, width, height);
        positionY += height + indent;
        creditsButton.setBounds(positionX, positionY, width, height);
        positionY += height + indent;
        exitButton.setBounds(positionX, positionY, width, height);

        // Add buttons to the panel
        mainMenuPanel.add(playButton);
        mainMenuPanel.add(optionsButton);
        mainMenuPanel.add(creditsButton);
        mainMenuPanel.add(highscoresButton);
        mainMenuPanel.add(exitButton);

        //------------------ACTION LISTENERS-----------------------

        //PLAY button
        playButton.addActionListener(e -> changeWindow("Play"));

        //HIGHSCORES button
        highscoresButton.addActionListener(e -> changeWindow("Highscores"));

        //OPTIONS button
        optionsButton.addActionListener(e -> changeWindow("Controls"));

        //CREDITS button
        creditsButton.addActionListener(e -> changeWindow("Credits"));

        //EXIT button
        exitButton.addActionListener(e -> {
            saveHighscores();
            System.exit(0);
        });

        return mainMenuPanel;
    }

    private JPanel createPlayPanel() {
        //panel for choosing game mode
        JPanel playPanel = new JPanel(null);
        playPanel.setOpaque(false);


        int width;
        int positionX;
        int height;
        int positionY = 0;

        //creating title
        JLabel titleLabel = createLabel("GAME SETTINGS");
        titleLabel.setFont(titleLabel.getFont().deriveFont(40f));
        width = 600;
        positionX = calculatePosX(width);
        height = 150;
        titleLabel.setBounds(positionX, positionY, width, height);
        playPanel.add(titleLabel);

        //adding JPanel to show chosen map
        JPanel previewMapPanel = new JPanel();
        previewMapPanel.setOpaque(false);
        previewMapPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));
        CardLayout cardLayout = new CardLayout();
        previewMapPanel.setLayout(cardLayout);
        for (String mapName : mapsNamesToPreviewPhotos.keySet()) {
            previewMapPanel.add(mapsNamesToPreviewPhotos.get(mapName), mapName);
        }
        width = 500;
        positionX = calculatePosX(width);
        height = 380;
        positionY = 125;
        previewMapPanel.setBounds(positionX, positionY, width, height);
        playPanel.add(previewMapPanel);

        //creating combobox to choose custom map
        String[] maps = mapsNamesToPreviewPhotos.keySet().toArray(new String[0]);
        JComboBox<String> mapsBox = getStringJComboBox(maps);

        width = 250;
        positionX = calculatePosX(width);
        height = 50;
        positionY += previewMapPanel.getHeight() + 50;
        mapsBox.setBounds(positionX, positionY, width, height);
        playPanel.add(mapsBox);

        mapsBox.addActionListener(e -> cardLayout.show(previewMapPanel, (String) mapsBox.getSelectedItem()));

        //creating back button
        JButton backButton = createButton("BACK");
        width = 100;
        positionX = 20;
        positionY = 800;
        backButton.setBounds(positionX, positionY, width, height);
        playPanel.add(backButton);

        //creating PLAY button
        JButton playButton = createButton("PLAY");
        width = 250;
        positionX = calculatePosX(width);
        height = 100;
        positionY = 750;
        playButton.setBounds(positionX, positionY, width, height);
        playPanel.add(playButton);

        //------------------ACTION LISTENERS-----------------------

        backButton.addActionListener(e -> changeWindow("MainMenu"));

        playButton.addActionListener(e -> {
            frame.setState(JFrame.ICONIFIED);
            Main.createGameWindow(frame, (String) mapsBox.getSelectedItem(), this);
        });

        return playPanel;
    }

    private JPanel createHighscoresPanel() {
        //panel to show highscores of the player
        JPanel highscoresPanel = new JPanel(null);
        highscoresPanel.setOpaque(false);

        int width;
        int positionX;
        int height;
        int positionY = 0;

        //title label
        JLabel titleLabel = createLabel("HIGHSCORES");
        titleLabel.setFont(titleLabel.getFont().deriveFont(40f));
        width = 600;
        positionX = calculatePosX(width);
        height = 150;
        titleLabel.setBounds(positionX, positionY, width, height);
        highscoresPanel.add(titleLabel);

        //JList & JScrollPane for highscores accounting

        highscoreList = new JList<>();
        highscoreList.setModel(listModel);
        highscoreList.setFont(emulogicFont);
        highscoreList.setFont(highscoreList.getFont().deriveFont(20f));
        highscoreList.setBackground(new Color(20, 20, 20, 170));
        highscoreList.setForeground(Color.WHITE);
        highscoreList.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        highscoreList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Highscore highscore) {
                    setText(highscore.toString());
                }
                return this;
            }
        });

        //adding all previous elements to JList
        ArrayList<Highscore> highscores = Highscore.readFromFile();
        for (Highscore highscore : highscores) {
            listModel.addElement(highscore);
        }

        positionY += 110;
        height = 650;
        highscoreList.setBounds(positionX, positionY, width, height);
        JScrollPane jScrollPane = new JScrollPane(highscoreList);
        jScrollPane.setBackground(new Color(20, 20, 20, 170));
        jScrollPane.setBounds(highscoreList.getBounds());
        highscoresPanel.add(jScrollPane);

        //creating BACK button
        JButton backButton = createButton("BACK");
        width = 200;
        positionX = calculatePosX(width);
        height = 50;
        positionY = 800;
        backButton.setBounds(positionX, positionY, width, height);
        highscoresPanel.add(backButton);

        //------------------ACTION LISTENERS-----------------------

        backButton.addActionListener(e -> changeWindow("MainMenu"));

        return highscoresPanel;
    }

    private JPanel createColntorlsPanel() {
        JPanel contorlsPanel = new JPanel(null); // null layout to freely position components
        contorlsPanel.setOpaque(false);

        int width;
        int positionX;
        int height;
        int positionY = 0;

        //creating title label
        JLabel titleLabel = createLabel("CONTROLS");
        titleLabel.setFont(titleLabel.getFont().deriveFont(70f));
        width = 600;
        positionX = calculatePosX(width) ;
        height = 150;
        titleLabel.setBounds(positionX, positionY, width, height);
        contorlsPanel.add(titleLabel);

        //creating controls

        //W - UP
        JLabel upLabel = createLabel("W -- UP");
        width = 250;
        positionX = calculatePosX(width);
        positionY += height + 25;
        height = 50;
        upLabel.setBounds(positionX, positionY, width, height);
        contorlsPanel.add(upLabel);

        //S -- DOWN
        JLabel downLabel = createLabel("S -- DOWN");
        positionY += height + 50;
        downLabel.setBounds(positionX, positionY, width, height);
        contorlsPanel.add(downLabel);

        //A -- LEFT
        JLabel leftLabel = createLabel("A -- LEFT");
        positionY += height + 50;
        leftLabel.setBounds(positionX, positionY, width, height);
        contorlsPanel.add(leftLabel);

        //D -- RIGHT
        JLabel rightLabel = createLabel("D -- RIGHT");
        positionY += height + 50;
        rightLabel.setBounds(positionX, positionY, width, height);
        contorlsPanel.add(rightLabel);

        //ESC -- PAUSE
        JLabel pauseLabel = createLabel("ESC -- PAUSE");
        positionY += height + 50;
        pauseLabel.setBounds(positionX, positionY, width, height);
        contorlsPanel.add(pauseLabel);

        //F1 -- EXIT
        JLabel exitLabel = createLabel("F1 -- EXIT");
        positionY += height + 50;
        exitLabel.setBounds(positionX, positionY, width, height);
        contorlsPanel.add(exitLabel);



        //creating back button
        JButton backButton = createButton("BACK");
        width = 200;
        positionX = calculatePosX(width);
        height = 50;
        positionY = 800;
        backButton.setBounds(positionX, positionY, width, height);
        contorlsPanel.add(backButton);

        //------------------ACTION LISTENERS-----------------------

        backButton.addActionListener(e -> changeWindow("MainMenu"));

        return contorlsPanel;
    }

    private JPanel createCreditsPanel() {
        JPanel creditsPanel = new JPanel(null);
        creditsPanel.setOpaque(false);

        //creating window title
        JLabel titleLabel = createLabel("CREDITS");
        titleLabel.setFont(titleLabel.getFont().deriveFont(70f));
        int width = 500;
        int positionX = calculatePosX(width);
        int height = 150;
        int positionY = 0;
        int nameIndent = 10;
        int indent = 40;
        titleLabel.setBounds(positionX, positionY, width, height);

        //creating other labels
        JLabel programmingLabel = createLabel("PROGRAMMING");
        JLabel programmerNameLabel = createLabel("Dmitriy Barmuta");
        JLabel artDesignLabel = createLabel("ART DESIGN");
        JLabel artDesignerLabel = createLabel("Dmitriy Barmuta");
        JLabel soundsLabel = createLabel("SOUNDS");
        JLabel sounderLabel = createLabel("Dmitriy Barmuta");

        //placing labels
        width = 400;
        height = 50;
        positionX = calculatePosX(width);
        positionY = 200;
        programmingLabel.setBounds(positionX, positionY, width, height);
        positionY += height + nameIndent;
        programmerNameLabel.setBounds(positionX, positionY, width, height);
        positionY += height + indent;
        artDesignLabel.setBounds(positionX, positionY, width, height);
        positionY += height + nameIndent;
        artDesignerLabel.setBounds(positionX, positionY, width, height);
        positionY += height + indent;
        soundsLabel.setBounds(positionX, positionY, width, height);
        positionY += height + nameIndent;
        sounderLabel.setBounds(positionX, positionY, width, height);

        //adding labels
        creditsPanel.add(titleLabel);
        creditsPanel.add(programmingLabel);
        creditsPanel.add(programmerNameLabel);
        creditsPanel.add(artDesignLabel);
        creditsPanel.add(artDesignerLabel);
        creditsPanel.add(soundsLabel);
        creditsPanel.add(sounderLabel);

        //creating caption
        ImageIcon logo = new ImageIcon("res/menu/pjait_logo.png");
        Image resizedLogoImage = logo.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        logo = new ImageIcon(resizedLogoImage);
        JLabel caption = createLabel("Made for PJAIT semester project 2024");
        caption.setIcon(logo);
        caption.setHorizontalTextPosition(JLabel.RIGHT);
        caption.setFont(caption.getFont().deriveFont(15f));
        caption.setIconTextGap(10);
        width = 650;
        positionX = calculatePosX(width);
        positionY += height + 2 * indent;
        caption.setBounds(positionX, positionY, width, height);
        creditsPanel.add(caption);

        //creating exit button
        JButton backButton = createButton("BACK");
        width = 200;
        positionX = calculatePosX(width);
        positionY = 800;
        backButton.setBounds(positionX, positionY, width, height);
        creditsPanel.add(backButton);

        //------------------ACTION LISTENERS-----------------------

        backButton.addActionListener(e -> changeWindow("MainMenu"));

        return creditsPanel;
    }

    private JPanel createHighscoreNameChoosePanel() {
        JPanel highscoreNameChooserPanel = new JPanel(null);
        highscoreNameChooserPanel.setOpaque(false);

        int width;
        int positionX;
        int height;
        int positionY = 0;

        //creating title
        JLabel titleLabel = createLabel("NEW SCORE");
        titleLabel.setFont(titleLabel.getFont().deriveFont(40f));
        width = 600;
        positionX = calculatePosX(width);
        height = 150;
        titleLabel.setBounds(positionX, positionY, width, height);
        highscoreNameChooserPanel.add(titleLabel);

        //label with prompt
        JLabel promptLabel = createLabel("Choose your name:");
        width = 400;
        positionX = calculatePosX(width);
        height = 75;
        positionY += 250;
        promptLabel.setBounds(positionX, positionY, width, height);
        highscoreNameChooserPanel.add(promptLabel);

        //adding field to enter name
        JTextField nameField = new JTextField();
        nameField.setFont(emulogicFont);
        nameField.setFont(nameField.getFont().deriveFont(21f));
        nameField.setForeground(new Color(255, 255, 255));
        nameField.setBackground(new Color(20, 20,20));
        nameField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

        positionY += 100;
        nameField.setBounds(positionX, positionY, width, height);
        highscoreNameChooserPanel.add(nameField);

        //creating helper JLabel
        JLabel helperLabel = createLabel("");
        positionY += 100;
        width = 600;
        positionX = calculatePosX(width);
        helperLabel.setBounds(positionX, positionY, width, height);
        helperLabel.setFont(helperLabel.getFont().deriveFont(13f));
        highscoreNameChooserPanel.add(helperLabel);

        //creating SUBMIT button
        JButton submitButton = createButton("SUBMIT");
        width = 200;
        positionX = calculatePosX(width);
        height = 50;
        positionY = 700;
        submitButton.setBounds(positionX, positionY, width, height);
        highscoreNameChooserPanel.add(submitButton);

        //------------------ACTION LISTENERS-----------------------

        submitButton.addActionListener(e -> {
            if (nameField.getText().isEmpty()) {
                helperLabel.setText("You must enter your nickname first!");
            }
            else {
                listModel.addElement(new Highscore(nameField.getText(), lastScorePlayed, lastTimePlayed, lastMaxLevelPlayed));
                nameField.setText("");
                lastScorePlayed = 0;
                lastTimePlayed = 0;
                changeWindow("MainMenu");
            }
        });

        return highscoreNameChooserPanel;
    }


    // HELPER METHODS
    public void changeWindow(String changeTo) {
        CardLayout cardLayout = (CardLayout) panelsDeck.getLayout();
        cardLayout.show(panelsDeck, changeTo);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);

        //set button properties
        button.setFont(emulogicFont);
        button.setFocusPainted(false);
        button.setBackground(new Color(20, 20, 20)); // Darker button color
        button.setForeground(Color.WHITE);
        button.setOpaque(true);

        //set button border
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3)); // Thick border

        //add mouse listener to change background color on hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(40, 40, 40)); // Highlight color
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(20, 20, 20)); // Original color
                button.repaint();
            }
        });

        return button;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);

        //set label properties
        label.setFont(emulogicFont);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        return label;
    }

    private JComboBox<String> getStringJComboBox(String[] maps) {
        JComboBox<String> mapsBox = new JComboBox<>(maps);
        mapsBox.setFont(emulogicFont);
        mapsBox.setFont(mapsBox.getFont().deriveFont(18f));
        mapsBox.setForeground(Color.WHITE);
        mapsBox.setFocusable(false);
        mapsBox.setBackground(new Color(40, 40, 40));
        mapsBox.setBorder(new LineBorder(Color.WHITE, 3));
        mapsBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value);
            label.setOpaque(true);
            label.setBackground(new Color(40, 40, 40));
            label.setForeground(Color.WHITE);
            label.setFont(emulogicFont);
            label.setFont(label.getFont().deriveFont(18f));

            if (isSelected) {
                label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
                label.setForeground(Color.WHITE);
            } else {
                label.setBorder(null);
            }

            return label;
        });
        return mapsBox;
    }

    private int calculatePosX(int width) {
        return (frame.getWidth() - width) / 2;
    }

    private void saveHighscores() {
        ArrayList<Highscore> highscoreArrayList = new ArrayList<>();
        for (int i = 0; i < highscoreList.getModel().getSize(); i++) {
            highscoreArrayList.add(highscoreList.getModel().getElementAt(i));
        }
        Highscore.placeInFile(highscoreArrayList);
    }


    //GETTERS & SETTERS
    //setters
    public void setLastTimePlayed(int lastTimePlayed) {
        this.lastTimePlayed = lastTimePlayed;
    }
    public void setLastScorePlayed(int lastScorePlayed) {
        this.lastScorePlayed = lastScorePlayed;
    }
    public void setLastMaxLevelPlayed(int lastMaxLevelPlayed) {
        this.lastMaxLevelPlayed = lastMaxLevelPlayed;
    }
}
