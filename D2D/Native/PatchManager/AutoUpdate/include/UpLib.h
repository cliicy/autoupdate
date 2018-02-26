#pragma once
#include <Windows.h>
#include "UpdateDefines.h"
#include "DbgLog.h"
#include <string>
#include <vector>
using namespace std;

//
// the functions used to handle path related...
//
namespace PATHUTILS
{
	// standlize a path - remove ".", "..", convert "/" to "\"
	std::wstring standlize_path(const std::wstring& strFullPath);

	// the home directory of current running application
	std::wstring home_dir();

	// ensure a path is end with "\"
	void path_ensure_end_with_slash(std::wstring& strPath);

	// ensure a path is ended without "\"
	void path_ensure_end_without_slash(std::wstring& strPath);

	// ensure a path is ended without "/"
	//void path_ensure_end_without_backslash(std::wstring& strPath);

	// check a path is started with "\"
	bool path_check_with_website(std::wstring& strPath);

	// join two paths 
	wstring path_join(const std::wstring& parent, const std::wstring& sub);

	// get all files under specified folder
	void files_under_folder(const std::wstring& str_folder, const wstring& strFormat, std::vector<wstring>& vec_files, bool bFullPath = true);

	// sub folders under specified path
	void sub_folders(const std::wstring& strParentFullPath, std::vector<std::wstring>& vecSubFolders, bool bFullPath = true);

	// detect if the given folder exists
	bool is_folder_exist(const std::wstring& strFolder);

	// detect if the given file exists
	bool is_file_exist(const std::wstring& strFile);

	// given a full path, return the file name only
	std::wstring file_name_of_path(const std::wstring& strFullPath);

	// given a full path, return the path of folder.
	std::wstring folder_of_path(const std::wstring& fullpath);

	// create folders and sub folders
	bool create_folder(const std::wstring& strfolder, bool bRecursivly = true);

	// calclate the MD5 of given file
	std::wstring md5_of_file(const std::wstring& strFilePath );

	// get the size of file
	ULONGLONG size_of_file(const std::wstring& strFile);
};

//
// the functions used to handle string related...
//
namespace STRUTILS
{
	// format a string
	std::wstring fstr(LPCWSTR pszFormat, ...);

	// convert string to wstring
	std::wstring str2wstr(const std::string& strA);

	// convert wstring to string
	std::string  wstr2str(const std::wstring& strW);

	// detect if the two string are same
	bool same_str(const std::wstring& str1, const std::wstring& str2, bool bCaseSensitive = false);

	// split a string
	void split_str(const std::wstring& str, wchar_t chSpliter, std::vector<wstring>& vecStrings);

	// convert guid to string
	std::wstring guid2str(GUID guid);

	// construct a URL, it is to remove L"//" from the URL
	std::wstring construct_url(LPCWSTR pszFormat, ...);

	// replace string
	void replace_str(std::wstring& str, const std::wstring& strOld, const std::wstring& strNew);

	// convert a string to boolean
	bool str2boolean(const wstring& str, bool bDefault);

	void trim_str(std::wstring& str, wchar_t ch=L' ');

	void trim_str(std::string& str, char ch = L' ');

	void trim_str_right(std::wstring& str, wchar_t ch = L' ');

	void trim_str_right(std::string& str, char ch=L' ');

	void trim_str_left(std::wstring& str, wchar_t ch = L' ');

	void trim_str_left(std::string& str, char ch = L' ');
};

//
// the functions used to encrypt / decrupt...
//
namespace ENCUTILS
{
	int		EncryptData(void *pvData, DWORD* pnDataSize, DWORD nBufSize);
	
	int		DecryptData(void *pvData, DWORD* pnDataSize);

	BOOL	DecryptFromString(const wchar_t *pszStr, std::wstring& strOutput);
	
	BOOL	EncryptToString(const wchar_t *pszStr, std::wstring& strOutput);
};

//
// the functions to get product information
//
namespace PRODUTILS
{
	// get UDP home path 
	//
	wstring		GetUDPHome();

	//
	// get the update manager home path
	//
	wstring		GetUpdateManagerHome();

	//
	// get the home path of product
	//
	wstring		GetProductHome(DWORD dwProd);

	//
	// get product version
	//
	DWORD		GetProductVersion(DWORD dwProd, UDP_VERSION_INFO& prodVer);

	//
	// detect if agent is managed by console
	//
	BOOL		IsAgentManagedByConsole();	

