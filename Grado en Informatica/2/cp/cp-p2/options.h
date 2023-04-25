#ifndef __OPTIONS_H__
#define __OPTIONS_H__

struct options {
    int compress;
    int num_threads;
    int size;
    int queue_size;
    char *file;
    char *out_file;
};

int read_options(int argc, char **argv, struct options *opt);


#endif
