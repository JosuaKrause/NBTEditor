package nbt.log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TimeStats implements LogInterpreter {

    private final Map<String, Date> login = new HashMap<String, Date>();

    private final Map<String, Long> time = new HashMap<String, Long>();

    private final long[][] punch = new long[7][24];

    private long total = 0;

    @Override
    public void log(final Date date, final String type, final String msg) {
        if (!type.equals(INFO) || date == null) {
            return;
        }
        if (LogFileReader.isLogonMessage(msg)) {
            final String name = LogFileReader.getName(msg);
            login.put(name, date);
        }
        if (LogFileReader.isLogoffMessage(msg)) {
            final String name = LogFileReader.getName(msg);
            final Date start = login.get(name);
            final long add = date.getTime() - start.getTime();
            if (time.containsKey(name)) {
                time.put(name, time.get(name) + add);
            } else {
                time.put(name, add);
            }
            login.remove(name);
            addInterval(start, date);
        }
    }

    private void addInterval(Date start, final Date end) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int dow = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int hod = cal.get(Calendar.HOUR_OF_DAY);
        do {
            start = new Date(start.getTime() + HOUR);
            ++punch[dow][hod];
            ++total;
            dow = (dow + 1) % 7;
            hod = (hod + 1) % 24;
        } while (start.before(end));
    }

    public static final long SEC = 1000;

    public static final long MIN = 60 * SEC;

    public static final long HOUR = 60 * MIN;

    public static final long DAY = 24 * HOUR;

    public static final long YEAR = 365 * DAY;

    public static String toNiceString(long time) {
        final StringBuilder sb = new StringBuilder();
        if (time >= YEAR) {
            sb.append(" " + (time / YEAR) + "y");
            time = time % YEAR;
        }
        if (time >= DAY) {
            sb.append(" " + (time / DAY) + "d");
            time = time % DAY;
        }
        if (time >= HOUR) {
            sb.append(" " + (time / HOUR) + "h");
            time = time % HOUR;
        }
        if (time >= MIN) {
            sb.append(" " + (time / MIN) + "m");
            time = time % MIN;
        }
        if (time >= SEC) {
            sb.append(" " + (time / SEC) + "s");
            time = time % SEC;
        }
        if (time > 0) {
            sb.append(" " + time + "ms");
        }
        return sb.toString();
    }

    private static final String[] name = { "Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday", "Saturday" };

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Entry<String, Long> entry : time.entrySet()) {
            sb.append(entry.getKey());
            sb.append(toNiceString(entry.getValue()));
            sb.append("\n");
        }
        sb.append("\n");
        for (int day = 0; day < 7; ++day) {
            sb.append(" ");
            sb.append(name[day]);
        }
        sb.append("\n");
        for (int hour = 0; hour < 24; ++hour) {
            for (int day = 0; day < 7; ++day) {
                if (day == 0) {
                    sb.append(hour);
                }
                sb.append(" ");
                final double perc = (double) punch[day][hour] / (double) total
                        * 100.0;
                sb.append(String.format("%1$f" + name[day].length(), perc)
                        + "%");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(final String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: <logfile>");
            return;
        }
        final TimeStats stats = new TimeStats();
        final LogFileReader lfr = new LogFileReader(stats);
        lfr.read(new FileReader(new File(args[0])));
        System.out.println(stats);
    }

}
