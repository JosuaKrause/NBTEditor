package nbt.record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nbt.read.PushBackReader;

public enum NBTType {
	END(0) {
		@Override
		public NBTRecord read(final PushBackReader in, final String name)
				throws IOException {
			return NBTEnd.INSTANCE;
		}
	},

	BYTE(1) { // signed byte
		@Override
		public NBTRecord read(final PushBackReader in, final String name)
				throws IOException {
			return new NBTNumeric(BYTE, name, in.readByte());
		}
	},

	SHORT(2) { // signed short (big endian)
		@Override
		public NBTRecord read(final PushBackReader in, final String name)
				throws IOException {
			return new NBTNumeric(SHORT, name, in.readShort());
		}
	},

	INT(3) { // signed int (big endian)
		@Override
		public NBTRecord read(final PushBackReader in, final String name)
				throws IOException {
			return new NBTNumeric(INT, name, in.readInt());
		}
	},

	LONG(4) { // signed long (big endian)
		@Override
		public NBTRecord read(final PushBackReader in, final String name)
				throws IOException {
			return new NBTNumeric(LONG, name, in.readLong());
		}
	},

	FLOAT(5) { // float (big endian, IEEE 754-2008, binary32)
		@Override
		public NBTRecord read(final PushBackReader in, final String name)
				throws IOException {
			return new NBTNumeric(FLOAT, name, in.readFloat());
		}
	},

	DOUBLE(6) { // double (big endian, IEEE 754-2008, binary64)
		@Override
		public NBTRecord read(final PushBackReader in, final String name)
				throws IOException {
			return new NBTNumeric(DOUBLE, name, in.readDouble());
		}
	},

	BYTE_ARRAY(7) { // NBTType.Int length ++ array of bytes
		@Override
		public NBTRecord read(final PushBackReader in, final String name)
				throws IOException {
			return new NBTByteArray(name, in.readByteArray());
		}
	},

	STRING(8) { // NBTType.Short length ++ array of UTF-8 bytes
		@Override
		public NBTRecord read(final PushBackReader in, final String name)
				throws IOException {
			return new NBTString(name, in.readString());
		}
	},

	LIST(9) { // NBTType.Byte tagId ++ NBTType.Int length ++ list
		@Override
		public NBTRecord read(final PushBackReader in, final String name)
				throws IOException {
			final NBTType type = forTagId(in.readByte());
			final int length = in.readInt();
			final NBTRecord[] list = new NBTRecord[length];
			for (int i = 0; i < length; ++i) {
				list[i] = type.read(in, null);
			}
			return new NBTList(name, type, list);
		}
	},

	COMPOUND(10) { // list of (unique) named tags until NBTType.End
		@Override
		public NBTRecord read(final PushBackReader in, final String name)
				throws IOException {
			NBTRecord cur;
			final List<NBTRecord> list = new ArrayList<NBTRecord>();
			for (;;) {
				cur = readRecord(in);
				if (cur == NBTEnd.INSTANCE) {
					break;
				}
				list.add(cur);
			}
			return new NBTCompound(name, list);
		}
	},

	INT_ARRAY(11) { // NBTType.Int length ++ array of integers
		@Override
		public NBTRecord read(final PushBackReader in, final String name)
				throws IOException {
			return new NBTIntArray(name, in.readIntArray());
		}
	},

	;

	public final byte byteValue;

	private NBTType(final int byteValue) {
		this.byteValue = (byte) byteValue;
	}

	public abstract NBTRecord read(final PushBackReader in, String name)
			throws IOException;

	private static NBTType[] lookup = null;

	public static NBTType forTagId(final int tagId) {
		if (lookup == null) {
			lookup = NBTType.values();
			int i = lookup.length;
			while (--i >= 0) {
				if (lookup[i].byteValue != i) {
					throw new InternalError("check order of NBTType");
				}
			}
		}
		return lookup[tagId];
	}

	public static final NBTRecord readRecord(final PushBackReader in)
			throws IOException {
		final NBTType type = forTagId(in.readByte());
		final String name = type == END ? null : in.readString();
		return type.read(in, name);
	}

}
