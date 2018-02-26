#include "stdafx.h"
#include "winsvc.h"
#include "arcsetupsvc.h"
#include "firewall.h"

BOOL __stdcall IsWindowsFirewallStarted()
{
	CFirewall fw;
	HRESULT hr = fw.WindowsFirewallInitialize();
	if (FAILED(hr))
	{
		return FALSE;
	}
	return TRUE;
	
}

BOOL __stdcall WindowsFirewallRemove(LPCTSTR lpszServiceFile, LPCTSTR lpszServiceName, LPTSTR lpMsg, DWORD ccBuffer)
{
	CFirewall fw;
	HRESULT hr;
	if (fw.IsWFCOMPermited())
	{
		hr = fw.WFCOMInitialize();
		if (FAILED(hr))
		{
			fw.GetErrorMessage(lpMsg, ccBuffer);
			return FALSE;
		}

		hr = fw.WFCOMRemoveAppFirewallRule(lpszServiceFile, lpszServiceName);
		if (FAILED(hr))
		{
			fw.GetErrorMessage(lpMsg, ccBuffer);
			return FALSE;
		}
	}
	else
	{
		hr = fw.WindowsFirewallInitialize();
		if (FAILED(hr))
		{
			fw.GetErrorMessage(lpMsg, ccBuffer);
			return FALSE;
		}

		hr = fw.WindowsFirewallRemoveApp(lpszServiceFile);
		if (FAILED(hr))
		{
			fw.GetErrorMessage(lpMsg, ccBuffer);
			return FALSE;
		}
	}

	return TRUE;
}

BOOL __stdcall WFCOMGetCurrentProfile(long& CurrentProfilesType, LPTSTR lpMsg, DWORD ccBuffer)
{
	CFirewall fw;
	HRESULT hr;
	if (fw.IsWFCOMPermited())
	{
		hr = fw.WFCOMInitialize();
		if (FAILED(hr))
		{
			fw.GetErrorMessage(lpMsg, ccBuffer);
			return FALSE;
		}

		hr = fw.WFCOMGetCurrentProfileType(CurrentProfilesType);
		if (FAILED(hr))
		{
			fw.GetErrorMessage(lpMsg, ccBuffer);
			return FALSE;
		}
	}
	else
	{
		//do nothing
	}

	return TRUE;
}

BOOL __stdcall IsWFCOMPermission()
{
	CFirewall fw;
	return fw.IsWFCOMPermited();
}

BOOL __stdcall WindowsFirewallAdd(LPCTSTR lpszServiceFile, LPCTSTR lpszServiceName, long lProfileTypesBitmask,LPTSTR lpMsg, DWORD ccBuffer)
{
	CFirewall fw;
	HRESULT hr;
	if (fw.IsWFCOMPermited())
	{
		hr = fw.WFCOMInitialize();
		if (FAILED(hr))
		{
			fw.GetErrorMessage(lpMsg, ccBuffer);
			return FALSE;
		}

		//first remove the previous rule
		if (fw.IsWFCOMAppFirewallRuleExist(lpszServiceFile, lpszServiceName))
		{
			hr = fw.WFCOMRemoveAppFirewallRule(lpszServiceFile, lpszServiceName);
			if (FAILED(hr))
			{
				fw.GetErrorMessage(lpMsg, ccBuffer);
				return FALSE;
			}
		}

		//add the application
		hr = fw.WFCOMAddAppFirewallRule(lpszServiceFile, lpszServiceName, lProfileTypesBitmask, lpszServiceFile);
		if (FAILED(hr))
		{
			fw.GetErrorMessage(lpMsg, ccBuffer);
			return FALSE;
		}
	}
	else
	{
		hr = fw.WindowsFirewallInitialize();
		if (FAILED(hr))
		{
			fw.GetErrorMessage(lpMsg, ccBuffer);
			return FALSE;
		}

		hr = fw.WindowsFirewallAddApp(lpszServiceFile, lpszServiceName);
		if (FAILED(hr))
		{
			fw.GetErrorMessage(lpMsg, ccBuffer);
			return FALSE;
		}
	}
	
	return TRUE;
}

