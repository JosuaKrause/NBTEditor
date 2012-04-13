package nbt.gui.brush;

import nbt.gui.MapViewer;
import nbt.gui.MapViewer.ChunkEdit;
import nbt.gui.MapViewer.ClickReceiver;
import nbt.map.Chunk;
import nbt.read.MapReader.Pair;

public abstract class Brush implements ClickReceiver {

    private final MapViewer viewer;

    private final ChunkEdit edit;

    private final boolean circle;

    private int radius;

    private int r2;

    protected Brush(final MapViewer viewer, final int radius,
            final boolean circle) {
        this.circle = circle;
        this.viewer = viewer;
        setRadius(radius);
        edit = new ChunkEdit() {

            @Override
            public void edit(final Chunk c, final Pair posInChunk) {
                Brush.this.edit(c, posInChunk);
            }

        };
    }

    @Override
    public void setRadius(final int newRadius) {
        radius = newRadius;
        r2 = radius * radius;
    }

    @Override
    public boolean isCircle() {
        return circle;
    }

    @Override
    public int radius() {
        return radius;
    }

    @Override
    public void clicked(final int x, final int z) {
        for (int i = -radius * 2; i <= radius * 2; ++i) {
            for (int j = -radius * 2; j <= radius * 2; ++j) {
                if (circle && (i * i + j * j > 4 * r2)) {
                    continue;
                }
                final int posX = x + i;
                final int posZ = z + j;
                viewer.editChunk(posX, posZ, edit);
            }
        }
        viewer.editFinished();
    }

    protected abstract void edit(Chunk c, Pair posInChunk);

}
