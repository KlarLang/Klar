# Klang Programming Language

Klang is an **experimental programming language** focused on **explicitness, rigor, and polyglot orchestration**.

It does **not** attempt to replace existing languages.  
Instead, it provides a disciplined way to **coordinate and integrate multiple languages** inside complex software systems.

Klang is designed for developers who value **clarity, determinism, and long-term maintainability** over convenience or brevity.

---

## Why Klang Exists

Modern software systems are inherently polyglot.

They combine:
- multiple languages
- multiple runtimes
- multiple semantic models

Most problems in large systems do **not** come from syntax errors.  
They come from **implicit assumptions**, **blurred boundaries**, and **semantic leakage** between languages.

Klang exists to:
- make cross-language boundaries **explicit**
- treat intent as a **first-class concept**
- reduce cognitive load in large, long-lived systems
- enforce clarity where complexity is unavoidable

---

## Core Principles

- **Explicitness over convenience**
- **Determinism over magic**
- **Clarity over brevity**
- **Intent over convention**

If something matters, it must be written down.

---

## Status

⚠ **Experimental**

Klang is under active development and **not production-ready**.

- Syntax and semantics may change
- Some design principles are not fully enforced yet
- The primary goal at this stage is feedback and iteration

---

## Current Capabilities

Implemented today:
- Lexer
- Parser
- Abstract Syntax Tree (AST)
- Foundational semantic analysis
- Structured and user-facing error reporting
- CLI tooling for inspection and debugging
- Java backend under active development

Not fully implemented yet:
- Full rigor level enforcement
- Intent-based naming validation
- Stable intermediate representation (IR)
- Multi-backend support

These features are part of the **design direction**, not guarantees of current behavior.

---

## Compilation Model

Klang follows a traditional but strictly separated compilation pipeline:

1. Lexical analysis
2. Parsing
3. Semantic validation
4. Intent validation (partial)
5. Backend target resolution
6. Code generation

Each stage is explicit and isolated.  
No stage is allowed to silently fix or infer developer intent.

---

## Strictness and Rigor

Strictness is a **core design goal** of Klang.

The language is designed to be **strict by default**, even where enforcement is still evolving.

Planned rigor levels:

### strict (planned default)
- Full intent validation
- Strict naming grammar
- No implicit conversions
- Intended for production systems

### explicit (planned)
- Same rules as strict
- Some violations emit warnings instead of errors

### lenient (planned)
- Disables intent enforcement
- Allows ambiguous naming
- Intended only for experimentation

⚠ Lenient mode weakens the guarantees that define Klang.

---

## Example (Early Syntax)

```k
integer x = 10;
integer y = 20;

public integer calculateSum(integer a, integer b) {
    return a + b;
}
```

CLI usage (debug-oriented at this stage):

```bash
kc lex example.k
kc parse example.k
```

---

What Klang Refuses to Do

Klang deliberately avoids:

implicit behavior

silent corrections

hidden coercions

convention without declaration


These are design decisions, not missing features.


---

Non-Goals

Klang is not:

a replacement for Java, Python, Rust, or C

a shortcut-focused scripting language

optimized for minimal syntax

designed for rapid prototyping



---

Legal Notice

Klang is an independent project.

It is not affiliated with, endorsed by, or sponsored by Oracle Corporation.
Java is a registered trademark of Oracle Corporation.


---

Roadmap (High-Level)

Stabilize core syntax and semantics

Define and freeze an intermediate representation (IR)

Complete rigor and intent enforcement

Stabilize the Java backend

Introduce a formal backend extension API

Explore additional backends (Python, C, Rust)


The roadmap is intentionally conservative.
Correctness takes precedence over speed.


---

Philosophy

Most long-term software failures are caused by ambiguity, not bugs.

Klang treats clarity as a structural requirement.

If the language feels demanding, it is working as intended.