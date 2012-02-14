package nbt.record;

import java.io.IOException;

import nbt.write.ByteWriter;

public class NBTList extends NBTRecord {

    private final NBTType type;

    private NBTRecord[] records;

    public NBTList(final String name, final NBTType type,
            final NBTRecord[] records) {
        super(NBTType.LIST, name);
        this.records = records;
        this.type = type;
    }

    public int getLength() {
        return records.length;
    }

    public NBTRecord getAt(final int pos) {
        return records[pos];
    }

    public void setAt(final int pos, final NBTRecord rec) {
        if (rec.getName() != null) {
            throw new IllegalArgumentException("list items must not be named: "
                    + rec.getName());
        }
        if (rec.getType() != type) {
            throw new IllegalArgumentException("item type must be consistent: "
                    + type + " expected got " + rec.getType());
        }
        records[pos] = rec;
        change();
    }

    public int indexOf(final NBTRecord r) {
        final String name = r.getName();
        for (int i = 0; i < records.length; ++i) {
            final String other = records[i].getName();
            if (other == name || (name != null && name.equals(other))) {
                return i;
            }
        }
        return -1;
    }

    public void setArray(final NBTRecord[] arr) {
        records = new NBTRecord[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            setAt(i, arr[i]);
        }
        change();
    }

    @Override
    public boolean hasChanged() {
        if (super.hasChanged()) {
            return true;
        }
        for (final NBTRecord r : records) {
            if (r.hasChanged()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void resetChange() {
        super.resetChange();
        for (final NBTRecord r : records) {
            r.resetChange();
        }
    }

    @Override
    public String getTypeInfo() {
        return super.getTypeInfo() + " " + type;
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
        out.write(type.byteValue);
        out.write(records.length);
        for (final NBTRecord r : records) {
            r.writePayload(out);
        }
    }

    @Override
    public String getPayloadString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(records.length);
        sb.append(" entries\n{\n");
        for (final NBTRecord r : records) {
            sb.append(r.toString());
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

}
