package nbt.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Reads a minecraft server log file.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class LogFileReader {

  private final LogInterpreter logger;

  /**
   * Creates a minecraft server log file reader.
   * 
   * @param logger The logger to read the content to.
   */
  public LogFileReader(final LogInterpreter logger) {
    this.logger = logger;
  }

  /**
   * Reads a log file from a reader.
   * 
   * @param r The reader.
   * @throws IOException I/O Exception.
   */
  public void read(final Reader r) throws IOException {
    final BufferedReader in = new BufferedReader(r);
    final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String line;
    while((line = in.readLine()) != null) {
      if(!line.matches("^[0-9].*")) {
        continue;
      }
      final String[] fields = line.split("\\s", 4);
      if(fields.length != 4) {
        System.err.println("invalid line: " + line);
        continue;
      }
      Date parse = null;
      try {
        parse = df.parse(fields[0] + " " + fields[1]);
      } catch(final ParseException e) {
        e.printStackTrace();
      }
      logger.log(parse, fields[2], fields[3]);
    }
  }

  /**
   * Whether the current message is a logon message.
   * 
   * @param msg The message.
   * @return Whether it is a logon message.
   */
  public static final boolean isLogonMessage(final String msg) {
    return msg.contains(" logged in with entity id ");
  }

  /**
   * Whether the current message is a logoff message.
   * 
   * @param msg The message.
   * @return Whether it is a logoff message.
   */
  public static final boolean isLogoffMessage(final String msg) {
    return msg.contains(" lost connection: ");
  }

  /**
   * Getter.
   * 
   * @param msg The message.
   * @return The name of the player in this log message.
   */
  public static final String getName(final String msg) {
    return msg.substring(0, msg.indexOf(' '));
  }

}
