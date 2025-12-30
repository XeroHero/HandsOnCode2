package dev.xerohero;

import java.io.PrintStream;

public class IO {
    private static PrintStream out = System.out;

    public static void println(String message) {
        out.println(message);
    }

    // For testing purposes - allows redirecting output
    static void setOutput(PrintStream printStream) {
        out = printStream;
    }

    // Reset to System.out
    static void resetOutput() {
        out = System.out;
    }
}
