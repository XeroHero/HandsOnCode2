package dev.xerohero.filter;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.operators.*;
import dev.xerohero.filter.operators.comparison.*;
import dev.xerohero.filter.visitor.ToStringVisitor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class FilterTest {

    @Test
    public void testEqualsFilter() {
        Map<String, String> user = new HashMap<>();
        user.put("role", "administrator");
        user.put("firstname", "Joe");

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
        Filter filter = new AndFilter(
                new EqualsFilter("role", "administrator"),
                new GreaterThanFilter("age", "30")
        );

        Map<String, String> user1 = new HashMap<>();
        user1.put("role", "administrator");
        user1.put("age", "35");
        assertTrue(filter.matches(user1));

        Map<String, String> user2 = new HashMap<>();
        user2.put("role", "administrator");
        user2.put("age", "25");
        assertFalse(filter.matches(user2));

        assertEquals("((role == 'administrator') AND (age > '30'))",
                filter.toString());
    }

    @Test
    public void testLogicalOperators() {
        Filter trueFilter = new TrueFilter();
        Filter falseFilter = new FalseFilter();

        Map<String, String> user = new HashMap<>();

        assertTrue(trueFilter.matches(user));
        assertFalse(falseFilter.matches(user));

        Filter orFilter = new OrFilter(trueFilter, falseFilter);
        assertTrue(orFilter.matches(user));

        Filter andFilter = new AndFilter(trueFilter, falseFilter);
        assertFalse(andFilter.matches(user));

        Filter notFilter = new NotFilter(falseFilter);
        assertTrue(notFilter.matches(user));
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

        Filter hasProperty = new HasPropertyFilter("name");
        assertTrue(hasProperty.matches(user));

        Filter noProperty = new HasPropertyFilter("email");
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
        Filter complexFilter = new AndFilter(
                new EqualsFilter("role", "admin"),
                new OrFilter(
                        new GreaterThanFilter("age", "30"),
                        new EqualsFilter("department", "IT")
                )
        );

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
}