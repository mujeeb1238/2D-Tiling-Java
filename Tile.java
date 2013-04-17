/**
   Contains information about tiles that are created 
   by using a texture. This class serves primarily to
   determine whether a tile can be collided with, although
   it is also used to move the tiles on the map
   when player movement is detected.
**/
public class Tile
{
  private boolean bCollidable;           // Can sprites collide with this tile?
  private int     LocX;                  // X location of this tile
  private int     LocY;                  // Y location of this tile

  public Tile(int x, int y, boolean bCollidable)
  {
    LocX = x;
    LocY = y;
    this.bCollidable = bCollidable;
  }

  public boolean isCollidable()
  {
    return bCollidable;
  }

  public void setCollidable(boolean flag)
  {
    bCollidable = flag;
  }
  
  public int getX()
  {
    return LocX;
  }
  
  public int getY()
  {
    return LocY;
  }
  
  public void setY(int locy)
  {
    LocY = locy;
  }
  
  public void setX(int locx)
  {
    LocX = locx;
  }
  
  public void moveY(int amt)
  {
    LocY = LocY + amt;
  }

  public void moveX(int amt)
  {
    LocX = LocX + amt;
  }
}

