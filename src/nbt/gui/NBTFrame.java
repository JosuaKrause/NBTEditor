package nbt.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;

import nbt.record.NBTEnd;

public class NBTFrame extends JFrame {

    private static final long serialVersionUID = -2257586634651534207L;

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

    public void setTitle(final File file, final boolean changed) {
        setTitle("NBTEdit: " + (changed ? "*" : "")
                + (file != null ? file.toString() : "-"));
    }

}
