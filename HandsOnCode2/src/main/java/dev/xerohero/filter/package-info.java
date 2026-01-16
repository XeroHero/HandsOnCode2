/**
 * Core filtering functionality for building and evaluating filter expressions.
 * 
 * <p>This package provides a comprehensive framework for creating, combining, and evaluating
 * filter expressions that can be used to query or validate resources. The design emphasizes
 * type safety, flexibility, and ease of use.</p>
 *
 * <h2>Key Components</h2>
 * 
 * <h3>Fluent API ({@link dev.xerohero.filter.FluentFilterBuilder})</h3>
 * <p>A builder-pattern API for creating complex filter expressions in a readable, chainable way.</p>
 * <pre>
 * // Example: Create a filter using FluentFilterBuilder
 * Filter filter = FluentFilterBuilder
 *     .where("age").greaterThan(18)
 *     .and("status").is("active")
 *     .or("isAdmin").is(true)
 *     .build();
 * </pre>
 *
 * <h3>Core Interfaces</h3>
 * <dl>
 *   <dt>{@link dev.xerohero.filter.Filter}</dt>
 *   <dd>The base interface for all filter implementations. Defines the core matching contract.</dd>
 *   
 *   <dt>{@link dev.xerohero.filter.FilterBuilder}</dt>
 *   <dd>Alternative builder for creating filter expressions programmatically.</dd>
 * </dl>
 *
 * <h3>Subpackages</h3>
 * <ul>
 *   <li>{@link dev.xerohero.filter.operators} - Core filter implementations</li>
 *   <li>{@link dev.xerohero.filter.parser} - String-to-filter parsing</li>
 *   <li>{@link dev.xerohero.filter.serialization} - JSON serialization/deserialization</li>
 *   <li>{@link dev.xerohero.filter.visitor} - Visitor pattern for filter traversal</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 * 
 * <h3>Creating Filters</h3>
 * <pre>
 * // Using FluentFilterBuilder
 * Filter userFilter = FluentFilterBuilder
 *     .where("role").in("admin", "editor")
 *     .and("lastLogin").greaterThan("2023-01-01")
 *     .build();
 *
 * // Using direct instantiation
 * Filter ageFilter = new GreaterThanFilter("age", "18");
 * </pre>
 *
 * <h3>Combining Filters</h3>
 * <pre>
 * // Using Fluent API
 * Filter combined = FluentFilterBuilder
 *     .where("status").is("active")
 *     .and("department").is("engineering")
 *     .or("isContractor").is(true)
 *     .build();
 *
 * // Using static methods
 * Filter combined2 = Filter.and(
 *     new EqualsFilter("status", "active"),
 *     Filter.or(
 *         new EqualsFilter("department", "engineering"),
 *         new EqualsFilter("isContractor", "true")
 *     )
 * );
 * </pre>
 *
 * <h2>Best Practices</h2>
 * <ul>
 *   <li>Use {@code FluentFilterBuilder} for better readability when building complex filters</li>
 *   <li>For simple filters, direct instantiation may be more straightforward</li>
 *   <li>Reuse common filter patterns as constants or factory methods</li>
 *   <li>Consider using the visitor pattern for operations on filter trees</li>
 * </ul>
 *
 * @see dev.xerohero.filter.FluentFilterBuilder For the fluent API
 * @see dev.xerohero.filter.Filter The base filter interface
 * @see dev.xerohero.filter.ValueComparator For details on value comparison rules
 */
package dev.xerohero.filter;
