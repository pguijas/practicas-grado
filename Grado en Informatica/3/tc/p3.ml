#load "talf.cma";;
open Conj;;
open Auto;;
open Ergo;;
open Graf;;

(*revisar tipados*)

(*
Gic of (simbolo conjunto * simbolo conjunto * regla_gic conjunto * simbolo);;
*)

(*
let gic1 = Gic (
  Conjunto [No_terminal "S"; No_terminal "A"; No_terminal "B"], 
  Conjunto [Terminal "a"; Terminal "b"; Terminal "c"], 
  Conjunto [
    Regla_gic (No_terminal "S", [Terminal "a"; No_terminal "A"]); 
    Regla_gic (No_terminal "A",[Terminal "a"; Terminal "b"; Terminal "c"; No_terminal "A"]);
    Regla_gic (No_terminal "A", [Terminal "b"; No_terminal "B"]); 
    Regla_gic (No_terminal "B",[Terminal "b"; Terminal "c"; No_terminal "B"]); 
    Regla_gic (No_terminal "B", [])
  ], 
  No_terminal "S"
);;

let gic_fnc = Gic (
  Conjunto [No_terminal "S"; No_terminal "A"; No_terminal "B"], 
  Conjunto [Terminal "a"; Terminal "b"; Terminal "c"], 
  Conjunto [
    Regla_gic (No_terminal "S", [No_terminal "B"; No_terminal "A"]); 
    Regla_gic (No_terminal "A",[Terminal "a"]);
    Regla_gic (No_terminal "B", [Terminal "b"]); 
    Regla_gic (No_terminal "B", [])
  ], 
  No_terminal "S"
);;

let gic_no_fnc = Gic (
  Conjunto [No_terminal "S"; No_terminal "A"; No_terminal "B"], 
  Conjunto [Terminal "a"; Terminal "b"; Terminal "c"], 
  Conjunto [
    Regla_gic (No_terminal "S", [No_terminal "B"; No_terminal "A"; No_terminal "A"]); 
    Regla_gic (No_terminal "A",[Terminal "a"]);
    Regla_gic (No_terminal "B", [Terminal "b"]); 
    Regla_gic (No_terminal "B", [])
  ], 
  No_terminal "S"
);;
*)

(*
  Duda, y esta regla -> Regla_gic (No_terminal "B", []) ¿?¿?¿?
*)


(*
  Precond: gic bien formada
  FNC: A -> a | A -> BC

  k hacer: 
    - si tiene un no term el cual no esté solo ya no es válido
    - si son terms -> tienen k ser 2
*)

(*Revisar y pulir bien*)
(* Usamos solo los nt dado que suponemos que la gic está bien formada y si no es un no term es un term y viceversa*)
let es_fnc = function (Gic(nt,t,(Conjunto(rp)),_)) -> 

  (*Comprobar regla (usamos un indicador para saber si es el primer simbolo)*)
  let rec check nt primero = function
    | [] -> true 
    | h::[] -> if (pertenece h nt) then (not primero) else primero  
    | h::t ->   
      if (pertenece h nt) then (*No term*)
        if (primero) then (check nt false t) else false
      else false               (*Term*)
                   
  (*Para cada regla*)
  in let rec loop nt = function
    | [] -> true 
    | (Regla_gic(_,simbolos))::t -> if (check nt true simbolos) then loop nt t else false
  in loop nt rp
;;

(*
Ej 1:
  let g_ej = Gic (
    Conjunto [No_terminal "S"; No_terminal "A"; No_terminal "B"; No_terminal "C"], 
    Conjunto [Terminal "a"; Terminal "b"], 
    Conjunto [
      Regla_gic (No_terminal "S", [No_terminal "A"; No_terminal "B"]);
      Regla_gic (No_terminal "S", [No_terminal "B"; No_terminal "C"]); 
      Regla_gic (No_terminal "A", [No_terminal "B"; No_terminal "A"]); 
      Regla_gic (No_terminal "A",[Terminal "a"]);
      Regla_gic (No_terminal "B", [No_terminal "C"; No_terminal "C"]); 
      Regla_gic (No_terminal "B", [Terminal "b"]); 
      Regla_gic (No_terminal "C", [No_terminal "A"; No_terminal "B"]); 
      Regla_gic (No_terminal "C",[Terminal "a"]);
    ], 
    No_terminal "S"
  );;

  let c_ej1 = cadena_of_string "b b a b";;

  let c_ej2 = cadena_of_string "b b a b";;
  
*)

(*
  1º Recorrer reglas nivel base
*)

type trazado =
  | Traza_prima of simbolo*simbolo
  | Traza of (simbolo * (simbolo * int * int)  * (simbolo * int * int))
;;

