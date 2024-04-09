grammar PL;

@header {
import backend.*;
}

@members {
}

program returns [Expr expr]
    : block EOF { $expr = $block.expr; };   
    
block returns [Expr expr]
    : {List<Expr> statements = new ArrayList<Expr>(); } 
    (statement { statements.add($statement.expr);})* 
    {$expr = new Block(statements);}
    ;
               
statement returns [Expr expr]  
    : assignment { $expr = $assignment.expr; }';'
    | expression { $expr = $expression.expr; } ';'
    | ifcheck    { $expr = $ifcheck.expr; }
    | loop       { $expr = $loop.expr;}
    | funcDef    { $expr = $funcDef.expr;}
    ;

assignment returns [Expr expr]
    : ID '=' expression { $expr = new Assign($ID.text, $expression.expr); }
    | ID CREMENT { $expr = new Assign($ID.text,new Crement(new Deref($ID.text), $CREMENT.text)); }
    ;

expression returns [Expr expr]
    : '(' expression ')'                      { $expr = $expression.expr; }
    | NUMERIC                                 { $expr = new IntLiteral($NUMERIC.text); }
    | STRING                                  { $expr = new StringLiteral($STRING.text); }
    | ID                                      { $expr = new Deref($ID.text); }
    | 'print(' expression ')'                 { $expr = new Print($expression.expr); }
    | e1=expression COMPARES e2=expression    { $expr = new Cmp($COMPARES.text, $e1.expr, $e2.expr); }
    | e1=expression op=OPERATOR e2=expression { $expr = new Arith($op.text, $e1.expr, $e2.expr); }
    | funcCall                                { $expr = $funcCall.expr; }
    ;


ifcheck returns [Expr expr]
    : 'if' '(' expression ')' '{' block '}'                            { $expr = new Ifelse($expression.expr, $block.expr, new NoneExpr());}
    | 'if' '(' expression ')' '{' b1=block '}' 'else' '{' b2=block '}' { $expr = new Ifelse($expression.expr, $b1.expr, $b2.expr); }
    ;
    
loop returns [Expr expr]
    : 'while' '(' cond=expression ')' '{' block '}' { $expr = new Loop(new NoneExpr(), $cond.expr, $block.expr, new NoneExpr()); }
    | 'for' '(' a1=assignment ';' e1=expression ';' a2=assignment ')' '{' block '}' { $expr = new Loop($a1.expr, $e1.expr, $block.expr, $a2.expr); }
    | 'for' '(' ID 'in' e1=expression '..' e2=expression ')' '{' block '}' { $expr = new Loop(new Assign($ID.text, $e1.expr), new Cmp("<=", new Deref($ID.text), $e2.expr), $block.expr, new Assign($ID.text, new Crement(new Deref($ID.text), "+++"))); }
    ;
    
funcDef returns [Expr expr]
    : {List<String> args = new ArrayList<String>(); }
    'function' ID '(' (id1=ID { args.add($id1.text); }(','id2=ID { args.add($id2.text); })*)? ')' '{' block '}' { $expr = new FuncDef($ID.text, args, $block.expr); }
    ;


funcCall returns [Expr expr]
    : {List<Expr> args = new ArrayList<Expr>(); } 
    ID '(' (e1=expression { args.add($e1.expr); }(','e2=expression { args.add($e2.expr); })*)? ')' { $expr = new FuncCall($ID.text, args); }
    ;


CREMENT    : ( '+++' | '---' );
OPERATOR   : ('+' | '-' | '*' | '/' | '++' );
COMPARES   : ('<' | '<=' | '>' | '>=' | '==' | '!=');
NUMERIC    : [0-9]+ | ([0-9]*'.'[0-9]+);
STRING     : '"' .*? (~('\\')'"');
ID         : [a-zA-Z_][a-zA-Z0-9_]*;
COMMENT    : '/*' .*? '*/';    
WHITESPACE : [ \t\r\n] -> skip;
