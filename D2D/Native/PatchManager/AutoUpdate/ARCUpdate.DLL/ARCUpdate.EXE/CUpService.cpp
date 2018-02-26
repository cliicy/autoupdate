#include "stdafx.h"
#include "CUpService.h"
#include "UpdateJobManager.h"

CUpService::CUpService()
	: m_pAgentSchedular(NULL)
	, m_pConsoleSchedular(NULL)
	, m_pPipeServer(NULL)
	, m_pThreadClean(NULL)
{

}

CUpService::~CUpService()
{
	SAFE_DELETE(m_pPipeServer);
	SAFE_DELETE(m_pThreadClean);
	SAFE_DELETE(m_pAgentSchedular);
	SAFE_DELETE(m_pConsoleSchedular);
}

DWORD  CUpService::Run()
{	
	wstring strCfgFile = PATHUTILS::path_join(PATHUTILS::home_dir(), L"Config");
	::CreateDirectory(strCfgFile.c_str(), NULL);;
	strCfgFile = PATHUTILS::path_join(strCfgFile, L"UpdateCfg.ini");
	wstring strVersion = STRUTILS::fstr(L"%d", AUTOUPDATE_INTERNAL_VERSION);
	::WritePrivateProfileString(L"Settings", L"AutoUpdateVersion", strVersion.c_str(), strCfgFile.c_str());
	m_log.LogW(LL_INF, 0, L"*** Auto Update Service started. Version: %d ***", AUTOUPDATE_INTERNAL_VERSION);
	//
	// create a pipe server to receive command
	//
	m_pPipeServer = new CPipeServer();
	m_pPipeServer->Start();

	//
	// create a thread to clean _install dir
	//
	m_pThreadClean = new CThreadClean();
	m_pThreadClean->Start();

	//
	// start the thread of auto update scheduler
	//
	wstring strProdhome = PRODUTILS::GetProductHome(ARCUPDATE_PRODUCT_AGENT);
	if (!strProdhome.empty()){
		m_log.LogW(LL_INF, 0, L"%s wawawawa strProdHome=%s oooo", __WFUNCTION__, strProdhome.c_str());
		m_pAgentSchedular = new CUpdateScheduler(ARCUPDATE_PRODUCT_AGENT);
		m_pAgentSchedular->Start();
	}
	m_log.LogW(LL_INF, 0, L"%s oooo  AGENT's path=%s oooo", __WFUNCTION__, strProdhome.c_str());
	strProdhome = PRODUTILS::GetProductHome(ARCUPDATE_PRODUCT_FULL);
	m_log.LogW(LL_INF, 0, L"%s oooo  FULL's path=%s oooo", __WFUNCTION__, strProdhome.c_str());
	if (!strProdhome.empty())
	{
		if (PRODUTILS::IsGatewayInstalled())
		{
			m_log.LogW(LL_INF, 0, L"The gateway was installed, do not have to run schedule to download gateway update.");
			//m_pConsoleSchedular = new CUpdateScheduler(ARCUPDATE_PRODUCT_GATEWAY);
			//m_pConsoleSchedular->Start();
		}
		else
		{
			m_log.LogW(LL_INF, 0, L"%s oooo  gateway wasnot installed,so will start ConsoleSchedular oooo", __WFUNCTION__);
			m_pConsoleSchedular = new CUpdateScheduler(ARCUPDATE_PRODUCT_FULL);
			m_pConsoleSchedular->Start();
		}
	}

	if (!m_pAgentSchedular && !m_pConsoleSchedular)
	{
		Sleep(10000);
	}
	else
	{
		if (m_pAgentSchedular)
			m_pAgentSchedular->Wait(INFINITE);
		if (m_pConsoleSchedular)
			m_pConsoleSchedular->Wait(INFINITE);
	}

	m_log.LogW(LL_INF, GetLastError(), L"*** Auto Update Service Exists ***");
	return 0;
}

