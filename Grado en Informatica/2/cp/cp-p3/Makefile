OBJS=archive.beam comp.beam compress.beam file_service.beam
FLAGS=+debug_info

all: $(OBJS)

%.beam: %.erl
	erlc $(FLAGS) $<

clean:
	rm -f *.beam
