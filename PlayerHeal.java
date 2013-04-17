import java.awt.*;
import java.awt.image.*;
import javax.swing.ImageIcon;

/**
   PlayerHeal is a Spell which heals the
   caster for 10 Health and costs 10 Mana to cost.

   Currently, only the Player sprite has access to
   this spell.
**/
public class PlayerHeal extends Spell
{
  /** The images that are used to draw the animation
      for this spell **/
  private BufferedImage forming;
  private BufferedImage formed;
  private BufferedImage dissipating;
  private BufferedImage dissipated;


  public PlayerHeal()
  {
    super();
    name = "Player Heal";
    createAnimation();
  }

  /**
     Makes the spell's effects go through.
     In this case, the spell heals the caster for 10
     and costs 10 mana to cast.
  **/
  protected void effect()
  {
    caster.drainMana(10);
    target.heal(10);
  }

  private Image loadImage(String imgURL)
  {
    return new ImageIcon(imgURL).getImage();
  }

  /**
     Creates the animation that represents this spell.
     
     The PlayerHeal spell consists of 4 animations:
     
         A 'forming' animation, when the spell is just coming together
         A 'formed' animation, when the spell is fully animated
         A 'dissipating' animation, when the spell is completing
         A 'dissipated' animation, when the spell has finished
  **/
  protected void createAnimation()
  {
    loadTiledImages();

    anim = new Animation();
    anim.addFrame(forming, 100);
    anim.addFrame(formed, 150);
    anim.addFrame(dissipating, 150);
    anim.addFrame(dissipated, 100);
  }
  
  /**
     Uses the ImageTileLoader class to load BufferedImages
     which represent the various stages of the spell **/
  protected void loadTiledImages()
  {
    ImageTileLoader.setTiledImage("../art/effects32.png");
    forming = ImageTileLoader.getTile(1, 4);
    formed = ImageTileLoader.getTile(1, 8);
    dissipating = ImageTileLoader.getTile(1, 5);
    dissipated = ImageTileLoader.getTile(1, 6);
    
    // IMPORTANT: Clear the ImageTileLoader
    ImageTileLoader.clearTiledImage();
  }

  /**
     Draws the animation image onto the sprite casting
     this spell, because it is a self-heal.
  **/
  public void draw(Graphics g, long elapsedTime)
  {
    /** If the spell was just cast... **/
    if (state == SpellState.CASTING)
    {
      anim.start(); // start the animation (from the beginning)
      g.drawImage(anim.getImage(), caster.getX(), caster.getY(), null);
      spellTimer += elapsedTime; // increase the spell timer so that the spell knows
                                 // when it is finished casting

      state = SpellState.CAST;   // the spell has now been 'cast', we just need to update
                                 // its animation from now on
    }

    /** If the spell has already been cast **/
    else if (state == SpellState.CAST)
    {
      anim.update(elapsedTime); // update the animation since the last drawing
      g.drawImage(anim.getImage(), caster.getX(), caster.getY(), null);
      spellTimer += elapsedTime;
    }

    /** The spell is done casting if this is true **/
    if (spellTimer > anim.getTotalDuration())
    {
      effect();
      
    /**
       The spell has finished casting,
       so we need to reset its state variable and timer.
    **/
    state = SpellState.STANDBY;
    spellTimer = 0;
    }
  }
}