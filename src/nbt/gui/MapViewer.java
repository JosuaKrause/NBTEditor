package nbt.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import nbt.map.Chunk;
import nbt.read.MapReader;
import nbt.read.MapReader.Pair;
import net.minecraft.world.level.chunk.storage.RegionFile;

public class MapViewer extends JComponent {

    private static final long serialVersionUID = 553314683721005657L;

    private final Map<Pair, Chunk> chunks;

    private final Map<Pair, File> reload;

    private final Map<Pair, Pair> otherPos;

    private final Set<Chunk> mayUnload;

    private int offX;

    private int offZ;

    private final double scale;

    private final MapFrame frame;

    public MapViewer(final MapFrame frame, final double scale) {
        this.frame = frame;
        chunks = new HashMap<Pair, Chunk>();
        reload = new HashMap<Pair, File>();
        otherPos = new HashMap<Pair, Pair>();
        mayUnload = new HashSet<Chunk>();
        this.scale = scale;
        final MouseAdapter mouse = new MouseAdapter() {

            private boolean drag;

            private int tmpX;

            private int tmpY;

            private int tmpOffX;

            private int tmpOffZ;

            @Override
            public void mousePressed(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (clickReceiver != null) {
                        clickReceiver.clicked(e.getX(), e.getY());
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    tmpX = e.getX();
                    tmpY = e.getY();
                    tmpOffX = offX;
                    tmpOffZ = offZ;
                    drag = true;
                }
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

            @Override
            public void mouseWheelMoved(final MouseWheelEvent e) {
                if (controls == null) {
                    return;
                }
                final int rot = e.getWheelRotation();
                int radius = controls.getRadius() + rot;
                if (radius < controls.getMinRadius()) {
                    radius = controls.getMinRadius();
                }
                if (radius > controls.getMaxRadius()) {
                    radius = controls.getMaxRadius();
                }
                controls.setRadius(radius);
            }

        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        addMouseWheelListener(mouse);
        final InputMap inp = new InputMap();
        inp.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, 0), "BIOME");
        final ActionMap am = new ActionMap();
        am.put("BIOME", new AbstractAction() {

            private static final long serialVersionUID = -5517714907864610590L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                setShowBiomes(!showsBiomes());
            }

        });
        setActionMap(am);
        setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inp);
        setFocusable(true);
        grabFocus();
        setBackground(Color.BLACK);
        final BufferedImage loading = new BufferedImage((int) (scale * 16),
                (int) (scale * 16), BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = (Graphics2D) loading.getGraphics();
        g.setColor(new Color(0x404040));
        final Rectangle r = new Rectangle(loading.getWidth(),
                loading.getHeight());
        g.fill(r);
        g.dispose();
        this.loading = loading;
    }

    private boolean showBiomes;

    public void setShowBiomes(final boolean showBiomes) {
        this.showBiomes = showBiomes;
        repaint();
    }

    public boolean showsBiomes() {
        return showBiomes;
    }

    private Controls controls;

    public void setControls(final Controls controls) {
        this.controls = controls;
    }

    protected Chunk getChunkAtScreen(final int x, final int z) {
        final int cx = (int) ((offX + x) / scale) / 16 - (offX + x < 0 ? 1 : 0);
        final int cz = (int) ((offZ + z) / scale) / 16 - (offZ + z < 0 ? 1 : 0);
        Chunk c;
        synchronized (chunks) {
            c = chunks.get(new Pair(cx * 16, cz * 16));
        }
        return c;
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
                    + (selPos.z + selChunk.getZ()) + " b: "
                    + selChunk.getBiome(selPos.x, selPos.z).name;
            frame.setTitleText(tt);
            setToolTipText(tt);
        } else {
            frame.setTitleText(null);
            setToolTipText(null);
        }
        repaint();
    }

    private Thread iniLoader;

    public void setFolder(final File folder) {
        final Dimension dim = getSize();
        offX = (int) (-dim.width * 0.25 * scale);
        offZ = (int) (-dim.height * 0.25 * scale);
        selChunk = null;
        selPos = null;
        showBiomes = false;
        grabFocus();
        if (iniLoader != null) {
            synchronized (iniLoader) {
                iniLoader.interrupt();
                iniLoader = null;
            }
        }
        iniLoader = new Thread() {

            @Override
            public void run() {
                setFolder0(folder, this);
            }

        };
        iniLoader.setDaemon(true);
        iniLoader.start();
    }

    private void setFolder0(final File folder, final Thread t) {
        synchronized (chunks) {
            chunks.clear();
        }
        MapReader.clearCache();
        final File[] files = folder.listFiles(new FileFilter() {

            @Override
            public boolean accept(final File f) {
                return f.isFile()
                        && f.getName().endsWith(RegionFile.ANVIL_EXTENSION);
            }

        });
        Arrays.sort(files, new Comparator<File>() {

            @Override
            public int compare(final File left, final File right) {
                final String leftStr = left.getName().replace("-", "");
                final String rightStr = right.getName().replace("-", "");
                return leftStr.compareTo(rightStr);
            }

        });
        for (final File f : files) {
            final MapReader r = MapReader.getForFile(f);
            final List<Pair> chunkList = r.getChunks();
            for (final Pair p : chunkList) {
                if (t != iniLoader || t.isInterrupted()) {
                    return;
                }
                final Chunk chunk = new Chunk(r.read(p.x, p.z), f, p);
                unloadChunk(chunk);
            }
            repaint();
        }
    }

    private static Pair[] asArrayPair(final Collection<Pair> entries) {
        return entries.toArray(new Pair[entries.size()]);
    }

    @Override
    public void paint(final Graphics gfx) {
        super.paint(gfx);
        final Graphics2D g = (Graphics2D) gfx;
        final Rectangle r = getBounds();
        g.setColor(getBackground());
        g.fill(r);
        g.translate(-offX, -offZ);
        final Pair[] reloadEntries;
        synchronized (reload) {
            reloadEntries = asArrayPair(reload.keySet());
        }
        for (final Pair pos : reloadEntries) {
            if (isValidPos(g, pos)) {
                synchronized (chunksToReload) {
                    chunksToReload.add(pos);
                }
                synchronized (reloader) {
                    reloader.notify();
                }
            }
        }
        final Pair[] chunksEntries;
        synchronized (chunks) {
            chunksEntries = asArrayPair(chunks.keySet());
        }
        boolean hasMid = false;
        double midX = 0;
        double midZ = 0;
        for (final Pair pos : chunksEntries) {
            final Chunk c;
            synchronized (chunks) {
                c = chunks.get(pos);
            }
            if (!isValidPos(g, pos)) {
                if (c != null) {
                    synchronized (mayUnload) {
                        mayUnload.add(c);
                    }
                }
                continue;
            }
            if (c != null) {
                synchronized (mayUnload) {
                    mayUnload.remove(c);
                }
            }
            final double x = pos.x * scale;
            final double z = pos.z * scale;
            if (selChunk == c) {
                midX = x;
                midZ = z;
                hasMid = true;
            }
            final Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, z);
            drawChunk(g2, c);
            g2.dispose();
        }
        if (hasMid) {
            final Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(midX, midZ);
            final Shape s;
            if (clickReceiver != null) {
                final double rad = clickReceiver.radius();
                s = new Ellipse2D.Double((selPos.x - rad) * scale,
                        (selPos.z - rad) * scale, scale * rad * 2, scale * rad
                                * 2);
            } else {
                s = new Rectangle2D.Double(selPos.x * scale, selPos.z * scale,
                        scale, scale);
            }
            g2.setColor(new Color(0x80000000, true));
            g2.fill(s);
            g2.dispose();
        }
    }

    private boolean isValidPos(final Graphics2D g, final Pair pos) {
        final double x = pos.x * scale;
        final double z = pos.z * scale;
        final Rectangle2D rect = new Rectangle2D.Double(x, z, 16 * scale,
                16 * scale);
        return g.hitClip((int) rect.getMinX() - 1, (int) rect.getMinY() - 1,
                (int) rect.getWidth() + 2, (int) rect.getHeight() + 2);
    }

    private void handleFullMemory() {
        System.err.println("full memory cleanup");
        for (;;) {
            // allocating no more memory but avoiding concurrent modification
            // exception
            final Chunk c;
            synchronized (mayUnload) {
                final Iterator<Chunk> it = mayUnload.iterator();
                if (!it.hasNext()) {
                    break;
                }
                c = it.next();
                it.remove();
            }
            unloadChunk(c);
        }
        System.gc();
    }

    protected void unloadChunk(final Chunk chunk) {
        final Image img;
        synchronized (imgCache) {
            img = imgCache.remove(chunk);
        }
        if (img != null) {
            img.flush();
        }
        if (selChunk == chunk) {
            selChunk = null;
        }
        final Pair pos = chunk.getPos();
        synchronized (chunks) {
            chunks.remove(pos);
        }
        synchronized (reload) {
            reload.put(pos, chunk.getFile());
        }
        synchronized (otherPos) {
            otherPos.put(pos, chunk.getOtherPos());
        }
        synchronized (mayUnload) {
            mayUnload.remove(chunk);
        }
        // writes the chunk if changed
        chunk.unload();
    }

    public static final double MEM_RATIO = 0.2;

    private static final String TOKEN = "nope";

    private static volatile boolean beFriendly;

    private static void checkHeapStatus() {
        if (beFriendly) {
            return;
        }
        final Runtime r = Runtime.getRuntime();
        final long free = r.freeMemory();
        final long max = r.maxMemory();
        final double ratio = (double) free / (double) max;
        if (ratio <= MEM_RATIO) {
            throw new OutOfMemoryError(TOKEN);
        }
    }

    protected void reloadChunk(final Pair pos) {
        boolean end = false;
        do {
            try {
                checkHeapStatus();
                final File f;
                synchronized (reload) {
                    f = reload.get(pos);
                }
                if (f == null) {
                    return;
                }
                final Pair op;
                synchronized (otherPos) {
                    op = otherPos.get(pos);
                }
                final MapReader r = MapReader.getForFile(f);
                final Chunk chunk = new Chunk(r.read(op.x, op.z), f, op);
                synchronized (chunks) {
                    chunks.put(pos, chunk);
                }
                synchronized (reload) {
                    reload.remove(pos);
                }
                synchronized (otherPos) {
                    otherPos.remove(pos);
                }
                end = true;
            } catch (final OutOfMemoryError e) {
                boolean canUnload;
                synchronized (mayUnload) {
                    canUnload = !mayUnload.isEmpty();
                }
                if (canUnload) {
                    beFriendly = false;
                    handleFullMemory();
                } else if (e.getMessage().equals(TOKEN)) {
                    beFriendly = true;
                } else {
                    throw new Error(e);
                }
            }
        } while (!end);
    }

    private final Image loading;

    private final Set<Chunk> chunksToDraw = new HashSet<Chunk>();

    private final Set<Pair> chunksToReload = new HashSet<Pair>();

    private final Map<Chunk, Image> imgCache = new HashMap<Chunk, Image>();

    private void drawChunk(final Graphics2D g, final Chunk chunk) {
        if (chunk == null) {
            g.drawImage(loading, 0, 0, this);
            return;
        }
        boolean contains;
        synchronized (imgCache) {
            contains = imgCache.containsKey(chunk);
        }
        if (chunk.oneTimeHasChanged() || !contains) {
            synchronized (imgCache) {
                imgCache.put(chunk, loading);
            }
            synchronized (chunksToDraw) {
                chunksToDraw.add(chunk);
            }
            synchronized (drawer) {
                drawer.notify();
            }
        }
        Image img;
        synchronized (imgCache) {
            img = imgCache.get(chunk);
        }
        g.drawImage(img, 0, 0, this);
        if (showBiomes) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    final Rectangle2D rect = new Rectangle2D.Double(x * scale,
                            z * scale, scale, scale);
                    g.setColor(chunk.getBiome(x, z).color);
                    g.fill(rect);
                }
            }
        }
    }

    private final Thread reloader = new Thread() {

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    Pair p;
                    for (;;) {
                        boolean b;
                        synchronized (chunksToReload) {
                            b = chunksToReload.isEmpty();
                        }
                        if (!b) {
                            break;
                        }
                        synchronized (reloader) {
                            wait();
                        }
                    }
                    synchronized (chunksToReload) {
                        final Iterator<Pair> it = chunksToReload.iterator();
                        p = it.next();
                        it.remove();
                    }
                    reloadChunk(p);
                    repaint();
                }
            } catch (final InterruptedException e) {
                interrupt();
            }
        }

    };

    {
        reloader.setDaemon(true);
        reloader.start();
    }

    private final Thread drawer = new Thread() {

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    Chunk c;
                    for (;;) {
                        boolean b;
                        synchronized (chunksToDraw) {
                            b = chunksToDraw.isEmpty();
                        }
                        if (!b) {
                            break;
                        }
                        synchronized (drawer) {
                            wait();
                        }
                    }
                    synchronized (chunksToDraw) {
                        final Iterator<Chunk> it = chunksToDraw.iterator();
                        c = it.next();
                        it.remove();
                    }
                    drawChunk0(c);
                    repaint();
                }
            } catch (final InterruptedException e) {
                interrupt();
            }
        }

    };

    {
        drawer.setDaemon(true);
        drawer.start();
    }

    private void drawChunk0(final Chunk chunk) {
        final BufferedImage img = new BufferedImage((int) (scale * 16),
                (int) (scale * 16), BufferedImage.TYPE_INT_RGB);
        final Graphics2D gi = (Graphics2D) img.getGraphics();
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                final Rectangle2D rect = new Rectangle2D.Double(x * scale, z
                        * scale, scale, scale);
                gi.setColor(chunk.getColorForColumn(x, z));
                gi.fill(rect);
            }
        }
        gi.dispose();
        synchronized (imgCache) {
            if (imgCache.containsKey(chunk)) {
                imgCache.put(chunk, img);
            }
        }
    }

    public static interface ChunkEdit {

        void edit(Chunk c, Pair posInChunk);

    }

    private final Set<Chunk> editedChunks = new HashSet<Chunk>();

    public void editChunk(final int x, final int z, final ChunkEdit editor) {
        final Chunk c = getChunkAtScreen(x, z);
        if (c == null) {
            return;
        }
        final Pair p = getPosInChunkAtScreen(x, z);
        editor.edit(c, p);
        editedChunks.add(c);
    }

    public void editFinished() {
        for (final Chunk c : editedChunks) {
            unloadChunk(c);
        }
        editedChunks.clear();
    }

    public static interface ClickReceiver {

        void clicked(int x, int z);

        String name();

        void setRadius(int newRadius);

        int radius();

    }

    private ClickReceiver clickReceiver;

    public void setClickReceiver(final ClickReceiver cr) {
        clickReceiver = cr;
        frame.setBrush(clickReceiver != null ? clickReceiver.name() : null);
        repaint();
        grabFocus();
    }

    public ClickReceiver getClickReceiver() {
        return clickReceiver;
    }

}
