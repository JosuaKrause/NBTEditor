package nbt.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;

import javax.swing.JComponent;

import nbt.map.Chunk;
import nbt.map.ChunkEdit;
import nbt.map.ChunkManager;
import nbt.map.ChunkPainter;
import nbt.map.UpdateReceiver;
import nbt.read.MapReader.Pair;

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

      private boolean drag;

      private int tmpX;

      private int tmpY;

      private int tmpOffX;

      private int tmpOffZ;

      @Override
      public void mousePressed(final MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3) {
          clickAt(e.getX(), e.getY());
        } else if(e.getButton() == MouseEvent.BUTTON1) {
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
    setBackground(Color.BLACK);
  }

  /**
   * Setter.
   * 
   * @param file The map folder.
   */
  public void setFolder(final File file) {
    manager.setFolder(file);
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
      painter.drawChunk(g2, c, this);
      g2.dispose();
    }
    if(hasMid) {
      final Graphics2D g2 = (Graphics2D) g.create();
      g2.translate(midX, midZ);
      final Shape s;
      if(clickReceiver != null) {
        final double rad = clickReceiver.radius();
        final double rad2 = painter.scale(rad * 2);
        s =
            new Ellipse2D.Double(painter.scale(selPos.x - rad),
                painter.scale(selPos.z - rad), rad2, rad2);
      } else {
        final double len = painter.scale(1);
        s =
            new Rectangle2D.Double(painter.scale(selPos.x),
                painter.scale(selPos.z), len, len);
      }
      g2.setColor(new Color(0x80000000, true));
      g2.fill(s);
      g2.dispose();
    }
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
