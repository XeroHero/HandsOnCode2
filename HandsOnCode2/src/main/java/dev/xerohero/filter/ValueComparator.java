package dev.xerohero.filter;

import java.util.Objects;

/**
 * Handles comparison of values with proper type checking and conversion.
 * Supports multiple numeric types (int, long, float, double) and boolean.
 */
public class ValueComparator {
    /**
     * Compares two string values with proper type conversion and comparison.
     * @param value1 First value to compare
     * @param value2 Second value to compare
     * @return Negative if value1 < value2, 0 if equal, positive if value1 > value2
     * @throws NumberFormatException if values are not comparable
     */
    /**
     * Helper method to compare strings with case-insensitive comparison by default.
     * @param value1 First string to compare
     * @param value2 Second string to compare
     * @return Negative if value1 < value2, 0 if equal, positive if value1 > value2 (case-insensitive)
     */
    private static int compareStrings(String value1, String value2) {
        // If either string is wrapped in /, treat as case-sensitive comparison
        if ((value1.startsWith("/") && value1.endsWith("/")) || 
            (value2.startsWith("/") && value2.endsWith("/"))) {
            return value1.compareTo(value2);
        }
        // Otherwise, perform case-insensitive comparison
        return value1.compareToIgnoreCase(value2);
    }
    
    /**
     * Compares two string values with proper type conversion and comparison.
     * @param value1 First value to compare
     * @param value2 Second value to compare
     * @return Negative if value1 < value2, 0 if equal, positive if value1 > value2
     * @throws NumberFormatException if values are not comparable
     */
    // In ValueComparator.java
    public static int compare(String value1, String value2) {
        // Handle null values - throw NPE for null arguments as per test expectations
        if (value1 == null || value2 == null) {
            throw new NullPointerException("Comparison value cannot be null");
        }

        // Try numeric comparison first
        try {
            // Try parsing as integers first (handles very large integers precisely)
            try {
                java.math.BigInteger int1 = new java.math.BigInteger(value1);
                java.math.BigInteger int2 = new java.math.BigInteger(value2);
                return int1.compareTo(int2);
            } catch (NumberFormatException e) {
                // Not an integer, try as decimal
                java.math.BigDecimal dec1 = new java.math.BigDecimal(value1);
                java.math.BigDecimal dec2 = new java.math.BigDecimal(value2);
                return dec1.compareTo(dec2);
            }
        } catch (NumberFormatException e) {
            // Not a number, continue with other comparison methods
        }

        // Check for regex patterns
        boolean isRegex1 = isRegexPattern(value1);
        boolean isRegex2 = isRegexPattern(value2);

        // Handle regex comparison
        if (isRegex1 || isRegex2) {
            if (isRegex1 && isRegex2) {
                throw new IllegalArgumentException("Cannot compare two regex patterns");
            }
            try {
                if (isRegex1) {
                    String pattern = value1.substring(1, value1.length() - 1);
                    if (pattern.isEmpty()) {
                        throw new IllegalArgumentException("Regex pattern cannot be empty");
                    }
                    // Compile the pattern to validate syntax
                    java.util.regex.Pattern.compile(pattern);
                    return value2.matches(pattern) ? 0 : -1;
                } else {
                    String pattern = value2.substring(1, value2.length() - 1);
                    if (pattern.isEmpty()) {
                        throw new IllegalArgumentException("Regex pattern cannot be empty");
                    }
                    // Compile the pattern to validate syntax
                    java.util.regex.Pattern.compile(pattern);
                    return value1.matches(pattern) ? 0 : 1;
                }
            } catch (java.util.regex.PatternSyntaxException e) {
                throw new IllegalArgumentException("Invalid regex pattern: " + e.getMessage(), e);
            }
        }

        // Check if both values are numeric
        boolean isNumeric1 = isNumeric(value1);
        boolean isNumeric2 = isNumeric(value2);

        if (isNumeric1 && isNumeric2) {
            try {
                // First try to parse as double to handle all cases including scientific notation
                try {
                    double d1 = Double.parseDouble(value1);
                    double d2 = Double.parseDouble(value2);
                    
                    // If both numbers are integers and not in scientific notation, compare as longs for exactness
                    if (!value1.toLowerCase().contains("e") && !value2.toLowerCase().contains("e")) {
                        try {
                            // Try to parse as long if they look like integers
                            if (value1.matches("-?\\d+") && value2.matches("-?\\d+")) {
                                // Handle large integers that might exceed Long.MAX_VALUE
                                if (isLargeNumber(value1) || isLargeNumber(value2)) {
                                    java.math.BigInteger bi1 = new java.math.BigInteger(value1);
                                    java.math.BigInteger bi2 = new java.math.BigInteger(value2);
                                    return bi1.compareTo(bi2);
                                }
                                
                                // Regular integers
                                long l1 = Long.parseLong(value1);
                                long l2 = Long.parseLong(value2);
                                return Long.compare(l1, l2);
                            }
                        } catch (NumberFormatException e) {
                            // Fall through to double comparison
                        }
                    }
                    
                    // For all other cases (decimals, scientific notation), use double comparison
                    return Double.compare(d1, d2);
                } catch (NumberFormatException e) {
                    // Fall through to string comparison
                }
                
            } catch (NumberFormatException e) {
                // Fall back to string comparison if number parsing fails
                return compareStrings(value1, value2);
            }
        } else if (isNumeric1 || isNumeric2) {
            // One is numeric, the other isn't - numeric comes first
            return isNumeric1 ? -1 : 1;
        }

        // Neither is numeric, do string comparison with proper case handling
        return compareStrings(value1, value2);
    }

