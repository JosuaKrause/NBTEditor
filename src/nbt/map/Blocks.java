package nbt.map;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public enum Blocks {

    AIR(0, new Color(0, true)),

    STONE(1, Color.LIGHT_GRAY),

    GRASS(2, new Color(0x40d040)),

    DIRT(3, new Color(0x964B00)),

    COBBLE(4, Color.GRAY),

    PLANK(5, new Color(0x964B00).brighter().brighter()),

    SAPLING(6, new Color(0x7020ff20, true)),

    BEDROCK(7, Color.BLACK),

    WATER(8, new Color(0x400000ff, true)),

    STAT_WATER(9, new Color(0x40000080, true)),

    LAVA(10, Color.RED),

    STAT_LAVA(11, Color.RED),

    SAND(12, Color.YELLOW),

    GRAVEL(13, Color.GRAY),

    GOLD_ORE(14, new Color(0x8000ffff, true)),

    IRON_ORE(15, new Color(0x80a0a0a0, true)),

    COAL(16, new Color(0x80404040, true)),

    WOOD(17, new Color(0x964B00).brighter()),

    LEAVES(18, new Color(0x4010a010, true)),

    SPONGE(19, Color.YELLOW),

    GLASS(20, new Color(0x40ffffff, true)),

    LAPIS_ORE(21, new Color(0x800000ff, true)),

    LAPIS(22, Color.BLUE),

    DISPENSER(23, Color.DARK_GRAY),

    SANDSTONE(24, new Color(0x40ffff)),

    NOTE(25, new Color(0x80964B00, true)),

    BED(26, new Color(0x8000ff00, true)),

    POWERED_RAIL(27, new Color(0x8080ff80, true)),

    DETECTOR_RAIL(28, new Color(0x80808080, true)),

    STICKY_PISTON(29, new Color(0x80964B00, true)),

    WEB(30, new Color(0x20ffffff, true)),

    TALL_GRASS(31, new Color(0x2000ff00, true)),

    DEAD_BUSH(32, new Color(0x20964B00, true)),

    PISTON(33, new Color(0x80964B00, true)),

    BLOCK34(34, Color.MAGENTA),

    WHITE_WOOL(35, Color.WHITE),

    TECHNICAL_BLOCK(36, new Color(0, true)),

    DANDELION(37, new Color(0x4000ffff, true)),

    ROSE(38, new Color(0x40ff0000, true)),

    MUSHROOM_BROWN(39, new Color(0x40964B00, true).brighter()),

    MUSHROOM_RED(40, new Color(0x40ff8080, true)),

    GOLD(41, Color.YELLOW),

    IRON(42, Color.LIGHT_GRAY),

    DBL_SLAB(43, Color.GRAY),

    SLAB(44, new Color(0x80808080, true)),

    BRICK(45, new Color(0xd64B00, false).darker()),

    TNT(46, Color.RED),

    BOOKS(47, new Color(0x964B00).brighter().brighter()),

    MOSS(48, new Color(0x80a080)),

    OBSIDIAN(49, new Color(0x202020)),

    TORCH(50, new Color(0x80ffa500, true)),

    FIRE(51, new Color(0x80ff8000, true)),

    SPAWNER(52, new Color(0x80202020, true)),

    WOOD_STAIRS(53, new Color(0x964B00).brighter().brighter()),

    CHEST(54, new Color(0x964B00).brighter().brighter()),

    REDSTONE_WIRE(55, new Color(0x80ff0000, true)),

    DIAMOND_ORE(56, new Color(0x8000ffff, true)),

    DIAMOND(57, new Color(0x00ffff)),

    CRAFTING(58, new Color(0x964B00).brighter()),

    CROPS(59, new Color(0x8000ffff, true)),

    FARMLAND(60, new Color(0x964B00).darker()),

    FURNACE(61, Color.DARK_GRAY),

    FURNACE_ACTIVE(62, new Color(100, 64, 64)),

    SIGN(63, new Color(0x60964B00, true).brighter().brighter()),

    WOOD_DOOR(64, new Color(0x80964B00, true).brighter().brighter()),

    LADDER(65, new Color(0x70964B00, true).brighter().brighter()),

    RAIL(66, new Color(0x80909090, true)),

    COBBLE_STAIRS(67, Color.GRAY),

    SIGN_WALL(68, new Color(0x60964B00, true).brighter().brighter()),

    LEVER(69, new Color(0x60964B00, true).brighter().brighter()),

    STONE_PLATE(70, new Color(0x80a0a0a0, true)),

    IRON_DOOR(71, new Color(0x80a0a0a0, true).brighter().brighter()),

    // Wooden Pressure Plate.png 72 48 Wooden Pressure Plate D
    // Redstone (Ore).png 73 49 Redstone Ore
    // Redstone (Ore).png 74 4A Glowing Redstone Ore
    // Redstone (Torch, Inactive).png 75 4B Redstone Torch ("off" state) D
    // Redstone (Torch, Active).png 76 4C Redstone Torch ("on" state) D
    // Stone Button.png 77 4D Stone Button D

    SNOW(78, new Color(0xc0ffffff, true)),

    ICE(79, Color.CYAN),

    SNOW_BLOCK(80, Color.WHITE),

    // Cactus.png 81 51 Cactus D
    // Clay (Block).png 82 52 Clay Block
    // Sugar Cane.png 83 53 Sugar Cane D I
    // Jukebox.png 84 54 Jukebox D T
    // Fence.png 85 55 Fence
    // Pumpkin.png 86 56 Pumpkin D
    // Netherrack.png 87 57 Netherrack
    // Soul Sand.png 88 58 Soul Sand
    // Glowstone (Block).png 89 59 Glowstone Block
    // Portal.png 90 5A Portal
    // Jack-O-Lantern.png 91 5B Jack-O-Lantern D
    // Cake.png 92 5C Cake Block D I
    // Redstone (Repeater, Inactive).png 93 5D Redstone Repeater ("off" state) D
    // I
    // Redstone (Repeater, Active).png 94 5E Redstone Repeater ("on" state) D I
    // Locked Chest.png 95 5F Locked Chest
    // Trapdoor.png 96 60 Trapdoor D
    // Stone.png 97 61 Hidden Silverfish D
    // Stone Brick.png 98 62 Stone Bricks D B
    // Huge Brown Mushroom.png 99 63 Huge Brown Mushroom D
    // Huge Red Mushroom.png 100 64 Huge Red Mushroom D
    // Iron Bars.png 101 65 Iron Bars
    // Glass Pane.png 102 66 Glass Pane
    // Melon (Block).png 103 67 Melon
    // Seed Stem.png 104 68 Pumpkin Stem D
    // Seed Stem.png 105 69 Melon Stem D
    // Vine.png 106 6A Vines D
    // Fence Gate (Closed).png 107 6B Fence Gate D
    // Brick Stairs.png 108 6C Brick Stairs D
    // Stone Brick Stairs.png 109 6D Stone Brick Stairs D
    // Mycelium.png 110 6E Mycelium
    // Lily Pad.png 111 6F Lily Pad
    // Nether Brick.png 112 70 Nether Brick
    // Nether Brick Fence.png 113 71 Nether Brick Fence
    // Nether Brick Stairs.png 114 72 Nether Brick Stairs D
    // Nether Wart.png 115 73 Nether Wart D I
    // Enchantment Table.png 116 74 Enchantment Table T
    // Brewing Stand.png 117 75 Brewing Stand D T I
    // Cauldron.png 118 76 Cauldron D I
    // End Portal.png 119 77 End Portal T
    // End Portal Frame.png 120 78 End Portal Frame D
    // End Stone.png 121 79 End Stone
    // Dragon Egg.png 122 7A Dragon Egg
    // Redstone Lamp.png 123 7B Redstone Lamp (inactive)
    // Redstone Lamp (Active).png 124 7C Redstone Lamp (active)

    DEFAULT_UNASSIGNED(-1, Color.MAGENTA),

    ;

    public final int id;

    public final Color color;

    private static final Map<Integer, Blocks> blockMap = new HashMap<Integer, Blocks>();

    static {
        for (final Blocks block : values()) {
            if (blockMap.containsKey(block.id)) {
                throw new InternalError("duplicate block id: " + block.id);
            }
            blockMap.put(block.id, block);
        }
    }

    private Blocks(final int id, final Color color) {
        this.id = id;
        this.color = color;
    }

    public static Blocks getBlockForId(final int id) {
        return blockMap.containsKey(id) ? blockMap.get(id) : DEFAULT_UNASSIGNED;
    }

}
