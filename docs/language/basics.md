---
title: Language Basics
sidebar_position: 2
---

# Language Basics

This page describes the current state of the Klar language. Klar is strict by design, prioritizing explicitness over convenience to ensure code safety and clarity.

:::caution Strictness Policy
Klar requires:
- Explicit modifiers
- Explicit types
- Explicit returns
- Explicit control-flow termination
:::

---

## File Extension

The official extension for Klar source files is `.kl`(recommended) or `.klar`.

---

## Comments

Klar supports both single-line and block comments:

```kl
// This is a single-line comment

/*
  This is a 
  block comment
*/

```

---

## Types (Current)

Klar maps its primitive and reference types directly to Java:

| Klar Type | Java Equivalent | Category |
| --- | --- | --- |
| `integer` | `int` | Primitive |
| `double` | `double` | Primitive |
| `boolean` | `boolean` | Primitive |
| `character` | `char` | Primitive |
| `String` | `java.lang.String` | Reference |
| `void` | `void` | Return Only |
| `null` | `null` | Value Only |

---

## Variables

Variable declaration in Klar is strictly enforced. You **must** provide a type and an initial value; uninitialized variables are not permitted.

```kl
integer score = 0;
double price = 99.90;
String name = "Klar";
character initial = 'K';
boolean isActive = false;

```

---

## Functions

A function definition requires a specific structure to be valid:

1. `@Use("java")`: Mandatory in the current version.
2. **Access Modifier**: Explicit (currently `public` is the standard).
3. **Return Type**: Must be explicitly stated.
4. **Explicit Return**: All code paths must terminate with a return statement.

### The `main` Entrypoint

```kl
@Use("java")
public void main() {
    println("Hello World From Klar!");

    return null; 
}

```

### Return Rules

* All functions **must** return explicitly.
* In `void` functions, `return;` and `return null;` are currently accepted, though `return null;` is the current recommendation for consistency.
* Missing a return statement will trigger a **Semantic Diagnostic**.

---

## Operators

### Supported

* **Comparisons:** `==`, `!=`, `<`, `>`, `<=`, `>=`
* **Arithmetic:** `+`, `-`, `*`, `/`, `%`
* **Logical:** `and`, `or` (Note: `&&` and `||` are NOT supported)

### Intentionally Not Supported (Syntax Sugar)

* `**` (Power operator): Use standard library functions instead.
* Implicit numeric conversions: You cannot assign an `integer` to a `double` without an explicit operation.

---

## Built-ins

* `print(...)`
* `println(...)`
* `printf(...)` (Currently a placeholder)

---

## Roadmap: What is Missing

The following features are currently **not** implemented:

* Arrays and Collections
* Structs / User-defined types
* Module imports / Cross-file linking
* Type Casting

