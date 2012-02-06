package nbt.record;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nbt.write.ByteWriter;

public class NBTCompound extends NBTRecord {

    private final Map<String, NBTRecord> map;

    public NBTCompound(final String name, final Collection<NBTRecord> content) {
        super(NBTType.COMPOUND, name);
        map = new HashMap<String, NBTRecord>(content.size());
        for (final NBTRecord rec : content) {
            add(rec);
        }
        resetChange();
    }

    public void add(final NBTRecord rec) {
        map.put(rec.getName(), rec);
        change();
    }

    public void remove(final String name) {
        map.remove(name);
        change();
    }

    public NBTRecord get(final String name) {
        return map.get(name);
    }

    public int indexOf(final NBTRecord r) {
        final String name = r.name;
        for (int i = 0; i < names.length; ++i) {
            final String other = names[i];
            if (other == name || (name != null && name.equals(other))) {
                return i;
            }
        }
        return -1;
    }

    public int size() {
        return map.size();
    }

    @Override
    protected void change() {
        names = null;
        super.change();
    }

    @Override
    public void resetChange() {
        super.resetChange();
        for (final NBTRecord r : map.values()) {
            r.resetChange();
        }
    }

    @Override
    public boolean hasChanged() {
        if (super.hasChanged()) {
            return true;
        }
        for (final NBTRecord r : map.values()) {
            if (r.hasChanged()) {
                return true;
            }
        }
        return false;
    }

    private String[] names = null;

    private void ensureNames() {
        if (names == null) {
            final String[] names = map.keySet().toArray(new String[0]);
            Arrays.sort(names);
            this.names = names;
        }
    }

    public NBTRecord get(final int index) {
        ensureNames();
        if (index < 0 || index >= names.length) {
            return null;
        }
        return map.get(names[index]);
    }

    @Override
    public void writePayload(final ByteWriter out) throws IOException {
        for (final NBTRecord r : map.values()) {
            r.write(out);
        }
        NBTEnd.INSTANCE.write(out);
    }

    @Override
    public String getPayloadString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(map.size());
        sb.append(" entries\n{\n");
        for (final NBTRecord r : map.values()) {
            sb.append(r.toString());
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

}
