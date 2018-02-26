#include "StdAfx.h"
#include "UDPBIUpdateJob.h"
#include "XXmlNode.h"
#include "..\UpdateJob\Cryptography.h"
#include <atlstr.h>
#include "..\UpdateJob\UpdateJobScript.h"
#include "..\include\UpdateError.h"
#include "winhttp.h"
#include "Log.h"
#include "..\UpdateRes\ARCUpdateRes.h"

extern PUPDATE_JOB_SCRIPT    g_pJobBIScript;

CUDPBIUpdateJob::CUDPBIUpdateJob()
{

}

CUDPBIUpdateJob::~CUDPBIUpdateJob(void)
{
	SAFE_DELETE(m_pUpdateInfo);
}

LONG CUDPBIUpdateJob::Run()
{
	m_log.LogW(LL_INF, 0, L"ooooo enter %s", __WFUNCTION__);
	
	g_pJobBIScript->pJobMonitor->UpdateJobPhase(AJP_CONNECTING);
	
	LONG lLastError = 0;
	CImpersonate doImpersonate;
	doImpersonate.LogonOnWithUser(g_pJobBIScript->adminUsername, g_pJobBIScript->adminPassword );
	do
	{
		break_if_canceled(inter_error_update_canceled);

		lLastError = initJobScript();

		break_if_canceled(inter_error_update_canceled);
		break_if(lLastError != 0);

		

		//
		// if the caller specified a http settings, will use it for download
		//
		if (g_pJobBIScript->pUpdateSvrInfo)
		{
			CAvailableBIUpdateDll* pTempInfo = NULL;
			IDownloader* pTempDownloader = NULL;
			m_log.LogW(LL_INF, 0, L"%s: oooo will create a job downloader object 49", __WFUNCTION__);
			CreateHttpDownloader(g_pJobBIScript->pUpdateSvrInfo, g_pJobBIScript->pJobMonitor, &pTempDownloader);
			lLastError = detectUpdateServer(pTempDownloader, g_pJobBIScript->pUpdateSvrInfo, &pTempInfo);
			if (lLastError == 0)
			{
				m_pUpdateInfo = pTempInfo;
				g_pJobBIScript->pDownloader = pTempDownloader;
			}
			else
			{
				SAFE_RELEASE(pTempDownloader);
				SAFE_DELETE(pTempInfo);
			}
		}
		else
		{
			m_log.LogW(LL_INF, 0, L"%s: oooo will selectUpdateServer 65", __WFUNCTION__);
			lLastError = selectUpdateServer();
		}		
		break_if_canceled(inter_error_update_canceled);
		break_if(lLastError != 0);
		

		//
		// detect if self update required.
		//
		if (m_pUpdateInfo->GetRequiredVersionOfAutoUpdate() > UPUTILS::GetAutoupdateVersion() )
		{
			m_log.LogW(LL_INF, 0, L"%s: A self update required. CurVer[%d], RequiredVer[%d]", __WFUNCTION__, UPUTILS::GetAutoupdateVersion(), m_pUpdateInfo->GetRequiredVersionOfAutoUpdate() );
			lLastError = inter_error_selfupdate_required;
			break;
		}
		
		//
		// verify if need to continue download update
		//
		lLastError = validateUpdateInfo(m_pUpdateInfo);
		break_if_canceled(inter_error_update_canceled);
		break_if(lLastError != 0);

		lLastError = downloadUpdate( );
		break_if_canceled(inter_error_update_canceled);
		break_if(lLastError != 0);

		lLastError = postDownload();
		break_if(lLastError != 0);
		
	} while (0);
	
	
	cleanFolder(g_pJobBIScript->tmpDownloadDir, TRUE);

	lLastError = handleErrors(lLastError);
	DWORD dwStatus = AJS_COMPLETED;
	if (isUpdateCanceled())
		dwStatus = AJS_CANCELED;
	else if (lLastError != 0)
		dwStatus = AJS_FAILED;
	g_pJobBIScript->pJobMonitor->EndUpdateJob(dwStatus, lLastError);	
	m_log.LogW(LL_INF, 0, L"UDP update ended with %d", lLastError);
	return lLastError;
}

BOOL CUDPBIUpdateJob::isUpdateCanceled()
{
	return g_pJobBIScript->pJobMonitor->IsJobCanceled();
}


LONG CUDPBIUpdateJob::cleanFolder(const wstring& strFolder, BOOL bIncludeSelf)
{
	LONG lRet = 0;
	if (!PATHUTILS::is_folder_exist(strFolder))
		return lRet;

	std::vector<wstring> vecFiles;
	PATHUTILS::files_under_folder(strFolder, L"", vecFiles, true);
	for (size_t i = 0; i < vecFiles.size(); i++)
	{
		if (!safeDeleteFile(vecFiles[i]))
		{
			lRet = GetLastError();
			m_log.LogW(LL_ERR, lRet, L"%s: Failed to delete file %s", __WFUNCTION__, vecFiles[i].c_str());
			break;
		}
	}
	if (lRet != 0)
		return lRet;

	std::vector<wstring> vecSubFolders;
	PATHUTILS::sub_folders(strFolder, vecSubFolders, true);
	for (size_t i = 0; i < vecSubFolders.size(); i++){
		lRet = cleanFolder(vecSubFolders[i], true);
		if (lRet != 0){
			m_log.LogW(LL_ERR, lRet, L"%s: Failed to clean folder %s", __WFUNCTION__, vecSubFolders[i].c_str());
			break;
		}
	}

	if (lRet != 0)
		return lRet;

	if (bIncludeSelf && !::RemoveDirectory(strFolder.c_str()))
	{
		lRet = GetLastError();
		m_log.LogW(LL_ERR, lRet, L"%s: Failed to remove directory %s", __WFUNCTION__, strFolder.c_str());
	}
	return lRet;
}


