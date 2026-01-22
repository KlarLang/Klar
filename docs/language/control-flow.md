---
title: Control Flow
sidebar_position: 8
---

# Control Flow

Klar’s control flow is strict by design, aiming to eliminate ambiguity in nested structures.

### The Two Golden Rules of Klar Flow:
1. **No Shorthands:** Blocks always use `{ }`. Single-line `if` statements without braces are prohibited.
2. **Explicit Termination:** Conditional chains must always terminate with the `afterall` keyword.

---

## `if / otherwise / afterall`

### Basic `if`
Even a simple `if` requires the `afterall;` terminator.

```kl
if (condition) {
    // code to execute
} afterall;

```

### `if` with Fallback (Else)

In Klar, what other languages call `else` is handled by the `afterall` block.

```kl
if (condition) {
    // code
} afterall {
    // fallback logic
}

```

### The `otherwise` Clause (Else-if)

The `otherwise` keyword requires a `because` clause. This forces the developer to document the intent of the alternative branch directly in the syntax.

```kl
if (score > 90) {
    println("A");
} otherwise (score > 80) because "high performer threshold" {
    println("B");
} afterall {
    println("F");
}

```

:::info Syntax Rules

* **`because "..."`** is mandatory on every `otherwise` block.
* **`afterall`** is mandatory to close the `if` chain.
* The body of `afterall { ... }` is optional, but the keyword (or semicolon `afterall;`) is not.
:::

---

## Loops

### `while`

Klar supports standard `while` loops. Unlike the conditional chain, `while` does not require a special terminator beyond the closing brace.

```kl
while (condition) {
    // repeat logic
}

```

**Notes on Loops:**

* Braces `{}` are mandatory even for single statements.
* `while` does **not** use `afterall`.

---


:::info Syntax Rules
* `for` loops
* `foreach` / Iterator-based loops
* `switch` / Pattern matching

All of these do not exist at Klar.
:::