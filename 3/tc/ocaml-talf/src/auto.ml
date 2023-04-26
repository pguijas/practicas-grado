
(*****************************************************************************
 *
 * auto.ml   Tipos predefinidos y operaciones básicas para las prácticas de
 *           Teoría de Autómatas y Lenguajes Formales.
 *
 *****************************************************************************)

open List;;
open Conj;;

(*****************************************************************************
 *
 * Definiciones de tipos
 *
 *****************************************************************************)

(*****************************************************************************
 *
 * Existen dos tipos de símbolos:
 *
 *    - Los terminales:    aquéllos que forman parte de las cadenas de los
 *                         lenguajes.
 *
 *    - Los no terminales: aquéllos que no forman parte de las cadenas de
 *                         los lenguajes, sino que ayudan a definirlos.
 *
 *****************************************************************************)

type simbolo =
     Terminal of string
   | No_terminal of string;;


(*****************************************************************************
 *
 * Las expresiones regulares se definen recursivamente como sigue:           
 *
 *    i)  El conjunto vacío, épsilon y cualquier símbolo terminal son        
 *        expresiones regulares básicas.                                     
 *
 *    ii) Si r y s son expresiones regulares, entonces también lo son:       
 *           r|s   (unión)                                                   
 *           r.s   (concatenación)                                           
 *           r*    (repetición)                                              
 *
 *****************************************************************************)

type er = 
     Vacio
   | Constante of simbolo
   | Union of (er * er)
   | Concatenacion of (er * er)
   | Repeticion of er;;


(*****************************************************************************
 *
 * Un autómata finito viene definido por la 5-tupla AF = (Q,E,q0,d,F) donde: 
 *    Q  = conjunto de estados      -> estado conjunto                       
 *    E  = alfabeto de entrada      -> simbolo conjunto                      
 *    q0 = estado inicial           -> estado                                
 *    d  = función  de transición   -> arco_af conjunto                      
 *    F  = conjunto estados finales -> estado conjunto                       
 *
 *****************************************************************************)

type estado =
   Estado of string;;

type arco_af =
   Arco_af of (estado * estado * simbolo);;

type af = 
   Af of (estado conjunto * simbolo conjunto * estado * arco_af conjunto * estado conjunto);;


(*****************************************************************************
 *
 * Una gramática independiente del contexto viene definida por la 4-tupla    
 * (N,T,P,S) donde:                                                          
 *    N = conjunto de símbolos no terminales -> simbolo conjunto             
 *    T = conjunto de símbolos terminales    -> simbolo conjunto             
 *    P = reglas de producción               -> regla_gic conjunto           
 *    S = símbolo inicial                    -> simbolo                      
 *
 *****************************************************************************)

type regla_gic =
   Regla_gic of (simbolo * simbolo list);;

type gic = 
   Gic of (simbolo conjunto * simbolo conjunto * regla_gic conjunto * simbolo);;


(*****************************************************************************
 *
 * Un autómata de pila viene definido por la 5-tupla AP = (Q,E,G,q0,d,Z,F) donde: 
 *    Q  = conjunto de estados       -> estado conjunto                       
 *    E  = alfabeto de entrada       -> simbolo conjunto                      
 *    G  = alfabeto de la pila       -> simbolo conjunto                      
 *    q0 = estado inicial            -> estado                                
 *    d  = función  de transición    -> arco_ap conjunto                      
 *    Z  = símbolo de inicio de pila -> simbolo
 *    F  = conjunto estados finales  -> estado conjunto                       
 *
 *****************************************************************************)

type arco_ap =
   Arco_ap of (estado * estado * simbolo * simbolo * simbolo list);;

type ap = 
   Ap of (estado conjunto * simbolo conjunto * simbolo conjunto *
          estado * arco_ap conjunto * simbolo * estado conjunto);;


(*****************************************************************************
 *
 * Una máquina de Turing viene dada por la 7-tupla MT = (Q,E,G,q0,d,B,F) donde:
 *    Q  = conjunto de estados      -> estado conjunto
 *    E  = alfabeto de entrada      -> simbolo conjunto
 *    G  = alfabeto de la cinta     -> simbolo conjunto
 *    q0 = estado inicial           -> estado
 *    d  = funcion  de transicion   -> arco_mt conjunto
 *    B  = simbolo blanco           -> simbolo
 *    F  = conjunto estados finales -> estado conjunto
 *
 *****************************************************************************)

type movimiento_mt =
     Izquierda
   | Derecha;;

type arco_mt =
   Arco_mt of (estado * estado * simbolo * simbolo * movimiento_mt);;

