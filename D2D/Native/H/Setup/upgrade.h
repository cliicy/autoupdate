#ifndef UPGRADE_H
#define UPGRADE_H

#include "Product.h"

#define INSINFO_NEED_UPGRADE			(0x00000000)
#define INSINFO_FORBIDEN_UPGRAGE		(0x00000001)
#define INSINFO_NOT_INSTALL				(0x00000002)
#define INSINFO_ALREADY_INSTALLED		(0x00000003)

#define MAX_PRODUCT						100

#define REGBACKUP						_T("(BACKUP)")
#define UPGRADEUTIL_NAME				_T("UpgradeUtil.exe")

typedef CArray<CString,CString> CAString;
static const TCHAR BKREGVALUES[]= {_T("Version,Dll,ProdSubtype,BuildNumber")};

typedef struct REGKEY_INFO
{
	TCHAR szKeyName[MAX_PATH];
	TCHAR szKeyString[MAX_PATH];
	DWORD dwKeyWord;	
}  RegKeyInfo, *pRegKeyInfo;

#ifdef __cplusplus
extern "C" {
#endif
	LONG ASetIsAllowUpgrade(LPCTSTR lpszSetupInf, LPCTSTR lpszProductDescriptor, DWORD* dwProdVersionBuildNo, LPCTSTR lpszMachineName=NULL);

	LONG ASetIsAllowUpgradeEx(LPCTSTR lpszSetupInf, LPCTSTR lpszProductDescriptor, DWORD* dwProdVersionBuildNo, LPCTSTR lpszMachineName, REGSAM samDesired);

	void ASetRSOldProdRegKey();

	void ASetRSOldProdRegKeyEx(REGSAM samDesired);

	BOOL ASetUninstallOldProd(LPCTSTR lpszProductCode, BOOL bCommandLine=FALSE, LPCTSTR lpszPath=NULL);

	BOOL ASetBKOldProduct(LPCTSTR lpszInfPath, LPCTSTR lpszProdDescriptor, CString* lpszProdCode, LPCTSTR lpszMachineName=NULL);

	BOOL ASetBKOldProductEx(LPCTSTR lpszInfPath, LPCTSTR lpszProdDescriptor, CString* lpsProdCode, LPCTSTR lpszMachineName, REGSAM samDesired);

	BOOL ASetBKOldProductEx2(LPCTSTR lpszInfPath, LPCTSTR lpszProdDescriptor, CString* lpsProdCode, LPCTSTR lpszMachineName, REGSAM samSource, REGSAM samDest);

	void ASetCreateRSSubRegKeys(CString sSubRegKeyName, REGKEY_INFO* pRegKey, int nElement);

	DWORD ASetBetaUpgrade_Backup(LPCTSTR lpszSetupICF, LPCTSTR lpszCaller);

	DWORD ASetBetaUpgrade_Restore(LPCTSTR lpszSetupICF, LPCTSTR lpszCaller);

#ifdef __cplusplus
}
#endif

UINT BeforeUpgradeCLS(BOOL bSilent, LPCTSTR lpszSetupInf, LPCTSTR lpszProductDescriptor);

BOOL BackUpOldProdRegKey(CProduct* lpOldProduct, CProduct* lpNewProduct);

BOOL BackUpOldProdRegKeyEx(CProduct* lpOldProduct, CProduct* lpNewProduct, REGSAM samDesired);

BOOL BackUpOldProdRegKeyEx(CProduct* lpOldProduct, CProduct* lpNewProduct, REGSAM samSource, REGSAM samDest);

LONG IsAllowUpgrade(LPCTSTR lpszSetupInf, LPCTSTR lpszProdDescriptor, 
						CProduct** lpOldProduct, LPCTSTR lpszMachineName=NULL);

LONG IsAllowUpgradeEx(LPCTSTR lpszSetupInf, LPCTSTR lpszProdDescriptor,
						CProduct** lpOldProduct, LPCTSTR lpszMachineName, REGSAM samMask);

BOOL IsProductInstalled(CProduct* lpProduct, LPCTSTR lpszMachine=NULL);

