/////////////////////////////////////////////////////////////////////////////
// CheyReg.h		CCheyRegistryW Interface
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.


#ifndef _CHEYREG
#define _CHEYREG
#ifndef UNICODE
#define UNICODE
#endif 

#ifndef _UNICODE
#define _UNICODE
#endif

#include "settings.h"
#include <brstruct.h>
#include <SetupINF.h>

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CCheyRegistryW
#else
class __declspec(dllimport) CCheyRegistryW
#endif
{
public:
	static LPCTSTR OldCheyenneKey()		{ return m_sOldCheyenneKey; }
	static LPCTSTR OldARCserveKey()		{ return m_sOldARCserveKey; }
	static LPCTSTR OldARCserveBaseKey()	{ return m_sOldARCserveBaseKey; }
	static LPCTSTR OldProductsKey()		{ return m_sOldProductsKey; }
	static LPCTSTR OldAgentKey()		{ return m_sOldAgentKey; }	
protected:
    static const CString m_sSNMPExtensionAgentsKey;
    static const CString m_sEventLogKey;
    static const CString m_sEnvironmentKey;
	static       CString m_sProductsKey;
    static const CString m_sMicrosoftKey;
    static const CString m_sProfileListKey;
    static const CString m_sSoftwareKey;
    static       CString m_sCheyenneKey;
    static const CString m_sAlertKey;
    static const CString m_sInoculanKey;
    static       CString m_sARCserveKey;
    static       CString m_sARCserveBaseKey;
    static       CString m_sSCCKey;
    static       CString m_sAgentKey;
    static const CString m_sPathSubKey;
    static const CString m_sDomainSubKey;
    static const CString m_sComponentsSubKey;
    static const CString m_sUserSubKey;
    static const CString m_sDatabaseSubKey;
    static const CString m_sSNMPSubKey;
    static const CString m_sHome;
    static const CString m_sDatabase;
    static const CString m_sLog;
    static const CString m_sTemp;
    static const CString m_sManager;
    static const CString m_sServer;
    static const CString m_sAdmin;
    static const CString m_sSponsor;
    static const CString m_sCPManager;
    static const CString m_sDRWiz;
    static const CString m_sSANWiz;
    static const CString m_sRegisteredName;
    static const CString m_sRegisteredOrg;
	static const CString m_sChangerLicLevel;
    static const CString m_sDatabaseType;
    static const CString m_sPathname;
    static const CString m_sRegisteredOrganization;
    static const CString m_sRegisteredOwner;
	static const CString m_sOldComponentsSubKey;
	static const CString m_sOldPathSubKey;
	static		 CString m_sARCserve;
	static const CString m_sARCserveOld;
	static		 CString m_sProductARCserve;
	static const CString m_sProductARCserveOld;
    static const CString m_sALERT;
    static const CString m_sCheyenne;
    static const CString m_sProducts;
    static const CString m_sOldProducts;
    static const CString m_sCurrentBuildNumber;
    static const CString m_sCurrentBuild;
    static const CString m_sInstallDate;
    static const CString m_sControl;
    static const CString m_sEventLog;
    static const CString m_sRegisteredCompany;
    static const CString m_sRegisteredLocation;
    static const CString m_sEventMessageFile;
    static const CString m_sTypesSupported;
    static const CString m_sAlert;
    static const CString m_sPath;
    static const CString m_sSmartReaction;
    static const CString m_sDaemonSize;
    static const CString m_sPruningFlag;
    static const CString m_sDelayFlag;
    static const CString m_sPruningDays;
    static const CString m_sPruningTime;
	static const CString m_sOldARCserveBaseKeyARcserve;

    static const CString m_sShellExtApprovedKey;
    static const CString m_sARCserveShellExtension;
	static const CString m_sARCserveShellExtensionOld;
    static const CString m_sARCserveShellExtensionCLSID;
    static const CString m_sARCserveShellExtensionCLSIDKey;
    static const CString m_sARCserveShellExtensionInProcServerKey;
    static const CString m_sThreadingModel;
    static const CString m_sDrivePropertySheetHandler;
    static const CString m_sDriveMenuHandler;
    static const CString m_sDirMenuHandler;
    static const CString m_sAllMenuHandler;
    static const CString m_sARCserveScriptExtension;
    static const CString m_sARCserveScriptFile;
    static const CString m_sARCserveScriptFileShellExtension;
    static const CString m_sARCserveScriptDefaultIcon;
    static const CString m_sARCserveScriptIconHandler;
    static const CString m_sARCserveScriptPropertySheetHandler;
    static const CString m_sARCserveScriptMenuHandler;
    static const CString m_sARCserveScriptPropertySheetHandlerCLSID;
    static const CString m_sARCserveScriptMenuHandlerCLSID;
	static const CString m_sOldArcserveKey;
	static const CString m_sOldARCserveITKey;
	static const CString m_sOldArcserveITProductsKey;
	static const CString m_sOldARCserveITAgentKey;

	static const CString m_sProductBEB;
	static const CString m_sBEBKey;
	static const CString m_sBEBBaseKey;
	static const CString m_sBEBProductsKey;
	static const CString m_sBEBAgentKey;

	static const CString m_sProductBAB;
	static const CString m_sBABKey;
	static const CString m_sBABBaseKey;
	static const CString m_sBABProductsKey;
	static const CString m_sBABAgentKey;

    static const CString m_sARCserveUninstallKey;
    static const CString m_sUninstallKey;
    static const CString m_sDisplayName;
    static const CString m_sUninstallString;
    static const CString m_sSharedDLLKey;
    static const CString m_sProfileImagePath;

	static const CString m_sOldCheyenneKey;
	static const CString m_sOldARCserveKey;
	static const CString m_sOldARCserveBaseKey;
	static const CString m_sOldProductsKey;
	static const CString m_sOldAgentKey;
	static		 CString m_sBrightStorProdKey;

	static const CString m_sProductBAB115;
	static const CString m_sBAB115Key;
	static const CString m_sBAB115BaseKey;
	static const CString m_sBAB115ProductsKey;
	static const CString m_sBAB115AgentKey;	

	static const CString m_sArcserveITRootKey;
	static const CString m_sCheetahRootKey;
	static const CString m_sBAB115RootKey;
	static const CString m_sBABRootKey;

    REGSAM    m_samDesired;
    CString   m_sCompany; //registered organization
    CString   m_sName;    //registered owner
    CString   m_sARCserveHomeDir;
    CString   m_sARCserveDBDir;
    CString   m_sARCserveLogDir;
    CString   m_sARCserveTempDir;
    CString   m_sAlertHomeDir;
	CString   m_sClientAgentHomeDir;
	DWORD	  m_dwARCserveUserLevel;
	DWORD	  m_dwARCserveProdCode;
    WORD      m_wARCserveVersion;
	WORD	  m_wARCserveBuild;
	DWORD	  m_dwProdCode;
    WORD      m_wVersionNumber;
	WORD	  m_wBuildNumber;
    WORD      m_wAlertBuild;
    CSettings m_Settings;
	HKEY	  m_hHKEYLocalMachine;
	ASProduct m_ASProductInfo;
	char	  m_cARCserveSubType;
	char	  m_cSubType;
	
	CString   m_sMachineName; // please do not change!!!!

    void Construct(LPWSTR lpMachineName, BOOL bReadMode);


public:
    CCheyRegistryW(BOOL		bAutoRead = TRUE, 
				  LPWSTR	lpLocalMachine = NULL);
    CCheyRegistryW(CSettings	&rSettings,
				  BOOL		bAutoWrite = TRUE,
				  LPWSTR	lpLocalMachine = NULL);
	~CCheyRegistryW();

    BOOL    Read();
    LPCTSTR GetRegisteredOwner();
    LPCTSTR GetRegisteredOrganization();
    LPCTSTR GetARCserveHomeDir();
    LPCTSTR GetARCserveDBDir();
    LPCTSTR GetARCserveLogDir();
    LPCTSTR GetARCserveTempDir();
    LPCTSTR GetAlertHomeDir();
	LPCTSTR GetClientAgentHomeDir();
	DWORD   GetARCserveUserLevel();
	DWORD   GetARCserveProductCode();
    WORD    GetARCserveVersionNumber();
	WORD	GetARCserveBuildNumber();
	char	GetARCserveSubType();
    DWORD	GetAlertBuildNumber();
	ASProduct	GetARCserveProductInfo();
	BOOL	IsARCserveInstalled();
	static  LPCTSTR GetARCserveKey() { return m_sARCserveKey; }
	static  LPCTSTR GetARCserveBaseKey() { return m_sARCserveBaseKey; }

	DWORD	GetProductCode(LPCTSTR szProductName);
    WORD	GetVersionNumber(LPCTSTR szProductName);
	WORD	GetBuildNumber(LPCTSTR szProductName);
    WORD	GetVersionNumber_SCC(LPCTSTR szProductName);
	WORD	GetBuildNumber_SCC(LPCTSTR szProductName);
	char	GetSubType(LPCTSTR szProductName);

    BOOL    Write();
    BOOL    SetARCserveUser();
    BOOL    SetARCserveCompany();
	BOOL    SetChangerLicLevel();
    BOOL    SetARCserveHomeDir();
    BOOL    SetARCserveDBDir();
    BOOL    SetARCserveLogDir();
    BOOL    SetARCserveTempDir();
    BOOL    SetARCserveManager();
    BOOL    SetARCserveServer();
    BOOL    SetARCserveAdmin();
	BOOL	SetCAITProduct();
    BOOL    SetARCserveEnvironmentVariables();
    BOOL    SetARCserveShellExtension();
    BOOL    SetARCserveDatabase();
    BOOL    CreateARCserveSNMPKey();
    BOOL    DeleteARCserveSNMPKey();
    BOOL    CreateARCserveUninstallKey();
    BOOL    SetAlertCurrentBuild();
    BOOL    SetAlertCurrentBuildNumber();
    BOOL    SetAlertInstallDate();
    BOOL    SetAlertRegisteredOwner();
    BOOL    SetAlertRegisteredOrganization();
    BOOL    SetAlertHomeDir();
    BOOL    SetAlertLogDir();
    BOOL    SetAlertControlDir();
    BOOL    SetAlertEventLogDir();
    BOOL    SetAlertCompany();
    BOOL    SetAlertLocation();
    BOOL    SetAlertEventLog();
	BOOL    SetClientAgentHomeDir();

    BOOL    Delete();
    void    DeleteARCserveUninstallKey();
    BOOL    DeleteARCserveShellExtension();
    void    DeleteARCserveBaseBranch();
    void    DeleteARCserveUserBranches();
    void    DeleteAlertBranch();
	void	DeleteCAITProductBranch();
    void    DeleteCheyenneUserBranches(BOOL bDeleteSubKeys = FALSE);

    static void DeleteCheyenneBranch(BOOL bDeleteSubKeys = FALSE);
    static void DeleteARCserveBranch(BOOL bDeleteSubKeys = FALSE);
    static BOOL GetRegisteredOrganization(LPTSTR lpCompany, LPDWORD lpdwSize, HKEY hRemoteKey = NULL);
    static BOOL GetRegisteredOwner(LPTSTR lpOwner, LPDWORD lpdwSize, HKEY hRemoteKey = NULL);
    static BOOL GetARCserveHomeDir(LPTSTR lpPath, LPDWORD lpdwSize, HKEY hRemoteKey = NULL);
    static BOOL GetARCserveDBDir(LPTSTR lpPath, LPDWORD lpdwSize, HKEY hRemoteKey = NULL);
    static BOOL GetARCserveLogDir(LPTSTR lpPath, LPDWORD lpdwSize, HKEY hRemoteKey = NULL);
    static BOOL GetARCserveTempDir(LPTSTR lpPath, LPDWORD lpdwSize, HKEY hRemoteKey = NULL);
	static BOOL GetARCserveProductInfo(ASProduct *pProduct, HKEY hRemoteKey = NULL);
	BOOL GetProductInfo(LPCTSTR szProductName, ASProduct *pProduct, HKEY hRemoteKey = NULL);
	BOOL GetProductInfoW(LPCWSTR szProductName, ASProduct *pProduct, HKEY hRemoteKey = NULL);
	BOOL GetProductInfo_SCC(LPCTSTR szProductName, ASProduct *pProduct, HKEY hRemoteKey = NULL);
    static BOOL GetAlertHomeDir(LPTSTR lpPath, LPDWORD lpdwSize, HKEY hRemoteKey = NULL);
    static BOOL GetAlertBuildNumber(LPWORD lpwBuild, HKEY hRemoteKey = NULL);
    static BOOL GetAgentHomeDir(LPTSTR lpPath, LPDWORD lpdwSize, HKEY hRemoteKey = NULL);
    static BOOL SetARCserveUser(LPCTSTR lpOwner);
    static BOOL SetARCserveCompany(LPCTSTR lpCompany);
	static BOOL SetChangerLicLevel(DWORD dwChangerLicLevel);
    static BOOL SetARCserveHomeDir(LPCTSTR lpPath);
    static BOOL SetARCserveDBDir(LPCTSTR lpPath);
    static BOOL SetARCserveLogDir(LPCTSTR lpPath);
    static BOOL SetARCserveTempDir(LPCTSTR lpPath);
    static BOOL SetARCserveManager(BOOL bInstalled);
    static BOOL SetARCserveServer(BOOL bInstalled);
    static BOOL SetARCserveAdmin(BOOL bInstalled);
    static BOOL SetARCserveShellExtension(LPCTSTR lpPath);
    static void DeleteARCserveShellExtension(LPCTSTR lpPath);
    static BOOL SetARCserveDatabase(BOOL bRAIMA);
    static BOOL SetARCserveSNMPPath(LPCTSTR lpPath);
    static BOOL SetAlertCurrentBuild(LPCTSTR lpCurrentBuild);
    static BOOL SetAlertCurrentBuildNumber(LPCTSTR lpCurrentBuildNumber);
    static BOOL SetAlertInstallDate(DWORD dwInstallDate);
    static BOOL SetAlertRegisteredOwner(LPCTSTR lpOwner);
    static BOOL SetAlertRegisteredOrganization(LPCTSTR lpCompany);
    static BOOL SetAlertHomeDir(LPCTSTR lpPath);
    static BOOL SetAlertLogDir(LPCTSTR lpPath);
    static BOOL SetAlertControlDir(LPCTSTR lpPath);
    static BOOL SetAlertEventLogDir(LPCTSTR lpPath);
    static BOOL SetAlertCompany(LPCTSTR lpCompany);
    static BOOL SetAlertLocation(LPCTSTR lpLocation);
    static BOOL SetAgentHomeDir(LPCTSTR lpPath);
    static BOOL SetEnvironmentVariable(LPCTSTR lpVarName, LPCTSTR lpValue);
	static BOOL GetEnvironmentVariable(LPCTSTR lpVarName, LPTSTR lpValue, LPDWORD lpdwSize);
    static BOOL SetEventLogApplication(LPCTSTR lpApplication, LPCTSTR lpExecutable, DWORD dwType);
    static BOOL SetSNMPRegistry(LPCTSTR lpKey);
    static BOOL DeleteSNMPRegistry(LPCTSTR lpKey);
    static BOOL DeleteEnvironmentVariable(LPCTSTR lpVarName);

    static BOOL  CreateUninstallKey(LPCTSTR lpKey, LPCTSTR lpDisplayName, LPCTSTR lpUninstallPath);
    static void  DeleteUninstallKey(LPCTSTR lpKey);

	static DWORD GetOripinARCserve_LocalHomeDir(LPTSTR lpPath, DWORD& dwSize);

	BOOL  IsProductInstalled(LPCTSTR lpProductName, HKEY hRemoteKey);
	BOOL  IsProductInstalled_SCC(LPCTSTR lpProductName, HKEY hRemoteKey);
    BOOL  IsProductInstalled(LPCTSTR lpProductName);

	BOOL  IsStandAloneServerInstalled( BOOL& bInstalled );
	// { #061212001 yuani01
	BOOL  IsPrimaryServerInstalled( BOOL& bInstalled );
	// } #061212001 yuani01
    
	//caowe01: 2007-04-28 for upgrade
	BOOL CCheyRegistryW::GetOldProductRootKey_ForUpg(LPTSTR lpBrightStorRootKey, HKEY hHKEYLocalMachine = NULL);
	BOOL  CCheyRegistryW::IsProductInstalledWithoutBase(HKEY hHKEYLocalMachine = NULL);
	BOOL CCheyRegistryW::IsBetaUpgProductInstalledWithoutBase(HKEY hHKEYLocalMachine /*= NULL*/);

	//caowe01: end

	static BOOL  RegisterProduct(LPCTSTR lpProductName, ASProduct *pProductInfo);
    static void  UnregisterProduct(LPCTSTR lpProductName);
	static BOOL  RegisterProduct_SCC(LPCTSTR lpProductName, ASProduct *pProductInfo);
    static void  UnregisterProduct_SCC(LPCTSTR lpProductName);
    static BOOL  IncrementSharedDLLCount(LPCTSTR lpSharedDLL, BOOL bReinstall = FALSE);
    static DWORD DecrementSharedDLLCount(LPCTSTR lpSharedDLL);
    static BOOL  InoculanExists(HKEY hRemoteKey = NULL);
    static BOOL  DeleteSubKeys(HKEY hKeyParent, LPCTSTR lpKey);

    static BOOL IsARCserveServerInstalled(HKEY hRemoteKey);
	BOOL IsVcRedisInstalled();

	BOOL IsARCserveServerInstalled();

    static BOOL IsARCserveManagerInstalled(HKEY hRemoteKey);
	BOOL IsARCserveManagerInstalled();

    static BOOL IsARCserveAdminInstalled(HKEY hRemoteKey);
	BOOL IsARCserveAdminInstalled();

    static BOOL IsARCserveSponsorInstalled(HKEY hRemoteKey);
	BOOL IsARCserveSponsorInstalled();

	static BOOL IsARCserveFeatureSponsorInstalled(HKEY hRemoteKey);
	BOOL IsARCserveFeatureSponsorInstalled();

    static BOOL IsARCserveCPMInstalled(HKEY hRemoteKey);
	BOOL IsARCserveCPMInstalled();

    static BOOL IsDRWizInstalled(HKEY hRemoteKey);
	BOOL IsDRWizInstalled();

    static BOOL IsSANWizInstalled(HKEY hRemoteKey);
	BOOL IsSANWizInstalled();

    static BOOL AddCheyenneKey(CString sSubKey);
    static void DeleteCheyenneKey(CString sSubKey);
    static BOOL SetCheyenneKeyValue(CString sSubKey, CString sValue, DWORD dwType, LPBYTE lpData, DWORD cbData);
    static BOOL QueryCheyenneKeyValue(CString sSubKey, CString sValue, LPDWORD lpType, LPBYTE lpData, LPDWORD lpcbData, HKEY hRemoteKey = NULL);

    static BOOL AddARCserveKey(CString sSubKey);
    static void DeleteARCserveKey(CString sSubKey);
    static BOOL SetARCserveKeyValue(CString sSubKey, CString sValue, DWORD dwType, LPBYTE lpData, DWORD cbData);
    static BOOL QueryARCserveKeyValue(CString sSubKey, CString sValue, LPDWORD lpType, LPBYTE lpData, LPDWORD lpcbData, HKEY hRemoteKey = NULL);
    
	static BOOL SetBackupDaemonBufferSize(DWORD dwValue);
    static BOOL SetDatabasePruning(DWORD dwValue);
    static BOOL SetDelayPruning(DWORD dwValue);
    static BOOL SetPruningDays(DWORD dwValue);
    static BOOL SetPruningTime(DWORD dwValue);

	static void	DeleteSmartReactionBranch();

//Add by MichaelX for BrighStor EB&AB in 03/18/2002
	static LPCTSTR GetBrightStorProdName(){ return m_sProductARCserve; }
	static LPCTSTR GetBrightStorProdKey(){ return m_sARCserve; }
	static LPCTSTR GetBrightStorProdRootKey(){ return m_sBrightStorProdKey; }

	static LPCTSTR GetArcserveITRootKey(){ return m_sArcserveITRootKey; }
	static LPCTSTR GetCheetahRootKey(){ return m_sCheetahRootKey; }
	static LPCTSTR GetBABRootKey(){ return m_sBABRootKey; }
	static LPCTSTR GetBAB115RootKey(){ return m_sBAB115RootKey; }//
	static CString Wow6432Node(CString sRegKey);

protected:
    static BOOL IsARCserveComponentInstalled(LPCTSTR lpComponent, HKEY hRemoteKey = NULL);		
//===============================================
// New add functions
//===============================================
public:
	// check whether there is another kind of agent exists.
	static BOOL ExistOther_DBAgents(CString sAgentKey, BOOL bType,CString &strPath);

	// Get agent home directory
	static BOOL GetDBAgentHomeDir(CString sAgentKey,LPTSTR lpPath,LPDWORD lpdwSize, BOOL bType = TRUE,HKEY hHKEYLocalMachine = NULL);

	// Delete agent subkey
	static void DeleteAgentSubKeys(CString sAgentKey, BOOL bType);

	// Delete cheyenne key
	static void DeleteOldARCserveKey(CString sSubKey);
	static void UpdateRPCImagePath(CString strPath);

	void  CreateOldProdDscrpt();
	void  DeleteOldProdDscrpt();

	BOOL   AllowProductUpdate(LPCTSTR szProductName, LPCTSTR szKeyName);
	BOOL   AllowProductUpdate (LPCTSTR szProductName, LPCTSTR szKeyName, CSetupInf SetupInf);
	static BOOL SetARCserveDatabase(DWORD nDBType);

	LPCTSTR GetARCserveUserName();
	LPCTSTR GetARCserveOrganization();

	static	BOOL GetARCserveUserName(LPTSTR lpUser, LPDWORD lpdwSize, HKEY hHKEYLocalMachine = NULL);
	static	BOOL GetARCserveOrganization(LPTSTR lpOrg, LPDWORD lpdwSize, HKEY hHKEYLocalMachine = NULL);

	// Add in Asnt v7.0
	BOOL	DeleteDynamicKey();
	void	UnregisterCheyenneProduct(LPCTSTR lpProductName);
	BOOL	SavePerviousRegisterProduct(LPCTSTR lpProductName, WORD wMajorVersion, WORD wMinorVersion);
	BOOL	IsProductInstalled(LPCTSTR lpProductName, WORD wMajorVersion, WORD wMinorVersion);

	// { #060712001 yuani01
	static BOOL AddAccessPermissionOnRegistryKey( HKEY hKey, const BOOL bIs64OS );
	// } #060712001 yuani01

	// { #060720001 yuani01
	static BOOL GetBuiltinDomainAccessMaskOnKey( HKEY hKey, const DWORD dwSubAuthority1, DWORD& dwMask );
	// } #060720001 yuani01

	/////////////////////////////////////////////////////////////
// Begin: new add function for installation of SQL2005 Express
// yuani01 07/24/2006
/////////////////////////////////////////////////////////////
public:
	enum MachineCPUType
	{
		MACHINE_CPU_TYPE_UNKNOW,//0
		MACHINE_CPU_TYPE_X86,	//1
		MACHINE_CPU_TYPE_AMD64,	//2
		MACHINE_CPU_TYPE_EM64T,	//3
		MACHINE_CPU_TYPE_IA64	//4
    };

	// check whether the machine allow to install the SQL2005Express
	BOOL IsAllowInstallSQL2005E( int& nOSVerMajor, int& nOSVerMinor );

	// get the CUP type of the machine
	UINT GetCPUTypeOnMachine();

	// determine whether the version of Internet Explorer above 6.0sp1
	BOOL IsIE6SP1orAboveInstalled();

	// check whether the OS allow to install the SQL2005 Express 
	BOOL IsOS_AllowInstallSQL2005E( int& nOSVerMajor, int& nOSVerMinor );

	// check whether the .NET Framework2.0 installed
	BOOL IsNETFramework20Installed( BOOL& bBetaVersion );

	// get the windows installer version number
	BOOL GetWindowsInstallerVer( int& nMajor, int& nMinor );

	// determine whether the SQL2005 Express was already installed
	BOOL IsSQL2005Installed( BOOL& bBetaVersion );

	// get the MDAC component version of the machine
	BOOL GetMDACVersion( int& nMajor, int& nMinor );

	// { #070424001 yuani
	// get the setupsql.exe return code from registry
	DWORD GetSetupSQLReturnCodeFromReg( DWORD& dwRet );

	//get the BConfig.exe retrun code from registry
	DWORD GetBConfigReturnCodeFromReg( DWORD& dwRet );

	//get the portmapper warning code from registry
	DWORD GetPortMapperWarningCodeFromReg( DWORD& dwRet );

	//get the server type code from registry
	DWORD GetServerTypeFromReg( DWORD& dwType );

	//set the setupsql.exe return code to registry
	BOOL SetSetupSQLReturnCodeToReg( const DWORD dwValue );

	//set the BConfig.exe retrun code to registry
	BOOL SetBConfigReturnCodeToReg( const DWORD dwValue );

	//set the portmapper warning code to registry
	BOOL SetPortMapperWarningCodeToReg( const DWORD dwValue );

	//Set the server type code to registry
	BOOL SetServerTypeToReg( const DWORD dwType );

	// } #070424001 yuani01

	// { #070514001 yuani01
	BOOL  SetPreviousASDBTypeToReg( const DWORD dwType );
	DWORD GetPreviousASDBTypeFromReg( DWORD& dwType );
	DWORD GetARCserveDatabase(DWORD& nDBType);
	// } #070514001 yuani01

	// { #070622001 yuani01
	DWORD GetOripinServerType();
	// } #070622001 yuani01

/////////////////////////////////////////////////////////////
// End: new add function for installation of SQL2005 Express
/////////////////////////////////////////////////////////////

};

#ifdef __cplusplus
extern "C" {
#endif  /* __cplusplus */

BOOL			GetAlertPathW							(LPTSTR lpPath, LPDWORD lpdwSize, HKEY hHKEYLocalMachine /*= NULL*/);
typedef	BOOL	(*PFNGETALERTPATH)						(LPTSTR, LPDWORD, HKEY);

DWORD	WINAPI	ASetGetProgramFilesPath					(LPTSTR lpPath, LPDWORD lpdwSize, HKEY hHKEYLocalMachine = NULL);
DWORD	WINAPI	ASetGetCommonProgramFilesPath			(LPTSTR lpPath, LPDWORD lpdwSize, HKEY hHKEYLocalMachine = NULL);
typedef	DWORD	(WINAPI *PFNASetGetProgramFilesPath)	(LPTSTR, LPDWORD, HKEY);

BOOL WINAPI IsMsiVersionOK(LPTSTR lpMachineName = NULL);

BOOL WINAPI IsSBS2000();
BOOL IsRemote_64Bit_System( LPTSTR lpMachine );
#ifdef __cplusplus
}
#endif

#endif //_CHEYREG

