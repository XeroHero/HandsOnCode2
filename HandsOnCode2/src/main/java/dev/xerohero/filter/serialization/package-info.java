/**
 * Provides JSON serialization and deserialization support for Filter objects.
 * 
 * <p>This package contains the necessary components to convert Filter objects to JSON format
 * and back, enabling easy storage, transmission, and reconstruction of filter expressions.</p>
 *
 * <h2>Main Components</h2>
 * <ul>
 *   <li>{@link dev.xerohero.filter.serialization.FilterSerialization} - Main utility class for serialization/deserialization</li>
 *   <li>{@link dev.xerohero.filter.serialization.FilterJsonSerializer} - Converts Filter objects to JSON</li>
 *   <li>{@link dev.xerohero.filter.serialization.FilterJsonDeserializer} - Converts JSON back to Filter objects</li>
 *   <li>{@link dev.xerohero.filter.serialization.FilterSerializationException} - Exception thrown for serialization/deserialization errors</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>
 * // Serialize a filter to JSON
 * Filter filter = ...; // your filter
 * String json = FilterSerialization.toJson(filter);
 * 
 * // Deserialize JSON back to a Filter
 * Filter deserializedFilter = FilterSerialization.fromJson(json);
 * </pre>
 *
 * <h2>JSON Format</h2>
 * <p>The serialized JSON format represents filters in a structured way, with each filter type
 * having a specific format. For example:</p>
 * <pre>
 * {
 *   "type": "and",
 *   "filters": [
 *     {
 *       "type": "equals",
 *       "key": "status",
 *       "value": "active"
 *     },
 *     {
 *       "type": "greaterThan",
 *       "key": "age",
 *       "value": "21"
 *     }
 *   ]
 * }
 * </pre>
 *
 * @see dev.xerohero.filter.Filter The base Filter interface
 * @see com.fasterxml.jackson.databind.ObjectMapper The underlying JSON processor
 */
package dev.xerohero.filter.serialization;
