package org.klang.core.diagnostic;

/**
 * Sistema modular de cores para diagnósticos da Klang.
 * Baseado em camadas semânticas, não decoração.
 */
public final class DiagnosticColors {

    // ========================================
    // CAMADA 0 - TEXTO NEUTRO
    // ========================================
    
    /** Texto de código, exemplos, conteúdo geral */
    public static final String NEUTRAL = "\u001B[38;2;220;220;220m";
    
    /** Texto absolutamente neutro (sem cor) */
    public static final String PLAIN = "\u001B[0m";

    // ========================================
    // CAMADA 1 - ESTRUTURA (metadados)
    // ========================================
    
    /** Metadados: [K:E001], ERROR (Lexical), at file:line:col */
    public static final String STRUCTURE = "\u001B[38;2;150;150;150m";
    
    /** Números de linha (discretos, não competem com código) */
    public static final String LINE_NUMBER = "\u001B[38;2;90;90;90m";
    
    /** Separadores: pipes |, dois-pontos em labels (mais escuros ainda) */
    public static final String SEPARATOR = "\u001B[38;2;70;70;70m";

    // ========================================
    // CAMADA 2 - ERRO (foco visual)
    // ========================================
    
    /** Código do erro: [K:E001] - âncora visual forte */
    public static final String ERROR_CODE = "\u001B[38;2;220;50;47m";
    
    /** Nome do erro: InvalidCharacter - descrição, não foco */
    public static final String ERROR_NAME = "\u001B[38;2;200;200;200m";
    
    /** Caret ^, token inválido - foco absoluto */
    public static final String ERROR_HIGHLIGHT = "\u001B[38;2;220;50;47m";

    // ========================================
    // CAMADA 3 - AJUDA (resolução)
    // ========================================
    
    /** Fix, Example, Note - instrução construtiva */
    public static final String HELP = "\u001B[38;2;100;150;200m";
    
    /** Destaque em exemplos (opcional) */
    public static final String HELP_ACCENT = "\u001B[38;2;120;180;220m";

    // ========================================
    // RESET
    // ========================================
    
    /** Sempre use após aplicar cor */
    public static final String RESET = "\u001B[0m";

    // ========================================
    // MODO DE RENDERIZAÇÃO
    // ========================================
    
    private static RenderMode mode = RenderMode.AUTO;

    public enum RenderMode {
        /** Detecção automática baseada em terminal */
        AUTO,
        /** Força cores ANSI 24-bit */
        RICH,
        /** Sem cores (ASCII puro) */
        PLAIN,
        /** Modo de teste (mostra códigos ANSI) */
        DEBUG
    }

    public static void setMode(RenderMode newMode) {
        mode = newMode;
    }

    public static RenderMode getMode() {
        return mode;
    }

    // ========================================
    // API PÚBLICA - COLORIZAÇÃO
    // ========================================

    /**
     * Aplica cor semântica a um texto.
     * Respeita o modo de renderização configurado.
     */
    public static String colorize(String text, String color) {
        return switch (mode) {
            case PLAIN -> text;
            case DEBUG -> "[COLOR:" + color + "]" + text + "[/COLOR]";
            case RICH, AUTO -> color + text + RESET;
        };
    }

    /** Texto neutro (código, exemplos) */
    public static String neutral(String text) {
        return colorize(text, NEUTRAL);
    }

    /** Estrutura (metadados, números de linha) */
    public static String structure(String text) {
        return colorize(text, STRUCTURE);
    }

    /** Números de linha (discretos) */
    public static String lineNumber(String text) {
        return colorize(text, LINE_NUMBER);
    }

    /** Separadores (pipes, dois-pontos) */
    public static String separator(String text) {
        return colorize(text, SEPARATOR);
    }

    /** Erro primário (foco visual) */
    public static String error(String text) {
        return colorize(text, ERROR_HIGHLIGHT);
    }

    /** Código do erro [K:E001] - âncora visual */
    public static String errorCode(String text) {
        return colorize(text, ERROR_CODE);
    }

    /** Nome do erro - descrição */
    public static String errorName(String text) {
        return colorize(text, ERROR_NAME);
    }

