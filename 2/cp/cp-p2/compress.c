#include <stdio.h>
#include <stdlib.h>
#include <zlib.h>
#include "compress.h"

chunk zcompress(chunk ch) {
    chunk res;
    z_stream st;
    int out_size;
    
    out_size = ch->size;
    
    res = malloc(sizeof(*res));
    
    res->data   = malloc(out_size);
    res->size   = 0;
    res->num    = ch->num;
    res->offset = ch->offset;
    
    st.zalloc = Z_NULL;
    st.zfree  = Z_NULL;
    st.opaque = Z_NULL;
    
    if(deflateInit(&st, Z_BEST_COMPRESSION) != Z_OK) {
        printf("Could not initialize zlib\n");
        exit(0);
    }

    st.avail_in  = ch->size;
    st.next_in   = ch->data;
    st.next_out  = res->data;
    st.avail_out = out_size;
    
    while(1) {
        switch(deflate(&st, Z_FINISH)) { 
            case Z_OK:  // Buffer was not big enough
            case Z_BUF_ERROR:
                res->data     = realloc(res->data, out_size*2);
                st.next_out   = res->data+out_size-st.avail_out;
                st.avail_out += out_size;
                out_size     *= 2;
                break;
            case Z_STREAM_END: // Done
                res->size = out_size-st.avail_out;
                deflateEnd(&st);
                return res;
            default:
                printf("Error compressing data\n");
                deflateEnd(&st);
                exit(0);
        }
    }

    return res;
}

chunk zdecompress(chunk ch) {
    z_stream st;
    chunk res;
    int out_size = ch->size*2;
    
    res = malloc(sizeof(*res));
    
    res->data   = malloc(out_size);
    res->num    = ch->num;
    res->offset = ch->offset;
    
    st.zalloc   = Z_NULL;
    st.zfree    = Z_NULL;
    st.opaque   = Z_NULL;

    st.avail_in  = ch->size;
    st.next_in   = ch->data;
    st.next_out  = res->data;
    st.avail_out = out_size;
    
    if(inflateInit(&st) != Z_OK) {
        printf("Could not initialize zlib\n");
    }
    
    int ret;
    do {
        switch(ret=inflate(&st, Z_FINISH)) {
            case Z_STREAM_ERROR:
                printf("Malformed stream (stray pointer?)\n");
                exit(0);
            case Z_NEED_DICT:            
            case Z_DATA_ERROR:
            case Z_MEM_ERROR:
                inflateEnd(&st);
                printf("Error decompressing data\n");
                exit(0);
            case Z_BUF_ERROR:
            case Z_OK:
                res->data     = realloc(res->data, out_size*2);
                st.next_out   = res->data+out_size-st.avail_out;
                st.avail_out += out_size;
                out_size     *= 2;
                break;
            case Z_STREAM_END:
                res->size = out_size-st.avail_out;
                inflateEnd(&st);
                return res;
        }
    } while (1);
}
