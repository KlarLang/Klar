package org.klar.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;

@Command(name = "run", description = "Transpile, compile and run a Klar program")
public class RunCommand implements Runnable {

    @Parameters(paramLabel = "FILE")
    private File file;

    @Option(names = { "--clean", "-c" }, description = "Limpa o diretorio out")
    private boolean clean = false;

    @Override
    public void run() {
        MultiCommandsUtils run = new MultiCommandsUtils(file, "run", clean);
        run.run();

    }
}