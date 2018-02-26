/****************************************************************************
 *
 * (C) Unpublished Copyright of Novell, Inc. All Rights Reserved.
 *
 * No part of this file may be duplicated, revised, translated, localized or
 * modified in any manner or compiled, linked or uploaded or downloaded to or
 * from any computer system without the prior written consent of Novell, Inc
 *
 ***************************************************************************/


#ifndef _TISPXIPX_
#ifdef __cplusplus
extern "C" {
#endif
#define _TISPXIPX_

typedef struct ipxaddr_s {
	unsigned char	ipxa_net[4];
	unsigned char	ipxa_node[6];
	unsigned char	ipxa_socket[2];
} IPX_ADDR;

typedef	struct ipxopt_s {
	unsigned char	ipx_type;		/* type field for ipx header */
	unsigned char	ipx_pad1[3];	/* pad structure to 4 bytes, set to 0's */
	unsigned char	ipx_hops;		/* Transport Control (hop count) */
	unsigned char	ipx_pad2[3];	/* pad structure to 8 bytes, set to 0's */
} IPX_OPTS;

/* SPX compatibility option structures, used if t_open("nspx",...) */
typedef struct spxopt_s {
	unsigned char	spx_connectionID[2];
	unsigned char	spx_allocationNumber[2];
	unsigned char	spx_pad1[4];	/* pad structure to 8 bytes, set to 0's */
} SPX_OPTS;

typedef struct spx_optmgmt {
	unsigned char	spxo_retry_count;
	unsigned char	spxo_watchdog_flag;
	unsigned long	spxo_min_retry_delay;
	unsigned char	spxo_pad2[2];	/* pad structure to 8 bytes, set to 0's */
} SPX_OPTMGMT;


/* SPX II option structure, used if t_open("nspx2",...) */
/*
 * New SPX II structure is used for all three calls t_listen, t_connect, 
 * t_optmgmt.  This structure is extensible in future versions of SPX II
 * and therefore a variable should never be declared directly (ie. 
 * struct spx2_options localOptions; should not be done) and the size of
 * the structure should never be taken (ie. sizeof(SPX2_OPTIONS).  The
 * function t_alloc should always be used to allocate the structure and
 * the options field of the t_info structure should be used after calling 
 * t_getinfo to determine the size of this structure.  Otherwise TBUFOVFLW
 * errors could be generated when an application is run with a newer SPX II
 * protocol stack.
 */
  
#define	OPTIONS_VERSION		1

typedef struct spx2_options	{
	unsigned long  		versionNumber;				//1 - must be set to OPTIONS_VERSION	
	unsigned long  		spxIIOptionNegotiate;		//2
	unsigned long		spxIIRetryCount;			//3
	unsigned long		spxIIMinimumRetryDelay;		//4
	unsigned long		spxIIMaximumRetryDelta;		//5
	unsigned long		spxIIWatchdogTimeout;		//6
	unsigned long		spxIIConnectTimeout;		//7
	unsigned long		spxIILocalWindowSize;		//8
	unsigned long		spxIIRemoteWindowSize;		//9
	unsigned long		spxIIConnectionID;			//10
	unsigned long		spxIIInboundPacketSize;		//11
	unsigned long		spxIIOutboundPacketSize;	//12
	unsigned long		spxIISessionFlags;			//13 - (version 1 ends)
} SPX2_OPTIONS;

/* option version history */
#define	VER1_OPTIONS_SIZE	(13 * sizeof (long))


/* SPX and SPX II Option Management values */

#define SPX_WATCHDOG_OFF				0
#define SPX_WATCHDOG_ON					! SPX_WATCHDOG_OFF
#define SPX_WATCHDOG_DEFAULT			SPX_WATCHDOG_ON
/* SPX options only above here */ 
#define SPX_RETRY_MIN					3
#define SPX_RETRY_MAX					50
#define SPX_RETRY_DEFAULT				10
/* SPX II options only below here */
#define SPX_WATCHDOG_TIMEOUT_MIN		3000			//msec == 3 seconds
#define SPX_WATCHDOG_TIMEOUT_MAX	    300000			//msec == 5 minutes
#define SPX_WATCHDOG_TIMEOUT_DEFAULT    60000			//msec == 1 minute
#define SPX_MIN_RETRY_DELAY_MIN			1				//msec
#define SPX_MIN_RETRY_DELAY_MAX			60000			//msec == 1 minute
#define SPX_MIN_RETRY_DELAY_DEFAULT		0				//Protocol stack decision
#define	SPX_MAX_RETRY_DELTA_MIN			1000			//msec == 1 second
#define	SPX_MAX_RETRY_DELTA_MAX			60000			//msec == 1 minute
#define	SPX_MAX_RETRY_DELTA_DEFAULT		5000	 		//msec == 5 seconds
#define SPX_OPTION_NEGOTIATE_OFF 		0
#define SPX_OPTION_NEGOTIATE_ON	  		(! SPX_OPTION_NEGOTIATE_OFF)
#define SPX_OPTION_NEGOTIATE_DEFAULT	SPX_OPTION_NEGOTIATE_ON
#define	SPX_CONNECT_TIMEOUT_MIN			1000			//msec == 1 second
#define	SPX_CONNECT_TIMEOUT_MAX			120000			//msec == 2 minutes
#define	SPX_CONNECT_TIMEOUT_DEFAULT		0				//Protocol stack decision							
#define	SPX_LOCAL_WINDOW_SIZE_MIN		1
#define	SPX_LOCAL_WINDOW_SIZE_MAX		16
#define	SPX_LOCAL_WINDOW_SIZE_DEFAULT	0				//Protocol stack decision
#define	SPX_SF_NONE						0				//session flags
#define	SPX_SF_DEFAULT					SPX_SF_NONE		//session flags
#define	SPX_SF_IPX_CHECKSUM				0x01			//session flags - do checksum
#define SPX_SF_SPX2_SESSION				0x02			//session flags - R/O

/* Misc. byte aligned data access macros */
#ifndef		BE16_TO_U16
#define	BE16_TO_U16(a)	((((u16)((u8 *)a)[0] << (u16)8) | ((u16)((u8 *)a)[1] & 0xFF)) & (u16)0xFFFF)
#define	BE32_TO_U32(a)	((((u32)((u8 *)a)[0] & 0xFF) << (u32)24) | (((u32)((u8 *)a)[1] & 0xFF) << (u32)16) | (((u32)((u8 *)a)[2] & 0xFF) << (u32)8)  | ((u32)((u8 *)a)[3] & 0xFF))
#define	U16_TO_BE16(u,a) ((((u8 *)a)[0] = (u8)((u) >> 8)), (((u8 *)a)[1] = (u8)(u)))
#define	U32_TO_BE32(u,a) ((((u8 *)a)[0] = (u8)((u) >> 24)), (((u8 *)a)[1] = (u8)((u) >> 16)), (((u8 *)a)[2] = (u8)((u) >> 8)),(((u8 *)a)[3] = (u8)(u)))
#endif

/* T_DISCONNECT reason codes for SPX */
#define TLI_SPX_CONNECTION_FAILED		0xed
#define TLI_SPX_CONNECTION_TERMINATED	0xec	/* T_DISCONNECT from remote */
#define TLI_SPX_MALFORMED_PACKET		0xfe
#define TLI_SPX_PACKET_OVERFLOW			0xfd	/* This is the preferred one */
#define TLI_SPX_UNREACHABLE_DEST		0x70	/* route not found */

/* t_rcvuderr uderr.error codes for IPX */
#define TLI_IPX_MALFORMED_ADDRESS		0xfe
#define TLI_IPX_PACKET_OVERFLOW			0xfd

#ifdef __cplusplus
};
#endif
#endif


