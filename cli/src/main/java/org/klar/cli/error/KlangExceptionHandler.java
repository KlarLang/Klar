package org.klar.cli.error;

import org.klar.core.errors.KException;

import picocli.CommandLine;

public class KlangExceptionHandler implements CommandLine.IExecutionExceptionHandler {

    @Override
    public int handleExecutionException(
            Exception ex, 
            CommandLine commandLine, 
            CommandLine.ParseResult parseResult) {
        
        // ===== CASO 1: Exceções customizadas do Klang (LexicalException, etc) =====
        if (ex instanceof KException) {
            KException kex = (KException) ex;
            System.err.println(kex.format());
            return 1;
        }
        
        // ===== CASO 2: Exceções de CLI customizadas =====
        if (ex instanceof KcCliException) {
            KcCliException cliEx = (KcCliException) ex;
            System.err.println(cliEx.format());
            return 1;
        }
        
        // ===== CASO 3: RuntimeException genéricas =====
        if (ex instanceof RuntimeException) {
            System.err.println("Runtime error: " + ex.getMessage());
            if (commandLine.getCommandSpec().usageMessage().showAtFileInUsageHelp()) {
                ex.printStackTrace(System.err);
            }
            return 1;
        }
        
        // ===== CASO 4: Outras exceções =====
        System.err.println("Unexpected error: " + ex.getMessage());
        ex.printStackTrace(System.err);
        return 1;
    }
}