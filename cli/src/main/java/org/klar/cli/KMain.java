package org.klar.cli;

import org.klar.cli.error.KlarExceptionHandler;
import org.klar.cli.error.KlarParameterExceptionHandler;
import org.klar.core.errors.KException;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "kc",
    description = "Klar CLI",
    mixinStandardHelpOptions = false,
    versionProvider = KVersionProvider.class,
    subcommands = {
        LexCommand.class,
        GenerateCompletion.class,
        HelpCommand.class,
        ParseCommand.class,
        BuildCommand.class,
        RunCommand.class
    }
)
public class KMain implements Runnable {

    @Option(
        names = { "-h", "--help" },
        description = "Show this help catalog"
    )
    boolean help = false;

    @Option(
        names = { "-V", "--version" },
        versionHelp = true,
        description = "Show Klar version"
    )
    boolean version = false;

    @Override
    public void run() {
        if (help) {
            new HelpCommand().run();
            return;
        }

        if (version) {
            System.out.println(new KVersionProvider().getVersion()[0]);
            return;
        }

        new HelpCommand().run();
    }

    public static void main(String[] args) {
        try {
            CommandLine cmd = new CommandLine(new KMain());
            
            cmd.setExecutionExceptionHandler(new KlarExceptionHandler());
            cmd.setParameterExceptionHandler(new KlarParameterExceptionHandler());
            
            int exitCode = cmd.execute(args);
            System.exit(exitCode);
            
        } catch (KException e) {
            System.err.println(e.format());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}