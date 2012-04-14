package nbt;

import nbt.gui.MapFrame;

/**
 * Starts the map editor. Arguments are ignored.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class MainMap {

  private MainMap() {
    // no constructor
  }

  /**
   * Starts the map editor.
   * 
   * @param args Ignored.
   */
  public static void main(final String[] args) {
    new MapFrame().setVisible(true);
  }

}
