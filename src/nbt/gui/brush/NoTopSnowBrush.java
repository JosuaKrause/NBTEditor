package nbt.gui.brush;

import nbt.gui.MapViewer;
import nbt.map.Blocks;
import nbt.map.Chunk;
import nbt.map.Chunk.Position;
import nbt.read.MapReader.Pair;

public class NoTopSnowBrush extends Brush {

    public NoTopSnowBrush(final MapViewer viewer, final int radius) {
        super(viewer, radius);
    }

    @Override
    public String name() {
        return "No Snow (" + radius() + ")";
    }

    @Override
    protected void edit(final Chunk c, final Pair p) {
        final Position pos = c.getTopNonAirBlock(p.x, p.z);
        final Blocks b = c.getBlock(pos);
        switch (b) {
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