BOOL __stdcall IsWindowsFirewallAdded(LPCTSTR lpszServiceFile, LPCTSTR lpszServiceName, LPTSTR lpMsg, DWORD ccBuffer)
{
	CFirewall fw;
	BOOL bEnabled = FALSE;
	HRESULT hr;
	if (fw.IsWFCOMPermited())
	{
		hr = fw.WFCOMInitialize();
		if (FAILED(hr))
		{
			fw.GetErrorMessage(lpMsg, ccBuffer);
			return FALSE;
		}

		hr = fw.WFCOMAppFirewallRuleEnabled(lpszServiceFile, lpszServiceName, bEnabled);

		if (FAILED(hr))
		{
			fw.GetErrorMessage(lpMsg, ccBuffer);
			return FALSE;
		}
	}
	else
	{
		hr = fw.WindowsFirewallInitialize();

		if (FAILED(hr))
		{
			fw.GetErrorMessage(lpMsg, ccBuffer);
			return FALSE;
		}

		hr = fw.WindowsFirewallAppIsEnabled(lpszServiceFile, bEnabled);
		if (FAILED(hr))
		{
			fw.GetErrorMessage(lpMsg, ccBuffer);
			return FALSE;
		}
	}
	
	fw.GetErrorMessage(lpMsg, ccBuffer);

	return bEnabled;
}


BOOL StartService(SC_HANDLE schService, BOOL bBlockOperation)
{
	SERVICE_STATUS ServiceStatus;
	DWORD dwLastCheckPoint;

	if (!::StartService(schService, 0, NULL))
	{
		if (GetLastError() == ERROR_SERVICE_ALREADY_RUNNING)
			return TRUE;

		return FALSE;
	}

	if (!bBlockOperation)
		return TRUE;

	ControlService(schService, SERVICE_CONTROL_INTERROGATE, &ServiceStatus);

	while (ServiceStatus.dwCurrentState == SERVICE_START_PENDING)
	{ //wait unitl service starts
		dwLastCheckPoint = ServiceStatus.dwCheckPoint;
		Sleep(ServiceStatus.dwWaitHint);

		if (!QueryServiceStatus(schService, &ServiceStatus))
			break;

		if (dwLastCheckPoint >= ServiceStatus.dwCheckPoint)
			break;
	}

	return ServiceStatus.dwCurrentState == SERVICE_RUNNING || ServiceStatus.dwCurrentState == SERVICE_START_PENDING;
}


BOOL __stdcall StartSpecService(LPCTSTR lpszServiceName, BOOL bBlockOperation /*= TRUE*/)
{
	SC_HANDLE schSCManager;
	SC_HANDLE schService;
	BOOL bStartService = FALSE;

	schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);

	if (schSCManager == NULL)
		return FALSE;

	schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

	if (schService != NULL)
	{
		bStartService = StartService(schService, bBlockOperation);

		CloseServiceHandle(schService);
	}

	CloseServiceHandle(schSCManager);

	return bStartService;
}

