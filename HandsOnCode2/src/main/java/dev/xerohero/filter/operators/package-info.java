/**
 * Contains the core filter implementations for logical and comparison operations.
 *
 * <h2>Logical Operators</h2>
 * <ul>
 *   <li>{@link dev.xerohero.filter.operators.AndFilter} - Logical AND operation</li>
 *   <li>{@link dev.xerohero.filter.operators.OrFilter} - Logical OR operation</li>
 *   <li>{@link dev.xerohero.filter.operators.NotFilter} - Logical NOT operation</li>
 *   <li>{@link dev.xerohero.filter.operators.TrueFilter} - Always matches</li>
 *   <li>{@link dev.xerohero.filter.operators.FalseFilter} - Never matches</li>
 * </ul>
 *
 * <h2>Comparison Operators</h2>
 * <ul>
 *   <li>{@link dev.xerohero.filter.operators.comparison.EqualsFilter} - Case-insensitive equality</li>
 *   <li>{@link dev.xerohero.filter.operators.comparison.GreaterThanFilter} - Greater than comparison</li>
 *   <li>{@link dev.xerohero.filter.operators.comparison.LessThanFilter} - Less than comparison</li>
 *   <li>{@link dev.xerohero.filter.operators.comparison.RegexFilter} - Regular expression matching</li>
 * </ul>
 *
 * <h2>Base Classes</h2>
 * <ul>
 *   <li>{@link dev.xerohero.filter.operators.BaseComparisonFilter} - Base class for all comparison filters</li>
 * </ul>
 *
 * @see dev.xerohero.filter.Filter
 * @see dev.xerohero.filter.FilterBuilder
 */
package dev.xerohero.filter.operators;
