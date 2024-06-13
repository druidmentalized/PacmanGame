package Main;

import Entity.*;
import Tile.TileManager;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import Entity.Ghost;
import Object.*;

public class GamePanel extends JPanel implements Runnable
{
    //SCREEN SETTINGS
    final int originalTileSize = 8; // 8x8 tile
    public final int scale = 3;
    public final int tileSize = originalTileSize * scale; //24x24 tile
    public int maxScreenColumn;
    public int maxScreenRow;
    private final int maxScreenHeight;
    private final int maxScreenWidth;

    //ENTITIES & OBJECTS
    public Player player;
    public ArrayList<Entity> objects = new ArrayList<>();
    public ArrayList<Ghost> ghosts = new ArrayList<>();
    public ArrayList<String> boostersCollection = new ArrayList<>(Arrays.asList("Speed booster",
            "Heart",
            "Wall piercer",
            "Invisibility mode",
            "Ghost freezer"));
    private String mapName;
    public ArrayList<Booster> boosters = new ArrayList<>();
    private double[][] mapConstrains;


    //GAME SETTINGS
    private final int FPS = 60;
    public int score = 0;
    public int level = 1;
    public GameState gameState = GameState.PLAY;
    public boolean pelletEaten = false;
    public boolean freezerEaten = false;

    //SYSTEM
    private JFrame window;
    public KeyHandler keyHandler = new KeyHandler(this);
    public TileManager tileManager = new TileManager(this);
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public UI ui = new UI(this);
    Thread gameThread;
    public GhostBehaviour behaviourThreadTask = new GhostBehaviour(this);
    public Thread ghostBehaviour;

    //DEATH PROCESSING
    public final byte deathStated = 0;
    public final byte deathAnimGoes = 1;
    public final byte deathAnimEnded = 2;
    public byte deathAnimState = deathStated;

    public GamePanel(JFrame window, String mapName, ArrayList<String> boostersCollection) {
        this.window = window;
        this.mapName = mapName;
        tileManager.loadMap(mapName);
        maxScreenWidth = maxScreenColumn * tileSize;
        maxScreenHeight = maxScreenRow * tileSize;
        this.setPreferredSize(new Dimension(maxScreenWidth, maxScreenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
        //this.boostersCollection = boostersCollection;
        //adding player
        player = new Player(this);
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
        ghostBehaviour = new Thread(behaviourThreadTask);
        ghostBehaviour.start();

        //filling field with pellets and dots if it is changing of the level
        if (gameState == GameState.LEVELCHANGE) {
            for (int row = 0; row < maxScreenRow; row++) {
                for (int column = 0; column < maxScreenColumn; column++) {
                    if (tileManager.tiles[row][column].tileType == 11) {
                        objects.add(new Point_obj(this, column * tileSize, row * tileSize));
                    }
                    else if (tileManager.tiles[row][column].tileType == 12) {
                        objects.add(new Power_pellet_obj(this, column * tileSize, row * tileSize));
                    }
                }
            }
        }
    }


    public void startGameThread() {
        gameThread = new Thread(this, "gamePanel");
        gameThread.start();
    }


    @Override
    public void run() {
        //making cycle game cycle using delta time approach
        double drawInterval = 1_000_000_000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        if (keyHandler.pausePressed) gameState = GameState.PAUSE;
        if (gameState == GameState.PLAY) {
            //updating
            player.update();

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
                if (boosters.get(i).consumed && boosters.get(i).timeCounter == boosters.get(i).limit)
                {
                    if (boosters.get(i) instanceof Speed_booster_obj) player.speed -= 2;
                    else if (boosters.get(i) instanceof Wall_piercer_booster_obj) player.ethereal = false;
                    else if (boosters.get(i) instanceof Invisibility_booster_obj) player.invisible = false;
                    else if (boosters.get(i) instanceof Ghosts_freezer_obj) {}
                    boosters.remove(i);
                }
                else if (boosters.get(i).timeCounter == 420) {
                    boosters.remove(i);
                }
            }
        }
        else if (gameState == GameState.PAUSE) {
            if (!keyHandler.pausePressed) gameState = GameState.PLAY;
        }
        else if (gameState == GameState.DEAD) {
            if (deathAnimState == deathStated) {
                ghosts.clear();
                boosters.clear();
                deathAnimState = deathAnimGoes;
            }
            else if (deathAnimState == deathAnimEnded && player.lives != 0) {
                player.setDefaultValues();
                setupGame();
                deathAnimState = deathStated;
                gameState = GameState.PLAY;
            }
        }
        else if (gameState == GameState.LEVELCHANGE) {
            if (tileManager.textureChangeCounter == 1) { //means we're only in the start of changing the level
                ghosts.clear();
                boosters.clear();
            }
            else if (tileManager.textureChangeCounter == 90) {
                player.setDefaultValues();
                setupGame();
                gameState = GameState.PLAY;
                tileManager.textureChangeCounter = 0;
            }
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D)g);
    }

    public void draw(Graphics2D g2) {
        //drawing tiles/map
        tileManager.draw(g2);
        //drawing points & pellets
        for (Entity object : objects) {
            object.draw(g2);
        }
        //drawing boosters
        for (Booster booster : boosters) {
            booster.draw(g2);
        }
        //drawing ghosts
        for (Ghost ghost : ghosts) {
            ghost.draw(g2);
        }
        //drawing player
        player.draw(g2);
        //drawing ui
        ui.draw(g2);
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
        int[] eatenGhostsCoords = new int[]{(int)(mapConstrains[5][0] * tileSize), (int)(mapConstrains[5][1] * tileSize)};
        Ghost.setEatenCoords(eatenGhostsCoords);
    }
}
