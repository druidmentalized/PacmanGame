package Main;

import Entity.*;
import Tile.TileManager;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import Entity.Ghost;
import Object.*;
import Entity.Point;

public class GamePanel extends JPanel implements Runnable, Resizable
{
    //SCREEN SETTINGS
    private final int originalTileSize = 8;
    //public int tileSize = 24; //24x24 tile(basic size)
    private int widthTileSize; // 24 basic size
    private int heightTileSize; // 24 basic size
    private int prevWidthTileSize;
    private int prevHeightTileSize;
    private double widthScale;
    private double heightScale;
    private double widthRatio;
    private double heightRatio;
    private int maxScreenColumn;
    private int maxScreenRow;
    private int maxScreenHeight;
    private int maxScreenWidth;

    //ENTITIES & OBJECTS
    private final Player player;
    private final ArrayList<Entity> objects = new ArrayList<>();
    private final ArrayList<Ghost> ghosts = new ArrayList<>();
    private final ArrayList<Booster> boosters = new ArrayList<>();
    private final ArrayList<String> boostersCollection = new ArrayList<>(Arrays.asList("Speed booster",
            "Heart",
            "Wall piercer",
            "Invisibility mode",
            "Ghost freezer"));
    private double[][] mapConstrains;

    //GAME SETTINGS
    private int score = 0;
    private int level = 1;
    private GameState gameState = GameState.PLAY;
    private boolean pelletEaten = false;
    private boolean freezerEaten = false;

    //SYSTEM
    private final JFrame window;
    private final KeyHandler keyHandler = new KeyHandler(this);
    private final TileManager tileManager = new TileManager(this);
    private final CollisionChecker collisionChecker = new CollisionChecker(this);
    private final UI ui = new UI(this);
    private Thread gameThread;
    private final GhostBehaviour ghostsBehaviourThreadTask = new GhostBehaviour(this);
    private Thread ghostBehaviour;
    private final JLayeredPane layeredPane;
    private final JPanel mapPanel;
    private final JPanel eatablesPanel;
    private final JPanel uiPanel;
    private final JPanel charactersPanel;

    //DEATH PROCESSING
    public static final byte deathStated = 0;
    public static final byte deathAnimGoes = 1;
    public static final byte deathAnimEnded = 2;
    private byte deathAnimState = deathStated;

    public GamePanel(JFrame window, String mapName) {
        //basic dimensions
        setLayout(null);
        widthTileSize = 24; // basic size
        heightTileSize = 24; // basic size
        widthScale = (double) widthTileSize / originalTileSize;
        heightScale = (double) heightTileSize / originalTileSize;
        this.window = window;

        //pane to store all other JPanels
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        //creating map panel
        mapPanel = new JPanel(new GridBagLayout());
        mapPanel.setBackground(Color.BLACK);

        //creating eatables panel
        eatablesPanel = new JPanel(null);
        eatablesPanel.setOpaque(false);

        //creating ui panel
        uiPanel = new JPanel(null);
        uiPanel.setOpaque(false);

        //creating characters panel
        charactersPanel = new JPanel(null);
        charactersPanel.setOpaque(false);

        //loading map and setting default resolution
        tileManager.loadMap(mapName, mapPanel);
        eatablesPanel.setBounds(mapPanel.getBounds());
        charactersPanel.setBounds(mapPanel.getBounds());
        layeredPane.setBounds(mapPanel.getBounds());

        //adding panels according to their overlapping order
        layeredPane.add(mapPanel, Integer.valueOf(1));
        layeredPane.add(eatablesPanel, Integer.valueOf(2));
        layeredPane.add(charactersPanel, Integer.valueOf(3));
        layeredPane.add(uiPanel, Integer.valueOf(4));

        //adding to main JPanel
        this.add(layeredPane);

        //extra
        maxScreenWidth = maxScreenColumn * widthTileSize;
        maxScreenHeight = maxScreenRow * heightTileSize;
        this.setPreferredSize(new Dimension(maxScreenWidth, maxScreenHeight));
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        //adding player
        player = new Player(this);

        //adding HUD on the screen
        ui.prepareUI();
    }

    public void setupGame() {

        //adding ghosts
        ghosts.add(new Blinky(this));
        ghosts.add(new Pinky(this));
        ghosts.add(new Inky(this));
        ghosts.add(new Clyde(this));

        //stopping previous behaviour thread when reviving
        if (ghostBehaviour != null) {
            ghostBehaviour.interrupt();
        }
        //making new behaviour thread
        ghostBehaviour = new Thread(ghostsBehaviourThreadTask);
        ghostBehaviour.start();
        //making sure that animation threads are available for entities
        Entity.setAnimationRunning(true);

        //filling field with pellets and dots(if it is changing of the level)
        if (gameState == GameState.LEVELCHANGE) {
            //filling field with pellets and dots if it is changing of the level
            for (int row = 0; row < maxScreenRow; row++) {
                for (int column = 0; column < maxScreenColumn; column++) {
                    if (tileManager.getTiles()[row][column].getTileType() == 11) {
                        objects.add(new Point_obj(this, column * widthTileSize, row * heightTileSize));
                    }
                    else if (tileManager.getTiles()[row][column].getTileType() == 12) {
                        objects.add(new Power_pellet_obj(this, column * widthTileSize, row * heightTileSize));
                    }
                }
            }
        }
        repaint();
    }


