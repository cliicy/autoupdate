#include "stdafx.h"
#include "PluginManager.h"

#define PLUGIN_CFG_FILE_NAME		L"Plugin.ini"
#define PLUGIN_CFG_APP_NAME			L"Plugin"


CPluginManager   g_pluginManager;


CPluginManager::CPluginManager()
{
}


CPluginManager::~CPluginManager()
{
}

DWORD CPluginManager::RemovePlugin(DWORD dwCommand)
{
	wstring strModule = GetPlugin(dwCommand);
	if (!strModule.empty())
	{
		wstring strFile = PATHUTILS::path_join(PATHUTILS::home_dir(), strModule);
		if (PATHUTILS::is_file_exist(strFile))
		{
			if (!::DeleteFile(strFile.c_str()))
				return GetLastError();
		}
	}

	wstring strCfgFile = PATHUTILS::path_join(PATHUTILS::home_dir(), L"\\Config\\" PLUGIN_CFG_FILE_NAME);
	wstring strKey = STRUTILS::fstr(L"%d", dwCommand);	
	if (!::WritePrivateProfileString(PLUGIN_CFG_APP_NAME, strKey.c_str(), NULL, strCfgFile.c_str()))
		return GetLastError();
	
	return 0;
}

DWORD CPluginManager::InstallPlugin(DWORD dwCommand, const wstring& strModule)
{
	wstring strCfgFile = PATHUTILS::path_join(PATHUTILS::home_dir(), L"\\Config\\" PLUGIN_CFG_FILE_NAME);
	wstring strKey = STRUTILS::fstr(L"%d", dwCommand);
	if (!::WritePrivateProfileString(PLUGIN_CFG_APP_NAME, strKey.c_str(), strModule.c_str(), strCfgFile.c_str()))
		return GetLastError();
	return 0;
}

wstring	CPluginManager::GetPlugin(DWORD dwCommand)
{
	WCHAR szModuleName[MAX_PATH] = { 0 };
	wstring strCfgFile = PATHUTILS::path_join(PATHUTILS::home_dir(), L"\\Config\\" PLUGIN_CFG_FILE_NAME);
	wstring strKey = STRUTILS::fstr(L"%d", dwCommand);
	::GetPrivateProfileString(PLUGIN_CFG_APP_NAME, strKey.c_str(), L"", szModuleName, _ARRAYSIZE(szModuleName), strCfgFile.c_str());
	if (wcslen(szModuleName) > 0)
		return wstring(szModuleName);
	return L"";
}

/*
// the class of an update plugin 
*/

typedef void (WINAPI* FUNC_OnPipeSession) (HANDLE, void*);

CUpdatePlugin::CUpdatePlugin(const wstring& strModule)
	: m_strModule( strModule )
	, m_hModule(NULL)
{
	
}

CUpdatePlugin::~CUpdatePlugin()
{
	if (m_hModule != NULL){
		FreeLibrary(m_hModule);
		m_hModule = NULL;
	}
}

BOOL CUpdatePlugin::OnPipeSession(HANDLE hPipe, void* pParams)
{
	BOOL bRet = FALSE;
	do
	{
		m_hModule = ::LoadLibrary(m_strModule.c_str());
		if (m_hModule == NULL){
			m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to load module %s", __WFUNCTION__, m_strModule.c_str());
			break;
		}

		FUNC_OnPipeSession func = (FUNC_OnPipeSession)GetProcAddress(m_hModule, "OnPipeSession");
		if (!func){
			m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed get proc address of 'OnPipeSession' from %s", __WFUNCTION__, m_strModule.c_str());
			break;
		}

		func(hPipe, pParams);

		bRet = TRUE;
	} while (0);

	return bRet;
}