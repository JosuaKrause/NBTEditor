package nbt.map;

/**
 * An update receiver can be notified to show changes.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public interface UpdateReceiver {

  /**
   * Something has changed. The update receiver should show these changes.
   */
  void somethingChanged();

  /**
   * Advises the update receiver to get rid of as much memory as possible
   * without allocating additional memory.
   */
  void memoryPanic();

}
