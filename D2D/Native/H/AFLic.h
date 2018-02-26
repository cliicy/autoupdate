#pragma once

#include <Windows.h>
#include <Wbemcli.h>
#include <comutil.h>
#include <atlbase.h>
#include <vector>
using namespace std;

typedef vector<int> VLICTYPE;
typedef struct _ARCFLASH_LIC_INFO
{
	TCHAR			pwszOSName[128];			//OS Name
	BOOL			bAFSupportOS;				//if AF support the OS
	BOOL			bWorkStation;				//work station or not
	BOOL			bServer;					//server machine
	BOOL			bSBS;						//SBS or EBS not
	BOOL			bFoundationServer;			//Foundation server
	BOOL			bStorageServer;				//Storage server
	BOOL			bVMGuest;					//VM guest machine or not
	BOOL			bVMHost;					//VM host machine or not
	BOOL			bAllowBMR;					//Allow BMR
	BOOL			bAllowBMRAlt;				//Allow BMR to alternative location
	BOOL			bProtectSQL;				//protect SQL APP
	BOOL			bProtectEXCH;				//protect Exchange APP
    BOOL            bExchDBRecovery;            //Support restore DB level restore.
    BOOL            bExchGRTRecovery;           //Support Exchange GRT restore.
	BOOL			bProtectHyperVM;			//protect HyperV VM
	BOOL			bWithBLI;					//with BLI license
    BOOL            bEncryption;                //Support encryption.
    BOOL            bManualExport;              //Support export manually
    BOOL            bScheduleExport;            //Support schedule export
	BOOL			bD2D2D;						//Support file copy to local disk

	OSVERSIONINFOEX osvi;						//version info
	SYSTEM_INFO		si;							//system info
	BOOL HasBaseLicense;	// whether has base (OS/BLI) license
}ARCFLASH_LIC_INFO, *PARCFLASH_LIC_INFO;

typedef void (WINAPI *PGNSI)(LPSYSTEM_INFO);
typedef BOOL (WINAPI *PGPI)(DWORD, DWORD, DWORD, DWORD, PDWORD);

// Newer product types than what is currently defined in Visual Studio 2005
#ifndef PRODUCT_ULTIMATE
#define PRODUCT_ULTIMATE                        0x00000001
#endif
#ifndef PRODUCT_HOME_BASIC
#define PRODUCT_HOME_BASIC                      0x00000002
#endif
#ifndef PRODUCT_HOME_PREMIUM
#define PRODUCT_HOME_PREMIUM                    0x00000003
#endif
#ifndef PRODUCT_ENTERPRISE
#define PRODUCT_ENTERPRISE                      0x00000004
#endif
#ifndef PRODUCT_HOME_BASIC_N
#define PRODUCT_HOME_BASIC_N                    0x00000005
#endif
#ifndef PRODUCT_BUSINESS
#define PRODUCT_BUSINESS                        0x00000006
#endif
#ifndef PRODUCT_STANDARD_SERVER
#define PRODUCT_STANDARD_SERVER                 0x00000007
#endif
#ifndef PRODUCT_DATACENTER_SERVER
#define PRODUCT_DATACENTER_SERVER               0x00000008
#endif
#ifndef PRODUCT_SMALLBUSINESS_SERVER
#define PRODUCT_SMALLBUSINESS_SERVER            0x00000009
#endif
#ifndef PRODUCT_ENTERPRISE_SERVER
#define PRODUCT_ENTERPRISE_SERVER               0x0000000A
#endif
#ifndef PRODUCT_STARTER
#define PRODUCT_STARTER                         0x0000000B
#endif
#ifndef PRODUCT_DATACENTER_SERVER_CORE
#define PRODUCT_DATACENTER_SERVER_CORE          0x0000000C
#endif
#ifndef PRODUCT_STANDARD_SERVER_CORE
#define PRODUCT_STANDARD_SERVER_CORE            0x0000000D
#endif
#ifndef PRODUCT_ENTERPRISE_SERVER_CORE
#define PRODUCT_ENTERPRISE_SERVER_CORE          0x0000000E
#endif
#ifndef PRODUCT_ENTERPRISE_SERVER_IA64
#define PRODUCT_ENTERPRISE_SERVER_IA64          0x0000000F
#endif
#ifndef PRODUCT_BUSINESS_N
#define PRODUCT_BUSINESS_N                      0x00000010
#endif
#ifndef PRODUCT_WEB_SERVER
#define PRODUCT_WEB_SERVER                      0x00000011
#endif
#ifndef PRODUCT_CLUSTER_SERVER
#define PRODUCT_CLUSTER_SERVER                  0x00000012
#endif
#ifndef PRODUCT_HOME_SERVER
#define PRODUCT_HOME_SERVER                     0x00000013
#endif
#ifndef PRODUCT_STORAGE_EXPRESS_SERVER
#define PRODUCT_STORAGE_EXPRESS_SERVER          0x00000014
#endif
#ifndef PRODUCT_STORAGE_STANDARD_SERVER
#define PRODUCT_STORAGE_STANDARD_SERVER         0x00000015
#endif
#ifndef PRODUCT_STORAGE_WORKGROUP_SERVER
#define PRODUCT_STORAGE_WORKGROUP_SERVER        0x00000016
#endif
#ifndef PRODUCT_STORAGE_ENTERPRISE_SERVER
#define PRODUCT_STORAGE_ENTERPRISE_SERVER       0x00000017
#endif
#ifndef PRODUCT_SERVER_FOR_SMALLBUSINESS
#define PRODUCT_SERVER_FOR_SMALLBUSINESS        0x00000018
#endif
#ifndef PRODUCT_SMALLBUSINESS_SERVER_PREMIUM
#define PRODUCT_SMALLBUSINESS_SERVER_PREMIUM    0x00000019
#endif

