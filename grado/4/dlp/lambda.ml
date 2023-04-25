(***********************************TYPES***********************************)

(* Base Types *)
type ty =
    TyBool
  | TyNat
  | TyArr of ty * ty (* arrow type *)
  | TyPair of ty * ty
  | TyString
  | TyList of ty
  | TyRecord of (string * ty) list

;;

(* Terms *)
type term =
  (* Bools *)
    TmTrue
  | TmFalse
  | TmIf of term * term * term
  (* Nats *)
  | TmZero
  | TmSucc of term
  | TmPred of term
  | TmIsZero of term
  (* Lambda Basics *)
  | TmVar of string
  | TmAbs of string * ty * term
  | TmApp of term * term
  | TmLetIn of string * term * term
  | TmFix of term
  (* Pairs *)
  | TmPair of term * term
  | TmProj of term * string
  (* Strings *)
  | TmString of string
  | TmConcat of term * term
  (* Lists *)
  | TmNil of ty
  | TmCons of ty * term * term
  | TmIsNil of ty * term
  | TmHead of ty * term
  | TmTail of ty * term
  (* Records *) 
  | TmRecord of (string * term) list
  
;;

(* Command *)
type command =
    Eval of term
  | Bind of string * term
;;

(* Context is a list of correspondences between free variables and their type / value *)
type context =
  (string * ty * term option) list
;;

exception Type_error of string;;


(******************************* Printing *******************************)

let rec string_of_ty ty = match ty with
    TyBool ->
      "Bool"
  | TyNat ->
      "Nat"
  | TyString ->
      "String"
  | TyArr (ty1, ty2) ->
      "(" ^ string_of_ty ty1 ^ ")" ^ " -> " ^ "(" ^ string_of_ty ty2 ^ ")"
  | TyPair (ty1, ty2) ->
      "(" ^ string_of_ty ty1 ^ " * " ^ string_of_ty ty2 ^ ")"
  | TyList ty -> 
      string_of_ty ty ^ " list"
  | TyRecord tyl -> 
      let rec print = function
        [] -> ""
        |((s,ty)::[]) -> s ^ ":" ^ (string_of_ty ty)
        |((s,ty)::t) ->  s ^ ":" ^ (string_of_ty ty) ^ ", " ^ print t
      in "{" ^ (print tyl) ^ "}"
;;

let rec string_of_term = function
    TmTrue ->
      "true"
  | TmFalse ->
      "false"
  | TmString str ->
      "\"" ^ str ^ "\""
  | TmConcat (t1,t2) ->
      string_of_term t1 ^ " ^ " ^ string_of_term t2
  | TmIf (t1,t2,t3) ->
      "if " ^ "(" ^ string_of_term t1 ^ ")" ^
      " then " ^ "(" ^ string_of_term t2 ^ ")" ^
      " else " ^ "(" ^ string_of_term t3 ^ ")"
  | TmZero ->
      "0"
  | TmSucc t ->
     let rec f n t' = match t' with
          TmZero -> string_of_int n
        | TmSucc s -> f (n+1) s
        | _ -> "succ " ^ "(" ^ string_of_term t ^ ")"
      in f 1 t
  | TmPred t ->
      "pred " ^ "(" ^ string_of_term t ^ ")"
  | TmIsZero t ->
      "iszero " ^ "(" ^ string_of_term t ^ ")"
  | TmVar s ->
      s
  | TmAbs (s, tyS, t) ->
      "(lambda " ^ s ^ ":" ^ string_of_ty tyS ^ ". " ^ string_of_term t ^ ")"
  | TmApp (t1, t2) ->
      "(" ^ string_of_term t1 ^ " " ^ string_of_term t2 ^ ")"
  | TmLetIn (s, t1, t2) ->
      "let " ^ s ^ " = " ^ string_of_term t1 ^ " in " ^ string_of_term t2
  | TmFix (t) ->
      "fix (" ^ string_of_term t ^ ")"
  | TmPair (t1, t2) ->
      "{" ^ string_of_term t1 ^ " , " ^ string_of_term t2 ^ "}"
  | TmProj (t, n) -> 
      string_of_term t ^ "." ^ n
  | TmNil ty -> 
      "nil[" ^ string_of_ty ty ^ "]" 
  | TmCons (ty,h,t) -> 
      "cons[" ^ string_of_ty ty ^ "] " ^ "(" ^ string_of_term h ^ ") (" ^ (string_of_term t) ^ ")"
  | TmIsNil (ty,t) -> 
      "isnil[" ^ string_of_ty ty ^ "] " ^ "(" ^ string_of_term t ^ ")"
  | TmHead (ty,t) -> 
      "head[" ^ string_of_ty ty ^ "] " ^ "(" ^ string_of_term t ^ ")"
  | TmTail (ty,t) -> 
      "tail[" ^ string_of_ty ty ^ "] " ^ "(" ^ string_of_term t ^ ")"
  | TmRecord tml -> 
      let rec print = function
        [] -> ""
        |((s,tm)::[]) -> s ^ "=" ^ (string_of_term tm)
        |((s,tm)::t) ->  s ^ "=" ^ (string_of_term tm) ^ ", " ^ print t
      in "{" ^ (print tml) ^ "}"  
