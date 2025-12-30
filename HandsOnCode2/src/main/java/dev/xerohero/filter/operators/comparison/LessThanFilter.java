package dev.xerohero.filter.operators.comparison;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.operators.BaseComparisonFilter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

public class LessThanFilter extends BaseComparisonFilter implements Filter {
    private final String value;

    public LessThanFilter(String key, String value) {
        super(key);
        this.value = value;
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource);
        if (actualValue == null) {
            return false; // Property doesn't exist
        }

        try {
            // Try numeric comparison first
            double actualNum = Double.parseDouble(actualValue);
            double targetNum = Double.parseDouble(value);
            return actualNum < targetNum;
        } catch (NumberFormatException e) {
            // Fall back to string comparison if not numeric
            return actualValue.compareTo(value) < 0;
        }
    }

    @Override
    public String toString() {
        return "(" + key + " < '" + value + "')";
    }

    public String getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}