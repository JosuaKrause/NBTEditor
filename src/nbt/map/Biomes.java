package nbt.map;

import nbt.DynamicArray;

public enum Biomes {

    OCEAN(0, "Ocean"),

    PLAINS(1, "Plains"),

    DESERT(2, "Desert"),

    EXT_HILLS(3, "Extreme Hills"),

    FOREST(4, "Forest"),

    TAIGA(5, "Taiga"),

    SWAMP(6, "Swampland"),

    RIVER(7, "River"),

    HELL(8, "Hell"),

    SKY(9, "Sky"),

    FROZEAN(10, "Frozen Ocean"),

    FRIVER(11, "Frozen River"),

    ICE_PLAINS(12, "Ice Plains"),

    ICE_MOUNT(13, "Ice Mountains"),

    MUSHR(14, "Mushroom Island"),

    MUSHR_SHORE(15, "Mushroom Island Shore"),

    BEACH(16, "Beach"),

    DESERT_HILLS(17, "Desert Hills"),

    FOREST_HILLS(18, "Forest Hills"),

    TAIGA_HILLS(19, "Taiga Hills"),

    EXT_HILLS_EDGE(20, "Extreme Hills Edge"),

    JUNGLE(21, "Jungle"),

    JUNGLE_HILLS(22, "Jungle Hills"),

    DEFAULT_UNASSIGNED(-1, "Unassigned"),

    ;

    public final int id;

    public final String name;

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

    private Biomes(final int id, final String name) {
        this.id = id;
        this.name = name;
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
