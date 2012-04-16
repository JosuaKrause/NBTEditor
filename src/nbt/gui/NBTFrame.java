package nbt.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;

import nbt.record.NBTEnd;

/**
 * The nbt editor window.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class NBTFrame extends JFrame {

  private static final long serialVersionUID = -2257586634651534207L;

  private JFrame frame;

  /**
   * Creates a new nbt editor.
   */
  public NBTFrame() {
    setTitle(null, false);
    setPreferredSize(new Dimension(800, 600));
    final JTree tree = new JTree(new NBTModel(NBTEnd.INSTANCE));
    tree.setRootVisible(true);
    tree.setCellRenderer(new NBTCellRenderer());
    setLayout(new BorderLayout());
    add(new JScrollPane(tree), BorderLayout.CENTER);
    add(new NBTEdit(tree, this), BorderLayout.SOUTH);
    pack();
    setLocationRelativeTo(null);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  /**
   * Sets the title of the window.
   * 
   * @param file The currently open file.
   * @param changed Whether the content of the nbt file has been changed.
   */
  public void setTitle(final File file, final boolean changed) {
    setTitle("NBTEdit: " + (changed ? "*" : "")
        + (file != null ? file.toString() : "-"));
  }

  /**
   * Sets the additional frame. E.g. the frame that is opened when a chunk has
   * been opened.
   * 
   * @param newFrame The additional frame.
   */
  public void setAdditionalFrame(final JFrame newFrame) {
    if(frame != null) {
      final JFrame f = frame;
      frame = null;
      f.setVisible(false);
      f.dispose();
    }
    frame = newFrame;
  }

  @Override
  public void dispose() {
    setAdditionalFrame(null);
    super.dispose();
  }

}
