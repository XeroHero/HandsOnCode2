package dev.xerohero.filter;

import java.util.Objects;

public class ValueComparator {
    public static int compare(String value1, String value2) {
        TypedValue tv1 = new TypedValue(value1);
        TypedValue tv2 = new TypedValue(value2);

        // If types are compatible, compare directly
        if (tv1.type == tv2.type || (isNumeric(tv1.type) && isNumeric(tv2.type))) {
            return compareTypedValues(tv1, tv2);
        }

        // Fallback to string comparison if types are incompatible
        return tv1.value.toString().compareTo(tv2.value.toString());
    }

    private static boolean isNumeric(ValueType type) {
        return type == ValueType.INTEGER || type == ValueType.LONG || type == ValueType.DOUBLE;
    }

    private static int compareTypedValues(TypedValue v1, TypedValue v2) {
        // Handle numeric comparisons
        if (isNumeric(v1.type) && isNumeric(v2.type)) {
            double d1 = ((Number) v1.value).doubleValue();
            double d2 = ((Number) v2.value).doubleValue();
            return Double.compare(d1, d2);
        }

        // Handle boolean comparisons
        if (v1.type == ValueType.BOOLEAN && v2.type == ValueType.BOOLEAN) {
            Boolean b1 = (Boolean) v1.value;
            Boolean b2 = (Boolean) v2.value;
            return b1.compareTo(b2);
        }

        // Handle string comparisons
        return v1.value.toString().compareTo(v2.value.toString());
    }

    public static boolean isNumeric(String value) {
        try {
            new TypedValue(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public enum ValueType {
        STRING, INTEGER, FLOAT, DOUBLE, LONG, BOOLEAN
    }

    public static class TypedValue {
        private final Object value;
        private final ValueType type;

        public TypedValue(String value) {
            this.value = parseValue(Objects.requireNonNull(value, "Value cannot be null"));
            this.type = determineType(this.value);
        }

        private static Object parseValue(String value) {
            // Try parsing as boolean
            if (value.equalsIgnoreCase("true")) return true;
            if (value.equalsIgnoreCase("false")) return false;

            // Try parsing as integer
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // Not an integer, continue
            }

            // Try parsing as long
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                // Not a long, continue
            }

            // Try parsing as double
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                // Not a number, treat as string
                return value;
            }
        }

        private static ValueType determineType(Object value) {
            if (value instanceof Integer) return ValueType.INTEGER;
            if (value instanceof Long) return ValueType.LONG;
            if (value instanceof Double || value instanceof Float) return ValueType.DOUBLE;
            if (value instanceof Boolean) return ValueType.BOOLEAN;
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