package nbt.read;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nbt.record.NBTRecord;
import nbt.write.NBTWriter;
import net.minecraft.world.level.chunk.storage.RegionFile;

public final class MapReader {

    public static final class Pair {
        public final int x;
        public final int z;

        public Pair(final int x, final int z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public String toString() {
            return "x: " + x + " z: " + z;
        }

        @Override
        public int hashCode() {
            return x * 31 + z;
        }

        @Override
        public boolean equals(final Object obj) {
            final Pair p = (Pair) obj;
            return p.x == x && p.z == z;
        }
    }

    private static final Map<File, MapReader> CACHE = new HashMap<File, MapReader>();

    public static MapReader getForFile(final File file) {
        synchronized (CACHE) {
            if (!CACHE.containsKey(file)) {
                CACHE.put(file, new MapReader(file));
            }
            return CACHE.get(file);
        }
    }

    public static void clearCache() {
        synchronized (CACHE) {
            CACHE.clear();
        }
    }

    private final RegionFile regionSource;

    private MapReader(final File regionFile) {
        regionSource = new RegionFile(regionFile);
    }

    public List<Pair> getChunks() {
        final List<Pair> res = new ArrayList<Pair>();
        try {
            for (int x = 0; x < 32; x++) {
                for (int z = 0; z < 32; z++) {
                    if (regionSource.hasChunk(x, z)) {
                        final DataInputStream regionChunkInputStream = regionSource
                                .getChunkDataInputStream(x, z);
                        if (regionChunkInputStream == null) {
                            System.out.println("Failed to fetch input stream");
                            continue;
                        }
                        res.add(new Pair(x, z));
                        regionChunkInputStream.close();
                    }
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public NBTRecord read(final int x, final int z) {
        NBTRecord rec = null;
        try {
            if (regionSource.hasChunk(x, z)) {
                final DataInputStream regionChunkInputStream = regionSource
                        .getChunkDataInputStream(x, z);
                if (regionChunkInputStream == null) {
                    throw new IOException("Failed to fetch input stream");
                }
                final NBTReader r = new NBTReader(regionChunkInputStream, false);
                rec = r.read();
                r.close();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return rec;
    }

    public void write(final NBTRecord rec, final int x, final int z)
            throws IOException {
        if (!rec.hasChanged()) {
            return;
        }
        final NBTWriter out = new NBTWriter(
                regionSource.getChunkDataOutputStream(x, z), false);
        out.write(rec);
        out.close();
    }

}
