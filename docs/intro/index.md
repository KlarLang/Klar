---
title: Introduction
sidebar_position: 1
---

# Klar

Klar is an **experimental transpiled language** designed around two non-negotiable goals:

1) **Extreme reliability through explicitness**  
2) **Native interoperability between languages** (starting with Java/JVM today)

Klar is not trying to compete with “general purpose” languages as a replacement.  
It is a language/toolchain experiment focused on making boundaries, assumptions, and intent explicit.

---

## What Klar refuses to do

Klar is built on a simple belief:

> Ambiguity is more dangerous than verbosity.

So Klar refuses to:
- Guess developer intent
- Introduce silent defaults
- “Help” by changing meaning implicitly
- Trade clarity for brevity

---

## Current status

Klar is **experimental** and **unstable before v1.0.0**.  
Breaking changes are expected.

What is **stable-ish** today:
- Lexer (most stable component)
- End-to-end `kc run` workflow (works and is usable)

What is **still unstable**:
- Parser
- Type checker
- Transpiler/codegen

---

## What works today (minimum reality)

Klar can:
- Lex, parse, type-check, transpile to Java, compile with `javac`, and run with `java`
- Produce structured diagnostics (LEXICAL / SYNTAX / SEMANTIC / BACKEND / CLI)
- Run a single `.kl` file at a time

Klar **does not** support yet:
- Arrays (planned)
- Modules/imports (keywords exist, but currently placebo)
- Structs / user-defined types
- Multi-file symbol linking (multiple files exist, but you cannot import/call functions across files)

---

## The one rule that surprises people

Klar is strict about “finishing what you started”:

- **Every function must have `@Use("java")`** (required on all methods by design)
- **Every function must return explicitly** (including `void`)
- **Control structures terminate explicitly** (`afterall` is mandatory; its body is optional)

This is not “missing features”. It is the design.

---

## Next steps

- Start here: **Get Started → Install**
- Then: **Hello World**
- Then: **Language Basics**
- If you care about the core idea: **Diagnostics**
