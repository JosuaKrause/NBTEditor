package nbt.read;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class PushBackReader implements Closeable {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    private InputStream is;

    private byte pushBack;

    private boolean hasPushBack;

    private boolean atEnd;

    public PushBackReader(final InputStream is) {
        this.is = is;
        atEnd = false;
        hasPushBack = false;
    }

    public final byte readByte() throws IOException {
        if (hasPushBack) {
            hasPushBack = false;
            return pushBack;
        }
        if (atEnd) {
            throw new IOException("early EOF");
        }
        final int b = is.read();
        if (b < 0) {
            atEnd = true;
        }
        pushBack = (byte) b;
        return pushBack;
    }

    public final int readUnsignedByte() throws IOException {
        return readByte() & 0xff;
    }

    public final short readShort() throws IOException {
        // big endian
        final int high = readUnsignedByte();
        final int low = readUnsignedByte();
        return (short) (high << 8 | low);
    }

    public final int readInt() throws IOException {
        // big endian
        final int pos3 = readUnsignedByte();
        final int pos2 = readUnsignedByte();
        final int pos1 = readUnsignedByte();
        final int pos0 = readUnsignedByte();
        return pos3 << 24 | pos2 << 16 | pos1 << 8 | pos0;
    }

    public final long readLong() throws IOException {
        // big endian
        final long pos7 = readUnsignedByte();
        final long pos6 = readUnsignedByte();
        final long pos5 = readUnsignedByte();
        final long pos4 = readUnsignedByte();
        final long pos3 = readUnsignedByte();
        final long pos2 = readUnsignedByte();
        final long pos1 = readUnsignedByte();
        final long pos0 = readUnsignedByte();
        return pos7 << 56 | pos6 << 48 | pos5 << 40 | pos4 << 32 | pos3 << 24
                | pos2 << 16 | pos1 << 8 | pos0;
    }

    public final float readFloat() throws IOException {
        // big endian, IEEE 754
        return Float.intBitsToFloat(readInt());
    }

    public final double readDouble() throws IOException {
        // big endian, IEEE 754
        return Double.longBitsToDouble(readLong());
    }

    private final byte[] readArray() throws IOException {
        final int length = readShort();
        final byte[] arr = new byte[length];
        for (int i = 0; i < length; ++i) {
            arr[i] = readByte();
        }
        return arr;
    }

    public final String readString() throws IOException {
        return new String(readArray(), UTF8);
    }

    public final byte[] readByteArray() throws IOException {
        final int length = readInt();
        final byte[] arr = new byte[length];
        for (int i = 0; i < length; ++i) {
            arr[i] = readByte();
        }
        return arr;
    }

    public int[] readIntArray() throws IOException {
        final int length = readInt();
        final int[] arr = new int[length];
        for (int i = 0; i < length; ++i) {
            arr[i] = readInt();
        }
        return arr;
    }

    public final boolean hasMore() throws IOException {
        readByte();
        pushBack();
        return !atEnd;
    }

    public final void pushBack() {
        hasPushBack = true;
    }

    @Override
    public final void close() throws IOException {
        if (is != null) {
            final InputStream tmp = is;
            is = null;
            tmp.close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

}