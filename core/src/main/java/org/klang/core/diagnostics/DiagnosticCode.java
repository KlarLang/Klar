package org.klang.core.diagnostics;

import org.klang.core.errors.Phase;

public enum DiagnosticCode {
    E001("InvalidCharacter", Phase.LEXICAL),
    E002("UnterminatedStringLiteral", Phase.LEXICAL),
    E004("InvalidCharacter", Phase.LEXICAL),

    E101("InvalidNumber", Phase.SYNTAX),
    E102("MissingStatementTerminator", Phase.SYNTAX),
    E103("MissingType", Phase.SYNTAX),
    E104("MultiCharacterOfTypeCharacter", Phase.SYNTAX),
    E105("MissingSizeArray", Phase.SYNTAX),
    
    E201("UnknownTypeIdentifier", Phase.SEMANTIC);

    public final String name;
    public final Phase phase;

    DiagnosticCode(String name, Phase phase) {
        this.name = name;
        this.phase = phase;
    }
}
