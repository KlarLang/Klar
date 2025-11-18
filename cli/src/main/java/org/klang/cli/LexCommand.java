package org.klang.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.klang.core.errors.DiagnosticException;
import org.klang.core.errors.DiagnosticFormatter;
import org.klang.core.lexer.Lexer;

@Command(name = "lex", description = "Mostra os tokens do arquivo")
public class LexCommand implements Runnable {

    @Parameters(paramLabel = "FILE")
    private File file;

    private final DiagnosticFormatter formatter = new DiagnosticFormatter();

    @Override
    public void run() {
        try {
            Path path = file.toPath();
            String source = Files.readString(path);

            Lexer lexer = new Lexer(source, file.getPath());

            lexer.tokenize().forEach(System.out::println);

            // Provavelmente na sua Main ou CLI
        } catch (DiagnosticException e) {
            formatter.print(e.diagnostic);

        } catch (Exception e) {
            // erro inesperado → mostrar stacktrace (para debug)
            e.printStackTrace();
            System.exit(2);
        }
    }
}
