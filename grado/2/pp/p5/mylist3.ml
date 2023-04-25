let remove c l = 
	let rec loop c l1 l2= match l1 with
		| [] -> List.rev l2
		| h::t -> if (h=c) then (List.rev l2)@t else loop c t (h::l2)
	in loop c l []
;;

let remove_all a l = List.filter (function x -> x<>a) l;;

let ldif l1 l2 = List.fold_left (fun l x -> remove_all x l) l1 l2;;

let lprod l1 l2 =
	let rec loop l2 = function
		| [] -> []
		| h::t -> ((List.map ((fun x y -> (x,y)) h) l2)::loop l2 t)
	in List.concat (loop l2 l1)
;;

let divide l = 	
	let rec loop x l l1 l2= match x,l with
		| true,h::t -> loop false t (h::l1) l2
		| false,h::t -> loop true t l1 (h::l2)
		| _,[] -> List.rev l1,List.rev l2
	in loop true l [] []
;;