;;

(*******************************CONTEXT MANAGEMENT*******************************)

(* Creates an empty context *)
let emptyctx =
  []
;;

(* Adds binding to a given context *)
let addbinding ctx x ty te =
  (x, ty, Some(te)) :: ctx
;;

let addbinding_type ctx x ty =
  (x, ty, None) :: ctx
;;

exception Not_Found of string;;

(* Gets binding to a given context *)
let rec getbinding_type ctx x = match ctx with
  ((a,ty,_)::t) -> if x=a then ty else getbinding_type t x
  |[] -> raise (Not_Found x)
;;

let rec getbinding_term ctx x = match ctx with
  ((a,_,Some(term))::t) -> if x=a then term else getbinding_term t x
  |((a,_,None)::t) -> getbinding_term t x
  |[] -> raise (Not_Found x)
;;

(*******************************TYPE MANAGEMENT (TYPING)*******************************)

(* tm1 supertype of tm2 *)
let rec subtypeof tm1 tm2 = match (tm1,tm2) with
  (* S-Arrow *)
  | (TyArr(s1,s2),TyArr(t1,t2)) -> ((subtypeof s1 t1) && (subtypeof t2 s2))
  (* S-RcdWidth / S-RcdPerm / S-RcdDepth*)
  | (TyRecord(l1),TyRecord(l2)) -> 
    let seach_and_check (x,ty) l = 
      try 
        subtypeof ty (List.assoc x l) 
      with _ -> false
    in let rec contains l1 l2 = match l1 with
      | []     -> true
      | (h::t) -> (&&) (seach_and_check h l2)  (contains t l2)
    in contains l1 l2
  (* S-Refl *)
  | (tm1,tm2) -> tm1=tm2
;;