BOOL CUDPBIUpdateJob::safeDeleteFile(const wstring& strFile)
{
	BOOL bRet = TRUE;
	if (!PATHUTILS::is_file_exist(strFile))
		return TRUE;

	::SetFileAttributes(strFile.c_str(), FILE_ATTRIBUTE_NORMAL);
	for (int i = 0; i < 60; i++)
	{
		if (!PATHUTILS::is_file_exist(strFile))
		{
			bRet = TRUE;
			break;
		}

		bRet = ::DeleteFile(strFile.c_str());
		if (!bRet)
			Sleep(1000);
		else
			break;
	}
	return bRet;
}

BOOL CUDPBIUpdateJob::safeMoveFile(const wstring& strSrcFile, const wstring& strDstFile)
{
	BOOL bRet = TRUE;

	bRet = safeDeleteFile(strDstFile);
	if (!bRet)
		return bRet;

	for (int i = 0; i < 60; i++)
	{
		bRet = ::MoveFile(strSrcFile.c_str(), strDstFile.c_str());
		if (!bRet)
			Sleep(1000);
		else
			break;
	}
	return bRet;
}

DWORD CUDPBIUpdateJob::moveFolder(const wstring& strSrcFolder, const wstring& strDstFolder, bool bCleanDestination /*= false*/)
{
	DWORD dwRet = 0;

	if (!PATHUTILS::is_folder_exist(strDstFolder))
	{
		if (!PATHUTILS::create_folder(strDstFolder))
		{
			dwRet = GetLastError();
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to create folder %s", __WFUNCTION__, strDstFolder.c_str());
			return dwRet;
		}
	}
	else
	{
		if (bCleanDestination)
		{
			dwRet = cleanFolder(strDstFolder, false);
			if (dwRet != 0)
			{
				m_log.LogW(LL_ERR, dwRet, L"%s: Failed to clear folder %s", __WFUNCTION__, strDstFolder.c_str());
				return dwRet;
			}
		}
	}

	std::vector<wstring> vecFiles;
	PATHUTILS::files_under_folder(strSrcFolder, L"", vecFiles, false);
	for (size_t i = 0; i < vecFiles.size(); i++)
	{
		wstring strSrcFile = PATHUTILS::path_join(strSrcFolder, vecFiles[i]);
		wstring strDstFile = PATHUTILS::path_join(strDstFolder, vecFiles[i]);
		if (!safeMoveFile(strSrcFile, strDstFile))
		{
			dwRet = GetLastError();
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to move file [%s] to [%s]", __WFUNCTION__, strSrcFile.c_str(), strDstFile.c_str());
			return dwRet;
		}
	}

	std::vector<wstring> vecSubFolders;
	PATHUTILS::sub_folders(strSrcFolder, vecSubFolders, false);
	for (size_t i = 0; i < vecSubFolders.size(); i++)
	{
		wstring strSrcSubFolder = PATHUTILS::path_join(strSrcFolder, vecSubFolders[i]);
		wstring strDstSubFolder = PATHUTILS::path_join(strDstFolder, vecSubFolders[i]);
		dwRet = moveFolder(strSrcSubFolder, strDstSubFolder, bCleanDestination);
		if (dwRet != 0)
			return dwRet;
	}

	return dwRet;
}


LONG CUDPBIUpdateJob::initJobScript()
{
	LONG lLastError = 0;
	do
	{
		//
		// read the update settings
		//
		if (g_pJobBIScript->pUpdateSvrInfo == NULL)
		{
			if (!PATHUTILS::is_file_exist(g_pJobBIScript->preferenceFile))
			{
				lLastError = inter_error_invalid_config_file;
				m_log.LogW(LL_ERR, lLastError, L"%s: Did not find file %s", __WFUNCTION__, g_pJobBIScript->preferenceFile.c_str());
				break;
			}

			UPUTILS::ReadUpdateSettingsFromFile(g_pJobBIScript->preferenceFile, m_upSettings);
		}


		//
		// init the dirs for downloading
		//
		g_pJobBIScript->tmpDownloadDir = g_pJobBIScript->downloadDir + L".downloading";
		PATHUTILS::create_folder(g_pJobBIScript->downloadDir, true);
		PATHUTILS::create_folder(g_pJobBIScript->tmpDownloadDir, true);


		if (!PATHUTILS::is_folder_exist(g_pJobBIScript->downloadDir))
		{
			lLastError = inter_error_folder_not_found;
			m_log.LogW(LL_ERR, lLastError, L"%s: Failed to create folder %s", __WFUNCTION__, g_pJobBIScript->downloadDir.c_str());
			break;
		}
		if (!PATHUTILS::is_folder_exist(g_pJobBIScript->tmpDownloadDir))
		{
			lLastError = inter_error_folder_not_found;
			m_log.LogW(LL_ERR, lLastError, L"%s: Failed to create folder %s", __WFUNCTION__, g_pJobBIScript->tmpDownloadDir.c_str());
			break;
		}
	} while (0);

	return lLastError;
}

