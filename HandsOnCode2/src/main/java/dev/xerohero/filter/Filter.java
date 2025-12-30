package dev.xerohero.filter;

import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

public interface Filter {
    boolean matches(Map<String, String> resource);

    <T> T accept(FilterVisitor<T> visitor);
}
