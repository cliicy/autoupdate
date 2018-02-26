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
#ifndef _RPC_XDR_H
#define _RPC_XDR_H

#include <rpc/types.h>

enum xdr_op
{
	XDR_ENCODE = 0,
	XDR_DECODE = 1,
	XDR_FREE   = 2
};

/* This is the number of bytes per unit of external data. */
#define BYTES_PER_XDR_UNIT	(4)
#define RNDUP(x)  ((((x) + BYTES_PER_XDR_UNIT - 1) / BYTES_PER_XDR_UNIT) \
		    * BYTES_PER_XDR_UNIT)

/*
 * A xdrproc_t exists for each data type which is to be encoded or decoded.
 *
 * The second argument to the xdrproc_t is a pointer to an opaque pointer.
 * The opaque pointer generally points to a structure of the data type
 * to be decoded.  If this pointer is 0, then the type routines should
 * allocate dynamic storage of the appropriate size and return it.
 */
 
typedef	bool_t (*xdrproc_t)(struct XDR *, caddr_t *, ...);

typedef struct
{
	bool_t (*x_getlong) (struct XDR *, long *);
	bool_t (*x_putlong) (struct XDR *, long *);
	bool_t (*x_getbytes)(struct XDR *, char *, u_int);
	bool_t (*x_putbytes)(struct XDR *, char *, u_int);
	u_int  (*x_getpostn)(struct XDR *);
	bool_t (*x_setpostn)(struct XDR *, u_int);
	long * (*x_inline)  (struct XDR *, u_int);
	void   (*x_destroy) (struct XDR *);
} xdr_ops;


/* The XDR handle. */
typedef struct XDR
{
	enum xdr_op x_op;
	xdr_ops *   x_ops;
	caddr_t     x_public;
	caddr_t     x_private;
	caddr_t     x_base;
	int         x_handy;
} XDR;

/*
 * Operations defined on a XDR handle
 *
 * XDR		*xdrs;
 * long		*longp;
 * caddr_t	 addr;
 * u_int	 len;
 * u_int	 pos;
 */
#define XDR_GETLONG(xdrs, longp)			\
	(*(xdrs)->x_ops->x_getlong)(xdrs, longp)
#define xdr_getlong(xdrs, longp)			\
	(*(xdrs)->x_ops->x_getlong)(xdrs, longp)

#define XDR_PUTLONG(xdrs, longp)			\
	(*(xdrs)->x_ops->x_putlong)(xdrs, longp)
#define xdr_putlong(xdrs, longp)			\
	(*(xdrs)->x_ops->x_putlong)(xdrs, longp)

#define XDR_GETBYTES(xdrs, addr, len)			\
	(*(xdrs)->x_ops->x_getbytes)(xdrs, addr, len)
#define xdr_getbytes(xdrs, addr, len)			\
	(*(xdrs)->x_ops->x_getbytes)(xdrs, addr, len)

#define XDR_PUTBYTES(xdrs, addr, len)			\
	(*(xdrs)->x_ops->x_putbytes)(xdrs, addr, len)
#define xdr_putbytes(xdrs, addr, len)			\
	(*(xdrs)->x_ops->x_putbytes)(xdrs, addr, len)

#define XDR_GETPOS(xdrs)				\
	(*(xdrs)->x_ops->x_getpostn)(xdrs)
#define xdr_getpos(xdrs)				\
	(*(xdrs)->x_ops->x_getpostn)(xdrs)

#define XDR_SETPOS(xdrs, pos)				\
	(*(xdrs)->x_ops->x_setpostn)(xdrs, pos)
#define xdr_setpos(xdrs, pos)				\
	(*(xdrs)->x_ops->x_setpostn)(xdrs, pos)

#define	XDR_INLINE(xdrs, len)				\
	(*(xdrs)->x_ops->x_inline)(xdrs, len)
#define	xdr_inline(xdrs, len)				\
	(*(xdrs)->x_ops->x_inline)(xdrs, len)

#define	XDR_DESTROY(xdrs)				\
	(*(xdrs)->x_ops->x_destroy)(xdrs)
#define	xdr_destroy(xdrs) XDR_DESTROY(xdrs)

/*
 * Support struct for discriminated unions.
 * You create an array of xdrdiscrim structures, terminated with
 * a entry with a null procedure pointer.  The xdr_union routine gets
 * the discriminant value and then searches the array of structures
 * for a matching value.  If a match is found the associated xdr routine
 * is called to handle that part of the union.  If there is
 * no match, then a default routine may be called.
 * If there is no match and no default routine it is an error.
 */
 
#define NULL_xdrproc_t ((xdrproc_t)0)

/*
 * In-line routines for fast encode/decode of primitve data types.
 * Caveat emptor: these use single memory cycles to get the
 * data from the underlying buffer, and will fail to operate
 * properly if the data is not aligned.  The standard way to use these
 * is to say:
 *	if ((buf = XDR_INLINE(xdrs, count)) == NULL)
 *		return (FALSE);
 *	<<< macro calls >>>
 * where ``count'' is the number of bytes of data occupied
 * by the primitive data types.
 *
 * N.B. and frozen for all time: each data type here uses 4 bytes
 * of external representation.
 */

