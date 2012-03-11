package nbt.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class MapFrame extends JFrame {

    private static final long serialVersionUID = 9004371841431541418L;

    private final MapViewer view;

    public MapFrame() {
        setTitle(null, false);
        setPreferredSize(new Dimension(800, 600));
        view = new MapViewer(this, 2.0);
        setLayout(new BorderLayout());
        add(view, BorderLayout.CENTER);
        add(new MapEdit(view, this), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static final String TITLE = "MapViewer: ";

    private File file;

    private boolean chg;

    private String str;

    private String brush;

    public void setTitleText(final String str) {
        this.str = str;
        setTitle(file, chg);
    }

    public void setBrush(final String brush) {
        this.brush = brush;
    }

    public void setTitle(final File file, final boolean changed) {
        this.file = file;
        chg = changed;
        setTitle(TITLE + (changed ? "*" : "")
                + (file != null ? file.toString() : "-")
                + (str != null ? " - " + str : "")
                + (brush != null ? " Brush: " + brush : ""));
    }

    @Override
    public void dispose() {
        view.setControls(null);
        super.dispose();
    }

}
