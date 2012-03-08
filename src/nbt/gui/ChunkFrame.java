package nbt.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import nbt.map.Chunk;

public class ChunkFrame extends JFrame {

    private static final long serialVersionUID = 1977165107627651898L;

    public static final String TITLE = "Chunk Vis";

    private final Chunk chunk;

    private final double scale;

    public ChunkFrame(final double scale, final Chunk chunk, final JFrame parent) {
        super(TITLE);
        this.chunk = chunk;
        this.scale = scale;
        setLayout(new BorderLayout());
        add(new Display(), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    protected Dimension getAssumedSize() {
        return new Dimension((int) (16 * scale), (int) (16 * scale));
    }

    private class Display extends JComponent {

        private static final long serialVersionUID = 2040449928368332789L;

        public Display() {
            setPreferredSize(getAssumedSize());
            addMouseMotionListener(new MouseAdapter() {

                @Override
                public void mouseMoved(final MouseEvent e) {
                    final int x = (int) (e.getX() / scale);
                    final int y = (int) (e.getY() / scale);
                    if (x >= 0 && x < 16 && y >= 0 && y < 16) {
                        final String tt = "x:" + x + " y:" + y + " b:"
                                + chunk.getBiome(x, y).name;
                        setTitle(TITLE + " - " + tt);
                        setToolTipText(tt);
                    } else {
                        setTitle(TITLE);
                        setToolTipText(null);
                    }
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    setTitle(TITLE);
                    setToolTipText(null);
                }

            });
        }

        @Override
        public void paint(final Graphics gfx) {
            final Graphics2D g = (Graphics2D) gfx;
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    final Rectangle2D rect = new Rectangle2D.Double(x * scale,
                            z * scale, scale, scale);
                    g.setColor(chunk.getColorForColumn(x, z));
                    g.fill(rect);
                }
            }
        }

    }

}
