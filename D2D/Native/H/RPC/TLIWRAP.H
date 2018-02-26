

#include <tiuser.h>

#ifdef _NETWARE_NLM

extern	char *rt_alloc (int fd, int struct_type, int fields);
extern	int	rt_free  (char  * ptr, int struct_type);

#else

#define rt_alloc t_alloc
#define rt_free t_free

#endif

#ifndef _TIUSER_

struct netbuf {
	unsigned	maxlen;
	unsigned	len;
	char		* buf;
};

#endif
