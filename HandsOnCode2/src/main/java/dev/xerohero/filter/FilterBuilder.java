package dev.xerohero.filter;

import dev.xerohero.filter.operators.*;
import dev.xerohero.filter.operators.comparison.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A fluent builder for creating filter expressions.
 * Example usage:
 * <pre>
 * Filter filter = FilterBuilder.and()
 *     .equalTo("name", "John")
 *     .greaterThan("age", "30")
 *     .build();
 * </pre>
 */
public class FilterBuilder {
    private final List<Filter> filters = new ArrayList<>();
    private final boolean isAnd;

    private FilterBuilder(boolean isAnd) {
        this.isAnd = isAnd;
    }

    // Factory methods
    public static FilterBuilder and() {
        return new FilterBuilder(true);
    }

    public static FilterBuilder or() {
        return new FilterBuilder(false);
    }

    public static Filter equalTo(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        return new EqualsFilter(key, value);
    }

    public static Filter notEquals(String key, String value) {
        return not(new EqualsFilter(key, value));
    }

    public static Filter lessThan(String key, String value) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");
        return new LessThanFilter(key, value);
    }

    public static Filter lessThanOrEqual(String key, String value) {
        return FilterBuilder.orFilter(new EqualsFilter(key, value), new LessThanFilter(key, value));
    }

    public static Filter greaterThan(String key, String value) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");
        return new GreaterThanFilter(key, value);
    }

    public static Filter greaterThanOrEqual(String key, String value) {
        return FilterBuilder.orFilter(new EqualsFilter(key, value), new GreaterThanFilter(key, value));
    }

    public static Filter matchesRegex(String key, String pattern) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(pattern, "Pattern cannot be null");
        return new RegexFilter(key, pattern);
    }

    public static Filter hasProperty(String key) {
        return new HasPropertyFiltre(key);
    }

    public static Filter not(Filter filter) {
        if (filter == null) {
            throw new NullPointerException("Filter cannot be null");
        }
        return new NotFilter(filter);
    }
    
    /**
     * Parses a filter expression string into a Filter object.
     * 
     * Example expressions:
     * - name = "John"
     * - age > 25
     * - status = "active" AND (role = "admin" OR role = "superuser")
     * 
     * @param expression The filter expression to parse
     * @return A Filter object representing the parsed expression
     * @throws FilterParseException if the expression is invalid
     */
    public static Filter parse(String expression) {
        return dev.xerohero.filter.parser.FilterParser.parse(expression);
    }

    public static Filter andFilter(Filter... filters) {
        if (filters == null || filters.length == 0) {
            throw new IllegalArgumentException("At least one filter is required");
        }
        return filters.length == 1 ? filters[0] : new AndFilter(filters);
    }

    public static Filter orFilter(Filter... filters) {
        if (filters == null || filters.length == 0) {
            throw new IllegalArgumentException("At least one filter is required");
        }
        return filters.length == 1 ? filters[0] : new OrFilter(filters);
    }

    public static Filter all() {
        return TrueFilter.INSTANCE;
    }

    public static Filter none() {
        return FalseFilter.INSTANCE;
    }

    // Chaining methods
    public FilterBuilder withEqualTo(String key, String value) {
        filters.add(FilterBuilder.equalTo(key, value));
        return this;
    }

    public FilterBuilder withNotEquals(String key, String value) {
        filters.add(FilterBuilder.notEquals(key, value));
        return this;
    }

    public FilterBuilder withLessThan(String key, String value) {
        filters.add(FilterBuilder.lessThan(key, value));
        return this;
    }

    public FilterBuilder withLessThanOrEqual(String key, String value) {
        filters.add(FilterBuilder.lessThanOrEqual(key, value));
        return this;
    }

    public FilterBuilder withGreaterThan(String key, String value) {
        filters.add(FilterBuilder.greaterThan(key, value));
        return this;
    }

    public FilterBuilder withGreaterThanOrEqual(String key, String value) {
        filters.add(FilterBuilder.greaterThanOrEqual(key, value));
        return this;
    }

    public FilterBuilder withMatchesRegex(String key, String pattern) {
        filters.add(FilterBuilder.matchesRegex(key, pattern));
        return this;
    }

    public FilterBuilder withHasProperty(String key) {
        filters.add(hasProperty(key));
        return this;
    }

    public FilterBuilder and(Filter... additionalFilters) {
        if (additionalFilters != null && additionalFilters.length > 0) {
            filters.add(FilterBuilder.andFilter(additionalFilters));
        }
        return this;
    }

    public FilterBuilder or(Filter... additionalFilters) {
        if (additionalFilters != null && additionalFilters.length > 0) {
            filters.add(FilterBuilder.orFilter(additionalFilters));
        }
        return this;
    }

    public Filter build() {
        if (filters.isEmpty()) {
            return isAnd ? all() : none();
        }
        if (filters.size() == 1) {
            return filters.get(0);
        }
        Filter[] filterArray = filters.toArray(new Filter[0]);
        return isAnd ? new AndFilter(filterArray) : new OrFilter(filterArray);
    }
}