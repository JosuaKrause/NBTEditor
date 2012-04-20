package nbt.map.pos;

/**
 * Block position in the world.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class WorldPosition extends Pair {

  private final boolean xlt;

  private final boolean zlt;

  /**
   * Creates a world position.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   */
  public WorldPosition(final int x, final int z) {
    this(x, z, x < 0, z < 0);
  }

  /**
   * Creates a world position.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   * @param xlt If x is smaller than zero. This information can be lost due to
   *          rounding errors.
   * @param zlt If z is smaller than zero. This information can be lost due to
   *          rounding errors.
   */
  public WorldPosition(final int x, final int z, final boolean xlt,
      final boolean zlt) {
    super(x, z);
    this.xlt = xlt;
    this.zlt = zlt;
  }

  /**
   * Calculates the position within the chunk.
   * 
   * @return The position in the chunk.
   */
  public InChunkPosition getPosInChunk() {
    final int cx = x % 16 + (xlt ? 15 : 0);
    final int cz = z % 16 + (zlt ? 15 : 0);
    return new InChunkPosition(cx, cz);
  }

  /**
   * Gets the position of the corresponding chunk.
   * 
   * @return The chunk position.
   */
  public ChunkPosition getPosOfChunk() {
    final int cx = x / 16 - (xlt ? 1 : 0);
    final int cz = z / 16 - (zlt ? 1 : 0);
    return new ChunkPosition(cx * 16, cz * 16);
  }

}