    public void startGameThread() {
        gameThread = new Thread(this, "gamePanel");
        gameThread.start();
    }


    //Main thread of the game. Carries two main parts of the game on itself:
    //updating & relocating object all around the map
    @Override
    public void run() {
        while(gameThread != null) {
            update();
            redraw();

            //delay 16 ms  = ~60 fps
            try {
                Thread.sleep(16);
            }
            catch (InterruptedException e) {
                //doing nothing
            }
        }
    }

    public void update() {
        if (keyHandler.isPausePressed()) gameState = GameState.PAUSE;
        if (gameState == GameState.PLAY) {
            //updating player
            player.update();

            //updating ghosts
            for (Ghost ghost : ghosts) {
                ghost.update();
            }

            //checking if map still contains dots
            if (objects.isEmpty()) {
                gameState = GameState.LEVELCHANGE;
            }

            //checking whether boosters ended
            for (int i = 0; i < boosters.size(); i++) {
                boosters.get(i).update();
                if (boosters.get(i).isConsumed() && boosters.get(i).getTimeCounter() == boosters.get(i).getLimit())
                {
                    //removing effects of ended boosters and boosters themselves
                    if (boosters.get(i) instanceof Speed_booster_obj) player.recalculateSpeed();
                    else if (boosters.get(i) instanceof Wall_piercer_booster_obj) {
                        player.setEthereal(false);
                        player.pushOutToMap();
                    }
                    else if (boosters.get(i) instanceof Invisibility_booster_obj) player.setInvisible(false);
                    eatablesPanel.remove(boosters.get(i));
                    boosters.remove(i);
                    repaint();
                }
                //removing boosters on the map
                else if (boosters.get(i).getTimeCounter() == 420 && !boosters.get(i).isConsumed()) {
                    eatablesPanel.remove(boosters.get(i));
                    boosters.remove(i);
                    repaint();
                }
            }
        }
        else if (gameState == GameState.PAUSE) {
            if (!keyHandler.isPausePressed()) gameState = GameState.PLAY;
        }
        else if (gameState == GameState.DEAD) {
            if (deathAnimState == deathStated) {
                //clearing panels from old ghosts and boosters, which are still on the field
                for (Ghost ghost : ghosts) {
                    charactersPanel.remove(ghost);
                }
                for (Booster booster : boosters) {
                    eatablesPanel.remove(booster);
                }
                ghosts.clear();
                boosters.clear();
                repaint();
                //starting to play death animation
                deathAnimState = deathAnimGoes;
            }
            else if (deathAnimState == deathAnimEnded && player.getLives() != 0) {
                //resetting the game
                player.setDefaultValues();
                setupGame();
                deathAnimState = deathStated;
                gameState = GameState.PLAY;
            }
            else if (deathAnimState == deathAnimEnded && player.getLives() == 0) {
                exit();
            }
        }
        else if (gameState == GameState.LEVELCHANGE) {
            if (tileManager.getTextureChangeCounter() == 1) { //means we're only in the start of changing the level
                //cleaning everything
                for (Ghost ghost : ghosts) {
                    charactersPanel.remove(ghost);
                }
                ghosts.clear();
                boosters.clear();
                eatablesPanel.removeAll();
                level++;
                repaint();
            }
            else if (tileManager.getTextureChangeCounter() == 90) {
                player.setDefaultValues();
                setupGame();
                gameState = GameState.PLAY;
                tileManager.setTextureChangeCounter(0);
            }
        }
    }

    private void redraw() {
        //redrawing points & pellets
        for (Entity object : objects) {
            object.redraw();
        }

        //redrawing boosters
        for (Booster booster : boosters) {
            booster.redraw();
        }

        //redrawing ghosts
        for (Ghost ghost : ghosts) {
            ghost.redraw();
        }

        //redrawing player
        player.redraw();

        //redrawing ui
        ui.redraw();

        //redrawing map if we are changing level
        if (gameState == GameState.LEVELCHANGE) {
            tileManager.redraw();
        }
    }

