package dev.xerohero.filter.parser;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.FilterBuilder;
import dev.xerohero.filter.operators.AndFilter;
import dev.xerohero.filter.operators.OrFilter;
import dev.xerohero.filter.operators.comparison.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Filter Parser Tests")
class FilterParserTest {
    
    private Map<String, String> testData;
    
    @BeforeEach
    void setUp() {
        testData = createTestData();
    }
    
    private Map<String, String> createTestData() {
        Map<String, String> data = new HashMap<>();
        data.put("name", "John");
        data.put("age", "30");
        data.put("status", "active");
        data.put("role", "admin");
        data.put("email", "john@example.com");
        data.put("score", "85");
        data.put("isAdmin", "true");
        data.put("lastLogin", "2025-01-01T12:00:00Z");
        return data;
    }
    
    @Nested
    @DisplayName("Basic Comparison Tests")
    class BasicComparisonTests {
        @Test
        @DisplayName("should parse simple equality expression")
        void parseSimpleEquals() {
            Filter filter = FilterBuilder.parse("name = \"John\"");
            assertTrue(filter.matches(testData), "Should match when name is John");
            assertInstanceOf(EqualsFilter.class, filter);
            
            filter = FilterBuilder.parse("name = \"Jane\"");
            assertFalse(filter.matches(testData), "Should not match when name is not John");
        }
        
        @Test
        @DisplayName("should parse not equals expression")
        void parseNotEquals() {
            Filter filter = FilterBuilder.parse("name != \"Jane\"");
            assertTrue(filter.matches(testData), "Should match when name is not Jane");
            
            filter = FilterBuilder.parse("name != \"John\"");
            assertFalse(filter.matches(testData), "Should not match when name is John");
        }
        
        @ParameterizedTest
        @CsvSource({
            "age > 25, true",
            "age < 35, true",
            "age >= 30, true",
            "age <= 30, true",
            "age > 40, false",
            "age < 25, false"
        })
        @DisplayName("should parse numeric comparisons")
        void parseNumericComparisons(String expression, boolean expected) {
            Filter filter = FilterBuilder.parse(expression);
            assertEquals(expected, filter.matches(testData), 
                String.format("Expression '%s' should %s", 
                    expression, 
                    expected ? "match" : "not match"));
        }
        
        @Test
        @DisplayName("should handle boolean values")
        void parseBooleanValues() {
            Filter filter = FilterBuilder.parse("isAdmin = true");
            assertTrue(filter.matches(testData), "Should match when isAdmin is true");
        }
    }
    
    @Nested
    @DisplayName("Logical Operator Tests")
    class LogicalOperatorTests {
        @Test
        @DisplayName("should parse AND operator")
        void parseAndOperator() {
            Filter filter = FilterBuilder.parse("name = \"John\" AND age = 30");
            assertTrue(filter.matches(testData), "Should match when both conditions are true");
            assertInstanceOf(AndFilter.class, filter);
            
            filter = FilterBuilder.parse("name = \"John\" AND age = 25");
            assertFalse(filter.matches(testData), "Should not match when one condition is false");
        }
        
        @Test
        @DisplayName("should parse OR operator")
        void parseOrOperator() {
            Filter filter = FilterBuilder.parse("name = \"Jane\" OR age = 30");
            assertTrue(filter.matches(testData), "Should match when at least one condition is true");
            assertInstanceOf(OrFilter.class, filter);
            
            filter = FilterBuilder.parse("name = \"Jane\" OR age = 25");
            assertFalse(filter.matches(testData), "Should not match when all conditions are false");
        }
        
        @Test
        @DisplayName("should parse NOT operator")
        void parseNotOperator() {
            Filter filter = FilterBuilder.parse("NOT name = \"Jane\"");
            assertTrue(filter.matches(testData), "Should match when condition is false");
            
            filter = FilterBuilder.parse("NOT (name = \"John\" AND age = 25)");
            assertTrue(filter.matches(testData), "Should match when AND condition is false");
            
            filter = FilterBuilder.parse("NOT (name = \"John\" AND age = 30)");
            assertFalse(filter.matches(testData), "Should not match when AND condition is true");
        }
    }
    
    @Test
    void parseOrOperator() {
        Filter filter = FilterBuilder.parse("name = \"Jane\" OR age = 30");
        assertTrue(filter.matches(testData));
        assertInstanceOf(OrFilter.class, filter);
        
        filter = FilterBuilder.parse("name = \"Jane\" OR age = 25");
        assertFalse(filter.matches(testData));
    }
    
    @Nested
    @DisplayName("Parentheses and Precedence Tests")
    class ParenthesesAndPrecedenceTests {
        @Test
        @DisplayName("should handle parentheses for grouping")
        void parseParentheses() {
            // (name = "John" AND age = 30) OR role = "admin"
            Filter filter = FilterBuilder.parse("(name = \"John\" AND age = 30) OR role = \"admin\"");
            assertTrue(filter.matches(testData), "Should match when either group matches");
            
            // name = "John" AND (age = 30 OR role = "user")
            filter = FilterBuilder.parse("name = \"John\" AND (age = 30 OR role = \"user\")");
            assertTrue(filter.matches(testData), "Should match when name matches and either condition in group matches");
            
            // (name = "Jane" OR name = "John") AND status = "active"
            filter = FilterBuilder.parse("(name = \"Jane\" OR name = \"John\") AND status = \"active\"");
            assertTrue(filter.matches(testData), "Should match when either name matches and status is active");
        }
        
