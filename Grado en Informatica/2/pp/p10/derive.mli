val regexp_of_string : string -> Regexp.regexp
val nullable         : Regexp.regexp -> Regexp.regexp
val derive           : char -> Regexp.regexp -> Regexp.regexp
val matches_regexp   : string -> Regexp.regexp -> bool
val matches          : string -> string -> bool
