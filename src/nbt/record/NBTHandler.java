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

  private final NBTCompound root;

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
    root = new NBTReader(nbtFile).read(NBTType.COMPOUND);
  }

  /**
   * Constructs a filter handler.
   * 
   * @param handler The handler to filter.
   */
  public NBTHandler(final NBTHandler handler) {
    nbtFile = handler.nbtFile;
    wrapZip = handler.wrapZip;
    root = handler.root;
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
    if(!root.hasChanged()) return;
    final NBTWriter out = new NBTWriter(nbtFile, wrapZip);
    out.write(root);
    out.close();
  }

  /**
   * Getter.
   * 
   * @return The root element of the nbt file.
   */
  public NBTCompound getRoot() {
    return root;
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
