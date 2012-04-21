package nbt;

import java.io.File;
import java.io.IOException;

import nbt.map.Biomes;
import nbt.map.Blocks;
import nbt.map.Chunk;
import nbt.map.SerialChunkManager;
import nbt.map.pos.GamePosition;
import nbt.map.pos.InChunkPosition;
import nbt.map.pos.Position3D;
import nbt.map.pos.WorldPosition;
import nbt.world.Level;
import nbt.world.Player;
import nbt.world.World;
import nbt.world.World.GameType;
import nbt.world.World.WorldDimension;

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
    System.err.println("Usage: [-r <radius>] [-b <border>] [-spng] <world>");
    System.err.println("-r: <radius> defines the area where the hardcore game is played");
    System.err.println("-b: <border> defines the border of the game area");
    System.err.println("-s: sets the spawn to a place outside of the game area");
    System.err.println("-p: places the players randomly in the game area");
    System.err.println("-n: creates no border");
    System.err.println("-g: sets the game type to survival");
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
      boolean noBorder = false;
      boolean survival = false;
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
          if(a.contains("n")) {
            noBorder = true;
          }
          if(a.contains("g")) {
            survival = true;
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
      if(survival) {
        System.err.println("[info] setting game type to survival");
        w.getLevelDat().setGameType(GameType.SURVIVAL);
      }
      if(setPlayer) {
        System.err.println("[info] setting player positions");
        setPlayers(w, radius, survival);
      }
      if(resetSpawn) {
        System.err.println("[info] resetting spawn");
        resetSpawn(w, radius, border);
      }
      if(!noBorder) {
        System.err.println("[info] creating border");
        createBorder(w, radius, border);
      }
      System.err.println("[info] finished");
    } catch(final Exception e) {
      e.printStackTrace();
      usageAndDie();
    }
  }

  private static void resetSpawn(final World w, final int radius,
      final int border) throws IOException {
    final int spawn = Math.max(1000000, (radius + border) * 2);
    final WorldPosition wp = new WorldPosition(spawn, 0);
    final Level dat = w.getLevelDat();
    dat.setSpawn(new GamePosition(wp, w, WorldDimension.OVERWORLD).spawnOnTop());
    dat.save();
  }

  private static double random(final int rad) {
    return Math.random() * rad * 2 - rad;
  }

  private static void setPlayers(final World w, final int radius,
      final boolean survival)
      throws IOException {
    for(final Player p : w.getPlayers()) {
      System.err.println("[info] setting position of player " + p.getName());
      final double x = random(radius);
      final double z = random(radius);
      final WorldPosition wp = new WorldPosition((int) x, (int) z);
      p.setDimension(WorldDimension.OVERWORLD);
      final GamePosition gp = new GamePosition(wp, w, WorldDimension.OVERWORLD);
      p.setPosition(gp.playerOnTop());
      if(survival && !p.isSinglePlayer()) {
        p.setGameType(GameType.SURVIVAL);
      }
      p.save();
    }
  }

  private static void createBorder(final World w, final int radius,
      final int border) {
    final SerialChunkManager ow = w.getOverworld();
    for(int b = 0; b < border; ++b) {
      round(ow, radius + b);
    }
  }

  private static void round(final SerialChunkManager ow, final int dist) {
    Chunk lastChunk = null;
    lastChunk = row(ow, dist, true, true, lastChunk);
    lastChunk = row(ow, dist, false, true, lastChunk);
    lastChunk = row(ow, dist, true, false, lastChunk);
    lastChunk = row(ow, dist, false, false, lastChunk);
    if(lastChunk != null) {
      ow.unloadChunk(lastChunk);
    }
  }

  private static Chunk row(final SerialChunkManager ow, final int dist,
      final boolean hor, final boolean positive, final Chunk lc) {
    Chunk lastChunk = lc;
    final int a = positive ? dist : -dist;
    for(int t = -dist; t <= dist; ++t) {
      final WorldPosition pos = new WorldPosition(hor ? t : a, hor ? a : t);
      final Chunk chunk = ow.getChunk(pos);
      if(chunk != lastChunk) {
        if(lastChunk != null) {
          ow.unloadChunk(lastChunk);
          if(chunk == null) {
            System.err.println("[warning] not yet created chunk");
          }
        }
        lastChunk = chunk;
      }
      if(chunk != null) {
        editColumn(chunk, pos.getPosInChunk());
      }
    }
    return lastChunk;
  }

  private static final int THRESHOLD = 65;

  private static void editColumn(final Chunk chunk, final InChunkPosition pos) {
    for(int y = 0; y <= Chunk.WORLD_MAX_Y; ++y) {
      if(!chunk.canEdit(y)) {
        continue;
      }
      chunk.setBlock(new Position3D(pos, y), y > THRESHOLD
          ? Blocks.AIR
          : Blocks.WATER_STAT);
    }
    chunk.setBiome(pos, Biomes.OCEAN);
  }

}
