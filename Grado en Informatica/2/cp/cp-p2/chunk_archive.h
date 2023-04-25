#ifndef __CHUNK_ARCHIVE_H__
#define __CHUNK_ARCHIVE_H__

// chunk is the main structure stored into the archive file
typedef struct {
    int size;             // size (in bytes) of the data
    int num;              // chunk number
    int offset;           // offset in the original file
    unsigned char *data;
} *chunk;

// an archive is a file that stores a sequence of numbered chunks
typedef struct {
    char *name;          // name of the file
    unsigned int chunks;          // number of chunks
    int *archive_offset; // offset table. archive_offset[i] is the offset in the archive where the data from chunk i starts.
    int *file_offset;    // offset table. file_offset[i] is the offset in the uncompressed file where chunk i starts.
    int *chunk_size;     // size table. chunk_size[i] is the size of the i chunk.
    int table_size;      // size of archive_offset, file_offset and chunk_size
    int fd;              // file descriptor
} *archive;

archive create_archive_file(char *filename); // create an archive with name filename
archive open_archive_file(char *filename);   // open an existing archive
void    close_archive_file(archive ar);      // close an archive

int   add_chunk(archive ar, chunk ch);          // add a chunk to a file
chunk get_chunk(archive ar, unsigned int chunk_num);    // get a chunk from a file
int   chunks(archive ar);                      // number of chunks the ar archive

chunk alloc_chunk(int size);  // Allocate a new chunk
void  free_chunk(chunk ch);   // Free the memory used by a chunk

#endif