BOOL StopDependentServices(LPCTSTR lpszServiceName, BOOL bBlockOperation)
{
	//Checks all services' "DependOnService" value and if lpszServiceName is in the DependOnService value 
	//then stop that service.

	HKEY hKey;
	HKEY hDependentKey;
	TCHAR szDependentService[256];
	DWORD dwSize = sizeof(szDependentService);
	TCHAR szData[256];
	DWORD dwDataSize;
	DWORD dwIndex = 0;
	BOOL bStopDependentServices = TRUE;

	if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, _T("SYSTEM\\CurrentControlSet\\Services"), 0, KEY_ALL_ACCESS, &hKey) != ERROR_SUCCESS) 
	{
		return FALSE;   //unable to enumerate services.
	}

	dwSize = _countof(szDependentService);
	while (RegEnumKeyEx(hKey, dwIndex++, szDependentService, &dwSize, NULL, NULL, NULL, NULL) == ERROR_SUCCESS)
	{
		dwSize = _countof(szDependentService);

		if (RegOpenKeyEx(hKey, szDependentService, 0, KEY_ALL_ACCESS, &hDependentKey) != ERROR_SUCCESS)
			continue;

		dwDataSize = sizeof(szData);

		if (RegQueryValueEx(hDependentKey, _T("DependOnService"), NULL, NULL, (LPBYTE)szData, &dwDataSize) == ERROR_SUCCESS)
		{
			LPCTSTR lpszService = szData;
			while (lstrlen(lpszService) > 0)
			{
				if (!lstrcmp(lpszService, lpszServiceName))
				{  //found a dependency so stop it.
					if (!StopSpecService(szDependentService, bBlockOperation, FALSE))
						bStopDependentServices = FALSE;

					break;
				}

				lpszService += lstrlen(lpszService)+1;
			}
		}

		RegCloseKey(hDependentKey);
	}

	RegCloseKey(hKey);

	return bStopDependentServices;
}

BOOL StopService(SC_HANDLE schService, LPCTSTR lpszServiceName, BOOL bBlockOperation, BOOL bStopDependents)
{
	SERVICE_STATUS ServiceStatus;
	//DWORD dwLastCheckPoint;
	DWORD 	dwMaxWaitTime;

	QueryServiceStatus(schService, &ServiceStatus);

	if (ServiceStatus.dwCurrentState == SERVICE_STOPPED || ServiceStatus.dwCurrentState == SERVICE_STOP_PENDING)
		return TRUE;

	if (!ControlService(schService, SERVICE_CONTROL_STOP, &ServiceStatus))
	{
		if (GetLastError() == ERROR_DEPENDENT_SERVICES_RUNNING && bStopDependents)
		{
			// Stop dependent services and try it again.
			StopDependentServices(lpszServiceName, bBlockOperation);

			if (!ControlService(schService, SERVICE_CONTROL_STOP, &ServiceStatus))
				return FALSE;
		}
		else
			return FALSE;
	}

	if (bBlockOperation)
	{ //Wait until service stops
		dwMaxWaitTime = 300; // Maximum wait time is 31 seconds

		// wait at least 1 second
		if (dwMaxWaitTime < 1)
			dwMaxWaitTime = 1;

		QueryServiceStatus(schService, &ServiceStatus);

		while (ServiceStatus.dwCurrentState != SERVICE_STOPPED)
		{
			//dwLastCheckPoint = ServiceStatus.dwCheckPoint;
			//avoid the service sleep too long, set min to 1500ms
			//Sleep(__min(ServiceStatus.dwWaitHint, 1500));

			//Sleep(ServiceStatus.dwWaitHint);
			Sleep(1000); // Loop 1 second at a time

			if (!QueryServiceStatus(schService, &ServiceStatus))
				break;
			//if (dwLastCheckPoint >= ServiceStatus.dwCheckPoint)
			//   break;

			// Do not wait more than suggested
			if (dwMaxWaitTime <= 0)
				break;

			dwMaxWaitTime--;
		}
	}

	//ISC 11/16/98: According to Chie's request to return TRUE only when STOPPED
	//return ServiceStatus.dwCurrentState == SERVICE_STOPPED || ServiceStatus.dwCurrentState == SERVICE_STOP_PENDING;
	return ServiceStatus.dwCurrentState == SERVICE_STOPPED;
}


