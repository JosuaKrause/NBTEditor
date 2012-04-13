package nbt.log;

import java.util.Date;

public interface LogInterpreter {

    void log(Date date, String type, String msg);

    String INFO = "[INFO]";

    String WARNING = "[WARNING]";

}
