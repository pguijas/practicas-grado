
(*****************************************************************************
 *
 * ergo.ml   Funciones de conversión desde string o file para los tipos de   
 *           auto.ml.                                                        
 *
 *****************************************************************************)

open Conj;;
open Auto;;

(*****************************************************************************
 *
 * incluir_escape : string -> string
 *
 * Función que dado un string y una lista de símbolos especiales, devuelve
 * el mismo string pero cambiando cada una de las apariciones de los símbolos
 * especiales por su versión "escapada", es decir, precedido por el símbolo \.
 *
 *****************************************************************************)

let incluir_escape s especiales =
   let
      l = String.length s
   in
      let rec aux lc llc n =
         if n < 0 then
            let rec aux2 ns i = function
                 [] -> Bytes.to_string ns
               | c::t -> Bytes.set ns i c; aux2 ns (i+1) t
            in
               aux2 (Bytes.create llc) 0 lc
         else
            if List.mem s.[n] especiales then
                aux ('\092' :: s.[n] :: lc) (llc+1) (n-1)
            else
                aux (s.[n] :: lc) llc (n-1)
      in
         aux [] l (l-1)
      ;;

(*****************************************************************************
 *
 * cadena_of_string : string -> Auto.simbolo list
 * string_of_cadena : Auto.simbolo list -> string
 *
 * Función que dado un string devuelve la lista de símbolos correspondiente, 
 * y viceversa.                                                              
 *
 *****************************************************************************)

let cadena_of_string s = 
   Simb_yacc.simbolo_parse Simb_lex.simbolo_token (Lexing.from_string s);;

let rec string_of_cadena = function
     []                    -> ""
   | [Terminal ""]         -> ""
   | [Terminal s]          -> incluir_escape s ['#']
   | [No_terminal ""]      -> "blanco"
   | (Terminal "") :: t    -> string_of_cadena t
   | (Terminal s) :: t     -> (incluir_escape s ['#']) ^ " " ^ (string_of_cadena t)
   | (No_terminal "") :: t -> "blanco" ^ " " ^ (string_of_cadena t)
   | _ -> 
        raise (Failure "string_of_cadena: la lista de simbolos contiene no terminales");;


(*****************************************************************************
 *
 * cadena_of_file : string -> Auto.simbolo list
 * file_of_cadena : Auto.simbolo list -> string -> unit
 *
 * Función que dado un nombre de fichero devuelve la lista de símbolos       
 * correspondiente, y viceversa.                                             
 *
 *****************************************************************************)

let cadena_of_file f = 
   Simb_yacc.simbolo_parse Simb_lex.simbolo_token (Lexing.from_channel (open_in f));;

let file_of_cadena s f =
   let
      c = open_out f
   in 
      output_string c (string_of_cadena s ^ "\n"); close_out c
   ;;


(*****************************************************************************
 *
 * er_of_string : string -> Auto.er
 * string_of_er : Auto.er -> string
 *
 * Función que dado un string devuelve la expresion regular correspondiente, 
 * y viceversa.                                                              
 *
 *****************************************************************************)

let er_of_string s = 
   Er_yacc.er_parse Er_lex.er_token (Lexing.from_string s);;

let string_of_er e = 
   let rec aux = function
        Vacio                   -> (0, "vacio")
      | Constante (Terminal "") -> (0, "epsilon")
      | Constante (Terminal s)  -> (0, incluir_escape s ['#'; '('; ')'; '|'; '.'; '*'])
      | Constante _ -> 
           raise (Failure "string_of_er: la expresion regular contiene un no terminal")
      | Union (e1, e2) -> 
           let
              (_, s1) = aux e1
           and
              (_, s2) = aux e2
           in
              (3, s1 ^ "|" ^ s2)
      | Concatenacion (e1, e2) -> 
           let
              (p1, s1) = aux e1
           and
              (p2, s2) = aux e2
           in
              if (p1 > 2) then
                 if (p2 > 2) then (2, "(" ^ s1 ^ ").(" ^ s2 ^ ")")
                             else (2, "(" ^ s1 ^ ")." ^ s2)
              else
                 if (p2 > 2) then (2, s1 ^ ".(" ^ s2 ^ ")")
                             else (2, s1 ^ "." ^ s2)
      | Repeticion e ->
           let
              (p, s) = aux e
           in
              if (p > 1) then (1, "(" ^ s ^ ")*")
                         else (1, s ^ "*")
   in
      snd (aux e)
   ;;


