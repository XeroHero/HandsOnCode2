package dev.xerohero.filter.visitor;

import dev.xerohero.filter.operators.*;
import dev.xerohero.filter.operators.comparison.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToStringVisitorTest {

    private final ToStringVisitor visitor = new ToStringVisitor();

    private static Stream<Arguments> provideEqualsTestCases() {
        return Stream.of(Arguments.of("name", "test", "name == test"), Arguments.of("age", "25", "age == 25"), Arguments.of("isActive", "true", "isActive == true"), Arguments.of("price", "19.99", "price == 19.99"), Arguments.of("id", null, "id == null"));
    }

    @Test
    void testVisitTrueFilter() {
        String result = TrueFilter.INSTANCE.accept(visitor);
        assertEquals("true", result);
    }

    @Test
    void testVisitFalseFilter() {
        String result = FalseFilter.INSTANCE.accept(visitor);
        assertEquals("false", result);
    }

    @Test
    void testVisitNotFilter() {
        String result = new NotFilter(TrueFilter.INSTANCE).accept(visitor);
        assertEquals("!true", result);

        // Test nested NOT
        String nestedNot = new NotFilter(new NotFilter(TrueFilter.INSTANCE)).accept(visitor);
        assertEquals("!!true", nestedNot);
    }

    @Test
    void testVisitAndFilter() {
        // Single filter
        String singleAnd = new AndFilter(TrueFilter.INSTANCE).accept(visitor);
        assertEquals("(true)", singleAnd);

        // Multiple filters
        String multiAnd = new AndFilter(new EqualsFilter("name", "test"), new GreaterThanFilter("age", "25")).accept(visitor);
        assertEquals("(name == test && age > 25)", multiAnd);

        // Nested AND
        String nestedAnd = new AndFilter(new AndFilter(TrueFilter.INSTANCE, FalseFilter.INSTANCE), new AndFilter(TrueFilter.INSTANCE)).accept(visitor);
        assertEquals("((true && false) && (true))", nestedAnd);
    }

    @Test
    void testVisitOrFilter() {
        // Single filter
        String singleOr = new OrFilter(TrueFilter.INSTANCE).accept(visitor);
        assertEquals("(true)", singleOr);

        // Multiple filters
        String multiOr = new OrFilter(new EqualsFilter("status", "active"), new LessThanFilter("loginAttempts", "3")).accept(visitor);
        assertEquals("(status == active || loginAttempts < 3)", multiOr);

        // Nested OR
        String nestedOr = new OrFilter(new OrFilter(TrueFilter.INSTANCE, FalseFilter.INSTANCE), new OrFilter(TrueFilter.INSTANCE)).accept(visitor);
        assertEquals("((true || false) || (true))", nestedOr);
    }

    @ParameterizedTest
    @ValueSource(strings = {"username", "email", "user.name"})
    void testVisitHasPropertyFilter(String property) {
        String result = new HasPropertyFiltre(property).accept(visitor);
        assertEquals("exists(" + property + ")", result);
    }

    @ParameterizedTest
    @MethodSource("provideEqualsTestCases")
    void testVisitEqualsFilter(String key, String value, String expected) {
        String result = new EqualsFilter(key, value).accept(visitor);
        assertEquals(expected, result);
    }

    @Test
    void testVisitLessThanFilter() {
        String result = new LessThanFilter("age", "18").accept(visitor);
        assertEquals("age < 18", result);

        // Test with different number types
        String doubleResult = new LessThanFilter("price", "9.99").accept(visitor);
        assertEquals("price < 9.99", doubleResult);
    }

    @Test
    void testVisitGreaterThanFilter() {
        String result = new GreaterThanFilter("score", "100").accept(visitor);
        assertEquals("score > 100", result);

        // Test with different number types
        String floatResult = new GreaterThanFilter("rating", "4.5").accept(visitor);
        assertEquals("rating > 4.5", floatResult);
    }

    @Test
    void testVisitRegexFilter() {
        String pattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        String result = new RegexFilter("email", pattern).accept(visitor);
        assertEquals("email matches " + pattern, result);
    }

    @Test
    void testComplexNestedExpression() {
        // Test a complex expression with multiple nested operations
        AndFilter complexFilter = new AndFilter(new OrFilter(new EqualsFilter("role", "admin"), new AndFilter(new EqualsFilter("status", "active"), new GreaterThanFilter("loginCount", "10"))), new NotFilter(new OrFilter(new EqualsFilter("banned", "true"), new LessThanFilter("age", "18"))));

        String expected = "((role == admin || (status == active && loginCount > 10)) && !(banned == true || age < 18))";
        String result = complexFilter.accept(visitor);
        assertEquals(expected, result);
    }
}
