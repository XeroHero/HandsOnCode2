package dev.xerohero.filter;

import dev.xerohero.filter.operators.FalseFilter;
import dev.xerohero.filter.operators.OrFilter;
import dev.xerohero.filter.operators.TrueFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Example usage of the filter API.
 */
public class FilterUsage {
    
    /**
     * Demonstrates basic filter usage.
     */
    public static void main(String[] args) {
        // Create a sample user
        Map<String, String> user = new HashMap<>();
        user.put("firstname", "Joe");
        user.put("lastname", "Bloggs");
        user.put("role", "Administrator");
        user.put("age", "35");

        // Example 1: Using FalseFilter
        Filter falseFilter = new FalseFilter();
        System.out.println("False filter result: " + falseFilter.matches(user));

        // Example 2: Using TrueFilter
        Filter trueFilter = new TrueFilter();
        System.out.println("True filter result: " + trueFilter.matches(user));

        // Example 3: Using OrFilter
        Filter orFilter = new OrFilter(trueFilter, falseFilter);
        System.out.println("OR filter result: " + orFilter.matches(user));
    }
}
