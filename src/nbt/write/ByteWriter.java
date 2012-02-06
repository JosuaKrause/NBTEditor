package nbt.write;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class ByteWriter implements Closeable {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    private OutputStream out;

    public ByteWriter(final OutputStream out) {
        this.out = out;
    }

    public final void write(final byte b) throws IOException {
        out.write(b);
    }

    public final void write(final short s) throws IOException {
        writeBigEndian(s, 2);
    }

    public final void write(final int i) throws IOException {
        writeBigEndian(i, 4);
    }

    public final void write(final long l) throws IOException {
        writeBigEndian(l, 8);
    }

    public final void write(final float f) throws IOException {
        write(Float.floatToIntBits(f));
    }

    public final void write(final double f) throws IOException {
        write(Double.doubleToLongBits(f));
    }

    public final void write(final byte[] arr) throws IOException {
        write(arr.length);
        out.write(arr);
    }

    public final void write(final String str) throws IOException {
        final byte[] arr = str.getBytes(UTF8);
        write((short) arr.length);
        out.write(arr);
    }

    private void writeBigEndian(long bytes, final int count) throws IOException {
        final byte[] b = new byte[count];
        int i = count;
        while (--i >= 0) {
            b[i] = (byte) bytes;
            bytes >>>= 8;
        }
        out.write(b);
    }

    @Override
    public void close() throws IOException {
        if (out != null) {
            final OutputStream t = out;
            out = null;
            t.close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

}
