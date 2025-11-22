package org.klang.core.errors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Diagnostic {
    public final DiagnosticType type;
    public final String message;
    public Span primarySpan;
    private List<Span> secondarySpans;
    private List<Note> notes;

    public Diagnostic(DiagnosticType type, String message, Span primarySpan) {
        this.type = Objects.requireNonNull(type, "type não pode ser null");
        this.message = Objects.requireNonNull(message, "message não pode ser null");
        this.primarySpan = Objects.requireNonNull(primarySpan, "primarySpan não pode ser null");
        this.secondarySpans = new ArrayList<>();
        this.notes = new ArrayList<>();
    }

    public Diagnostic(DiagnosticType type, String message) {
        this.type = type;
        this.message = message;

    }

    public Diagnostic addSecondarySpan(Span span) {
        if (span != null) {
            this.secondarySpans.add(span);
        }

        return this;
    }

    public Diagnostic addNote(Note note) {
        if (note != null) {
            this.notes.add(note);
        }
        return this;
    }

    public List<Span> secondarySpans() {
        return Collections.unmodifiableList(secondarySpans);
    }

    public List<Note> notes() {
        return Collections.unmodifiableList(notes);
    }

    public boolean hasSecondarySpans() {
        return !secondarySpans.isEmpty();
    }

    public boolean hasNotes() {
        return !notes.isEmpty();
    }
}