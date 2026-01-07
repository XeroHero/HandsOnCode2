package dev.xerohero.filter.operators.comparison;

import dev.xerohero.filter.operators.BaseComparisonFilter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;
import java.util.Objects;

/**
 * A filter that checks if a resource's value is less than or equal to a specified value.
 */
public class LessThanOrEqualFilter extends BaseComparisonFilter {

    /**
     * Creates a new less-than-or-equal filter.
     *
     * @param key   The key to check in the resource
     * @param value The value that the resource's value should be less than or equal to
     * @throws IllegalArgumentException if key is null or empty, or if value is not a valid number
     */
    public LessThanOrEqualFilter(String key, String value) {
        super(key, value);
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        Objects.requireNonNull(resource, "Resource map cannot be null");
        String actualValue = resource.get(key);
        
        // If the key doesn't exist or is null, it's not less than or equal
        if (actualValue == null) {
            return false;
        }
        
        try {
            // Try numeric comparison first
            double actual = Double.parseDouble(actualValue);
            double expected = Double.parseDouble(value);
            return actual <= expected;
        } catch (NumberFormatException e) {
            // Fall back to string comparison if not a number
            return actualValue.compareTo(value) <= 0;
        }
    }

    @Override
    public String toString() {
        return String.format("(%s <= %s)", key, value);
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
