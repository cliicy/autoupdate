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
#ifndef _RPC_CLNT_H
#define _RPC_CLNT_H

/*
 * rpc calls return an enum clnt_stat.  This should be looked at more,
 * since each implementation is required to live with this (implementation
 * independent) list of errors.
 */
 
enum clnt_stat
{
	RPC_SUCCESS=0,           /* call succeeded */

	/* local errors */

	RPC_CANTENCODEARGS=1,    /* can't encode arguments */
	RPC_CANTDECODERES=2,     /* can't decode results */
	RPC_CANTSEND=3,          /* failure in sending call */
	RPC_CANTRECV=4,          /* failure in receiving result */
	RPC_TIMEDOUT=5,          /* call timed out */
	RPC_INTR=18,             /* call interrupted */
	RPC_UDERROR=23,          /* recv got uderr indication */

	/* remote errors */

	RPC_VERSMISMATCH=6,      /* rpc versions not compatible */
	RPC_AUTHERROR=7,         /* authentication error */
	RPC_PROGUNAVAIL=8,       /* program not available */
	RPC_PROGVERSMISMATCH=9,  /* program version mismatched */
	RPC_PROCUNAVAIL=10,      /* procedure unavailable */
	RPC_CANTDECODEARGS=11,   /* decode arguments error */
	RPC_SYSTEMERROR=12,      /* generic "other problem" */

	/* rpc_call & clnt_create errors */

	RPC_UNKNOWNHOST=13,      /* unknown host name */
	RPC_UNKNOWNPROTO=17,     /* unknown protocol */
	RPC_UNKNOWNADDR=19,      /* Remote address unknown */
	RPC_NOBROADCAST=21,      /* Broadcasting not supported */

	/* rpcbind errors */

	RPC_RPCBFAILURE=14,       /* the pmapper failed in its call */
	RPC_PROGNOTREGISTERED=15, /* remote program is not registered */
	RPC_N2AXLATEFAILURE=22,   /* Name to address translation failed */

	#define RPC_PMAPFAILURE RPC_RPCBFAILURE

	/* Misc error in the TLI library */
	RPC_TLIERROR=20,

	/* unspecified error */
	RPC_FAILED=16,

	/* asynchronous errors */
	RPC_INPROGRESS=24,
	RPC_STALERACHANDLE=25,

	RPC__PAD = 0x7FFFFFFF  /* expand the size of this enum to 4 bytes */
};


/* Error info. */
struct rpc_err
{
	enum clnt_stat re_status;
	union
	{
		struct
		{
			int _errno;	/* related system error */
			int _t_errno;	/* related tli error number */
		} RE_err;
		enum auth_stat RE_why;	/* why the auth error occurred */
		struct
		{
			u_long low;	/* lowest verion supported */
			u_long high;	/* highest verion supported */
		} RE_vers;
		struct
		{		/* maybe meaningful if RPC_FAILED */
			long s1;
			long s2;
		} RE_lb;		/* life boot & debugging only */
	} ru;

#define	re_errno	ru.RE_err._errno
#define	re_terrno	ru.RE_err._t_errno
#define	re_why		ru.RE_why
#define	re_vers		ru.RE_vers
#define	re_lb		ru.RE_lb
};

/*
 * Client rpc handle.
 * Created by individual implementations
 * Client is responsible for initializing auth, see e.g. auth_none.c.
 */

struct clnt_ops
{
	/* call remote procedure */
	enum clnt_stat(*cl_call)
	(
		struct _clnt*cl, u_long proc,
		xdrproc_t xdr_args, caddr_t args_ptr,
		xdrproc_t xdr_results, caddr_t results_ptr,
		struct timeval timeout
	);

	/* abort a call */
	void (*cl_abort)(struct _clnt*);

	/* get specific error code */
	void (*cl_geterr)(struct _clnt*, struct rpc_err *);

	/* frees results */
	bool_t (*cl_freeres)(struct _clnt*, xdrproc_t, caddr_t);

	/* destroy this structure */
	void (*cl_destroy)(struct _clnt*);

	/* the ioctl() of rpc */
	bool_t (*cl_control)(struct _clnt*, u_int, caddr_t);
};

typedef struct _clnt
{
	AUTH            *cl_auth;       /* authenticator */
	struct clnt_ops *cl_ops;
	caddr_t          cl_private;    /* private stuff */
	char            *cl_netid;      /* network token */
	char            *cl_tp;         /* device name */
} CLIENT;

/*
 * Timers used for the pseudo-transport protocol when using datagrams
 */
struct rpc_timers
{
	u_short  rt_srtt;      /* smoothed round-trip time */
	u_short  rt_deviate;   /* estimated deviation */
	u_long   rt_rtxcur;    /* current (backed-off) rto */
};

/*
 * Feedback values used for possible congestion and rate control
 */
