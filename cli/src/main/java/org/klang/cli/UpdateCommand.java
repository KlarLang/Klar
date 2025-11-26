package org.klang.cli;

import java.io.File;
import java.io.IOException;

import org.klang.core.errors.Diagnostic;
import org.klang.core.errors.DiagnosticException;
import org.klang.core.errors.DiagnosticType;
import org.klang.core.errors.Note;
import org.klang.core.errors.Span;

import picocli.CommandLine.Command;

@Command(name = "update", description = "Update Klang properties")
public class UpdateCommand implements Runnable {

    // Estética Klang
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String WINE = "\u001B[38;2;127;0;31m";
    private static final String GRAY = "\u001B[38;2;180;180;180m";
    private static final String GREEN = "\u001B[38;2;80;200;120m";
    private static final String RED = "\u001B[38;2;255;80;80m";

    // Spinner Klang Pulse
    private static final String[] FRAMES = { "⦿", "⦾" };
    private volatile boolean spinning = true;

    private final String PROJECT_DIR = System.getProperty("user.home") + "/Klang";

    @Override
    public void run() {
        System.out.println();
        System.out.println(WINE + BOLD + "KLANG" + RESET + " " + GRAY + "- Updating…" + RESET);
        System.out.println();

        startSpinner();
        update();
    }

    private void startSpinner() {
        Thread t = new Thread(() -> {
            int i = 0;
            while (spinning) {
                System.out.print("\r  " + GRAY + " Updating… " + RESET + FRAMES[i % FRAMES.length]);
                System.out.flush(); // ← Adicione isso para garantir que apareça imediatamente
                i++;
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt(); // boa prática
                }
            }
        });

        t.setDaemon(true);
        t.start();

        // Dê tempo para o spinner começar
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {
        }
    }

    private void stopSpinner() {
        spinning = false;
        System.out.print("\r"); // limpa a linha
    }

    private void update() {
        updateProjectGit();
        updateCli();
        updateinstallCLi();
    }

    private void updateProjectGit() {
        File dir = new File(PROJECT_DIR);

        if (!dir.exists()) {
            stopSpinner();

            Diagnostic d = Diagnostic.builder(DiagnosticType.INFO,
                    "The path '" + PROJECT_DIR + "' does not exist.")
                    .addNote(new Note("Try cloning the repository again."))
                    .primary(new Span(null, 1, 1, 1, 1))
                    .build();

            System.out.println("  " + RED + " - Path not found" + RESET);
            throw new DiagnosticException(d);
        }

        ProcessBuilder pb = new ProcessBuilder("git", "pull");
        pb.directory(dir);
        pb.inheritIO();

        try {
            Process proc = pb.start();
            int exit = proc.waitFor();

            stopSpinner();

            if (exit != 0) {
                System.out.println("  " + RED + " - Update failed" + RESET);

                Diagnostic d = Diagnostic.builder(DiagnosticType.ERROR,
                        "git pull failed (exit=" + exit + ")")
                        .primary(new Span(null, 1, 1, 1, 1))
                        .addNote(new Note("Run 'git status' inside the project to diagnose the problem."))
                        .build();

                throw new DiagnosticException(d);
            }

            System.out.println("  " + GREEN + " - Updated successfully." + RESET);

        } catch (IOException | InterruptedException e) {

            stopSpinner();
            System.out.println("  " + RED + " - Update failed" + RESET);

            Diagnostic d = Diagnostic.builder(DiagnosticType.ERROR,
                    "Failed to execute update: " + e.getMessage())
                    .primary(new Span(null, 1, 1, 1, 1))
                    .addNote(new Note("This usually happens when git is missing or corrupted."))
                    .build();

            throw new DiagnosticException(d);
        }
    }

    private void updateCli() {

        String osName = System.getProperty("os.name");
        String command;

        if (osName != null && osName.toLowerCase().startsWith("windows")) {
            command = "gradlew.bat";
        } else {
            command = "./gradlew";
        }

        ProcessBuilder pb = new ProcessBuilder(command, ":cli:shadowJar");
        pb.inheritIO();

        try {
            Process proc = pb.start();
            int exit = proc.waitFor();

            stopSpinner();

            if (exit != 0) {
                System.out.println("  " + RED + " - Update cli failed" + RESET);

                Diagnostic d = Diagnostic.builder(DiagnosticType.ERROR,
                        "git pull failed (exit=" + exit + ")")
                        .primary(new Span(null, 1, 1, 1, 1))
                        .addNote(new Note("Run './gradlew --status' inside the project to diagnose the problem."))
                        .build();

                throw new DiagnosticException(d);
            }

            System.out.println("  " + GREEN + " - Updated cli successfully." + RESET);

        } catch (IOException | InterruptedException e) {

            stopSpinner();
            System.out.println("  " + RED + " - Update cli failed" + RESET);

            Diagnostic d = Diagnostic.builder(DiagnosticType.ERROR,
                    "Failed to execute update: " + e.getMessage())
                    .primary(new Span(null, 1, 1, 1, 1))
                    .addNote(new Note("This usually happens when gradlew is missing or corrupted."))
                    .build();

            throw new DiagnosticException(d);
        }
    }

    private void updateinstallCLi() {
        File dir = new File(PROJECT_DIR);

        if (!dir.exists()) {
            stopSpinner();

            Diagnostic d = Diagnostic.builder(DiagnosticType.INFO,
                    "The path '" + PROJECT_DIR + "' does not exist.")
                    .addNote(new Note("Try cloning the repository again."))
                    .primary(new Span(null, 1, 1, 1, 1))
                    .build();

            System.out.println("  " + RED + " - Path not found" + RESET);
            throw new DiagnosticException(d);
        }

        String osName = System.getProperty("os.name");
        String command;
        if (osName != null && osName.toLowerCase().startsWith("windows")) {
            command = "install.bat";
        } else {
            command = "./install.sh";
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(dir);
        pb.inheritIO();

        try {
            Process proc = pb.start();
            int exit = proc.waitFor();

            stopSpinner();

            if (exit != 0) {
                System.out.println("  " + RED + " - Update cli failed" + RESET);

                Diagnostic d = Diagnostic.builder(DiagnosticType.ERROR,
                        "git pull failed (exit=" + exit + ")")
                        .primary(new Span(null, 1, 1, 1, 1))
                        .addNote(new Note("Open an issue at https://github.com/KlangLang/Klang reporting the problem."))
                        .build();

                throw new DiagnosticException(d);
            }

            System.out.println("  " + GREEN + " - Updated cli successfully." + RESET);

        } catch (IOException | InterruptedException e) {

            stopSpinner();
            System.out.println("  " + RED + " - Update cli failed" + RESET);

            Diagnostic d = Diagnostic.builder(DiagnosticType.ERROR,
                    "Failed to execute update: " + e.getMessage())
                    .primary(new Span(null, 1, 1, 1, 1))
                    .addNote(new Note("This usually happens when " + command + " is missing or corrupted."))
                    .build();

            throw new DiagnosticException(d);
        }
    }
}