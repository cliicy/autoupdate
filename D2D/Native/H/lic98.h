/* ---------------------------------------------------------------------- */
/*               Proprietary and Confidential Information                 */
/*                                                                        */
/* Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
All rights reserved.  Any third party trademarks or copyrights are the
property of their respective owners.                                      */
/*                                                                        */
/* ---------------------------------------------------------------------- */
/*  SCCS Module Control Stamp                                             */
/* ---------------------------------------------------------------------- */

/* "%W% %Y% %G% %U%" */
/* "%Q%" */

/* license.h - interface to ca licensing (lic) */
#ifndef NEWLICENSE_H
#define NEWLICENSE_H


#ifdef __cplusplus
extern "C"{
#endif
#ifndef WIN32
#ifndef __cdecl
#define __cdecl
#endif
#endif

#include <time.h>

#define LIC_CURRENT_VERSION  3		/* Current internal version */ 
#define LIC_MAGIC_COMPCODE   "1VRR" /* A license check on this returns LIC_CURRENT_VERSION */ 

#ifdef WIN32
#include <TCHAR.H>
#endif

#ifndef WIN32
typedef char _TCHAR;
#endif

typedef time_t	LIC_TIME;
typedef char *LIC_STRING;

/* 
   User identification structure returned on ca_*_fb calls 
   Each of these fields is optional, depending on the type of client 
*/
 
typedef struct 
{
 char id_user[256];   	/* User information      */
 char id_org[256];      /* Organization */ 
 char id_site[256];     /* Site ID */ 
 char id_ident[256];    /* Extra identity information, if any */ 
} LIC_IDENT; 

/* return codes for MLA check function: lic_get_siteid */
#define		SUCCESS						0
#define		CAN_NOT_OPEN_LICENSE_FILE	1
#define		CAN_NOT_OPEN_REGISTRY		2
#define		BAD_CHECKSUM				3
#define		CAN_NOT_FIND_SITEID			4

/* detail return codes - licensed component must display appropriate error
	messages for detail. */
typedef enum {
	LIC_AOK=7300,		/* all ok */
	LIC_NO_LICENSE=7302,	/* component not licensed */
	LIC_WG_COUNT=7303,	/* workgroup license count exceeded */
	LIC_EXPIRED=7304,	/* license expired */
	LIC_WILL_EXPIRE=7305,	/* license will expire */
	LIC_DLL_EXPIRED=7306,	/* license dll expired */
	LIC_CANT_OPEN=7309,	/* can't open file */
	LIC_CORR_FILE=7311,	/* corrupt file */
	LIC_CANT_READ=7312,	/* can't read file */
	LIC_MAC_SERIAL=7315,	/* MAC address or serial number incorrect */
	LIC_MACHINETYPE=7316,	/* machinetype incorrect */
	LIC_TERMINATE=7317,	/*Enterprise or Workgroup grace period expired*/
	LIC_EE_COUNT=7318,	/* Enterprise/TNG license count exceeded */
	LIC_UPG_AOK=7319,   /*Product is not trial version, can be upgraded*/
	LIC_UPG_NAOK=7320,  /*Product is trial version, can not be upgraded*/
	LIC_REG_LATER=7321,
	LIC_SAFETY=7322,		/*We are using the safety key*/
	LIC_INTERNAL_ERROR=7399 /* An internal error */
	} LIC_RETURN;

typedef enum {
	LIC_ERR_APP=0,		/* caller does error processing */
	LIC_ERR_LIC=1,		/* ca_license check does error processing */
	} LIC_ERRFLAG;

/* possible values that can be queried via ca_license_query */
typedef enum {
    LIC_CHECK=500,          /* what a license check would return - dont log anything */
    LIC_EXPIRES=501,        /* next expiration date for licenses of a given comp */
    LIC_USER_COUNT=502,     /* total (non-expired) user count available for a given comp */
    LIC_INSTALL_DATE=503,   /* time of installtion of a given comp */
    LIC_COMP_INT=504,       /* int value stored for a given comp */
    LIC_LAST_CHECK=505,     /* time of previous license check on a given comp */
    LIC_LAST_ERROR=506,     /* time of previous license error on a given comp */
    LIC_LAST_WARN=507,      /* time of previous license warning (7305) on a given comp */
    } LIC_QUERY;


/* typedef of upgrade check function in lic98upgdll. User should pass the 4-digit component code 
and that function should return either 7319 or 7320 */
typedef LIC_RETURN (*PCA_LICENSE_UPGRADE_CHECK)(LIC_STRING); 

#ifdef AUTHENTICATE_DLL	
	/* only for license internal builds. */
	extern LIC_RETURN __cdecl ca_license_check(
		LIC_STRING comp,
		unsigned int wg_count,
		LIC_ERRFLAG process_error,
		int *enc);

	extern LIC_RETURN __cdecl ca_license_check_m(
		LIC_STRING comp,
		unsigned int wg_count,
		LIC_ERRFLAG process_error,
		_TCHAR msgret[512],
		int *enc);

extern LIC_RETURN __cdecl ca_license_check_fb(
		LIC_STRING comp, 
		unsigned int wg_count, 
		LIC_ERRFLAG process_error, 
		LIC_IDENT  *cust_info, 
		int *enc);

extern LIC_RETURN __cdecl ca_license_check_fbm(
		LIC_STRING comp, 
		unsigned int wg_count, 
		LIC_ERRFLAG process_error, 
		LIC_IDENT  *cust_info, 
		_TCHAR msgret[512], 
		int *enc); 		


#else	/* everyone should use this */

/* ca_license_check:		Prototype for standard license check. Takes
							a component code, a count of clients and 
							returns an indication of whether a license
							exists in this environment. 
 */

extern LIC_RETURN __cdecl ca_license_check(
		LIC_STRING comp,	/* component name */
		unsigned int wg_count,	/* number of "things" */
		LIC_ERRFLAG process_error);/* error processing flag */

/* ca_license_check_m:	   Same as ca_license_check (above) but 
                           provides a formatted error message in 
						   a caller supplied buffer. 
 */

extern LIC_RETURN __cdecl ca_license_check_m(	
		LIC_STRING comp,	/* component name */
		unsigned int wg_count,	/* number of "things" */
		LIC_ERRFLAG process_error, /* error processing flag */
		_TCHAR msgret[512]);	/* !0 - write msg here, not to syslog */
/* ca_license_check_fb:	  Identical to ca_license_check, but returns 
						  information about the user in the "cust_info"
						  parameter. Up to four identification fields 
						  are supplied: user name, organization, 
						  site id, and a generic identification field
						  that can be used for things like contract #'s
 */

extern LIC_RETURN __cdecl ca_license_check_fb(
		LIC_STRING comp, 
		unsigned int wg_count, 
		LIC_ERRFLAG process_error, 
		LIC_IDENT   *cust_info);

/* ca_license_check_fbm:  Identical to ca_license_check_m, but returns 
					      information about the user in the "cust_info"
						  parameter. Up to four identification fields 
						  are supplied: user name, organization, 
						  site id, and a generic identification field
						  that can be used for things like contract #'s
 */

extern LIC_RETURN __cdecl ca_license_check_fbm(
		LIC_STRING comp, 
		unsigned int wg_count, 
		LIC_ERRFLAG process_error, 
		LIC_IDENT  *cust_info, 
		_TCHAR msgret[512]); 

#endif

/* prototype for license error string */
/* This function is useful ONLY for pre-approved components 
   that are handling violations (i.e. process_error set to 
   LIC_ERR_APP when calling ca_license_check).  In this case,
   the application should use the standard set of error strings
   for each LIC_RECTURN.  This is the function that returns 
   a pointer to the corresponding string for each LIC_RETURN.
   The strings are NULL terminated standard C strings.*/
extern char * __cdecl ca_license_string(/* translates LIC_RETURN into string */
	LIC_RETURN retcode);   	/* return code from ca_license_check */
#ifdef WIN32
extern wchar_t * __cdecl ca_license_wstring(/* translates LIC_RETURN into string */
	LIC_RETURN retcode);   	/* return code from ca_license_check */
#endif
/* This is the function to get site id and company name from olf file by passing site_id
   company name and component code. This function check if there is a master license in the
   olf file and the checksum is correct. The function returns SUCCESS if everything is OK,
   otherwise return the above error message. */

extern  int __cdecl lic_get_siteid(char * comp_code, char* site_id, char* company_name);

/* ca_license_version:    Returns the version of the licensing system 
                          If anything other than LIC_CURRENT_VERSION is 
                          returned, then there may be an installaiton problem 
                          of some type (it's OK to run with runtime version
						  *higher* than you expect; but not lower). 
 */

#ifdef NETWARE
extern LIC_RETURN __cdecl _ca_license_version(void);
#else
extern LIC_RETURN __cdecl ca_license_version(void);
#endif


/* ca_license_getident:   Returns identification of the current user. 
                          See definition of the LIC_IDENT structure
						  above - up to 4 fields can be returned if
						  they are defined in the license file. 
 */
 
#ifdef NETWARE
extern LIC_RETURN __cdecl _ca_license_getident(LIC_IDENT *idp);
#else
extern LIC_RETURN __cdecl ca_license_getident(LIC_IDENT *idp);
#endif

/* ca_license_log_usage:  Updates product last-used information and
                          client (wg) count. 
 */

#ifdef NETWARE
LIC_RETURN __cdecl _ca_license_log_usage(char *comp, int wg_count);
#else
LIC_RETURN __cdecl ca_license_log_usage(char *comp, int wg_count);
#endif

/* ca_license_log_int:    Updates a product-supplied word of information
                          in the local and remote log files. 
 */

#ifdef NETWARE
LIC_RETURN __cdecl _ca_license_log_int(char *comp, int value);
#else
LIC_RETURN __cdecl ca_license_log_int(char *comp, int value);
#endif

/* ca_license_release:    Notifies the licensing system that a license
                          is no longer in use. 
 */

#ifdef NETWARE
LIC_RETURN __cdecl _ca_license_release(char *comp);
#else
LIC_RETURN __cdecl ca_license_release(char *comp);
#endif

/* ca_license_query	  
                      
                      
*/

#ifdef NETWARE
extern LIC_RETURN __cdecl _ca_license_query(LIC_QUERY, 
                                           char *, 
                                           void *);
#else

extern LIC_RETURN __cdecl ca_license_query(LIC_QUERY, 
                                           char *, 
                                           void *);
#endif

/* ca_license_build:	  Returns the build version of the licensing 
                          system and optionally the build date and
                          the build time.
*/

#ifdef NETWARE
extern LIC_RETURN __cdecl _ca_license_build(_CHAR **, 
											_TCHAR **, 
											_TCHAR **);
#else
extern LIC_RETURN __cdecl ca_license_build(_TCHAR **, 
										   _TCHAR **, 
										   _TCHAR **);
#endif

/* 
    The following macro tells a product that the newer LIC98 functions are available.
 */

#define CA_LICENSE_ISCURRENT (ca_license_check(LIC_MAGIC_COMPCODE, 0, LIC_ERR_APP) < LIC_AOK ? 1 : 0)

#ifdef __cplusplus
}
#endif

#endif /* NEWLICENSE_H*/