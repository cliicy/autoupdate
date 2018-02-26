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
 * svc.h, Server-side remote procedure call interface.
 *
 * Copyright (C) 1984, Sun Microsystems, Inc.
 */

#ifndef _RPC_SVC_SOC_H
#define _RPC_SVC_SOC_H

/*
 * All the following declarations are only for backward compatibility
 * with SUNOS 4.0.
 */

/*
 *  Approved way of getting address of caller
 */
#define svc_getcaller(x) ((struct sockaddr_in *)(x)->xp_rtaddr.buf)

/*
 * Service registration
 *
 * svc_register(xprt, prog, vers, dispatch, protocol)
 *	SVCXPRT *xprt;
 *	u_long prog;
 *	u_long vers;
 *	void (*dispatch)();
 *	int protocol;  // like TCP or UDP, zero means do not register
 */
extern bool_t svc_register (SVCXPRT *, u_long, u_long, void (*)(struct svc_req *, SVCXPRT *), int);

/*
 * Service un-registration
 *
 * svc_unregister(prog, vers)
 *	u_long prog;
 *	u_long vers;
 */
extern void svc_unregister (u_long, u_long);

/*
 * Memory based rpc for testing and timing.
 */
extern SVCXPRT *svcraw_create (void);

/*
 * Udp based rpc. For compatibility reasons
 */
extern SVCXPRT *svcudp_create (int);
extern SVCXPRT *svcudp_bufcreate (int, u_int, u_int);

/*
 * Tcp based rpc.
 */
extern SVCXPRT *svctcp_create (int, u_int, u_int);
extern SVCXPRT *svcfd_create (int, u_int, u_int);

#endif /* !_RPC_SVC_SOC_H */
