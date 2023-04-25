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
#define N_CMDS 13
#define MAX_CMDS 4096


//FUNCIONES:
void listar_r(char * ruta, int flag);
void listar(char * ruta, int flag);
int TrocearCadena(char * cadena, char * trozos[]);
void autores(int trozos, char * cmd_troceado[]);
void pid(int trozos, char * cmd_troceado[]);
void cdir(int trozos, char * cmd_troceado[]);
void hora();
void fecha();
void histf(char arr[MAX_CMDS][255], int tamano);
void cmd2int(char * cmd, int * i);
void not_found(char * comando);
char TipoFichero (mode_t m);
void info(char * file);
void listar(char * ruta, int flag);
void listar_r(char * dir, int flag);
void crear(int trozos, char * cmd_troceado[]);
void borrar(char * arch);
void borrarR(char * dir);


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
   	"crear",			
   	"borrar",			
   	"info",			
   	"listar",	
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


//Funciones nuevas:


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
					printf("%s %lu \n" , dp->d_name, statbuf.st_size);
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
	if (trozos >= 2){	
		if (0 == strcmp(cmd_troceado[1],"-d")) { // -d carpeta
			if (trozos > 2){
				if (mkdir(cmd_troceado[2], 0666)==(-1)) perror("Error");
			} else listar(".",0); //-d (solo)
		} else {
			if (open(cmd_troceado[1], O_CREAT | O_EXCL, 0666) == -1) perror("Error");
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