LONG CUDPBIUpdateJob::selectUpdateServer()
{
	LONG lLastError = 0;
	do
	{
		if (m_upSettings.nServerType == ARCUPDATE_SERVER_DEFAULT)
		{
			m_log.LogW(LL_INF, 0, L"%s: oooo will select default server 119", __WFUNCTION__);
			ARCUPDATE_SERVER_INFO* pTempSvrInfo = new ARCUPDATE_SERVER_INFO();
			pTempSvrInfo->serverType = ARCUPDATE_SERVER_DEFAULT;
			CA_UPDATE_SERVER_INFO defSvrInfo;
			UPUTILS::GetDefaultUpdateServerInfo(g_pJobBIScript->dwProdCode, defSvrInfo);
			wcsncpy_s(pTempSvrInfo->downloadServer, _countof(pTempSvrInfo->downloadServer), defSvrInfo.strServerName.c_str(), _TRUNCATE);
			pTempSvrInfo->nServerPort = 80;

			pTempSvrInfo->bDefaultIEProxy = m_upSettings.ieProxy.bDefaultIEProxy;
			if (!pTempSvrInfo->bDefaultIEProxy)
			{
				pTempSvrInfo->proxyServerPort = m_upSettings.ieProxy.nProxyPort;
				wcsncpy_s(pTempSvrInfo->proxyServerName, _countof(pTempSvrInfo->proxyServerName),m_upSettings.ieProxy.proxyServer.c_str(), _TRUNCATE);
				wcsncpy_s(pTempSvrInfo->proxyPassword, _countof(pTempSvrInfo->proxyPassword), m_upSettings.ieProxy.proxyPassword.c_str(), _TRUNCATE);
				wcsncpy_s(pTempSvrInfo->proxyUserName, _countof(pTempSvrInfo->proxyUserName), m_upSettings.ieProxy.proxyUserName.c_str(), _TRUNCATE);
			}			

			CAvailableBIUpdateDll* pTempInfo = NULL;
			IDownloader* pTempDownloader = NULL;
			m_log.LogW(LL_INF, 0, L"%s: oooo will create a job downloader object 136", __WFUNCTION__);
			CreateHttpDownloader(pTempSvrInfo, g_pJobBIScript->pJobMonitor, &pTempDownloader);
			lLastError = detectUpdateServer(pTempDownloader, pTempSvrInfo, &pTempInfo);
			if (lLastError != 0)
			{
				SAFE_DELETE(pTempSvrInfo);
				SAFE_DELETE(pTempInfo);
				SAFE_RELEASE(pTempDownloader);
			}
			else
			{
				m_pUpdateInfo = pTempInfo;
				g_pJobBIScript->pUpdateSvrInfo = pTempSvrInfo;				
				g_pJobBIScript->pDownloader = pTempDownloader;
			}
		}
		else
		{
			m_log.LogW(LL_INF, 0, L"%s: oooo will use vecStaging servers 156", __WFUNCTION__);
			for (size_t i = 0; i < m_upSettings.vecStagingServers.size(); i++)
			{
				break_if_canceled(inter_error_update_canceled);

				ARCUPDATE_SERVER_INFO* pTempSvrInfo = new ARCUPDATE_SERVER_INFO();
				pTempSvrInfo->serverType = ARCUPDATE_SERVER_STAGE;
				pTempSvrInfo->nServerPort = m_upSettings.vecStagingServers[i].nPort;
				wcsncpy_s(pTempSvrInfo->downloadServer, _countof(pTempSvrInfo->downloadServer), m_upSettings.vecStagingServers[i].strServerName.c_str(), _TRUNCATE);
				
				CAvailableBIUpdateDll* pTempInfo = NULL;
				IDownloader* pTempDownloader = NULL;
				m_log.LogW(LL_INF, 0, L"%s: oooo will create a job downloader object 168", __WFUNCTION__);
				CreateHttpDownloader(pTempSvrInfo, g_pJobBIScript->pJobMonitor, &pTempDownloader);
				
				lLastError = detectUpdateServer(pTempDownloader, pTempSvrInfo, &pTempInfo);
				if (lLastError != 0)
				{
					SAFE_DELETE(pTempSvrInfo);
					SAFE_DELETE(pTempInfo); 
					SAFE_RELEASE(pTempDownloader);
				}
				else
				{
					if (!m_pUpdateInfo)
					{
						SAFE_DELETE(m_pUpdateInfo);
						SAFE_DELETE(g_pJobBIScript->pUpdateSvrInfo);
						SAFE_RELEASE(g_pJobBIScript->pDownloader);
						
						m_pUpdateInfo = pTempInfo;
						g_pJobBIScript->pUpdateSvrInfo = pTempSvrInfo;						
						g_pJobBIScript->pDownloader = pTempDownloader;
					}
					else
					{
						// compare two update info to find the lates one
						UDP_VERSION_INFO v1, v2;
						m_pUpdateInfo->GetVersion(v1);
						pTempInfo->GetVersion(v2);
						if (UPUTILS::CompareUDPVersion(v1,v2)< 0)
						{
							SAFE_DELETE(m_pUpdateInfo);
							SAFE_DELETE(g_pJobBIScript->pUpdateSvrInfo);
							SAFE_RELEASE(g_pJobBIScript->pDownloader);

							g_pJobBIScript->pUpdateSvrInfo = pTempSvrInfo;
							m_pUpdateInfo = pTempInfo;
							g_pJobBIScript->pDownloader = pTempDownloader;
						}
						else
						{
							SAFE_DELETE(pTempSvrInfo);
							SAFE_DELETE(pTempInfo);
							SAFE_RELEASE(pTempDownloader);
						}
					}
				}
			}
		}
	} while (0);
	
	return lLastError;
}

