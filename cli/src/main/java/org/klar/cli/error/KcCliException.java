package org.klar.cli.error;

import org.klar.cli.error.diagnostic.KcDiagnosticCode;

abstract public class KcCliException extends RuntimeException {
    protected final KcDiagnosticCode code;
    protected final String command;
    protected final String fix;
    private volatile String cachedMessage;

    public KcCliException(
        KcDiagnosticCode code,
        String command,
        String fix
    ){
        this.code = code;
        this.command = command;
        this.fix = fix;
    }

    protected final String[] getCommands(){
        return new String[]{"lex", "parse", "help", "gen-completion"};
    }

    @Override
    public final String getMessage() {
        if (cachedMessage == null){
            cachedMessage = format();
        }

        return cachedMessage;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this; 
    }

    public abstract String format();
}