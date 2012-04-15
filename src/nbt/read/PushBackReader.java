package nbt.read;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * A push back reader that operates on a byte stream.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class PushBackReader implements Closeable {

  /**
   * The utf-8 charset.
   */
  public static final Charset UTF8 = Charset.forName("UTF-8");

  private InputStream is;

  private byte pushBack;

  private boolean hasPushBack;

  private boolean atEnd;

  /**
   * Creates a pushback reader for an input stream.
   * 
   * @param is The input stream.
   */
  public PushBackReader(final InputStream is) {
    this.is = is;
    atEnd = false;
    hasPushBack = false;
  }

  /**
   * Reads a single byte.
   * 
   * @return The byte.
   * @throws IOException If the end of stream is reached.
   */
  public final byte readByte() throws IOException {
    if(hasPushBack) {
      hasPushBack = false;
      return pushBack;
    }
    if(atEnd) throw new IOException("early EOF");
    final int b = is.read();
    if(b < 0) {
      atEnd = true;
    }
    pushBack = (byte) b;
    return pushBack;
  }

  /**
   * Reads an unsigned byte.
   * 
   * @return The unsigned byte as integer.
   * @throws IOException If the end of stream is reached.
   */
  public final int readUnsignedByte() throws IOException {
    return readByte() & 0xff;
  }

  /**
   * Reads a big endian short.
   * 
   * @return The short.
   * @throws IOException If the end of stream is reached.
   */
  public final short readShort() throws IOException {
    // big endian
    final int high = readUnsignedByte();
    final int low = readUnsignedByte();
    return (short) (high << 8 | low);
  }

  /**
   * Reads a big endian integer.
   * 
   * @return The integer.
   * @throws IOException If the end of stream is reached.
   */
  public final int readInt() throws IOException {
    // big endian
    final int pos3 = readUnsignedByte();
    final int pos2 = readUnsignedByte();
    final int pos1 = readUnsignedByte();
    final int pos0 = readUnsignedByte();
    return pos3 << 24 | pos2 << 16 | pos1 << 8 | pos0;
  }

  /**
   * Reads a big endian long.
   * 
   * @return The long.
   * @throws IOException If the end of stream is reached.
   */
  public final long readLong() throws IOException {
    // big endian
    final long pos7 = readUnsignedByte();
    final long pos6 = readUnsignedByte();
    final long pos5 = readUnsignedByte();
    final long pos4 = readUnsignedByte();
    final long pos3 = readUnsignedByte();
    final long pos2 = readUnsignedByte();
    final long pos1 = readUnsignedByte();
    final long pos0 = readUnsignedByte();
    return pos7 << 56 | pos6 << 48 | pos5 << 40 | pos4 << 32 | pos3 << 24
        | pos2 << 16 | pos1 << 8 | pos0;
  }

  /**
   * Reads a big endian IEEE 754 float.
   * 
   * @return The float.
   * @throws IOException If the end of stream is reached.
   */
  public final float readFloat() throws IOException {
    // big endian, IEEE 754
    return Float.intBitsToFloat(readInt());
  }

  /**
   * Reads a big endian IEEE 754 double.
   * 
   * @return The double.
   * @throws IOException If the end of stream is reached.
   */
  public final double readDouble() throws IOException {
    // big endian, IEEE 754
    return Double.longBitsToDouble(readLong());
  }

  private byte[] readArray() throws IOException {
    final int length = readShort();
    final byte[] arr = new byte[length];
    for(int i = 0; i < length; ++i) {
      arr[i] = readByte();
    }
    return arr;
  }

  /**
   * Reads a utf-8 string.
   * 
   * @return The string.
   * @throws IOException If the end of stream is reached.
   */
  public final String readString() throws IOException {
    return new String(readArray(), UTF8);
  }

  /**
   * Reads a byte array.
   * 
   * @return The array.
   * @throws IOException If the end of stream is reached.
   */
  public final byte[] readByteArray() throws IOException {
    final int length = readInt();
    final byte[] arr = new byte[length];
    for(int i = 0; i < length; ++i) {
      arr[i] = readByte();
    }
    return arr;
  }

  /**
   * Reads an integer array.
   * 
   * @return The array.
   * @throws IOException If the end of stream is reached.
   */
  public int[] readIntArray() throws IOException {
    final int length = readInt();
    final int[] arr = new int[length];
    for(int i = 0; i < length; ++i) {
      arr[i] = readInt();
    }
    return arr;
  }

  /**
   * Whether there are still bytes to read.
   * 
   * @return <code>true</code> if there are still bytes to read.
   * @throws IOException I/O Exception.
   */
  public final boolean hasMore() throws IOException {
    readByte();
    pushBack();
    return !atEnd;
  }

  /**
   * Pushes the last read byte back on the stream.
   */
  public final void pushBack() {
    hasPushBack = true;
  }

  @Override
  public final void close() throws IOException {
    if(is != null) {
      final InputStream tmp = is;
      is = null;
      tmp.close();
    }
  }

  @Override
  protected void finalize() throws Throwable {
    close();
    super.finalize();
  }

}
