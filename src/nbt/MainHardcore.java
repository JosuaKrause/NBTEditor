package nbt;

import java.io.File;
import java.io.IOException;

import nbt.record.NBTList;
import nbt.record.NBTNumeric;
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

  private static void usageAndDie() {
    System.err.println("Usage: [-r <radius>] [-b <border>] [-sp] <world>");
    System.err.println("-r: <radius> defines the area where the hardcore game is played");
    System.err.println("-b: <border> defines the border of the game area");
    System.err.println("-s: sets the spawn to a place outside of the game area");
    System.err.println("-p: places the players randomly in the game area");
    System.exit(1);
  }

  /**
   * Converts a map to an ultra hardcore map.
   * 
   * @param args With {@code -r} and {@code -b} the size of the game area can be
   *          set. The world folder must be passed. With {@code -s} the spawn
   *          will be set outside of the game area. With {@code -p} all already
   *          present players will be placed randomly in the game area.
   */
  public static void main(final String[] args) {
    try {
      int opt = 0;
      File world = null;
      boolean setPlayer = false;
      boolean resetSpawn = false;
      int radius = 700;
      int border = 80;
      for(int i = 0; i < args.length; ++i) {
        final String a = args[i];
        if(a.startsWith("-")) {
          if(opt != 0) {
            usageAndDie();
          }
          if(a.contains("r")) {
            opt = 1;
          } else if(a.contains("b")) {
            opt = 2;
          }
          if(a.contains("p")) {
            setPlayer = true;
          }
          if(a.contains("s")) {
            resetSpawn = true;
          }
        } else {
          switch(opt) {
            case 0:
              if(world != null) {
                usageAndDie();
              }
              world = new File(a);
              break;
            case 1: // r
              radius = Integer.valueOf(a);
              break;
            case 2: // b
              border = Integer.valueOf(a);
              break;
          }
          opt = 0;
        }
      }
      final World w = new World(world);
      createBorder(w, radius, border);
      if(setPlayer) {
        setPlayers(w, radius);
      }
      if(resetSpawn) {
        resetSpawn(w, radius);
      }
    } catch(final Exception e) {
      e.printStackTrace();
      usageAndDie();
    }
  }

  @SuppressWarnings("unused")
  private static void resetSpawn(final World w, final int radius) {
    // TODO
    System.err.println("resetting spawn not yet implemented");
  }

  private static double random(final int rad) {
    return Math.random() * rad * 2 - rad;
  }

  private static void setPlayers(final World w, final int radius)
      throws IOException {
    for(final String player : w.listPlayers()) {
      final Player p = w.getPlayer(player);
      final NBTList<NBTNumeric<Double>> pos = p.getPosition();
      final NBTNumeric<Double> x = pos.getAt(0);
      final NBTNumeric<Double> z = pos.getAt(2);
      x.setPayload(random(radius));
      z.setPayload(random(radius));
      p.save();
    }
  }

  @SuppressWarnings("unused")
  private static void createBorder(final World w, final int radius,
      final int border) {
    // SerialChunkManager ow = w.getOverworld();
    // Chunk lastChunk = null;
    // TODO:
  }

}
