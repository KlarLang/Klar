---
title: CLI diagnostics
sidebar_position: 5
---

# CLI diagnostics

Klar’s CLI produces diagnostics too.

Example:

```text
[K:KC002] InvalidFileType
ERROR (CLI)
at input file

The file '<fileName>' is not a Klar source file.

Cause:
  Klar only processes files with the extensions '.kl' and '.klar'.

Fix:
  Rename the file or select a valid '.kl' or '.klar' source.

Example:
  kc run program.kl
```

If you see a CLI diagnostic, fix the CLI usage first — then rerun.