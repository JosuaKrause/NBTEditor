package nbt.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class MapEdit extends JPanel {

    private static final long serialVersionUID = 8342464413305576303L;

    public static final File LAST = new File(".lastMap");

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
                if (LAST.exists()) {
                    try {
                        final Scanner s = new Scanner(LAST);
                        final File lastDir = new File(s.nextLine());
                        fc.setSelectedFile(lastDir);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
                final int returnVal = fc.showOpenDialog(MapEdit.this
                        .getParent());
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                file = fc.getSelectedFile();
                try {
                    final PrintWriter pw = new PrintWriter(LAST, "UTF-8");
                    pw.println(file);
                    pw.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                view.setFolder(file);
                frame.setTitle(file, false);
            }
        }));
    }

}
