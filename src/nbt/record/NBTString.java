package nbt.record;

import java.io.IOException;
import java.text.ParseException;

import nbt.write.ByteWriter;

/**
 * A string nbt record.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class NBTString extends NBTRecord {

  private String content;

  /**
   * Creates a string record.
   * 
   * @param name The name.
   * @param str The string.
   */
  public NBTString(final String name, final String str) {
    super(NBTType.STRING, name);
    content = str;
  }

  /**
   * Getter.
   * 
   * @return The content string.
   */
  public String getContent() {
    return content;
  }

  /**
   * Setter.
   * 
   * @param content The new content string.
   */
  public void setContent(final String content) {
    this.content = content;
    change();
  }

  @Override
  public boolean isTextEditable() {
    return true;
  }

  @Override
  public String getParseablePayload() {
    return getContent();
  }

  @Override
  public void parsePayload(final String str) throws ParseException {
    setContent(str);
  }

  @Override
  public String getPayloadString() {
    return "\"" + content + "\"";
  }

  @Override
  public void writePayload(final ByteWriter out) throws IOException {
    out.write(content);
  }

}
