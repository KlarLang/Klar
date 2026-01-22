---
title: "@Use (Backend Target)"
sidebar_position: 9
---

# `@Use("java")`

Klar uses `@Use` to declare the backend target for a function.

Today:
- `@Use` is mandatory on **all** functions.
- Only `@Use("java")` is supported.

This is by design: explicitness over convention.

---

## Example

```kl
@Use("java")
public void main(){
    println("Hello!");

    return null;
}

```

---

## Missing annotation

If you omit `@Use`, Klar emits a syntax diagnostic like:

```text
[K:E113] MissingAnnotation
ERROR (SYNTAX)
...
Expected:
  '@Use'

```

---

## Unsupported target

If you specify an unsupported backend target:

```kl
@Use("python")
public void main(){
    return null;
}

```

Klar emits a backend diagnostic:

```text
[K:E400] UnknownBackendTarget
ERROR (BACKEND)
...
Cause:
  Unsupported backend target 'python'
Fix:
  Use a supported backend (currently only 'java' is supported)

```

---

## Roadmap

Planned: interoperability starting with **JSON interop**.

Additional backends exist as a long-term direction, not a current promise.
