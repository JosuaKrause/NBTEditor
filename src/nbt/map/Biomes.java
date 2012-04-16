package nbt.map;

import nbt.DynamicArray;

/**
 * Represents the biome data of chunks.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public enum Biomes {

  /** Ocean biome. */
  OCEAN(0, "Ocean"),

  /** Plains biome. */
  PLAINS(1, "Plains"),

  /** Desert biome. */
  DESERT(2, "Desert"),

  /** Extreme hills biome. */
  EXT_HILLS(3, "Extreme Hills"),

  /** Forest biome. */
  FOREST(4, "Forest"),

  /** Taiga biome. */
  TAIGA(5, "Taiga"),

  /** Swamp biome. */
  SWAMP(6, "Swampland"),

  /** River biome. */
  RIVER(7, "River"),

  /** Nether biome. */
  HELL(8, "Hell"),

  /** End biome. */
  SKY(9, "Sky"),

  /** Frozen ocean biome. */
  FROZEAN(10, "Frozen Ocean"),

  /** Frozen river biome. */
  FRIVER(11, "Frozen River"),

  /** Ice plains biome. */
  ICE_PLAINS(12, "Ice Plains"),

  /** Ice mountains biome. */
  ICE_MOUNT(13, "Ice Mountains"),

  /** Mushroom island biome. */
  MUSHR(14, "Mushroom Island"),

  /** Mushroom island shore biome. */
  MUSHR_SHORE(15, "Mushroom Island Shore"),

  /** Beach biome. */
  BEACH(16, "Beach"),

  /** Desert hills biome. */
  DESERT_HILLS(17, "Desert Hills"),

  /** Forest hills biome. */
  FOREST_HILLS(18, "Forest Hills"),

  /** Taiga hills biome. */
  TAIGA_HILLS(19, "Taiga Hills"),

  /** Extreme hills edge biome. */
  EXT_HILLS_EDGE(20, "Extreme Hills Edge"),

  /** Jungle biome. */
  JUNGLE(21, "Jungle"),

  /** Jungle hills biome. */
  JUNGLE_HILLS(22, "Jungle Hills"),

  /**
   * Signals that a biome id is currently unassigned.
   */
  DEFAULT_UNASSIGNED(-1, "Unassigned"),

  /* end of declaration */;

  /**
   * The id of the biome.
   */
  public final int id;

  /**
   * The name of the biome.
   */
  public final String name;

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

  private Biomes(final int id, final String name) {
    this.id = id;
    this.name = name;
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
