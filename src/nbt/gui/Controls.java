package nbt.gui;

/**
 * Controls for the brush tools.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public interface Controls {

  /**
   * Getter.
   * 
   * @return The minimal radius.
   */
  int getMinRadius();

  /**
   * Getter.
   * 
   * @return The maximal radius.
   */
  int getMaxRadius();

  /**
   * Setter.
   * 
   * @param radius Sets the radius of the brush.
   */
  void setRadius(int radius);

  /**
   * Getter.
   * 
   * @return The radius of the brush.
   */
  int getRadius();

}
