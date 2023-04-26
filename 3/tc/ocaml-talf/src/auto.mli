
(*****************************************************************************
 *
 * auto.mli   Tipos predefinidos y operaciones básicas para las prácticas de
 *            Teoría de Autómatas y Lenguajes Formales.                     
 *
 *****************************************************************************)

type simbolo =
     Terminal of string 
   | No_terminal of string;;

(*****************************************************************************)

type er =
     Vacio
   | Constante of simbolo
   | Union of (er * er)
   | Concatenacion of (er * er)
   | Repeticion of er;;

(*****************************************************************************)

type estado =
   Estado of string;;

type arco_af =
   Arco_af of (estado * estado * simbolo);;

type af =
   Af of (estado Conj.conjunto *
          simbolo Conj.conjunto * 
          estado *
          arco_af Conj.conjunto * 
          estado Conj.conjunto);;

(*****************************************************************************)

type regla_gic =
   Regla_gic of (simbolo * simbolo list);;

type gic =
   Gic of (simbolo Conj.conjunto * 
           simbolo Conj.conjunto *
           regla_gic Conj.conjunto * 
           simbolo);;

(*****************************************************************************)

type arco_ap =
   Arco_ap of (estado * estado * simbolo * simbolo * simbolo list);;

type ap = 
   Ap of (estado Conj.conjunto * simbolo Conj.conjunto * simbolo Conj.conjunto *
          estado * arco_ap Conj.conjunto * simbolo * estado Conj.conjunto);;

(*****************************************************************************)

type movimiento_mt =
     Izquierda
   | Derecha;;

type arco_mt =
   Arco_mt of (estado * estado * simbolo * simbolo * movimiento_mt);;

type mt =
   Mt of (estado Conj.conjunto * simbolo Conj.conjunto * simbolo Conj.conjunto *
          estado * arco_mt Conj.conjunto * simbolo * estado Conj.conjunto);;

(*****************************************************************************)

val epsilon_cierre : estado Conj.conjunto -> af -> estado Conj.conjunto;;
val avanza : simbolo -> estado Conj.conjunto -> af -> estado Conj.conjunto;;
val escaner_af : simbolo list -> af -> bool;;
val es_regular : gic -> bool;;
val af_of_gic : gic -> af;;
val gic_of_af : af -> gic;;

val escaner_ap : simbolo list -> ap -> bool;;

val escaner_mt : simbolo list -> mt -> bool;;
val scpm : mt -> simbolo list -> (string * string) list;;

(*****************************************************************************)

