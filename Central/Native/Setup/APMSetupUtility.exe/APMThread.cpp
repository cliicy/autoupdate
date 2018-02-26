#include "StdAfx.h"

#include <string>
#include <vector>
#include <process.h>
#include "ARCUpdate.h"
#include "APMThread.h"
#include "Golbals.h"
#include "APMSetupUtilityDlg.h"
#include "EdgeAPM.h"
#include "APMSetupUtility.h"
#include "Utility.h"
#include "MSXMLParserWrapper.h"
#include "ApmFactory.h"
#include "ApmBackendStatus.h"
#include "StatusObserver.h"
#include "CAComponent.h"

#define EDGE_APM_EDGE_PIPE_NAME_GUID	L"\\\\.\\PIPE\\9106a996-d21f-49a6-bc9e-f4fd3ecadc9e"
#define EDGE_APM_D2D_PIPE_NAME_GUID			L"\\\\.\\PIPE\\0EC961A0-32EA-4eff-82A8-D1D08B77A053"
#define EDGE_APM_D2D_PROCESS_RUNNING_GUID	L"Global\\56E363E4-1CD4-4c2e-ADE2-71A2257DEB95"
#define EDGE_APM_D2D_PROCESS_BUSY_GUID		L"Global\\B35FAC3C-66A6-467f-B37D-567503A52A86"

using namespace std;
using namespace EdgeAPM;

extern CUpdateCheckApp theApp;
//