#ifndef PRODUCT_HOME_PREMIUM_N
#define PRODUCT_HOME_PREMIUM_N                      0x0000001A
#endif

#ifndef PRODUCT_ENTERPRISE_N
#define PRODUCT_ENTERPRISE_N                        0x0000001B
#endif

#ifndef PRODUCT_ULTIMATE_N 
#define PRODUCT_ULTIMATE_N                          0x0000001C
#endif

#ifndef PRODUCT_WEB_SERVER_CORE 
#define PRODUCT_WEB_SERVER_CORE                     0x0000001D
#endif

#ifndef PRODUCT_MEDIUMBUSINESS_SERVER_MANAGEMENT
#define PRODUCT_MEDIUMBUSINESS_SERVER_MANAGEMENT    0x0000001E
#endif

#ifndef PRODUCT_MEDIUMBUSINESS_SERVER_SECURITY
#define PRODUCT_MEDIUMBUSINESS_SERVER_SECURITY      0x0000001F
#endif

#ifndef PRODUCT_MEDIUMBUSINESS_SERVER_MESSAGING
#define PRODUCT_MEDIUMBUSINESS_SERVER_MESSAGING     0x00000020
#endif

#ifndef PRODUCT_SERVER_FOUNDATION
#define PRODUCT_SERVER_FOUNDATION                   0x00000021
#endif

#ifndef PRODUCT_HOME_PREMIUM_SERVER
#define PRODUCT_HOME_PREMIUM_SERVER                 0x00000022
#endif

#ifndef PRODUCT_SERVER_FOR_SMALLBUSINESS_V
#define PRODUCT_SERVER_FOR_SMALLBUSINESS_V          0x00000023
#endif

#ifndef PRODUCT_STANDARD_SERVER_V
#define PRODUCT_STANDARD_SERVER_V                   0x00000024
#endif

#ifndef PRODUCT_DATACENTER_SERVER_V
#define PRODUCT_DATACENTER_SERVER_V                 0x00000025
#endif

#ifndef PRODUCT_ENTERPRISE_SERVER_V 
#define PRODUCT_ENTERPRISE_SERVER_V                 0x00000026
#endif

#ifndef PRODUCT_DATACENTER_SERVER_CORE_V 
#define PRODUCT_DATACENTER_SERVER_CORE_V            0x00000027
#endif

#ifndef PRODUCT_STANDARD_SERVER_CORE_V
#define PRODUCT_STANDARD_SERVER_CORE_V              0x00000028
#endif

#ifndef PRODUCT_ENTERPRISE_SERVER_CORE_V
#define PRODUCT_ENTERPRISE_SERVER_CORE_V            0x00000029
#endif

#ifndef PRODUCT_HYPERV 
#define PRODUCT_HYPERV                              0x0000002A
#endif

#ifndef PRODUCT_STORAGE_EXPRESS_SERVER_CORE
#define PRODUCT_STORAGE_EXPRESS_SERVER_CORE         0x0000002B
#endif

#ifndef PRODUCT_STORAGE_STANDARD_SERVER_CORE
#define PRODUCT_STORAGE_STANDARD_SERVER_CORE        0x0000002C
#endif

#ifndef PRODUCT_STORAGE_WORKGROUP_SERVER_CORE
#define PRODUCT_STORAGE_WORKGROUP_SERVER_CORE       0x0000002D
#endif

