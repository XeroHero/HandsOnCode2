package dev.xerohero.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple debug logging utility class.
 */
public class DebugLog {
    private static final Logger logger = LoggerFactory.getLogger(DebugLog.class);

    /**
     * Logs a debug message.
     *
     * @param message the message to log
     */
    public static void log(String message) {
        logger.debug(message);
    }
}
