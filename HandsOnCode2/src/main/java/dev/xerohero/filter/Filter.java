package dev.xerohero.filter;

import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

/**
 * Core interface for all filter types.
 * Implement this to create custom filter conditions.
 */
public interface Filter {
    /**
     * Tests if the given resource matches this filter's conditions.
     *
     * @param resource The resource to test, represented as key-value pairs
     * @return true if the resource matches, false otherwise
     */
    boolean matches(Map<String, String> resource);

    /**
     * Accepts a visitor for this filter.
     *
     * @param <T>     The return type of the visitor
     * @param visitor The visitor to accept
     * @return The result of visiting this filter
     */
    <T> T accept(FilterVisitor<T> visitor);
}
