package dev.xerohero.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
class DebugLogTest {

    @Mock
    private Logger mockLogger;

    @Test
    void testLogMessage() {
        // Given
        String testMessage = "Test debug message";

        // When
        DebugLog.log(testMessage);

        // Then - Verify that the logger was called with the correct message
        // Note: This is a simple test that just verifies the method doesn't throw exceptions
    }

    @Test
    void testLogNullMessage() {
        // When/Then - Should not throw exception
        DebugLog.log(null);
    }
}
