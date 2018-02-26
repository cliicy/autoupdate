// ARCAutoUpdate.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <Windows.h>
#include <Winsvc.h>
#include "UpdateService.h"
#include "CUpService.h"

DWORD WINAPI AFCheckBIUpdate()
{
	CDbgLog logObj;
	DWORD dwRet = 0;

	DWORD dwProd = ARCUPDATE_PRODUCT_FULL;
	if (dwProd == ARCUPDATE_PRODUCT_GATEWAY){
		logObj.LogW(LL_ERR, 0, L"%s: Auto-update is not supported on gateway.", __WFUNCTION__);
		return ERROR_INVALID_PARAMETER;
	}

	_CARCUpdateDLL dll;
	dwRet = dll.Load(NULL);
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: AUTOUPDATE - Failed to load module ARCUpdate.dll", __WFUNCTION__);
		return dwRet;
	}

	FUNC_CheckBIUpdate func = FuncOfDll<FUNC_CheckBIUpdate>(dll.Dll(), "CheckBIUpdate");
	if (!func)
	{
		logObj.LogW(LL_ERR, 87, L"%s: oooohh AUTOUPDATE - Failed to get proc address of CheckBIUpdate", __WFUNCTION__);
		return 87;
	}

	return func(dwProd, 0, NULL, TRUE);
}

int _tmain(int argc, _TCHAR* argv[])
{
	wstring strLogFile = PATHUTILS::path_join(PATHUTILS::home_dir(), L"\\Logs\\ARCUpdate.log");
	CDbgLog::SetGlobalLogFileName( strLogFile.c_str() );

	CDbgLog log;
	log.LogW(LL_INF, 0, L"Command Line: %s hahahahahaha", ::GetCommandLine() );
	log.LogW(LL_INF, 0, L"argc= %d wawawa", argc);

	//for test added by cliicy.luo
	//AFCheckBIUpdate();
	//for test added by cliicy.luo

	if(argc==1)
	{
		log.LogW(LL_INF, 0, L"argc=1 ----wawawa");
		SERVICE_TABLE_ENTRY DispatchTable[]={ {SERVICE_NAME, ServiceMain },{NULL,NULL}};  
		StartServiceCtrlDispatcher(DispatchTable); 
	}
	else if( argc==2 )
	{
		if(_wcsicmp(argv[1], L"-i")==0 || _wcsicmp(argv[1], L"/i")==0)
		{
			if(InstallService())
				printf("\nService Installed Sucessfully.");
			else
				printf("\nError Installing Service.");
			return 0;
		}
		else if(_wcsicmp(argv[1], L"-r")==0 || _wcsicmp(argv[1], L"/r")==0)
		{
			if(RemoveService())
				printf("\nService Removed Sucessfully.");
			else
				printf("\nError Removing Service.");
			return 0;
		}
		else
		{
			printf("\nInvalid command line. -i to install service, -r to remove service.");
			return 0;
		}
	}
	else if( argc==3 && (_wcsicmp(argv[1], L"-install")==0 || _wcsicmp(argv[1], L"/install")==0) )
	{
		DWORD dwProductToInstall = ARCUPDATE_PRODUCT_AUTOSELECT;
		if( _wcsicmp(argv[2], L"-engine")==0 || _wcsicmp(argv[2], L"/engine")==0 )
		{
			dwProductToInstall = ARCUPDATE_PRODUCT_AGENT;
		}
		else if( _wcsicmp(argv[2], L"-full")==0 || _wcsicmp(argv[2], L"/full")==0 )
		{
			dwProductToInstall = ARCUPDATE_PRODUCT_FULL;
		}

		if( dwProductToInstall == ARCUPDATE_PRODUCT_AUTOSELECT )
		{
			log.LogMessageW(LL_ERR, 0, L"Invalid command line." );
		}
		else
		{
			//return InstallUpdate( dwProductToInstall );
		}
	}
	else
	{
		log.LogMessageW(LL_ERR, 0, L"Invalid command line." );
	}
	log.LogW(LL_INF, 0, L"oooo SEE will exit %s", __WFUNCTION__);

	Sleep(60000);
	return 0;
}