(*****************************************************************************
 *
 * er_of_file : string -> Auto.er
 * file_of_er : Auto.er -> string -> unit
 *
 * Función que dado un nombre de fichero devuelve la expresion regular       
 * correspondiente, y viceversa.                                             
 *
 *****************************************************************************)

let er_of_file f = 
   Er_yacc.er_parse Er_lex.er_token (Lexing.from_channel (open_in f));;

let file_of_er e f =
   let
      c = open_out f
   in 
      output_string c (string_of_er e ^ "\n"); close_out c
   ;;


(*****************************************************************************
 *
 * af_of_string : string -> Auto.af
 * string_of_af : Auto.af -> string
 *
 * Función que dado un string devuelve el autómata finito correspondiente,   
 * y viceversa.                                                              
 *
 *****************************************************************************)

let af_of_string s = 
   Af_yacc.af_parse Af_lex.af_token (Lexing.from_string s);;

let string_of_af
   (Af (Conjunto estados, Conjunto terminales, Estado inicial, Conjunto arcos, Conjunto finales)) =
   let
      esp = ['#'; ';']
   in
      let rec aux1 = function
           [] -> ""
         | [Estado s] -> incluir_escape s esp
         | Estado s :: t -> (incluir_escape s esp) ^ " " ^ (aux1 t)
      and aux2 = function
           [] -> ""
         | [Terminal s] -> incluir_escape s esp
         | Terminal s :: t -> (incluir_escape s esp) ^ " " ^ (aux2 t)
         | _ -> raise (Failure "string_of_af: el conjunto de símbolos contiene un no terminal")
      and aux3 = function
           [] -> ""
         | Arco_af (Estado o, Estado d, Terminal "") :: t ->
              (incluir_escape o esp) ^ " " ^ (incluir_escape d esp) ^ " epsilon;\n" ^ (aux3 t)
         | Arco_af (Estado o, Estado d, Terminal s) :: t ->
              (incluir_escape o esp) ^ " " ^ (incluir_escape d esp) ^ " " ^ 
              (incluir_escape s esp) ^ ";\n" ^ (aux3 t)
         | _ -> raise (Failure "string_of_af: el arco contiene un no terminal")
      in
         (aux1 estados) ^ ";\n" ^
         (aux2 terminales) ^ ";\n" ^
         (incluir_escape inicial esp) ^ ";\n" ^ 
         (aux1 finales) ^ ";\n" ^ 
         (aux3 arcos)
      ;;
   

(*****************************************************************************
 *
 * af_of_file : string -> Auto.af
 * file_of_af : Auto.af -> string -> unit
 *
 * Función que dado un nombre de fichero devuelve el autómata finito         
 * correspondiente, y viceversa.                                             
 *
 *****************************************************************************)

let af_of_file f = 
   Af_yacc.af_parse Af_lex.af_token (Lexing.from_channel (open_in f));;

let file_of_af a f =
   let
      c = open_out f
   in 
      output_string c (string_of_af a ^ "\n"); close_out c
   ;;


(*****************************************************************************
 *
 * gic_of_string : string -> Auto.gic
 * string_of_gic : Auto.gic -> string
 *
 * Función que dado un string devuelve la gramática independiente del        
 * contexto correspondiente, y viceversa.                                    
 *
 *****************************************************************************)

let gic_of_string s = 
   Gic_yacc.gic_parse Gic_lex.gic_token (Lexing.from_string s);;

