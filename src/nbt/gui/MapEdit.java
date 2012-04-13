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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nbt.gui.MapViewer.ClickReceiver;
import nbt.gui.brush.BiomeBrush;
import nbt.gui.brush.DesertBrush;
import nbt.gui.brush.NoTopSnowBrush;
import nbt.gui.brush.WorldBorderBrush;

public class MapEdit extends JPanel implements Controls {

    private static final long serialVersionUID = 8342464413305576303L;

    public static final File LAST = new File(".lastMap");

    private File file;

    private MapViewer view;

    private JSlider radius;

    public MapEdit(final MapViewer v, final MapFrame frame) {
        view = v;
        view.setControls(this);
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
                } else {
                    JOptionPane.showMessageDialog(frame, "<html>WARNING!!!<br>"
                            + "Make a backup of your world before opening.<br>"
                            + "All brush operations are saved "
                            + "immediately and permanent!<br>"
                            + "Proceed at your own risk!");
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
        add(new JButton(new AbstractAction("No Snow") {

            private static final long serialVersionUID = -688821755368432842L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                view.setClickReceiver(new NoTopSnowBrush(view, radius
                        .getValue(), false));
            }

        }));
        add(new JButton(new AbstractAction("Desert Brush") {

            private static final long serialVersionUID = -16006819268382428L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                view.setClickReceiver(DesertBrush.brushGUI(frame, view,
                        radius.getValue()));
            }

        }));
        add(new JButton(new AbstractAction("Biome Brush") {

            private static final long serialVersionUID = -5839320662899741392L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                view.setClickReceiver(BiomeBrush.getBrushGUI(frame, view,
                        radius.getValue()));
            }

        }));
        add(new JButton(new AbstractAction("World Border Brush") {

            private static final long serialVersionUID = -5333405739914885063L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                view.setClickReceiver(new WorldBorderBrush(view, radius
                        .getValue()));
            }

        }));
        radius = new JSlider(getMinRadius(), getMaxRadius());
        radius.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                setRadius(radius.getValue());
            }

        });
        add(radius);
    }

    @Override
    public int getMaxRadius() {
        return 50;
    }

    @Override
    public int getMinRadius() {
        return 1;
    }

    @Override
    public int getRadius() {
        return radius.getValue();
    }

    @Override
    public void setRadius(final int radius) {
        if (radius == getRadius()) {
            return;
        }
        this.radius.setValue(radius);
        final ClickReceiver clickReceiver = view.getClickReceiver();
        if (clickReceiver == null) {
            return;
        }
        clickReceiver.setRadius(radius);
        view.repaint();
    }

}
