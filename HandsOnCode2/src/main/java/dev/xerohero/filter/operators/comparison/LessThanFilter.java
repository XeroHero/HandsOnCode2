package dev.xerohero.filter.operators.comparison;

import dev.xerohero.filter.operators.BaseComparisonFilter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;
import java.util.Objects;

/**
 * A filter that checks if a resource's value is less than a specified value.
 * <p>
 * This filter first attempts numeric comparison. If both values can be parsed as numbers,
 * it performs numeric comparison. Otherwise, it falls back to lexicographical string comparison.
 * </p>
 */
public class LessThanFilter extends BaseComparisonFilter {
    private final String value;

    /**
     * Creates a new less-than filter.
     *
     * @param key   The key to check in the resource
     * @param value The value to compare against
     * @throws IllegalArgumentException if value is null
     */
    public LessThanFilter(String key, String value) {
        super(key);
        this.value = Objects.requireNonNull(value, "Comparison value cannot be null");
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource);
        if (actualValue == null) {
            return false; // Propertie doesn't exist
        }

        try {
            // Try numeric comparison first for better precision
            double actualNum = Double.parseDouble(actualValue);
            double targetNum = Double.parseDouble(value);
            return actualNum < targetNum;
        } catch (NumberFormatException e) {
            // Fall back to string comparison if values aren't numeric
            return actualValue.compareTo(value) < 0;
        }
    }

    @Override
    public String toString() {
        return String.format("(%s < '%s')", getKey(), value);
    }

    /**
     * Gets the comparison value.
     *
     * @return The value to compare against
     */
    public String getValue() {
        return value;
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        // TODO: add error handling here
        return visitor.visit(this);
    }
}