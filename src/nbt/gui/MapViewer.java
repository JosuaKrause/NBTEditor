package nbt.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import nbt.map.Chunk;
import nbt.read.MapReader;
import nbt.read.MapReader.Pair;
import net.minecraft.world.level.chunk.storage.RegionFile;

public class MapViewer extends JComponent {

    private static final long serialVersionUID = 553314683721005657L;

    private final List<Chunk> chunks;

    private int offX;

    private int offZ;

    private final double scale;

    private final MapFrame frame;

    public MapViewer(final MapFrame frame, final double scale) {
        this.frame = frame;
        chunks = new ArrayList<Chunk>();
        this.scale = scale;
        final MouseAdapter mouse = new MouseAdapter() {

            private boolean drag;

            private int tmpX;

            private int tmpY;

            private int tmpOffX;

            private int tmpOffZ;

            @Override
            public void mousePressed(final MouseEvent e) {
                tmpX = e.getX();
                tmpY = e.getY();
                tmpOffX = offX;
                tmpOffZ = offZ;
                drag = true;
            }

            @Override
            public void mouseDragged(final MouseEvent e) {
                if (drag) {
                    final int x = e.getX();
                    final int z = e.getY();
                    setOffset(x, z);
                    setToolTipText(null);
                }
            }

            @Override
            public void mouseMoved(final MouseEvent e) {
                final int x = e.getX();
                final int z = e.getY();
                selectAtScreen(x, z);
            }

            private void setOffset(final int x, final int y) {
                offX = tmpOffX + (tmpX - x);
                offZ = tmpOffZ + (tmpY - y);
                repaint();
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                if (drag) {
                    final int x = e.getX();
                    final int z = e.getY();
                    setOffset(x, z);
                    drag = false;
                }
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                setToolTipText(null);
            }

        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        setBackground(Color.BLACK);
    }

    public Chunk getChunkAtScreen(final int x, final int z) {
        final int cx = (int) ((offX + x) / scale) / 16 - (offX + x < 0 ? 1 : 0);
        final int cz = (int) ((offZ + z) / scale) / 16 - (offZ + z < 0 ? 1 : 0);
        for (final Chunk c : chunks) {
            if (c.getX() / 16 == cx && c.getZ() / 16 == cz) {
                return c;
            }
        }
        return null;
    }

    public Pair getPosInChunkAtScreen(final int x, final int z) {
        final int cx = (int) ((offX + x) / scale) % 16
                + (offX + x < 0 ? 15 : 0);
        final int cz = (int) ((offZ + z) / scale) % 16
                + (offZ + z < 0 ? 15 : 0);
        return new Pair(cx, cz);
    }

    private Chunk selChunk;

    private Pair selPos;

    public void selectAtScreen(final int x, final int z) {
        selChunk = getChunkAtScreen(x, z);
        selPos = getPosInChunkAtScreen(x, z);
        if (selChunk != null) {
            final String tt = "x:" + (selPos.x + selChunk.getX()) + " z:"
                    + (selPos.z + selChunk.getZ()) + " b:"
                    + selChunk.getBiome(selPos.x, selPos.z).name;
            frame.setTitleText(tt);
            setToolTipText(tt);
        } else {
            frame.setTitleText(null);
            setToolTipText(null);
        }
        repaint();
    }

    public void setFolder(final File folder) {
        chunks.clear();
        for (final File f : folder.listFiles(new FileFilter() {

            @Override
            public boolean accept(final File f) {
                return f.isFile()
                        && f.getName().endsWith(RegionFile.ANVIL_EXTENSION);
            }

        })) {
            final MapReader r = new MapReader(f);
            for (final Pair p : r.getChunks()) {
                chunks.add(new Chunk(r.read(p.x, p.z), f));
            }
        }
        repaint();
    }

    @Override
    public void paint(final Graphics gfx) {
        super.paint(gfx);
        final Graphics2D g = (Graphics2D) gfx;
        g.translate(-offX, -offZ);
        for (final Chunk c : chunks) {
            final double x = c.getX() * scale;
            final double z = c.getZ() * scale;
            final Rectangle2D rect = new Rectangle2D.Double(x, z, 16 * scale,
                    16 * scale);
            if (!g.hitClip((int) rect.getMinX() - 1, (int) rect.getMinY() - 1,
                    (int) rect.getWidth() + 2, (int) rect.getHeight() + 2)) {
                continue;
            }
            final Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, z);
            drawChunk(g2, c);
            g2.dispose();
        }
    }

    private void drawChunk(final Graphics2D g, final Chunk chunk) {
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                final Rectangle2D rect = new Rectangle2D.Double(x * scale, z
                        * scale, scale, scale);
                if (selChunk == chunk && selPos.x == x && selPos.z == z) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(chunk.getColorForColumn(x, z));
                }
                g.fill(rect);
            }
        }
    }

}
