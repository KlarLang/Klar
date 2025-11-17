# Klang Language Specification (v1.0.0 — Draft)

Klang is a statically-typed, minimal and deterministic language designed for
modular transpilation across multiple backends (Java, C, Python, Go, Rust).

This document defines the **syntax**, **lexical rules** and the **core language constructs**.
It does not define standard library behavior or backend-specific semantics.

---

## 1. Lexical Structure

### 1.1 Tokens
- Identifiers: `[A-Za-z_][A-Za-z0-9_]*`
- Literals:
  - integer: `10`, `42`
  - double: `3.14`
  - char: `'a'`
  - string: `"text"`
  - boolean: `true`, `false`

### 1.2 Comments
- Single-line: `// comment`
- Block: `/* comment */`

### 1.3 Blocks and Terminators
- Statements end with `;`
- Blocks are delimited by `{ ... }`

---

## 2. Types

### 2.1 Primitive Types
```

integer
double
character
boolean

```

### 2.2 Reference Types
```

String
Array<T>
Map<K,V>
Set<T>

```

Primitives do not accept null. Reference types may accept null.

---

## 3. Variable Declarations

```

integer x = 10;
double y = 3.14;
String name = "Klang";
Array<Integer> list = new Array();

```

Type must be explicit.

---

## 4. Operators

### 4.1 Arithmetic
```

+  -  *  /  %  **

```

`**` is syntactic sugar for exponentiation.

### 4.2 Comparison
```

> <  >=  <=  ==  !=

```

### 4.3 Boolean
```

!  &&  ||

```

---

## 5. Control Flow

### 5.1 Conditional

```

if (x > 0) {
    println("Positive");
} otherwise (x == 0) {
    println("Zero");
} afterall {
    println("Negative");
}

```

- `otherwise` → else-if  
- `afterall` → final else branch

### 5.2 Loops

Index loop:
```

for (integer i = 0; i < n; i++) {
    println(i);
}

```

Iterator loop:
```

for (integer value -> numbers) {
    println(value);
}

```

---

## 6. Functions

```

public static integer sum(integer a, integer b) {
    return a + b;
}

```

- Access modifiers follow Java semantics.
- Static functions belong to the module.
- Return type must be explicit.

---

## 7. @Use Annotation

`@Use(target)` defines the backend for transpilation.

Examples:

```

@Use("java")
public static integer sum(integer a, integer b) { ... }

@Use("c")
public static void main() { ... }

```

Rules:
- May be applied to file or function.
- Function-level annotation overrides file-level.
- Global default target = `"java"`.

No cascading semantics.

---

## 8. Collections (Language-Level)

These are language-defined types, not part of the standard library API.

### 8.1 Array
```

Array<Integer> nums = {1, 2, 3};
nums.append(4);
nums.get(0);

```

### 8.2 Map
```

Map<String,integer> ages = new Map();
ages.put("K", 25);

```

### 8.3 Set
```

Set<String> names = new Set();
names.add("Klang");

```

---

## 9. Error Handling

```

try {
// ...
} catch (Exception e) {
    println("Error: " + e);
}

```

The exception system is backend-dependent in v1.  
Custom exceptions will be defined in future versions.

---

## 10. Complete Example

```
@Use("c")
public static Array<Integer> build(integer n) {
    if (n <= 0) {
        println("Using fallback size 10");
        n = 10;
    }

    Array<Integer> nums = new Array(n);
    for (integer i = 0; i < n; i++) {
        nums.append(i);
    }

    return nums;
}

@Use("java")
public static void main(Array<String> args) {
    Array<Integer> nums = build(10);
    for (integer x -> nums) {
        println(x);
    }
}
```
