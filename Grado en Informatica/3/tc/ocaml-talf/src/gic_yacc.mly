
/*****************************************************************************
 *
 * gic_yacc.mly   Analizador sintáctico para gramáticas independientes del   
 *                contexto.                                                  
 *
 *****************************************************************************/

%{
   open Conj;;
   open Auto;;
   open List;;
%}

%token ENTONCES BARRA PUNTO_Y_COMA EPSILON FIN 
%token <string> NOMBRE
%token ERROR

%start gic_parse
%type <Auto.gic> gic_parse

%%

gic_parse :
   nombres PUNTO_Y_COMA nombres PUNTO_Y_COMA NOMBRE PUNTO_Y_COMA reglas FIN
      { let
           n = Conjunto (rev (map (function x -> No_terminal x) $1))
        and
           t = Conjunto (rev (map (function x -> Terminal x) $3))
        and
           s = No_terminal $5
        in
           let rec aux p = function
                [] -> p
              | (nt, sr)::r ->
                   if (mem nt $1) then
                      aux ((Regla_gic (No_terminal nt,
                                       map (function x -> 
                                               if mem x $1 then
                                                  No_terminal x
                                               else
                                                  if mem x $3 then
                                                     Terminal x
                                                  else
                                                     raise Parsing.Parse_error) sr)) :: p) r
                   else
                      raise Parsing.Parse_error
           in
              if (interseccion (Conjunto $1) (Conjunto $3) = conjunto_vacio) &&
                 (pertenece s n)
              then
                 Gic (n, t, Conjunto (aux [] $7), s)
              else
                 raise Parsing.Parse_error
      }
;


nombres :
                    { [] }
 | nombres NOMBRE   { $2::$1 }
;


reglas :
                  { [] }
 | reglas regla   { $2 @ $1 }
;


regla :
   NOMBRE ENTONCES partes_derechas PUNTO_Y_COMA
      { map (function x -> ($1, x)) $3 }
;


partes_derechas :
   EPSILON   { [[]] }
 | nombres   { [rev $1] }
 | partes_derechas BARRA EPSILON   { []::$1 }
 | partes_derechas BARRA nombres   { (rev $3)::$1 }
;

%%

