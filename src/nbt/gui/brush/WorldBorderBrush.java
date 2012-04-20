package nbt.gui.brush;

import nbt.gui.MapViewer;
import nbt.map.Biomes;
import nbt.map.Blocks;
import nbt.map.Chunk;
import nbt.map.pos.InChunkPosition;
import nbt.map.pos.Position3D;

/**
 * Replaces all blocks with air or water sources depending on height. This
 * creates a world border.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class WorldBorderBrush extends Brush {

  /**
   * Creates a world border brush.
   * 
   * @param viewer The viewer.
   * @param radius The initial radius.
   */
  public WorldBorderBrush(final MapViewer viewer, final int radius) {
    super(viewer, radius, false);
  }

  @Override
  public String name() {
    return "World Border Brush";
  }

  @Override
  protected void edit(final Chunk c, final InChunkPosition posInChunk) {
    for(int y = 0; y <= Chunk.WORLD_HEIGHT; ++y) {
      if(!c.canSetBlock(y)) {
        continue;
      }
      final Position3D pos = new Position3D(posInChunk, y);
      if(y <= 65) {
        c.setBlock(pos, Blocks.WATER_STAT);
      } else {
        c.setBlock(pos, Blocks.AIR);
      }
    }
    c.setBiome(posInChunk, Biomes.OCEAN);
  }

}
