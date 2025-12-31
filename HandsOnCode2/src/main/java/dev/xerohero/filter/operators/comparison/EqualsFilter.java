package dev.xerohero.filter.operators.comparison;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.operators.BaseComparisonFilter;
import dev.xerohero.filter.visitor.FilterVisitor;
import java.util.Map;

/**
 * A filter that checks if a resource's value equals a specified value.
 * Performs case-insensitive comparison by default.
 */
public class EqualsFilter extends BaseComparisonFilter {
    private final String value;

    /**
     * Creates a new equals filter.
     * 
     * @param key The key to check in the resource
     * @param value The value to compare against (case-insensitive)
     */
    public EqualsFilter(String key, String value) {
        super(key);
        this.value = value;
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource);
        if (actualValue == null) {
            return false; // Property doesn't exist
        }
        return actualValue.equalsIgnoreCase(value);
    }

    @Override
    public String toString() {
        return String.format("(%s == '%s')", getKey(), value);
    }

    /**
     * Gets the value to compare against.
     * 
     * @return The comparison value
     */
    public String getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}