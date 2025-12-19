# Klang Programming Language

Klang is a strictly explicit programming language designed to organize
complex, polyglot software systems.

It does not attempt to replace existing languages.
Instead, it orchestrates them.

## Core Principles

- Explicitness over convenience
- Determinism over magic
- Clarity over brevity
- Intent over convention

Klang treats language boundaries, types, and intent as first-class concepts.

---

## Strict by Default

Klang is **strict by default**.

Running:

    klang build

is equivalent to:

    klang build --rigor=strict

In strict mode:
- ambiguous identifiers are rejected
- public symbols must declare intent
- implicit behavior is forbidden
- semantic vagueness is treated as a compile-time error

This is intentional.

Klang believes that most long-term software problems are caused not by bugs,
but by ambiguity.

---

## Compilation Rigor Levels

Klang supports different rigor levels, but relaxing rules is always explicit.

### strict (default)
- full intent validation
- strict naming grammar
- no implicit conversions
- recommended for all production code

### explicit
- same as strict, but some violations emit warnings instead of errors

### lenient
- disables intent enforcement
- allows ambiguous naming
- intended only for experimentation

⚠ WARNING:
Lenient mode exists for learning and experimentation only.
It weakens the guarantees that define the Klang language.

---

## Why Klang Exists

Modern software systems are inherently polyglot.

Klang exists to:
- make cross-language boundaries explicit
- prevent semantic leakage between ecosystems
- reduce cognitive load in large systems
- enforce clarity where complexity is unavoidable

Klang does not optimize for speed of writing.
It optimizes for speed of understanding.

---

## Philosophy

Klang rejects:
- silent corrections
- implicit coercions
- ambiguous defaults
- convention without declaration

If something matters, it must be written down.

---

## Status

Klang is an experimental language.
Its design favors long-term correctness over short-term convenience.
