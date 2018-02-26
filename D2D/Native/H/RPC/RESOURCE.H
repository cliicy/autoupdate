
// For resource tracking

#ifdef _NETWARE_NLM

void  *rpc_malloc(int __size);
void  rpc_free(void * __mem);
void  *rpc_calloc(int __um, int __size );
void  *rpc_realloc(void *__mem, int __size );

char *rpc_strdup(const char *__string);

bool_t rpc_register_mem(void *__mem);
bool_t rpc_deregister_mem(void *__mem);

#else

#include <malloc.h>
#define rpc_malloc malloc
#define rpc_calloc calloc
#define rpc_free free
#define rpc_realloc realloc
#define rpc_strdup strdup

#endif
