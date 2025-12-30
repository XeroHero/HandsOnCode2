package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

public class NotFilter implements Filter {
    private final Filter filter;

    public NotFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        return !filter.matches(resource);
    }

    @Override
    public String toString() {
        return "NOT " + filter.toString();
    }

    public Filter getFilter() {
        return filter;
    }
    
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}