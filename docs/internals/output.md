---
title: Output layout
sidebar_position: 3
---

# Output layout

Klar writes generated artifacts into `out/`.

Typical:
- `out/<name>.java`
- `out/<name>.class`

Current behavior:
- each function becomes a Java output unit
- `main` becomes the Java entrypoint: `public static void main(String[] args)`

This behavior may evolve before v1.0.0.