    public static boolean isNumeric(String value) {
        if (value == null) {
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        // Match valid numeric patterns, including scientific notation
        return trimmed.matches("-?\\d+([.,]\\d+)?([eE][-+]?\\d+)?");
    }

    /**
     * Compares two numeric values with proper type conversion.
     */
    private static int compareNumeric(TypedValue v1, TypedValue v2) {
        try {
            // Handle large numbers first
            if (isLargeNumber(v1.value) || isLargeNumber(v2.value)) {
                java.math.BigInteger bi1 = toBigInteger(v1.value);
                java.math.BigInteger bi2 = toBigInteger(v2.value);
                return bi1.compareTo(bi2);
            }
            
            // If either value is a floating point type, compare as double
            if (v1.type == ValueType.DOUBLE || v1.type == ValueType.FLOAT ||
                v2.type == ValueType.DOUBLE || v2.type == ValueType.FLOAT) {
                double d1 = ((Number) v1.value).doubleValue();
                double d2 = ((Number) v2.value).doubleValue();
                return Double.compare(d1, d2);
            }
            
            // For integer types, compare as long
            long l1 = ((Number) v1.value).longValue();
            long l2 = ((Number) v2.value).longValue();
            return Long.compare(l1, l2);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid numeric comparison between " + 
                v1.value + " (" + v1.type + ") and " + v2.value + " (" + v2.type + ")", e);
        }
    }
    
    private static java.math.BigInteger toBigInteger(Object value) {
        if (value instanceof String) {
            return new java.math.BigInteger(((String) value).trim());
        } else if (value instanceof Number) {
            return java.math.BigInteger.valueOf(((Number) value).longValue());
        }
        throw new IllegalArgumentException("Cannot convert " + value + " to BigInteger");
    }
    
    private static boolean isFloatingPoint(Object value) {
        return value instanceof Float || value instanceof Double || 
               (value instanceof String && ((String) value).contains("."));
    }
    
    private static boolean isLargeNumber(Object value) {
        if (value == null) return false;
        String s = value.toString().trim();
        
        // Check if it's a valid integer (possibly large)
        if (!s.matches("-?\\d+")) {
            return false;
        }
        
        try {
            // Try to parse as long first
            Long.parseLong(s);
            return false; // Successfully parsed as long, not a large number
        } catch (NumberFormatException e) {
            // Check if it's a valid large integer
            try {
                new java.math.BigInteger(s);
                return true; // Valid large integer
            } catch (NumberFormatException e2) {
                return false; // Not a valid number format
            }
        }
    }

