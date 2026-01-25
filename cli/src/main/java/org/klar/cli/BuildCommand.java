package org.klar.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import org.klar.cli.utils.MultiCommandsUtils;

@Command(name = "build", description = "Build Klar source to Java")
public class BuildCommand implements Runnable {

    @Parameters(paramLabel = "FILE")
    private File file;

    @Option(names = { "--clean", "-c" }, description = "Limpa o diretorio out")
    private boolean clean = false;

    @Override
    public void run() {
        MultiCommandsUtils build = new MultiCommandsUtils(file, "build", clean);
        build.run();
    }
}