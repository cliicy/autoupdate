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
#ifndef _RPC_AUTH_H
#define _RPC_AUTH_H

#define MAX_AUTH_BYTES	400
#define MAXNETNAMELEN	255	/* maximum length of network user's name */

/*
 * Status returned from authentication check
 */
enum auth_stat
{
	AUTH_OK=0,

	/* failed at remote end */

	AUTH_BADCRED=1,			/* bogus credentials (seal broken) */
	AUTH_REJECTEDCRED=2,		/* client should begin new session */
	AUTH_BADVERF=3,			/* bogus verifier (seal broken) */
	AUTH_REJECTEDVERF=4,		/* verifier expired or was replayed */
	AUTH_TOOWEAK=5,			/* rejected due to security reasons */

	/* failed locally */

	AUTH_INVALIDRESP=6,		/* bogus response verifier */
	AUTH_FAILED=7,			/* some unknown reason */

	AUTH__PAD = 0x7FFFFFFF  /* expand the size of this enum to 4 bytes */
};

typedef u_long u_int32;	/* 32-bit unsigned integers */

union des_block
{
	struct
	{
		u_int32 high;
		u_int32 low;
	} key;
	char c[8];
};
typedef union des_block des_block;

extern bool_t xdr_des_block (XDR *, des_block *);

/* Authentication info. Opaque to client. */
struct opaque_auth
{
	enum_t	oa_flavor;		/* flavor of auth */
	caddr_t	oa_base;		/* address of more auth stuff */
	u_int	oa_length;		/* not to exceed MAX_AUTH_BYTES */
};

struct auth_ops
{
	void (*ah_nextverf)(struct _auth*);
	int  (*ah_marshal) (struct _auth*, XDR *);
	int  (*ah_validate)(struct _auth*, struct opaque_auth);
	int  (*ah_refresh) (struct _auth*);
	void (*ah_destroy) (struct _auth*);
};

/* Auth handle, interface to client side authenticators. */
typedef struct _auth
{
	struct opaque_auth ah_cred;
	struct opaque_auth ah_verf;
	union  des_block   ah_key;
	struct auth_ops   *ah_ops;
	caddr_t            ah_private;
} AUTH;

/*
 * Authentication ops.
 * The ops and the auth handle provide the interface to the authenticators.
 *
 * AUTH	*auth;
 * XDR	*xdrs;
 * struct opaque_auth verf;
 */
#define AUTH_NEXTVERF(auth)		\
		((*((auth)->ah_ops->ah_nextverf))(auth))
#define auth_nextverf(auth)		\
		((*((auth)->ah_ops->ah_nextverf))(auth))

#define AUTH_MARSHALL(auth, xdrs)	\
		((*((auth)->ah_ops->ah_marshal))(auth, xdrs))
#define auth_marshall(auth, xdrs)	\
		((*((auth)->ah_ops->ah_marshal))(auth, xdrs))

#define AUTH_VALIDATE(auth, verfp)	\
		((*((auth)->ah_ops->ah_validate))((auth), verfp))
#define auth_validate(auth, verfp)	\
		((*((auth)->ah_ops->ah_validate))((auth), verfp))

#define AUTH_REFRESH(auth)		\
		((*((auth)->ah_ops->ah_refresh))(auth))
#define auth_refresh(auth)		\
		((*((auth)->ah_ops->ah_refresh))(auth))

#define AUTH_DESTROY(auth)		\
		((*((auth)->ah_ops->ah_destroy))(auth))
#define auth_destroy(auth)		\
		((*((auth)->ah_ops->ah_destroy))(auth))

extern const struct opaque_auth _null_auth;

extern bool_t xdr_opaque_auth (XDR *, struct opaque_auth *);

/*
 * These are the various implementations of client side authenticators.
 */

/*
 * System style authentication
 * AUTH *authsys_create(machname, uid, gid, len, aup_gids)
 *	char *machname;
 *	uid_t uid;
 *	gid_t gid;
 *	int len;
 *	gid_t *aup_gids;
 */

extern AUTH *authnone_create (void);		/* takes no parameters */

#define AUTH_NONE	0		/* no authentication */
#define	AUTH_NULL	0		/* backward compatibility */
#define	AUTH_SYS	1		/* unix style (uid, gids) */

#endif /* !_RPC_AUTH_H */
