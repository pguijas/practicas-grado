#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include "chunk_archive.h"

#define CHUNK_LIST_DEFAULT_SIZE 1000

typedef struct {
    int size;
    int num;
    char *data;
} disk_chunk;

archive create_archive_file(char *filename) {
    int fd;
    unsigned int chunks=0;

    archive ar;

    if((fd=open(filename, O_RDWR | O_CREAT | O_TRUNC, S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH))==-1) {
        printf("Could not create file %s: %s\n", filename, strerror(errno));
        exit(0);
    }

    write(fd, "CHUNK", 5);
    write(fd, &chunks, sizeof(unsigned int));

    ar=malloc(sizeof(*ar));

    ar->fd             = fd;
    ar->chunks         = 0;
    ar->name           = strdup(filename);
    ar->archive_offset = NULL;
    ar->file_offset    = NULL;
    ar->chunk_size     = NULL;
    ar->table_size     = 0;

    return ar;
}

void check_chunk_list_size(archive ar, int chunk_num) {
    while(chunk_num >= ar->table_size) {
        ar->archive_offset = realloc(ar->archive_offset, (ar->table_size+CHUNK_LIST_DEFAULT_SIZE)*sizeof(unsigned int));
        ar->chunk_size     = realloc(ar->chunk_size    , (ar->table_size+CHUNK_LIST_DEFAULT_SIZE)*sizeof(unsigned int));
        ar->file_offset    = realloc(ar->file_offset   , (ar->table_size+CHUNK_LIST_DEFAULT_SIZE)*sizeof(unsigned int));
        ar->table_size    += CHUNK_LIST_DEFAULT_SIZE;
    }
}

archive open_archive_file(char *filename) {
    int fd;
    unsigned int i;
    char magic[5];
    unsigned int chunks;
    int offset;
    archive ar;

    if((fd=open(filename, O_RDWR))==-1) {
        printf("Could not open file %s: %s\n", filename, strerror(errno));
        exit(0);
    }

    if(read(fd, magic, 5) < 5) {
        printf("Could not read %s\n", filename);
        exit(0);
    }

    if(strncmp(magic, "CHUNK", 5)) {
        printf("%s is not an archive file\n", filename);
        exit(0);
    }

    if(read(fd, &chunks, sizeof(unsigned int)) < (ssize_t) sizeof(unsigned int)) {
        printf("Could not read %s\n", filename);
        exit(0);
    }

    ar = malloc(sizeof(*ar));

    ar->fd             = fd;
    ar->chunks         = 0;
    ar->name           = strdup(filename);
    ar->archive_offset = malloc(chunks * sizeof(unsigned int));
    ar->chunk_size     = malloc(chunks * sizeof(unsigned int));
    ar->file_offset    = malloc(chunks * sizeof(unsigned int));
    ar->table_size     = 0;

    for(i=0; i<chunks; i++) {
        int size, chunk_num;
        read(fd, &size,      sizeof(unsigned int));
        read(fd, &chunk_num, sizeof(unsigned int));
        read(fd, &offset,    sizeof(unsigned int));

        ar->archive_offset[chunk_num] = lseek(fd, 0, SEEK_CUR);
        ar->chunk_size[chunk_num]     = size;
        ar->file_offset[chunk_num]    = offset;

        ar->chunks++;

        lseek(fd, size, SEEK_CUR);
    }

    return ar;
}

void close_archive_file(archive ar) {
    close(ar->fd);
    free(ar->archive_offset);
    free(ar->chunk_size);
    free(ar->name);
    free(ar->file_offset);
    free(ar);
}

int add_chunk(archive ar,chunk ch) {
    check_chunk_list_size(ar, ch->num);

    lseek(ar->fd, 0, SEEK_END);
    write(ar->fd, &ch->size, sizeof(unsigned int));
    write(ar->fd, &ch->num, sizeof(unsigned int));
    write(ar->fd, &ch->offset, sizeof(unsigned int));

    ar->archive_offset[ch->num] = lseek(ar->fd, 0, SEEK_CUR);
    ar->file_offset[ch->num]    = ch->offset;
    write(ar->fd, ch->data, ch->size);

    ar->chunk_size[ch->num] = ch->size;
    ar->chunks++;

    lseek(ar->fd, 5, SEEK_SET);
    write(ar->fd, &ar->chunks, sizeof(unsigned int));

    return 0;
}

chunk get_chunk(archive ar, unsigned int chunk_num) {
    chunk res;

    res=malloc(sizeof(*res));
    if(chunk_num > ar->chunks) {
        res->size   = 0;
        res->data   = NULL;
        res->num    = chunk_num;
        res->offset = -1;
        return res;
    }

    res->size   = ar->chunk_size[chunk_num];
    res->data   = malloc(res->size);
    res->offset = ar->file_offset[chunk_num];

    lseek(ar->fd, ar->archive_offset[chunk_num], SEEK_SET);
    read(ar->fd, res->data, res->size);

    return res;
}

int chunks(archive ar) {
    return ar->chunks;
}

chunk alloc_chunk(int size) {
    chunk res;
    res       = malloc(sizeof(*res));
    res->data = malloc(size);

    res->size   = size;
    res->offset = 0;

    return res;
}

void free_chunk(chunk ch) {
    free(ch->data);
    free(ch);
}
