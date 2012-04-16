package nbt.record;

import java.io.IOException;

import nbt.write.ByteWriter;

/**
 * The nbt end record signals the end of a compound list.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class NBTEnd extends NBTRecord {

  private NBTEnd() {
    super(NBTType.END, null);
  }

  /**
   * The only nbt end instance.
   */
  public static final NBTEnd INSTANCE = new NBTEnd();

  @Override
  public String getPayloadString() {
    return "";
  }

  @Override
  public void writePayload(final ByteWriter out) throws IOException {
    // nothing to do
  }

}
