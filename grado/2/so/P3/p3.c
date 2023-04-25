#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <unistd.h>	//pid,cdir
#include <time.h>
#include <sys/stat.h> 
#include <fcntl.h>
#include <dirent.h>
#include <pwd.h>	//Oobtener nombre usuario
#include <grp.h>	//Oobtener nombre grupo
#include "listMem.h"	//Lista almacena reservas memoria
#include "listProc.h"	//Lista almacena procesos 2º plano
#include <sys/mman.h> //mmap
#include <sys/shm.h> //memoria compartida
#include <errno.h> //manejo errores
#include <ctype.h>
#include <unistd.h>
#include <sys/resource.h> //priority
#include <sys/wait.h>
#define N_CMDS 32
#define MAX_CMDS 4096

//FUNCIONES:
//P0
int TrocearCadena(char * cadena, char * trozos[]);
void autores(int trozos, char * cmd_troceado[]);
void pid(int trozos, char * cmd_troceado[]);
void cdir(int trozos, char * cmd_troceado[]);
void hora();
void fecha();
void histf(char arr[MAX_CMDS][255], int tamano);
void cmd2int(char * cmd, int * i);
//P1
char TipoFichero (mode_t m);
void info(char * file);
void listar(char * ruta, int flag);
void listar_r(char * dir, int flag);
void crear(int trozos, char * cmd_troceado[]);
void borrar(char * arch);
void borrarR(char * dir);
void listar_r(char * ruta, int flag);
void listar(char * ruta, int flag);
//P2
void a_malloc(int tamano, struct listS * list);
void * a_mmap (char * fichero, int protection, struct listS * list);
void * a_shared (key_t clave, size_t tam, struct listS * list);
void cmd_AsignarMalloc (char *cmd_troceado[], struct listS * list);
void cmd_AsignarMmap (char *cmd_troceado[], struct listS * list);
void cmd_AsignarCreateShared (char *cmd_troceado[], struct listS * list);
void cmd_borrakey (char *cmd_troceado[]);
void d_block(struct listS * list, struct blockS * block);
void d_malloc(struct listS * list, int tamano);
void d_mmap(struct listS * list, char * name);
void d_shared(struct listS * list, int key);
void d_address(struct listS * list, void * address);
void mem3x3(void * vg1, void * vg2, void * vg3, void * fp1, void * fp2, void * fp3);
void volcar(char * address, int cont);
void llenar(char * address, int cont, char byte);
void doRecursiva (int n);
ssize_t rfich (char *fich, void *p, ssize_t n);
ssize_t wfich (bool o,char *fich, void *p, ssize_t n);
void finalizar(struct listS * list);
//P3
void priority(int trozos, char * cmd_troceado[]);
void fork_cmd();
void exec(int trozos, char * cmd_troceado[]);
void pplano(int n, char * args[]);
void splano(int n, char * args[], struct nodo * listProcs);
void proc(int trozos, char * cmd_troceado[], struct nodo * listProcs);
void borrarprocs(int trozos, char * cmd_troceado[], struct nodo * listProcs);
void not_in_shell(int trozos, char * cmd_troceado[], struct nodo * listProcs);

const char valid_cmds[N_CMDS][30] = {
   	"autores",	
   	"pid",
   	"cdir",
   	"fecha",
   	"hora",
  	"hist",
   	"fin",		
   	"end",			
   	"exit",			
   	"crear",			
   	"borrar",			
   	"info",			
   	"listar",			
   	"asignar",			
   	"desasignar",			
   	"borrarkey",			
   	"mem",			
   	"volcar",			
   	"llenar",		
   	"recursiva",	
   	"rfich",		
   	"wfich",
   	"priority",
   	"fork",
   	"exec",
   	"pplano",
   	"splano",
   	"listarprocs",
   	"proc",
   	"borrarprocs",	
};

int TrocearCadena(char * cadena, char * trozos[]){//divide el string en x cachos, los mete en un array y devuelve en cuantos lo dividió 
	int i=1;
	if ((trozos[0]=strtok(cadena," \n\t"))==NULL) return 0;
	while ((trozos[i]=strtok(NULL," \n\t"))!=NULL) i++;
	return i;
}

