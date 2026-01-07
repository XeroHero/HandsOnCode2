package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.ValueComparator;
import dev.xerohero.filter.operators.comparison.HasPropertyFiltre;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;
import java.util.Objects;

/**
 * Base class for filters that compare a single key-value pair.
 * Handles common functionality for key-based comparisons.
 */
public abstract class BaseComparisonFilter implements Filter {
    protected final String key;
    protected final String value;
    protected final ValueComparator.TypedValue typedValue;

    /**
     * Creates a filter that will compare values using the specified key and value.
     *
     * @param key The key to use for comparison in resource maps
     * @param value The value to compare against
     * @throws IllegalArgumentException if key is null or empty, or value is invalid
     */
    protected BaseComparisonFilter(String key, String value) {
        validateKey(key);
        validateValue(value);
        this.key = key;
        this.value = value;
        this.typedValue = new ValueComparator.TypedValue(value);
    }

    /**
     * Validates that the key is not null or empty.
     * @param key The key to validate
     * @throws IllegalArgumentException if key is invalid
     */
    protected void validateKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
    }

    /**
     * Validates that the value is valid for comparison.
     * @param value The value to validate
     * @throws IllegalArgumentException if value is invalid
     */
    protected void validateValue(String value) {
        // Allow null values for comparison with null
        if (value == null) {
            return;
        }
        // Allow empty strings for hasProperty checks
        if (this instanceof HasPropertyFiltre) {
            return;
        }
    }

    /**
     * Retrieves the value associated with this filter's key from a resource.
     *
     * @param resource The resource map to get the value from
     * @return The value for the key, or null if not found
     */
    protected String getValue(Map<String, String> resource) {
        Objects.requireNonNull(resource, "Resource map cannot be null");
        return resource.get(key);
    }

    /**
     * Gets the key this filter operates on.
     *
     * @return The filter's key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the comparison value.
     *
     * @return The comparison value
     */
    public String getValue() {
        return value;
    }

    @Override
    public abstract boolean matches(Map<String, String> resource);

    @Override
    public abstract <T> T accept(FilterVisitor<T> visitor);
}
