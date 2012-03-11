package nbt.map;

import java.awt.Color;

import nbt.DynamicArray;

public enum Biomes {

    OCEAN(0, "Ocean", Color.BLUE),

    PLAINS(1, "Plains", Color.GREEN),

    DESERT(2, "Desert", Color.YELLOW),

    EXT_HILLS(3, "Extreme Hills", new Color(0x964b00)),

    FOREST(4, "Forest", new Color(0x00a000)),

    TAIGA(5, "Taiga", new Color(0x80a080)),

    SWAMP(6, "Swampland", new Color(0x006000)),

    RIVER(7, "River", new Color(0x8080ff)),

    HELL(8, "Hell", Color.RED),

    SKY(9, "Sky", Color.CYAN),

    FROZEAN(10, "Frozen Ocean", new Color(0x8080ff)),

    FRIVER(11, "Frozen River", new Color(0x80a0a0)),

    ICE_PLAINS(12, "Ice Plains", new Color(0x80ff80)),

    ICE_MOUNT(13, "Ice Mountains", new Color(0x964b00).brighter().brighter()),

    MUSHR(14, "Mushroom Island", new Color(0xffa000)),

    MUSHR_SHORE(15, "Mushroom Island Shore", new Color(0xffa060)),

    BEACH(16, "Beach", new Color(0xffff80)),

    DESERT_HILLS(17, "Desert Hills", new Color(0x808000)),

    FOREST_HILLS(18, "Forest Hills", new Color(0x80ff00)),

    TAIGA_HILLS(19, "Taiga Hills", new Color(0x964b00).brighter()),

    EXT_HILLS_EDGE(20, "Extreme Hills Edge", new Color(0x863b00).brighter()),

    JUNGLE(21, "Jungle", new Color(0x00d000)),

    JUNGLE_HILLS(22, "Jungle Hills", new Color(0x20d020)),

    DEFAULT_UNASSIGNED(-1, "Unassigned", Color.MAGENTA),

    ;

    public final int id;

    public final String name;

    public final Color color;

    private static final DynamicArray<Biomes> biomeMap;

    static {
        final Biomes[] biomes = values();
        biomeMap = new DynamicArray<Biomes>(biomes.length);
        for (final Biomes biome : biomes) {
            if (biome.id < 0) {
                continue;
            }
            if (biomeMap.get(biome.id) != null) {
                throw new InternalError("duplicate biome id: " + biome.id);
            }
            biomeMap.set(biome.id, biome);
        }
    }

    private Biomes(final int id, final String name, final Color color) {
        this.id = id;
        this.name = name;
        this.color = new Color((color.getRGB() & 0x00ffffff) | 0x80000000, true);
    }

    public static Biomes getBlockForId(final int id) {
        if (id < 0) {
            return DEFAULT_UNASSIGNED;
        }
        final Biomes biome = biomeMap.get(id);
        return biome != null ? biome : DEFAULT_UNASSIGNED;
    }

    @Override
    public String toString() {
        return name;
    }

}
