package nbt.record;

import java.io.IOException;
import java.text.ParseException;

import nbt.write.ByteWriter;

/**
 * A numerical value record.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * @param <T> The number type.
 */
public class NBTNumeric<T extends Number> extends NBTRecord {

  private T payload;

  /**
   * Creates a numerical value.
   * 
   * @param type The type of the numerical value.
   * @param name The name of the record.
   * @param payload The numerical value.
   */
  public NBTNumeric(final NBTType type, final String name, final T payload) {
    super(type, name);
    this.payload = payload;
  }

  /**
   * Getter.
   * 
   * @return The number.
   */
  public T getPayload() {
    return payload;
  }

  @Override
  public String getPayloadString() {
    return payload.toString();
  }

  @Override
  public void writePayload(final ByteWriter out) throws IOException {
    switch(getType()) {
      case BYTE:
        out.write((Byte) payload);
        break;
      case SHORT:
        out.write((Short) payload);
        break;
      case INT:
        out.write((Integer) payload);
        break;
      case LONG:
        out.write((Long) payload);
        break;
      case FLOAT:
        out.write((Float) payload);
        break;
      case DOUBLE:
        out.write((Double) payload);
        break;
      default:
        throw new InternalError("missing type? " + getType());
    }
  }

  @Override
  public boolean isTextEditable() {
    return true;
  }

  @Override
  public String getParseablePayload() {
    return payload.toString();
  }

  @Override
  public void parsePayload(final String str) throws ParseException {
    try {
      switch(getType()) {
        case BYTE:
          payload = (T) Byte.valueOf(str);
          break;
        case SHORT:
          payload = (T) Short.valueOf(str);
          break;
        case INT:
          payload = (T) Integer.valueOf(str);
          break;
        case LONG:
          payload = (T) Long.valueOf(str);
          break;
        case FLOAT:
          payload = (T) Float.valueOf(str);
          break;
        case DOUBLE:
          payload = (T) Double.valueOf(str);
          break;
        default:
          throw new InternalError("missing type? " + getType());
      }
    } catch(final NumberFormatException e) {
      throw new ParseException("invalid number", 0);
    }
    change();
  }

}