DWORD WINAPI RunApm( __in LPVOID lpParameter )
{
	WriteLog(_T("begin to run Apm update thread."));
//	int nPercent;
	CUpdateCheckPage *pDlg = (CUpdateCheckPage*)lpParameter;
	//
	// detect which product is installed
	//
	DWORD dwProduct = ARCUPDATE_PRODUCT_AGENT;
	if (IsProductInstalled(UDP_CONSOLE))
		dwProduct = ARCUPDATE_PRODUCT_FULL;

	//
	// load arcupdate.dll
	//
	_CARCUpdateDLL dll;
	DWORD dwErr = dll.Load(NULL);
	if (dwErr != 0)
	{
		WriteLog(_T("Failed to load ARCUpdate.dll. ERR=%d"), dwErr );
		CString strMsg;
		strMsg.LoadString(IDS_FAILED_DOWNLOAD);
		WriteLog(strMsg);
		wstring wstrMsg = strMsg;
		pDlg->PostMessage(WM_APM_STATUS_CHANGED, (WPARAM)wstrMsg.c_str(), APM_STATUS_ERROR);
		Sleep(1000);
		return 1;
	}
	FUNC_SaveUpdateServerInfo pfnSaveUpdateServerInfo = FuncOfDll<FUNC_SaveUpdateServerInfo>(dll.Dll(), "SaveUpdateServerInfo");
	FUNC_CheckUpdate pfnCheckUpdate = FuncOfDll<FUNC_CheckUpdate>(dll.Dll(), "CheckUpdate");
	FUNC_GetUpdateErrorMessage pfnGetUpdateErrorMessage = FuncOfDll<FUNC_GetUpdateErrorMessage>(dll.Dll(), "GetUpdateErrorMessage");
	FUNC_QueryUpdateStatus pfnQueryUpdateStatus = FuncOfDll<FUNC_QueryUpdateStatus>(dll.Dll(), "QueryUpdateStatus");
	FUNC_GetLastAvailableUpdate pfnGetLastAvailableUpdate = FuncOfDll<FUNC_GetLastAvailableUpdate>(dll.Dll(), "GetLastAvailableUpdate");
	if (!pfnSaveUpdateServerInfo || !pfnCheckUpdate || !pfnGetUpdateErrorMessage ||
		!pfnQueryUpdateStatus || !pfnGetLastAvailableUpdate)
	{
		WriteLog(_T("Failed to get proc addresss of arcupdate.dll") );
		CString strMsg;
		strMsg.LoadString(IDS_FAILED_DOWNLOAD);
		WriteLog(strMsg);
		wstring wstrMsg = strMsg;
		pDlg->PostMessage(WM_APM_STATUS_CHANGED, (WPARAM)wstrMsg.c_str(), APM_STATUS_ERROR);
		Sleep(1000);
		return 1;
	}


	CString strDownloadedUpdateFile = L"";
	ARCUPDATE_SERVER_INFO updateSvr;
	if (pDlg->GetApmSetting()->m_nServerType)
	{
		updateSvr.serverType = ARCUPDATE_SERVER_STAGE;
		wcsncpy_s(updateSvr.downloadServer, _countof(updateSvr.downloadServer), pDlg->GetApmSetting()->m_vecStaging[0].m_strServer.c_str(), _TRUNCATE);
		updateSvr.nServerPort = pDlg->GetApmSetting()->m_vecStaging[0].m_nPort;
		WriteLog(_T("Staging server setting:%s %d"), pDlg->GetApmSetting()->m_vecStaging[0].m_strServer.c_str(), pDlg->GetApmSetting()->m_vecStaging[0].m_nPort);
	}
	else
	{
		updateSvr.serverType = ARCUPDATE_SERVER_DEFAULT;
		if (!pDlg->GetApmSetting()->m_bProxy)
		{
			updateSvr.bDefaultIEProxy = TRUE;
		}
		else
		{
			updateSvr.bDefaultIEProxy = FALSE;
			wcsncpy_s(updateSvr.proxyServerName, _countof(updateSvr.proxyServerName), pDlg->GetApmSetting()->m_proxyServer.m_strServer.c_str(), _TRUNCATE);
			wcsncpy_s(updateSvr.proxyUserName, _countof(updateSvr.proxyUserName), pDlg->GetApmSetting()->m_strProxyUsername.c_str(), _TRUNCATE);
			wcsncpy_s(updateSvr.proxyPassword, _countof(updateSvr.proxyPassword), pDlg->GetApmSetting()->m_strProxyPwd.c_str(), _TRUNCATE);
			updateSvr.proxyServerPort = pDlg->GetApmSetting()->m_proxyServer.m_nPort;
		}
	}

	WriteLog(_T("Save Apm setting..."));
	pfnSaveUpdateServerInfo(dwProduct, &updateSvr);

	//
	// start to checking for updates
	//
	WriteLog(_T("start to download patch."));	
	dwErr = pfnCheckUpdate(dwProduct, dwProduct, &updateSvr, FALSE);
	if (0 != dwErr)
	{
		wstring wstrMsg = L"";
		pfnGetUpdateErrorMessage(dwProduct, dwErr, wstrMsg);
		pDlg->PostMessage(WM_APM_STATUS_CHANGED, (WPARAM)wstrMsg.c_str(), APM_STATUS_ERROR);
		Sleep(1000);
		return 1;
	}

	//
	// wait until checking for updates end
	//
	UPDATE_JOB_MONITOR updateJobMonitor;
	while (1)
	{
		ZeroMemory(&updateJobMonitor, sizeof(updateJobMonitor));
		dwErr = pfnQueryUpdateStatus(dwProduct, &updateJobMonitor);
		WriteLog(_T("Failed to query update downloading status. ERR=%d"), dwErr);
		if (dwErr!= 0)
			break;

		if (updateJobMonitor.dwJobPhase == AJP_END){
			dwErr = updateJobMonitor.lLastError;
			break;
		}

		if (updateJobMonitor.dwJobPhase == AJP_DOWNLOADING)
		{
			WriteLog(_T("Downloading update [%I64d / %I64d]"), updateJobMonitor.ullDownloadedSize, updateJobMonitor.ullTotalSize);
			int nPercent = 0;
			if (updateJobMonitor.ullDownloadedSize != 0)
				nPercent = (double)( (updateJobMonitor.ullDownloadedSize / updateJobMonitor.ullTotalSize*2.0) ) * 100;
			pDlg->PostMessage(WM_APM_STATUS_CHANGED, 0, APM_STATUS_DOWNLOADING);
			pDlg->PostMessage(WM_APM_STATUS_CHANGED, nPercent + 15, APM_STATUS_PROCESS_PERCENT);
		}
		Sleep(2000);
	}
		
	WCHAR szDownloadedFile[1024] = { 0 };
	DWORD dwFilePathSize = _countof(szDownloadedFile);
	WCHAR szVersion[MAX_PATH] = { 0 };
	DWORD dwSizeOfVersion = _countof(szVersion);
	BOOL bReboot = FALSE;
	if(dwErr == 0)
	{	
		pfnGetLastAvailableUpdate(dwProduct, szVersion, &dwSizeOfVersion, szDownloadedFile, &dwFilePathSize, &bReboot);
		WriteLog(_T("The file %s has been downloaded successfully."), szDownloadedFile );
		if(bReboot)
			theApp.m_breboot = TRUE;
		pDlg->PostMessage( WM_APM_STATUS_CHANGED, 70, APM_STATUS_INSTALL_PATCH);
	}
	else
	{
		CString m_strMsg;
		wstring wstrMsg;
		switch(dwErr)
		{
			case ARCUPDATE_ERROR_NO_UPDATE_FOUND:
				m_strMsg.LoadString(IDS_NO_UPDATE);
				wstrMsg = m_strMsg;
				pDlg->PostMessage( WM_APM_STATUS_CHANGED,  (WPARAM)wstrMsg.c_str(), APM_STATUS_BACKEND_MESSAGE);
				Sleep(1000);
				return 0;
			case ARCUPDATE_ERROR_UP_TO_DATE:
				m_strMsg.LoadString(IDS_ALREADY_UPDATE);
				break;
			case ARCUPDATE_ERROR_FAILED_TO_DOWNLOAD:
				m_strMsg.LoadString(IDS_FAILED_DOWNLOAD);
				break;
			case ARCUPDATE_ERROR_SERVER_UNAVAILABLE:
				m_strMsg.LoadString(IDS_SERVER_UNAVAILABLE);
				break;
			case ARCUPDATE_ERROR_CANCELED:
				m_strMsg.LoadString(IDS_UPDATE_CANCLE);
				break;
			default:
				m_strMsg.LoadString(IDS_ERROR_UNKNOWN);
				m_strMsg.Format(m_strMsg, dwErr);
				break;
		}
		WriteLog(m_strMsg);
		wstrMsg = m_strMsg;
		pDlg->PostMessage( WM_APM_STATUS_CHANGED,  (WPARAM)wstrMsg.c_str(), APM_STATUS_ERROR);
		Sleep(1000);
		return 1;
	}
	::Sleep(2000);
	WriteLog(_T("Start to install UDP patch"));
	CString strcmd = _T("");
	strcmd = szDownloadedFile;
	strcmd = _T("\"") + strcmd + _T("\"") + _T(" /s /a /s /NoAllSessionRebootPopup");
	DWORD m_dwExitCode;
	WriteLog(_T("begin to install updates, the command line: %s"), strcmd);
	pDlg->PostMessage( WM_APM_STATUS_CHANGED, 85, APM_STATUS_PROCESS_PERCENT);
	LaunchProcess(strcmd, NULL, m_dwExitCode, INFINITE, DETACHED_PROCESS|CREATE_NO_WINDOW);
	if(m_dwExitCode == ERROR_SUCCESS)
	{
		pDlg->PostMessage( WM_APM_STATUS_CHANGED, (WPARAM)NULL, APM_STATUS_INSTALL_FINISHED);
	}
	else if(m_dwExitCode == ERROR_SUCCESS_REBOOT_REQUIRED)
	{
		theApp.m_breboot = TRUE;
		pDlg->PostMessage( WM_APM_STATUS_CHANGED, (WPARAM)NULL, APM_STATUS_INSTALL_FINISHED);
	}
	else
	{
		pDlg->PostMessage( WM_APM_STATUS_CHANGED, (WPARAM)m_dwExitCode, APM_STATUS_INSTALL_FAILED);
	}
	

	WriteLog(_T("finish to check updates"));
	return 0;
}


