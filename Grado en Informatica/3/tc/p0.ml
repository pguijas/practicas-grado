(*Ejercicio 1*)
let rec mapdoble f1 f2 = function
  | [] -> []
  | h::t -> (f1 h)::(mapdoble f2 f1 t)
;;
(*
  val mapdoble : ('a -> 'b) -> ('a -> 'b) -> 'a list -> 'b list = <fun>
*)

(*
  mapdoble (function x -> x) (function x -> -x) [1;1;1;1;1];;

  mapdoble (function x -> x*2) (function x -> "x") [1;2;3;4;5];;
  Error de tipos: ('a -> 'b) -> ('a -> 'b) ; (int -> int) ->('a -> string) ; int!=string. La 'a lista no puede tener ints y strings

  let y = function x -> 5 in mapdoble y;;
  - : ('_weak1 -> int) -> '_weak1 list -> int list = <fun>

  Ejemplo de lo anterior:
  let aaaa = let y = function x -> 5 in mapdoble y;;
  aaaa;;
  aaaa (function x -> -x);;
  aaaa;;
*)

(*Ejercicio 2*)
let rec primero_que_cumple p = function
  | [] -> raise (Failure "Ningún elemento machea") 
  | h::t -> if p h then h else primero_que_cumple p t
;;
(*
  val primero_que_cumple : ('a -> bool) -> 'a list -> 'a = <fun>
*)

let existe p l = 
  try primero_que_cumple p l;true
  with Failure _ -> false
;;

let asociado l clave =
  snd (primero_que_cumple (function x -> fst x = clave) l)
;;
(*
  let map = ["uno",1;"dos",2;"tres",3;"cuatro",4;"cinco",5];;
  asociado map "cinco";;
  existe (function x -> snd x=10) map;;
  primero_que_cumple (function x -> snd x=5) map;;
*)

(*Ejercicio 3*)
type 'a arbol_binario =
  | Vacio
  | Nodo of 'a * 'a arbol_binario * 'a arbol_binario
;;

(*
  let hoja x = Nodo(x,Vacio,Vacio);;
  let arbol_pruebas = 
    Nodo(3,
      (hoja 2),
      Nodo(5,
        (hoja 4),
        (hoja 1)
      )
    )
  ;;
*)

let rec in_orden = function
  | Vacio -> []
  | Nodo(x,h1,h2) ->  (in_orden h1) @ ((x::in_orden h2))
;;

let rec pre_orden = function
  | Vacio -> []
  | Nodo(x,h1,h2) -> x::((pre_orden h1) @ (pre_orden h2))
;;

let rec post_orden = function
  | Vacio -> []
  | Nodo(x,h1,h2) -> (post_orden h1) @ (post_orden h2) @ [x]
;;

(*
  2 listas, una para guardar os valores de los nodos (tail recursive)
  y otra para establecer un orden "por niveles"
*)
let anchura a = 
  let rec loop l = function
    | [] -> List.rev l
    | Vacio::t -> loop l t
    | Nodo(x,Vacio,Vacio)::t -> loop (x::l) t
    | Nodo(x,Vacio,h)::t  (*redundante: hijo vacio lo saltaría*)
    | Nodo(x,h,Vacio)::t -> loop (x::l) (t@[h])
    | Nodo(x,h1,h2)::t -> loop (x::l) (t@[h1;h2])
  in loop [] [a]
 ;;

(*Ejercicio 4*)
(*algunas operaciones cambian el orden pero da igual, sigue siendo el mismo conjunto (se hace por la eficiencia)*)
type 'a conjunto = Conjunto of 'a list;;

(*
  let conjunto_vacio = Conjunto [];;
  let c1 = Conjunto ["a";"b";"c";"d";"e"];;
  let c2 = Conjunto ["c";"d"];;
*)

let rec pertenece a = function
  | Conjunto(h::t) -> if h=a then true else pertenece a (Conjunto(t))
  | Conjunto([]) -> false
;;

let agregar a = function Conjunto(x) ->
  if (pertenece a (Conjunto(x))) then Conjunto(x)
  else Conjunto(a::x)
;;

let conjunto_of_list l = 
  let rec loop l c = match l with
    | h::t -> loop t (agregar h c)
    | [] -> c
  in loop l (Conjunto([]))
;;

let suprimir a c =
  let rec loop a l c = match c with
    | Conjunto(h::t) -> if (a=h) then Conjunto(l@t) else loop a (h::l) (Conjunto(t))
    | Conjunto([]) -> Conjunto(l)
  in loop a [] c
;;

let cardinal c = 
  let rec loop c n = match c with
   | Conjunto([]) -> n
   | Conjunto(h::t) -> loop (Conjunto(t)) n+1
  in loop c 0
;;

let rec union c1 = function
  | Conjunto([]) -> c1
  | Conjunto(h::t) -> union (agregar h c1) (Conjunto(t))
;;

let interseccion c1 c2 =
  let rec loop c1 c2 lR = match c1 with
    | Conjunto([]) -> Conjunto(lR)
    | Conjunto(h::t) -> if (pertenece h c2) then loop (Conjunto(t)) c2 (h::lR) else loop (Conjunto(t)) c2 lR
  in loop c1 c2 []
;;

(*C1-C2*)
let rec diferencia c1 = function
  | Conjunto([]) -> c1
  | Conjunto(h::t) -> diferencia (suprimir h c1) (Conjunto(t))
;;

(*C1 pertenece C2*)
let rec incluido c1 c2 = 
  match c1 with
    | Conjunto([]) -> true
    | Conjunto(h::t) -> if (pertenece h c2) then (incluido (Conjunto(t)) c2) else false
;;

(*
usamos (&&) y no && dado que nos interesa que nos interesa la Lazy Evaluation
*)
let igual c1 c2 = (&&) ((cardinal c1)=(cardinal c2)) (incluido c1 c2);;

let producto_cartesiano c1 c2 = 
  let rec obtener_pares a = function
    | Conjunto([]) -> []
    | Conjunto(h::t) -> (a,h)::(obtener_pares a (Conjunto(t)))
  in let rec loop c1 c2 = match c1 with
    | Conjunto([]) -> []
    | Conjunto(h::t) -> (obtener_pares h c2)@(loop (Conjunto(t)) c2)
  in Conjunto(loop c1 c2)
;;

let list_of_conjunto = function Conjunto(l) -> l;;