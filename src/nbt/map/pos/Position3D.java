package nbt.map.pos;

/**
 * Defines a 3d position within a chunk.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class Position3D extends InChunkPosition {
  /** The y coordinate. */
  public final int y;

  /**
   * Creates a position.
   * 
   * @param x The x coordinate.
   * @param y The y coordinate.
   * @param z The z coordinate.
   */
  public Position3D(final int x, final int y, final int z) {
    super(x, z);
    this.y = y;
  }

  /**
   * Creates a 3d position from a flat position and height.
   * 
   * @param pos The flat position.
   * @param y The height.
   */
  public Position3D(final InChunkPosition pos, final int y) {
    this(pos.x, y, pos.z);
  }

}
