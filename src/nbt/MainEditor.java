package nbt;

import nbt.gui.NBTFrame;

/**
 * Starts the nbt editor. Arguments are ignored.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class MainEditor {

  private MainEditor() {
    // no constructor
  }

  /**
   * Starts the nbt editor.
   * 
   * @param args Ignored.
   */
  public static void main(final String[] args) {
    new NBTFrame().setVisible(true);
  }

}