DWORD getProductsToDownloadPatch( OUT int arrProductId[], IN OUT int &nSize )
{
	int i = 1;
	BOOL bCMInstalled = IsProductInstalled( APM_EDGE_CM );
	
	//if( nSize < i )
	//	return 1;
	//arrProductId[i-1] = APM_EDGE_COMMON;
	//i++;
	
	if( nSize < i)
		return 1;
	if( bCMInstalled ){
		arrProductId[i-1] = APM_EDGE_CM;
		i++;
	}

	if( nSize < i)
		return 1;
	if( bCMInstalled || IsProductInstalled( APM_EDGE_VCM) ){
		arrProductId[i-1] = APM_EDGE_VCM;
		i++;
	}

	if( nSize < i)
		return 1;
	if( bCMInstalled || IsProductInstalled( APM_EDGE_VSPHERE) ){
		arrProductId[i-1] = APM_EDGE_VSPHERE;
		i++;
	}

	if( nSize < i)
		return 1;
	if( bCMInstalled || IsProductInstalled( APM_EDGE_REPORT) ){
		arrProductId[i-1] = APM_EDGE_REPORT;
		i++;
	}

	nSize = i - 1;
	return 0;
}

DWORD getFirstXmlDoc( IN const wstring &strSource, OUT wstring &strFirstXml )
{
	wstring strXmlBegin = _T("<?xml");
	wstring::size_type idx = 0;

	idx = strSource.find( strXmlBegin );
	if( idx == wstring::npos ){
		strFirstXml.clear();
		return 1;
	}

	idx++;
	idx = strSource.find( strXmlBegin, idx );
	strFirstXml = strSource.substr( 0, idx );

	return 0;
}


