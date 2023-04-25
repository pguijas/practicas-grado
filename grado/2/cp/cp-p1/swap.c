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
	int 		thread_num;       // application defined thread #
	int 	        delay;			  // delay between operations
	int		iterations;
	struct buffer   *buffer;		  // Shared buffer
	pthread_mutex_t * mutex;
};

void *swap(void *ptr){
	struct args *args =  ptr;
	pthread_mutex_lock(args->mutex); //bloqueamos acceso al array
	while(args->iterations--) {
		int i,j, tmp;
		//intercambiar el mismo elemento no hace nada
		do{
			i=rand() % args->buffer->size;
			j=rand() % args->buffer->size;	
		} while(i==j);

		printf("Thread %d swapping positions %d (== %d) and %d (== %d)\n", 
			args->thread_num, i, args->buffer->data[i], j, args->buffer->data[j]);
				
		tmp = args->buffer->data[i];
		if(args->delay) usleep(args->delay); // Force a context switch

		args->buffer->data[i] = args->buffer->data[j];
		if(args->delay) usleep(args->delay);
		
		args->buffer->data[j] = tmp;
		if(args->delay) usleep(args->delay);

	}
	pthread_mutex_unlock(args->mutex);	//desbloqueamos acceso al array
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
	pthread_mutex_t mutex;

	srand(time(NULL));
	//reserva espacio 
	if((buffer.data=malloc(opt.buffer_size*sizeof(int)))==NULL) {
		printf("Out of memory\n");
		exit(1);
	}
	buffer.size = opt.buffer_size;
	//rellena el array
	for(i=0; i<buffer.size; i++)
		buffer.data[i]=i;

	printf("creating %d threads\n", opt.num_threads);
	threads = malloc(sizeof(struct thread_info) * opt.num_threads);
	args = malloc(sizeof(struct args) * opt.num_threads);

	if (threads == NULL || args==NULL) {
		printf("Not enough memory\n");
		exit(1);
	}

	printf("Buffer before: ");
	print_buffer(buffer);
	pthread_mutex_init(&mutex, NULL);

	// Create num_thread threads running swap() 
	for (i = 0; i < opt.num_threads; i++) {
		threads[i].thread_num = i;
		
		args[i].thread_num = i;
		args[i].buffer     = &buffer;
		args[i].mutex      = &mutex;
		args[i].delay      = opt.delay;
		args[i].iterations = opt.iterations;

		if ( 0 != pthread_create(&threads[i].thread_id, NULL,
					 swap, &args[i])) {
			printf("Could not create thread #%d", i);
			exit(1);
		}
	}
	
	// Wait for the threads to finish
	for (i = 0; i < opt.num_threads; i++)
		pthread_join(threads[i].thread_id, NULL);

	// Print the buffer
	printf("Buffer after:  ");
	print_buffer(buffer);
		
	free(args);
	free(threads);
	free(buffer.data);
    pthread_mutex_destroy(&mutex);
	pthread_exit(NULL);
}

int main (int argc, char **argv){
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