    /** Erro secundário (contexto) */
    public static String errorDim(String text) {
        return colorize(text, ERROR_HIGHLIGHT);
    }

    /** Ajuda (fix, example) */
    public static String help(String text) {
        return colorize(text, HELP);
    }

    /** Destaque em ajuda */
    public static String helpAccent(String text) {
        return colorize(text, HELP_ACCENT);
    }

    // ========================================
    // DETECÇÃO DE SUPORTE
    // ========================================

    /**
     * Detecta se o terminal atual suporta cores ANSI.
     * Baseado em variáveis de ambiente e propriedades do sistema.
     */
    public static boolean isColorSupported() {
        // Windows 10+ com terminal moderno
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            String wtSession = System.getenv("WT_SESSION");
            return wtSession != null; // Windows Terminal
        }

        // Unix-like: verifica TERM
        String term = System.getenv("TERM");
        if (term == null) return false;

        return term.contains("color") 
            || term.contains("xterm") 
            || term.contains("screen")
            || term.equals("linux");
    }

    /**
     * Configura modo automaticamente baseado no ambiente.
     */
    public static void autoDetect() {
        if (isColorSupported()) {
            setMode(RenderMode.RICH);
        } else {
            setMode(RenderMode.PLAIN);
        }
    }

    // ========================================
    // BUILDER SEMÂNTICO (uso avançado)
    // ========================================

    /**
     * Builder para composição complexa de mensagens coloridas.
     */
    public static class Builder {
        private final StringBuilder sb = new StringBuilder();

        public Builder neutral(String text) {
            sb.append(DiagnosticColors.neutral(text));
            return this;
        }

        public Builder structure(String text) {
            sb.append(DiagnosticColors.structure(text));
            return this;
        }

        public Builder lineNumber(String text) {
            sb.append(DiagnosticColors.lineNumber(text));
            return this;
        }

        public Builder separator(String text) {
            sb.append(DiagnosticColors.separator(text));
            return this;
        }

        public Builder error(String text) {
            sb.append(DiagnosticColors.error(text));
            return this;
        }

        public Builder errorCode(String text) {
            sb.append(DiagnosticColors.errorCode(text));
            return this;
        }

        public Builder errorName(String text) {
            sb.append(DiagnosticColors.errorName(text));
            return this;
        }

        public Builder help(String text) {
            sb.append(DiagnosticColors.help(text));
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

    // ========================================
    // TESTES VISUAIS
    // ========================================

    /**
     * Imprime exemplo visual de todas as cores.
     * Útil para testar em diferentes terminais.
     */
    public static void printColorTest() {
        System.out.println("\n=== Klang Diagnostic Colors Test ===\n");
        
        System.out.println(neutral("NEUTRAL:    ") + neutral("This is neutral text (code, examples)"));
        System.out.println(structure("STRUCTURE:  ") + structure("This is structure (metadata)"));
        System.out.println(lineNumber("LINE_NUM:   ") + lineNumber("This is line number (very discrete)"));
        System.out.println(separator("SEPARATOR:  ") + separator("| : (pipes and colons - almost invisible)"));
        System.out.println(errorCode("ERROR_CODE: ") + errorCode("[K:E001] - visual anchor"));
        System.out.println(errorName("ERROR_NAME: ") + errorName("InvalidCharacter - description"));
        System.out.println(error("ERROR_HIGH: ") + error("^ caret and invalid token"));
        System.out.println(help("HELP:       ") + help("This is help (fix, example)"));
        System.out.println(helpAccent("HELP_ACC:   ") + helpAccent("This is help accent"));
        
        System.out.println("\n=== Combined Example ===\n");
        System.out.println(
            structure("[K:")
            + errorCode("E001")
            + structure("] ")
            + errorName("InvalidCharacter") 
            + "\n"
            + structure("ERROR (Lexical)")
            + "\n"
            + structure("at ")
            + neutral("examples/teste.k:")
            + structure("38:0") +
            "\n\n" +
            lineNumber("36") + separator(" | ") + neutral("$") +
            "\n" + separator("   | ") + error("^")
        );
        
        System.out.println("\nCurrent mode: " + mode);
    }
}