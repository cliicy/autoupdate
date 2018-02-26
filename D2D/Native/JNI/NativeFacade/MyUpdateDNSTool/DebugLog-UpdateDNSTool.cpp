#include "..\stdafx.h" //new filter for update dns tool


//////////////////////////////////////////////////////////////////////////
static WCHAR g_pLogFile[MAX_PATH] = {0};
static CComAutoCriticalSection g_LogLock;


static LPCTSTR GetLogFileName()
{
	TCHAR pModuleFile[MAX_PATH + MAX_PATH] = {0};
	DWORD dwSize = GetModuleFileName(NULL, pModuleFile, _countof(pModuleFile));
	pModuleFile[dwSize] = 0;
	if(dwSize > 4 && pModuleFile[dwSize-4] == L'.')
	{
		pModuleFile[dwSize-4] = 0;
		swprintf_s(g_pLogFile, L"%s.log", pModuleFile);
	}
	else
	{
		_tcscpy_s(g_pLogFile, _countof(g_pLogFile), TEXT("DNSUpdaterTool.log"));
	}

	return g_pLogFile;
}

//////////////////////////////////////////////////////////////////////////
static VOID DebugWriteString(CONST WCHAR* pMsg)
{
	// write error or other information into log file
	DWORD pid = GetCurrentProcessId();
	DWORD tid = GetCurrentThreadId();
	SYSTEMTIME oT = {0};
	FILE* pLog = NULL;

	g_LogLock.Lock();
	__try
	{
		if (0 == g_pLogFile[0])
		{
			GetLogFileName();
		}

		::GetLocalTime(&oT);
		errno_t err = _wfopen_s(&pLog, g_pLogFile, L"a");
		if (0  ==  err)
		{
			fwprintf(pLog, L"%04d-%02d-%02d %02d:%02d:%02d:%03d\t(%06u,%06u)(0x%04x,0x%04x)\t%s\n", 
				oT.wYear, oT.wMonth, oT.wDay, 
				oT.wHour, oT.wMinute, oT.wSecond, oT.wMilliseconds, 
				pid, tid, pid, tid, 
				pMsg); 
			fclose(pLog); pLog = NULL;
		}
	} 
	__except(EXCEPTION_EXECUTE_HANDLER) 
	{
	}
	g_LogLock.Unlock();
}


VOID DebugWriteStringV(CONST WCHAR * pFormat, ...)
{
	CString str;

	// format and write the data you were given
	va_list args;
	va_start(args, pFormat);
	str.FormatV(pFormat, args);
	va_end(args);

	DebugWriteString(str.GetString());
	return;
}