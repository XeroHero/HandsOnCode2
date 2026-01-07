package dev.xerohero.filter.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import dev.xerohero.filter.Filter;
import dev.xerohero.filter.operators.*;
import dev.xerohero.filter.operators.comparison.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Deserializes JSON into Filter objects.
 */
public class FilterJsonDeserializer extends JsonDeserializer<Filter> {
    
    private final ObjectMapper objectMapper;
    
    public FilterJsonDeserializer() {
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public Filter deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        try {
            JsonNode node = p.getCodec().readTree(p);
            return deserializeFilterNode(node);
        } catch (JsonProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonMappingException(p, "Failed to deserialize filter", e);
        }
    }
    
    private Filter deserializeFilterNode(JsonNode node) throws JsonProcessingException {
        if (node == null) {
            throw new JsonMappingException("Filter node cannot be null");
        }
        if (!node.has("type")) {
            throw new JsonMappingException("Missing 'type' field in filter");
        }
        
        String type = node.get("type").asText();
        
        switch (type) {
            case "and":
                return deserializeAndFilter(node);
            case "or":
                return deserializeOrFilter(node);
            case "not":
                return deserializeNotFilter(node);
            case "true":
                return new TrueFilter();
            case "false":
                return new FalseFilter();
            case "equals":
                return deserializeEqualsFilter(node);
            case "lt":
                return deserializeLessThanFilter(node);
            case "gt":
                return deserializeGreaterThanFilter(node);
            case "regex":
                return deserializeRegexFilter(node);
            case "hasProperty":
                return deserializeHasPropertyFilter(node);
            default:
                throw new JsonMappingException("Unknown filter type: " + type);
        }
    }
    
    private AndFilter deserializeAndFilter(JsonNode node) throws JsonProcessingException {
        if (!node.has("filters") || !node.get("filters").isArray()) {
            throw new JsonProcessingException("AND filter must have a 'filters' array") {};
        }
        
        List<Filter> filters = StreamSupport.stream(node.get("filters").spliterator(), false)
                .map(filterNode -> {
                    try {
                        return deserializeFilterNode(filterNode);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to deserialize filter", e);
                    }
                })
                .collect(Collectors.toList());
                
        return new AndFilter(filters.toArray(new Filter[0]));
    }
    
    private OrFilter deserializeOrFilter(JsonNode node) throws JsonProcessingException {
        if (!node.has("filters") || !node.get("filters").isArray()) {
            throw new JsonProcessingException("OR filter must have a 'filters' array") {};
        }
        
        List<Filter> filters = StreamSupport.stream(node.get("filters").spliterator(), false)
                .map(filterNode -> {
                    try {
                        return deserializeFilterNode(filterNode);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to deserialize filter", e);
                    }
                })
                .collect(Collectors.toList());
                
        return new OrFilter(filters.toArray(new Filter[0]));
    }
    
    private NotFilter deserializeNotFilter(JsonNode node) throws JsonProcessingException {
        if (!node.has("filter")) {
            throw new JsonProcessingException("NOT filter must have a 'filter' property") {};
        }
        Filter filter = deserializeFilterNode(node.get("filter"));
        return new NotFilter(filter);
    }
    
    private EqualsFilter deserializeEqualsFilter(JsonNode node) throws JsonProcessingException {
        if (!node.has("key") || node.get("key").isNull()) {
            throw new JsonProcessingException("Equals filter must have a 'key' property") {};
        }
        String key = node.get("key").asText();
        String value = node.has("value") && !node.get("value").isNull() ? node.get("value").asText() : null;
        return new EqualsFilter(key, value);
    }
    
    private LessThanFilter deserializeLessThanFilter(JsonNode node) throws JsonProcessingException {
        if (!node.has("key") || node.get("key").isNull()) {
            throw new JsonProcessingException("LessThan filter must have a 'key' property") {};
        }
        if (!node.has("value") || node.get("value").isNull()) {
            throw new JsonProcessingException("LessThan filter must have a 'value' property") {};
        }
        String key = node.get("key").asText();
        String value = node.get("value").asText();
        return new LessThanFilter(key, value);
    }
    
    private GreaterThanFilter deserializeGreaterThanFilter(JsonNode node) throws JsonProcessingException {
        if (!node.has("key") || node.get("key").isNull()) {
            throw new JsonProcessingException("GreaterThan filter must have a 'key' property") {};
        }
        if (!node.has("value") || node.get("value").isNull()) {
            throw new JsonProcessingException("GreaterThan filter must have a 'value' property") {};
        }
        String key = node.get("key").asText();
        String value = node.get("value").asText();
        return new GreaterThanFilter(key, value);
    }
    
    private RegexFilter deserializeRegexFilter(JsonNode node) throws JsonProcessingException {
        if (!node.has("key") || node.get("key").isNull()) {
            throw new JsonProcessingException("Regex filter must have a 'key' property") {};
        }
        if (!node.has("pattern") || node.get("pattern").isNull()) {
            throw new JsonProcessingException("Regex filter must have a 'pattern' property") {};
        }
        String key = node.get("key").asText();
        String pattern = node.get("pattern").asText();
        return new RegexFilter(key, pattern);
    }
    
    private HasPropertyFiltre deserializeHasPropertyFilter(JsonNode node) throws JsonProcessingException {
        if (!node.has("key") || node.get("key").isNull()) {
            throw new JsonProcessingException("HasProperty filter must have a 'key' property") {};
        }
        String key = node.get("key").asText();
        return new HasPropertyFiltre(key);
    }
}
