parser: regexp.cmi regexp_parser.cmo regexp_lex.cmo

regexp: parser regexp.cmo

derive: regexp derive.cmo

grep: grep.ml derive
	ocamlc -o grep regexp.cmo regexp_parser.cmo regexp_lex.cmo derive.cmo grep.ml

find: find.ml derive
	ocamlc -o find regexp.cmo regexp_parser.cmo regexp_lex.cmo derive.cmo find.ml

regexp.cmo: regexp.cmi regexp.ml regexp_parser.cmo regexp_lex.cmo
	ocamlc -c regexp.ml

regexp.cmi: regexp.mli
	ocamlc -c regexp.mli

regexp_parser.cmo: regexp_parser.mly regexp.cmi
	ocamlyacc regexp_parser.mly
	ocamlc -c regexp_parser.mli regexp_parser.ml

regexp_lex.cmo: regexp_lex.mll regexp_parser.cmo
	ocamllex regexp_lex.mll
	ocamlc -c regexp_lex.ml

derive.cmo: derive.mli derive.ml
	ocamlc -c derive.mli derive.ml

clean:
	rm -f *.cmo *.cmi grep find regexp_lex.ml regexp_parser.ml regexp_parser.mli regexp_parser.output
