let input_opt_line in_file =
    try Some (input_line in_file)
    with End_of_file -> None

let rec parse_lines reg in_file =
    match input_opt_line in_file with
        Some s ->
            if Derive.matches_regexp s reg then
                print_endline s;
            parse_lines reg in_file
    |   None -> ()

let main () =
    if Array.length Sys.argv <> 3 then
        print_endline "grep regexp file"
    else
        try let in_file = open_in Sys.argv.(2)
            and parsed_reg = Derive.regexp_of_string Sys.argv.(1)
              in let all_line_reg = Regexp.concat (Regexp.concat (Regexp.repeat Regexp.any) parsed_reg)
                                                  (Regexp.repeat Regexp.any)
              in parse_lines all_line_reg in_file; close_in in_file
        with
            Parsing.Parse_error -> print_endline "Malformed regular expression"
        |   Sys_error x-> print_endline ("Could not open "^Sys.argv.(2)^": "^x);;

main ()
