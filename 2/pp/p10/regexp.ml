type symbol = 
	| S_Single of char
	| S_Range of char*char
;;

type regexp =
	| Empty
	| Empty_string 
	| Single of symbol (*ranges incluidos*)
	| Except of symbol
	| Any
	| Concat of regexp * regexp
	| Repeat of regexp
	| Alt of regexp * regexp
	| All of regexp * regexp
;;

let symbol_of_char x = S_Single(x);;

let symbol_of_range x y = if (x>y) then S_Range(y,x) else S_Range(x,y);; 


let empty = Empty_string;;

let empty_string = Empty_string;;

(*este single lo capta como regexp por proximidad*)
let single x = Single(x);;

let except x = Except(x);;

let any = Any;;

let concat x y = Concat(x,y);;

let repeat x = Repeat(x);;

let alt x y = Alt(x,y);;

let all x y = All(x,y);;