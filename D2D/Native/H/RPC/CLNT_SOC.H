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
 * clnt.h - Client side remote procedure call interface.
 *
 * Copyright (C) 1984, Sun Microsystems, Inc.
 */

#ifndef _RPC_CLNT_SOC_H
#define _RPC_CLNT_SOC_H

/*
 * All the following declarations are only for backward compatibility
 * with SUNOS 4.0.
 */

#define UDPMSGSIZE	8800	/* rpc imposed limit on udp msg size */

/*
 * callrpc(host, prognum, versnum, procnum, inproc, in, outproc, out)
 *	char *host;
 *	u_long prognum, versnum, procnum;
 *	xdrproc_t inproc, outproc;
 *	char *in, *out;
 */
extern int callrpc (char *, u_long, u_long, u_long, xdrproc_t, char *, xdrproc_t, char *);

/*
 * TCP based rpc
 * CLIENT *
 * clnttcp_create(raddr, prog, vers, fdp, sendsz, recvsz)
 *	struct sockaddr_in *raddr;
 *	u_long prog;
 *	u_long version;
 *	int *fdp;
 *	u_int sendsz;
 *	u_int recvsz;
 */
extern CLIENT *clnttcp_create (struct sockaddr_in *, u_long, u_long, int *, u_int, u_int);


/*
 * TCP based rpc
 * CLIENT *
 * clnttcp_create_ex(hostname, raddr, prog, vers, fdp, sendsz, recvsz)
 * 	char * hostname;
 *  struct sockaddr_in *raddr;
 *	u_long prog;
 *	u_long version;
 *	int *fdp;
 *	u_int sendsz;
 *	u_int recvsz;
 */
extern CLIENT *clnttcp_create_ex (char *, struct sockaddr_in *, u_long, u_long, int *, u_int, u_int);

/*
 * UDP based rpc.
 * CLIENT *
 * clntudp_create(raddr, program, version, wait, fdp)
 *	struct sockaddr_in *raddr;
 *	u_long program;
 *	u_long version;
 *	struct timeval wait;
 *	int *fdp;
 *
 * Same as above, but you specify max packet sizes.
 * CLIENT *
 * clntudp_bufcreate(raddr, program, version, wait, fdp, sendsz, recvsz)
 *	struct sockaddr_in *raddr;
 *	u_long program;
 *	u_long version;
 *	struct timeval wait;
 *	int *fdp;
 *	u_int sendsz;
 *	u_int recvsz;
 *
 */
extern CLIENT *clntudp_create (struct sockaddr_in *, u_long, u_long, struct timeval, int *);
extern CLIENT *clntudp_bufcreate (struct sockaddr_in *, u_long, u_long, struct timeval, int *, u_int, u_int);

/*
 * Memory based rpc (for speed check and testing)
 * CLIENT *
 * clntraw_create(prog, vers)
 *	u_long prog;
 *	u_long vers;
 */
extern CLIENT *clntraw_create (u_long, u_long);

#endif /* _RPC_CLNT_SOC_H */
