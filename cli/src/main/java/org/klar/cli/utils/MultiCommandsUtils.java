package org.klar.cli.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.klar.cli.error.KcInvalidFileType;
import org.klar.cli.error.diagnostic.KcDiagnosticCode;
import org.klar.core.IR.IntentResolution;
import org.klar.core.errors.KException;
import org.klar.core.errors.SourceManager;
import org.klar.core.lexer.Lexer;
import org.klar.core.lexer.Token;
import org.klar.core.parser.Parser;
import org.klar.core.parser.ast.ProgramNode;
import org.klar.core.semantics.TypeChecker;
import org.klar.core.transpilers.JavaTranspiler;

public class MultiCommandsUtils implements Runnable {

    private final File file;
    private final String caller;
    private final boolean clean;

    public MultiCommandsUtils(File file, String caller, boolean clean) {
        this.file = file;
        this.caller = caller;
        this.clean = clean;
    }

    @Override
    public void run() {
        Path path = file.toPath();

        String _fileName = path.getFileName().toString();
        String sufix = _fileName.substring(_fileName.lastIndexOf(".") + 1);
        String fileName = _fileName.replaceFirst("[.][^.]+$", "");

        System.err.println(_fileName);
        if (!_fileName.endsWith(".kl") && !_fileName.endsWith(".klar")) {
            throw new KcInvalidFileType(
                    KcDiagnosticCode.KC002,
                    caller,
                    null,
                    _fileName);
        }

        try {
            Path outDir = Path.of("out");
            Path sourceOutDir = outDir.resolve("java");
            Path classOutDir = sourceOutDir.resolve("class");
            Path cacheDir = outDir.resolve(".cache");

            if (clean) {
                apagarDiretorio(outDir);
            }

            Files.createDirectories(cacheDir);
            Files.createDirectories(sourceOutDir);
            Files.createDirectories(classOutDir);

            Path cacheFile = cacheDir.resolve(fileName + ".hash");
            Path outputFile = sourceOutDir.resolve(fileName + ".java");

            boolean needsRebuild = BuildCache.needsRebuild(path, cacheFile);

            // Build phase
            if (!needsRebuild && !clean) {
                System.out.println("✓ " + fileName + ".java is up to date (skipping build)");
            } else {
                System.out.println("Building " + fileName + sufix + "...");

                // 1. Read
                String source = Files.readString(path);
                SourceManager sourceManager = new SourceManager(source);

                // 2. Lex
                Lexer lexer = new Lexer(source, file.getPath(), sourceManager);
                List<Token> tokens = lexer.tokenizeSourceCode();

                // 3. Parse
                Parser parser = new Parser(tokens, path, sourceManager);
                ProgramNode program = parser.parseProgram();

                // 4. Type check
                TypeChecker checker = new TypeChecker(sourceManager, path);
                checker.check(program);

                // 5. Validation
                IntentResolution iR = new IntentResolution(program, path, sourceManager);
                iR.validateItent();

                // 6. Transpile
                JavaTranspiler transpiler = new JavaTranspiler(fileName, sourceManager, path);
                String javaCode = transpiler.transpile(program);

                // 7. Write output
                Files.writeString(outputFile, javaCode);

                // 8. Save hash for next time
                BuildCache.saveHash(path, cacheFile);

                if (caller.equals("build")) {
                    System.out.println(
                            "✓ Build successful → " + fileName + ".java generated at " + sourceOutDir.toAbsolutePath());
                }
            }

            // Compile phase - always compile for run, or if we just rebuilt
            if (caller.equals("run") || needsRebuild || clean) {
                compileJavaSource(sourceOutDir, classOutDir, fileName);
            }

            // Run phase
            if (caller.equals("run")) {
                runProject(classOutDir, fileName);
            }

        } catch (KException e) {
            System.out.println(e.format());
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Process interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Build error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void compileJavaSource(Path sourceOutDir, Path classOutDir, String fileName)
            throws IOException, InterruptedException {
        System.out.println("Compiling " + fileName + ".java...");

        Path javaFile = sourceOutDir.resolve(fileName + ".java");

        Process javac = new ProcessBuilder(
                "javac",
                "-d", classOutDir.toAbsolutePath().toString(),
                javaFile.toAbsolutePath().toString()).inheritIO().start();

        int exitCode = javac.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Compilation failed with exit code: " + exitCode);
        }

        System.out.println("✓ Compilation successful");
    }

    private void runProject(Path classOutDir, String fileName) {
        try {
            System.out.println("Running " + fileName + "...\n");

            Process java = new ProcessBuilder(
                    "java", "-cp", classOutDir.toAbsolutePath().toString(), fileName).inheritIO().start();

            int exitCode = java.waitFor();
            System.err.println("\nProgram exited with code: " + exitCode);

        } catch (KException e) {
            System.out.println(e.format());
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Process interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Build error: " + e.getMessage());
        }
    }

    public static void apagarDiretorio(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<Path> walk = Files.walk(path)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                System.err.println("Erro ao apagar: " + p + " - " + e.getMessage());
                            }
                        });
            }
        }
    }
}