---
title: Format
sidebar_position: 2
---

# Diagnostic format

Typical format:

- `[K:E217]` code
- title (e.g., `UnresolvedSymbol`)
- severity + category (`ERROR (SEMANTIC)`)
- location (`file:line:col`)
- snippet + caret `^`
- Cause
- Fix
- sometimes Expected / Example / Note

---

## Example

```text
[K:E217] UnresolvedSymbol
ERROR (SEMANTIC)
at teste.kl:3:12

1 | @Use("java")
2 | public void main(){
3 |     println(a);
  |             ^

Cause:
  The variable 'a' does not exist

Fix:
  Remove it or create it
```