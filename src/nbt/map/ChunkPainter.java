package nbt.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import nbt.map.pos.OwnChunkPosition;

/**
 * The chunk painter paints chunks on an image and on the screen.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class ChunkPainter {

  /**
   * Creates the image that is shown when a chunk is not loaded yet.
   * 
   * @param scale The scaling factor.
   * @return The image.
   */
  public static Image createLoadingImage(final double scale) {
    final BufferedImage loading = new BufferedImage((int) (scale * 16),
        (int) (scale * 16), BufferedImage.TYPE_INT_RGB);
    final Graphics2D g = (Graphics2D) loading.getGraphics();
    g.setColor(new Color(0x404040));
    final Rectangle r = new Rectangle(loading.getWidth(),
        loading.getHeight());
    g.fill(r);
    g.dispose();
    return loading;
  }

  private final double scale;

  private final Set<Chunk> chunksToDraw = new HashSet<Chunk>();

  private final Map<Chunk, Image> imgCache = new HashMap<Chunk, Image>();

  private final Object drawLock = new Object();

  private final UpdateReceiver user;

  private final Image loading;

  /**
   * Creates a chunk painter.
   * 
   * @param user The user that is notified when something changes.
   * @param scale The scaling factor.
   */
  public ChunkPainter(final UpdateReceiver user, final double scale) {
    this.user = user;
    this.scale = scale;
    loading = createLoadingImage(scale);
    int numThreads = Math.max(Runtime.getRuntime().availableProcessors(), 2);
    System.out.println("Using " + numThreads + " image loader");
    while(--numThreads >= 0) {
      startOneImageThread();
    }
  }

  private void drawChunk0(final Chunk chunk) {
    final BufferedImage img = new BufferedImage((int) (scale * 16),
        (int) (scale * 16), BufferedImage.TYPE_INT_RGB);
    final Graphics2D gi = (Graphics2D) img.getGraphics();
    for(int x = 0; x < 16; ++x) {
      for(int z = 0; z < 16; ++z) {
        final Rectangle2D rect = new Rectangle2D.Double(x * scale, z
            * scale, scale, scale);
        gi.setColor(chunk.getColorForColumn(x, z));
        gi.fill(rect);
      }
    }
    gi.dispose();
    synchronized(imgCache) {
      if(imgCache.containsKey(chunk)) {
        imgCache.put(chunk, img);
      }
    }
  }

  /**
   * Starts another image thread. Note that these threads may not be stopped
   * manually. So be careful how often you call this method.
   */
  private void startOneImageThread() {
    final Thread t = new Thread() {

      @Override
      public void run() {
        try {
          while(!isInterrupted()) {
            for(;;) {
              if(hasPendingChunks()) {
                break;
              }
              waitOnDrawer();
            }
            pollChunkAndDraw();
          }
        } catch(final InterruptedException e) {
          interrupt();
        }
      }

    };
    t.setDaemon(true);
    t.start();
  }

  /**
   * Polls the next chunk and draws its offscreen image.
   */
  public void pollChunkAndDraw() {
    Chunk c;
    synchronized(chunksToDraw) {
      final Iterator<Chunk> it = chunksToDraw.iterator();
      if(!it.hasNext()) return;
      c = it.next();
      it.remove();
    }
    drawChunk0(c);
    somethingChanged();
  }

  /**
   * Getter.
   * 
   * @return Whether there are chunks waiting to be drawn.
   */
  public boolean hasPendingChunks() {
    boolean b;
    synchronized(chunksToDraw) {
      b = chunksToDraw.isEmpty();
    }
    return !b;
  }

  /**
   * Waits for chunks to draw.
   * 
   * @throws InterruptedException If the thread is interrupted.
   */
  public void waitOnDrawer() throws InterruptedException {
    synchronized(drawLock) {
      drawLock.wait();
    }
  }

  /**
   * Notifies that there are chunks to draw.
   */
  public void notifyDrawer() {
    synchronized(drawLock) {
      drawLock.notifyAll();
    }
  }

  /**
   * Draws a chunk on the screen.
   * 
   * @param g The graphics device.
   * @param chunk The chunk.
   */
  public void drawChunk(final Graphics2D g, final Chunk chunk) {
    if(chunk == null) {
      g.drawImage(loading, 0, 0, null);
      return;
    }
    boolean contains;
    synchronized(imgCache) {
      contains = imgCache.containsKey(chunk);
    }
    if(chunk.oneTimeHasChanged() || !contains) {
      synchronized(imgCache) {
        imgCache.put(chunk, loading);
      }
      synchronized(chunksToDraw) {
        chunksToDraw.add(chunk);
      }
      notifyDrawer();
    }
    Image img;
    synchronized(imgCache) {
      img = imgCache.get(chunk);
    }
    g.drawImage(img, 0, 0, null);
  }

  /**
   * Notifies the update receiver.
   */
  public void somethingChanged() {
    user.somethingChanged();
  }

  /**
   * Removes the cached image of a chunk.
   * 
   * @param chunk The chunk to unload.
   */
  public void unloadChunk(final Chunk chunk) {
    final Image img;
    synchronized(imgCache) {
      img = imgCache.remove(chunk);
    }
    if(img != null) {
      img.flush();
    }
  }

  /**
   * Whether the given position is visible in the graphics context.
   * 
   * @param g The graphics context.
   * @param pos The position.
   * @return Whether the position is visible.
   */
  public boolean isValidPos(final Graphics2D g, final OwnChunkPosition pos) {
    final double x = scale(pos.x);
    final double z = scale(pos.z);
    final double len = scale(16);
    final Rectangle2D rect = new Rectangle2D.Double(x, z, len, len);
    return g.hitClip((int) rect.getMinX() - 1, (int) rect.getMinY() - 1,
        (int) rect.getWidth() + 2, (int) rect.getHeight() + 2);
  }

  /**
   * Unscales a coordinate.
   * 
   * @param coord The scaled coordinate.
   * @return The coordinate.
   */
  public double unscale(final double coord) {
    return coord / scale;
  }

  /**
   * Scales a coordinate.
   * 
   * @param coord The coordinate.
   * @return The scaled coordinate.
   */
  public double scale(final double coord) {
    return coord * scale;
  }

}
