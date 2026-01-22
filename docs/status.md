---
title: Status & Limitations
sidebar_position: 99
---

# Status & Limitations

Klar is currently in an **experimental state**. 

The current versioning line is **v0.x** (e.g., v0.13.0). This means the language is still evolving rapidly, and we prioritize correctness and architectural integrity over feature completeness.

:::danger Breaking Changes
Before version **1.0.0**, you should expect breaking changes in:
* Language Syntax
* Diagnostic Codes and Wording
* CLI Commands and Flags
:::

---

## Current Stability Matrix

We are transparent about what is ready for use and what is still under heavy construction:

| Component | Status | Description |
| :--- | :--- | :--- |
| **Lexer** | 🟢 Stable | Highly reliable tokenization of `.kl` files. |
| **Diagnostics** | 🟢 Stable | Consistent format and structured error reporting. |
| **CLI** | 🟡 Usable | `kc run` works for single-file compilation. |
| **Parser** | 🟡 Unstable | Core grammar is solid, but edge cases are being refined. |
| **Type Checker** | 🔴 Experimental | Basic type safety is enforced; advanced checks are pending. |
| **Transpiler** | 🔴 Experimental | Currently focuses strictly on Java output. |

---

## Known Hard Limitations

As of the current version, the following features are **not yet implemented**:

* **Data Structures:** Arrays and user-defined types (Structs).
* **Module System:** The `import` keyword exists as a placeholder but currently has no functionality (placebo).
* **Multi-file Linking:** Compilation is limited to one source file at a time; cross-file function calls are not supported.
* **Type Conversions:** No implicit numeric conversions or explicit casts.
* **Compilation Targets:** Only `@Use("java")` is supported.

---

## Roadmap (High-Level)

Our development is focused on the following milestones:

1.  **JSON Interoperability:** Enabling Klar to communicate with external data structures (our first real interop milestone).
2.  **Hardened Semantic Validation:** Implementing "intent/rigor" layers to ensure code behavior matches developer intent.
3.  **Multi-diagnostic Reporting:** Reporting multiple independent errors without cascading failures.
4.  **Backend Expansion:** Researching additional compilation targets beyond the JVM.

---

## How to Evaluate Klar

If you are evaluating Klar today, focus on its core philosophy rather than its feature set:
* **Strict Explicitness:** No hidden behaviors.
* **Deterministic Rules:** Predictable execution flow.
* **Diagnostic Clarity:** A compiler that talks to you, not at you.

---