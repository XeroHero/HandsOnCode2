package dev.xerohero.filter.serialization;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.FilterBuilder;
import dev.xerohero.filter.operators.AndFilter;
import dev.xerohero.filter.operators.NotFilter;
import dev.xerohero.filter.operators.OrFilter;
import dev.xerohero.filter.operators.comparison.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FilterSerializationTest {
    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_INACTIVE = "inactive";
    private static final String STATUS_PENDING = "pending";
    private static final String EMAIL_KEY = "email";
    private static final String STATUS_KEY = "status";
    private static final String ROLE_KEY = "role";
    private static final String ADMIN_ROLE = "admin";
    private static final String SUPERUSER_ROLE = "superuser";
    private static final String EXAMPLE_EMAIL_PATTERN = ".*@example\\.com$";
    private static final String TEST_EMAIL = "test@example.com";

    @Nested
    class BasicFilterTests {
        @Test
        void should_SerializeAndDeserialize_When_FilterIsEquals() throws Exception {
            // Given
            Filter original = new EqualsFilter(STATUS_KEY, STATUS_ACTIVE);

            // When
            String json = FilterSerialization.toJson(original);
            Filter deserialized = FilterSerialization.fromJson(json);

            // Then
            assertInstanceOf(EqualsFilter.class, deserialized);
            EqualsFilter eqFilter = (EqualsFilter) deserialized;
            assertEquals(STATUS_KEY, eqFilter.getKey());
            assertEquals(STATUS_ACTIVE, eqFilter.getValue());
        }

        @Test
        void should_SerializeAndDeserialize_When_FilterIsRegex() throws Exception {
            // Given
            Filter original = new RegexFilter(EMAIL_KEY, EXAMPLE_EMAIL_PATTERN);

            // When
            String json = FilterSerialization.toJson(original);
            Filter deserialized = FilterSerialization.fromJson(json);

            // Then
            assertInstanceOf(RegexFilter.class, deserialized);
            RegexFilter regexFilter = (RegexFilter) deserialized;
            assertEquals(EMAIL_KEY, regexFilter.getKey());
            assertTrue(regexFilter.matches(Map.of(EMAIL_KEY, TEST_EMAIL)));
        }
    }

    @Nested
    class CompositeFilterTests {
        @Test
        void should_SerializeAndDeserialize_When_FilterIsAnd() throws Exception {
            // Given
            Filter original = new AndFilter(
                    new EqualsFilter(STATUS_KEY, STATUS_ACTIVE),
                    new GreaterThanFilter("age", "18")
            );

            // When
            String json = FilterSerialization.toJson(original);
            Filter deserialized = FilterSerialization.fromJson(json);

            // Then
            assertInstanceOf(AndFilter.class, deserialized);
            AndFilter andFilter = (AndFilter) deserialized;
            assertEquals(2, andFilter.filters().length);
        }

        @Test
        void should_SerializeAndDeserialize_When_FilterIsOr() throws Exception {
            // Given
            Filter original = new OrFilter(
                    new EqualsFilter(ROLE_KEY, ADMIN_ROLE),
                    new EqualsFilter(ROLE_KEY, SUPERUSER_ROLE)
            );

            // When
            String json = FilterSerialization.toJson(original);
            Filter deserialized = FilterSerialization.fromJson(json);

            // Then
            assertInstanceOf(OrFilter.class, deserialized);
            OrFilter orFilter = (OrFilter) deserialized;
            assertEquals(2, orFilter.filters().length);
        }

        @Test
        void should_SerializeAndDeserialize_When_FilterIsNot() throws Exception {
            // Given
            Filter original = new NotFilter(new EqualsFilter(STATUS_KEY, STATUS_INACTIVE));

            // When
            String json = FilterSerialization.toJson(original);
            Filter deserialized = FilterSerialization.fromJson(json);

            // Then
            assertInstanceOf(NotFilter.class, deserialized);
            NotFilter notFilter = (NotFilter) deserialized;
            assertInstanceOf(EqualsFilter.class, notFilter.filter());
        }
    }

    @Nested
    class ComplexFilterTests {
        @Test
        void should_MaintainStructure_When_SerializingComplexFilter() throws Exception {
            // Given
            Filter original = createComplexFilter();

            // When
            String json = FilterSerialization.toJson(original);
            Filter deserialized = FilterSerialization.fromJson(json);

            // Then
            assertInstanceOf(AndFilter.class, deserialized);
            AndFilter andFilter = (AndFilter) deserialized;
            assertEquals(3, andFilter.filters().length);

            // Verify the first filter (OR)
            assertInstanceOf(OrFilter.class, andFilter.filters()[0]);
            OrFilter orFilter = (OrFilter) andFilter.filters()[0];
            assertEquals(2, orFilter.filters().length);

            // Verify the second filter (NOT)
            assertInstanceOf(NotFilter.class, andFilter.filters()[1]);
            NotFilter notFilter = (NotFilter) andFilter.filters()[1];
            assertInstanceOf(OrFilter.class, notFilter.filter());

            // Verify the third filter (Regex)
            assertInstanceOf(RegexFilter.class, andFilter.filters()[2]);
            RegexFilter regexFilter = (RegexFilter) andFilter.filters()[2];
            assertEquals(EMAIL_KEY, regexFilter.getKey());
        }

        private Filter createComplexFilter() {
            return new AndFilter(
                    FilterBuilder.orFilter(
                            new EqualsFilter(STATUS_KEY, STATUS_ACTIVE),
                            new EqualsFilter(STATUS_KEY, STATUS_PENDING)
                    ),
                    new NotFilter(
                            FilterBuilder.orFilter(
                                    new EqualsFilter("banned", "true"),
                                    new LessThanFilter("age", "18")
                            )
                    ),
                    new RegexFilter(EMAIL_KEY, EXAMPLE_EMAIL_PATTERN)
            );
        }
    }

    @Nested
    class ValidationTests {
        @ParameterizedTest
        @ValueSource(strings = {
                "invalid json",
                "{}",
                "{\"type\":\"unknown\"}",
                "{\"type\":\"equals\"}",
                "{\"type\":\"equals\", \"key\":null}",
                "{\"type\":\"regex\"}",
                "{\"type\":\"regex\", \"key\":\"email\"}",
                "{\"type\":\"and\"}",
                "{\"type\":\"and\", \"filters\":null}",
                "{\"type\":\"and\", \"filters\":[{}]}",
                "{\"type\":\"not\"}",
                "{\"type\":\"not\", \"filter\":null}",
                "{\"type\":\"not\", \"filter\":{}}"
        })
        void should_ThrowException_When_JsonIsInvalid(String invalidJson) {
            assertThrows(FilterSerializationException.class, () -> {
                FilterSerialization.fromJson(invalidJson);
            });
        }

        @Test
        void should_ThrowException_When_FilterIsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                FilterSerialization.toJson(null);
            });
        }

        @Test
        void should_ThrowException_When_JsonIsNullOrBlank() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> FilterSerialization.fromJson(null)),
                    () -> assertThrows(IllegalArgumentException.class, () -> FilterSerialization.fromJson("")),
                    () -> assertThrows(IllegalArgumentException.class, () -> FilterSerialization.fromJson("   "))
            );
        }
    }

    @Test
    void testSerializeDeserialize_EqualsFilter() throws Exception {
        // Given
        Filter original = new EqualsFilter("status", "active");

        // When
        String json = FilterSerialization.toJson(original);
        Filter deserialized = FilterSerialization.fromJson(json);

        // Then
        assertTrue(deserialized instanceof EqualsFilter);
        EqualsFilter eqFilter = (EqualsFilter) deserialized;
        assertEquals("status", eqFilter.getKey());
        assertEquals("active", eqFilter.getValue());
    }

    @Test
    void testSerializeDeserialize_RegexFilter() throws Exception {
        // Given
        Filter original = new RegexFilter("email", ".*@example\\.com$");

        // When
        String json = FilterSerialization.toJson(original);
        Filter deserialized = FilterSerialization.fromJson(json);

        // Then
        assertTrue(deserialized instanceof RegexFilter);
        RegexFilter regexFilter = (RegexFilter) deserialized;
        assertEquals("email", regexFilter.getKey());
        assertTrue(regexFilter.matches(Map.of("email", "test@example.com")));
    }

    @Test
    void testSerializeDeserialize_AndFilter() throws Exception {
        // Given
        Filter original = new AndFilter(
                new EqualsFilter("status", "active"),
                new GreaterThanFilter("age", "18")
        );

        // When
        String json = FilterSerialization.toJson(original);
        Filter deserialized = FilterSerialization.fromJson(json);

        // Then
        assertTrue(deserialized instanceof AndFilter);
        AndFilter andFilter = (AndFilter) deserialized;
        assertEquals(2, andFilter.filters().length);
    }

    @Test
    void testSerializeDeserialize_OrFilter() throws Exception {
        // Given
        Filter original = new OrFilter(
                new EqualsFilter("role", "admin"),
                new EqualsFilter("role", "superuser")
        );

        // When
        String json = FilterSerialization.toJson(original);
        Filter deserialized = FilterSerialization.fromJson(json);

        // Then
        assertTrue(deserialized instanceof OrFilter);
        OrFilter orFilter = (OrFilter) deserialized;
        assertEquals(2, orFilter.filters().length);
    }

    @Test
    void testSerializeDeserialize_NotFilter() throws Exception {
        // Given
        Filter original = new NotFilter(new EqualsFilter("status", "inactive"));

        // When
        String json = FilterSerialization.toJson(original);
        Filter deserialized = FilterSerialization.fromJson(json);

        // Then
        assertTrue(deserialized instanceof NotFilter);
        NotFilter notFilter = (NotFilter) deserialized;
        assertTrue(notFilter.filter() instanceof EqualsFilter);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid json",
            "{}",
            "{\"type\":\"unknown\"}",
            "{\"type\":\"equals\"}",
            "{\"type\":\"equals\", \"key\":null}",
            "{\"type\":\"regex\"}",
            "{\"type\":\"regex\", \"key\":\"email\"}",
            "{\"type\":\"and\"}",
            "{\"type\":\"and\", \"filters\":null}",
            "{\"type\":\"and\", \"filters\":[{}]}",
            "{\"type\":\"not\"}",
            "{\"type\":\"not\", \"filter\":null}",
            "{\"type\":\"not\", \"filter\":{}}"
    })
    void testDeserialize_InvalidJson_ThrowsException(String invalidJson) {
        assertThrows(FilterSerializationException.class, () -> {
            FilterSerialization.fromJson(invalidJson);
        });
    }

    @Test
    void testToJson_NullFilter_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            FilterSerialization.toJson(null);
        });
    }

    @Test
    void testFromJson_NullOrEmptyJson_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            FilterSerialization.fromJson(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            FilterSerialization.fromJson("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            FilterSerialization.fromJson("   ");
        });
    }

    @Test
    void testFilterSerialization_RoundTrip() throws Exception {
        // Create a complex filter
        Filter original = new AndFilter(
                new OrFilter(
                        new EqualsFilter("status", "active"),
                        new EqualsFilter("status", "pending")
                ),
                new NotFilter(
                        new OrFilter(
                                new EqualsFilter("banned", "true"),
                                new LessThanFilter("age", "18")
                        )
                ),
                new RegexFilter("email", ".*@example\\.com$")
        );

        // Serialize and deserialize
        String json = FilterSerialization.toJson(original);
        Filter deserialized = FilterSerialization.fromJson(json);

        // Verify the structure
        assertTrue(deserialized instanceof AndFilter);
        AndFilter andFilter = (AndFilter) deserialized;
        assertEquals(3, andFilter.filters().length);

        // Verify the first filter (OR)
        assertTrue(andFilter.filters()[0] instanceof OrFilter);
        OrFilter orFilter = (OrFilter) andFilter.filters()[0];
        assertEquals(2, orFilter.filters().length);

        // Verify the second filter (NOT)
        assertTrue(andFilter.filters()[1] instanceof NotFilter);
        NotFilter notFilter = (NotFilter) andFilter.filters()[1];
        assertTrue(notFilter.filter() instanceof OrFilter);

        // Verify the third filter (Regex)
        assertTrue(andFilter.filters()[2] instanceof RegexFilter);
        RegexFilter regexFilter = (RegexFilter) andFilter.filters()[2];
        assertEquals("email", regexFilter.getKey());
    }
}