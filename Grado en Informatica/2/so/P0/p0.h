#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <unistd.h>	//pid,cdir
#include <time.h>
#define N_CMDS 9
#define MAX_CMDS 4096

const char valid_cmds[N_CMDS][33] = {
   	"autores",	
   	"pid",
   	"cdir",
   	"fecha",
   	"hora",
  	"hist",
   	"fin",		
   	"end",			
   	"exit",	
};

int TrocearCadena(char * cadena, char * trozos[]){				//divide el string en x cachos, los mete en un array y devuelve en cuantos lo dividió 
	int i=1;
	if ((trozos[0]=strtok(cadena," \n\t"))==NULL) return 0;
	while ((trozos[i]=strtok(NULL," \n\t"))!=NULL) i++;
	return i;
}

// cambiar esto pa que trague todo
void autores(int trozos, char * cmd_troceado[]){ 
	if (trozos==1) 		//si no tiene argumentos
		printf("Pedro Guijas Bravo: p.guijas \nRoque Mayo Esperante: roque.mayo.esperante\n");
	else if (strcmp(cmd_troceado[1],"-l")==0){
		printf("p.guijas\nroque.mayo.esperante\n");
	} else if (strcmp(cmd_troceado[1],"-n")==0){
	printf("Pedro Guijas Bravo\nRoque Mayo Esperante\n");
	} //solo revisamos el 1º argumento de la función pero lo dejamos así para imitar el funcionamiento del script0
	

}

// cambiar esto pa que trague todo
void pid(int trozos, char * cmd_troceado[]){
	if (trozos==1){
		printf("%i \n", getpid());	//id proceso
	} else if (/* trozos==2 && */ strcmp(cmd_troceado[1],"-p")==0) 
	{
		printf("%i \n", getppid());	//id proceso padre
	} 
}

// cambiar esto pa que trague todo
void cdir(int trozos, char * cmd_troceado[]){
	char ruta[255];
	if (trozos==1){ //mostramos ruta
		if (getcwd(ruta, 255)==NULL){
			printf("ERROR\n");
		} else printf("%s\n", ruta);
	} else if (chdir(cmd_troceado[1]) != 0){ //yo comprobaria los demas argumentos, pero el profesor nos sugirió imitar el script
		printf("Imposible cambiar directorio: No such file or directory\n");
	}
} 

void hora(){
	time_t tiempo = time(0);
    struct tm *tlocal = localtime(&tiempo);
    char output[128];
    strftime(output,128,"%H:%M:%S",tlocal);
    printf("%s\n", output);
}


void fecha(){
	time_t t; // time_t e unha variable de tipo puntero
    struct tm *tm;
  	char fecha[100];
  	t = time(NULL);
  	tm = localtime(&t); //Recibe un punteiro a unha variable de tempo (t*) e devolve a súa conversión como data LOCAL
  	printf("%s",asctime(tm));
}

void histf(char arr[MAX_CMDS][255], int tamano){
		for (int j = 0; j < tamano; ++j){
			printf("%s",arr[j]);
		}	
}

void cmd2int(char * cmd, int * i){
	for (*i = 0; *i < N_CMDS; ++*i){
		if (0 == strcmp(cmd, valid_cmds[*i])){	
			break;
		}
	}
}


void not_found(char * comando){
	printf("%s no encontrado\n", comando);
}

