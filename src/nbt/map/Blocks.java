package nbt.map;

import java.awt.Color;

import nbt.DynamicArray;

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

  WATER_STAT(9, new Color(0x40000080, true)),

  LAVA(10, Color.RED),

  LAVA_STAT(11, Color.RED),

  SAND(12, Color.YELLOW),

  GRAVEL(13, Color.GRAY),

  GOLD_ORE(14, new Color(0x80ffff00, true)),

  IRON_ORE(15, new Color(0x80a0a0a0, true)),

  COAL(16, new Color(0x80404040, true)),

  WOOD(17, new Color(0x964B00).brighter()),

  LEAVES(18, new Color(0x4010a010, true)),

  SPONGE(19, Color.YELLOW),

  GLASS(20, new Color(0x40ffffff, true)),

  LAPIS_ORE(21, new Color(0x800000ff, true)),

  LAPIS(22, Color.BLUE),

  DISPENSER(23, Color.DARK_GRAY),

  SANDSTONE(24, new Color(0xffff40)),

  NOTE(25, new Color(0x80964B00, true)),

  BED(26, new Color(0x8000ff00, true)),

  RAIL_POWERED(27, new Color(0x8080ff80, true)),

  RAIL_DETECTOR(28, new Color(0x80808080, true)),

  PISTON_STICKY(29, new Color(0x80964B00, true)),

  WEB(30, new Color(0x20ffffff, true)),

  GRASS_TALL(31, new Color(0x2000ff00, true)),

  DEAD_BUSH(32, new Color(0x20964B00, true)),

  PISTON(33, new Color(0x80964B00, true)),

  BLOCK34(34, Color.MAGENTA),

  WOOL_WHITE(35, Color.WHITE),

  TECHNICAL_BLOCK(36, new Color(0, true)),

  DANDELION(37, new Color(0x40ffff00, true)),

  ROSE(38, new Color(0x40ff0000, true)),

  MUSHROOM_BROWN(39, new Color(0x40964B00, true).brighter()),

  MUSHROOM_RED(40, new Color(0x40ff8080, true)),

  GOLD(41, Color.YELLOW),

  IRON(42, Color.LIGHT_GRAY),

  SLAB_DBL(43, Color.GRAY),

  SLAB(44, new Color(0x80808080, true)),

  BRICK(45, new Color(0xd64B00, false).darker()),

  TNT(46, Color.RED),

  BOOKS(47, new Color(0x964B00).brighter().brighter()),

  MOSS(48, new Color(0x80a080)),

  OBSIDIAN(49, new Color(0x202020)),

  TORCH(50, new Color(0x80ffa500, true)),

  FIRE(51, new Color(0x80ff8000, true)),

  SPAWNER(52, new Color(0x80202020, true)),

  STAIRS_WOOD(53, new Color(0x964B00).brighter().brighter()),

  CHEST(54, new Color(0x964B00).brighter().brighter()),

  REDSTONE_WIRE(55, new Color(0x70ff0000, true)),

  DIAMOND_ORE(56, new Color(0x8000ffff, true)),

  DIAMOND(57, new Color(0x00ffff)),

  CRAFTING_TABLE(58, new Color(0x964B00).brighter()),

  CROPS(59, new Color(0x80ffff00, true)),

  FARMLAND(60, new Color(0x964B00).darker()),

  FURNACE(61, Color.DARK_GRAY),

  FURNACE_ACTIVE(62, new Color(100, 64, 64)),

  SIGN(63, new Color(0x60964B00, true).brighter().brighter()),

  DOOR_WOOD(64, new Color(0x80964B00, true).brighter().brighter()),

  LADDER(65, new Color(0x70964B00, true).brighter().brighter()),

  RAIL(66, new Color(0x80909090, true)),

  COBBLE_STAIRS(67, Color.GRAY),

  SIGN_WALL(68, new Color(0x60964B00, true).brighter().brighter()),

  LEVER(69, new Color(0x60964B00, true).brighter().brighter()),

  PLATE_STONE(70, new Color(0x80a0a0a0, true)),

  DOOR_IRON(71, new Color(0x80a0a0a0, true).brighter().brighter()),

  PLATE_WOOD(72, new Color(0x80964B00, true).brighter().brighter()),

  REDSTONE_ORE(73, new Color(0x80ff0000, true)),

  REDSTONE_ORE_ON(74, new Color(0x90ff0000, true)),

  REDSTONE_TORCH(75, new Color(0x60ff0000, true)),

  REDSTONE_TORCH_ON(76, new Color(0x80ff0000, true)),

  BUTTON(77, new Color(0x40a0a0a0, true)),

  SNOW(78, new Color(0xd0ffffff, true)),

  ICE(79, new Color(0xc000ffff, true)),

  SNOW_BLOCK(80, Color.WHITE),

  CACTUS(81, new Color(0x00d000)),

  CLAY(82, Color.LIGHT_GRAY),

  CANE(83, new Color(0xd060ff60, true)),

  JUKEBOX(84, new Color(0x80964B00, true)),

  FENCE(85, new Color(0x80964B00, true).brighter().brighter()),

  PUMPKIN(86, new Color(0xffa400)),

  NETHERRACK(87, new Color(0xe080a0)),

  SOULSAND(88, new Color(0x9080a0)),

  GLOWSTONE(89, new Color(0xffff80)),

  PORTAL(90, new Color(0x40800080, true)),

  PUMPKIN_LIGHT(91, new Color(0xffc440)),

  CAKE(92, new Color(0x80ffa400, true)),

  REDSTONE_REP(93, new Color(0x70ff0000, true)),

  REDSTONE_REP_ON(94, new Color(0x80ff0000, true)),

  CHEST_LOCKED(95, new Color(0x964B00).brighter().brighter()),

  TRAP(96, new Color(0x80964B00, true).brighter().brighter()),

  STONE_SILVERFISH(97, Color.LIGHT_GRAY),

  STONE_BRICK(98, Color.GRAY),

  MUSHROOM_BROWN_HUGE(99, new Color(0x964B00).brighter()),

  MUSHROOM_RED_HUGE(100, new Color(0xff8080)),

  IRON_BAR(101, new Color(0x80a0a0a0, true).brighter().brighter()),

  GLASS_PANE(102, new Color(0x20ffffff, true)),

  MELON(103, new Color(0x29ff29)),

  STEM_PUMPKIN(104, new Color(0x008070)),

  STEM_MELON(105, new Color(0x008070)),

  VINE(106, new Color(0x80008000, true)),

  FENCE_GATE(107, new Color(0x80964B00, true).brighter().brighter()),

  STAIRS_BRICK(108, new Color(0xd64B00, false).darker()),

  STAIRS_STONE_BRICK(109, Color.GRAY),

  MYCELLIUM(110, new Color(0x964B00)),

  LILY_PAD(111, new Color(0x80008000, true)),

  NETHER_BRICK(112, new Color(0x600000)),

  FENCE_NETHER_BRICK(113, new Color(0x80600000, true)),

  STAIRS_NETHER_BRICK(114, new Color(0x600000)),

  NETHER_WART(115, new Color(0x80800045, true)),

  ENCHANTMENT(116, new Color(0x8000d0d0, true)),

  BREWING(117, new Color(0x80d00000, true)),

  CAULDRON(118, new Color(0x80707070, true)),

  PORTAL_END(119, new Color(0x80000000, true)),

  PORTAL_END_FRAME(120, new Color(0x00d0d0)),

  STONE_END(121, new Color(0xa0a070)),

  DRAGON_EGG(122, new Color(0x80202020, true)),

  REDSTONE_LAMP(123, new Color(0x408080)),

  REDSTONE_LAMP_ON(124, new Color(0x40ffff)),

  DEFAULT_UNASSIGNED(-1, Color.MAGENTA),

  /* end of declaration */;

  public final int id;

  public final Color color;

  private static final DynamicArray<Blocks> BLOCK_MAP;

  static {
    final Blocks[] blocks = values();
    BLOCK_MAP = new DynamicArray<Blocks>(blocks.length);
    for(final Blocks block : blocks) {
      if(block.id < 0) {
        continue;
      }
      if(BLOCK_MAP.get(block.id) != null) throw new InternalError(
          "duplicate block id: " + block.id);
      BLOCK_MAP.set(block.id, block);
    }
  }

  private Blocks(final int id, final Color color) {
    this.id = id;
    this.color = color;
  }

  public static Blocks getBlockForId(final int id) {
    if(id < 0) return DEFAULT_UNASSIGNED;
    final Blocks block = BLOCK_MAP.get(id);
    return block != null ? block : DEFAULT_UNASSIGNED;
  }

}