	//
	// detect if gateway in installed
	//
	BOOL		IsGatewayInstalled();
};

namespace UPUTILS
{
	//
	// get the download home directory
	//
	wstring		GetDownloadHomeDirectory(DWORD dwProd);

	//
	// get the download home directory added by cliicy.luo
	//
	wstring		GetBIDownloadHomeDirectory(DWORD dwProd);

	//
	// Get the server URL of prodcut
	// this URL is defined in file "\Update Manager\UpdateURL.xml"
	//
	DWORD		GetDefaultUpdateServerInfo(DWORD dwProd, CA_UPDATE_SERVER_INFO& upSvrInfo);

	//
	// given a product cod, return its product name
	//
	wstring		GetProductName(DWORD dwProd);

	//
	// used for debug only
	//
	BOOL		IgnoreSign();

	//
	// get retry count
	//
	int			GetDownloadRetryCount();

	//
	// get signature
	//
	wstring		GetUpdateSignature();

	//
	// get the update status file
	//
	wstring		GetUpdateStatusFile(DWORD dwProd);

	wstring		GetBIUpdateStatusFile(DWORD dwProd);//added by cliicy.luo
		
	//
	// get the update setting xml file
	//
	wstring		GetUpdateSettingXmlFile(DWORD dwProd, BOOL bCreateIfNotExists = FALSE);

	//
	// get the file "updatecfg.ini"
	//
	wstring		GetUpdateCfgFile();

	//
	// get the file "arcserve.sig"
	//
	wstring		GetUpdateSigFile();

	//
	// read update settings from file
	//
	void		ReadUpdateSettingsFromFile(const wstring& strFile, UDP_UPDATE_SETTINGS& updateSettings);

	//
	// save update settings to file
	//
	DWORD		SaveUpdateSettingsToFile(const wstring& strFile, const UDP_UPDATE_SETTINGS& updateSettings);

	//
	// get admin username and password
	//
	DWORD		GetAdminUserOfProduct(DWORD dwProd, wstring& strUser, wstring& strPassword);

	//
	// get the resource string
	//
	wstring		GetUpdateResourceString(DWORD dwMsgID, ...);

	wstring		GetUpdateResourceStringEx(DWORD dwMsgID, va_list* pArgList = NULL);

	//
	// get the last available update information
	//
	DWORD		GetLastAvailableUpdateOfProduct(DWORD dwProduct, UDP_VERSION_INFO& version, wstring& strFilePath, PBOOL pbRebootRequired=NULL);

	//
	// get the last available update infor of self update
	//
	DWORD		GetLastAvailableSelfUpdate(DWORD& dwVersion, wstring& strFilePath);

	//
	// compare UDP version
	//
	int			CompareUDPVersion(const UDP_VERSION_INFO& v1, const UDP_VERSION_INFO& v2);

	//
	// get the internal version of update
	//
	DWORD		GetUpdateInternalInfo(DWORD& dwUpdate, DWORD& dwBuild, DWORD& dwPatch, DWORD& dwSvrRole);

	//
	// get auto update internal version
	//
	DWORD		GetAutoupdateVersion();

	//
	// get the URL on specified server
	//
	wstring		GetURLOfFileOnServer(DWORD dwProdCode, ARCUPDATE_SERVER_INFO* pSvrInf, const wstring& strFile, const wstring& strPathDir = ARCUPDATE_SITE_RELEASE_VERSION );

	//
	// convert a UDP version to a string, "5.0.0.1897.1.256"
	//
	wstring		VersionToString(const UDP_VERSION_INFO& vi);

	//
	// convert a string to UDP Version
	//
	void		VersionFromString(const wstring& strVersion, UDP_VERSION_INFO& vi);
};

//
// the functions to write activity log
//
namespace ACTLOGUTILS
{
	void ActivityLog(DWORD dwProduct, DWORD dwLevel, DWORD dwMsgID, ...);

	void ActivityLogEx(DWORD dwProduct, DWORD dwLevel, const wstring& strMsg);
};

class CThreadBase
{
public:
	CThreadBase(BOOL bSelfDelete = FALSE)
		: m_bSelfDelete(bSelfDelete)
		, m_hThread(NULL)
		, m_dwThreadId(0)
	{
	}

	virtual ~CThreadBase()
	{
		if (m_hThread != NULL)
			CloseHandle(m_hThread);
		m_hThread = NULL;
		m_dwThreadId = 0;
	}

