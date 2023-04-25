let f = function x -> function y -> function z ->
(z > y) || ((x <> y) && (z / (x - y) > y));;

let f = function x -> function y -> function z ->
if (z > y) then true else (if (x <> y) then (z / (x - y) > y) else false);;




false && (2 / 0 > 0);;
(* - : bool = false *)

true && (2 / 0 > 0);;
(* casca: division x 0 *)

true || (2 / 0 > 0);;
(* - : bool = true *)

false || (2 / 0 > 0);;
(* casca: division x 0 *)

let con = (&&);;
(*val con : bool -> bool -> bool = <fun>*)

let dis = (||);;
(*val dis : bool -> bool -> bool = <fun>*)

(&&) (1 < 0) (2 / 0 > 0);;
(* - : bool = false *)

con (1 < 0) (2 / 0 > 0);;
(* casca al dividir entre 0, pasa a ser eager*)

(||) (1 > 0) (2 / 0 > 0);;
(* - : bool = false *)

dis (1 > 0) (2 / 0 > 0);;
(* casca al dividir entre 0, pasa a ser eager*)



let curry a b c = a(b,c);;
let uncurry a (b,c) = a b c;;

uncurry (+);;
(*- : int * int -> int = <fun>*)

let sum = (uncurry (+));;
(*val sum : int * int -> int = <fun>*)

(*sum 1;;
error, espera un producto cartesiano
*)

sum (2,1);;
(*- : int = 3*)

let g = curry (function p -> 2 * fst p + 3 * snd p);;
(*val g : int -> int -> int = <fun>*)

(*g (2,5);;
error
*)

let h = g 2;;
(*val h : int -> int = <fun>*)

h 1, h 2, h 3;;
(*- : int * int * int = (7, 10, 13)*)

(*comp : (’a -> ’b) -> (’c -> ’a) -> (’c -> ’b)*)
let comp a b c = a (b c);;

let f2 = let square x = x * x in comp square ((+) 1);;
(*val f2 : int -> int = <fun>*)

f2 1, f2 2, f2 3;;
(*- : int * int * int = (4, 9, 16)*)






