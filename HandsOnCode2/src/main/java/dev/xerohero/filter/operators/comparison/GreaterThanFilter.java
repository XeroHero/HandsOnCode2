package dev.xerohero.filter.operators.comparison;

import dev.xerohero.filter.operators.BaseComparisonFilter;
import dev.xerohero.filter.ValueComparator;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

/**
 * A filter that checks if a resource's value is greater than the specified value.
 * Supports strict type checking for numeric comparisons.
 */
public class GreaterThanFilter extends BaseComparisonFilter {

    /**
     * Creates a new GreaterThanFilter with the specified key and value.
     *
     * @param key The key to compare against
     * @param value The value to compare with
     * @throws IllegalArgumentException if key is null/empty or value is not a valid number
     */
    public GreaterThanFilter(String key, String value) {
        super(key, value);
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource);
        if (actualValue == null) {
            return false; // Property doesn't exist
        }

        try {
            return ValueComparator.compare(actualValue, value) > 0;
        } catch (IllegalArgumentException e) {
            // Type mismatch or invalid comparison, treat as non-matching
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("(%s > '%s')", getKey(), value);
    }


    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}