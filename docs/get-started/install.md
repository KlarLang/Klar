---
title: Install
sidebar_position: 2
---

# Install Klar (via loom)

Klar is installed using **Loom**, the Klar project manager.

## Prerequisites

- Java 21 installed (Klar runs via a `java` process under the hood)

That’s it.

---

## Install Loom

1) Clone Loom:

```bash
git clone https://github.com/KlarLang/loom
cd loom
```

Run the installer for your OS:

Linux
```bash
./install.sh
```
Windows
```bat
install.bat
```

Install Klar:

```bash
loom install
```

This installs:

klar.jar into ~/.klar/active/

a launcher script kc into ~/.klar/bin/

Loom also configures your environment, but you may need to reload your shell.

If your terminal doesn’t recognize kc immediately, run:

```bash
source ~/.bashrc
```
(or the relevant shell config you use)

Install a specific Klar version (optional)
```bash
loom install v0.13.0
```
If no version is provided, Loom installs the latest available release.

Verify installation
```bash
kc --version
kc --help
```
If kc --version works, you’re ready.