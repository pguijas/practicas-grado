#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <stdlib.h>
#include <fcntl.h>
#include <string.h>
#include <stdio.h>
#include <errno.h>
#include <pthread.h>
#include "compress.h"
#include "chunk_archive.h"
#include "queue.h"
#include "options.h"

#define CHUNK_SIZE (1024*1024)
#define QUEUE_SIZE 20

#define COMPRESS 1
#define DECOMPRESS 0


//  Ejercicio 1: queue.c
//  Ejercicio 2: threads_worker()
//  Ejercicio 3: read_comp(),save_comp()
//  Ejercicio 4: thread_worker(), read_decomp(), save_decomp()

//Argumentos Worker
struct args_w{
    queue in;
    queue out;
    chunk (*process)(chunk);
    pthread_mutex_t mutex;
    int restantes;
};

//Argumentos Read File de comprimir
struct args_rfc{
    queue in;
    int chunks;
    int chunk_size;
    int fd;
};

//Argumentos Save File de comprimir
struct args_sfc{
    queue out;
    int chunks;
    archive ar;
};

//Argumentos Read File de descomprimir
struct args_rfd{
    queue in;
    int chunks;
    archive ar;
};

//Argumentos Save File de descomprimir
struct args_sfd{
    queue out;
    int chunks;
    int fd;
};

//Función que se ejecutará en un thread para leer el archivo a comprimir
void * read_comp(void * args_){
    chunk ch;
    struct args_rfc * args = (struct args_rfc *)args_;
    int offset;
    for(int i=0; i<args->chunks; i++) {
        ch = alloc_chunk(args->chunk_size);

        offset=lseek(args->fd, 0, SEEK_CUR);

        ch->size   = read(args->fd, ch->data, args->chunk_size);
        ch->num    = i;
        ch->offset = offset;

        q_insert(args->in, ch);
    }
    return NULL;
}

//Función que se ejecutará en un thread para ir guardando el archivo comprimido
void * save_comp(void * args_){
    chunk ch;
    struct args_sfc * args = (struct args_sfc *)args_;
    for(int i=0; i<args->chunks; i++) {
        ch = q_remove(args->out);

        add_chunk(args->ar, ch);
        free_chunk(ch);
    }
    return NULL;
}

//Función que se ejecutará en un thread para leer el archivo comprimido
void * read_decomp(void * args_){
    chunk ch;
    struct args_rfd * args = (struct args_rfd *)args_;
    for(int i=0; i<args->chunks; i++) {
        ch = get_chunk(args->ar, i);
        q_insert(args->in, ch);
    }
    return NULL;
}

//Función que se ejecutará en un thread para ir guardando el archivo descomprimido
void * save_decomp(void * args_){
    chunk ch;
    struct args_sfd * args = (struct args_sfd *)args_;
    for(int i=0; i<args->chunks; i++) {
        ch=q_remove(args->out);
        lseek(args->fd, ch->offset, SEEK_SET);
        write(args->fd, ch->data, ch->size);
        free_chunk(ch);
    }
    return NULL;
}


// take chunks from queue in, run them through process (compress or decompress), send them to queue out
void * worker(void * args_) {
    chunk ch, res;
    struct args_w * args = (struct args_w *)args_;

    while (1){
        //Hasta que se procesen todos los chunks no terminan los hilos
        pthread_mutex_lock(&args->mutex); //protegemos el contador
        if (args->restantes>0){
            args->restantes=args->restantes-1;
            pthread_mutex_unlock(&args->mutex);

            ch = q_remove(args->in);
            res = args->process(ch);
            free_chunk(ch);
            q_insert(args->out, res);
        } else{
            pthread_mutex_unlock(&args->mutex);
            break;
        } 
    }
    return NULL;
}

//Crea threads que ejecutan worker
pthread_t * threads_worker(int n_threads, struct args_w * args){
    pthread_t * threads;
    threads = malloc(n_threads*sizeof(pthread_t));
    //Start
	for (int i = 0; i < n_threads; i++) {
		if ( 0 != pthread_create(threads+i, NULL, worker, args)) {
			printf("Could not create thread #%d", i);
			exit(1);
		}
	}
    return threads;
}


