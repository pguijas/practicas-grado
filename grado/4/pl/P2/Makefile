FUENTE = practica2
PRUEBA = prueba.xml

all_mac: compile_mac run
all: compile_linux run

compile_mac:
	$(info ************************ COMPILING 4 MAC ************************ )
	flex $(FUENTE).l
	bison -o $(FUENTE).tab.c $(FUENTE).y -yd -v
	gcc -o $(FUENTE) lex.yy.c $(FUENTE).tab.c -ll -ly

compile_linux:
	$(info ************************ COMPILING 4 LINUX ************************ )
	flex $(FUENTE).l
	bison -o $(FUENTE).tab.c $(FUENTE).y -yd
	gcc -o $(FUENTE) lex.yy.c $(FUENTE).tab.c -lfl -ly

run:
	$(info ************************ RUNING WITH $(PRUEBA) ************************ )
	./$(FUENTE) < $(PRUEBA)

clean:
	rm $(FUENTE) lex.yy.c $(FUENTE).tab.c $(FUENTE).tab.h

