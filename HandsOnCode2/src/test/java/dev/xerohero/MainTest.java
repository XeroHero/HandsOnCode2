package dev.xerohero;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        // Capture console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        IO.setOutput(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        // Restore original System.out
        IO.resetOutput();
    }

    @Test
    void testMainPrintsWelcomeMessage() {
        // Act
        Main.main();

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Hello and welcome!"),
                "Expected welcome message 'Hello and welcome!' to be printed");
    }

    @Test
    void testMainPrintsSequentialNumbers() {
        // Act
        Main.main();

        // Assert
        String output = outputStream.toString();
        String[] lines = output.split(System.lineSeparator());

        // Check that we have at least 6 lines (1 welcome + 5 numbers)
        assertTrue(lines.length >= 6,
                "Expected at least 6 lines of output");

        // Verify sequential output from i = 1 to i = 5
        for (int i = 1; i <= 5; i++) {
            String expectedLine = "i = " + i;
            assertTrue(output.contains(expectedLine),
                    String.format("Expected output to contain '%s'", expectedLine));
        }

        // Verify order: find positions of each "i = " line
        int pos1 = output.indexOf("i = 1");
        int pos2 = output.indexOf("i = 2");
        int pos3 = output.indexOf("i = 3");
        int pos4 = output.indexOf("i = 4");
        int pos5 = output.indexOf("i = 5");

        assertTrue(pos1 < pos2 && pos2 < pos3 && pos3 < pos4 && pos4 < pos5,
                "Expected sequential output to be in order: i = 1, i = 2, i = 3, i = 4, i = 5");
    }

    @Test
    void testMainUsesIOPrintln() {
        // This test verifies that IO.println is being used by checking
        // that output is captured through our IO class redirection
        
        // Act
        Main.main();

        // Assert
        String output = outputStream.toString();
        
        // If IO.println is used, we should capture the output
        assertFalse(output.isEmpty(),
                "Expected output to be captured through IO.println");
        
        // Verify both the welcome message and loop outputs are captured
        assertTrue(output.contains("Hello and welcome!"),
                "Expected IO.println to be used for welcome message");
        assertTrue(output.contains("i = 1"),
                "Expected IO.println to be used for loop output");
        
        // Count the number of lines - should be exactly 6
        String[] lines = output.split(System.lineSeparator());
        long nonEmptyLines = java.util.Arrays.stream(lines)
                .filter(line -> !line.trim().isEmpty())
                .count();
        
        assertEquals(6, nonEmptyLines,
                "Expected exactly 6 lines of output (1 welcome + 5 numbered lines) through IO.println");
    }
}
