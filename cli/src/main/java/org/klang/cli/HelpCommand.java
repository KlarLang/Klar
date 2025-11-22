package org.klang.cli;

import picocli.CommandLine.Command;

@Command(name = "help", description = "Show the Klang help catalog")
public class HelpCommand implements Runnable {

    @Override
    public void run() {
        printHelp();
    }

    private void printHelp() {
        System.out.println("info: Klang Command Line Interface");
        System.out.println("  --> kc --help\n");

        System.out.println("Usage:");
        System.out.println("  kc <command> [options]\n");

        System.out.println("Commands:");
        System.out.println("  lex               Show file tokens of a .k file");
        System.out.println("  gen-completion    Generate autocomplete script for bash/zsh\n");

        System.out.println("Options:");
        System.out.println("  -h, --help        Show this help catalog");
        System.out.println("  -V, --version     Show Klang version\n");

        System.out.println("Examples:");
        System.out.println("  kc lex test.k");
        System.out.println("  kc gen-completion bash\n");

        System.out.println("  > note: Use `kc <command> --help` for command-specific information.");
    }
}
