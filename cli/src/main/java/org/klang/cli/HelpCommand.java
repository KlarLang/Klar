package org.klang.cli;

import picocli.CommandLine.Command;

@Command(name = "help", description = "Show the Klang help catalog")
public class HelpCommand implements Runnable {

    // ANSI
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";

    // Klang palette
    private static final String WINE = "\u001B[38;2;127;0;31m";
    private static final String GRAY_LIGHT = "\u001B[38;2;209;209;209m";
    // private static final String INFO_COLOR = "\u001B[38;2;100;149;237m";
    private static final String NOTE_COLOR = GRAY_LIGHT;

    @Override
    public void run() {

        printHelpBody();
    }

    private void printHelpBody() {
        // Header
        System.out.println();
        System.out.println(WINE + BOLD + "KLANG • Command Line Interface" + RESET);
        System.out.println();

        // Usage
        System.out.println(WINE + BOLD + "  Usage" + RESET);
        System.out.println("    kc <command> [options]");
        System.out.println();

        // Commands
        System.out.println(WINE + BOLD + "  Commands" + RESET);
        System.out.println("    " + BOLD + "lex" + RESET + "              Show tokens of a .k source file");
        System.out.println("    " + BOLD + "gen-completion" + RESET + "   Generate autocomplete script");
        System.out.println("    " + BOLD + "parse" + RESET + "   Parse file.k");
        System.out.println();

        // Options
        System.out.println(WINE + BOLD + "  Options" + RESET);
        System.out.println("    -h, --help        Show this help catalog");
        System.out.println("    -V, --version     Show Klang version");
        System.out.println();

        // Examples
        System.out.println(WINE + BOLD + "  Examples" + RESET);
        System.out.println("    kc lex file.k");
        System.out.println("    kc gen-completion bash");
        System.out.println();

        // Note
        System.out.println("  " + NOTE_COLOR + "note:" + RESET +
                " Use `kc <command> --help` for more information.");
    }

}
