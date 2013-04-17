import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;

/** 
    The main class which represents the game world
**/

public class TileTest extends JFrame implements Runnable
{
  // The .ini file used by this version of the program
  public static final String INI_FILE = "TileTest.ini";
  
  // number of 0ms delays before thread yields
  private static final int NO_DELAYS_PER_YIELD = 16;
  
  // max number of frames un-rendered
  private static final int MAX_FRAME_SKIPS = 5;
  
  private static final int FPS = 100; // the desired FPS

  private static String VersionID;   // Game version
  private volatile boolean isRunning;// is the game running?

  private int FrameWidth;            // The width of the Frame
  private int FrameHeight;           // The height of the Frame
  
  private Thread animator;           // create a thread seperate from AWT dispatch for rendering

  private Map[]           maps;      // Used to store the tileset

  private BufferedImage   bImg;      // Back buffer img
  private Graphics2D       dbg;      // Back buffer graphics
  
  private Font font;                 // Font used to write messages to the screen
  private FontMetrics metrics;       // Config. for the font

  private Sprite player;             // The player character
  private Spell[] spells;            // The player's spells

  private int tWidth;
  private int tHeight;
  
  private long gameStartTime;        // time that the game started
  private long period;               // the period between rendering


  protected GameAction moveUp;
  protected GameAction moveDown;
  protected GameAction exit;
  protected GameAction moveLeft;
  protected GameAction moveRight;
  protected GameAction heal;
  protected GameAction takeDamage;
  protected InputManager inputManager;

  //---- Methods ---- //

  public TileTest()
  {
    loadMainConfig();
  }
  
  /**
     Called to signal that the game should begin!
  **/
  public void startGame()
  {
    // start the animation thread
    if (animator == null || !isRunning)
    {
      animator = new Thread(this);
      animator.start();
    }
  }
  
  /**
     Initiates the game settings
  **/
  private void init()
  {
    isRunning = false;
    // set the period
    period = (long)1000.0 / FPS;
    
    /** Create the game's input manager **/
    inputManager = new InputManager(this);
    createGameActions();

    /** Set up the screen size and whatnot **/
    setSize(FrameWidth, FrameHeight);
    setResizable(false);
    setTitle("TileTest v" + VersionID);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    font = new Font("SansSerif", Font.BOLD, 16);
    metrics = this.getFontMetrics(font);

    /**
       Create the player's spells. This is a test area
       and should be changed/deleted when a better approach
       is implemented.
    **/
    spells = new Spell[1];
    spells[0] = new PlayerHeal();
    player = new Sprite(FrameWidth/2, FrameHeight/2, "../art/player.png", Direction.NORTH, 100, 100, 10, 10, 10, spells);

    /**
       Creates the map. For now, there is only one map.
       This is also a test area and should be changed
       or deleted.
    **/
    maps = new Map[1];
    maps[0] = new Map(this, player);

    tWidth = maps[0].getTileWidth();
    tHeight = maps[0].getTileHeight();
    bImg = new BufferedImage(FrameWidth,FrameHeight,BufferedImage.TYPE_INT_RGB);
  }

  /**
     Creates game actions associated with particular
     key presses (i.e. casting a spell).
  **/
  private void createGameActions()
  {
    moveUp = new GameAction("move up");
    exit = new GameAction("exit",
    GameAction.DETECT_INITIAL_PRESS_ONLY);
    moveLeft = new GameAction("moveLeft");
    moveRight = new GameAction("moveRight");
    moveDown = new GameAction("move down");
    heal = new GameAction("heal", GameAction.DETECT_INITIAL_PRESS_ONLY);
    takeDamage = new GameAction("takeDamage", GameAction.DETECT_INITIAL_PRESS_ONLY);

    inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
    inputManager.mapToKey(moveDown, KeyEvent.VK_DOWN);
    inputManager.mapToKey(heal, KeyEvent.VK_H);
    inputManager.mapToKey(takeDamage, KeyEvent.VK_E);

    inputManager.mapToKey(moveUp, KeyEvent.VK_UP);

    // move with the arrow keys...
    inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
    inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
  }

  /**
     Updates the game's state.
     For now, this just checks the input
  **/
  private void update(long elapsedTime)
  {
    checkInput();
  }

  private void checkInput()
  {
    if (moveLeft.isPressed())
    {
      maps[0].adjustX(tWidth);
      player.changeDir(Direction.WEST);
    }
    if (moveRight.isPressed())
    {
      maps[0].adjustX(-tWidth);
      player.changeDir(Direction.EAST);
    }
    if (moveUp.isPressed())
    {
      maps[0].adjustY(tHeight);
      player.changeDir(Direction.NORTH);
    }
    if (moveDown.isPressed())
    {
      maps[0].adjustY(-tHeight);
      player.changeDir(Direction.SOUTH);
    }
    if (exit.isPressed())
       stop();

    if (takeDamage.isPressed())
       player.takeDamage(10);

    if (heal.isPressed())
    {
      player.castSpell(spells[0], player);
    }
  }

