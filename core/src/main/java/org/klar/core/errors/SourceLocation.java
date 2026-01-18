package org.klar.core.errors;

public record SourceLocation(
    String file,
    int line,
    int column
) {
    public SourceLocation(String file, int line, int column){
        this.file = file;
        this.line = line;
        this.column = column;
    }
}
