
let hd = function 
	| [] -> raise (Failure "hd") 
	| h::_ -> h
;;

let tl = function 
	| [] -> raise (Failure "hd") 
	| _::t -> t 
;;

let rec length = function 
	| [] -> 0
	| _::t -> 1 + length t 
;;

(*
let compare_lengths l1 l2 = compare (length l1) (length l2);;
*)

let rec compare_lengths x d = match x,d with
	| [],[] -> 0
	| [], _ -> -1
	| _,[] -> 1
	| _::t1,_::t2 -> compare_lengths t1 t2
;;

let rec nth l n = match l,n with
	| [],_ -> raise(Failure "nth")
	| h::_,0 -> h
	| _::t,_ -> nth t (n-1)
;;

let rec append l1 l2 = match l1 with
	| [] -> l2
	| h::t -> h::append t l2
;;

let rec init n f =
	if n=1 then [(f (n-1))] else append (init (n-1) f) [(f (n-1))]
;;

(*no tail recursive*) 
(*
let rec rev l = 
	if length l = 1 then l else append (rev (tl l)) [hd l]
;;
*)

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

let rec fold_left f a l = match l with
	| [] -> a
	| h::t -> fold_left f (f a h) t;;
;;
   
(*no tail recursive*)  
let rec fold_right f l a = match l with
	| [] -> a
	| h::t -> f h (fold_right f t a) 
;;
    

let rec find f = function
	| [] -> raise (Not_found)
	| h::t -> if f h then h else find f t
;;

let rec for_all f = function
	| [] -> true
	| h::t -> f h && for_all f t
;;

let rec exists f = function
	| [] -> false
	| h::t -> f h || exists f t
;;

let rec mem a = function
	| [] -> false
	| h::t -> if a=h then true else mem a t
;;

let rec filter f = function
	| [] -> []
	| h::t -> if f h then h::filter f t else filter f t
;;

let rec find_all f l = filter f l;;

let rec partition f = function
	| [] -> [],[]
	| h::t -> if f h then h::(fst (partition f t)),snd (partition f t) 
					else fst (partition f t), h::snd (partition f t)
;;

let rec split = function
	| [] -> [],[] 
	| h::t -> fst h::fst (split t), snd h::snd (split t) 
;;


let rec combine l1 l2= match l1,l2 with
	| [],[] -> []
	| [], _ -> raise (Invalid_argument "combine")
	| _,[] -> raise (Failure "combine")
	| h1::t1,h2::t2 -> (h1,h2)::combine t1 t2 ;;
 
  
   
    
    
      
       
		