std::wstring generateRequestId()
{
	static int id = 0;
	WCHAR buf[10];
	wstring strId;
	
	_itow(id++, buf, 10);
	strId = wstring(_T("UICommand_")) + buf;
	return strId;
}

wstring generateMessgeForProduct( int nProductId, const wstring &strMessage )
{
	WCHAR *strBuf = new WCHAR[1024];
	const int nBufSize = 1024;
	UINT resourcId;

	switch( nProductId ){
		case APM_EDGE_CM:
			resourcId = IDS_productNameCM;
			break;
		case APM_EDGE_VCM:
			resourcId = IDS_productNameVCM;
			break;
		case APM_EDGE_VSPHERE:
			resourcId = IDS_productNameVsphere;
			break;
		case APM_EDGE_REPORT:
			resourcId = IDS_productNameReport;
			break;
	}

	LoadString(GetModuleHandle(FILE_APM_RESOURCE_DLL),
		resourcId, strBuf, nBufSize );

	wcscat_s( strBuf, nBufSize, _T(":\r\n"));
	wcscat_s( strBuf, nBufSize, strMessage.c_str() );

	return wstring(strBuf);
}

DWORD getProductsToInstallPatch( OUT int arrProductId[], IN OUT int &nSize )
{
	int i = 1;
	BOOL bCMInstalled = IsProductInstalled( APM_EDGE_CM );

	//if( nSize < i )
	//	return 1;
	//arrProductId[i-1] = APM_EDGE_COMMON;
	//i++;

	if( nSize < i)
		return 1;
	if( bCMInstalled ){
		arrProductId[i-1] = APM_EDGE_CM;
		i++;
	}

	if( nSize < i)
		return 1;
	if( IsProductInstalled( APM_EDGE_VCM) ){
		arrProductId[i-1] = APM_EDGE_VCM;
		i++;
	}

	if( nSize < i)
		return 1;
	if( IsProductInstalled( APM_EDGE_VSPHERE) ){
		arrProductId[i-1] = APM_EDGE_VSPHERE;
		i++;
	}

	if( nSize < i)
		return 1;
	if( IsProductInstalled( APM_EDGE_REPORT) ){
		arrProductId[i-1] = APM_EDGE_REPORT;
		i++;
	}

	nSize = i - 1;
	return 0;
}

std::wstring loadString( UINT resourceId )
{
	WCHAR buf[1024];
	const int nBufSize = 1024;

	memset( buf, 0, sizeof(buf) );
	LoadString(GetModuleHandle(FILE_APM_RESOURCE_DLL),
		resourceId, buf, nBufSize );

	return wstring(buf);
}

std::wstring generateMessage( const vector<UpdateMessage> &vecUpdateMessage )
{
	wstring strMessage;
	vector<UpdateMessage>::const_iterator itr;

	if( !vecUpdateMessage.empty() ){
		for( itr = vecUpdateMessage.begin(); itr != vecUpdateMessage.end(); itr++ ){
			wstring str = itr->m_strError;
			strMessage += str + _T("\r\n\r\n");
		}
	}
	return strMessage;
}

