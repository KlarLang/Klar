package org.klar.cli;

import java.io.IOException;
import java.nio.file.Path;

import org.klar.cli.utils.MultiCommandsUtils;
import org.klar.core.errors.KException;

import picocli.CommandLine.Command;

@Command(name = "clean", description = "Clean out/")
public class CleanCommand implements Runnable {

    @Override
    public void run() {
        try {
            MultiCommandsUtils.apagarDiretorio(Path.of("out"));
            System.out.println("✓ Clean successful");

        } catch (KException e) {
            System.out.println(e.format());
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Build error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
