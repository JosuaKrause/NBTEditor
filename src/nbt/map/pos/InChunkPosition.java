package nbt.map.pos;

/**
 * References the position in a chunk.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class InChunkPosition extends Pair {

  /**
   * Creates an in-chunk position.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   */
  public InChunkPosition(final int x, final int z) {
    super(x, z);
  }

}