        @Test
        @DisplayName("should respect operator precedence")
        void testOperatorPrecedence() {
            // Test that AND has higher precedence than OR
            // name = "John" OR (name = "Jane" AND age = 30)
            Filter filter1 = FilterBuilder.parse("name = \"John\" OR name = \"Jane\" AND age = 30");
            
            // (name = "John" OR name = "Jane") AND age = 30
            Filter filter2 = FilterBuilder.parse("(name = \"John\" OR name = \"Jane\") AND age = 30");
            
            // With test data: name="John", age=30
            // filter1: true (because name="John" is true)
            // filter2: true (because name="John" is true AND age=30 is true)
            assertTrue(filter1.matches(testData), "Should match when first condition is true");
            assertTrue(filter2.matches(testData), "Should match when both conditions are true");
            
            // Change test data to make the first condition false
            testData.put("name", "Alice");
            // filter1: false (because name="Alice" is false AND age=30 is true)
            // filter2: false (because name="Alice" is false AND age=30 is true)
            assertFalse(filter1.matches(testData), "Should not match when first condition is false");
            assertFalse(filter2.matches(testData), "Should not match when first condition is false");
            
            // Test with different data where only the second part would match
            testData.put("name", "Jane");
            testData.put("age", "25");
            // filter1: false (because name="Jane" is true AND age=25 is false)
            // filter2: false (because name="Jane" is true AND age=25 is false)
            assertFalse(filter1.matches(testData), "Should not match when second condition is false");
            assertFalse(filter2.matches(testData), "Should not match when second condition is false");
        }
    }
    
    
    @Nested
    @DisplayName("Complex Expression Tests")
    class ComplexExpressionTests {
        @Test
        @DisplayName("should parse complex nested expressions")
        void parseComplexExpression() {
            // status = "active" AND (role = "admin" OR role = "superuser") AND (age > 25 AND age < 40)
            String expression1 = 
                "status = \"active\" AND (role = \"admin\" OR role = \"superuser\") AND (age > 25 AND age < 40)";
            Filter filter = FilterBuilder.parse(expression1);
            assertTrue(filter.matches(testData), "Should match complex expression: " + expression1);
            
            // (name = "John" OR name = "Jane") AND (age > 25 OR status = "inactive") AND NOT role = "user"
            String expression2 = 
                "(name = \"John\" OR name = \"Jane\") AND (age > 25 OR status = \"inactive\") AND NOT role = \"user\"";
            filter = FilterBuilder.parse(expression2);
            assertTrue(filter.matches(testData), "Should match complex expression: " + expression2);
        }
        
        @Test
        @DisplayName("should handle complex boolean logic")
        void testComplexBooleanLogic() {
            // (isAdmin = true OR role = 'superuser') AND (status = 'active' OR lastLogin > '2024-01-01')
            String expression = 
                "(isAdmin = true OR role = 'superuser') AND (status = 'active' OR lastLogin > '2024-01-01')";
            Filter filter = FilterBuilder.parse(expression);
            assertTrue(filter.matches(testData), "Should match complex boolean expression");
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        @ParameterizedTest
        @ValueSource(strings = {
            "name \"John\"",          // Missing operator
            "name = ",                  // Missing value
            "= 'value'",                // Missing field
            "name = 'value' AND",       // Trailing operator
            "name = 'value' AND ",      // Trailing operator with space
            "name = 'value' OR",        // Trailing OR
            "name = 'value' AND OR",    // Consecutive operators
            "name = 'value' (age > 25)", // Missing operator between expressions
            "name = 'value' 42"         // Invalid syntax
        })
        @DisplayName("should reject malformed expressions")
        void parseMalformedExpressions(String expression) {
            assertThrows(FilterParseException.class, 
                () -> FilterBuilder.parse(expression),
                "Should reject malformed expression: " + expression);
        }
        
        @Test
        @DisplayName("should handle syntax errors")
        void parseSyntaxErrors() {
            // Unbalanced parentheses
            assertThrows(FilterParseException.class, 
                () -> FilterBuilder.parse("(name = \"John\""),
                "Should reject unbalanced parentheses");
                
            assertThrows(FilterParseException.class, 
                () -> FilterBuilder.parse("name = \"John\")"),
                "Should reject unbalanced parentheses");
                
            // Empty expression
            assertThrows(FilterParseException.class, 
                () -> FilterBuilder.parse(""),
                "Should reject empty expression");
                
            // Null expression
            assertThrows(FilterParseException.class, 
                () -> FilterBuilder.parse(null),
                "Should reject null expression");
                
            // Invalid operator
            assertThrows(FilterParseException.class,
                () -> FilterBuilder.parse("name LIKE 'John'"),
                "Should reject unknown operator");
        }
        
        @Test
        @DisplayName("should handle invalid field references")
        void testInvalidFieldReferences() {
            // Non-existent field should not throw, but return false for non-matching filters
            Filter filter = FilterBuilder.parse("nonexistent = 'value'");
            assertFalse(filter.matches(testData), "Should handle non-existent fields gracefully");
            
            // Invalid field name
            assertThrows(FilterParseException.class,
                () -> FilterBuilder.parse("invalid-field = 'value'"),
                "Should reject invalid field names");
        }
    }
}
