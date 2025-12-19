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

- AND             &&
- OR              ||
 
- ARROW           ->

## Literals
- NUMBER          [0-9]+
- IDENTIFIER      [A-Za-z_][A-Za-z0-9_]*
- STRING          " ... "
- CHARACTER       'x'

## Keywords
- CLASS
- USE
- RETURN
- IF
- OTHERWISE
- AFTERALL
- FOR
- WHILE
- BREAK
- CONTINUE
- PUBLIC
- PRIVATE
- PROTECTED
- STATIC
- TRUE
- FALSE
- INTEGER
- DOUBLE
- BOOLEAN
- CHARACTER
- STRING
- VOID
- FRESH // at new


## Special
- AT              @
- EOF

## Note on Identifier Validation

The lexer recognizes identifiers generically.

Strict validation of identifier intent and naming rules
is performed at the parser and semantic analysis stages.

This separation is intentional.