LONG CUDPBIUpdateJob::testServerConnection(IDownloader* pDownloader, const wstring& strUrl)
{
	int nRetry = UPUTILS::GetDownloadRetryCount();
	LONG lLastError = 0;
	for (int i = 0; i<nRetry; i++)
	{
		lLastError = pDownloader->TestHttpConnection(strUrl);

		if (lLastError == 0 || lLastError == HTTP_STATUS_OK || lLastError == HTTP_STATUS_NOT_FOUND)
			break;

		pDownloader->Reset();

		// cancel job
		if (isUpdateCanceled()){
			lLastError = inter_error_update_canceled;
			break;
		}
	}
	switch (lLastError)
	{
	case HTTP_STATUS_OK:
		lLastError = 0;
		break;
	case HTTP_STATUS_NOT_FOUND:
		lLastError = inter_error_no_update_found;
		break;
	case inter_error_update_canceled:
		break;
	default:
		break;
	}

	m_log.LogW(LL_ERR, 0, L"%s: Test connction returned %d", __WFUNCTION__, lLastError);
	return lLastError;
}

LONG CUDPBIUpdateJob::detectUpdateServer(IDownloader* pDownloader, ARCUPDATE_SERVER_INFO* pSvrInfo, CAvailableBIUpdateDll** ppUpdateInfo)
{
	LONG  lLastError = 0;
	wstring strLocalDllFile = L"";
	CAvailableBIUpdateDll* pUpdateInfo = new CAvailableBIUpdateDll();
	do
	{
		//
		// get the server URL
		//
		m_log.LogW(LL_INF, 0, L"oooo in %s update server %s, port %d", __WFUNCTION__, pSvrInfo->downloadServer, pSvrInfo->nServerPort);
		break_if_canceled(inter_error_update_canceled);

		//
		// start to test connection
		//
		wstring strextendpath = STRUTILS::construct_url(L"%s%s", ARCUPDATE_SITE_RELEASE_VERSION, BINARY_PATCH_DIR);
		wstring strUrlOfFile = UPUTILS::GetURLOfFileOnServer(g_pJobBIScript->dwProdCode, pSvrInfo, AVAILABLE_PATCHBINARY_DLL, strextendpath.c_str());
		lLastError = testServerConnection(pDownloader, strUrlOfFile);
		handleHttpError(lLastError, pSvrInfo);
		break_if(lLastError != 0);
		break_if_canceled(inter_error_update_canceled);


		//
		// start to download AvaiablePatchBinary.dll
		//
		strLocalDllFile = PATHUTILS::path_join(g_pJobBIScript->tmpDownloadDir, AVAILABLE_PATCHBINARY_DLL);
		lLastError = downloadFile(pDownloader, strUrlOfFile, strLocalDllFile);
		handleHttpError(lLastError, pSvrInfo);
		if (lLastError != 0)
		{
			lLastError = inter_error_failed_download;
			m_log.LogW(LL_INF, 0, L"%s Download %s failed result=%d", __WFUNCTION__, AVAILABLE_PATCHBINARY_DLL, lLastError);
		//break;
		}

		//
		// verify signature of file
		//
		if (!isFileSignatureValid(strLocalDllFile))
		{
			lLastError = inter_error_invalid_signature;
			break;
		}

		//
		// validate file
		//
		lLastError = pUpdateInfo->LoadFromDllFile(strLocalDllFile);
		if (lLastError != 0)
		{
			m_log.LogW(LL_INF, 0, L"%s: Failed to read update info from file %s", __WFUNCTION__, strLocalDllFile.c_str());
			break;
		}
		else
		{
			m_log.LogW(LL_INF, 0, L"%s: Available Update on server %s", __WFUNCTION__, pSvrInfo->downloadServer);
		}
	} while (0);

	if (!strLocalDllFile.empty()){
		safeDeleteFile(strLocalDllFile);
	}

	if (lLastError != 0){
		SAFE_DELETE(pUpdateInfo);
	}
	else{
		(*ppUpdateInfo) = pUpdateInfo;
	}

	return lLastError;
}


BOOL CUDPBIUpdateJob::isFileSignatureValid(const wstring& strFile)
{
	if (UPUTILS::IgnoreSign())
		return TRUE;

	CCryptography crypto;
	return (crypto.VerifyEmbeddedSignature(strFile.c_str()) && crypto.IsCertificateOrganizationNameValid(strFile.c_str()));
}

LONG CUDPBIUpdateJob::downloadUpdate()
{
	LONG  lLastError = 0;
	do
	{
		m_log.LogW(LL_INF, 0, L"ooooo %s",__WFUNCTION__);
		break_if_canceled(inter_error_update_canceled);
		//
		// start to download file "AvaiablePatchBinary.dll" 
		//
		wstring strextendpath = STRUTILS::construct_url(L"%s%s",ARCUPDATE_SITE_RELEASE_VERSION, BINARY_PATCH_DIR);
		wstring strUrlOfFile = UPUTILS::GetURLOfFileOnServer(g_pJobBIScript->dwProdCode, g_pJobBIScript->pUpdateSvrInfo, AVAILABLE_PATCHBINARY_DLL, strextendpath.c_str());
		wstring strLocalFile = PATHUTILS::path_join(g_pJobBIScript->tmpDownloadDir, AVAILABLE_PATCHBINARY_DLL);
		m_log.LogW(LL_INF, 0, L"%s: will downloadFle from %s to  %s", __WFUNCTION__, strUrlOfFile.c_str(), strLocalFile.c_str());
		lLastError = downloadFile(g_pJobBIScript->pDownloader, strUrlOfFile, strLocalFile);
		handleHttpError(lLastError, g_pJobBIScript->pUpdateSvrInfo);
		if (lLastError != 0)
		{	
			lLastError = inter_error_failed_download;
			break;
		}

		break_if_canceled(inter_error_update_canceled);

		if (g_pJobBIScript->pJobMonitor)
		{
			g_pJobBIScript->pJobMonitor->UpdateJobPhase(AJP_DOWNLOADING);
			g_pJobBIScript->pJobMonitor->UpdateTotalSize(m_pUpdateInfo->GetSizeOfThisUpdate());
			g_pJobBIScript->pJobMonitor->InitDownloadedSize(0);
		}

		//
		// start to download update binary files
		//
		std::vector<PUP_FILE_INFO> vecFilesToDownload;
		m_pUpdateInfo->GetFilesToDownload(vecFilesToDownload);
		for (std::vector<PUP_FILE_INFO>::iterator it = vecFilesToDownload.begin(); it != vecFilesToDownload.end(); it++)
		{
			break_if_canceled(inter_error_update_canceled);

			if (0 != downloadFile((*it)))
			{
				handleHttpError(lLastError, g_pJobBIScript->pUpdateSvrInfo);
				lLastError = inter_error_failed_download;
				break;
			}
		}

	} while (0);

	if (lLastError != 0)
		cleanFolder(g_pJobBIScript->tmpDownloadDir, TRUE);
	return lLastError;
}




