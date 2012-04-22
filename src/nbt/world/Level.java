package nbt.world;

import java.io.File;
import java.io.IOException;

import nbt.map.pos.GamePosition;
import nbt.record.NBTCompound;
import nbt.record.NBTHandler;
import nbt.record.NBTNumeric;
import nbt.world.World.GameType;
import nbt.world.World.WorldDimension;

/**
 * Represents the level information file.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class Level extends NBTHandler {

  /**
   * The file where level informations are stored.
   */
  public static final String LEVEL = "level.dat";

  private final NBTCompound data;

  /**
   * Creates a level information handler.
   * 
   * @param worldFolder The world folder.
   * @throws IOException I/O Exception.
   */
  public Level(final File worldFolder) throws IOException {
    super(new File(worldFolder, LEVEL));
    data = getRoot().get("Data");
  }

  /**
   * Getter.
   * 
   * @return Whether this level.dat represents a single player world.
   */
  public boolean isSinglePlayer() {
    return data.has("Player");
  }

  /**
   * Getter.
   * 
   * @return The single player record on a single player map.
   */
  public NBTCompound getPlayerRecord() {
    if(!isSinglePlayer()) throw new IllegalStateException(
        "This world is a multi player world!");
    return data.get("Player");
  }

  /**
   * Getter.
   * 
   * @return The spawn x coordinate.
   */
  protected NBTNumeric<Integer> getSpawnX() {
    return data.get("SpawnX");
  }

  /**
   * Getter.
   * 
   * @return The spawn y coordinate.
   */
  protected NBTNumeric<Integer> getSpawnY() {
    return data.get("SpawnY");
  }

  /**
   * Getter.
   * 
   * @return The spawn z coordinate.
   */
  protected NBTNumeric<Integer> getSpawnZ() {
    return data.get("SpawnZ");
  }

  /**
   * Sets the spawn of the world.
   * 
   * @param pos The spawn.
   */
  public void setSpawn(final GamePosition pos) {
    if(pos.dim != WorldDimension.OVERWORLD) throw new IllegalArgumentException(
        "spawns may only be in the overworld - got: " + pos.dim);
    getSpawnX().setPayload((int) pos.x);
    getSpawnY().setPayload((int) pos.y);
    getSpawnZ().setPayload((int) pos.z);
  }

  /**
   * Getter.
   * 
   * @return The spawn.
   */
  public GamePosition getSpawn() {
    return new GamePosition(getSpawnX().getPayload(), getSpawnY().getPayload(),
        getSpawnZ().getPayload(), WorldDimension.OVERWORLD);
  }

  /**
   * Getter.
   * 
   * @return The game type field.
   */
  protected NBTNumeric<Integer> getNBTGameType() {
    return data.get("GameType");
  }

  /**
   * Setter.
   * 
   * @param type Sets the game type.
   */
  public void setGameType(final GameType type) {
    final NBTNumeric<Integer> t = getNBTGameType();
    switch(type) {
      case SURVIVAL:
        t.setPayload(0);
        break;
      case CREATIVE:
        t.setPayload(1);
        break;
    }
  }

  /**
   * Getter.
   * 
   * @return Gets the game type.
   */
  public GameType getGameType() {
    switch(getNBTGameType().getPayload()) {
      case 0:
        return GameType.SURVIVAL;
      case 1:
        return GameType.CREATIVE;
    }
    throw new InternalError();
  }

  /**
   * Getter.
   * 
   * @return The single player on a single player map.
   */
  public Player getSinglePlayer() {
    return new Player(this, getPlayerRecord());
  }

}
