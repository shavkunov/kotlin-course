grammar Fun;

file
    :   block
    ;

block
    :   (statement)*
    ;

blockWithBraces
    :   '{' block '}'
    ;

statement
    :   function
    |   variable
    |   expression
    |   whileCycle
    |   ifStatement
    |   assignment
    |   returnStatement
;

function
    : 'fun' IDENTIFIER '(' parameterNames ')' blockWithBraces
    ;

variable
    : 'var' IDENTIFIER ('=' expression)?
    ;

parameterNames
    : (IDENTIFIER (',' IDENTIFIER)*)?
    ;

whileCycle
    : 'while' '(' expression ')' blockWithBraces
    ;

ifStatement
    : 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?
    ;

assignment
    : IDENTIFIER '=' expression
    ;

returnStatement
    : 'return' expression
    ;


expression
    : functionCall
    | IDENTIFIER
    | LITERAL
    | innerExpression

    | leftOp = expression operation = ('*' | '/' | '%') rightOp = expression
    | leftOp = expression operation = ('+' | '-') rightOp = expression
    | leftOp = expression operation = ('<' | '>' | '<=' | '>=') rightOp = expression
    | leftOp = expression operation = ('==' | '!=') rightOp = expression
    | leftOp = expression operation = '&&' rightOp = expression
    | leftOp = expression operation = '||' rightOp = expression
    ;

innerExpression
    : '(' expression ')'
    ;

functionCall
    : IDENTIFIER '(' arguments ')'
    ;

arguments
    : (expression (',' expression)*)?
    ;

IDENTIFIER
    : ([a-zA-Z] | '_') ([a-zA-Z] | '_' | [0-9])*
    ;

LITERAL
    :   '0'
    |   '-'? [1-9] [0-9]*
    ;

COMMENTARY
    : '//' ~[\r\n]* -> skip
    ;

WHITESPACE
    : (' ' | '\t' | '\r'| '\n') -> skip
    ;