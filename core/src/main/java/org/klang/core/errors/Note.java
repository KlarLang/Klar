package org.klang.core.errors;

import java.util.Objects;

public final class Note {
    public final String message;
    public final Span span; // pode ser null

    public Note(String message) {
        this(message, null);
    }

    public Note(String message, Span span) {
        this.message = Objects.requireNonNull(message, "message não pode ser null");
        this.span = span;
    }

    public boolean hasSpan() {
        return span != null;
    }

    @Override
    public String toString() {
        if (span != null) {
            return "note: " + message + " [" + span + "]";
        }
        return "note: " + message;
    }
}