  /**
     The main method of the program.
     
     Controls program flow and contains
     the game loop.
  **/
  public void run()
  {
    // set up variables for calculating the FPS
    long beforeTime, afterTime, timeDiff, sleepTime;
    long overSleepTime = 0L;
    int  noDelays      = 0;
    long excess        = 0L;

    gameStartTime = System.nanoTime();
    beforeTime    = gameStartTime;
    
    long startTime = System.nanoTime() / 1000000L; // nano->milli
    long currTime = startTime;

    isRunning = true;
    setVisible(true);
    while (isRunning)
    {
      long elapsedTime = System.nanoTime() / 1000000L - currTime;
      currTime += elapsedTime;

      update(elapsedTime);
      render(elapsedTime);
      paintScreen();

      // calculate the FPS
       afterTime = System.nanoTime();
       timeDiff = afterTime - beforeTime;
       sleepTime = (period - timeDiff) - overSleepTime;

       if (sleepTime > 0)  // Some time left in this cycle...
       {
         try
         {
           Thread.sleep(sleepTime/1000000L); // nano -> milli
         }
         catch (InterruptedException ex){}
         overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
       }
       else // sleepTime <= 0; the frame took longer than the period
       {
         excess -= sleepTime;       // store excess time value
         overSleepTime = 0L;

         if (++noDelays >= NO_DELAYS_PER_YIELD)
         {
           Thread.yield(); // give another thread a chance to run
           noDelays = 0;
         }
       }

       beforeTime = System.nanoTime();

       // adjust if necessary to maintain desired FPS
       int skips = 0;
       while ((excess > period) && (skips < MAX_FRAME_SKIPS))
       {
         excess -= period;
         update(elapsedTime);    // update state but don't render
         skips++;
       }
    }
    /** Now that we're done playing... adios! **/
    System.exit(0);
  }
  
  /**
     Called to stop the game. This is different from pausing;
     this will actually kill the game.
  **/
  private void stop()
  {
    isRunning = false;
  }
  
  /**
     Handles all of the rendering done for the game
  **/
  private void render(long elapsedTime)
  {
    /** 
        Gets the graphic's context to the backbuffer
        and clears it to a black background.
    **/
    dbg = (Graphics2D)bImg.getGraphics();
    dbg.setColor(Color.black);
    dbg.fillRect(0,0, FrameWidth, FrameHeight);

    /** If we can't draw the map, abandon ship **/
    if (!((maps[0].draw(dbg))))
       return;

    player.draw(dbg); // Draw the player sprite

    /** for all of the player's spells... **/
    for (Spell sp : spells)
    {
      /** if one of the spells is being cast... **/
      if (sp.getState() == SpellState.CAST || sp.getState() == SpellState.CASTING)
         /** make sure the spell's effect goes through **/
         sp.draw(dbg, elapsedTime);
    }
    /**
       Draw the player's stats on the screen. This needs
       to be overhauled later.
    **/
    drawStats(dbg);
    
    // Get rid of the graphic's context since we're done drawing
    dbg.dispose();
  }

  /**
     Paints the backbuffer image
     to the canvas
  **/
  private void paintScreen()
  {
    try
    {
      // get this JFrame's graphics context
      Graphics g = this.getGraphics();

      // if the backbuffer is not null
      if ((g != null) && (bImg != null))
         // draw the backbuffer img using the graphics context
         g.drawImage(bImg, 0, 0, null);

      // sync the display
      Toolkit.getDefaultToolkit().sync();

      // dispose of the graphics context
      g.dispose();
    }
    catch (Exception e) {} // ignore it
  }

  /** 
      Loads configuration settings from a .INI file
      using a String tokenizer.
  **/
  private void loadMainConfig()
  {
    InputStream in = this.getClass().getResourceAsStream(INI_FILE);
    BufferedReader br = new BufferedReader(new InputStreamReader(in));

    String line;
    String[] tokens;
    try
    {
     while ((line = br.readLine()) != null)
     {
       if (line.length() == 0)     // blank line
          continue;
       if (line.startsWith("["))   // comment
          continue;

       tokens = line.split("=");
       printTokens(tokens);
       if (tokens[0].equals("Version"))
       {
         VersionID = tokens[1];
       }
       else if (tokens[0].equals("FrameWidth"))
       {
         FrameWidth = Integer.parseInt(tokens[1]);
       }
       else if (tokens[0].equals("FrameHeight"))
       {
         FrameHeight = Integer.parseInt(tokens[1]);
       }
     }
     br.close();
    }
    catch (IOException e)
    {
      System.err.println("Error reading file("+INI_FILE+"):\n"+e);
    }
    
    // initialize the rest of the code
    init();
  }

  public int getFrameWidth()
  {
    return FrameWidth;
  }
  
  public int getFrameHeight()
  {
    return FrameHeight;
  }

  /** Clearly the most important method. **/
  public static void main(String[] args)
  {
    new TileTest().startGame();
  }

  /** DEBUG METHOD. **/
  private void printTokens(String[] tokens)
  {
    for (int i = 0; i < tokens.length; i++)
        System.out.println(tokens[i]);
  }

  /**
     Draws the player's stats onto the screen
     
     DEBUG METHOD.
  **/
  private void drawStats(Graphics2D g)
  {
    g.setColor(Color.WHITE);
    g.setFont(font);

    int space = 10;
    
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    String msg = Integer.toString(player.getHealth());
    int x = (FrameWidth - 20) - metrics.stringWidth(msg);
    int y = (FrameHeight - 30)- metrics.getHeight();
    g.drawString(msg, x, y);

    y += space;
    msg = Integer.toString(player.getMana());
    g.drawString(msg, x, y);

    for (int i : player.getAttributes())
    {
      y += space;
      msg = Integer.toString(i);
      g.drawString(msg, x, y);
    }
  }
}