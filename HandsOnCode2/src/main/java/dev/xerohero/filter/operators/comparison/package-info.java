/**
 * Contains implementations of comparison operators for filter expressions.
 * 
 * <p>This package provides a set of comparison filters that can be used to evaluate
 * conditions against resource properties. These filters support type-aware comparison
 * of different value types, including numbers, strings, and special types.</p>
 *
 * <h2>Comparison Operators</h2>
 * <dl>
 *   <dt>{@link dev.xerohero.filter.operators.comparison.EqualsFilter}</dt>
 *   <dd>Checks if a resource's value equals the specified value. Supports null checks.</dd>
 *   
 *   <dt>{@link dev.xerohero.filter.operators.comparison.NotEqualsFilter}</dt>
 *   <dd>Checks if a resource's value does not equal the specified value.</dd>
 *   
 *   <dt>{@link dev.xerohero.filter.operators.comparison.GreaterThanFilter}</dt>
 *   <dd>Checks if a resource's value is greater than the specified value.</dd>
 *   
 *   <dt>{@link dev.xerohero.filter.operators.comparison.GreaterThanOrEqualFilter}</dt>
 *   <dd>Checks if a resource's value is greater than or equal to the specified value.</dd>
 *   
 *   <dt>{@link dev.xerohero.filter.operators.comparison.LessThanFilter}</dt>
 *   <dd>Checks if a resource's value is less than the specified value.</dd>
 *   
 *   <dt>{@link dev.xerohero.filter.operators.comparison.LessThanOrEqualFilter}</dt>
 *   <dd>Checks if a resource's value is less than or equal to the specified value.</dd>
 *   
 *   <dt>{@link dev.xerohero.filter.operators.comparison.RegexFilter}</dt>
 *   <dd>Checks if a resource's value matches the specified regular expression pattern.</dd>
 *   
 *   <dt>{@link dev.xerohero.filter.operators.comparison.HasPropertyFiltre}</dt>
 *   <dd>Checks if a resource contains the specified property, regardless of its value.</dd>
 * </dl>
 *
 * <h2>Type Handling</h2>
 * <p>Comparison filters use {@link dev.xerohero.filter.ValueComparator} to handle different
 * value types, including:</p>
 * <ul>
 *   <li>Numeric types (integers, decimals, scientific notation)</li>
 *   <li>String comparisons (case-insensitive by default)</li>
 *   <li>Null values (treated as non-existent properties)</li>
 *   <li>Regular expression patterns</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 * <pre>
 * // Create filters programmatically
 * Filter ageFilter = new GreaterThanOrEqualFilter("age", "18");
 * Filter nameFilter = new EqualsFilter("name", "John");
 * Filter emailFilter = new RegexFilter("email", ".*@example\\.com");
 * 
 * // Check if a resource matches the filter
 * Map<String, String> resource = Map.of("age", "25", "name", "John", "email", "john@example.com");
 * boolean matches = ageFilter.matches(resource) && nameFilter.matches(resource);
 * </pre>
 *
 * <h2>String Literals vs. Numbers</h2>
 * <p>When comparing values, the system will attempt to convert string representations
 * to appropriate numeric types for comparison. For example:</p>
 * <ul>
 *   <li>{@code "100" > "20"} evaluates to true (numeric comparison)</li>
 *   <li>{@code "100" > "99"} evaluates to true (numeric comparison)</li>
 *   <li>{@code "100" > "99a"} falls back to string comparison</li>
 *   <li>{@code "100" = 100} evaluates to true (type conversion)</li>
 * </ul>
 *
 * @see dev.xerohero.filter.operators.BaseComparisonFilter Base class for all comparison filters
 * @see dev.xerohero.filter.ValueComparator For details on type conversion and comparison rules
 * @see dev.xerohero.filter.Filter The base Filter interface
 */
package dev.xerohero.filter.operators.comparison;
