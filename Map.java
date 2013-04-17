import javax.imageio.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;
import java.awt.*;

/**
   Contains all of the information for a particular
   map, which is composed of tiled images representing
   individual 'blocks' in the map.
   
   Sprites and other objects may be placed onto the map,
   however the map itself only contains information about its
   tiles and the Sprite representing the player.
**/

public class Map
{
  private TileTest tiletest;        // Local copy of the game world
  private Sprite   player;          // Local copy of the Player sprite
  private char[][] CharMap;         // The map data (stored as a .txt file)
  private Tile[][] TileMap;         // The actual tiles
  private int      TileCountX;      // The number of tiles in each row
  private int      TileCountY;      // The number of tiles in each column
  private int      TileWidth;       // The width of a single tile
  private int      TileHeight;      // The height of a single tile
  private int      MapWidth;        // Width of the entire map
  private int      MapHeight;       // Height of the entire map
  private int      InitX;           // Initial X position of first tile
  private int      InitY;           // Initial Y position of first tile

  private BufferedImage   ImgFile;  // Stores the image file used by this map
  private BufferedImage[] Textures; // Stores the individual textures used by this map
  
  /** --------------------- Methods -------------------------- **/
  
  /**
     Creates a Map.
     
     Maps must be supplied with a copy of the game world
     and the player class in order to determine the size that
     the map should be, as well as to account for 
     player movement.
  **/
  public Map(TileTest tiletest, Sprite player)
  {
    this.tiletest = tiletest;
    this.player   = player;

    InitX = tiletest.getFrameWidth() / 4; // Center of the screen
    InitY = tiletest.getFrameHeight() / 4; // Center of the screen

    LoadMapConfig();
    LoadMapData();
    CreateTileMap();
    LoadTextures();
  }
  
  /**
     Loads all of the configuration info. for this map
     from a .INI file using a String tokenizer.
  **/
  private void LoadMapConfig()
  {
    try
    {
      InputStream is = getClass().getResourceAsStream(TileTest.INI_FILE);
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      
      String line;
      String tokens[];
      while ((line = br.readLine()) != null)
      {
        if (line.length() == 0)
           continue;
        if (line.startsWith("["))
           continue;

        tokens = line.split("=");
        if (tokens[0].equals("TileCountX"))
        {
          TileCountX = Integer.parseInt(tokens[1]);
        }
        else if (tokens[0].equals("TileCountY"))
        {
          TileCountY = Integer.parseInt(tokens[1]);
        }
        else if (tokens[0].equals("TileWidth"))
        {
          TileWidth = Integer.parseInt(tokens[1]);
        }
        else if (tokens[0].equals("TileHeight"))
        {
          TileHeight = Integer.parseInt(tokens[1]);
        }
        else if (tokens[0].equals("ImgFile"))
        {
          try { ImgFile=ImageIO.read(new File(tokens[1])); } catch(IOException e) {
          System.out.println("Error reading file("+tokens[1]+"):\n"+e); }
        }
        else if (tokens[0].equals("MapWidth"))
        {
          MapWidth = Integer.parseInt(tokens[1]);
        }
        else if (tokens[0].equals("MapHeight"))
        {
          MapHeight = Integer.parseInt(tokens[1]);
        }
      }
      br.close();
    }
    catch (IOException e)
    {
      System.out.println("Error reading file("+TileTest.INI_FILE+"):\n"+e);
    }
  }
  
  /**
     Loads the actual map data, which is stored
     in the form of a .txt file.
  **/
  private void LoadMapData()
  {
    CharMap = new char[MapHeight][MapWidth];
    File mapFile = null;
    Scanner sc = null;
    char c;
    try
    {
      mapFile = new File("map.dat");
      sc = new Scanner(mapFile);
      for (int y = 0; y < MapHeight; y++)
      {
        for (int x = 0; x < MapWidth; x++)
        {
          CharMap[y][x] = (char)sc.nextByte();
        }
      }
    }
    catch (IOException e)
    {
      System.out.println("Error reading file("+mapFile+"):\n"+e);
    }
    finally
    {
      if (sc != null)
         sc.close();
    }
  }
  
  /**
     Creates the tile map using information
     retrieved from the map data.
     
     Tiles have a position and a collidable flag.
  **/
  private void CreateTileMap()
  {
    TileMap = new Tile[MapHeight][MapWidth];

    for (int y = 0; y < MapHeight; y++)
    {
      for (int x = 0; x < MapWidth; x++)
      {
        TileMap[y][x] = new Tile(InitX+x*TileWidth, InitY+y*TileHeight, ((int)CharMap[y][x] == 1));
      }
    }
  }

  /**
     Loads the actual textures which represent
     the particular tiles in this map
  **/
  private void LoadTextures()
  {
    int numImages = TileCountX * TileCountY;
    Textures = new BufferedImage[numImages];

    int i = 0;
    for (int y = 0; y < TileCountY; y++)
    {
      for (int x = 0; x < TileCountX; x++)
      {
        Textures[i] = ImgFile.getSubimage(x*TileWidth, y*TileHeight, TileWidth, TileHeight);
        i++;
      }
    }
  } 

  /**
     Draws the map, based on the number of tiles
     and the map size.
  **/
  public boolean draw(Graphics2D g)
  {
    int tile;
    for (int y = 0; y < MapHeight; y++)
    {
      for (int x = 0; x < MapWidth; x++)
      {
        tile = CharMap[y][x];
        g.drawImage(Textures[tile], TileMap[y][x].getX(), TileMap[y][x].getY(), null);
      }
    }
    return true;
  }

  /**
     Called when the player sprite moves in the X direction
  **/
  public void adjustX(int amt)
  {
    for (int y = 0; y < MapHeight; y++)
    {
      for (int x = 0; x < MapWidth; x++)
      {
        TileMap[y][x].moveX(amt);
        if (TileMap[y][x].isCollidable() && ((player.getX() == TileMap[y][x].getX()) && (player.getY() == TileMap[y][x].getY())))
        {
          movebackX(-amt);
        }
      }
    }
  }

  /**
     Called when the player sprite moves in the Y direction
  **/
  public void adjustY(int amt)
  {
    for (int y = 0; y < MapHeight; y++)
    {
      for (int x = 0; x < MapWidth; x++)
      {
        TileMap[y][x].moveY(amt);
        if (TileMap[y][x].isCollidable() && ((player.getX() == TileMap[y][x].getX()) && (player.getY() == TileMap[y][x].getY())))
           movebackY(-amt);
      }
    }
  }

  /** Saves space **/
  private void movebackY(int amt)
  {
    for (int y = 0; y < MapHeight; y++)
    {
      for (int x = 0; x < MapWidth; x++)
      {
          TileMap[y][x].moveY(amt);
      }
    }
  }

  /** Saves space **/
  private void movebackX(int amt)
  {
    for (int y = 0; y < MapHeight; y++)
    {
      for (int x = 0; x < MapWidth; x++)
      {
        TileMap[y][x].moveX(amt);
      }
    }
  }

  public int getTileWidth()
  {
    return TileWidth;
  }
  public int getTileHeight()
  {
    return TileHeight;
  }
  public int getMapWidth()
  {
    return MapWidth;
  }
  public int getMapHeight()
  {
    return MapHeight;
  }
}