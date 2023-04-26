
shell: list.o
	gcc -Wall -o shell shell.c list.o

list.o: list.c
	@echo "\033[0;31mCompi\033[1;37mlando:\033[0m"
	gcc -Wall -c list.c

clean:
	rm -f *.o shell