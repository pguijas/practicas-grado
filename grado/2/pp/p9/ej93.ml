open Bintree;;

let rec strict = function
	| Empty -> true
	| Node(_,Empty,Empty)->true
	| Node(_,_,Empty)->false
	| Node(_,Empty,_)->false
	| Node(_,h1,h2)->true && strict h1 && strict h2
;;

let perfect = function
	| Empty -> true
	| Node(x,h1,h2) -> 
		let rec altura = function
			| Empty -> 0
			| Node(_,Empty,Empty)->1
			| Node(_,h,Empty)
			| Node(_,Empty,h)-> 1 + altura h
			| Node(_,h1,h2)-> 1 + max (altura h1) (altura h2)
		in (strict (Node(x,h1,h2))) && (altura h1 = altura h2)
;;

let complete a = 
    let rec loop r l = match r,l with
        | r,[] -> r
        | r,Empty::t -> loop r t
        | false,Node(x,Empty,Empty)::t -> loop true t
        | true,Node(x,Empty,Empty)::t -> loop true t
        | false,Node(x,Empty,h)::t -> false  
        | false,Node(x,h,Empty)::t -> loop true (t@[h])                
        | true,Node(x,Empty,h)::t 
        | true,Node(x,h,Empty)::t -> false
        | false,Node(x,h1,h2)::t -> loop false (t@[h1;h2])
        | true,Node(x,h1,h2)::t -> false
    in loop (false) [a]
;;  
