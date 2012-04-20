package nbt.gui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import nbt.record.NBTCompound;
import nbt.record.NBTList;
import nbt.record.NBTRecord;

/**
 * The tree model for nbt files.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class NBTModel implements TreeModel {

  private final NBTRecord root;

  /**
   * Creates a tree model for the given nbt record.
   * 
   * @param root The root record.
   */
  public NBTModel(final NBTRecord root) {
    this.root = root;
  }

  @Override
  public Object getRoot() {
    return root;
  }

  @Override
  public Object getChild(final Object parent, final int index) {
    if(parent instanceof NBTCompound) {
      final NBTCompound comp = (NBTCompound) parent;
      return comp.get(index);
    }
    if(parent instanceof NBTList) return ((NBTList<?>) parent).getAt(index);
    return null;
  }

  @Override
  public int getChildCount(final Object parent) {
    if(parent instanceof NBTCompound) return ((NBTCompound) parent).size();
    if(parent instanceof NBTList) return ((NBTList<?>) parent).getLength();
    return 0;
  }

  @Override
  public boolean isLeaf(final Object node) {
    return !((node instanceof NBTCompound) || (node instanceof NBTList));
  }

  @Override
  public int getIndexOfChild(final Object parent, final Object child) {
    final NBTRecord c = (NBTRecord) child;
    if(parent instanceof NBTCompound) return ((NBTCompound) parent).indexOf(c);
    if(parent instanceof NBTList) return ((NBTList<?>) parent).indexOf(c);
    return -1;
  }

  @Override
  public void valueForPathChanged(final TreePath path, final Object newValue) {
    for(final TreeModelListener l : listeners) {
      l.treeNodesChanged(new TreeModelEvent(newValue, path));
    }
  }

  private final List<TreeModelListener> listeners =
      new LinkedList<TreeModelListener>();

  @Override
  public void addTreeModelListener(final TreeModelListener l) {
    listeners.add(l);
  }

  @Override
  public void removeTreeModelListener(final TreeModelListener l) {
    listeners.remove(l);
  }

}
