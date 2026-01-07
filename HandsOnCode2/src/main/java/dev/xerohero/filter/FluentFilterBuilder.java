package dev.xerohero.filter;

import dev.xerohero.filter.operators.*;
import dev.xerohero.filter.operators.comparison.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Fluent API for building complex filter expressions.
 */
public final class FluentFilterBuilder {

    private FluentFilterBuilder() {
        // Prevent instantiation
    }

    public static PropertyFilterBuilder where(String property) {
        return new PropertyFilterBuilder(property);
    }

    public static Filter all() {
        return TrueFilter.INSTANCE;
    }

    public static Filter none() {
        return FalseFilter.INSTANCE;
    }

    public static Filter or(Filter... filters) {
        if (filters == null || filters.length == 0) {
            throw new IllegalArgumentException("At least one filter is required");
        }
        // Filter out nulls and FalseFilters, if all are FalseFilters, return FalseFilter
        List<Filter> validFilters = Arrays.stream(filters)
                .filter(Objects::nonNull)
                .filter(f -> f != FalseFilter.INSTANCE)
                .collect(Collectors.toList());

        if (validFilters.isEmpty()) {
            return FalseFilter.INSTANCE;
        }
        if (validFilters.size() == 1) {
            return validFilters.get(0);
        }
        return new OrFilter(validFilters.toArray(new Filter[0]));
    }

    public static Filter and(Filter... filters) {
        if (filters == null || filters.length == 0) {
            throw new IllegalArgumentException("At least one filter is required");
        }
        // Filter out nulls and TrueFilters, if all are TrueFilters, return TrueFilter
        List<Filter> validFilters = Arrays.stream(filters)
                .filter(Objects::nonNull)
                .filter(f -> f != TrueFilter.INSTANCE)
                .collect(Collectors.toList());

        if (validFilters.isEmpty()) {
            return TrueFilter.INSTANCE;
        }
        if (validFilters.size() == 1) {
            return validFilters.get(0);
        }
        return new AndFilter(validFilters.toArray(new Filter[0]));
    }

    public static Filter not(Filter filter) {
        return new NotFilter(Objects.requireNonNull(filter, "Filter cannot be null"));
    }

    public static final class PropertyFilterBuilder {
        private final String property;
        private Filter currentFilter;
        private Filter previousFilter;
        private boolean useOrForNextCombine = false;
        private boolean negateNext = false;

        public PropertyFilterBuilder(String property) {
            this.property = Objects.requireNonNull(property, "Property cannot be null");
            if (property.trim().isEmpty()) {
                throw new IllegalArgumentException("Property cannot be empty");
            }
        }

        private PropertyFilterBuilder withPrevious(Filter filter) {
            this.previousFilter = filter;
            return this;
        }

        public PropertyFilterBuilder is(String value) {
            return setCurrentFilter(new EqualsFilter(property, value));
        }

        public PropertyFilterBuilder is(Number value) {
            return setCurrentFilter(new EqualsFilter(property, String.valueOf(value)));
        }

        public PropertyFilterBuilder is(boolean value) {
            return setCurrentFilter(new EqualsFilter(property, String.valueOf(value)));
        }

        public PropertyFilterBuilder isNull() {
            return setCurrentFilter(new EqualsFilter(property, null));
        }

        public PropertyFilterBuilder isNotNull() {
            return setCurrentFilter(FluentFilterBuilder.not(new EqualsFilter(property, null)));
        }

        public PropertyFilterBuilder isNot(String value) {
            return setCurrentFilter(FluentFilterBuilder.not(new EqualsFilter(property, value)));
        }

        public PropertyFilterBuilder isNot(Number value) {
            return setCurrentFilter(FluentFilterBuilder.not(new EqualsFilter(property, String.valueOf(value))));
        }

        public PropertyFilterBuilder greaterThan(Number value) {
            return setCurrentFilter(new GreaterThanFilter(property, String.valueOf(value)));
        }

        public PropertyFilterBuilder greaterThan(String value) {
            return setCurrentFilter(new GreaterThanFilter(property, value));
        }

        public PropertyFilterBuilder greaterThanOrEqual(Number value) {
            return greaterThanOrEqual(String.valueOf(value));
        }

        public PropertyFilterBuilder greaterThanOrEqual(String value) {
            return setCurrentFilter(FluentFilterBuilder.or(
                    new EqualsFilter(property, value),
                    new GreaterThanFilter(property, value)
            ));
        }

        public PropertyFilterBuilder lessThan(Number value) {
            return setCurrentFilter(new LessThanFilter(property, String.valueOf(value)));
        }

        public PropertyFilterBuilder lessThan(String value) {
            return setCurrentFilter(new LessThanFilter(property, value));
        }

        public PropertyFilterBuilder lessThanOrEqual(Number value) {
            return lessThanOrEqual(String.valueOf(value));
        }

        public PropertyFilterBuilder lessThanOrEqual(String value) {
            return setCurrentFilter(FluentFilterBuilder.or(
                    new EqualsFilter(property, value),
                    new LessThanFilter(property, value)
            ));
        }

        public PropertyFilterBuilder matches(String pattern) {
            return setCurrentFilter(new RegexFilter(property, pattern));
        }

        public PropertyFilterBuilder contains(String substring) {
            return setCurrentFilter(new RegexFilter(property, ".*" + escapeRegex(substring) + ".*"));
        }

