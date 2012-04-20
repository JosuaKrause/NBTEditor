package nbt.gui.brush;

import nbt.gui.MapViewer;
import nbt.map.Blocks;
import nbt.map.Chunk;
import nbt.map.pos.InChunkPosition;
import nbt.map.pos.Position3D;

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
   * @param onlyOnTop Whether only top blocks should be removed.
   */
  public NoTopSnowBrush(final MapViewer viewer, final int radius,
      final boolean onlyOnTop) {
    super(viewer, radius, true);
    this.onlyOnTop = onlyOnTop;
  }

  @Override
  public String name() {
    return "No Snow (" + radius() + ")";
  }

  private final boolean onlyOnTop;

  @Override
  protected void edit(final Chunk c, final InChunkPosition p) {
    Position3D pos = c.getTopNonAirBlock(p);
    while(pos.y >= 0) {
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
      if(onlyOnTop) {
        break;
      }
      pos = new Position3D(pos.x, pos.y - 1, pos.z);
    }
  }

}