type mt =
   Mt of (estado conjunto * simbolo conjunto * simbolo conjunto *
          estado * arco_mt conjunto * simbolo * estado conjunto);;


(*****************************************************************************
 *
 * Operaciones con lenguajes regulares.                                      
 *
 *****************************************************************************)

(*****************************************************************************
 *
 * es_regular : gic -> bool
 *
 * Función que comprueba si una gramatica es regular o no.                   
 * Esta función es sólo válida para gramáticas generadas con la              
 * funcion gic_of_string, ya que las comprobaciones que no se                
 * realizan aquí las hace automáticamente esa función.                       
 * Por tanto, si la gramática resulta no ser regular, sigue siendo           
 * una gramática correcta, lo que ocurre es que será una gramática           
 * independiente del contexto no regular.                                    
 *
 *****************************************************************************)

let es_regular = function
     Gic (ns, ts, Conjunto p, No_terminal a) ->
        let rec aux = function
             [] -> true

           | ((Regla_gic (No_terminal n, []))::r) -> 
                (pertenece (No_terminal n) ns) &&
                (aux r)

           | ((Regla_gic (No_terminal n, [Terminal t]))::r) -> 
                (pertenece (No_terminal n) ns) &&
                (pertenece (Terminal t) ts) &&
                (aux r)

           | ((Regla_gic (No_terminal n, [No_terminal m]))::r) -> 
                (pertenece (No_terminal n) ns) &&
                (pertenece (No_terminal m) ns) &&
                (aux r)

           | ((Regla_gic (No_terminal n, [Terminal t; No_terminal m]))::r) ->
                (pertenece (No_terminal n) ns) &&
                (pertenece (Terminal t) ts) &&
                (pertenece (No_terminal m) ns) &&
                (aux r)

           | ((Regla_gic (n,(Terminal t)::s))::r) ->
                (pertenece (Terminal t) ts) &&
                (aux ((Regla_gic (n,s))::r))

           | _ -> false
        in
           (pertenece (No_terminal a) ns) && (aux p)
   | _ -> raise (Failure "es_regular: el axioma de la gramatica es un terminal");;


(*****************************************************************************
 *
 * nuevo_estado : int -> simbolo Conjunto -> int
 *
 * Función que dado un entero y un conjunto de simbolos devuelve un entero
 * igual o mayor que el entero dado, cuyo string correspondiente no genera
 * conflictos con el espacio de nombres del conjunto de simbolos, siendo
 * por tanto un nombre válido para un nuevo estado, en el proceso que transforma
 * una expresión regular o una gramática regular en un autómata.
 *
 *****************************************************************************)

let nuevo_estado n (Conjunto simbolos) =
   let
      nombres = map (function Terminal s -> s | No_terminal s -> s) simbolos
   in
      let rec aux m =
         if mem (string_of_int m) nombres then
            aux (m+1)
         else
            m
      in
         aux n
      ;;


(*****************************************************************************
 *
 * af_of_gic : gic -> af
 *
 * Función que dada una gramática regular, devuelve el automáta finito       
 * correspondiente. El autómata tendrá un solo estado final artificial       
 * denotado por Estado "0".                                                  
 *
 *****************************************************************************)

let af_of_gic = function
     Gic (Conjunto n, t, Conjunto p, No_terminal s) ->
        let
           simbolos = union (Conjunto n) t
        in
           let
              final = nuevo_estado 0 simbolos
           in
              let rec arcos extra efinal qi acum = function
                   [] -> (extra, acum)
                 | Regla_gic (No_terminal x, []) :: t ->
                      arcos extra efinal qi 
                         ((Arco_af (Estado x, efinal, Terminal "")) :: acum) t
                 | Regla_gic (No_terminal x, [Terminal y]) :: t ->
                      arcos extra efinal qi
                         ((Arco_af (Estado x, efinal, Terminal y)) :: acum) t
                 | Regla_gic (No_terminal x, [No_terminal y]) :: t ->
                      arcos extra efinal qi 
                         ((Arco_af (Estado x, Estado y, Terminal "")) :: acum) t
                 | Regla_gic (No_terminal x, [Terminal y; No_terminal z]) :: t ->
                      arcos extra efinal qi
                         ((Arco_af (Estado x, Estado z, Terminal y)) :: acum) t
                 | Regla_gic (No_terminal x, (Terminal y)::r) :: t ->
                      let
                         nqi = nuevo_estado qi simbolos
                      in
                         arcos ((Estado (string_of_int nqi))::extra) efinal (nqi+1)
                            ((Arco_af (Estado x, Estado (string_of_int nqi), Terminal y)) :: acum)
                            ((Regla_gic (No_terminal (string_of_int nqi), r))::t)
                 | _ -> raise (Failure "af_of_gic: la gramatica no es regular")
              in
                 let
                    (extra, delta) = arcos [Estado (string_of_int final)]
                                           (Estado (string_of_int final)) (final+1) [] p
                 in
                    Af (Conjunto ((map (function 
                                             No_terminal x -> Estado x
                                           | _ -> raise (Failure "af_of_gic: el conjunto de no terminales de la gramatica contiene un terminal")) 
                                   n) @ (rev extra)),
                        t, 
                        Estado s,
                        Conjunto (rev delta), 
                        Conjunto [Estado (string_of_int final)])
   | _ -> raise (Failure "af_of_gic: el axioma de la gramatica es un terminal");;