let string_of_gic = function
     Gic (Conjunto no_terminales, Conjunto terminales, Conjunto reglas, No_terminal axioma) ->
        let
           esp = ['#'; '|'; ';']
        in
           let rec aux1 = function
                [] -> ""
              | [No_terminal s] -> incluir_escape s esp
              | No_terminal s :: t -> (incluir_escape s esp) ^ " " ^ (aux1 t)
              | _ -> raise (Failure "string_of_gic: el conjunto de símbolos no_terminales contiene un terminal")
           and aux2 = function
                [] -> ""
              | [Terminal s] -> incluir_escape s esp
              | Terminal s :: t -> (incluir_escape s esp) ^ " " ^ (aux2 t)
              | _ -> raise (Failure "string_of_gic: el conjunto de símbolos terminales contiene un no terminal")
           and aux3 = function
                [] -> ""
              | Regla_gic (No_terminal n, dch) :: t -> 
                   let rec aux4 = function
                        [] -> "epsilon"
                      | [Terminal s] -> incluir_escape s esp
                      | [No_terminal s] -> incluir_escape s esp
                      | Terminal s :: r -> (incluir_escape s esp) ^ " " ^ (aux4 r)
                      | No_terminal s :: r -> (incluir_escape s esp) ^ " " ^ (aux4 r)
                   in
                      (incluir_escape n esp) ^ " -> " ^ (aux4 dch) ^ ";\n" ^ (aux3 t)

              | _ -> raise (Failure "string_of_gic: la parte izquierda de la regla es un terminal")
           in
              (aux1 no_terminales) ^ ";\n" ^
              (aux2 terminales) ^ ";\n" ^
              (incluir_escape axioma esp) ^ ";\n" ^ 
              (aux3 reglas)
   | _ -> raise (Failure "string_of_gic: el axioma de la gramatica es un terminal")
      ;;
   

(*****************************************************************************
 *
 * gic_of_file : string -> Auto.gic
 * file_of_gic : Auto.gic -> string -> unit
 *
 * Función que dado un nombre de fichero devuelve la gramática independiente 
 * del contexto correspondiente, y viceversa.                                
 *
 *****************************************************************************)

let gic_of_file f = 
   Gic_yacc.gic_parse Gic_lex.gic_token (Lexing.from_channel (open_in f));;

let file_of_gic g f =
   let
      c = open_out f
   in 
      output_string c (string_of_gic g ^ "\n"); close_out c
   ;;



(*****************************************************************************
 *
 * ap_of_string : string -> Auto.ap
 * string_of_ap : Auto.ap -> string
 *
 * Función que dado un string devuelve el autómata de pila correspondiente,   
 * y viceversa.                                                              
 *
 *****************************************************************************)

let ap_of_string s = 
   Ap_yacc.ap_parse Ap_lex.ap_token (Lexing.from_string s);;

let string_of_ap
   (Ap (Conjunto estados, Conjunto terminales, Conjunto pila, Estado inicial, 
        Conjunto arcos, _, Conjunto finales)) =
   let
      esp = ['#'; ';']
   in
      let rec aux1 = function
           [] -> ""
         | [Estado s] -> incluir_escape s esp
         | Estado s :: t -> (incluir_escape s esp) ^ " " ^ (aux1 t)
      and aux2 = function
           [] -> ""
         | [Terminal s] -> incluir_escape s esp
         | Terminal s :: t -> (incluir_escape s esp) ^ " " ^ (aux2 t)
         | _ -> raise (Failure "string_of_ap: el conjunto de símbolos contiene un no terminal")
      and aux3 = function
           [] -> "epsilon"
         | [Terminal s] -> incluir_escape s esp
         | [No_terminal ""] -> "zeta"
         | [No_terminal s] -> incluir_escape s esp
         | Terminal s :: t -> (incluir_escape s esp) ^ " " ^ (aux3 t)
         | No_terminal "" :: t -> "zeta" ^ " " ^ (aux3 t)
         | No_terminal s :: t -> (incluir_escape s esp) ^ " " ^ (aux3 t)
      and aux4 = 
         let aux5 = function
              Terminal "" -> "epsilon"
            | Terminal s -> s
            | No_terminal "" -> "zeta"
            | No_terminal s -> s
         in
            function
                [] -> ""
              | Arco_ap (Estado o, Estado d, s1, s2, lista) :: t ->
                   (incluir_escape o esp) ^ " " ^ (incluir_escape d esp) ^ " " ^ 
                   (aux5 s1) ^ " " ^ (aux5 s2) ^ " " ^ (aux3 lista) ^ ";\n" ^ (aux4 t)
      in
         (aux1 estados) ^ ";\n" ^
         (aux2 terminales) ^ ";\n" ^
         (aux3 pila) ^ ";\n" ^
         (incluir_escape inicial esp) ^ ";\n" ^ 
         (aux1 finales) ^ ";\n" ^ 
         (aux4 arcos)
      ;;


(*****************************************************************************
 *
 * ap_of_file : string -> Auto.ap
 * file_of_ap : Auto.ap -> string -> unit
 *
 * Función que dado un nombre de fichero devuelve el autómata de pila         
 * correspondiente, y viceversa.                                             
 *
 *****************************************************************************)

