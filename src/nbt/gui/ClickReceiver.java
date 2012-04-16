package nbt.gui;

/**
 * Receives click events on the map.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public interface ClickReceiver {

  /**
   * Is called when a click occured.
   * 
   * @param x The center x position.
   * @param z The center z position.
   */
  void clicked(int x, int z);

  /**
   * Getter.
   * 
   * @return The name of the click receiver.
   */
  String name();

  /**
   * Setter.
   * 
   * @param newRadius Sets the radius of the click receiver.
   */
  void setRadius(int newRadius);

  /**
   * Getter.
   * 
   * @return Returns the radius that affects the area of the click.
   */
  int radius();

  /**
   * Getter.
   * 
   * @return Whether the click receiver is a circle or a rectangle.
   */
  boolean isCircle();

}
