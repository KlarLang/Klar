package org.klar.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.klar.cli.error.KcInvalidFileType;
import org.klar.cli.error.diagnostic.KcDiagnosticCode;
import org.klar.core.errors.SourceManager;
import org.klar.core.lexer.Lexer;
import org.klar.core.lexer.Token;
import org.klar.core.parser.Parser;
import org.klar.core.parser.ast.ProgramNode;

@Command(
    name = "parse",
    description = "Parse file.kl | file.klar"
)
public class ParseCommand implements Runnable {

    @Parameters(paramLabel = "FILE")
    private File file;

    @Override
    public void run() {
        Path path = file.toPath();
        String fileName = path.getFileName().toString();

        if (!fileName.endsWith(".kl") || !fileName.endsWith(".klar")) {
            throw new KcInvalidFileType(
                KcDiagnosticCode.KC002,
                 "parse",
                  null,
                   path.getFileName().toString());
        }

        try {
            String source = Files.readString(path);
            SourceManager sourceManager = new SourceManager(source);

            Lexer lexer = new Lexer(source, file.getPath(), sourceManager);
            List<Token> tokens = lexer.tokenizeSourceCode();

            Parser parser = new Parser(tokens, path, sourceManager);
            ProgramNode program = parser.parseProgram();

            System.out.println("Parsed successfully.");

        } catch (RuntimeException e) {
            throw e;

        } catch (Exception e) {
            throw new RuntimeException("Internal compiler error", e);
        }
    }
}
