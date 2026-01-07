package dev.xerohero.filter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValueComparatorTest {

    @Test
    void testRegexMatching() {
        // Test regex pattern matching
        assertTrue(ValueComparator.compare("/.*hello.*/", "hello world") == 0, "Should match substring");
        assertTrue(ValueComparator.compare("hello world", "/.*hello.*/") == 0, "Should match substring");
        assertTrue(ValueComparator.compare("/\\d+/", "123") == 0, "Should match digits");
        assertTrue(ValueComparator.compare("/[A-Z]+/", "HELLO") == 0, "Should match uppercase letters");
        assertTrue(ValueComparator.compare("test", "/t.st/") == 0, "Should match pattern");
        
        // Test case-insensitive regex
        assertTrue(ValueComparator.compare("/(?i)hello/", "HELLO") == 0, "Should be case-insensitive match");
        
        // Test non-matching patterns
        assertTrue(ValueComparator.compare("/^hello$/", "hello world") != 0, "Should not match partial string");
        assertTrue(ValueComparator.compare("world", "/^hello$/") != 0, "Should not match different strings");
    }

    @Test
    void testRegexEdgeCases() {
        // Test empty string with pattern
        assertDoesNotThrow(() -> ValueComparator.compare("", "/.*/"), 
            "Should handle empty string with pattern");
        
        // Test regex with just slashes (empty pattern)
        assertThrows(IllegalArgumentException.class, 
            () -> ValueComparator.compare("test", "//"),
            "Should reject empty pattern"
        );
        
        // Test regex with only one slash (unclosed pattern)
        assertThrows(IllegalArgumentException.class, 
            () -> ValueComparator.compare("test", "/invalid"),
            "Should reject unclosed pattern"
        );
        
        // Test valid regex with escaped characters
        assertTrue(ValueComparator.compare("/\\d+\\.\\d+/", "3.14") == 0, 
            "Should match decimal number pattern");
            
        // Test null values
        assertThrows(NullPointerException.class, 
            () -> ValueComparator.compare(null, "/pattern/"),
            "Should throw NPE for null first argument");
            
        assertThrows(NullPointerException.class,
            () -> ValueComparator.compare("test", null),
            "Should throw NPE for null second argument");
    }

    @Test
    void testNumericComparison() {
        // Test basic integer comparisons
        assertTrue(ValueComparator.compare("10", "5") > 0, "10 should be greater than 5");
        assertTrue(ValueComparator.compare("5", "10") < 0, "5 should be less than 10");
        assertEquals(0, ValueComparator.compare("10", "10"), "10 should be equal to 10");
        
        // Test different numeric types
        assertTrue(ValueComparator.compare("10.5", "10") > 0, "10.5 should be greater than 10");
        assertTrue(ValueComparator.compare("10", "10.5") < 0, "10 should be less than 10.5");
        
        // Test scientific notation
        assertEquals(0, ValueComparator.compare("1e3", "1000"), "1e3 should equal 1000");
        assertTrue(ValueComparator.compare("1.5e3", "1499") > 0, "1.5e3 should be greater than 1499");
        assertTrue(ValueComparator.compare("1.5e-3", "0.0015") == 0, "1.5e-3 should equal 0.0015");
        
        // Test large numbers
        String largeNum1 = "12345678901234567890";
        String largeNum2 = "12345678901234567891";
        assertTrue(ValueComparator.compare(largeNum1, largeNum2) < 0, largeNum1 + " should be less than " + largeNum2);
        assertTrue(ValueComparator.compare(largeNum2, largeNum1) > 0, largeNum2 + " should be greater than " + largeNum1);
        assertEquals(0, ValueComparator.compare(largeNum1, largeNum1), "Equal large numbers should be equal");
        
        // Test decimal precision
        assertTrue(ValueComparator.compare("0.1", "0.2") < 0, "0.1 should be less than 0.2");
        assertTrue(ValueComparator.compare("0.1000001", "0.1") > 0, "0.1000001 should be greater than 0.1");
        
        // Test negative numbers
        assertTrue(ValueComparator.compare("-10", "10") < 0, "-10 should be less than 10");
        assertTrue(ValueComparator.compare("-10", "-5") < 0, "-10 should be less than -5");
        assertEquals(0, ValueComparator.compare("-123.456", "-123.456"), "Equal negative decimals should be equal");
    }
    
    @Test
    void testIsNumeric() {
        // Valid numbers
        assertTrue(ValueComparator.isNumeric("123"), "Integer should be numeric");
        assertTrue(ValueComparator.isNumeric("-123"), "Negative integer should be numeric");
        assertTrue(ValueComparator.isNumeric("123.456"), "Decimal should be numeric");
        assertTrue(ValueComparator.isNumeric("-123.456"), "Negative decimal should be numeric");
        assertTrue(ValueComparator.isNumeric("1.23e4"), "Scientific notation should be numeric");
        assertTrue(ValueComparator.isNumeric("-1.23e-4"), "Negative scientific notation should be numeric");
        assertTrue(ValueComparator.isNumeric("12345678901234567890"), "Large integer should be numeric");
        
        // Invalid numbers
        assertFalse(ValueComparator.isNumeric("abc"), "Letters should not be numeric");
        assertFalse(ValueComparator.isNumeric("123abc"), "Mixed letters and numbers should not be numeric");
        assertFalse(ValueComparator.isNumeric("1.2.3"), "Multiple decimal points should not be numeric");
        assertFalse(ValueComparator.isNumeric("1e2e3"), "Multiple 'e' should not be numeric");
        assertFalse(ValueComparator.isNumeric(""), "Empty string should not be numeric");
        assertFalse(ValueComparator.isNumeric(" "), "Whitespace should not be numeric");
        assertFalse(ValueComparator.isNumeric(null), "Null should not be numeric");
    }

    @Test
    void testStringComparison() {
        // Test string comparisons still work
        assertTrue(ValueComparator.compare("apple", "banana") < 0);
        assertTrue(ValueComparator.compare("banana", "apple") > 0);
        assertTrue(ValueComparator.compare("apple", "apple") == 0);
    }

    @Test
    void testBooleanComparison() {
        // Test boolean comparisons still work
        assertTrue(ValueComparator.compare("true", "false") > 0);
        assertTrue(ValueComparator.compare("false", "true") < 0);
        assertTrue(ValueComparator.compare("true", "true") == 0);
    }
}
