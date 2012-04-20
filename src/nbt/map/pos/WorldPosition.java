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

}
