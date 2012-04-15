package nbt.map;

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

/**
 * The chunk manager manages to load and unload chunks in a given folder.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class ChunkManager {

  private final Map<Pair, Chunk> chunks;

  private final Map<Pair, File> reload;

  private final Map<Pair, Pair> otherPos;

  private final Set<Chunk> mayUnload;

  private final UpdateReceiver user;

  private final Object reloaderLock = new Object();

  /**
   * Creates a chunk manager.
   * 
   * @param user The user that is notified when something changes.
   */
  public ChunkManager(final UpdateReceiver user) {
    this.user = user;
    chunks = new HashMap<Pair, Chunk>();
    reload = new HashMap<Pair, File>();
    otherPos = new HashMap<Pair, Pair>();
    mayUnload = new HashSet<Chunk>();
    int numThreads = Math.max(Runtime.getRuntime().availableProcessors(), 2);
    while(--numThreads >= 0) {
      startOneReloaderThread();
    }
  }

  private Thread iniLoader;

  /**
   * Setter.
   * 
   * @param folder The folder whose chunks are being loaded.
   */
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
        loadFolder(folder);
      }

    };
    iniLoader.setDaemon(true);
    iniLoader.start();
  }

  /**
   * Loads a folder. This method should only be used by the loader thread.
   * Otherwise this method is a no-op.
   * 
   * @param folder The folder to load.
   */
  public void loadFolder(final File folder) {
    final Thread t = Thread.currentThread();
    if(t != iniLoader || t.isInterrupted()) return;
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
      // allocating no more memory but avoiding
      // concurrent modification exception
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

  /**
   * Unloads a chunk. The chunk is saved, when it has been changed.
   * 
   * @param chunk The chunk to unload.
   */
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

  /**
   * The free to max memory ratio where a {@link OutOfMemoryError} will be
   * thrown.
   */
  public static final double MEM_RATIO = 0.2;

  /**
   * A token string to identify the early thrown out of memory error to avoid a
   * real {@link OutOfMemoryError}.
   */
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

  /**
   * Reloads a chunk.
   * 
   * @param pos The position of the chunk.
   */
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

  /**
   * Converts a collection of pairs to an array.
   * 
   * @param entries The collection of pairs.
   * @return The array.
   */
  private static Pair[] asArrayPair(final Collection<Pair> entries) {
    return entries.toArray(new Pair[entries.size()]);
  }

  private final Set<Pair> chunksToReload = new HashSet<Pair>();

  /**
   * Starts another reloader thread. Note that these threads may not be stopped
   * manually. So be carefull how often you call this method.
   */
  private void startOneReloaderThread() {
    final Thread res = new Thread() {

      @Override
      public void run() {
        try {
          while(!isInterrupted()) {
            for(;;) {
              if(hasContentToReload()) {
                break;
              }
              waitForReloader();
            }
            reloadNext();
          }
        } catch(final InterruptedException e) {
          interrupt();
        }
      }

    };
    res.setDaemon(true);
    res.start();
  }

  /**
   * Getter.
   * 
   * @return Whether there are chunks waiting to be reloaded.
   */
  public boolean hasContentToReload() {
    boolean b;
    synchronized(chunksToReload) {
      b = chunksToReload.isEmpty();
    }
    return !b;
  }

  /**
   * Reloads the next chunk in the list.
   */
  public void reloadNext() {
    final Pair p;
    synchronized(chunksToReload) {
      final Iterator<Pair> it = chunksToReload.iterator();
      p = it.next();
      it.remove();
    }
    reloadChunk(p);
    user.somethingChanged();
  }

  /**
   * Waits on the reloader monitor until chunks need to be reloaded.
   * 
   * @throws InterruptedException If the thread was interrupted.
   */
  public void waitForReloader() throws InterruptedException {
    synchronized(reloaderLock) {
      reloaderLock.wait();
    }
  }

  /**
   * Notifies that a chunk is waiting to be reloaded.
   */
  public void notifyReloader() {
    synchronized(reloaderLock) {
      reloaderLock.notifyAll();
    }
  }

  private final Set<Chunk> editedChunks = new HashSet<Chunk>();

  private boolean multi;

  /**
   * Edits the given chunk with a chunk editor.
   * 
   * @param c The chunk.
   * @param p The position.
   * @param editor The editor.
   */
  public void editChunk(final Chunk c, final Pair p, final ChunkEdit editor) {
    editor.edit(c, p);
    editedChunks.add(c);
  }

  /**
   * Sets the multi edit mode. In multi edit mode {@link #editFinished()} calls
   * are no-ops. When the multi edit mode is set to <code>false</code> the real
   * {@link #editFinished()} call is made.
   * 
   * @param multi Set multi edit mode.
   */
  public void setMultiedit(final boolean multi) {
    this.multi = multi;
    if(!multi) {
      editFinished();
    }
  }

  /**
   * Reports that the editing has finished and that the altered chunks should be
   * saved.
   */
  public void editFinished() {
    if(multi) return;
    for(final Chunk c : editedChunks) {
      unloadChunk(c);
    }
    editedChunks.clear();
  }

  /**
   * Gets the chunk at the given position.
   * 
   * @param x The x position.
   * @param z The z position.
   * @return The chunk.
   */
  public Chunk getChunk(final int x, final int z) {
    return getChunk(new Pair(x * 16, z * 16));
  }

  /**
   * Signals that a chunk may be unloaded.
   * 
   * @param c The chunk.
   */
  public void mayUnload(final Chunk c) {
    if(c != null) {
      synchronized(mayUnload) {
        mayUnload.add(c);
      }
    }
  }

  /**
   * Signals that a chunk must stay loaded.
   * 
   * @param c The chunk.
   */
  public void stayLoaded(final Chunk c) {
    if(c != null) {
      synchronized(mayUnload) {
        mayUnload.remove(c);
      }
    }
  }

  /**
   * Getter.
   * 
   * @param pos The position.
   * @return The chunk at the given position.
   */
  public Chunk getChunk(final Pair pos) {
    Chunk c;
    synchronized(chunks) {
      c = chunks.get(pos);
    }
    return c;
  }

  /**
   * Signals that a chunk needs to be reloaded.
   * 
   * @param pos The position of the chunk.
   */
  public void needsReload(final Pair pos) {
    synchronized(chunksToReload) {
      chunksToReload.add(pos);
    }
    notifyReloader();
  }

  /**
   * Getter.
   * 
   * @return All chunk positions that may be reloaded.
   */
  public Pair[] getReloadEntries() {
    Pair[] reloadEntries;
    synchronized(reload) {
      reloadEntries = asArrayPair(reload.keySet());
    }
    return reloadEntries;
  }

  /**
   * Getter.
   * 
   * @return All chunks positions.
   */
  public Pair[] getChunkEntries() {
    Pair[] chunkEntries;
    synchronized(chunks) {
      chunkEntries = asArrayPair(chunks.keySet());
    }
    return chunkEntries;
  }

}
