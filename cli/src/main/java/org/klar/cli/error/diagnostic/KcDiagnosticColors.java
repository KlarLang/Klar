package org.klar.cli.error.diagnostic;

/**
 * Sistema de cores para diagnósticos da CLI kc.
 *
 * Semântica:
 * - Âmbar = erro de uso (user fault)
 * - Azul = orientação / ajuda
 * - Cinza = estrutura
 * - Vermelho = erro interno (kc bug)
 */
public final class KcDiagnosticColors {

    // RESET
    public static final String RESET = "\u001B[0m";

    // TEXTO BASE
    public static final String NEUTRAL = "\u001B[38;2;220;220;220m";

    // ESTRUTURA / METADADOS
    public static final String STRUCTURE = "\u001B[38;2;150;150;150m";
    public static final String SEPARATOR = "\u001B[38;2;80;80;80m";

    // ERRO DE USO (user fault)
    // Âmbar / laranja suave
    public static final String CLI_ERROR_CODE = "\u001B[38;2;230;170;60m";
    public static final String CLI_ERROR_HIGHLIGHT = "\u001B[38;2;230;170;60m";

    // AJUDA / CORREÇÃO
    public static final String HELP = "\u001B[38;2;100;150;200m";
    public static final String HELP_ACCENT = "\u001B[38;2;120;180;220m";

    // ERRO INTERNO (bug do kc)
    public static final String INTERNAL_ERROR = "\u001B[38;2;220;50;47m";

    // RENDER MODE
    private static RenderMode mode = RenderMode.AUTO;

    public enum RenderMode {
        AUTO,
        RICH,
        PLAIN,
        DEBUG
    }

    public static void setMode(RenderMode newMode) {
        mode = newMode;
    }

    public static RenderMode getMode() {
        return mode;
    }

    public static void autoDetect() {
        if (isColorSupported()) {
            setMode(RenderMode.RICH);
        } else {
            setMode(RenderMode.PLAIN);
        }
    }

    private static boolean isColorSupported() {
        String term = System.getenv("TERM");
        if (term == null) return false;

        return term.contains("color")
            || term.contains("xterm")
            || term.contains("screen")
            || term.equals("linux");
    }

    // CORE API

    public static String colorize(String text, String color) {
        return switch (mode) {
            case PLAIN -> text;
            case DEBUG -> "[COLOR:" + color + "]" + text + "[/COLOR]";
            case RICH, AUTO -> color + text + RESET;
        };
    }

    public static String neutral(String text) {
        return colorize(text, NEUTRAL);
    }

    public static String structure(String text) {
        return colorize(text, STRUCTURE);
    }

    public static String separator(String text) {
        return colorize(text, SEPARATOR);
    }

    public static String cliErrorCode(String text) {
        return colorize(text, CLI_ERROR_CODE);
    }

    public static String cliError(String text) {
        return colorize(text, CLI_ERROR_HIGHLIGHT);
    }

    public static String help(String text) {
        return colorize(text, HELP);
    }

    public static String helpAccent(String text) {
        return colorize(text, HELP_ACCENT);
    }

    public static String internalError(String text) {
        return colorize(text, INTERNAL_ERROR);
    }

    // BUILDER

    public static class Builder {
        private final StringBuilder sb = new StringBuilder();

        public Builder neutral(String text) {
            sb.append(KcDiagnosticColors.neutral(text));
            return this;
        }

        public Builder structure(String text) {
            sb.append(KcDiagnosticColors.structure(text));
            return this;
        }

        public Builder separator(String text) {
            sb.append(KcDiagnosticColors.separator(text));
            return this;
        }

        public Builder cliErrorCode(String text) {
            sb.append(KcDiagnosticColors.cliErrorCode(text));
            return this;
        }

        public Builder cliError(String text) {
            sb.append(KcDiagnosticColors.cliError(text));
            return this;
        }

        public Builder help(String text) {
            sb.append(KcDiagnosticColors.help(text));
            return this;
        }

        public Builder helpAccent(String text) {
            sb.append(KcDiagnosticColors.helpAccent(text));
            return this;
        }

        public Builder internalError(String text) {
            sb.append(KcDiagnosticColors.internalError(text));
            return this;
        }

        public Builder raw(String text) {
            sb.append(text);
            return this;
        }

        public String build() {
            return sb.toString();
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
