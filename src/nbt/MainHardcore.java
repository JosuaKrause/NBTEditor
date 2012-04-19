package nbt;

import java.io.File;

import nbt.map.Chunk;
import nbt.map.Chunk.Position;
import nbt.map.Pair;
import nbt.map.SerialChunkManager;
import nbt.world.World;

/**
 * A command line interface for creating a ultra hardcore world.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class MainHardcore {

  private MainHardcore() {
    // no constructor
  }

  /**
   * Converts a map to an ultra hardcore map.
   * 
   * @param args With {@code -r} the size of the game area can be set. The world
   *          folder must be passed. With {@code -s} the spawn will be set
   *          outside of the game area. With {@code -p} all already present
   *          players will be placed randomly in the game area.
   */
  public static void main(final String[] args) {
    if(args.length != 1) // TODO: argument parsing
    return;
    // TODO: do the work ;)
    final File file = new File(args[0]);
    final World world = new World(file);
    final SerialChunkManager manager = world.getOverworld();
    final Chunk c = manager.getChunk(-263, 287);
    final Pair pos = manager.getPosInChunk(-263, 287);
    final Position b = c.getTopNonAirBlock(pos.x, pos.z);
    System.out.println(c.getBlock(b));
    manager.unloadChunk(c);
  }

}
