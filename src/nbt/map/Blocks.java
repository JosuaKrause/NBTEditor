package nbt.map;

import java.awt.Color;

import nbt.DynamicArray;

/**
 * Represents blocks. Every block has an attached color to draw the block.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public enum Blocks {

  /** Air block. */
  AIR(0, new Color(0, true)),

  /** Stone block. */
  STONE(1, Color.LIGHT_GRAY),

  /** Grass block. */
  GRASS(2, new Color(0x40d040)),

  /** Dirt block. */
  DIRT(3, new Color(0x964B00)),

  /** Cobblestone block. */
  COBBLE(4, Color.GRAY),

  /** Plank block. */
  PLANK(5, new Color(0x964B00).brighter().brighter()),

  /** Sapling block. */
  SAPLING(6, new Color(0x7020ff20, true)),

  /** Bedrock block. */
  BEDROCK(7, Color.BLACK),

  /** Flowing water block. */
  WATER(8, new Color(0x400000ff, true)),

  /** Water source block. */
  WATER_STAT(9, new Color(0x40000080, true)),

  /** Flowing lava block. */
  LAVA(10, Color.RED),

  /** Lava source block. */
  LAVA_STAT(11, Color.RED),

  /** Sand block. */
  SAND(12, Color.YELLOW),

  /** Gravel block. */
  GRAVEL(13, Color.GRAY),

  /** Gold ore block. */
  GOLD_ORE(14, new Color(0x80ffff00, true)),

  /** Iron ore block. */
  IRON_ORE(15, new Color(0x80a0a0a0, true)),

  /** Coal block. */
  COAL(16, new Color(0x80404040, true)),

  /** Wood block. */
  WOOD(17, new Color(0x964B00).brighter()),

  /** Leaves block. */
  LEAVES(18, new Color(0x4010a010, true)),

  /** Sponge block. */
  SPONGE(19, Color.YELLOW),

  /** Glass block. */
  GLASS(20, new Color(0x40ffffff, true)),

  /** Lapis lazuli ore block. */
  LAPIS_ORE(21, new Color(0x800000ff, true)),

  /** Lapis lazuli block. */
  LAPIS(22, Color.BLUE),

  /** Dispenser block. */
  DISPENSER(23, Color.DARK_GRAY),

  /** Sandstone block. */
  SANDSTONE(24, new Color(0xffff40)),

  /** Note block. */
  NOTE(25, new Color(0x80964B00, true)),

  /** Bed block. */
  BED(26, new Color(0x8000ff00, true)),

  /** Powered rail block. */
  RAIL_POWERED(27, new Color(0x8080ff80, true)),

  /** Detector rail block. */
  RAIL_DETECTOR(28, new Color(0x80808080, true)),

  /** Sticky piston block. */
  PISTON_STICKY(29, new Color(0x80964B00, true)),

  /** Coweb block. */
  WEB(30, new Color(0x20ffffff, true)),

  /** Tall grass block. */
  GRASS_TALL(31, new Color(0x2000ff00, true)),

  /** Dead bush block. */
  DEAD_BUSH(32, new Color(0x20964B00, true)),

  /** Normal piston block. */
  PISTON(33, new Color(0x80964B00, true)),

  /** Block 34. */
  BLOCK34(34, Color.MAGENTA),

  /** White wool block. */
  WOOL_WHITE(35, Color.WHITE),

  /** Technical block. */
  TECHNICAL_BLOCK(36, new Color(0, true)),

  /** Dandelion flower block. */
  DANDELION(37, new Color(0x40ffff00, true)),

  /** Rose flower block. */
  ROSE(38, new Color(0x40ff0000, true)),

  /** Brown mushroom block. */
  MUSHROOM_BROWN(39, new Color(0x40964B00, true).brighter()),

  /** Red mushroom block. */
  MUSHROOM_RED(40, new Color(0x40ff8080, true)),

  /** Gold block. */
  GOLD(41, Color.YELLOW),

  /** Iron block. */
  IRON(42, Color.LIGHT_GRAY),

  /** Double slab block. */
  SLAB_DBL(43, Color.GRAY),

  /** Single slab block. */
  SLAB(44, new Color(0x80808080, true)),

  /** Stone brick block. */
  BRICK(45, new Color(0xd64B00, false).darker()),

  /** TNT block. */
  TNT(46, Color.RED),

  /** Bookshelf block. */
  BOOKS(47, new Color(0x964B00).brighter().brighter()),

  /** Mossy cobblestone block. */
  MOSS(48, new Color(0x80a080)),

  /** Obsidian block. */
  OBSIDIAN(49, new Color(0x202020)),

  /** Torch block. */
  TORCH(50, new Color(0x80ffa500, true)),

  /** Fire block. */
  FIRE(51, new Color(0x80ff8000, true)),

  /** Spawner block. */
  SPAWNER(52, new Color(0x80202020, true)),

  /** Wooden stairs block. */
  STAIRS_WOOD(53, new Color(0x964B00).brighter().brighter()),

  /** Chest block. */
  CHEST(54, new Color(0x964B00).brighter().brighter()),

  /** Redstone wire block. */
  REDSTONE_WIRE(55, new Color(0x70ff0000, true)),

  /** Diamond ore block. */
  DIAMOND_ORE(56, new Color(0x8000ffff, true)),

  /** Diamond block. */
  DIAMOND(57, new Color(0x00ffff)),

  /** Crafting table block. */
  CRAFTING_TABLE(58, new Color(0x964B00).brighter()),

  /** Crops block. */
  CROPS(59, new Color(0x80ffff00, true)),

  /** Farmland block. */
  FARMLAND(60, new Color(0x964B00).darker()),

  /** Furnace block. */
  FURNACE(61, Color.DARK_GRAY),

  /** Burning (active) furnace block. */
  FURNACE_ACTIVE(62, new Color(100, 64, 64)),

  /** Standing sign block. */
  SIGN(63, new Color(0x60964B00, true).brighter().brighter()),

  /** Wooden door block. */
  DOOR_WOOD(64, new Color(0x80964B00, true).brighter().brighter()),

  /** Ladder block. */
  LADDER(65, new Color(0x70964B00, true).brighter().brighter()),

  /** Rail block. */
  RAIL(66, new Color(0x80909090, true)),

  /** Cobblestone stairs block. */
  COBBLE_STAIRS(67, Color.GRAY),

  /** Wall sign block. */
  SIGN_WALL(68, new Color(0x60964B00, true).brighter().brighter()),

  /** Lever block. */
  LEVER(69, new Color(0x60964B00, true).brighter().brighter()),

  /** Stone pressure plate block. */
  PLATE_STONE(70, new Color(0x80a0a0a0, true)),

  /** Iron door block. */
  DOOR_IRON(71, new Color(0x80a0a0a0, true).brighter().brighter()),

  /** Wooden pressure plate block. */
  PLATE_WOOD(72, new Color(0x80964B00, true).brighter().brighter()),

  /** Redstone ore block. */
  REDSTONE_ORE(73, new Color(0x80ff0000, true)),

  /** Active redstone ore block. */
  REDSTONE_ORE_ON(74, new Color(0x90ff0000, true)),

  /** Redstone torch (off) block. */
  REDSTONE_TORCH(75, new Color(0x60ff0000, true)),

  /** Redstone torch (on) block. */
  REDSTONE_TORCH_ON(76, new Color(0x80ff0000, true)),

  /** Button block. */
  BUTTON(77, new Color(0x40a0a0a0, true)),

  /** Ground snow block. */
  SNOW(78, new Color(0xd0ffffff, true)),

  /** Ice block. */
  ICE(79, new Color(0xc000ffff, true)),

  /** Full snow block. */
  SNOW_BLOCK(80, Color.WHITE),

  /** Cactus block. */
  CACTUS(81, new Color(0x00d000)),

  /** Clay block. */
  CLAY(82, Color.LIGHT_GRAY),

  /** Sugar cane block. */
  CANE(83, new Color(0xd060ff60, true)),

  /** Juke-box block. */
  JUKEBOX(84, new Color(0x80964B00, true)),

  /** Wooden fence block. */
  FENCE(85, new Color(0x80964B00, true).brighter().brighter()),

  /** Pumpkin block. */
  PUMPKIN(86, new Color(0xffa400)),

  /** Netherrack block. */
  NETHERRACK(87, new Color(0xe080a0)),

  /** Soulsand block. */
  SOULSAND(88, new Color(0x9080a0)),

  /** Glowstone block. */
  GLOWSTONE(89, new Color(0xffff80)),

  /** Inner nether portal block. */
  PORTAL(90, new Color(0x40800080, true)),

  /** Jack'o'lantern block. */
  PUMPKIN_LIGHT(91, new Color(0xffc440)),

  /** Cake block. */
  CAKE(92, new Color(0x80ffa400, true)),

  /** Redstone repeater (off) block. */
  REDSTONE_REP(93, new Color(0x70ff0000, true)),

  /** Redstone repeater (on) block. */
  REDSTONE_REP_ON(94, new Color(0x80ff0000, true)),

  /** Locked chest block. */
  CHEST_LOCKED(95, new Color(0x964B00).brighter().brighter()),

  /** Trapdoor block. */
  TRAP(96, new Color(0x80964B00, true).brighter().brighter()),

  /** Silverfish spawning stone block. */
  STONE_SILVERFISH(97, Color.LIGHT_GRAY),

  /** Stone brick block. */
  STONE_BRICK(98, Color.GRAY),

  /** Huge brown mushroom block. */
  MUSHROOM_BROWN_HUGE(99, new Color(0x964B00).brighter()),

  /** Huge red mushroom block. */
  MUSHROOM_RED_HUGE(100, new Color(0xff8080)),

  /** Iron bar block. */
  IRON_BAR(101, new Color(0x80a0a0a0, true).brighter().brighter()),

  /** Glass pane block. */
  GLASS_PANE(102, new Color(0x20ffffff, true)),

  /** Melon block. */
  MELON(103, new Color(0x29ff29)),

  /** Pumpkin stem block. */
  STEM_PUMPKIN(104, new Color(0x008070)),

  /** Melon stem block. */
  STEM_MELON(105, new Color(0x008070)),

  /** Vine block. */
  VINE(106, new Color(0x80008000, true)),

  /** Fence gate block. */
  FENCE_GATE(107, new Color(0x80964B00, true).brighter().brighter()),

  /** Brick stairs block. */
  STAIRS_BRICK(108, new Color(0xd64B00, false).darker()),

  /** Stone stairs block. */
  STAIRS_STONE_BRICK(109, Color.GRAY),

  /** Mycellium block. */
  MYCELLIUM(110, new Color(0x964B00)),

  /** Lily pad block. */
  LILY_PAD(111, new Color(0x80008000, true)),

  /** Nether brick block. */
  NETHER_BRICK(112, new Color(0x600000)),

  /** Nether brick fence block. */
  FENCE_NETHER_BRICK(113, new Color(0x80600000, true)),

  /** Nether brick stairs block. */
  STAIRS_NETHER_BRICK(114, new Color(0x600000)),

  /** Nether wart block. */
  NETHER_WART(115, new Color(0x80800045, true)),

  /** Enchantment table block. */
  ENCHANTMENT(116, new Color(0x8000d0d0, true)),

  /** Brewing stand block. */
  BREWING(117, new Color(0x80d00000, true)),

  /** Cauldron block. */
  CAULDRON(118, new Color(0x80707070, true)),

  /** Inner end portal block. */
  PORTAL_END(119, new Color(0x80000000, true)),

  /** End portal frame block. */
  PORTAL_END_FRAME(120, new Color(0x00d0d0)),

  /** End stone block. */
  STONE_END(121, new Color(0xa0a070)),

  /** Dragon egg block. */
  DRAGON_EGG(122, new Color(0x80202020, true)),

  /** Redstone lamp (off) block. */
  REDSTONE_LAMP(123, new Color(0x408080)),

  /** Redstone lamp (on) block. */
  REDSTONE_LAMP_ON(124, new Color(0x40ffff)),

  /**
   * Signals that a block id is currently unassigned.
   */
  DEFAULT_UNASSIGNED(-1, Color.MAGENTA),

  /* end of declaration */;

  /** The block id. */
  public final int id;

  /** The color to draw the block. */
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

  /**
   * Getter.
   * 
   * @param id The block id.
   * @return The corresponding block.
   */
  public static Blocks getBlockForId(final int id) {
    if(id < 0) return DEFAULT_UNASSIGNED;
    final Blocks block = BLOCK_MAP.get(id);
    return block != null ? block : DEFAULT_UNASSIGNED;
  }

}
