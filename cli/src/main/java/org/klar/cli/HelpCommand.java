package org.klar.cli;

import org.klar.cli.error.diagnostic.KcDiagnosticColors;

import picocli.CommandLine.Command;

@Command(
    name = "help",
    description = "Show the Klar help catalog"
)
public class HelpCommand implements Runnable {
    private final StringBuilder str = new StringBuilder();

    @Override
    public void run() {
        printHelpBody();
    }

    private void printHelpBody() {
        final String NEW_LINE = "\n\n";
        str.setLength(0);

        str.append(KcDiagnosticColors.structure("KLAR - Command Line Interface"))
        .append(NEW_LINE);

        str.append("  ").append(KcDiagnosticColors.structure("Usage"))
        .append("\n");
        
        str.append("    ").append(KcDiagnosticColors.structure("kc ")).append(KcDiagnosticColors.neutral("<command> [options]"))
        .append(NEW_LINE);

        str.append("  ").append(KcDiagnosticColors.structure("Commands"))
        .append("\n");

        str.append("    ").append(KcDiagnosticColors.structure("lex")).append("              ").append(KcDiagnosticColors.neutral("Show tokens of a .k source file"))
        .append("\n");
        
        str.append("    ").append(KcDiagnosticColors.structure("parse")).append("            ").append(KcDiagnosticColors.neutral("Parse file.k"))
        .append("\n");
        
        str.append("    ").append(KcDiagnosticColors.structure("build")).append("            ").append(KcDiagnosticColors.neutral("Build Klar source to Java (for now)"))
        .append("\n");

        str.append("    ").append(KcDiagnosticColors.structure("run")).append("              ").append(KcDiagnosticColors.neutral("Transpile, compile and run a Klar program"))
        .append("\n");

        str.append("    ").append(KcDiagnosticColors.structure("gen-completion")).append("   ").append(KcDiagnosticColors.neutral("Generate autocomplete script"))
        .append(NEW_LINE);
        
        str.append("  ").append(KcDiagnosticColors.structure("Options"))
        .append("\n");
        
        str.append("    ").append(KcDiagnosticColors.structure("-h")).append(KcDiagnosticColors.separator(", ")).append(KcDiagnosticColors.structure("--help")).append("        ").append(KcDiagnosticColors.neutral("Show this help catalog"))
        .append("\n");
        str.append("    ").append(KcDiagnosticColors.structure("-V")).append(KcDiagnosticColors.separator(", ")).append(KcDiagnosticColors.structure("--version")).append("     ").append(KcDiagnosticColors.neutral("Show Klar version"))
        .append(NEW_LINE);
        
        str.append("  ").append(KcDiagnosticColors.structure("Examples"))
        .append("\n");

        str.append("    ").append(KcDiagnosticColors.structure("kc ")).append(KcDiagnosticColors.neutral("lex ")).append(KcDiagnosticColors.structure("file.k"))
        .append("\n");

        str.append("    ").append(KcDiagnosticColors.structure("kc ")).append(KcDiagnosticColors.neutral("parse ")).append(KcDiagnosticColors.structure("file.k"))
        .append("\n");

        str.append("    ").append(KcDiagnosticColors.structure("kc ")).append(KcDiagnosticColors.neutral("build ")).append(KcDiagnosticColors.structure("file.k"))
        .append("\n");

        str.append("    ").append(KcDiagnosticColors.structure("kc ")).append(KcDiagnosticColors.neutral("run ")).append(KcDiagnosticColors.structure("file.k"))
        .append("\n\n");

        str.append(KcDiagnosticColors.helpAccent("note:"))
        .append("\n");

        // str.append(KcDiagnosticColors.neutral(" Use `kc <command> --help` for more information."));

        System.out.println(str.toString());
        str.setLength(0);
    }
}
