---
title: Project layout
sidebar_position: 4
---

# Project layout

Klar can run any `.kl` file if you pass the correct path.

Loom can generate a project skeleton:

```bash
loom new my-project

```

Today, `loom new` is mostly a convenience/readme generator.

### Typical output:

```text
my-project/
  src/
    main.kl
  out/
  README.md

```

:::important

* **`kc run`** works with a file path, so you can place sources in any folder.
* **Output** is written to `out/` (relative to where you run the command).
:::
