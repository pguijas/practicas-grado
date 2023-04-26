#include "p0.h"		



int main(){
	char hist[MAX_CMDS][255];
	bool funcionando=true;
	char comando[255];
	char * cmd_troceado[5]; 
	int trozos;
	int i;
	int bucle=0;

	while(funcionando){
		printf("\033[0m"); //pone color por defecto
		printf("-> ");
		fgets(comando,255,stdin);
		//si copiamos al array el cmd despues de trocear cadena solo nos pone la 1º división 
		strcpy(hist[bucle++], comando); 	//introducimos el comando en el array para el hist	
		trozos = TrocearCadena(comando,cmd_troceado);

		if (!(trozos==0)){ //para que no casque al meter vacio el string cmd	
			//error al meter comandos en arraty
			cmd2int(cmd_troceado[0],&i);
			printf("\033[1;31m"); //cambia color
			switch(i){		
				case 0:	//autores
					autores(trozos,cmd_troceado);
					break;

				case 1:	//pid
					pid(trozos,cmd_troceado);
					break;
			
				case 2:	//cdir
					cdir(trozos,cmd_troceado);
					break;
			
				case 3:	//fecha
					fecha();
					break;
			
				case 4:	//hora
					hora();
					break;

				case 5:	//hist
					histf(hist, bucle);
					break;
			
				case 6:	//fin, end, exit
				case 7:
				case 8:
					printf("\033[0m"); 
					funcionando=false;
					break;
			
				default : 
					not_found(cmd_troceado[0]);
					break;
			}	
		} else bucle--;
		
	}
	return 0;
}