BOOL __stdcall StopSpecService(LPCTSTR lpszServiceName, BOOL bBlockOperation /* = TRUE*/, BOOL bStopDependents /*= FALSE*/)
{
	SC_HANDLE schSCManager;
	SC_HANDLE schService;
	BOOL bStopService = FALSE;

	schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_CONNECT);
	if (schSCManager == NULL)
		return FALSE;

	schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

	if (schService == NULL)
	{
		if (GetLastError() == ERROR_SERVICE_DOES_NOT_EXIST)
			bStopService = TRUE;
	}
	else
	{
		bStopService = StopService(schService, lpszServiceName, bBlockOperation, bStopDependents);
		CloseServiceHandle(schService);
	}

	CloseServiceHandle(schSCManager);

	return bStopService;
}

BOOL __stdcall IsServiceStopped(LPCTSTR lpszServiceName)
{
	SC_HANDLE schSCManager;
	SC_HANDLE schService;
	SERVICE_STATUS ServiceStatus;
	BOOL bIsStopped = TRUE;

	schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);
	if (schSCManager != NULL)
	{
		schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

		if (schService != NULL)
		{  // Service exists
			QueryServiceStatus(schService, &ServiceStatus);  
			CloseServiceHandle(schService);

			if (ServiceStatus.dwCurrentState != SERVICE_STOPPED)   //check if service is running.
				bIsStopped = FALSE;
		}

		CloseServiceHandle(schSCManager);
	}

	return bIsStopped;
}

BOOL __stdcall IsServiceStarted(LPCTSTR lpszServiceName)
{
	SC_HANDLE schSCManager;
	SC_HANDLE schService;
	SERVICE_STATUS ServiceStatus;
	BOOL bStarted = FALSE;

	schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);
	if (schSCManager != NULL)
	{
		schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

		if (schService != NULL)
		{  // Service exists
			QueryServiceStatus(schService, &ServiceStatus); 
			//			CloseServiceHandle(schService);//cliicy marked it and move down for issue 14538109

			if (ServiceStatus.dwCurrentState != SERVICE_STOPPED)   //check if service is running.
				bStarted = TRUE;
			CloseServiceHandle(schService);//cliicy change line to here for issue 14538109
		}

		CloseServiceHandle(schSCManager);
	}  

	return bStarted;
}

//void StopService(SC_HANDLE schService)
//{
//	DWORD dwOldCkPoint;
//	SERVICE_STATUS	svcStatus;
//	
//	if(QueryServiceStatus(schService, &svcStatus) == FALSE)
//	{
//		return;
//	}
//	
//	//check if service is running.
//	if (svcStatus.dwCurrentState != SERVICE_STOPPED)   
//	{
//		ControlService(schService, SERVICE_CONTROL_STOP, &svcStatus);
//		
//		//Wait until service stops
//		while (svcStatus.dwCurrentState == SERVICE_STOP_PENDING) 
//		{
//			dwOldCkPoint = svcStatus.dwCheckPoint;
//			Sleep (svcStatus.dwWaitHint);
//
//			if(QueryServiceStatus (schService, &svcStatus) == FALSE)
//			{
//				return;
//			}
//			
//			if (dwOldCkPoint >= svcStatus.dwCheckPoint)
//				break;
//		}
//	}
//}

BOOL __stdcall GetServiceFileName(LPCTSTR lpszServiceName, LPTSTR lpServiceFile, DWORD ccBuffer)
{
	SC_HANDLE schSCManager = NULL;
	SC_HANDLE schService = NULL;
	LPQUERY_SERVICE_CONFIG lpsc = NULL; 
	DWORD dwBytesNeeded = 0, cbBufSize = 0;

	schSCManager = OpenSCManager(NULL, NULL, SERVICE_QUERY_CONFIG);

	if (schSCManager != NULL)
	{
		schService = OpenService(schSCManager, lpszServiceName, SERVICE_QUERY_CONFIG);

		if (schService != NULL)
		{  // Service exists
			if (!QueryServiceConfig( 
				schService, 
				NULL, 
				0, 
				&dwBytesNeeded))
			{
				DWORD dwError = GetLastError();
				if (ERROR_INSUFFICIENT_BUFFER == dwError)
				{
					cbBufSize = dwBytesNeeded;
					lpsc = (LPQUERY_SERVICE_CONFIG) LocalAlloc(LPTR, cbBufSize);
				}
				else
				{
					CloseServiceHandle(schService);
					CloseServiceHandle(schSCManager);
					return FALSE;
				}
			}

			if (!QueryServiceConfig( 
				schService, 
				lpsc, 
				cbBufSize, 
				&dwBytesNeeded) ) 
			{
				::LocalFree(lpsc);
				CloseServiceHandle(schService);
				CloseServiceHandle(schSCManager);
				return FALSE;
			}

			if (lpsc)
			{
				_tcscpy_s(lpServiceFile, ccBuffer, lpsc->lpBinaryPathName);

				::LocalFree(lpsc);
			}

			CloseServiceHandle(schService);
		}

		CloseServiceHandle(schSCManager);
	}

	return TRUE;
}

