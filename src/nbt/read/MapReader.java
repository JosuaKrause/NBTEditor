package nbt.read;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nbt.map.pos.ChunkInFilePosition;
import nbt.record.NBTCompound;
import nbt.record.NBTRecord;
import nbt.record.NBTType;
import nbt.write.NBTWriter;
import net.minecraft.world.level.chunk.storage.RegionFile;

/**
 * The map reader reads map files from the disk and returns records for the
 * chunks.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class MapReader {

  private static final Map<File, MapReader> CACHE =
      new HashMap<File, MapReader>();

  /**
   * Finds the appropriate and maybe cached reader for the given file.
   * 
   * @param file The file.
   * @return The map reader.
   */
  public static MapReader getForFile(final File file) {
    synchronized(CACHE) {
      if(!CACHE.containsKey(file)) {
        CACHE.put(file, new MapReader(file));
      }
      return CACHE.get(file);
    }
  }

  /**
   * Clears the map reader cache.
   */
  public static void clearCache() {
    synchronized(CACHE) {
      CACHE.clear();
    }
  }

  private final RegionFile regionSource;

  private MapReader(final File regionFile) {
    regionSource = new RegionFile(regionFile);
  }

  /**
   * Getter.
   * 
   * @return Creates a list of chunks in this region file.
   */
  public synchronized List<ChunkInFilePosition> getChunks() {
    final List<ChunkInFilePosition> res = new ArrayList<ChunkInFilePosition>();
    try {
      for(int x = 0; x < 32; x++) {
        for(int z = 0; z < 32; z++) {
          if(regionSource.hasChunk(x, z)) {
            final DataInputStream regionChunkInputStream =
                regionSource.getChunkDataInputStream(x, z);
            if(regionChunkInputStream == null) {
              System.err.println("Failed to fetch input stream");
              continue;
            }
            res.add(new ChunkInFilePosition(x, z));
            regionChunkInputStream.close();
          }
        }
      }
    } catch(final IOException e) {
      e.printStackTrace();
    }
    return res;
  }

  /**
   * Reads a chunk record.
   * 
   * @param pos The position of the chunk.
   * @return The record.
   */
  public synchronized NBTCompound read(final ChunkInFilePosition pos) {
    NBTCompound rec = null;
    try {
      if(regionSource.hasChunk(pos.x, pos.z)) {
        final DataInputStream regionChunkInputStream =
            regionSource.getChunkDataInputStream(pos.x, pos.z);
        if(regionChunkInputStream == null) throw new IOException(
            "Failed to fetch input stream");
        final NBTReader r = new NBTReader(regionChunkInputStream, false);
        rec = r.read(NBTType.COMPOUND);
        r.close();
      }
    } catch(final IOException e) {
      e.printStackTrace();
    }
    return rec;
  }

  /**
   * Writes a changed chunk record into the map file.
   * 
   * @param rec The chunk record.
   * @param x The x position of the chunk.
   * @param z The z position of the chunk.
   * @throws IOException I/O Exception.
   */
  public synchronized void write(final NBTRecord rec, final int x, final int z)
      throws IOException {
    if(!rec.hasChanged()) return;
    final NBTWriter out =
        new NBTWriter(regionSource.getChunkDataOutputStream(x, z), false);
    out.write(rec);
    out.close();
  }

}
