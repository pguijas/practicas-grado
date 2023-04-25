#include "p1.h"

int main(int argc, char *argv[]){
	int i, flag = 0;
	for (i = 1; i < argc; ++i){	//codificamos los parametros
		if (0 == strcmp(argv[i],"-l")){
			flag = flag ^ 1;
		} else if (0 == strcmp(argv[i],"-r")){
			flag = flag ^ 2;
		} else if (0 == strcmp(argv[i],"-v")){
			flag = flag ^ 4;
		} else break;
	}

	//si obtuvimos todos los parametros y no quedan trozos
	if (i == argc){ 
		listar(".",flag);
	} else {
		for (i=i ; i < argc; ++i){
			listar(argv[i],flag);
		}
	}
	return 0;
}