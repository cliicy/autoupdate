/////////////////////////////////////////////////////////////////////////////
// Service.h		CService Interface
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.
#ifndef _SERVICE
#define _SERVICE

#include <winsvc.h>

#ifndef _WIN95AGENT

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CService
#else
class __declspec(dllimport) CService
#endif
{
private:
    CString m_sName;
    CString m_sDisplayName;
    CString m_sFileName;
    DWORD   m_dwServiceType;
    CString m_sGroup;

    static const CString m_sServicesKey;
    static const CString m_sDependOnService;

    static BOOL StartService(SC_HANDLE schService, BOOL bBlockOperation);
    static BOOL StopService(SC_HANDLE schService, LPCTSTR lpszServiceName, BOOL bBlockOperation, BOOL bStopDependents);
    static BOOL StopDependentServices(LPCTSTR lpszServiceName, BOOL bBlockOperation);

    void Construct(LPCTSTR lpszServiceName, LPCTSTR lpszFileName, LPCTSTR lpszDisplayName = NULL, DWORD dwServiceType = SERVICE_WIN32_OWN_PROCESS|SERVICE_INTERACTIVE_PROCESS, LPCTSTR lpszGroup = NULL);

public:
    enum ServiceType{
        ASJobEngine = 0,
        ASTapeEngine = 1,
        ASDBEngine = 2,
        ASMsgEngine = 3,
        Alert = 4,
		ASDiscovery = 5,
    };

    // Constructor
    CService(ServiceType service);
    CService(LPCTSTR lpszServiceName, LPCTSTR lpszFileName, LPCTSTR lpszDisplayName = NULL, DWORD dwServiceType = SERVICE_WIN32_OWN_PROCESS|SERVICE_INTERACTIVE_PROCESS, LPCTSTR lpszGroup = NULL);


    // Attributes
    LPCTSTR Name()          {return m_sName;}
    BOOL IsStarted()        {return IsStarted(m_sName);}
    BOOL Exists()           {return ServiceExists(m_sName);}
	BOOL IsStopped()		{return IsStopped(m_sName);}

    BOOL BackupServiceStatus(LPCTSTR lpszKey)
							{return BackupServiceStatus(lpszKey, m_sName);}
    BOOL RestoreServiceStatus(LPCTSTR lpszKey)
							{return RestoreServiceStatus(lpszKey, m_sName, m_sDisplayName);}

    // Operations
    BOOL InstallService(LPCTSTR lpszPath, DWORD dwStartType = SERVICE_AUTO_START, LPCTSTR lpszAccountName = NULL, LPCTSTR lpszAccountPassword = NULL);
    BOOL ChangeService(LPCTSTR lpszPath, DWORD dwStartType = SERVICE_AUTO_START, LPCTSTR lpszAccountName = NULL, LPCTSTR lpszAccountPassword = NULL);
    BOOL StartService(BOOL bBlockOperation = TRUE)     {return StartService(m_sName, bBlockOperation);}
    BOOL StopService(BOOL bBlockOperation = TRUE, BOOL bStopDependents = FALSE) 
                                                       {return StopService(m_sName, bBlockOperation, bStopDependents);}
    BOOL RemoveService()                               {return RemoveService(m_sName);}

	// { #060712001 yuani01
	BOOL AddAccessPermission()
	{
		return AddAccessPermission( m_sName );
	}
	static BOOL AddAccessPermission( LPCTSTR lpszServiceName );
	// } #060712001 yuani01


    static BOOL IsStarted(LPCTSTR lpszServiceName);
	static BOOL IsStopped(LPCTSTR lpszServiceName);
    static BOOL StopService(LPCTSTR lpszServiceName, BOOL bBlockOperation = TRUE, BOOL bStopDependents = FALSE);
    static BOOL RemoveService(LPCTSTR lpszServiceName);
    static BOOL ServiceExists(LPCTSTR lpszServiceName);
	static BOOL ServiceExists(LPCTSTR lpMachine, LPCTSTR lpszServiceName);
    static BOOL StartService(LPCTSTR lpszServiceName, BOOL bBlockOperation = TRUE);
    static BOOL StartDependentServices(LPCTSTR lpszServiceName, BOOL bBlockOperation = TRUE);
    static BOOL InstallService(LPCTSTR lpszServiceName, LPCTSTR lpszDisplayName, LPCTSTR lpszServiceExe, DWORD dwServiceType, BOOL dwStartType, LPCTSTR lpszGroup = NULL, LPCTSTR lpszzDependencies = NULL, LPCTSTR lpszAccountName = NULL, LPCTSTR lpszAccountPassword = NULL);
    static BOOL ChangeService(LPCTSTR lpszServiceName, LPCTSTR lpszDisplayName, LPCTSTR lpszServiceExe, DWORD dwServiceType, BOOL dwStartType, LPCTSTR lpszGroup = NULL, LPCTSTR lpszzDependencies = NULL, LPCTSTR lpszAccountName = NULL, LPCTSTR lpszAccountPassword = NULL);

    static BOOL BackupServiceStatus(LPCTSTR lpszKey, LPCTSTR lpszServiceName);
    static BOOL RestoreServiceStatus(LPCTSTR lpszKey, LPCTSTR lpszServiceName, LPCTSTR lpszDisplayName);

	static BOOL ChangeSvcFailureActions( LPCTSTR lpszServiceName, UINT uFirstAction, UINT uSecondAction, UINT uThirdAction, DWORD dwDelay );

	// { #070228001 yuani01
	static BOOL ChangeSvcStartType( LPCTSTR lpszServiceName, const DWORD dwStartType );
	// } #070228001 yuani01

	//get the service file name(including full path), e.g. c:\Program Files\CA\ARCserve Backup\msgeng.exe
	static BOOL GetServiceFileName(LPCTSTR lpszServiceName,LPTSTR lpServiceFile,DWORD ccBuffer);

	//lpszServiceFile must be full path
	static BOOL IsWindowsFirewallStarted();
	static BOOL IsWindowsFirewallAdded(LPCTSTR lpszServiceFile,LPCTSTR lpszServiceName,LPTSTR lpMsg,DWORD ccBuffer);
	static BOOL WindowsFirewallAdd(LPCTSTR lpszServiceFile,LPCTSTR lpszServiceName,long lProfileTypesBitmask,LPTSTR lpMsg,DWORD ccBuffer);
	static BOOL WindowsFirewallRemove(LPCTSTR lpszServiceFile,LPCTSTR lpszServiceName,LPTSTR lpMsg,DWORD ccBuffer);
	static BOOL WFCOMGetCurrentProfile(long& CurrentProfilesType,LPTSTR lpMsg,DWORD ccBuffer);
	static BOOL IsWFCOMPermission();

	//yauni01 20090518
	static BOOL CheckWMISvcStatus( DWORD* pdwStartType );
};

