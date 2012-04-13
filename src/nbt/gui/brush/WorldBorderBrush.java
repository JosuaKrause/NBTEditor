package nbt.gui.brush;

import nbt.gui.MapViewer;
import nbt.map.Biomes;
import nbt.map.Blocks;
import nbt.map.Chunk;
import nbt.read.MapReader.Pair;

public class WorldBorderBrush extends Brush {

    public WorldBorderBrush(final MapViewer viewer, final int radius) {
        super(viewer, radius, false);
    }

    @Override
    public String name() {
        return "World Border Brush";
    }

    @Override
    protected void edit(final Chunk c, final Pair posInChunk) {
        final int x = posInChunk.x;
        final int z = posInChunk.z;
        for (int y = 0; y <= Chunk.WORLD_HEIGHT; ++y) {
            if (!c.canSetBlock(y)) {
                continue;
            }
            if (y <= 65) {
                c.setBlock(x, y, z, Blocks.WATER_STAT);
            } else {
                c.setBlock(x, y, z, Blocks.AIR);
            }
        }
        c.setBiome(x, z, Biomes.OCEAN);
    }

}
