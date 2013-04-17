import java.awt.Graphics;

/**
   A Spell is any action taken by a sprite,
   and includes Attacking (with a weapon),
   casting a Heal, casting a damaging ability,
   or interacting with the environment; for example,
   opening a chest or door.
   
   Spells are abstract -- subclasses must implement
   the effect(), createAnimation(), and animate()
   methods.
**/
public abstract class Spell
{
  protected String name;                // The name of the spell, for descriptive purposes

  protected Sprite caster;              // The sprite casting the spell

  protected Sprite target;              // The target of the cast; this can be the caster
                                        // as in the case of a self-heal

  protected Animation anim;             // The animation associated with this spell's cast
  
  /**
     A spell can have three states:
       
       STANDBY -- The spell has not been casting and is idling
       CASTING -- The spell is being cast at this moment
       CAST    -- The spell has been cast and just needs its anim. updated
     
     Spells default to STANDBY and are placed in the other states
     when cast() is called.
  **/
  protected SpellState state;

  /**
     The spell timer indicates how much time is remaining before
     the spell has finished casting, based on the spell's
     animation.
  **/
  protected long spellTimer;

  /** 
      Creates a spell with no name and no animation.
      Not really useful at all, actually.
  **/
  public Spell()
  {
    state = SpellState.STANDBY;
    spellTimer = 0;
  }

  /** 
      Creates a spell with the specified name and animation
  **/
  public Spell(String name, Animation anim)
  {
    this.name = name;
    this.anim = anim;
    state = SpellState.STANDBY;
    spellTimer = 0;
  }

  /**
     Called to indicate that the spell has just been
     cast. This puts the spell in the CASTING state.
  **/
  public void cast(Sprite caster, Sprite target)
  {
    if (state == SpellState.STANDBY)
    {
      this.caster = caster;
      this.target = target;
      state = SpellState.CASTING;
    }
  }
  
  public SpellState getState()
  {
    return state;
  }

  /** 
      Returns a description of the spell (its name)
  **/
  @Override public String toString()
  {
    return name;
  }

  /**
     The effect() method should carry out the actual effects
     of the spell (i.e. damage the target for 50 damage and
     subtract 35 mana from the caster).
  **/
  protected abstract void effect();
  
  /**
     The createAnimation() method should make the
     various imaeges which comprise of the spell's
     animation.
  **/
  protected abstract void createAnimation();
  
  /**
     The draw() method actually animates the
     spell, depending on how much time has elapsed
     since the spell was cast and the current animation
     frame that needs to be drawn.
  **/
  protected abstract void draw(Graphics g, long elapsedTime);
}