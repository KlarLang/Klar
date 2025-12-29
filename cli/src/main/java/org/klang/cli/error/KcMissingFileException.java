package org.klang.cli.error;

import org.klang.cli.error.diagnostic.KcDiagnosticCode;
import org.klang.cli.error.diagnostic.KcDiagnosticColors;
import org.klang.core.diagnostics.DiagnosticColors;

public class KcMissingFileException extends KcCliException {
    public KcMissingFileException(KcDiagnosticCode code, String command, String fix){
        super(code, command, fix);
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();

        sb.append(KcDiagnosticColors.structure("[K:")).append(KcDiagnosticColors.cliErrorCode(code.name())).append(KcDiagnosticColors.structure("] "))
          .append(KcDiagnosticColors.neutral(code.name)).append("\n")
          .append(KcDiagnosticColors.structure("ERROR (" + code.phase.name() + (")\n")))
          .append(KcDiagnosticColors.structure("at") + " " + KcDiagnosticColors.neutral("command usage\n\n"))
          .append(KcDiagnosticColors.structure("The command '")).append(KcDiagnosticColors.cliError(this.command)).append(KcDiagnosticColors.structure("' requires a source file."))
          .append("\n");
        
        sb.append(KcDiagnosticColors.structure("\nExpected:"));
        sb.append("\n  ").append(KcDiagnosticColors.structure("kc")).append(" ").append(KcDiagnosticColors.neutral("lex")).append("<FILE>");

        sb.append(KcDiagnosticColors.structure("\n\nExample:\n  "))
        .append("  ").append(KcDiagnosticColors.structure("kc")).append(" ").append(KcDiagnosticColors.neutral("lex")).append(" ").append(DiagnosticColors.neutral(("examples/test.k")));
        return sb.toString();
    }
}
