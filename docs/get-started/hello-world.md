---
title: Hello World
sidebar_position: 3
---

# Hello World

This guide assumes you already installed Klar via Loom.

---

## Create a project (optional)

`loom new` exists mainly to generate a simple project tree and README.
It is **not required** to run Klar.

```bash
loom new my-project
cd my-project

```

Loom will generate something like:

```text
.
├── out/
├── README.md
└── src/
    └── main.kl

```

## Your first Klar program

Create `src/main.kl`:

```java
@Use("java")
public void main(){
    println("Hello from Klar!");

    return null;
}

```

### Notes:

* `@Use("java")` is required
* `public` is required
* `void` functions still return explicitly (`return;` also works, but `return null;` is currently recommended)

## Run it

```bash
kc run src/main.kl

```

Expected output:

```text
Hello from Klar!

Program exited with code: 0

```

## Where output goes

Klar generates Java output in:

```text
out/

```

You will usually see both:

* `*.java`
* `*.class`

## If you get an error

Klar fails fast and prints a structured diagnostic.
Start with:

* the diagnostic code (`[K:Exxx]`)
* the category (LEXICAL, SYNTAX, SEMANTIC, BACKEND, CLI)
* the fix section
    
Further on, the Diagnostic Docs explain this in more detail.