	virtual DWORD Start()
	{
		m_hThread = ::CreateThread(NULL,
			0,
			(LPTHREAD_START_ROUTINE)CThreadBase::_thread_func_,
			this,
			CREATE_SUSPENDED,
			&m_dwThreadId);
		if (m_hThread == NULL)
			return GetLastError();

		::ResumeThread(m_hThread);

		if (m_bSelfDelete)
		{
			CloseHandle(m_hThread);
			m_hThread = NULL;
			m_dwThreadId = 0;
		}
		return 0;
	}

	// subclass will override this function if necessary
	virtual DWORD Stop()
	{
		return 0;
	}

	// the thread routinue
	virtual DWORD Main() = 0;

	virtual DWORD Wait(DWORD dwMilliseconds)
	{
		if (m_hThread == NULL)
			return WAIT_FAILED;
		return ::WaitForSingleObject(m_hThread, dwMilliseconds);
	}

	virtual BOOL Terminate(DWORD dwExitCode)
	{
		if (m_hThread == NULL)
			return TRUE;

		DWORD exitCode = 0;
		::GetExitCodeThread(&m_hThread, &exitCode);
		if (exitCode == STILL_ACTIVE)
			return ::TerminateThread(m_hThread, dwExitCode);
		return TRUE;
	}

	virtual HANDLE GetHandle()
	{
		return m_hThread;
	}

	virtual DWORD GetThreadId()
	{
		return m_dwThreadId;
	}
protected:
	static DWORD WINAPI _thread_func_(LPVOID pArg)
	{
		if (!pArg) return 87;
		CThreadBase* p = (CThreadBase*)pArg;
		DWORD dwRet = p->Main();
		if (p->m_bSelfDelete)
			delete p;
		return dwRet;
	}

protected:
	HANDLE m_hThread;
	DWORD  m_dwThreadId;
	BOOL   m_bSelfDelete;
};

//
// the baseclass of job monitor
//
class IUpdateJobMonitor
{
public:
	virtual void	Release() = 0;

	virtual void	CancelUpdateJob() = 0;

	virtual BOOL	IsJobCanceled() = 0;

	virtual void	UpdateJobPhase(DWORD dwJobPhase) = 0;

	virtual DWORD	GetJobPhase() = 0;

	virtual void	UpdateTotalSize(ULONGLONG ullSize) = 0;

	virtual void	InitDownloadedSize(ULONGLONG ullSize) = 0;

	virtual void	UpdateDownloadedSize(ULONGLONG ullSize) = 0;

	virtual void	StartUpdateJob() = 0;

	virtual void	EndUpdateJob(DWORD dwStatus, LONG lLastError) = 0;

	virtual DWORD	GetJobStatus() = 0;

	virtual DWORD	GetDataOfJobMonitor(UPDATE_JOB_MONITOR& pData) = 0;

	virtual LONG	GetLastError() = 0;

	virtual void	UpdateProcessID(DWORD dwProcID) = 0;
};
DWORD CreateIUpdateJobMonitor(DWORD dwJobID, IUpdateJobMonitor** pJobMonitor);
DWORD OpenIUpdateJobMonitor(DWORD dwJobID, IUpdateJobMonitor** pJobMonitor);


//
// the class to do impersonation
//
class CImpersonate
{
public:
	CImpersonate();

	~CImpersonate();

	void  LogonOnWithUser( const wstring& strUserName, const wstring& strPassword );

protected:
	void  GetStandardAccount(const wstring &strUserName, wstring &strDomain, wstring &strUser);

	BOOL  IsBadAccount(const wstring& strUsername, const wstring& strPassword);

	DWORD SaveBadAccount(const wstring& strUsename, const wstring& strPassword);

protected:
	CDbgLog m_log;
	BOOL    m_bOK;
};

//
// the class to download file
//
class IDownloader
{
public:
	virtual DWORD		DownloadFile(const wstring& strUrlOfFile, const wstring& strDstFile) = 0;

	virtual DWORD		TestHttpConnection(const wstring& strUrlOfFile) = 0;

	virtual void		Reset() = 0;

	virtual void		Release() = 0;

	virtual ULONGLONG	GetDownloadedSize() = 0;
};

DWORD CreateHttpDownloader( ARCUPDATE_SERVER_INFO* pSvrInfo, IUpdateJobMonitor* pJobMonitor, IDownloader** ppDownloader );
