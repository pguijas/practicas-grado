#include <time.h>
#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#define MAX_LIST 4096
#define T_MALLOC 0
#define T_MMAP 1
#define T_SHARED 2
#define ADDRESS 3

struct blockS{
	void * address;
	char name[33];
	int size;
	time_t time;
	int fd;
	int type;
	int key;
	//tendré que agregar un vaina para el tipo
};

struct listS{
	struct blockS array[MAX_LIST];
	int tamano;
};


void inicializarL(struct listS * list);
void insertarL(struct listS * list, struct blockS block);
void borrarL(struct listS * list, struct blockS * block);
void recorrerL(struct listS * list,  int tipo);
struct blockS * buscarL(struct listS * list, int size_key, char * name, void * addr, int type);
bool isEmptyL(struct listS * list);
struct blockS * firstL(struct listS * list);