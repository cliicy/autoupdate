#ifndef _CHEYPROD_H
#define _CHEYPROD_H

#include <TCHAR.H>
#include <lic98.h>	// For CA license
#include <brstruct.h>
#include <idomain.h>

#ifdef __cplusplus		   	
  extern "C" {                     /* avoid name-mangling if used from C++ */
#endif /* __cplusplus */


#define COMPONENT_CODE_SIZE		5
#define NUMBER_OF_PRODLICCOUNT	67

typedef struct tagMapNameID
{
	DWORD	dwProdId;						//4
	TCHAR	szProdName[PRODUCT_NAME_SIZE];	//20 defined in idomain.h				
	USHORT	usASServer;						//
}MapNameID	;

typedef enum					// Return code for CA License
{
	ASLIC_AOK					= 0,	// all OK
	ASLIC_TERMINATE				= 1,	// Enterprise or Workgroup grace period expired
	ASLIC_NO_PRODSUBTYPE		= 2,	// Product subtype is not found
	ASLIC_NO_COMPCODE			= 3,	// Component code string was not found
	ASLIC_INVALID_ARGUMENTS		= 4,	// Invalid Arguments (ex: NULL Pointer)
	ASLIC_UNEXPECTED_EXCEPTION	= 5		// Unexpected exception
}ASLIC_RETURN;

typedef enum					// return code for checking the compatibility between products
{
	ASPROD_OK					 = 0,	// Products compatible
	ASPROD_INCOMPATIBLE_VERSION  = 1,	// Incompatible versions
	ASPROD_INCOMPATIBLE_TYPES	 = 2,	// Incompatible types
	ASPROD_INCOMPATIBLE_PLATFORMS= 3,	// incompatible platform
	ASPROD_FAIL_GET_INFO		 = 4	// Fail to get the information
}ASPROD_RETURN;

/* MC 18/04/2002: Changed the number of component codes from 3 to 4 to include the advanced edition */

typedef struct tagMapIDCompCode	// Mapping ID - Component Code
{
	DWORD	dwProdId;									//4
	TCHAR	szComponentCode[4][COMPONENT_CODE_SIZE];	
	// Increase the size in order to support more upgrade codes
	TCHAR	szUpgradeCompCode[64];						//  64 chars for upgrade string			
}MapIDCompCode;

typedef struct tagMapIDLicCount             // Mapping ID - License Count
{
	DWORD	dwProdId;
	int		nCount;				// license count
    TCHAR	szProdudtName[128];
    int		nMajor;
    int		nMinor;
}MapIDLicCount;

#if     defined(UNICODE)
#define CPGetProductLicCount		CPGetProductLicCountW
#define CPGetProductVersion			CPGetProductVersionW
#define CPca_license_count			CPca_license_countW
#define CPGetBaseOptionsPorductID	CPGetBaseOptionsPorductIDW
#define CPGetProductName			CPGetProductNameW
#define CPGetShortProductName		CPGetShortProductNameW
#define CPGetProductSubType			CPGetProductSubTypeW
#define CPca_license_query_cmo		CPca_license_query_cmoW	
#define CPca_is_installed			CPca_is_installedW
#define CPGetProductComponentCode	CPGetProductComponentCodeW
#define CPGetProductComponentCode2	CPGetProductComponentCode2W
#define CPca_license_check			CPca_license_checkW
#define CPca_license_check2			CPca_license_check2W
#define CPCheckLicense				CPCheckLicenseW
#define CPLicenseLogMsg				CPLicenseLogMsgW
#define CPASHasSameType				CPASHasSameTypeW
#define CPIsInPowerPackList			CPIsInPowerPackListW
#define CPGetProductId				CPGetProductIdW
#define CPGetProductName2			CPGetProductName2W
#define CPGetShortProductName2		CPGetShortProductName2W
#define CPGetProductDescription		CPGetProductDescriptionW
#define CPGetProductDescription2	CPGetProductDescription2W
#define	CPCheckASCompatibility		CPCheckASCompatibilityW
#define CPGetUpgradeComponentCode	CPGetUpgradeComponentCodeW
#define CPGetUpgradeComponentCode2	CPGetUpgradeComponentCode2W
#define	CPCheckASCompatibility2		CPCheckASCompatibility2W
#define	CPGetSBSComponentCode		CPGetSBSComponentCodeW

#else
#define CPGetProductLicCount		CPGetProductLicCountA
#define CPGetProductVersion			CPGetProductVersionA
#define CPca_license_count			CPca_license_countA
#define CPGetBaseOptionsPorductID	CPGetBaseOptionsPorductIDA
#define CPGetProductName			CPGetProductNameA
#define CPGetShortProductName		CPGetShortProductNameA
#define CPGetProductSubType			CPGetProductSubTypeA
#define CPca_license_query_cmo		CPca_license_query_cmoA
#define CPca_is_installed			CPca_is_installedA
#define CPGetProductComponentCode	CPGetProductComponentCodeA
#define CPGetProductComponentCode2	CPGetProductComponentCode2A
#define CPca_license_check			CPca_license_checkA
#define CPca_license_check2			CPca_license_check2A
#define CPCheckLicense				CPCheckLicenseA
#define CPLicenseLogMsg				CPLicenseLogMsgA
#define CPASHasSameType				CPASHasSameTypeA
#define CPIsInPowerPackList			CPIsInPowerPackListA
#define CPGetProductId				CPGetProductIdA
#define CPGetProductName2			CPGetProductName2A
#define CPGetShortProductName2		CPGetShortProductName2A
#define CPGetProductDescription		CPGetProductDescriptionA
#define CPGetProductDescription2	CPGetProductDescription2A
#define	CPCheckASCompatibility		CPCheckASCompatibilityA
#define CPGetUpgradeComponentCode	CPGetUpgradeComponentCodeA
#define CPGetUpgradeComponentCode2	CPGetUpgradeComponentCode2A
#define	CPCheckASCompatibility2		CPCheckASCompatibility2A
#define	CPGetSBSComponentCode		CPGetSBSComponentCodeA
#endif

#if     defined(UNICODE)
#define ProdIdName					ProdIdNameW
#define IDCompCode					IDCompCodeW
#define ProdLicCount				ProdLicCountW
#else
#define ProdIdName					ProdIdNameA
#define IDCompCode					IDCompCodeA
#define ProdLicCount				ProdLicCountA
#endif

BOOL WINAPI CPGetProductLicCount(MapIDLicCount *pProdLicCount, int* nNumProd);
BOOL WINAPI CPGetProductVersion(DWORD dwProdId, int* nMajorVer, int* nMinorVer);
DWORD WINAPI CPGetBaseOptionsPorductID(DWORD prodID);
DWORD WINAPI CPca_license_count(DWORD prodId);

//CPGetProductName return the long name of the product, knowing the product ID 
USHORT WINAPI CPGetProductName(DWORD prodID, PTCHAR name/*256 long*/);	// 
USHORT WINAPI CPGetProductName2(DWORD prodID, PTCHAR name/*256 long*/, BYTE	bSubType, DWORD dwProdVersion);
USHORT WINAPI CPGetShortProductName(DWORD prodID, PTCHAR name);
USHORT WINAPI CPGetShortProductName2(DWORD prodID, PTCHAR name, BYTE bSubType, DWORD dwProdVersion);
// CPGetProductDescription returns a string with component edition
USHORT WINAPI CPGetProductDescription(PTCHAR name, BYTE	 bSubType, DWORD dwProdVersion);
USHORT WINAPI CPGetProductDescription2(DWORD prodID, PTCHAR name, BYTE bSubType, DWORD dwProdVersion, LPTSTR SerialNumber);

USHORT WINAPI CPGetProductMapInfo(DWORD prodID, BYTE* lpData);

//CPGetNumberOfProducts retrieve the number of products;
//to retrieve the data from this table, call first CPGetNumberOfProducts, then
//allocate the necessary memory and then pass the pointer to CPLoadProductMap
DWORD WINAPI CPGetNumberOfProducts();
USHORT WINAPI CPLoadProductMap(BYTE* lpData, DWORD count);

long WINAPI CPIsTrialPeriodExpired(BOOL);
BOOL WINAPI CPVerifyARCserveLicense();

// Functions for CA License
// CPLicenseLogMsg log a message in ASLog
ULONG _cdecl CPLicenseLogMsg(ULONG ulID, ULONG ulFlags, LPTSTR szFmtIn, ...);

// CPGetProductSubType returns the subtype of a product
ASLIC_RETURN WINAPI CPGetProductSubType(DWORD prodID, BYTE* ProdSubType);

// CPGetProductComponentCode returns the Component Code of a product
ASLIC_RETURN WINAPI CPGetProductComponentCode(DWORD	prodID,LPTSTR  lpstrComponentCode);

// CPGetProductComponentCode2 returns the Component Code of a product
ASLIC_RETURN WINAPI CPGetProductComponentCode2(	DWORD	prodID,
												BYTE	ProdSubType,
												LPTSTR  lpstrComponentCode);
// CPca_license_check check the license of the product
ASLIC_RETURN WINAPI CPca_license_check(DWORD prodId); // Product ID

//CPca_license_check check license of CMO
ASLIC_RETURN WINAPI CPca_license_query_cmo();

BOOL WINAPI CPca_is_installed(DWORD ProdID);

// CPca_license_check2 check the license of the product
ASLIC_RETURN WINAPI CPca_license_check2(DWORD		 prodId,
										unsigned int wg_count,
										LIC_ERRFLAG	 process_error,
										LIC_RETURN*  pca_lic_error);

// CheckLicense check the license of the product
// This API is now only used by cheyprod.dll and About box
LIC_RETURN WINAPI CPCheckLicense(CHAR* szAscCompCode);

// Wrapper function for ca_license_log_usage
LIC_RETURN WINAPI CPca_license_log_usage(char *comp, int wg_count);

// IsInPowerPackList = TRUE if "szName" is in PowerPack List
BOOL WINAPI CPIsInPowerPackList(LPCTSTR szName);

// CPGetPowerPackNrOfServers returns Nr of PowerPack Servers
UINT WINAPI CPGetPowerPackNrOfServers();

// CPGetProductId = ERROR_SUCCESS if ID found
USHORT WINAPI CPGetProductId(LPTSTR lpstrComponentCode, DWORD* prodID, LPTSTR szName);

// Return the ID of resource string
UINT WINAPI _CPGetStringID(DWORD prodID, BOOL bLong);

// Return the ID of resource string for the relase 11.01 or above
UINT WINAPI _CPGetStringID2(DWORD prodID); 

// CPASHasSameType == TRUE if we have the same subtype
BOOL WINAPI CPASHasSameType(	LPTSTR szServerName,// Server Name
								DWORD dwProdId,		// ProductID
								BOOL bDefault);

// Returns TRUE if Cross-platform Management Option is installed on local machine
BOOL CPIsCPMEnabled();

// Return TRUE if the subtypes are compatible
BOOL CPSubtypeCompatible(BYTE bSt1, BYTE bSt2);

// Returns ASPROD_OK if Product1 is compatible with Product2
ASPROD_RETURN CPCheckCompatibility(ASProduct	asProd1, ASProduct asProd2);

// Returns ASPROD_OK if the local ARCserve and the remote ARCserve are compatible
ASPROD_RETURN WINAPI CPCheckASCompatibility(LPTSTR szServerName);

// Returns the ProductID and the product descriptor of the local machine 
DWORD	CPGetLocalASDescriptor(ASProduct* pAsProduct);

// Return TRUE if the ARCserve types are compatible
BOOL CPCompatibleWithLocalASType(int type);

// Returns the product descriptor of a product installed on local machine 
// pAsProduct can be null if you need only to find if the product is installed
BOOL	CPGetLocalProductDescriptor(DWORD prodID, ASProduct* pAsProduct);

// CPGetUpgradeComponentCode returns a list of upgrade component codes for prodID and the local subtype
ASLIC_RETURN WINAPI CPGetUpgradeComponentCode(DWORD	prodID, LPTSTR lpstrComponentCode, UINT uiBuffSize);

// CPGetProductComponentCode2 returns a list of upgrade component codes for prodID and a specified subtype
ASLIC_RETURN WINAPI CPGetUpgradeComponentCode2(	DWORD	prodID,
												BYTE	ProdSubType,
												LPTSTR  lpstrComponentCode,
												UINT	uiBuffSize);

// Returns ASPROD_OK if the local ARCserve and the remote ARCserve are compatrible
ASPROD_RETURN WINAPI CPCheckASCompatibility2(ASProduct asproduct, LPTSTR szServerName);

// Return TRUE if the ARCserve type is SBS version 
BOOL CPIsSBSVersion();

// Return TRUE if the ARCserve type is Standard version
BOOL CPIsSEVersion();

// Return TRUE if the ARCserve product subtype is changed.
BOOL ReadLicfileAndUpdateProductSubType();

// Return TRUE if the local machine is a SBS server.
BOOL IsMSSBSServer();

// retur TRUE if the local Base product is an AB product with Enterprise bundle
BOOL IsBaseABWithEnterpriseBundle();


// Return TRUE if AADR is installed and enabled.
BOOL WINAPI CPIsAADREnabled();

// Return 0 if successful and non-zero to indicate error condition.
DWORD WINAPI CPActivateAADR();
DWORD WINAPI CPDisableAADR();
ASLIC_RETURN WINAPI CPGetSBSComponentCode(DWORD	,BYTE , int, LPTSTR);

#ifdef __cplusplus
  }
#endif /* __cplusplus */

#endif //_CHEYPROD_H
