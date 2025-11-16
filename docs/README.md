# Klang — A Polyglot Programming Language

Klang é uma linguagem experimental focada em **clareza**, **consistência semântica** e **interoperabilidade real entre linguagens**.  
Criada por ~K', a Klang busca oferecer uma sintaxe moderna e previsível, inspirada em Java e Python, mas com decisões próprias orientadas à legibilidade e ao design sólido de compiladores.

---

## 🚀 Visão

A Klang nasce com um propósito claro:

- Fornecer uma **linguagem poliglota verdadeira**, capaz de se integrar diretamente com Java, Python, Go, Rust e C.  
- Criar um ambiente onde cada arquivo ou módulo pode optar pela **linguagem-alvo mais eficiente**, sem perder coesão sintática.  
- Construir uma base simples, minimalista e expressiva, que permita evolução saudável do compilador e da linguagem.

> Klang não tenta competir com linguagens consolidadas.  
> Ela existe para interligá-las.

---

## ⚙️ Filosofia

Klang é guiada por quatro princípios:

- **Legibilidade humana primeiro**  
  Sintaxe limpa, mínima e sem ruído.

- **Determinismo semântico**  
  Nada ambíguo; tudo previsível.

- **Modularidade forte**  
  Arquivos transpiláveis individualmente.

- **Interop como feature nativa**  
  A linguagem não “imita” outras — ela as usa diretamente.

---

## 💡 Exemplo de Sintaxe

```k
if (x > 0) {
    println("Positivo");
} afterall {
    println("Negativo ou zero");
}
````

---

## 🧩 Estrutura do Projeto

```
klang/
├── docs/        # Documentação e especificação da linguagem
├── src/         # Lexer, parser, AST, compiler core
├── examples/    # Exemplos oficiais
├── tests/       # Testes de unidade e integração
└── LICENSE      # GPL-3.0
```

---

## 📌 Roadmap Rápido

* [ ] Lexer funcional e estável
* [ ] Parser recursivo + AST
* [ ] Primeira versão do transpiler Java
* [ ] CLI para build/run
* [ ] Documentação v1 da sintaxe
* [ ] Módulos interoperáveis
* [ ] Runtime básico

A prioridade atual é consolidar **lexer → parser → AST**.

---

## 📄 Licença

Klang é distribuída sob a **Apache-2.0 license**.
Você pode usar, modificar e redistribuir, desde que preserve a mesma licença.

---

## 🤝 Contribuindo

Contribuições são bem-vindas — especialmente em compiladores, estruturas de AST, ferramentas CLI e documentação.

1. Faça um fork
2. Crie uma branch (`feature/nome`)
3. Envie um PR

Se quiser debater ideias antes, abra uma issue.

---

## 📬 Autor

Criado e mantido por **~K' (Lucas Paulino da Silva)**
💻 Klang © 2025 — Open Source para sempre

```
