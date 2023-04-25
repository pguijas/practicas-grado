-module(compress).

-export([compress/1, decompress/1]).

-define(BEST_COMPRESSION, 9).

compress(Data) ->
    Z = zlib:open(),
    zlib:deflateInit(Z, ?BEST_COMPRESSION),
    Comp_data = zlib:deflate(Z, Data, finish),
    zlib:deflateEnd(Z),
    Comp_data.

decompress(Comp_Data) ->
    Z = zlib:open(),
    zlib:inflateInit(Z),
    Data = zlib:inflate(Z, Comp_Data),
    zlib:inflateEnd(Z),
    Data.
