
(*****************************************************************************

conj.mli

Una implementación basada en listas para realizar operaciones básicas
sobre conjuntos de elementos de cualquier tipo.

******************************************************************************)

type 'a conjunto = Conjunto of 'a list;;
val conjunto_vacio : 'a conjunto;;
val es_vacio : 'a conjunto -> bool;;
val pertenece : 'a -> 'a conjunto -> bool;;
val agregar : 'a -> 'a conjunto -> 'a conjunto;;
val conjunto_of_list : 'a list -> 'a conjunto;;
val suprimir : 'a -> 'a conjunto -> 'a conjunto;;
val cardinal : 'a conjunto -> int;;
val union : 'a conjunto -> 'a conjunto -> 'a conjunto;;
val interseccion : 'a conjunto -> 'a conjunto -> 'a conjunto;;
val diferencia : 'a conjunto -> 'a conjunto -> 'a conjunto;;
val incluido : 'a conjunto -> 'a conjunto -> bool;;
val igual : 'a conjunto -> 'a conjunto -> bool;;
val list_of_conjunto : 'a conjunto -> 'a list;;
val cartesiano : 'a conjunto -> 'b conjunto -> ('a * 'b) conjunto;;

(*****************************************************************************)

