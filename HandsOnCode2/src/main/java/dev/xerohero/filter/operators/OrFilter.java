package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Arrays;
import java.util.Map;

public record OrFilter(Filter... filters) implements Filter {

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

    @Override
    public String toString() {
        return "(" + String.join(" OR ",
                Arrays.stream(filters)
                        .map(Filter::toString)
                        .toArray(String[]::new)) + ")";
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}