package nbt.map;

import java.awt.Color;

import nbt.DynamicArray;

/**
 * Represents the biome data of chunks.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public enum Biomes {

  /** Ocean biome. */
  OCEAN(0, "Ocean", Color.BLUE),

  /** Plains biome. */
  PLAINS(1, "Plains", Color.GREEN),

  /** Desert biome. */
  DESERT(2, "Desert", Color.YELLOW),

  /** Extreme hills biome. */
  EXT_HILLS(3, "Extreme Hills", new Color(0x964b00)),

  /** Forest biome. */
  FOREST(4, "Forest", new Color(0x00a000)),

  /** Taiga biome. */
  TAIGA(5, "Taiga", new Color(0x80a080)),

  /** Swamp biome. */
  SWAMP(6, "Swampland", new Color(0x006000)),

  /** River biome. */
  RIVER(7, "River", new Color(0x8080ff)),

  /** Nether biome. */
  HELL(8, "Hell", Color.RED),

  /** End biome. */
  SKY(9, "Sky", Color.CYAN),

  /** Frozen ocean biome. */
  FROZEAN(10, "Frozen Ocean", new Color(0x8080ff)),

  /** Frozen river biome. */
  FRIVER(11, "Frozen River", new Color(0x80a0a0)),

  /** Ice plains biome. */
  ICE_PLAINS(12, "Ice Plains", new Color(0x80ff80)),

  /** Ice mountains biome. */
  ICE_MOUNT(13, "Ice Mountains", new Color(0x964b00).brighter().brighter()),

  /** Mushroom island biome. */
  MUSHR(14, "Mushroom Island", new Color(0xffa000)),

  /** Mushroom island shore biome. */
  MUSHR_SHORE(15, "Mushroom Island Shore", new Color(0xffa060)),

  /** Beach biome. */
  BEACH(16, "Beach", new Color(0xffff80)),

  /** Desert hills biome. */
  DESERT_HILLS(17, "Desert Hills", new Color(0x808000)),

  /** Forest hills biome. */
  FOREST_HILLS(18, "Forest Hills", new Color(0x80ff00)),

  /** Taiga hills biome. */
  TAIGA_HILLS(19, "Taiga Hills", new Color(0x964b00).brighter()),

  /** Extreme hills edge biome. */
  EXT_HILLS_EDGE(20, "Extreme Hills Edge", new Color(0x863b00).brighter()),

  /** Jungle biome. */
  JUNGLE(21, "Jungle", new Color(0x00d000)),

  /** Jungle hills biome. */
  JUNGLE_HILLS(22, "Jungle Hills", new Color(0x20d020)),

  /**
   * Signals that a biome id is currently unassigned.
   */
  DEFAULT_UNASSIGNED(-1, "Unassigned", Color.MAGENTA),

  /* end of declaration */;

  /**
   * The id of the biome.
   */
  public final int id;

  /**
   * The name of the biome.
   */
  public final String name;

  /**
   * The color of the biome as in the overlay.
   */
  public final Color color;

  private static final DynamicArray<Biomes> BIOME_MAP;

  static {
    final Biomes[] biomes = values();
    BIOME_MAP = new DynamicArray<Biomes>(biomes.length);
    for(final Biomes biome : biomes) {
      if(biome.id < 0) {
        continue;
      }
      if(BIOME_MAP.get(biome.id) != null) throw new InternalError(
          "duplicate biome id: " + biome.id);
      BIOME_MAP.set(biome.id, biome);
    }
  }

  private Biomes(final int id, final String name, final Color color) {
    this.id = id;
    this.name = name;
    this.color = new Color((color.getRGB() & 0x00ffffff) | 0x80000000, true);
  }

  /**
   * Getter.
   * 
   * @param id The biome id.
   * @return Gets the biome for the given id.
   */
  public static Biomes getBlockForId(final int id) {
    if(id < 0) return DEFAULT_UNASSIGNED;
    final Biomes biome = BIOME_MAP.get(id);
    return biome != null ? biome : DEFAULT_UNASSIGNED;
  }

  @Override
  public String toString() {
    return name;
  }

}
