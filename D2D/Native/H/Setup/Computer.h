/////////////////////////////////////////////////////////////////////////////
// Computer.h		CCurrentUser Interface
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.

#ifndef _COMPUTER
#define _COMPUTER

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CComputer
#else
class __declspec(dllimport) CComputer
#endif
{
private:
    CString m_sName;
	CString m_wsName;
    CString m_sDomain;
    BOOL m_bPrimaryDomainController;
    BOOL m_bBackupDomainController;
    BOOL m_bServer;
	BOOL m_bIsLocal;
    DWORD m_dwNTVersion;

public:
    // Constructor
    CComputer(LPCTSTR lpComputerName = NULL);

    // Attributes
    LPCTSTR Name(){return m_sName;}

    LPCTSTR Domain(){return m_sDomain;}

    BOOL DomainController(){return m_bPrimaryDomainController || m_bBackupDomainController;}

    BOOL PrimaryDomainController(){return m_bPrimaryDomainController;}

    BOOL BackupDomainController(){return m_bBackupDomainController;}

    BOOL Server(){return m_bServer;}

	BOOL IsLocal(){return m_bIsLocal;}

	int DisplayTrustedDomains(LPWSTR TargetComputer, CComboBox* pCombo);

    static BOOL GetComputerName(LPTSTR lpszName, LPDWORD lpdwSize);

    static BOOL GetDomain(LPTSTR lpszDomain, LPDWORD lpdwSize, LPCTSTR lpMachineName = NULL);

    static BOOL IsPrimaryDomainController(LPCTSTR lpMachineName = NULL);

    static BOOL IsBackupDomainController(LPCTSTR lpMachineName = NULL);

    static BOOL IsServer(LPCTSTR lpMachineName = NULL);

    static DWORD GetNTVersion(LPCTSTR lpMachineName = NULL);

    static BOOL IsNT4xOrLater(LPCTSTR lpMachineName = NULL);

	static BOOL IsWindows95(LPCTSTR lpMachineName = NULL);

	static BOOL IsLocalMachine(LPCTSTR lpMachine = NULL);

    static TCHAR FindFirstHardDrive();

	static DWORD DriveFreeSpaceBytes(LPCTSTR lpszPath);

    static UINT GetDriveType(LPCTSTR lpszPath);

    static BOOL DirectoryExists(LPCTSTR lpszPath);

    static BOOL CreateDirectory(LPCTSTR lpszPath, BOOL bCreateIntermediates = TRUE);

    static BOOL ChangeDirectory(LPCTSTR lpszPath);

    static BOOL RemoveDirectory(LPCTSTR lpszPath);

    static BOOL CreateARCserveHomeShare(LPCTSTR lpPath, LPCTSTR lpSystemAccountServer, LPCTSTR lpSystemAccountName);

    static BOOL DeleteARCserveHomeShare();

    static BOOL DeleteTree(LPCTSTR lpszPath);

    static BOOL DeleteFiles(LPCTSTR lpszPath, LPCTSTR lpszFilePattern);

    static BOOL PathExists(LPCTSTR lpszPath);

    static BOOL PathEmpty(LPCTSTR lpszPath, LPCTSTR lpszFilePattern = NULL);

	static BOOL ChangeFilesAttribute(LPCTSTR lpszPath, LPCTSTR lpszFilePattern, DWORD dwFileAttributes);

	// Verify Windows version 
	static BOOL IsWindowsVersionOK(DWORD dwMajor, DWORD dwMinor, DWORD dwSPMajor);

	//add the functions for copy by fanzh01 Oct. 17, 2006
	static BOOL CopyFileRecursively(LPCTSTR lpszSrcPath, LPCTSTR lpszDestPath, BOOL bFailIfExists = FALSE);
	//end

	static BOOL IsMemberServer();

	static BOOL GetPrimaryServerName(TCHAR* tcBuffer, const DWORD dwSize);

	static BOOL GetASDomainName(TCHAR* tcBuffer, const DWORD dwSize);

	static long AddMemberServerToDomain(const CString& strDomainName, const CString& strPrimaryName, const CString& strLocalMachineName, const CString& strUserName, const CString& strPSW);

	static long DeletePrimaryMemberInfoInDomain(const CString& strDomainName, const CString& strPrimaryName, const CString& strUserName, const CString& strPSW);

	static long RemoveMemberServerFromDomain(const CString& strPrimaryName);

	static long GetDomainServerNamesArray(const CString& strDomainName, const CString& strPrimaryName, const CString& strUserName, const CString& strPSW, CStringArray& NameArray);

	static long SetMemberServerInfoToDomain(const CString& strDomainName, const CString& strPrimaryName, const CString& strLocalMachineName, const CString& strUserName, const CString& strPSW);

	static long CMO_LicenseCheck(LPCTSTR lpPrimaryServer);

	static long LicenseCheck(LPCTSTR lpPrimaryServer, const long lProdID, const int nCount, UINT& process_error, UINT& pca_lic_error);

	static long Add_MemberServer_License(LPCTSTR lpPrimaryServer, LPCTSTR lpMemberServer, const long lProdID, int& nCount);

	static long Remove_MemberServer_License(LPCTSTR lpPrimaryServer, LPCTSTR lpMemberServer, const long lProdID, int& nCount);

	static long GetPrimaryServerBuildVersion(LPCTSTR lpPrimary, int& nBuildNum);

	static long Verify_Username_Password(LPCTSTR lpPrimary, LPCTSTR lpMember, LPCTSTR lpUserName, LPCTSTR lpPassword);
};
#endif
