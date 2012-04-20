package nbt.map.pos;

/**
 * References the position of a chunk in the world.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class ChunkPosition extends Pair {

  /**
   * Creates a chunk position.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   */
  public ChunkPosition(final int x, final int z) {
    super(x, z);
  }

}
