package org.klang.cli;

import picocli.CommandLine.IVersionProvider;

public class KVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() {
        return new String[] {
                "Klang Language 0.1.3-dev",
                "CLI 0.1.2-dev",
                "Author: ~K'"
        };
    }
}
