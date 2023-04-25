type 'a g_tree = Gt of 'a * 'a g_tree list;;

(*
	let hoja = Gt(0,[]);;
	let tallo = Gt(1,[hoja;hoja;hoja]);;
	let raiz = Gt(2,[tallo;tallo]);;
*)

(*vamos añadiendo al final lista hijos de nodos visitados*)
let rec breadth_first = function 
	| Gt (x, []) -> [x]
	| Gt (x, (Gt (y, t2))::t1) -> x :: breadth_first (Gt (y, t1@t2))
;;

(*utilizamos rev_append(terminal) por si tiene muchisimos hijos, no queremos stack overflow*)
let breadth_first_t a = 
	let rec loop l = function 
		| Gt (x, []) -> List.rev_append (x::l) []
		| Gt (x, (Gt (y, t2))::t1) -> loop (x::l) (Gt(y, List.rev_append (List.rev_append t1 []) t2))
	in loop [] a
;;

(*arbol de altura 1_000_000*)
let t = 	
	let rec loop a = function
		| 0 -> a 
		| x -> loop (Gt(x-1,[a])) (x-1)
	in loop (Gt(1_000_000,[])) 1_000_000
;;