import javax.imageio.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
   The Sprite class represents a character in the game.
   Sprites have a location, image, are able to move around
   the map, and possess combat statistics.
   
   At this time, sprites do not have inventories, but that
   will be a future addition.
**/
public class Sprite
{
  protected int LocationX;      // The X location of this Sprite on the map
  protected int LocationY;      // The Y location of this Sprite on the map
  protected Direction dir;      // The direction the sprite is facing right now
  protected Direction oldDir;   // The direction the sprite was just facing

  protected BufferedImage img;  // The Image used to represent this Sprite
  
  /** Attributes **/
  protected int[] Attributes;  // Stores all of the attributes for retrieval
  protected int Strength;       // The physical strength of this sprite
  protected int Defense;        // The physical defense of this sprite
  protected int MagicDefense;   // The magical defense of this sprite

  /** Stats **/
  protected int Health;         // How much health does the sprite have?
  protected int Mana;           // How much Mana does the sprite have?

  /** Abilities **/
  protected Spell[] Spells;     // The spells that this sprite can use
  
  protected Sprite()
  {
    // stuff
  }

  public Sprite(String imgURL)
  {
    this(0, 0, imgURL, Direction.NORTH, 100, 100, 10, 10, 10, null);
  }
  
  public Sprite(int x, int y, String imgURL, Direction dir, int health, int mana, int str, int def, int mdef, Spell[] sp)
  {
    LocationX = x;
    LocationY = y;
    this.dir = dir;
    oldDir = Direction.NORTH;
    Health = health;
    Mana = mana;
    Strength = str;
    Defense = def;
    MagicDefense = mdef;
    Spells = sp;
    
    Attributes = new int[3];
    Attributes[0] = str;
    Attributes[1] = def;
    Attributes[2] = mdef;

    try { img = ImageIO.read(new File(imgURL)); } catch (IOException e) {
    System.out.println("Error reading img.\n"+e); }
  }
  
  public Spell[] getSpells()
  {
    return Spells;
  }
  
  public int[] getAttributes()
  {
    return Attributes;
  }
  
  public void castSpell(Spell spell, Sprite target)
  {
    spell.cast(this, target);
  }
  
  /**
     Called when the sprite takes damage
     from any source.
     
     If the sprite's health reaches 0,
     the sprite dies and it's die() method
     is called.
  **/
  public void takeDamage(int amount)
  {
    Health -= amount;
    if (Health < 0)
    {
      Health = 0;
      die();
    }
  }
  
  /**
     Called when the sprite is healed
     from any source.
     
     The maximum health for any sprite
     is 100. This may be modified at a future date
     to account for boss characters with large
     health pools. 
  **/
  public void heal(int amount)
  {
    Health += amount;
    if (Health > 100)
       Health = 100;
  }
  
  
  /**
     Called when the sprite's mana is drained
     from any source.
     
     No sprite can have negative mana.
  **/
  public void drainMana(int amount)
  {
    Mana -= amount;
    if (Mana < 0)
       Mana = 0;
  }

  public int getHealth()
  {
    return Health;
  }
  
  public int getMana()
  {
    return Mana;
  }

  /**
     Called when the sprite's health reaches 0.
     This method's body will be different for
     most sprites.
  **/
  protected void die()
  {
    // Stuff
  }

  public int getX()
  {
    return LocationX;
  }

  public int getY()
  {
    return LocationY;
  }
  
  
  /**
     Returns the direction that the sprite
     is currently facing.
  **/
  public Direction getFacing()
  {
    return dir;
  }
  
  /**
     Returns the direction that the sprite
     was facing previously.
  **/
  public Direction getPrevFacing()
  {
    return oldDir;
  }
  
