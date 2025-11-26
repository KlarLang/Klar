package org.klang.cli;

import picocli.CommandLine.IVersionProvider;

public class KVersionProvider implements IVersionProvider {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";

    private static final String WINE = "\u001B[38;2;127;0;31m";
    private static final String GRAY = "\u001B[38;2;180;180;180m";

    @Override
    public String[] getVersion() {
        return new String[] {
                "",
                formatHeader(),
                "",
                formatEntry("CLI", "0.1.6-dev"),
                formatEntry("Backend", "JVM"),
                formatEntry("Build", "debug"),
                formatEntry("Target", detectTarget()),
                ""
        };
    }

    private String formatHeader() {
        return WINE + BOLD + "KLANG" + RESET +
                " " + "• 0.1.8-dev" + RESET;
    }

    private String formatEntry(String label, String value) {
        return "  " + label +
                padRight(label, 12) +
                RESET + GRAY + value + RESET;
    }

    private String detectTarget() {
        return System.getProperty("os.name") + "-" + System.getProperty("os.arch");
    }

    private String padRight(String text, int total) {
        int missing = total - text.length();
        return " ".repeat(Math.max(1, missing));
    }
}
