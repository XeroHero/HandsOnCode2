package dev.xerohero.filter.operators.comparison;

import dev.xerohero.filter.operators.BaseComparisonFilter;
import dev.xerohero.filter.ValueComparator;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

public class EqualsFilter extends BaseComparisonFilter {
    private final String value;

    public EqualsFilter(String key, String value) {
        super(key);
        this.value = value; // Allow null values
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource);

        // Handle null cases
        if (actualValue == null) {
            return value == null; // Both are null -> true, otherwise false
        }
        if (value == null) {
            return false; // actualValue is not null but value is null
        }

        try {
            ValueComparator.TypedValue tv1 = new ValueComparator.TypedValue(actualValue);
            ValueComparator.TypedValue tv2 = new ValueComparator.TypedValue(value);

            // For strings, do case-insensitive comparison
            if (tv1.getType() == ValueComparator.ValueType.STRING &&
                    tv2.getType() == ValueComparator.ValueType.STRING) {
                return actualValue.equalsIgnoreCase(value);
            }

            // For other types, do exact comparison
            return ValueComparator.compare(actualValue, value) == 0;
        } catch (NumberFormatException e) {
            return false; // Invalid comparison, treat as non-matching
        }
    }

    @Override
    public String toString() {
        return String.format("(%s %s '%s')", getKey(), value == null ? "is" : "==", value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}