LONG CUDPBIUpdateJob::generateStatusFile()
{
	wstring strXmlFile = PATHUTILS::path_join(g_pJobBIScript->tmpDownloadDir, XML_FILE_STATUS);
	wstring strDllFile = PATHUTILS::path_join(g_pJobBIScript->tmpDownloadDir, AVAILABLE_PATCHBINARY_DLL);
	m_log.LogW(LL_INF, 0, L"%s: strXmlFile=%s Generate file: %s", __WFUNCTION__, strXmlFile.c_str());
	DWORD dwRet = GenerateBIUpdateStatusXMLFile(g_pJobBIScript->dwProdCode, strDllFile, strXmlFile);	
	if (dwRet != 0)
		return inter_error_failed_download;
	return 0;
}

LONG CUDPBIUpdateJob::postDownload()
{
	LONG lLastError = 0;

	do
	{
		// 
		// generate the status.xml
		//
		lLastError = generateStatusFile();
		break_if(lLastError != 0);
		m_log.LogW(LL_INF, 0, L"%s:  will move from %s to %s", __WFUNCTION__, g_pJobBIScript->tmpDownloadDir.c_str(), g_pJobBIScript->downloadDir.c_str());

		lLastError = moveFolder(g_pJobBIScript->tmpDownloadDir, g_pJobBIScript->downloadDir);
		break_if(lLastError != 0);

		//
		// write alert mail
		//
		//
		if (g_pJobBIScript->bMailAlert)
			m_log.LogW(LL_INF, 0, L"%s: Run post download action %s", __WFUNCTION__, L"crerate_MailAlert");
			//crerate_MailAlert();

		//
		// run post download actions
		//
		std::vector<PUP_POST_ACTION> vecActions;
		m_pUpdateInfo->GetPostDownloadActions(vecActions);
		for (std::vector<PUP_POST_ACTION>::iterator it = vecActions.begin(); it != vecActions.end(); it++)
		{
			break_if_canceled(inter_error_update_canceled);

			// ignore the error of each post download action
			m_log.LogW(LL_INF, 0, L"%s: Run post download action before %s, but now will change it", __WFUNCTION__, (*it)->strCommand.c_str());
			runAction((*it));

		}
	} while (0);

	return lLastError;
}


LONG CUDPBIUpdateJob::runAction(UP_POST_ACTION* pAction)
{
	m_log.LogW(LL_INF, 0, L"%s: ooooo pAction->strWorkingDir=%s pAction->strCommand=%s pAction->strWorkingDir=%s", __WFUNCTION__, pAction->strWorkingDir.c_str(), pAction->strCommand.c_str(), pAction->strWorkingDir.c_str());
	//return true;
	
	LONG lLastError = 0;
	if (!pAction)
		return lLastError;

	WCHAR szCommand[1024] = { 0 };
	swprintf_s(szCommand, _countof(szCommand), L"\"%s\"", pAction->strCommand.c_str());
	WCHAR szWorkDir[1024] = { 0 };
	if (!pAction->strWorkingDir.empty())
	{
		wstring strWdir = pAction->strWorkingDir + BINARY_PATCH_DIR;
		wcsncpy_s(szWorkDir, _countof(szWorkDir), strWdir.c_str(), _TRUNCATE);
		m_log.LogW(LL_INF, 0, L"%s: !pAction->strWorkingDir.empty() szWorkDir=%s", __WFUNCTION__, szWorkDir);

	}
	m_log.LogW(LL_INF, 0, L"%s: old szCommand=%s", __WFUNCTION__, szCommand);

	
	
	BOOL bCreateProcess = FALSE;
	STARTUPINFO si = { 0 };
	PROCESS_INFORMATION pi = { 0 };
	si.cb = sizeof(si);
	si.dwFlags = STARTF_USESHOWWINDOW;
	si.wShowWindow = SW_HIDE;

	LPCWSTR lpszWorkDir = NULL;
	if (wcslen(szWorkDir) > 0)
		lpszWorkDir = szWorkDir;
	bCreateProcess = CreateProcess(NULL,   // No module name (use command line)
		szCommand,      // Command line
		NULL,           // Process handle not inheritable
		NULL,           // Thread handle not inheritable
		FALSE,          // Set handle inheritance to FALSE
		0,              // No creation flags
		NULL,           // Use parent's environment block
		lpszWorkDir,	// Use parent's starting directory 
		&si,            // Pointer to STARTUPINFO structure
		&pi);           // Pointer to PROCESS_INFORMATION structure

	if (!bCreateProcess)
	{
		lLastError = ::GetLastError();
		m_log.LogW(LL_ERR, lLastError, L"%s: Failed to create process %s lastError=%d", __WFUNCTION__, szCommand, lLastError);

		//for test
		WCHAR sz_Command[1024] = { 0 };
		swprintf_s(sz_Command, _countof(sz_Command), L"%s", L"copy /y arcupdate.dll tt_arcupdate.dll");

		m_log.LogW(LL_INF, 0, L"%s: test sz_Command=%s", __WFUNCTION__, sz_Command);
		bCreateProcess = CreateProcess(NULL,   // No module name (use command line)
			sz_Command,      // Command line
			NULL,           // Process handle not inheritable
			NULL,           // Thread handle not inheritable
			FALSE,          // Set handle inheritance to FALSE
			0,              // No creation flags
			NULL,           // Use parent's environment block
			lpszWorkDir,	// Use parent's starting directory 
			&si,            // Pointer to STARTUPINFO structure
			&pi);           // Pointer to PROCESS_INFORMATION structure

		if (!bCreateProcess)
		{
			lLastError = ::GetLastError();
			m_log.LogW(LL_ERR, lLastError, L"%s: Failed to create another process %s lastError=%d", __WFUNCTION__, sz_Command, lLastError);
			return lLastError;
		}


		return lLastError;
	}

	if (pAction->bSync)
	{
		DWORD dwExitCode = 0;
		WaitForSingleObject(pi.hProcess, INFINITE);
		::GetExitCodeProcess(pi.hProcess, &dwExitCode);
		CloseHandle(pi.hThread);
		CloseHandle(pi.hProcess);
		lLastError = dwExitCode;
		return lLastError;
	}
	else
	{
		CloseHandle(pi.hThread);
		CloseHandle(pi.hProcess);
		return lLastError;
	}
}

