# Klar Programming Language

Klar is an **experimental programming language** focused on **explicitness, rigor, and polyglot orchestration**.

It is **not designed to replace existing languages**.  
Instead, Klang explores how software systems can be made more reliable by **making intent, boundaries, and assumptions explicit**.

Klar is designed for developers who value **clarity, determinism, and long-term maintainability** over convenience or brevity.

---

## Why Klar Exists

Modern systems are inherently **polyglot**.

They combine:
- multiple languages
- multiple runtimes
- multiple semantic models

Most long-term failures do not come from syntax errors.  
They come from **implicit assumptions**, **unclear intent**, and **leaking abstractions**.

Klar exists to:
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

## Current Status

⚠ **Experimental**

Klar is under active development and **not production-ready**.

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

Klar follows a traditional but strictly separated compilation pipeline:

Some notable rules:
- Every function must declare a backend using `@Use`
- All control structures must terminate explicitly (`afterall`)
- All functions must end with an explicit `return` (including `void`)
- Magic numbers may be rejected as a semantic violation

These are **design decisions**, not missing features.

---

## Strictness and Rigor

Strictness is a **core design goal** of Klar.

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

⚠ Lenient mode weakens the guarantees that define Klar.

---

## Example (Early Syntax)

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

Klar uses **Loom**, its bootstrap and project manager.

```bash
git clone https://github.com/KlarLang/loom
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

What Klar Refuses to Do

Klar deliberately avoids:

1. Lexical analysis  
2. Parsing  
3. Semantic validation  
4. Intent validation (partial)  
5. Backend resolution  
6. Code generation  

No stage is allowed to silently infer or correct developer intent.

---

## Non-Goals

Klar is not:

a replacement for Java, Python, Rust, or C

a shortcut-focused scripting language

optimized for minimal syntax

designed for rapid prototyping



---

Legal Notice

Klar is an independent project.

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

Klar treats clarity as a structural requirement.

If the language feels demanding, it is working as intended.

---

## Legal Notice

Klar is an independent project.

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
