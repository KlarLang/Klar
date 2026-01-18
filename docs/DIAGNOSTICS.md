# Klar Diagnostic Specification

This document defines the **official diagnostic codes, categories and semantics** used by the Klar language.

Diagnostics are a **public, stable API** of the language.
Their meaning MUST NOT change across versions.

Textual descriptions may evolve.
Diagnostic codes MUST remain immutable.

---

## Design Principles

* Every diagnostic represents **one and only one root cause**
* Diagnostics must never guess developer intent
* No diagnostic may be emitted without a known corrective action
* Multiple diagnostics may be emitted **only if they are independent**
* False or cascading diagnostics are forbidden

---

## Diagnostic Code Format

```
E<category><number>
```

Example:

```
E102
```

Where:

* `E` denotes an error diagnostic
* `<category>` defines the compilation phase
* `<number>` is a sequential, stable identifier

---

## Diagnostic Categories

| Range | Category                     |
| ----- | ---------------------------- |
| E0xx  | Lexical analysis             |
| E1xx  | Syntax parsing               |
| E2xx  | Types and semantic analysis  |
| E3xx  | Intent and rigor enforcement |
| E4xx  | Backend and target binding   |
| E5xx  | Cross-language integration   |

---

## 🔴 E0xx — Lexical Diagnostics (Fail-Fast)

Lexical diagnostics are **non-recoverable**.
Compilation MUST stop after the first lexical error.

---

### E001 — InvalidCharacter

**Category:** Lexical
**Severity:** Error
**Recoverable:** No

**Description:**
An invalid character was encountered during lexical analysis.

**Example:**

```
integer x = 10 @
               ^
```

**Cause:**
The lexer found a character that does not belong to the Klar lexical alphabet.

**Fix:**
Remove the invalid character or replace it with a valid token.

---

### E002 — UnterminatedStringLiteral

**Category:** Lexical
**Severity:** Error
**Recoverable:** No

**Description:**
A string literal was not properly terminated.

**Example:**

```
string name = "Klar
                    ^
```

**Cause:**
String literals must be explicitly closed.

**Fix:**
Add the closing string delimiter (`"`).

---

### E003 — InvalidNumericLiteral

**Category:** Lexical
**Severity:** Error
**Recoverable:** No

**Description:**
An invalid numeric literal format was detected.

**Example:**

```
integer x = 12.3.4;
                ^
```

**Cause:**
The numeric literal does not follow a valid Klar number format.

**Fix:**
Rewrite the literal using a valid numeric representation.

---

## 🟠 E1xx — Syntax Diagnostics (Parser)

Syntax diagnostics may be **recoverable** if safe parser synchronization is possible.

---

### E101 — UnexpectedToken

**Category:** Syntax
**Severity:** Error
**Recoverable:** Yes

**Description:**
A token was encountered that is not valid in the current syntactic context.

**Cause:**
The source code does not conform to the expected grammar structure.

**Fix:**
Reorganize the code to match the expected syntax.

---

### E102 — MissingStatementTerminator

**Category:** Syntax
**Severity:** Error
**Recoverable:** Yes

**Description:**
A statement terminator (`;`) is missing.

**Example:**

```
integer x = 10
```

**Cause:**
Klar requires explicit statement termination to ensure deterministic parsing and backend compatibility.

**Fix:**
Add a semicolon at the end of the statement.

**Example Fix:**

```
integer x = 10;
```

---

### E103 — UnclosedBlock

**Category:** Syntax
**Severity:** Error
**Recoverable:** Yes

**Description:**
A block was opened but not properly closed.

**Cause:**
A closing `}` is missing.

**Fix:**
Add the corresponding closing brace.

---

### E104 — InvalidFunctionSignature

**Category:** Syntax
**Severity:** Error
**Recoverable:** No

**Description:**
A function declaration does not follow the canonical Klar signature format.

**Cause:**
Function declarations must explicitly define visibility, return type and parameter list.

**Fix:**
Rewrite the function signature according to the language specification.

---

## 🟡 E2xx — Type and Semantic Diagnostics

Semantic diagnostics validate meaning, types and symbol resolution.

---

### E201 — UnknownTypeIdentifier

**Category:** Semantic
**Severity:** Error
**Recoverable:** Yes

**Description:**
A referenced type identifier does not exist.

**Example:**

```
Integer x = 10;
```

**Cause:**
Klar does not normalize or infer type identifiers.

**Fix:**
Use the canonical type name.

