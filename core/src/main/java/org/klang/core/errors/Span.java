package org.klang.core.errors;

import java.util.Objects;

/**
 * Representa um intervalo (start..end) no arquivo.
 * startLine/column são 1-based.
 */
public final class Span {
    public final String fileName;
    public final int startLine;
    public final int startColumn;
    public final int endLine;
    public final int endColumn;

    public Span(String fileName, int startLine, int startColumn, int endLine, int endColumn) {
        this.fileName = Objects.requireNonNull(fileName, "fileName não pode ser null");

        if (startLine < 1) {
            throw new IllegalArgumentException("startLine deve ser >= 1");
        }
        if (startColumn < 1) {
            startColumn++;
        }
        if (endLine < startLine) {
            throw new IllegalArgumentException("endLine não pode ser menor que startLine");
        }
        if (endLine == startLine && endColumn < startColumn) {
            throw new IllegalArgumentException("endColumn não pode ser menor que startColumn na mesma linha");
        }

        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    public boolean isSingleLine() {
        return startLine == endLine;
    }

    public int length() {
        if (isSingleLine()) {
            return endColumn - startColumn + 1;
        }
        return -1; // multiline não tem length simples
    }

    @Override
    public String toString() {
        if (isSingleLine()) {
            return String.format("%s:%d:%d", fileName, startLine, startColumn);
        }
        return String.format("%s:%d:%d-%d:%d", fileName, startLine, startColumn, endLine, endColumn);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Span))
            return false;
        Span other = (Span) obj;
        return startLine == other.startLine &&
                startColumn == other.startColumn &&
                endLine == other.endLine &&
                endColumn == other.endColumn &&
                fileName.equals(other.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, startLine, startColumn, endLine, endColumn);
    }
}
