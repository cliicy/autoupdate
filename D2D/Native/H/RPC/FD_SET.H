#ifndef _RPC_FD_SET_
#define _RPC_FD_SET_

#ifndef RPC_FD_SETSIZE
#define RPC_FD_SETSIZE      64
#endif

typedef struct rpc_fd_set
{
	u_int fd_count;
	u_int fd_array[RPC_FD_SETSIZE];
} rpc_fd_set;

#define RPC_FD_SET(fd, set) do { \
    if ((set)->fd_count < RPC_FD_SETSIZE) \
        (set)->fd_array[(set)->fd_count++]=(fd);\
} while(0)

#define RPC_FD_ZERO(set) ((set)->fd_count=0)

#define RPC_FD_CLR(fd, set) do { \
    u_int __i; \
    for (__i = 0; __i < (set)->fd_count ; __i++) { \
        if ((set)->fd_array[__i] == fd) { \
            while (__i < (set)->fd_count-1) { \
                (set)->fd_array[__i] = \
                    (set)->fd_array[__i+1]; \
                __i++; \
            } \
            (set)->fd_count--; \
            break; \
        } \
    } \
} while(0)


extern int rpc_select(rpc_fd_set *readfds, struct timeval *wait);

#endif /* !_RPC_FD_SET */
