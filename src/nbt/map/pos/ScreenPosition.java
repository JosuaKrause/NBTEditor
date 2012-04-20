package nbt.map.pos;

/**
 * References a position on the screen.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class ScreenPosition extends Pair {

  /**
   * Creates a screen position.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   */
  public ScreenPosition(final int x, final int z) {
    super(x, z);
  }

}
