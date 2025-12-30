package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

public record NotFilter(Filter filter) implements Filter {

    @Override
    public boolean matches(Map<String, String> resource) {
        return !filter.matches(resource);
    }

    @Override
    public String toString() {
        return "NOT " + filter.toString();
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}