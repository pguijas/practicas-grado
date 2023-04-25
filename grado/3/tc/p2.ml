#load "talf.cma";;
open Conj;;
open Auto;;
open Ergo;;
open Graf;;

let es_fnc = function (Gic(nt,t,(Conjunto(rp)),_)) -> 
  (*
    Nos centramos solo en las partes derechas de las reglas -> AB | a
  *)
  let rec check = function
    | (Terminal (_))::[] -> true   
    | (No_terminal (_))::[No_terminal (_)] -> true
    | _ -> false

  (*Para cada regla*)
  in let rec loop nt = function
    | [] -> true 
    | (Regla_gic(_,simbolos))::t -> if (check simbolos) then loop nt t else false
  in loop nt rp
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
        | (Regla_gic(no_tem,[term]))::t -> if term=s then (loop s (no_tem::l) t) else (loop s l t) 
        | (Regla_gic(_,_))::t -> (loop s l t)
      in loop s [] rp (*tail recursive*)

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
        (*Lazy Evaluation*)
        if ((&&) (pertenece term1 c1) (pertenece term2 c2)) then gen_prods c1 c2 (agregar no_tem cS) t 
        else gen_prods c1 c2 cS t
      | _::t -> gen_prods c1 c2 cS t
    
    in let get_simbols i j matrix = List.nth (List.nth matrix (j-1)) (i-1)

    (*k*)
    in let generar_celda i j top matrix rp =
      let rec loop k i j top c matrix rp =
        if k>top then
          c
        else
          loop (k+1) i j top (gen_prods (get_simbols i k matrix) (get_simbols (i+k) (j-k) matrix) c rp) matrix rp
      in loop 1 i j top (Conjunto([])) matrix rp

    (*i*)
    in let generar_celdas j top matrix rp=
      let rec loop i j top l matrix rp=
        if i>top then
          l
        else 
          loop (i+1) j top (l@[(generar_celda i j (j-1) matrix rp)]) matrix rp
      in loop 1 j top [] matrix rp

    in let rec check_last_level axiom = function
      | [last]::[] -> pertenece axiom last
      | h::t -> check_last_level axiom t
      | _ -> raise (Failure "Niveles mal generados")

    (*j*)
    in let cyk_loop n axiom matrix rp =
      let rec loop j n axiom matrix rp =
        if j>n then 
          check_last_level axiom matrix
        else 
          loop (j+1) n axiom (matrix@[(generar_celdas j (n-j+1) matrix rp)]) rp 
      in loop 2 n axiom matrix rp

    in let get_rp_axiom = function (Gic(_,_,(Conjunto(rp)),axiom)) -> (rp,axiom)
    in let (rp,axiom) = get_rp_axiom gic
    in let lvl1 = inicializar rp cadena
    in cyk_loop (List.length cadena) axiom [lvl1] rp
;;

(************************)
(******  cyk plus  ******)
(************************)

type trazado =
  | Traza_prima of simbolo*simbolo
  | Traza of (simbolo * (simbolo * int * int)  * (simbolo * int * int))
;;

let cyk_plus cadena = function gic -> 
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

    in let rec inicializar rp = function
      | [] -> [] 
      | h::t -> (buscar_reglas h rp)::(inicializar rp t)

    (*Ahora añadimos trazas en vez de No Terminales únicamente*)
    in let rec gen_prods c1 i1 j1 c2 i2 j2 cS = function
      | [] -> cS
      | (Regla_gic(no_tem,[term1;term2]))::t -> 
        (*Lazy eval*)
        if ((&&) (pertenece term1 c1) (pertenece term2 c2)) then gen_prods c1 i1 j1 c2 i2 j2 (agregar (Traza(no_tem,(term1,i1,j1),(term2,i2,j2))) cS) t 
        else gen_prods c1 i1 j1 c2 i2 j2 cS t
      | _::t -> gen_prods c1 i1 j1 c2 i2 j2 cS t
    
    (*Obtenemos cabezas de las trazas*)
    in let rec get_heads = function
      | Traza(head,_,_)::t  -> head::(get_heads (t))
      | Traza_prima(head,_)::t  -> head::(get_heads (t))
      | [] -> []

    in let get_simbols i j matrix = Conjunto (get_heads (list_of_conjunto (List.nth (List.nth matrix (j-1)) (i-1))))

    (*k*)
    in let generar_celda i j top matrix rp =
      let rec loop k i j top c matrix rp =
        if k>top then
          c
        else
          loop (k+1) i j top (gen_prods (get_simbols i k matrix) i k (get_simbols (i+k) (j-k) matrix) (i+k) (j-k) c rp) matrix rp (*estoy mal (i+k) (j-k)*)
      in loop 1 i j top (Conjunto([])) matrix rp

    (*i*)
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

    (****************************************)
    (******    Parte que se centra     ******)
    (******    en seguir el trazado    ******)
    (****************************************)

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

    (*Para todas las Trazas*)
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

    (*j*)
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