DWORD CUpService::Stop()
{
	if (m_pPipeServer)
		m_pPipeServer->Stop();

	g_upJobManager.StopAllUpdateJobs();

	if (m_pAgentSchedular)
		m_pAgentSchedular->Stop();

	if (m_pConsoleSchedular)
		m_pConsoleSchedular->Stop();

	if (m_pThreadClean)
		m_pThreadClean->Stop();

	if (m_pPipeServer)
		m_pPipeServer->Wait(INFINITE);

	if (m_pThreadClean)
		m_pThreadClean->Wait(INFINITE);

	return 0;
}

/*
	the thread to clean self update
*/
CThreadClean::CThreadClean()
	: CThreadBase( FALSE)
	, m_bStopFlag(FALSE)
{

}

CThreadClean::~CThreadClean()
{

}

DWORD CThreadClean::Stop()
{
	m_bStopFlag = TRUE;
	return 0;
}

DWORD CThreadClean::Main()
{
	wstring strInstallDir = PATHUTILS::home_dir();
	strInstallDir = PATHUTILS::path_join(strInstallDir, L"_install");
	if (!PATHUTILS::is_folder_exist(strInstallDir))
		return 0;

	m_log.LogW(LL_INF, 0, L"%s: Start to clean folder %s", __WFUNCTION__, strInstallDir.c_str());
	DWORD dwRet = 0;
	while ( dwRet!=0 )
	{
		if (m_bStopFlag)
			break;

		// first to delete file ..\_install\ARCUpdate.exe
		wstring strExe = PATHUTILS::path_join(strInstallDir, L"ARCUpdate.exe");
		if (PATHUTILS::is_file_exist(strExe))
		{
			SetFileAttributes(strExe.c_str(), FILE_ATTRIBUTE_NORMAL);
			if (!::DeleteFile(strExe.c_str()))
			{
				dwRet = GetLastError();
				m_log.LogW(LL_ERR, dwRet, L"%s: Failed to delete file %s", __WFUNCTION__, strExe.c_str());
				Sleep(1000);
				continue;
			}
		}

		dwRet = clean_Folder(strInstallDir);
		if (dwRet !=0 )
		{
			Sleep(1000);
			continue;
		}
	}

	m_log.LogW(LL_INF, dwRet, L"%s: Exit thread to clean folder", __WFUNCTION__ );
	return 0;
}

DWORD CThreadClean::clean_Folder(const wstring strFolder, bool bIncludeSelf /*= true*/)
{
	if (!PATHUTILS::is_folder_exist(strFolder))
		return 0;

	wstring strFormat = PATHUTILS::path_join(strFolder, L"*");
	WIN32_FIND_DATA wfd; ZeroMemory(&wfd, sizeof(wfd));
	HANDLE hFind = ::FindFirstFile(strFormat.c_str(), &wfd);
	DWORD dwRet = 0;
	do
	{
		if (hFind == INVALID_HANDLE_VALUE)
		{
			m_log.LogW(LL_INF, 0, L"%s: Did not find any files under folder %s", __WFUNCTION__, strFolder.c_str());
			break;
		}

		if (_wcsicmp(wfd.cFileName, L".") == 0 || _wcsicmp(wfd.cFileName, L"..") == 0)
			continue;

		if (0 != (wfd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY))
		{
			wstring strSubFolder = PATHUTILS::path_join(strFolder, wfd.cFileName);
			dwRet = clean_Folder(strSubFolder, true);
			if (dwRet != 0)
				break;
			continue;
		}

		wstring strFile = PATHUTILS::path_join(strFolder, wfd.cFileName);
		::SetFileAttributes(strFile.c_str(), FILE_ATTRIBUTE_NORMAL);
		if (!DeleteFile(strFile.c_str()))
		{
			dwRet = GetLastError();
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to delete file %s", __WFUNCTION__, strFile.c_str());
			break;
		}

	} while (::FindNextFile(hFind, &wfd));

	if (hFind != INVALID_HANDLE_VALUE)
		::FindClose(hFind);
	hFind = INVALID_HANDLE_VALUE;

	if (dwRet == 0 && bIncludeSelf)
	{
		if (!::RemoveDirectory(strFolder.c_str()))
		{
			dwRet = GetLastError();
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to remove directory %s", __WFUNCTION__, strFolder.c_str());
		}
	}
	return dwRet;
}