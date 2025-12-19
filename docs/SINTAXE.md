# Klang Syntax Specification

This document defines the syntax and structural rules of the Klang language.

Klang syntax is intentionally explicit.
Clarity and predictability take precedence over brevity.

---

## Compilation Rigor

Klang enforces strict compilation rules by default.

Default behavior:

    --rigor=strict

Relaxing rigor requires explicit opt-in.

---

## Identifiers

### General Identifier Rules

- camelCase is mandatory
- underscores (`_`) are forbidden in public symbols
- underscores are allowed only in:
  - generated code
  - backend-specific glue code
  - internal compiler artifacts

---

## Strict Naming Rules (Public Symbols)

In `strict` mode, all public symbols must follow intent-based naming rules.

### Public Functions

Public function names must follow this grammar:

    Verb [Noun]

Examples:
- calculateChecksum
- validateUserData
- persistSession

Invalid examples:
- process
- run
- doStuff
- handle

---

### Allowed Verbs (Strict Mode)

The following verbs are permitted in strict mode:

- calculate, compute, build, create, generate
- validate, parse, serialize, deserialize
- persist, load, fetch, store
- send, dispatch, emit
- open, close, read, write
- convert, transform
- compare, check
- initialize, finalize

This list is controlled by the language specification.

---

### Functions With Return Values

Functions that return a value:
- must not start with generic verbs
- must describe the produced result

Invalid:
- doCalculation
- processData

---

### Void Functions

Void functions must imply side effects through their verb.

Valid:
- persistUser
- sendMessage
- writeFile

---

## Private and Local Functions

Private and local functions:
- may use short names
- may use abbreviations
- must not be exported
- must not cross module boundaries

---

## Variables

### Public Variables

Public variables must use descriptive nouns:

Examples:
- UserRepository
- SessionConfig

Invalid:
- data
- tmp
- x

---

### Local Variables

Local variables are less restricted, but:

- single-letter names are allowed only in loops
- generic names emit warnings in strict mode

---

## Types

Types must be named using PascalCase nouns.

Valid:
- UserData
- HttpRequest
- ChecksumResult

Invalid:
- data
- thing
- object

---

## Blacklisted Identifiers

The following identifiers are forbidden in strict mode:

- do
- stuff
- thing
- data
- process
- handle
- run
- exec
- work
- test
- misc
- util
- helper
- manager

These identifiers are rejected regardless of scope.

---

## Design Note

These rules exist to prevent ambiguity, not to enforce style.

Klang treats clarity as a structural requirement.