(************************)
(******  cyk prob  ******)
(************************)

type regla_gicp =
  Regla_gicp of (simbolo * simbolo list * float);;

type gicp =
  Gicp of (simbolo conjunto * simbolo conjunto * regla_gicp conjunto * simbolo);;

type trazadop =
  | TrazaP_prima of simbolo*simbolo*float
  | TrazaP of (simbolo * (simbolo * int * int)  * (simbolo * int * int) * float)
;;

let gicp_of_gic = function (Gic(nt,t,(Conjunto(rp)),axiom)) -> function prob_list ->
  let rec loop rp prob_list = match (rp,prob_list) with
    | (((Regla_gic(s,sl))::t1),(prob::t2)) -> (Regla_gicp(s,sl,prob))::(loop t1 t2)
    | ([],[]) -> []
    | ([],_)
    | (_,[]) -> raise (Failure "El nº de probabilidads y reglas debe ser el mismo")
  in (Gicp(nt,t,(Conjunto(loop rp prob_list)),axiom))
;;

let es_fncp = function (Gicp(nt,t,(Conjunto(rp)),_)) -> 
  (*Nos centramos solo en las partes derechas de las reglas -> AB | a*)
  let rec check = function
    | (Terminal (_))::[] -> true   
    | (No_terminal (_))::[No_terminal (_)] -> true
    | _ -> false
               
  (*Para cada regla*)
  in let rec loop nt = function
    | [] -> true 
    | (Regla_gicp(_,simbolos,_))::t -> if (check simbolos) then loop nt t else false
  in loop nt rp
;;

