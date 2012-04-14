package nbt;

/**
 * Indents a multi-line string.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class IndentString {

  private static final String NL = "\r\n";

  private final char in;

  private final char out;

  private final char space;

  private final int width;

  /**
   * The default indentation. Depth is 2 spaces and curly brackets signal
   * indentation.
   */
  public IndentString() {
    this('{', '}', ' ', 2);
  }

  /**
   * Defines an indentation string converter.
   * 
   * @param in The character where the indentation increases.
   * @param out The character where the indentation decreases.
   * @param space The character that is used to indent.
   * @param width The width of the indentation.
   */
  public IndentString(final char in, final char out, final char space,
      final int width) {
    this.in = in;
    this.out = out;
    this.space = space;
    this.width = width;
  }

  /**
   * Indents the given multi-line string.
   * 
   * @param str The string.
   * @return The indented string.
   */
  public String indent(final String str) {
    boolean wasNL = false;
    int indent = 0;
    final StringBuilder sb = new StringBuilder();
    for(final char c : str.toCharArray()) {
      final boolean isNL = NL.indexOf(c) >= 0;
      if(c == out) {
        indent -= width;
      }
      if(wasNL && !isNL) {
        doIndent(sb, indent);
      }
      if(c == in) {
        indent += width;
      }
      sb.append(c);
      wasNL = isNL;
    }
    return sb.toString();
  }

  private void doIndent(final StringBuilder sb, final int width) {
    int w = width;
    while(--w >= 0) {
      sb.append(space);
    }
  }

}
