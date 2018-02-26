#include "StdAfx.h"
#include "UpdateService.h"
#include "..\UpdateRes\ARCUpdateRes.h"
#include "DRCommonlib.h"
#include "CUpService.h"
#include "ExceptionDump.h"

VOID MySetExceptionHandler(BOOL bFullMemDump)
{
	__try
	{
		ExceptionDump::SetExceptionHandler(bFullMemDump);
	}
	__except (EXCEPTION_EXECUTE_HANDLER)
	{
		_ftprintf_s(stdout, TEXT("SetExceptionHandler EXCEPTION"));
	}
}

CUpService*    g_pUpdateService = NULL;
///////////////////////////////////////////////////////////////////////////////////////////////////////
BOOL InstallService()
{
	CDbgLog log;
	log.LogW(LL_INF, 0, L"Start to install update service." );

	wstring strDisplayName = UPUTILS::GetUpdateResourceString(ARCUPDATE_SERVICE_DISPLAY_NAME);
	wstring strDescription = UPUTILS::GetUpdateResourceString(ARCUPDATE_SERVICE_DESCRIPTION);
	if (strDisplayName.empty())
		strDisplayName = L"Arcserve UDP Update Service";
	if (strDescription.empty())
		strDescription = L"Arcserve Unified Data Protection Update Service";

	if(strDisplayName.empty())
		strDisplayName = SERVICE_DISPLAY_NAME;
	if(strDescription.empty())
		strDescription = SERVICE_DESCRIPTION;

	wstring strExe = PATHUTILS::path_join(PATHUTILS::home_dir(), L"ARCUpdate.exe");
	CService service;
	if( !service.Exists(SERVICE_NAME) )
	{
		if( !service.Create2( SERVICE_NAME, strExe.c_str(), strDisplayName.c_str(), (LPWSTR)strDescription.c_str(), 
							 NULL, SC_MANAGER_ALL_ACCESS, SERVICE_WIN32_OWN_PROCESS, SERVICE_AUTO_START, 
							 SERVICE_ERROR_NORMAL, NULL, NULL, NULL, NULL) )
		{
			log.LogW(LL_ERR, GetLastError(), L"Failed to create service %s",  SERVICE_NAME );
			return FALSE;
		}
	}
	if( !service.Start( SERVICE_NAME ) )
	{
		log.LogW(LL_ERR, GetLastError(), L"Failed to start service %s", SERVICE_NAME );
		return FALSE;
	}
	return TRUE;
}

BOOL RemoveService()
{
	CDbgLog log;
	log.LogW(LL_INF, 0, L"Start to remove update service." );

	CService service;
	if( !service.Exists( SERVICE_NAME ) )
	{
		log.LogW(LL_INF, 0, L"Service %s does not exist", SERVICE_NAME );
		return TRUE;
	}

	if( !service.Stop( SERVICE_NAME ) )
	{
		log.LogW(LL_INF, GetLastError(), L"Failed to stop service %s", SERVICE_NAME );
		return FALSE;
	}

	if( !service.Delete( SERVICE_NAME ) )
	{
		log.LogW(LL_INF, GetLastError(), L"Failed to delete service %s", SERVICE_NAME );
		return FALSE;
	}
	return TRUE;
}
///////////////////////////////////////////////////////////////////////////////////////////////////////
SERVICE_STATUS				g_ServiceStatus;
SERVICE_STATUS_HANDLE		g_ServiceStatusHandle;
void WINAPI ServiceMain(DWORD argc, LPTSTR *argv)
{
	MySetExceptionHandler(FALSE);

    DWORD status = 0; 
    DWORD specificError = 0; 
	
	CDbgLog log;
    
	g_ServiceStatus.dwServiceType        = SERVICE_WIN32; 
    g_ServiceStatus.dwCurrentState       = SERVICE_START_PENDING; 
    g_ServiceStatus.dwControlsAccepted   = SERVICE_ACCEPT_STOP; 
    g_ServiceStatus.dwWin32ExitCode      = 0; 
    g_ServiceStatus.dwServiceSpecificExitCode = 0; 
    g_ServiceStatus.dwCheckPoint         = 0; 
    g_ServiceStatus.dwWaitHint           = 0; 
 
    g_ServiceStatusHandle = RegisterServiceCtrlHandler( SERVICE_NAME,ServiceCtrlHandler);  
    if(g_ServiceStatusHandle == (SERVICE_STATUS_HANDLE)0 ) 
    { 
		log.LogW(LL_ERR, GetLastError(), L"ServiceMain: Failed to RegisterServiceCtrlHandler" );
        return; 
    }     

    g_ServiceStatus.dwCurrentState       = SERVICE_RUNNING; 
    g_ServiceStatus.dwCheckPoint         = 0; 
    g_ServiceStatus.dwWaitHint           = 0;  
    if (!SetServiceStatus (g_ServiceStatusHandle, &g_ServiceStatus)) 
    { 
		log.LogW(LL_ERR, GetLastError(), L"ServiceMain: Failed to set service status as RUNNING" );
    } 
	
	wstring strUDPHome = PRODUTILS::GetUDPHome();
	PATHUTILS::path_ensure_end_without_slash(strUDPHome);
	SetEnvironmentVariable(L"UDPHOME", strUDPHome.c_str() );

	g_pUpdateService = new CUpService();
	g_pUpdateService->Run();

	g_ServiceStatus.dwCurrentState       = SERVICE_STOPPED; 
	if (!SetServiceStatus (g_ServiceStatusHandle, &g_ServiceStatus)) 
		log.LogW(LL_ERR, GetLastError(), L"ServiceMain: Failed to set service status as STOPPED" );

	SAFE_DELETE(g_pUpdateService);
}

