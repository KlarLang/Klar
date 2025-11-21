package org.klang.cli;

import picocli.CommandLine.Command;

@Command(name = "--version", description = "Show Klang version")
public class VersionCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Klang version 0.1.2-dev");
    }
}