(*****************************************************************************
 *
 * gic_of_af : af -> gic
 *
 * Función que dado un autómata finito, devuelve la gramática regular
 * correspondiente.
 *
 *****************************************************************************)

let gic_of_af
   (Af (Conjunto estados, terminales, Estado inicial, Conjunto arcos, Conjunto finales)) =

   Gic (Conjunto (map (function Estado q -> No_terminal q) estados),

        terminales,

        Conjunto
           ((map (function 
                       Arco_af (Estado o, Estado d, Terminal "") -> 
                          Regla_gic (No_terminal o, [No_terminal d])
                     | Arco_af (Estado o, Estado d, simbolo) -> 
                          Regla_gic (No_terminal o, [simbolo; No_terminal d])) arcos)
            @
            (map (function Estado q -> Regla_gic (No_terminal q, [])) finales)),

        No_terminal inicial);;


(*****************************************************************************
 *
 * simbolo_of_er : er -> simbolo Conjunto
 *
 * Función que dada una expresión regular devuelve el conjunto de los
 * diferentes símbolos terminales que aparecen en dicha expresión regular.
 *
 *****************************************************************************)

let rec simbolo_of_er = function
     Vacio                   -> conjunto_vacio
   | Constante (Terminal "") -> conjunto_vacio
   | Constante (Terminal s)  -> agregar (Terminal s) conjunto_vacio
   | Constante _ ->
        raise (Failure "simb_of_er: la expresion regular contiene no terminales")
   | Union (e1,e2)           -> union (simbolo_of_er e1) (simbolo_of_er e2)
   | Concatenacion (e1,e2)   -> union (simbolo_of_er e1) (simbolo_of_er e2)
   | Repeticion e            -> simbolo_of_er e;;


(*****************************************************************************
 *
 * epsilon_cierre : estado conjunto -> af -> estado conjunto
 *
 * Función que dado un conjunto de estados y un autómata calcula la unión de
 * los épsilon-cierres de todos esos estados, a partir de las
 * épsilon-transiciones del autómata.                
 *
 *****************************************************************************)

let epsilon_cierre estados (Af (_, _, _, Conjunto arcos, _)) =

   let rec aux cambio cierre arcos_pendientes = function

        [] ->
           if cambio then
              aux false cierre [] arcos_pendientes
           else
              cierre

      | (Arco_af (origen, destino, Terminal "") as arco) :: t ->
           if (pertenece origen cierre) then
              aux true (agregar destino cierre) arcos_pendientes t
           else
              aux cambio cierre (arco :: arcos_pendientes) t

      | _ :: t ->
           aux cambio cierre arcos_pendientes t

   in
      aux false estados [] arcos
   ;;


(*****************************************************************************
 *
 * avanza : simbolo -> estado conjunto -> af -> estado conjunto
 * 
 * Función que dado un símbolo, un conjunto de estados, y un autómata finito
 * intenta consumir ese símbolo utilizando todos los arcos presentes en el
 * autómata excepto los arcos épsilon, y devuelve el conjunto de estados
 * de destino.
 *
 *****************************************************************************)

let avanza simbolo estados (Af (_, _, _, Conjunto arcos, _)) =

   let rec aux destinos = function

        [] ->
           destinos

      | Arco_af (origen, destino, s) :: t ->
           if (s = simbolo) && (pertenece origen estados) then
              aux (agregar destino destinos) t
           else
              aux destinos t

   in
      aux conjunto_vacio arcos
   ;;