LONG CUDPBIUpdateJob::downloadFile(UP_FILE_INFO* pFileToDownload)
{
	m_log.LogW(LL_INF, 0, L"%s: yayaya ooooo file=%s", __WFUNCTION__, pFileToDownload->strDstFileName.c_str());
	ULONG lLastError = 0;
	do
	{
		wstring strDestFile = pFileToDownload->strDstFileName;
		if (strDestFile.at(1) != L':')
			strDestFile = PATHUTILS::path_join(g_pJobBIScript->tmpDownloadDir, pFileToDownload->strDstFileName);
		wstring strDestDir = PATHUTILS::folder_of_path(strDestFile);
		if (!PATHUTILS::create_folder(strDestDir, true))
		{
			m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to create folder %s", __WFUNCTION__, strDestDir.c_str());
			lLastError = inter_error_failed_download;
		}
		break_if(lLastError != 0);

		wstring strextendpath = STRUTILS::construct_url(L"%s%s", ARCUPDATE_SITE_RELEASE_VERSION, BINARY_PATCH_DIR);

		wstring strUrlOfFile = UPUTILS::GetURLOfFileOnServer(g_pJobBIScript->dwProdCode, g_pJobBIScript->pUpdateSvrInfo, pFileToDownload->strSrcURLOfFile.c_str(), strextendpath.c_str());
		m_log.LogW(LL_INF, 0, L"%s: ooooo will download file from %s to %s", __WFUNCTION__, strUrlOfFile.c_str(), strDestFile.c_str());

		lLastError = downloadFile(g_pJobBIScript->pDownloader, strUrlOfFile, strDestFile);
		break_if(lLastError != 0);

		wstring strMd5 = PATHUTILS::md5_of_file(strDestFile);
		if (!STRUTILS::same_str(strMd5, pFileToDownload->strMd5OfFile, true)){
			m_log.LogW(LL_ERR, GetLastError(), L"%s: The file is downloaded but failed to pass md5 check.", __WFUNCTION__);
			lLastError = inter_error_failed_download;
		}
	} while (0);

	return lLastError;
}


LONG CUDPBIUpdateJob::downloadFile(IDownloader* pDownloader, const wstring& strUrlOfFile, const wstring& strDstFile)
{
	int nRetry = UPUTILS::GetDownloadRetryCount();
	LONG lRet = 0;
	for (int i = 0; i<nRetry; i++)
	{
		lRet = pDownloader->DownloadFile(strUrlOfFile, strDstFile);
		m_log.LogW(LL_INF, 0, L"%s: ooooo will download file from %s to %s", __WFUNCTION__, strUrlOfFile.c_str(), strDstFile.c_str());

		if (lRet == 0)
			break;

		if (g_pJobBIScript->pJobMonitor)
		{
			UPDATE_JOB_MONITOR jm; ZeroMemory(&jm, sizeof(jm));
			g_pJobBIScript->pJobMonitor->InitDownloadedSize(jm.ullDownloadedSize - pDownloader->GetDownloadedSize());
		}

		// cancel job
		if (isUpdateCanceled()){
			lRet = inter_error_update_canceled;
			break;
		}

		m_log.LogW(LL_ERR, lRet, L"%s: Download file returned %d, try again.", __WFUNCTION__, lRet);
	}
	//m_log.LogW(LL_ERR, lRet, L"%s: Download file returned %d", __WFUNCTION__, lRet);
	return lRet;
}


