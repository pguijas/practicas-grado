#include "listProc.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/resource.h>

struct SEN{
	char *nombre;
	int senal;
};

static struct SEN sigstrnum[]={
        {"HUP", SIGHUP},
        {"INT", SIGINT},
        {"QUIT", SIGQUIT},
        {"ILL", SIGILL},
        {"TRAP", SIGTRAP},
        {"ABRT", SIGABRT},
        {"IOT", SIGIOT},
        {"BUS", SIGBUS},
        {"FPE", SIGFPE},
        {"KILL", SIGKILL},
        {"USR1", SIGUSR1},
        {"SEGV", SIGSEGV},
        {"USR2", SIGUSR2},
        {"PIPE", SIGPIPE},
        {"ALRM", SIGALRM},
        {"TERM", SIGTERM},
        {"CHLD", SIGCHLD},
        {"CONT", SIGCONT},
        {"STOP", SIGSTOP},
        {"TSTP", SIGTSTP},
        {"TTIN", SIGTTIN},
        {"TTOU", SIGTTOU},
        {"URG", SIGURG},
        {"XCPU", SIGXCPU},
        {"XFSZ", SIGXFSZ},
        {"VTALRM", SIGVTALRM},
        {"PROF", SIGPROF},
        {"WINCH", SIGWINCH},
        {"IO", SIGIO},
        {"SYS", SIGSYS},
		/*senales que no hay en todas partes*/
		#ifdef SIGPOLL
		    {"POLL", SIGPOLL},
		#endif
		#ifdef SIGPWR
			{"PWR", SIGPWR},
		#endif
		#ifdef SIGEMT
		    {"EMT", SIGEMT},
		#endif
		#ifdef SIGINFO
		    {"INFO", SIGINFO},
		#endif
		#ifdef SIGSTKFLT
		    {"STKFLT", SIGSTKFLT},
		#endif
		#ifdef SIGCLD
		    {"CLD", SIGCLD},
		#endif
		#ifdef SIGLOST
		    {"LOST", SIGLOST},
		#endif
		#ifdef SIGCANCEL
		    {"CANCEL", SIGCANCEL},
		#endif
		#ifdef SIGTHAW
		    {"THAW", SIGTHAW},
		#endif
		#ifdef SIGFREEZE
		    {"FREEZE", SIGFREEZE},
		#endif
		#ifdef SIGLWP
		    {"LWP", SIGLWP},
		#endif
		#ifdef SIGWAITING
		    {"WAITING", SIGWAITING},
		#endif
		{NULL,-1},
};    /*fin array sigstrnum */

char *NombreSenal(int sen){  /*devuelve el nombre senal a partir de la senal*/
    int i;                 	/* para sitios donde no hay sig2str*/
	for (i=0; sigstrnum[i].nombre!=NULL; i++)
		if (sen==sigstrnum[i].senal)
			return sigstrnum[i].nombre;
	return ("SIGUNKNOWN");
}

static struct nodo *crearnodo(){
	struct nodo *tmp = malloc(sizeof(struct nodo));
	if (tmp == NULL) {
		printf("memoria agotada\n"); exit(EXIT_FAILURE);
	}
	return tmp;
}

struct nodo * inicializarP(){
	struct nodo *l = crearnodo();
	l->sig=NULL;
	return l;
}


void insertarP(struct nodo * list, struct blockProc bloque){
	struct nodo * n = crearnodo();
	struct nodo * i = list;
	//ponemos valores al nodo
	n->contenido = bloque; 
	n->sig = NULL;
	while((i->sig)!=NULL)
		i=i->sig;
	i->sig=n;
}

void actualizar_mostrar(struct nodo * i){
	char * time;
	int status;
	//fecha
	time = asctime(gmtime(&(i->contenido.time)));
	time[strlen(time)-1]=0;
	//Actualizamos status
	if (strcmp(i->contenido.status,"RUNNING")==0 || strcmp(i->contenido.status,"STOPPED")==0){
		int wtpd=waitpid(i->contenido.pid, &status, WNOHANG |WUNTRACED |WCONTINUED);
		if (wtpd==-1)
			perror("Error");
		else if (wtpd==i->contenido.pid){
			if (WIFEXITED(status)){
	            i->contenido.status="TERMINATED";
	            i->contenido.r_value=WEXITSTATUS(status);
				i->contenido.priority=getpriority(PRIO_PROCESS,i->contenido.pid);
			} else if (WIFSIGNALED(status)){
	            i->contenido.status="SIGNALED";
	            i->contenido.signal=NombreSenal(WTERMSIG(status));
				i->contenido.priority=getpriority(PRIO_PROCESS,i->contenido.pid);
	        } else if (WIFSTOPPED(status)){
	        	i->contenido.status="STOPPED";
	      		i->contenido.signal=NombreSenal(WSTOPSIG(status));
				i->contenido.priority=getpriority(PRIO_PROCESS,i->contenido.pid);
	        }
		} else{
			i->contenido.status="RUNNING";
			i->contenido.priority=getpriority(PRIO_PROCESS,i->contenido.pid);
		}
	}
	//Mostrar
	if (strcmp(i->contenido.status,"TERMINATED")==0)
		printf("%i p=%i %s %s (%i) %s\n", i->contenido.pid, i->contenido.priority, time, i->contenido.status, i->contenido.r_value, i->contenido.cmdline);
	else if (strcmp(i->contenido.status,"RUNNING")==0 )
		printf("%i p=%i %s %s %s\n", i->contenido.pid, i->contenido.priority, time, i->contenido.status, i->contenido.cmdline);
	else
		printf("%i p=%i %s %s (%s) %s\n", i->contenido.pid, i->contenido.priority, time, i->contenido.status, i->contenido.signal, i->contenido.cmdline);
}

void recorrerP(struct nodo * list){
	struct nodo * i = list->sig;
	while(i!=NULL){
		actualizar_mostrar(i);
		i=i->sig;
	}
}

void buscarP(int pid, struct nodo * list){
	struct nodo * i = list->sig;
	while(i!=NULL){
		if (i->contenido.pid==pid)
			break;
		i=i->sig;
	}
	if (i!=NULL)
		actualizar_mostrar(i);
	 else
		recorrerP(list);
}

void borrarP(char * tipo, int pid,struct nodo * list){
	struct nodo * i = list;	
	while(i->sig!=NULL){
		if (((strcmp(tipo,i->sig->contenido.status)==0 || strcmp(tipo,"TODO")==0) && !(pid)) || pid==i->sig->contenido.pid){
	 		if (i->sig->sig==NULL){
				free(i->sig);
				i->sig=NULL;
			} else{
				struct nodo * aux=i->sig->sig;
				free(i->sig);
				i->sig=aux;
			}
		} else
			i=i->sig;
	}
}

void liberarP(struct nodo * list){
	borrarP("TODO",0,list);
	free(list);
}

