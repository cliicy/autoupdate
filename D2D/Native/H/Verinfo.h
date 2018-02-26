
#ifndef __BUILD_H__
#define __BUILD_H__

/*
// MACRO DOTNETVERSION is added by David.Yuan (yuada01).
// The purpose of this macro is to make visual C++ 7.0 project can be compiled after import VerInfo.RC
// In default setting, VC7's include path do not include ver.h, so vc7 projects can not compiled
// if we use <ver.h>
// So I made some this DOTNETVERSION and only defined it at VC7's project. This change should not effect 
// any other VC6's project  ----  05/26/2005


#ifndef DOTNETVERSION	//it's a vc6 project

#ifdef _WINDOWS
#include <winver.h>
#endif

#ifdef RC_INVOKED
#include <ver.h>
#endif


#else	//#ifndef DOTNETVERSION	-- it's a vc7 project
#include <winver.h>		//we only need this file

#endif //#ifndef DOTNETVERSION
*/
#include <winver.h>

#include "brandname.h"
/* ********************************************************************** */
/* Change these defines to identify the product                           */
/* These are expected to be changed automagically by \MY\VER\V.BAT        */
/* ********************************************************************** */
#define BUILD				1500
#define BUILD_STR			"1500"
#define ECN					0
#define ECN_STR				"0"
#define BUILD_DATE_STR		"07/01/2009"


/*
 ***************************************************************************
 * This file is to be used by ARCserve in the VERSIONINFO resource for all
 * EXE's and DLL's
 *
 * The sample VERSIONINFO block below uses defines from this file to make
 * sure the version info is consistent.
 *
 * 1 VERSIONINFO
 *  FILEVERSION	  		1,0,0,0
 *  PRODUCTVERSION		PRODUCT_VERSION
 *  FILEFLAGSMASK		VS_FFI_FILEFLAGSMASK
 * #ifdef _DEBUG
 *  FILEFLAGS			VS_FF_DEBUG
 * #else
 *  FILEFLAGS			0x0L
 * #endif
 *  FILEOS				VOS__WINDOWS16
 *  FILETYPE			VFT_APP
 *  FILESUBTYPE 		0x0L
 *
 * BEGIN
 *   BLOCK "StringFileInfo"
 *   BEGIN
 *     BLOCK "040904E4"
 *     BEGIN
 *       VALUE "FileVersion",		"10.0.0000"
 *       VALUE "FileDescription",	"BrightStor EB GUI\0"
 *       VALUE "InternalName",		"\0"
 *       VALUE "OriginalFilename",	"\0"
 *       VALUE "Comments",         	BUILD_ID_STR
 *       VALUE "CompanyName",	COMPANY_NAME_STR
 *       VALUE "LegalCopyright",	COPYRIGHT_STR
 *       VALUE "ProductName",		PRODUCT_NAME_STR
 *       VALUE "ProductVersion",	PRODUCT_VERSION_STR
 *     END
 *   END
 *   BLOCK "VarFileInfo"
 *   BEGIN
 *     VALUE "Translation", 0x409, 0x4E4
 *   END
 * END
 *
 ***************************************************************************
 */


/* ********************************************************************** */
#define BUILD_ID_STR			"Build " BUILD_STR "." ECN_STR " " BUILD_DATE_STR "\0"
#define	COMPANY_NAME_STR		"Arcserve\0"
#define	COPYRIGHT_STR			"Copyright (C) 2016 Arcserve (USA), LLC\0"


/* ********************************************************************** */
#define PRODUCT_VERSION_MAJOR	6 
#define PRODUCT_VERSION_MINOR	0  
#define PRODUCT_VERSION			PRODUCT_VERSION_MAJOR,PRODUCT_VERSION_MINOR,0000,0
#define PRODUCT_VERSION_STR		"6.0"  
#define PRODUCT_NAME_STR		"Arcserve Unified Data Protection"  
#define PRODUCT_TYPE_SHORT		"UDP"
#define VERSION_FILENAME		"" 
#define VERSION_BLOCK			"040904E4"
#define VERSION_TRANSLATION		0x409, 0x4E4
#define FILE_PROPERTY_VERSION_STR	"6.0"
#define FILE_PROPERTY_VERSION_MAJOR PRODUCT_VERSION_MAJOR
#define FILE_PROPERTY_VERSION_MINOR PRODUCT_VERSION_MINOR


/* ********************************************************************** */
#endif

