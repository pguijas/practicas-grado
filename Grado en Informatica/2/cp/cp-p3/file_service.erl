-module(file_service).

-export([start_file_reader/2, start_file_writer/1, stop_file_reader/1, stop_file_writer/1]).

-export([init_file_reader/3, init_file_writer/2]).

-define(INT_SIZE, 32).

%% File Reader

start_file_reader(File, Chunk_Size) -> %% Create a new file reader, returns {ok, PID}
    Reader = spawn_link(?MODULE, init_file_reader, [File, Chunk_Size, self()]),
    receive
        file_reader_init_ok         -> {ok, Reader};
        {file_reader_error, Reason} -> {error, Reason}
    end.

stop_file_reader(Reader) ->
    Reader ! {stop, self()},
    receive
        file_reader_stopped -> ok
    end.

init_file_reader(File, Chunk_Size, From) -> %% Init file reader. Opens the file
    case file:open(File, [read, binary]) of
        {ok, IoDev} ->
            From ! file_reader_init_ok,
            file_reader_loop(IoDev, Chunk_Size, 0);
        {error, Reason} ->
            From ! {file_reader_error, Reason}
    end.

file_reader_loop(IoDev, Chunk_Size, Chunk_Number) ->  %% Read chunks until done.
    receive
        {get_chunk, From} ->
            case file:position(IoDev, cur) of
                {ok, Offset} ->
                    case file:read(IoDev, Chunk_Size) of
                        {ok, Data} ->
                            From ! {chunk, Chunk_Number, Offset, Data},
                            file_reader_loop(IoDev, Chunk_Size, Chunk_Number+1);
                        eof ->
                            From ! eof,
                            file_reader_loop(IoDev, Chunk_Size, Chunk_Number);
                        {error, Reason} ->
                            From ! {error, Reason},
                            file_reader_loop(IoDev, Chunk_Size, Chunk_Number)
                    end;
                {error, Reason} ->
                    From ! {error, Reason},
                    file_reader_loop(IoDev, Chunk_Size, Chunk_Number)
            end;
        stop ->
            ok
    end.


%% File Writer

start_file_writer(File) -> %% Create a new file writer, returns {ok, Pid}
    Writer = spawn_link(?MODULE, init_file_writer, [File, self()]),
    receive
        file_writer_init_ok              -> {ok, Writer};
        {file_writer_init_error, Reason} -> {error, Reason}
    end.

stop_file_writer(Writer) ->
    Writer ! {stop, self()},
    receive
        file_writer_stopped -> ok
    end.

init_file_writer(File, From) ->  %% Open File. File is deleted if it already exists
    case file:open(File, [write, binary]) of
        {ok, IoDev} ->
            From ! file_writer_init_ok,
            file_writer_loop(IoDev, File);
        {error, Reason} ->
            From ! {file_writer_init_error, Reason}
    end.

file_writer_loop(IoDev, File) ->
    receive
        {write_chunk, Offset, Data} ->
            file:position(IoDev, {bof, Offset}),
            file:write(IoDev, Data),
            file_writer_loop(IoDev, File);
        stop ->
            ok;
        abort ->
            file:delete(File)
    end.
