package nbt.write;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import nbt.record.NBTRecord;

/**
 * Writes a nbt file.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class NBTWriter extends ByteWriter {

  /**
   * Creates a writer for a zipped nbt file.
   * 
   * @param file The output file.
   * @throws IOException I/O Exception.
   */
  public NBTWriter(final File file) throws IOException {
    this(file, true);
  }

  /**
   * Creates a writer for a maybe zipped nbt file.
   * 
   * @param file The output file.
   * @param wrapZip Whether the file is zipped or not.
   * @throws IOException I/O Exception.
   */
  public NBTWriter(final File file, final boolean wrapZip) throws IOException {
    this(new BufferedOutputStream(new FileOutputStream(file)), wrapZip);
  }

  /**
   * Creates a writer for a maybe zipped nbt stream.
   * 
   * @param out The output stream.
   * @param wrapZip Whether the file is zipped or not.
   * @throws IOException I/O Exception.
   */
  public NBTWriter(final OutputStream out, final boolean wrapZip)
      throws IOException {
    super(wrapZip ? new GZIPOutputStream(out) : out);
  }

  /**
   * Writes the nbt content.
   * 
   * @param record The root element.
   * @throws IOException I/O Exception.
   */
  public void write(final NBTRecord record) throws IOException {
    record.write(this);
  }

}
