package dev.xerohero.filter;

import dev.xerohero.filter.operators.*;
import dev.xerohero.filter.operators.comparison.*;

/**
 * Builder class for creating filters in a fluent, readable way, using variable arguments (`Filter... filters`).
 * This makes it easier for clients to construct complex filters.
 */
public class FilterBuilder {

    // Private constructor to prevent instantiation
    private FilterBuilder() {}

    /**
     * Create an AND filter that matches when ALL sub-filters match.
     */
    public static Filter and(Filter... filters) {
        if (filters == null || filters.length == 0) {
            throw new IllegalArgumentException("At least one filter is required for AND operation");
        }
        for (Filter filter : filters) {
            if (filter == null) {
                throw new IllegalArgumentException("Filter cannot be null");
            }
        }
        return new AndFilter(filters);
    }

    /**
     * Create an OR filter that matches when ANY sub-filter matches.
     */
    public static Filter or(Filter... filters) {
        if (filters == null || filters.length == 0) {
            throw new IllegalArgumentException("At least one filter is required for OR operation");
        }
        for (Filter filter : filters) {
            if (filter == null) {
                throw new IllegalArgumentException("Filter cannot be null");
            }
        }
        return new OrFilter(filters);
    }

    /**
     * Create a NOT filter that inverts another filter.
     */
    public static Filter not(Filter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("Filter cannot be null");
        }
        return new NotFilter(filter);
    }

    /**
     * Create a filter that checks if a property exists.
     */
    public static Filter hasProperty(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        return new HasPropertyFiltre(key);
    }

    /**
     * Create a filter that checks if a property equals a value (case-insensitive).
     */
    public static Filter equals(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        // Note: value can be null for equals filter (to check for null values)
        return new EqualsFilter(key, value);
    }

    /**
     * Create a filter that checks if a property is less than a value.
     * Tries numeric comparison first, falls back to string comparison.
     */
    public static Filter lessThan(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        return new LessThanFilter(key, value);
    }

    /**
     * Create a filter that checks if a property is greater than a value.
     * Tries numeric comparison first, falls back to string comparison.
     */
    public static Filter greaterThan(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        return new GreaterThanFilter(key, value);
    }

    /**
     * Create a filter that checks if a property matches a regex pattern.
     */
    public static Filter matchesRegex(String key, String regex) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (regex == null) {
            throw new IllegalArgumentException("Regex pattern cannot be null");
        }
        return new RegexFilter(key, regex);
    }

    /**
     * Create a filter that always matches.
     */
    public static Filter alwaysTrue() {
        return new TrueFilter();
    }

    /**
     * Create a filter that never matches.
     */
    public static Filter alwaysFalse() {
        return new FalseFilter();
    }

    // test filter
    public static Filter administratorsOlderThan30() {
        return and(
                equals("role", "administrator"),
                greaterThan("age", "30")
        );
    }


//test filter
    public static Filter ageBetween(int min, int max) {
        return and(
                greaterThan("age", String.valueOf(min - 1)), // > min-1 means >= min
                lessThan("age", String.valueOf(max + 1))     // < max+1 means <= max
        );
    }

    /**
     * Create a filter for role-based access.
     */
    public static Filter hasRole(String... roles) {
        if (roles.length == 0) {
            return alwaysFalse();
        }

        Filter[] roleFilters = new Filter[roles.length];
        for (int i = 0; i < roles.length; i++) {
            roleFilters[i] = equals("role", roles[i]);
        }
        return or(roleFilters);
    }
}