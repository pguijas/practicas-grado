#load "talf.cma";;
open Conj;;
open Auto;;
open Ergo;;
open Graf;;

(*
  Ej 1.

Ejemplos:

let afne = Af (
  Conjunto [Estado "0"; Estado "1"; Estado "2"; Estado "3"], 
  Conjunto [Terminal "a"; Terminal "b"; Terminal "c"], 
  Estado "0",
  Conjunto [
    Arco_af (Estado "0", Estado "1", Terminal "a");
    Arco_af (Estado "1", Estado "1", Terminal "b");   
    Arco_af (Estado "1", Estado "2", Terminal "a"); 
    Arco_af (Estado "2", Estado "0", Terminal ""); 
    Arco_af (Estado "2", Estado "3", Terminal ""); 
    Arco_af (Estado "2", Estado "3", Terminal "c")],
  Conjunto [Estado "1"; Estado "3"])
;;

let afn = Af (
  Conjunto [Estado "0"; Estado "1"; Estado "2"; Estado "3"], 
  Conjunto [Terminal "a"; Terminal "b"; Terminal "c"], 
  Estado "0",
  Conjunto [
    Arco_af (Estado "0", Estado "1", Terminal "a");
    Arco_af (Estado "0", Estado "2", Terminal "a");
    Arco_af (Estado "1", Estado "1", Terminal "b");   
    Arco_af (Estado "1", Estado "2", Terminal "a");  
    Arco_af (Estado "2", Estado "3", Terminal "c")],
  Conjunto [Estado "1"; Estado "3"])
;;


let afd = Af (
  Conjunto [Estado "0"; Estado "1"; Estado "2"; Estado "3"], 
  Conjunto [Terminal "a"; Terminal "b"; Terminal "c"], 
  Estado "0",
  Conjunto [
    Arco_af (Estado "0", Estado "1", Terminal "a");
    Arco_af (Estado "1", Estado "1", Terminal "b");   
    Arco_af (Estado "1", Estado "2", Terminal "a");  
    Arco_af (Estado "2", Estado "3", Terminal "c")],
  Conjunto [Estado "1"; Estado "3"])
;;
*)

(*Busca transiciones epsilon transiciones*)
let es_afne = function (Af(_,_,_,arcos,_)) -> 
  let rec loop = function
      | Arco_af(_,_,(Terminal ""))::_ -> true
      | Arco_af(_,_,_)::t -> loop t
      | [] -> false
    in loop (list_of_conjunto(arcos))
;;

(*Buscamos no determinismo a parte de las e-transiciones (estados conducen a mas de 1 estado con el mismo símbolo)*)
let es_afn = function (Af(_,_,_,arcos,_)) -> 
  let rec loop trans_def= function
      | Arco_af(e,_,(Terminal ""))::t -> loop trans_def t (*Ignoramos las e-trans*)
      | Arco_af(e,_,s)::t -> if pertenece (e,s) trans_def then true else loop (agregar (e,s) trans_def) t
      | [] -> false
    in loop (Conjunto ([])) (list_of_conjunto(arcos))
;;

(*ningún no determinismo ni e-transiciones -> afd*)
let es_afd a = not ((||) (es_afne a) (es_afn a));;

(*
  Ej 2.

Ejemplos:

let er_vacia = Vacio;;
let er_cte1 = Constante(Terminal("a"));;
let er_cte2 = Constante(Terminal("b"));;
let er_union = Union(er_cte1,er_cte2);;
let er_concat = Concatenacion(er_cte1,er_cte2);;
let er_repe = Repeticion(er_union);;
let er_chulesca = er_of_string "a.(b|c)*";;
let er_chulesca2 = er_of_string "a.(be|ce)";;

dibuja_af (af_of_er er_union);;
dibuja_af (af_of_er er_concat);;
dibuja_af (af_of_er er_repe);;
dibuja_af (af_of_er er_chulesca);;
dibuja_af (af_of_er er_chulesca2);;
*)

let af_of_er exp = 
  let union_priv i = function Af(e1,alfa1,e_ini1,arcos1,e_fin1) -> function Af(e2,alfa2,e_ini2,arcos2,e_fin2) ->
    let nuevo_estado = Estado (string_of_int i) in 
    Af(
      agregar nuevo_estado (union e1 e2),
      union alfa1 alfa2,
      nuevo_estado,
      union 
        (Conjunto [
          Arco_af (nuevo_estado, e_ini1, Terminal "");
          Arco_af (nuevo_estado, e_ini2, Terminal "")
        ]) 
        (union arcos1 arcos2),
      union e_fin1 e_fin2 
    )
  (*Dada una lista de estados y un estado objetvo traza arcos con e-transiciones desde los estados de la lista hasta el objetivo*)
  in let rec get_epsilons list_est est_objetivo list_arcos = match list_est with
    | h::t -> get_epsilons t est_objetivo ((Arco_af (h, est_objetivo, Terminal ""))::list_arcos)
    | [] -> Conjunto(list_arcos)
  in let concatenacion_priv = function Af(e1,alfa1,e_ini1,arcos1,e_fin1) -> function Af(e2,alfa2,e_ini2,arcos2,e_fin2) ->
    Af(
      union e1 e2,
      union alfa1 alfa2,
      e_ini1,
      union 
        (get_epsilons (list_of_conjunto e_fin1) e_ini2 [])
        (union arcos1 arcos2),
      e_fin2
    )
  in let repeticion_priv i = function Af(e,alfa,e_ini,arcos,e_fin) ->
    let nuevo_estado = Estado (string_of_int i) in 
    Af(
      agregar nuevo_estado e,
      alfa,
      nuevo_estado,
      union 
        (get_epsilons (list_of_conjunto e_fin) nuevo_estado [])
        (agregar (Arco_af (nuevo_estado, e_ini, Terminal "")) arcos),
      Conjunto ([nuevo_estado])
    )

  (*
  Cuerpo de la función:   
    Args: i (entero que usamos para enumerar los estados), er
    Return: Tupla(af,entero que corresponde al numero que tendría un siguiente estado para que no se sobreescriban)
  *)
  in let rec generate i = function
    | Vacio -> 
      (
        (Af (Conjunto [Estado (string_of_int i)], Conjunto [], Estado (string_of_int i), Conjunto [], Conjunto [])),
        i+1
      )
    | Constante(s) -> 
      if s=Terminal("") then (*epsilon*)
        (
          (Af (Conjunto [Estado (string_of_int i)], Conjunto [], Estado (string_of_int i), Conjunto [], Conjunto [Estado (string_of_int i)])),
          i+1
        )
      else
        (
          (Af (
            Conjunto [Estado (string_of_int i); Estado (string_of_int (i+1))], 
            Conjunto [s], 
            Estado (string_of_int i), 
            Conjunto [Arco_af (Estado (string_of_int i), Estado (string_of_int (i+1)), s);], 
            Conjunto [Estado (string_of_int (i+1))]
          )),
          i+2
        )
    | Union(e1,e2) -> 
      let g1 = generate i e1 in
      let g2 = generate ((snd g1)) e2 in
      (
        (union_priv (snd g2) (fst g1) (fst g2) ),
        ((snd g2)+1)
      )
    | Concatenacion(e1,e2) -> 
      let g1 = generate i e1 in
      let g2 = generate ((snd g1)) e2 in
      (
        (concatenacion_priv (fst g1) (fst g2)),
        (snd g2)
      )
    | Repeticion(e) -> 
      let g = generate i e in
      (
        repeticion_priv (snd g) (fst g),
        ((snd g)+1)
      )
  in fst (generate 0 exp)
;;
