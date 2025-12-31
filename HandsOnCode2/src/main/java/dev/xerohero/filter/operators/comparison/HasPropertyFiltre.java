package dev.xerohero.filter.operators.comparison;

import dev.xerohero.filter.operators.BaseComparisonFilter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;
import java.util.Objects;

/**
 * A filter that checks if a resource contains a specific property.
 * <p>
 * This filter returns {@code true} if the resource contains the specified key,
 * regardless of the key's value (even if the value is null or empty).
 * </p>
 */
public class HasPropertyFiltre extends BaseComparisonFilter {

    /**
     * Creates a new has-property filter.
     *
     * @param key The property key to check for existence
     * @throws IllegalArgumentException if key is null or empty
     */
    public HasPropertyFiltre(String key) {
        super(key);
    }

    /**
     * Checks if the resource contains the specified key.
     *
     * @param resource The resource map to check
     * @return {@code true} if the key exists in the resource, {@code false} otherwise
     * @throws NullPointerException if the resource map is null
     */
    @Override
    public boolean matches(Map<String, String> resource) {
        Objects.requireNonNull(resource, "Resource map can't be null");
        // Check if the resource has the specified key
        return resource.containsKey(getKey()); // TODO: nested properties
    }

    @Override
    public String toString() {
        return String.format("(%s EXISTS)", getKey());
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        // FIXME: add null check for visitor
        return visitor.visit(this);
    }
}