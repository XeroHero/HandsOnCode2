package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Arrays;
import java.util.Map;

public record AndFilter(Filter... filters) implements Filter {

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

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}