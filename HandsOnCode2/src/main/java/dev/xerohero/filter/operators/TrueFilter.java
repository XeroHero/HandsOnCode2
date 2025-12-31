package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

/**
 * A filter that always evaluates to {@code true}.
 * <p>
 * This is an identity element for AND operations and a neutral element for OR operations.
 * It can be used as a default or fallback filter when no filtering is needed.
 * </p>
 */
public final class TrueFilter implements Filter {
    
    /**
     * The singleton instance of TrueFilter.
     */
    public static final TrueFilter INSTANCE = new TrueFilter();
    
    public TrueFilter() {
        // Private constructor to enforce singleton pattern
    }
    
    /**
     * Always returns {@code true}, regardless of the input.
     *
     * @param resource The resource to check (ignored)
     * @return Always returns {@code true}
     */
    @Override
    public boolean matches(Map<String, String> resource) {
        return true;
    }

    @Override
    public String toString() {
        return "TRUE";
    }
    
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    /**
     * Ensures that only one instance of TrueFilter exists.
     *
     * @return the singleton instance
     */
    protected Object readResolve() {
        return INSTANCE;
    }
}