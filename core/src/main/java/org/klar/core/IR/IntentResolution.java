package org.klar.core.IR;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private final EnumSet<KnowTargets> targetsResolved = EnumSet.noneOf(KnowTargets.class);
    private final Map<KnowTargets, String[][]> versionCommandByLang = Map.of(
            KnowTargets.JAVA, new String[][] {
                    { "java", "--version" },
                    { "javac", "-version" }
            });
    private static final int PROBE_TIMEOUT_SECONDS = 2;

    public IntentResolution(ProgramNode program, Path filePath, SourceManager sm) {
        this.program = program;
        this.filePath = filePath;
        this.sm = sm;
    }

    private String normalizeString(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (c == '\'' || c == '\"') {
                continue;
            }

            sb.append(c);
        }

        return sb.toString();
    }

    public void validateIntent() {
        for (StatementNode node : program.statements) {
            if (node instanceof FunctionDeclarationNode f) {
                Token target = f.use.target;
                String targetNormalized = normalizeString(target.getValue());

                if (targetNormalized.isEmpty() || targetNormalized.isBlank()) {
                    throw new BackendException(
                            DiagnosticCode.E401,
                            new SourceLocation(filePath.toString(), target.getLine(),
                                    Math.max(target.getColumn() - 1, 0)),
                            sm.getContextLines(target.getLine(), 2),
                            "Empty backend target",
                            "Use a supported backend (currently only 'java' is supported)",
                            "@Use(\"java\")\n  public void myFunction() { ... }",
                            "Klar currently supports only Java as a compilation target.",
                            target.getValue().length());
                }

                try {
                    verifyExistsTarget(f.use, targetNormalized);
                } catch (BackendException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException("Unexpected error verifying backend target", e);
                }
            }
        }
    }

    private void verifyExistsTarget(UseAnnotationNode use, String targetNormalized) throws Exception {
        KnowTargets enumTarget = getEnumByString(targetNormalized);

        if (enumTarget == KnowTargets.OUTSIDER) {
            throw new BackendException(
                    DiagnosticCode.E402,
                    new SourceLocation(filePath.toString(), use.target.getLine(),
                            Math.max(use.target.getColumn() - 1, 0)),
                    sm.getContextLines(use.target.getLine(), 2),
                    "Unsupported backend target '" + targetNormalized + "'",
                    "Use a supported backend (currently only 'java' is supported)",
                    "@Use(\"java\")\n  public void myFunction() { ... }",
                    "Klar currently supports only Java as a compilation target.",
                    use.target.getValue().length());
        }

        if (targetsResolved.contains(enumTarget)) {
            return;
        }

        String[][] probes = versionCommandByLang.get(enumTarget);
        ArrayList<String> failed = new ArrayList<>(probes.length);

        for (String[] probe : probes) {
            String tool = probe[0];
            String flag = probe[1];

            try {
                Process p = new ProcessBuilder(tool, flag).redirectErrorStream(true).start();
                boolean finished = p.waitFor(PROBE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

                if (finished == false) {
                    p.destroyForcibly();
                    throw new BackendException(
                            DiagnosticCode.E400,
                            new SourceLocation(filePath.toString(), use.target.getLine(),
                                    Math.max(use.target.getColumn() - 1, 0)),
                            sm.getContextLines(use.target.getLine(), 2),
                            "Backend toolchain probe timed out: '" + tool + " " + flag + "'",
                            "Ensure the tool is installed and responsive. Try running '" + tool + " " + flag
                                    + "' manually.",
                            "@Use(\"java\")\n  public void myFunction() { ... }",
                            "Klar requires a responsive backend toolchain (e.g., java + javac).",
                            use.target.getValue().length());
                }

                int code = p.exitValue();
                if (code != 0) {
                    failed.add(tool + " " + flag + " (exit " + code + ")");
                }

            } catch (BackendException e) {
                throw e;
            } catch (Exception e) {
                failed.add(tool + " " + flag + " (" + e.getClass().getSimpleName() + ")");
            }
        }

        if (!failed.isEmpty()) {
            String joined = String.join(", ", failed);

            throw new BackendException(
                    DiagnosticCode.E404,
                    new SourceLocation(filePath.toString(), use.target.getLine(),
                            Math.max(use.target.getColumn() - 1, 0)),
                    sm.getContextLines(use.target.getLine(), 2),
                    "Backend toolchain verification failed for target '" + targetNormalized + "'",
                    "Install/fix the missing tools and retry. Failed checks: " + joined,
                    "@Use(\"java\")\n  public void myFunction() { ... }",
                    "Klar requires a working toolchain for the selected backend (e.g., java + javac).",
                    use.target.getValue().length());
        }

        targetsResolved.add(enumTarget);
    }

    private KnowTargets getEnumByString(String s) {
        switch (s.toLowerCase()) {
            case "java":
                return KnowTargets.JAVA;

            default:
                return KnowTargets.OUTSIDER;
        }
    }
}