#endif	// _WIN95AGENT

#ifdef _SETUPCLS_DLL 
#define SERVICE_EXPORT_IMPORT_PREFIX __declspec(dllexport) 
#else
#define SERVICE_EXPORT_IMPORT_PREFIX __declspec(dllimport) 
#endif

#ifdef __cplusplus
extern "C" {
#endif  /* __cplusplus */

BOOL SERVICE_EXPORT_IMPORT_PREFIX WINAPI StopLicenseCheck();
BOOL SERVICE_EXPORT_IMPORT_PREFIX WINAPI StartLicenseCheck();

//
BOOL SERVICE_EXPORT_IMPORT_PREFIX WINAPI InstallServiceByCmdLine( LPCTSTR lpszServiceCmd );
BOOL SERVICE_EXPORT_IMPORT_PREFIX WINAPI RemoveServiceByCmdLine( LPCTSTR lpszServiceCmd );
BOOL SERVICE_EXPORT_IMPORT_PREFIX WINAPI StartServiceByCmdLine( LPCTSTR lpszServiceCmd );
BOOL SERVICE_EXPORT_IMPORT_PREFIX WINAPI StopServiceByCmdLine( LPCTSTR lpszServiceCmd );

// { #060810001 yuani01
BOOL SERVICE_EXPORT_IMPORT_PREFIX WINAPI AddAccessPermissionOnFolder( LPCTSTR lpFolderPath );
BOOL SetPrivilege( HANDLE hToken, LPCTSTR lpszPrivilege, BOOL bEnablePrivilege );
BOOL SERVICE_EXPORT_IMPORT_PREFIX WINAPI GetBuiltinDomainAccessMaskOnFolder( LPCTSTR lpFolderPath, const DWORD dwSubAuthority1, DWORD& dwMask );
// } #060810001 yuani01

//added by fanzh01 on April 13,2007
BOOL AddAccessPermissionOnFolderEx( LPCTSTR lpFolderPath );
//end of April 13,2007

BOOL WINAPI ChangeServiceInfo(SC_HANDLE scmgr, LPTSTR name, LPTSTR dispname, LPTSTR desc);

#ifdef __cplusplus
}
#endif

#endif	//_SERVICE