
(*****************************************************************************
 *
 * graf.mli  Funciones para la visualización gráfica e impresión de los
 *           autómatas.
 *
 *****************************************************************************)

val dot_of_af : string -> Auto.af -> string;;
val dibuja_af : ?titulo:string -> Auto.af -> unit;;

val dot_of_ap : string -> Auto.ap -> string;;
val dibuja_ap : ?titulo:string -> Auto.ap -> unit;;

val dot_of_mt : string -> Auto.mt -> string;;
val dibuja_mt : ?titulo:string -> Auto.mt -> unit;;

(*****************************************************************************)

