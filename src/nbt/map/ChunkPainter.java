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

import nbt.map.pos.ChunkPosition;
import nbt.map.pos.InChunkPosition;

/**
 * The chunk painter paints chunks on an image and on the screen.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class ChunkPainter {

  /**
   * Creates the image that is shown when a chunk is not loaded yet.
   * 
   * @return The image.
   */
  public static Image createLoadingImage() {
    final BufferedImage loading =
        new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
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

  private final Set<Chunk> biomesToDraw = new HashSet<Chunk>();

  private final Map<Chunk, Image> imgCache = new HashMap<Chunk, Image>();

  private final Map<Chunk, Image> biomeCache = new HashMap<Chunk, Image>();

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
    loading = createLoadingImage();
    int numThreads = Math.max(Runtime.getRuntime().availableProcessors(), 2);
    System.out.println("Using " + numThreads + " image loader");
    while(--numThreads >= 0) {
      startOneImageThread();
    }
  }

  private void drawChunk(final Chunk chunk) {
    final BufferedImage img =
        new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
    final Graphics2D gi = (Graphics2D) img.getGraphics();
    for(int x = 0; x < 16; ++x) {
      for(int z = 0; z < 16; ++z) {
        final Rectangle2D rect = new Rectangle2D.Double(x, z, 1, 1);
        final InChunkPosition pos = new InChunkPosition(x, z);
        gi.setColor(chunk.getColorForColumn(pos));
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

  private void drawBiome(final Chunk chunk) {
    final BufferedImage biome =
        new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D gb = (Graphics2D) biome.getGraphics();
    for(int x = 0; x < 16; ++x) {
      for(int z = 0; z < 16; ++z) {
        final Rectangle2D rect = new Rectangle2D.Double(x, z, 1, 1);
        final InChunkPosition pos = new InChunkPosition(x, z);
        gb.setColor(chunk.getBiome(pos).color);
        gb.fill(rect);
      }
    }
    gb.dispose();
    synchronized(biomeCache) {
      biomeCache.put(chunk, biome);
    }
  }

  /**
   * The current number of active painter threads.
   */
  protected int numberOfThreads;

  /**
   * Starts another image thread. Note that these threads may not be stopped
   * manually. So be careful how often you call this method.
   */
  private void startOneImageThread() {
    final Thread t = new Thread() {

      @Override
      public void run() {
        try {
          synchronized(ChunkPainter.this) {
            ++numberOfThreads;
          }
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
        } finally {
          synchronized(ChunkPainter.this) {
            --numberOfThreads;
          }
        }
      }

    };
    t.setDaemon(true);
    t.start();
  }

  private ChunkPosition pos;

  /**
   * Setter.
   * 
   * @param pos Sets the currently selected position on the screen.
   */
  public void setPos(final ChunkPosition pos) {
    this.pos = pos;
  }

  private static Chunk getBestChunk(final Iterator<Chunk> it,
      final ChunkPosition pos) {
    Chunk best = null;
    double dist = Double.POSITIVE_INFINITY;
    while(it.hasNext()) {
      final Chunk next = it.next();
      if(pos == null) return next;
      final ChunkPosition np = next.getPos();
      final double dx = np.x - pos.x;
      final double dz = np.z - pos.z;
      final double d = dx * dx + dz * dz;
      if(d < dist) {
        best = next;
        dist = d;
      }
    }
    return best;
  }

  /**
   * Polls the next chunk and draws its offscreen image.
   */
  public void pollChunkAndDraw() {
    Chunk c;
    synchronized(chunksToDraw) {
      c = getBestChunk(chunksToDraw.iterator(), pos);
      if(c != null) {
        chunksToDraw.remove(c);
      }
    }
    if(c != null) {
      drawChunk(c);
    }
    Chunk b;
    synchronized(biomesToDraw) {
      b = getBestChunk(biomesToDraw.iterator(), pos);
      if(b != null) {
        biomesToDraw.remove(c);
      }
    }
    if(b != null) {
      drawBiome(b);
    }
    if(c != null || b != null) {
      somethingChanged();
    }
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
    if(b) {
      synchronized(biomesToDraw) {
        b = biomesToDraw.isEmpty();
      }
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
    if(!contains || chunk.oneTimeHasChanged()) {
      synchronized(imgCache) {
        imgCache.put(chunk, loading);
      }
      final Image biome;
      synchronized(biomeCache) {
        biome = biomeCache.remove(chunk);
      }
      if(biome != null) {
        biome.flush();
      }
      synchronized(chunksToDraw) {
        chunksToDraw.add(chunk);
      }
      if(showBiomes) {
        synchronized(biomesToDraw) {
          biomesToDraw.add(chunk);
        }
      }
      notifyDrawer();
    }
    final Image img;
    synchronized(imgCache) {
      img = imgCache.get(chunk);
    }
    g.scale(scale, scale);
    g.drawImage(img, 0, 0, null);
    if(showBiomes) {
      final Image biome;
      synchronized(biomeCache) {
        biome = biomeCache.get(chunk);
      }
      if(biome != null) {
        g.drawImage(biome, 0, 0, null);
      } else {
        synchronized(biomesToDraw) {
          biomesToDraw.add(chunk);
        }
        notifyDrawer();
      }
    }
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
    final Image biome;
    synchronized(biomeCache) {
      biome = biomeCache.remove(chunk);
    }
    if(biome != null) {
      biome.flush();
    }
  }

  /**
   * Clears all biome images.
   */
  public void clearBiomes() {
    synchronized(biomeCache) {
      final Image[] values =
          biomeCache.values().toArray(new Image[biomeCache.size()]);
      biomeCache.clear();
      for(final Image biome : values) {
        if(biome != null) {
          biome.flush();
        }
      }
    }
  }

  /**
   * Clears all images.
   */
  public void clearAll() {
    synchronized(chunksToDraw) {
      chunksToDraw.clear();
    }
    synchronized(biomesToDraw) {
      biomesToDraw.clear();
    }
    clearBiomes();
    synchronized(imgCache) {
      final Image[] values =
          imgCache.values().toArray(new Image[imgCache.size()]);
      imgCache.clear();
      for(final Image i : values) {
        if(i != null) {
          i.flush();
        }
      }
    }
  }

  /**
   * Whether the given position is visible in the graphics context.
   * 
   * @param g The graphics context.
   * @param pos The position.
   * @return Whether the position is visible.
   */
  public boolean isValidPos(final Graphics2D g, final ChunkPosition pos) {
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

  private boolean showBiomes;

  /**
   * Setter.
   * 
   * @param showBiomes Whether to show biome data in the map.
   */
  public void setShowBiomes(final boolean showBiomes) {
    this.showBiomes = showBiomes;
    user.somethingChanged();
  }

  /**
   * Getter.
   * 
   * @return Whether to show biome data in the map.
   */
  public boolean showsBiomes() {
    return showBiomes;
  }

}
