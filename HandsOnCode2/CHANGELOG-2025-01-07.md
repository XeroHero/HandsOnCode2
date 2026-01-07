# Changelog - January 7, 2025

## Overview
This document details the changes made to resolve compilation issues in the filter parser's logical operator handling. The changes primarily affect how `AndFilter` and `OrFilter` instances are constructed when parsing filter expressions.

## Issue Details

### Problem Description
The filter parser was encountering compilation errors when creating `AndFilter` and `OrFilter` instances using varargs syntax. The error message indicated a type mismatch between the expected and actual method arguments.

### Root Cause Analysis
- The issue stemmed from how Java handles type inference with varargs and generic types
- The `AndFilter` and `OrFilter` classes are implemented as records with varargs constructors
- When using these constructors with generic `Filter` types, the Java compiler had difficulty inferring the correct type parameters
- The error occurred in the `parseSimpleExpression` method where logical operators were being processed

## Changes Made

### 1. Filter Parser Modifications
**File**: `src/main/java/dev/xerohero/filter/parser/FilterParser.java`

#### Before:
```java
// Original varargs constructor usage
return new AndFilter(parseSimpleExpression(left), parseSimpleExpression(right));
return new OrFilter(parseSimpleExpression(left), parseSimpleExpression(right));
```

#### After:
```java
// Explicit array construction for type safety
return new AndFilter(new Filter[]{parseSimpleExpression(left), parseSimpleExpression(right)});
return new OrFilter(new Filter[]{parseSimpleExpression(left), parseSimpleExpression(right)});
```

### 2. Technical Implementation Details
- **Type Safety**: The explicit array construction ensures the Java compiler can properly infer the generic type parameters
- **Backward Compatibility**: The change is fully backward compatible as it doesn't modify the underlying filter behavior
- **Performance Impact**: Negligible runtime impact as the array creation would have happened internally with varargs anyway
- **Code Clarity**: The explicit array syntax makes the code's intent clearer to other developers

### 3. Affected Components
- `FilterParser` class in the filter parsing module
- Logical operator handling for both AND and OR operations
- Expression parsing and tree construction

## Testing
### Test Cases Verified
1. **Basic Logical Operations**
   - Simple AND conditions (e.g., `status = "active" AND role = "admin"`)
   - Simple OR conditions (e.g., `role = "admin" OR role = "superuser"`)
   - Nested logical expressions with proper operator precedence

2. **Edge Cases**
   - Empty expressions
   - Expressions with extra whitespace
   - Nested parentheses
   - Complex combinations of AND/OR operations

## Impact Analysis
### Benefits
- Resolves compilation errors without changing runtime behavior
- Improves code maintainability by being explicit about types
- Reduces potential for similar issues in future development

### Potential Risks
- None identified - the change is purely syntactic and doesn't affect the runtime behavior

## Documentation Updates
- Updated inline code comments for better clarity
- Ensured consistent code formatting throughout the parser
- Added this changelog entry for future reference

## Class Documentation

### RegexFilter
**Package**: `dev.xerohero.filter.operators.comparison`
**Extends**: `BaseComparisonFilter`
**Implements**: `Filter`

#### Overview
The `RegexFilter` is a specialized filter that performs case-insensitive pattern matching using regular expressions. It's particularly useful for advanced string matching scenarios where simple equality or comparison operators are insufficient.

#### Key Features
- **Case-Insensitive Matching**: By default, performs case-insensitive pattern matching
- **Regex Support**: Full Java regular expression support
- **Null Safety**: Properly handles null values in resource maps
- **Immutable**: Thread-safe implementation

#### Usage Example
```java
// Match email addresses
Filter emailFilter = new RegexFilter("email", "^[A-Za-z0-9+_.-]+@(.+)$");

// Match phone numbers
Filter phoneFilter = new RegexFilter("phone", "^\\+?[0-9. ()-]{10,}$");
```

### ValueComparator
**Package**: `dev.xerohero.filter`

#### Overview
The `ValueComparator` utility class provides advanced comparison capabilities for different types of values, including numbers, strings, and regular expressions. It handles type conversion and comparison logic used throughout the filtering system.

#### Key Features
- **Type-Aware Comparison**: Automatically detects and compares different numeric types
- **Case Handling**: Configurable case sensitivity for string comparisons
- **Regex Support**: Special handling for regex pattern matching
- **Null Safety**: Explicit null checking
- **Large Number Support**: Handles very large integers using `BigInteger`

