package nbt.record;

import java.io.IOException;

import nbt.write.ByteWriter;

public class NBTEnd extends NBTRecord {

    private NBTEnd() {
        super(NBTType.END, null);
    }

    public static final NBTEnd INSTANCE = new NBTEnd();

    @Override
    public String getPayloadString() {
        return "";
    }

    @Override
    public void writePayload(final ByteWriter out) throws IOException {
        // nothing to do
    }

}
