package nbt.record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nbt.read.PushBackReader;

/**
 * The type of a nbt record.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public enum NBTType {
  /**
   * Signals the end of a compound.
   */
  END(0) {
    @Override
    public NBTRecord read(final PushBackReader in, final String name)
        throws IOException {
      return NBTEnd.INSTANCE;
    }
  },

  /**
   * A byte record.
   */
  BYTE(1) { // signed byte
    @Override
    public NBTRecord read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTNumeric(BYTE, name, in.readByte());
    }
  },

  /**
   * A short record.
   */
  SHORT(2) { // signed short (big endian)
    @Override
    public NBTRecord read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTNumeric(SHORT, name, in.readShort());
    }
  },

  /**
   * An integer record.
   */
  INT(3) { // signed int (big endian)
    @Override
    public NBTRecord read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTNumeric(INT, name, in.readInt());
    }
  },

  /**
   * A long record.
   */
  LONG(4) { // signed long (big endian)
    @Override
    public NBTRecord read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTNumeric(LONG, name, in.readLong());
    }
  },

  /**
   * A float record.
   */
  FLOAT(5) { // float (big endian, IEEE 754-2008, binary32)
    @Override
    public NBTRecord read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTNumeric(FLOAT, name, in.readFloat());
    }
  },

  /**
   * A double record.
   */
  DOUBLE(6) { // double (big endian, IEEE 754-2008, binary64)
    @Override
    public NBTRecord read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTNumeric(DOUBLE, name, in.readDouble());
    }
  },

  /**
   * A byte array record.
   */
  BYTE_ARRAY(7) { // NBTType.Int length ++ array of bytes
    @Override
    public NBTRecord read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTByteArray(name, in.readByteArray());
    }
  },

  /**
   * A string record.
   */
  STRING(8) { // NBTType.Short length ++ array of UTF-8 bytes
    @Override
    public NBTRecord read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTString(name, in.readString());
    }
  },

  /**
   * A list record.
   */
  LIST(9) { // NBTType.Byte tagId ++ NBTType.Int length ++ list
    @Override
    public NBTRecord read(final PushBackReader in, final String name)
        throws IOException {
      final NBTType type = forTagId(in.readByte());
      final int length = in.readInt();
      final NBTRecord[] list = new NBTRecord[length];
      for(int i = 0; i < length; ++i) {
        list[i] = type.read(in, null);
      }
      return new NBTList(name, type, list);
    }
  },

  /**
   * A compound record.
   */
  COMPOUND(10) { // list of (unique) named tags until NBTType.End
    @Override
    public NBTRecord read(final PushBackReader in, final String name)
        throws IOException {
      NBTRecord cur;
      final List<NBTRecord> list = new ArrayList<NBTRecord>();
      for(;;) {
        cur = readRecord(in);
        if(cur == NBTEnd.INSTANCE) {
          break;
        }
        list.add(cur);
      }
      return new NBTCompound(name, list);
    }
  },

  /**
   * An integer array record.
   */
  INT_ARRAY(11) { // NBTType.Int length ++ array of integers
    @Override
    public NBTRecord read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTIntArray(name, in.readIntArray());
    }
  },

  /* end of declaration */;

  /**
   * The id of the type.
   */
  public final byte byteValue;

  private NBTType(final int byteValue) {
    this.byteValue = (byte) byteValue;
  }

  /**
   * Defines how a record type should be read from the input stream.
   * 
   * @param in The reader.
   * @param name The name of the record.
   * @return The newly generated record.
   * @throws IOException I/O Exception.
   */
  public abstract NBTRecord read(final PushBackReader in, String name)
      throws IOException;

  private static NBTType[] lookup;

  /**
   * Returns a type for the id.
   * 
   * @param tagId The id.
   * @return The corresponding type.
   */
  public static NBTType forTagId(final int tagId) {
    if(lookup == null) {
      lookup = NBTType.values();
      int i = lookup.length;
      while(--i >= 0) {
        if(lookup[i].byteValue != i) throw new InternalError(
            "check order of NBTType");
      }
    }
    return lookup[tagId];
  }

  /**
   * Reads a record from the stream.
   * 
   * @param in The stream.
   * @return The record.
   * @throws IOException I/O Exception.
   */
  public static final NBTRecord readRecord(final PushBackReader in)
      throws IOException {
    final NBTType type = forTagId(in.readByte());
    final String name = type == END ? null : in.readString();
    return type.read(in, name);
  }

}
