// CUI.H 
///////////////////////////////////////////////////////////////////////////////////
#ifndef __CUI_H__
#define __CUI_H__

/*
 *   Host Server OS Type values 
 */
typedef enum _HSOST_TYPE 
{
	HSOST_UNKNOWN	= 0,		// Undefined OS	
	HSOST_NT		= 1,		// The operating system is NT
	HSOST_NETWARE	= 2, 		// The operating system is NetWare 
	HSOST_UNIX		= 3			// The operating system is UNIX
} HSOST_TYPE;

/*
 *  Host Server OS Type API implemented as macros
 */

#define  ASIsHostServerOSNT(x)                 (x == HSOST_NT)      
#define  ASIsHostServerOSUnix(x)               (x == HSOST_UNIX)      
#define  ASIsHostServerOSNetware(x)            (x == HSOST_NETWARE) 

#define  ASSetHostServerOSNT(x)                (x = HSOST_NT)      
#define  ASSetHostServerOSUnix(x)              (x = HSOST_UNIX)      
#define  ASSetHostServerOSNetware(x)           (x = HSOST_NETWARE)

#endif
///////////////////////////////////////////////////////////////////////////////////



