package dev.xerohero.filter.operators.comparison;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.operators.BaseComparisonFilter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;
import java.util.regex.Pattern;

public class RegexFilter extends BaseComparisonFilter implements Filter {
    private final Pattern pattern;
    private final String regex;

    public RegexFilter(String key, String regex) {
        super(key);
        this.regex = regex;
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource);
        if (actualValue == null) {
            return false; // Property doesn't exist
        }
        return pattern.matcher(actualValue).matches();
    }

    @Override
    public String toString() {
        return "(" + key + " MATCHES '" + regex + "')";
    }

    public String getRegex() {
        return regex;
    }
    
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}