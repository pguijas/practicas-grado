let hd = function 
	| [] -> raise (Failure "hd") 
	| h::_ -> h
;;

let tl = function 
	| [] -> raise (Failure "tl") 
	| _::t -> t 
;;

(*Tail Recursive*)
let length l = 
	let rec loop n = function 
		| [] -> n
		| _::t -> loop (n+1) t 
	in loop 0 l
;;

(*Tail Recursive*)
let rec compare_lengths x d = match x,d with
	| [],[] -> 0
	| [], _ -> -1
	| _,[] -> 1
	| _::t1,_::t2 -> compare_lengths t1 t2
;;

(*Tail Recursive*)
let rec nth l n = match l,n with
	| [],_ -> raise(Failure "List.nth")
	| h::_,0 -> h
	| _::t,_ -> nth t (n-1)
;;

let rec append l1 l2 = match l1 with
	| [] -> l2
	| h::t -> h::append t l2
;;

(*Tail Recursive*)
let init n f = 
	let rec loop n f l = match n with
		| -1 -> l
		| n -> loop (n-1) f ((f n)::l)
	in if (0<=n) then loop (n-1) f [] else raise(Failure "List.init")
;;

(*Tail Recursive*)
let rev l=
	let rec loop l1 l2 = match l1 with
		| [] -> l2
		| h::t -> loop t (h::l2)
	in loop l []
;;


(*tail recursive*)
let rec rev_append l1 l2 = match l1 with
	| [] -> l2
	| h::t -> rev_append t (h::l2)
;;

let rec concat = function
	| [] -> []
	| h::t -> append h (concat t)
;;

let flatten x = concat x;;

(*no tail recursive*)
let rec map f l = match l with
	| [] -> []
	| h::t -> (f h)::(map f t)
;;

(*tail recursive*)
let rev_map f l1=
	let rec loop f l1 l2 = match l1 with
		| [] -> l2
		| h::t -> loop f t ((f h)::l2)
	in loop f l1 []
;;

(*no tail recursive*)
let rec map2 f l1 l2 = match l1,l2 with
	| [],[] -> []
	| [], _ -> raise (Invalid_argument "map2")
	| _,[] -> raise (Failure "map2")
	| h1::t1,h2::t2 -> (f h1 h2)::(map2 f t1 t2)
;;

(*tail recursive*)
let rec fold_left f a l = match l with
	| [] -> a
	| h::t -> fold_left f (f a h) t;;
;;
   
(*no tail recursive*)  
let rec fold_right f l a = match l with
	| [] -> a
	| h::t -> f h (fold_right f t a) 
;;
    
(*tail recursive*)
let rec find f = function
	| [] -> raise (Not_found)
	| h::t -> if f h then h else find f t
;;

(*tail recursive*)
let for_all f l = 
	let rec loop f l b = match b,l with
		| _,[] -> true		
		| false,_ -> false
		| true,h::t -> loop f t (f h && b)
	in loop f l true
;;


(*Tail Recursive*)
let rec exists f = function
	| [] -> false
	| h::t -> if (f h) then true else exists f t
;;

(*Tail Recursive*)
let rec mem a = function
	| [] -> false
	| h::t -> if a=h then true else mem a t
;;


(*Tail Recursive*)
let filter f l = 
	let rec loop f l lr= match l with
		| [] -> rev lr
		| h::t -> if f h then loop f t (h::lr) else loop f t lr
	in loop f l []
;;

let rec find_all f l = filter f l;;

(*Tail Recursive*)
let partition f l = 
	let rec loop f l1 l2 = function
		| [] -> (rev l1),(rev l2)
		| h::t -> if f h then loop f (h::l1) l2 t else loop f l1 (h::l2) t
	in loop f [] [] l
;; 

let rec split = function
	| [] -> [],[] 
	| h::t -> fst h::fst (split t), snd h::snd (split t) 
;;


let rec combine l1 l2= match l1,l2 with
	| [],[] -> []
	| [], _ -> raise (Invalid_argument "combine")
	| _,[] -> raise (Failure "combine")
	| h1::t1,h2::t2 -> (h1,h2)::combine t1 t2 
;;
 
  