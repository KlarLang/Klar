package org.klang.core.errors;

import org.klang.core.diagnostics.DiagnosticCode;
import org.klang.core.diagnostics.DiagnosticColors;

public class ParserException extends KException {
    private final String expected;

    public ParserException(
            DiagnosticCode code,
            SourceLocation location,
            String[] contextLines,
            String cause,
            String fix,
            String expected,
            String example,
            String note,
            int length) {
        super(code, location, contextLines, cause, fix, example, note, length);
        this.expected = expected;
      }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();

        // header
        sb.append(DiagnosticColors.structure("[K:"))
          .append(DiagnosticColors.errorCode(code.name()))
          .append(DiagnosticColors.structure("] "))
          .append(DiagnosticColors.errorName(code.name))
          .append("\n");
        
        sb.append(DiagnosticColors.structure("ERROR (")).append(code.phase.name()).append(")")
          .append("\n");
        
        sb.append(DiagnosticColors.structure("at "))
          .append(DiagnosticColors.neutral(location.file()))
          .append(DiagnosticColors.separator(":"))
          .append(DiagnosticColors.structure(String.valueOf(location.line())))
          .append(DiagnosticColors.separator(":"))
          .append(DiagnosticColors.structure(String.valueOf(location.column())))
          .append("\n\n");

        // context
        int errorLine = location.line();
        int maxLineDigitWidth = String.valueOf(errorLine).length();
        
        int firstLineInContext = errorLine - (contextLines.length - 1);

        for (int i = 0; i < contextLines.length; i++) {
            int currentLine = firstLineInContext + i;
            
            sb.append(DiagnosticColors.lineNumber(
              padLeft(currentLine, maxLineDigitWidth)
              ))
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
        
        // cause
        sb.append("\n");
        sb.append(DiagnosticColors.structure("Cause:"))
          .append("\n  ")
          .append(DiagnosticColors.neutral(cause))
          .append("\n");
        
        if (fix != null){
          sb.append("\n");
          sb.append(DiagnosticColors.structure("Fix:"))  
          .append("\n  ")
          .append(DiagnosticColors.neutral(fix))
          .append("\n");
        }

        if (expected != null){
          sb.append("\n");
          sb.append(DiagnosticColors.structure("Expected:"))
          .append("\n  '")
          .append(DiagnosticColors.neutral(expected))
          .append("'\n");
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

    /**
     * Retorna versão do erro sem cores (para logs, arquivos).
     */
    public String formatPlain() {
        DiagnosticColors.RenderMode original = DiagnosticColors.getMode();
        DiagnosticColors.setMode(DiagnosticColors.RenderMode.PLAIN);
        
        String result = format();
        
        DiagnosticColors.setMode(original);
        return result;
    }
}