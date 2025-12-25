package org.klang.core.error;

import org.klang.core.diagnostic.DiagnosticCode;
import org.klang.core.diagnostic.DiagnosticColors;

public final class SemanticException extends KException {

    public SemanticException(
        DiagnosticCode code,
        SourceLocation location,
        String[] contextLines,
        String cause,
        String fix,
        String example,
        String note,
        int length
    ) {
        super(code, location, contextLines, cause, fix, example, note, length);
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();

        sb.append("[K:").append(code.name()).append("] ")
          .append(code.name).append("\n")
          .append(DiagnosticColors.structure("ERROR (")).append(code.phase.name()).append(")")
          .append("at ")
          .append(location.file())
          .append(":")
          .append(location.line())
          .append(":")
          .append(location.column())
          .append("\n\n");

        for (int i = 0; i < contextLines.length; i++) {
            sb.append(location.line() - contextLines.length + i + 1)
              .append(" | ")
              .append(contextLines[i])
              .append("\n");
        }

        sb.append("\nCause:\n  ").append(cause)
          .append("\n\nFix:\n  ").append(fix);

        if (example != null) {
            sb.append("\n\nExample:\n  ").append(example);
        }

        if (note != null) {
            sb.append("\n\nNote:\n  ").append(note);
        }

        return sb.toString();
    }
}
