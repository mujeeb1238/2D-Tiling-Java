/**
   All Spells have a SpellState, which is either
   
       STANDBY  -- Default state; does nothing
       CASTING  -- The spell is being cast right now
       CAST     -- The spell was just cast and needs it's animation updated
**/
public enum SpellState
{
  STANDBY,
  CASTING,
  CAST
}