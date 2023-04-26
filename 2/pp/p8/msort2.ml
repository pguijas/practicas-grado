let rec split = function
	[] -> [], []
	| h::[] -> [h],[]
	| h1::h2::t -> let t1,t2 = split t in (h1::t1, h2::t2)
;;

let rec merge ord l1 l2 = match l1,l2 with
	[],l | l,[] -> l
	| h1::t1, h2::t2 -> if ord h1 h2 then h1::merge ord t1 l2
						else h2::merge ord l1 t2
;;

let rec msort1 ord l = match l with
	| [] | _::[] -> l
	| _ -> let l1, l2 = split l in
		merge ord (msort1 ord l1) (msort1 ord l2)
;;

(*
Igual que con qsort la no terminalidad de determinadas operaciones puede propiciarnos 
un stack overflow cuando las listas que pasamos son muy grandes.
*)

let split_t l= 
	let rec loop l (a,b)= match l with
		[] -> (List.rev a,List.rev b)
		| h::[] -> loop [] (h::a, b)
		| h1::h2::t -> loop t (h1::a, h2::b)
	in loop l ([],[])
;;

let merge_t ord l1 l2 = 
	let rec loop l1 l2 lr= match l1,l2 with
		| [],l | l,[] -> List.rev_append lr l
		| h1::t1, h2::t2 -> if ord h1 h2 then loop t1 l2 (h1::lr)
			else loop l1 t2 (h2::lr)
	in loop l1 l2 []
;;

let rec msort2 ord l = match l with
	| [] | _::[] -> l
	| _ -> let l1, l2 = split_t l in
		merge_t ord (msort2 ord l1) (msort2 ord l2)
;;

let crono f x = 
	let t = Sys.time () in
	let _ = f x in
	Sys.time () -. t
;;

let l2= List.init 100_000 (fun _ -> Random.int 100_000);;

(*msort1 (<=) (l2);; Stack Overflow*)
msort2 (<=) (l2);;

let l4= List.init 75_000 (fun _ -> Random.int 1000_000);;

crono (msort1 (<=)) l4;;
crono (msort2 (<=)) l4;;
(*crono (qsort3 (<=)) l4;; 
	podriamos poner un open Qsort;;
	pero en las instrucciones de entrega nos indican que se va a compilar con:
	 ocamlc msort2.mli msort2.ml (con esto no me compila si tengo el open), en vez de con:
	  ocamlc qsort.ml msort2.mli msort2.ml
*)

(*
	Tiempo de ejecución aproximado de:
		msort1 (<=)) l4: 0.107
		msort2 (<=)) l4: 0.130
		qsort3 (<=)) l4: 0.113
	Como podemos observar, para una lista de 75000 elementos, el algoritmo mas veloz es msort1.
		-msort1 es un 21.4% mas veloz que msort2 y un 5.6% más que qsort3.
		-qsort3 es un 15.0% mas veloz que msort2.
		
*)

