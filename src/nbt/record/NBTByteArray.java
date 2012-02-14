package nbt.record;

import java.io.IOException;
import java.text.ParseException;

import nbt.write.ByteWriter;

public class NBTByteArray extends NBTRecord {

    protected byte[] arr;

    public NBTByteArray(final String name, final byte[] arr) {
        super(NBTType.BYTE_ARRAY, name);
        this.arr = arr;
    }

    public int getLength() {
        return arr.length;
    }

    public byte getAt(final int pos) {
        return arr[pos];
    }

    public void setAt(final int pos, final byte value) {
        arr[pos] = value;
        change();
    }

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
        for (final byte b : arr) {
            final String str = Integer.toHexString(b & 0xff);
            sb.append(str.length() < 2 ? "0" + str : str);
        }
        return sb.toString();
    }

    @Override
    public void parsePayload(final String str) throws ParseException {
        if (str.length() % 2 == 1) {
            throw new ParseException("incorrect length", str.length());
        }
        final byte[] arr = new byte[str.length() / 2];
        for (int i = 0; i < arr.length; ++i) {
            final char high = str.charAt(i * 2);
            final char low = str.charAt(i * 2 + 1);
            try {
                final int n = Integer.parseInt("" + high + low, 16);
                arr[i] = (byte) n;
            } catch (final NumberFormatException e) {
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
