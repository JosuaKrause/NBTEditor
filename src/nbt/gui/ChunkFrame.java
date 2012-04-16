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

/**
 * A small frame to show the loaded chunk from the nbt editor. Note that this
 * class is <em>not</em> used by the map editor.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class ChunkFrame extends JFrame {

  private static final long serialVersionUID = 1977165107627651898L;

  /**
   * The title of the frame.
   */
  public static final String TITLE = "Chunk Vis";

  /**
   * Creates a frame for a chunk.
   * 
   * @param scale The visual scaling of the chunk.
   * @param chunk The chunk.
   * @param parent The parent window.
   */
  public ChunkFrame(final double scale, final Chunk chunk, final JFrame parent) {
    super(TITLE);
    setLayout(new BorderLayout());
    add(new Display(scale, chunk), BorderLayout.CENTER);
    pack();
    setLocationRelativeTo(parent);
    setVisible(true);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  private class Display extends JComponent {

    private static final long serialVersionUID = 2040449928368332789L;

    private final double scale;

    private final Chunk chunk;

    public Display(final double s, final Chunk c) {
      chunk = c;
      scale = s;
      setPreferredSize(getAssumedSize());
      addMouseMotionListener(new MouseAdapter() {

        @Override
        public void mouseMoved(final MouseEvent e) {
          final double scale = getScale();
          final int x = (int) (e.getX() / scale);
          final int y = (int) (e.getY() / scale);
          if(x >= 0 && x < 16 && y >= 0 && y < 16) {
            final String tt = "x:" + x + " y:" + y + " b:"
                + getChunk().getBiome(x, y).name;
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

    protected Dimension getAssumedSize() {
      return new Dimension((int) (16 * scale), (int) (16 * scale));
    }

    public double getScale() {
      return scale;
    }

    public Chunk getChunk() {
      return chunk;
    }

    @Override
    public void paint(final Graphics gfx) {
      final Graphics2D g = (Graphics2D) gfx;
      for(int x = 0; x < 16; ++x) {
        for(int z = 0; z < 16; ++z) {
          final Rectangle2D rect = new Rectangle2D.Double(x * scale,
              z * scale, scale, scale);
          g.setColor(chunk.getColorForColumn(x, z));
          g.fill(rect);
        }
      }
    }

  }

}