#ifndef PRODUCT_STORAGE_ENTERPRISE_SERVER_CORE
#define PRODUCT_STORAGE_ENTERPRISE_SERVER_CORE      0x0000002E
#endif

#ifndef PRODUCT_STARTER_N 
#define PRODUCT_STARTER_N                           0x0000002F
#endif

#ifndef PRODUCT_PROFESSIONAL
#define PRODUCT_PROFESSIONAL                        0x00000030
#endif

#ifndef PRODUCT_PROFESSIONAL_N
#define PRODUCT_PROFESSIONAL_N                      0x00000031
#endif

#ifndef PRODUCT_SB_SOLUTION_SERVER
#define PRODUCT_SB_SOLUTION_SERVER                  0x00000032
#endif

#ifndef PRODUCT_SERVER_FOR_SB_SOLUTIONS
#define PRODUCT_SERVER_FOR_SB_SOLUTIONS             0x00000033
#endif

#ifndef PRODUCT_STANDARD_SERVER_SOLUTIONS 
#define PRODUCT_STANDARD_SERVER_SOLUTIONS           0x00000034
#endif

#ifndef PRODUCT_STANDARD_SERVER_SOLUTIONS_CORE
#define PRODUCT_STANDARD_SERVER_SOLUTIONS_CORE      0x00000035
#endif

#ifndef PRODUCT_SB_SOLUTION_SERVER_EM
#define PRODUCT_SB_SOLUTION_SERVER_EM               0x00000036
#endif

#ifndef PRODUCT_SERVER_FOR_SB_SOLUTIONS_EM
#define PRODUCT_SERVER_FOR_SB_SOLUTIONS_EM          0x00000037
#endif

#ifndef PRODUCT_SOLUTION_EMBEDDEDSERVER 
#define PRODUCT_SOLUTION_EMBEDDEDSERVER             0x00000038
#endif

#ifndef PRODUCT_SOLUTION_EMBEDDEDSERVER_CORE
#define PRODUCT_SOLUTION_EMBEDDEDSERVER_CORE        0x00000039
#endif

#ifndef PRODUCT_SMALLBUSINESS_SERVER_PREMIUM_CORE 
#define PRODUCT_SMALLBUSINESS_SERVER_PREMIUM_CORE   0x0000003F
#endif

#ifndef PRODUCT_ESSENTIALBUSINESS_SERVER_MGMT
#define PRODUCT_ESSENTIALBUSINESS_SERVER_MGMT       0x0000003B
#endif

#ifndef PRODUCT_ESSENTIALBUSINESS_SERVER_ADDL
#define PRODUCT_ESSENTIALBUSINESS_SERVER_ADDL       0x0000003C
#endif

#ifndef PRODUCT_ESSENTIALBUSINESS_SERVER_MGMTSVC 
#define PRODUCT_ESSENTIALBUSINESS_SERVER_MGMTSVC    0x0000003D
#endif

#ifndef PRODUCT_ESSENTIALBUSINESS_SERVER_ADDLSVC
#define PRODUCT_ESSENTIALBUSINESS_SERVER_ADDLSVC    0x0000003E
#endif

#ifndef PRODUCT_CLUSTER_SERVER_V
#define PRODUCT_CLUSTER_SERVER_V                    0x00000040
#endif

#ifndef PRODUCT_EMBEDDED
#define PRODUCT_EMBEDDED                            0x00000041
#endif

#ifndef PRODUCT_STARTER_E
#define PRODUCT_STARTER_E                           0x00000042
#endif

#ifndef PRODUCT_HOME_BASIC_E
#define PRODUCT_HOME_BASIC_E                        0x00000043
#endif

#ifndef PRODUCT_HOME_PREMIUM_E
#define PRODUCT_HOME_PREMIUM_E                      0x00000044
#endif

#ifndef PRODUCT_PROFESSIONAL_E
#define PRODUCT_PROFESSIONAL_E                      0x00000045
#endif

#ifndef PRODUCT_ENTERPRISE_E 
#define PRODUCT_ENTERPRISE_E                        0x00000046
#endif

#ifndef PRODUCT_ULTIMATE_E
#define PRODUCT_ULTIMATE_E                          0x00000047
#endif

#ifndef PRODUCT_UNLICENSED 
#define PRODUCT_UNLICENSED                          0xABCDABCD
#endif

