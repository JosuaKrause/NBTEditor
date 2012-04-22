package nbt.map.pos;

import nbt.world.World;
import nbt.world.World.WorldDimension;

/**
 * A position as represented in-game. I.e. the coordinates are double values.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class GamePosition {

  /**
   * The dimension this position is in.
   */
  public final WorldDimension dim;

  /**
   * The x coordinate.
   */
  public final double x;

  /**
   * The y coordinate.
   */
  public final double y;

  /**
   * The z coordinate.
   */
  public final double z;

  /**
   * Creates a new game position.
   * 
   * @param x The x coordinate.
   * @param y The y coordinate.
   * @param z The z coordinate.
   * @param dim The dimension of the position.
   */
  public GamePosition(final double x, final double y, final double z,
      final WorldDimension dim) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.dim = dim;
  }

  /**
   * Creates a new game position.
   * 
   * @param x The x coordinate.
   * @param y The y coordinate.
   * @param z The z coordinate.
   * @param dim The dimension of the position.
   */
  public GamePosition(final Double x, final Double y, final Double z,
      final WorldDimension dim) {
    this((double) x, (double) y, (double) z, dim);
  }

  /**
   * Creates a new game position on top of the world position. If the
   * corresponding chunk is not yet generated the y coordinate defaults to 64.
   * 
   * @param wp The world position.
   * @param w The world.
   * @param dim The dimension the position is in.
   */
  public GamePosition(final WorldPosition wp, final World w,
      final WorldDimension dim) {
    this(wp.x, getTopMostPositionAt(wp, w, dim), wp.z, dim);
  }

  /**
   * Converts this position to a player position on top of this block.
   * 
   * @return The position on top of this block.
   */
  public GamePosition playerOnTop() {
    return new GamePosition(x + .5, y + 1.7, z + .5, dim);
  }

  /**
   * Converts this position to a spawn position on top of this block.
   * 
   * @return The position on top of this block.
   */
  public GamePosition spawnOnTop() {
    if(dim != WorldDimension.OVERWORLD) throw new IllegalArgumentException(
        "spawns may only be in the overworld - got: " + dim);
    return new GamePosition(x, y + 1, z, dim);
  }

  /**
   * Calculates the altitude on top of the world position. If the corresponding
   * chunk is not yet generated the altitude coordinate defaults to 64.
   * 
   * @param wp The world position.
   * @param w The world.
   * @param dim The dimension the position should be in.
   * @return The altitude (y coordinate).
   */
  public static double getTopMostPositionAt(final WorldPosition wp,
      final World w, final WorldDimension dim) {
    return w.chunkExists(wp, dim) ? w.getTopMostPosition(wp, dim) : 64;
  }

}
