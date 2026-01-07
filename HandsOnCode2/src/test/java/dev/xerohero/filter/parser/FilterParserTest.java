package dev.xerohero.filter.parser;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.FilterBuilder;
import dev.xerohero.filter.operators.AndFilter;
import dev.xerohero.filter.operators.OrFilter;
import dev.xerohero.filter.operators.comparison.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FilterParserTest {
    
    private final Map<String, String> testData = createTestData();
    
    private Map<String, String> createTestData() {
        Map<String, String> data = new HashMap<>();
        data.put("name", "John");
        data.put("age", "30");
        data.put("status", "active");
        data.put("role", "admin");
        data.put("email", "john@example.com");
        data.put("score", "85");
        return data;
    }
    
    @Test
    void parseSimpleEquals() {
        Filter filter = FilterBuilder.parse("name = \"John\"");
        assertTrue(filter.matches(testData));
        assertInstanceOf(EqualsFilter.class, filter);
        
        filter = FilterBuilder.parse("name = \"Jane\"");
        assertFalse(filter.matches(testData));
    }
    
    @Test
    void parseNotEquals() {
        Filter filter = FilterBuilder.parse("name != \"Jane\"");
        assertTrue(filter.matches(testData));
        
        filter = FilterBuilder.parse("name != \"John\"");
        assertFalse(filter.matches(testData));
    }
    
    @Test
    void parseNumericComparisons() {
        Filter filter = FilterBuilder.parse("age > 25");
        assertTrue(filter.matches(testData));
        
        filter = FilterBuilder.parse("age < 35");
        assertTrue(filter.matches(testData));
        
        filter = FilterBuilder.parse("age >= 30");
        assertTrue(filter.matches(testData));
        
        filter = FilterBuilder.parse("age <= 30");
        assertTrue(filter.matches(testData));
        
        filter = FilterBuilder.parse("age > 40");
        assertFalse(filter.matches(testData));
    }
    
    @Test
    void parseAndOperator() {
        Filter filter = FilterBuilder.parse("name = \"John\" AND age = 30");
        assertTrue(filter.matches(testData));
        assertInstanceOf(AndFilter.class, filter);
        
        filter = FilterBuilder.parse("name = \"John\" AND age = 25");
        assertFalse(filter.matches(testData));
    }
    
    @Test
    void parseOrOperator() {
        Filter filter = FilterBuilder.parse("name = \"Jane\" OR age = 30");
        assertTrue(filter.matches(testData));
        assertInstanceOf(OrFilter.class, filter);
        
        filter = FilterBuilder.parse("name = \"Jane\" OR age = 25");
        assertFalse(filter.matches(testData));
    }
    
    @Test
    void parseParentheses() {
        // (name = "John" AND age = 30) OR role = "admin"
        Filter filter = FilterBuilder.parse("(name = \"John\" AND age = 30) OR role = \"admin\"");
        assertTrue(filter.matches(testData));
        
        // name = "John" AND (age = 30 OR role = "user")
        filter = FilterBuilder.parse("name = \"John\" AND (age = 30 OR role = \"user\")");
        assertTrue(filter.matches(testData));
        
        // (name = "Jane" OR name = "John") AND status = "active"
        filter = FilterBuilder.parse("(name = \"Jane\" OR name = \"John\") AND status = \"active\"");
        assertTrue(filter.matches(testData));
    }
    
    @Test
    void parseNotOperator() {
        Filter filter = FilterBuilder.parse("NOT name = \"Jane\"");
        assertTrue(filter.matches(testData));
        
        filter = FilterBuilder.parse("NOT (name = \"John\" AND age = 25)");
        assertTrue(filter.matches(testData));
        
        filter = FilterBuilder.parse("NOT (name = \"John\" AND age = 30)");
        assertFalse(filter.matches(testData));
    }
    
    @Test
    void parseComplexExpression() {
        // status = "active" AND (role = "admin" OR role = "superuser") AND (age > 25 AND age < 40)
        Filter filter = FilterBuilder.parse(
            "status = \"active\" AND (role = \"admin\" OR role = \"superuser\") AND (age > 25 AND age < 40)"
        );
        assertTrue(filter.matches(testData));
        
        // (name = "John" OR name = "Jane") AND (age > 25 OR status = "inactive") AND NOT role = "user"
        filter = FilterBuilder.parse(
            "(name = \"John\" OR name = \"Jane\") AND (age > 25 OR status = \"inactive\") AND NOT role = \"user\""
        );
        assertTrue(filter.matches(testData));
    }
    
    @Test
    void parseInvalidExpressions() {
        // Missing operator
        assertThrows(FilterParseException.class, () -> FilterBuilder.parse("name \"John\""));
        
        // Unbalanced parentheses
        assertThrows(FilterParseException.class, () -> FilterBuilder.parse("(name = \"John\""));
        
        // Empty expression
        assertThrows(FilterParseException.class, () -> FilterBuilder.parse(""));
        
        // Null expression
        assertThrows(FilterParseException.class, () -> FilterBuilder.parse(null));
    }
}
