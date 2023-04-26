
(*****************************************************************************
 *
 * af_lex.mll   Analizador léxico para autómatas finitos.
 *
 *****************************************************************************)

{
   open Af_yacc;;

   let eliminar_escape s =
      let
         especiales = ['#'; ';']
      and
         l = String.length s
      in
         let rec aux r n m =
            if (n = l) then
               Bytes.to_string (Bytes.sub r 0 m)
            else
               if (s.[n] = '\092') && (n <> (l-1)) && (List.mem s.[n+1] especiales) then
                  aux r (n+1) m
               else
                  (Bytes.set r m s.[n]; aux r (n+1) (m+1))
         in
            aux (Bytes.create l) 0 0
         ;;
}

let normales = ['\033'-'\034' '\036'-'\058' '\060'-'\126' '\161'-'\255']

let especiales = "\\#" | "\\;"

rule af_token = parse
     [' ' '\t' '\n']            { af_token lexbuf }
   | '#'[^'\n']*'\n'            { af_token lexbuf } (* caracter  35 *)
   | ';'                        { PUNTO_Y_COMA }    (* caracter  59 *)
   | "epsilon"                  { EPSILON }
   | eof                        { FIN }
   | (normales | especiales)+   { NOMBRE (eliminar_escape (Lexing.lexeme lexbuf)) }
   | _                          { ERROR }

(*****************************************************************************)