(*****************************************************************************
 *
 * escaner_af : simbolo list -> af -> bool
 * 
 * Función que dada una lista de símbolos terminales y un autómata finito
 * indica si dicha cadena de símbolos es aceptada o no por el autómata.
 * Se trata de una versión de la función de reconocimiento más general
 * posible, es decir, aquélla que es capaz de simular el funcionamiento
 * de cualquier tipo de autómata finito (determinista, no determinista,
 * e incluso no determinista con épsilon-transiciones).
 *
 *****************************************************************************)

let escaner_af cadena (Af (_, _, inicial, _, finales) as a) =

   let rec aux = function

        (Conjunto [], _) ->
           false

      | (actuales, []) ->
           not (es_vacio (interseccion actuales finales))

      | (actuales, simbolo :: t) ->
           aux ((epsilon_cierre (avanza simbolo actuales a) a), t)

   in
      aux ((epsilon_cierre (Conjunto [inicial]) a), cadena)
   ;;


(*****************************************************************************
 *
 * Operaciones con autómatas de pila.
 *
 *****************************************************************************)

(*****************************************************************************
 *
 * escaner_ap : simbolo list -> ap -> bool
 * 
 * Función que dada una lista de símbolos terminales y un autómata de pila
 * indica si dicha cadena de símbolos es aceptada o no por el autómata.
 *
 *****************************************************************************)

exception No_encaja;;

let encaja (estado, cadena, pila_conf) (Arco_ap (origen, destino, entrada, cima, pila_arco)) =
   let
      nuevo_estado =
         if estado = origen then
            destino
         else
            raise No_encaja
   and
      nueva_cadena =
         if entrada = Terminal "" then
            cadena
         else
            if (cadena <> []) && (entrada = hd cadena) then
               tl cadena
            else
               raise No_encaja
   and
      nueva_pila_conf =
         if cima = Terminal "" then
            pila_arco @ pila_conf
         else
            if (pila_conf <> []) && (cima = hd pila_conf) then
               pila_arco @ (tl pila_conf)
            else
               raise No_encaja
   in
      (nuevo_estado, nueva_cadena, nueva_pila_conf)
   ;;

let es_conf_final finales = function
     (estado, [], _) -> pertenece estado finales
   | _ -> false;;

let escaner_ap cadena (Ap (_, _, _, inicial, Conjunto delta, zeta, finales)) =
   let rec aux = function
        ([], [], _) -> false
      | ([], l, _) -> aux (l, [], delta)
      | (_::cfs, l, []) -> aux (cfs, l, delta)
      | (cf::cfs, l, a::arcos) ->
           try
              let
                 ncf = encaja cf a
              in
                 (es_conf_final finales ncf) || (aux (cf::cfs, ncf::l, arcos))
           with
              No_encaja -> aux (cf::cfs, l, arcos)
   in
      aux ([(inicial, cadena, [zeta])], [], delta)
   ;;



(*****************************************************************************
 *
 * Operaciones con máquinas de Turing.
 *
 *****************************************************************************)

(*****************************************************************************
 *
 * escaner_mt : simbolo list -> mt -> bool
 * 
 * Función que dada una lista de símbolos terminales y una máquina de Turing
 * indica si dicha cadena de símbolos es aceptada o no por la máquina.
 * Se trata de la versión más básica de la función,
 * es decir, aquélla que simplemente indica si la máquina se detiene, y
 * si lo hace en un estado final o de aceptación, pero no devuelve el
 * contenido de la cinta después de la parada.
 *
 * Las porciones de cinta que quedan a derecha e izquierda de la cabeza se
 * guardan como listas. Para facilitar el acceso a los símbolos, la porción
 * izquierda de la cinta se almacena en orden inverso.
 *
 * Para simular la longitud inifinita de la cinta, cada vez que una de las
 * porciones se hace vacía, se introduce artificialmente un nuevo símbolo blanco.
 *
 *****************************************************************************)

let escaner_mt cadena (Mt (_, _, _, inicial, Conjunto delta, _, finales)) =

   (pertenece inicial finales)

   ||

   let
      cinta = if cadena = [] then [No_terminal ""] else cadena
   in
      let rec aux = function
           ([], [], _) -> false
   
         | (sigcfs, [], _) -> aux ([], sigcfs, delta)
   
         | (sigcfs, _::cfs, []) -> aux (sigcfs, cfs, delta)
   
         | (sigcfs, (((i::c1, e, s::c2)::_) as cfs),
                    (Arco_mt (e1, e2, s1, s2, Izquierda))::arcos)
                    when e = e1 && s = s1 ->
              (pertenece e2 finales) || 
              let
                 nc1 = if c1 = [] then [No_terminal ""] else c1
              in
                 (aux ((nc1, e2, i::s2::c2)::sigcfs, cfs, arcos))

         | (sigcfs, (((c1, e, s::c2)::_) as cfs),
                    (Arco_mt (e1, e2, s1, s2, Derecha))::arcos)
                    when e = e1 && s = s1 ->
              (pertenece e2 finales) || 
              let
                 nc2 = if c2 = [] then [No_terminal ""] else c2
              in
                 (aux ((s2::c1, e2, nc2)::sigcfs, cfs, arcos))

         | (sigcfs, cfs, _::arcos) -> aux (sigcfs, cfs, arcos)
   
      in
         aux ([], [([No_terminal ""], inicial, cinta)], delta)
      ;;


