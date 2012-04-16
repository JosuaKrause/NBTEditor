package nbt.log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Collects time statistics of a minecraft log file.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class TimeStats implements LogInterpreter {

  private final Map<String, Date> login = new HashMap<String, Date>();

  private final Map<String, Long> time = new HashMap<String, Long>();

  private final long[][] punch = new long[7][24];

  private long total;

  @Override
  public void log(final Date date, final String type, final String msg) {
    if(!type.equals(INFO) || date == null) return;
    if(LogFileReader.isLogonMessage(msg)) {
      final String name = LogFileReader.getName(msg);
      login.put(name, date);
    }
    if(LogFileReader.isLogoffMessage(msg)) {
      final String name = LogFileReader.getName(msg);
      final Date start = login.get(name);
      final long add = date.getTime() - start.getTime();
      if(time.containsKey(name)) {
        time.put(name, time.get(name) + add);
      } else {
        time.put(name, add);
      }
      login.remove(name);
      addInterval(start, date);
    }
  }

  private void addInterval(final Date start, final Date end) {
    Date s = start;
    final Calendar cal = Calendar.getInstance();
    cal.setTime(s);
    int dow = cal.get(Calendar.DAY_OF_WEEK) - 1;
    int hod = cal.get(Calendar.HOUR_OF_DAY);
    do {
      s = new Date(s.getTime() + HOUR);
      ++punch[dow][hod];
      ++total;
      dow = (dow + 1) % 7;
      hod = (hod + 1) % 24;
    } while(s.before(end));
  }

  /**
   * Seconds in milliseconds.
   */
  public static final long SEC = 1000;

  /**
   * Minutes in milliseconds.
   */
  public static final long MIN = 60 * SEC;

  /**
   * Hours in milliseconds.
   */
  public static final long HOUR = 60 * MIN;

  /**
   * Days in milliseconds.
   */
  public static final long DAY = 24 * HOUR;

  /**
   * Years (approx) in milliseconds.
   */
  public static final long YEAR = 365 * DAY;

  /**
   * Converts a time in milliseconds to a nice string.
   * 
   * @param time The milliseconds.
   * @return A human readable time representation.
   */
  public static String toNiceString(final long time) {
    long t = time;
    final StringBuilder sb = new StringBuilder();
    if(t >= YEAR) {
      sb.append(" " + (t / YEAR) + "y");
      t = t % YEAR;
    }
    if(t >= DAY) {
      sb.append(" " + (t / DAY) + "d");
      t = t % DAY;
    }
    if(t >= HOUR) {
      sb.append(" " + (t / HOUR) + "h");
      t = t % HOUR;
    }
    if(t >= MIN) {
      sb.append(" " + (t / MIN) + "m");
      t = t % MIN;
    }
    if(t >= SEC) {
      sb.append(" " + (t / SEC) + "s");
      t = t % SEC;
    }
    if(t > 0) {
      sb.append(" " + t + "ms");
    }
    return sb.toString();
  }

  private static final String[] WEEKNAMES = { "Sunday", "Monday", "Tuesday",
      "Wednesday", "Thursday", "Friday", "Saturday"};

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for(final Entry<String, Long> entry : time.entrySet()) {
      sb.append(entry.getKey());
      sb.append(toNiceString(entry.getValue()));
      sb.append("\n");
    }
    sb.append("\n");
    for(int day = 0; day < 7; ++day) {
      sb.append(" ");
      sb.append(WEEKNAMES[day]);
    }
    sb.append("\n");
    for(int hour = 0; hour < 24; ++hour) {
      for(int day = 0; day < 7; ++day) {
        if(day == 0) {
          sb.append(hour);
        }
        sb.append(" ");
        final double perc = (double) punch[day][hour] / (double) total
            * 100.0;
        sb.append(String.format("%1$f" + WEEKNAMES[day].length(), perc)
            + "%");
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * Reads a log file and prints a time info.
   * 
   * @param args The logfile.
   * @throws IOException I/O Exception.
   */
  public static void main(final String[] args) throws IOException {
    if(args.length != 1) {
      System.err.println("Usage: <logfile>");
      return;
    }
    final TimeStats stats = new TimeStats();
    final LogFileReader lfr = new LogFileReader(stats);
    lfr.read(new FileReader(new File(args[0])));
    System.out.println(stats);
  }

}
