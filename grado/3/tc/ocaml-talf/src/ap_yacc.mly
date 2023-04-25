
/*****************************************************************************
 *
 * ap_yacc.mly   Analizador sintáctico para autómatas de pila.
 *
 *****************************************************************************/

%{
   open Conj;;
   open Auto;;
   open List;;
%}

%token PUNTO_Y_COMA EPSILON ZETA FIN 
%token <string> NOMBRE
%token ERROR

%start ap_parse
%type <Auto.ap> ap_parse

%%

ap_parse :
   nombres PUNTO_Y_COMA nombres PUNTO_Y_COMA nombres_z PUNTO_Y_COMA NOMBRE PUNTO_Y_COMA 
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
              | (s1, s2, s3, s4, s5) :: r ->
                   let
                      e1 = if mem s1 $1 then Estado s1 else raise Parsing.Parse_error
                   and
                      e2 = if mem s2 $1 then Estado s2 else raise Parsing.Parse_error
                   and
                      e3 = if s3 = "" then Terminal ""
                           else if mem s3 $3 then Terminal s3
                                else raise Parsing.Parse_error
                   and
                      e4 = if s4 = "" then Terminal ""
                           else if s4 = "zeta" then No_terminal ""
                                else if mem s4 $3 then Terminal s4
                                     else if mem s4 $5 then No_terminal s4
                                          else raise Parsing.Parse_error
                   and
                      e5 = map (function x -> 
                                   if x = "zeta" then No_terminal ""
                                   else if mem x $3 then Terminal x
                                        else if mem x $5 then No_terminal x
                                             else raise Parsing.Parse_error) s5
                   in
                      aux ((Arco_ap (e1, e2, e3, e4, e5))::acum) r
           in
              if (interseccion (Conjunto $1) (Conjunto $3) = conjunto_vacio) &&
                 (interseccion (Conjunto $1) (Conjunto $5) = conjunto_vacio) &&
                 (pertenece i q) &&
                 (incluido f q)
              then
                 Ap (q, 
                     t, 
                     union (Conjunto [No_terminal ""]) g, 
                     i, 
                     Conjunto (aux [] $11), 
                     No_terminal "", 
                     f)
              else
                 raise Parsing.Parse_error
      }
;


nombres :
                    { [] }
 | nombres NOMBRE   { $2::$1 }
;


nombres_z :
                      { [] }
 | nombres_z NOMBRE   { $2::$1 }
 | nombres_z ZETA     { ""::$1 }
;


transiciones :
                             { [] }
 | transiciones transicion   { $2::$1 }
;


transicion :
   NOMBRE NOMBRE trn3 trn4 trn5 PUNTO_Y_COMA
      { ($1, $2, $3, $4, $5) }
;

trn3 :
   EPSILON   { "" }
 | NOMBRE    { $1 }
;

trn4 :
   EPSILON   { "" }
 | ZETA      { "zeta" }
 | NOMBRE    { $1 }
;

trn5 :
   EPSILON   { [] }
 | trn6      { List.rev $1 }
;

trn6 :
 | ZETA          { ["zeta"] }
 | NOMBRE        { [$1] }
 | trn6 ZETA     { "zeta" :: $1 }
 | trn6 NOMBRE   { $2 :: $1 }
;

%%

