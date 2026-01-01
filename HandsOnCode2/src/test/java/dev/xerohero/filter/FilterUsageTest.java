package dev.xerohero.filter;

import dev.xerohero.filter.operators.FalseFilter;
import dev.xerohero.filter.operators.TrueFilter;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterUsageTest {
    
    @Test
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
    
    @Test
    void testTrueFilter() {
        Map<String, String> user = new HashMap<>();
        user.put("firstname", "Joe");
        
        Filter trueFilter = new TrueFilter();
        assertTrue(trueFilter.matches(user));
    }
}
