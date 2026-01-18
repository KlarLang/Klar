package org.klar.core.errors;

import org.klar.core.diagnostics.DiagnosticCode;
import org.klar.core.diagnostics.DiagnosticColors;

/**
 * Exception thrown during the Semantic Analysis phase.
 * <p>
 * Provides rich, colored formatting for terminal output.
 * </p>
 */
public final class SemanticException extends KException {

    public SemanticException(
            DiagnosticCode code,
            SourceLocation location,
            String[] contextLines,
            String cause,
            String fix,
            String example,
            String note,
            int length) {
        super(code, location, contextLines, cause, fix, example, note, length);
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append(DiagnosticColors.structure("[K:"))
                .append(DiagnosticColors.errorCode(code.name()))
                .append(DiagnosticColors.structure("] "))
                .append(DiagnosticColors.errorName(code.name))
                .append("\n");

        // Phase & Location
        sb.append(DiagnosticColors.structure("ERROR ("))
                .append(code.phase.name())
                .append(")\n");

        sb.append(DiagnosticColors.structure("at "))
                .append(DiagnosticColors.neutral(location.file()))
                .append(DiagnosticColors.separator(":"))
                .append(DiagnosticColors.structure(String.valueOf(location.line())))
                .append(DiagnosticColors.separator(":"))
                .append(DiagnosticColors.structure(String.valueOf(location.column())))
                .append("\n\n");

        // Context Lines
        int errorLine = location.line();
        int maxLineDigitWidth = String.valueOf(errorLine).length();
        int firstLineInContext = errorLine - (contextLines.length - 1);

        for (int i = 0; i < contextLines.length; i++) {
            int currentLine = firstLineInContext + i;

            sb.append(DiagnosticColors.lineNumber(padLeft_(currentLine, maxLineDigitWidth)))
                    .append(DiagnosticColors.separator(" | "))
                    .append(DiagnosticColors.neutral(contextLines[i]))
                    .append("\n");

            if (currentLine == errorLine) {
                sb.append(" ".repeat(maxLineDigitWidth))
                        .append(DiagnosticColors.separator(" | "))
                        .append(" ".repeat(Math.max(0, location.column())))
                        .append(DiagnosticColors.error("^"));
                sb.append("\n");
            }
        }

        // Details
        sb.append("\n");
        sb.append(DiagnosticColors.structure("Cause:"))
                .append("\n  ")
                .append(DiagnosticColors.neutral(cause))
                .append("\n");

        if (fix != null) {

            sb.append("\n");
            sb.append(DiagnosticColors.structure("Fix:"))
                    .append("\n  ")
                    .append(DiagnosticColors.neutral(fix))
                    .append("\n");
        }

        if (example != null) {
            sb.append("\n");
            sb.append(DiagnosticColors.structure("Example:"))
                    .append("\n  ")
                    .append(DiagnosticColors.neutral(example))
                    .append("\n");
        }

        if (note != null) {
            sb.append("\n");
            sb.append(DiagnosticColors.structure("Note:"))
                    .append("\n  ")
                    .append(DiagnosticColors.neutral(note))
                    .append("\n");
        }

        return sb.toString();
    }

    public String formatPlain() {
        DiagnosticColors.RenderMode original = DiagnosticColors.getMode();
        DiagnosticColors.setMode(DiagnosticColors.RenderMode.PLAIN);
        String result = format();
        DiagnosticColors.setMode(original);
        return result;
    }

    private String padLeft_(int value, int width) {
        String s = String.valueOf(value);
        return " ".repeat(Math.max(0, width - s.length())) + s;
    }
}