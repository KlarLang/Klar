---
title: Codes (current examples)
sidebar_position: 4
---

# Diagnostic codes

Before v1.0.0, codes are not guaranteed stable.  
Still, documenting current codes helps users navigate errors faster.

---

## CLI

### `KC002` — InvalidFileType
Triggered when the file extension is not supported.

---

## LEXICAL

### `E000` — ExpectedCharacter
Example: annotation target must be a string.

---

## SYNTAX

### `E113` — MissingAnnotation
Missing `@Use` on a function.

---

## SEMANTIC

### `E102` — MissingSemicolon
Missing `;` at end of statement.

### `E205` — MissingReturnStatement
Even `void` functions must return explicitly.

### `E217` — UnresolvedSymbol
Used an identifier that does not exist in scope.

---

## BACKEND

### `E400` — UnknowBackendTarget
Unsupported backend target (today only `java` is supported).

---

## Note

This list will expand.  
If you want a “stable contract” for diagnostics, that will be a v1.0+ goal.
