# HandsOnCode Assignment 2
#### A filter system that uses the Visitor pattern to process and convert filter expressions into string representations
[![Java CI with Maven](https://github.com/XeroHero/HandsOnCode2/actions/workflows/maven.yml/badge.svg)](https://github.com/XeroHero/HandsOnCode2/actions/workflows/maven.yml)
[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

Project developed for Java JDK17 and above (tested on IntelliJ IDEA with OpenJDK 25 and OpenJDK 17, language level 25).

⚠️To execute, enter the submodule folder (`HandsOnCode2 -> HandsOnCode2`). Due to my laptop's quirk, I had to structure it this way.

## 🔍 Core Concepts

The Filter Framework is built around several key concepts:
2. **Logical Operators**: Combine multiple filters (AND, OR, NOT)
3. **Comparison Operators**: Perform specific comparisons on resource properties
4. **Visitor Pattern**: Enables operations on filters without modifying their classes
5. **Fluent API**: Easy construction of complex filter expressions

### How it works

This set of filters is based on the 'Visitor' design pattern: the filter runs on the whole data structure, 'visiting' each element as it progresses the task.

### Implementation
#### 1. Filter Interface
The base Filter interface defines two main methods:
- `matches()`, which checks if the resource matching the filter is present
- `accept()`, which accepts the visitor to process the filter operation on the data

#### 2. Filter Implementation
**A. Logical Operators**
- `AndFilter` represents logical AND between 2 filters
- `OrFilter` represents logical OR between 2 filters
- `NotFilter` represents logical NOT on a filter
- `TrueFilter` is a constant filter that alwasy will return `true`
- `FalseFilter` is a constant filter that always returns `false`

**B. Comparison Operators**
- `EqualsFilter` checks if a property equals a value (`==` or `.equals(...)`)
- `LessThanFilter` checks if a property is less than a value (`<`)
- `GreaterThanFilter` checks if a property is greater than a value (`>`)
- `RegexFilter` checks is a property matches a regular expression (regex)
- `HasPropertyFilter` checks if a property exists in the dataset (`.contains(...)`)

**C. `ToStringVisitor`**
- Implements the Visitor pattern to convert filters into their string representations, with each `visit()` method handling a specific fitler type of the ones shown above. 
- It uses method overloading to provide type-specific behaviour.

### How it works
**1. Visitor Pattern:** The `accept()` method in each filter calls the appropriate `visit()` method on the visitor, checking for type-specific processing without requiring an `instanceOf` check.

**2. String Building:** For composite filters with 2 or more AND/OR filters, it recursively porcesses the child filters, using Java Streams to porcess and join them wiht the appropriate operator. Eg. `(age < 30 && name == "Lorenzo")`

**Key Features:**
- Type Safety: Each filter type has its own class and visitor method
- Extensibility: Easy to add new filter types by implementing the Filter interface
- Flexibility: The visitor pattern allows for different types of processing (not just string conversion)
- Immutability: Most filter implementations are records, making them immutable

This design makes the code library useful for building and manipulating complex filter expressions that can be used for querying or filtering data, with the ability to easily convert them to different representations (like SQL WHERE clauses, API query parameters, etc.) by implementing different visitors.

## Available Filters

### Comparison Filters
- `equals(key, value)`: Case-insensitive equality check
- `greaterThan(key, value)`: Numeric or string comparison
- `lessThan(key, value)`: Numeric or string comparison
- `hasProperty(key)`: Checks if a property exists
- `matchesRegex(key, pattern)`: Regex pattern matching

### Logical Operators
- `and(filter1, filter2, ...)`: All filters must match
- `or(filter1, filter2, ...)`: Any filter can match
- `not(filter)`: Inverts the result of a filter

### FilterBuilder Methods

| Method | Description | Example |
|--------|-------------|---------|
| `and(Filter...)` | Logical AND of multiple filters | `and(equals("a", "1"), equals("b", "2"))` |
| `or(Filter...)` | Logical OR of multiple filters | `or(equals("role", "admin"), equals("role", "moderator"))` |
| `not(Filter)` | Logical NOT of a filter | `not(equals("status", "banned"))` |
| `equals(key, value)` | Case-insensitive equality | `equals("status", "active")` |
| `greaterThan(key, value)` | Numeric or string comparison | `greaterThan("age", "18")` |
| `lessThan(key, value)` | Numeric or string comparison | `lessThan("price", "100")` |
| `hasProperty(key)` | Checks if property exists | `hasProperty("email")` |
| `matchesRegex(key, regex)` | Matches against regex | `matchesRegex("email", ".+@.+\\..+")` |
| `alwaysTrue()` | Always matches | `alwaysTrue()` |
| `alwaysFalse()` | Never matches | `alwaysFalse()` |

## ⚡ Performance Considerations

### Time Complexity
- **Simple filters** (Equals, HasProperty): O(1) per check
- **Logical operators**:
    - `AndFilter`: O(n) where n is the number of sub-filters (short-circuits on first false)
    - `OrFilter`: O(n) where n is the number of sub-filters (short-circuits on first true)
    - `NotFilter`: O(1) plus the cost of the wrapped filter
- **Comparison operators**:
    - Numeric comparisons: O(1) after parsing
    - String comparisons: O(k) where k is the length of the strings
    - Regex: O(m) where m is the length of the input string (depends on pattern complexity)

### Memory Usage
- Each filter instance is immutable and thread-safe
- Consider reusing filter instances when possible
- Be cautious with very large filter expressions (deeply nested)

### Optimization Tips
1. **Place most selective filters first** in AND conditions
2. **Use appropriate comparison types** (e.g., prefer numeric comparisons for numbers)
3. **Cache compiled regex patterns** if using the same pattern repeatedly
4. **Consider filter simplification** for complex expressions

## 🛡️ Error Handling

### Common Exceptions

| Exception | Cause | Recommended Action |
|-----------|-------|-------------------|
| `NullPointerException` | Null resource or visitor | Check for null before passing to methods |
| `IllegalArgumentException` | Invalid arguments (null/empty keys) | Validate inputs before creating filters |
| `PatternSyntaxException` | Invalid regex pattern | Validate regex patterns before use |
| `NumberFormatException` | Invalid numeric format | Ensure numeric fields contain valid numbers |

### Best Practices
1. **Validate (sanitize) inputs** before creating filters
2. **Handle null values** appropriately in custom filters
3. **Use specific exception types** for different error cases
4. **Document error conditions** in method contracts

## 🧪 Testing
Test suite included in the code comprises 53 test cases, testing basic code functionality. This can be launched via the following command

   `mvn test`

