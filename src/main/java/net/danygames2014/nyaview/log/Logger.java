package net.danygames2014.nyaview.log;

public class Logger {

    private final String name;
    private final String formattedName;
    private final LogLevel logLevel;

    public Logger(String name) {
        this(name, LogLevel.INFO);
    }

    public Logger(String name, LogLevel level) {
        if (name == null || name.isEmpty()) {
            this.name = "";
            this.formattedName = "";
        } else {
            this.name = name;
            this.formattedName = name + " | ";
        }
        
        this.logLevel = level;
    }

    public String getName() {
        return name;
    }

    public void log(LogLevel level, String message) {
        if (!(level.level >= logLevel.level)) {
            return;
        }

        switch (level) {
            case TRACE -> {
                System.out.println(Color.WHITE + "[" + formattedName + "TRACE] " + message + Color.RESET);
            }
            case DEBUG -> {
                System.out.println(Color.WHITE + "[" + formattedName + "DEBUG] " + message + Color.RESET);
            }
            case INFO -> {
                System.out.println(Color.RESET + "[" + formattedName + "INFO] " + message + Color.RESET);
            }
            case WARN -> {
                System.out.println(Color.YELLOW + "[" + formattedName + "WARN] " + message + Color.RESET);
            }
            case ERROR -> {
                System.err.println(formattedName + "[" + formattedName + "ERROR] " + message);
            }
            case CRITICAL -> {
                System.err.println(formattedName + "[" + formattedName + "CRITICAL] " + message);
            }
        }
    }

    public void trace(String message) {
        log(LogLevel.TRACE, message);
    }
    
    // Debug
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    // Info
    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    // Warn
    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public void warn(String message, Throwable e) {
        log(LogLevel.WARN, message);
        log(LogLevel.WARN, e.toString());
        for (var item : e.getStackTrace()) {
            log(LogLevel.WARN, "   " + item.toString());
        }
    }

    // Error
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public void error(String message, Throwable e) {
        log(LogLevel.ERROR, message);
        log(LogLevel.ERROR, e.toString());
        for (var item : e.getStackTrace()) {
            log(LogLevel.ERROR, "   " + item.toString());
        }
    }

    // Critical
    public void critical(String message) {
        log(LogLevel.CRITICAL, message);
    }

    public enum LogLevel {
        TRACE(0),
        DEBUG(1),
        INFO(2),
        WARN(3),
        ERROR(4),
        CRITICAL(5);

        public final int level;

        LogLevel(int level) {
            this.level = level;
        }
    }
}
