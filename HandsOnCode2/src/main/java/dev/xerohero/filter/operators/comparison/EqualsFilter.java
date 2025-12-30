package dev.xerohero.filter.operators.comparison;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.operators.BaseComparisonFilter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

public class EqualsFilter extends BaseComparisonFilter implements Filter {
    private final String value;

    public EqualsFilter(String key, String value) {
        super(key);
        this.value = value;
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource);
        if (actualValue == null) {
            return false; // Property doesn't exist
        }
        // Property values are case-insensitive (per requirements)
        return actualValue.equalsIgnoreCase(value);
    }

    @Override
    public String toString() {
        return "(" + key + " == '" + value + "')";
    }

    public String getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }


}