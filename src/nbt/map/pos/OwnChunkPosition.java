package nbt.map.pos;

/**
 * References the position of a chunk in the world as provided by the chunk.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class OwnChunkPosition extends Pair {

  /**
   * Creates an own chunk position.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   */
  public OwnChunkPosition(final int x, final int z) {
    super(x, z);
  }

}
