package org.klang.cli;


import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.klang.cli.error.KcInvalidFileType;
import org.klang.cli.error.diagnostic.KcDiagnosticCode;
import org.klang.core.errors.SourceManager;
import org.klang.core.lexer.Lexer;
import org.klang.core.lexer.Token;
import org.klang.core.parser.Parser;
import org.klang.core.parser.ast.ProgramNode;
import org.klang.core.semantics.TypeChecker;
import org.klang.core.transpilers.JavaTranspiler;

@Command(
    name = "run",
    description = "Transpile, compile and run a Klang program"
)
public class RunCommand implements Runnable {

    @Parameters(paramLabel = "FILE")
    private File file;

    @Override
    public void run() {
        try {
            // 1. Ler fonte
            String source = Files.readString(file.toPath());
            SourceManager sm = new SourceManager(source);

            // 2. Lexer / Parser
            Lexer lexer = new Lexer(source, file.getPath(), sm);
            List<Token> tokens = lexer.tokenizeSourceCode();
            Parser parser = new Parser(tokens, file.toPath(), sm);
            ProgramNode program = parser.parseProgram();

            // 3. Transpilar
            JavaTranspiler transpiler = new JavaTranspiler();
            String javaCode = transpiler.transpile(program);

            Path outDir = Path.of("out");
            Files.createDirectories(outDir);

            Path javaFile = outDir.resolve("Main.java");
            Files.writeString(javaFile, javaCode);

            // 4. Compilar
            Process javac = new ProcessBuilder(
                "javac", javaFile.toString()
            ).inheritIO().start();

            if (javac.waitFor() != 0) {
                throw new RuntimeException("Java compilation failed");
            }

            // 5. Executar
            Process java = new ProcessBuilder(
                "java", "-cp", outDir.toString(), "Main"
            ).inheritIO().start();

            java.waitFor();

        } catch (Exception e) {
            throw new RuntimeException("Run failed", e);
        }
    }
}
