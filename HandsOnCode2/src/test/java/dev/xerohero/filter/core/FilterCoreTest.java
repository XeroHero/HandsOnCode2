package dev.xerohero.filter.core;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.FilterBuilder;
import dev.xerohero.filter.operators.FalseFilter;
import dev.xerohero.filter.operators.TrueFilter;
import dev.xerohero.filter.operators.comparison.EqualsFilter;
import dev.xerohero.filter.operators.comparison.GreaterThanFilter;
import dev.xerohero.filter.operators.comparison.LessThanFilter;
import dev.xerohero.filter.operators.comparison.RegexFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Core Filter Tests")
class FilterCoreTest {
    
    // Test data
    private Map<String, String> createTestUser() {
        Map<String, String> user = new HashMap<>();
        user.put("firstname", "Joe");
        user.put("lastname", "Bloggs");
        user.put("role", "administrator");
        user.put("age", "35");
        user.put("email", "joe.bloggs@example.com");
        return user;
    }

    @Nested
    @DisplayName("Basic Filter Tests")
    class BasicFilterTests {
        @Test
        @DisplayName("TrueFilter should always return true")
        void testTrueFilter() {
            Map<String, String> user = new HashMap<>();
            user.put("firstname", "Joe");
            
            Filter trueFilter = new TrueFilter();
            assertTrue(trueFilter.matches(user));
        }

        @Test
        @DisplayName("FalseFilter should always return false")
        void testFalseFilter() {
            Map<String, String> user = new HashMap<>();
            user.put("firstname", "Joe");
            user.put("lastname", "Bloggs");
            user.put("role", "Administrator");
            user.put("age", "35");

            Filter filter = new FalseFilter();
            user.put("age", "25");
            assertFalse(filter.matches(user));
        }
    }

    @Nested
    @DisplayName("Equality Filter Tests")
    class EqualityFilterTests {
        @Test
        @DisplayName("Should match when values are equal")
        void testEqualsFilter() {
            Map<String, String> user = createTestUser();
            Filter filter = new EqualsFilter("role", "administrator");

            assertTrue(filter.matches(user));
            assertEquals("(role == 'administrator')", filter.toString());
        }

        @Test
        @DisplayName("Should be case insensitive by default")
        void testEqualsFilterCaseInsensitive() {
            Map<String, String> user = new HashMap<>();
            user.put("role", "ADMINISTRATOR"); // Uppercase

            Filter filter = new EqualsFilter("role", "administrator");
            assertTrue(filter.matches(user));
        }

        @Test
        @DisplayName("Should handle missing properties gracefully")
        void testMissingProperty() {
            Map<String, String> user = new HashMap<>();
            user.put("name", "Joe");

            Filter filter = new EqualsFilter("role", "administrator");
            assertFalse(filter.matches(user)); // Should return false, not crash
        }
    }

    @Nested
    @DisplayName("Comparison Filter Tests")
    class ComparisonFilterTests {
        @Test
        @DisplayName("Should compare numeric values correctly")
        void testNumericComparison() {
            Map<String, String> user = createTestUser();
            
            Filter ageFilter = new GreaterThanFilter("age", "30");
            assertTrue(ageFilter.matches(user));
            
            ageFilter = new LessThanFilter("age", "40");
            assertTrue(ageFilter.matches(user));
        }
    }

    @Nested
    @DisplayName("Composite Filter Tests")
    class CompositeFilterTests {
        @Test
        @DisplayName("Should combine filters with AND logic")
        void testAndFilter() {
            Map<String, String> user = createTestUser();
            
            Filter roleFilter = new EqualsFilter("role", "administrator");
            Filter ageFilter = new GreaterThanFilter("age", "30");
            Filter andFilter = FilterBuilder.andFilter(roleFilter, ageFilter);
            
            assertTrue(andFilter.matches(user));
            
            // Test short-circuit behavior
            user.put("age", "25");
            assertFalse(andFilter.matches(user));
        }

        @Test
        @DisplayName("Should combine filters with OR logic")
        void testOrFilter() {
            Map<String, String> user = createTestUser();
            
            Filter adminFilter = new EqualsFilter("role", "admin");
            Filter ageFilter = new GreaterThanFilter("age", "30");
            Filter orFilter = FilterBuilder.orFilter(adminFilter, ageFilter);
            
            // Should match because age > 30, even though role is not admin
            assertTrue(orFilter.matches(user));
            
            // Test short-circuit behavior
            user.put("age", "25");
            assertFalse(orFilter.matches(user));
        }
    }

    @Nested
    @DisplayName("Regex Filter Tests")
    class RegexFilterTests {
        @Test
        @DisplayName("Should match email pattern")
        void testEmailMatching() {
            Map<String, String> user = createTestUser();
            
            Filter emailFilter = new RegexFilter("email", "^[A-Za-z0-9+_.-]+@(.+)$");
            assertTrue(emailFilter.matches(user));
            
            user.put("email", "invalid-email");
            assertFalse(emailFilter.matches(user));
        }
    }
}