        public PropertyFilterBuilder startsWith(String prefix) {
            return setCurrentFilter(new RegexFilter(property, "^" + escapeRegex(prefix) + ".*"));
        }

        public PropertyFilterBuilder endsWith(String suffix) {
            return setCurrentFilter(new RegexFilter(property, ".*" + escapeRegex(suffix) + "$"));
        }

        @SafeVarargs
        public final PropertyFilterBuilder in(String... values) {
            return in(Arrays.asList(values));
        }

        public PropertyFilterBuilder in(Collection<String> values) {
            if (values == null || values.isEmpty()) {
                return setCurrentFilter(FalseFilter.INSTANCE);
            }
            List<Filter> filters = values.stream()
                    .map(value -> new EqualsFilter(property, value))
                    .collect(Collectors.toList());
            return setCurrentFilter(new OrFilter(filters.toArray(new Filter[0])));
        }

        @SafeVarargs
        public final PropertyFilterBuilder notIn(String... values) {
            return notIn(Arrays.asList(values));
        }

        public PropertyFilterBuilder notIn(Collection<String> values) {
            if (values == null || values.isEmpty()) {
                return setCurrentFilter(TrueFilter.INSTANCE);
            }
            List<Filter> filters = values.stream()
                    .map(value -> new EqualsFilter(property, value))
                    .collect(Collectors.toList());
            return setCurrentFilter(not(new OrFilter(filters.toArray(new Filter[0]))));
        }

        public PropertyFilterBuilder between(Number min, Number max) {
            return between(String.valueOf(min), String.valueOf(max));
        }

        public PropertyFilterBuilder between(String min, String max) {
            return setCurrentFilter(FluentFilterBuilder.and(
                    new GreaterThanOrEqualFilter(property, min),
                    new LessThanOrEqualFilter(property, max)
            ));
        }

        public PropertyFilterBuilder negate() {
            this.negateNext = true;
            return this;
        }

        private PropertyFilterBuilder setCurrentFilter(Filter filter) {
            if (filter == null) {
                throw new IllegalArgumentException("Filter cannot be null");
            }
            this.currentFilter = negateNext ? FluentFilterBuilder.not(filter) : filter;
            this.negateNext = false;
            return this;
        }

        private String escapeRegex(String input) {
            return input.replaceAll("([\\[\\](){}.*+?^$|])/", "\\$1");
        }

        public PropertyFilterBuilder exists() {
            currentFilter = new HasPropertyFiltre(property);
            return this;
        }

        public PropertyFilterBuilder doesNotExist() {
            currentFilter = FluentFilterBuilder.not(new HasPropertyFiltre(property));
            return this;
        }
        
        public Filter build() {
            if (currentFilter == null && previousFilter == null) {
                return all();
            }
            
            if (currentFilter != null) {
                return combineWithPrevious(currentFilter);
            }
            
            return previousFilter;
        }

        public PropertyFilterBuilder and(String nextProperty) {
            // If we have a current filter, combine it with previous first
            if (currentFilter != null) {
                previousFilter = combineWithPrevious(currentFilter);
                currentFilter = null;
            }
            
            // Create a new builder for the next property
            PropertyFilterBuilder nextBuilder = new PropertyFilterBuilder(nextProperty);
            
            // If we have a previous filter, pass it along
            if (previousFilter != null) {
                nextBuilder = nextBuilder.withPrevious(previousFilter);
            }
            
            return nextBuilder;
        }

        public PropertyFilterBuilder or(String nextProperty) {
            // If we have a current filter, combine it with previous first
            if (currentFilter != null) {
                previousFilter = combineWithPrevious(currentFilter);
                currentFilter = null;
            }
            
            // Create a new builder for the next property
            PropertyFilterBuilder nextBuilder = new PropertyFilterBuilder(nextProperty);
            
            // If we have a previous filter, pass it along and set the OR flag
            if (previousFilter != null) {
                nextBuilder = nextBuilder.withPrevious(previousFilter);
                nextBuilder.useOrForNextCombine = true;
            } else {
                // If no previous filter, start with a FalseFilter to make OR work correctly
                nextBuilder = nextBuilder.withPrevious(FalseFilter.INSTANCE);
                nextBuilder.useOrForNextCombine = true;
            }
            
            return nextBuilder;
        }

        public Filter and(Filter filter) {
            if (currentFilter != null) {
                previousFilter = combineWithPrevious(currentFilter);
                currentFilter = null;
            }
            
            if (previousFilter == null) {
                return filter;
            }
            
            return FluentFilterBuilder.and(previousFilter, filter);
        }

        public Filter or(Filter filter) {
            if (currentFilter != null) {
                previousFilter = combineWithPrevious(currentFilter);
                currentFilter = null;
            }
            
            if (previousFilter == null) {
                return filter;
            }
            
            return FluentFilterBuilder.or(previousFilter, filter);
        }

        private Filter combineWithPrevious(Filter currentFilter) {
            if (previousFilter == null) {
                return currentFilter;
            }
            
            // If we're supposed to use OR for this combination
            if (useOrForNextCombine) {
                useOrForNextCombine = false; // Reset the flag
                return FluentFilterBuilder.or(previousFilter, currentFilter);
            }
            
            // Default to AND combination
            return FluentFilterBuilder.and(previousFilter, currentFilter);
        }
    }
}