let cyk_prob cadena = function gic -> 
  if (not (es_fncp gic)) then 
    raise (Failure "No está en FNC")
  else if ((List.length cadena)=0) then
    raise (Failure "La cadena de entrada debe tener al menos un símbolo")
  else 
    (*Reglas para hojas -> está en FNC por lo que las reglas de producción que buscamos son del formato A->a*)
    let buscar_reglas s rp =
      let rec loop s l = function
        | [] -> Conjunto (l)
        | (Regla_gicp(no_tem,[term],prob))::t -> if term=s then (loop s ((TrazaP_prima(no_tem,term,prob))::l) t) else (loop s l t) 
        | (Regla_gicp(_,_,_))::t -> (loop s l t)
      in loop s [] rp (*tail recursive*)

    in let rec inicializar rp = function
      | [] -> [] 
      | h::t -> (buscar_reglas h rp)::(inicializar rp t)

    (*Ahora añadimos trazas en vez de No Terminales únicamente*)
    in let rec gen_prods c1 i1 j1 c2 i2 j2 cS = function
      | [] -> cS
      | (Regla_gicp(no_tem,[term1;term2],prob))::t -> 
        if ((&&) (pertenece term1 c1) (pertenece term2 c2)) then gen_prods c1 i1 j1 c2 i2 j2 (agregar (TrazaP(no_tem,(term1,i1,j1),(term2,i2,j2),prob)) cS) t 
        else gen_prods c1 i1 j1 c2 i2 j2 cS t
      | _::t -> gen_prods c1 i1 j1 c2 i2 j2 cS t
    
    (*Obtenemos cabezas de las trazas*)
    in let rec get_heads = function
      | TrazaP(head,_,_,_)::t  -> head::(get_heads (t))
      | TrazaP_prima(head,_,_)::t  -> head::(get_heads (t))
      | [] -> []

    in let get_simbols i j matrix = Conjunto (get_heads (list_of_conjunto (List.nth (List.nth matrix (j-1)) (i-1))))

    (*k*)
    in let generar_celda i j top matrix rp =
      let rec loop k i j top c matrix rp =
        if k>top then
          c
        else
          loop (k+1) i j top (gen_prods (get_simbols i k matrix) i k (get_simbols (i+k) (j-k) matrix) (i+k) (j-k) c rp) matrix rp (*estoy mal (i+k) (j-k)*)
      in loop 1 i j top (Conjunto([])) matrix rp

    (*i*)
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

    in let get_pos i j matrix =  (List.nth (List.nth matrix (j-1)) (i-1))

    in let str_of_simbol = function 
      | No_terminal (a) -> a
      | Terminal (a) -> a

    (****************************)
    (******   "Chicha" de  ******)
    (******  este apartado ******)
    (****************************)

    in let rec fusion primero l ultimo prob= match l with
      | (h,prob1)::t -> ((primero^h^ultimo),(prob1*.prob))::(fusion primero t ultimo prob)
      | [] -> []
    
    in let rec fusion2 primero l1 medio l2 ultimo prob=  match l2 with
      | (h,prob1)::t -> (fusion primero l1 (medio^h^ultimo) (prob1*.prob))@(fusion2 primero l1 medio t ultimo prob)
      | [] -> []

    in let rec foreach simbol matrix = function
      | Conjunto(TrazaP(s,(s1,i1,j1),(s2,i2,j2),prob)::t)  -> 
        if s=simbol then
          let r1 = (foreach s1 matrix (get_pos i1 j1 matrix))
          in let r2 = (foreach s2 matrix (get_pos i2 j2 matrix))
          in
            (
              (fusion2 
                (((str_of_simbol simbol) ^  " ( "))         (*primero*)
                r1                                          (*l1*)
                (" ) " ^ "( ")                              (*medio*)
                r2                                          (*l2*)
                (" )")                                     (*ultimo*)
                prob
              )
            )@(foreach simbol matrix (Conjunto(t)))
        else 
         foreach simbol matrix (Conjunto(t))
      | Conjunto(TrazaP_prima(s1,s2,prob)::t) -> 
        if s1=simbol then
          [((str_of_simbol s1) ^ "  " ^ (str_of_simbol s2),prob)]
        else
          foreach simbol matrix (Conjunto(t))
      | Conjunto([]) -> []

    in let recorrer_trazas axiom n matrix = foreach axiom matrix (get_pos 1 n matrix)

    (*j*)
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

    in let get_rp_axiom = function (Gicp(_,_,(Conjunto(rp)),axiom)) -> (rp,axiom)
    in let (rp,axiom) = get_rp_axiom gic
    in let lvl1 = inicializar rp cadena
    in cyk_loop (List.length cadena) axiom [lvl1] rp
;;


(*------------------------------------FNC------------------------------------

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
  
  es_fnc gic_no_fnc;;

---------------------------------------------------------------------------*)


(*------------------------------------CYK------------------------------------

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

  let c_ej2 = cadena_of_string "b b b b";;
  
  cyk c_ej1 g_ej;;

---------------------------------------------------------------------------*)


(*---------------------------------CYK-PLUS---------------------------------
  cyk_plus c_ej1 g_ej;;
---------------------------------------------------------------------------*)


(*---------------------------------CYK-PROB---------------------------------
  let gp_ej = gicp_of_gic g_ej [0.25; 0.75; 0.5; 0.5; 0.1; 0.9; 0.2; 0.8];;
  cyk_prob c_ej1 gp_ej;;
  ---------------------------------------------------------------------------*)


