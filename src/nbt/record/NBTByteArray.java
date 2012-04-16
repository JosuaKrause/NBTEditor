package nbt.record;

import java.io.IOException;
import java.text.ParseException;

import nbt.write.ByteWriter;

/**
 * A nbt byte array.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class NBTByteArray extends NBTRecord {

  private byte[] arr;

  /**
   * Creates a new byte array.
   * 
   * @param name The name of the record.
   * @param arr The byte array.
   */
  public NBTByteArray(final String name, final byte[] arr) {
    super(NBTType.BYTE_ARRAY, name);
    this.arr = arr;
  }

  /**
   * Getter.
   * 
   * @return The length of the byte array.
   */
  public int getLength() {
    return arr.length;
  }

  /**
   * Getter.
   * 
   * @param pos The index.
   * @return Gets the byte at the given index.
   */
  public byte getAt(final int pos) {
    return arr[pos];
  }

  /**
   * Setter.
   * 
   * @param pos The index.
   * @param value Sets the byte at the given index.
   */
  public void setAt(final int pos, final byte value) {
    arr[pos] = value;
    change();
  }

  /**
   * Setter.
   * 
   * @param arr Sets the byte array.
   */
  public void setArray(final byte[] arr) {
    this.arr = arr;
    change();
  }

  @Override
  public boolean isTextEditable() {
    return true;
  }

  @Override
  public String getParseablePayload() {
    final StringBuilder sb = new StringBuilder(arr.length * 2);
    for(final byte b : arr) {
      final String str = Integer.toHexString(b & 0xff);
      sb.append(str.length() < 2 ? "0" + str : str);
    }
    return sb.toString();
  }

  @Override
  public void parsePayload(final String str) throws ParseException {
    if(str.length() % 2 == 1) throw new ParseException("incorrect length",
        str.length());
    final byte[] arr = new byte[str.length() / 2];
    for(int i = 0; i < arr.length; ++i) {
      final char high = str.charAt(i * 2);
      final char low = str.charAt(i * 2 + 1);
      try {
        final int n = Integer.parseInt("" + high + low, 16);
        arr[i] = (byte) n;
      } catch(final NumberFormatException e) {
        throw new ParseException("illegal characters " + high + low,
            i * 2);
      }
    }
    setArray(arr);
  }

  @Override
  public boolean hasSize() {
    return true;
  }

  @Override
  public int size() {
    return getLength();
  }

  @Override
  public void writePayload(final ByteWriter out) throws IOException {
    out.write(arr);
  }

  @Override
  public String getPayloadString() {
    return "[" + getLength() + " bytes]";
  }

}
