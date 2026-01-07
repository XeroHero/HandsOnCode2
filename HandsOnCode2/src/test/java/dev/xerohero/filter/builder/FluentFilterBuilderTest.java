package dev.xerohero.filter.builder;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.FluentFilterBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Fluent Filter Builder Tests")
class FluentFilterBuilderTest {
    
    private Map<String, String> createTestUser() {
        Map<String, String> user = new HashMap<>();
        user.put("name", "John");
        user.put("age", "30");
        user.put("status", "active");
        user.put("role", "user");
        user.put("email", "john@example.com");
        return user;
    }

    @Test
    @DisplayName("should build simple equality filter")
    void testSimpleEquality() {
        // When
        Filter filter = FluentFilterBuilder.where("name").is("John").build();
        
        // Then
        Map<String, String> resource = createTestUser();
        assertTrue(filter.matches(resource), "Should match when name is John");
        
        resource.put("name", "Jane");
        assertFalse(filter.matches(resource), "Should not match when name is not John");
    }
    
    @Test
    @DisplayName("should chain multiple conditions with AND")
    void testAndChaining() {
        // When
        Filter filter = FluentFilterBuilder
            .where("age").greaterThan("25")
            .and("status").is("active")
            .build();
        
        // Then
        Map<String, String> resource = createTestUser();
        assertTrue(filter.matches(resource), "Should match when both conditions are true");
        
        resource.put("status", "inactive");
        assertFalse(filter.matches(resource), "Should not match when status is not active");
    }
    
    @Test
    @DisplayName("should chain multiple conditions with OR")
    void testOrChaining() {
        // When
        Filter filter = FluentFilterBuilder
            .where("role").is("admin")
            .or("age").greaterThan("18")
            .build();
        
        // Then
        Map<String, String> resource = createTestUser();
        assertTrue(filter.matches(resource), "Should match when age > 18");
        
        resource.put("age", "15");
        assertFalse(filter.matches(resource), "Should not match when age <= 18 and role is not admin");
        
        resource.put("role", "admin");
        assertTrue(filter.matches(resource), "Should match when role is admin");
    }
    
    @Test
    @DisplayName("should handle complex nested conditions")
    void testComplexNestedConditions() {
        // When
        Filter filter = FluentFilterBuilder
            .where("role").is("admin")
            .or("age").greaterThanOrEqual("18")
            .and("status").is("active")
            .build();
        
        // Then
        Map<String, String> resource = createTestUser();
        assertTrue(filter.matches(resource), "Should match when role is admin");
        
        resource.put("role", "user");
        resource.put("age", "20");
        assertTrue(filter.matches(resource), "Should match when age >= 18 and status is active");
        
        resource.put("status", "inactive");
        assertFalse(filter.matches(resource), "Should not match when status is inactive");
    }
    
    @Test
    @DisplayName("should build regex filter")
    void testRegexFilter() {
        // When
        Filter filter = FluentFilterBuilder
            .where("email")
            .matches(".*@example\\.com$")
            .build();
        
        // Then
        Map<String, String> resource = createTestUser();
        assertTrue(filter.matches(resource), "Should match email pattern");
        
        resource.put("email", "invalid-email");
        assertFalse(filter.matches(resource), "Should not match invalid email");
    }
    
    @Test
    @DisplayName("should handle negation")
    void testNegation() {
        // When
        Filter filter = FluentFilterBuilder
            .where("role")
            .isNot("admin")
            .build();
        
        // Then
        Map<String, String> resource = createTestUser();
        assertTrue(filter.matches(resource), "Should match when role is not admin");
        
        resource.put("role", "admin");
        assertFalse(filter.matches(resource), "Should not match when role is admin");
    }
}
