package nbt.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFileReader {

    private final LogInterpreter logger;

    public LogFileReader(final LogInterpreter logger) {
        this.logger = logger;
    }

    public void read(final Reader r) throws IOException {
        final BufferedReader in = new BufferedReader(r);
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String line;
        while ((line = in.readLine()) != null) {
            if (!line.matches("^[0-9].*")) {
                continue;
            }
            final String[] fields = line.split("\\s", 4);
            if (fields.length != 4) {
                System.err.println("invalid line: " + line);
                continue;
            }
            Date parse = null;
            try {
                parse = df.parse(fields[0] + " " + fields[1]);
            } catch (final ParseException e) {
                e.printStackTrace();
            }
            logger.log(parse, fields[2], fields[3]);
        }
    }

    public static final boolean isLogonMessage(final String msg) {
        return msg.contains(" logged in with entity id ");
    }

    public static final boolean isLogoffMessage(final String msg) {
        return msg.contains(" lost connection: ");
    }

    public static final String getName(final String msg) {
        return msg.substring(0, msg.indexOf(' '));
    }

}
