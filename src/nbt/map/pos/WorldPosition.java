package nbt.map.pos;

/**
 * Block position in the world.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class WorldPosition extends Pair {

  /**
   * Creates a world position.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   */
  public WorldPosition(final int x, final int z) {
    super(x, z);
  }

  /**
   * Calculates the position within the chunk.
   * 
   * @return The position in the chunk.
   */
  public InChunkPosition getPosInChunk() {
    final int cx = x >= 0 ? x % 16 : 15 + (x + 1) % 16;
    final int cz = z >= 0 ? z % 16 : 15 + (z + 1) % 16;
    return new InChunkPosition(cx, cz);
  }

  /**
   * Gets the position of the corresponding chunk.
   * 
   * @return The chunk position.
   */
  public ChunkPosition getPosOfChunk() {
    final int cx = x >= 0 ? x / 16 : (x + 1) / 16 - 1;
    final int cz = z >= 0 ? z / 16 : (z + 1) / 16 - 1;
    return new ChunkPosition(cx * 16, cz * 16);
  }

}
