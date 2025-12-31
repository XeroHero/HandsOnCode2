# Filter Framework

[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A lightweight Java library for building and applying filters to collections of key-value pairs. Perfect for filtering resources based on various conditions.

## Getting Started

Add this to your project's `pom.xml`:

```xml
<dependency>
    <groupId>dev.xerohero</groupId>
    <artifactId>HandsOnCode2</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Quick Example

```java
import dev.xerohero.filter.FilterBuilder;
import java.util.List;
import java.util.Map;

public class Example {
    public static void main(String[] args) {
        // Sample user data
        List<Map<String, String>> users = List.of(
            Map.of("name", "Alice", "age", "30", "role", "admin"),
            Map.of("name", "Bob", "age", "25", "role", "user")
        );

        // Create a filter: role is 'admin' AND age > 25
        var filter = FilterBuilder.and(
            FilterBuilder.equals("role", "admin"),
            FilterBuilder.greaterThan("age", "25")
        );

        // Apply the filter
        var filteredUsers = users.stream()
            .filter(filter::matches)
            .toList();
    }
}
```

## Features

- Simple, fluent API for building filters
- Support for common comparisons (equals, greater than, less than)
- Logical operators (AND, OR, NOT)
- Case-insensitive string comparison
- Extensible through the visitor pattern

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

## Building Complex Filters

```java
// Example: Active admins or users with high score
Filter complexFilter = FilterBuilder.or(
    FilterBuilder.and(
        FilterBuilder.equals("role", "admin"),
        FilterBuilder.equals("status", "active")
    ),
    FilterBuilder.greaterThan("score", "1000")
);
```

## Contributing

Feel free to submit issues and pull requests. For major changes, please open an issue first to discuss your ideas.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📖 API Reference

### Filter Interface

```java
/**
 * Core interface that all filters implement.
 * Defines the contract for matching resources and accepting visitors.
 */
public interface Filter {
    /**
     * Determines if the given resource matches this filter.
     * 
     * @param resource The resource to check against the filter
     * @return true if the resource matches the filter, false otherwise
     * @throws NullPointerException if resource is null
     */
    boolean matches(Map<String, String> resource);
    
    /**
     * Accepts a visitor for this filter (Visitor pattern).
     * 
     * @param <T> The return type of the visitor
     * @param visitor The visitor to accept
     * @return The result of the visitor's operation
     * @throws NullPointerException if visitor is null
     */
    <T> T accept(FilterVisitor<T> visitor);
}
```

### Logical Operators

#### AndFilter
Combines multiple filters with a logical AND operation.

```java
/**
 * A filter that matches when ALL of its sub-filters match.
 * Short-circuits evaluation on the first non-matching filter.
 * 
 * <p>Example: and(equals("a", "1"), equals("b", "2"))</p>
 */
public record AndFilter(Filter... filters) implements Filter {
    @Override
    public boolean matches(Map<String, String> resource) {
        // All filters must match
        for (Filter filter : filters) {
            if (!filter.matches(resource)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
```

#### OrFilter
Combines multiple filters with a logical OR operation.

```java
/**
 * A filter that matches when ANY of its sub-filters match.
 * Short-circuits evaluation on the first matching filter.
 * 
 * <p>Example: or(equals("role", "admin"), equals("role", "moderator"))</p>
 */
public record OrFilter(Filter... filters) implements Filter {
    @Override
    public boolean matches(Map<String, String> resource) {
        // At least one filter must match
        for (Filter filter : filters) {
            if (filter.matches(resource)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
```

#### NotFilter
Inverts the result of another filter.

```java
/**
 * A filter that inverts the result of another filter.
 * 
 * <p>Example: not(equals("status", "banned"))</p>
 */
public record NotFilter(Filter filter) implements Filter {
    @Override
    public boolean matches(Map<String, String> resource) {
        return !filter.matches(resource);
    }
    
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
```

### Comparison Operators

#### BaseComparisonFilter
Abstract base class for all comparison filters.

```java
/**
 * Base class for all comparison filters that operate on a single key-value pair.
 * Provides common functionality for key-based comparisons.
 */
public abstract class BaseComparisonFilter implements Filter {
    protected final String key;

    /**
     * Creates a new BaseComparisonFilter for the specified key.
     * 
     * @param key The key to compare in the resource map
     * @throws IllegalArgumentException if key is null or empty
     */
    protected BaseComparisonFilter(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        this.key = key;
    }

    /**
     * Gets the value for this filter's key from the resource.
     * 
     * @param resource The resource map
     * @return The value for this filter's key, or null if not found
     * @throws NullPointerException if resource is null
     */
    protected String getValue(Map<String, String> resource) {
        Objects.requireNonNull(resource, "Resource cannot be null");
        return resource.get(key);
    }
    
    @Override
    public abstract boolean matches(Map<String, String> resource);
    
    @Override
    public abstract <T> T accept(FilterVisitor<T> visitor);
}
```

#### EqualsFilter
Checks if a property equals a value (case-insensitive).

```java
/**
 * A filter that matches when a property equals a specified value.
 * Performs case-insensitive comparison.
 * 
 * <p>Example: equals("status", "active")</p>
 */
public class EqualsFilter extends BaseComparisonFilter {
    private final String value;

    /**
     * Creates a new EqualsFilter.
     * 
     * @param key The property key to check
     * @param value The value to compare against (case-insensitive)
     * @throws IllegalArgumentException if key is null or empty
     */
    public EqualsFilter(String key, String value) {
        super(key);
        this.value = value;
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource);
        return actualValue != null && actualValue.equalsIgnoreCase(value);
    }
    
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    public String getValue() {
        return value;
    }
}
```

#### GreaterThanFilter / LessThanFilter
Compare numeric or string values.

```java
/**
 * A filter that matches when a property is greater than a specified value.
 * Performs numeric comparison if possible, falls back to string comparison.
 * 
 * <p>Example: greaterThan("age", "18")</p>
 */
public class GreaterThanFilter extends BaseComparisonFilter {
    private final String value;

    /**
     * Creates a new GreaterThanFilter.
     * 
     * @param key The property key to check
     * @param value The value to compare against
     * @throws IllegalArgumentException if key is null or empty, or value is null
     */
    public GreaterThanFilter(String key, String value) {
        super(key);
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.value = value;
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource);
        if (actualValue == null) return false;
        
        // Try numeric comparison first
        try {
            double actual = Double.parseDouble(actualValue);
            double expected = Double.parseDouble(value);
            return actual > expected;
        } catch (NumberFormatException e) {
            // Fall back to string comparison
            return actualValue.compareTo(value) > 0;
        }
    }
    
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    public String getValue() {
        return value;
    }
}
```

### FilterBuilder

```java
/**
 * Fluent builder for creating filter expressions.
 * Provides static factory methods for all filter types.
 */
public final class FilterBuilder {
    private FilterBuilder() {}

    /**
     * Creates an AND filter that matches when ALL sub-filters match.
     * 
     * @param filters The filters to combine with AND
     * @return A new AndFilter instance
     * @throws IllegalArgumentException if filters is null or empty
     */
    public static Filter and(Filter... filters) {
        if (filters == null || filters.length == 0) {
            throw new IllegalArgumentException("At least one filter is required");
        }
        return new AndFilter(filters);
    }
    
    /**
     * Creates an OR filter that matches when ANY sub-filter matches.
     * 
     * @param filters The filters to combine with OR
     * @return A new OrFilter instance
     * @throws IllegalArgumentException if filters is null or empty
     */
    public static Filter or(Filter... filters) {
        if (filters == null || filters.length == 0) {
            throw new IllegalArgumentException("At least one filter is required");
        }
        return new OrFilter(filters);
    }
    
    // Additional builder methods...
}
```

## 🚀 Examples

### Basic Usage

```java
// Simple equality check
Filter isAdmin = FilterBuilder.equals("role", "admin");

// Combining filters with logical operators
Filter filter = FilterBuilder.and(
    FilterBuilder.greaterThan("age", "18"),
    FilterBuilder.or(
        FilterBuilder.equals("status", "active"),
        FilterBuilder.equals("status", "pending")
    )
);

// Using with Optional
Optional<Filter> optionalFilter = Optional.ofNullable(someCondition ? filter : null);
```

### Using with Collections

```java
List<Map<String, String>> users = // ...

// Find active admins
List<Map<String, String>> activeAdmins = users.stream()
    .filter(user -> FilterBuilder.and(
        FilterBuilder.equals("role", "admin"),
        FilterBuilder.equals("status", "active")
    ).matches(user))
    .toList();

// Count premium users
long premiumCount = users.stream()
    .filter(FilterBuilder.or(
        FilterBuilder.equals("tier", "premium"),
        FilterBuilder.greaterThan("purchaseCount", "10")
    )::matches)
    .count();
```

### Advanced Filtering

```java
// Complex filter with nested conditions
Filter complexFilter = FilterBuilder.and(
    FilterBuilder.or(
        FilterBuilder.and(
            FilterBuilder.equals("department", "engineering"),
            FilterBuilder.greaterThan("level", "3")
        ),
        FilterBuilder.and(
            FilterBuilder.equals("department", "management"),
            FilterBuilder.greaterThan("level", "2")
        )
    ),
    FilterBuilder.not(FilterBuilder.equals("status", "inactive")),
    FilterBuilder.matchesRegex("email", ".+@company\\.com$")
);

// Using with a repository
List<User> filteredUsers = userRepository.findAll().stream()
    .filter(user -> complexFilter.matches(user.toMap()))
    .toList();
```

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

### Defensive Programming

```java
try {
    Filter filter = FilterBuilder.equals("age", "25");
    boolean result = filter.matches(userData);
} catch (NullPointerException e) {
    // Handle null input
    logger.error("Invalid input data", e);
} catch (IllegalArgumentException e) {
    // Handle invalid filter configuration
    logger.error("Invalid filter configuration", e);
}
```

### Best Practices
1. **Validate inputs** before creating filters
2. **Handle null values** appropriately in custom filters
3. **Use specific exception types** for different error cases
4. **Document error conditions** in method contracts

## 🧪 Testing

### Unit Tests

```java
class FilterTest {
    private Map<String, String> testUser;
    
    @BeforeEach
    void setUp() {
        testUser = Map.of(
            "id", "123",
            "name", "John Doe",
            "email", "john@example.com",
            "age", "30",
            "role", "admin"
        );
    }
    
    @Test
    void testAdminFilter() {
        Filter adminFilter = FilterBuilder.equals("role", "admin");
        assertTrue(adminFilter.matches(testUser));
    }
    
    @Test
    void testAgeFilter() {
        Filter ageFilter = FilterBuilder.greaterThan("age", "25");
        assertTrue(ageFilter.matches(testUser));
    }
    
    @Test
    void testComplexFilter() {
        Filter filter = FilterBuilder.and(
            FilterBuilder.equals("role", "admin"),
            FilterBuilder.greaterThan("age", "25")
        );
        assertTrue(filter.matches(testUser));
    }
}
```

### Integration Tests

```java
@SpringBootTest
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testFindByFilter() {
        // Create test data
        User admin = new User("admin", "admin@example.com", 30, "admin");
        User user = new User("user", "user@example.com", 20, "user");
        userRepository.saveAll(List.of(admin, user));
        
        // Create and apply filter
        Filter filter = FilterBuilder.and(
            FilterBuilder.equals("role", "admin"),
            FilterBuilder.greaterThan("age", "25")
        );
        
        List<User> result = userRepository.findAll().stream()
            .filter(u -> filter.matches(u.toMap()))
            .toList();
            
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getUsername());
    }
}
```

## 🤝 Contributing

1. **Fork** the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add some amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. Open a **Pull Request**

### Development Guidelines
- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Write **unit tests** for new features
- Update **documentation** when adding new features
- Keep the code **clean** and **readable**
- Use **meaningful** commit messages

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📚 Additional Resources

- [Visitor Pattern](https://refactoring.guru/design-patterns/visitor)
- [Java Collections Framework](https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html)
- [Java Functional Programming](https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html)
- [Effective Java](https://www.oreilly.com/library/view/effective-java/9780134686097/)

## 👥 Contributors

- [Your Name](https://github.com/yourusername)

## 🙏 Acknowledgments

- Thanks to all contributors who have helped improve this project
- Inspired by various filter implementations in popular Java frameworks

## 📚 Table of Contents
- [Core Concepts](#-core-concepts)
- [API Reference](#-api-reference)
  - [Filter Interface](#filter-interface)
  - [Logical Operators](#logical-operators)
    - [AndFilter](#andfilter)
    - [OrFilter](#orfilter)
    - [NotFilter](#notfilter)
    - [TrueFilter/FalseFilter](#truefilter--falsefilter)
  - [Comparison Operators](#comparison-operators)
    - [BaseComparisonFilter](#basecomparisonfilter)
    - [EqualsFilter](#equalsfilter)
    - [GreaterThanFilter/LessThanFilter](#greaterthanfilter--lessthanfilter)
    - [HasPropertyFilter](#haspropertyfilter)
    - [RegexFilter](#regexfilter)
  - [FilterBuilder](#filterbuilder)
  - [Visitor Pattern](#visitor-pattern)
- [Examples](#-examples)
- [Advanced Usage](#-advanced-usage)
- [Testing](#-testing)
- [Contributing](#-contributing)

## 🔍 Core Concepts

The Filter Framework is built around several key concepts:

1. **Filter Interface**: The foundation that all filters implement
2. **Logical Operators**: Combine multiple filters (AND, OR, NOT)
3. **Comparison Operators**: Perform specific comparisons on resource properties
4. **Visitor Pattern**: Enables operations on filters without modifying their classes
5. **Fluent API**: Easy construction of complex filter expressions

## 📖 API Reference

### Filter Interface

```java
public interface Filter {
    /**
     * Determines if the given resource matches this filter
     * @param resource The resource to check against the filter
     * @return true if the resource matches the filter, false otherwise
     */
    boolean matches(Map<String, String> resource);
    
    /**
     * Accepts a visitor for this filter (Visitor pattern)
     * @param visitor The visitor to accept
     * @param <T> The return type of the visitor
     * @return The result of the visitor's operation
     */
    <T> T accept(FilterVisitor<T> visitor);
}
```

### Logical Operators

#### AndFilter
Combines multiple filters with a logical AND operation.

```java
public record AndFilter(Filter... filters) implements Filter {
    @Override
    public boolean matches(Map<String, String> resource) {
        // All filters must match
        for (Filter filter : filters) {
            if (!filter.matches(resource)) {
                return false;
            }
        }
        return true;
    }
}
```

#### OrFilter
Combines multiple filters with a logical OR operation.

```java
public record OrFilter(Filter... filters) implements Filter {
    @Override
    public boolean matches(Map<String, String> resource) {
        // At least one filter must match
        for (Filter filter : filters) {
            if (filter.matches(resource)) {
                return true;
            }
        }
        return false;
    }
}
```

#### NotFilter
Inverts the result of another filter.

```java
public record NotFilter(Filter filter) implements Filter {
    @Override
    public boolean matches(Map<String, String> resource) {
        return !filter.matches(resource);
    }
}
```

#### TrueFilter / FalseFilter
Constant filters that always return true or false, respectively.

```java
public record TrueFilter() implements Filter {
    @Override
    public boolean matches(Map<String, String> resource) {
        return true;
    }
}
```

### Comparison Operators

#### BaseComparisonFilter
Abstract base class for all comparison filters.

```java
public abstract class BaseComparisonFilter implements Filter {
    protected final String key;

    protected BaseComparisonFilter(String key) {
        this.key = key;
    }

    protected String getValue(Map<String, String> resource) {
        return resource.get(key);
    }
}
```

#### EqualsFilter
Checks if a property equals a value (case-insensitive).

```java
public class EqualsFilter extends BaseComparisonFilter {
    private final String value;

    public EqualsFilter(String key, String value) {
        super(key);
        this.value = value;
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource);
        return actualValue != null && actualValue.equalsIgnoreCase(value);
    }
}
```

#### GreaterThanFilter / LessThanFilter
Compare numeric or string values.

```java
public class GreaterThanFilter extends BaseComparisonFilter {
    private final String value;

    public GreaterThanFilter(String key, String value) {
        super(key);
        this.value = value;
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource);
        if (actualValue == null) return false;
        
        // Try numeric comparison first
        try {
            double actual = Double.parseDouble(actualValue);
            double expected = Double.parseDouble(value);
            return actual > expected;
        } catch (NumberFormatException e) {
            // Fall back to string comparison
            return actualValue.compareTo(value) > 0;
        }
    }
}
```

#### HasPropertyFilter
Checks if a property exists in the resource.

```java
public class HasPropertyFilter extends BaseComparisonFilter {
    public HasPropertyFilter(String key) {
        super(key);
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        return resource.containsKey(key);
    }
}
```

#### RegexFilter
Checks if a property matches a regular expression.

```java
public class RegexFilter extends BaseComparisonFilter {
    private final Pattern pattern;
    private final String regex;

    public RegexFilter(String key, String regex) {
        super(key);
        this.regex = regex;
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public boolean matches(Map<String, String> resource) {
        String actualValue = getValue(resource);
        return actualValue != null && pattern.matcher(actualValue).matches();
    }
}
```

### FilterBuilder
A fluent builder for creating filter expressions.

```java
public class FilterBuilder {
    private FilterBuilder() {}

    /**
     * Creates an AND filter that matches when ALL sub-filters match
     * @param filters The filters to combine with AND
     * @return A new AndFilter instance
     */
    public static Filter and(Filter... filters) {
        return new AndFilter(filters);
    }
    
    // Other builder methods...
}
```

### Visitor Pattern

The visitor pattern is used to implement operations on filters without modifying their classes.

```java
public interface FilterVisitor<T> {
    T visit(AndFilter filter);
    T visit(OrFilter filter);
    T visit(NotFilter filter);
    // Other visit methods...
}

// Example implementation: Converts filter to string
public class ToStringVisitor implements FilterVisitor<String> {
    @Override
    public String visit(AndFilter filter) {
        return Arrays.stream(filter.filters())
                   .map(f -> f.accept(this))
                   .collect(Collectors.joining(" && ", "(", ")"));
    }
    // Other visit method implementations...
}
```

## 🚀 Examples

### Basic Usage

```java
// Simple equality check
Filter isAdmin = FilterBuilder.equals("role", "admin");

// Combining filters
Filter filter = FilterBuilder.and(
    FilterBuilder.greaterThan("age", "18"),
    FilterBuilder.or(
        FilterBuilder.equals("status", "active"),
        FilterBuilder.equals("status", "pending")
    )
);
```

### Using with Collections

```java
List<Map<String, String>> users = // ...
List<Map<String, String>> activeAdmins = users.stream()
    .filter(user -> FilterBuilder.and(
        FilterBuilder.equals("role", "admin"),
        FilterBuilder.equals("status", "active")
    ).matches(user))
    .toList();
```

### Custom Filter

```java
public class CustomFilter implements Filter {
    @Override
    public boolean matches(Map<String, String> resource) {
        // Custom matching logic
        return /* ... */;
    }
    
    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        // Handle visitor if needed, or throw UnsupportedOperationException
        throw new UnsupportedOperationException("Visitor not supported");
    }
}
```

## 🧪 Testing

Run tests with Maven:

```bash
mvn test
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

A flexible and type-safe filter framework for Java that allows building complex filter expressions programmatically. This framework is designed to create, combine, and apply filters to collections of resources in a fluent and readable way.

## ✨ Features

- **Type-safe** filter construction
- **Fluent API** for building complex filter expressions
- **Extensible** architecture for custom filters
- **Visitor pattern** implementation for filter traversal
- **Comprehensive test coverage**
- **Modern Java** (Java 17+ with records)

## 📦 Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>dev.xerohero</groupId>
    <artifactId>HandsOnCode2</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## 🚀 Quick Start

```java
import dev.xerohero.filter.FilterBuilder;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Create a list of users
        List<Map<String, String>> users = List.of(
            Map.of("name", "Alice", "age", "30", "role", "admin"),
            Map.of("name", "Bob", "age", "25", "role", "user"),
            Map.of("name", "Charlie", "age", "35", "role", "admin")
        );

        // Create a filter: role is 'admin' AND age > 25
        var filter = FilterBuilder.and(
            FilterBuilder.equals("role", "admin"),
            FilterBuilder.greaterThan("age", "25")
        );

        // Apply the filter
        List<Map<String, String>> adminsOver25 = users.stream()
            .filter(filter::matches)
            .toList();
            
        System.out.println("Admins over 25: " + adminsOver25);
    }
}
```

## 🏗️ Core Components

### Filter Interface

The foundation of the framework is the `Filter` interface with two key methods:

```java
public interface Filter {
    boolean matches(Map<String, String> resource);
    <T> T accept(FilterVisitor<T> visitor);
}
```

### Built-in Filters

#### Logical Operators
- `AndFilter`: Matches when ALL sub-filters match (logical AND)
- `OrFilter`: Matches when ANY sub-filter matches (logical OR)
- `NotFilter`: Inverts another filter (logical NOT)
- `TrueFilter`: Always matches
- `FalseFilter`: Never matches

#### Comparison Operators
- `EqualsFilter`: Case-insensitive equality check
- `GreaterThanFilter`: Numeric or string comparison
- `LessThanFilter`: Numeric or string comparison
- `HasPropertyFilter`: Checks if a property exists
- `RegexFilter`: Matches against a regular expression

### FilterBuilder

A fluent builder for creating filter expressions:

```java
// Simple filter
Filter filter1 = FilterBuilder.equals("status", "active");

// Complex filter
Filter filter2 = FilterBuilder.and(
    FilterBuilder.or(
        FilterBuilder.equals("role", "admin"),
        FilterBuilder.equals("role", "moderator")
    ),
    FilterBuilder.greaterThan("loginCount", "10")
);
```

## 🧩 Visitor Pattern

The framework uses the Visitor pattern to implement operations on filters without modifying their classes:

```java
// Example: Convert filter to string
ToStringVisitor visitor = new ToStringVisitor();
String filterString = filter.accept(visitor);
```

## 📚 Full Documentation

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

## 🧪 Testing

Run the tests with Maven:

```bash
mvn test
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
