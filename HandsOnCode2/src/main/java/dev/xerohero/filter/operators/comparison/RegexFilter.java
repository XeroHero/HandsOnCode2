package dev.xerohero.filter.operators.comparison;

import dev.xerohero.filter.DebugLog;
import dev.xerohero.filter.operators.BaseComparisonFilter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * A filter that checks if a resource's value matches a regular expression (regex) pattern.
 * The matching is performed in a case-insensitive manner by default.
 *
 */
public class RegexFilter extends BaseComparisonFilter {
    private final Pattern pattern;
    private final String regex;

    /**
     * Creates a new regex filter with the specified pattern.
     *
     * @param key   The key to check in the resource
     * @param regex The regular expression pattern to match against
     * @throws IllegalArgumentException if the regex pattern is invalid
     * @throws NullPointerException     if either key or regex is null
     */
    public RegexFilter(String key, String regex) {
        super(key, regex);
        this.regex = Objects.requireNonNull(regex, "Regex pattern cannot be null");
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    /**
     * Gets the regex pattern string used by this filter.
     *
     * @return the regex pattern string
     */
    public String getPattern() {
        return regex;
    }

    /**
     * Gets the compiled Pattern object used for matching.
     *
     * @return the compiled Pattern object
     */
    public Pattern getCompiledPattern() {
        return pattern;
    }

    /**
     * Checks if the resource's value matches the regex pattern.
     *
     * @param resource The resource map containing the value to check
     * @return {@code true} if the value exists and matches the pattern, {@code false} otherwise
     * @throws NullPointerException if the resource map is null
     */
    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource); //value from resource key
        if (actualValue == null) { // Propery doesn't exist
            DebugLog.log("Property doesn't exist in resource map");
            return false;
        }
        return pattern.matcher(actualValue).matches();
    }

    @Override
    public String toString() {
        return String.format("(%s MATCHES '%s')", getKey(), regex);
    }

    /**
     * Gets the regular expression pattern used by this filter.
     *
     * @return The regex pattern as a string
     */
    public String getRegex() {
        return regex;
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        if (visitor == null) { //visitor is null
            DebugLog.log("Null visitor");
        }
        return Objects.requireNonNull(visitor).visit(this);
    }
}