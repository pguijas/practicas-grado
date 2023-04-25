open Regexp;;

let regexp_of_string s = 
	Regexp_parser.main Regexp_lex.token (Lexing.from_string s)
;;


let rec nullable = function
	| Empty -> Empty
	| Empty_string -> Empty_string
	| Single(_) -> Empty
	| Except(_) -> Empty
	| Any -> Empty
	| Concat(e1,e2) -> 
		if (nullable(e1)=Empty_string && nullable(e2)=Empty_string) 
			then Empty_string 
			else Empty
	| Repeat(_) -> Empty_string
	| Alt(e1,e2) -> 		
		if (nullable(e1)=Empty_string || nullable(e2)=Empty_string) 
			then Empty_string 
			else Empty
	| All(e1,e2) -> 
		if (nullable(e1)=Empty_string && nullable(e2)=Empty_string) 
			then Empty_string 
			else Empty
;;

let rec derive c = function
	| Empty -> Empty
	| Empty_string -> Empty
	| Single(s) -> let single s = match s with
		| S_Single(a) -> if (a=c) then Empty_string else Empty
		| S_Range(a,b) -> if (a<=c && c<=b) then Empty_string else Empty
		in single s
	| Except(s) -> let except s = match s with
		| S_Single(a) -> if (a=c) then Empty else Empty_string
		| S_Range(a,b) -> if (a<=c && c<=b) then Empty else Empty_string
		in except s
	| Any -> Empty_string
	| Concat(e1,e2) ->  Alt(Concat((derive c e1),e2),Concat((nullable e1),(derive c e2))) 
	| Repeat(e) -> Concat((derive c e),Repeat(e))
	| Alt(e1,e2) -> Alt((derive c e1), (derive c e2))
	| All(e1,e2) -> All((derive c e1), (derive c e2))
;;

let rec simplify = function
	| Concat(Empty,_) 
	| Concat(_,Empty) -> Empty 
	| Concat(Empty_string,e)
	| Concat(e,Empty_string) -> simplify e
	| Concat(e1,e2) -> Concat((simplify e1),(simplify e2))
	| Repeat(Empty_string) -> Empty_string
	| Repeat(Empty) -> Empty
	| Repeat(e) -> Repeat(simplify e)
	| Alt(Empty,e) 
	| Alt(e,Empty) -> simplify e
	| Alt(e1,e2) -> Alt((simplify e1),(simplify e2))
	| All(Empty,e) 
	| All(e,Empty) -> Empty 
	| All(e1,e2) -> All((simplify e1),(simplify e2))
	| r -> r 
;;

let matches_regexp s r= 
	let rec loop s r = function
		| 0 -> (nullable r) = Empty_string
		| i ->  loop s (simplify (derive (String.get s ((String.length s)-i)) r)) (i-1)
	in loop s r (String.length s) 
;;

let matches s sp = matches_regexp s (regexp_of_string sp);;