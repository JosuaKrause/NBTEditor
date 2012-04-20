package nbt.record;

import java.io.IOException;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import nbt.write.ByteWriter;

/**
 * A compound is a name record map of records. The entries are sorted by the
 * names of the records.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class NBTCompound extends NBTRecord {

  private final SortedMap<String, NBTRecord> map;

  /**
   * Creates a new compound for the given collection.
   * 
   * @param name The name of the compound.
   * @param content The initial contents.
   */
  public NBTCompound(final String name, final Collection<NBTRecord> content) {
    super(NBTType.COMPOUND, name);
    map = new TreeMap<String, NBTRecord>();
    for(final NBTRecord rec : content) {
      add(rec);
    }
    resetChange();
  }

  /**
   * Adds a new record.
   * 
   * @param rec The new record.
   */
  public void add(final NBTRecord rec) {
    map.put(rec.getName(), rec);
    change();
  }

  /**
   * Removes the record with the given name.
   * 
   * @param name The name.
   */
  public void remove(final String name) {
    map.remove(name);
    change();
  }

  /**
   * Getter.
   * 
   * @param <T> Record type.
   * @param name The name.
   * @return The record with the given name.
   */
  public <T extends NBTRecord> T get(final String name) {
    return (T) map.get(name);
  }

  /**
   * Getter.
   * 
   * @param name The name.
   * @return Whether a record for the given name exists in the compund.
   */
  public boolean has(final String name) {
    return map.containsKey(name);
  }

  /**
   * Getter.
   * 
   * @param r The record.
   * @return The index of the record in the list (lookup by name).
   */
  public int indexOf(final NBTRecord r) {
    final String name = r.getName();
    if(!map.containsKey(name)) return -1;
    return map.headMap(name).size();
  }

  @Override
  public boolean hasSize() {
    return true;
  }

  @Override
  public int size() {
    return map.size();
  }

  private NBTRecord[] list;

  @Override
  protected void change() {
    list = null;
    super.change();
  }

  @Override
  public void resetChange() {
    super.resetChange();
    for(final NBTRecord r : map.values()) {
      r.resetChange();
    }
  }

  @Override
  public boolean hasChanged() {
    if(super.hasChanged()) return true;
    for(final NBTRecord r : map.values()) {
      if(r.hasChanged()) return true;
    }
    return false;
  }

  /**
   * Getter.
   * 
   * @param index The index.
   * @return Gets the record at the given index.
   */
  public NBTRecord get(final int index) {
    if(index < 0 || index >= map.size()) return null;
    if(list == null) {
      final Collection<NBTRecord> val = map.values();
      list = val.toArray(new NBTRecord[val.size()]);
    }
    return list[index];
  }

  @Override
  public void writePayload(final ByteWriter out) throws IOException {
    for(final NBTRecord r : map.values()) {
      r.write(out);
    }
    NBTEnd.INSTANCE.write(out);
  }

  @Override
  public String getPayloadString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(map.size());
    sb.append(" entries\n{\n");
    for(final NBTRecord r : map.values()) {
      sb.append(r.toString());
      sb.append("\n");
    }
    sb.append("}");
    return sb.toString();
  }

}
