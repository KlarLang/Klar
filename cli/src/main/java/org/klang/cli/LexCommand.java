package org.klang.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.klang.cli.error.KcInvalidFileType;
import org.klang.cli.error.diagnostic.KcDiagnosticCode;
import org.klang.core.errors.KException;
import org.klang.core.errors.SourceManager;
import org.klang.core.lexer.Lexer;
import org.klang.core.lexer.Token;

@Command(
    name = "lex",
    description = "Show file tokens"
)
public class LexCommand implements Runnable {

    @Parameters(paramLabel = "FILE")
    private File file;

    @Option(names = {"--show-tokens", "-st"}, description = "Explicitly show tokens")
    private boolean showTokens = false;

    @Option(names = {"--show-error", "-se"}, description = "Explicitly show tokens")
    private boolean showError = false;

    @Override
    public void run() {
        Path path = file.toPath();

        if (!path.getFileName().toString().endsWith(".k")) {

            throw new KcInvalidFileType(KcDiagnosticCode.KC002, "lex", null, path.getFileName().toString());
        }

        try {
            String source = Files.readString(path);
            SourceManager sourceManager = new SourceManager(source);

            Lexer lexer = new Lexer(source, file.getPath(), sourceManager);
            List<Token> a = lexer.tokenizeSourceCode();

            if (showTokens){
                for (Token var : a) {
                    System.out.println(var);
                };
            } else {
                System.out.println("Lexing successful! (Use --show-tokens to see the output)");
            }
            

        } catch (KException e) {
            System.out.println(e.format());

        } catch (Exception e) {
            if (showError){
                e.printStackTrace();
            }{
                System.out.println("Use --show-error or -se to show stack trace");                
            }
            throw new RuntimeException("Internal compiler error", e);
        }
    }
}
