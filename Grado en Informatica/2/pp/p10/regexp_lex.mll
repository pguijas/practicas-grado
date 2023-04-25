{
    open Regexp_parser
}

rule token = parse
|   eof    { END }
|   '+'    { PLUS }
|   '*'    { ASTERISK }
|   '('    { LEFTPAR }
|   ')'    { RIGHTPAR }
|   '['    { LEFTBRACKET }
|   ']'    { RIGHTBRACKET }
|   '\\'   { BACKSLASH }
|   '?'    { QUESTION }
|   '|'    { OR }
|   '^'    { ACCENT }
|   '.'    { DOT }
|   '-'    { MINUS }
|   _ as c { CHAR c }
