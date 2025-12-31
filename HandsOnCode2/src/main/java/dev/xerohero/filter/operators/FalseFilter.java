package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

/**
 * A filter that always evaluates to {@code false}.
 * <p>
 * This is an identity element for OR operations and an absorbing element for AND operations.
 * It can be used to represent an impossible condition or to disable filtering.
 * </p>
 */
public final class FalseFilter implements Filter {

    /**
     * The singleton instance of FalseFilter.
     */
    public static final FalseFilter INSTANCE = new FalseFilter();

    public FalseFilter() {
        // Private constructor to enforce singleton pattern
    }

    /**
     * Always returns {@code false}, regardless of the input.
     *
     * @param resource The resource to check (ignored)
     * @return Always returns {@code false}
     */
    @Override
    public boolean matches(Map<String, String> resource) {
        return false;
    }

    @Override
    public String toString() {
        return "FALSE";
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Ensures that only one instance of FalseFilter exists.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return INSTANCE;
    }
}