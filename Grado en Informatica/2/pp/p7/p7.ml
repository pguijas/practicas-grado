(*
let rec suml = function
	[] -> 0
	| h::t -> h + suml t
;;
*)

let suml l= 
	let rec loop s = function
		| [] -> s
		| h::t -> loop (h+s) t
	in loop 0 l
;;

(*
let rec maxl = function
	[] -> raise (Failure "maxl")
	| h::[] -> h
	| h::t -> max h (maxl t)	
;;
*)

let maxl l = 
	let rec loop m = function 
		| [] -> m
		| h::[] -> max h m
		| h::t -> loop (max h m) t
	in try
		loop (List.hd l) (List.tl l)
		with
			| _ -> raise (Failure "maxl")
;;

(*
let rec to0from n =
	if n < 0 then []
	else n :: to0from (n-1)
;;
*)

let to0from n =
	let rec loop l = function
		| 0 -> List.rev (0::l)
		| n -> loop (n::l) (n-1)
	in loop [] n
;;

(*
let rec fromto m n =
	if m > n then []
	else m :: fromto (m+1) n
;;
*)

let fromto m n =
	let rec loop l m n = 
		if (m=n) then (m::l) 
		else if (m>n) then [] 
			else loop (n::l) m (n-1)
	in loop [] m n
;;

(*
let rec from1to n =
	if n < 1 then []
	else from1to (n-1) @ [n]
;;
*)

let from1to n = fromto 1 n;;

(*
let append = List.append;;
*)

let append l1 l2= 
	let rec loop r= function
		| [] -> r
		| h::t -> loop (h::r) t
	in loop l2 (List.rev l1)
;;

(*
let map = List.map;;
*)

let map f l= 
	let rec loop f r = function
		| [] -> List.rev r 
		| h::t -> loop f ((f h)::r) t
	in loop f [] l
;;

(*
let power x y =
	let rec innerpower x y =
		if y = 0 then 1
		else x * innerpower x (y-1)
	in
		if y >= 0 then innerpower x y
		else invalid_arg "power"
;;
*)

let power x y = 
	let rec loop r x = function
		| 0 -> r
		| n -> loop (x*r) x (n-1)
	in
		if y >= 0 then loop 1 x y
		else invalid_arg "power"
;; 

(*
let incseg l =
	List.fold_right (fun x t -> x::List.map ((+) x) t) l []
;;
*)

let incseg l =
	let rec loop l lr= match l with
		| [] -> lr
		| h::t -> loop t (h::(List.map ((+) h) lr))
	in loop (List.rev l) []
;;

(*
let rec remove x = function
	[] -> []
	| h::t -> if x = h then t
		else h :: remove x t
;;
*)

let remove x l = 
	let rec loop x l lr= match l with
		| [] -> List.rev lr
		| h::t -> if (h=x) then (List.rev lr)@t else loop x t (h::lr)
	in loop x l []
;;

(*
let rec insert x = function
	[] -> [x]
	| h::t -> if x <= h then x::h::t
		else h :: insert x t
;;
*)

let insert x l = 
	let rec loop x l lr= match l with
		| [] -> List.rev lr
		| h::t -> if (x<=h) then (List.rev lr)@(x::h::t) else loop x t (h::lr)
	in loop x l []
;;

(*
let rec insert_gen f x l = match l with
	[] -> [x]
	| h::t -> if f x h then x::l
		else h :: insert_gen f x t
;;
*)

let insert_gen f x l = 
	let rec loop f x l lr= match l with
		| [] -> List.rev lr
		| h::t -> if (f x h) then (List.rev lr)@(x::h::t) else loop f x t (h::lr)
	in loop f x l []
;;

