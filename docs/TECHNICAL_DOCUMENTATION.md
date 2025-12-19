# Klang Technical Documentation

This document describes the internal architecture of the Klang language.

---

## Design Goals

- Deterministic compilation
- Explicit semantics
- Strong separation of concerns
- No hidden behavior

---

## Compilation Pipeline

1. Lexical Analysis
2. Parsing
3. Semantic Validation
4. Intent Validation
5. Backend Target Resolution
6. Code Generation

Intent validation occurs before any backend-specific transformation.

---

## Intent Metadata

Each public symbol carries intent metadata:

- rigor level
- verb
- noun (if present)
- ambiguity flag

This metadata is stored in the AST.

---

## Rigor Enforcement

Rigor level affects:
- naming validation
- error severity
- code generation strictness

Strict mode:
- rejects ambiguity
- emits hard errors

Lenient mode:
- allows ambiguity
- annotates AST nodes
- emits warnings

---

## Error Philosophy

Errors must:
- describe what failed
- explain why it failed
- suggest a corrective action

Silent failure is forbidden.

---

## Why Klang Avoids Implicit Behavior

Implicit behavior leads to:
- hidden bugs
- fragile integrations
- unpredictable systems

Klang favors explicit declarations even when verbose.

This is a deliberate tradeoff.
