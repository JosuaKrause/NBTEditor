package nbt.record;

import java.io.IOException;
import java.text.ParseException;

import nbt.write.ByteWriter;

public class NBTIntArray extends NBTRecord {

    protected int[] arr;

    public NBTIntArray(final String name, final int[] arr) {
        super(NBTType.INT_ARRAY, name);
        this.arr = arr;
    }

    public int getLength() {
        return arr.length;
    }

    public int getAt(final int pos) {
        return arr[pos];
    }

    public void setAt(final int pos, final int value) {
        arr[pos] = value;
        change();
    }

    public void setArray(final int[] arr) {
        this.arr = arr;
        change();
    }

    @Override
    public boolean isTextEditable() {
        return true;
    }

    @Override
    public String getParseablePayload() {
        final StringBuilder sb = new StringBuilder(arr.length * 8);
        for (final int i : arr) {
            final String str = "0000000" + Integer.toHexString(i);
            sb.append(str.substring(str.length() - 8));
        }
        return sb.toString();
    }

    @Override
    public void parsePayload(final String str) throws ParseException {
        if (str.length() % 8 != 0) {
            throw new ParseException("incorrect length", str.length());
        }
        final int[] arr = new int[str.length() / 8];
        for (int i = 0; i < arr.length; ++i) {
            final char h0 = str.charAt(i * 8);
            final char h1 = str.charAt(i * 8 + 1);
            final char h2 = str.charAt(i * 8 + 2);
            final char h3 = str.charAt(i * 8 + 3);
            final char h4 = str.charAt(i * 8 + 4);
            final char h5 = str.charAt(i * 8 + 5);
            final char h6 = str.charAt(i * 8 + 6);
            final char h7 = str.charAt(i * 8 + 7);
            try {
                final int n = Integer.parseInt("" + h0 + h1 + h2 + h3 + h4 + h5
                        + h6 + h7, 16);
                arr[i] = n;
            } catch (final NumberFormatException e) {
                throw new ParseException("illegal characters " + h0 + h1 + h2
                        + h3 + h4 + h5 + h6 + h7, i * 8);
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
        return "[" + getLength() + " ints]";
    }

}
