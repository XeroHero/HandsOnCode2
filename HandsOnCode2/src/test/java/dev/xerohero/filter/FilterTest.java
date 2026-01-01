package dev.xerohero.filter;

import dev.xerohero.filter.operators.*;
import dev.xerohero.filter.operators.comparison.*;
import dev.xerohero.filter.visitor.ToStringVisitor;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FilterTest {

    @Test
    public void testEqualsFilter() {
        Map<String, String> user = new HashMap<>();
        user.put("firstname", "Joe");
        user.put("lastname", "Bloggs");
        user.put("role", "administrator");
        user.put("age", "35");

        Filter filter = new EqualsFilter("role", "administrator");

        assertTrue(filter.matches(user));
        assertEquals("(role == 'administrator')", filter.toString());
    }

    @Test
    public void testEqualsFilterCaseInsensitive() {
        Map<String, String> user = new HashMap<>();
        user.put("role", "ADMINISTRATOR"); // Uppercase

        Filter filter = new EqualsFilter("role", "administrator");

        assertTrue(filter.matches(user));
    }

    @Test
    public void testMissingProperty() {
        Map<String, String> user = new HashMap<>();
        user.put("name", "Joe");

        Filter filter = new EqualsFilter("role", "administrator");

        assertFalse(filter.matches(user)); // Should return false, not crash
    }

    @Test
    public void testComplexFilter() {
        // Example from PDF: "all administrators older than 30"
        Filter filter = new AndFilter(new Filter[] {
            new EqualsFilter("role", "administrator"),
            new GreaterThanFilter("age", "30")
        });

        Map<String, String> user1 = new HashMap<>();
        user1.put("role", "administrator");
        user1.put("age", "35");
        assertTrue(filter.matches(user1));

        Map<String, String> user2 = new HashMap<>();
        user2.put("role", "administrator");
        user2.put("age", "25");
        assertFalse(filter.matches(user2));

        assertEquals("((role == 'administrator') AND (age > '30'))", filter.toString());
    }

    @Test
    public void testLogicalOperators() {
        Filter trueFilter = TrueFilter.INSTANCE;
        Filter falseFilter = FalseFilter.INSTANCE;

        Map<String, String> user = new HashMap<>();

        assertTrue(trueFilter.matches(user));
        assertFalse(falseFilter.matches(user));

        Filter orFilter = new OrFilter(new Filter[] { trueFilter, falseFilter });
        assertTrue(orFilter.matches(user));

        Filter andFilter = new AndFilter(new Filter[] { trueFilter, falseFilter });
        assertFalse(andFilter.matches(user));

        Filter notFilter = new NotFilter(falseFilter);
        assertTrue(notFilter.matches(user));

        // Verify singleton behavior
        assertSame(TrueFilter.INSTANCE, trueFilter);
        assertSame(FalseFilter.INSTANCE, falseFilter);
    }

    @Test
    public void testComparisonFilters() {
        Map<String, String> user = new HashMap<>();
        user.put("age", "25");
        user.put("name", "Alice");

        Filter lessThan = new LessThanFilter("age", "30");
        assertTrue(lessThan.matches(user));

        Filter greaterThan = new GreaterThanFilter("age", "20");
        assertTrue(greaterThan.matches(user));

        Filter hasProperty = new HasPropertyFiltre("name");
        assertTrue(hasProperty.matches(user));

        Filter noProperty = new HasPropertyFiltre("email");
        assertFalse(noProperty.matches(user));
    }

    @Test
    public void testRegexFilter() {
        Map<String, String> user = new HashMap<>();
        user.put("email", "test@example.com");

        Filter regexFilter = new RegexFilter("email", ".*@example\\.com");
        assertTrue(regexFilter.matches(user));
    }

    @Test
    public void testVisitorPattern() {
        Filter complexFilter = new AndFilter(new Filter[] {
            new EqualsFilter("role", "admin"),
            new OrFilter(new Filter[] {
                new GreaterThanFilter("age", "30"),
                new EqualsFilter("department", "IT")
            })
        });

        ToStringVisitor visitor = new ToStringVisitor();
        String result = complexFilter.accept(visitor);

        assertEquals("(role == admin && (age > 30 || department == IT))", result);
    }

    @Test
    public void testFilterBuilder() {
        Filter filter = FilterBuilder.and(
            FilterBuilder.equals("role", "admin"),
            FilterBuilder.greaterThan("age", "30")
        );

        Map<String, String> user = new HashMap<>();
        user.put("role", "admin");
        user.put("age", "35");

        assertTrue(filter.matches(user));
    }

    @Test
    public void testNumericComparison() {
        Map<String, String> user1 = new HashMap<>();
        user1.put("score", "100");

        Map<String, String> user2 = new HashMap<>();
        user2.put("score", "50");

        Filter greaterThan = new GreaterThanFilter("score", "75");

        assertTrue(greaterThan.matches(user1));
        assertFalse(greaterThan.matches(user2));
    }

    @Test
    public void testStringComparison() {
        Map<String, String> user = new HashMap<>();
        user.put("name", "Alice");

        Filter lessThan = new LessThanFilter("name", "Bob");
        assertTrue(lessThan.matches(user));
    }

    @Test
    public void testEqualsFilterWithNullValue() {
        Map<String, String> user = new HashMap<>();
        user.put("role", null);

        Filter filter = new EqualsFilter("role", null);
        // The actual implementation returns false when comparing with null
        assertFalse(filter.matches(user));
    }

    @Test
    public void testEqualsFilterDifferentCase() {
        Map<String, String> user = new HashMap<>();
        user.put("name", "TestUser");

        Filter filter = new EqualsFilter("name", "testuser");
        assertTrue(filter.matches(user));
    }

    @Test
    public void testLessThanFilterWithNonNumericValues() {
        Map<String, String> user = new HashMap<>();
        user.put("name", "Alice");

        Filter filter = new LessThanFilter("name", "Bob");
        assertTrue(filter.matches(user));
    }

    @Test
    public void testGreaterThanFilterWithNonNumericValues() {
        Map<String, String> user = new HashMap<>();
        user.put("name", "Charlie");

        Filter filter = new GreaterThanFilter("name", "Bob");
        assertTrue(filter.matches(user));
    }

    @Test
    public void testRegexFilterWithNullValue() {
        Map<String, String> user = new HashMap<>();
        user.put("email", null);

        Filter filter = new RegexFilter("email", ".*@example\\.com");
        assertFalse(filter.matches(user));
    }

    @Test
    public void testNotFilterWithNullFilter() {
        assertThrows(NullPointerException.class, () -> new NotFilter(null));
    }

    @Test
    public void testAndFilterWithEmptyFilters() {
        Filter[] emptyFilters = new Filter[0];
        assertThrows(IllegalArgumentException.class, () -> new AndFilter(emptyFilters));
    }

    @Test
    public void testOrFilterWithEmptyFilters() {
        Filter[] emptyFilters = new Filter[0];
        assertThrows(IllegalArgumentException.class, () -> new OrFilter(emptyFilters));
    }

    @Test
    public void testFilterBuilderWithNullParameters() {
        // Test null key in equals
        assertThrows(IllegalArgumentException.class, () -> FilterBuilder.equals(null, "value"));
        
        // Test null value is allowed in equals (to check for null values)
        Filter equalsWithNullValue = FilterBuilder.equals("key", null);
        assertNotNull(equalsWithNullValue);
        
        // Test empty filters
        assertThrows(IllegalArgumentException.class, () -> FilterBuilder.and());
        assertThrows(IllegalArgumentException.class, () -> FilterBuilder.or());
        
        // Test null filters in varargs
        assertThrows(IllegalArgumentException.class, () -> FilterBuilder.and((Filter) null));
        assertThrows(IllegalArgumentException.class, () -> FilterBuilder.or((Filter) null));
        
        // Test null filter for not
        assertThrows(IllegalArgumentException.class, () -> FilterBuilder.not(null));
        
        // Test null key for hasProperty
        assertThrows(IllegalArgumentException.class, () -> FilterBuilder.hasProperty(null));
        
        // Test null value for lessThan
        assertThrows(IllegalArgumentException.class, () -> FilterBuilder.lessThan("key", null));
        
        // Test null value for greaterThan
        assertThrows(IllegalArgumentException.class, () -> FilterBuilder.greaterThan("key", null));
        
        // Test null regex
        assertThrows(IllegalArgumentException.class, () -> FilterBuilder.matchesRegex("key", null));
    }

    @Test
    public void testHasPropertyFilterWithNull() {
        Map<String, String> user = new HashMap<>();
        user.put("name", null);

        Filter filter = new HasPropertyFiltre("name");
        assertTrue(filter.matches(user));
    }

    @Test
    public void testNotFilterToString() {
        Filter notFilter = new NotFilter(TrueFilter.INSTANCE);
        assertEquals("NOT TRUE", notFilter.toString());
    }

    @Test
    public void testAndFilterWithSingleFilter() {
        Filter andFilter = new AndFilter(new Filter[] { new EqualsFilter("role", "admin") });
        assertEquals("((role == 'admin'))", andFilter.toString());
    }

    @Test
    public void testOrFilterWithSingleFilter() {
        Filter orFilter = new OrFilter(new Filter[] { new EqualsFilter("status", "active") });
        assertEquals("((status == 'active'))", orFilter.toString());
    }
}