void WINAPI ServiceCtrlHandler(DWORD Opcode)
{
	SetUnhandledExceptionFilter( (LPTOP_LEVEL_EXCEPTION_FILTER)HandleSEH );

	CDbgLog log;
	switch(Opcode) 
	{ 
	case SERVICE_CONTROL_PAUSE: 
		log.LogW(LL_INF, 0, L"ServiceCtrlHandler: Received a PAUSE control." );
		g_ServiceStatus.dwCurrentState = SERVICE_PAUSED; 
		break; 

	case SERVICE_CONTROL_CONTINUE: 
		log.LogW(LL_INF, 0, L"ServiceCtrlHandler: Received a continue control." );
		g_ServiceStatus.dwCurrentState = SERVICE_RUNNING; 
		break; 

	case SERVICE_CONTROL_STOP: 
		log.LogW(LL_INF, 0, L"ServiceCtrlHandler: Received a stop control." );
		g_ServiceStatus.dwWin32ExitCode = 0; 
		g_ServiceStatus.dwCurrentState  = SERVICE_STOP_PENDING; 
		g_ServiceStatus.dwCheckPoint    = 0; 
		g_ServiceStatus.dwWaitHint      = 0; 
		SetServiceStatus (g_ServiceStatusHandle, &g_ServiceStatus);		
		
		if (g_pUpdateService)
			g_pUpdateService->Stop();

		break;
	case SERVICE_CONTROL_INTERROGATE: 
		break; 
	}      
	return; 
}

//
// generate minidump of this exe.
// <> the code was extracted from NativeFacade.dll
//
LONG HandleSEH( const EXCEPTION_POINTERS* _pEx )
{
	CDbgLog log;
	if (_pEx->ExceptionRecord->ExceptionCode == EXCEPTION_STACK_OVERFLOW)
	{
		log.LogW(LL_ERR, -1, __WFUNCTION__  L": stack overflow caught.");
		return EXCEPTION_CONTINUE_SEARCH;
	}

	log.LogW(LL_ERR, -1, __WFUNCTION__  L": exception caught" );
	WCHAR szEptName[MAX_PATH * 2] = {0}, szDllName[MAX_PATH] = {0}, *backslash = NULL;

	WCHAR szTempPath[MAX_PATH * 2] = {0};

	wstring strHomeDir = PATHUTILS::home_dir();
	
	int iIndex = 0;
	do
	{
		swprintf_s(szTempPath, L"%s\\ARCUpdate%05d.dmp", strHomeDir.c_str(), iIndex);
		iIndex++;
	}
	while (PATHUTILS::is_file_exist(szTempPath));
	    
	GenDumpFile(szTempPath, _pEx);
	return EXCEPTION_EXECUTE_HANDLER;
}

typedef BOOL (WINAPI * PFN_MINIDUMPWRITEDUMP)( \
											  IN HANDLE hProcess, \
											  IN DWORD ProcessId, \
											  IN HANDLE hFile, \
											  IN MINIDUMP_TYPE DumpType, \
											  IN CONST PMINIDUMP_EXCEPTION_INFORMATION ExceptionParam, OPTIONAL \
											  IN CONST PMINIDUMP_USER_STREAM_INFORMATION UserStreamParam, OPTIONAL \
											  IN CONST PMINIDUMP_CALLBACK_INFORMATION CallbackParam OPTIONAL \
											  );

void  GenDumpFile(WCHAR *szDumpFileName, const EXCEPTION_POINTERS *pExcept)
{
	DWORD dwExceptCode = 0;
	DWORD dwProcId = GetCurrentProcessId();
	HANDLE hProc = INVALID_HANDLE_VALUE;
	HANDLE hFile = INVALID_HANDLE_VALUE;
	MINIDUMP_EXCEPTION_INFORMATION exceptInfo;

	HMODULE	hDbgHelp = NULL;
	PFN_MINIDUMPWRITEDUMP pfnMiniDumpWriteDump = NULL;

	hDbgHelp = LoadLibraryW(L"dbghelp.dll");
	if(NULL != hDbgHelp)
	{
		pfnMiniDumpWriteDump = (PFN_MINIDUMPWRITEDUMP)GetProcAddress(hDbgHelp, "MiniDumpWriteDump");
		if(NULL != pfnMiniDumpWriteDump)
		{
			hProc = ::OpenProcess(PROCESS_ALL_ACCESS,FALSE,GetCurrentProcessId());
			if(NULL == hProc)
				hProc = GetCurrentProcess(); // hanl got here is a pseudo handle to current process

			hFile = CreateFileW(szDumpFileName, GENERIC_WRITE, FILE_SHARE_READ, NULL,
				CREATE_ALWAYS, 0, NULL);
			if(INVALID_HANDLE_VALUE != hFile)

			{
				exceptInfo.ThreadId = GetCurrentThreadId();
				exceptInfo.ExceptionPointers = (EXCEPTION_POINTERS *)pExcept;
				exceptInfo.ClientPointers = TRUE;
				MINIDUMP_TYPE DumpType = MiniDumpNormal;
				pfnMiniDumpWriteDump( hProc, dwProcId, hFile, DumpType, 
					&exceptInfo, NULL, NULL);
				CloseHandle(hFile);
			}
		}
		FreeLibrary(hDbgHelp);
	}
}

