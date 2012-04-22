package nbt.world;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import nbt.map.pos.GamePosition;
import nbt.record.NBTCompound;
import nbt.record.NBTHandler;
import nbt.record.NBTList;
import nbt.record.NBTNumeric;
import nbt.world.World.GameType;
import nbt.world.World.WorldDimension;

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

  private final String name;

  /**
   * Creates a player from a level.dat file.
   * 
   * @param level The level.
   * @param playerRoot The root record of the player informations in level.
   */
  public Player(final Level level, final NBTCompound playerRoot) {
    super(level);
    playerRecord = playerRoot;
    name = "";
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
    this.name = name;
  }

  /**
   * Getter.
   * 
   * @return The name of the player or the empty string if this player has no
   *         name.
   */
  public String getName() {
    return name;
  }

  /**
   * Getter.
   * 
   * @return Whether this player is the single player.
   */
  public boolean isSinglePlayer() {
    return name.isEmpty();
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
   * Getter.
   * 
   * @return The player position.
   */
  protected NBTList<NBTNumeric<Double>> getNBTPosition() {
    return playerRecord.get("Pos");
  }

  /**
   * Getter.
   * 
   * @return The player position.
   */
  public GamePosition getPosition() {
    final NBTList<NBTNumeric<Double>> list = getNBTPosition();
    return new GamePosition(list.getAt(0).getPayload(),
        list.getAt(1).getPayload(), list.getAt(2).getPayload(), getDimension());
  }

  /**
   * Sets the position of the player.
   * 
   * @param pos The position.
   */
  public void setPosition(final GamePosition pos) {
    final NBTList<NBTNumeric<Double>> p = getNBTPosition();
    p.getAt(0).setPayload(pos.x);
    p.getAt(1).setPayload(pos.y);
    p.getAt(2).setPayload(pos.z);
    setDimension(pos.dim);
  }

  /**
   * Getter.
   * 
   * @return The dimension the player is in. 0 is the overworld, -1 is the
   *         nether and 1 is the end.
   */
  protected NBTNumeric<Integer> getNBTDimension() {
    return playerRecord.get("Dimension");
  }

  /**
   * Getter.
   * 
   * @return The dimension the player is in.
   */
  public WorldDimension getDimension() {
    switch(getNBTDimension().getPayload()) {
      case 0:
        return WorldDimension.OVERWORLD;
      case -1:
        return WorldDimension.NETHER;
      case 1:
        return WorldDimension.END;
    }
    throw new InternalError("missing dimension?");
  }

  /**
   * Sets the dimension the player is in.
   * 
   * @param dim The dimension.
   */
  public void setDimension(final WorldDimension dim) {
    final NBTNumeric<Integer> d = getNBTDimension();
    switch(dim) {
      case OVERWORLD:
        d.setPayload(0);
        break;
      case NETHER:
        d.setPayload(-1);
        break;
      case END:
        d.setPayload(1);
        break;
    }
  }

  /**
   * Getter.
   * 
   * @return The game type field for the player or <code>null</code> if this is
   *         a single player nbt record.
   */
  protected NBTNumeric<Integer> getNBTGameType() {
    return playerRecord.get("playerGameType");
  }

  /**
   * Getter.
   * 
   * @return The game type of the player. Note that only multi-player player
   *         have individual game types. Check with {@link #isSinglePlayer()}.
   */
  public GameType getGameType() {
    final NBTNumeric<Integer> gt = getNBTGameType();
    if(gt == null) throw new IllegalStateException(
        "single player does not have a game type");
    switch(gt.getPayload()) {
      case 0:
        return GameType.SURVIVAL;
      case 1:
        return GameType.CREATIVE;
    }
    throw new InternalError();
  }

  /**
   * Setter.
   * 
   * @param type Sets the game type of the player. Note that only multi-player
   *          player have individual game types. Check with
   *          {@link #isSinglePlayer()}.
   */
  public void setGameType(final GameType type) {
    final NBTNumeric<Integer> gt = getNBTGameType();
    if(gt == null) throw new IllegalStateException(
        "single player does not have a game type");
    switch(type) {
      case SURVIVAL:
        gt.setPayload(0);
        break;
      case CREATIVE:
        gt.setPayload(1);
        break;
    }
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
