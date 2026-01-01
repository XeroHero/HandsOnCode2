package dev.xerohero.filter.operators;

import dev.xerohero.filter.visitor.FilterVisitor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BaseComparisonFilterTest {

    @Test
    void constructor_WithValidKey_SetsKey() {
        String testKey = "testKey";
        TestFilter filter = new TestFilter(testKey);
        assertEquals(testKey, filter.getKey());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void constructor_WithEmptyOrBlankKey_ThrowsException(String key) {
        assertThrows(IllegalArgumentException.class, () -> new TestFilter(key));
    }

    @Test
    void constructor_WithNullKey_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new TestFilter(null));
    }

    @Test
    void getValue_WithValidResource_ReturnsValue() {
        String key = "testKey";
        String value = "testValue";
        Map<String, String> resource = new HashMap<>();
        resource.put(key, value);

        TestFilter filter = new TestFilter(key);
        assertEquals(value, filter.getValue(resource));
    }

    @Test
    void getValue_WithNullResource_ThrowsException() {
        TestFilter filter = new TestFilter("testKey");
        assertThrows(NullPointerException.class, () -> filter.getValue(null));
    }

    @Test
    void getValue_WithNonExistentKey_ReturnsNull() {
        Map<String, String> resource = new HashMap<>();
        resource.put("otherKey", "value");

        TestFilter filter = new TestFilter("nonExistentKey");
        assertNull(filter.getValue(resource));
    }

    private static class TestFilter extends BaseComparisonFilter {
        public TestFilter(String key) {
            super(key);
        }

        @Override
        public boolean matches(Map<String, String> resource) {
            return false; // Not used in these tests
        }

        @Override
        public <T> T accept(FilterVisitor<T> visitor) {
            return null; // Not used in these tests
        }
    }
}
