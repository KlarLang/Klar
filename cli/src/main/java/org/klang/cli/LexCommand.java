package org.klang.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.klang.cli.error.KcInvalidFileType;
import org.klang.cli.error.diagnostic.KcDiagnosticCode;
import org.klang.core.error.KException;
import org.klang.core.lexer.Lexer;
import org.klang.core.lexer.Token;

@Command(
    name = "lex",
    description = "Show file tokens"
)
public class LexCommand implements Runnable {

    @Parameters(paramLabel = "FILE")
    private File file;

    @Override
    public void run() {
        Path path = file.toPath();

        if (!path.getFileName().toString().endsWith(".k")) {

            throw new KcInvalidFileType(KcDiagnosticCode.KC002, "lex", null, path.getFileName().toString());
        }

        try {
            String source = Files.readString(path);
            Lexer lexer = new Lexer(source, file.getPath());
            List<Token> a = lexer.tokenizeSourceCode();

            for (Token var : a) {
                System.out.println(var);
            };
            

        } catch (KException e) {
            System.out.println(e.format());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Internal compiler error", e);
        }
    }
}
