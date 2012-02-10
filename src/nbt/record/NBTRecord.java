package nbt.record;

import java.io.IOException;
import java.text.ParseException;

import nbt.write.ByteWriter;

public abstract class NBTRecord {

	private final NBTType tagId;

	private final String name;

	protected NBTRecord(final NBTType tagId, final String name) {
		this.tagId = tagId;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void write(final ByteWriter out) throws IOException {
		out.write(tagId.byteValue);
		final String name = getName();
		if (name != null) {
			out.write(name);
		}
		writePayload(out);
	}

	public boolean isTextEditable() {
		return false;
	}

	public String getParseablePayload() {
		throw new UnsupportedOperationException("is not text editable");
	}

	public void parsePayload(final String str) throws ParseException {
		throw new UnsupportedOperationException("is not text editable");
	}

	public abstract void writePayload(ByteWriter out) throws IOException;

	public abstract String getPayloadString();

	public String getFullRepresentation() {
		final String name = getName();
		return tagId.toString()
				+ (name == null ? ": " : "(\"" + name + "\"): ")
				+ getPayloadString();
	}

	private boolean hasChanged = false;

	protected void change() {
		hasChanged = true;
	}

	public void resetChange() {
		hasChanged = false;
	}

	public boolean hasChanged() {
		return hasChanged;
	}

	public NBTType getType() {
		return tagId;
	}

	public String getTypeInfo() {
		return getType() + (hasSize() ? " " + size() : "");
	}

	public boolean hasSize() {
		return false;
	}

	public int size() {
		return 0;
	}

}
