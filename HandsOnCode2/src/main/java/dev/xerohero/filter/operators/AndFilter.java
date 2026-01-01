package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * A composite filter that implements a logical AND operation across multiple filters.
 * This filter returns true only if all of its component filters return true
 * for a given resource. The evaluation is short-circuiting - it will stop at the first
 * filter that returns false.
 *
 * @param filters the array of filters to combine with AND logic (must not be null or empty)
 */
public record AndFilter(Filter... filters) implements Filter {

    /**
     * Creates a new AND filter with the specified filters.
     *
     * @param filters the filters to combine with AND logic
     * @throws IllegalArgumentException if no filters are provided
     * @throws NullPointerException     if the filters array or any filter is null
     */
    public AndFilter {
        if (filters == null || filters.length == 0) {
            throw new IllegalArgumentException("At least one filter is required");
        }
        for (Filter filter : filters) {
            Objects.requireNonNull(filter, "Filter cannot be null");
        }
    }

    /**
     * Evaluates the AND filter against the given resource.
     * Returns true if all component filters match the resource, false otherwise.
     * The evaluation is short-circuiting - it stops at the first filter that doesn't match.
     *
     * @param resource the resource to evaluate (may be null)
     * @return true if all filters match the resource, false otherwise
     */
    @Override
    public boolean matches(Map<String, String> resource) {
        // All filters must match
        for (Filter filter : filters) {
            if (!filter.matches(resource)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a string representation of this AND filter.
     * The string is formatted as "(filter1 AND filter2 AND ...)" with proper
     * grouping for nested filters.
     *
     * @return a string representation of this filter
     */
    @Override
    public String toString() {
        return "(" + String.join(" AND ", Arrays.stream(filters).map(Filter::toString).toArray(String[]::new)) + ")";
    }

    /**
     * Accepts a visitor to implement visitor pattern.
     *
     * @param <T>     the type of the result
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