BOOL IsProductInstalledEx(CProduct* lpProduct, LPCTSTR lpszMachine, REGSAM samMask);

LONG IsAllowProductUpgrade(CProduct* lpOldProduct, CProduct* lpNewProduct);

BOOL IsCompareVersionBuild(CProduct* lpOldProduct, CProduct* lpNewProduct);

BOOL IsProductCodeInUnInstall(LPCTSTR lpMachine,CProduct* lpProduct);

BOOL IsLastOldProduct(CProduct* lpProduct);

UINT IsInVersionRange(CProduct* lpOldProduct, CProduct* lpNewProduct);

DWORD WaitWithMessageLoop(HANDLE hEvent, DWORD dwTimeout /*= INFINITE*/);

void ShowWaitDialog(CString sMsg);

void DeleteBKRegValues(CProduct* lpOldProduct,LPTSTR lpRootKey);

void DeleteBKRegValuesEx(CProduct* lpOldProduct, LPTSTR lpRegKey, REGSAM samDesired);

BOOL FindOldInstallProduct(LPCTSTR lpszSetupInf, LPCTSTR lpszProdDescriptor, CProduct** lpOldProduct, LPCTSTR lpszMachineName=NULL);

BOOL IsSameProduct(CProduct* lpNewProduct, LPCTSTR szProdDescriptor,LPCTSTR lpszMachine=NULL);

BOOL IsSameProductEx(CProduct* lpNewProduct, LPCTSTR szProdDescriptor,LPCTSTR lpszMachine, REGSAM samMask);

BOOL CreateSubKeyValues(CString sSubRegKey, REGKEY_INFO* pRegKey, int nElement);

void WriteToTempLog(const TCHAR* pszFormat, ...);

WORD SV2W(CString sVerionNo);

void Split(LPTSTR lpszStr, TCHAR cSplit, CAString* caRet);

void GetLocalMachineHandle(LPCTSTR lpMachine, HKEY* hLocalMachine);

BOOL IsRemoteMachine(LPCTSTR lpMachine);

void GetInfFileName(LPCTSTR lpszFilePath, LPTSTR lpszInfFileName);

ULONG __cdecl SetcnvrtCopyRegSubTree(HKEY hRoot, LPCTSTR sSourcePath, LPCTSTR sDestinationPath, BOOL bDeleteSource);

LONG __cdecl SetcnvrtCopyRegSubTreeEx(HKEY hRoot, LPCTSTR sSourcePath, LPCTSTR sDestinationPath, BOOL bDeleteSource, REGSAM samDesired);

ULONG __cdecl SetcnvrtCopyRegSubTreeEx(HKEY hRoot, LPCTSTR sSourcePath, LPCTSTR sDestinationPath, BOOL bDeleteSource, REGSAM samSource, REGSAM samDest);

void GetHkeyString(HKEY hSourceKey, LPTSTR szKeyName);

BOOL RegRecurCopyTree(HKEY hSourceKeyParent, LPCTSTR sSourcePath, HKEY hDestnKeyParent, LPCTSTR sDestnPath, BOOL bHKCUser = TRUE);

BOOL RegRecurCopyTreeEx(HKEY hSourceKeyParent, LPCTSTR sSourcePath, HKEY hDestnKeyParent, LPCTSTR sDestnPath, BOOL bHKCUser, REGSAM samDesired);

BOOL RegRecurCopyTreeEx(HKEY hSourceKeyParent, LPCTSTR sSourcePath, HKEY hDestnKeyParent, LPCTSTR sDestnPath, BOOL bHKCUser, REGSAM samSource, REGSAM samDest);

BOOL DeleteSourceKeys(HKEY parentKeyHandle, LPCTSTR childKeyName);

BOOL DeleteSourceKeysEx(HKEY parentKeyHandle, LPCTSTR childKeyName, REGSAM samDesired);

DWORD ASetCall_UpgradeUtility(LPCTSTR lpszSetupICF, LPCTSTR lpszCaller, BOOL bBackup);

#endif 
