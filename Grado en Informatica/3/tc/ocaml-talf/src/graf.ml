
(*****************************************************************************
 *
 * graf.ml   Funciones para la visualización gráfica e impresión de los
 *           autómatas.
 *
 *****************************************************************************)

open Conj;;
open Auto;;
open List;;

(*****************************************************************************
 *
 * dot_of_af : string -> af -> string
 *
 * Función que dado un autómata finito devuelve su especificación tipo
 * texto en sintaxis dot de graphviz.
 * Opcionalmente, se puede dar un título para el grafo.
 *
 *****************************************************************************)

let dot_of_af titulo
   (Af (estados, _, Estado inicial, Conjunto arcos, Conjunto finales)) =
      let rec aux1 = function
           [] -> ""
         | Estado s :: t -> "\"" ^ s ^ "\"; " ^ (aux1 t)
      and aux2 = function
           [] -> ""
         | Arco_af (Estado o, Estado d, Terminal "") :: t ->
              "   \\\"" ^ o ^ "\\\" -> \\\"" ^ d ^ "\\\" [label=\\\"&#949;\\\"];\n" ^ (aux2 t)
         | Arco_af (Estado o, Estado d, Terminal s) :: t ->
              "   \\\"" ^ o ^ "\\\" -> \\\"" ^ d ^ "\\\" [label=\\\"" ^ s ^ "\\\"];\n" ^ (aux2 t)
         | _ -> raise (Failure "dot_of_af: hay arcos cuya etiqueta es un no terminal")
      in
         "digraph grafo {\n" ^
         "   label=" ^
         (if titulo = "" then ("\\\"\\\"") else ("\\\"" ^ titulo ^ "\\\"")) ^
         ";\n" ^
         "   labelloc=top;\n" ^
         "   labeljust=left;\n" ^
         "   node [style=invis, height=0, width=0]; \\\"\\\";\n" ^
         "   node [shape=doublecircle, style=solid]; " ^
         aux1 finales ^ "\n" ^
         "   node [shape=circle, style=solid];\n" ^
         "   rankdir = LR;\n" ^
         "   \\\"\\\" -> \\\"" ^ inicial ^ "\\\";\n" ^
         aux2 arcos ^
         "}\n"
      ;;
 

(*****************************************************************************
 *
 * dibuja_af : ?titulo:string -> af -> unit
 *
 * Función que dado un autómata finito, llama al comando dot para
 * visualizar el grafo de dicho autómata en una ventana x11.
 * Opcionalmente, se puede dar un título para el grafo.
 *
 *****************************************************************************)

let dibuja_af ?titulo:(titulo="") a =
   let
      c = "echo \"" ^ (dot_of_af titulo a) ^ "\" | dot -Tx11 &"
   in
      ignore (Sys.command c)
   ;;


(*****************************************************************************
 *
 * dot_of_ap : ap -> string
 *
 * Función que dado un autómata de pila devuelve su especificación tipo
 * texto en sintaxis dot de graphviz.
 * Opcionalmente, se puede dar un título para el grafo.
 *
 *****************************************************************************)

let dot_of_ap titulo
   (Ap (estados, _, _, Estado inicial, Conjunto arcos, _, Conjunto finales)) =
      let rec aux1 = function
           [] -> ""
         | Estado s :: t -> "\"" ^ s ^ "\"; " ^ (aux1 t)
      and aux2 = 
         let rec aux3 = function
              [] -> "&#949;"
            | [Terminal ""] -> "&#949;"
            | [Terminal s] -> s
            | [No_terminal ""] -> "Z"
            | [No_terminal s] -> s
            | Terminal s :: t -> s ^ " " ^ (aux3 t)
            | No_terminal "" :: t -> "Z" ^ " " ^ (aux3 t)
            | No_terminal s :: t -> s ^ " " ^ (aux3 t)
         in
            function
              [] -> ""
            | Arco_ap (Estado o, Estado d, s1, s2, lista) :: t ->
                 "   \\\"" ^ o ^ "\\\" -> \\\"" ^ d ^ "\\\" [label=\\\"" ^ 
                  (aux3 [s1]) ^ ", " ^ (aux3 [s2]) ^ ", " ^ (aux3 lista) ^
                 "\\\"];\n"^ (aux2 t)
      in
         "digraph grafo {\n" ^
         "   label=" ^
         (if titulo = "" then ("\\\"\\\"") else ("\\\"" ^ titulo ^ "\\\"")) ^
         ";\n" ^
         "   labelloc=top;\n" ^
         "   labeljust=left;\n" ^
         "   node [style=invis, height=0, width=0]; \\\"\\\";\n" ^
         "   node [shape=doublecircle, style=solid]; " ^
         aux1 finales ^ "\n" ^
         "   node [shape=circle, style=solid];\n" ^
         "   rankdir = LR;\n" ^
         "   \\\"\\\" -> \\\"" ^ inicial ^ "\\\";\n" ^
         aux2 arcos ^
         "}\n"
      ;;
   

