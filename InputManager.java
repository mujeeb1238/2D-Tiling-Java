import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

public class InputManager implements KeyListener
{
  /** An invisible cursor **/
  public static final Cursor INVISIBLE_CURSOR =
         Toolkit.getDefaultToolkit().createCustomCursor(
            Toolkit.getDefaultToolkit().getImage(""),
            new Point(0,0),
            "Invisible");
  
  private static final int NUM_KEY_CODES = 600;
  private GameAction[] keyActions = new GameAction[NUM_KEY_CODES];
  
  private Component comp;
  
  /** Creates an InputManager that listens to input from the
      specified component.
  **/
  public InputManager(Component comp)
  {
    this.comp = comp;
    
    comp.addKeyListener(this);
    
    // Disable key traversals(TAB, etc.)
    comp.setFocusTraversalKeysEnabled(false);
  }
  
  public void setCursor(Cursor cursor)
  {
    comp.setCursor(cursor);
  }
  
  /**
     Maps a GameAction to a key. The key codes are
     defined in java.awt.KeyEvent. If the key already
     has a GameAction mapped to it, the new GameAction
     overwrites it.
  **/
  public void mapToKey(GameAction action, int keyCode)
  {
    keyActions[keyCode] = action;
  }
  
  /** Clears all mapped key actions **/
  public void clearMap(GameAction action)
  {
    for (int i = 0; i < keyActions.length; i++)
    {
      if (keyActions[i] == action)
         keyActions[i] = null;
    }
    
    action.reset();
  }
  
  /**
     Gets a list of the names of the key actions mapped
     to this GameAction. Each entry in the list is a String
  **/
  public List<String> getMaps(GameAction gameCode)
  {
    ArrayList<String> list = new ArrayList<String>();
    
    for (int i = 0; i < keyActions.length; i++)
    {
      if (keyActions[i] == gameCode)
         list.add(getKeyName(i));
    }
    return list;
  }
  
  public void resetAllGameActions()
  {
    for (int i = 0; i < keyActions.length; i++)
    {
      if (keyActions[i] != null)
         keyActions[i].reset();
    }
  }
  
  public static String getKeyName(int keyCode)
  {
    return KeyEvent.getKeyText(keyCode);
  }
  
  private GameAction getKeyAction(KeyEvent e)
  {
    int keyCode = e.getKeyCode();
    if (keyCode < keyActions.length)
      return keyActions[keyCode];
    else
      return null;
  }
  
  /** -------- KeyListener methods --------- **/
  public void keyPressed(KeyEvent e)
  {
    GameAction action = getKeyAction(e);
    if (action != null)
       action.press();
    
    e.consume();
  }
  
  public void keyReleased(KeyEvent e)
  {
    GameAction action = getKeyAction(e);
    if (action != null)
       action.release();
    
    e.consume();
  }
  
  public void keyTyped(KeyEvent e)
  {
    e.consume();
  }
}


