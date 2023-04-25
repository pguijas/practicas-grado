
{
  open Parser;;
  exception Lexical_error;; 
}

rule token = parse
    [' ' '\t']  { token lexbuf }
  | "lambda"    { LAMBDA }
  | "L"         { LAMBDA }
  | "true"      { TRUE }
  | "false"     { FALSE }
  | "if"        { IF }
  | "then"      { THEN }
  | "else"      { ELSE }
  | "succ"      { SUCC }
  | "pred"      { PRED }
  | "iszero"    { ISZERO }
  | "let"       { LET }
  | "letrec"    { LETREC }
  | "in"        { IN }
  | "Bool"      { BOOL }
  | "Nat"       { NAT }
  | "String"    { STRING }
  | "*"         { TPAIR }
  | "list"         { LIST }
  | '('         { LPAREN }
  | ')'         { RPAREN }
  | '.'         { DOT }
  | '='         { EQ }
  | ':'         { COLON }
  | "->"        { ARROW }
  | '^'         { UP }
  | '{'         { LBRACKET }
  | ','         { COMMA }
  | '}'         { RBRACKET }
  | "nil"       { NIL }
  | "cons"      { CONS }
  | '['         { LCORCHETE }
  | ']'         { RCORCHETE }
  | "isnil"     { ISNIL }
  | "head"      { HEAD }
  | "tail"      { TAIL }

  | '"'         { QUOTE }
  | ['0'-'9']+  { INTV (int_of_string (Lexing.lexeme lexbuf)) }
  | ['a'-'z']['a'-'z' '_' '0'-'9']*
                { STRINGV ( Lexing.lexeme lexbuf ) }
  | '\"'[^';''\"''\'']*'\"'
                { STRINGT (
                  let s = Lexing.lexeme lexbuf
                  in String.sub s 1 ((String.length s)-2)
                ) }
  | eof         { EOF }
  | _           { raise Lexical_error } 