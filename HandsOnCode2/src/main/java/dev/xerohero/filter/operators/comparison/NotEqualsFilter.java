package dev.xerohero.filter.operators.comparison;

import dev.xerohero.filter.operators.BaseComparisonFilter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;
import java.util.Objects;

/**
 * A filter that checks if a resource's value is not equal to a specified value.
 */
public class NotEqualsFilter extends BaseComparisonFilter {

    /**
     * Creates a new not-equals filter.
     *
     * @param key   The key to check in the resource
     * @param value The value that the resource's value should not equal
     * @throws IllegalArgumentException if key is null or empty
     */
    public NotEqualsFilter(String key, String value) {
        super(key, value);
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        Objects.requireNonNull(resource, "Resource map cannot be null");
        String actualValue = resource.get(key);
        
        // If the key doesn't exist, it's not equal to any value
        if (actualValue == null) {
            return true;
        }
        
        // Compare the string representations
        return !actualValue.equals(value);
    }

    @Override
    public String toString() {
        return String.format("(%s != %s)", key, value);
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
