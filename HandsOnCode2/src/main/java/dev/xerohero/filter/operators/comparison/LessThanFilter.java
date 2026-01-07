package dev.xerohero.filter.operators.comparison;

import dev.xerohero.filter.operators.BaseComparisonFilter;
import dev.xerohero.filter.ValueComparator;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;
import java.util.Objects;

public class LessThanFilter extends BaseComparisonFilter {
    private final String value;

    public LessThanFilter(String key, String value) {
        super(key);
        Objects.requireNonNull(value, "Comparison value cannot be null");
        if (!isValidValue(value)) {
            throw new IllegalArgumentException("Value must be a valid number, boolean, or non-empty string");
        }
        this.value = value;
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource);
        if (actualValue == null) {
            return false; // Property doesn't exist
        }

        try {
            return ValueComparator.compare(actualValue, value) < 0;
        } catch (NumberFormatException e) {
            return false; // Invalid comparison, treat as non-matching
        }
    }

    @Override
    public String toString() {
        return String.format("(%s < '%s')", getKey(), value);
    }

    public String getValue() {
        return value;
    }

    private boolean isValidValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            new ValueComparator.TypedValue(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}