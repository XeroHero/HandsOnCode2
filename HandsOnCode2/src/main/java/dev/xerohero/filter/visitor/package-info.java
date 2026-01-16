/**
 * Implements the Visitor pattern for traversing and processing filter expressions.
 * 
 * <p>This package provides a type-safe way to perform operations on filter expressions
 * without modifying the filter classes themselves. It's particularly useful for:</p>
 * <ul>
 *   <li>Converting filters to different formats (SQL, JSON, custom DSLs)</li>
 *   <li>Validating filter expressions</li>
 *   <li>Collecting statistics about filter structures</li>
 *   <li>Transforming or optimizing filter expressions</li>
 *   <li>Generating documentation from filter definitions</li>
 * </ul>
 *
 * <h2>Core Components</h2>
 * <dl>
 *   <dt>{@link dev.xerohero.filter.visitor.FilterVisitor}</dt>
 *   <dd>Interface defining visit methods for each filter type, enabling type-safe operations
 *   on filter expressions without using instanceof checks.</dd>
 *
 *   <dt>{@link dev.xerohero.filter.visitor.ToStringVisitor}</dt>
 *   <dd>Example implementation that converts filters to a human-readable string representation.</dd>
 * </dl>
 *
 * <h2>Supported Filter Types</h2>
 * <p>The visitor supports all standard filter types including:</p>
 * <ul>
 *   <li>Logical operators: AND, OR, NOT</li>
 *   <li>Comparison operators: =, !=, >, >=, <, <=</li>
 *   <li>Special operators: Regex matching, property existence</li>
 *   <li>Constants: TRUE, FALSE</li>
 * </ul>
 *
 * <h2>Example: Custom JSON Visitor</h2>
 * <pre>{@code
 * public class JsonVisitor implements FilterVisitor<String> {
 *     @Override
 *     public String visit(AndFilter filter) {
 *         String conditions = Arrays.stream(filter.filters())
 *             .map(f -> f.accept(this))
 *             .collect(Collectors.joining(", "));
 *         return String.format("{\"and\": [%s]}", conditions);
 *     }
 *     
 *     @Override
 *     public String visit(EqualsFilter filter) {
 *         return String.format(
 *             "{\"field\": \"%s\", \"op\": \"=\", \"value\": \"%s\"}",
 *             filter.getKey(), filter.getValue()
 *         );
 *     }
 *     
 *     // Implement other visit methods...
 * }
 * }</pre>
 *
 * <h2>Usage</h2>
 * <pre>
 * // Create a filter
 * Filter filter = new AndFilter(
 *     new EqualsFilter("status", "active"),
 *     new GreaterThanFilter("age", "18")
 * );
 * 
 * // Use the ToStringVisitor
 * String result = filter.accept(new ToStringVisitor());
 * // Result: "(status == active && age > 18)"
 * 
 * // Use a custom visitor
 * String json = filter.accept(new JsonVisitor());
 * // Result: {"and": [{"field": "status", "op": "=", "value": "active"}, ...]}
 * </pre>
 *
 * @see dev.xerohero.filter.Filter#accept The accept method that enables visitor pattern
 * @see dev.xerohero.filter.operators For the filter implementations that can be visited
 */
package dev.xerohero.filter.visitor;
