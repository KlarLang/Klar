# Klang Programming Language (K)

Klang is an **experimental programming language** focused on **explicitness, semantic rigor, and polyglot orchestration**.

It is **not designed to replace existing languages**.  
Instead, Klang explores how software systems can be made more reliable by **making intent, boundaries, and assumptions explicit**.

Klang is opinionated by design.

---

## TL;DR

- ✔ Compiles and runs real programs today  
- ✔ Generates Java code via an experimental backend  
- ✔ Has its own lexer, parser, AST, and semantic analysis  
- ✔ Produces structured, human-oriented error messages  
- ⚠ Not production-ready  
- ⚠ Syntax and semantics are still evolving  

---

## Why Klang Exists

Modern systems are inherently **polyglot**.

They combine:
- multiple languages
- multiple runtimes
- multiple semantic models

Most long-term failures do not come from syntax errors.  
They come from **implicit assumptions**, **unclear intent**, and **leaking abstractions**.

Klang exists to:
- make cross-language boundaries explicit
- treat intent as a first-class concept
- reduce ambiguity in long-lived systems
- prioritize clarity over convenience

---

## Core Principles

- **Explicitness over convenience**
- **Determinism over magic**
- **Clarity over brevity**
- **Intent over convention**

If something matters, it must be written down.

---

## Current Status

⚠ **Experimental**

Klang is under active development and intended for:
- learning
- experimentation
- language and compiler research

It is **not suitable for production use**.

---

## What Works Today

Implemented:
- Lexer
- Parser
- Abstract Syntax Tree (AST)
- Foundational semantic analysis
- User-facing, structured error diagnostics
- CLI tooling (`kc`)
- Java backend (experimental but functional)

Partially implemented:
- Intent validation
- Strictness enforcement
- Semantic error ordering

Planned (not yet stable):
- Intermediate Representation (IR)
- Multiple backends
- Configurable rigor levels

---

## Language Characteristics (Important)

Klang is intentionally strict and explicit.

Some notable rules:
- Every function must declare a backend using `@Use`
- All control structures must terminate explicitly (`afterall`)
- All functions must end with an explicit `return` (including `void`)
- Magic numbers may be rejected as a semantic violation

These are **design decisions**, not missing features.

---

## Example

```k
@Use("java")
public void main() {
    integer x = 10;
    integer y = 20;

    integer result = calculateSum(x, y);
    println(result);

    return null;
}

@Use("java")
public integer calculateSum(integer a, integer b) {
    return a + b;
}
```

---

## Installation (Early Tooling)

Klang uses **Loom**, its bootstrap and project manager.

```bash
git clone https://github.com/KlangLang/loom
cd loom
./install.sh   # or install.bat on Windows
```

Then:

```bash
loom install
loom new my_project
cd my_project
kc run src/main.k
```

---

## Compilation Model

Klang follows a strictly separated pipeline:

1. Lexical analysis  
2. Parsing  
3. Semantic validation  
4. Intent validation (partial)  
5. Backend resolution  
6. Code generation  

No stage is allowed to silently infer or correct developer intent.

---

## Non-Goals

Klang is not:
- a replacement for Java, Python, Rust, or C
- optimized for brevity or rapid prototyping
- a convenience-first scripting language

---

## Philosophy

Most long-term software failures are caused by ambiguity, not bugs.

Klang treats clarity as a structural requirement.

If the language feels demanding, it is working as intended.

---

## Legal Notice

Klang is an independent project.

It is not affiliated with, endorsed by, or sponsored by Oracle Corporation.  
Java is a registered trademark of Oracle Corporation.

---

## Roadmap (High-Level)

- Stabilize core syntax and semantics  
- Finalize semantic error ordering  
- Define and freeze an IR  
- Stabilize Java backend  
- Introduce backend extension API  
- Explore additional backends  

Correctness takes precedence over speed.
