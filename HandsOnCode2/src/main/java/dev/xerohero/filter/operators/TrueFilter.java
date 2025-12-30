package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

public class TrueFilter implements Filter {
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
}