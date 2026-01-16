/**
 * Provides parsing functionality for filter expressions in a simple query language.
 * 
 * <p>This package contains the components needed to parse string-based filter expressions
 * into executable Filter objects that can be used to evaluate conditions against resources.</p>
 *
 * <h2>Main Components</h2>
 * <ul>
 *   <li>{@link dev.xerohero.filter.parser.FilterParser} - Main parser class for converting string expressions to Filter objects</li>
 *   <li>{@link dev.xerohero.filter.parser.FilterParseException} - Exception thrown when parsing fails due to invalid input</li>
 * </ul>
 *
 * <h2>Expression Syntax</h2>
 * <p>The parser supports the following syntax:</p>
 * <ul>
 *   <li><b>Comparisons</b>: {@code property operator value}</li>
 *   <li><b>String literals</b>: Enclose in double quotes: {@code "value"}</li>
 *   <li><b>Logical AND</b>: {@code condition1 AND condition2} or {@code condition1 && condition2}</li>
 *   <li><b>Logical OR</b>: {@code condition1 OR condition2} or {@code condition1 || condition2}</li>
 *   <li><b>Logical NOT</b>: {@code NOT condition} or {@code !condition}</li>
 *   <li><b>Grouping</b>: Use parentheses: {@code (condition1 OR condition2) AND condition3}</li>
 * </ul>
 *
 * <h2>Supported Operators</h2>
 * <ul>
 *   <li><b>=</b> - Equals</li>
 *   <li><b>!=</b> - Not equals</li>
 *   <li><b>></b> - Greater than</li>
 *   <li><b>>=</b> - Greater than or equal to</li>
 *   <li><b><</b> - Less than</li>
 *   <li><b><=</b> - Less than or equal to</li>
 *   <b>~</b> - Regex match</li>
 * </ul>
 *
 * <h2>Examples</h2>
 * <pre>
 * // Simple comparison
 * Filter f1 = FilterParser.parse("name = \"John\"");
 * 
 * // Numeric comparison
 * Filter f2 = FilterParser.parse("age > 25");
 * 
 * // Logical operators
 * Filter f3 = FilterParser.parse("status = \"active\" AND (role = \"admin\" OR role = \"superuser\")");
 * 
 * // Regex match
 * Filter f4 = FilterParser.parse("email ~ \".*@example\\.com$"");
 * </pre>
 *
 * @see dev.xerohero.filter.Filter The base Filter interface
 * @see dev.xerohero.filter.operators For the filter implementations used by the parser
 */
package dev.xerohero.filter.parser;
