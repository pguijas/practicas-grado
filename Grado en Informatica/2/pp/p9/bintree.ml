type 'a bin_tree = 
	| Empty 
	| Node of 'a * 'a bin_tree * 'a bin_tree
;;

(*
	let hoja = Node(0,Empty,Empty);;
	let tallo = Node(1,hoja,hoja);;
	let raiz = Node(2,tallo,tallo);;
*)

(*nos apoyamos en una cola*)
let breadth_first a = 
	let rec loop l = function
		| [] -> List.rev l
		| Empty::t -> loop l t
		| Node(x,Empty,Empty)::t -> loop (x::l) t
		| Node(x,Empty,h)::t 
		| Node(x,h,Empty)::t -> loop (x::l) (t@[h])
		| Node(x,h1,h2)::t -> loop (x::l) (t@[h1;h2])
	in loop [] [a]
;;

