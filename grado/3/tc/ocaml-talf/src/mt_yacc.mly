
/*****************************************************************************
 *
 * mt_yacc.mly   Analizador sintáctico para máquinas de Turing.
 *
 *****************************************************************************/

%{
   open Conj;;
   open Auto;;
   open List;;
%}

%token PUNTO_Y_COMA EPSILON BLANCO IZQUIERDA DERECHA FIN 
%token <string> NOMBRE
%token <string> ESTADO
%token ERROR

%start mt_parse
%type <Auto.mt> mt_parse

%%

mt_parse :
   nombres PUNTO_Y_COMA nombres PUNTO_Y_COMA nombres_b PUNTO_Y_COMA NOMBRE PUNTO_Y_COMA 
   nombres PUNTO_Y_COMA transiciones FIN
      { let
           q = Conjunto (rev (map (function x -> Estado x) $1))
        and
           t = Conjunto (rev (map (function x -> Terminal x) $3))
        and
           g = let
                  nt = diferencia (Conjunto $5) (Conjunto $3)
               in
                  Conjunto (rev (map (function x -> 
                                         if pertenece x nt then 
                                            No_terminal x 
                                         else 
                                            Terminal x) $5))
        and
           i = Estado $7
        and
           f = Conjunto (rev (map (function x -> Estado x) $9))
        in
           let rec aux acum = function
                [] -> acum
              | (s1, s2, s3, s4, mv) :: r ->
                   let
                      e1 = if mem s1 $1 then Estado s1 else raise Parsing.Parse_error
                   and
                      e2 = if mem s2 $1 then Estado s2 else raise Parsing.Parse_error
                   and
                      e3 = if s3 = "" then Terminal ""
                           else if s3 = "blanco" then No_terminal ""
                                else if mem s3 $3 then Terminal s3
                                     else if mem s3 $5 then No_terminal s3
                                          else raise Parsing.Parse_error
                   and
                      e4 = if s4 = "" then Terminal ""
                           else if s4 = "blanco" then No_terminal ""
                                else if mem s4 $3 then Terminal s4
                                     else if mem s4 $5 then No_terminal s4
                                          else raise Parsing.Parse_error
                   in
                      aux ((Arco_mt (e1, e2, e3, e4, mv))::acum) r
           in
              if (interseccion (Conjunto $1) (Conjunto $3) = conjunto_vacio) &&
                 (interseccion (Conjunto $1) (Conjunto $5) = conjunto_vacio) &&
                 (pertenece i q) &&
                 (incluido f q)
              then
                 Mt (q, 
                     t, 
                     union (Conjunto [No_terminal ""]) g, 
                     i, 
                     Conjunto (aux [] $11), 
                     No_terminal "", 
                     f)
              else
                 raise Parsing.Parse_error
      }


nombres :
                    { [] }
 | nombres NOMBRE   { $2::$1 }
;


nombres_b :
                      { [] }
 | nombres_b NOMBRE   { $2::$1 }
 | nombres_b BLANCO   { ""::$1 }
;


transiciones :
                             { [] }
 | transiciones transicion   { $2::$1 }
;


transicion :
   NOMBRE NOMBRE simb simb mov PUNTO_Y_COMA  { ($1, $2, $3, $4, $5) }
;


simb :
   EPSILON   { "" }
 | NOMBRE    { $1 }
 | BLANCO    { "blanco" }
;


mov :
   IZQUIERDA   { Izquierda }
 | DERECHA     { Derecha }
;

%%

