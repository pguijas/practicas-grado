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
        | (Regla_gic(no_tem,[term]))::t -> if term=s then (loop s (no_tem::l) t) else (loop s l t) 
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


    in let rec gen_prods c1 c2 cS = function
      | [] -> cS
      | (Regla_gic(no_tem,[term1;term2]))::t -> 
        (*cambiar esto por eager*)
        if ((pertenece term1 c1) && (pertenece term2 c2)) then gen_prods c1 c2 (agregar no_tem cS) t 
        else gen_prods c1 c2 cS t
      | _::t -> gen_prods c1 c2 cS t
    
    (*revisar convenio de ij*)
    in let get_simbols i j matrix = (Printf.printf "i %8d j %8d\n" i j; List.nth (List.nth matrix (j-1)) (i-1)) 

    in let generar_celda i j top matrix rp =

      Printf.printf "k max: %8d\n" top;
      let rec loop k i j top c matrix rp =
        Printf.printf "lo intentaremos con: (%8d,%8d) y (%8d,%8d)\n" i k (i+k) (j-k);
        if k>top then
          c
        else
          loop (k+1) i j top (gen_prods (get_simbols i k matrix) (get_simbols (i+k) (j-k) matrix) c rp) matrix rp (*estoy mal (i+k) (j-k)*)
          
      in loop 1 i j top (Conjunto([])) matrix rp

    in let generar_celdas j top matrix rp=
      let rec loop i j top l matrix rp=
        if i>top then
          l
        else 
          (Printf.printf "-gen celda: %8d\n" i;loop (i+1) j top (l@[(generar_celda i j (j-1) matrix rp)]) matrix rp)
      in loop 1 j top [] matrix rp

    in let rec check_last_level axiom = function
      | [last]::[] -> pertenece axiom last
      | h::t -> check_last_level axiom t
      | _ -> raise (Failure "Niveles mal generados")

    in let cyk_loop n axiom matrix rp =
      let rec loop j n axiom matrix rp =
        if j>n then (*revisar que llegue a donde debe*)
          check_last_level axiom matrix
        else 
          (Printf.printf "------Cyk bulce j: %8d\n" j;loop (j+1) n axiom (matrix@[(generar_celdas j (n-j+1) matrix rp)]) rp) (*mirar de poner tail recursive*)
      in loop 2 n axiom matrix rp




    in let get_rp_axiom = function (Gic(_,_,(Conjunto(rp)),axiom)) -> (rp,axiom)
    in let (rp,axiom) = get_rp_axiom gic
    in let lvl1 = inicializar rp cadena
    in cyk_loop (List.length cadena) axiom [lvl1] rp

    (*
    in (generar_celdas 2 ((List.length cadena)-1) [lvl1] rp)
    
    *)
;;

(*

  Casca con j=2, donde se suma j??

*)


cyk cadena_cyk gic_cyk;;

(*
cyk plus : Auto.simbolo list -> Auto.gic -> bool * string list
*)

[
  [
    Conjunto [No_terminal "B"]; 
    Conjunto [No_terminal "B"];
    Conjunto [No_terminal "C"; No_terminal "A"]; 
    Conjunto [No_terminal "B"]
  ];
 [
   Conjunto []; 
   Conjunto [No_terminal "A"; No_terminal "S"];
  Conjunto [No_terminal "C"; No_terminal "S"]
  ];
 [
   Conjunto [No_terminal "A"]; 
   Conjunto [No_terminal "C"; No_terminal "S"]
 ];
 [
   Conjunto [No_terminal "C"; No_terminal "S"]]
  ]