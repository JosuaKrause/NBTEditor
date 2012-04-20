package nbt.gui.brush;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import nbt.gui.MapViewer;
import nbt.map.Biomes;
import nbt.map.Chunk;
import nbt.map.pos.InChunkPosition;

/**
 * The biome brush sets the biome of an area of the map.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class BiomeBrush extends Brush {

  private final Biomes biome;

  /**
   * Creates a new biome brush.
   * 
   * @param viewer The associated viewer.
   * @param radius The initial radius.
   * @param biome The biome that the brush paints.
   */
  public BiomeBrush(final MapViewer viewer, final int radius,
      final Biomes biome) {
    super(viewer, radius, true);
    if(biome == null) throw new NullPointerException("biome");
    this.biome = biome;
  }

  @Override
  public String name() {
    return "Biome Setter: " + biome + " (" + radius() + ")";
  }

  @Override
  protected void edit(final Chunk c, final InChunkPosition p) {
    c.setBiome(p.x, p.z, biome);
  }

  /**
   * Opens a choose biome dialog to create the brush.
   * 
   * @param frame The parent window.
   * @param viewer The map viewer.
   * @param radius The initial radius.
   * @return The brush.
   */
  public static BiomeBrush getBrushGUI(final JFrame frame,
      final MapViewer viewer, final int radius) {
    final Biomes b = (Biomes) JOptionPane.showInputDialog(frame,
        "Choose the Biome", "Biome Brush", JOptionPane.PLAIN_MESSAGE,
        null, Biomes.values(), null);
    if(b == null) return null;
    return new BiomeBrush(viewer, radius, b);
  }

}