(* Given a context and a term we find its type (Inversion Lema) *)
let rec typeof ctx tm = match tm with
  (* T-True *)
  | TmTrue ->
      TyBool

  (* T-False *)
  | TmFalse ->
      TyBool

  | TmString _ ->
      TyString

  (* T-Concat *)
  | TmConcat (t1, t2) -> 
      if (typeof ctx t1 = TyString) && (typeof ctx t2 = TyString) then
        TyString
      else
        raise (Type_error "terms of concatenate are not strings")

  (* T-If *)
  | TmIf (t1, t2, t3) ->
      if typeof ctx t1 = TyBool then
        let tyT2 = typeof ctx t2 in
        if typeof ctx t3 = tyT2 then tyT2
        else raise (Type_error "arms of conditional have different types")
      else
        raise (Type_error "guard of conditional not a boolean")
      
  (* T-Zero *)
  | TmZero ->
      TyNat

  (* T-Succ *)
  | TmSucc t1 ->
      if typeof ctx t1 = TyNat then TyNat
      else raise (Type_error "argument of succ is not a number")

  (* T-Pred *)
  | TmPred t1 ->
      if typeof ctx t1 = TyNat then TyNat
      else raise (Type_error "argument of pred is not a number")

  (* T-Iszero *)
  | TmIsZero t1 ->
      if typeof ctx t1 = TyNat then TyBool
      else raise (Type_error "argument of iszero is not a number")

  (* T-Var *)
  | TmVar x ->
      (try getbinding_type ctx x with
       _ -> raise (Type_error ("no binding type for variable " ^ x)))

  (* T-Abs *)
  | TmAbs (x, tyT1, t2) ->
      let ctx' = addbinding_type ctx x tyT1 in
        let tyT2 = typeof ctx' t2 in
          TyArr (tyT1, tyT2)

  (* T-App *)
  | TmApp (t1, t2) ->
      let tyT1 = typeof ctx t1 in
      let tyT2 = typeof ctx t2 in
      (match tyT1 with
          TyArr (tyT11, tyT12) ->
            if (subtypeof tyT11 tyT2) then tyT12
            else raise (Type_error "parameter type mismatch")
          | _ -> raise (Type_error "arrow type expected"))

  (* T-Let *)
  | TmLetIn (x, t1, t2) ->
      let tyT1 = typeof ctx t1 in
      let ctx' = addbinding_type ctx x tyT1 in
      typeof ctx' t2
   
  (* T-TmFix *)
  | TmFix (t1) ->
      let tyT1 = typeof ctx t1 in
      (match tyT1 with
        TyArr (tyT11,tyT12) ->
          if tyT11 = tyT12 then tyT12
          else raise (Type_error "result of body not compatible wirh domain")
        | _ -> raise (Type_error "arrow type excepted")
      )
  (* T-Pair *)
  | TmPair (t1, t2) ->
      let tyT1 = typeof ctx t1 
      and tyT2 = typeof ctx t2 in
      TyPair(tyT1, tyT2)

  | TmProj (t, n) -> 
      (match (typeof ctx t, n) with
        (* T-Proj1 *)
        (TyPair (ty1, _), "1") -> ty1
        (* T-Proj2 *)
        | (TyPair (_, ty2), "2") -> ty2
        (* T-Proj *)
        | (TyRecord (tyl), s) -> 
          (try List.assoc s tyl with
          _ -> raise (Type_error ("cannot project " ^ s ^ ", this key does not exist in the record")))
        (* Errors *)
        | (TyPair (_, _), _) -> raise (Type_error "pair out of bounds")
        | (x, _) -> raise (Type_error ("cannot project type " ^ string_of_ty x))
      )
  
  (* T-Nil *)
  | TmNil ty -> TyList ty
  
  (* T-Cons *)
  | TmCons (ty,h,t) ->
      let tyTh = typeof ctx h in
        let tyTt = typeof ctx t in
          if (tyTh = ty) && (tyTt = TyList(ty)) then TyList(ty)
          else raise (Type_error "elements of list have different types")
  
  (* T-IsNil *)
  | TmIsNil (ty,t) -> 
    if typeof ctx t = TyList(ty) then TyBool
    else raise (Type_error ("argument of isempty is not a " ^ (string_of_ty ty) ^ " list"))
 
  (* T-Head *)    
  | TmHead (ty,t) ->     
    if typeof ctx t = TyList(ty) then ty
    else raise (Type_error ("argument of head is not a " ^ (string_of_ty ty) ^ " list"))
    
  (* T-Tail *)    
  | TmTail (ty,t) -> 
    if typeof ctx t = TyList(ty) then TyList(ty)
    else raise (Type_error ("argument of tail is not a " ^ (string_of_ty ty) ^ " list"))

  (* T-Rcd *)
  | TmRecord tml ->
      let rec get_types = function
        [] -> []
        |((s,tm)::t) -> ((s,typeof ctx tm)::(get_types t))
      in TyRecord (get_types tml)

;;


(*********************************** EVAL ***********************************)

(* l1 - l2 (lists) *)
let rec ldif l1 l2 = match l1 with
    [] -> []
  | h::t -> if List.mem h l2 then ldif t l2 else h::(ldif t l2)
;;

(* l1 u l2 (lists) *)
let rec lunion l1 l2 = match l1 with
    [] -> l2
  | h::t -> if List.mem h l2 then lunion t l2 else h::(lunion t l2)
;;

