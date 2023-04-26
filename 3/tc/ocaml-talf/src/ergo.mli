
(*****************************************************************************
 *
 * ergo.mli   Funciones de conversión desde string o file para los tipos de  
 *            auto.ml.                                                       
 *
 *****************************************************************************)

val cadena_of_string : string -> Auto.simbolo list;;
val string_of_cadena : Auto.simbolo list -> string;;
val cadena_of_file : string -> Auto.simbolo list;;
val file_of_cadena : Auto.simbolo list -> string -> unit;;

val er_of_string : string -> Auto.er;;
val string_of_er : Auto.er -> string;;
val er_of_file : string -> Auto.er;;
val file_of_er : Auto.er -> string -> unit;;

val af_of_string : string -> Auto.af;;
val string_of_af : Auto.af -> string;;
val af_of_file : string -> Auto.af;;
val file_of_af : Auto.af -> string -> unit;;

val gic_of_string : string -> Auto.gic;;
val string_of_gic : Auto.gic -> string;;
val gic_of_file : string -> Auto.gic;;
val file_of_gic : Auto.gic -> string -> unit;;

val ap_of_string : string -> Auto.ap;;
val string_of_ap : Auto.ap -> string;;
val ap_of_file : string -> Auto.ap;;
val file_of_ap : Auto.ap -> string -> unit;;

val mt_of_string : string -> Auto.mt;;
val string_of_mt : Auto.mt -> string;;
val mt_of_file : string -> Auto.mt;;
val file_of_mt : Auto.mt -> string -> unit;;

val file_of_scpm : string -> (string * string) list -> unit;;

(*****************************************************************************)