(*****************************************************************************
 *
 * dibuja_ap : ap -> unit
 *
 * Función que dado un autómata de pila, llama al comando dot para
 * visualizar el grafo de dicho autómata en una ventana x11.
 * Opcionalmente, se puede dar un título para el grafo.
 *
 *****************************************************************************)

let dibuja_ap ?titulo:(titulo="") a =
   let
      c = "echo \"" ^ (dot_of_ap titulo a) ^ "\" | dot -Tx11 &"
   in
      ignore (Sys.command c)
   ;;


(*****************************************************************************
 *
 * dot_of_mt : mt -> string
 *
 * Función que dada una máquina de Turing devuelve su especificación tipo
 * texto en sintaxis dot de graphviz.
 * Opcionalmente, se puede dar un título para el grafo.
 *
 *****************************************************************************)

let dot_of_mt titulo
   (Mt (estados, _, _, Estado inicial, Conjunto arcos, _, Conjunto finales)) =
      let rec aux1 = function
           [] -> ""
         | Estado s :: t -> "\"" ^ s ^ "\"; " ^ (aux1 t)
      and aux2 = 
         let aux3 = function
              Terminal "" -> "&#949;"
            | Terminal s -> s
            | No_terminal "" -> "B"
            | No_terminal s -> s
         and aux4 = function
              Izquierda -> "L"
            | Derecha -> "R"
         in
            function
              [] -> ""
            | Arco_mt (Estado o, Estado d, s1, s2, mov) :: t ->
                 "   \\\"" ^ o ^ "\\\" -> \\\"" ^ d ^ "\\\" [label=\\\"" ^ 
                 (aux3 s1) ^ ", " ^ (aux3 s2) ^ ", " ^ (aux4 mov) ^ 
                 "\\\"];\n" ^ (aux2 t)
      in
         "digraph grafo {\n" ^
         "   label=" ^
         (if titulo = "" then ("\\\"\\\"") else ("\\\"" ^ titulo ^ "\\\"")) ^
         ";\n" ^
         "   labelloc=top;\n" ^
         "   labeljust=left;\n" ^
         "   node [style=invis, height=0, width=0]; \\\"\\\";\n" ^
         "   node [shape=doublecircle, style=solid]; " ^
         aux1 finales ^ "\n" ^
         "   node [shape=circle, style=solid];\n" ^
         "   rankdir = LR;\n" ^
         "   \\\"\\\" -> \\\"" ^ inicial ^ "\\\";\n" ^
         aux2 arcos ^
         "}\n"
      ;;
   

(*****************************************************************************
 *
 * dibuja_mt : mt -> unit
 *
 * Función que dada una máquina de Turing, llama al comando dot para
 * visualizar el grafo de dicha máquina en una ventana x11.
 * Opcionalmente, se puede dar un título para el grafo.
 *
 *****************************************************************************)

let dibuja_mt ?titulo:(titulo="") m =
   let
      c = "echo \"" ^ (dot_of_mt titulo m) ^ "\" | dot -Tx11 &"
   in
      ignore (Sys.command c)
   ;;

(*****************************************************************************)

