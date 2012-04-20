package nbt.map;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nbt.DynamicArray;
import nbt.map.pos.ChunkPosition;
import nbt.map.pos.OwnChunkPosition;
import nbt.map.pos.Position3D;
import nbt.read.MapReader;
import nbt.record.NBTByteArray;
import nbt.record.NBTCompound;
import nbt.record.NBTList;
import nbt.record.NBTNumeric;
import net.minecraft.world.level.chunk.storage.RegionFile;

/**
 * A chunk is 16 by 16 block field in a map.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class Chunk {

  /**
   * The maximal world height.
   */
  public static final int WORLD_HEIGHT = 256;

  private final NBTCompound level;

  private final NBTCompound root;

  private final File file;

  private final ChunkPosition otherPos;

  /**
   * Creates a chunk from a given record.
   * 
   * @param root The record.
   * @param file The associated file.
   * @param otherPos The position of the map file.
   */
  public Chunk(final NBTCompound root, final File file,
      final ChunkPosition otherPos) {
    final boolean validExt =
        file.getName().endsWith(RegionFile.ANVIL_EXTENSION);
    if(!validExt) throw new IllegalArgumentException(
        file + " not in anvil format!");
    this.root = root;
    this.otherPos = otherPos;
    this.file = file;
    level = root.get("Level");
    xCache = ((NBTNumeric<Integer>) level.get("xPos")).getPayload();
    zCache = ((NBTNumeric<Integer>) level.get("zPos")).getPayload();
    biomes = level.get("Biomes");
    sections = level.get("Sections");
    sectionCache = new DynamicArray<NBTCompound>(sections.getLength());
    for(final NBTCompound comp : sections) {
      sectionCache.set(getSectionY(comp), comp);
    }
  }

  /**
   * Getter.
   * 
   * @return Gets the position of the map file.
   */
  public ChunkPosition getOtherPos() {
    return otherPos;
  }

  /**
   * Getter.
   * 
   * @return Gets the position of the chunk.
   */
  public OwnChunkPosition getPos() {
    return new OwnChunkPosition(getX(), getZ());
  }

  /**
   * Getter.
   * 
   * @return Gets the associated file.
   */
  public File getFile() {
    return file;
  }

  private final NBTByteArray biomes;

  /**
   * Getter.
   * 
   * @return Gets the biome array.
   */
  protected NBTByteArray getBiomesArray() {
    return biomes;
  }

  private final NBTList<NBTCompound> sections;

  private final DynamicArray<NBTCompound> sectionCache;

  /**
   * Gets a vertical section.
   * 
   * @param y The y position.
   * @return The section corresponding to the y position.
   */
  protected NBTCompound getSection(final int y) {
    return sectionCache.get(y);
  }

  private static int getSectionY(final NBTCompound section) {
    return ((NBTNumeric<Byte>) section.get("Y")).getPayload();
  }

  private static int getBlockPositionInSection(final int x, final int y,
      final int z) {
    return (y << 8) | (z << 4) | x;
  }

  private final Map<NBTCompound, NBTByteArray> cacheBlocks =
      new HashMap<NBTCompound, NBTByteArray>();

  private final Map<NBTCompound, NBTByteArray> cacheAddBlocks =
      new HashMap<NBTCompound, NBTByteArray>();

  private int getBlockInSection(final NBTCompound section, final int x,
      final int y, final int z) {
    if(section == null) return Blocks.AIR.id;
    final int pos = getBlockPositionInSection(x, y, z);
    if(!cacheBlocks.containsKey(section)) {
      cacheBlocks.put(section, (NBTByteArray) section.get("Blocks"));
    }
    final NBTByteArray blocks = cacheBlocks.get(section);
    if(!cacheAddBlocks.containsKey(section)) {
      cacheAddBlocks.put(section, (NBTByteArray) section.get("AddBlocks"));
    }
    final NBTByteArray addBlocks = cacheAddBlocks.get(section);
    return blocks.getAt(pos)
        + (addBlocks != null ? (addBlocks.getAt(pos) << 8) : 0);
  }

  private void setBlockInSection(final NBTCompound section, final int x,
      final int y, final int z, final Blocks b) {
    if(section == null) throw new NullPointerException(
        "section may not be null");
    final int pos = getBlockPositionInSection(x, y, z);
    if(!cacheBlocks.containsKey(section)) {
      cacheBlocks.put(section, (NBTByteArray) section.get("Blocks"));
    }
    final NBTByteArray blocks = cacheBlocks.get(section);
    if(!cacheAddBlocks.containsKey(section)) {
      cacheAddBlocks.put(section, (NBTByteArray) section.get("AddBlocks"));
    }
    final NBTByteArray addBlocks = cacheAddBlocks.get(section);
    final int id = b.id;
    if(id > 0xff && addBlocks == null) throw new UnsupportedOperationException(
        "need to create an AddBlocks");
    blocks.setAt(pos, (byte) id);
    if(addBlocks != null) {
      addBlocks.setAt(pos, (byte) (id >>> 8));
    }
    changeAt(x, z);
  }

  /**
   * Getter.
   * 
   * @param x The x position.
   * @param y The y position.
   * @param z The z position.
   * @return The block at the given position.
   */
  protected int getBlockFor(final int x, final int y, final int z) {
    final int sectionY = y / 16;
    final int inSectionY = y % 16;
    return getBlockInSection(getSection(sectionY), x, inSectionY, z);
  }

  /**
   * Setter.
   * 
   * @param x The x position.
   * @param y The y position.
   * @param z The z position.
   * @param b The block to set at the given position.
   */
  protected void setBlockAt(final int x, final int y, final int z,
      final Blocks b) {
    final int sectionY = y / 16;
    final int inSectionY = y % 16;
    final NBTCompound section = getSection(sectionY);
    if(section == null) throw new UnsupportedOperationException(
        "attempt to create a new section");
    setBlockInSection(section, x, inSectionY, z, b);
  }

  /**
   * Getter.
   * 
   * @param pos The position.
   * @return The block at the given position.
   */
  public Blocks getBlock(final Position3D pos) {
    return getBlock(pos.x, pos.y, pos.z);
  }

  /**
   * Getter.
   * 
   * @param x The x position.
   * @param y The y position.
   * @param z The z position.
   * @return The block at the given position.
   */
  public Blocks getBlock(final int x, final int y, final int z) {
    return Blocks.getBlockForId(getBlockFor(x, y, z));
  }

  /**
   * Setter.
   * 
   * @param pos The position.
   * @param b The block to set at the given position.
   */
  public void setBlock(final Position3D pos, final Blocks b) {
    setBlock(pos.x, pos.y, pos.z, b);
  }

  /**
   * Setter.
   * 
   * @param x The x position.
   * @param y The y position.
   * @param z The z position.
   * @param b The block to set at the given position.
   */
  public void setBlock(final int x, final int y, final int z, final Blocks b) {
    setBlockAt(x, y, z, b);
  }

  /**
   * Gets the topmost non air block. Remember that vines etc. are non air blocks
   * too.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   * @return The Position of the topmost non air block.
   */
  public Position3D getTopNonAirBlock(final int x, final int z) {
    int y = WORLD_HEIGHT;
    while(hasBlockFor(y)) {
      if(getBlock(x, y, z) != Blocks.AIR) return new Position3D(x, y, z);
      --y;
    }
    // actually never really happens
    // but when it does we return an air block
    return new Position3D(x, y, z);
  }

  /**
   * Whether the given coordinate is in the correct range.
   * 
   * @param y The vertical coordinate.
   * @return If the value is in the correct range.
   */
  public boolean hasBlockFor(final int y) {
    return y <= WORLD_HEIGHT && y >= 0;
  }

  /**
   * Whether the corresponding block exists.
   * 
   * @param y The y coordinate.
   * @return Whether there is a block at the given position that can be edited.
   */
  public boolean canSetBlock(final int y) {
    final int sectionY = y / 16;
    return getSection(sectionY) != null && hasBlockFor(y);
  }

  private static int getBiomePosition(final int x, final int z) {
    return (z << 4) | x;
  }

  /**
   * Gets the biome for the given position.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   * @return The biome id.
   */
  protected int getBiomeFor(final int x, final int z) {
    return getBiomesArray().getAt(getBiomePosition(x, z));
  }

  /**
   * Setter.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   * @param biome Sets the biome at the given position.
   */
  public void setBiome(final int x, final int z, final Biomes biome) {
    getBiomesArray().setAt(getBiomePosition(x, z), (byte) biome.id);
    changeAt(x, z);
  }

  /**
   * Gets the biome for the given position.
   * 
   * @param x The x coordinate.
   * @param z The z coordinate.
   * @return The biome.
   */
  public Biomes getBiome(final int x, final int z) {
    return Biomes.getBlockForId(getBiomeFor(x, z));
  }

  private boolean hasChanged;

  private final Color[][] colors = new Color[16][16];

  /**
   * Flags a change in the column of the given position. The flag is removed by
   * {@link #oneTimeHasChanged()}.
   * 
   * @param x The x position.
   * @param z The z position.
   */
  public void changeAt(final int x, final int z) {
    colors[x][z] = null;
    hasChanged = true;
  }

  /**
   * Checks whether the chunk has been changed and resets the change flag
   * afterwards.
   * 
   * @return Whether the chunk has been changed.
   */
  public boolean oneTimeHasChanged() {
    final boolean hc = hasChanged;
    hasChanged = false;
    return hc;
  }

  /**
   * Getter.
   * 
   * @param x The x position.
   * @param z The y position.
   * @return Gets the color for the given column.
   */
  public Color getColorForColumn(final int x, final int z) {
    if(colors[x][z] == null) {
      int y = 0;
      Color res = new Color(0, true);
      while(hasBlockFor(y)) {
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
    if(alpha == 255) return add;
    if(alpha == 0) return old;
    final double a = alpha / 255.0;
    final double am = 1.0 - a;
    final double r = old.getRed() * am + add.getRed() * a;
    final double g = old.getGreen() * am + add.getGreen() * a;
    final double b = old.getBlue() * am + add.getBlue() * a;
    return new Color((int) r, (int) g, (int) b);
  }

  private final int xCache;

  /**
   * Getter.
   * 
   * @return Gets the x position of the chunk.
   */
  public int getXPos() {
    return xCache;
  }

  private final int zCache;

  /**
   * Getter.
   * 
   * @return Gets the z position of the chunk.
   */
  public int getZPos() {
    return zCache;
  }

  /**
   * Getter.
   * 
   * @return Gets the x position of the chunk in blocks.
   */
  public int getX() {
    return getXPos() * 16;
  }

  /**
   * Getter.
   * 
   * @return Gets the z position of the chunk in blocks.
   */
  public int getZ() {
    return getZPos() * 16;
  }

  private boolean active = true;

  /**
   * Unloads the chunk and saves changes to the file.
   */
  public void unload() {
    if(active) {
      if(root.hasChanged()) {
        try {
          final MapReader r = MapReader.getForFile(file);
          r.write(root, otherPos.x, otherPos.z);
        } catch(final IOException e) {
          e.printStackTrace();
        }
      }
      active = false;
    }
  }

  /**
   * Makes the chunk unsaveable. All changes will be ignored when unloading the
   * chunk.
   */
  public void noSave() {
    active = false;
  }

}
