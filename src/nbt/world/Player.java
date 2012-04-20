package nbt.world;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import nbt.record.NBTCompound;
import nbt.record.NBTHandler;
import nbt.record.NBTList;
import nbt.record.NBTNumeric;

/**
 * Represents a player nbt file or the player part in the level.dat file.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class Player extends NBTHandler {

  /**
   * The extension of player nbt files.
   */
  public static final String PLAYER_EXT = ".dat";

  /**
   * The folder where player data is stored.
   */
  public static final String PLAYER = "players/";

  // the real record root -- if the player data is in level.dat
  private final NBTCompound playerRecord;

  /**
   * Creates a player from a level.dat file.
   * 
   * @param level The level.
   * @param playerRoot The root record of the player informations in level.
   */
  public Player(final Level level, final NBTCompound playerRoot) {
    super(level);
    playerRecord = playerRoot;
  }

  /**
   * Getter.
   * 
   * @return Gets the actual player root record. Note that this is not
   *         necessarily equal to {@link #getRoot()}.
   */
  public NBTCompound getPlayerRecord() {
    return playerRecord;
  }

  /**
   * Creates a player.
   * 
   * @param worldFolder The world folder.
   * @param name The name of the player.
   * @throws IOException I/O Exception.
   */
  public Player(final File worldFolder, final String name) throws IOException {
    super(new File(worldFolder, PLAYER + name + PLAYER_EXT));
    playerRecord = getRoot();
  }

  /**
   * Getter.
   * 
   * @return The player position.
   */
  public NBTList<NBTNumeric<Double>> getPosition() {
    return playerRecord.get("Pos");
  }

  /**
   * Getter.
   * 
   * @param worldFolder The world folder.
   * @return A list of all players that have been connected to this world.
   */
  public static final String[] listPlayers(final File worldFolder) {
    final File playerFolder = new File(worldFolder, PLAYER);
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

}
