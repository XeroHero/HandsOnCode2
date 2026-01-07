package dev.xerohero.filter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

class FluentFilterBuilderTest {
    
    @Test
    void testSimpleEquality() {
        // Test: name == "John"
        Filter filter = FluentFilterBuilder.where("name").is("John").build();
        
        Map<String, String> resource = new HashMap<>();
        resource.put("name", "John");
        assertTrue(filter.matches(resource), "Should match when name is John");
        
        resource.put("name", "Jane");
        assertFalse(filter.matches(resource), "Should not match when name is not John");
    }
    
    @Test
    void testAndChaining() {
        // Test: age > 25 AND status == "active"
        Filter filter = FluentFilterBuilder.where("age").greaterThan("25")
                                         .and("status").is("active").build();
        
        Map<String, String> resource = new HashMap<>();
        resource.put("age", "30");
        resource.put("status", "active");
        assertTrue(filter.matches(resource), "Should match when both conditions are true");
        
        resource.put("status", "inactive");
        assertFalse(filter.matches(resource), "Should not match when status is not active");
    }
    
    @Test
    void testOrChaining() {
        // Test: type == "admin" OR (age > 18 AND hasPermission == "true")
        Filter adminFilter = FluentFilterBuilder.where("type").is("admin").build();
        Filter ageAndPermissionFilter = FluentFilterBuilder.where("age").greaterThan("18")
                .and("hasPermission").is("true").build();
        Filter filter = FluentFilterBuilder.or(adminFilter, ageAndPermissionFilter);
        
        Map<String, String> resource = new HashMap<>();
        
        // Case 1: type is admin
        resource.put("type", "admin");
        assertTrue(filter.matches(resource), "Should match when type is admin");
        
        // Case 2: not admin but meets other conditions
        resource.remove("type");
        resource.put("age", "25");
        resource.put("hasPermission", "true");
        assertTrue(filter.matches(resource), "Should match when age > 18 and hasPermission is true");
        
        // Case 3: none of the conditions are met
        resource.put("age", "15");
        resource.put("hasPermission", "false");
        assertFalse(filter.matches(resource), "Should not match when no conditions are met");
    }
    
    @Test
    void testNestedConditions() {
        // Test: (status == "active" AND (age > 18 OR hasParentalConsent == "true"))
        Filter filter = FluentFilterBuilder.where("status").is("active")
                                         .and(FluentFilterBuilder.where("age").greaterThan("18")
                                                              .or("hasParentalConsent").is("true").build());
        
        Map<String, String> resource = new HashMap<>();
        
        // Case 1: status active and age > 18
        resource.put("status", "active");
        resource.put("age", "25");
        assertTrue(filter.matches(resource), "Should match when status is active and age > 18");
        
        // Case 2: status active and has parental consent
        resource.put("age", "15");
        resource.put("hasParentalConsent", "true");
        assertTrue(filter.matches(resource), "Should match when status is active and has parental consent");
        
        // Case 3: status inactive
        resource.put("status", "inactive");
        assertFalse(filter.matches(resource), "Should not match when status is inactive");
    }
    
    @Test
    void testStringMatching() {
        // Test: name matches "J.*n"
        Filter filter = FluentFilterBuilder.where("name").matches("J.*n").build();
        
        Map<String, String> resource = new HashMap<>();
        resource.put("name", "John");
        assertTrue(filter.matches(resource), "Should match when name is John");
        
        resource.put("name", "Jane");
        assertFalse(filter.matches(resource), "Should not match when name is not John");
    }
    
    @Test
    void testNotOperator() {
        // Test: NOT (age < 18)
        Filter filter = FluentFilterBuilder.not(
                FluentFilterBuilder.where("age").lessThan("18").build()
        );
        
        Map<String, String> resource = new HashMap<>();
        resource.put("age", "30");
        assertTrue(filter.matches(resource), "Should match when age is not less than 18");
        
        resource.put("age", "15");
        assertFalse(filter.matches(resource), "Should not match when age is less than 18");
    }
    
