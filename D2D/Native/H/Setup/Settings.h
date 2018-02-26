/////////////////////////////////////////////////////////////////////////////
// Settings.h		CSettings Interface
// Copyright (C) 1998 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.

#ifndef _SETTINGS
#define _SETTINGS

#include <setupinfo.h>

BOOL __declspec(dllexport) TrimTailBackSlash(CString &sPath);
BOOL __declspec(dllexport) AppendBackSlash(CString &sPath);
BOOL __declspec(dllexport) AdjustAccessRights(LPTSTR lpszServer, LPTSTR lpszUser, LPTSTR lpszPassword, TCHAR *lpszFileName, TCHAR *lpszAccountName, DWORD dwAccessMask);

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CSettings
#else
class __declspec(dllimport) CSettings
#endif
{
private:
    // Setup information
    BOOL    m_bUninstallMode;
    CString m_sProfile;
    CString m_sTraceFile;
    CString m_sSourcePath;
    CString m_sBatchFile;
    CString m_sMIFFile;
    BOOL    m_bMIF;
    BOOL    m_bSilentMode;
    BOOL    m_bIgnoreErrors;
    BOOL    m_bTrace;
    CString m_sLogFile;
    BOOL    m_bRemoteMode;
    CString m_sRemoteHostServer;
    BOOL    m_bCustomSetup;
    BOOL    m_bAborted;
    CString m_sProgramGroupName;
    BOOL    m_bConfigurationOnly;
    BOOL    m_bReboot;
    CString m_sProductName;
	CString m_sProductShortName;
	CString m_sEditionName;
	CString m_sProduct;
	CString m_sComponentCode;
	BOOL	m_bIsUpgrade;

    // User Info
    CString m_sUser;
    CString m_sCompany;

    // License Info
	CString m_sCDKey;
    BOOL    m_bShowCDKeyDialog;
	
    // Install Path
	BOOL    m_bARCserveExists;
	BOOL    m_bAlertExists;
    BOOL    m_bNewAlertExists; 
    BOOL    m_bInoculanExists;
	BOOL    m_bClientAgentExists;
	CString m_sCurrentARCservePath;
    CString m_sCurrentAlertPath;
	CString m_sCurrentClientAgentPath;
    CString m_sARCservePath;
	CString m_sAlertPath;
	CString m_sClientAgentPath;

	// System Account Info
    CString m_sSystemAccountComputer;
    CString m_sSystemAccountDomain;
    CString m_sSystemAccountUser;
    CString m_sSystemAccountPassword;

    // Components
    DWORD   m_fInstallComponents;  
    BOOL    m_bHSMAvailable;
    BOOL    m_bTNGAvailable;
    CString m_sTNGSetupFileName;
    CString m_sLaunchSetupProductName;
    CString m_sLaunchSetupFileName;
    BOOL    m_bReplaceAlert;
    LONG    m_lProductType;
	int		m_nProdSubType;
	BOOL	m_bRAIDAvailable;
	BOOL	m_bChangerAvailable;
	CString m_sLicInstallFile;

	// Database to Install
    DWORD   m_fInstallDatabase;
	BOOL	m_bOverwriteDB;
    BOOL    m_bDBExists;

    // Queue 
    BOOL    m_bDeleteQueue;
    BOOL    m_bQueueExists;

    // Services
	BOOL    m_bAutoStartServices;

    void AddComponent(DWORD fComponent)    {m_fInstallComponents |= fComponent;}
    void RemoveComponent(DWORD fComponent) {m_fInstallComponents &= ~fComponent;}
    void AddDatabase(DWORD fDatabase)      {m_fInstallDatabase |= fDatabase;}
    void RemoveDatabase(DWORD fDatabase)   {m_fInstallDatabase &= ~fDatabase;}
    void Construct();

    BOOL    m_bCopyOnlyMode;
	DWORD	m_nInstallDBType;

public:
    static const DWORD INSTALL_MANAGER;
    static const DWORD INSTALL_SERVER;
    static const DWORD INSTALL_ADMIN;
    static const DWORD INSTALL_ALERT;
    static const DWORD INSTALL_HSM;
    static const DWORD INSTALL_TNG;
    static const DWORD INSTALL_AV;

    static const DWORD INSTALL_SQL;
    static const DWORD INSTALL_RAIMA;

    static const DWORD UNINSTALL_ARCSERVE;
    static const DWORD UNINSTALL_ALERT;
    static const DWORD UNINSTALL_SQL;

    CSettings();
    CSettings(LPSETUP_INFO lpSetupInfo, BOOL bUninstallMode = FALSE);

    void    Initialize();
    BOOL    Uninstall()                { return m_bUninstallMode;  }
    LPCTSTR Profile()                  { return m_sProfile; }
    LPCTSTR TraceFile()                { return m_sTraceFile; }
    LPCTSTR SourcePath()               { return m_sSourcePath; }
    LPCTSTR BatchFile()                { return m_sBatchFile; }
    LPCTSTR MIFFile()                  { return m_sMIFFile; }
    BOOL    MIF()                      { return m_bMIF; }
    BOOL    BatchOperation()           { return m_bSilentMode;     }
    BOOL    IgnoreErrors()             { return m_bIgnoreErrors;   }
    BOOL    Trace()                    { return m_bTrace; }
    LPCTSTR LogFile()                  { return m_sLogFile; }
    BOOL    RemoteSetup()              { return m_bRemoteMode;     }
    LPCTSTR RemoteHostServer()         { return m_sRemoteHostServer; }
    BOOL    CustomSetup()              { return m_bCustomSetup;    }
    BOOL    ExpressSetup()             { return !m_bCustomSetup;   }
    BOOL    Aborted()                  { return m_bAborted; }
    void    Abort()                    { m_bAborted = TRUE; }
    LPCTSTR ProgramGroupName()         { return m_sProgramGroupName; }
    BOOL    ConfigurationOnly()        { return m_bConfigurationOnly; }
    BOOL    HSMAvailable()             { return m_bHSMAvailable;   }
    BOOL    TNGFWAvailable()           { return m_bTNGAvailable;   }
    LPCTSTR TNGFWSetupFileName()       { return m_sTNGSetupFileName; }
    LPCTSTR LaunchSetupProductName()   { return m_sLaunchSetupProductName; }
    LPCTSTR LaunchSetupFileName()      { return m_sLaunchSetupFileName; }
    BOOL    InstallARCserve()          { ASSERT(!m_bUninstallMode); return InstallManager() || InstallServer() || InstallAdmin(); }
    BOOL    InstallManager()           { ASSERT(!m_bUninstallMode); return (m_fInstallComponents & INSTALL_MANAGER) == INSTALL_MANAGER; }
    BOOL    InstallServer()            { ASSERT(!m_bUninstallMode); return (m_fInstallComponents & INSTALL_SERVER) == INSTALL_SERVER;  }
    BOOL    InstallAdmin()             { ASSERT(!m_bUninstallMode); return (m_fInstallComponents & INSTALL_ADMIN) == INSTALL_ADMIN;   }
    BOOL    InstallAlert()             { ASSERT(!m_bUninstallMode); return (m_fInstallComponents & INSTALL_ALERT) == INSTALL_ALERT;   }
    BOOL    InstallAV()				   { ASSERT(!m_bUninstallMode); return (m_fInstallComponents & INSTALL_AV) == INSTALL_AV;   }
    BOOL    InstallHSM()               { ASSERT(!m_bUninstallMode); return (m_fInstallComponents & INSTALL_HSM) == INSTALL_HSM;     }
    BOOL    InstallTNG()               { ASSERT(!m_bUninstallMode); return (m_fInstallComponents & INSTALL_TNG) == INSTALL_TNG;     }
    BOOL    InstallSQL()               { ASSERT(!m_bUninstallMode); return (m_fInstallDatabase & INSTALL_SQL) == INSTALL_SQL; }
    BOOL    InstallRAIMA()             { ASSERT(!m_bUninstallMode); return (m_fInstallDatabase & INSTALL_RAIMA) == INSTALL_RAIMA; }
    BOOL    NewARCserveInstall()       { ASSERT(!m_bUninstallMode); return !m_bARCserveExists || (m_sCurrentARCservePath != m_sARCservePath); }
    BOOL    NewAlertInstall()          { ASSERT(!m_bUninstallMode); return !m_bAlertExists || (m_sCurrentAlertPath != m_sAlertPath); }
    BOOL    UninstallARCserve()        { ASSERT(m_bUninstallMode);  return (m_fInstallComponents & UNINSTALL_ARCSERVE) == UNINSTALL_ARCSERVE; }
    BOOL    UninstallAlert()           { ASSERT(m_bUninstallMode);  return (m_fInstallComponents & UNINSTALL_ALERT) == UNINSTALL_ALERT; }
    BOOL    UninstallSQL()			   { ASSERT(m_bUninstallMode);  return (m_fInstallDatabase & UNINSTALL_SQL) == UNINSTALL_SQL; }
    LONG    ProductType()              { return m_lProductType; }
    BOOL    OverwriteDB()              { return m_bOverwriteDB; }
    BOOL    ReplaceAlert()             { return m_bReplaceAlert; }
    BOOL    DeleteQueue()              { return m_bDeleteQueue; }
    BOOL    DBExists()                 { return m_bDBExists; }
    DWORD   DiskSpaceNeeded();
    LPCTSTR RegisteredUser()         { return m_sUser; }
    LPCTSTR RegisteredCompany()      { return m_sCompany; }
	//IvanC 5/6/98: Always disable CDKey Dialog for ASNT 6.6
	//BOOL    ShowCDKeyDialog()			 { return m_bShowCDKeyDialog; }
	BOOL    ShowCDKeyDialog()			 { return FALSE; }
	//IvanC
	LPCTSTR CDKey()					 { return m_sCDKey; }
	LPCTSTR ARCservePath()			 { return m_sARCservePath; }
	LPCTSTR AlertPath()				 { return m_sAlertPath; }
    //LPCTSTR Agent95Path()            { return m_sAgent95Path; }
    //LPCTSTR AgentNTPath()            { return m_sAgentNTPath; }
	LPCTSTR ClientAgentPath()        { return m_sClientAgentPath; }
    LPCTSTR CurrentARCservePath()    { return m_sCurrentARCservePath; }
    LPCTSTR CurrentAlertPath()       { return m_sCurrentAlertPath; }
    //LPCTSTR CurrentAgent95Path()     { return m_sCurrentAgent95Path; }
    //LPCTSTR CurrentAgentNTPath()     { return m_sCurrentAgentNTPath; }
	LPCTSTR CurrentClientAgentPath() { return m_sCurrentClientAgentPath; }
	BOOL	ARCserveExists()		 { return m_bARCserveExists; }
	BOOL	AlertExists()			 { return m_bAlertExists; }
    //BOOL    Agent95Exists()          { return m_bAgent95Exists; }
    //BOOL    AgentNTExists()          { return m_bAgentNTExists; }
	BOOL    ClientAgentExists()      { return m_bClientAgentExists; }
    BOOL    NewAlertExists()         { return m_bNewAlertExists; }
    BOOL    InoculanExists()         { return m_bInoculanExists; }
    BOOL    QueueExists()            { return m_bQueueExists; }
    LPCTSTR SystemAccountServer()    { return m_sSystemAccountComputer; }
    LPCTSTR SystemAccountDomain()    { return m_sSystemAccountDomain; }
    LPCTSTR SystemAccountName()      { return m_sSystemAccountUser; }
    LPCTSTR SystemAccountPassword()  { return m_sSystemAccountPassword; }
    BOOL    AutoStartServices()      { return m_bAutoStartServices; }
    BOOL    Reboot()                 { return m_bReboot; }
    int	    ProductSubType()         { return m_nProdSubType; }
    LPCTSTR ProductName()            { return m_sProductName; }
    LPCTSTR ProductShortName()       { return m_sProductShortName; }
    LPCTSTR EditionName()            { return m_sEditionName; }
	BOOL	ChangerAvailable()		 { return m_bChangerAvailable; }
	BOOL	RAIDAvailable()			 { return m_bRAIDAvailable; }
	LPCTSTR LicInstallFile()		 { return m_sLicInstallFile; }
	LPCTSTR Product()				 { return m_sProduct; }
	LPCTSTR ComponentCode()			 { return m_sComponentCode; }
	BOOL	IsUpgrade()				 { return m_bIsUpgrade; }

    void CustomSetup(BOOL bCustomSetup);
    void InstallManager(BOOL bInstallManager);
    void InstallServer(BOOL bInstallServer);
    void InstallAdmin(BOOL bInstallAdmin);
    void InstallAlert(BOOL bInstallAlert);
    void InstallAV(BOOL bInstallAV);
    void InstallHSM(BOOL bInstallHSM);
    void InstallTNG(BOOL bInstallTNG);
    void InstallSQL(BOOL bInstallSQL); 
    void InstallRAIMA(BOOL bInstallRAIMA);
    void UninstallARCserve(BOOL bUninstallARCserve);
    void UninstallAlert(BOOL bUninstallAlert);
    void UninstallSQL(BOOL bUninstallSQL);
    void RegisteredUser(LPCTSTR lpszUser)		{m_sUser = lpszUser;}
    void RegisteredCompany(LPCTSTR lpszCompany) {m_sCompany = lpszCompany;}
	void CDKey(LPCTSTR lpszCDKey)				{m_sCDKey = lpszCDKey;}
	void ARCservePath(LPCTSTR lpszPath);
	void AlertPath(LPCTSTR lpszPath);
    //void Agent95Path(LPCTSTR lpszPath);
    //void AgentNTPath(LPCTSTR lpszPath);
	void ClientAgentPath(LPCTSTR lpszPath);
    void OverwriteDB(BOOL bOverwriteDB)         {m_bOverwriteDB = bOverwriteDB;}
    void ReplaceAlert(BOOL bReplaceAlert)       {m_bReplaceAlert = bReplaceAlert;}
    void DeleteQueue(BOOL bDeleteQueue)         {m_bDeleteQueue = bDeleteQueue;}
    void SystemAccountServer(LPCTSTR lpszServer){m_sSystemAccountComputer = lpszServer;}
    void SystemAccountDomain(LPCTSTR lpszDomain){m_sSystemAccountDomain = lpszDomain;}
    void SystemAccountName(LPCTSTR lpszUser)    {m_sSystemAccountUser = lpszUser;}
    void SystemAccountPassword(LPCTSTR lpszPwd) {m_sSystemAccountPassword = lpszPwd;}
    void AutoStartServices(BOOL bAutoStart)     {m_bAutoStartServices = bAutoStart;}
    void ProductType(LONG lType)                {m_lProductType = lType;}
    void ProductSubType(int nSubType)           {m_nProdSubType = nSubType;}
    void Reboot(BOOL bReboot)                   {m_bReboot = bReboot;}
    BOOL   IsCopyOnlyMode()						{ return m_bCopyOnlyMode; }
    DWORD  InstallDBType()						{ return m_nInstallDBType; }
    void   InstallDBType(DWORD nDBType);

    CSettings &operator=(CSettings &Settings);


};

#endif