BOOL __stdcall ChangeSvcStartType(LPCTSTR lpszServiceName, const DWORD dwStartType)
{
	if (NULL == lpszServiceName)
		return FALSE;

	BOOL bRet = TRUE;
	SC_HANDLE schSCManager = NULL;
	SC_HANDLE schService = NULL;

	//Open the service control manager
	schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);

	if (schSCManager == NULL)
		return FALSE;

	//open the services
	schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

	if (schService == NULL)
	{
		//the service do not existed
		bRet = FALSE;
		goto End;
	}

	if (!ChangeServiceConfig(
		schService,        // handle of service 
		SERVICE_NO_CHANGE, // service type: no change 
		dwStartType,       // change service start type 
		SERVICE_NO_CHANGE, // error control: no change 
		NULL,              // binary path: no change 
		NULL,              // load order group: no change 
		NULL,              // tag ID: no change 
		NULL,              // dependencies: no change 
		NULL,              // account name: no change 
		NULL,              // password: no change 
		NULL))            // display name: no change
	{
		bRet = FALSE; 
	}

End:
	if (NULL != schService)
		CloseServiceHandle(schService);

	if (NULL != schSCManager)
		CloseServiceHandle(schSCManager);

	return bRet;
}

BOOL __stdcall IsServiceExist(LPCTSTR lpszServiceName)
{
	if (NULL == lpszServiceName || _tcslen(lpszServiceName)==0)
		return FALSE;

	BOOL bRet = TRUE;
	SC_HANDLE schSCManager = NULL;
	SC_HANDLE schService = NULL;

	//Open the service control manager
	schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);

	if (schSCManager == NULL)
		return FALSE;

	//open the services
	schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

	if (schService == NULL)
	{
		bRet = FALSE;
	}
	else
	{
		CloseServiceHandle(schService);
	}

	CloseServiceHandle(schSCManager);

	return bRet;
}

BOOL __stdcall ChangeServiceDisplayName(LPCTSTR lpctServiceName, LPCTSTR lpctDisplayName)
{
	BOOL bRet = FALSE;

	if (lpctServiceName && lpctDisplayName && _tcslen(lpctServiceName)>0 && _tcslen(lpctDisplayName)>0)
	{
		SC_HANDLE schSCManager;
		SC_HANDLE schService;

		// Get a handle to the SCM database. 
		schSCManager = OpenSCManager( 
			NULL,                    // local computer
			NULL,                    // ServicesActive database 
			SC_MANAGER_ALL_ACCESS);  // full access rights 

		if (NULL == schSCManager) 
		{
			return bRet;
		}

		// Get a handle to the service.
		schService = OpenService( 
			schSCManager,            // SCM database 
			lpctServiceName,               // name of service 
			SERVICE_CHANGE_CONFIG);  // need change config access 

		if (schService == NULL)
		{ 
			CloseServiceHandle(schSCManager);

			return bRet;
		}    

		// Change the service display name.
		bRet = ChangeServiceConfig(schService, SERVICE_NO_CHANGE,	SERVICE_NO_CHANGE, SERVICE_NO_CHANGE, NULL, NULL, NULL, NULL, NULL, NULL, lpctDisplayName);

		CloseServiceHandle(schService); 
		CloseServiceHandle(schSCManager);
	}

	return bRet;
}

