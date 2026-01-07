package dev.xerohero.filter.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.xerohero.filter.Filter;

import java.io.IOException;

/**
 * Utility class for serializing and deserializing Filter objects to/from JSON.
 */
public class FilterSerialization {
    
    private static final ObjectMapper objectMapper = createObjectMapper();
    
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Configure the mapper
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        // Register our custom serializers/deserializers
        SimpleModule module = new SimpleModule("FilterModule");
        module.addSerializer(Filter.class, new FilterJsonSerializer());
        module.addDeserializer(Filter.class, new FilterJsonDeserializer());
        
        mapper.registerModule(module);
        return mapper;
    }
    
    /**
     * Serializes a Filter to a JSON string.
     *
     * @param filter the filter to serialize
     * @return JSON string representation of the filter
     * @throws FilterSerializationException if serialization fails
     * @throws IllegalArgumentException if the filter is null
     */
    public static String toJson(Filter filter) throws FilterSerializationException {
        if (filter == null) {
            throw new IllegalArgumentException("Filter cannot be null");
        }
        try {
            return objectMapper.writeValueAsString(filter);
        } catch (JsonProcessingException e) {
            throw new FilterSerializationException("Failed to serialize filter to JSON", e);
        }
    }
    
    /**
     * Deserializes a Filter from a JSON string.
     *
     * @param json the JSON string to deserialize
     * @return the deserialized Filter
     * @throws FilterSerializationException if deserialization fails or JSON is invalid
     * @throws IllegalArgumentException if the JSON string is null or empty
     */
    public static Filter fromJson(String json) throws FilterSerializationException {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }
        try {
            return objectMapper.readValue(json, Filter.class);
        } catch (JsonMappingException e) {
            throw new FilterSerializationException("Failed to map JSON to Filter: " + e.getOriginalMessage(), e);
        } catch (JsonProcessingException e) {
            throw new FilterSerializationException("Failed to process JSON: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gets the configured ObjectMapper instance for advanced usage.
     *
     * @return the configured ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper.copy();
    }
}
