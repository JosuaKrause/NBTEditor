package nbt.map;

import java.awt.Color;
import java.io.File;

import nbt.record.NBTByteArray;
import nbt.record.NBTCompound;
import nbt.record.NBTList;
import nbt.record.NBTNumeric;
import nbt.record.NBTRecord;
import net.minecraft.world.level.chunk.storage.RegionFile;

public class Chunk {

    private final NBTCompound level;

    public Chunk(final NBTRecord root, final File file) {
        if (!file.getName().endsWith(RegionFile.ANVIL_EXTENSION)) {
            throw new IllegalArgumentException(file + " not in anvil format!");
        }
        level = (NBTCompound) ((NBTCompound) root).get("Level");
    }

    protected NBTByteArray getBiomesArray() {
        return (NBTByteArray) level.get("Biomes");
    }

    protected NBTCompound getSection(final int y) {
        final NBTList sections = (NBTList) level.get("Sections");
        if (sections.getLength() > y) {
            final NBTCompound comp = (NBTCompound) sections.getAt(y);
            if (checkSectionY(comp, y)) {
                return comp;
            }
        }
        for (final NBTRecord r : sections) {
            final NBTCompound comp = (NBTCompound) r;
            if (checkSectionY(comp, y)) {
                return comp;
            }
        }
        return null;
    }

    private static boolean checkSectionY(final NBTCompound section, final int y) {
        return ((NBTNumeric) section.get("Y")).getPayload().byteValue() == y;
    }

    private static int getBlockPositionInSection(final int x, final int y,
            final int z) {
        return (y << 8) | (z << 4) | x;
    }

    private static int getBlockInSection(final NBTCompound section,
            final int x, final int y, final int z) {
        if (section == null) {
            return Blocks.AIR.id;
        }
        final int pos = getBlockPositionInSection(x, y, z);
        final NBTByteArray blocks = (NBTByteArray) section.get("Blocks");
        final NBTByteArray addBlocks = (NBTByteArray) section.get("AddBlocks");
        return blocks.getAt(pos)
                + (addBlocks != null ? (addBlocks.getAt(pos) << 8) : 0);
    }

    public int getBlockFor(final int x, final int y, final int z) {
        final int sectionY = y / 16;
        final int inSectionY = y % 16;
        return getBlockInSection(getSection(sectionY), x, inSectionY, z);
    }

    public boolean hasBlockFor(final int y) {
        final int sectionY = y / 16;
        return getSection(sectionY) != null;
    }

    private static int getBiomePosition(final int x, final int z) {
        return (z << 4) | x;
    }

    public int getBiomeFor(final int x, final int z) {
        return getBiomesArray().getAt(getBiomePosition(x, z));
    }

    public Biomes getBiome(final int x, final int z) {
        return Biomes.getBlockForId(getBiomeFor(x, z));
    }

    public Color getColorForColumn(final int x, final int z) {
        int y = 0;
        Color res = new Color(0, true);
        while (hasBlockFor(y)) {
            final Blocks b = Blocks.getBlockForId(getBlockFor(x, y, z));
            res = combine(res, b.color);
            ++y;
        }
        return res;
    }

    private static Color combine(final Color old, final Color add) {
        final double a = add.getAlpha() / 255.0;
        final double r = old.getRed() * (1.0 - a) + add.getRed() * a;
        final double g = old.getGreen() * (1.0 - a) + add.getGreen() * a;
        final double b = old.getBlue() * (1.0 - a) + add.getBlue() * a;
        return new Color((int) r, (int) g, (int) b);
    }

    public int getXPos() {
        return ((NBTNumeric) level.get("xPos")).getPayload().intValue();
    }

    public int getZPos() {
        return ((NBTNumeric) level.get("zPos")).getPayload().intValue();
    }

    public int getX() {
        return getXPos() * 16;
    }

    public int getZ() {
        return getZPos() * 16;
    }

}
