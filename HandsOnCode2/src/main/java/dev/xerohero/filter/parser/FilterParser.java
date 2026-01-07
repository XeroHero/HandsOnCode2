package dev.xerohero.filter.parser;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.operators.AndFilter;
import dev.xerohero.filter.operators.NotFilter;
import dev.xerohero.filter.operators.OrFilter;
import dev.xerohero.filter.operators.comparison.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Parser for filter expressions in a simple query language.
 * Supports basic comparisons and logical operators.
 * Example queries:
 * - name = "John"
 * - age > 25
 * - status = "active" AND (role = "admin" OR role = "superuser")
 */
public class FilterParser {

    private static final Map<String, BiFunction<String, String, Filter>> OPERATORS = new HashMap<>();

    static {
        OPERATORS.put("=", EqualsFilter::new);
        OPERATORS.put("!=", NotEqualsFilter::new);
        OPERATORS.put(">", GreaterThanFilter::new);
        OPERATORS.put(">=", GreaterThanOrEqualFilter::new);
        OPERATORS.put("<", LessThanFilter::new);
        OPERATORS.put("<=", LessThanOrEqualFilter::new);
        OPERATORS.put("~", RegexFilter::new);
    }

    /**
     * Parses a filter expression string into a Filter object.
     *
     * @param expression The filter expression to parse
     * @return A Filter object representing the parsed expression
     * @throws FilterParseException if the expression is invalid
     */
    public static Filter parse(String expression) {
        if (expression == null) {
            throw new FilterParseException("Expression cannot be null");
        }

        String trimmed = expression.trim();
        if (trimmed.isEmpty()) {
            throw new FilterParseException("Expression cannot be empty");
        }

        // Check for invalid expression format (missing operator)
        // This pattern matches "word whitespace something" but not "word operator something"
        if (trimmed.matches("^\\w+\\s+[^=<>!~].*") && !trimmed.matches(".*\\b(AND|OR|NOT)\\b.*") && !trimmed.matches(".*[=<>!~].*")) {
            throw new FilterParseException("Invalid expression: missing operator in '" + trimmed + "'");
        }

        return parseSimpleExpression(trimmed);
    }

    // Add this method to validate field names
    private static boolean isValidFieldName(String fieldName) {
        // Field names must start with a letter or underscore, followed by letters, digits, or underscores
        return fieldName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
    }