let cyk cadena = function gic -> 
  if (not (es_fnc gic)) then 
    raise (Failure "No está en FNC")
  else if ((List.length cadena)=0) then
    raise (Failure "La cadena de entrada debe tener al menos un símbolo")
  else 
    (*Reglas para hojas -> está en FNC por lo que las reglas de producción que buscamos son del formato A->a*)
    let buscar_reglas s rp =
      let rec loop s l = function
        | [] -> Conjunto (l)
        | (Regla_gic(no_tem,[term]))::t -> if term=s then (loop s ((Traza_prima(no_tem,term))::l) t) else (loop s l t) 
        | (Regla_gic(_,_))::t -> (loop s l t)
      in loop s [] rp (*tail recursive*)

    (*habrá que comprobar que ninguno quede vacío ¿?¿?*)
    in let rec inicializar rp = function
      | [] -> [] 
      | h::t -> (buscar_reglas h rp)::(inicializar rp t)

    (*
      dados 2 cjtos de posibles símbolos, 
      calculamos que reglas (FNC) podrían generar cualquien combinación de 1s de c1 y 1s de c2.
      Añadimos los no terminales de las reglas a cS
    *)


    in let rec gen_prods c1 i1 j1 c2 i2 j2 cS = function
      | [] -> cS
      | (Regla_gic(no_tem,[term1;term2]))::t -> 
        (*cambiar esto por eager*)
        if ((pertenece term1 c1) && (pertenece term2 c2)) then gen_prods c1 i1 j1 c2 i2 j2 (agregar (Traza(no_tem,(term1,i1,j1),(term2,i2,j2))) cS) t 
        else gen_prods c1 i1 j1 c2 i2 j2 cS t
      | _::t -> gen_prods c1 i1 j1 c2 i2 j2 cS t
    
    (*cambios aqui*)
    in let rec get_heads = function
      | Traza(head,_,_)::t  -> head::(get_heads (t))
      | Traza_prima(head,_)::t  -> head::(get_heads (t))
      | [] -> []

    in let get_simbols i j matrix = Conjunto (get_heads (list_of_conjunto (List.nth (List.nth matrix (j-1)) (i-1))))

    in let generar_celda i j top matrix rp =
      let rec loop k i j top c matrix rp =
        if k>top then
          c
        else
          loop (k+1) i j top (gen_prods (get_simbols i k matrix) i k (get_simbols (i+k) (j-k) matrix) (i+k) (j-k) c rp) matrix rp (*estoy mal (i+k) (j-k)*)
      in loop 1 i j top (Conjunto([])) matrix rp


    in let generar_celdas j top matrix rp=
      let rec loop i j top l matrix rp=
        if i>top then
          l
        else 
          loop (i+1) j top (l@[(generar_celda i j (j-1) matrix rp)]) matrix rp
      in loop 1 j top [] matrix rp

    in let rec check_last_level axiom = function
      | [last]::[] -> pertenece axiom (Conjunto(get_heads (list_of_conjunto last)))
      | h::t -> check_last_level axiom t
      | _ -> raise (Failure "Niveles mal generados")

    (************************)
    (******            ******)
    (************************)
    in let get_pos i j matrix =  (List.nth (List.nth matrix (j-1)) (i-1))

    in let str_of_simbol = function 
      | No_terminal (a) -> a
      | Terminal (a) -> a

    in let rec fusion primero l ultimo = match l with
      | h::t -> (primero^h^ultimo)::(fusion primero t ultimo)
      | [] -> []
    
    in let rec fusion2 primero l1 medio l2 ultimo =  match l2 with
      | h::t -> (fusion primero l1 (medio^h^ultimo))@(fusion2 primero l1 medio t ultimo)
      | [] -> []

    in let rec foreach simbol matrix = function
      | Conjunto(Traza(s,(s1,i1,j1),(s2,i2,j2))::t)  -> 
        if s=simbol then
          (fusion2 
            (((str_of_simbol simbol) ^  " ( "))                     (*primero*)
            (foreach s1 matrix (get_pos i1 j1 matrix))              (*l1*)
            (" ) " ^ "( ")                                          (*medio*)
            (foreach s2 matrix (get_pos i2 j2 matrix))              (*l2*)
            (" ) ")                                                 (*ultimo*)
          )@(foreach simbol matrix (Conjunto(t)))
          
        else 
         foreach simbol matrix (Conjunto(t))
      | Conjunto(Traza_prima(s1,s2)::t) -> 
        if s1=simbol then
          [(str_of_simbol s1) ^ "  " ^ (str_of_simbol s2)]
        else
          foreach simbol matrix (Conjunto(t))
      | Conjunto([]) -> []

    in let recorrer_trazas axiom n matrix = foreach axiom matrix (get_pos 1 n matrix)

    in let cyk_loop n axiom matrix rp =
      let rec loop j n axiom matrix rp =
        if j>n then 
          (
            check_last_level axiom matrix,
            recorrer_trazas axiom n matrix
          )
        else 
          loop (j+1) n axiom (matrix@[(generar_celdas j (n-j+1) matrix rp)]) rp (*mirar de poner tail recursive*)
      in loop 2 n axiom matrix rp

    in let get_rp_axiom = function (Gic(_,_,(Conjunto(rp)),axiom)) -> (rp,axiom)
    in let (rp,axiom) = get_rp_axiom gic
    in let lvl1 = inicializar rp cadena
    in cyk_loop (List.length cadena) axiom [lvl1] rp
;;

cyk c_ej1 g_ej;;
