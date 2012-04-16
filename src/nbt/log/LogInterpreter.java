package nbt.log;

import java.util.Date;

/**
 * A log interpreter.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public interface LogInterpreter {

  /**
   * Interprets a single log file entry.
   * 
   * @param date The date of the entry.
   * @param type The type of the entry.
   * @param msg The message of the entry.
   */
  void log(Date date, String type, String msg);

  /**
   * The info log type.
   */
  String INFO = "[INFO]";

  /**
   * The warning log type.
   */
  String WARNING = "[WARNING]";

}
