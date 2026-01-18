package org.klar.cli.error;

import org.klar.cli.error.diagnostic.KcDiagnosticCode;
import org.klar.cli.error.diagnostic.KcDiagnosticColors;

public class KcUnknowCommandException extends KcCliException {
    
    private final String suggestedCommand; // Novo campo
    
    public KcUnknowCommandException(
            KcDiagnosticCode code, 
            String command, 
            String fix) {
        this(code, command, fix, null);
    }
    
    public KcUnknowCommandException(
            KcDiagnosticCode code, 
            String command, 
            String fix,
            String suggestedCommand) {
        super(code, command, fix);
        this.suggestedCommand = suggestedCommand;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();

        sb.append(KcDiagnosticColors.structure("[K:")).append(KcDiagnosticColors.cliErrorCode(code.name())).append(KcDiagnosticColors.structure("] "))
          .append(KcDiagnosticColors.neutral(code.name)).append("\n")
          .append(KcDiagnosticColors.structure("ERROR (" + code.phase.name() + (")\n")))
          .append(KcDiagnosticColors.structure("at") + " " + KcDiagnosticColors.neutral("command input\n\n"))
          .append(KcDiagnosticColors.structure("The command '")).append(KcDiagnosticColors.cliError(this.command)).append(KcDiagnosticColors.structure("' is not recognized."))
          .append("\n");
        
        // Se há uma sugestão específica, mostra ela em destaque
        if (suggestedCommand != null) {
            sb.append(KcDiagnosticColors.structure("\nDid you mean:\n"))
              .append("  " + KcDiagnosticColors.structure("kc") + " " + KcDiagnosticColors.neutral(suggestedCommand)).append("\n");
        }
        
        // Sempre mostra todos os comandos disponíveis
        String[] commands = super.getCommands();
        sb.append(KcDiagnosticColors.structure("\nAvailable commands:\n"));
        for (String cmd : commands) {
            sb.append("  ").append(KcDiagnosticColors.structure("kc ")).append(KcDiagnosticColors.neutral(cmd)).append("\n");
        }

        sb.append(KcDiagnosticColors.structure("\nFix:\n  ")).append(KcDiagnosticColors.neutral(fix));

        return sb.toString();
    }
}