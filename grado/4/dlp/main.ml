open Parsing;;
open Lexing;;
open Lambda;;
open Parser;;
open Lexer;;

(* error mesaje of a bad parameter *)
let usage_msg = "top [-d|--debug]"
(* auxlar variable to allow debugging *)
let debug = ref false
(* we will not use this *)
let input_files = ref []
let output_file = ref ""
let anon_fun filename = input_files := filename::!input_files
(* arguments that we might expect *)
let speclist = [("--debug", Arg.Set debug, "Output debug information"); ("-d", Arg.Set debug, "Output debug information")]

exception Not_Ending;;

(* will not stop until receiving ;; *)
let rec get_exp s = 
  (* Split is gonna divide our string by the ;. So we want to look an empty string which means ;; or ;(end of string) *)
  let rec check_exp l p = match l with
    | ""::[]    -> raise (Not_Ending) (* when the expresion ends with ; (not with ;;)*)
    | []        -> raise (Not_Ending)
    | ""::t     -> List.rev p
    | h::t      -> check_exp t (h::p)
  in try 
    check_exp (String.split_on_char ';' s) []
  with 
    (* getting next line introduced *)
    Not_Ending -> get_exp (s^" "^(read_line ()))
;;

(* Tokenizing and evaluating a list of expresions (strings) *)
let rec exec exp ctx = match exp with
  | [] -> ctx
  | h::t -> 
      match s token (from_string (h)) with
        | Eval tm  -> 
            let ty = (string_of_ty (typeof ctx tm)) and tm = string_of_term (eval ctx tm (!debug))
            in print_endline ("- : " ^ ty ^ " = " ^ tm);
            exec t ctx
        | Bind (name,tm) -> 
            let ty = (string_of_ty (typeof ctx tm)) and tm_eval = eval ctx tm (!debug) in
              print_endline ("val " ^ name ^ " : " ^ ty ^ " = " ^ string_of_term (tm_eval) );
              (* Updating Context *)
              exec t (addbinding ctx name (typeof ctx tm) (tm_eval)) 
;;

(* Reading, parsing, evaluating and recursion*)
let top_level_loop () =
  print_endline "Evaluator of lambda expressions...";
  let rec loop ctx =
    print_string ">> ";
    flush stdout;
    try
      (* Getting text over Stdin, Executing, Updating context and Recursion *)
      loop (exec (get_exp (read_line ())) ctx);
    with
       Lexical_error ->
         print_endline "lexical error";
         loop ctx
     | Parse_error ->
         print_endline "syntax error";
         loop ctx
     | Type_error e ->
         print_endline ("type error: " ^ e);
         loop ctx
     | End_of_file ->
         print_endline "...bye!!!"
  in
    loop emptyctx
  ;;

(* Program Init *)
Arg.parse speclist anon_fun usage_msg;
top_level_loop ()
;;