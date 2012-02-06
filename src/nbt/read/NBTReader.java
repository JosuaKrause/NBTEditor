package nbt.read;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import nbt.record.NBTRecord;
import nbt.record.NBTType;

public class NBTReader extends PushBackReader {

    private static final InputStream fromZip(final File file)
            throws IOException {
        return new GZIPInputStream(new BufferedInputStream(new FileInputStream(
                file)));
    }

    public NBTReader(final File file) throws IOException {
        this(fromZip(file));
    }

    public NBTReader(final InputStream is) {
        super(is);
    }

    public NBTRecord read() throws IOException {
        return NBTType.readRecord(this);
    }

}