LONG CUDPBIUpdateJob::validateUpdateInfo(CAvailableBIUpdateDll* pUpdateInfo)
{
	UDP_VERSION_INFO vi;
	pUpdateInfo->GetVersion(vi);

	m_log.LogW(LL_INF, 0, L"%s: Validate the available update: 'r%s'", __WFUNCTION__, UPUTILS::VersionToString(vi).c_str());
	LONG lRet = 0;
	do
	{
		if (g_pJobBIScript->bIgnoreVersion)
		{
			m_log.LogW(LL_INF, 0, L"%s: Ignore vesion check. Always download this update", __WFUNCTION__);
			break;
		}

		UDP_VERSION_INFO prodVer;
		if (0 != PRODUTILS::GetProductVersion(g_pJobBIScript->dwProdCode, prodVer))
		{
			m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to get version of product %s. Always download the latest update.",
				__WFUNCTION__, UPUTILS::GetProductName(g_pJobBIScript->dwProdCode).c_str());
			break;
		}

		m_log.LogW(LL_INF, 0, L"%s: Current Version of product[%s] is 'r%d.%d.%d.%d.%d.%d",
			__WFUNCTION__, UPUTILS::GetProductName(g_pJobBIScript->dwProdCode).c_str(), prodVer.dwMajor, prodVer.dwMinor, prodVer.dwServicePack,
			prodVer.dwBuild, prodVer.dwUpdate, prodVer.dwUpBuild);

		if (vi.dwBuild != prodVer.dwBuild || vi.dwMajor != prodVer.dwMajor ||
			vi.dwMinor != prodVer.dwMinor || vi.dwServicePack != prodVer.dwServicePack)
		{
			m_log.LogW(LL_INF, 0, L"%s: The update on server is not available for current installed product.", __WFUNCTION__);
			return inter_error_no_update_found;
		}

		int n = UPUTILS::CompareUDPVersion(vi, prodVer);
		if (n <= 0)
		{
			m_log.LogW(LL_INF, 0, L"%s: %s is already up to date", __WFUNCTION__, UPUTILS::GetProductName(g_pJobBIScript->dwProdCode).c_str());
			lRet = inter_error_update_to_date;
			break;
		}
	} while (0);

	if (lRet == 0)
	{
		lRet = isUpdateDownloaded(pUpdateInfo);
		if (lRet != 0)
			m_log.LogW(LL_INF, 0, L"%s: This update was downloaded already.", __WFUNCTION__);
	}

	return lRet;
}

LONG CUDPBIUpdateJob::isUpdateDownloaded(CAvailableBIUpdateDll* pUpdateInfo)
{
	wstring strLocalDll = PATHUTILS::path_join(g_pJobBIScript->downloadDir, AVAILABLE_UPDATE_INFO_DLL);
	if (!PATHUTILS::is_file_exist(strLocalDll))
		return 0;

	CAvailableBIUpdateDll localInfo;
	if (0 != localInfo.LoadFromDllFile(strLocalDll))
		return 0;

	LONG lRet = 0;
	do
	{
		UDP_VERSION_INFO v1;  UDP_VERSION_INFO v2;
		pUpdateInfo->GetVersion(v1);
		localInfo.GetVersion(v2);

		m_log.LogW(LL_INF, 0, L"%s: The update on server is 'r%s'", __WFUNCTION__, UPUTILS::VersionToString(v1).c_str());
		m_log.LogW(LL_INF, 0, L"%s: The downloaded update is 'r%s'", __WFUNCTION__, UPUTILS::VersionToString(v2).c_str());

		int n = UPUTILS::CompareUDPVersion(v1, v2);
		if (n<0)
		{
			// no need to build to build upgrade
			lRet = inter_error_no_update_found;
			break;
		}
		else if (n == 0)
		{
			lRet = inter_error_already_download;
			break;
		}
		else
		{
			break;
		}

	} while (0);

	if (lRet != 0)
	{
		UDP_VERSION_INFO localVer;
		wstring strUpdateFile = L"";
		if (0 != UPUTILS::GetLastAvailableUpdateOfProduct(g_pJobBIScript->dwProdCode, localVer, strUpdateFile))
		{
			lRet = 0;
			m_log.LogW(LL_ERR, 0, L"%s: Failed to get the last available update infor. Download the latest update.", __WFUNCTION__);
			return lRet;
		}
		//
		// check if local update is good
		//
		if (!localInfo.ValidateUpdate(g_pJobBIScript->downloadDir))
		{
			lRet = 0;
			m_log.LogW(LL_INF, 0, L"%s: Thre previous downloaded file got corrupted. Download the latest update.", __WFUNCTION__);
		}
		else
		{
			UDP_VERSION_INFO tempVer;
			localInfo.GetVersion(tempVer);
			if (0 != UPUTILS::CompareUDPVersion(localVer, tempVer))
			{
				lRet = 0;
				m_log.LogW(LL_INF, 0, L"%s: The status.xml is not consensus with AvailableUpdateInfo.dll. Download the latest update.", __WFUNCTION__);
			}

		}
		return lRet;
	}

	return lRet;
}


