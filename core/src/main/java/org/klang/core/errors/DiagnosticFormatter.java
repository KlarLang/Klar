package org.klang.core.errors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Formata diagnósticos com cores e estilo para o terminal.
 * Inspirado em rustc, com as cores do Klang.
 */
public final class DiagnosticFormatter {

    // Cores ANSI
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";

    // Paleta Klang
    private static final String WINE = "\u001B[38;2;127;0;31m"; // #7F001F
    private static final String WINE_LIGHT = "\u001B[38;2;179;0;45m"; // #B3002D
    private static final String GRAY_LIGHT = "\u001B[38;2;209;209;209m"; // #D1D1D1
    private static final String GRAY_STEEL = "\u001B[38;2;46;46;46m"; // #2E2E2E

    // Cores semânticas
    private static final String ERROR_COLOR = WINE;
    private static final String WARNING_COLOR = "\u001B[38;2;255;165;0m"; // Laranja
    private static final String INFO_COLOR = "\u001B[38;2;100;149;237m"; // Azul
    private static final String NOTE_COLOR = GRAY_LIGHT;
    private static final String LINE_NUMBER_COLOR = GRAY_LIGHT;
    private static final String HIGHLIGHT_COLOR = WINE_LIGHT;

    private final boolean useColors;

    public DiagnosticFormatter(boolean useColors) {
        this.useColors = useColors;
    }

    public DiagnosticFormatter() {
        this(shouldUseColors());
    }

    private static boolean shouldUseColors() {
        String term = System.getenv("TERM");
        String noColor = System.getenv("NO_COLOR");
        return noColor == null && term != null && !term.equals("dumb");
    }

    public String format(Diagnostic diagnostic) {
        StringBuilder sb = new StringBuilder();

        // Header: error: mensagem
        sb.append(formatHeader(diagnostic));
        sb.append("\n");

        // Location: --> file:line:col
        sb.append(formatLocation(diagnostic.primarySpan));
        sb.append("\n");

        // Source code com highlight
        try {
            sb.append(formatSourceCode(diagnostic.primarySpan));
        } catch (IOException e) {
            // Se não conseguir ler o arquivo, continua sem o código
        }

        // Secondary spans
        for (Span span : diagnostic.secondarySpans()) {
            sb.append("\n");
            sb.append(formatLocation(span));
            sb.append("\n");
            try {
                sb.append(formatSourceCode(span));
            } catch (IOException e) {
                // Ignora
            }
        }

        // Notes
        for (Note note : diagnostic.notes()) {
            sb.append("\n");
            sb.append(formatNote(note));
        }

        return sb.toString();
    }

    private String formatHeader(Diagnostic diagnostic) {
        String label = getTypeLabel(diagnostic.type);
        String color = getTypeColor(diagnostic.type);

        return color(color, BOLD + label + RESET) +
                color(BOLD, ": " + diagnostic.message);
    }

    private String formatLocation(Span span) {
        return color(BOLD, "  --> ") +
                color(GRAY_LIGHT, span.toString());
    }

    private String formatSourceCode(Span span) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(span.fileName));
        StringBuilder sb = new StringBuilder();

        int lineNumWidth = String.valueOf(span.endLine).length();

        // Linha antes (contexto)
        if (span.startLine > 1) {
            sb.append(formatCodeLine(span.startLine - 1,
                    lines.get(span.startLine - 2),
                    lineNumWidth, false));
        }

        // Linhas com erro
        for (int line = span.startLine; line <= span.endLine; line++) {
            String code = lines.get(line - 1);
            sb.append(formatCodeLine(line, code, lineNumWidth, true));

            // Highlight underline
            if (span.isSingleLine()) {
                sb.append(formatHighlight(span, lineNumWidth));
            }
        }

        // Linha depois (contexto)
        if (span.endLine < lines.size()) {
            sb.append(formatCodeLine(span.endColumn + 1,
                    lines.get(span.endLine),
                    lineNumWidth, false));
        }

        return sb.toString();
    }

    private String formatCodeLine(int lineNum, String code, int width, boolean highlight) {
        String lineNumStr = String.format("%" + width + "d", lineNum);
        String separator = highlight ? " | " : " │ ";

        return color(LINE_NUMBER_COLOR, lineNumStr) +
                color(GRAY_STEEL, separator) +
                code + "\n";
    }

    private String formatHighlight(Span span, int lineNumWidth) {
        StringBuilder sb = new StringBuilder();

        // Padding para alinhar com o código
        sb.append(" ".repeat(lineNumWidth));
        sb.append(color(GRAY_STEEL, " | "));

        // Espaços até a coluna de início
        sb.append(" ".repeat(span.startColumn - 1));

        // Underline (^^^)
        int length = span.endColumn - span.startColumn + 1;
        String underline = "^".repeat(Math.max(1, length));
        sb.append(color(HIGHLIGHT_COLOR, underline));

        sb.append("\n");
        return sb.toString();
    }

    private String formatNote(Note note) {
        StringBuilder sb = new StringBuilder();
        sb.append(color(NOTE_COLOR, BOLD + "  > note: " + RESET));
        sb.append(note.message);

        if (note.hasSpan()) {
            sb.append("\n");
            sb.append(formatLocation(note.span));
            try {
                sb.append("\n");
                sb.append(formatSourceCode(note.span));
            } catch (IOException e) {
                // Ignora
            }
        }

        return sb.toString();
    }

    private String getTypeLabel(DiagnosticType type) {
        switch (type) {
            case LEXICAL:
            case SYNTAX:
            case SEMANTIC:
                return "error";
            case WARNING:
                return "warning";
            case INFO:
                return "info";
            default:
                return "diagnostic";
        }
    }

    private String getTypeColor(DiagnosticType type) {
        switch (type) {
            case LEXICAL:
            case SYNTAX:
            case SEMANTIC:
                return ERROR_COLOR;
            case WARNING:
                return WARNING_COLOR;
            case INFO:
                return INFO_COLOR;
            default:
                return GRAY_LIGHT;
        }
    }

    private String color(String code, String text) {
        if (!useColors) {
            return text;
        }
        return code + text + RESET;
    }

    // Método auxiliar para imprimir no stderr
    public void print(Diagnostic diagnostic) {
        System.err.println(format(diagnostic));
    }
}