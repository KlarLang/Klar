package org.klang.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.klang.core.errors.Diagnostic;
import org.klang.core.errors.DiagnosticException;
import org.klang.core.errors.DiagnosticPrinter;
import org.klang.core.errors.DiagnosticType;
import org.klang.core.errors.Note;
import org.klang.core.errors.Span;
import org.klang.core.lexer.Lexer;

@Command(name = "lex", description = "Show file tokens")
public class LexCommand implements Runnable {

    @Parameters(paramLabel = "FILE")
    private File file;

    @Override
    public void run() {
        try {
            Path path = file.toPath();

            if (!path.getFileName().toString().endsWith(".k")) {

                Span span = new Span(null, 1, 1, 1, 1); // fileless → works in any context

                Diagnostic d = Diagnostic.builder(DiagnosticType.TYPE, "The file must be a .k file")
                        .primary(span)
                        .addNote(new Note("Maybe you got confused with another file type?"))
                        .build();

                throw new DiagnosticException(d);
            }

            String source = Files.readString(path);

            Lexer lexer = new Lexer(source, file.getPath());
            lexer.tokenizeSourceCode().forEach(System.out::println);

        } catch (DiagnosticException e) {
            DiagnosticPrinter printer = new DiagnosticPrinter(true, true);
            printer.print(e.diagnostic);

            System.exit(1);
        } catch (Exception e) {
            // unexpected errors → stacktrace
            e.printStackTrace();
            System.exit(2);
        }
    }

}
