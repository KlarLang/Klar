package org.klar.cli.error;

import org.klar.cli.error.diagnostic.KcDiagnosticCode;
import picocli.CommandLine;
import picocli.CommandLine.MissingParameterException;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.UnmatchedArgumentException;

public class KlarParameterExceptionHandler implements CommandLine.IParameterExceptionHandler {

    private static final String[] AVAILABLE_COMMANDS = {
            "lex", "parse", "help", "gen-completion"
    };

    @Override
    public int handleParseException(ParameterException ex, String[] args) {
        CommandLine cmd = ex.getCommandLine();

        // ===== CASO 1: Comando desconhecido (ex: kc j, kc pars) =====
        if (ex instanceof UnmatchedArgumentException) {
            UnmatchedArgumentException uae = (UnmatchedArgumentException) ex;

            if (!uae.getUnmatched().isEmpty()) {
                String unknownCommand = uae.getUnmatched().get(0);

                // Encontra o comando mais próximo
                String suggestion = findClosestCommand(unknownCommand, AVAILABLE_COMMANDS);

                KcUnknowCommandException kException = new KcUnknowCommandException(
                        KcDiagnosticCode.KC001, // Use o código apropriado
                        unknownCommand,
                        "Use 'kc --help' to see available commands.",
                        suggestion);

                // Imprime o erro formatado
                System.err.println(kException.format());

                // Retorna código de erro
                return 1;
            }
        }

        // ===== CASO 2: Arquivo faltando (ex: kc lex) =====
        if (ex instanceof MissingParameterException) {
            // Verifica se é o comando lex ou parse que precisa de arquivo
            String commandName = cmd.getCommandName();

            if ("lex".equals(commandName) || "parse".equals(commandName)) {
                KcMissingFileException kException = new KcMissingFileException(
                        KcDiagnosticCode.KC003, // Use o código apropriado
                        commandName,
                        "Provide a source file path.");

                System.err.println(kException.format());
                return 1;
            }
        }

        return 1;
    }

    private String findClosestCommand(String input, String[] commands) {
        int minDistance = Integer.MAX_VALUE;
        String closest = null;

        for (String cmd : commands) {
            int distance = levenshteinDistance(
                    input.toLowerCase(),
                    cmd.toLowerCase());

            if (distance < minDistance) {
                minDistance = distance;
                closest = cmd;
            }
        }

        return minDistance <= 3 ? closest : null;
    }

    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;

                dp[i][j] = Math.min(
                        Math.min(
                                dp[i - 1][j] + 1,
                                dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }

        return dp[a.length()][b.length()];
    }
}