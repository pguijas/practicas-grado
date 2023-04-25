
/*****************************************************************************
 *
 * simb_yacc.mly   Analizador sintáctico para cadenas de símbolos.           
 *
 *****************************************************************************/

%{
   open Auto;;
   open List;;
%}

%token EPSILON NO_TERMINAL_ESPECIAL FIN ERROR
%token <string> TERMINAL

%start simbolo_parse
%type <Auto.simbolo list> simbolo_parse

%% 

simbolo_parse :
   FIN                      { [] }
 | cadena FIN               { rev $1 }
;

cadena :
 | EPSILON                       { [] }
 | NO_TERMINAL_ESPECIAL          { [No_terminal ""] }
 | TERMINAL                      { [Terminal $1] }
 | cadena EPSILON                { $1 }
 | cadena NO_TERMINAL_ESPECIAL   { (No_terminal "") :: $1 }
 | cadena TERMINAL               { (Terminal $2) :: $1 }
;

%%      