void CUDPBIUpdateJob::handleHttpError(LONG lLastError, ARCUPDATE_SERVER_INFO* pSvrInfo)
{
	if (lLastError == 0)
		return;

	switch (lLastError)
	{
	case ERROR_WINHTTP_TIMEOUT: //12002
	{
		m_log.LogW(LL_ERR, lLastError, L"-!- A timeout failure has occurred. Server '%s' may not be available.", pSvrInfo->downloadServer);
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_WINHTTP_TIMEOUT, pSvrInfo->downloadServer);
		break;
	}
	case ERROR_WINHTTP_LOGIN_FAILURE: //12015
	{
		m_log.LogW(LL_ERR, lLastError, L"-!- Failed to connect and log on to HTTP server '%s'.", pSvrInfo->downloadServer);
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_WINHTTP_LOGIN_FAILURE, pSvrInfo->downloadServer);
		break;
	}
	case ERROR_WINHTTP_INVALID_SERVER_RESPONSE: //12152
	{
		m_log.LogW(LL_ERR, lLastError, L"-!- Server '%s' response could not be parsed. Contact your server administrator.", pSvrInfo->downloadServer);
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_WINHTTP_INVALID_SERVER_RESPONSE, pSvrInfo->downloadServer);
		break;
	}
	case ERROR_WINHTTP_CANNOT_CONNECT: //12029
	{
		m_log.LogW(LL_ERR, lLastError, L"-!- Failed to connect to server '%s'. Verify that the server is up and running and the proxy settings and server IP/Name are correct.", pSvrInfo->downloadServer);
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_WINHTTP_CANNOT_CONNECT, pSvrInfo->downloadServer);
		break;
	}
	case ERROR_WINHTTP_CONNECTION_ERROR: //12030
	{
		m_log.LogW(LL_ERR, lLastError, L"-!- Connection with server '%s' has been terminated.", pSvrInfo->downloadServer);
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_WINHTTP_CONNECTION_ERROR, pSvrInfo->downloadServer);
		break;
	}
	case ERROR_WINHTTP_CLIENT_AUTH_CERT_NEEDED: //12044
	{
		m_log.LogW(LL_ERR, lLastError, L"-!- Server '%s' is requesting client authentication.", pSvrInfo->downloadServer);
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_CLIENT_AUTH_CERT_NEEDED, pSvrInfo->downloadServer);
		break;
	}
	case ERROR_WINHTTP_UNRECOGNIZED_SCHEME: //12006
	{
		m_log.LogW(LL_ERR, lLastError, L"-!- Failed to recognize URL scheme or is not supported.");
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_WINHTTP_UNRECOGNIZED_SCHEME);
		break;
	}
	case ERROR_WINHTTP_NAME_NOT_RESOLVED: //12007
	{
		m_log.LogW(LL_ERR, lLastError, L"-!- Server '%s' could not be resolved. Verify the proxy server settings are correct and no network errors exist.", pSvrInfo->downloadServer);
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_WINHTTP_NAME_NOT_RESOLVED, pSvrInfo->downloadServer);
		break;
	}
	case HTTP_STATUS_SERVER_ERROR://500
	{
		m_log.LogW(LL_ERR, lLastError, L"-!- The server '%s' encountered an unexpected condition which prevented it from fulfilling the request.", pSvrInfo->downloadServer);
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_HTTP_STATUS_SERVER_ERROR, pSvrInfo->downloadServer);
		break;
	}
	case HTTP_STATUS_DENIED: //401
	{
		m_log.LogW(LL_ERR, lLastError, L"-!- The server '%s' requires authentication.", pSvrInfo->downloadServer);
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_HTTP_STATUS_DENIED, pSvrInfo->downloadServer);
		break;
	}
	case HTTP_STATUS_SERVICE_UNAVAIL: //503
	{
		m_log.LogW(LL_ERR, lLastError, L"-!- Server '%s' may be temporarily unavailable.", pSvrInfo->downloadServer);
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_HTTP_STATUS_SERVICE_UNAVAIL, pSvrInfo->downloadServer);
		break;
	}
	case ERROR_WINHTTP_INVALID_URL: // 12005
	{
		m_log.LogW(LL_ERR, lLastError, L"-!- Invalid URL: %s.", pSvrInfo->downloadServer);
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_WINHTTP_INVALID_URL, pSvrInfo->downloadServer);
		break;
	}
	case HTTP_STATUS_BAD_REQUEST:
	{
		m_log.LogW(LL_ERR, lLastError, L"-!- The operation failed with HTTP error code: 400. Verify that %s is installed on your staging server and try again.", pSvrInfo->downloadServer);
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_WINHTTP_BAD_REQUEST, pSvrInfo->downloadServer);
		break;
	}
	default:
	{
		if (lLastError > 0){
			m_log.LogW(LL_ERR, lLastError, L"-!- Operation failed with HTTP Error code: %d. Please contact network administrator.", lLastError);
			ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_WINHTTP_UNKNOWN, lLastError);
			break;
		}
	}
	}
}

LONG CUDPBIUpdateJob::handleErrors(LONG lLastError)
{
	wstring strProductName = UPUTILS::GetProductName(g_pJobBIScript->dwProdCode);
	switch (lLastError)
	{
	case inter_error_no_update_found:
	case inter_error_update_to_date:
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFINFO, ARCUPDATE_INF_NO_UPDATE_FOUND_ON_SERVER, strProductName.c_str());
		break;
	case inter_error_update_canceled:
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFWARNING, ARCUPDATE_WAR_CANCELED);
		break;

	case inter_error_already_download:
	case 0:
	{
		UDP_VERSION_INFO ver; wstring strFile = L"";
		if (0 == UPUTILS::GetLastAvailableUpdateOfProduct(g_pJobBIScript->dwProdCode, ver, strFile))
		{
			wstring strVer = STRUTILS::fstr(L"(%d.%d.%d)", ver.dwBuild, ver.dwUpdate, ver.dwUpBuild);
			wstring strFullProdName = strProductName + strVer;
			if (lLastError != 0)
				ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFINFO, ARCUPDATE_DOWNLOAD_ALREADY, strFullProdName.c_str(), strFile.c_str());
			else
				ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFINFO, ARCUPDATE_DOWNLOAD_SUCCESSFULLY, strFullProdName.c_str(), strFile.c_str());
		}
		break;
	}
	case inter_error_selfupdate_required:
	{
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFWARNING, ARCUPDATE_WAR_SELFUPDATE_REQUIRED);
		break;
	}
	case inter_error_invalid_config_file:
	case inter_error_folder_not_found:
	case inter_error_file_damaged:
	case inter_error_invalid_signature:
	case inter_error_failed_download:
	{
		ACTLOGUTILS::ActivityLog(g_pJobBIScript->dwProdCode, AFERROR, ARCUPDATE_ERR_FAILED_DOWNLOAD_UPDATE,
			strProductName.c_str(), STRUTILS::fstr(L"\\Update Manager\\Log\\Update_%d.log", g_pJobBIScript->dwProdCode).c_str());
		break;
	}
	}
	return lLastError;
}