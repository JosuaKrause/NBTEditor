package nbt.map.pos;

import nbt.map.ChunkPainter;

/**
 * References a position on the screen.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class ScreenPosition extends Pair {

  /**
   * Creates a screen position.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   */
  public ScreenPosition(final int x, final int z) {
    super(x, z);
  }

  /**
   * Gets the world position represented by this screen position.
   * 
   * @param painter The painter.
   * @param offX The x offset.
   * @param offZ The z offset.
   * @return The world position.
   */
  public WorldPosition getWorldPosition(final ChunkPainter painter,
      final int offX, final int offZ) {
    return new WorldPosition((int) painter.unscale(offX + x),
        (int) painter.unscale(offZ + z), offX + x < 0, offZ + z < 0);
  }

}
