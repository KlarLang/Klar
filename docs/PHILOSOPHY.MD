# Klar Syntax & Semantics Specification — vNext

> **Ambiguity is more dangerous than verbosity.**
>
> This document defines the updated syntactic and semantic rules of the Klar language,
> incorporating the latest anti-ambiguity and clarity-oriented design decisions.
>
> Klar is strict by default. All rules below apply under `--rigor=strict` unless stated otherwise.

---

## 1. Core Principles (Reaffirmed)

Klar is designed around the following non-negotiable principles:

* Explicitness over convenience
* Determinism over inference
* Intent over convention
* Readability for humans over compactness for machines

Klar rejects:

* implicit behavior
* hidden defaults
* silent fallbacks
* semantic shortcuts

If something matters, it must be written down.

---

## 2. Boolean Logic

### 2.1 Boolean Operators

Klar uses **lexical boolean operators**:

* `and`
* `or`

Symbolic operators are forbidden:

* `&&` ❌
* `||` ❌

There are no aliases. One concept has one representation.

---

### 2.2 Operator Precedence Enforcement

To prevent ambiguity, Klar enforces explicit grouping.

When mixing logical operators (`and`, `or`), parentheses are **mandatory**.

Invalid:

```k
if (a == b or c == d and e == f) { ... }
```

Valid:

```k
if ((firstValue == secondValue) or (thirdValue == fourValue and fifthValue == sixthValue)) {
    // ...
}
afterall;
```

---

## 3. Variable Declarations

### 3.1 Mandatory Initialization

All variables must be initialized at declaration time.

Invalid:

```k
integer x;
```

Valid:

```k
integer variableName = 0;
```

There are no implicit defaults.

---

## 4. Functions

### 4.1 Visibility Is Mandatory

All functions must declare visibility explicitly:

* `public`
* `protected`
* `internal`

Invalid:

```k
integer sum() { ... }
```

Valid:

```k
public integer sumNumbers() { 
    // ...
}
```

---

### 4.2 Explicit Returns

All functions must end with an explicit return.

#### Void functions

Void functions must return explicitly using `return void;`.

Invalid:

```k
public void sendEmail() {
    send();
}
```

Valid:

```k
public void sendEmail() {
    send();
    return void;
}
```

Implicit returns are forbidden.

---

### 4.3 Boolean Return Functions

Boolean-returning functions are allowed **only** when the domain is truly binary.

Rules:

* Public boolean functions must start with `is`, `has`, `can`, or `should`.
* The meaning of `true` and `false` must be semantically unambiguous.

Discouraged:

```k
public boolean isValidOrNot();
```

Preferred:

```k
public boolean isEmailValid();
```

When ambiguity exists, a result type is preferred:

```k
public ValidationResult validateEmail();
```

---

## 5. Conditional Expressions

### 5.1 Boolean-Only Conditions

All `if` conditions must evaluate to `boolean` explicitly.

Forbidden:

```k
if (x) { ... }
```

Required:

```k
if (isActive == true) { 
    // ...
}
```

Truthiness and implicit coercion do not exist.

---

### 5.2 Explicit Equality

Boolean negation shortcuts are forbidden.

Invalid:

```k
if (!isEnabled) { ... }
```

Valid:

```k
if (isEnabled == false) {
    // ...
}
```

---

## 6. Decision Structure (`if / otherwise / afterall`)

Klar treats decisions as **explicit, closable constructs**.

### 6.1 `if`

Defines the primary decision branch.

```k
if (condition == true) {
    // ...
}
```

---

### 6.2 `otherwise`

Defines an alternative conditional branch.

Syntax:

```k
otherwise (condition) because "reason" {
    // ...
}
```

Properties:

* `otherwise` is not an `if`
* it represents an alternative branch within the same decision
* the condition is mandatory

In strict mode, `because` is mandatory for `otherwise` branches.

---

### 6.3 `because`

`because` attaches **semantic justification metadata** to a decision branch.

* It does not affect runtime behavior
* It is preserved in the AST and IR
* It is emitted in transpiled code as structured comments or annotations

Generic reasons such as:

```k
because "unspecified"
```

emit a **warning**, asking the developer to confirm intent.

---

### 6.4 `afterall`

All decisions **must** terminate with `afterall`.

`afterall` represents the closure of the decision, not an alternative branch.

Two forms exist:

#### Declarative closure

```k
afterall;
```

#### Executable closure

```k
afterall {
    cleanUp();
}
```

This ensures all decisions are explicitly closed.

---

## 7. Side-Effect Restrictions

### 7.1 No Side Effects in Conditions

Expressions used in conditions must be side-effect free.

Invalid:

```k
if (incrementCounter() == true) { ... }
```

Valid:

```k
boolean incremented = incrementCounter();

if (incremented == true) { 
    // ... 
}
afterall;
```

---

### 7.2 No Assignment in Expressions

Assignment is a statement, not an expression.

Invalid:

```k
if (x = y) { ... }
```

---

## 8. Parameters and Magic Values

### 8.1 Boolean Parameters

Public functions must not accept boolean parameters.

Invalid:

```k
public void setMode(boolean fast)
```

Valid:

```k
public void setMode(ExecutionMode mode)
```

---

### 8.2 Magic Numbers

Numeric literals must be named unless trivially obvious (`0`, `1`).

Invalid:

```k
retry(3);
```

Valid:

```k
integer maxRetries = 3;
retry(maxRetries);
```

---

## 9. Transpilation Guarantees

Klar guarantees that:

* No explicit structure is lost during transpilation
* `because`, `otherwise`, and `afterall` are preserved as comments or annotations
* Generated code prioritizes readability over idiomatic compactness

The transpiler is a semantic translator, not a stylistic optimizer.

---

## 10. Closing Law

> Every decision must be closed.
> Every action must be named.
> Every value must mean something.

Klar is strict by default.
Clarity is not optional.