    @Override
    public void resize() {
        //changing dimensions
        maxScreenWidth = widthTileSize * maxScreenColumn;
        maxScreenHeight = heightTileSize * maxScreenRow;
        widthScale = (double) widthTileSize / originalTileSize;
        heightScale = (double) heightTileSize / originalTileSize;
        widthRatio = (double) widthTileSize / prevWidthTileSize;
        heightRatio = (double) heightTileSize / prevHeightTileSize;

        //resizing(recounting) everything:

        //labels
        layeredPane.setBounds(this.getBounds());
        mapPanel.setBounds(this.getBounds());
        eatablesPanel.setBounds(this.getBounds());
        uiPanel.setBounds(this.getBounds());
        charactersPanel.setBounds(this.getBounds());

        //resizing tiles
        tileManager.resize();

        //resizing player
        player.resize();

        //resizing all ghosts and their targets
        for (Ghost ghost : ghosts) {
            ghost.resize();
        }

        //resizing all dots and pellets
        for(Entity object : objects) {
            object.resize();
        }

        //resizing all boosters
        for (Booster booster : boosters) {
            booster.resize();
        }

        //resizing ui
        ui.resize();

        repaint();
        gameState = GameState.PLAY;
    }

    public void exit() {
        //ending of the game
        setGameThread(null);
        getWindow().dispose();
    }

    //GETTERS & SETTERS

    //getters
    public JFrame getWindow() {
        return window;
    }
    public double[][] getMapConstrains() {
        return mapConstrains;
    }
    public int getMaxScreenColumn() {
        return maxScreenColumn;
    }
    public int getMaxScreenRow() {
        return maxScreenRow;
    }
    public int getMaxScreenWidth() {
        return maxScreenWidth;
    }
    public int getMaxScreenHeight() {
        return maxScreenHeight;
    }
    public JPanel getCharactersPanel() {
        return charactersPanel;
    }
    public JPanel getEatablesPanel() {
        return eatablesPanel;
    }
    public JPanel getUiPanel() {
        return uiPanel;
    }
    public Thread getGameThread() {
        return gameThread;
    }
    public double getWidthRatio() {
        return widthRatio;
    }
    public double getHeightRatio() {
        return heightRatio;
    }
    public int getScore() {
        return score;
    }
    public GameState getGameState() {
        return gameState;
    }
    public int getWidthTileSize() {
        return widthTileSize;
    }
    public int getHeightTileSize() {
        return heightTileSize;
    }
    public double getWidthScale() {
        return widthScale;
    }
    public double getHeightScale() {
        return heightScale;
    }
    public Player getPlayer() {
        return player;
    }
    public ArrayList<Entity> getObjects() {
        return objects;
    }
    public ArrayList<Ghost> getGhosts() {
        return ghosts;
    }
    public ArrayList<Booster> getBoosters() {
        return boosters;
    }
    public ArrayList<String> getBoostersCollection() {
        return boostersCollection;
    }
    public int getLevel() {
        return level;
    }
    public boolean isPelletEaten() {
        return pelletEaten;
    }
    public boolean isFreezerEaten() {
        return freezerEaten;
    }
    public KeyHandler getKeyHandler() {
        return keyHandler;
    }
    public TileManager getTileManager() {
        return tileManager;
    }
    public CollisionChecker getCollisionChecker() {
        return collisionChecker;
    }
    public UI getUi() {
        return ui;
    }
    public GhostBehaviour getGhostsBehaviourThreadTask() {
        return ghostsBehaviourThreadTask;
    }
    public Thread getGhostBehaviour() {
        return ghostBehaviour;
    }
    public byte getDeathAnimGoes() {
        return deathAnimGoes;
    }
    public byte getDeathAnimEnded() {
        return deathAnimEnded;
    }
    public byte getDeathAnimState() {
        return deathAnimState;
    }

    //------------------------------------------------

    //setters
    public void setMaxScreenColumn(int maxScreenColumn) {
        this.maxScreenColumn = maxScreenColumn;
    }
    public void setMaxScreenRow(int maxScreenRow) {
        this.maxScreenRow = maxScreenRow;
    }
    public void setMapConstrains(double[][] mapConstrains) {
        this.mapConstrains = mapConstrains;
        Point eatenGhostsCoords = new Point((int)(mapConstrains[5][0] * widthTileSize), (int)(mapConstrains[5][1] * heightTileSize));
        Ghost.setEatenCoords(eatenGhostsCoords);
    }
    public void setGameThread(Thread gameThread) {
        this.gameThread = gameThread;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
    public void setWidthTileSize(int widthTileSize) {
        this.widthTileSize = widthTileSize;
    }
    public void setHeightTileSize(int heightTileSize) {
        this.heightTileSize = heightTileSize;
    }
    public void setPrevWidthTileSize(int prevWidthTileSize) {
        this.prevWidthTileSize = prevWidthTileSize;
    }
    public void setPrevHeightTileSize(int prevHeightTileSize) {
        this.prevHeightTileSize = prevHeightTileSize;
    }
    public void setPelletEaten(boolean pelletEaten) {
        this.pelletEaten = pelletEaten;
    }
    public void setFreezerEaten(boolean freezerEaten) {
        this.freezerEaten = freezerEaten;
    }
    public void setDeathAnimState(byte deathAnimState) {
        this.deathAnimState = deathAnimState;
    }
}
