/////////////////////////////////////////////////////////////////////////////
// SetupIcf.h		CSetupIcf Interface
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.

#ifndef _SETUPICF
#define _SETUPICF

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CSetupIcf
#else
class __declspec(dllimport) CSetupIcf
#endif
{
public: 
    static const LPCTSTR DefaultAgent95Path()  { return m_sDefaultAgent95Path;  };
    static const LPCTSTR DefaultAgentNTPath()  { return m_sDefaultAgentNTPath;  };
	static const LPCTSTR DefaultMandalayJBossPath()  { return m_sDefaultMandalayJBossPath;  };
	static const LPCTSTR DefaultMandalayPath()  { return m_sDefaultMandalayPath;  };
	static const LPCTSTR DefaultMandalayPort()  { return m_sDefaultMandalayPort;  };
	static const LPCTSTR DefaultMandalayTomcatPort()  { return m_sDefaultMandalayTomcatPort;  };
	static const LPCTSTR DefaultMandalayHost()  { return m_sDefaultMandalayHost;  };
	static const LPCTSTR DefaultARCservePath() { return m_sDefaultARCservePath; };
	static const LPCTSTR DefaultAlertPath()	  { return m_sDefaultAlertPath; };
	static const LPCTSTR DefaultICFFileName()  { return m_sDefaultFilename; };
	static const LPCTSTR DefaultMDBPath()  { return m_sDefaultMDBPath;  };
	static const LPCTSTR DefaultLocalMDB()  { return m_sDefaultLocalMDB;  };
	static const LPCTSTR DefaultMDBHost()  { return m_sDefaultMDBHost;  };
	static const LPCTSTR DefaultMDBUser()  { return m_sDefaultMDBUser;  };
	static const LPCTSTR DefaultMDBPassword()  { return m_sDefaultMDBPassword;  };
	static const LPCTSTR DefaultMDBID()  { return m_sDefaultMDBID;  };
	static const LPCTSTR DefaultMDBDatabase()  { return m_sDefaultMDBDatabase;  };
	static const LPCTSTR DefaultLocaleIAM()  { return m_sDefaultLocaleIAM;  };
	static const LPCTSTR DefaultRemoteMDBeIAM()  { return m_sDefaultRemoteMDBeIAM;  };
	static const LPCTSTR DefaulteIAMPass()  { return m_sDefaulteIAMPass;  };
	static const LPCTSTR DefaulteIAMRemoteHost()  { return m_sDefaulteIAMRemoteHost;  };

	static const LPCTSTR DefaultDatabaseLoc()  { return m_sDefaultDatabasePath;  };
	static const LPCTSTR DefaultCheckLoc()  { return m_sDefaultCheckPath;  };
	static const LPCTSTR DefaultDumpLoc()  { return m_sDefaultDumpPath;  };
	static const LPCTSTR DefaultJourLoc()  { return m_sDefaultJourPath;  };
	static const LPCTSTR DefaultWorkLoc()  { return m_sDefaultWorkPath;  };
	
private:
	static const CString m_sDefaultFilename;
    static const CString m_sDefaultARCservePath;
    static const CString m_sDefaultAlertPath;
    static const CString m_sDefaultAgent95Path;
    static const CString m_sDefaultAgentNTPath;
	static const CString m_sDefaultMandalayPath;
	static const CString m_sDefaultMandalayJBossPath;
	static const CString m_sDefaultMandalayPort;
	static const CString m_sDefaultMandalayTomcatPort;
	static const CString m_sDefaultMandalayRMIPort;
	static const CString m_sDefaultMandalayJRMPPort;
	static const CString m_sDefaultMandalayGOSSIPPort;
	static const CString m_sDefaultMandalayHost;
	static const CString m_sDefaultMDBPath;
    static const CString m_sDefaultDomainName;
    static const CString m_sDefaultServerName;
    static const CString m_sDefaultUserName;
    static const CString m_sDefaultPassword;
    static const CString m_sDefaultID;

    static const CString m_sUserInfo;
    static const CString m_sFullName;
    static const CString m_sCompanyName;
    static const CString m_sProductInfo;
    static const CString m_sCDKey;
    static const CString m_sInstallPath;
    static const CString m_sARCservePath;
    static const CString m_sAlertPath;
    static const CString m_sAgentPath;
    static const CString m_sSystemAccount;
    static const CString m_sInstall;
    static const CString m_sARCManager;
    static const CString m_sARCServer;
    static const CString m_sARCServerAdmin;
    static const CString m_sAlert;
	static const CString m_siSponsor;
	static const CString m_sCPManager;
    static const CString m_sAV;
    static const CString m_sDataMigration;
    static const CString m_sOverwriteDB;
    static const CString m_sOverwriteJobQueue;
    static const CString m_sDatabaseOption;
    static const CString m_sSQL;
    static const CString m_sUninstall;
    static const CString m_sMVSConfig;
    static const CString m_sSiteName;
    static const CString m_sSystemID;
    static const CString m_sLocalLUAlias;
    static const CString m_sPartnerLUAlias;
    static const CString m_sMandalayPath;
	static const CString m_sDatabasePath;
	static const CString m_sCheckPath;
	static const CString m_sDumpPath;
	static const CString m_sJourPath;
	static const CString m_sWorkPath;
    static const CString m_sMandalayJBossPath;
    static const CString m_sMandalayPort;
	static const CString m_sMandalayTomcatPort;
	static const CString m_sMandalayRMIPort;
	static const CString m_sMandalayJRMPPort;
	static const CString m_sMandalayGOSSIPPort;
    static const CString m_sMandalayHost;
    static const CString m_sMDBPath;

    static const CString m_sLocalMDB;
	static const CString m_sLocaleIAM;
	static const CString m_sRemoteMDBeIAM;
	static const CString m_seIAMPass;
	static const CString m_seIAMRemoteHost;
	
	static const CString m_sMDBHost;
	static const CString m_sMDBUser;
	static const CString m_sMDBPassword;
	static const CString m_sMDBDatabase;
	static const CString m_sMDBID;

	static const CString m_sDefaultMDBHost;
	static const CString m_sDefaultMDBUser;
	static const CString m_sDefaultMDBPassword;
	static const CString m_sDefaultMDBDatabase;
	static const CString m_sDefaultMDBID;

	static const CString m_sDefaultLocalMDB;	  
	static const CString m_sDefaultLocaleIAM;
	static const CString m_sDefaultRemoteMDBeIAM;
	static const CString m_sDefaulteIAMPass;
	static const CString m_sDefaulteIAMRemoteHost;

	static const CString m_sDefaultDatabasePath;
	static const CString m_sDefaultCheckPath;
	static const CString m_sDefaultJourPath;
	static const CString m_sDefaultDumpPath;
	static const CString m_sDefaultWorkPath;
	
//add by xu$mi01 for common feature in 3/20/2002
	static const CString m_sProdSubType;

//add by xu$mi01 for ARCserve Domain in 3/20/2001
	static const CString m_sASDomain;
	static const CString m_sDSname;
	static const CString m_sPriDSname;
	static const CString m_sSecDSname;
	static const CString m_sUserName;
	static const CString m_sDomainPWD;
	static const CString m_sCreateASDomain;
//add by MichaelX to save BrightStor Domain password in 9/19/2002
	static const CString m_sSaveASDomainPWD;
//add by MichaelX for NW features in 9/24/2002
    static const CString m_sDRWizFeature;
    static const CString m_sSANWizFeature;

//add by xu$mi01 for master setup in 11/16/2001
//This is Product section list in setup.icf
	static const CString m_sBaseProduct;
	static const CString m_sDBAOracle;
	static const CString m_sDBASQL;
	static const CString m_sDBAEXCH;
	static const CString m_sDBANOTE;
	static const CString m_sDBAINFO;
	static const CString m_sDBASYBASE;
	static const CString m_sDBAINGRES;
	static const CString m_sDBASAPSQL;
	static const CString m_sDBASAPORA;
	static const CString m_sClientAgent;
	static const CString m_sSPAgent;	
	static const CString m_sMandalay;
	static const CString m_sMDB;
	static const CString m_sSQLSetup;//added by luocl01 for issue 16359650

	static const CString m_sSQLExpSAPWD;
	static const CString m_sSQLExpInstanceName;
	static const CString m_sBABDBUpdateFlag;
	static const CString m_sSQLExpInstallFlag;
	static const CString m_sSQLEUpgradeFlag;
	static const CString m_sSQLRemoteSerPWD;//added by luocl01 for issue 16359650
	static const CString m_sSetupHostMachineName;
	static const CString m_sSetupMode;
	static const CString m_sStandAloneServer;
	static const CString m_sPrimaryServer;
	static const CString m_sMemberServer;
	static const CString m_sBaseCMO;
	static const CString m_sBaseTLO;
	static const CString m_sBaseSANO;
	static const CString m_sBaseD2D2T;
	static const CString m_sBaseVMWare;
	static const CString m_sBaseEModule;
	static const CString m_sBaseDRO;
	static const CString m_sBaseNASAgent;
	static const CString m_sBaseUnicenter;
	static const CString m_sBetaUpgFlag;

//Added by ChongW, 2001/5/14
//for Upgrade IE 
	static const CString m_sUpgradeIE;
	BOOL	m_bNeedUpgradeIE;
	static const CString m_sLanguageID;
	TCHAR	m_szLanguageID[10];

    TCHAR   m_szDSname[48];
    TCHAR   m_szPriDSname[48];
    TCHAR   m_szSecDSname[48];
    TCHAR   m_szUserName[48];
    TCHAR   m_szDomainPWD[48];
	BOOL	m_bCreateASDomain;
//add by MichaelX to save BrightStor Domain password in 9/19/2002
	BOOL	m_bSaveASDomainPWD;
	
//add by MichaelX for NW features in 9/24/2002
	BOOL	m_bDRWizFeature;
	BOOL	m_bSANWizFeature;

	CString m_sIcfFile;
	CString m_sDefaultInstallPath;

    BOOL    m_bExists;
    TCHAR   m_szFullName[256];
    TCHAR   m_szCompanyName[256];
    TCHAR   m_szCDKey[32];
    TCHAR   m_szARCservePath[MAX_PATH];
    TCHAR   m_szAlertPath[MAX_PATH];
    TCHAR   m_szAgentPath[MAX_PATH];
	TCHAR   m_szMandalayPath[MAX_PATH];
	TCHAR   m_szMandalayJBossPath[MAX_PATH];
	TCHAR   m_szMandalayPort[MAX_PATH];
	TCHAR   m_szMandalayTomcatPort[MAX_PATH];
	TCHAR   m_szMandalayRMIPort[MAX_PATH];
	TCHAR   m_szMandalayJRMPPort[MAX_PATH];
	TCHAR   m_szMandalayGOSSIPPort[MAX_PATH];
	TCHAR   m_szMandalayHost[MAX_PATH];
	TCHAR   m_szMDBPath[MAX_PATH];
	TCHAR   m_szMDBHost[48];
	TCHAR   m_szMDBUser[48];
	TCHAR   m_szMDBPassword[48];
	TCHAR   m_szMDBID[48];
	TCHAR   m_szMDBDatabase[48];
	TCHAR   m_szLocalMDB[48];
	TCHAR   m_szLocaleIAM[48];
	TCHAR   m_szRemoteMDBeIAM[48];
	TCHAR   m_szeIAMPass[48];
	TCHAR   m_szeIAMRemoteHost[MAX_PATH];
	TCHAR   m_szSADomain[48];
    TCHAR   m_szSAServer[48];
    TCHAR   m_szSAUser[48];
    TCHAR   m_szSAPassword[48];
    BOOL    m_bInstallManager;
    BOOL    m_bInstallServer;
    BOOL    m_bInstallAdmin;
    BOOL    m_bInstallAlert;
	BOOL	m_bInstalliSponsor;
	BOOL	m_bInstallCPM;
	BOOL	m_bInstallAV;
    BOOL    m_bInstallHSM;
    BOOL    m_bInstallTNG;
    BOOL    m_bOverwriteDB;
    BOOL    m_bOverwriteJobQueue;
    BOOL    m_bSQLDatabase;
    BOOL    m_bUninstallARCserve;
    BOOL    m_bUninstallAlert;
    TCHAR   m_szSiteName[48];
    TCHAR   m_szSystemID[48];
    TCHAR   m_szLocalLUAlias[48];
    TCHAR   m_szPartnerLUAlias[48];
	TCHAR	m_szSetupHostMachineName[48];

	BOOL	m_bInstallStandAloneSrv;
	BOOL	m_bInstallPrimarySrv;
	BOOL	m_bInstallMemberSrv;
	BOOL	m_bInstallBaseCMO;
	BOOL	m_bInstallBaseTLO;
	BOOL	m_bInstallBaseSANO;
	BOOL	m_bInstallBaseD2D2T;
	BOOL	m_bInstallBaseVMWare;
	BOOL	m_bInstallBaseEModule;
	BOOL    m_bInstallBaseDRO;
	BOOL    m_bInstallBaseNASAgent;
	BOOL    m_bInstallBaseUnicenter;
	int		m_nSetupMode;

	TCHAR   m_szDatabasePath[MAX_PATH];
	TCHAR   m_szCheckPath[MAX_PATH];
	TCHAR   m_szJourPath[MAX_PATH];
	TCHAR   m_szDumpPath[MAX_PATH];
	TCHAR   m_szWorkPath[MAX_PATH];

    BOOL    FindFile();

public:
    CSetupIcf(LPCTSTR lpszFileName = NULL);

	~CSetupIcf();

    BOOL    Exists() { return m_bExists; }
	BOOL	Init(LPCTSTR lpszFileName = NULL);

    // Property Gets
    LPCTSTR FullName();
    LPCTSTR CompanyName();
    LPCTSTR CDKey();
    LPCTSTR ARCservePath();
    LPCTSTR AlertPath();
    //LPCTSTR Agent95Path();
    //LPCTSTR AgentNTPath();
	LPCTSTR ClientAgentPath();
//add by xu$mi01 for master setup in 4/22/2002
	LPCTSTR DBAOraPath();
	LPCTSTR DBASqlPath();
	LPCTSTR DBAExchPath();
	LPCTSTR DBANotePath();
	LPCTSTR DBAInfoPath();
	LPCTSTR DBASybasePath();
	LPCTSTR DBAIngresPath();
	LPCTSTR DBASapSqlPath();
	LPCTSTR DBASapOraPath();
	LPCTSTR	SPAgentPath();	
	LPCTSTR	MandalayPath();
	LPCTSTR	MandalayJBossPath();
	LPCTSTR	MandalayPort();
	LPCTSTR	MandalayTomcatPort();
	LPCTSTR	MandalayJRMPPort();
	LPCTSTR	MandalayRMIPort();
	LPCTSTR	MandalayGOSSIPPort();
	LPCTSTR	MandalayHost();
	LPCTSTR	MDBPath();
	LPCTSTR	LocalMDB();
	LPCTSTR	MDBHost();
	LPCTSTR	MDBUser();
	LPCTSTR	MDBPassword();
	LPCTSTR	MDBID();
	LPCTSTR	MDBDatabase();
	LPCTSTR DatabaseLoc();
	LPCTSTR CheckLoc();
	LPCTSTR JourLoc();
	LPCTSTR DumpLoc();
	LPCTSTR WorkLoc();
	LPCTSTR	LocaleIAM();
	LPCTSTR	RemoteMDBeIAM();
	LPCTSTR	eIAMPass();
	LPCTSTR	eIAMRemoteHost();
	LPCTSTR SystemAccountDomain();
    LPCTSTR SystemAccountServer();
    LPCTSTR SystemAccountUser();
    LPCTSTR SystemAccountPassword();
    BOOL    InstallManager();
    BOOL    InstallServer();
    BOOL    InstallAdmin();
    BOOL    InstallAlert();
	BOOL	InstalliSponsor();
	BOOL	InstallCPM();
	BOOL	InstallAV();
    BOOL    InstallHSM();
    BOOL    InstallTNG();
    BOOL    OverwriteDB();
    BOOL    OverwriteJobQueue();
    BOOL    SQLDB();
    BOOL    UninstallARCserve();
    BOOL    UninstallAlert();
    LPCTSTR SiteName();
    LPCTSTR SystemID();
    LPCTSTR LocalLUAlias();
    LPCTSTR PartnerLUAlias();

	BOOL InstallBaseStandAlone();
	BOOL InstallBasePrimary();
	BOOL InstallBaseMember();
	BOOL InstallBaseCMO();
	BOOL InstallBaseTLO();
	BOOL InstallBaseSANO();
	BOOL InstallBaseD2D2T();
	BOOL InstallBaseVMWare();
	BOOL InstallBaseEModule();
	BOOL InstallBaseDRO();
	BOOL InstallBaseNASAgent();
	BOOL InstallBaseUnicenter();
	
    // Property Lets
    BOOL FullName(LPCTSTR lpszFullName);
    BOOL CompanyName(LPCTSTR lpszCompanyName);
    BOOL CDKey(LPCTSTR lpszCDKey);
    BOOL ARCservePath(LPCTSTR lpszARCservePath);
    BOOL AlertPath(LPCTSTR lpszAlertPath);
    //BOOL Agent95Path(LPCTSTR lpszAgentPath) {return AgentPath(lpszAgentPath);}
    //BOOL AgentNTPath(LPCTSTR lpszAgentPath) {return AgentPath(lpszAgentPath);}
	BOOL ClientAgentPath(LPCTSTR lpszAgentPath) {return AgentPath(lpszAgentPath);}
    BOOL AgentPath(LPCTSTR lpszAgentPath);
//add by xu$mi01 for master setup in 4/22/2002
    BOOL DBAOraPath(LPCTSTR lpszAgentPath);
    BOOL DBASqlPath(LPCTSTR lpszAgentPath);
    BOOL DBAExchPath(LPCTSTR lpszAgentPath);
    BOOL DBANotePath(LPCTSTR lpszAgentPath);
    BOOL DBAInfoPath(LPCTSTR lpszAgentPath);
    BOOL DBASybasePath(LPCTSTR lpszAgentPath);
    BOOL DBAIngresPath(LPCTSTR lpszAgentPath);
    BOOL DBASapSqlPath(LPCTSTR lpszAgentPath);
	BOOL MandalayPath(LPCTSTR lpszMandalayPath);
	BOOL MandalayJBossPath(LPCTSTR lpszMandalayJBossPath);
	BOOL MandalayPort(LPCTSTR lpszMandalayPort);
	BOOL MandalayTomcatPort(LPCTSTR lpszMandalayTomcatPort);
	BOOL MandalayRMIPort(LPCTSTR lpszMandalayRMIPort);
	BOOL MandalayJRMPPort(LPCTSTR lpszMandalayJRMPPort);
	BOOL MandalayGOSSIPPort(LPCTSTR lpszMandalayGOSSIPPort);
	BOOL MandalayHost(LPCTSTR lpszMandalayHost);
	BOOL MDBPath(LPCTSTR lpszMDBPath);
 	BOOL LocalMDB(LPCTSTR lpszLocalMDB);
 	BOOL MDBHost(LPCTSTR lpszMDBHost);
 	BOOL MDBUser(LPCTSTR lpszMDBUser);
 	BOOL MDBPassword(LPCTSTR lpszMDBPassword);
	BOOL MDBID(LPCTSTR lpszMDBID);
 	BOOL MDBDatabase(LPCTSTR lpszMDBDatabase);
 	BOOL DatabaseLoc(LPCTSTR lpszDadatabaseLoc);
 	BOOL CheckLoc(LPCTSTR lpszCheckLoc);
 	BOOL JourLoc(LPCTSTR lpszJourLoc);
 	BOOL DumpLoc(LPCTSTR lpszDumpLoc);
 	BOOL WorkLoc(LPCTSTR lpszWorkLoc);
	BOOL LocaleIAM(LPCTSTR lpszLocaleIAM);
 	BOOL RemoteMDBeIAM(LPCTSTR lpszRemoteMDBeIAM);
	BOOL eIAMPass(LPCTSTR lpszeIAMPass);
	BOOL eIAMRemoteHost(LPCTSTR lpszeIAMRemoteHost);
	BOOL DBASapOraPath(LPCTSTR lpszAgentPath);
    BOOL SPAgentPath(LPCTSTR lpszAgentPath);    
    BOOL SystemAccountDomain(LPCTSTR lpszSystemAccountDomain);
    BOOL SystemAccountServer(LPCTSTR lpszSystemAccountServer);
    BOOL SystemAccountUser(LPCTSTR lpszSystemAccountUser);
    BOOL SystemAccountPassword(LPCTSTR lpszSystemAccountPassword);
    BOOL InstallManager(BOOL bInstallManager);
    BOOL InstallServer(BOOL bInstallServer);
    BOOL InstallAdmin(BOOL bInstallAdmin);
    BOOL InstallAlert(BOOL bInstallAlert);
    BOOL InstalliSponsor(BOOL InstalliSponsor);
    BOOL InstallCPM(BOOL InstallCPM);
	BOOL InstallAV(BOOL bInstallAV);
    BOOL InstallHSM(BOOL bInstallHSM);
    BOOL InstallTNG(BOOL bInstallTNG);
    BOOL OverwriteDB(BOOL bOverwriteDB);
    BOOL OverwriteJobQueue(BOOL bOverwriteJobQueue);
    BOOL SQLDB(BOOL bSQLDB);
    BOOL UninstallARCserve(BOOL bUninstallARCserve);
    BOOL UninstallAlert(BOOL bUninstallAlert);
    BOOL SiteName(LPCTSTR lpszSiteName);
    BOOL SystemID(LPCTSTR lpszSystemID);
    BOOL LocalLUAlias(LPCTSTR lpszLocalLUAlias);
    BOOL PartnerLUAlias(LPCTSTR lpszPartnerLUAlias);

	BOOL InstallBaseStandAlone(BOOL bInstallBaseStandAlone);
	BOOL InstallBasePrimary(BOOL bInstallBasePrimary);
	BOOL InstallBaseMember(BOOL bInstallBaseMember);
	BOOL InstallBaseCMO(BOOL bInstallBaseCMO);
	BOOL InstallBaseTLO(BOOL bInstallBaseTLO);
	BOOL InstallBaseSANO(BOOL bInstallBaseSANO);
	BOOL InstallBaseD2D2T(BOOL bInstallBaseD2D2T);
	BOOL InstallBaseVMWare(BOOL bInstallBaseVMWare);
	BOOL InstallBaseEModule(BOOL bInstallBaseEModule);
	BOOL InstallBaseDRO(BOOL bInstallBaseDRO);
	BOOL InstallBaseNASAgent(BOOL bInstallBaseNASAgent);
	BOOL InstallBaseUnicenter(BOOL bInstallBaseUnicenter);

    BOOL WriteString (LPCTSTR lpSession, LPCTSTR lpKey, LPCTSTR lpStrValue);
    BOOL WriteInt    (LPCTSTR lpSession, LPCTSTR lpKey, INT dIntValue);
    BOOL GetString   (LPCTSTR lpSession, LPCTSTR lpKey, LPTSTR lpStrValue);
    BOOL GetInt      (LPCTSTR lpSession, LPCTSTR lpKey, INT *pIntValue);
	BOOL InstallManager(LPCTSTR szInstallManager, DWORD nSize);
	BOOL InstallServer(LPCTSTR szInstallServer, DWORD nSize);
	BOOL InstallAdmin(LPCTSTR szInstallAdmin, DWORD nSize);
	BOOL InstalliSponsor(LPCTSTR szInstalliSponsor, DWORD nSize);
	BOOL InstallCPM(LPCTSTR szInstallCPM, DWORD nSize);
	BOOL InstallAV(LPCTSTR szInstallAV, DWORD nSize);

//add by xu$mi01 for common feature in 3/20/2002
	int		GetProductSubType();
//add by xu$mi01 for ARCserve Domain in 3/20/2001
	BOOL CreateASDomain();
    LPCTSTR ASDomainName();
    LPCTSTR PriASDomainName();
    LPCTSTR SecASDomainName();
    LPCTSTR ASDomainUserName();
    LPCTSTR ASDomainPassword();
	BOOL CreateASDomain(BOOL bCreateASDomain);
	BOOL ASDomainName(LPCTSTR lpszASDomainName);
	BOOL PriASDomainName(LPCTSTR lpszPriASDomainName);
	BOOL SecASDomainName(LPCTSTR lpszSecASDomainName);
	BOOL ASDomainUserName(LPCTSTR lpszASDomainUserName);
	BOOL ASDomainPassword(LPCTSTR lpszASDomainPassword);

//add by MichaelX to save BrightStor Domain password in 9/19/2002
	BOOL SaveASDomainPWD();
	BOOL SaveASDomainPWD(BOOL bSaveASDomainPWD);


	BOOL SetPassPhrasePWD(LPCTSTR tcPassPhrasePWD);
	BOOL GetPassPhrasePWD(LPTSTR tcPassPhrasePWD, const DWORD dwSize);

//add by MichaelX for NW features in 9/24/2002
	BOOL InstallDRWiz();
	BOOL InstallDRWiz(BOOL	bInstallDRWiz);
	BOOL InstallDRWiz(LPCTSTR szInstallDRWIZ, DWORD nSize);

	BOOL InstallSANWiz();
	BOOL InstallSANWiz(BOOL	bInstallSANWiz);
	BOOL InstallSANWiz(LPCTSTR szInstallSANWIZ, DWORD nSize);

//Added by ChongW, 2001/5/14
//for Upgrade IE 
	BOOL	UpgradeIE();
	LPCTSTR	LanguageID();
	BOOL	UpgradeIE(BOOL bNeedUpgrade);
	BOOL	LanguageID(LPCTSTR szLanguageID);


	int GetDBType();
	void SetDBType(const int nDBType);

	BOOL SaveSQLExpPWD(LPCTSTR lpcstrPassword);
	BOOL GetSQLExpPWD(LPTSTR tcSAPassword, const DWORD dwSize);
	BOOL SaveSQLExpInstanceName(LPCTSTR lpctstrInstanceName);
	BOOL GetSQLExpInstanceName(LPTSTR tcInstanceName, const DWORD dwSize);
	void SetARCserveDBUpgradeFlag(const DWORD dwUpdateFlag);
	DWORD GetARCserveDBUpgradeFlag();
	void SetSQLExpInstallFlag(const DWORD dwSQLExpInstallFlag);
	DWORD GetSQLExpInstallFlag();
	void SetSQLEUpgradeFlag(const DWORD dwSQLEUpgradeFlag);
	DWORD GetSQLEUpgradeFlag();
	BOOL GetSQLRemoteSerPWD(LPTSTR tcLoginPassword, const DWORD dwSize);//added by luocl01 for issue 16359650
	BOOL GetSQLLoginPWD(LPTSTR tcLoginPassword, const DWORD dwSize);
	BOOL SetSQLRemoteSrvPWD(LPCTSTR tcLoginPassword);
	BOOL SetSQLLoginPWD(LPCTSTR tcLoginPassword);

	BOOL GetSetupHostMachineName(LPTSTR lpName, const DWORD dwSize);
	BOOL SetSetupHostMachineName(LPCTSTR lpName);

	int GetSetupMode();
	void SetSetupMode(const int nSetupMode);

	void SetSQLAgentInstanceName(LPCTSTR lpInstanceName);
	void SetSQLAgentSQLUserName(LPCTSTR lpUserName);
	void SetSQLAgentInstanceAuthType(const UINT nType);

	BOOL GetSQLAgentInstanceName(LPTSTR lpInstanceName, const DWORD dwBufSize) const;
	BOOL GetSQLAgentSQLUserName(LPTSTR lpUserName, const DWORD dwBufSize) const;
	UINT GetSQLAgentInstanceAuthType() const;
	void SetSQLAgent_Install_Flag(const UINT uInstallType);
	UINT GetSQLAgent_Install_Flag() const;

	void SetClusterInstallFlag(const BOOL bClusterInstall);
	BOOL GetClusterInstallFlag() const;

	void SetClusterEnvType(const DWORD dwType);
	DWORD GetClusterEnvType() const;
	void SetWANSync_ReplicaNodeName(LPCTSTR lpszReplicaNodeName);
	BOOL GetWANSync_ReplicaNodeName(LPTSTR lpszReplicaNodeName, const DWORD dwBuffSize);
	void SetServerDB_DataFilePath(LPCTSTR lpszDataFilePath);
	BOOL GetServerDB_DataFilePath(LPTSTR lpszDataFilePath, const DWORD dwBuffSize);
	void SetVirtualServerName(LPCTSTR lpszVirtualServerName);
	BOOL GetVirtualServerName(LPTSTR lpszVirtualServerName, const DWORD dwBuffSize);
	void SetVirtualServerIP(LPCTSTR lpszVirtualServerIP);
	BOOL GetVirtualServerIP(LPTSTR lpszVirtualServerIP, const DWORD dwBuffSize);
	void SetMasterNodeIP(LPCTSTR lpszMasterNodeIP);
	BOOL GetMasterNodeIP(LPTSTR lpszMasterNodeIP, const DWORD dwBuffSize);
	void SetWANSync_ReplicaNodeIP(LPCTSTR lpszReplicaNodeIP);
	BOOL GetWANSync_ReplicaNodeIP(LPTSTR lpszReplicaNodeIP, const DWORD dwBuffSize);
	void SetMasterNodeName(LPCTSTR lpszMasterNodeName);
	BOOL GetMasterNodeName(LPTSTR lpszMasterNodeName, const DWORD dwBuffSize);
	void Set_WANSync_MasterType(const DWORD dwWANSyncMasterType);
	DWORD Get_WANSync_MasterType() const;
	BOOL GetIsSANEnv();
	BOOL SetIsSANEnv(BOOL bIsSANEnv);
	LPCTSTR SANPrimaryServer();
	BOOL SANPrimaryServer(LPCTSTR lpszSANPrimaryServer);
	void SetOripinBetaUpgFlag(const BOOL bBetaUpg);
	BOOL GetOripinBetaUpgFlag();

	BOOL    m_bIsSANEnv;
	TCHAR   m_szSANPrimaryServer[48];
	static const CString m_sSANEnv;
	static const CString m_sSANPrimaryServer;
};
#endif