BOOL __stdcall ChangeServiceDescription(LPCTSTR lpctServiceName, LPCTSTR lpctDescription)
{
	BOOL bRet = FALSE;

	if (lpctServiceName && lpctDescription && _tcslen(lpctServiceName)>0 && _tcslen(lpctDescription)>0)
	{
		SC_HANDLE schSCManager;
		SC_HANDLE schService;
		SERVICE_DESCRIPTION sd;

		// Get a handle to the SCM database. 
		schSCManager = OpenSCManager( 
			NULL,                    // local computer
			NULL,                    // ServicesActive database 
			SC_MANAGER_ALL_ACCESS);  // full access rights 

		if (NULL == schSCManager) 
		{
			return bRet;
		}

		// Get a handle to the service.

		schService = OpenService( 
			schSCManager,            // SCM database 
			lpctServiceName,               // name of service 
			SERVICE_CHANGE_CONFIG);  // need change config access 

		if (schService == NULL)
		{ 
			CloseServiceHandle(schSCManager);
			return bRet;
		}    

		// Change the service description.

		sd.lpDescription = (LPTSTR)lpctDescription;

		bRet = ChangeServiceConfig2(schService, SERVICE_CONFIG_DESCRIPTION, &sd);

		CloseServiceHandle(schService); 
		CloseServiceHandle(schSCManager);
	}

	return bRet;
}

BOOL __stdcall ChangeSvcFailureActions(LPCTSTR lpszServiceName, UINT uFirstAction, UINT uSecondAction, UINT uThirdAction, DWORD dwDelay)
{
	if (NULL == lpszServiceName)
		return FALSE;

	BOOL bRet = TRUE;
	SC_HANDLE schSCManager = NULL;
	SC_HANDLE schService = NULL;
	SERVICE_STATUS ServiceStatus;
	ZeroMemory(&ServiceStatus, sizeof(SERVICE_STATUS));

	SC_ACTION SCActionArray[3];
	ZeroMemory(SCActionArray, sizeof(SC_ACTION));

	SCActionArray[0].Delay = dwDelay;
	SCActionArray[0].Type = (SC_ACTION_TYPE)uFirstAction;

	SCActionArray[1].Delay = dwDelay;
	SCActionArray[1].Type = (SC_ACTION_TYPE)uSecondAction;

	SCActionArray[2].Delay = dwDelay;
	SCActionArray[2].Type = (SC_ACTION_TYPE)uThirdAction;

	SERVICE_FAILURE_ACTIONS SvcFailureAction;
	ZeroMemory(&SvcFailureAction, sizeof(SERVICE_FAILURE_ACTIONS));

	SvcFailureAction.dwResetPeriod = 604800;
	SvcFailureAction.cActions = 3;
	SvcFailureAction.lpsaActions = SCActionArray;

	//Open the service control manager
	schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);

	if (schSCManager == NULL)
		return FALSE;

	//open the services
	schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

	if (schService == NULL)
	{
		//the service do not existed
		bRet = FALSE;
		goto End;
	}

	//change the service failure action
	if (0 == ChangeServiceConfig2(schService, SERVICE_CONFIG_FAILURE_ACTIONS, &SvcFailureAction))
	{
		bRet = FALSE;
		goto End;
	}

End:
	if (NULL != schService)
		CloseServiceHandle(schService);

	if (NULL != schSCManager)
		CloseServiceHandle(schSCManager);

	return bRet;
}

