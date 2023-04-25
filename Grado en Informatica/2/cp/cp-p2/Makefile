CFLAGS=-g -pthread
OBJS=compress.o chunk_archive.o options.o queue.o comp.o
LIBS=-lz
CC=gcc

all: comp

comp: $(OBJS)
	$(CC) $(CFLAGS) -o $@ $(OBJS) $(LIBS)

test_queue: $(OBJS)
	gcc -pthread test_queue.c queue.o -o test_queue

clean: 
	rm -f *.o comp