#define AFLIC_ERROR_NO_COMPUTERNAME			0x00000001
#define AFLIC_ERROR_OS_NOTSUPPORT			0x00000002
#define AFLIC_ERROR_LIC_INSTANCE			0x00000003
#define AFLIC_ERROR_BASE_LIC				0x00000004
#define AFLIC_ERROR_SERVER_LIC				0x00000005  //No Server OS license.
#define AFLIC_ERROR_WORKSTATION_LIC			0x00000006  //No workstation license 
#define AFLIC_ERROR_FOUNDATIONSERVER_LIC	0x00000007  //No Foundation Server license
#define AFLIC_ERROR_SBS_LIC					0x00000008  //No SBS OS license
#define AFLIC_ERROR_VMGUEST_LIC				0x00000009  //No VM OS license.



#define APP_SQL                                         0x00000001
#define APP_EXCH                                        0x00000002
#define APP_OPT_ENCRYPTION                              0x00000003
#define APP_OPT_SCHEDULE_EXPORT                         0x00000004
#define APP_OPT_D2D2D									0x00000005

typedef struct license_extend
{
	DWORD version;		// size of current structure
	DWORD dwFlags;		// [in/out]
	BOOL isTRIAL;		// [out] whether it's trial license
	DWORD dwFirstID;	// [out] the first license id that is allocated from local license system
	DWORD dwMask;		// [out] the mask of current allocated licenses (may be include more than one license)
}LIC_EXTEND, *PLIC_EXTEND;

#define LICEX_FLG_CHK_CPM	0x01  //before checking license , need first check whether is under cpm. if under return directly.

//////////////////////////////////////////////////////////////////////////
//Function Name: IsUnderCPM()
//				 whether Current node is under CPM
//Parameters: 
//////////////////////////////////////////////////////////////////////////
BOOL IsUnderCPM();

//////////////////////////////////////////////////////////////////////////
//Function Name: AFLicCheck()
//				 check d2d license
//Parameters: 
//		pAFLicInfo [out] the result of checking license 
//		isCheckAll [in] TRUE, checking all the license
//						FALSE, don't check Base and OS license but only check the d2d feature license
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////
DWORD AFLicCheck( PARCFLASH_LIC_INFO pAFLicInfo, BOOL isCheckAll = TRUE, PLIC_EXTEND pExtend = NULL );

//////////////////////////////////////////////////////////////////////////
//Function Name: AFLicCheckEx()
//				 check d2d license with add event log when meeting error
//Parameters: 
//		pAFLicInfo [out] the result of checking license 
//		bEventLogging [in] whether add event log when meeting error
//		isCheckAll    [in] TRUE, checking all the license
//						FALSE, don't check Base and OS license but only check the d2d feature license
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////


DWORD AFLicCheckEx(PARCFLASH_LIC_INFO pAFLicInfo, bool bEventLogging = true, BOOL isCheckAll = TRUE, PLIC_EXTEND pExtend = NULL);

//////////////////////////////////////////////////////////////////////////
//Function Name: CheckLicense_APPEventLogging()
//				 check d2d application license
//Parameters: 
//		dwAPPType [in] the type of d2d application
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////
DWORD CheckLicense_APPEventLogging(DWORD dwAPPType);


typedef enum _ARCFLASH_LIC_CHECK_TYPE
{ 
	check_unknown			=0,
	check_WorkStation		=1,			//work station or not
	check_Server			=2,			//server machine
	check_SBS				=3,			//SBS or EBS not
	check_FoundationServer	=4,			//Foundation server
	check_StorageServer		=5,			//Storage server
	check_VMGuest			=6,			//VM guest machine or not
	check_VMHost			=7,			//VM host machine or not
	check_AllowBMR			=8,			//Allow BMR
	check_AllowBMRAlt		=9,			//Allow BMR to alternative locat
	check_ProtectSQL		=10,		//protect SQL APP
	check_ProtectEXCH		=11,		//protect Exchange APP
	check_ExchDBRecovery	=12,        //Support restore DB level resto
	check_ExchGRTRecovery	=13,        //Support Exchange GRT restore.
	check_ProtectHyperVM	=14,		//protect HyperV VM
	check_WithBLI			=15,		//with BLI license
	check_Encryption		=16,		//Support encryption.
	check_ManualExport		=17,		//Support export manually
	check_ScheduleExport	=19,		//Support schedule export
	check_D2D2D				=20,		//Support file copy to local dis
	check_Base				=21,		//BASE

}ARCFLASH_LIC_CHECK_TYPE, PARCFLASH_LIC_CHECK_TYPE;

