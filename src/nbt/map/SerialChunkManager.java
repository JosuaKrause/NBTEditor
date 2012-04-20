package nbt.map;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nbt.map.pos.ChunkInFilePosition;
import nbt.map.pos.ChunkPosition;
import nbt.map.pos.WorldPosition;
import nbt.read.MapReader;
import net.minecraft.world.level.chunk.storage.RegionFile;

/**
 * A chunk manager that does not rely on threads but ensures that a chunk will
 * be loaded on request. This way is easier to use for non interactive edits.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class SerialChunkManager {

  private final Map<ChunkPosition, Chunk> chunks;

  private final Map<ChunkPosition, File> reload;

  private final Map<ChunkPosition, ChunkInFilePosition> otherPos;

  /**
   * Creates a serial chunk manager.
   */
  public SerialChunkManager() {
    chunks = new HashMap<ChunkPosition, Chunk>();
    reload = new HashMap<ChunkPosition, File>();
    otherPos = new HashMap<ChunkPosition, ChunkInFilePosition>();
  }

  /**
   * Setter.
   * 
   * @param folder The folder whose chunks are loaded.
   * @param clearCache Whether to clear the map reader cache.
   */
  public void setFolder(final File folder, final boolean clearCache) {
    chunks.clear();
    if(clearCache) {
      MapReader.clearCache();
    }
    final File[] files = folder.listFiles(new FileFilter() {

      @Override
      public boolean accept(final File f) {
        return f.isFile()
            && f.getName().endsWith(RegionFile.ANVIL_EXTENSION);
      }

    });
    for(final File f : files) {
      final MapReader r = MapReader.getForFile(f);
      final List<ChunkInFilePosition> chunkList = r.getChunks();
      for(final ChunkInFilePosition p : chunkList) {
        final Chunk chunk = new Chunk(r.read(p), f, p);
        unloadChunk(chunk);
      }
    }
  }

  /**
   * Removes the chunk from the memory and saves changes.
   * 
   * @param chunk The chunk to unload.
   */
  public void unloadChunk(final Chunk chunk) {
    final ChunkPosition pos = chunk.getPos();
    chunks.remove(pos);
    reload.put(pos, chunk.getFile());
    otherPos.put(pos, chunk.getInFilePos());
    // writes the chunk if changed
    chunk.unload();
  }

  /**
   * Gets the chunk at the given position. The chunk should be unloaded with
   * {@link #unloadChunk(Chunk)} after usage.
   * 
   * @param pos The position.
   * @return The chunk.
   */
  public Chunk getChunk(final WorldPosition pos) {
    return getChunk(pos.getPosOfChunk());
  }

  /**
   * Getter.
   * 
   * @param pos The position.
   * @return The chunk at the given position.
   */
  private Chunk getChunk(final ChunkPosition pos) {
    if(!chunks.containsKey(pos)) {
      reloadChunk(pos);
    }
    return chunks.get(pos);
  }

  /**
   * Reloads a chunk.
   * 
   * @param pos The position of the chunk.
   */
  private void reloadChunk(final ChunkPosition pos) {
    final File f = reload.get(pos);
    if(f == null) return;
    final ChunkInFilePosition op = otherPos.get(pos);
    final MapReader r = MapReader.getForFile(f);
    final Chunk chunk = new Chunk(r.read(op), f, op);
    chunks.put(pos, chunk);
    reload.remove(pos);
    otherPos.remove(pos);
  }

}
