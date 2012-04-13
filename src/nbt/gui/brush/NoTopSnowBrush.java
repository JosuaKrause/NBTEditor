package nbt.gui.brush;

import nbt.gui.MapViewer;
import nbt.map.Blocks;
import nbt.map.Chunk;
import nbt.map.Chunk.Position;
import nbt.read.MapReader.Pair;

public class NoTopSnowBrush extends Brush {

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
    protected void edit(final Chunk c, final Pair p) {
        Position pos = c.getTopNonAirBlock(p.x, p.z);
        while (pos.y >= 0) {
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
            if (onlyOnTop) {
                break;
            }
            pos = new Position(pos.x, pos.y - 1, pos.z);
        }
    }

}
