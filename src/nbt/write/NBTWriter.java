package nbt.write;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import nbt.record.NBTRecord;

public class NBTWriter extends ByteWriter {

	public NBTWriter(final File file) throws IOException {
		this(new BufferedOutputStream(new FileOutputStream(file)), true);
	}

	public NBTWriter(final OutputStream out, final boolean wrapZip)
			throws IOException {
		super(wrapZip ? new GZIPOutputStream(out) : out);
	}

	public void write(final NBTRecord record) throws IOException {
		record.write(this);
	}

}
