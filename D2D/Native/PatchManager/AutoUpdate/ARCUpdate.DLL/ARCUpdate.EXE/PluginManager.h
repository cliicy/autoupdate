#pragma once

class CPluginManager
{
public:
	CPluginManager();

	~CPluginManager();

public:
	DWORD	RemovePlugin(DWORD dwCommand);

	DWORD	InstallPlugin(DWORD dwCommand, const wstring& strModule);

	wstring	GetPlugin(DWORD dwCommand);
};

extern CPluginManager g_pluginManager;


class CUpdatePlugin
{
public:
	CUpdatePlugin(const wstring& strModule);

	~CUpdatePlugin();

	BOOL OnPipeSession(HANDLE hPipe, void* pParams);

protected:
	wstring		m_strModule;
	HMODULE		m_hModule;
protected:
	CDbgLog		m_log;
};