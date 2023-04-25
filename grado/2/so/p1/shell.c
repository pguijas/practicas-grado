#include "p1.h"		

int main(){
	char hist[MAX_CMDS][255];
	bool funcionando=true;
	char comando[255];
	char * cmd_troceado[5]; 
	int trozos;
	int i;
	int bucle=0;
	int flag = 0;

	while(funcionando){
		flag = 0;
		printf("-> ");
		fgets(comando,255,stdin);
		strcpy(hist[bucle++], comando); 	//introducimos el comando en el array para el hist	
		trozos = TrocearCadena(comando,cmd_troceado);

		if (!(trozos==0)){ //para que no casque al meter vacio el string cmd	
			cmd2int(cmd_troceado[0],&i);
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
					funcionando=false;
					break;
				
				case 9: //crear
					crear(trozos,cmd_troceado);
					break;

				case 10: //borrar
					if (trozos >= 2){
						if (strcmp(cmd_troceado[1],"-r")==0){ //borrar -r (nombre)
							if (trozos > 2) borrarR(cmd_troceado[2]); else listar(".",0);
						} else borrar(cmd_troceado[1]); //borrar nombre
					} else listar(".",0); //borrar 
					break;
				
				case 11: //info
					for (i = 1; i < trozos; ++i){
						info(cmd_troceado[i]);
					}
					break;
				
				case 12: //listar
					for (i = 1; i < trozos; ++i){	//codificamos los parametros
						if (0 == strcmp(cmd_troceado[i],"-l")){
							flag = flag ^ 1;
						} else if (0 == strcmp(cmd_troceado[i],"-r")){
							flag = flag ^ 2;
						} else if (0 == strcmp(cmd_troceado[i],"-v")){
							flag = flag ^ 4;
						} else break;
					}

					//si obtuvimos todos los parametros y no quedan trozos
					if (i == trozos){ 
						listar(".",flag);
					} else {
						for (i=i ; i < trozos; ++i){
							listar(cmd_troceado[i],flag);
						}
					}

					break;

				default : 
					not_found(cmd_troceado[0]);
					break;
			}	
		} else bucle--;
		
	}
	return 0;
}



