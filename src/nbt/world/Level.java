package nbt.world;

import java.io.File;
import java.io.IOException;

import nbt.record.NBTHandler;
import nbt.record.NBTRecord;

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

  /**
   * Creates a level information handler.
   * 
   * @param worldFolder The world folder.
   * @throws IOException I/O Exception.
   */
  public Level(final File worldFolder) throws IOException {
    super(new File(worldFolder, LEVEL));
  }

  /**
   * Getter.
   * 
   * @return Whether this level.dat represents a single player world.
   */
  public boolean isSinglePlayer() {
    // TODO: yada yada
    return false;
  }

  /**
   * Getter.
   * 
   * @return The single player record on a single player map.
   */
  public NBTRecord getPlayerRecord() {
    if(!isSinglePlayer()) throw new IllegalStateException(
        "This world is a multi player world!");
    // TODO: yada yada
    return null;
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
