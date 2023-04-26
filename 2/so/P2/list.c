#include "list.h"

void inicializarL(struct listS * list){
	list->tamano=0;
};


void insertarL(struct listS * list, struct blockS block){
	if (list->tamano>=MAX_LIST){
		printf("Error, lista llena\n");
	} else {
		list->array[list->tamano++]=block;
	}
};

void borrarL(struct listS * list, struct blockS * block){
	int i, j;
	//obtener el indice
	for (i = 0; i < list->tamano-1; ++i){
		if (&(list->array[i])==block){
			break;
		}
	}
	for (j = i; j < list->tamano-1; ++j){
		list->array[j]=list->array[j+1];
	}
	list->tamano--;
};


void recorrerL(struct listS * list, int flag){
	char * time;
	int type;
	for (int i = 0; i < list->tamano; ++i){
		type = list->array[i].type;
		if ( ((type==T_MALLOC)&&(flag&1)) || ((type==T_SHARED)&&((flag>>1)&1)) || ((type==T_MMAP)&&((flag>>2)&1))){
			time = asctime(gmtime(&(list->array[i].time)));
			time[strlen(time)-1]=0;
			printf("%14p %11i %s %s", list->array[i].address, list->array[i].size, time, list->array[i].name);

			switch(list->array[i].type){
				case T_MALLOC:
					printf("\n");
					break;

				case T_MMAP:
					printf(" (descriptor %i)\n", list->array[i].fd);
					break;

				case T_SHARED:
					printf(" (key %i)\n", list->array[i].key);
					break;
			}
		}
	}
};

//obtenemos puntero del array a struct de determinada posición
//	(ojo a borrar ese elemento por que puntero apuntará al otro struct(elnsiguiente))
//
//FUNCIONAMIENTO: dependiendo de type buscará distintas cosas {T_MALLOC,T_MMAP,T_SHARED,ADDRESS}
struct blockS * buscarL(struct listS * list, int size_key, char * name, void * addr, int type){
	struct blockS * block;
	switch(type){
		case T_MALLOC:
			for (int i = 0; i < list->tamano; ++i){
				block=&list->array[i];
				if (block->size==size_key && block->type==type){
					return block;
				}
			}
			break;

		case T_MMAP:
			for (int i = 0; i < list->tamano; ++i){
				block=&list->array[i];
				if (strcmp(block->name,name)==0 && block->type==type){
					return block;
				}
			}
			break;

		case T_SHARED:
			for (int i = 0; i < list->tamano; ++i){
				block=&list->array[i];
				if (block->key==size_key && block->type==type){
					return block;
				}
			}
			break;

		case ADDRESS:
			for (int i = 0; i < list->tamano; ++i){
				block=&list->array[i];
				if (block->address==addr){
					return block;
				}
			}
			break;
	}
	return NULL;
};

bool isEmptyL(struct listS * list){
	return list->tamano<=0;
};

struct blockS * firstL(struct listS * list){
	if (isEmptyL(list))
		return NULL;
	else
		return &list->array[0];
};









