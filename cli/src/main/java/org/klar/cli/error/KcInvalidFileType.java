package org.klar.cli.error;

import org.klar.cli.error.diagnostic.KcDiagnosticCode;
import org.klar.cli.error.diagnostic.KcDiagnosticColors;

public class KcInvalidFileType extends KcCliException {
  private final String fileName;

  public KcInvalidFileType(KcDiagnosticCode code, String command, String fix, String fileName) {
    super(code, command, fix);

    this.fileName = fileName;
  }

  @Override
  public String format() {
    StringBuilder sb = new StringBuilder();
    sb.append(KcDiagnosticColors.structure("[K:"))
        .append(KcDiagnosticColors.cliErrorCode(code.name()))
        .append(KcDiagnosticColors.structure("] "))
        .append(KcDiagnosticColors.neutral(code.name))
        .append("\n");

    // ERROR (Lexical) discreto, só informativo
    sb.append(KcDiagnosticColors.structure("ERROR (" + code.phase.name() + ")"))
        .append("\n");

    sb.append(KcDiagnosticColors.structure("at input file"))
        .append("\n\n");

    sb.append(KcDiagnosticColors.structure("The file '")).append(KcDiagnosticColors.neutral(fileName))
        .append(KcDiagnosticColors.structure("' is not a Klar source file."))
        .append("\n\n");

    sb.append(KcDiagnosticColors.structure("Cause:"))
        .append("\n  ")
        .append(KcDiagnosticColors.neutral("Klar only processes files with the extensions '.kl' and '.klar'."))
        .append("\n\n");

    sb.append(KcDiagnosticColors.structure("Fix:"))
        .append("\n  ")
        .append(KcDiagnosticColors.neutral("Rename the file or select a valid '.kl' or '.klar' source."))
        .append("\n\n");

    sb.append(KcDiagnosticColors.structure("Example:"))
        .append("\n  ")
        .append(KcDiagnosticColors.neutral("kc " + this.command + " program.kl"));

    return sb.toString();
  }
}
