package nbt.write;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import nbt.record.NBTRecord;

public class NBTWriter extends ByteWriter {

    private static final OutputStream toZip(final File file) throws IOException {
        return new GZIPOutputStream(new BufferedOutputStream(
                new FileOutputStream(file)));
    }

    public NBTWriter(final File file) throws IOException {
        this(toZip(file));
    }

    public NBTWriter(final OutputStream out) {
        super(out);
    }

    public void write(final NBTRecord record) throws IOException {
        record.write(this);
    }

}
