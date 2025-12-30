/**
 * Provides a flexible and type-safe framework for building and applying filters to collections of resources.
 * 
 * <h2>Overview</h2>
 * <p>The Filter Framework allows you to create complex filter expressions programmatically and apply them to
 * collections of resources. It's designed to be:
 * <ul>
 *   <li><b>Type-safe</b>: Compile-time checking of filter expressions</li>
 *   <li><b>Extensible</b>: Easy to add custom filter types</li>
 *   <li><b>Efficient</b>: Optimized for performance with lazy evaluation</li>
 *   <li><b>Thread-safe</b>: All filter implementations are immutable</li>
 * </ul>
 * 
 * <h2>Core Components</h2>
 * <dl>
 *   <dt>{@link dev.xerohero.filter.Filter}</dt>
 *   <dd>Base interface that all filters implement</dd>
 *   
 *   <dt>{@link dev.xerohero.filter.FilterBuilder}</dt>
 *   <dd>Fluent API for building filter expressions</dd>
 *   
 *   <dt>{@link dev.xerohero.filter.visitor.FilterVisitor}</dt>
 *   <dd>Visitor pattern implementation for filter traversal</dd>
 * </dl>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create a filter: role is 'admin' AND (age > 25 OR status = 'active')
 * Filter filter = FilterBuilder.and(
 *     FilterBuilder.equals("role", "admin"),
 *     FilterBuilder.or(
 *         FilterBuilder.greaterThan("age", "25"),
 *         FilterBuilder.equals("status", "active")
 *     )
 * );
 * 
 * // Apply the filter to a collection
 * List<Map<String, String>> filtered = users.stream()
 *     .filter(filter::matches)
 *     .collect(Collectors.toList());
 * }</pre>
 * 
 * @see dev.xerohero.filter.Filter
 * @see dev.xerohero.filter.FilterBuilder
 * @see dev.xerohero.filter.operators
 * @see dev.xerohero.filter.visitor
 */
package dev.xerohero.filter;
