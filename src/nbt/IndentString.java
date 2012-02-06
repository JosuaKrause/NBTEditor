package nbt;

public class IndentString {

    private static final String NL = "\r\n";

    private final char in;

    private final char out;

    private final char space;

    private final int width;

    public IndentString() {
        this('{', '}', ' ', 2);
    }

    public IndentString(final char in, final char out, final char space,
            final int width) {
        this.in = in;
        this.out = out;
        this.space = space;
        this.width = width;
    }

    public String indent(final String str) {
        boolean wasNL = false;
        int indent = 0;
        final StringBuilder sb = new StringBuilder();
        for (final char c : str.toCharArray()) {
            final boolean isNL = NL.indexOf(c) >= 0;
            if (c == out) {
                indent -= width;
            }
            if (wasNL && !isNL) {
                doIndent(sb, indent);
            }
            if (c == in) {
                indent += width;
            }
            sb.append(c);
            wasNL = isNL;
        }
        return sb.toString();
    }

    private void doIndent(final StringBuilder sb, int w) {
        while (--w >= 0) {
            sb.append(space);
        }
    }

}
