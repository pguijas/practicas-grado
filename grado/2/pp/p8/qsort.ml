open List;;

let rec qsort1 ord = function
	| [] -> []
	| h::t -> let after, before = partition (ord h) t in
				qsort1 ord before @ h :: qsort1 ord after
;;

let rec qsort2 ord = function
	| [] -> []
	| h::t -> let after, before = partition (ord h) t in
				rev_append (rev (qsort2 ord before)) (h :: qsort2 ord after)
;;

let l1= List.init 1_000_000 (fun _ -> Random.int 1_000_000);;

(*qsort1 (<=) (l1);; Stack Overflow*)
qsort2 (<=) (l1);;
(*
	Con la lista l1 de 1000000 elementos, qsort1 no funcionará, esto es debido a como está impmentado @ (va recorriendo la 1º lista 
	y añadiendo los elementos a la 2 de manera no terminal). Si la 1º lista que recibe @ (qsort1 ord before) tiene demasiados elementos habrá un stack overflow.
	De manera que depende del pivote que escoja, si este hace que before tenga pocos elementos no dará ningún stack overflow.

	Para resolver el problema de stack overflow se usará recursividad terminal (rev_append), pero esta tiene un precio, 
	tenemos que hacer un rev a (qsort2 ord before) y esto esta operación añade algo de tiempo extra. 
	Por lo que con listas pequeñas es preferible qsort1 y con listas muy grandes no funciona correctamente.
*)

let l3= List.init 100_000 (fun _ -> Random.int 1000_000);;

let crono f x = 
	let t = Sys.time () in
	let _ = f x in
	Sys.time () -. t
;;

crono (qsort1 (<=)) l3;;
crono (qsort2 (<=)) l3;;
(*
	Tiempo de ejecución aproximado de:
		qsort1 (<=) l2: 0.213
		qsort2 (<=) l2: 0.226 (+ lento)
	Penalización: 0.013
	%tiempo extra: 6.10%
*)

let qsort3 ord l =
	let rec sort_asc = function
		| [] -> []
		| h::t -> let after, before = partition (ord h) t in
				rev_append (sort_des before) (h :: sort_asc after)
	and sort_des = function
		| [] -> []
		| h::t -> let after, before = partition (ord h) t in
				rev_append (sort_asc after) (h :: (sort_des before))
	in sort_asc l
;;

crono (qsort2 (<=)) l1;;
crono (qsort3 (<=)) l1;;
(*
	Tiempo de ejecución aproximado de:
		qsort2 (<=) l1: 2.91 (+ lento)
		qsort3 (<=) l1: 2.56
	Penalización: 0.35
	%tiempo extra: 13.51%
*)

crono (qsort1 (<=)) l3;;
crono (qsort3 (<=)) l3;;
(*
	Tiempo de ejecución aproximado de:
		qsort1 (<=) l2: 0.195
		qsort2 (<=) l2: 0.154
	Penalización: 0.001, practiamente mínima, sus tiempos de ejecución son muy similares
	%tiempo extra: 0.05%(nada)
*)




