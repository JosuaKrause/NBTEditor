package nbt.map;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nbt.DynamicArray;
import nbt.read.MapReader;
import nbt.read.MapReader.Pair;
import nbt.record.NBTByteArray;
import nbt.record.NBTCompound;
import nbt.record.NBTList;
import nbt.record.NBTNumeric;
import nbt.record.NBTRecord;
import net.minecraft.world.level.chunk.storage.RegionFile;

public class Chunk {

    public static final int WORLD_HEIGHT = 256;

    private final NBTCompound level;

    private final NBTRecord root;

    private final File file;

    private final Pair otherPos;

    public Chunk(final NBTRecord root, final File file, final Pair otherPos) {
        if (!file.getName().endsWith(RegionFile.ANVIL_EXTENSION)) {
            throw new IllegalArgumentException(file + " not in anvil format!");
        }
        this.root = root;
        this.otherPos = otherPos;
        this.file = file;
        level = (NBTCompound) ((NBTCompound) root).get("Level");
        xCache = ((NBTNumeric) level.get("xPos")).getPayload().intValue();
        zCache = ((NBTNumeric) level.get("zPos")).getPayload().intValue();
        biomes = (NBTByteArray) level.get("Biomes");
        sections = (NBTList) level.get("Sections");
        sectionCache = new DynamicArray<NBTCompound>(sections.getLength());
        for (final NBTRecord r : sections) {
            final NBTCompound comp = (NBTCompound) r;
            final int y = getSectionY(comp);
            sectionCache.set(y, comp);
        }
    }

    public Pair getOtherPos() {
        return otherPos;
    }

    public Pair getPos() {
        return new Pair(getX(), getZ());
    }

    public File getFile() {
        return file;
    }

    private final NBTByteArray biomes;

    protected NBTByteArray getBiomesArray() {
        return biomes;
    }

    private final NBTList sections;

    private final DynamicArray<NBTCompound> sectionCache;

    protected NBTCompound getSection(final int y) {
        return sectionCache.get(y);
    }

    private static int getSectionY(final NBTCompound section) {
        return ((NBTNumeric) section.get("Y")).getPayload().byteValue();
    }

    private static int getBlockPositionInSection(final int x, final int y,
            final int z) {
        return (y << 8) | (z << 4) | x;
    }

    private final Map<NBTCompound, NBTByteArray> cacheBlocks = new HashMap<NBTCompound, NBTByteArray>();

    private final Map<NBTCompound, NBTByteArray> cacheAddBlocks = new HashMap<NBTCompound, NBTByteArray>();

    private int getBlockInSection(final NBTCompound section, final int x,
            final int y, final int z) {
        if (section == null) {
            return Blocks.AIR.id;
        }
        final int pos = getBlockPositionInSection(x, y, z);
        if (!cacheBlocks.containsKey(section)) {
            cacheBlocks.put(section, (NBTByteArray) section.get("Blocks"));
        }
        final NBTByteArray blocks = cacheBlocks.get(section);
        if (!cacheAddBlocks.containsKey(section)) {
            cacheAddBlocks
                    .put(section, (NBTByteArray) section.get("AddBlocks"));
        }
        final NBTByteArray addBlocks = cacheAddBlocks.get(section);
        return blocks.getAt(pos)
                + (addBlocks != null ? (addBlocks.getAt(pos) << 8) : 0);
    }

    private void setBlockInSection(final NBTCompound section, final int x,
            final int y, final int z, final Blocks b) {
        if (section == null) {
            throw new NullPointerException("section may not be null");
        }
        final int pos = getBlockPositionInSection(x, y, z);
        if (!cacheBlocks.containsKey(section)) {
            cacheBlocks.put(section, (NBTByteArray) section.get("Blocks"));
        }
        final NBTByteArray blocks = cacheBlocks.get(section);
        if (!cacheAddBlocks.containsKey(section)) {
            cacheAddBlocks
                    .put(section, (NBTByteArray) section.get("AddBlocks"));
        }
        final NBTByteArray addBlocks = cacheAddBlocks.get(section);
        final int id = b.id;
        if (id > 0xff && addBlocks == null) {
            throw new UnsupportedOperationException(
                    "need to create an AddBlocks");
        }
        blocks.setAt(pos, (byte) id);
        if (addBlocks != null) {
            addBlocks.setAt(pos, (byte) (id >>> 8));
        }
        changeAt(x, z);
    }

