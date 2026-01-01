package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

/**
 * A filter that inverts the result of another filter.
 * <p>
 * This filter acts as a logical NOT operation, returning the opposite of the input filter.
 * </p>
 *
 * @param filter The filter to negate (must not be null)
 */
public record NotFilter(Filter filter) implements Filter {

    /**
     * Creates a new NOT filter that inverts the result of the specified filter.
     *
     * @param filter the filter to negate
     * @throws NullPointerException if the filter is null
     */
    public NotFilter {
        if (filter == null) {
            throw new NullPointerException("Filter cannot be null");
        }
    }

    /**
     * Evaluates the filter against the given resource by negating the result
     * of the underlying filter.
     *
     * @param resource the resource to evaluate (may be null)
     * @return true if the underlying filter returns false,
     * and vice versa
     */
    @Override
    public boolean matches(Map<String, String> resource) {
        return !filter.matches(resource);
    }

    /**
     * Returns a string representation of this NOT filter.
     *
     * @return a string in the format "NOT [filter]"
     */
    @Override
    public String toString() {
        return "NOT " + filter.toString();
    }

    /**
     * Accepts a visitor for implementing the visitor pattern.
     *
     * @param <T>     the type of the result
     * @param visitor the visitor to accept (must not be null)
     * @return the result of the visitor's visit method
     * @throws NullPointerException if the visitor is null
     */
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        if (visitor == null) {
            throw new NullPointerException("Visitor cannot be null");
        }
        return visitor.visit(this);
    }
}