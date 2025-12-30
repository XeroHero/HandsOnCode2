package dev.xerohero.filter;

import dev.xerohero.filter.operators.AndFilter;
import dev.xerohero.filter.operators.OrFilter;
import dev.xerohero.filter.operators.NotFilter;
import dev.xerohero.filter.operators.TrueFilter;
import dev.xerohero.filter.operators.FalseFilter;
import dev.xerohero.filter.operators.comparison.EqualsFilter;
import dev.xerohero.filter.operators.comparison.GreaterThanFilter;
import dev.xerohero.filter.operators.comparison.LessThanFilter;
import dev.xerohero.filter.operators.comparison.HasPropertyFilter;
import dev.xerohero.filter.operators.comparison.RegexFilter;

/**
 * Builder class for creating filters in a fluent, readable way.
 * This makes it easier for clients to construct complex filters.
 */
public class FilterBuilder {

    // Private constructor to prevent instantiation
    private FilterBuilder() {}

    /**
     * Create an AND filter that matches when ALL sub-filters match.
     */
    public static Filter and(Filter... filters) {
        return new AndFilter(filters);
    }

    /**
     * Create an OR filter that matches when ANY sub-filter matches.
     */
    public static Filter or(Filter... filters) {
        return new OrFilter(filters);
    }

    /**
     * Create a NOT filter that inverts another filter.
     */
    public static Filter not(Filter filter) {
        return new NotFilter(filter);
    }

    /**
     * Create a filter that checks if a property exists.
     */
    public static Filter hasProperty(String key) {
        return new HasPropertyFilter(key);
    }

    /**
     * Create a filter that checks if a property equals a value (case-insensitive).
     */
    public static Filter equals(String key, String value) {
        return new EqualsFilter(key, value);
    }

    /**
     * Create a filter that checks if a property is less than a value.
     * Tries numeric comparison first, falls back to string comparison.
     */
    public static Filter lessThan(String key, String value) {
        return new LessThanFilter(key, value);
    }

    /**
     * Create a filter that checks if a property is greater than a value.
     * Tries numeric comparison first, falls back to string comparison.
     */
    public static Filter greaterThan(String key, String value) {
        return new GreaterThanFilter(key, value);
    }

    /**
     * Create a filter that checks if a property matches a regex pattern.
     */
    public static Filter matchesRegex(String key, String regex) {
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

    /**
     * Helper method to create the example filter from the PDF:
     * "all administrators older than 30"
     */
    public static Filter administratorsOlderThan30() {
        return and(
                equals("role", "administrator"),
                greaterThan("age", "30")
        );
    }

    /**
     * Create a filter for age range (inclusive).
     */
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