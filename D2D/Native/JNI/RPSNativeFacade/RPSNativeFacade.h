#pragma once
#include "DbgLog.h"

typedef struct _DATASTORE_INFO
{
	wstring strDSName;
	wstring strDir;
	wstring strUsername;
	wstring strPassword;
} DATASTORE_INFO, *PDATASTOREINFO;


//
//  Helper class to load function pointer for DLL dynamically
//
class FuncLoader
{
public:
	FuncLoader(CDbgLog* pDbgLog = NULL)
	{
		m_pDbgLog = pDbgLog;
	}
	~FuncLoader()
	{
		for (size_t idx = 0; idx < m_hModules.size(); idx++)
		{
			FreeLibrary(m_hModules[idx]);
			m_hModules[idx] = NULL;
		}
		m_hModules.clear();
	}

private:
	CDbgLog*				m_pDbgLog;
	std::vector<HMODULE>	m_hModules;

	HMODULE LoadLib(const WCHAR* pLibraryName)
	{
		HMODULE hLib = LoadLibraryW(pLibraryName);
		if (hLib == NULL)
		{
			DWORD dwRet = GetLastError();
			if (m_pDbgLog != NULL)
				m_pDbgLog->LogW(LL_ERR, dwRet, __FUNCTIONW__ L": Failed to load library [%s].", pLibraryName);
		}
		else
		{
			m_hModules.push_back(hLib);
		}

		return hLib;
	}

public:
	FARPROC GetFuncPtr(const WCHAR* pLibraryName, const char* pFuncName)
	{
		DWORD dwRet = 0;
		FARPROC pFunPtr = NULL;

		if (pLibraryName == NULL || pFuncName == NULL)
		{
			dwRet = ERROR_INVALID_PARAMETER;
			if (m_pDbgLog)
				m_pDbgLog->LogW(LL_ERR, dwRet, __FUNCTIONW__ L": Invalid parameter [%s :: %S].", pLibraryName, pFuncName);

			return pFunPtr;
		}

		HMODULE h = LoadLib(pLibraryName);
		if (h)
		{
			pFunPtr = GetProcAddress(h, pFuncName);
			if (pFunPtr == NULL)
			{
				dwRet = GetLastError();
				if (m_pDbgLog)
					m_pDbgLog->LogW(LL_ERR, dwRet, __FUNCTIONW__ L": Failed to get function [%S] pointer.", pFuncName);
			}
		}

		return pFunPtr;
	}
};


#define ERROR_DATA_STORE_GET_MEMORY_STATUS			-1300
#define ERROR_NATIVEFACADE_INVALID_PARAMETER		-1301