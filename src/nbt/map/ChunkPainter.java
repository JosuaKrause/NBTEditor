package nbt.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import nbt.read.MapReader.Pair;

public class ChunkPainter {

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

  private final UpdateReceiver user;

  private final Image loading;

  public ChunkPainter(final UpdateReceiver user, final double scale) {
    this.user = user;
    this.scale = scale;
    loading = createLoadingImage(scale);
    drawer.setDaemon(true);
    drawer.start();
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

  private final Thread drawer = new Thread() {

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

  public void pollChunkAndDraw() {
    Chunk c;
    synchronized(chunksToDraw) {
      final Iterator<Chunk> it = chunksToDraw.iterator();
      c = it.next();
      it.remove();
    }
    drawChunk0(c);
    somethingChanged();
  }

  public boolean hasPendingChunks() {
    boolean b;
    synchronized(chunksToDraw) {
      b = chunksToDraw.isEmpty();
    }
    return !b;
  }

  public void waitOnDrawer() throws InterruptedException {
    synchronized(drawer) {
      drawer.wait();
    }
  }

  public void notifyDrawer() {
    synchronized(drawer) {
      drawer.notify();
    }
  }

  public void drawChunk(final Graphics2D g, final Chunk chunk,
      final ImageObserver observer) {
    if(chunk == null) {
      g.drawImage(loading, 0, 0, observer);
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
    g.drawImage(img, 0, 0, observer);
  }

  public void somethingChanged() {
    user.somethingChanged();
  }

  public void unloadChunk(final Chunk chunk) {
    final Image img;
    synchronized(imgCache) {
      img = imgCache.remove(chunk);
    }
    if(img != null) {
      img.flush();
    }
  }

  public boolean isValidPos(final Graphics2D g, final Pair pos) {
    final double x = scale(pos.x);
    final double z = scale(pos.z);
    final double len = scale(16);
    final Rectangle2D rect = new Rectangle2D.Double(x, z, len, len);
    return g.hitClip((int) rect.getMinX() - 1, (int) rect.getMinY() - 1,
        (int) rect.getWidth() + 2, (int) rect.getHeight() + 2);
  }

  public double unscale(final double coord) {
    return coord / scale;
  }

  public double scale(final double coord) {
    return coord * scale;
  }

}
