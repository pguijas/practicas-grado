-module(comp).

-export([comp/1, comp/2 , comp_proc/2, comp_proc/3 , decomp/1, decomp/2, decomp_proc/2, decomp_proc/3]).
-export([comp_loop/3,decomp_loop/3,wait_workers/1]).

-define(DEFAULT_CHUNK_SIZE, 1024*1024).
-define(DEFAULT_NUM_PROCS, 3). 

%%% File Compression

comp(File) -> %% Compress file to file.ch
    comp(File, ?DEFAULT_CHUNK_SIZE).

comp(File, Chunk_Size) -> 
    comp_proc(File, Chunk_Size,?DEFAULT_NUM_PROCS).

comp_proc(File, Procs) ->
    comp_proc(File, ?DEFAULT_CHUNK_SIZE, Procs).

comp_proc(File, Chunk_Size, Procs) -> %% Starts a reader, a writer and the workers in separate processes
        case file_service:start_file_reader(File, Chunk_Size) of
        {ok, Reader} ->
            case archive:start_archive_writer(File++".ch") of
                {ok, Writer} ->
                    %Generamos los workers y esperamos a que acaben/fallen
                    Workers = start_workers(Procs,[],comp_loop,Reader,Writer),
                    wait_workers(Workers),
                    Reader ! stop,
                    Writer ! stop;
                {error, Reason} ->
                    io:format("Could not open output file: ~w~n", [Reason])
            end;
        {error, Reason} ->
            io:format("Could not open input file: ~w~n", [Reason])
    end.

comp_loop(Reader, Writer, Pid_Shell) ->  %% Compression loop => get a chunk, compress it, send to writer
    Reader ! {get_chunk, self()},  %% request a chunk from the file reader
    receive
        {chunk, Num, Offset, Data} ->   %% got one, compress and send to writer
            Comp_Data = compress:compress(Data),
            Writer ! {add_chunk, Num, Offset, Comp_Data},
            comp_loop(Reader, Writer, Pid_Shell);
        eof ->  %% notificamos al shell
            Pid_Shell ! {done,self()};
        {error, id,Reason} ->
            Pid_Shell ! {error, self(), Reason}
    end.

% Worker Gestion

start_workers(0,W,_,_,_) -> W;
start_workers(N,W,Funcion,Reader,Writer) when N>0 -> 
    Pid=spawn_link(?MODULE,Funcion,[Reader,Writer,self()]),
    start_workers(N-1,[Pid|W],Funcion,Reader,Writer).

wait_workers([]) -> ok;
wait_workers(W) -> 
    receive 
        {done, Pid} -> 
            wait_workers([X || X<-W, X=/=Pid]);
        {error, Pid, Reason} -> 
            io:format("Error reading input file: ~w~p~n",[Reason,Pid]),
            [exit(X,"Error reading input file") || X<-W, X=/=Pid]
    end.
    

%% File Decompression

decomp(Archive) ->
    decomp(Archive, string:replace(Archive, ".ch", "", trailing)).

decomp(Archive, Output_File) ->
    decomp_proc(Archive, Output_File, ?DEFAULT_NUM_PROCS).

decomp_proc(Archive, Procs) ->
    decomp_proc(Archive, string:replace(Archive, ".ch", "", trailing), Procs).

decomp_proc(Archive, Output_File, Procs) ->
    case archive:start_archive_reader(Archive) of
        {ok, Reader} ->
            case file_service:start_file_writer(Output_File) of
                {ok, Writer} ->
                    %Generamos los workers y esperamos a que acaben/fallen
                    Workers = start_workers(Procs,[],decomp_loop,Reader,Writer),
                    wait_workers(Workers),
                    Reader ! stop,
                    Writer ! stop;
                {error, Reason} ->
                    io:format("Could not open output file: ~w~n", [Reason])
            end;
        {error, Reason} ->
            io:format("Could not open input file: ~w~n", [Reason])
    end.

decomp_loop(Reader, Writer, Pid_Shell) ->
    Reader ! {get_chunk, self()},  %% request a chunk from the reader
    receive
        {chunk, _Num, Offset, Comp_Data} ->  %% got one
            Data = compress:decompress(Comp_Data),
            Writer ! {write_chunk, Offset, Data},
            decomp_loop(Reader, Writer, Pid_Shell);
        eof ->    %% end of file => exit decompression
            Pid_Shell ! {done, self()};
        {error, Reason} ->
            Pid_Shell ! {error, self(), Reason}
    end.

