package nbt.gui;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class MapEdit extends JPanel {

    private static final long serialVersionUID = 8342464413305576303L;

    private File file;

    private MapViewer view;

    public MapEdit(final MapViewer v, final MapFrame frame) {
        view = v;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(new JButton(new AbstractAction("Open") {

            private static final long serialVersionUID = 1258082592429332554L;

            @Override
            public void actionPerformed(final ActionEvent ae) {
                final JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                final int returnVal = fc.showOpenDialog(MapEdit.this
                        .getParent());
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                file = fc.getSelectedFile();
                view.setFolder(file);
                frame.setTitle(file, false);
            }
        }));
    }

}
