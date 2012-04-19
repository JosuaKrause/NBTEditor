package nbt.map;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import nbt.read.NBTReader;
import nbt.record.NBTRecord;

/**
 * An interface to gather informations about a minecraft world.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class World {

  /**
   * The file where level informations are stored.
   */
  public static final String LEVEL = "level.dat";

  /**
   * The extension of player nbt files.
   */
  public static final String PLAYER_EXT = ".dat";

  /**
   * The folder where player data is stored.
   */
  public static final String PLAYER = "players/";

  /**
   * The folder where the overworld map is stored.
   */
  public static final String OVERWORLD = "region/";

  /**
   * The folder where the nether map is stored.
   */
  public static final String NETHER = "DIM-1/";

  /**
   * The folder where the end map is stored.
   */
  public static final String WORLD_END = "DIM1/";

  private final File rootFolder;

  /**
   * Creates a world from a given folder.
   * 
   * @param rootFolder The root folder of the world.
   * @param multiPlayer Whether the world is a multi player world.
   */
  public World(final File rootFolder, final boolean multiPlayer) {
    if(!multiPlayer) throw new UnsupportedOperationException(
        "single player not supported yet");
    this.rootFolder = rootFolder;
  }

  /**
   * Getter.
   * 
   * @return The root folder of the world.
   */
  public File getRootFolder() {
    return rootFolder;
  }

  /**
   * Getter.
   * 
   * @return The root record of the level information nbt file.
   * @throws IOException I/O Exception.
   */
  public NBTRecord getLevelDat() throws IOException {
    final NBTReader reader = new NBTReader(new File(rootFolder, LEVEL));
    return reader.read();
  }

  /**
   * Getter.
   * 
   * @param name The name of the player.
   * @return The root record of the player information nbt file.
   * @throws IOException I/O Exception.
   */
  public NBTRecord getPlayer(final String name) throws IOException {
    final File player = new File(rootFolder, PLAYER + name + PLAYER_EXT);
    return new NBTReader(player).read();
  }

  /**
   * Getter.
   * 
   * @return A list of all players that have been connected to this world.
   */
  public String[] listPlayers() {
    final File playerFolder = new File(rootFolder, PLAYER);
    final String[] list = playerFolder.list(new FilenameFilter() {

      @Override
      public boolean accept(final File dir, final String name) {
        return name.endsWith(PLAYER_EXT);
      }

    });
    final String[] res = new String[list.length];
    for(int i = 0; i < res.length; ++i) {
      final String l = list[i];
      res[i] = l.substring(0, l.length() - PLAYER_EXT.length());
    }
    return res;
  }

  /**
   * Getter.
   * 
   * @return The chunk manager for the overworld.
   */
  public SerialChunkManager getOverworld() {
    final File world = new File(rootFolder, OVERWORLD);
    final SerialChunkManager manager = new SerialChunkManager();
    manager.setFolder(world, false);
    return manager;
  }

  /**
   * Getter.
   * 
   * @return The chunk manager for the nether.
   */
  public SerialChunkManager getNether() {
    final File nether = new File(rootFolder, NETHER);
    final SerialChunkManager manager = new SerialChunkManager();
    manager.setFolder(nether, false);
    return manager;
  }

  /**
   * Getter.
   * 
   * @return The chunk manager for the end.
   */
  public SerialChunkManager getWorldEnd() {
    final File worldEnd = new File(rootFolder, WORLD_END);
    final SerialChunkManager manager = new SerialChunkManager();
    manager.setFolder(worldEnd, false);
    return manager;
  }

}
