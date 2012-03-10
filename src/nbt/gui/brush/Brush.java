package nbt.gui.brush;

import nbt.gui.MapViewer;
import nbt.gui.MapViewer.ChunkEdit;
import nbt.gui.MapViewer.ClickReceiver;
import nbt.map.Chunk;
import nbt.read.MapReader.Pair;

public abstract class Brush implements ClickReceiver {

    private final MapViewer viewer;

    private final ChunkEdit edit;

    private final int radius;

    private final int r2;

    public Brush(final MapViewer viewer, final int radius) {
        this.viewer = viewer;
        this.radius = radius;
        r2 = radius * radius;
        edit = new ChunkEdit() {

            @Override
            public void edit(final Chunk c, final Pair posInChunk) {
                Brush.this.edit(c, posInChunk);
            }

        };
    }

    public int radius() {
        return radius;
    }

    @Override
    public void clicked(final int x, final int z) {
        for (int i = -radius; i <= radius; ++i) {
            for (int j = -radius; j <= radius; ++j) {
                if (i * i + j * j > r2) {
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
