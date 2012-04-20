package nbt.map.pos;

/**
 * A immutable pair of two integers. Normally holding map coordinates.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
class Pair {
  /** The x coordinate. */
  public final int x;
  /** The z coordinate. */
  public final int z;

  /**
   * Creates a pair.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   */
  public Pair(final int x, final int z) {
    this.x = x;
    this.z = z;
  }

  @Override
  public String toString() {
    return "x: " + x + " z: " + z;
  }

  @Override
  public int hashCode() {
    return x * 31 + z;
  }

  @Override
  public boolean equals(final Object obj) {
    final Pair p = (Pair) obj;
    return p.x == x && p.z == z;
  }
}
