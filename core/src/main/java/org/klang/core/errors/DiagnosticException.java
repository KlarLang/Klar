package org.klang.core.errors;

import java.util.Objects;

/**
 * Exception que carrega o Diagnostic. Em geral é lançada quando não há como
 * recuperar do erro.
 */
public final class DiagnosticException extends RuntimeException {
    public final Diagnostic diagnostic;

    public DiagnosticException(Diagnostic diagnostic) {
        super(diagnostic.message);
        this.diagnostic = Objects.requireNonNull(diagnostic, "diagnostic não pode ser null");
    }

    public DiagnosticException(Diagnostic diagnostic, Throwable cause) {
        super(diagnostic.message, cause);
        this.diagnostic = Objects.requireNonNull(diagnostic, "diagnostic não pode ser null");
    }

    @Override
    public String toString() {
        return diagnostic.type + ": " + diagnostic.message +
                " em " + diagnostic.primarySpan;
    }
}