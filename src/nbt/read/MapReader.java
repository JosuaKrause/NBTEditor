package nbt.read;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nbt.record.NBTRecord;
import net.minecraft.world.level.chunk.storage.RegionFile;

public class MapReader {

    public class Pair {
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
    }

    private final File regionFile;

    private final RegionFile regionSource;

    public MapReader(final File regionFile) {
        this.regionFile = regionFile;
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

    public File getRegionFile() {
        return regionFile;
    }

}
