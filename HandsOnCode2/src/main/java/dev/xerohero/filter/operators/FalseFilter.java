package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

public class FalseFilter implements Filter {
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
}