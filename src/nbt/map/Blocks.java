package nbt.map;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public enum Blocks {

    AIR(0, new Color(0, true)),

    STONE(1, Color.GRAY),

    GRASS(2, Color.GREEN),

    DIRT(3, new Color(0x964B00)),

    BEDROCK(7, Color.BLACK),

    WATER(8, Color.BLUE),

    STAT_WATER(9, Color.BLUE),

    LAVA(10, Color.RED),

    STAT_LAVA(11, Color.RED),

    SAND(12, Color.YELLOW),

    GLASS(20, new Color(0x40ffffff, true)),

    SNOW(78, Color.WHITE),

    ICE(79, Color.CYAN),

    SNOW_BLOCK(80, Color.WHITE),

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
