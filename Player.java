/**
   The Player is a specific sprite
   that represents the main character.
   
   For now, the class is barren. In the future,
   the die() method will be implemented to end
   the game if this sprite dies. An inventory
   may also be added.
**/
public class Player extends Sprite
{
  protected Player()
  {
    super();
  }
  
  /**
     If the player dies, the game is over
  **/
  @Override protected void die()
  {
    /**
       Play Game Over message
    **/
    super.die();
  }  
}    