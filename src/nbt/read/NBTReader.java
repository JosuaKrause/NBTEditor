package nbt.read;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import nbt.record.NBTRecord;
import nbt.record.NBTType;

/**
 * Reads a nbt file.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class NBTReader extends PushBackReader {

  /**
   * Creates a reader for a nbt file.
   * 
   * @param file The nbt file.
   * @throws IOException I/O Exception.
   */
  public NBTReader(final File file) throws IOException {
    this(file, true);
  }

  /**
   * Creates a reader for a maybe zipped nbt file.
   * 
   * @param file The nbt file.
   * @param wrapZip Whether the file is zipped or not.
   * @throws IOException I/O Exception.
   */
  public NBTReader(final File file, final boolean wrapZip) throws IOException {
    this(new BufferedInputStream(new FileInputStream(file)), wrapZip);
  }

  /**
   * Creates a reader for a maybe zipped nbt stream.
   * 
   * @param is The input stream.
   * @param wrapZip Whether the file is zipped or not.
   * @throws IOException I/O Exception.
   */
  public NBTReader(final InputStream is, final boolean wrapZip)
      throws IOException {
    super(wrapZip ? new GZIPInputStream(is) : is);
  }

  /**
   * Reads the nbt stream.
   * 
   * @param exp Expected type, {@code null} for no check.
   * @param <T> The type.
   * @return The root record.
   * @throws IOException I/O Exception.
   */
  @SuppressWarnings("unchecked")
  public <T extends NBTRecord> T read(final NBTType exp) throws IOException {
    final NBTRecord read = NBTType.readRecord(this);
    if(exp != null && read.getType() != exp) throw new IllegalStateException(
        "Expected: " + exp + " Got: " + read.getType());
    return (T) read;
  }

}
