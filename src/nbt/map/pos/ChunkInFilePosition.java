package nbt.map.pos;

/**
 * References the position of a chunk in the file.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class ChunkInFilePosition extends Pair {

  /**
   * Creates a chunk position.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   */
  public ChunkInFilePosition(final int x, final int z) {
    super(x, z);
  }

}
