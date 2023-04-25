
all: conj.ml conj.mli \
     auto.ml auto.mli \
     simb_yacc.mly simb_lex.mll \
     er_yacc.mly er_lex.mll \
     af_yacc.mly af_lex.mll \
     gic_yacc.mly gic_lex.mll \
     ap_yacc.mly ap_lex.mll \
     mt_yacc.mly mt_lex.mll \
     ergo.ml ergo.mli \
     graf.ml graf.mli
	ocamlc -c conj.mli
	ocamlc -c conj.ml

	ocamlc -c auto.mli
	ocamlc -c auto.ml

	ocamlyacc simb_yacc.mly
	ocamlc -c simb_yacc.mli
	ocamlc -c simb_yacc.ml
	ocamllex simb_lex.mll
	ocamlc -c  simb_lex.ml
	rm -f simb_yacc.ml simb_yacc.mli simb_lex.ml

	ocamlyacc er_yacc.mly
	ocamlc -c er_yacc.mli
	ocamlc -c er_yacc.ml
	ocamllex er_lex.mll
	ocamlc -c  er_lex.ml
	rm -f er_yacc.ml er_yacc.mli er_lex.ml

	ocamlyacc af_yacc.mly
	ocamlc -c af_yacc.mli
	ocamlc -c af_yacc.ml
	ocamllex af_lex.mll
	ocamlc -c  af_lex.ml
	rm -f af_yacc.ml af_yacc.mli af_lex.ml

	ocamlyacc gic_yacc.mly
	ocamlc -c gic_yacc.mli
	ocamlc -c gic_yacc.ml
	ocamllex gic_lex.mll
	ocamlc -c  gic_lex.ml
	rm -f gic_yacc.ml gic_yacc.mli gic_lex.ml

	ocamlyacc ap_yacc.mly
	ocamlc -c ap_yacc.mli
	ocamlc -c ap_yacc.ml
	ocamllex ap_lex.mll
	ocamlc -c  ap_lex.ml
	rm -f ap_yacc.ml ap_yacc.mli ap_lex.ml

	ocamlyacc mt_yacc.mly
	ocamlc -c mt_yacc.mli
	ocamlc -c mt_yacc.ml
	ocamllex mt_lex.mll
	ocamlc -c  mt_lex.ml
	rm -f mt_yacc.ml mt_yacc.mli mt_lex.ml

	ocamlc -c ergo.mli
	ocamlc -c ergo.ml

	ocamlc -c graf.mli
	ocamlc -c graf.ml

	ocamlc -a conj.cmo auto.cmo \
	  simb_lex.cmo simb_yacc.cmo \
	  er_lex.cmo er_yacc.cmo \
	  af_lex.cmo af_yacc.cmo \
	  gic_lex.cmo gic_yacc.cmo \
	  ap_lex.cmo ap_yacc.cmo \
	  mt_lex.cmo mt_yacc.cmo \
	  ergo.cmo graf.cmo -o talf.cma

	rm -f *.cmo

clean:
	rm -f *~ *.cm*

