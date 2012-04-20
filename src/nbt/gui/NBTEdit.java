package nbt.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nbt.map.Chunk;
import nbt.map.pos.ChunkPosition;
import nbt.read.MapReader;
import nbt.read.NBTReader;
import nbt.record.NBTCompound;
import nbt.record.NBTRecord;
import nbt.record.NBTType;
import nbt.write.NBTWriter;
import net.minecraft.world.level.chunk.storage.RegionFile;

/**
 * The editor panel of the nbt editor.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class NBTEdit extends JPanel {

  private static final long serialVersionUID = 6117715159789114581L;

  /**
   * The file for saving the last opened nbt file.
   */
  public static final File LAST = new File(".lastNBT");

  private final JLabel name;

  private final JTextField text;

  private TreePath path;

  private NBTRecord cur;

  private File file;

  private boolean wrapZip;

  private boolean canSave;

  /**
   * Creates a new nbt editor panel.
   * 
   * @param tree The tree.
   * @param frame The underlying frame.
   */
  public NBTEdit(final JTree tree, final NBTFrame frame) {
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    name = new JLabel(" ");
    text = new JTextField();
    cur = null;
    add(name);
    add(text);
    final Action action = new AbstractAction("Edit") {

      private static final long serialVersionUID = -5082168158857360395L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        edit(tree, frame);
      }

    };
    add(new JButton(action));
    text.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(final KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
          action.actionPerformed(null);
        }
      }

    });
    add(new JButton(new AbstractAction("Open") {

      private static final long serialVersionUID = 4663994095611490449L;

      @Override
      public void actionPerformed(final ActionEvent ae) {
        final JFileChooser fc = new JFileChooser();
        if(LAST.exists()) {
          try {
            final Scanner s = new Scanner(LAST);
            final File last = new File(s.nextLine());
            fc.setSelectedFile(last);
          } catch(final IOException e) {
            e.printStackTrace();
          }
        }
        final int returnVal = fc.showOpenDialog(NBTEdit.this.getParent());
        if(returnVal != JFileChooser.APPROVE_OPTION) return;
        setFile(fc.getSelectedFile(), tree, frame);
      }

    }));
    add(new JButton(new AbstractAction("Save") {

      private static final long serialVersionUID = -7308700550861060140L;

      @Override
      public void actionPerformed(final ActionEvent ae) {
        final File file = getFile();
        if(file == null || !canSave()) return;
        final NBTRecord r = (NBTRecord) tree.getModel().getRoot();
        if(!r.hasChanged()) return;
        try {
          final NBTWriter write = new NBTWriter(file, wrapZip());
          write.write(r);
          write.close();
        } catch(final IOException e) {
          e.printStackTrace();
        }
        r.resetChange();
        tree.getModel().valueForPathChanged(new TreePath(r), r);
        frame.setTitle(file, r.hasChanged());
      }

    }));
    tree.getSelectionModel().setSelectionMode(
        TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addTreeSelectionListener(new TreeSelectionListener() {

      @Override
      public void valueChanged(final TreeSelectionEvent e) {
        setPath(e.getPath());
      }

    });
  }

  /**
   * Getter.
   * 
   * @return Whether the currently open file has a zipped content.
   */
  public boolean wrapZip() {
    return wrapZip;
  }

  /**
   * Edits the currently selected nbt record.
   * 
   * @param tree The tree.
   * @param frame The frame.
   */
  public void edit(final JTree tree, final NBTFrame frame) {
    try {
      cur.parsePayload(text.getText());
    } catch(final ParseException ex) {
      // record has not changed
    }
    final NBTRecord r = (NBTRecord) tree.getModel().getRoot();
    tree.getModel().valueForPathChanged(new TreePath(r), r);
    text.setText(cur.getParseablePayload());
    frame.setTitle(getFile(), r.hasChanged());
  }

  /**
   * Sets the selection in the tree.
   * 
   * @param p The path.
   */
  public void setPath(final TreePath p) {
    path = p;
    final NBTRecord r = (NBTRecord) path.getLastPathComponent();
    final String n = r.getName();
    name.setText(n != null ? n + ": " : " ");
    if(r.isTextEditable()) {
      text.setEnabled(true);
      text.setText(r.getParseablePayload());
    } else {
      text.setText("");
      text.setEnabled(false);
    }
    cur = r;
  }

  /**
   * Checks whether a file has one of the given extensions.
   * 
   * @param file The file.
   * @param ext The extensions.
   * @return Whether the file has such an extension.
   */
  public static boolean hasExtension(final File file, final String... ext) {
    final String name = file.getName();
    for(final String e : ext) {
      if(name.endsWith(e)) return true;
    }
    return false;
  }

  /**
   * Sets the currently opened file.
   * 
   * @param file The file to open.
   * @param tree The tree.
   * @param frame The frame.
   */
  public void setFile(final File file, final JTree tree, final NBTFrame frame) {
    this.file = file;
    try {
      final PrintWriter pw = new PrintWriter(LAST, "UTF-8");
      pw.println(file);
      pw.close();
    } catch(final IOException e) {
      e.printStackTrace();
    }
    NBTReader read = null;
    NBTCompound r = null;
    try {
      if(hasExtension(file, RegionFile.ANVIL_EXTENSION,
          RegionFile.MCREGION_EXTENSION)) {
        MapReader.clearCache();
        final MapReader mr = MapReader.getForFile(file);
        final List<ChunkPosition> coords = mr.getChunks();
        final ChunkPosition chunk =
            (ChunkPosition) JOptionPane.showInputDialog(frame,
                "Choose the chunk to display", "Choose chunk",
                JOptionPane.PLAIN_MESSAGE, null, coords.toArray(), null);
        r = chunk != null ? mr.read(chunk) : null;
        if(r != null && hasExtension(file, RegionFile.ANVIL_EXTENSION)) {
          final Chunk c = new Chunk(r, file, chunk);
          new ChunkFrame(8.0, c, frame);
        }
        wrapZip = false;
        canSave = false;
      } else {
        read = new NBTReader(file);
        r = read.read(NBTType.COMPOUND);
        wrapZip = true;
        canSave = true;
      }
    } catch(final IOException e) {
      // do not do this!
      // try {
      // read = new NBTReader(file, false);
      // r = read.read();
      // wrapZip = false;
      // } catch (final IOException ie) {
      // ie.printStackTrace();
      // }
      e.printStackTrace();
    } finally {
      if(read != null) {
        try {
          read.close();
        } catch(final IOException e) {
          e.printStackTrace();
        }
      }
    }
    if(r != null) {
      tree.setModel(new NBTModel(r));
      frame.setTitle(file, r.hasChanged());
    }
  }

  /**
   * Getter.
   * 
   * @return Whether the file can be saved.
   */
  public boolean canSave() {
    return canSave;
  }

  /**
   * Getter.
   * 
   * @return The currently open file.
   */
  public File getFile() {
    return file;
  }

}
