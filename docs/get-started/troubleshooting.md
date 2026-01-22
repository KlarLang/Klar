---
title: Troubleshooting
sidebar_position: 6
---

# Troubleshooting

## `kc: command not found`

Your shell may not have reloaded the PATH update.

Try:

```bash
source ~/.bashrc

```

*(or your shell config)*

---

## Invalid file type (CLI error)

Klar only processes `.kl` or `.klar` (documented standard).

Example:

```bash
kc run a

```

Typical output:

```text
[K:KC002] InvalidFileType
ERROR (CLI)
at input file

The file 'a' is not a Klar source file.

Cause:
  Klar only processes files with the extensions '.kl' and '.klar'.

Fix:
  Rename the file or select a valid '.kl' or '.klar' source.

Example:
  kc run program.kl
```

---

## Java not found

Klar runs via a java process internally.
Make sure `java` is available in your PATH:

```bash
java -version

```