let ap_of_file f = 
   Ap_yacc.ap_parse Ap_lex.ap_token (Lexing.from_channel (open_in f));;

let file_of_ap a f =
   let
      c = open_out f
   in 
      output_string c (string_of_ap a ^ "\n"); close_out c
   ;;


(*****************************************************************************
 *
 * mt_of_string : string -> Auto.mt
 * string_of_mt : Auto.mt -> string
 *
 * Función que dado un string devuelve la máquina de Turing correspondiente,   
 * y viceversa.                                                              
 *
 *****************************************************************************)

let mt_of_string s = 
   Mt_yacc.mt_parse Mt_lex.mt_token (Lexing.from_string s);;

let string_of_mt
   (Mt (Conjunto estados, Conjunto terminales, Conjunto cinta, Estado inicial, 
        Conjunto arcos, _, Conjunto finales)) =
   let
      esp = ['#'; ';']
   in
      let rec aux1 = function
           [] -> ""
         | [Estado s] -> incluir_escape s esp
         | Estado s :: t -> (incluir_escape s esp) ^ " " ^ (aux1 t)
      and aux2 = function
           [] -> ""
         | [Terminal s] -> incluir_escape s esp
         | Terminal s :: t -> (incluir_escape s esp) ^ " " ^ (aux2 t)
         | _ -> raise (Failure "string_of_mt: el conjunto de símbolos contiene un no terminal")
      and aux3 = function
           [] -> ""
         | [Terminal s] -> incluir_escape s esp
         | [No_terminal ""] -> "blanco"
         | [No_terminal s] -> incluir_escape s esp
         | Terminal s :: t -> (incluir_escape s esp) ^ " " ^ (aux3 t)
         | No_terminal "" :: t -> "blanco" ^ " " ^ (aux3 t)
         | No_terminal s :: t -> (incluir_escape s esp) ^ " " ^ (aux3 t)
      and aux4 = 
         let aux5 = function
              Terminal "" -> "epsilon"
            | Terminal s -> s
            | No_terminal "" -> "blanco"
            | No_terminal s -> s
         in
            function
                [] -> ""
              | Arco_mt (Estado o, Estado d, s1, s2, Izquierda) :: t ->
                   (incluir_escape o esp) ^ " " ^ (incluir_escape d esp) ^ " " ^ 
                   (aux5 s1) ^ " " ^ (aux5 s2) ^ " izquierda;\n" ^ (aux4 t)
              | Arco_mt (Estado o, Estado d, s1, s2, Derecha) :: t ->
                   (incluir_escape o esp) ^ " " ^ (incluir_escape d esp) ^ " " ^ 
                   (aux5 s1) ^ " " ^ (aux5 s2) ^ " derecha;\n" ^ (aux4 t)
      in
         (aux1 estados) ^ ";\n" ^
         (aux2 terminales) ^ ";\n" ^
         (aux3 cinta) ^ ";\n" ^
         (incluir_escape inicial esp) ^ ";\n" ^ 
         (aux1 finales) ^ ";\n" ^ 
         (aux4 arcos)
      ;;
   

(*****************************************************************************
 *
 * mt_of_file : string -> Auto.mt
 * file_of_mt : Auto.mt -> string -> unit
 *
 * Función que dado un nombre de fichero devuelve la máquina de Turing
 * correspondiente, y viceversa.                                             
 *
 *****************************************************************************)

let mt_of_file f = 
   Mt_yacc.mt_parse Mt_lex.mt_token (Lexing.from_channel (open_in f));;

let file_of_mt a f =
   let
      c = open_out f
   in 
      output_string c (string_of_mt a ^ "\n"); close_out c
   ;;


(*****************************************************************************
 *
 * file_of_scpm : string -> (string * string) list -> unit
 *
 * Funcion que dado un string y un Sistema de Correspondencia de Post
 * Modificado (SCPM), genera un fichero cuyo nombre es el string y cuyo 
 * contenido es una linea de texto con el formato
 *    ficha <cadena superior> <cadena inferior>
 * para cada una de las fichas del SCPM.
 *
 *****************************************************************************)

let file_of_scpm name l =
   let
      f = open_out name
   in
      let rec aux = function
           []       -> flush f; close_out f 
         | (u,d)::t -> output_string f ("ficha " ^ u ^ " " ^ d ^ "\n"); aux t
      in
         aux l
   ;;


(*****************************************************************************)

