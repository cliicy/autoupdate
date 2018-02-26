#ifndef _ASVCTL_H
#define _ASVCTL_H

#ifdef __cplusplus		   	
  extern "C" {                     /* avoid name-mangling if used from C++ */
#endif /* __cplusplus */

#include <tchar.h>
#ifndef PTCHAR
typedef TCHAR *PTCHAR;
#endif

#ifdef _TBYTES
#undef _TBYTES
#endif
#ifdef _TCHARS
#undef _TCHARS
#endif
#if     defined(UNICODE)
#define _TBYTES(s)      (lstrlen(s) * sizeof(TCHAR))
#define _TCHARS(s)      (sizeof(s) / sizeof(TCHAR))
#else
#define _TBYTES(s)      strlen(s)
#define _TCHARS(s)      sizeof(s)
#endif

#define _ASVL_ERROR           255
/*
 *      Languages
 */

#define _CSTL_ENGLISH           0
#define _CSTL_FRENCH            1
#define _CSTL_GERMAN            2
#define _CSTL_SPANISH           3
#define _CSTL_ITALIAN           4
#define _CSTL_JAPANESE          5
#define _CSTL_CHINESE           6
#define _CSTL_KOREAN            7
#define _CSTL_TCHINESE          8
#define _CSTL_PORTUGUESE        9
#define _CSTL_CZECH				10
#define _CSTL_DANISH			11
#define	_CSTL_DUTCH				12
#define	_CSTL_FINNISH			13		
#define	_CSTL_GREEK				14
#define	_CSTL_HUNGARIAN			15
#define	_CSTL_NORWEGIAN			16
#define	_CSTL_POLISH			17
#define	_CSTL_RUSSIAN			18
#define	_CSTL_SWEDISH			19
#define	_CSTL_TURKISH			20

/*
 *      Product Codes
 */

#define _CSTPC_RELEASE          0
#define _CSTPC_EVAL             1
#define _CSTPC_TRIAL            2
#define _CSTPC_BETA             3
#define _CSTPC_SITE				4

/*
 *      Product Flags
 */

#define _CSTPF_DISK_LOCAL       0x00000001      // Works with local drives (C:)
#define _CSTPF_DISK_REMOTE      0x00000002      // Works with remote drives (\\Server\Share)
#define _CSTPF_DISK_MASK        0x0000000f
#define _CSTPF_TAPE_LOCAL       0x00000010      // Works with local tape engine
#define _CSTPF_TAPE_REMOTE      0x00000020      // Works with remote tape engine
#define _CSTPF_TAPE_MASK        0x000000f0
#define _CSTPF_AGENT_PULL       0x00000100      // Works with pull (legacy) agents 
#define _CSTPF_AGENT_PUSH       0x00000200      // Works with push agents 
#define _CSTPF_AGENT_DSA        0x00000400      // Works with database agents
#define _CSTPF_AGENT_MASK       0x00000f00
#define _CSTPF_MGR_LOCAL        0x00001000      // Works with local manager
#define _CSTPF_MGR_REMOTE       0x00002000      // Works with remote manager
#define _CSTPF_MGR_MASK         0x0000f000
#define _CSTPF_NO_DBMGR         0x00010000      // Remove managers
#define _CSTPF_NO_DEVMGR        0x00020000
#define _CSTPF_NO_RPTMGR        0x00040000
#define _CSTPF_NO_ALERT         0x00080000
#define _CSTPF_NO_VSCAN         0x00100000


/*
 *      OEM Code 
 */
#if     (!defined(OEM_CHEYENNE))

#define  OEM_CHEYENNE                           0x00000000
#define  OEM_COMPAQ                             0x00000001
#define  OEM_IBM                                0x00000002
#define  OEM_HP                                 0x00000003
#define  OEM_UNISYS                             0x00000004
#define  OEM_NEC                                0x00000005
#define  OEM_DEC                                0x00000006
#define  OEM_INTEL                              0x00000007

#endif //  OEM_CHEYENNE

#define  OEM_TYPE08                             0x00000008 // Single Server + No agents


/*
 *      Product Type 
 */

#define _CSTPT_ARCSERVE				0x00000010	//ARCserveIT - Enterprise
#define _CSTPT_ARCSERVE_SINGLE		0x00000011	//ARCserveIT - Workgroup
#define _CSTPT_ARCSERVE_TNG			0x00000012	//ARCserveIT - TNG/ASM
#define _CSTPT_ARCSERVE_ASO			0x00000013	//ADvanced Storage Option
#define _CSTPT_ARCSERVE_ADVANCED	0x00000014	//ARCserveIT - Advanced
#define _CSTPT_ARCSOLO				0x00000020
#define _CSTPT_ARCSOLO_LITE			0x00000021
#define _CSTPT_ARCSOLO_OPTICAL		0x00000022



/*
 *      Strings
 */

#define _CSTS_PRODUCT           100
#define _CSTS_PRODUCT_LONG_EE   101
#define _CSTS_VERSION           102
#define _CSTS_VERSION_LONG      103
#define _CSTS_RELEASE           104
#define _CSTS_RELEASE_LONG      105
#define _CSTS_BUILD             106
#define _CSTS_BUILD_LONG        107
#define _CSTS_COPYRIGHT         108
#define _CSTS_COPYRIGHT_LONG    109
#define _CSTS_OEM               110
#define _CSTS_OEM_LONG          111
#define _CSTS_PRODUCT_TYPE      112
#define _CSTS_PRODUCT_LONG_SS   113
#define _CSTS_PRODUCT_LONG_ASO  114
#define _CSTS_OEM_COMPAQ		115
#define _CSTS_OEM_LONG_COMPAQ	116
#define _CSTS_OEM_IBM			117
#define _CSTS_OEM_LONG_IBM		118
#define _CSTS_OEM_HP			119
#define _CSTS_OEM_LONG_HP		120
#define _CSTS_OEM_UNISYS		121
#define _CSTS_OEM_LONG_UNISYS	122
#define _CSTS_OEM_NEC			123
#define _CSTS_OEM_LONG_NEC		124
#define _CSTS_OEM_DEC			125
#define _CSTS_OEM_LONG_DEC		126
#define _CSTS_OEM_INTEL			127
#define _CSTS_OEM_LONG_INTEL	128
#define _CSTS_PRODUCT_TYPE_EVAL	129
#define _CSTS_PRODUCT_TYPE_TRIAL	130
#define _CSTS_PRODUCT_TYPE_SITE	131
#define _CSTS_OEM_CODE			132
#define _CSTS_OEM_OTHERS		133
#define _CSTS_OEM_LONG_OTHERS	134
#define _CSTS_PRODUCT_LONG_TNG  135
#define _CSTS_PRODUCT_LONG_ADVANCED	136
#define _CSTS_PRODUCT_LONG_NWADVANCED   137
#define _CSTS_COPYRIGHT_DBYTE        138
#define _CSTS_COPYRIGHT_LONG_DBYTE   139





#if     defined(UNICODE)
#define AsvGetProductName       AsvGetProductNameW
#define AsvGetProductNameLong   AsvGetProductNameLongW
#define AsvGetProductTypeName   AsvGetProductTypeNameW
#define AsvGetBuildNumber       AsvGetBuildNumberW
#define AsvGetBuildNumberLong   AsvGetBuildNumberLongW
#define AsvGetCopyright			AsvGetCopyrightW
#define AsvGetCopyrightLong     AsvGetCopyrightLongW
#define AsvGetRelease			AsvGetReleaseW
#define AsvGetReleaseLong       AsvGetReleaseLongW
#define AsvGetVersion			AsvGetVersionW
#define AsvGetVersionLong       AsvGetVersionLongW
#define AsvGetOEM				AsvGetOEMW
#define AsvGetOEMLong			AsvGetOEMLongW
#define AsvGetSerialNumber		AsvGetSerialNumberW
//#define AsvGetStringData		AsvGetStringDataW
#else
#define AsvGetProductName       AsvGetProductNameA
#define AsvGetProductNameLong   AsvGetProductNameLongA
#define AsvGetProductTypeName   AsvGetProductTypeNameA
#define AsvGetBuildNumber       AsvGetBuildNumberA
#define AsvGetBuildNumberLong   AsvGetBuildNumberLongA
#define AsvGetCopyright         AsvGetCopyrightA
#define AsvGetCopyrightLong     AsvGetCopyrightLongA
#define AsvGetRelease			AsvGetReleaseA
#define AsvGetReleaseLong       AsvGetReleaseLongA
#define AsvGetVersion			AsvGetVersionA
#define AsvGetVersionLong       AsvGetVersionLongA
#define AsvGetOEM				AsvGetOEMA
#define AsvGetOEMLong			AsvGetOEMLongA
#define AsvGetSerialNumber		AsvGetSerialNumberA
//#define AsvGetStringData		AsvGetStringDataA
#endif

USHORT WINAPI AsvGetProductName(PTCHAR name, USHORT limit);


USHORT WINAPI AsvGetProductNameLong(PTCHAR name);
USHORT WINAPI AsvGetProductTypeName(PTCHAR name);
USHORT WINAPI AsvGetBuildNumber(PTCHAR name);
USHORT WINAPI AsvGetBuildNumberLong(PTCHAR name);
USHORT WINAPI AsvGetCopyright(PTCHAR name);
USHORT WINAPI AsvGetCopyrightLong(PTCHAR name);
USHORT WINAPI AsvGetRelease(PTCHAR name);
USHORT WINAPI AsvGetReleaseLong(PTCHAR name);
USHORT WINAPI AsvGetVersion(PTCHAR name);
USHORT WINAPI AsvGetVersionLong(PTCHAR name);
USHORT WINAPI AsvGetOEM(PTCHAR name);
USHORT WINAPI AsvGetOEMLong(PTCHAR name);
USHORT WINAPI AsvGetSerialNumber(PTCHAR name);

DWORD	AsvGetStringData( UINT id, LPSTR type, LPSTR text);
USHORT	GetSerialNumberFromReg(LPSTR name);	 //for internal use only
UINT	 GetRealProductType(BOOL bProductType);//for internal use only

/*
 *      Cheyenne Version Info
 */

#if     defined(UNICODE)
#define AsvCheckPlatform AsvCheckPlatformW
#else
#define AsvCheckPlatform AsvCheckPlatformA
#endif

BOOL   WINAPI AsvCheckPlatform(PTCHAR errorMsg, UINT limit);

BYTE   WINAPI AsvGetCharSet(VOID);
UINT   WINAPI AsvGetLanguage(VOID);
UINT   WINAPI AsvGetProductCode(VOID);
UINT   WINAPI AsvGetProductFlags(VOID);
UINT   WINAPI AsvGetProductType(VOID);
UINT   WINAPI AsvGetOEMCode(VOID);


#ifdef __cplusplus
  }
#endif /* __cplusplus */

#endif //_ASVCTL_H
