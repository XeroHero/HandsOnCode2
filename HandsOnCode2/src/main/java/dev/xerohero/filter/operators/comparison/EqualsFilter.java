package dev.xerohero.filter.operators.comparison;

import dev.xerohero.filter.operators.BaseComparisonFilter;
import dev.xerohero.filter.ValueComparator;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;
import java.util.Objects;

/**
 * A filter that checks if a resource's value equals the specified value.
 * Supports strict type checking and proper handling of different value types.
 */
public class EqualsFilter extends BaseComparisonFilter {

    /**
     * Creates a new EqualsFilter with the specified key and value.
     *
     * @param key The key to compare against
     * @param value The value to compare with (can be null for null checks)
     * @throws IllegalArgumentException if key is null or empty, or value is invalid
     */
    public EqualsFilter(String key, String value) {
        super(key, value);
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource);

        // Handle null cases
        if (actualValue == null) {
            return value == null; // Both are null -> true, otherwise false
        }
        if (value == null) {
            return false; // actualValue is not null but value is null
        }

        try {
            return ValueComparator.compare(actualValue, value) == 0;
        } catch (IllegalArgumentException e) {
            // Type mismatch or invalid comparison, treat as non-matching
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("(%s %s '%s')",
                getKey(),
                value == null ? "is" : "==",
                value == null ? "null" : value);
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}