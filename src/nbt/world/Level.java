package nbt.world;

import java.io.File;
import java.io.IOException;

import nbt.record.NBTCompound;
import nbt.record.NBTHandler;

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
   * @return The single player on a single player map.
   */
  public Player getSinglePlayer() {
    return new Player(this, getPlayerRecord());
  }

}
