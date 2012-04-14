package nbt.map;

import java.awt.Graphics2D;
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

import nbt.read.MapReader;
import nbt.read.MapReader.Pair;
import net.minecraft.world.level.chunk.storage.RegionFile;

public class ChunkManager {

  private final Map<Pair, Chunk> chunks;

  private final Map<Pair, File> reload;

  private final Map<Pair, Pair> otherPos;

  private final Set<Chunk> mayUnload;

  private final UpdateReceiver user;

  public ChunkManager(final UpdateReceiver user) {
    this.user = user;
    chunks = new HashMap<Pair, Chunk>();
    reload = new HashMap<Pair, File>();
    otherPos = new HashMap<Pair, Pair>();
    mayUnload = new HashSet<Chunk>();
    reloader.setDaemon(true);
    reloader.start();
  }

  private Thread iniLoader;

  public void setFolder(final File folder) {
    if(iniLoader != null) {
      synchronized(iniLoader) {
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
    synchronized(chunks) {
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
    for(final File f : files) {
      final MapReader r = MapReader.getForFile(f);
      final List<Pair> chunkList = r.getChunks();
      for(final Pair p : chunkList) {
        if(t != iniLoader || t.isInterrupted()) return;
        final Chunk chunk = new Chunk(r.read(p.x, p.z), f, p);
        unloadChunk(chunk);
      }
      user.somethingChanged();
    }
  }

  private void handleFullMemory() {
    System.err.println("full memory cleanup");
    for(;;) {
      // allocating no more memory but avoiding concurrent modification
      // exception
      final Chunk c;
      synchronized(mayUnload) {
        final Iterator<Chunk> it = mayUnload.iterator();
        if(!it.hasNext()) {
          break;
        }
        c = it.next();
        it.remove();
      }
      unloadChunk(c);
    }
    System.gc();
  }

  public void unloadChunk(final Chunk chunk) {
    final Pair pos = chunk.getPos();
    synchronized(chunks) {
      chunks.remove(pos);
    }
    synchronized(reload) {
      reload.put(pos, chunk.getFile());
    }
    synchronized(otherPos) {
      otherPos.put(pos, chunk.getOtherPos());
    }
    synchronized(mayUnload) {
      mayUnload.remove(chunk);
    }
    // writes the chunk if changed
    chunk.unload();
  }

  public static final double MEM_RATIO = 0.2;

  private static final String TOKEN = "nope";

  private static volatile boolean beFriendly;

  private static void checkHeapStatus() {
    if(beFriendly) return;
    final Runtime r = Runtime.getRuntime();
    final long free = r.freeMemory();
    final long max = r.maxMemory();
    final double ratio = (double) free / (double) max;
    if(ratio <= MEM_RATIO) throw new OutOfMemoryError(TOKEN);
  }

  protected void reloadChunk(final Pair pos) {
    boolean end = false;
    do {
      try {
        checkHeapStatus();
        final File f;
        synchronized(reload) {
          f = reload.get(pos);
        }
        if(f == null) return;
        final Pair op;
        synchronized(otherPos) {
          op = otherPos.get(pos);
        }
        final MapReader r = MapReader.getForFile(f);
        final Chunk chunk = new Chunk(r.read(op.x, op.z), f, op);
        synchronized(chunks) {
          chunks.put(pos, chunk);
        }
        synchronized(reload) {
          reload.remove(pos);
        }
        synchronized(otherPos) {
          otherPos.remove(pos);
        }
        end = true;
      } catch(final OutOfMemoryError e) {
        boolean canUnload;
        synchronized(mayUnload) {
          canUnload = !mayUnload.isEmpty();
        }
        if(canUnload) {
          beFriendly = false;
          handleFullMemory();
        } else if(e.getMessage().equals(TOKEN)) {
          beFriendly = true;
        } else throw new Error(e);
      }
    } while(!end);
  }

  private static Pair[] asArrayPair(final Collection<Pair> entries) {
    return entries.toArray(new Pair[entries.size()]);
  }

  private final Set<Pair> chunksToReload = new HashSet<Pair>();

  private final Thread reloader = new Thread() {

    @Override
    public void run() {
      try {
        while(!isInterrupted()) {
          Pair p;
          for(;;) {
            boolean b;
            synchronized(chunksToReload) {
              b = chunksToReload.isEmpty();
            }
            if(!b) {
              break;
            }
            synchronized(reloader) {
              wait();
            }
          }
          synchronized(chunksToReload) {
            final Iterator<Pair> it = chunksToReload.iterator();
            p = it.next();
            it.remove();
          }
          reloadChunk(p);
          user.somethingChanged();
        }
      } catch(final InterruptedException e) {
        interrupt();
      }
    }

  };

  private final Set<Chunk> editedChunks = new HashSet<Chunk>();

  public void editChunk(final Chunk c, final Pair p, final ChunkEdit editor) {
    editor.edit(c, p);
    editedChunks.add(c);
  }

  public void editFinished() {
    for(final Chunk c : editedChunks) {
      unloadChunk(c);
    }
    editedChunks.clear();
  }

  public Chunk getChunk(final int x, final int z) {
    return getChunk(new Pair(x * 16, z * 16));
  }

  public boolean isVisible(final Chunk c, final Graphics2D g, final Pair pos,
      final ChunkPainter painter) {
    if(painter.isValidPos(g, pos)) return true;
    if(c != null) {
      synchronized(mayUnload) {
        mayUnload.add(c);
      }
    }
    return false;
  }

  public void stayLoaded(final Chunk c) {
    if(c != null) {
      synchronized(mayUnload) {
        mayUnload.remove(c);
      }
    }
  }

  public Chunk getChunk(final Pair pos) {
    Chunk c;
    synchronized(chunks) {
      c = chunks.get(pos);
    }
    return c;
  }

  public void reloadEntries(final Graphics2D g, final ChunkPainter painter) {
    final Pair[] reloadEntries;
    synchronized(reload) {
      reloadEntries = asArrayPair(reload.keySet());
    }
    for(final Pair pos : reloadEntries) {
      if(painter.isValidPos(g, pos)) {
        synchronized(chunksToReload) {
          chunksToReload.add(pos);
        }
        synchronized(reloader) {
          reloader.notify();
        }
      }
    }
  }

  public Pair[] getChunkEntries() {
    Pair[] chunkEntries;
    synchronized(chunks) {
      chunkEntries = asArrayPair(chunks.keySet());
    }
    return chunkEntries;
  }

}
