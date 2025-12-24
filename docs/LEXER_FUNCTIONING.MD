# Klang Token Specification (v1.0)

## Single-character
- LPAREN      (
- RPAREN      )
- LBRACE      {
- RBRACE      }
- LBRACKET    [
- RBRACKET    ]
- COMMA       ,
- SEMICOLON   ;
- COLON       :
- DOT         .

- PLUS        +
- MINUS       -
- MULTIPLY    *
- DIVISION    /
- REMAINDER   %

- ASSIGNMENT  =
- LT          <
- GT          >
- BANG        !

## Multi-character
- INCREMENT       ++
- DECREMENT       --
- POWER           **

- LTE             <=
- GTE             >=
- DOUBLEEQUAL     ==
- NOTEQUAL        !=

- AND             and
- OR              or
 
- ARROW           ->

## Literals
- NUMBER                  [0-9]+
- IDENTIFIER              [A-Za-z_][A-Za-z0-9_]*
- STRING_LITERAL          " ... "
- CHARACTER_LITERAL       'x'

## Keywords
- USE
- RETURN
- IF
- OTHERWISE
- BECAUSE
- AFTERALL
- FOR
- WHILE
- BREAK
- CONTINUE
- PUBLIC
- PROTECTED
- INTERNAL
- TRUE
- FALSE
- INTEGER
- DOUBLE
- BOOLEAN
- CHARACTER
- STRING
- VOID
- NEW

## Special
- AT              @
- EOF

## Note on Identifier Validation

The lexer recognizes identifiers generically.

Strict validation of identifier intent and naming rules
is performed at the parser and semantic analysis stages.

This separation is intentional.