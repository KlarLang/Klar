package org.klang.cli;

import picocli.CommandLine.IVersionProvider;

public class KVersionProvider implements IVersionProvider {

    private static final String RESET = "\u001B[0m";
    private static final String WINE = "\u001B[38;2;127;0;31m";
    private static final String GRAY = "\u001B[38;2;180;180;180m";

    @Override
    public String[] getVersion() {
        return new String[] {
                WINE + "KLANG Language " + GRAY + "0.1.4-dev" + RESET,
                WINE + "  ├─ CLI:          " + GRAY + "0.1.3-dev" + RESET,
                WINE + "  ├─ Backend:      " + GRAY + "JVM" + RESET,
                WINE + "  ├─ Build:        " + GRAY + "debug" + RESET,
                WINE + "  └─ Target:       " + GRAY + detectTarget() + RESET
        };
    }

    private String detectTarget() {
        return System.getProperty("os.name") + "-" + System.getProperty("os.arch");
    }
}
