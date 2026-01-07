package dev.xerohero.filter.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.xerohero.filter.Filter;
import dev.xerohero.filter.operators.*;
import dev.xerohero.filter.operators.comparison.*;
import dev.xerohero.filter.visitor.FilterVisitor;

import java.io.IOException;
import java.util.*;

/**
 * Serializes Filter objects to JSON format.
 */
public class FilterJsonSerializer extends JsonSerializer<Filter> {
    
    @Override
    public void serialize(Filter value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            throw new IOException("Cannot serialize null filter");
        }
        
        try {
            Map<String, Object> filterMap = value.accept(new FilterVisitor<Map<String, Object>>() {
            @Override
            public Map<String, Object> visit(AndFilter filter) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "and");
                map.put("filters", filter.filters());
                return map;
            }

            @Override
            public Map<String, Object> visit(OrFilter filter) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "or");
                map.put("filters", filter.filters());
                return map;
            }

            @Override
            public Map<String, Object> visit(NotFilter filter) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "not");
                map.put("filter", filter.filter());
                return map;
            }

            @Override
            public Map<String, Object> visit(TrueFilter filter) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "true");
                return map;
            }

            @Override
            public Map<String, Object> visit(FalseFilter filter) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "false");
                return map;
            }

            @Override
            public Map<String, Object> visit(EqualsFilter filter) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "equals");
                map.put("key", filter.getKey());
                map.put("value", filter.getValue());
                return map;
            }

            @Override
            public Map<String, Object> visit(LessThanFilter filter) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "lt");
                map.put("key", filter.getKey());
                map.put("value", filter.getValue());
                return map;
            }

            @Override
            public Map<String, Object> visit(GreaterThanFilter filter) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "gt");
                map.put("key", filter.getKey());
                map.put("value", filter.getValue());
                return map;
            }

            @Override
            public Map<String, Object> visit(RegexFilter filter) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "regex");
                map.put("key", filter.getKey());
                map.put("pattern", filter.getPattern());
                return map;
            }

            @Override
            public Map<String, Object> visit(NotEqualsFilter filter) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "not_equals");
                map.put("key", filter.getKey());
                map.put("value", filter.getValue());
                return map;
            }

            @Override
            public Map<String, Object> visit(GreaterThanOrEqualFilter filter) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "greater_than_or_equal");
                map.put("key", filter.getKey());
                map.put("value", filter.getValue());
                return map;
            }

            @Override
            public Map<String, Object> visit(LessThanOrEqualFilter filter) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "less_than_or_equal");
                map.put("key", filter.getKey());
                map.put("value", filter.getValue());
                return map;
            }

            @Override
            public Map<String, Object> visit(HasPropertyFiltre filter) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "hasProperty");
                map.put("key", filter.getKey());
                return map;
            }
            });
            
            gen.writeObject(filterMap);
        } catch (Exception e) {
            throw new IOException("Failed to serialize filter: " + e.getMessage(), e);
        }
    }

    @Override
    public Class<Filter> handledType() {
        return Filter.class;
    }
    
    /**
     * Creates a map with the common fields for a filter.
     * 
     * @param type the filter type
     * @return a new map with the type field set
     */
    private Map<String, Object> createFilterMap(String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        return map;
    }
}
