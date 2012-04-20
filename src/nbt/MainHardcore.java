package nbt;

import java.io.File;
import java.io.IOException;

import nbt.world.Player;
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
   * @throws IOException I/O Exception.
   */
  public static void main(final String[] args) throws IOException {
    if(args.length != 1) // TODO: argument parsing
    return;
    // TODO: do the work ;)
    final File file = new File(args[0]);
    final World world = new World(file);
    for(final String player : world.listPlayers()) {
      final Player p = world.getPlayer(player);
      System.out.println("Player: " + player + " Pos: "
          + p.getPosition().getFullRepresentation());
    }
  }

}
