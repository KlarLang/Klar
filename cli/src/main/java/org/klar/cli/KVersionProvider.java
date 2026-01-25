package org.klar.cli;

import org.klar.cli.error.diagnostic.KcDiagnosticColors;

import picocli.CommandLine.IVersionProvider;

public class KVersionProvider implements IVersionProvider {
    private static final String VERSION = "0.14.1";
    private static final StringBuilder str = new StringBuilder();

    @Override
    public String[] getVersion() {
        return new String[] {
                "",
                formatHeader(),
                "",
                formatEntry("Backend", "JVM"),
                formatEntry("Build", "debug"),
                formatEntry("Target", detectTarget()),
                ""
        };
    }

    private static String formatHeader() {
        str.setLength(0);

        str.append(KcDiagnosticColors.structure("KLAR")).append(" ").append(KcDiagnosticColors.separator("-"))
                .append(" ").append(KcDiagnosticColors.neutral(VERSION + "-dev"));
        return str.toString();
    }

    private static String formatEntry(String label, String value) {
        str.setLength(0);
        str.append("  ").append(KcDiagnosticColors.structure(label)).append(padRight(label, 12))
                .append(KcDiagnosticColors.neutral(value));

        return str.toString();
    }

    private static String detectTarget() {
        return System.getProperty("os.name") + "-" +
                System.getProperty("os.arch");
    }

    private static String padRight(String text, int total) {
        int missing = total - text.length();
        return " ".repeat(Math.max(1, missing));
    }
}
