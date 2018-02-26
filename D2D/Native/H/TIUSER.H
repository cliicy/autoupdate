/** Copyright (c) 1989  Mentat Inc.
 ** tiuser.h 1.5, last change 3/28/90
 **/

/****************************************************************************
 *                                                                          *
 *             Copyright 1991  Novell, Inc.  All Rights Reserved.           *
 *                                                                          *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND        *
 * TREATIES.  NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED,       *
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, *
 * EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED, OR ADAPTED   *
 * WITHOUT THE PRIOR WRITTEN CONSENT OF NOVELL.  ANY USE OR EXPLOITATION    *
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO      *
 * CRIMINAL AND CIVIL LIABILITY.                                            *
 *                                                                          *
 ****************************************************************************/

#ifndef _TIUSER_
#ifdef __cplusplus
extern "C" {
#endif
#define _TIUSER_

#define	O_NONBLOCK	O_NDELAY

#ifndef EAGAIN
#define EAGAIN  -1
#endif



/* Error values */
#define	TACCES			1
#define	TBADADDR		2
#define	TBADDATA		3
#define	TBADF			4
#define	TBADFLAG		5
#define	TBADOPT			6
#define	TBADSEQ			7
#define	TBUFOVFLW		8
#define	TFLOW			9
#define	TLOOK			10
#define	TNOADDR			11
#define	TNODATA			12
#define	TNOREL			13
#define	TNOTSUPPORT		14
#define	TOUTSTATE		15
#define	TSTATECHNG		16
#define TSYSERR			17
#define	TNOUDERR		18
#define	TNODIS			19
#define	TNOSTRUCTYPE	20
#define TBADNAME		21
#define	TBADQLEN		22
#define	TADDRBUSY		23

/* t_look events */
#define	T_LISTEN		1
#define	T_CONNECT		2
#define	T_DATA			3
#define	T_EXDATA		4
#define	T_DISCONNECT	5
#define	T_ORDREL		6
#define	T_ERROR			7
#define	T_UDERR			8
#define	T_GODATA		9
#define	T_GOEXDATA		10
#define	T_EVENTS		11

/* Flag definitions */
#define	T_EXPEDITED		0x01
#define	T_MORE			0x02
#define	T_NEGOTIATE		0x04
#define	T_CHECK			0x08
#define	T_DEFAULT		0x10
#define T_SUCCESS		0x20
#define	T_FAILURE		0x40

#define t_errno		(*__get_t_errno_ptr())
extern int t_errno;

struct t_info {
	long	addr;
	long	options;
	long	tsdu;
	long	etsdu;
	long	connect;
	long	discon;
	long	servtype;
};

/* Service types */
#define T_COTS		1	/* Connection-mode service */
#define	T_COTS_ORD	2	/* Connection service with orderly release */
#define	T_CLTS		3	/* Connectionless-mode service */

struct netbuf {
	unsigned	maxlen;
	unsigned	len;
	char		* buf;
};

struct t_bind {
	struct netbuf	addr;
	unsigned	qlen;
};

struct t_optmgmt {
	struct netbuf	opt;
	long	flags;
};

struct t_discon {
	struct netbuf	udata;
	int		reason;
	int		sequence;
};

struct t_call {
	struct netbuf	addr;
	struct netbuf	opt;
	struct netbuf	udata;
	int		sequence;
};

struct t_unitdata {
	struct netbuf	addr;
	struct netbuf	opt;
	struct netbuf	udata;
};

struct t_uderr {
	struct netbuf	addr;
	struct netbuf	opt;
	long		error;
};

/* t_alloc structure types */
#define	T_BIND		0x1
#define	T_CALL			0x2
#define T_OPTMGMT		0x4
#define	T_DIS			0x8
#define	T_UNITDATA		0x10
#define	T_UDERROR		0x20
#define	T_INFO			0x40

/* XTI names for t_alloc structure types */
#define	T_BIND_STR		T_BIND
#define T_OPTMGMT_STR	T_OPTMGMT
#define	T_CALL_STR		T_CALL
#define	T_DIS_STR		T_DIS
#define	T_UNITDATA_STR	T_UNITDATA
#define	T_UDERROR_STR	T_UDERROR
#define	T_INFO_STR		T_INFO

/* t_alloc field identifiers */
#define	T_ADDR			0x1000
#define	T_OPT			0x2000
#define	T_UDATA			0x4000
#define	T_ALL			0x8000

/* State values */
#define	T_UNINIT	0	/* added to match xti state tables */
#define	T_UNBND		1	/* unbound */
#define	T_IDLE		2	/* idle */
#define	T_OUTCON	3	/* outgoing connection pending */
#define	T_INCON		4	/* incoming connection pending */
#define	T_DATAXFER	5	/* data transfer */
#define	T_OUTREL	6	/* outgoing orderly release */
#define	T_INREL		7	/* incoming orderly release */

/* general purpose defines */
#define	T_YES		1
#define	T_NO		0
#define	T_UNUSED	-1
#define	T_NULL		0
#define	T_ABSREQ	0x8000

/* t_event flag values */
#define	T_SAME		0x0
#define	T_ENABLE	0x1
#define T_DISABLE	0x2


extern	char	* t_errlist[];
extern	int	t_nerr;

/*---------------------TCP specific Options--------------------------*/
	

/* TCP Precedence Levels */
#define	T_ROUTINE	0
#define	T_PRIORITY	1
#define	T_IMMEDIATE	2
#define	T_FLASH		3
#define	T_OVERRIDEFLASH	4
#define	T_CRITIC_ECP	5
#define	T_INETCONTROL	6
#define	T_NETCONTROL	7

/* TCP security options structure */
struct secoptions {
	short	security;
	short	compartment;
	short	handling;	/* handling restrictions */
	long	tcc;		/* transmission control code */
};

/* TCP options */
struct tcp_options {
	short		precedence;
	long		timeout;	/* abort timeout */
	long		max_seg_size;
	struct secoptions secopt;	/* security options */
};





#ifndef	NO_PROTOTYPES

extern	int	 t_accept (int fd, int resfd, struct t_call * call);
extern	char *t_alloc (int fd, int struct_type, int fields);
extern	int	 t_bind (int fd, struct t_bind * req, struct t_bind  * ret);
extern	int	 t_blocking(int fd);
extern	int	 t_close (int fd);
extern	int	 t_connect (int fd, struct t_call  * sndcall, struct t_call  * rcvcall);
extern	void t_error (char  * errmsg);
extern	int	 t_event(int fd, int (*func)(int fd, int event, void *parm), 
					int flag, void *parm);
extern	int	 t_free (char  * ptr, int struct_type);
extern	int	 t_getinfo (int fd, struct t_info  * info);
extern	int	 t_getstate (int fd);
extern	int	 t_listen (int fd, struct t_call  * call);
extern	int	 t_look (int fd);
extern	int	 t_nonblocking(int fd);
extern	int	 t_open (char  * path, int	oflag, struct t_info  * info);
extern	int	 t_optmgmt (int fd, struct t_optmgmt  * req, struct t_optmgmt  * ret);
extern	int	 t_rcv (int fd, char  * buf, unsigned nbytes, int  * flags);
extern	int	 t_rcvconnect (int fd, struct t_call  * call);
extern	int	 t_rcvdis (int fd, struct t_discon  * discon);
extern	int	 t_rcvrel (int fd);
extern	int	 t_rcvudata (int fd, struct t_unitdata  * unitdata, int  * flags);
extern	int	 t_rcvuderr (int fd, struct t_uderr  * uderr);
extern	int	 t_snd (int fd, char  * buf, unsigned nbytes, int flags);
extern	int	 t_snddis (int fd, struct t_call  * call);
extern	int	 t_sndrel (int fd);
extern	int	 t_sndudata (int fd, struct t_unitdata  * unitdata);
extern	int	 t_sync(int fd);
extern	int	 t_unbind(int fd);

#endif	/* ifndef no_prototypes */

#ifndef _TISPXIPX_
#include 	<tispxipx.h>
#endif

#ifdef __cplusplus
};
#endif
#endif	/* ifdef _TIUSER_ */

