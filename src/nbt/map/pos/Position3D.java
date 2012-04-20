package nbt.map.pos;

/**
 * Defines a 3d position within a chunk.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class Position3D {
  /** The x coordinate. */
  public final int x;
  /** The y coordinate. */
  public final int y;
  /** The z coordinate. */
  public final int z;

  /**
   * Creates a position.
   * 
   * @param x The x coordinate.
   * @param y The y coordinate.
   * @param z The z coordinate.
   */
  public Position3D(final int x, final int y, final int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}