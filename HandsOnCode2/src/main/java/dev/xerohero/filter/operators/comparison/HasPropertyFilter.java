package dev.xerohero.filter.operators.comparison;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.operators.BaseComparisonFilter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

public class HasPropertyFilter extends BaseComparisonFilter {

    public HasPropertyFilter(String key) {
        super(key);
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        return resource.containsKey(key);
    }

    @Override
    public String toString() {
        return "(" + key + " EXISTS)";
    }
    
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}