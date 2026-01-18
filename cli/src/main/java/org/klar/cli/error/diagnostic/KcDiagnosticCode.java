package org.klar.cli.error.diagnostic;

public enum KcDiagnosticCode {
    KC001("UnknowCommand", KcPhase.CLI),
    KC002("InvalidFileType", KcPhase.CLI),
    KC003("MissingArgument", KcPhase.CLI),
    KC900("InternalError", KcPhase.CLI);

    public final String name;
    public final KcPhase phase;

    KcDiagnosticCode(String name, KcPhase phase) {
        this.name = name;
        this.phase = phase;
    }
}


