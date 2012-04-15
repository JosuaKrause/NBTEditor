package nbt.record;

import java.io.IOException;
import java.text.ParseException;

import nbt.write.ByteWriter;

/**
 * Represents an arbitrary nbt record.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public abstract class NBTRecord {

  private final NBTType tagId;

  private final String name;

  /**
   * Creates a new nbt record.
   * 
   * @param tagId The tag id of the record.
   * @param name The name of the record.
   */
  protected NBTRecord(final NBTType tagId, final String name) {
    this.tagId = tagId;
    this.name = name;
  }

  /**
   * Getter.
   * 
   * @return The name of the record.
   */
  public String getName() {
    return name;
  }

  /**
   * Writes this record to a byte writer.
   * 
   * @param out The writer.
   * @throws IOException I/O Exception.
   */
  public void write(final ByteWriter out) throws IOException {
    out.write(tagId.byteValue);
    final String name = getName();
    if(name != null) {
      out.write(name);
    }
    writePayload(out);
  }

  /**
   * Getter.
   * 
   * @return Whether this record is text editable.
   */
  public boolean isTextEditable() {
    return false;
  }

  /**
   * Getter.
   * 
   * @return The text editable string when this record is text editable.
   */
  public String getParseablePayload() {
    throw new UnsupportedOperationException("is not text editable");
  }

  /**
   * Setter.
   * 
   * @param str The string that is interpreted as new content of the record when
   *          the record is text editable.
   * @throws ParseException If the text could not be interpreted.
   */
  @SuppressWarnings("unused")
  public void parsePayload(final String str) throws ParseException {
    throw new UnsupportedOperationException("is not text editable");
  }

  /**
   * Writes the payload of the record into a byte writer.
   * 
   * @param out The writer.
   * @throws IOException I/O Exception.
   */
  public abstract void writePayload(ByteWriter out) throws IOException;

  /**
   * Getter.
   * 
   * @return A string representation of the payload.
   */
  public abstract String getPayloadString();

  /**
   * Getter.
   * 
   * @return The full text representation.
   */
  public String getFullRepresentation() {
    final String name = getName();
    return tagId.toString()
        + (name == null ? ": " : "(\"" + name + "\"): ")
        + getPayloadString();
  }

  private boolean hasChanged;

  /**
   * Signals that the record has been changed.
   */
  protected void change() {
    hasChanged = true;
  }

  /**
   * Resets the change flag.
   */
  public void resetChange() {
    hasChanged = false;
  }

  /**
   * Getter.
   * 
   * @return Whether the record has been changed.
   */
  public boolean hasChanged() {
    return hasChanged;
  }

  /**
   * Getter.
   * 
   * @return The type of record.
   */
  public NBTType getType() {
    return tagId;
  }

  /**
   * Getter.
   * 
   * @return A type info string.
   */
  public String getTypeInfo() {
    return getType() + (hasSize() ? " " + size() : "");
  }

  /**
   * Getter.
   * 
   * @return Whether the record has an associated size.
   */
  public boolean hasSize() {
    return false;
  }

  /**
   * Getter.
   * 
   * @return The associated size or 0 if the record has no associated size.
   */
  public int size() {
    return 0;
  }

}