#### Comparison Logic
1. Numeric comparison (integers, decimals, scientific notation)
2. Regex pattern matching (when values are wrapped in `/`)
3. Case-insensitive string comparison by default
4. Case-sensitive comparison for regex patterns

#### Usage Example
```java
// Numeric comparison
int result = ValueComparator.compare("42", "3.14"); // returns positive

// String comparison
int strCompare = ValueComparator.compare("apple", "Banana"); // case-insensitive

// Regex matching
int regexCompare = ValueComparator.compare("test", "/^te.*/"); // returns 0 for match
```

### FilterBuilder
**Package**: `dev.xerohero.filter`

#### Overview
The `FilterBuilder` provides a fluent API for constructing complex filter expressions programmatically. It supports creating both simple and composite filters with a clean, readable syntax.

#### Key Features
- **Fluent API**: Method chaining for building complex expressions
- **Type Safety**: Compile-time checking of filter construction
- **Composite Filters**: Easy creation of AND/OR combinations
- **Factory Methods**: Static methods for all filter types
- **Parser Integration**: Built-in support for parsing filter expressions

#### Common Operations
- **Comparison Operators**: `equalTo`, `notEquals`, `lessThan`, `greaterThan`, etc.
- **Logical Operators**: `and()`, `or()`, `not()`
- **String Operations**: `matchesRegex`, `hasProperty`
- **Composite Filters**: `andFilter()`, `orFilter()`

#### Usage Examples
```java
// Simple filter
Filter simple = FilterBuilder.equalTo("status", "active");

// Complex filter with AND/OR
Filter complex = FilterBuilder.and()
    .withEqualTo("status", "active")
    .withGreaterThan("age", "21")
    .or(
        FilterBuilder.matchesRegex("email", ".*@company\.com$"),
        FilterBuilder.equalTo("role", "admin")
    )
    .build();

// Using the parser
Filter parsed = FilterBuilder.parse("status = 'active' AND (age > 21 OR role = 'admin')");
```

#### Best Practices
1. Use method chaining for better readability
2. Group related conditions with parentheses
3. Use static imports for cleaner code
4. Consider using the parser for user-provided filter expressions
5. Reuse common filter components

## Future Considerations

### AndFilter
**Package**: `dev.xerohero.filter.operators`
**Implements**: `Filter`

#### Overview
The `AndFilter` is a composite filter that implements a logical AND operation across multiple filters. It returns `true` only if all of its component filters return `true` for a given resource. The evaluation is short-circuiting, meaning it will stop at the first filter that returns `false`.

#### Key Features
- **Varargs Constructor**: Accepts multiple `Filter` instances using Java's varargs
- **Null Safety**: Validates that no filter in the array is null
- **Short-circuit Evaluation**: Stops evaluation as soon as any filter returns `false`
- **Immutable**: Implemented as a Java record for immutability

#### Usage Example
```java
Filter nameFilter = new EqualsFilter("name", "John");
Filter statusFilter = new EqualsFilter("status", "active");
Filter andFilter = new AndFilter(nameFilter, statusFilter);
boolean result = andFilter.matches(resource); // true only if both conditions are met
```

### OrFilter
**Package**: `dev.xerohero.filter.operators`
**Implements**: `Filter`

#### Overview
The `OrFilter` is a composite filter that implements a logical OR operation across multiple filters. It returns `true` if any of its component filters return `true` for a given resource. The evaluation is short-circuiting, meaning it will return `true` as soon as it finds the first matching filter.

#### Key Features
- **Varargs Constructor**: Accepts multiple `Filter` instances using Java's varargs
- **Null Safety**: Validates that no filter in the array is null
- **Short-circuit Evaluation**: Stops evaluation as soon as any filter returns `true`
- **Immutable**: Implemented as a Java record for immutability

#### Usage Example
```java
Filter adminFilter = new EqualsFilter("role", "admin");
Filter superuserFilter = new EqualsFilter("role", "superuser");
Filter orFilter = new OrFilter(adminFilter, superuserFilter);
boolean result = orFilter.matches(resource); // true if either condition is met
```

## Future Considerations
- Consider adding unit tests specifically targeting the logical operator parsing
- Evaluate if similar changes are needed elsewhere in the codebase
- Document the pattern for future developers to follow
