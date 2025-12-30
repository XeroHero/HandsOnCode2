package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Arrays;
import java.util.Map;

public class AndFilter implements Filter {
    private final Filter[] filters;

    public AndFilter(Filter... filters) {
        this.filters = filters;
    }

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

    @Override
    public String toString() {
        return "(" + String.join(" AND ",
                Arrays.stream(filters)
                        .map(Filter::toString)
                        .toArray(String[]::new)) + ")";
    }

    public Filter[] getFilters() {
        return filters;
    }
    
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}