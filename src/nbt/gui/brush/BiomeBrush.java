package nbt.gui.brush;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import nbt.gui.MapViewer;
import nbt.map.Biomes;
import nbt.map.Chunk;
import nbt.read.MapReader.Pair;

public class BiomeBrush extends Brush {

    private final Biomes biome;

    public BiomeBrush(final MapViewer viewer, final int radius,
            final Biomes biome) {
        super(viewer, radius);
        if (biome == null) {
            throw new NullPointerException("biome");
        }
        this.biome = biome;
    }

    @Override
    public String name() {
        return "Biome Setter: " + biome + " (" + radius() + ")";
    }

    @Override
    protected void edit(final Chunk c, final Pair p) {
        c.setBiome(p.x, p.z, biome);
    }

    public static BiomeBrush getBrushGUI(final JFrame frame,
            final MapViewer viewer, final int radius) {
        final Biomes b = (Biomes) JOptionPane.showInputDialog(frame,
                "Choose the Biome", "Biome Brush", JOptionPane.PLAIN_MESSAGE,
                null, Biomes.values(), null);
        if (b == null) {
            return null;
        }
        return new BiomeBrush(viewer, radius, b);
    }

}
