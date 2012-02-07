package nbt.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nbt.read.NBTReader;
import nbt.record.NBTRecord;
import nbt.write.NBTWriter;

public class NBTEdit extends JPanel {

	private static final long serialVersionUID = 6117715159789114581L;

	private final JLabel name;

	private final JTextField text;

	private TreePath path;

	private NBTRecord cur;

	private File file;

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
					read = new NBTReader(file);
					r = read.read();
				} catch (final IOException e) {
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
				if (file == null) {
					return;
				}
				final NBTRecord r = (NBTRecord) tree.getModel().getRoot();
				if (!r.hasChanged()) {
					return;
				}
				try {
					final NBTWriter write = new NBTWriter(file);
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
}