// cambiar esto pa que trague todo
void autores(int trozos, char * cmd_troceado[]){ 
	if (trozos==1) 		//si no tiene cmd_troceadoumentos
		printf("Pedro Guijas Bravo: p.guijas \nRoque Mayo Esperante: roque.mayo.esperante\n");
	else if (strcmp(cmd_troceado[1],"-l")==0){
		printf("p.guijas\nroque.mayo.esperante\n");
	} else if (strcmp(cmd_troceado[1],"-n")==0){
	printf("Pedro Guijas Bravo\nRoque Mayo Esperante\n");
	} //solo revisamos el 1º cmd_troceadoumento de la función pero lo dejamos así para imitar el funcionamiento del script0
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
	} else if (chdir(cmd_troceado[1]) != 0){ //yo comprobaria los demas cmd_troceadoumentos, pero el profesor nos sugirió imitar el script
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

char TipoFichero (mode_t m){
	switch (m&S_IFMT) { /*and bit a bit con los bits de formato,0170000 */
		case S_IFSOCK: return 's'; /*socket */
		case S_IFLNK: return 'l'; /*symbolic link*/
		case S_IFREG: return '-'; /* fichero normal*/
		case S_IFBLK: return 'b'; /*block device*/
		case S_IFDIR: return 'd'; /*directorio */
		case S_IFCHR: return 'c'; /*char device*/
		case S_IFIFO: return 'p'; /*pipe*/
		default: return '?'; /*desconocido, no deberia aparecer*/
	}
}

char * ConvierteModo2 (mode_t m){
	static char permisos[12];
	strcpy (permisos,"---------- ");
	permisos[0]=TipoFichero(m);
	if (m&S_IRUSR) permisos[1]='r'; /*propietario*/
	if (m&S_IWUSR) permisos[2]='w';
	if (m&S_IXUSR) permisos[3]='x';
	if (m&S_IRGRP) permisos[4]='r'; /*grupo*/
	if (m&S_IWGRP) permisos[5]='w';
	if (m&S_IXGRP) permisos[6]='x';
	if (m&S_IROTH) permisos[7]='r'; /*resto*/
	if (m&S_IWOTH) permisos[8]='w';
	if (m&S_IXOTH) permisos[9]='x';
	if (m&S_ISUID) permisos[3]='s'; /*setuid, setgid y stickybit*/
	if (m&S_ISGID) permisos[6]='s';
	if (m&S_ISVTX) permisos[9]='t';
	return (permisos);
}

//mac:%10llu %s %3u %10s %10s %10llu %ºs %s
//ubuntu:%10ld %s %3li %10s %10s %10ld %s %s
void info(char * file){
	struct stat statbuf;
	char * f_mod;
	if (lstat(file,&statbuf)!=-1){
		//fecha de ultima modificación
		f_mod = asctime(localtime(&statbuf.st_atime));
		f_mod[strlen(f_mod) - 1] = 0; //con esto borramos el salto de linea indeseado por asctime
		printf("%10ld %s %3li %10s %10s %10ld %s %s" , 
			statbuf.st_ino, ConvierteModo2(statbuf.st_mode), statbuf.st_nlink , getpwuid(statbuf.st_uid)->pw_name, 
	  		getgrgid(statbuf.st_gid)->gr_name, statbuf.st_size, f_mod, file);
		//si es un symbolic link
		if (S_ISLNK(statbuf.st_mode)){
			char buffer[255] = "";
			readlink(file, buffer, sizeof(buffer));
			printf(" -> %s", buffer);

		} 
		printf("\n");
	} else perror("Error");
}

void listar(char * ruta, int flag){
	printf("********* %s\n", ruta);
	char * f_mod = NULL;
	struct dirent *dp = NULL;
	DIR * dirp = opendir(ruta);
	struct stat statbuf;

	if (dirp == NULL){
		perror("Error");
	} else {
		while((dp = readdir(dirp)) !=NULL){ //recorremos directorio
		   	char *dir_abs = malloc(strlen(ruta) + strlen(dp->d_name) + 3);
			strcpy(dir_abs, ruta);
			strcat(dir_abs,"/");
			strcat(dir_abs,dp->d_name);
			lstat(dir_abs,&statbuf);
		    if ( (flag>>2 & 1)==0 || ((strcmp(dp->d_name,".")!=0 && (strcmp(dp->d_name,"..")!=0))) ){
				if ((flag & 1)==1){	
					//fecha de ultima modificación
					f_mod = asctime(localtime(&statbuf.st_atime));
					f_mod[strlen(f_mod) - 1] = 0; //con esto borramos el salto de linea indeseado por asctime
				    printf("%10ld %s %3li %10s %10s %10ld %s %s" , 
				    	statbuf.st_ino, ConvierteModo2(statbuf.st_mode), statbuf.st_nlink , getpwuid(statbuf.st_uid)->pw_name, 
				    	getgrgid(statbuf.st_gid)->gr_name, statbuf.st_size, f_mod , dp->d_name);
					//si es simbolico
					if (S_ISLNK(statbuf.st_mode)){
						char buffer[255] = "";
						readlink(dir_abs, buffer, sizeof(buffer));
						printf(" -> %s", buffer);
					}  	
					printf("\n");
				} else {
					printf("%s %ld \n" , dp->d_name, statbuf.st_size);
				}
		    }
		    free (dir_abs);
		}
		closedir(dirp);
		if ((flag>>1 & 1)==1){ //recursividad: volvemos a recorrer buscando carpetas
			listar_r(ruta,flag);
		}
	}
}


void listar_r(char * dir, int flag){
	DIR * dirp = opendir(dir);
	struct dirent *dp;
	if(dirp != NULL){
		while((dp = readdir(dirp)) !=NULL){
			//si hay un directorio que no sea . o .. lo recorremos recursivamente
			if ((strcmp(dp->d_name,".")!=0 && (strcmp(dp->d_name,"..")!=0)) && dp->d_type == DT_DIR){
				char *dir_abs = malloc(strlen(dir) + strlen(dp->d_name) + 3);
				strcpy(dir_abs, dir);
				strcat(dir_abs,"/");
				strcat(dir_abs,dp->d_name);
				printf("%s\n", dir_abs);
				listar(dir_abs,flag);
				free(dir_abs);
			}
		}
	} else {
		perror("Error");
		closedir(dirp);
	}
}


void crear(int trozos, char * cmd_troceado[]){
	int fd;
	if (trozos >= 2){	
		if (0 == strcmp(cmd_troceado[1],"-d")) { // -d carpeta
			if (trozos > 2){
				if (mkdir(cmd_troceado[2], 0666)==(-1)) perror("Error");
			} else listar(".",0); //-d (solo)
		} else {
			if ((fd=open(cmd_troceado[1], O_CREAT | O_EXCL, S_IRUSR | S_IWUSR)) == -1) perror("Error");
			else close(fd);
		}
	} else listar(".",0);

}

void borrar(char * arch){
	if( remove(arch) != 0 ) perror( "Error" );
}


void borrarR(char * dir){
	DIR * dirp = opendir(dir);
	struct dirent *dp;
	char dir_abs [999];
	if(dirp != NULL){
		while((dp = readdir(dirp)) !=NULL){
			strcpy(dir_abs, dir);
			strcat(dir_abs,"/");
			strcat(dir_abs,dp->d_name);
			if ((strcmp(dp->d_name,".")!=0 && (strcmp(dp->d_name,"..")!=0))){
				if ( dp->d_type == DT_DIR ){
					borrarR(dir_abs);
				} else {
					borrar(dir_abs);
				}
			}
		}
		borrar(dir);
	} else perror("Error");
	closedir(dirp);
}

void a_malloc(int tamano, struct listS * list){
    struct blockS block;
	void * puntero=malloc(tamano);
	if (puntero!=NULL){
		block.address=puntero;
		block.size=tamano;
		block.time=time(NULL);
		block.type=T_MALLOC;
		strcpy(block.name,"malloc");
		insertarL(list, block);
		printf("Asignados %i bytes en %p\n", block.size, block.address);
    } else perror("Error Malloc");
}

void * a_mmap (char * fichero, int protection, struct listS * list){
	int fd, modo=O_RDONLY; 
	struct stat statbuf;
	void *puntero;
	struct blockS block;

	//si solo le dan w ponemos rw
	if (protection&PROT_WRITE) modo=O_RDWR;

	//abrimos fichero
	if (stat(fichero,&statbuf)==-1 || (fd=open(fichero, modo))==-1) 
		return NULL;

	//mapeamos fichero
	if ((puntero=mmap (NULL,statbuf.st_size, protection,MAP_PRIVATE,fd,0))==MAP_FAILED) 
		return NULL;
	
	/*Guardar Direccion de Mmap (p, s.st_size,fichero,df......);*/
	block.address=puntero;
	block.size=statbuf.st_size;
	block.time=time(NULL);
	block.fd=fd;
	block.type=T_MMAP;
	strcpy(block.name,fichero);
	insertarL(list, block);
   	
	return puntero; 
}

void * a_shared (key_t clave, size_t tam, struct listS * list){
	void * puntero;
	int aux,id,flags=0777; 
	struct shmid_ds s;
	struct blockS block;
	
	/*si tam no es 0 la crea en modo exclusivo */ 
	/*si tam es 0 intenta acceder a una ya creada*/ 
	if (tam) 
		flags=flags | IPC_CREAT | IPC_EXCL;

	/*no nos vale*/
	if (clave==IPC_PRIVATE){ 
	       errno=EINVAL; return NULL;}
	
	//obtenemos identificador
	if ((id=shmget(clave, tam, flags))==-1) 
		return (NULL);
	
	//insertamos en el espacio de direcciones
	if ((puntero=shmat(id,NULL,0))==(void*) -1){
		aux=errno; /*si se ha creado y no se puede mapear*/
		if (tam) /*se borra */ 
			shmctl(id,IPC_RMID,NULL);
		errno=aux;
		return (NULL);
	} 
	shmctl (id,IPC_STAT,&s);
	/* Guardar En Direcciones de Memoria Shared (p, s.shm_segsz, clave.....);*/ 
	block.address=puntero;
	block.size=s.shm_segsz;
	block.time=time(NULL);
	block.key=clave;
	block.type=T_SHARED;
	strcpy(block.name,"shared");
	insertarL(list, block);
	return (puntero);
}

void cmd_AsignarMalloc (char *cmd_troceado[], struct listS * list){
    if (cmd_troceado[2]==NULL){
    	recorrerL(list,1);
    } else
    	a_malloc(atoi(cmd_troceado[2]), list);
}

void cmd_AsignarMmap (char *cmd_troceado[], struct listS * list) {
   	char *perm;
   	void *p;
   	int protection=0;
   	
   	if (cmd_troceado[2]==NULL){
   		recorrerL(list,4);
   		return;
   	}

   	//codifica permisos
	if ((perm=cmd_troceado[3])!=NULL && strlen(perm)<4) {
		if (strchr(perm,'r')!=NULL) protection|=PROT_READ; 
		if (strchr(perm,'w')!=NULL) protection|=PROT_WRITE; 
		if (strchr(perm,'x')!=NULL) protection|=PROT_EXEC;
	}
	
	if ((p=a_mmap(cmd_troceado[2],protection, list))==NULL)
		perror ("Imposible mapear fichero");  
	else
	    printf ("fichero %s mapeado en %p\n", cmd_troceado[2], p);
}

void cmd_AsignarCreateShared (char *cmd_troceado[], struct listS * list) {
	key_t k; 
	size_t tam=0; 
	void *p;
	
	//si no pasan 2 cmd_troceadoumentos mostramos las direcciones de la lista
	if (cmd_troceado[3]==NULL){ //tal como lo programamos si 2 es null, 3 es null
		/*Listar Direcciones de Memoria Shared */
		recorrerL(list,2);
	   	return;
	}

	//tamaño 0 
	if (atoi(cmd_troceado[3])==0){
		printf("No se asignan bloques de 0 bytes\n");
		return;
	}
	
	k=(key_t) atoi(cmd_troceado[2]);
	tam=(size_t) atoll(cmd_troceado[3]);

	//Creamos memoria compartida
	if ((p=a_shared(k,tam, list))==NULL)
	        perror ("Imposible asignar memoria compartida");
	else
		printf ("Asignados %i bytes en %p\n",(int)tam,p);
}

void cmd_AsignarShared (char *cmd_troceado[], struct listS * list) {
    void * puntero;
	
	if (cmd_troceado[2]==NULL){
		recorrerL(list,2);
	} else if ((puntero=a_shared(atoi(cmd_troceado[2]),0, list))==NULL)
	    perror ("Imposible asignar memoria compartida");
	else
		printf ("Memoria compartida de clave %s en %p\n", cmd_troceado[2], puntero);
}


void cmd_borrakey (char *cmd_troceado[]){
	key_t clave;
	int id;
	char *key=cmd_troceado[1];

	if (key==NULL || (clave=(key_t) strtoul(key,NULL,10))==IPC_PRIVATE){ 
		printf ("	rmkey clave_valida\n");
		return;
	}

	if ((id=shmget(clave,0,0666))==-1){
		perror ("shmget: imposible obtener memoria compartida");
		return; 
	}

	if (shmctl(id,IPC_RMID,NULL)==-1)
		perror ("shmctl: imposible eliminar memoria compartida\n");
}

void d_block(struct listS * list, struct blockS * block){
	switch(block->type){
		case T_MALLOC:
			free(block->address);
			printf("block at address %p deallocated (malloc)\n", block->address);
			borrarL(list,block);
			break;

		case T_MMAP:
			if (munmap(block->address, block->size)==-1) perror("No se ha podido desmapear el fichero");
			close(block->fd);
			printf("block at address %p deallocated (mmap)\n", block->address);
			borrarL(list,block);
			break;

		case T_SHARED:
			if (shmdt(block->address)==-1) perror("No se ha podido borrar la memoria compartida del espacio de direcciones");
			printf("block at address %p deallocated (shared)\n", block->address);
			borrarL(list,block);
			break;

	}
}

void d_malloc(struct listS * list, int tamano){
	struct blockS * block;
	//buscar bloque de ese tamaño
	block=buscarL(list,tamano,NULL,NULL,T_MALLOC);
	if (block!=NULL){
		d_block(list,block);
	} else printf("No hay un bloque de ese tamano asignado con malloc\n");
}

void d_mmap(struct listS * list, char * name){
	struct blockS * block;
	//buscar bloque de archivo mapeado
	block=buscarL(list,0,name,NULL,T_MMAP);
	if (block!=NULL){
		d_block(list,block);
	} else printf("Fichero %s no mapeado\n", name);

}

void d_shared(struct listS * list, int key){
	struct blockS * block;
	//buscar bloque de memoria compartida cargada
	block=buscarL(list,key,NULL,NULL,T_SHARED);
	if (block!=NULL){
		d_block(list,block);
	} else printf("No hay bloque con clave %i mapeado en el proceso\n", key);
}

void d_address(struct listS * list, void * address){
	struct blockS * block;
	//buscar direccion
	block=buscarL(list,0,NULL,address,3);
	if (block!=NULL){
		d_block(list,block);
	} else printf("Direccion %p no asignada con malloc, shared o mmap\n", address);
}

void mem3x3(void * vg1, void * vg2, void * vg3, void * fp1, void * fp2, void * fp3){
	char vl[3];
	printf("Variables locales  %16p, %16p, %16p\n", &vl[0], &vl[1], &vl[2]);
	printf("Variables globales %16p, %16p, %16p\n", vg1, vg2, vg3);
	printf("Funciones programa %16p, %16p, %16p\n", fp1, fp2, fp3);	
	printf("Funciones libreria %16p, %16p, %16p\n", strcmp,strtoul,atoi);
}

void volcar(char * address, int cont){ 
	char * p = address;  	
	char c;
	int i = cont;
	int top = 25;
	while(i>0){
		i=i-25;
		if (i<0) top=25+i;
		for (int j = 0; j < top; j++) {
	        c = p[j];
	        switch(c){
		        case '\n':
		            printf(" \\n");
		            break;
		        case '\r':
		            printf(" \\r");
		            break;
		        case '\t':
		            printf(" \\t");
		            break;
		    	default:
		    		if (isprint(c)!=0)
		        		printf(" %2c", c);
					else
						printf("   ");

	        	break;
	        }
	    }	
	    printf("\n");
	    for (int j = 0; j < top; j++) {
	        c = p[j];
	        printf(" %2x", (unsigned char)c);
	    }
	    printf("\n");
	    p=&p[25];
	}
}

void llenar(char * address, int cont, char byte){
	for (int i = 0; i < cont; ++i){
		address[i]=byte;
	}
}

void doRecursiva (int n){
    char automatico[2048];
    static char estatico[2048];
    printf ("parametro n:%d en %p\n",n,&n);
    printf ("array estatico en:%p \n",estatico);
    printf ("array automatico en %p\n",automatico);
	n--;
	if (n>0)
        doRecursiva(n);
}

//n=-1 indica que se lea todo
ssize_t rfich (char *fich, void *p, ssize_t n){ 
	ssize_t nleidos,tam=n; 
	int df, aux;
	struct stat s;

	if (stat (fich,&s)==-1 || (df=open(fich,O_RDONLY))==-1) 
		return ((ssize_t)-1); //error

	if (n==(ssize_t)-1) 
		tam=(ssize_t) s.st_size;

	if ((nleidos=read(df,p, tam))==-1){ 
		aux=errno;
		close(df);
		errno=aux;
		return ((ssize_t)-1);
	}
	close (df);
	return (nleidos);
}

ssize_t wfich (bool o,char *fich, void *p, ssize_t n){ 
	int fd, nescritos, aux;
	int perm=O_RDWR | O_CREAT ;
	if (!o) perm=perm | O_EXCL;

	if ((fd=open(fich, perm, S_IRUSR | S_IWUSR)) == -1) 
		return ((ssize_t)-1); //error

	if ((nescritos=write(fd,p, n))==-1){ 
		aux=errno;
		close(fd);
		errno=aux;
		return ((ssize_t)-1);
	}
	close (fd);
	return (nescritos);
}

//liberamos toda la memodia
void finalizar(struct listS * list){
	struct blockS * block;
	while( !(isEmptyL(list)) ){
		block=firstL(list);
		switch(block->type){
			case T_MALLOC:
				free(block->address);
				borrarL(list,block);
				break;

			case T_MMAP:
				if (munmap(block->address, block->size)==-1) perror("No se ha podido desmapear el fichero");
				close(block->fd);
				borrarL(list,block);
				break;

			case T_SHARED:
				if (shmdt(block->address)==-1) perror("No se ha podido borrar la memoria compartida del espacio de direcciones");
				borrarL(list,block);
				break;

		}
	}
}

//Funciones nuevas

void priority(int trozos, char * cmd_troceado[]){
	if (trozos>=3){
		if (setpriority(PRIO_PROCESS,atoi(cmd_troceado[1]),atoi(cmd_troceado[2]))==-1)
			perror("Imposible Ejecutar");
		else
			printf("Prioridad del proceso %i es %i\n", atoi(cmd_troceado[1]), atoi(cmd_troceado[2]));
	} else {
		id_t pid;
		if (trozos==2)
			pid = atoi(cmd_troceado[1]);
		else
			pid = getpid();
		printf("Prioridad del proceso %i es %i\n", pid, getpriority(PRIO_PROCESS,pid));
	}
}

void fork_cmd(){
	pid_t pidH = fork();
	if (pidH==-1)
		perror("Error");
	else if (pidH > 0){
		printf("ejecutando proceso %i\n", pidH);
		waitpid(pidH,NULL,0);
	}
}

void exec(int trozos, char * cmd_troceado[]){
	if (trozos>=2){
		if (cmd_troceado[1][0]=='@'){
			if (trozos>=3){
				printf("prio %s\n", cmd_troceado[1]+1);
				if (setpriority(PRIO_PROCESS,0,atoi(cmd_troceado[1]+1))==-1)
					perror("Exec");
				execvp(cmd_troceado[2],cmd_troceado+2);
			} else
				printf("Exec: Faltan Argumentos\n");	
		} else
			execvp(cmd_troceado[1],cmd_troceado+1);
	}else 
		printf("exec: Faltan Argumentos\n");
}

void pplano(int n, char * args[]){
	int despl=0;
	pid_t pidH;
	if (n>=1)
		if (args[0][0]=='@'){
			despl=1; 
		}
	if ((n-despl)>=1){			
		pidH = fork();
		if (pidH==-1)
			perror("Imposible ejecutar");
		else{
			if (pidH>0)
				waitpid(pidH,NULL,0);
			else if (pidH==-1)
				perror("Imposible ejecutar");
			else{
				if (despl==1)
					if (setpriority(PRIO_PROCESS,0,atoi(args[0]+1))==-1)
						perror("Imposible ejecutar");	
				if (execvp(args[despl],args+despl)==-1){
					perror("Imposible ejecutar");
					exit(0);
				}
			}
		}
	}else 
		printf("pplano: Faltan Argumentos\n");
}

void splano(int n, char * args[], struct nodo * listProcs){
	struct blockProc bloque;
	int despl=0;
	pid_t pidH;

	if (n>=1)
		if (args[0][0]=='@'){
			despl=1; 
		}
	if (n-despl>=1){
		pidH = fork();
		if (pidH==-1)
			perror("Error");
		else{
			if (pidH>0){
				bloque.pid=pidH;
				bloque.status="RUNNING";
				bloque.r_value=0;
				bloque.time = time(0);
				strcpy(bloque.cmdline,"");
				for (int i = 0; i < n; ++i){
					strcat(bloque.cmdline,args[i]);
					strcat(bloque.cmdline," ");
				}
				insertarP(listProcs,bloque);
			}else if (pidH==-1)
				perror("Imposible ejecutar");
			else{
				if (despl==1)
					if (setpriority(PRIO_PROCESS,0,atoi(args[0]+1))==-1)
						perror("Imposible ejecutar");	
				if (execvp(args[despl],args+despl)==-1){
					perror("Imposible ejecutar");
					exit(0);
				}	
			}			
		}
	} else 
		printf("splano: Faltan Argumentos\n");
}


void proc(int trozos, char * cmd_troceado[], struct nodo * listProcs){
	if (trozos>=2){
		if (strcmp(cmd_troceado[1],"-fg")==0){
			if (trozos>2){
				int status;
				waitpid(atoi(cmd_troceado[2]),&status,0);
				printf("Proceso %i terminado", atoi(cmd_troceado[2]));		
				if (WIFEXITED(status))
					printf(" normalmente. Valor devuelto %i\n", WEXITSTATUS(status));
				else if (WIFSIGNALED(status))
		            printf(" por la senal %i\n", WTERMSIG(status));
				borrarP("",atoi(cmd_troceado[2]),listProcs);
			} else 
				printf("proc: faltan argumentos\n");
		} else buscarP(atoi(cmd_troceado[1]),listProcs);
	} else recorrerP(listProcs);

}

void borrarprocs(int trozos, char * cmd_troceado[], struct nodo * listProcs){
	bool ter, sig=false;
	if (trozos>=2){
		for (int i = 1; i < trozos; ++i){
			if (strcmp(cmd_troceado[i],"-term")==0)
				ter=true;			
			else if (strcmp(cmd_troceado[i],"-sig")==0)
				sig=true;
		}
		if (ter)
			borrarP("TERMINATED",0,listProcs);
		if (sig)
			borrarP("SIGNALED",0,listProcs);
	} else
		recorrerP(listProcs);
}

void not_in_shell(int trozos, char * cmd_troceado[], struct nodo * listProcs){
	if (strcmp(cmd_troceado[trozos-1],"&")==0){		
		cmd_troceado[trozos-1]=NULL;
		trozos--;
		splano(trozos,cmd_troceado,listProcs);
	} else
		pplano(trozos,cmd_troceado);
}

