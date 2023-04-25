let f x = x;;
let g x = x,x;;
let h (x,y) = x;;
let i (x,y) = y;;
let j x = [x];;

(*
	¿Cuántas funciones se pueden escribir para cada uno de esos tipos?
		infinitas, ya que en caml hay inifinitos tipos
*)

let rec k x = k x;;
let l x = [k x];;
(*
	¿Cuántas funciones se pueden escribir con esos tipos? ¿Qu´e tienen en com´un las
	funciones con tipo 'a → 'b?

	Un número finito y pequeño, para poder obtener un tipo indefinido tenemos que ayudarnos 
	de la incertidumbre de una función recursiva finita, la cual nunca nos devuelve nada.
	Lo que tienen en común es que el tipo 'b nunca se obtendrá.
*)



(*
	let fun_123_ab f = f ['a';'b'] + f [1;2;3];;
	
	No, no es posible debido al polimorfismo paramétrico de ocaml. La función pasada como parametro 
	coge el 1º tipo y nos porpicia un error al querer usarla con otro tipo.

*)