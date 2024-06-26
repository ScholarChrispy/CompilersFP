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
    | returnStmt { $expr = $returnStmt.expr; }
    ;

assignment returns [Expr expr]
    : ID '=' expression { $expr = new Assign($ID.text, $expression.expr); }
    | ID CREMENT { $expr = new Assign($ID.text,new Crement(new Deref($ID.text), $CREMENT.text)); }
    ;

expression returns [Expr expr]
    : '(' expression ')'                      { $expr = $expression.expr; }
    | numeric                                 { $expr = $numeric.expr; }
    | BOOLEAN                                 { $expr = new BooleanLiteral($BOOLEAN.text); }
    | STRING                                  { $expr = new StringLiteral($STRING.text); }
    | ID                                      { $expr = new Deref($ID.text); }
    | '-' '('? expression ')'?                { $expr = new Arith("-", new IntLiteral("0"), $expression.expr); }
    | 'print(' expression ')'                 { $expr = new Print($expression.expr); }
    | e1=expression COMPARES e2=expression    { $expr = new Cmp($COMPARES.text, $e1.expr, $e2.expr); }
    | e1=expression op=OPERATOR e2=expression { $expr = new Arith($op.text, $e1.expr, $e2.expr); }
    | funcCall                                { $expr = $funcCall.expr; }
    | sum                                     { $expr = $sum.expr; }
    | max                                     { $expr = $max.expr; }
    | min                                     { $expr = $min.expr; }
    | len                                     { $expr = $len.expr; }
    | n1=numeric'-'n2=numeric                 { $expr = new Arith("-", $n1.expr, $n2.expr); }
    | e1=expression'-'n2=numeric              { $expr = new Arith("-", $e1.expr, $n2.expr); }
    | n1=numeric'-'e2=expression              { $expr = new Arith("-", $n1.expr, $e2.expr); }
    | array                                   { $expr = $array.expr; }
    | arrayIndex                              { $expr = $arrayIndex.expr; }
    ;


ifcheck returns [Expr expr]
    : 'if' '(' expression ')' '{' block '}'                            { $expr = new Ifelse($expression.expr, $block.expr, new NoneExpr());}
    | 'if' '(' expression ')' '{' b1=block '}' 'else' '{' b2=block '}' { $expr = new Ifelse($expression.expr, $b1.expr, $b2.expr); }
    | 'if' '(' expression ')' '{' b1=block '}' 'else' ifcheck          { $expr = new Ifelse($expression.expr, $b1.expr, $ifcheck.expr); }
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
    
numeric returns [Expr expr]
    : INT   {$expr = new IntLiteral($INT.text); }
    | FLOAT {$expr = new FloatLiteral($FLOAT.text); }
    | DOUBLE {$expr = new DoubleLiteral($DOUBLE.text); }
    | '-' '('? INT ')'?  { $expr = new Arith("-", new IntLiteral("0"), new IntLiteral($INT.text)); }
    | '-' '('? FLOAT ')'?   { $expr = new Arith("-", new FloatLiteral("0.0"), new FloatLiteral($FLOAT.text)); }
    | '-' '('? DOUBLE ')'?   { $expr = new Arith("-", new DoubleLiteral("0.0"), new DoubleLiteral($DOUBLE.text)); }
    ;
    
sum returns [Expr expr]
    : 'sum(' ID ')'  { $expr = new DerefSum($ID.text); }  // dereferenced list
    | {List<Expr> args = new ArrayList<Expr>(); }
    'sum(' (e1=expression {args.add($e1.expr); } (','e2=expression { args.add($e2.expr); })*)? ')' { $expr = new Sum(args); }
    ;

max returns [Expr expr]
    : 'max(' ID ')'  { $expr = new DerefMax($ID.text); }  // dereferenced list
    | {List<Expr> args = new ArrayList<Expr>(); }
    'max(' (e1=expression {args.add($e1.expr); } (','e2=expression { args.add($e2.expr); })*)? ')' { $expr = new Max(args); }
    ;
    
min returns [Expr expr]
    : 'min(' ID ')'  { $expr = new DerefMin($ID.text); }  // dereferenced list
    | {List<Expr> args = new ArrayList<Expr>(); }
    'min(' (e1=expression {args.add($e1.expr); } (','e2=expression { args.add($e2.expr); })*)? ')' { $expr = new Min(args); }
    ;

len returns [Expr expr]
    : 'len(' ID ')'  { $expr = new DerefLen($ID.text); }  // dereferenced list
    | { List<Expr> args = new ArrayList<Expr>(); }
      'len(' (e1=expression {args.add($e1.expr); } (','e2=expression { args.add($e2.expr); })*)? ')' { $expr = new Len(args); }  // manual list
    ;
    
array returns [Expr expr]
    : { List<Expr> args = new ArrayList<Expr>(); }
    ('int[]' ID '=' '[' (e1=expression {args.add($e1.expr); } (','e2=expression { args.add($e2.expr); })*)? ']'{ $expr = new ArrayDef(new IntData(0), $ID.text, args); }
    |'double[]' ID '=' '[' (e1=expression {args.add($e1.expr); } (','e2=expression { args.add($e2.expr); })*)? ']'{ $expr = new ArrayDef(new DoubleData(0.0), $ID.text, args); }
    |'float[]' ID '=' '[' (e1=expression {args.add($e1.expr); } (','e2=expression { args.add($e2.expr); })*)? ']'{ $expr = new ArrayDef(new FloatData(0.0f), $ID.text, args); }
    |'String[]' ID '=' '[' (e1=expression {args.add($e1.expr); } (','e2=expression { args.add($e2.expr); })*)? ']'{ $expr = new ArrayDef(new StringData("a"), $ID.text, args); }
    |'bool[]' ID '=' '[' (e1=expression {args.add($e1.expr); } (','e2=expression { args.add($e2.expr); })*)? ']'{ $expr = new ArrayDef(new BooleanData(true), $ID.text, args); }
    )
    ;
    
arrayIndex returns [Expr expr]
    : ID'[' expression ']' { $expr = new ArrayIndex($ID.text, $expression.expr); }
    ;

returnStmt returns [Expr expr]
    : 'return' ';'  { $expr = new ReturnLiteral(new NoneExpr()); }
    | 'return' expression ';' { $expr = new ReturnLiteral($expression.expr); }
    ;

CREMENT    : ( '+++' | '---' );
OPERATOR   : ('+' | MINUS | '*' | '/' | '++' );
MINUS      : ('-');
COMPARES   : ('<' | '<=' | '>' | '>=' | '==' | '!=');

INT        : [0-9]+ ;
FLOAT      : ('0' .. '9')+ '.' ('0' .. '9')+ 'f';
DOUBLE     : ('0' .. '9')+ '.' ('0' .. '9')+ ;

STRING     : '"' .*? (~('\\')'"');
BOOLEAN    : 'true' | 'false';
ID         : [a-zA-Z_][a-zA-Z0-9_]*;

L_COMMENT  : '//' .*? '\n' -> skip;
COMMENT    : '/*' .*? '*/' -> skip;
WHITESPACE : [ \t\r\n] -> skip;