#define IXDR_GET_LONG(buf)		((long)rpc_ntohl((u_long)*(buf)++))
#define IXDR_PUT_LONG(buf, v)	(*(buf)++ = (long)rpc_htonl((u_long)v))

#define IXDR_GET_BOOL(buf)		((bool_t)IXDR_GET_LONG(buf))
#define IXDR_GET_ENUM(buf, t)	((t)IXDR_GET_LONG(buf))
#define IXDR_GET_U_LONG(buf)	((u_long)IXDR_GET_LONG(buf))
#define IXDR_GET_SHORT(buf)	((short)IXDR_GET_LONG(buf))
#define IXDR_GET_U_SHORT(buf)	((u_short)IXDR_GET_LONG(buf))

#define IXDR_PUT_BOOL(buf, v)		IXDR_PUT_LONG((buf), ((long)(v)))
#define IXDR_PUT_ENUM(buf, v)		IXDR_PUT_LONG((buf), ((long)(v)))
#define IXDR_PUT_U_LONG(buf, v)	IXDR_PUT_LONG((buf), ((long)(v)))
#define IXDR_PUT_SHORT(buf, v)	IXDR_PUT_LONG((buf), ((long)(v)))
#define IXDR_PUT_U_SHORT(buf, v)	IXDR_PUT_LONG((buf), ((long)(v)))

struct xdr_discrim
{
	int	value;
	xdrproc_t proc;
};

extern bool_t xdr_void      (XDR *, caddr_t);
extern bool_t xdr_int       (XDR *, int *);
extern bool_t xdr_u_int     (XDR *, u_int *);
extern bool_t xdr_long      (XDR *, long *);
extern bool_t xdr_u_long    (XDR *, u_long *);
extern bool_t xdr_short     (XDR *, short *);
extern bool_t xdr_u_short   (XDR *, u_short *);
extern bool_t xdr_bool      (XDR *, bool_t *);
extern bool_t xdr_enum      (XDR *, enum_t *);
extern bool_t xdr_array     (XDR *, caddr_t *, u_int *, u_int, u_int, xdrproc_t);
extern bool_t xdr_bytes     (XDR *, char **, u_int *, u_int);
extern bool_t xdr_opaque    (XDR *, caddr_t, u_int);
extern bool_t xdr_string    (XDR *, char **, u_int);
extern bool_t xdr_union     (XDR *, enum_t *, caddr_t,
                                      struct xdr_discrim *, xdrproc_t);
extern bool_t xdr_char      (XDR *, char *);
extern bool_t xdr_u_char    (XDR *, char *);
extern bool_t xdr_vector    (XDR *, char *, u_int, u_int, xdrproc_t);
extern bool_t xdr_float     (XDR *, float *);
extern bool_t xdr_double    (XDR *, double *);
extern bool_t xdr_reference (XDR *, caddr_t *, u_int, xdrproc_t);
extern bool_t xdr_pointer   (XDR *, char **, u_int, xdrproc_t);
extern bool_t xdr_wrapstring (XDR *, char **);

extern void	xdr_free (xdrproc_t, caddr_t);

/*
 * Common opaque bytes objects used by many rpc protocols;
 * declared here due to commonality.
 */
#define MAX_NETOBJ_SZ 1024
struct netobj
{
	u_int	n_len;
	char	*n_bytes;
};
typedef struct netobj netobj;
extern bool_t   xdr_netobj (XDR *, struct netobj *);

/*
 * These are the public routines for the various implementations of
 * xdr streams.
 */
 
/* XDR using memory buffers */
extern void xdrmem_create (XDR *, caddr_t, u_int, enum xdr_op);
//#define SEC_ALERT_2 

//Above fix was for issue:12970047.Disable this fix for now.
//If this fix is enabled, also enable it in clnt_vc.c, xdr_rec.c,tsim.c, svc_vc.c & h\rpc\xdr.h files.
//Due to a short timeout, where we expect the entire result to be returned within that time.
//we may get login failure or failed to retrieve activity log. On heavy CPU usage machines, it may happen. There is no
//definite way to observe/repeat such problems. The reliability of the product gets affected. Hence we disable the fix ...

/* XDR pseudo records for tcp */
#ifdef SEC_ALERT_2
extern bool_t xdrrec_create (XDR *, u_int, u_int, caddr_t, 
                  int (*)(caddr_t, caddr_t, int,struct timeval *), 
                  int (*)(caddr_t, caddr_t, int) );
#else
extern bool_t xdrrec_create (XDR *, u_int, u_int, caddr_t, 
                  int (*)(caddr_t, caddr_t, int), 
                  int (*)(caddr_t, caddr_t, int) );
#endif

extern bool_t xdrrec_endofrecord (XDR *, bool_t);	/* make end of xdr record */
extern bool_t xdrrec_skiprecord (XDR *);	/* move to beginning of next record */
extern bool_t xdrrec_eof (XDR *);		/* true if no more input */
extern void  xdrrec_resetinput(XDR *);

#endif /* ! _RPC_XDR_H */