(*****************************************************************************
 *
 * scpm : mt -> simbolo list -> (string * string) list
 *
 * Función que dada una máquina de Turing y una cadena de entrada,
 * devuelve el Sistema de Correspondecia de Post Modificado (SCPM).
 *
 *****************************************************************************)

let rec string_of_cadena = function
     []                  -> ""
   | (Terminal s)::t     -> s ^ (string_of_cadena t)
   | (No_terminal s)::t  -> s ^ (string_of_cadena t);;

let string_of_simbolo = function
     Terminal s -> s
   | No_terminal s -> s;;

let scpm (Mt (_, _, Conjunto c, Estado i, Conjunto d, b, Conjunto f)) w =
   let 
      rec g2 fichas = function
           [] -> rev fichas
         | simb::t -> 
              if simb = b then
                 g2 fichas t
              else
                 let
                    s = string_of_simbolo simb
                 in
                    g2 ((s, s)::fichas) t
   and
      g3 fichas = function
           [] -> rev fichas

         | (Arco_mt (Estado e1, Estado e2, simb1, simb2, Derecha))::t ->
              if simb1 = b then
                 g3 ((e1^"$", (string_of_simbolo simb2)^e2^"$")::fichas) t
              else
                 g3 ((e1^(string_of_simbolo simb1), (string_of_simbolo simb2)^e2)::fichas) t

         | (Arco_mt (Estado e1, Estado e2, simb1, simb2, Izquierda))::t ->
              let rec aux nuevas = function
                   [] -> nuevas
                 | sc::scs ->
                      if sc = b then
                         aux nuevas scs
                      else
                         if simb1 = b then
                            aux (((string_of_simbolo sc)^e1^"$",
                                  e2^(string_of_simbolo sc)^(string_of_simbolo simb2)^"$")::nuevas) scs
                         else 
                            aux (((string_of_simbolo sc)^e1^(string_of_simbolo simb1),
                                  e2^(string_of_simbolo sc)^(string_of_simbolo simb2))::nuevas) scs
              in
                 g3 (aux fichas c) t
   and
      g41 fichas = function
           (_, _, [])   -> rev fichas
         | ([], _, _::fs) -> g41 fichas (c, c, fs)
         | (_::scs, [], fs)   -> g41 fichas (scs, c, fs)
         | (sc1::scs1, sc2::scs2, (Estado q)::fs) -> 
              if (sc1 = b) || (sc2 = b) then
                 g41 fichas (sc1::scs1, scs2, (Estado q)::fs)
              else
                 g41 (((string_of_simbolo sc1)^q^(string_of_simbolo sc2), q)::fichas)
                     (sc1::scs1, scs2, (Estado q)::fs)
   and
      g42 fichas = function
           (_, []) -> rev fichas
         | ([], _::fs) -> g42 fichas (c, fs)
         | (sc::scs, (Estado q)::fs) ->
              if sc = b then
                 g42 fichas (scs, (Estado q)::fs)
              else
                 g42 (((string_of_simbolo sc)^q^"$", q^"$")::fichas) (scs, (Estado q)::fs)
   and
      g43 fichas = function
           (_, []) -> rev fichas
         | ([], _::fs) -> g43 fichas (c, fs)
         | (sc::scs, (Estado q)::fs) ->
              if sc = b then
                 g43 fichas (scs, (Estado q)::fs)
              else
                 g43 (("$"^q^(string_of_simbolo sc), "$"^q)::fichas) (scs, (Estado q)::fs)
   and
      g44 fichas = function
           []            -> rev fichas
         | (Estado q)::t -> g44 ((q^"$$", "$")::fichas) t
   in
      ( [("$", "$"^i^(string_of_cadena w)^"$")]
        @ 
        (g2 [("$", "$")] c)
        @
        (g3 [] d)
        @
        (g41 [] (c, c, f)) @ (g42 [] (c, f)) @ (g43 [] (c, f)) @ (g44 [] f) )
   ;;


(*****************************************************************************)

