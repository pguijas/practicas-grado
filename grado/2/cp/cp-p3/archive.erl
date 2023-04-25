-module(archive).

-export([start_archive_reader/1, start_archive_writer/1, stop_archive_reader/1, stop_archive_writer/1]).

-export([init_archive_reader/2, init_archive_writer/2]).

-define(INT_SIZE, 32).
-define(INT_SIZE_BYTES, 4).
-define(HEADER_SIZE, 9).

%% Archive Writer

start_archive_writer(File) ->
    Writer = spawn_link(?MODULE, init_archive_writer, [File, self()]),
    receive
        archive_writer_init_ok              -> {ok, Writer};
        {archive_writer_init_error, Reason} -> {error, Reason}
    end.

stop_archive_writer(Writer) ->
    Writer ! {stop, self()},
    receive
        archive_writer_stopped -> ok
    end.

init_archive_writer(File, From) ->
    case file:open(File, [write, binary]) of
        {ok, IoDev} ->
            case file:write(IoDev, <<"CHUNK">>) of
                ok ->
                    case file:write(IoDev, <<0:?INT_SIZE/integer-unsigned-little>>) of
                        ok ->
                            From ! archive_writer_init_ok,
                            archive_writer_loop(IoDev, 0);
                        {error, Reason} ->
                            From ! {archive_writer_init_error, Reason}
                    end;
                {error, Reason} ->
                    From ! {archive_writer_init_error, Reason}
            end;
        {error, Reason} ->
            From ! {archive_writer_init_error, Reason}
    end.

archive_writer_loop(IoDev, Chunks) ->
    receive
        {add_chunk, Num, Offset, Data} ->
            Flat_Data = list_to_binary(Data),
            file:position(IoDev, eof),
            Size = size(Flat_Data),
            file:write(IoDev, <<Size:?INT_SIZE/integer-unsigned-little, Num:?INT_SIZE/integer-unsigned-little,
                                Offset:?INT_SIZE/integer-unsigned-little, Flat_Data/binary>>),
            file:position(IoDev, 5),
            file:write(IoDev, <<(Chunks+1):?INT_SIZE/integer-unsigned-little>>),
            archive_writer_loop(IoDev, Chunks+1);
        stop ->
            ok
    end.

%% Archive Reader

start_archive_reader(File) ->
    Reader = spawn_link(?MODULE, init_archive_reader, [File, self()]),
    receive
        archive_reader_init_ok              -> {ok, Reader};
        {archive_reader_init_error, Reason} -> {error, Reason}
    end.

stop_archive_reader(Reader) ->
    Reader ! {stop, self()},
    receive
        archive_reader_stopped -> ok
    end.

init_archive_reader(File, From) ->
    case file:open(File, [read, binary]) of
        {ok, IoDev} ->
            case file:read(IoDev, 5) of
                {ok, <<"CHUNK">>} ->
                    case file:read(IoDev, ?INT_SIZE_BYTES) of
                        {ok, <<Chunks:?INT_SIZE/integer-unsigned-little>>} ->
                            From ! archive_reader_init_ok,
                            archive_reader_loop(IoDev, File, Chunks, 0, read_chunk_map(IoDev, Chunks));
                        {error, Reason} ->
                            From ! {archive_reader_init_error, Reason};
                        _ ->
                            From ! {archive_reader_init_error, not_a_chunk_file}
                    end;
                {error, Reason} ->
                    From ! {archive_reader_init_error, Reason};
                _ ->
                    From ! {archive_reader_init_error, not_a_chunk_file}
            end;
        {error, Reason} ->
            From ! {archive_reader_init_error, Reason}
    end.

archive_reader_loop(IoDev, File, Chunks, Current_Chunk, Chunk_Map) ->
    receive
        {get_chunk, From} ->
            if
                Current_Chunk==Chunks ->
                    From ! eof,
                    archive_reader_loop(IoDev, File, Chunks, Current_Chunk, Chunk_Map);
                true ->
                    try
                        #{Current_Chunk := {Size, File_Offset, Archive_Offset}} = Chunk_Map,
                        file:position(IoDev, Archive_Offset),
                        {ok, Data} = file:read(IoDev, Size),
                        From ! {chunk, Current_Chunk, File_Offset, Data}
                    catch
                        badmatch -> From ! {error, no_chunk}
                    end,
                    archive_reader_loop(IoDev, File, Chunks, Current_Chunk+1, Chunk_Map)
            end;
        stop ->
            ok;
        abort ->
            file:delete(File)
    end.


read_chunk_map(_, 0, _, Map) ->
    Map;
read_chunk_map(IoDev, Chunks, Archive_Offset, Map) ->
    file:position(IoDev, Archive_Offset),
    {ok, <<Size:?INT_SIZE/integer-unsigned-little, Num:?INT_SIZE/integer-unsigned-little, File_Offset:?INT_SIZE/integer-unsigned-little>>} = file:read(IoDev, ?INT_SIZE_BYTES*3),
    read_chunk_map(IoDev, Chunks-1, Archive_Offset+Size+3*?INT_SIZE_BYTES, Map#{Num => {Size, File_Offset, Archive_Offset+?INT_SIZE_BYTES*3}}).

read_chunk_map(IoDev, Chunks) ->
    read_chunk_map(IoDev, Chunks, ?HEADER_SIZE, #{}).