typedef struct _D2D_CHKLIC_PARA
{
	DWORD size;				// the size of current structure.
	BOOL isFeature;			// whether check license related with feature: 
							// TRUE, only check the license with  componentType identifing the component type
							// FALSE, check the BASE license and OS licene, the result is identified by both isBaseValid and  isOSValid
	BOOL isBaseValid;		// whether the base license is valid. this parameter only valid when the isFeature is FALSE;
	BOOL isOSValid;			// whether the base OS is valid. this parameter only valid when the isFeature is FALSE;
	BOOL isFeatureValid;	// whether the base OS is valid. this parameter only valid when the isFeature is TRUE;
	DWORD componentType;	// the component check type, should be one value of ARCFLASH_LIC_CHECK_TYPE
	DWORD LicXID;			// [out] the license id
}D2D_CHKLIC_PARA, *PD2D_CHKLIC_PARA;

//////////////////////////////////////////////////////////////////////////
//Function Name: AFChkLicUnderEdge()
//				 try to allocate license from central license system
//Parameters: 
//				pvCHK[in/out] the information related with license checking
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////
DWORD AFChkLicUnderEdge(PD2D_CHKLIC_PARA pvCHK);


//////////////////////////////////////////////////////////////////////////
//Function Name: AFChkLicUnderEdgeEx()
//				 try to allocate license from central license system 
//Parameters: 
//				pvCHK[in/out] the information related with license checking
//				isById [in]  TRUE : the componentType is identifyed by component id
//							 FALSE: the componentType is identifyed by cARCFLASH_LIC_CHECK_TYPE
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////
DWORD AFChkLicUnderEdgeEx(PD2D_CHKLIC_PARA pvCHK, BOOL isById);

//////////////////////////////////////////////////////////////////////////
//Function Name: AFLicVerify()
//				 try to allocate license from central license system
//Parameters: 
//				eType[in] the licence type needed to be checked
//				isChkLcal[in] whether need check local license
//return value: 0, success; otherwise, failed.
//////////////////////////////////////////////////////////////////////////
DWORD AFLicVerify(ARCFLASH_LIC_CHECK_TYPE eType, BOOL isChkLocal = FALSE);

typedef struct _D2D_CHKLIC_PARA_PRI
{
	DWORD size;				// the size of current structure.
	BOOL isBaseValid;		// whether the base license is valid. this parameter only valid when the isFeature is FALSE;
	BOOL isOSValid;			// whether the base OS is valid. this parameter only valid when the isFeature is FALSE;
}D2D_CHKLIC_PARA_PRI, *PD2D_CHKLIC_PARA_PRI;
 
#define AFLIC_INFO_ID_ENCRYPTION						0x00000001
#define AFLIC_INFO_ID_D2D2D								0x00000002
#define AFLIC_INFO_ID_SCHEDULE_EXPORT					0x00000004
#define AFLIC_INFO_ID_BLI								0x00000008
#define AFLIC_INFO_ID_HYPERVM							0x00000010
#define AFLIC_INFO_ID_SQL								0x00000020
#define AFLIC_INFO_ID_EXCH_DB							0x00000040
#define AFLIC_INFO_ID_EXCH_GR							0x00000080
#define AFLIC_INFO_ID_BMR								0x00000100
#define AFLIC_INFO_ID_BMRALT							0x00000200
#define AFLIC_INFO_IS_TRIAL								0x00000400
#define AFLIC_INFO_IS_FREE_EDITION						0x00000800

#define AFLIC_INFO_ID_MASK								0x00000FFF
#define AFLIC_INFO_MASK_NO_FREE							0x000007FF

#define AFLIC_ERROR_TRACK_FILE			L"Configuration\\AFLic_Error_Track.dat"
#define AFLIC_ERROR_TRACK_LOCK			L"Global\\$AFLic_Error_Track_Lock$"

BOOL AFLicAddLicenseError(DWORD dwLicenseId);
BOOL AFLicDelLicenseError(DWORD dwLicenseId);
BOOL AFLicSetLicenseError(DWORD value);
BOOL AFLicGetLicenseError(DWORD &value);

//
//
//
#ifndef AFLIC_R16_WINDOWS_ENCRYPTION
#define	AFLIC_R16_WINDOWS_ENCRYPTION						0x00000000
#endif

#ifndef AFLIC_R16_WINDOWS_SIMPLE_VIRTUAL_CONVERTER
#define AFLIC_R16_WINDOWS_SIMPLE_VIRTUAL_CONVERTER			0x00000001
#endif

#ifndef AFLIC_R16_UNKNOWN
#define AFLIC_R16_UNKNOWN									0xffffffff
#endif

DWORD AFLicGetComponentName(DWORD dwComponentId, std::wstring& strComName);
DWORD AFLicGetComponentNameEx(DWORD dwComponentId, LPWSTR lpComName, DWORD& dwLength);