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
 * rpcb_prot.h
 * Protocol for the local rpcbinder service
 *
 * Copyright (C) 1988, Sun Microsystems, Inc.
 */

/*
 * The following procedures are supported by the protocol:
 *
 * RPCBPROC_NULL() returns ()
 * 	takes nothing, returns nothing
 *
 * RPCBPROC_SET(RPCB) returns (bool_t)
 * 	TRUE is success, FALSE is failure.  Registers the tuple
 *	[prog, vers, netid] with address
 *
 * RPCBPROC_UNSET(RPCB) returns (bool_t)
 *	TRUE is success, FALSE is failure.  Un-registers tuple
 *	[prog, vers, netid].  address is ignored.
 *
 * RPCBPROC_GETADDR(RPCB) returns (Universal address).
 *	0 is failure.  Otherwise returns the universal address where the pair
 *	[prog, vers, netid] is registered.
 *
 * RPCBPROC_DUMP() RETURNS (RPCBLIST *)
 *	used for dumping the entire rpcbind maps
 *
 * RPCBPROC_CALLIT(unsigned, unsigned, unsigned, string<>)
 * 	RETURNS (address, string<>);
 * usage: encapsulatedresults = RPCBPROC_CALLIT(prog, vers, proc, encapsulatedargs);
 * 	Calls the procedure on the local machine.  If it is not registered,
 *	this procedure is quiet; i.e. it does not return error information!!!
 *	This routine only passes null authentication parameters.
 *	This file has no interface to xdr routines for RPCBPROC_CALLIT.
 *
 * RPCBPROC_GETTIME() returns (bool_t).
 *	TRUE is success, FALSE is failure.  Gets the remote machines time
 *
 */

#ifndef _RPC_RPCB_PROT_H
#define _RPC_RPCB_PROT_H

#define RPCBPROG		((u_long)100000)
#define RPCBVERS		((u_long)3)

/*
 * All the defined procedures on it
 */
#define RPCBPROC_NULL		((u_long)0)
#define RPCBPROC_SET		((u_long)1)
#define RPCBPROC_UNSET		((u_long)2)
#define RPCBPROC_GETADDR	((u_long)3)
#define RPCBPROC_DUMP		((u_long)4)
#define RPCBPROC_CALLIT		((u_long)5)
#define RPCBPROC_GETTIME	((u_long)6)
#define RPCBPROC_UADDR2TADDR	((u_long)7)
#define RPCBPROC_TADDR2UADDR	((u_long)8)

/*
 * All rpcbind stuff (vers 3)
 */

/*
 * A mapping of (program, version, network ID) to address
 */
struct rpcb
{
	u_long r_prog;			/* program number */
	u_long r_vers;			/* version number */
	char *r_netid;			/* network id */
	char *r_addr;			/* universal address */
	char *r_owner;			/* owner of the mapping */
};

typedef struct rpcb RPCB;

extern bool_t xdr_rpcb();
extern bool_t xdr_netbuf();

/*
 * A list of mappings
 */
struct rpcblist
{
	RPCB rpcb_map;
	struct rpcblist *rpcb_next;
};
typedef struct rpcblist RPCBLIST;

extern bool_t xdr_rpcblist();

/*
 * Remote calls arguments
 */
struct rpcb_rmtcallargs
{
	u_long prog;			/* program number */
	u_long vers;			/* version number */
	u_long proc;			/* procedure number */
	u_long arglen;			/* arg len */
	caddr_t args_ptr;		/* argument */
	xdrproc_t xdr_args;		/* XDR routine for argument */
};

extern bool_t xdr_rpcb_rmtcallargs();

/*
 * Remote calls results
 */
struct rpcb_rmtcallres
{
	char *addr_ptr;			/* remote universal address */
	u_long resultslen;		/* results length */
	caddr_t results_ptr;		/* results */
	xdrproc_t xdr_results;		/* XDR routine for result */
};

extern bool_t xdr_rpcb_rmtcallres();

#endif /*!_RPC_RPCB_PROT_H*/
