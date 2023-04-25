
/*****************************************************************************
 *
 * af_yacc.mly   Analizador sintáctico para autómatas finitos.
 *
 *****************************************************************************/

%{
   open Conj;;
   open Auto;;
   open List;;
%}

%token PUNTO_Y_COMA EPSILON FIN 
%token <string> NOMBRE
%token ERROR

%start af_parse
%type <Auto.af> af_parse

%%

af_parse :
   nombres PUNTO_Y_COMA nombres PUNTO_Y_COMA NOMBRE PUNTO_Y_COMA nombres PUNTO_Y_COMA transiciones FIN
      { let
           q = Conjunto (rev (map (function x -> Estado x) $1))
        and
           t = Conjunto (rev (map (function x -> Terminal x) $3))
        and
           i = Estado $5
        and
           f = Conjunto (rev (map (function x -> Estado x) $7))
        in
           let rec aux = function
                [] -> true
              | (Arco_af (o, d, Terminal ""))::r ->
                   (pertenece o q) && (pertenece d q) && (aux r)
              | (Arco_af (o, d, s))::r ->
                   (pertenece o q) && (pertenece d q) && (pertenece s t) && (aux r)
           in
              if (interseccion (Conjunto $1) (Conjunto $3) = conjunto_vacio) &&
                 (pertenece i q) &&
                 (incluido f q) &&
                 (aux $9)
              then
                 Af (q, t, i, Conjunto (rev $9), f)
              else
                 raise Parsing.Parse_error
      }
;


nombres :
                    { [] }
 | nombres NOMBRE   { $2::$1 }
;


transiciones :
                             { [] }
 | transiciones transicion   { $2::$1 }
;


transicion :
   NOMBRE NOMBRE EPSILON PUNTO_Y_COMA  { Arco_af (Estado $1, Estado $2, Terminal "") }
 | NOMBRE NOMBRE NOMBRE PUNTO_Y_COMA   { Arco_af (Estado $1, Estado $2, Terminal $3) }
;


%%

