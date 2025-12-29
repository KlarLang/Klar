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
    name = "build",
    description = "Build Klang source to Java"
)
public class BuildCommand implements Runnable {

    @Parameters(paramLabel = "FILE")
    private File file;

    @Override
    public void run() {
        Path path = file.toPath();

        if (!path.getFileName().toString().endsWith(".k")) {
            throw new KcInvalidFileType(
                KcDiagnosticCode.KC002,
                "build",
                null,
                path.getFileName().toString()
            );
        }

        try {
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
            TypeChecker checker = new TypeChecker();
            checker.check(program);

            // 5. Transpile
            JavaTranspiler transpiler = new JavaTranspiler();
            String javaCode = transpiler.transpile(program);

            // 6. Write output
            Path out = Path.of("Main.java");
            Files.writeString(out, javaCode);

            System.out.println("Build successful → Main.java generated");

        } catch (RuntimeException e) {
            throw e;

        } catch (Exception e) {
            throw new RuntimeException("Internal compiler error", e);
        }
    }
}
