/**
 * Contains the core filter implementations for logical and comparison operations.
 * This package provides a flexible and extensible way to build and combine filters
 * for evaluating conditions against resources.
 *
 * <h2>Base Classes</h2>
 * <ul>
 *   <li>{@link dev.xerohero.filter.operators.BaseComparisonFilter} - Base class for all comparison filters</li>
 * </ul>
 *
 * <h2>Logical Operators</h2>
 * <ul>
 *   <li>{@link dev.xerohero.filter.operators.AndFilter} - Logical AND operation (combines multiple filters with AND logic)</li>
 *   <li>{@link dev.xerohero.filter.operators.OrFilter} - Logical OR operation (combines multiple filters with OR logic)</li>
 *   <li>{@link dev.xerohero.filter.operators.NotFilter} - Logical NOT operation (inverts the result of a filter)</li>
 *   <li>{@link dev.xerohero.filter.operators.TrueFilter} - Singleton filter that always evaluates to true</li>
 *   <li>{@link dev.xerohero.filter.operators.FalseFilter} - Singleton filter that always evaluates to false</li>
 * </ul>
 *
 * <h2>Comparison Operators</h2>
 * <ul>
 *   <li>{@link dev.xerohero.filter.operators.comparison.EqualsFilter} - Equality comparison (case-insensitive)</li>
 *   <li>{@link dev.xerohero.filter.operators.comparison.NotEqualsFilter} - Inequality comparison</li>
 *   <li>{@link dev.xerohero.filter.operators.comparison.GreaterThanFilter} - Greater than comparison</li>
 *   <li>{@link dev.xerohero.filter.operators.comparison.GreaterThanOrEqualFilter} - Greater than or equal comparison</li>
 *   <li>{@link dev.xerohero.filter.operators.comparison.LessThanFilter} - Less than comparison</li>
 *   <li>{@link dev.xerohero.filter.operators.comparison.LessThanOrEqualFilter} - Less than or equal comparison</li>
 *   <li>{@link dev.xerohero.filter.operators.comparison.RegexFilter} - Regular expression pattern matching</li>
 *   <li>{@link dev.xerohero.filter.operators.comparison.HasPropertyFiltre} - Checks if a property exists in the resource</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>
 * // Create a filter for age > 21 AND (name contains 'John' OR status equals 'active')
 * Filter filter = new AndFilter(
 *     new GreaterThanFilter("age", "21"),
 *     new OrFilter(
 *         new RegexFilter("name", ".*John.*"),
 *         new EqualsFilter("status", "active")
 *     )
 * );
 * </pre>
 *
 * @see dev.xerohero.filter.Filter The base Filter interface
 * @see dev.xerohero.filter.FilterBuilder For a builder pattern alternative
 * @see dev.xerohero.filter.FluentFilterBuilder For a fluent API alternative
 */

package dev.xerohero.filter.operators;