  /**
     Orients the sprite to the value of dir.
     
     This method can only be called by the Sprite class
     and its subclasses; the game calls changeDir() instead,
     which alters the sprite's direction. This method is
     then called internally.
  **/
  protected void orient()
  {
    switch(dir) // The direction we want the sprite to face
    {
      case NORTH:
           orientNorth();
           break;
      case EAST:
           orientEast();
           break;
      case SOUTH:
           orientSouth();
           break;
      case WEST:
           orientWest();
           break;
    }
  }
  
  /**
     Called by the game to indicate that the sprite
     should change its direction.
  **/
  public void changeDir(Direction direction)
  {
    oldDir = dir;
    dir = direction;
    orient();
  }

  /**
     Turns the sprite to face North
  **/
  protected void orientNorth()
  {
    AffineTransform transform = new AffineTransform();
    switch(oldDir) // the direction the sprite was just facing
    {
      case NORTH:
           // already facing north
           return;
      case EAST:
           // need to rotate 90
           transform.rotate((-Math.PI/2), img.getWidth()/2, img.getHeight()/2);
           break;
      case SOUTH:
           // need to rotate 180
           transform.rotate(Math.PI, img.getWidth()/2, img.getHeight()/2);
           break;
      case WEST:
           // need to rotate -90
           transform.rotate(Math.PI/2, img.getWidth()/2, img.getHeight()/2);
           break;
    }
    oldDir = dir;

    AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
    img = op.filter(img, null);

    dir = Direction.NORTH;
  }
  
  /**
     Turns the sprite to face East
  **/
  protected void orientEast()
  {
    AffineTransform transform = new AffineTransform();
    switch(oldDir)
    {
      case NORTH:
           // rotate 90
           transform.rotate(Math.PI/2, img.getWidth()/2, img.getHeight()/2);
           break;
      case EAST:
           // already facing east
           return;
      case SOUTH:
           // rotate -90
           transform.rotate(-Math.PI/2, img.getWidth()/2, img.getHeight()/2);
           break;
      case WEST:
           // rotate 180
           transform.rotate(Math.PI, img.getWidth()/2, img.getHeight()/2);
           break;
    }
    oldDir = dir;

    AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
    img = op.filter(img, null);

    dir = Direction.EAST;
  }

  /** 
      Turns the sprite to face South
  **/
  protected void orientSouth()
  {
    AffineTransform transform = new AffineTransform();
    switch(oldDir)
    {
      case NORTH:
           // rotate 180
           transform.rotate(Math.PI, img.getWidth()/2, img.getHeight()/2);
           break;
      case EAST:
           // rotate 90
           transform.rotate(Math.PI/2, img.getWidth()/2, img.getHeight()/2);
           break;
      case SOUTH:
           // already South
           return;
      case WEST:
           // rotate -90
           transform.rotate(-Math.PI/2, img.getWidth()/2, img.getHeight()/2);
           break;
    }
    oldDir = dir;

    AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
    img = op.filter(img, null);

    dir = Direction.SOUTH;
  }
  
  /**
     Turns the sprite to face West
  **/
  protected void orientWest()
  {
    AffineTransform transform = new AffineTransform();
    switch(oldDir)
    {
      case NORTH:
           // rotate -90
           transform.rotate(-Math.PI/2, img.getWidth()/2, img.getHeight()/2);
           break;
      case EAST:
           // rotate 180
           transform.rotate(Math.PI, img.getWidth()/2, img.getHeight()/2);
           break;
      case SOUTH:
           // rotate 90
           transform.rotate(Math.PI/2, img.getWidth()/2, img.getHeight()/2);
           break;
      case WEST:
           // already facing West
           return;
    }
    oldDir = dir;

    AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
    img = op.filter(img, null);

    dir = Direction.WEST;
  }


  public void move(int x, int y)
  {
    LocationX += x;
    LocationY += y;
  }

  /**
     Draws this sprite by drawing it's image.
     Pretty straightforward.
  **/
  public void draw(Graphics2D g)
  {
    g.drawImage(img, LocationX, LocationY, null);
  }  
}