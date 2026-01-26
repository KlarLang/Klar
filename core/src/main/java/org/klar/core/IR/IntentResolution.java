package org.klar.core.IR;

import java.nio.file.Path;
import java.util.Set;

import org.klar.core.diagnostics.DiagnosticCode;
import org.klar.core.errors.BackendException;
import org.klar.core.errors.SourceLocation;
import org.klar.core.errors.SourceManager;
import org.klar.core.lexer.Token;
import org.klar.core.parser.ast.FunctionDeclarationNode;
import org.klar.core.parser.ast.ProgramNode;
import org.klar.core.parser.ast.StatementNode;
import org.klar.core.parser.ast.UseAnnotationNode;

public class IntentResolution {
    private final ProgramNode program;
    private final SourceManager sm;
    private final Path filePath;
    private Set<String> targetsResolveds = Set.of();

    public IntentResolution(ProgramNode program, Path filePath, SourceManager sm) {
        this.program = program;
        this.filePath = filePath;
        this.sm = sm;
    }

    public void validateItent() {
        for (StatementNode node : program.statements) {
            if (node instanceof FunctionDeclarationNode f) {
                Token target = f.use.target;
                String targetNormalized = target.getValue().replaceAll("\"", "");

                if (targetNormalized.isEmpty() || targetNormalized.isBlank()) {
                    throw new BackendException(
                            DiagnosticCode.E400,
                            new SourceLocation(filePath.toString(), target.getLine(),
                                    Math.max(target.getColumn() - 1, 0)),
                            sm.getContextLines(target.getLine(), 2),
                            "Empty backend target",
                            "Use a supported backend (currently only 'java' is supported)",
                            "@Use(\"java\")\n  public void myFunction() { ... }",
                            "Klar currently supports only Java as a compilation target.",
                            target.getValue().length());
                }

                if (!targetNormalized.equals("java")) {
                    throw new BackendException(
                            DiagnosticCode.E400,
                            new SourceLocation(filePath.toString(), target.getLine(),
                                    Math.max(target.getColumn() - 1, 0)),
                            sm.getContextLines(target.getLine(), 2),
                            "Unsupported backend target '" + targetNormalized + "'",
                            "Use a supported backend (currently only 'java' is supported)",
                            "@Use(\"java\")\n  public void myFunction() { ... }",
                            "Klar currently supports only Java as a compilation target.",
                            target.getValue().length());
                }

                try {
                    verifyExistsTarget(f.use);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void verifyExistsTarget(UseAnnotationNode use) throws Exception {
        String targetNormalized = use.target.getValue().replaceAll("\"", "");

        if (targetNormalized.contains(targetNormalized)) {
            return;
        }

        Process versionCommand = new ProcessBuilder(targetNormalized, "--version").inheritIO().start();

        int exitCode = versionCommand.waitFor();

        if (exitCode != 0) {
            throw new BackendException(
                    DiagnosticCode.E401,
                    new SourceLocation(filePath.toString(), use.target.getLine(),
                            Math.max(use.target.getColumn() - 1, 0)),
                    sm.getContextLines(use.target.getLine(), 2),
                    "Unsupported backend target '" + targetNormalized + "'",
                    "Use a supported backend (currently only 'java' is supported)",
                    "@Use(\"java\")\n  public void myFunction() { ... }",
                    "Klar currently supports only Java as a compilation target.",
                    use.target.getValue().length());
        }

        targetsResolveds.add(targetNormalized);
    }
}