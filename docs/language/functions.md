---
title: Functions
sidebar_position: 5
---

# Functions

A Klar function requires:

1. `@Use("java")` (mandatory today)
2. An explicit access modifier (currently only `public` is fully supported)
3. A return type
4. A name
5. Parameters (optional)
6. An explicit return statement

---

## Example: main

```kl
@Use("java")
public void main(){
    println("Hello World From Klar!");

    return null;
}

```

## Example: typed function

```kl
@Use("java")
public integer sumOf(integer a, integer b){
    return a + b;
}

```

---

## Important restrictions (today)

* **`@Use("java")`** is mandatory on all methods (design choice).
* **`protected` / `internal**` may exist as keywords, but only `public` is guaranteed to transpile correctly today.