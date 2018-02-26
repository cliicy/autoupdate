#pragma once
#include <string>
#include <vector>
#include "EdgeUtility.h"

using namespace std;


#define APM_SERVERTYPE_CA			0
#define APM_SERVERTYPE_STAGING		1

//These 3 value should be consistent with the values define in UpdateExitCodeSaverThread.java
#define SETUP_UPDATE_EXITCODE_SUCCESS						L"0x0"
#define SETUP_UPDATE_EXITCODE_RESET							L"-1"
#define SETUP_UPDATE_EXITCODE_REBOOT						L"0xbc2"

#define APM_REQUEST_TYPE_UICOMMAND				L"UICommand"

#define APM_EDGE_PROCESS_RUNNING_GUID			L"Global\\44466f66-6123-4208-9846-3f5215ce5e25"
#define APM_EDGE_PROCESS_BUSY_GUID				L"Global\\592d5d85-a7fc-41dd-9db5-df6e3311aa30"

extern bool g_bContinuousMode;

namespace EdgeAPM
{
	struct PackInfo{
		PackInfo();
		wstring			m_strPackId;
		wstring			m_strDate;
		wstring			m_strDesc;
		wstring			m_strDownloadLocation;
		wstring			m_strUpdateUrl;
		int				m_iReboot;
		int				m_iSize;
		wstring			m_strUpdateVersionNumber;
		int				m_iAvailabe;
		int				m_iDownload;
		int				m_iInstall;
		wstring			m_strError;
	};

	struct Response{
		Response();
		wstring			m_strId;
		wstring			m_strType;
		int				m_iError;			//0:success, 1:read response error, 2:request failed
		wstring			m_strError;
	};

	struct ServerInfo{
		ServerInfo();
		wstring			m_strServer;
		USHORT			m_nPort;
	};

	struct ServerInfoModel{
	CString			m_strServer;
	CString			m_strPort;
	ServerInfoModel();
	};

	struct APMSettingModel{
	int				m_nServerType;
	BOOL			m_bProxy;
	ServerInfoModel				m_proxyServer;
	BOOL			m_bProxyAuth;
	CString			m_strProxyUsername;
	CString			m_strProxyPwd;
	ServerInfoModel		m_stagingServer;
	APMSettingModel();
	};

	struct APMSetting{
		APMSetting();
		int				m_nServerType;	//1:CA Server; 2:Staging Server;
		bool			m_bProxy;
		ServerInfo		m_proxyServer;
		bool			m_bProxyAuth;
		wstring			m_strProxyUsername;
		wstring			m_strProxyPwd;
		vector<ServerInfo>		m_vecStaging;
	};

	typedef struct LastModifiedDateTime
	{
		wstring wsLastModifiedDate;
		wstring wsLastModifiedTime;
	}_LAST_MODIFIED_DATE_TIME;

	typedef struct DownloadRequestStatus
	{
		DWORD dwLastErrorCode;
		wstring wsLastErrorsMessage;
		DWORD dwActivityLogMessageType;
		BOOL bIsreqActivityLogMessage;
		wstring wsFinalStatus;
		_LAST_MODIFIED_DATE_TIME stLastModifiedDateTime;
	}_DOWNLOAD_REQUEST_STATUS_INFO;

	//************************************
	// Returns: 1: format error; 2: registry access error;
	//************************************
	DWORD	GetProductVersion(UINT &majorVer, UINT &minorVer);

	//returns: 2: registry error
	DWORD	GetProductPatchVersion( int nProductId, UINT& nPatchVersion);

	DWORD	createCheckUpdateRequest(const wstring &strRequestId, wstring &strRequest, int nProductId, int nmajorver, int minorver);
	DWORD	createTerminateRequest(const wstring &strRequestId, wstring &strRequest, int nProductId, int nmajorver, int minorver);

	//************************************
	// Parameter: int command: command id
	// Parameter: int nProduct: edge produce, like VCM, Reporting, etc.
	// Parameter: wstring & strRequest: output request xml string
	// Returns: 1: fail get product version
	//************************************
	DWORD	createRequest(const wstring &strRequestId, int command, int nProduct, wstring &strRequest);

	//************************************
	// Returns: 1: xml format error
	//************************************
	DWORD	ProcessResponse(const wstring &strResponse, struct Response &response);

	//************************************
	// Returns: 1: package is not ready for install
	//************************************
	DWORD	InstallPatch(const PackInfo &pack);

	//************************************
	// Returns: 1: file is not found.
	//************************************
	//DWORD	LoadAPMSetting( const wstring &strFilePath, APMSetting &apmSetting );

	//************************************
	// Returns: 1: file is not found.
	//************************************
	//DWORD	SaveAPMSetting( const wstring &strFilePath, const APMSetting &apmSetting );

	//************************************
	// Returns: 1: AfcoreInterface.dll cannot be found; 2: back end api failed
	//************************************

	DWORD	SetRestartServerAfterPatch( int nProductId, bool bValue, OUT bool &bOriginal);


	//************************************
	// Returns: 1: other error; 2: strFilePath does not exist, or xml format error
	//************************************
	DWORD GetPackInfoFromXmlFile( const wstring &strFilePath, PackInfo &packInfo );

	//************************************
	// Returns: 1: other error; 2: strFilePath does not exist, or xml format error
	//************************************
	DWORD GetPackInfo( int nProductId, PackInfo &packInfo );

	//************************************
	//This function only test the CA server or the first staging server available
	// Returns: 1: other error; 2: fail connect to server
	//************************************
	//DWORD TestDownloadServerConnection(const APMSetting &apmSetting);
}