    /**
     * Checks if the given string is a valid regex pattern wrapped in slashes.
     * @param value The string to check
     * @return true if the string is a valid regex pattern, false otherwise
     * @throws IllegalArgumentException if the pattern is invalid
     */
    private static boolean isRegexPattern(String value) {
        if (value == null || value.length() < 2) {
            return false;
        }
        
        boolean isWrapped = value.startsWith("/") && value.endsWith("/");
        if (!isWrapped) {
            if (value.startsWith("/") || value.endsWith("/")) {
                throw new IllegalArgumentException("Invalid regex pattern: pattern must be properly wrapped in slashes");
            }
            return false;
        }
        
        // Check for empty pattern (just slashes)
        if (value.length() == 2) {
            throw new IllegalArgumentException("Regex pattern cannot be empty");
        }
        
        // Validate regex syntax
        String pattern = value.substring(1, value.length() - 1);
        try {
            java.util.regex.Pattern.compile(pattern);
            return true;
        } catch (java.util.regex.PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex pattern: " + e.getMessage(), e);
        }
    }

    /**
     * Represents the type of a value for comparison purposes.
     */
    public enum ValueType {
        STRING,   // Any non-numeric, non-boolean string
        INTEGER,  // 32-bit integer
        LONG,     // 64-bit integer
        FLOAT,    // 32-bit floating point
        DOUBLE,   // 64-bit floating point
        BOOLEAN   // true/false
    }

    public static class TypedValue {
        private final Object value;
        private final ValueType type;

        public TypedValue(String value) {
            if (value == null) {
                this.value = null;
                this.type = ValueType.STRING; // or a new NULL type if you prefer
            } else {
                this.value = parseValue(value);
                this.type = determineType(this.value);
            }
        }

        /**
         * Parses a string value into the most appropriate Java type.
         * @param value The string value to parse
         * @return The parsed value as the most specific type possible
         * @throws NumberFormatException if the string cannot be parsed as a number
         */
        private static Object parseValue(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        
        // Handle boolean values
        if ("true".equalsIgnoreCase(trimmed)) return true;
        if ("false".equalsIgnoreCase(trimmed)) return false;
        
        // Don't parse regex patterns as numbers
        if (isRegexPattern(trimmed)) {
            return trimmed; // Return as string to be handled in compare()
        }
        
        try {
            // Try parsing as integer first (most common case)
            if (trimmed.matches("-?\\d+")) {
                try {
                    return Integer.parseInt(trimmed);
                } catch (NumberFormatException e) {
                    // Try as long if it doesn't fit in int
                    try {
                        return Long.parseLong(trimmed);
                    } catch (NumberFormatException e2) {
                        // If too big for long, try BigInteger
                        return new java.math.BigInteger(trimmed);
                    }
                }
            }
            
            // Try parsing as floating point
            if (trimmed.matches("-?\\d+\\.\\d*([eE][-+]?\\d+)?")) {
                try {
                    return Float.parseFloat(trimmed);
                } catch (NumberFormatException e) {
                    // If float doesn't have enough precision, use double
                    return Double.parseDouble(trimmed);
                }
            }
            
            // Not a recognized number format, return as string
            return value;
        } catch (NumberFormatException e) {
            // If any number parsing fails, return as string
            return value;
        }
        }

        /**
         * Determines the ValueType of a parsed value.
         */
        private static ValueType determineType(Object value) {
            if (value == null) {
                return ValueType.STRING; // or a new NULL type if you prefer
            }
            if (value instanceof Integer) return ValueType.INTEGER;
            if (value instanceof Long) return ValueType.LONG;
            if (value instanceof Float) return ValueType.FLOAT;
            if (value instanceof Double) return ValueType.DOUBLE;
            if (value instanceof Boolean) return ValueType.BOOLEAN;
            // Regex patterns are handled separately in compare()
            return ValueType.STRING;
        }

        public Object getValue() {
            return value;
        }

        public ValueType getType() {
            return type;
        }

        public <T> T getValue(Class<T> type) {
            if (type.isInstance(value)) {
                return type.cast(value);
            }
            throw new ClassCastException("Cannot convert " + value.getClass().getSimpleName() + " to " + type.getSimpleName());
        }
    }
}