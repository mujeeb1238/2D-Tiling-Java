import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
/**
   Loads images that are stored in the form of
   one large tiled PNG file (such as tilemaps).
   This ImageTileLoader only works with tiled images
   of size 32x32.

   This class is composed entirely of static methods
   and cannot be instantiated.
**/
public final class ImageTileLoader
{
  // The tiled image that contains all of the tiles
  static BufferedImage tiledImage = new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
  
  /**
     Sets the image that will be used as the tiledImage,
     using a String as the img URL.
  **/
  public static void setTiledImage(String imgURL)
  {
    if (imgURL == null)
       tiledImage = null;
    try
    {
      tiledImage = ImageIO.read(new File(imgURL));
    }
    catch (IOException e){}
  }
  
  /**
     Sets the image that will be used as the tiledImage,
     using a File as the img URL.
  **/
  public static void setTiledImage(File f)
  {
    if (f == null)
       tiledImage = null;
    try
    {
      tiledImage = ImageIO.read(f);
    }
    catch (IOException e){}
  }
  
  /**
     Clears the tiledImage by setting it to null
  **/
  public static void clearTiledImage()
  {
    tiledImage = null;
  }

  /** Returns the image corresponding to the coordinates
      (32x,32y) on the tiledImage.
  **/
  public static BufferedImage getTile(int x, int y)
  {
    return tiledImage.getSubimage(x*32, y*32, 32, 32);
  }

  /** Can't create an ImageTileLoader **/
  private ImageTileLoader(){}
}
  



