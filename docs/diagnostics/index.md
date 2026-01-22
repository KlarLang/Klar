---
title: Diagnostics
sidebar_position: 3
---

# Diagnostics

In Klar, diagnostics are not merely "error messages." They are treated as a **structured interface** designed to guide the developer through the resolution process. 

Every diagnostic follows a consistent schema: `Code + Category + Span + Cause + Fix`.

:::warning Stability Note
Before version **1.0.0**, diagnostic codes and wording are subject to change. However, the underlying philosophy remains stable.
:::

---

## Diagnostic Categories

Klar classifies issues into five distinct categories to help you identify where the failure occurred in the pipeline:

| Category | Description |
| :--- | :--- |
| **LEXICAL** | Issues during tokenization (e.g., invalid characters). |
| **SYNTAX** | Violations of the language grammar (e.g., missing braces). |
| **SEMANTIC** | Logical errors in valid syntax (e.g., unresolved symbols). |
| **BACKEND** | Failures during code generation or target mapping. |
| **CLI** | Invalid usage of the compiler command line. |

---

## Anatomy of a Diagnostic

A typical Klar diagnostic provides the following context:

* **Code:** Unique identifier (e.g., `[K:E217]`).
* **Title:** Descriptive name of the error.
* **Severity + Category:** The impact and type.
* **Location:** File path, line, and column.
* **Visual Pointer:** A source snippet with a caret (`^`) pointing to the exact issue.
* **Cause/Fix:** Human-readable explanation and actionable steps.

---

## Real-World Examples

### 1. Semantic Error: Unresolved Symbol
Triggered when referencing a variable that hasn't been declared.

```text
[K:E217] UnresolvedSymbol
ERROR (SEMANTIC)
at teste.kl:3:12

1 | @Use("java")
2 | public void main(){
3 |     println(a);
  |             ^

Cause:
  The variable 'a' does not exist.

Fix:
  Declare the variable 'a' before use or remove the reference.

```

### 2. Syntax Error: Missing Annotation

Triggered when a function lacks the mandatory environment context.

```text
[K:E113] MissingAnnotation
ERROR (SYNTAX)
at teste.kl:1:0

1 | public void main(){
  | ^

Cause:
  Missing @Use annotation on function.

Fix:
  Annotate the function with @Use to specify the target context.

Expected:
  '@Use'

Example:
  @Use("java")
  public void myFunction() { ... }

```

### 3. Backend Error: Invalid Target

Triggered when an unsupported compilation target is specified.

```text
[K:E400] UnknowBackendTarget
ERROR (BACKEND)
at teste.kl:1:4

1 | @Use("python")
  |      ^^^^^^^^

Cause:
  Unsupported backend target 'python'.

Fix:
  Use a supported backend (currently only 'java' is supported).

Note:
  Klar currently supports only Java as a compilation target.

```

---

## Current Limitations

Currently, the Klar compiler **stops after the first diagnostic** per compilation. This is a deliberate choice to prevent "error cascading" and ensure the developer fixes the root cause first.

**Planned for future releases:**

* Multi-diagnostic reporting (for independent errors).
* JSON output format for IDE integration.