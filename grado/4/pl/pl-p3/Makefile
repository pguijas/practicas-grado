BISON = gramm
LEX = lex
PRUEBA = acciones.txt

all: compile run

compile:
	flex $(LEX).l
	bison -o $(LEX).tab.c $(BISON).y -yd 
	gcc -o $(BISON) lex.yy.c $(LEX).tab.c -ll -ly

run:
	./$(BISON) < ej/$(PRUEBA)

run2:
	./$(BISON) ej/$(PRUEBA)

clean:
	rm $(BISON) lex.yy.c $(LEX).tab.c $(LEX).tab.h
