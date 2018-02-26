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

/*
 * Usage:
 *	success = rpcb_set(program, version, nconf, address);
 *	success = rpcb_unset(program, version, nconf);
 *	success = rpcb_getaddr(program, version, nconf, address, host);
 * success = rpcb_getaddr_by_uaddr(program, version, nconf, address, uaddr);
 *	head = rpcb_getmaps(nconf, host);
 *	clnt_stat = rpcb_rmtcall(nconf, host, program, version, procedure,
 *		xdrargs, argsp, xdrres, resp, tout, addr_ptr)
 *	success = rpcb_gettime(host, timep)
 *	uaddr = rpcb_taddr2uaddr(nconf, taddr);
 *	taddr = rpcb_uaddr2uaddr(nconf, uaddr);
 */

#ifndef _RPC_RPCB_CLNT_H
#define _RPC_RPCB_CLNT_H

#include <rpc/rpcb_pro.h>
#include <time.h>

extern bool_t rpcb_set (u_long, u_long, struct netconfig *, struct netbuf *);
extern bool_t rpcb_unset (u_long, u_long, struct netconfig *);
extern RPCBLIST *rpcb_getmaps (struct netconfig *, char *);
extern enum clnt_stat 
	rpcb_rmtcall (struct netconfig *, char *, u_long, u_long, u_long, xdrproc_t, 
		caddr_t, xdrproc_t, caddr_t, struct timeval, struct netbuf *);
extern bool_t rpcb_getaddr (u_long, u_long, struct netconfig *, 
						struct netbuf *, char *);
extern bool_t rpcb_getaddr_by_uaddr (u_long, u_long, struct netconfig *, 
						struct netbuf *, char *);
extern bool_t rpcb_gettime (char *, time_t);
extern char *rpcb_taddr2uaddr (struct netconfig *, struct netbuf *);
extern struct netbuf *rpcb_uaddr2taddr (struct netconfig *, char *);

#endif /*!_RPC_RPCB_CLNT_H*/
