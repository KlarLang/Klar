package org.klang.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.klang.cli.error.KcInvalidFileType;
import org.klang.cli.error.diagnostic.KcDiagnosticCode;
import org.klang.cli.utils.BuildCache;
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
        
        String _fileName = path.getFileName().toString(); 
        String fileName = _fileName.substring(0, _fileName.length() - 2);

        if (!_fileName.endsWith(".k")) {
            throw new KcInvalidFileType(
                KcDiagnosticCode.KC002,
                "build",
                null,
                _fileName
            );
        }

        try {
            Path outDir = Path.of("out");
            Path cacheDir = outDir.resolve(".cache");
            Files.createDirectories(cacheDir);

            Path cacheFile = cacheDir.resolve(fileName + ".hash");
            Path outputFile = outDir.resolve(fileName + ".java");

            // Verificar se precisa rebuildar
            if (!BuildCache.needsRebuild(path, cacheFile)) {
                System.out.println("✓ " + fileName + ".java is up to date (skipping build)");
                return;
            }

            System.out.println("Building " + fileName + ".k...");

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
            JavaTranspiler transpiler = new JavaTranspiler(fileName);
            String javaCode = transpiler.transpile(program);

            // 6. Write output
            Files.writeString(outputFile, javaCode);

            // 7. Salvar hash para próxima vez
            BuildCache.saveHash(path, cacheFile);

            System.out.println("✓ Build successful → " + fileName + ".java generated");

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Internal compiler error", e);
        }
    }
}