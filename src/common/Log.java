package common;

import java.util.logging.ConsoleHandler; 
import java.util.logging.Level; 
import java.util.logging.LogRecord; 
import java.util.logging.Logger; 
import java.util.logging.SimpleFormatter; 

/**
 * Wrapper for the Java logger.
 */
public class Log {
    
    /** The internal Java logger. */
    private static final Logger LOGGER;

    /** The default logging level. */
    private static final Level DEFAULT_LEVEL = Level.INFO;

    // Static initialization block to configure the logger
    static {
        LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        LOGGER.setLevel(DEFAULT_LEVEL);

        // Override the default logger handler to prevent timestamps from being
        // printed for each statement. We may want to print them in the future,
        // in which case we can delete the following
        ConsoleHandler handler = new ConsoleHandler();
        SimpleFormatter formatter = new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord record) {
                return record.getMessage() + '\n';
            }
        };
        handler.setFormatter(formatter);
        LOGGER.addHandler(handler);
        LOGGER.setUseParentHandlers(false);
    }

    /**
     * Disable all logging.
     */
    public static void disable() {
        LOGGER.setLevel(Level.OFF);
    }

    /**
     * Print an info level log message.
     * @param string The first string or the format string.
     * @param args The format string arguments or other strings.
     */
    public static void print(String string, Object ...args) {
        LOGGER.log(DEFAULT_LEVEL, String.format(string, args));
    }
}
