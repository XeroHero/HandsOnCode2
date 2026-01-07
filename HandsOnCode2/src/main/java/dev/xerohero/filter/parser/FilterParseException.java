package dev.xerohero.filter.parser;

/**
 * Exception thrown when there is an error parsing a filter expression.
 */
public class FilterParseException extends RuntimeException {
    
    public FilterParseException(String message) {
        super(message);
    }
    
    public FilterParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
