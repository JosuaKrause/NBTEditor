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
  END(0, NBTEnd.class) {
    @Override
    public NBTEnd read(final PushBackReader in, final String name)
        throws IOException {
      return NBTEnd.INSTANCE;
    }
  },

  /**
   * A byte record.
   */
  BYTE(1, NBTNumeric.class) { // signed byte
    @Override
    public NBTNumeric<Byte> read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTNumeric<Byte>(BYTE, name, in.readByte());
    }
  },

  /**
   * A short record.
   */
  SHORT(2, NBTNumeric.class) { // signed short (big endian)
    @Override
    public NBTNumeric<Short> read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTNumeric<Short>(SHORT, name, in.readShort());
    }
  },

  /**
   * An integer record.
   */
  INT(3, NBTNumeric.class) { // signed int (big endian)
    @Override
    public NBTNumeric<Integer> read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTNumeric<Integer>(INT, name, in.readInt());
    }
  },

  /**
   * A long record.
   */
  LONG(4, NBTNumeric.class) { // signed long (big endian)
    @Override
    public NBTNumeric<Long> read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTNumeric<Long>(LONG, name, in.readLong());
    }
  },

  /**
   * A float record.
   */
  FLOAT(5, NBTNumeric.class) { // float (big endian, IEEE 754-2008, binary32)
    @Override
    public NBTNumeric<Float> read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTNumeric<Float>(FLOAT, name, in.readFloat());
    }
  },

  /**
   * A double record.
   */
  DOUBLE(6, NBTNumeric.class) { // double (big endian, IEEE 754-2008, binary64)
    @Override
    public NBTNumeric<Double> read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTNumeric<Double>(DOUBLE, name, in.readDouble());
    }
  },

  /**
   * A byte array record.
   */
  BYTE_ARRAY(7, NBTByteArray.class) { // NBTType.Int length ++ array of bytes
    @Override
    public NBTByteArray read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTByteArray(name, in.readByteArray());
    }
  },

  /**
   * A string record.
   */
  STRING(8, NBTString.class) { // NBTType.Short length ++ array of UTF-8 bytes
    @Override
    public NBTString read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTString(name, in.readString());
    }
  },

  /**
   * A list record.
   */
  LIST(9, NBTList.class) { // NBTType.Byte tagId ++ NBTType.Int length ++ list
    @Override
    public NBTList<NBTRecord> read(final PushBackReader in, final String name)
        throws IOException {
      final NBTType type = forTagId(in.readByte());
      final int length = in.readInt();
      final NBTRecord[] list = new NBTRecord[length];
      for(int i = 0; i < length; ++i) {
        list[i] = type.read(in, null);
      }
      return new NBTList<NBTRecord>(name, type, list);
    }
  },

  /**
   * A compound record.
   */
  COMPOUND(10, NBTCompound.class) { // list of (unique) named tags until
                                    // NBTType.End
    @Override
    public NBTCompound read(final PushBackReader in, final String name)
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
  INT_ARRAY(11, NBTIntArray.class) { // NBTType.Int length ++ array of integers
    @Override
    public NBTIntArray read(final PushBackReader in, final String name)
        throws IOException {
      return new NBTIntArray(name, in.readIntArray());
    }
  },

  /* end of declaration */;

  /**
   * The id of the type.
   */
  public final byte byteValue;

  /**
   * The associated type.
   */
  public final Class<? extends NBTRecord> type;

  private NBTType(final int byteValue, final Class<? extends NBTRecord> type) {
    this.byteValue = (byte) byteValue;
    this.type = type;
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
