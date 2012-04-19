package nbt.record;

import java.io.File;
import java.io.IOException;

import nbt.read.NBTReader;
import nbt.write.NBTWriter;

/**
 * Handles the loading and saving of nbt files.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class NBTHandler {

  private final File nbtFile;

  private final boolean wrapZip;

  private final NBTRecord record;

  /**
   * Creates a nbt handler.
   * 
   * @param nbtFile The nbt file.
   * @param wrapZip Whether this file is zipped.
   * @throws IOException I/O Exception.
   */
  public NBTHandler(final File nbtFile, final boolean wrapZip)
      throws IOException {
    this.nbtFile = nbtFile;
    this.wrapZip = wrapZip;
    record = new NBTReader(nbtFile).read();
  }

  /**
   * Constructs a filter handler.
   * 
   * @param handler The handler to filter.
   */
  public NBTHandler(final NBTHandler handler) {
    nbtFile = handler.nbtFile;
    wrapZip = handler.wrapZip;
    record = handler.record;
  }

  /**
   * Creates a nbt handler.
   * 
   * @param nbtFile The nbt file.
   * @throws IOException I/O Exception.
   */
  public NBTHandler(final File nbtFile) throws IOException {
    this(nbtFile, true);
  }

  /**
   * Saves the nbt file if it has been changed.
   * 
   * @throws IOException I/O Exception.
   */
  public void save() throws IOException {
    if(!record.hasChanged()) return;
    final NBTWriter out = new NBTWriter(nbtFile, wrapZip);
    out.write(record);
    out.close();
  }

  /**
   * Getter.
   * 
   * @return The root element of the nbt file.
   */
  public NBTRecord getRecord() {
    return record;
  }

  /**
   * Getter.
   * 
   * @return The associated nbt file.
   */
  public File getNbtFile() {
    return nbtFile;
  }

}
