package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * A composite filter that implements a logical OR operation across multiple filters.
 * This filter returns true if any of its component filters return true
 * for a given resource. The evaluation is short-circuiting - it will return true
 * as soon as it finds the first matching filter
 *
 * @param filters the array of filters to combine with OR logic (must not be null or empty)
 * @throws IllegalArgumentException if no filters are provided
 * @throws NullPointerException if the filters array or any filter is null
 */
public record OrFilter(Filter... filters) implements Filter {
    
    /**
     * Creates a new OR filter with the specified filters.
     *
     * @param filters the filters to combine with OR logic
     * @throws IllegalArgumentException if no filters are provided
     * @throws NullPointerException if the filters array or any filter is null
     */
    public OrFilter {
        if (filters == null || filters.length == 0) {
            throw new IllegalArgumentException("At least one filter is required");
        }
        for (Filter filter : filters) {
            Objects.requireNonNull(filter, "Filter cannot be null");
        }
    }

    /**
     * Evaluates the OR filter against the given resource.
     * <p>
     * Returns {@code true} if any component filter matches the resource, {@code false} otherwise.
     * The evaluation is short-circuiting - it returns {@code true} at the first matching filter.
     * </p>
     *
     * @param resource the resource to evaluate which coul be null)
     * @return {@code true} if any filter matches the resource, {@code false} otherwise
     */
    @Override
    public boolean matches(Map<String, String> resource) {
        // At least one filter must match
        for (Filter filter : filters) {
            if (filter.matches(resource)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a string representation of this OR filter.
     * <p>
     * The string is formatted as "(filter1 OR filter2 OR ...)" with proper
     * grouping for nested filters.
     * </p>
     *
     * @return a string representation of this filter
     */
    @Override
    public String toString() {
        return "(" + String.join(" OR ",
                Arrays.stream(filters)
                        .map(Filter::toString)
                        .toArray(String[]::new)) + ")";
    }

    /**
     * Accepts a visitor for implementing the visitor pattern.
     *
     * @param <T> the type of the result
     * @param visitor the visitor to accept (must not be null)
     * @return the result of the visitor's visit method
     * @throws NullPointerException if the visitor is null
     */
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        Objects.requireNonNull(visitor, "Visitor cannot be null");
        return visitor.visit(this);
    }
}