#define	FEEDBACK_REXMIT1	1	/* first retransmit */
#define	FEEDBACK_OK		2	/* no retransmits */

#define	RPCSMALLMSGSIZE	400	/* a more reasonable packet size */

#define	KNC_STRSIZE	128	/* maximum length of knetconfig strings */

struct knetconfig
{
	unsigned long knc_semantics;   /* token name */
	char         *knc_protofmly;   /* protocol family */
	char         *knc_proto;       /* protocol */
	int           knc_rdev;        /* device id */
	unsigned long knc_unused[8];
};

/*
 * client side rpc interface ops
 */

/*
 * enum clnt_stat
 * CLNT_CALL(rh, proc, xargs, argsp, xres, resp, timeout)
 * 	CLIENT *rh;
 *	u_long proc;
 *	xdrproc_t xargs;
 *	caddr_t argsp;
 *	xdrproc_t xres;
 *	caddr_t resp;
 *	struct timeval timeout;
 */
#define	CLNT_CALL(rh, proc, xargs, argsp, xres, resp, secs)	\
	((*(rh)->cl_ops->cl_call)(rh, proc, xargs, argsp, xres, resp, secs))
#define	clnt_call(rh, proc, xargs, argsp, xres, resp, secs)	\
	((*(rh)->cl_ops->cl_call)(rh, proc, xargs, argsp, xres, resp, secs))

/*
 * void
 * CLNT_ABORT(rh);
 * 	CLIENT *rh;
 */
#define	CLNT_ABORT(rh)	((*(rh)->cl_ops->cl_abort)(rh))
#define	clnt_abort(rh)	((*(rh)->cl_ops->cl_abort)(rh))

/*
 * struct rpc_err
 * CLNT_GETERR(rh);
 * 	CLIENT *rh;
 */
#define	CLNT_GETERR(rh, errp)	((*(rh)->cl_ops->cl_geterr)(rh, errp))
#define	clnt_geterr(rh, errp)	((*(rh)->cl_ops->cl_geterr)(rh, errp))

/*
 * bool_t
 * CLNT_FREERES(rh, xres, resp);
 * 	CLIENT *rh;
 *	xdrproc_t xres;
 *	caddr_t resp;
 */
#define	CLNT_FREERES(rh,xres,resp) ((*(rh)->cl_ops->cl_freeres)(rh,xres,resp))
#define	clnt_freeres(rh,xres,resp) ((*(rh)->cl_ops->cl_freeres)(rh,xres,resp))

/*
 * bool_t
 * CLNT_CONTROL(cl, request, info)
 *	CLIENT *cl;
 *	u_int request;
 *	char *info;
 */
#define	CLNT_CONTROL(cl, rq, in) ((*(cl)->cl_ops->cl_control)(cl, rq, in))
#define	clnt_control(cl, rq, in) ((*(cl)->cl_ops->cl_control)(cl, rq, in))


/*
 * control operations that apply to all transports
 */
#define	CLSET_TIMEOUT		1	/* set timeout (timeval) */
#define	CLGET_TIMEOUT		2	/* get timeout (timeval) */
#define	CLGET_SERVER_ADDR	3	/* get server's address (sockaddr) */
#define	CLGET_FD		6	/* get connections file descriptor */
#define	CLGET_SVC_ADDR		7	/* get server's address (netbuf) */
#define	CLSET_FD_CLOSE		8	/* close fd while clnt_destroy */
#define	CLSET_FD_NCLOSE		9	/* Do not close fd while clnt_destroy */
/*
 * Connectionless only control operations
 */
#define	CLSET_RETRY_TIMEOUT 4   /* set retry timeout (timeval) */
#define	CLGET_RETRY_TIMEOUT 5   /* get retry timeout (timeval) */

/*
 * void
 * CLNT_DESTROY(rh);
 * 	CLIENT *rh;
 */
#define	CLNT_DESTROY(rh)	((*(rh)->cl_ops->cl_destroy)(rh))
#define	clnt_destroy(rh)	((*(rh)->cl_ops->cl_destroy)(rh))


/*
 * RPCTEST is a test program which is accessable on every rpc
 * transport/port.  It is used for testing, performance evaluation,
 * and network administration.
 */

#define	RPCTEST_PROGRAM		((u_long)1)
#define	RPCTEST_VERSION		((u_long)1)
#define	RPCTEST_NULL_PROC	((u_long)2)
#define	RPCTEST_NULL_BATCH_PROC	((u_long)3)

/*
 * By convention, procedure 0 takes null arguments and returns them
 */

#define	NULLPROC ((u_long)0)

/*
 * Below are the client handle creation routines for the various
 * implementations of client side rpc.  They can return NULL if a
 * creation failure occurs.
 */

/*
 * Generic client creation routine. Supported protocols are which belong
 * to the nettype name space
 */
