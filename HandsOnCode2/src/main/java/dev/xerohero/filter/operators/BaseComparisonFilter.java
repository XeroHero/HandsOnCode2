package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;
import java.util.Objects;

/**
 * Base class for filters that compare a single key-value pair.
 * Handles common functionality for key-based comparisons.
 */
public abstract class BaseComparisonFilter implements Filter {
    protected final String key;

    /**
     * Creates a filter that will compare values using the specified key.
     *
     * @param key The key to use for comparison in resource maps
     * @throws IllegalArgumentException if key is null or empty
     */
    protected BaseComparisonFilter(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        this.key = key;
    }

    /**
     * Retrieves the value associated with this filter's key from a resource.
     *
     * @param resource The resource map to get the value from
     * @return The value for the key, or null if not found
     * @throws NullPointerException if the resource map is null
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

    @Override
    public abstract boolean matches(Map<String, String> resource);

    @Override
    public abstract <T> T accept(FilterVisitor<T> visitor);
}