// Compress file taking chunks of opt.size from the input file,
// inserting them into the in queue, running them using a worker,
// and sending the output from the out queue into the archive file
void comp(struct options opt) {
    int fd, chunks;
    struct stat st;
    char comp_file[256];
    archive ar;
    queue in, out;
    pthread_t read_thread, save_thread;
    pthread_t * threads_w;
    struct args_rfc args_rfc;
    struct args_w * args_w;
    struct args_sfc args_sfc;

    if((fd=open(opt.file, O_RDONLY))==-1) {
        printf("Cannot open %s\n", opt.file);
        exit(0);
    }

    fstat(fd, &st);
    chunks = st.st_size/opt.size+(st.st_size % opt.size ? 1:0);

    if(opt.out_file) {
        strncpy(comp_file,opt.out_file,255);
    } else {
        strncpy(comp_file, opt.file, 255);
        strncat(comp_file, ".ch", 255);
    }

    ar = create_archive_file(comp_file);

    in  = q_create(opt.queue_size);
    out = q_create(opt.queue_size);

    // read input file and send chunks to the in queue (1 thread)
    args_rfc.in=in;
    args_rfc.chunks=chunks;
    args_rfc.chunk_size=opt.size;
    args_rfc.fd=fd;
    if ( 0 != pthread_create(&read_thread, NULL, read_comp, &args_rfc)) {
		printf("Could not create the read file thread");
		exit(1);
	}    

    // compression of chunks from in to out
    args_w = malloc(sizeof(struct args_w));
    args_w->in=in;
    args_w->out=out;
    args_w->process=zcompress;
    args_w->restantes=chunks;
    pthread_mutex_init(&args_w->mutex,NULL);
    threads_w = threads_worker(opt.num_threads,args_w);

    // send chunks to the output archive file
    args_sfc.out=out;
    args_sfc.chunks=chunks;
    args_sfc.ar=ar;
    if ( 0 != pthread_create(&save_thread, NULL, save_comp, &args_sfc)) {
		printf("Could not create the read file thread");
		exit(1);
	}

    //Esperamos a que se escriba el archivo -> determinará el final del programa
    pthread_join(save_thread,NULL);

    free(threads_w);
    pthread_mutex_destroy(&args_w->mutex);
    free(args_w);
    close_archive_file(ar);
    close(fd);
    q_destroy(in);
    q_destroy(out);
}

// Decompress file taking chunks of opt.size from the input file,
// inserting them into the in queue, running them using a worker,
// and sending the output from the out queue into the decompressed file
void decomp(struct options opt) {
    int fd;
    struct stat st;
    char uncomp_file[256];
    archive ar;
    queue in, out;
    pthread_t read_thread, save_thread;
    pthread_t * threads_w;
    struct args_rfd args_rfd;
    struct args_w * args_w;
    struct args_sfd args_sfd;

    if((ar=open_archive_file(opt.file))==NULL) {
        printf("Cannot open archive file\n");
        exit(0);
    };

    if(opt.out_file) {
        strncpy(uncomp_file, opt.out_file, 255);
    } else {
        strncpy(uncomp_file, opt.file, strlen(opt.file) -3);
        uncomp_file[strlen(opt.file)-3] = '\0';
    }

    if((fd=open(uncomp_file, O_RDWR | O_CREAT | O_TRUNC, S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH))== -1) {
        printf("Cannot create %s: %s\n", uncomp_file, strerror(errno));
        exit(0);
    }

    in  = q_create(opt.queue_size);
    out = q_create(opt.queue_size);

    // read chunks with compressed data
    args_rfd.in=in;
    args_rfd.chunks=chunks(ar);
    args_rfd.ar=ar;
    if ( 0 != pthread_create(&read_thread, NULL, read_decomp, &args_rfd)) {
		printf("Could not create the read file thread");
		exit(1);
	}

    // decompress from in to out
    args_w = malloc(sizeof(struct args_w));
    args_w->in=in;
    args_w->out=out;
    args_w->process=zdecompress;
    args_w->restantes=chunks(ar);
    pthread_mutex_init(&args_w->mutex,NULL);
    threads_w = threads_worker(opt.num_threads,args_w);

    // write chunks from output to decompressed file
    args_sfd.out=out;
    args_sfd.chunks=chunks(ar);
    args_sfd.fd=fd;
    if ( 0 != pthread_create(&save_thread, NULL, save_decomp, &args_sfd)) {
		printf("Could not create the read file thread");
		exit(1);
	}
    //Esperamos a que se escriba el archivo -> determinará el final del programa
    pthread_join(save_thread,NULL);

    free(threads_w);
    free(args_w);
    close_archive_file(ar);
    close(fd);
    q_destroy(in);
    q_destroy(out);
}

int main(int argc, char *argv[]) {
    struct options opt;

    opt.compress    = COMPRESS;
    opt.num_threads = 3;
    opt.size        = CHUNK_SIZE;
    opt.queue_size  = QUEUE_SIZE;
    opt.out_file    = NULL;

    read_options(argc, argv, &opt);
    if(opt.compress == COMPRESS) comp(opt);
    else decomp(opt);
}
