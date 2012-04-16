package nbt.gui.brush;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import nbt.gui.MapViewer;
import nbt.map.Biomes;
import nbt.map.Blocks;
import nbt.map.Chunk;
import nbt.map.Pair;

/**
 * Converts a part of a map into a dessert. That is every stone/dirt block over
 * a given height will be replaced by sand and the biome will be changed.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class DesertBrush extends Brush {

  private final boolean sandstone;

  /**
   * Creates a desert brush.
   * 
   * @param viewer The viewer.
   * @param radius The initial radius.
   * @param sandstone Whether sand or sandstone is created.
   */
  public DesertBrush(final MapViewer viewer, final int radius,
      final boolean sandstone) {
    super(viewer, radius, true);
    this.sandstone = sandstone;
  }

  @Override
  public String name() {
    return "Desert Brush";
  }

  /**
   * The minimum height for sand.
   */
  public static final int START = 64;

  @Override
  protected void edit(final Chunk c, final Pair posInChunk) {
    final int x = posInChunk.x;
    final int z = posInChunk.z;
    for(int y = START; y <= Chunk.WORLD_HEIGHT; ++y) {
      if(!c.hasBlockFor(y)) {
        continue;
      }
      final Blocks b = c.getBlock(x, y, z);
      switch(b) {
        case WATER_STAT:
        case WATER:
        case GRASS:
        case STONE:
        case DIRT:
        case GRAVEL:
        case SAND:
        case SANDSTONE:
          c.setBlock(x, y, z, sandstone ? Blocks.SANDSTONE : Blocks.SAND);
          break;
        case DANDELION:
        case ROSE:
          c.setBlock(x, y, z, Blocks.AIR);
          break;
        default:
          break;
      }
    }
    c.setBiome(x, z, Biomes.DESERT);
  }

  /**
   * Shows a GUI to select a propriate desert brush.
   * 
   * @param frame The frame.
   * @param viewer The viewer.
   * @param radius The initial radius.
   * @return The brush.
   */
  public static DesertBrush brushGUI(final JFrame frame,
      final MapViewer viewer, final int radius) {
    final Object o = JOptionPane.showInputDialog(frame,
        "Sandstone or sand?", "Choose", JOptionPane.PLAIN_MESSAGE,
        null, new String[] { "Sandstone", "Sand"}, null);
    if(o == null) return null;
    return new DesertBrush(viewer, radius, o.equals("Sandstone"));
  }

}