BOOL __stdcall RemoveService(LPCTSTR lpszServiceName)
{
	SC_HANDLE schSCManager = NULL;
	SC_HANDLE schService = NULL;
	BOOL bRet = FALSE;

	schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);

	if (schSCManager == NULL)
		return FALSE;

	// Service exists
	schService = OpenService(schSCManager, lpszServiceName, SERVICE_ALL_ACCESS);

	if (schService == NULL)
	{
		if (GetLastError() == ERROR_SERVICE_DOES_NOT_EXIST)
			bRet = TRUE;
	}
	else
	{
		StopService(schService, lpszServiceName, TRUE, TRUE);

		if (DeleteService(schService))    //Mark service for deletion
		{
			bRet = TRUE;
		}
		else
		{
			if (GetLastError() == ERROR_SERVICE_MARKED_FOR_DELETE)
				bRet = TRUE;
		}

		CloseServiceHandle(schService);
	}

	CloseServiceHandle(schSCManager);

	return bRet;
}

BOOL __stdcall ChangeServiceFailAction(LPCTSTR lpctName)
{
	BOOL bRet = FALSE;

	if(lpctName==NULL || _tcslen(lpctName)==0)
		return bRet;

	SC_HANDLE schSCManager;
	SC_HANDLE schService;

	schSCManager = OpenSCManager( 
		NULL,                    // local computer
		NULL,                    // ServicesActive database 
		SC_MANAGER_ALL_ACCESS);  // full access rights 

	if (NULL == schSCManager) 
	{
		return bRet;
	}

	schService = OpenService( 
		schSCManager,            // SCM database 
		lpctName,               // name of service 
		SERVICE_CHANGE_CONFIG|SERVICE_START);  // need change config access 

	if (schService == NULL)
	{ 
		CloseServiceHandle(schSCManager);
		return bRet;
	}    

	SC_ACTION sca[3];

	sca[0].Delay = 60000;
	sca[0].Type = SC_ACTION_RESTART;
	sca[1].Delay = 60000;
	sca[1].Type = SC_ACTION_RESTART;
	sca[2].Delay = 60000;
	sca[2].Type = SC_ACTION_NONE;

	SERVICE_FAILURE_ACTIONS sfa = {0};

	sfa.dwResetPeriod = 24*3600;
	sfa.lpRebootMsg = NULL;
	sfa.lpCommand = NULL;
	sfa.cActions = 3;
	sfa.lpsaActions = sca;

	bRet = ChangeServiceConfig2(schService, SERVICE_CONFIG_FAILURE_ACTIONS, &sfa);

	CloseServiceHandle(schService); 
	CloseServiceHandle(schSCManager);

	return bRet;
}

BOOL __stdcall SetServiceStartType(LPCTSTR lpctName, DWORD dwStartType)
{
	SC_HANDLE schSCManager;
	SC_HANDLE schService;

	// Get a handle to the SCM database. 
	schSCManager = ::OpenSCManager( 
		NULL,                    // local computer
		NULL,                    // ServicesActive database 
		SC_MANAGER_ALL_ACCESS);  // full access rights 

	if (NULL == schSCManager) 
	{
		return FALSE;
	}

	// Get a handle to the service.
	schService = ::OpenService( 
		schSCManager,            // SCM database 
		lpctName,               // name of service 
		SERVICE_CHANGE_CONFIG);  // need change config access 

	if (NULL == schService)
	{
		::CloseServiceHandle(schSCManager);
		return FALSE;
	}    

	// Change the service start type
	BOOL bRet = ::ChangeServiceConfig( 
		schService,            // handle of service 
		SERVICE_NO_CHANGE,     // service type: no change 
		dwStartType,			// service start type 
		SERVICE_NO_CHANGE,     // error control: no change 
		NULL,                  // binary path: no change 
		NULL,                  // load order group: no change 
		NULL,                  // tag ID: no change 
		NULL,                  // dependencies: no change 
		NULL,                  // account name: no change 
		NULL,                  // password: no change 
		NULL);                 // display name: no change

	::CloseServiceHandle(schService); 
	::CloseServiceHandle(schSCManager);

	return bRet;
}