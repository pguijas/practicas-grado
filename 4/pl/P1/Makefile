FUENTE = practica1
PRUEBA = ej.txt

all_mac: compile_mac run
all: compile_linux run

compile_mac:
	flex $(FUENTE).l
	gcc -o $(FUENTE) lex.yy.c -ll

compile_linux:
	flex $(FUENTE).l
	gcc -o $(FUENTE) lex.yy.c -lfl

clean:
	rm $(FUENTE) lex.yy.c 

run:
	./$(FUENTE) < $(PRUEBA)

