package org.klar.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.AutoComplete;

@Command(
    name = "gen-completion",
    description = "Generate autocomplete script for bash/zsh"
)
public class GenerateCompletion implements Runnable {

    @Override
    public void run() {
        CommandLine cmd = new CommandLine(new KMain());
        String script = AutoComplete.bash("kc", cmd);
        System.out.println(script);
    }
}