let rec free_vars tm = match tm with
    TmTrue ->
      []
  | TmFalse ->
      []
  | TmString _ ->
      []
  | TmConcat (t1, t2) ->
      lunion (free_vars t1) (free_vars t2)
  | TmIf (t1, t2, t3) ->
      lunion (lunion (free_vars t1) (free_vars t2)) (free_vars t3)
  | TmZero ->
      []
  | TmSucc t ->
      free_vars t
  | TmPred t ->
      free_vars t
  | TmIsZero t ->
      free_vars t
  | TmVar s ->
      [s]
  | TmAbs (s, _, t) ->
      ldif (free_vars t) [s]
  | TmApp (t1, t2) ->
      lunion (free_vars t1) (free_vars t2)
  | TmLetIn (s, t1, t2) ->
      lunion (ldif (free_vars t2) [s]) (free_vars t1)
  | TmFix t ->
      free_vars t
  | TmPair (t1, t2) ->
      lunion (free_vars t1) (free_vars t2)
  | TmProj (t, n) -> 
      free_vars t
  | TmNil ty -> 
      []
  | TmCons (ty,t1,t2) -> 
      lunion (free_vars t1) (free_vars t2)
  | TmIsNil (ty,t) ->
      free_vars t
  | TmHead (ty,t) ->
      free_vars t
  | TmTail (ty,t) ->
      free_vars t
  | TmRecord tml ->
      let rec get_free = function
        [] -> []
        |((_,tm)::t) -> lunion (free_vars tm) (get_free t)
      in get_free tml
;;

