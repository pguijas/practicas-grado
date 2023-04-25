#include <stdlib.h>
#include <pthread.h>
#include "queue.h"

// circular array
typedef struct _queue {
    int size;
    int used;
    int first;
    void **data;
    pthread_mutex_t mutex;
    pthread_cond_t full, empty;
} _queue;


queue q_create(int size) {
    queue q = malloc(sizeof(_queue));
 
    q->size  = size;
    q->used  = 0;
    q->first = 0;
    q->data  = malloc(size*sizeof(void *));
    pthread_mutex_init(&q->mutex, NULL);
    pthread_cond_init(&q->full,NULL);
    pthread_cond_init(&q->empty,NULL);
    
    return q;
}

int q_elements(queue q) {
    return q->used;
}

int q_insert(queue q, void *elem) {
    pthread_mutex_lock(&q->mutex);
    //Esperamos por un sitio
    while(q->size == q->used)
        pthread_cond_wait(&q->full,&q->mutex);
    q->data[(q->first+q->used) % q->size] = elem;    
    q->used++;
    //Si estaba vacio despertamos a los que estaban esoperando
    if (q->used==1)
        pthread_cond_broadcast(&q->empty);
    pthread_mutex_unlock(&q->mutex);
    return 1;
}

void *q_remove(queue q) {
    void *res;
    pthread_mutex_lock(&q->mutex);
    //Esperamos por un elemento
    while(q->used==0)
        pthread_cond_wait(&q->empty,&q->mutex);
    res = q->data[q->first];
    q->first = (q->first+1) % q->size;
    q->used--;
    //Si estaba lleno despertamos a los que estaban esoperando
    if (q->used==q->size-1)
        pthread_cond_broadcast(&q->full);
    pthread_mutex_unlock(&q->mutex);
    return res;
}

void q_destroy(queue q) {
    free(q->data);
    pthread_mutex_destroy(&q->mutex);
    pthread_cond_destroy(&q->full);
    pthread_cond_destroy(&q->empty);
    free(q);
    
}
