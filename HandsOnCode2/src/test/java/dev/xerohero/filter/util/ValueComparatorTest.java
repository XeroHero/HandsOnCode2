package dev.xerohero.filter.util;

import dev.xerohero.filter.ValueComparator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Value Comparator Tests")
class ValueComparatorTest {

    @Nested
    @DisplayName("Regex Matching")
    class RegexMatchingTests {
        @Test
        @DisplayName("should match patterns in strings")
        void testBasicRegexMatching() {
            assertEquals(0, ValueComparator.compare("/.*hello.*/", "hello world"), "Should match substring");
            assertEquals(0, ValueComparator.compare("hello world", "/.*hello.*/"), "Should match substring in reverse");
            assertEquals(0, ValueComparator.compare("/\\d+/", "123"), "Should match digits");
            assertEquals(0, ValueComparator.compare("/[A-Z]+/", "HELLO"), "Should match uppercase letters");
            assertEquals(0, ValueComparator.compare("test", "/t.st/"), "Should match pattern");
        }

        @Test
        @DisplayName("should handle case-insensitive matching")
        void testCaseInsensitiveMatching() {
            assertEquals(0, ValueComparator.compare("/(?i)hello/", "HELLO"), "Should be case-insensitive match");
            assertEquals(0, ValueComparator.compare("HELLO", "/(?i)hello/"), "Should be case-insensitive match in reverse");
        }

        @Test
        @DisplayName("should handle non-matching patterns")
        void testNonMatchingPatterns() {
            assertTrue(ValueComparator.compare("/^hello$/", "hello world") != 0, "Should not match partial string");
            assertTrue(ValueComparator.compare("world", "/^hello$/") != 0, "Should not match different strings");
        }
    }

    @Nested
    @DisplayName("Regex Edge Cases")
    class RegexEdgeCasesTests {
        @Test
        @DisplayName("should handle empty strings with patterns")
        void testEmptyStringWithPattern() {
            assertDoesNotThrow(() -> ValueComparator.compare("", "/.*/"), 
                "Should handle empty string with pattern");
        }

        @Test
        @DisplayName("should reject invalid regex patterns")
        void testInvalidRegexPatterns() {
            assertThrows(IllegalArgumentException.class, 
                () -> ValueComparator.compare("test", "//"),
                "Should reject empty pattern"
            );
            
            assertThrows(IllegalArgumentException.class, 
                () -> ValueComparator.compare("test", "/invalid"),
                "Should reject unclosed pattern"
            );
        }

        @Test
        @DisplayName("should handle escaped characters in patterns")
        void testEscapedCharacters() {
            assertEquals(0, ValueComparator.compare("/\\d+\\.\\d+/", "3.14"), "Should match decimal number pattern");
        }
            
        @Test
        @DisplayName("should reject null values")
        void testNullValues() {
            assertThrows(NullPointerException.class, 
                () -> ValueComparator.compare(null, "/pattern/"),
                "Should throw NPE for null first argument");
                
            assertThrows(NullPointerException.class,
                () -> ValueComparator.compare("test", null),
                "Should throw NPE for null second argument");
        }
    }

    @Nested
    @DisplayName("Numeric Comparison")
    class NumericComparisonTests {
        @Test
        @DisplayName("should compare basic integers")
        void testBasicIntegerComparison() {
            assertTrue(ValueComparator.compare("10", "5") > 0, "10 should be greater than 5");
            assertTrue(ValueComparator.compare("5", "10") < 0, "5 should be less than 10");
            assertEquals(0, ValueComparator.compare("10", "10"), "10 should be equal to 10");
        }

        @Test
        @DisplayName("should compare different numeric types")
        void testMixedNumericTypes() {
            assertTrue(ValueComparator.compare("10.5", "10") > 0, "10.5 should be greater than 10");
            assertTrue(ValueComparator.compare("10", "10.5") < 0, "10 should be less than 10.5");
        }

        @Test
        @DisplayName("should handle scientific notation")
        void testScientificNotation() {
            assertEquals(0, ValueComparator.compare("1e3", "1000"), "1e3 should equal 1000");
            assertTrue(ValueComparator.compare("1.5e3", "1499") > 0, "1.5e3 should be greater than 1499");
            assertEquals(0, ValueComparator.compare("1.5e-3", "0.0015"), "1.5e-3 should equal 0.0015");
        }

        @Test
        @DisplayName("should handle large numbers")
        void testLargeNumbers() {
            String largeNum1 = "12345678901234567890";
            String largeNum2 = "12345678901234567891";
            assertTrue(ValueComparator.compare(largeNum1, largeNum2) < 0, largeNum1 + " should be less than " + largeNum2);
            assertTrue(ValueComparator.compare(largeNum2, largeNum1) > 0, largeNum2 + " should be greater than " + largeNum1);
            assertEquals(0, ValueComparator.compare(largeNum1, largeNum1), "Equal large numbers should be equal");
        }

        @Test
        @DisplayName("should handle decimal precision")
        void testDecimalPrecision() {
            assertTrue(ValueComparator.compare("0.1", "0.2") < 0, "0.1 should be less than 0.2");
            assertTrue(ValueComparator.compare("0.1000001", "0.1") > 0, "0.1000001 should be greater than 0.1");
        }

        @Test
        @DisplayName("should handle negative numbers")
        void testNegativeNumbers() {
            assertTrue(ValueComparator.compare("-10", "10") < 0, "-10 should be less than 10");
            assertTrue(ValueComparator.compare("-10", "-5") < 0, "-10 should be less than -5");
            assertEquals(0, ValueComparator.compare("-123.456", "-123.456"), "Equal negative decimals should be equal");
        }
    }

    @Nested
    @DisplayName("Numeric Validation")
    class NumericValidationTests {
        @ParameterizedTest
        @ValueSource(strings = {
            "123", "-123", "123.456", "-123.456", 
            "1.23e4", "-1.23e-4", "12345678901234567890"
        })
        @DisplayName("should validate numeric strings")
        void testValidNumbers(String value) {
            assertTrue(ValueComparator.isNumeric(value), "Should be valid number: " + value);
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "abc", "123abc", "1.2.3", "1e2e3", "", " "
        })
        @DisplayName("should reject invalid numeric strings")
        void testInvalidNumbers(String value) {
            assertFalse(ValueComparator.isNumeric(value), "Should not be valid number: " + value);
        }

        @Test
        @DisplayName("should reject null value")
        void testNullValue() {
            assertFalse(ValueComparator.isNumeric(null), "Null should not be numeric");
        }
    }

    @Nested
    @DisplayName("String and Boolean Comparison")
    class StringAndBooleanTests {
        @Test
        @DisplayName("should compare strings lexicographically")
        void testStringComparison() {
            assertTrue(ValueComparator.compare("apple", "banana") < 0);
            assertTrue(ValueComparator.compare("banana", "apple") > 0);
            assertEquals(0, ValueComparator.compare("apple", "apple"));
        }

        @Test
        @DisplayName("should compare boolean values")
        void testBooleanComparison() {
            assertTrue(ValueComparator.compare("true", "false") > 0);
            assertTrue(ValueComparator.compare("false", "true") < 0);
            assertEquals(0, ValueComparator.compare("true", "true"));
        }
    }
}
