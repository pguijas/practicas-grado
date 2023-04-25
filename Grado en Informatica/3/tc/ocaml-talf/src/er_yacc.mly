
/*****************************************************************************
 *
 * er_yacc.mly   Analizador sintáctico para expresiones regulares.           
 *
 *****************************************************************************/

%{
   open Auto;;
%}

%token PARENTESIS_IZQ PARENTESIS_DCH
%token BARRA PUNTO ASTERISCO
%token EPSILON VACIO FIN
%token <string> TERMINAL
%token ERROR

%start er_parse
%type <Auto.er> er_parse

%left BARRA           /* precedencia minima */
%left PUNTO
%nonassoc ASTERISCO   /* precedencia maxima */

%% 

er_parse :
   expr FIN                             { $1 }
;

expr : 
   VACIO                                { Vacio }
 | EPSILON                              { Constante (Terminal "") }
 | TERMINAL                             { Constante (Terminal $1) }
 | expr BARRA expr                      { Union ($1, $3) }
 | expr PUNTO expr                      { Concatenacion ($1, $3) }
 | expr ASTERISCO                       { Repeticion $1 }
 | PARENTESIS_IZQ expr PARENTESIS_DCH   { $2 }
;

%%      

