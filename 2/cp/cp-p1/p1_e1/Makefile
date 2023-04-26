
CC=gcc
CFLAGS=-Wall -pthread -g
LIBS=
OBJS=swap.o options.o

PROGS= swap

all: $(PROGS)

%.o : %.c
	$(CC) $(CFLAGS) -c $<

swap: $(OBJS)
	$(CC) $(CFLAGS) -o $@ $(OBJS) $(LIBS)

clean:
	rm -f $(PROGS) *.o *~

