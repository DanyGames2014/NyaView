package net.danygames2014.nyaview.util;

public class Logger {

    public static LogLevel logLevel = LogLevel.INFO;

    public static void log(LogLevel level, String message){
        if(!(level.level >= logLevel.level)){
            return;
        }
        switch (level){
            case DEBUG -> {
                System.out.println(Color.WHITE + "[DEBUG] " + message + Color.RESET);
            }
            case INFO -> {
                System.out.println(Color.RESET + "[INFO] " + message + Color.RESET);
            }
            case WARN -> {
                System.out.println(Color.YELLOW + "[WARN] " + message + Color.RESET);
            }
            case ERROR -> {
                System.err.println(" [ERROR] " + message);
            }
            case CRITICAL -> {
                System.err.println(" [CRITICAL] " + message);
            }
        }
    }

    // Debug
    public static void debug(String message){
        log(LogLevel.DEBUG, message);
    }

    // Info
    public static void info(String message){
        log(LogLevel.INFO, message);
    }

    // Warn
    public static void warn(String message){
        log(LogLevel.WARN, message);
    }

    public static void warn(String message, Throwable e){
        log(LogLevel.WARN, message);
        log(LogLevel.WARN, e.toString());
        for (var item : e.getStackTrace()){
            log(LogLevel.WARN, "   " + item.toString());
        }
    }

    // Error
    public static void error(String message){
        log(LogLevel.ERROR, message);
    }

    public static void error(String message, Throwable e){
        log(LogLevel.ERROR, message);
        log(LogLevel.ERROR, e.toString());
        for (var item : e.getStackTrace()){
            log(LogLevel.ERROR, "   " + item.toString());
        }
    }

    // Critical
    public static void critical(String message){
        log(LogLevel.CRITICAL, message);
    }

    public enum LogLevel {
        DEBUG(0),
        INFO(1),
        WARN(2),
        ERROR(3),
        CRITICAL(4);

        public final int level;

        LogLevel(int level) {
            this.level = level;
        }
    }
}