(*generates a name which isn't free *)
let rec fresh_name x l =
  if not (List.mem x l) then x else fresh_name (x ^ "'") l
;;
    
(* replace tmvars by expressions *)
let rec subst ctx x s tm = match tm with
    TmTrue ->
      TmTrue
  | TmFalse ->
      TmFalse
  | TmString _ ->
      tm
  | TmConcat (t1, t2) ->
      TmConcat (subst ctx x s t1, subst ctx x s t2)
  | TmIf (t1, t2, t3) ->
      TmIf (subst ctx x s t1, subst ctx x s t2, subst ctx x s t3)
  | TmZero ->
      TmZero
  | TmSucc t ->
      TmSucc (subst ctx x s t)
  | TmPred t ->
      TmPred (subst ctx x s t)
  | TmIsZero t ->
      TmIsZero (subst ctx x s t)
  | TmVar y ->
      if y = x then s else tm
  | TmAbs (y, tyY, t) -> 
      if y = x then tm
      else let fvs = free_vars s in
           if not (List.mem y fvs)
           then TmAbs (y, tyY, subst ctx x s t)
           else let z = fresh_name y (free_vars t @ fvs) in
                TmAbs (z, tyY, subst ctx x s (subst ctx y (TmVar z) t))  
  | TmApp (t1, t2) ->
      TmApp (subst ctx x s t1, subst ctx x s t2)
  | TmLetIn (y, t1, t2) ->
      if y = x then TmLetIn (y, subst ctx x s t1, t2)
      else let fvs = free_vars s in
           if not (List.mem y fvs)
           then TmLetIn (y, subst ctx x s t1, subst ctx x s t2)
           else let z = fresh_name y (free_vars t2 @ fvs) in
                TmLetIn (z, subst ctx x s t1, subst ctx x s (subst ctx y (TmVar z) t2))
  | TmFix t -> 
      TmFix (subst ctx x s t)
  | TmPair (t1, t2) -> 
      TmPair ((subst ctx x s t1), (subst ctx x s t2))
  | TmProj (t, n) -> 
      TmProj (subst ctx x s t, n)
  | TmNil ty -> 
      tm
  | TmCons (ty,t1,t2) -> 
      TmCons (ty, (subst ctx x s t1), (subst ctx x s t2))
  | TmIsNil (ty,t) ->
      TmIsNil (ty, (subst ctx x s t))
  | TmHead (ty,t) ->
      TmHead (ty, (subst ctx x s t))
  | TmTail (ty,t) ->
      TmTail (ty, (subst ctx x s t))
  | TmRecord tml ->
      let rec subs_rcd = function
        [] -> []
        |((str,tm)::t) -> (str,(subst ctx x s tm))::(subs_rcd t)
      in TmRecord (subs_rcd tml)
;;

let rec isnumericval tm = match tm with
    TmZero -> true
  | TmSucc t -> isnumericval t
  | _ -> false
;;

let rec isval tm = match tm with
    TmTrue  -> true
  | TmFalse -> true
  | TmAbs _ -> true
  | TmString _ -> true
  | TmPair(t1,t2) -> (&&) (isval t1) (isval t2) (* Lazy Eval *)
  | TmNil _ -> true
  | TmCons(_,h,t) -> (&&) (isval h) (isval t)
  | TmRecord [] -> true
  | TmRecord ((_,h)::t) -> (&&) (isval h) (isval (TmRecord t))
  | t when isnumericval t -> true
  | _ -> false
;;

exception NoRuleApplies;;

let rec eval1 ctx tm = match tm with
  (* E-IfTrue *)
  | TmIf (TmTrue, t2, _) ->
      t2

  (* E-IfFalse *)
  | TmIf (TmFalse, _, t3) ->
      t3

  (* E-If *)
  | TmIf (t1, t2, t3) ->
      let t1' = eval1 ctx t1 in
      TmIf (t1', t2, t3)

  (* E-Succ *)
  | TmSucc t1 ->
      let t1' = eval1 ctx t1 in
      TmSucc t1'

  (* E-PredZero *)
  | TmPred TmZero ->
      TmZero

  (* E-PredSucc *)
  | TmPred (TmSucc nv1) when isnumericval nv1 ->
      nv1

  (* E-Pred *)
  | TmPred t1 ->
      let t1' = eval1 ctx t1 in
      TmPred t1'

  (* E-IszeroZero *)
  | TmIsZero TmZero ->
      TmTrue

  (* E-IszeroSucc *)
  | TmIsZero (TmSucc nv1) when isnumericval nv1 ->
      TmFalse

  (* E-Iszero *)
  | TmIsZero t1 ->
      let t1' = eval1 ctx t1 in
      TmIsZero t1'

  (* E-AppAbs *)
  | TmApp (TmAbs(x, _, t12), v2) when isval v2 ->
      subst ctx x v2 t12

  (* E-App2 *)
  | TmApp (v1, t2) when isval v1 ->
      let t2' = eval1 ctx t2 in
      TmApp (v1, t2')

  (* E-App1 *)
  | TmApp (t1, t2) ->
      let t1' = eval1 ctx t1 in
      TmApp (t1', t2)

  (* E-LetV *)
  | TmLetIn (x, v1, t2) when isval v1 ->
      subst ctx x v1 t2

  (* E-Let *)
  | TmLetIn(x, t1, t2) ->
      let t1' = eval1 ctx t1 in
      TmLetIn (x, t1', t2) 

  (* E-FixBeta *)
  | TmFix (TmAbs (x,_,t12)) ->
      subst ctx x tm t12

  (* E-Fix *)
  | TmFix t1 ->
      let t1' = eval1 ctx t1 in
      TmFix t1'
 
  (* E-Pair2 *)
  | TmPair (v1, t2) when isval v1 ->
      let t2' = eval1 ctx t2 in 
      TmPair (v1, t2')

  (* E-Pair1 *)
  | TmPair (t1, t2) ->
      let t1' = eval1 ctx t1 in
      TmPair (t1', t2)

  (* E-PairBeta1 *)
  | TmProj ((TmPair (t1, _), "1")) -> 
      t1
        
  (* E-PairBeta2 *)
  | TmProj ((TmPair (_, t2), "2")) ->
      t2

  (* E-ProjRcd *)
  | TmProj (TmRecord (tml), n) ->
      List.assoc n tml (* Not necesary to handling error because typeof aldready did it *)

  (* E-Proj *)
  | TmProj (t, n) -> 
      TmProj ((eval1 ctx t), n)

  | TmVar x ->  
      getbinding_term ctx x (* Not necesary to handling error because typeof aldready did it *)
      
  (* E-Concat *) 
  | TmConcat (TmString(s1),TmString(s2)) ->  
      TmString(s1^s2)
  
  (* E-Concat2 *)
  | TmConcat (TmString(s),t1) -> 
      let t1' = eval1 ctx t1 
      in TmConcat (TmString(s),t1')
  
  (* E-Concat1 *)
  | TmConcat (t1,t2) -> 
      let t1' = eval1 ctx t1 
      in TmConcat (t1',t2)

  (* E-Cons2 *)
  | TmCons(ty,h,t) when isval h -> 
      TmCons(ty,h,(eval1 ctx t)) 
  
  (* E-Cons1 *)
  | TmCons(ty,h,t) -> 
      TmCons(ty,(eval1 ctx h),t)
  
  (* E-IsNilNil *)
  | TmIsNil(ty,TmNil(_)) -> 
      TmTrue  
  
  (* E-IsNilCons *)
  | TmIsNil(ty,TmCons(_,_,_)) -> 
      TmFalse
  
  (* E-IsNil *)
  | TmIsNil(ty,t) -> 
      TmIsNil(ty,eval1 ctx t)
  
  (* E-HeadCons *)
  | TmHead(ty,TmCons(_,h,_)) -> 
      h
  
  (* E-Head *)
  | TmHead(ty,t) -> 
      TmHead(ty,eval1 ctx t)
  
  (* E-TailCons *)
  | TmTail(ty,TmCons(_,_,t)) -> 
      t
  
  (* E-Tail *)
  | TmTail(ty,t) -> 
      TmTail(ty,eval1 ctx t)
  
  (* E-Rcd *)
  | TmRecord tml ->
      let rec eval_rcd = function
        [] -> raise NoRuleApplies
        |((str,tm)::t) when isval tm -> (str,tm)::(eval_rcd t)
        |((str,tm)::t) -> (str,(eval1 ctx tm))::t
      in TmRecord (eval_rcd tml)
  
  | _ ->
      raise NoRuleApplies
;;

(* replace any free variables that may have remained *)
let rec subs_ctx ctx tm vl = match tm with
  | TmTrue -> TmTrue
  | TmFalse -> TmFalse
  | TmString _ -> tm
  | TmConcat (t1, t2) -> TmConcat (subs_ctx ctx t1 vl, subs_ctx ctx t2 vl) 
  | TmIf (t1, t2, t3) -> TmIf (subs_ctx ctx t1 vl, subs_ctx ctx t2 vl, subs_ctx ctx t3 vl) 
  | TmZero -> TmZero
  | TmSucc t -> TmSucc (subs_ctx ctx t vl)
  | TmPred t -> TmPred (subs_ctx ctx t vl)
  | TmIsZero t -> TmIsZero (subs_ctx ctx t vl)
  | TmVar x -> 
    if List.mem x vl then
      tm
    else
      getbinding_term ctx x (* Not necesary to handling error because typeof aldready did it *)
  | TmAbs (y, tyY, t) -> TmAbs (y, tyY, subs_ctx ctx t (y::vl))
  | TmApp (t1, t2) -> TmApp (subs_ctx ctx t1 vl, subs_ctx ctx t2 vl)
  | TmLetIn (y, t1, t2) -> TmLetIn (y, subs_ctx ctx t1 vl, subs_ctx ctx t2 (y::vl))
  | TmFix t ->  TmFix (subs_ctx ctx t vl)
  | TmPair (t1, t2) -> TmPair ((subs_ctx ctx t1 vl), (subs_ctx ctx t2 vl))
  | TmProj (t, proj) -> TmProj (subs_ctx ctx t vl, proj)
  | TmNil ty -> TmNil ty
  | TmCons (ty,t1,t2) -> TmCons (ty,subs_ctx ctx t1 vl,subs_ctx ctx t2 vl) 
  | TmIsNil (ty,t) -> TmIsNil (ty,subs_ctx ctx t vl)
  | TmHead (ty,t) -> TmHead (ty,subs_ctx ctx t vl)
  | TmTail (ty,t) -> TmTail (ty,subs_ctx ctx t vl) 
  | TmRecord tml ->
      let rec subs_rcd = function
        [] -> []
        |((str,tm)::t) -> (str,(subs_ctx ctx tm vl))::(subs_rcd t)(* Para hacerlo inmutable *)
      in TmRecord (subs_rcd tml)
;;

(* Evaluate until no more terms can be evaluated *)
let rec eval ctx tm d =
  try
    let tm' = eval1 ctx tm in
      if (d) then print_endline ("\t" ^ string_of_term (tm'));
      eval ctx tm' d
  with
    NoRuleApplies -> subs_ctx ctx tm []
;;
