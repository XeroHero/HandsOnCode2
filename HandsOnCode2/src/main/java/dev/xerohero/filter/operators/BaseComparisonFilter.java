package dev.xerohero.filter.operators;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.util.Map;

public abstract class BaseComparisonFilter implements Filter {
    protected final String key;

    protected BaseComparisonFilter(String key) {
        this.key = key;
    }

    protected String getValue(Map<String, String> resource) {
        return resource.get(key);
    }

    public String getKey() {
        return key;
    }
    
    @Override
    public abstract boolean matches(Map<String, String> resource);
    
    @Override
    public abstract <T> T accept(FilterVisitor<T> visitor);
}