**Example Fix:**

```
integer x = 10;
```

---

### E202 — TypeMismatch

**Category:** Semantic
**Severity:** Error
**Recoverable:** Yes

**Description:**
Incompatible types were used in an assignment or expression.

**Cause:**
The operands involved do not share a compatible type relationship.

**Fix:**
Ensure that all operands and assigned values have compatible types.

---

### E203 — UndefinedSymbol

**Category:** Semantic
**Severity:** Error
**Recoverable:** Yes

**Description:**
A variable or function was referenced before being declared.

**Cause:**
The symbol is not visible in the current scope.

**Fix:**
Declare the symbol before its usage or correct the identifier.

---

### E204 — InvalidReturnType

**Category:** Semantic
**Severity:** Error
**Recoverable:** Yes

**Description:**
A returned value does not match the declared function return type.

**Cause:**
The function contract was violated.

**Fix:**
Return a value compatible with the declared return type.

---

### E205 — MissingInitializer

**Category:** Semantic
**Severity:** Error
**Recoverable:** No

**Description:**
A variable was declared without an explicit initializer.

**Example:**

```
integer x;
```

**Cause:**
Klar forbids uninitialized variables to prevent undefined state.

**Fix:**
Provide an explicit initializer.

**Example Fix:**

```
integer x = 0;
```

---

## 🟣 E3xx — Intent and Rigor Diagnostics

Intent diagnostics enforce semantic clarity and strict design rules.

---

### E301 — MissingVisibilityModifier

**Category:** Intent
**Severity:** Error
**Recoverable:** No

**Description:**
A function declaration lacks an explicit visibility modifier.

**Cause:**
Visibility must always be explicitly declared.

**Fix:**
Add a visibility modifier (`public`, `internal`, `protected`).

---

### E302 — InvalidPublicIdentifier

**Category:** Intent
**Severity:** Error
**Recoverable:** No

**Description:**
A public identifier violates strict naming and intent rules.

**Fix:**
Rename the identifier to comply with the intent-based naming grammar.

---

### E303 — ForbiddenIdentifier

**Category:** Intent
**Severity:** Error
**Recoverable:** No

**Description:**
A forbidden identifier was used in strict mode.

**Fix:**
Replace the identifier with a permitted name.

---

### E304 — ImplicitBehaviorNotAllowed

**Category:** Intent
**Severity:** Error
**Recoverable:** No

**Description:**
Implicit behavior was detected.

**Cause:**
Klar forbids implicit conversions or assumptions.

**Fix:**
Rewrite the code to be fully explicit.

---

## 🔵 E4xx — Backend and Target Diagnostics

---

### E401 — MissingBackendTarget

**Category:** Backend
**Severity:** Error
**Recoverable:** No

**Description:**
No backend target was specified for the symbol.

**Fix:**
Declare a backend target explicitly.

---

### E402 — InvalidBackendBinding

**Category:** Backend
**Severity:** Error
**Recoverable:** No

**Description:**
The specified backend is not recognized.

**Fix:**
Use a valid backend identifier.

---

### E403 — BackendTypeUnsupported

**Category:** Backend
**Severity:** Error
**Recoverable:** No

**Description:**
A Klar type cannot be mapped to the selected backend.

**Fix:**
Replace the type with a backend-compatible alternative.

---

### E404 — BackendConstraintViolation

**Category:** Backend
**Severity:** Error
**Recoverable:** No

**Description:**
A backend-specific technical constraint was violated.

**Fix:**
Adjust the code to comply with backend limitations.

---

## 🟢 E5xx — Cross-Language Integration Diagnostics

---

### E501 — CrossLanguageBoundaryViolation

**Category:** Integration
**Severity:** Error
**Recoverable:** No

**Description:**
A symbol violates a cross-language integration contract.

**Fix:**
Adjust the symbol to comply with the integration boundary.

---

### E502 — IncompatibleCallingConvention

**Category:** Integration
**Severity:** Error
**Recoverable:** No

**Description:**
An incompatible calling convention was detected between integrated languages.

**Fix:**
Align the calling conventions explicitly.

---

### E503 — TranspilationAborted

**Category:** Integration
**Severity:** Error
**Recoverable:** No

**Description:**
Transpilation was aborted due to an unrecoverable error.

---

## Stability Guarantee

Diagnostic codes defined in this document are **stable and permanent**.

Changing the meaning or category of an existing diagnostic code
constitutes a **breaking change** in the Klar language.
