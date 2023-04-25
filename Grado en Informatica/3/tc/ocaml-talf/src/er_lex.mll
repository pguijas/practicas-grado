
(*****************************************************************************
 *
 * er_lex.mll   Analizador léxico para expresiones regulares.                
 *
 *****************************************************************************)

{
   open Er_yacc;;

   let eliminar_escape s =
      let
         especiales = ['#'; '('; ')'; '|'; '.'; '*']
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

let normales = ['\033'-'\034' '\036'-'\039' '\043'-'\045' '\047'-'\123' '\125'-'\126' '\161'-'\255']

let especiales = "\\#" | "\\(" | "\\)" | "\\|" | "\\." | "\\*"

rule er_token = parse
     [' ' '\t' '\n']            { er_token lexbuf }
   | '#'[^'\n']*'\n'            { er_token lexbuf } (* caracter  35 *)
   | '('                        { PARENTESIS_IZQ }  (* caracter  40 *)
   | ')'                        { PARENTESIS_DCH }  (* caracter  41 *)
   | '|'                        { BARRA }           (* caracter 124 *)
   | '.'                        { PUNTO }           (* caracter  46 *)
   | '*'                        { ASTERISCO }       (* caracter  42 *)
   | "epsilon"                  { EPSILON }
   | "vacio"                    { VACIO }
   | eof                        { FIN }
   | (normales | especiales)+   { TERMINAL (eliminar_escape (Lexing.lexeme lexbuf)) }
   | _                          { ERROR }


(*****************************************************************************)

