package nbt.world;

import java.io.File;
import java.io.IOException;

import nbt.map.Chunk;
import nbt.map.SerialChunkManager;
import nbt.map.pos.WorldPosition;

/**
 * An interface to gather informations about a minecraft world.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class World {

  /**
   * Represents the game type.
   * 
   * @author Joschi <josua.krause@googlemail.com>
   */
  public static enum GameType {
    /**
     * Survival mode.
     */
    SURVIVAL,

    /**
     * Creative mode.
     */
    CREATIVE,

    /* end of declaration */;
  }

  /**
   * Represents one of the game dimensions.
   * 
   * @author Joschi <josua.krause@googlemail.com>
   */
  public static enum WorldDimension {
    /**
     * The overworld.
     */
    OVERWORLD,

    /**
     * The nether.
     */
    NETHER,

    /**
     * The end.
     */
    END,

    /* end of declaration */;
  }

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
   */
  public World(final File rootFolder) {
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

  private Level level;

  /**
   * Getter.
   * 
   * @return The root record of the level information nbt file.
   * @throws IOException I/O Exception.
   */
  public Level getLevelDat() throws IOException {
    if(level == null) {
      level = new Level(rootFolder);
    }
    return level;
  }

  /**
   * Getter.
   * 
   * @return Whether this world is a single player world.
   * @throws IOException I/O Exception.
   */
  public boolean isSinglePlayer() throws IOException {
    return getLevelDat().isSinglePlayer();
  }

  /**
   * Getter.
   * 
   * @param name The name of the player.
   * @return The root record of the player information nbt file.
   * @throws IOException I/O Exception.
   */
  public Player getPlayer(final String name) throws IOException {
    if(isSinglePlayer()) throw new IllegalStateException(
        "This world is a single player world!");
    return new Player(rootFolder, name);
  }

  /**
   * Getter.
   * 
   * @return The single player on a single player map.
   * @throws IOException I/O Exception.
   */
  public Player getSinglePlayer() throws IOException {
    return getLevelDat().getSinglePlayer();
  }

  /**
   * Getter.
   * 
   * @return A list of all players that have been connected to this world. This
   *         method only returns names in multi-player worlds.
   * @throws IOException I/O Exception.
   */
  public String[] listPlayers() throws IOException {
    if(isSinglePlayer()) return new String[0];
    return Player.listPlayers(rootFolder);
  }

  /**
   * Gets a list of all players in this world. If not all players are accessed
   * {@link #listPlayers()} is a more efficient alternative.
   * 
   * @return All players that have been connected to this world or the player in
   *         the single player world.
   * @throws IOException I/O Exception.
   */
  public Player[] getPlayers() throws IOException {
    if(isSinglePlayer()) return new Player[] { getSinglePlayer()};
    final String[] names = listPlayers();
    final Player[] res = new Player[names.length];
    for(int i = 0; i < names.length; ++i) {
      res[i] = getPlayer(names[i]);
    }
    return res;
  }

  /**
   * Getter.
   * 
   * @param dim The dimension.
   * @return The chunk manager for the given dimension.
   */
  public SerialChunkManager getDimension(final WorldDimension dim) {
    switch(dim) {
      case OVERWORLD:
        return getOverworld();
      case NETHER:
        return getNether();
      case END:
        return getWorldEnd();
    }
    throw new InternalError();
  }

  /**
   * Tests whether a chunk in a given dimension exists.
   * 
   * @param pos The position.
   * @param dim The dimension.
   * @return Whether the chunk at the given position exists.
   */
  public boolean chunkExists(final WorldPosition pos, final WorldDimension dim) {
    return getDimension(dim).existChunk(pos);
  }

  /**
   * Getter.
   * 
   * @param pos The position.
   * @param dim The dimension.
   * @return The topmost non-air height at the given position.
   */
  public int getTopMostPosition(final WorldPosition pos,
      final WorldDimension dim) {
    final SerialChunkManager scm = getDimension(dim);
    final Chunk chunk = scm.getChunk(pos);
    if(chunk == null) throw new IllegalStateException("chunk does not exist");
    final int res = chunk.getTopNonAirBlock(pos.getPosInChunk()).y;
    scm.unloadChunk(chunk);
    return res;
  }

  private SerialChunkManager overworld;

  private SerialChunkManager nether;

  private SerialChunkManager endworld;

  /**
   * Getter.
   * 
   * @return The chunk manager for the overworld.
   */
  public SerialChunkManager getOverworld() {
    if(overworld == null) {
      final File world = new File(rootFolder, OVERWORLD);
      final SerialChunkManager manager = new SerialChunkManager();
      manager.setFolder(world, false);
      overworld = manager;
    }
    return overworld;
  }

  /**
   * Getter.
   * 
   * @return The chunk manager for the nether.
   */
  public SerialChunkManager getNether() {
    if(nether == null) {
      final File netherFile = new File(rootFolder, NETHER);
      final SerialChunkManager manager = new SerialChunkManager();
      manager.setFolder(netherFile, false);
      nether = manager;
    }
    return nether;
  }

  /**
   * Getter.
   * 
   * @return The chunk manager for the end.
   */
  public SerialChunkManager getWorldEnd() {
    if(endworld == null) {
      final File worldEnd = new File(rootFolder, WORLD_END);
      final SerialChunkManager manager = new SerialChunkManager();
      manager.setFolder(worldEnd, false);
      endworld = manager;
    }
    return endworld;
  }

}
