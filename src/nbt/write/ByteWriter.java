package nbt.write;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * A byte writer with multi byte writer methods.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class ByteWriter implements Closeable {

  /**
   * The utf-8 charset.
   */
  public static final Charset UTF8 = Charset.forName("UTF-8");

  private OutputStream out;

  /**
   * Creates a byte writer for an output stream.
   * 
   * @param out The output stream.
   */
  public ByteWriter(final OutputStream out) {
    this.out = out;
  }

  /**
   * Writes a single byte.
   * 
   * @param b The byte.
   * @throws IOException I/O Exception.
   */
  public final void write(final byte b) throws IOException {
    out.write(b);
  }

  /**
   * Writes a short.
   * 
   * @param s The short.
   * @throws IOException I/O Exception.
   */
  public final void write(final short s) throws IOException {
    writeBigEndian(s, 2);
  }

  /**
   * Writes an integer.
   * 
   * @param i The integer.
   * @throws IOException I/O Exception.
   */
  public final void write(final int i) throws IOException {
    writeBigEndian(i, 4);
  }

  /**
   * Writes a long.
   * 
   * @param l The long.
   * @throws IOException I/O Exception.
   */
  public final void write(final long l) throws IOException {
    writeBigEndian(l, 8);
  }

  /**
   * Writes a float.
   * 
   * @param f The float.
   * @throws IOException I/O Exception.
   */
  public final void write(final float f) throws IOException {
    write(Float.floatToIntBits(f));
  }

  /**
   * Writes a double.
   * 
   * @param f The double.
   * @throws IOException I/O Exception.
   */
  public final void write(final double f) throws IOException {
    write(Double.doubleToLongBits(f));
  }

  /**
   * Writes a byte array. Note that this is <em>not</em> equivalent to
   * successive {@link #write(byte)} calls!
   * 
   * @param arr The array.
   * @throws IOException I/O Exception.
   */
  public final void write(final byte[] arr) throws IOException {
    write(arr.length);
    out.write(arr);
  }

  /**
   * Writes an integer array. Note that this is <em>not</em> equivalent to
   * successive {@link #write(int)} calls!
   * 
   * @param arr The array.
   * @throws IOException I/O Exception.
   */
  public final void write(final int[] arr) throws IOException {
    write(arr.length);
    for(final int i : arr) {
      write(i);
    }
  }

  /**
   * Writes a string.
   * 
   * @param str The string.
   * @throws IOException I/O Exception.
   */
  public final void write(final String str) throws IOException {
    final byte[] arr = str.getBytes(UTF8);
    write((short) arr.length);
    out.write(arr);
  }

  private void writeBigEndian(final long bytes, final int count)
      throws IOException {
    long lb = bytes;
    final byte[] b = new byte[count];
    int i = count;
    while(--i >= 0) {
      b[i] = (byte) lb;
      lb >>>= 8;
    }
    out.write(b);
  }

  @Override
  public void close() throws IOException {
    if(out != null) {
      final OutputStream t = out;
      out = null;
      t.close();
    }
  }

  @Override
  protected void finalize() throws Throwable {
    close();
    super.finalize();
  }

}