    private static Filter parseSimpleExpression(String expr) {
        // Check for empty expression
        if (expr == null || expr.trim().isEmpty()) {
            throw new FilterParseException("Expression cannot be null or empty");
        }

        String trimmedExpr = expr.trim();

        // Check for unbalanced parentheses
        if (countChar(trimmedExpr, '(') != countChar(trimmedExpr, ')')) {
            throw new FilterParseException("Unbalanced parentheses in expression: " + expr);
        }

        // Check for expressions with spaces but no operators (e.g., "name \"John\"")
        if (trimmedExpr.matches(".*\\s+.*") &&  // Contains whitespace
                !trimmedExpr.matches(".*[=<>!~].*") &&  // No comparison operators
                !trimmedExpr.matches(".*\\b(AND|OR|NOT)\\b.*") &&  // No logical operators
                !(trimmedExpr.startsWith("(") && trimmedExpr.endsWith(")"))) {  // Not a parenthesized expression
            // Check if this is a case of missing operator between expressions
            if (trimmedExpr.matches(".*['\"].* +[^=<>!~].*['\"].*")) {
                throw new FilterParseException("Invalid expression: missing operator between expressions in: " + expr);
            }
            throw new FilterParseException("Invalid expression: missing operator in '" + expr + "'");
        }
        // First, try to split by top-level AND/OR operators
        int andPos = findTopLevelOperator(expr, "AND");
        if (andPos != -1) {
            String left = expr.substring(0, andPos).trim();
            String right = expr.substring(andPos + 3).trim();
            if (left.isEmpty() || right.isEmpty()) {
                throw new FilterParseException("Invalid AND expression: " + expr);
            }
            return new AndFilter(new Filter[]{parseSimpleExpression(left), parseSimpleExpression(right)});
        }

        int orPos = findTopLevelOperator(expr, "OR");
        if (orPos != -1) {
            String left = expr.substring(0, orPos).trim();
            String right = expr.substring(orPos + 2).trim();
            if (left.isEmpty() || right.isEmpty()) {
                throw new FilterParseException("Invalid OR expression: " + expr);
            }
            return new OrFilter(new Filter[]{parseSimpleExpression(left), parseSimpleExpression(right)});
        }

        // Handle parentheses
        if (expr.startsWith("(") && expr.endsWith(")")) {
            String subExpr = expr.substring(1, expr.length() - 1).trim();
            if (subExpr.isEmpty()) {
                throw new FilterParseException("Empty parentheses in expression: " + expr);
            }
            return parseSimpleExpression(subExpr);
        }

        // Handle NOT operator
        if (expr.toUpperCase().startsWith("NOT ")) {
            String subExpr = expr.substring(3).trim();
            if (subExpr.isEmpty()) {
                throw new FilterParseException("Invalid NOT expression: " + expr);
            }
            return new NotFilter(parseSimpleExpression(subExpr));
        }

        // Handle comparison operators
        for (Map.Entry<String, BiFunction<String, String, Filter>> entry : OPERATORS.entrySet()) {
            String op = entry.getKey();
            int opPos = findOperator(expr, op);
            if (opPos > 0) { // Ensure operator is not at the start
                String key = expr.substring(0, opPos).trim();
                String value = expr.substring(opPos + op.length()).trim();

                if (key.isEmpty()) {
                    throw new FilterParseException("Missing key in expression: " + expr);
                }
                if (value.isEmpty()) {
                    throw new FilterParseException("Missing value in expression: " + expr);
                }

                // Validate field name
                if (!isValidFieldName(key)) {
                    throw new FilterParseException("Invalid field name: " + key + ". Field names must start with a letter or underscore, followed by letters, digits, or underscores.");
                }

                // Check for empty quoted values
                if ((value.startsWith("'") && value.endsWith("'")) || 
                    (value.startsWith("\"") && value.endsWith("\""))) {
                    if (value.length() == 2) { // Just quotes with nothing between them
                        throw new FilterParseException("Empty value in expression: " + expr);
                    }
                    value = value.substring(1, value.length() - 1);
                }

                // Check for unclosed quotes
                if ((value.startsWith("'") && !value.endsWith("'")) || 
                    (value.startsWith("\"") && !value.endsWith("\"")) ||
                    (!value.startsWith("'") && value.endsWith("'")) || 
                    (!value.startsWith("\"") && value.endsWith("\""))) {
                    throw new FilterParseException("Unclosed quotes in value: " + value);
                }

                return entry.getValue().apply(key, value);
            }
        }

        // If we get here and the expression contains spaces but no operator, it's invalid
        if (expr.trim().contains(" ")) {
            throw new FilterParseException("Invalid expression: " + expr);
        }

        throw new FilterParseException("Unsupported expression: " + expr);
    }

    // Helper method to find operator position, handling spaces around operator
    private static int findOperator(String expr, String op) {
        int pos = expr.indexOf(op);
        while (pos >= 0) {
            // Check if the operator is at the start or end of the string (invalid for comparison)
            if (pos == 0 || pos + op.length() == expr.length()) {
                pos = expr.indexOf(op, pos + 1);
                continue;
            }

            // Check if the operator is properly surrounded by whitespace or is part of a word
            boolean validBefore = Character.isWhitespace(expr.charAt(pos - 1));
            boolean validAfter = Character.isWhitespace(expr.charAt(pos + op.length()));

            if (validBefore && validAfter) {
                return pos;
            }

            // Look for next occurrence of the operator
            pos = expr.indexOf(op, pos + 1);
        }
        return -1;
    }

    private static int countChar(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }

    private static int findTopLevelOperator(String expr, String op) {
        int depth = 0;
        String upperExpr = expr.toUpperCase();
        op = op.toUpperCase();

        // Process the expression from left to right
        for (int i = 0; i < upperExpr.length() - op.length() + 1; i++) {
            char c = upperExpr.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if (depth == 0 && upperExpr.startsWith(op, i)) {
                // Check if this is a whole word match
                boolean startOk = i == 0 || Character.isWhitespace(upperExpr.charAt(i - 1));
                boolean endOk = (i + op.length() == upperExpr.length()) || Character.isWhitespace(upperExpr.charAt(i + op.length()));

                if (startOk && endOk) {
                    // For AND, make sure it's not part of a larger word
                    if (op.equals("AND") && i + 3 < upperExpr.length() && !Character.isWhitespace(upperExpr.charAt(i + 3))) {
                        i += 2; // Skip past this "AND" to continue searching
                        continue;
                    }
                    return i;
                }
            }
        }
        return -1;
    }
}

