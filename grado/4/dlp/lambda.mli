type ty =
    TyBool
  | TyNat
  | TyArr of ty * ty (* arrow type *)
  | TyPair of ty * ty
  | TyString
  | TyList of ty
  | TyRecord of (string * ty) list
;;

type term =
    TmTrue
  | TmFalse
  | TmIf of term * term * term
  | TmZero
  | TmSucc of term
  | TmPred of term
  | TmIsZero of term
  | TmVar of string
  | TmAbs of string * ty * term
  | TmApp of term * term
  | TmLetIn of string * term * term
  | TmFix of term
  | TmPair of term * term
  | TmProj of term * string
  | TmString of string
  | TmConcat of term * term
  | TmNil of ty
  | TmCons of ty * term * term
  | TmIsNil of ty * term
  | TmHead of ty * term
  | TmTail of ty * term  
  | TmRecord of (string * term) list
;;

type command =
    Eval of term
  | Bind of string * term
;;

type context =
  (string * ty * term option) list
;;

val emptyctx : context;;
val addbinding : context -> string -> ty -> term -> context;;
val addbinding_type : context -> string -> ty -> context;;
val getbinding_type : context -> string -> ty;;
val getbinding_term : context -> string -> term;;

val string_of_ty : ty -> string;;
exception Type_error of string;;
val typeof : context -> term -> ty;;

val string_of_term : term -> string;;
exception NoRuleApplies;;
val eval : context -> term -> bool -> term;;
exception Not_Found of string;;
