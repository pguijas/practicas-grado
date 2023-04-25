#include <errno.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "options.h"

//array y tamaño
struct buffer {
	int *data;
	int size;
};

struct thread_info {
	pthread_t       thread_id;        // id returned by pthread_create()
	int             thread_num;       // application defined thread #
};

struct args {
	int thread_num;       // application defined thread #
	int delay;			  // delay between operations
	int * iterations;	  //puntero a las iterracciones que deben hacer
	pthread_mutex_t * mutex_it;	  //puntero al mutex que controla el acceso a iterations
	struct buffer   *buffer;		  // Shared buffer
	pthread_mutex_t * mutexes;
};


void *swap(void *ptr){
	struct args *args =  ptr;
	while(1){
		//bloqueamos,comprobamos y actualizamos(si podemos) las iteracciones
		pthread_mutex_lock(args->mutex_it);
		if (*(args->iterations)>0){
			*(args->iterations)=*(args->iterations)-1;
			pthread_mutex_unlock(args->mutex_it);
			int i,j,tmp,w;
			w=1;
			//intercambiar el mismo elemento no hace nada
			do{
				i=rand() % args->buffer->size;
				j=rand() % args->buffer->size;	
			} while(i==j);
			
			while(w==1){
				//bloqueamos los mutex de las 2 posiciones a las que accedemos (si es posible)
				pthread_mutex_lock(args->mutexes+i);
				if (pthread_mutex_trylock(args->mutexes+j)==0){
					printf("Thread %d swapping positions %d (== %d) and %d (== %d)\n", 
					args->thread_num, i, args->buffer->data[i], j, args->buffer->data[j]);

					tmp = args->buffer->data[i];
					if(args->delay) usleep(args->delay); // Force a context switch

					args->buffer->data[i] = args->buffer->data[j];
					if(args->delay) usleep(args->delay);
						
					args->buffer->data[j] = tmp;
					if(args->delay) usleep(args->delay);
					
					w=0;
					pthread_mutex_unlock(args->mutexes+j);	//desbloqueamos una posicion
				}
				pthread_mutex_unlock(args->mutexes+i);	//desbloqueamos otra posicion
			}
		} else {
			//desbloqueamos el mutex de las iteraciones y paramos si ya se ejecutaron las iteraciones dadas
			pthread_mutex_unlock(args->mutex_it); 
			break;
		}		
	}


	return NULL;
}

void print_buffer(struct buffer buffer) {
	int i;
	
	for (i = 0; i < buffer.size; i++)
		printf("%i ", buffer.data[i]);
	printf("\n");
}


void start_threads(struct options opt){
	int i;
	struct thread_info *threads;
	struct args *args;
	struct buffer buffer;
	pthread_mutex_t * mutexes;
	int * iterations;
	pthread_mutex_t * mutex_it;

	srand(time(NULL));
	//reserva espacio 
	if((buffer.data=malloc(opt.buffer_size*sizeof(int)))==NULL) {
		printf("Out of memory\n");
		exit(1);
	}
	buffer.size = opt.buffer_size;

	printf("creating %d threads\n", opt.num_threads);
	threads = malloc(sizeof(struct thread_info) * opt.num_threads);
	args = malloc(sizeof(struct args) * opt.num_threads);
	mutexes = malloc(sizeof(pthread_mutex_t) * opt.buffer_size);
	iterations = malloc(sizeof(int));
	mutex_it = malloc(sizeof(pthread_mutex_t));

	if (threads == NULL || args==NULL || mutexes==NULL) {
		printf("Not enough memory\n");
		exit(1);
	}

	//rellena 
	*iterations=opt.iterations;
	pthread_mutex_init(mutex_it, NULL);
	for(i=0; i<buffer.size; i++){
		buffer.data[i]=i;
		pthread_mutex_init(mutexes+i, NULL);
	}

	printf("Buffer before: ");
	print_buffer(buffer);	

	// Create num_thread threads running swap() 
	for (i = 0; i < opt.num_threads; i++) {
		threads[i].thread_num = i;
		
		args[i].thread_num = i;
		args[i].buffer     = &buffer;
		args[i].mutexes    = mutexes;
		args[i].delay      = opt.delay;
		args[i].iterations = iterations;
		args[i].mutex_it = mutex_it;

		if ( 0 != pthread_create(&threads[i].thread_id, NULL, swap, &args[i])) {
			printf("Could not create thread #%d", i);
			exit(1);
		}
	}
	
	// Wait for the threads to finish
	for (i = 0; i < opt.num_threads; i++)
		pthread_join(threads[i].thread_id, NULL);

	for (i = 0; i < buffer.size; ++i)
		pthread_mutex_destroy(mutexes+i);
	pthread_mutex_destroy(mutex_it);

	// Print the buffer
	printf("Buffer after:  ");
	print_buffer(buffer);
		
	free(args);
	free(threads);
	free(buffer.data);
	free(mutexes);
	free(mutex_it);
        
	pthread_exit(NULL);
}

int main (int argc, char **argv){
	//tiempos, destroys y frees
	struct options opt;
	
	// Default values for the options
	opt.num_threads = 10;
	opt.buffer_size = 10;
	opt.iterations  = 100;
	opt.delay       = 10;
	
	read_options(argc, argv, &opt);

	start_threads(opt);

	exit (0);
}
