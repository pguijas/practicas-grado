open Gtree;;
open Bintree;;

let cod_as_bin (Gt (r,h))=
	let rec loop = function
		| []-> Empty
		| (Gt(r',l))::t -> Node (r',loop l , loop t)
	in Node (r,loop h , Empty)
;;


let decod_from_bin = function
	| Empty-> raise(Invalid_argument "dec_from_bin")
	| Node (x,h1,_)->let rec loop =function
		| Empty->[]
		| Node (r,h1,h2)->[Gt (x,loop h1)] @ loop h2
		in Gt (x,loop h1)
;;