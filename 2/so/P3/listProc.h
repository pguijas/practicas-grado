#include <time.h>
#include <stdbool.h>

struct blockProc{
	int pid;
	int priority;
	char * status;
	char * signal;
	int r_value;
	char cmdline[255];
	time_t time;
};

struct nodo{
	struct blockProc contenido;
	struct nodo * sig;
};

struct nodo * inicializarP();
void liberarP(struct nodo * list);
void insertarP(struct nodo * list, struct blockProc bloque);
void recorrerP(struct nodo * list);
void buscarP(int pid, struct nodo * list);
void borrarP(char * tipo, int pid, struct nodo * list);