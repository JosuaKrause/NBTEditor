package nbt.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import nbt.map.Chunk;
import nbt.map.ChunkEdit;
import nbt.map.ChunkManager;
import nbt.map.ChunkPainter;
import nbt.map.Pair;
import nbt.map.UpdateReceiver;

/**
 * The map viewer component. It shows a map and is interactable.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class MapViewer extends JComponent implements UpdateReceiver {

  private static final long serialVersionUID = 553314683721005657L;

  private final MapFrame frame;

  private final ChunkPainter painter;

  private final ChunkManager manager;

  private int offX;

  private int offZ;

  /**
   * Creates a map viewer.
   * 
   * @param frame The parent frame.
   * @param scale The scale of the map.
   */
  public MapViewer(final MapFrame frame, final double scale) {
    this.frame = frame;
    manager = new ChunkManager(this);
    painter = new ChunkPainter(this, scale);
    final MouseAdapter mouse = new MouseAdapter() {

      private final List<Pair> clickList = new LinkedList<Pair>();

      private boolean clicking;

      private boolean drag;

      private int tmpX;

      private int tmpY;

      private int tmpOffX;

      private int tmpOffZ;

      @Override
      public void mousePressed(final MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3 && !drag) {
          clickList.clear();
          clicking = true;
        } else if(e.getButton() == MouseEvent.BUTTON1 && !clicking) {
          tmpX = e.getX();
          tmpY = e.getY();
          tmpOffX = getXOffset();
          tmpOffZ = getZOffset();
          drag = true;
        }
      }

      @Override
      public void mouseDragged(final MouseEvent e) {
        if(drag) {
          final int x = e.getX();
          final int z = e.getY();
          setOffset(x, z);
          setToolTipText(null);
        }
        if(clicking) {
          final int x = e.getX();
          final int z = e.getY();
          selectAtScreen(x, z);
          clickList.add(new Pair(x, z));
          addSelectionShape(getClickReceiverShape(false), x, z);
        }
      }

      @Override
      public void mouseMoved(final MouseEvent e) {
        final int x = e.getX();
        final int z = e.getY();
        selectAtScreen(x, z);
      }

      private void setOffset(final int x, final int y) {
        MapViewer.this.setOffset(tmpOffX + (tmpX - x), tmpOffZ + (tmpY - y));
      }

      @Override
      public void mouseReleased(final MouseEvent e) {
        if(drag) {
          final int x = e.getX();
          final int z = e.getY();
          setOffset(x, z);
          drag = false;
        }
        if(clicking) {
          if(clickList.isEmpty()) {
            clickList.add(new Pair(e.getX(), e.getY()));
          }
          multiEdit(clickList);
          clickList.clear();
          clicking = false;
          clearSelectionShape();
        }
      }

      @Override
      public void mouseExited(final MouseEvent e) {
        setToolTipText(null);
      }

      @Override
      public void mouseWheelMoved(final MouseWheelEvent e) {
        changeRadius(e.getWheelRotation());
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
        toggleBiomes();
      }

    });
    setActionMap(am);
    setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inp);
    setFocusable(true);
    grabFocus();
    setBackground(Color.BLACK);
  }

  /**
   * Performs a multi edit.
   * 
   * @param clickList The list of clicks.
   */
  protected void multiEdit(final List<Pair> clickList) {
    final List<Pair> cl = new ArrayList<Pair>(clickList);
    final ChunkManager manager = this.manager;
    final Waiter w = new Waiter(1500, frame);
    w.start();
    final Thread t = new Thread() {
      @Override
      public void run() {
        manager.setMultiedit(true);
        final double len = cl.size();
        double num = 0;
        for(final Pair p : cl) {
          clickAt(p.x, p.z);
          ++num;
          w.progress(num / len);
        }
        manager.setMultiedit(false);
        w.finish();
        somethingChanged();
      }
    };
    t.start();
  }

  /**
   * Toggles whether to show biome data in the map view.
   */
  public void toggleBiomes() {
    painter.setShowBiomes(!painter.showsBiomes());
  }

  /**
   * Setter.
   * 
   * @param file The map folder.
   */
  public void setFolder(final File file) {
    manager.setFolder(file);
    final Dimension size = getSize();
    offX = -size.width / 2;
    offZ = -size.height / 2;
  }

  /**
   * Changes the radius of the current click receiver.
   * 
   * @param amount The change amount.
   */
  public void changeRadius(final int amount) {
    if(controls == null) return;
    int radius = controls.getRadius() + amount;
    if(radius < controls.getMinRadius()) {
      radius = controls.getMinRadius();
    }
    if(radius > controls.getMaxRadius()) {
      radius = controls.getMaxRadius();
    }
    controls.setRadius(radius);
  }

  /**
   * Getter.
   * 
   * @return The x offset of the map.
   */
  public int getXOffset() {
    return offX;
  }

  /**
   * Getter.
   * 
   * @return The z offset of the map.
   */
  public int getZOffset() {
    return offZ;
  }

  /**
   * Sets the offset of the map.
   * 
   * @param x The x offset.
   * @param z The z offset.
   */
  public void setOffset(final int x, final int z) {
    offX = x;
    offZ = z;
    repaint();
  }

  @Override
  public void somethingChanged() {
    repaint();
  }

  private Controls controls;

  /**
   * Setter.
   * 
   * @param controls Sets the controller for the brushes.
   */
  public void setControls(final Controls controls) {
    this.controls = controls;
  }

  /**
   * Getter.
   * 
   * @param x The x position.
   * @param z The z position.
   * @return The chunk at the given position.
   */
  protected Chunk getChunkAtScreen(final int x, final int z) {
    final int cx =
        (int) (painter.unscale(offX + x)) / 16 - (offX + x < 0 ? 1 : 0);
    final int cz =
        (int) (painter.unscale(offZ + z)) / 16 - (offZ + z < 0 ? 1 : 0);
    return manager.getChunk(cx, cz);
  }

  /**
   * Getter.
   * 
   * @param x The x position.
   * @param z The z position.
   * @return The position within the chunk.
   */
  public Pair getPosInChunkAtScreen(final int x, final int z) {
    final int cx = (int) (painter.unscale(offX + x)) % 16
        + (offX + x < 0 ? 15 : 0);
    final int cz = (int) (painter.unscale(offZ + z)) % 16
        + (offZ + z < 0 ? 15 : 0);
    return new Pair(cx, cz);
  }

  /**
   * Edits a column.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   * @param editor The editor.
   */
  public void editChunk(final int x, final int z, final ChunkEdit editor) {
    final Chunk c = getChunkAtScreen(x, z);
    if(c == null) return;
    final Pair p = getPosInChunkAtScreen(x, z);
    manager.editChunk(c, p, editor);
  }

  private final class Waiter extends Thread {

    private final int timeMillis;

    private final JFrame parent;

    private JDialog frame;

    private JProgressBar bar;

    private volatile boolean finished;

    public Waiter(final int timeMillis, final JFrame parent) {
      this.timeMillis = timeMillis;
      this.parent = parent;
      setDaemon(true);
    }

    public void finish() {
      finished = true;
      if(frame != null) {
        frame.setVisible(false);
        frame.dispose();
        synchronized(this) {
          bar = null;
          frame = null;
          interrupt();
        }
      }
    }

    public void progress(final double r) {
      final JProgressBar b = bar;
      if(b != null) {
        b.setIndeterminate(false);
        b.setValue((int) (Math.round(r * 1000.0)));
      }
    }

    @Override
    public void run() {
      try {
        synchronized(this) {
          wait(timeMillis);
          if(!finished) {
            frame = createFrame();
            frame.setVisible(true);
          }
        }
      } catch(final InterruptedException e) {
        interrupt();
      }
    }

    private JDialog createFrame() {
      final JDialog res = new JDialog(parent);
      res.setTitle("Processing brush action...");
      bar = new JProgressBar(0, 1000);
      bar.setIndeterminate(true);
      bar.setPreferredSize(new Dimension(200, 50));
      res.setLayout(new BorderLayout());
      res.add(bar, BorderLayout.CENTER);
      res.pack();
      res.setLocationRelativeTo(parent);
      res.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      res.setResizable(false);
      res.setModal(true);
      return res;
    }

  }

  /**
   * Signals that an edit has finished.
   */
  public void editFinished() {
    manager.editFinished();
  }

  private Chunk selChunk;

  private Pair selPos;

  /**
   * Selects a screen coordinate.
   * 
   * @param x The x position.
   * @param z The z position.
   */
  public void selectAtScreen(final int x, final int z) {
    selChunk = getChunkAtScreen(x, z);
    selPos = getPosInChunkAtScreen(x, z);
    if(selChunk != null) {
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

  @Override
  public void paint(final Graphics gfx) {
    super.paint(gfx);
    final Graphics2D g = (Graphics2D) gfx;
    final Rectangle r = getBounds();
    g.setColor(getBackground());
    g.fill(r);
    g.translate(-offX, -offZ);
    final Pair[] reloadEntries = manager.getReloadEntries();
    for(final Pair pos : reloadEntries) {
      if(painter.isValidPos(g, pos)) {
        manager.needsReload(pos);
      }
    }
    final Pair[] chunksEntries = manager.getChunkEntries();
    boolean hasMid = false;
    double midX = 0;
    double midZ = 0;
    for(final Pair pos : chunksEntries) {
      final Chunk c = manager.getChunk(pos);
      if(!painter.isValidPos(g, pos)) {
        manager.mayUnload(c);
        continue;
      }
      manager.stayLoaded(c);
      final double x = painter.scale(pos.x);
      final double z = painter.scale(pos.z);
      if(selChunk == c) {
        midX = x;
        midZ = z;
        hasMid = true;
      }
      final Graphics2D g2 = (Graphics2D) g.create();
      g2.translate(x, z);
      painter.drawChunk(g2, c);
      g2.dispose();
    }
    if(hasMid) {
      final Graphics2D g2 = (Graphics2D) g.create();
      g2.translate(midX, midZ);
      g2.setColor(new Color(0x80000000, true));
      g2.fill(getClickReceiverShape(true));
      g2.dispose();
    }
    if(selectionShape != null) {
      final Graphics2D g2 = (Graphics2D) g.create();
      g2.setColor(new Color(0x80ff0000, true));
      g2.fill(selectionShape);
      g2.dispose();
    }
  }

  private Area selectionShape;

  /**
   * Clears the selection shape and redraws.
   */
  public void clearSelectionShape() {
    selectionShape = null;
    somethingChanged();
  }

  /**
   * Adds another shape to the selection shape.
   * 
   * @param s The shape.
   * @param x The x position of the shape.
   * @param z The z position of the shape.
   */
  public void addSelectionShape(final Shape s, final int x, final int z) {
    final AffineTransform d =
        AffineTransform.getTranslateInstance(offX + x, offZ + z);
    final Area a = new Area(s);
    a.transform(d);
    if(selectionShape == null) {
      selectionShape = a;
    } else {
      selectionShape.add(a);
    }
    somethingChanged();
  }

  /**
   * Getter.
   * 
   * @param atSelection Whether the shape lies at the current selection.
   * @return The shape of the currently installed click receiver or a single
   *         pixel rectangle if no click receiver is installed.
   */
  public Shape getClickReceiverShape(final boolean atSelection) {
    if(clickReceiver == null) {
      final double len = painter.scale(1);
      return new Rectangle2D.Double(painter.scale(atSelection ? selPos.x : 0),
          painter.scale(atSelection ? selPos.z : 0), len, len);
    }
    final double rad = clickReceiver.radius();
    final double rad2 = painter.scale(rad * 2);
    final double x = painter.scale((atSelection ? selPos.x : 0) - rad);
    final double z = painter.scale((atSelection ? selPos.z : 0) - rad);
    return clickReceiver.isCircle()
        ? new Ellipse2D.Double(x, z, rad2, rad2)
        : new Rectangle2D.Double(x, z, rad2, rad2);
  }

  /**
   * Unloads a chunk.
   * 
   * @param chunk The chunk.
   */
  protected void unloadChunk(final Chunk chunk) {
    painter.unloadChunk(chunk);
    if(selChunk == chunk) {
      selChunk = null;
    }
    manager.unloadChunk(chunk);
  }

  private ClickReceiver clickReceiver;

  /**
   * Setter.
   * 
   * @param cr Sets the click receiver.
   */
  public void setClickReceiver(final ClickReceiver cr) {
    clickReceiver = cr;
    frame.setBrush(clickReceiver != null ? clickReceiver.name() : null);
    repaint();
    grabFocus();
  }

  /**
   * Getter.
   * 
   * @return The currently installed click receiver.
   */
  public ClickReceiver getClickReceiver() {
    return clickReceiver;
  }

  /**
   * Clicks at a screen position.
   * 
   * @param x The x position.
   * @param z The z position.
   */
  public void clickAt(final int x, final int z) {
    if(clickReceiver != null) {
      clickReceiver.clicked(x, z);
    }
  }

}
