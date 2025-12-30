/**
 * Contains the visitor pattern implementation for filter traversal and processing.
 * 
 * <h2>Overview</h2>
 * <p>The visitor pattern allows you to add new operations to existing filter classes
 * without modifying them. This is particularly useful for:
 * <ul>
 *   <li>Converting filters to different formats (e.g., SQL, JSON)</li>
 *   <li>Validating filter expressions</li>
 *   <li>Collecting statistics about filter structures</li>
 *   <li>Transforming filter expressions</li>
 * </ul>
 * 
 * <h2>Core Components</h2>
 * <dl>
 *   <dt>{@link dev.xerohero.filter.visitor.FilterVisitor}</dt>
 *   <dd>Interface defining visit methods for each filter type</dd>
 *   
 *   <dt>{@link dev.xerohero.filter.visitor.ToStringVisitor}</dt>
 *   <dd>Converts filters to their string representation</dd>
 * </dl>
 * 
 * <h2>Example: Custom Visitor</h2>
 * <pre>{@code
 * public class JsonVisitor implements FilterVisitor<String> {
 *     @Override
 *     public String visit(AndFilter filter) {
 *         return String.format("{\"and\": [%s]}", 
 *             Arrays.stream(filter.getFilters())
 *                   .map(f -> f.accept(this))
 *                   .collect(Collectors.joining(",")));
 *     }
 *     
 *     // Implement other visit methods...
 * }
 * }</pre>
 * 
 * @see dev.xerohero.filter.Filter#accept
 * @see dev.xerohero.filter.visitor.FilterVisitor
 */
package dev.xerohero.filter.visitor;
