package org.klar.core.diagnostics;

import org.klar.core.errors.Phase;

/**
 * Enumeration of all possible diagnostic codes in the Klar compiler.
 * <p>
 * Acts as a central registry for errors, categorized by compiler phase.
 * </p>
 *
 * @author Lucas Paulino Da Silva (~K')
 */
public enum DiagnosticCode {
    // --- Lexical Errors (000 - 099) ---
    E000("ExpectedCharacter", Phase.LEXICAL),
    E001("UnexpectedCharacter", Phase.LEXICAL),
    E002("UnterminatedString", Phase.LEXICAL),

    // --- Syntax Errors (100 - 199) ---
    E101("InvalidNumericLiteral", Phase.SYNTAX),
    E102("MissingSemicolon", Phase.SYNTAX),
    E103("ExpectedType", Phase.SYNTAX),
    E104("InvalidCharLiteral", Phase.SYNTAX),
    E105("MissingArrayBracket", Phase.SYNTAX),
    E106("MissingArraySize", Phase.SYNTAX),
    E107("MalformedArrayInitializer", Phase.SYNTAX),
    E108("ExpectedExpression", Phase.SYNTAX),
    E109("MissingReturnType", Phase.SYNTAX),
    E110("MissingParameterType", Phase.SYNTAX),
    E111("MisplacedAfterall", Phase.SYNTAX),
    E112("MissingAccessModifier", Phase.SYNTAX),
    E113("MissingAnnotation", Phase.SYNTAX),

    // --- Semantic Errors (200 - 299) ---
    E201("UnknownType", Phase.SEMANTIC),
    E202("MultipleReturnStatements", Phase.SEMANTIC),
    E203("InvalidReturnPlacement", Phase.SEMANTIC),
    E204("UnknownAnnotation", Phase.SEMANTIC),
    E205("MissingReturnStatement", Phase.SEMANTIC),
    E206("SymbolRedeclaration", Phase.SEMANTIC),
    E207("TypeMismatch", Phase.SEMANTIC),
    E208("ArgumentCountMismatch", Phase.SEMANTIC),
    E209("InvalidAssignment", Phase.SEMANTIC),
    E210("InvalidMainSignature", Phase.SEMANTIC),
    E211("InvalidConditionType", Phase.SEMANTIC),
    E212("MagicNumberViolation", Phase.SEMANTIC),
    E213("NonConstantExpression", Phase.SEMANTIC),
    E214("InvalidOperation", Phase.SEMANTIC),
    E215("NotAnArray", Phase.SEMANTIC),
    E216("ArraySizeMismatch", Phase.SEMANTIC),
    E217("UnresolvedSymbol", Phase.SEMANTIC),

    E400("BackendProbeTimeout", Phase.BACKEND),
    E401("MissingBackendTarget", Phase.BACKEND),
    E402("InvalidBackendBinding", Phase.BACKEND),
    E404("BackendConstraintViolation", Phase.BACKEND);

    public final String name;
    public final Phase phase;

    DiagnosticCode(String name, Phase phase) {
        this.name = name;
        this.phase = phase;
    }
}