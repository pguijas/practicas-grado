#ifndef __COMPRESS_H__
#define __COMPRESS_H__

#include "chunk_archive.h"


chunk zcompress(chunk);    // Compress a chunk using zlib
chunk zdecompress(chunk);  // Decompress a chunk using zlib

#endif
