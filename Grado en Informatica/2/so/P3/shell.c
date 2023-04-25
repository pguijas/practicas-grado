//Autor:
//	Pedro Guijas Bravo 53797370w

#include "p3.c"		

//para mem
void f1(){}
void f2(){}
void f3(){}
int vg1, vg2, vg3;

int main(){
	char hist[MAX_CMDS][255];
	int bucle=0;
	bool funcionando=true;
	char comando[255];
	char * cmd_troceado[5]; 
	int trozos;
	int i, size;
	int flag = 0;
	struct listS list;
	struct nodo * listProcs = inicializarP();
	inicializarL(&list);

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
					if (trozos==1)
						histf(hist, bucle);
					else if (strcmp(cmd_troceado[1],"-c")==0){
						bucle=0;
						printf("Historic borrado\n");
					}
					break;
			
				case 6:	//fin, end, exit
				case 7:
				case 8:
					finalizar(&list);
					liberarP(listProcs);
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
						while(i < trozos){
							listar(cmd_troceado[i],flag);
							i++;
						}
					}

					break;

				case 13: //asignar
					if (trozos>=2){
				    	if (0 == strcmp(cmd_troceado[1],"-malloc")){
				    		//malloc
				    		cmd_AsignarMalloc(cmd_troceado,&list);

						} else if (0 == strcmp(cmd_troceado[1],"-mmap")){
							//mmap 
							cmd_AsignarMmap(cmd_troceado,&list);

						} else if (0 == strcmp(cmd_troceado[1],"-createshared")){ //el que hizo el pdf se confundió de nombre
							//creteshared
							cmd_AsignarCreateShared(cmd_troceado,&list);

						} else if (0 == strcmp(cmd_troceado[1],"-shared")){
							//shared
							cmd_AsignarShared(cmd_troceado,&list);

						} else printf("uso: asignar [-malloc|-shared|-createshared|-mmap] ....\n");
				    } else recorrerL(&list,7);
					break;

				case 14: //desasignar
					if (trozos>2){
    					if (0 == strcmp(cmd_troceado[1],"-malloc")){
    						//malloc
    						if (trozos==2){
    							recorrerL(&list,1);
    						} else{
    							d_malloc(&list, atoi(cmd_troceado[2]));
    						}
    					} else if (0 == strcmp(cmd_troceado[1],"-mmap")){
    						//mmap
    						if (trozos==2){
    							recorrerL(&list,4);
    						} else{
    							d_mmap(&list, cmd_troceado[2]);
    						}
    					} else if (0 == strcmp(cmd_troceado[1],"-shared")){
    						//shared
    						if (trozos==2){
    							recorrerL(&list,2);
    						} else{
    							d_shared(&list, atoi(cmd_troceado[2]));
    						}
    					}
    				} else if (trozos==2){
    					d_address(&list,(void *)strtoul(cmd_troceado[1],NULL,16));//mal no es address
    				} else recorrerL(&list,7);
    				break;

				case 15: //borrarkey
						cmd_borrakey(cmd_troceado);
						break;
				case 16: //mem
					if (trozos>=2){
						for (int i = 1; i < trozos; ++i){
							if (strcmp(cmd_troceado[i],"-malloc")==0){
								flag = flag ^ 1;
							} else if (strcmp(cmd_troceado[i],"-shared")==0){
								flag = flag ^ 2;
							} else if (strcmp(cmd_troceado[i],"-mmap")==0){
								flag = flag ^ 4;
							} else if (strcmp(cmd_troceado[i],"-all")==0){
								flag=7;
								break;
							} else {
								printf("algumento no válido\n");
								break;
							}
						}
						recorrerL(&list,flag);
					} else{
						mem3x3(&vg1,&vg2,&vg3,f1,f2,f3);
					}
					break;
				case 17: //volcar
					if (trozos>=2){
						if (trozos==2)
							volcar((char *)strtoul(cmd_troceado[1],NULL,16),25);
						else
							volcar((char *)strtoul(cmd_troceado[1],NULL,16),atoi(cmd_troceado[2]));
					}
					break;
				case 18: //llenar
					if (trozos>=2){
						if (trozos==2)
							llenar((char *)strtoul(cmd_troceado[1],NULL,16),128,65);
						else if (trozos==3)
							llenar((char *)strtoul(cmd_troceado[1],NULL,16),atoi(cmd_troceado[2]),65);
						else if (trozos==4)
							llenar((char *)strtoul(cmd_troceado[1],NULL,16),atoi(cmd_troceado[2]),atoi(cmd_troceado[3]));
					}
					break;
				case 19: //recursiva
					if (trozos>=2) doRecursiva(atoi(cmd_troceado[1]));
					break;

				case 20: //rfich
					if (trozos>=3){
						if (trozos==3)
							size=rfich(cmd_troceado[1],(void *)strtoul(cmd_troceado[2],NULL,16),-1);
						else
							size=rfich(cmd_troceado[1],(void *)strtoul(cmd_troceado[2],NULL,16),atoi(cmd_troceado[3]));
 						
 						if(size==-1) 
							perror("Imposible leer fichero");
						else
							printf("leidos %i bytes de %s en %p\n", size, cmd_troceado[1] ,(void *)strtoul(cmd_troceado[2],NULL,16));
					} else printf("faltan parametros\n");
					break;

				case 21: //wfich
					if (trozos>=4){
						if (strcmp(cmd_troceado[1],"-o")==0){
							size=wfich(true,cmd_troceado[2],(void *)strtoul(cmd_troceado[3],NULL,16), atoi(cmd_troceado[4]));
							if (size==-1)
								perror("Imposible escribir fichero");
							else
								printf("escritos %i bytes en %s desde %p\n", size,  cmd_troceado[2], (void *)strtoul(cmd_troceado[3],NULL,16));
						}else {
							size=wfich(false,cmd_troceado[1],(void *)strtoul(cmd_troceado[2],NULL,16), atoi(cmd_troceado[3]));
							if (size==-1)
								perror("Imposible escribir fichero");
							else
								printf("escritos %i bytes en %s desde %p\n", size, cmd_troceado[1], (void *)strtoul(cmd_troceado[2],NULL,16));
						}
						
					} else printf("faltan parametros\n");	
					break;
				
				case 22: //priority
					priority(trozos,cmd_troceado);
					break;
				case 23: //fork
					fork_cmd();
					break;

				case 24: //exec
					exec(trozos,cmd_troceado);
					break;

				case 25: //pplano
					pplano(trozos-1,cmd_troceado+1);
					break;

				case 26: //splano
					splano(trozos-1,cmd_troceado+1,listProcs);
					break;

				case 27: //listarprocs
					recorrerP(listProcs);
					break;

				case 28: //proc
					proc(trozos,cmd_troceado,listProcs);
					break;

				case 29: //borrarprocs
					borrarprocs(trozos,cmd_troceado,listProcs);
					break;

				default: 
					not_in_shell(trozos,cmd_troceado,listProcs);
					break;
			}	
		} else bucle--;
		
	}
	return 0;
}





