/**************************************************************************
This program is an unpublished work fully protected by the United States
Copyright laws and is considered a trade secret belonging to Cheyenne
Software, Inc.

Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
All rights reserved.  Any third party trademarks or copyrights are the
property of their respective owners.

 Program Name:  ARCserve APIs, Defines Header
      Version:  Release 5.0, Rev 1.0
 Version Date:  August 10, 1992
**************************************************************************/

#ifndef _ASDEFS_H_
#define _ASDEFS_H_

#ifdef __cplusplus
  extern "C" {                     /* avoid name-mangling if used from C++ */
#endif /* __cplusplus */

//CUI
#ifndef _STR_OP_DEFINED
#if     (!defined(UNICODE) || !defined(_UNICODE))

#define STRLEN   strlen
#define STRCAT   strcat
#define STRCPY   strcpy
#define STRNCPY  strncpy
#define STRCMP   strcmp
#define STRNCMP  strncmp
#define STRICMP  _stricmp
#define STRNICMP _strnicmp
#define STRCHR   strchr
#define STRSTR   strstr
#define STRUPR   _strupr
#define SPRINTF  sprintf
#define PRINTF   printf
#define HREAD    _read
#define HWRITE   _write
#define STRDUP   strdup
#define STRRCHR  strrchr
#define VSPRINTF vsprintf
#else

#ifdef STRLEN
#undef STRLEN
#endif
#define STRLEN   wcslen

#ifdef STRCAT
#undef STRCAT
#endif
#define STRCAT   wcscat

#ifdef STRCPY
#undef STRCPY
#endif
#define STRCPY   wcscpy

#ifdef STRNCPY
#undef STRNCPY
#endif
#define STRNCPY  wcsncpy

#ifdef STRCMP
#undef STRCMP
#endif
#define STRCMP   wcscmp

#ifdef STRNCMP
#undef STRNCMP
#endif
#define STRNCMP  wcsncmp

#ifdef STRICMP
#undef STRICMP
#endif
#define STRICMP  _wcsicmp

#ifdef STRNICMP
#undef STRNICMP
#endif
#define STRNICMP _wcsnicmp

#ifdef STRCHR
#undef STRCHR
#endif
#define STRCHR   wcschr

#ifdef STRSTR
#undef STRSTR
#endif
#define STRSTR   wcsstr

#ifdef STRUPR
#undef STRUPR
#endif
#define STRUPR   _wcsupr

#ifdef SPRINTF
#undef SPRINTF
#endif
#define SPRINTF  _swprintf

#ifdef PRINTF
#undef PRINTF
#endif
#define PRINTF   swprintf
#define HREAD    _read
#define HWRITE   _write

#ifdef STRDUP
#undef STRDUP
#endif
#define STRDUP   wcsdup

#ifdef STRRCHR
#undef STRRCHR
#endif
#define STRRCHR  wcsrchr
#define VSPRINTF vswprintf

#endif      //_UNICODE
//CUI
#endif

#define MEMSET   memset
#define MEMCMP   memcmp
#define MEMICMP  _memicmp
#define MEMCPY   memcpy

#pragma warning(disable:4005)
#define OPEN     _open
#pragma warning(default:4005)

#define CREAT    _creat
#define CLOSE    _close
#define MALLOC(len)   HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, (len))
#define FREE(ptr)     HeapFree(GetProcessHeap(), 0, (ptr))
#define REALLOC(ptr, len) HeapReAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, (ptr), (len))

#ifndef TRUE
#define TRUE 1
#endif

#ifndef FALSE
#define FALSE 0
#endif

#ifndef _ULONG_DEFINED
typedef unsigned long  ULONG;
#define _ULONG_DEFINED
#endif

#ifndef _UINT_DEFINED
#ifdef MIDL_INVOKED
typedef unsigned long   UINT;
#else
typedef unsigned int    UINT;
#endif
#define _UINT_DEFINED
#endif

#ifndef _USHORT_DEFINED
typedef unsigned short USHORT;
#define _USHORT_DEFINED
#endif

#ifndef _UCHAR_DEFINED
typedef unsigned char  UCHAR;
#define _UCHAR_DEFINED
#endif

#ifndef _TUCHAR_DEFINED
#ifdef UNICODE
typedef unsigned short   TUCHAR;
#else
typedef unsigned char    TUCHAR;
#endif
#define _TUCHAR_DEFINED
#endif

#ifndef _LONG_DEFINED
typedef long           LONG;
#define _LONG_DEFINED
#endif

#ifndef _INT_DEFINED
#ifdef MIDL_INVOKED
typedef long           INT;
#else
typedef int            INT;
#endif
#define _INT_DEFINED
#endif

#ifndef _SHORT_DEFINED
typedef short          SHORT;
#define _SHORT_DEFINED
#endif

#ifndef _CHAR_DEFINED
typedef char           CHAR;
#define _CHAR_DEFINED
#endif

#ifndef _WCHAR_DEFINED
typedef wchar_t           WCHAR;
#define _WCHAR_DEFINED
#endif
#ifndef _VOID_DEFINED
#ifdef MIDL_INVOKED
#define VOID
#else
#define VOID           void
#endif
#define _VOID_DEFINED
#endif

#ifndef _FLAG8_DEFINED
typedef UCHAR          FLAG8;
#define _FLAG8_DEFINED
#endif

#ifndef FLAG16_DEFINED
typedef USHORT         FLAG16;
#define _FLAG16_DEFINED
#endif

#ifndef _FLAG32_DEFINED
typedef ULONG          FLAG32;
#define _FLAG32_DEFINED
#endif

#ifndef _BOOL_DEFINED
#ifdef MIDL_INVOKED
typedef long           BOOL;
#else
typedef int            BOOL;
#endif
#define _BOOL_DEFINED
#endif

#ifndef _PULONG_DEFINED
typedef ULONG *    PULONG;
#define _PULONG_DEFINED
#endif

#ifndef _PUINT_DEFINED
typedef UINT *     PUINT;
#define _PUINT_DEFINED
#endif

#ifndef _PUSHORT_DEFINED
typedef USHORT *   PUSHORT;
#define _PUSHORT_DEFINED
#endif

#ifndef _PUCHAR_DEFINED
typedef UCHAR *    PUCHAR;
#define _PUCHAR_DEFINED
#endif

#ifndef _PLONG_DEFINED
typedef LONG *     PLONG;
#define _PLONG_DEFINED
#endif

#ifndef _PINT_DEFINED
typedef INT *      PINT;
#define _PINT_DEFINED
#endif

#ifndef _PSHORT_DEFINED
typedef SHORT *    PSHORT;
#define _PSHORT_DEFINED
#endif

#ifndef _PCHAR_DEFINED
typedef CHAR *     PCHAR;
#define _PCHAR_DEFINED
#endif

#ifndef _PSZ_DEFINED
typedef CHAR *     PSZ;
#define _PSZ_DEFINED
#endif

#ifndef _PWSZ_DEFINED
typedef wchar_t *     PWSZ;
#define _PWSZ_DEFINED
#endif

#ifndef _PTSZ_DEFINED
#ifdef UNICODE
typedef wchar_t *    PTSZ;
#else
typedef char *     PTSZ;
#endif
#define _PTSZ_DEFINED
#endif

#ifndef _PVOID_DEFINED
#ifdef MIDL_INVOKED
typedef char *     PVOID;
#else
typedef void *     PVOID;
#endif
#define _PVOID_DEFINED
#endif

#ifndef _HFILE_DEFINED
#ifdef MIDL_INVOKED
typedef long       HFILE;
#else
typedef int        HFILE;
#endif
#define _HFILE_DEFINED
#endif

#ifndef _handle_t_DEFINED
#ifndef MIDL_INVOKED
typedef void *     handle_t;
#endif
#define _handle_t_DEFINED
#endif

#ifndef _HANDLE_DEFINED
#ifndef MIDL_INVOKED
typedef void *     HANDLE;
#else
typedef char *     HANDLE;
#endif
#define _HANDLE_DEFINED
#endif

#define HFILE_ERROR ((HFILE)-1)

#ifndef _ASRET_DEFINED
typedef LONG       ASRET;
#define _ASRET_DEFINED
#endif

#ifndef _QSIRET_DEFINED
typedef LONG       QSIRET;
#define _QSIRET_DEFINED
#endif

//
//   Resume Key - used by ASEnumXXXX() functions for resuming enumeration
//
typedef struct tagRKEY
{
  ULONG ulInit;
  ULONG ulID;
  UCHAR aucExtra[260];
} RKEY;
typedef RKEY *PRKEY;

#define INITRESUMEKEY(RKey)  { (RKey).ulInit = 0L; (RKey).ulID = (ULONG) -1L; }

//
//   Data Return Detail - usDetail
//
#define DTL_FIXEDVAR      0            // Return fixed and variable data
#define DTL_FIXEDONLY     1            // Return fixed data only
#define DTL_TOTALS        2            // Return total entries and size

// CUI Start
typedef struct {
   CHAR   day;
   CHAR   mon;
   USHORT year;
} BTR_DATE;

typedef struct {
   CHAR   hsec;   // hundreds of second
   CHAR   sec;
   CHAR   min;
   CHAR   hour;
} BTR_TIME;
// CUI End

#define PATH_SIZE		1024
#define MAX_NT_PATH		512			// 1024 is too large to stack !
#define MAX_NT_PATH_EXT	648
#define MAX_NT_PATH_MAX_LEN 519

#define STREND		cststrend
#define SBSTRCPY	strcpy
#define SBSTRNCPY	strncpy

#define SBSTRCAT	strcat
#define STRNCAT		_tcsncat
#define SBSTRNCAT	strncat

#define SBSTRCMP	strcmp
#define SBSTRICMP	stricmp
#define MBSTRCMP	_mbscmp

#define MBSTRNCMP	_mbsnbcmp
#define SBSTRNCMP	strncmp

#define STRCMPI		_tcsicmp
#define MBSTRICMP	_mbsicmp

#define SBSTRNICMP	_strnicmp
#define SBSTRLEN	strlen
#define MBSTRLEN	_mbslen
#define MBSTRUPR	_mbsupr

#define SSCANF		_stscanf
#define SBSSCANF	sscanf
#define SBSPRINTF	wsprintfA

#define SPRINTF_S	_sntprintf_s
#define STRCAT_S	_tcsncat_s
#define SBSTRCAT_S	 strncat_s
#define SBSTRNCAT_S	 strncat_s

#define SBSTRCPY_S	 strncpy_s
#define SBSTRNCPY_S	 strncpy_s
#define STRCPY_S	_tcsncpy_s
#define STRNCPY_S	_tcsncpy_s
#define MBSTRCHR	_mbschr
#define MBSTRRCHR	_mbsrchr
#define MBSTRSTR	_mbsstr

#define STRTOK		_tcstok

#define TOUPPER		_totupper
#define SBTOUPPER	toupper
#define STRPBRK		_tcspbrk
#define MBSTRPBRK	_mbspbrk

#define BKSCH		_T('\\')
#define FILESEPCH	BKSCH
#define BKSSZ		_T("\\")
#define FILESEPSZ	BKSSZ
#define NULLCH		_T('\0')
#define DOTTCH		_T('.')
#define COLLCH		_T(':')
#define DOLLCH		_T('$')

#define MALLOC(len)   HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, (len))
#define FREE(ptr)     HeapFree(GetProcessHeap(), 0, (ptr))
#define REALLOC(ptr, len) HeapReAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, (ptr), (len))

#ifdef __cplusplus
  }
#endif /* __cplusplus */

// Some limitation parameter in D2D
// maximal character count of session password.
//#define AF_SESSPWD_MAXLEN_IN_CHARACTER   23
#endif   // #ifndef _ASDEFS_H_
