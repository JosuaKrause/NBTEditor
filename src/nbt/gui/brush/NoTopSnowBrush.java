package nbt.gui.brush;

import nbt.gui.MapViewer;
import nbt.map.Blocks;
import nbt.map.Chunk;
import nbt.map.Chunk.Position;
import nbt.read.MapReader.Pair;

/**
 * This brush removes all snow and ice blocks that are at the top of a column.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class NoTopSnowBrush extends Brush {

  /**
   * Creates a no top snow brush.
   * 
   * @param viewer The viewer.
   * @param radius The initial radius.
   */
  public NoTopSnowBrush(final MapViewer viewer, final int radius) {
    super(viewer, radius, true);
  }

  @Override
  public String name() {
    return "No Snow (" + radius() + ")";
  }

  @Override
  protected void edit(final Chunk c, final Pair p) {
    final Position pos = c.getTopNonAirBlock(p.x, p.z);
    final Blocks b = c.getBlock(pos);
    switch(b) {
      case SNOW:
        c.setBlock(pos, Blocks.AIR);
        break;
      case ICE:
        c.setBlock(pos, Blocks.WATER_STAT);
        break;
      default:
        break;
    }
  }

}
