#include "queue.h"
#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>
#define NUMS 30
#define SIZE 10


void * introducir(void * args_) {
    int * i_p;
    queue q = (queue)args_;
    for (int i = 0; i < NUMS; i++){
        i_p=malloc(sizeof(int));
        *i_p=NUMS-i;
        q_insert(q,i_p);
        printf("%i in\n",*i_p);
    }
    return NULL;
}

void * sacar(void * args_) {
    int * i_p;
    queue q = (queue)args_;
    for (int e = 0; e < NUMS; e++){
        i_p = q_remove(q);
        printf("%i out\n",*i_p);
        free(i_p);
    }
    return NULL;
}

int main(){
    queue cola = q_create(SIZE);
    pthread_t i_t,s_t;
    if ( 0 != pthread_create(&i_t, NULL, introducir, cola)) {
		printf("Could not create thread");
	    exit(1);
	}    
    
    if ( 0 != pthread_create(&s_t, NULL, sacar, cola)) {
		printf("Could not create thread");
	    exit(1);
	}
    
    pthread_join(i_t,NULL);
    pthread_join(s_t,NULL);
    return 0;
}
