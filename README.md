# HandsOnCode Assignment 2
#### A filter system that uses the Visitor pattern to process and convert filter expressions into string representations

### How it works

Based on the 'Visitor' design pattern: the filter runs on the whole data structure, 'visiting' each element as it progresses the task.

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
