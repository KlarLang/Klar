package org.klang.core.errors;

import org.klang.core.diagnostics.DiagnosticCode;

public abstract class KException extends RuntimeException {

    protected final DiagnosticCode code;
    protected final SourceLocation location;
    protected final String[] contextLines;
    protected final String cause;
    protected final String fix;
    protected final String example;
    protected final String note;
    protected final int length;
    private volatile String cachedMessage;

    protected KException(
        DiagnosticCode code,
        SourceLocation location,
        String[] contextLines,
        String cause,
        String fix,
        String example,
        String note,
        int length
    ) {
        super(code.name);
        this.code = code;
        this.location = location;
        this.contextLines = contextLines;
        this.cause = cause;
        this.fix = fix;
        this.example = example;
        this.note = note;
        this.length = length;
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

    protected static String padLeft(int value, int width) {
        String s = String.valueOf(value);
        int padding = width - s.length();

        if (padding <= 0) {
            return s;
        }

        return " ".repeat(padding) + s;
    }

    public abstract String format();
}
