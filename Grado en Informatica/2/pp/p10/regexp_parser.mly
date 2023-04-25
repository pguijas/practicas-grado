%{
open Regexp

let escaped_char = function
    'n' -> '\n'
|   't' -> '\t'
|   'r' -> '\r'
|   'b' -> '\b'
|    c  -> c
%}

%token PLUS
%token ASTERISK
%token LEFTPAR
%token RIGHTPAR
%token LEFTBRACKET
%token RIGHTBRACKET
%token BACKSLASH
%token QUESTION
%token MINUS
%token ACCENT
%token END
%token DOT
%token <char> CHAR
%token NOTOR
%token OR
%start main
%type <Regexp.regexp> main
%left OR
%left NOTOR


%%
main:
    exprs END   { $1 }
|   END         { empty }

exprs:
|   expr       { $1 }
|   expr exprs %prec NOTOR { concat $1 $2 }
|   exprs OR exprs { alt $1 $3 }

escaped_symbol:
|    BACKSLASH LEFTPAR                         { '(' }
|    BACKSLASH RIGHTPAR                        { ')' }
|    BACKSLASH BACKSLASH                       { '\\' }
|    BACKSLASH LEFTBRACKET                     { '[' }
|    BACKSLASH RIGHTBRACKET                    { ']' }
|    BACKSLASH ASTERISK                        { '*' }
|    BACKSLASH QUESTION                        { '?' }
|    BACKSLASH OR                              { '|' }
|    BACKSLASH CHAR                            { escaped_char $2 }

base_symbol:
    CHAR                { $1 }
|   escaped_symbol      { $1 }

symbol:
|   base_symbol                   { symbol_of_char $1 }
|   base_symbol MINUS base_symbol { symbol_of_range $1 $3 }

symbols:
|   symbol              { single $1 }
|   symbol symbols      { alt (single $1) $2 }

except_symbols:
|   symbol                { except $1 }
|   symbol except_symbols { all (except $1) $2 }

expr:
|    CHAR                                      { single (symbol_of_char $1) }
|    DOT                                       { any }
|    expr ASTERISK                             { repeat $1 }
|    expr PLUS                                 { concat $1 (repeat $1) }
|    expr QUESTION                             { alt empty_string $1 }
|    escaped_symbol                            { single (symbol_of_char $1) }
|    LEFTPAR exprs RIGHTPAR                    { $2 }
|    LEFTBRACKET ACCENT except_symbols RIGHTBRACKET { $3 }
|    LEFTBRACKET symbols RIGHTBRACKET          { $2 }