    @Test
    void testRegexMatching() {
        // Test: email matches email pattern
        Filter filter = FluentFilterBuilder.where("email").matches("^[A-Za-z0-9+_.-]+@(.+)$").build();
        
        Map<String, String> resource = new HashMap<>();
        resource.put("email", "test@example.com");
        assertTrue(filter.matches(resource), "Should match valid email");
        
        resource.put("email", "invalid-email");
        assertFalse(filter.matches(resource), "Should not match invalid email");
    }
    
    @Test
    void testPropertyExistence() {
        // Test: has property "email"
        Filter existsFilter = FluentFilterBuilder.where("email").exists().build();
        Filter notExistsFilter = FluentFilterBuilder.where("email").doesNotExist().build();
        
        Map<String, String> resource = new HashMap<>();
        
        // Case 1: property exists
        resource.put("email", "test@example.com");
        assertTrue(existsFilter.matches(resource), "Exists filter should match when property exists");
        assertFalse(notExistsFilter.matches(resource), "Not-exists filter should not match when property exists");
        
        // Case 2: property doesn't exist
        resource.remove("email");
        assertFalse(existsFilter.matches(resource), "Exists filter should not match when property doesn't exist");
        assertTrue(notExistsFilter.matches(resource), "Not-exists filter should match when property doesn't exist");
    }
    
    @Test
    void testCombinedOperators() {
        // Test: (status == "active" AND age >= 18) OR (type == "admin")
        Filter activeAndAdultFilter = FluentFilterBuilder.where("status").is("active")
                .and("age").greaterThanOrEqual("18").build();
        Filter adminFilter = FluentFilterBuilder.where("type").is("admin").build();
        Filter filter = FluentFilterBuilder.or(activeAndAdultFilter, adminFilter);
        
        Map<String, String> resource = new HashMap<>();
        
        // Case 1: status active and age >= 18
        resource.put("status", "active");
        resource.put("age", "25");
        assertTrue(filter.matches(resource), "Should match when status is active and age >= 18");
        
        // Case 2: type is admin
        resource.remove("status");
        resource.remove("age");
        resource.put("type", "admin");
        assertTrue(filter.matches(resource), "Should match when type is admin");
        
        // Case 3: none of the conditions are met
        resource.remove("type");
        resource.put("status", "inactive");
        resource.put("age", "15");
        assertFalse(filter.matches(resource), "Should not match when no conditions are met");
    }
    
    @Test
    void testComplexCombination() {
        // Test: (status == "active" AND (age >= 18 OR (hasParentalConsent == "true" AND age >= 13)))
        Filter ageFilter = FluentFilterBuilder.where("age").greaterThanOrEqual("18").build();
        Filter consentFilter = FluentFilterBuilder.where("hasParentalConsent").is("true")
                .and("age").greaterThanOrEqual("13").build();
        Filter ageOrConsentFilter = FluentFilterBuilder.or(ageFilter, consentFilter);
        Filter statusFilter = FluentFilterBuilder.where("status").is("active").build();
        Filter filter = FluentFilterBuilder.and(statusFilter, ageOrConsentFilter);
        
        Map<String, String> resource = new HashMap<>();
        resource.put("status", "active");
        
        // Case 1: status active and age >= 18
        resource.put("age", "25");
        assertTrue(filter.matches(resource), "Should match when status is active and age >= 18");
        
        // Case 2: status active, age < 18 but has parental consent and age >= 13
        resource.put("age", "15");
        resource.put("hasParentalConsent", "true");
        assertTrue(filter.matches(resource), "Should match with parental consent and age >= 13");
        
        // Case 3: status active, age < 18, no parental consent
        resource.remove("hasParentalConsent");
        resource.put("age", "15");
        assertFalse(filter.matches(resource), "Should not match when age < 18 and no parental consent");
        
        // Case 4: status active, has parental consent but age < 13
        resource.put("hasParentalConsent", "true");
        resource.put("age", "12");
        assertFalse(filter.matches(resource), "Should not match when age < 13 even with parental consent");
    }
}
