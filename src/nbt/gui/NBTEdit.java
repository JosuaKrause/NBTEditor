package nbt.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

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
import nbt.read.MapReader;
import nbt.read.MapReader.Pair;
import nbt.read.NBTReader;
import nbt.record.NBTRecord;
import nbt.write.NBTWriter;
import net.minecraft.world.level.chunk.storage.RegionFile;

public class NBTEdit extends JPanel {

    private static final long serialVersionUID = 6117715159789114581L;

    private final JLabel name;

    private final JTextField text;

    private TreePath path;

    private NBTRecord cur;

    private File file;

    private boolean wrapZip;

    private boolean canSave;

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
                try {
                    cur.parsePayload(text.getText());
                } catch (final ParseException ex) {
                    // record has not changed
                }
                final NBTRecord r = (NBTRecord) tree.getModel().getRoot();
                tree.getModel().valueForPathChanged(new TreePath(r), r);
                text.setText(cur.getParseablePayload());
                frame.setTitle(file, r.hasChanged());
            }

        };
        add(new JButton(action));
        text.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    action.actionPerformed(null);
                }
            }

        });
        add(new JButton(new AbstractAction("Open") {

            private static final long serialVersionUID = 4663994095611490449L;

            @Override
            public void actionPerformed(final ActionEvent ae) {
                final JFileChooser fc = new JFileChooser();
                final int returnVal = fc.showOpenDialog(NBTEdit.this
                        .getParent());
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                file = fc.getSelectedFile();
                NBTReader read = null;
                NBTRecord r = null;
                try {
                    if (hasExtension(file, RegionFile.ANVIL_EXTENSION,
                            RegionFile.MCREGION_EXTENSION)) {
                        final MapReader mr = new MapReader(file);
                        final List<Pair> coords = mr.getChunks();
                        final Pair chunk = (Pair) JOptionPane.showInputDialog(
                                frame, "Choose the chunk to display",
                                "Choose chunk", JOptionPane.PLAIN_MESSAGE,
                                null, coords.toArray(), null);
                        r = chunk != null ? mr.read(chunk.x, chunk.z) : null;
                        final Chunk c = new Chunk(r);
                        new ChunkFrame(8.0, c, frame);
                        read = null;
                        wrapZip = false;
                        canSave = false;
                    } else {
                        read = new NBTReader(file);
                        r = read.read();
                        wrapZip = true;
                        canSave = true;
                    }
                } catch (final IOException e) {
                    // try {
                    // read = new NBTReader(file, false);
                    // r = read.read();
                    // wrapZip = false;
                    // } catch (final IOException ie) {
                    // ie.printStackTrace();
                    // }
                    e.printStackTrace();
                } finally {
                    if (read != null) {
                        try {
                            read.close();
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (r != null) {
                    tree.setModel(new NBTModel(r));
                    frame.setTitle(file, r.hasChanged());
                }
            }

        }));
        add(new JButton(new AbstractAction("Save") {

            private static final long serialVersionUID = -7308700550861060140L;

            @Override
            public void actionPerformed(final ActionEvent ae) {
                if (file == null || !canSave) {
                    return;
                }
                final NBTRecord r = (NBTRecord) tree.getModel().getRoot();
                if (!r.hasChanged()) {
                    return;
                }
                try {
                    final NBTWriter write = new NBTWriter(file, wrapZip);
                    write.write(r);
                    write.close();
                } catch (final IOException e) {
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
                path = e.getPath();
                final NBTRecord r = (NBTRecord) path.getLastPathComponent();
                final String n = r.getName();
                name.setText(n != null ? n + ": " : " ");
                if (r.isTextEditable()) {
                    text.setEnabled(true);
                    text.setText(r.getParseablePayload());
                } else {
                    text.setText("");
                    text.setEnabled(false);
                }
                cur = r;
            }

        });
    }

    private static boolean hasExtension(final File file, final String... ext) {
        final String name = file.getName();
        for (final String e : ext) {
            if (name.endsWith(e)) {
                return true;
            }
        }
        return false;
    }

}
