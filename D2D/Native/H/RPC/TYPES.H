/**************************************************************************
 * Sun RPC is a product of Sun Microsystems, Inc. and is provided for
 * unrestricted use provided that this legend is included on all tape
 * media and as a part of the software program in whole or part.  Users
 * may copy or modify Sun RPC without charge, but are not authorized
 * to license or distribute it to anyone else except as part of a product or
 * program developed by the user or with the express written consent of
 * Sun Microsystems, Inc.
 *
 * SUN RPC IS PROVIDED AS IS WITH NO WARRANTIES OF ANY KIND INCLUDING THE
 * WARRANTIES OF DESIGN, MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, OR ARISING FROM A COURSE OF DEALING, USAGE OR TRADE PRACTICE.
 *
 * Sun RPC is provided with no support and without any obligation on the
 * part of Sun Microsystems, Inc. to assist in its use, correction,
 * modification or enhancement.
 *
 * SUN MICROSYSTEMS, INC. SHALL HAVE NO LIABILITY WITH RESPECT TO THE
 * INFRINGEMENT OF COPYRIGHTS, TRADE SECRETS OR ANY PATENTS BY SUN RPC
 * OR ANY PART THEREOF.
 *
 * In no event will Sun Microsystems, Inc. be liable for any lost revenue
 * or profits or other special, indirect and consequential damages, even if
 * Sun has been advised of the possibility of such damages.
 *
 * Sun Microsystems, Inc.
 * 2550 Garcia Avenue
 * Mountain View, California  94043
**************************************************************************/
/* Rpc type additions to <sys/types.h> */
#ifndef _RPC_TYPES_H
#define _RPC_TYPES_H

#ifdef _NETWARE_NLM  /* for timeval definition */
#include <sys/types.h>
#include <sys/time.h>

/*
 * Operations on timevals.
 *
 * NB: timercmp does not work for >= or <=.
 */
#define timerisset(tvp)         ((tvp)->tv_sec || (tvp)->tv_usec)
#define timercmp(tvp, uvp, cmp) \
        ((tvp)->tv_sec cmp (uvp)->tv_sec || \
         (tvp)->tv_sec == (uvp)->tv_sec && (tvp)->tv_usec cmp (uvp)->tv_usec)
#define timerclear(tvp)         (tvp)->tv_sec = (tvp)->tv_usec = 0

#else
typedef unsigned long  u_long;
typedef unsigned int   u_int;
typedef unsigned short u_short;
typedef unsigned char  u_char;
typedef void *         caddr_t;
#endif

#ifdef WIN32        /* for timeval definition */
#include <winsock.h>
#endif

#ifndef bool_t
#define	bool_t	int
#endif

typedef int            enum_t;

#define __dontcare__	-1

#ifndef FALSE
#	define	FALSE	0
#endif

#ifndef TRUE
#	define	TRUE	1
#endif

#ifndef NULL
#	define NULL  0
#endif

#ifdef _NEW_NAMES
#include <rpc\newnames.h>
#endif

void    rpc_syslog(char *, ...);

u_short rpc_ntohs (u_short x);
u_long  rpc_ntohl (u_long x);
u_short rpc_htons (u_short x);
u_long  rpc_htonl (u_long x);

#endif	/* _RPC_TYPES_H */