extern CLIENT * clnt_create ( char *, u_long, u_long, char * /*hostname, prog, vers, nettype*/);

/*
 * Generic client creation routine. Supported protocols are which belong
 * to the nettype name space.
 */
extern CLIENT * clnt_create_vers (
	char *, u_long, u_long *, u_long, u_long, char * /*host, prog, vers_out, vers_low, vers_high, nettype*/);

/*
 * Generic client creation routine. It takes a netconfig structure
 * instead of nettype
 */
extern CLIENT * clnt_tp_create ( char *, u_long, u_long, struct netconfig * /*hostname, prog, vers, netconf*/);

/*
 * Generic TLI create routine
 */
extern CLIENT * clnt_tli_create ( int, struct netconfig *, struct netbuf *, u_long, u_long, u_int, u_int /*fd, netconf, svcaddr, prog, vers, sendsz, recvsz*/);

/*
 * Low level clnt create routine for connectionful transports, e.g. tcp.
 */
extern CLIENT * clnt_vc_create ( int, struct netbuf *, u_long, u_long, u_int, u_int /*fd, svcaddr, prog, vers, sendsz, recvsz*/);

/*
 * Generic client creation routine. It takes a netbuf address structure
 * instead of a hostname
 */
extern CLIENT * clnt_netbuf_create ( struct netbuf *, char *, u_long, u_long /*hostaddr, netid, prog, vers*/);

/*
 * Low level clnt create routine for connectionless transports, e.g. udp.
 */
extern CLIENT * clnt_dg_create ( int, struct netbuf *, u_long, u_long, u_int, u_int /*fd, svcaddr, program, version, sendsz, recvsz*/);

/*
 * Memory based rpc (for speed check and testing)
 * CLIENT *
 * clnt_raw_create(prog, vers)
 *	u_long prog;			-- program number
 *	u_long vers;			-- version number
 */
extern CLIENT *clnt_raw_create ( u_long, u_long);

/*
 * Print why creation failed
 */
void clnt_pcreateerror (char *);

/*
 * Like clnt_perror(), but is more verbose in its output
 */
void clnt_perrno (enum clnt_stat);

/*
 * Print an error message, given the client error code
 */
void clnt_perror (CLIENT *, char * ); 
char *clnt_sperror (CLIENT *, char * );

/*
 * If a creation fails, the following allows the user to figure out why.
 */
struct rpc_createerr
{
	enum clnt_stat cf_stat;
	struct rpc_err cf_error; /* useful when cf_stat == RPC_PMAPFAILURE */
};

extern struct rpc_createerr *_get_rpc_createerr_(void);

/*
 * The simplified interface:
 * enum clnt_stat
 * rpc_call(host, prognum, versnum, procnum, inproc, in, outproc, out, nettype)
 *	char *host;
 *	u_long prognum, versnum, procnum;
 *	xdrproc_t inproc, outproc;
 *	char *in, *out;
 *	char *nettype;
 */
extern enum clnt_stat rpc_call (char *, u_long, u_long, u_long, xdrproc_t, char *, xdrproc_t, char *, char *);

/*
 * RPC broadcast interface
 * The call is broadcasted to all locally connected nets.
 *
 * extern enum clnt_stat
 * rpc_broadcast(prog, vers, proc, xargs, argsp, xresults, resultsp,
 *			eachresult, nettype)
 *	u_long		prog;		-- program number
 *	u_long		vers;		-- version number
 *	u_long		proc;		-- procedure number
 *	xdrproc_t	xargs;		-- xdr routine for args
 *	caddr_t		argsp;		-- pointer to args
 *	xdrproc_t	xresults;	-- xdr routine for results
 *	caddr_t		resultsp;	-- pointer to results
 *	resultproc_t	eachresult;	-- call with each result obtained
 *	char		*nettype;	-- Transport type
 *
 * For each valid response received, the procedure eachresult is called.
 * Its form is:
 *		done = eachresult(resp, raddr, netconf)
 *			bool_t done;
 *			caddr_t resp;
 *			struct netbuf *raddr;
 *			struct netconfig *netconf;
 * where resp points to the results of the call and raddr is the
 * address if the responder to the broadcast.  netconf is the transport
 * on which the response was received.
 */
typedef bool_t (*resultproc_t)(caddr_t resp, struct netbuf *raddr,
	struct netconfig *netconf);
extern enum clnt_stat rpc_broadcast (u_long, u_long, u_long, xdrproc_t, caddr_t, xdrproc_t, caddr_t, resultproc_t, char *);


/*
 * Copy error message to buffer.
 */
char *clnt_sperrno (enum clnt_stat /* enum clnt_stat num */);	/* string */

#ifdef PORTMAP
/* For backword compatibility */
#include <rpc/clnt_soc.h>
#endif

#endif /* !_RPC_CLNT_H */
