package org.klang.cli;

import picocli.CommandLine.Command;

@Command(name = "help", description = "Show the Klang help catalog")
public class HelpCommand implements Runnable {

    // Reset e estilos
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";

    // Paleta Klang (branding)
    private static final String WINE = "\u001B[38;2;127;0;31m"; // #7F001F
    private static final String WINE_LIGHT = "\u001B[38;2;179;0;45m"; // #B3002D
    private static final String GRAY_LIGHT = "\u001B[38;2;209;209;209m"; // #D1D1D1
    private static final String GRAY_STEEL = "\u001B[38;2;46;46;46m"; // #2E2E2E

    // Semântica Klang
    private static final String INFO_COLOR = "\u001B[38;2;100;149;237m";
    private static final String NOTE_COLOR = GRAY_LIGHT;

    @Override
    public void run() {
        printHelp();
    }

    private void printHelp() {

        System.out.println(WINE + "      ___           ___       ___           ___           ___     \n" + //
                "     /\\__\\         /\\__\\     /\\  \\         /\\__\\         /\\  \\    \n" + //
                "    /:/  /        /:/  /    /::\\  \\       /::|  |       /::\\  \\   \n" + //
                "   /:/__/        /:/  /    /:/\\:\\  \\     /:|:|  |      /:/\\:\\  \\  \n" + //
                "  /::\\__\\____   /:/  /    /::\\~\\:\\  \\   /:/|:|  |__   /:/  \\:\\  \\ \n" + //
                " /:/\\:::::\\__\\ /:/__/    /:/\\:\\ \\:\\__\\ /:/ |:| /\\__\\ /:/__/_\\:\\__\\\n" + //
                " \\/_|:|~~|~    \\:\\  \\    \\/__\\:\\/:/  / \\/__|:|/:/  / \\:\\  /\\ \\/__/\n" + //
                "    |:|  |      \\:\\  \\        \\::/  /      |:/:/  /   \\:\\ \\:\\__\\  \n" + //
                "    |:|  |       \\:\\  \\       /:/  /       |::/  /     \\:\\/:/  /  \n" + //
                "    |:|  |        \\:\\__\\     /:/  /        /:/  /       \\::/  /   \n" + //
                "     \\|__|         \\/__/     \\/__/         \\/__/         \\/__/   \n" + RESET);

        // Header
        System.out.println(INFO_COLOR + "info:" + RESET + " Klang Command Line Interface");
        System.out.println("  " + GRAY_LIGHT + "--> kc --help" + RESET + "\n");

        // Usage
        System.out.println(WINE + BOLD + "Usage:" + RESET);
        System.out.println("  kc <command> [options]\n");

        // Commands
        System.out.println(WINE + BOLD + "Commands:" + RESET);
        System.out.println("  " + BOLD + "lex" + RESET + "               Show file tokens of a .k file");
        System.out.println("  " + BOLD + "gen-completion" + RESET + "    Generate autocomplete script for bash/zsh\n");

        // Options
        System.out.println(WINE + BOLD + "Options:" + RESET);
        System.out.println("  -h, --help        Show this help catalog");
        System.out.println("  -V, --version     Show Klang version\n");

        // Examples
        System.out.println(WINE + BOLD + "Examples:" + RESET);
        System.out.println("  kc lex test.k");
        System.out.println("  kc gen-completion bash\n");

        // Note
        System.out.println("  " + NOTE_COLOR + "> note:" + RESET
                + " Use `kc <command> --help` for command-specific information.");
    }
}
