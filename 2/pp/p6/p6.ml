let split l = List.map fst l, List.map snd l;;

let combine l1 l2 = let par e1 e2 = e1,e2 
	in try
		List.map2 par l1 l2
	with _ -> raise (Invalid_argument "List.combine")
;;

let length l = let sumador x y = x + 1
	in List.fold_left sumador 0 l
;;

let append l1 l2 = let f a l1 = a::l1
	in List.fold_right f l1 l2
;;

let rev l = let f l1 a = a::l1
	in List.fold_left f [] l
;;

let concat l = List.fold_left (@) [] l;;

(*
let concatf l = List.fold_right (@) l [];;

¿Cambia la complejidad al usar fold left frente a fold right?
	Sería más costoso y más complejo, por la manera en la que está implementada (@),
	la cual tiene que recorrer los elementos de la 1º lista e ir añadiendolos a la 2.
*)

let partition f l = 
	let negado f a = if (f a) then false else true
	in (List.filter f l),(List.filter (negado f) l)
;;

let remove_all a l = List.filter (function x -> x<>a) l;;

let ldif l1 l2 = List.fold_left (fun l x -> remove_all x l) l1 l2;;

let lprod l1 l2 =
	let rec loop l2 = function
		| [] -> []
		| h::t -> ((List.map ((fun x y -> (x,y)) h) l2)::loop l2 t)
	in List.concat (loop l2 l1)
;;

let comp a b c = a (b c);;
let multicomp l = List.fold_right comp l (function x -> x);;