    protected int getBlockFor(final int x, final int y, final int z) {
        final int sectionY = y / 16;
        final int inSectionY = y % 16;
        return getBlockInSection(getSection(sectionY), x, inSectionY, z);
    }

    protected void setBlockAt(final int x, final int y, final int z,
            final Blocks b) {
        final int sectionY = y / 16;
        final int inSectionY = y % 16;
        final NBTCompound section = getSection(sectionY);
        if (section == null) {
            throw new UnsupportedOperationException(
                    "attempt to create a new section");
        }
        setBlockInSection(section, x, inSectionY, z, b);
    }

    public Blocks getBlock(final Position pos) {
        return getBlock(pos.x, pos.y, pos.z);
    }

    public Blocks getBlock(final int x, final int y, final int z) {
        return Blocks.getBlockForId(getBlockFor(x, y, z));
    }

    public void setBlock(final Position pos, final Blocks b) {
        setBlock(pos.x, pos.y, pos.z, b);
    }

    public void setBlock(final int x, final int y, final int z, final Blocks b) {
        setBlockAt(x, y, z, b);
    }

    public static final class Position {
        public final int x;
        public final int y;
        public final int z;

        public Position(final int x, final int y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public Position getTopNonAirBlock(final int x, final int z) {
        int y = WORLD_HEIGHT;
        while (hasBlockFor(y)) {
            if (getBlock(x, y, z) != Blocks.AIR) {
                return new Position(x, y, z);
            }
            --y;
        }
        // actually never really happens -- but when it does we return an air
        // block
        return new Position(x, y, z);
    }

    public boolean hasBlockFor(final int y) {
        // does not handle holes correctly
        // final int sectionY = y / 16;
        // return getSection(sectionY) != null;
        return y <= WORLD_HEIGHT && y >= 0;
    }

    public boolean canSetBlock(final int y) {
        final int sectionY = y / 16;
        return getSection(sectionY) != null && hasBlockFor(y);
    }

    private static int getBiomePosition(final int x, final int z) {
        return (z << 4) | x;
    }

    protected int getBiomeFor(final int x, final int z) {
        return getBiomesArray().getAt(getBiomePosition(x, z));
    }

    public void setBiome(final int x, final int z, final Biomes biome) {
        getBiomesArray().setAt(getBiomePosition(x, z), (byte) biome.id);
        changeAt(x, z);
    }

    public Biomes getBiome(final int x, final int z) {
        return Biomes.getBlockForId(getBiomeFor(x, z));
    }

    private boolean hasChanged;

    private final Color colors[][] = new Color[16][16];

    public void changeAt(final int x, final int z) {
        colors[x][z] = null;
        hasChanged = true;
    }

    public boolean oneTimeHasChanged() {
        final boolean hc = hasChanged;
        hasChanged = false;
        return hc;
    }

    public Color getColorForColumn(final int x, final int z) {
        if (colors[x][z] == null) {
            int y = 0;
            Color res = new Color(0, true);
            while (hasBlockFor(y)) {
                final Blocks b = Blocks.getBlockForId(getBlockFor(x, y, z));
                res = combine(res, b.color);
                ++y;
            }
            colors[x][z] = res;
        }
        return colors[x][z];
    }

    private static Color combine(final Color old, final Color add) {
        final int alpha = add.getAlpha();
        if (alpha == 255) {
            return add;
        }
        if (alpha == 0) {
            return old;
        }
        final double a = alpha / 255.0;
        final double r = old.getRed() * (1.0 - a) + add.getRed() * a;
        final double g = old.getGreen() * (1.0 - a) + add.getGreen() * a;
        final double b = old.getBlue() * (1.0 - a) + add.getBlue() * a;
        return new Color((int) r, (int) g, (int) b);
    }

    private final int xCache;

    public int getXPos() {
        return xCache;
    }

    private final int zCache;

    public int getZPos() {
        return zCache;
    }

    public int getX() {
        return getXPos() * 16;
    }

    public int getZ() {
        return getZPos() * 16;
    }

    private boolean active = true;

    public void unload() {
        if (active) {
            if (root.hasChanged()) {
                try {
                    final MapReader r = MapReader.getForFile(file);
                    r.write(root, otherPos.x, otherPos.z);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            active = false;
        }
    }

}
