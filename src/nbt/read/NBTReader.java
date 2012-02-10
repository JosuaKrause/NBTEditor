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

	public NBTReader(final File file) throws IOException {
		this(new BufferedInputStream(new FileInputStream(file)), true);
	}

	public NBTReader(final InputStream is, final boolean wrapZip)
			throws IOException {
		super(wrapZip ? new GZIPInputStream(is) : is);
	}

	public NBTRecord read() throws IOException {
		return NBTType.readRecord(this);
	}

}
