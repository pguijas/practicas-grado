
shell: listMem.o listProc.o
	gcc -Wall -o shell shell.c listMem.o listProc.o

list.o: listMem.c
	gcc -Wall -c listMem.c

lista.o: listProc.c
	gcc -Wall -c listProc.c

clean:
	rm -f *.o shell