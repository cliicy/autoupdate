#include "stdafx.h"

#include <Winsvc.h>
#include "Utility.h"
#include <psapi.h>
#include <tlhelp32.h>
#include <errno.h>
#include <atlbase.h>
#include "Golbals.h"

DWORD String2Int( const wstring &str, int &val )
{
	const WCHAR *p = str.c_str();
	for (int i = 0; i < str.length(); i++,p++){
		if( *p < L'0' || *p > L'9' )
			return 1;
	}

	val = _wtoi(str.c_str());
	if (val == INT_MAX || val == INT_MIN)
		return 1;
	else
		return 0;
}

DWORD ConvertString2Version( wstring strVer, UINT &majorVer, UINT &minorVer )
{
	int idx1 = 0;
	int idx2 = 0;
	int idx3 = 0;
	wstring strMajor, strMinor;
	int iMajor, iMinor;

	idx1 = strVer.find(L'.');
	if( idx1 == wstring::npos )
		return 1;
	strMajor = strVer.substr(0, idx1);

	idx2 = strVer.find(L'.', idx1 + 1);
	if( idx2 == wstring::npos )
		return 1;
	strMinor = strVer.substr( idx1 + 1, idx2 - idx1 -1);

	if(String2Int(strMajor, iMajor))
		return 1;
	if(String2Int(strMinor, iMinor))
		return 1;

	majorVer = iMajor;
	minorVer = iMinor;
	return 0;
}

HANDLE DynGetProcAddress( const wstring &dllName, const char* procName, HMODULE & dll)
{
	dll = GetModuleHandle( dllName.c_str() );
	HANDLE proc = NULL;
	if( dll == NULL ){
		dll = ::LoadLibraryEx(dllName.c_str(), NULL, LOAD_WITH_ALTERED_SEARCH_PATH);
		if( dll == NULL ){
			WriteLog( L"Fail to load library %s, ec=%d", dllName.c_str(), GetLastError() );
			return NULL;
		}
	}
	proc = GetProcAddress( dll, procName );

	return proc;
}

DWORD RunProcess( const wstring &strPath, HANDLE &handle )
{
	STARTUPINFO si;
	PROCESS_INFORMATION pi;
	WCHAR strCmdLine[512];

	wcscpy_s( strCmdLine, 512, strPath.c_str() );
	si.dwFlags = STARTF_USESHOWWINDOW;
	si.wShowWindow = SW_HIDE;
	ZeroMemory( &si, sizeof(si) );
	si.cb = sizeof(si);
	ZeroMemory( &pi, sizeof(pi) );
	if( !CreateProcess( NULL,
		strCmdLine,        // Command line
		NULL,           // Process handle not inheritable
		NULL,           // Thread handle not inheritable
		FALSE,          // Set handle inheritance to FALSE
		CREATE_NO_WINDOW,   //creation flags
		NULL,           // Use parent's environment block
		NULL,           // Use parent's starting directory 
		&si,            // Pointer to STARTUPINFO structure
		&pi )           // Pointer to PROCESS_INFORMATION structure
		){
		handle = INVALID_HANDLE_VALUE;
		return GetLastError();
	}

	handle = pi.hProcess;
	return 0;
	
}

DWORD RunProcessWithWorkingDir( const wstring &strPath, HANDLE &handle, const wstring &strWorkDir)
{
	STARTUPINFO si;
	PROCESS_INFORMATION pi;
	WCHAR strCmdLine[512];

	wcscpy_s( strCmdLine, 512, strPath.c_str() );
	si.dwFlags = STARTF_USESHOWWINDOW;
	si.wShowWindow = SW_HIDE;
	ZeroMemory( &si, sizeof(si) );
	si.cb = sizeof(si);
	ZeroMemory( &pi, sizeof(pi) );
	if( !CreateProcess( NULL,
		strCmdLine,        // Command line
		NULL,           // Process handle not inheritable
		NULL,           // Thread handle not inheritable
		FALSE,          // Set handle inheritance to FALSE
		CREATE_NO_WINDOW,   //creation flags
		NULL,           // Use parent's environment block
		strWorkDir.c_str(),          
		&si,            // Pointer to STARTUPINFO structure
		&pi )           // Pointer to PROCESS_INFORMATION structure
		){
			handle = INVALID_HANDLE_VALUE;
			return GetLastError();
	}

	handle = pi.hProcess;
	return 0;

}

DWORD StartWindowsService( const wstring &strServiceName, bool bWait )
{
	SERVICE_STATUS_PROCESS ssStatus; 
	DWORD dwBytesNeeded;
	SC_HANDLE schSCManager;
	SC_HANDLE schService;
	DWORD dwRet = 0;


	// Get a handle to the SCM database. 

	schSCManager = OpenSCManager( 
		NULL,                    // local computer
		NULL,                    // servicesActive database 
		SC_MANAGER_ALL_ACCESS);  // full access rights 

	if (NULL == schSCManager) 
		return GetLastError();

	// Get a handle to the service.

	schService = OpenService( 
		schSCManager,         // SCM database 
		strServiceName.c_str(),            // name of service 
		SERVICE_ALL_ACCESS);  // full access 

	if (schService == NULL){ 
		CloseServiceHandle(schSCManager);
		return GetLastError();
	}    

	// Check the status in case the service is not stopped. 

	if (!QueryServiceStatusEx( 
		schService,                     // handle to service 
		SC_STATUS_PROCESS_INFO,         // information level
		(LPBYTE) &ssStatus,             // address of structure
		sizeof(SERVICE_STATUS_PROCESS), // size of structure
		&dwBytesNeeded ) )              // size needed if buffer is too small
	{
		CloseServiceHandle(schService); 
		CloseServiceHandle(schSCManager);
		return GetLastError(); 
	}

	// Check if the service is already running. It would be possible
	// to stop the service here, but for simplicity this example just returns. 

	if(ssStatus.dwCurrentState != SERVICE_STOPPED )
	{
		CloseServiceHandle(schService); 
		CloseServiceHandle(schSCManager);
		return 2; 
	}
	// Attempt to start the service.

	if (!StartService(
		schService,  // handle to service 
		0,           // number of arguments 
		NULL) )      // no arguments 
	{
		CloseServiceHandle(schService); 
		CloseServiceHandle(schSCManager);
		return GetLastError(); 
	}

	if( bWait ){
		// Check the status until the service is no longer start pending. 

		if (!QueryServiceStatusEx( 
			schService,                     // handle to service 
			SC_STATUS_PROCESS_INFO,         // info level
			(LPBYTE) &ssStatus,             // address of structure
			sizeof(SERVICE_STATUS_PROCESS), // size of structure
			&dwBytesNeeded ) )              // if buffer too small
		{
			CloseServiceHandle(schService); 
			CloseServiceHandle(schSCManager);
			return GetLastError(); 
		}

		while (ssStatus.dwCurrentState == SERVICE_START_PENDING) 
		{
			Sleep( 2000 );

			// Check the status again. 
			if (!QueryServiceStatusEx( 
				schService,             // handle to service 
				SC_STATUS_PROCESS_INFO, // info level
				(LPBYTE) &ssStatus,             // address of structure
				sizeof(SERVICE_STATUS_PROCESS), // size of structure
				&dwBytesNeeded ) )              // if buffer too small
			{
				CloseServiceHandle(schService); 
				CloseServiceHandle(schSCManager);
				return GetLastError();  
			}
		} 

		// Determine whether the service is running.
		CloseServiceHandle(schService); 
		CloseServiceHandle(schSCManager);
		if (ssStatus.dwCurrentState != SERVICE_RUNNING) 
			return 1;
		else
			return 0;
	}else
		return 0;
}

DWORD GetOpt( const wstring &strParamters, vector<wstring> &vecKey, vector<wstring> &vecValue )
{
	LPWSTR *szArglist = NULL;
	int nArgs = 0;
	WCHAR strDelimit[] = L"/=";

	szArglist = CommandLineToArgvW( strParamters.c_str(), &nArgs );
	if( NULL == szArglist )
		return 1;
	for( int i=0; i<nArgs; i++){
		WCHAR *token = NULL;
		WCHAR *nextToken = NULL;
		token = wcstok_s( szArglist[i], strDelimit, &nextToken );
		if( token == NULL ){
			vecKey.push_back( szArglist[i] );
			vecValue.push_back( L"" );
		}else{
			vecKey.push_back(token);
			vecValue.push_back(nextToken);
		}
	}

	if( NULL != szArglist ){
		LocalFree( szArglist );
	}
	return 0;
}

DWORD WINAPI RebootSystem( bool force )
{
	DWORD dwRet = 0;

	dwRet = AdjustPrivilege(SE_SHUTDOWN_NAME, true);
	if( dwRet )
		return 1;

	UINT flag = EWX_REBOOT;
	if(force)
		flag = flag | EWX_FORCEIFHUNG;

	dwRet = ExitWindowsEx(flag, 0);
	return 0;
}

DWORD AdjustPrivilege( LPCWSTR lpPrivilege, bool bEnable )
{
	DWORD	dwRet = ERROR_SUCCESS;
	LUID	luid;
	HANDLE	hProcessHandle = NULL;
	HANDLE	hTokenHandle = NULL;

	BOOL bRet = RevertToSelf();

	if( !LookupPrivilegeValueW(NULL, lpPrivilege, &luid) )
		dwRet = GetLastError();

	if( dwRet == ERROR_SUCCESS )
	{
		hProcessHandle = GetCurrentProcess();
		if( !OpenProcessToken(hProcessHandle, TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, &hTokenHandle) )
			dwRet = GetLastError();
	}

	if( dwRet == ERROR_SUCCESS )
	{
		TOKEN_PRIVILEGES tp;
		tp.PrivilegeCount = 1;
		tp.Privileges[0].Luid = luid;
		tp.Privileges[0].Attributes = bEnable? SE_PRIVILEGE_ENABLED : 0;

		if( !AdjustTokenPrivileges(hTokenHandle, FALSE, &tp, sizeof(TOKEN_PRIVILEGES), NULL, NULL))
			dwRet = GetLastError();
	}

	CloseHandle(hTokenHandle);
	CloseHandle(hProcessHandle);

	return dwRet;
}

DWORD KillProcess( const wstring &strProcessName )
{
	HANDLE handle = INVALID_HANDLE_VALUE;
	wstring cmd = L"taskkill.exe /F /IM ";
	cmd += strProcessName + L"\"";

	DWORD dwError = RunProcess( cmd, handle );
	if( dwError )
		return 2;
	if( WAIT_OBJECT_0 != WaitForSingleObject( handle, INFINITE ) )
		return 3;

	return 0;
}

DWORD StopWindowsService( const wstring &strServiceName, bool bWait )
{
	SERVICE_STATUS_PROCESS ssp;
	DWORD dwStartTime = GetTickCount();
	DWORD dwBytesNeeded = 0;
	DWORD dwTimeout = 30000; // 30-second time-out
	SC_HANDLE schSCManager;
	SC_HANDLE schService;
	DWORD dwRet = 0;

	// Get a handle to the SCM database. 
	schSCManager = OpenSCManager( 
		NULL,                    // local computer
		NULL,                    // ServicesActive database 
		SC_MANAGER_ALL_ACCESS);  // full access rights 

	if (NULL == schSCManager) 
	{
		WriteLog( L"OpenSCManager failed (%d)\n", GetLastError() );
		return 1;
	}

	// Get a handle to the service.

	schService = OpenService( 
		schSCManager,         // SCM database 
		strServiceName.c_str(),            // name of service 
		SERVICE_STOP | 
		SERVICE_QUERY_STATUS | 
		SERVICE_ENUMERATE_DEPENDENTS);  

	if (schService == NULL)
	{ 
		WriteLog( L"OpenService failed (%d)\n", GetLastError() ); 
		CloseServiceHandle(schSCManager);
		return GetLastError();
	}    

	// Make sure the service is not already stopped.

	if ( !QueryServiceStatusEx( 
		schService, 
		SC_STATUS_PROCESS_INFO,
		(LPBYTE)&ssp, 
		sizeof(SERVICE_STATUS_PROCESS),
		&dwBytesNeeded ) )
	{
		WriteLog( L"QueryServiceStatusEx failed (%d)\n", GetLastError()); 
		dwRet = GetLastError();
		goto stop_cleanup;
	}

	if ( ssp.dwCurrentState == SERVICE_STOPPED )
	{
		WriteLog( L"Service is already stopped.\n");
		goto stop_cleanup;
	}

	// If a stop is pending, wait for it.

	while ( ssp.dwCurrentState == SERVICE_STOP_PENDING ) 
	{
		WriteLog( L"Service stop pending...\n");
		Sleep( ssp.dwWaitHint );
		if ( !QueryServiceStatusEx( 
			schService, 
			SC_STATUS_PROCESS_INFO,
			(LPBYTE)&ssp, 
			sizeof(SERVICE_STATUS_PROCESS),
			&dwBytesNeeded ) )
		{
			WriteLog( L"QueryServiceStatusEx failed (%d)\n", GetLastError()); 
			dwRet = GetLastError();
			goto stop_cleanup;
		}

		if ( ssp.dwCurrentState == SERVICE_STOPPED )
		{
			WriteLog( L"Service stopped successfully.\n");
			dwRet = GetLastError();
			goto stop_cleanup;
		}

		if ( GetTickCount() - dwStartTime > dwTimeout )
		{
			WriteLog( L"Service stop timed out.\n");
			dwRet = GetLastError();
			goto stop_cleanup;
		}
	}

	// If the service is running, dependencies must be stopped first.

	//StopDependentServices();

	// Send a stop code to the service.

	if ( !ControlService( 
		schService, 
		SERVICE_CONTROL_STOP, 
		(LPSERVICE_STATUS) &ssp ) )
	{
		WriteLog( L"ControlService failed (%d)\n", GetLastError() );
		dwRet = GetLastError();
		goto stop_cleanup;
	}

	// Wait for the service to stop.

	if( bWait ){
		while ( ssp.dwCurrentState != SERVICE_STOPPED ) 
		{
			Sleep( ssp.dwWaitHint );
			if ( !QueryServiceStatusEx( 
				schService, 
				SC_STATUS_PROCESS_INFO,
				(LPBYTE)&ssp, 
				sizeof(SERVICE_STATUS_PROCESS),
				&dwBytesNeeded ) )
			{
				WriteLog( L"QueryServiceStatusEx failed (%d)\n", GetLastError() );
				dwRet = GetLastError();
				goto stop_cleanup;
			}

			if ( ssp.dwCurrentState == SERVICE_STOPPED )
				break;

			if ( GetTickCount() - dwStartTime > dwTimeout )
			{
				WriteLog( L"Wait timed out\n" );
				dwRet = GetLastError();
				goto stop_cleanup;
			}
		}
	}
	//printf("Service stopped successfully\n");

stop_cleanup:
	CloseServiceHandle(schService); 
	CloseServiceHandle(schSCManager);
	return dwRet;

}

DWORD IsWindowsServiceRunning( const wstring &strServiceName, OUT bool &bRuning )
{
	SERVICE_STATUS_PROCESS ssStatus; 
	DWORD dwWaitTime = 0;
	DWORD dwBytesNeeded;
	SC_HANDLE schSCManager;
	SC_HANDLE schService;
	DWORD dwRet = 0;

	// Get a handle to the SCM database. 

	schSCManager = OpenSCManager( 
		NULL,                    // local computer
		NULL,                    // servicesActive database 
		SC_MANAGER_ALL_ACCESS);  // full access rights 

	if (NULL == schSCManager) 
		return GetLastError();

	// Get a handle to the service.

	schService = OpenService( 
		schSCManager,         // SCM database 
		strServiceName.c_str(),            // name of service 
		SERVICE_ALL_ACCESS);  // full access 

	if (schService == NULL){ 
		CloseServiceHandle(schSCManager);
		return GetLastError();
	}    

	if (!QueryServiceStatusEx( 
		schService,                     // handle to service 
		SC_STATUS_PROCESS_INFO,         // information level
		(LPBYTE) &ssStatus,             // address of structure
		sizeof(SERVICE_STATUS_PROCESS), // size of structure
		&dwBytesNeeded ) )              // size needed if buffer is too small
	{
		CloseServiceHandle(schService); 
		CloseServiceHandle(schSCManager);
		return GetLastError(); 
	}


	if(ssStatus.dwCurrentState == SERVICE_RUNNING || ssStatus.dwCurrentState == SERVICE_START_PENDING ){
		bRuning = true;
	}else{
		bRuning = false;
	}

	CloseServiceHandle(schService); 
	CloseServiceHandle(schSCManager);
	return 0;
}

DWORD GetProcessByName( const wstring &strProcessName, HANDLE &hProcess )
{
	HANDLE hProcessSnap;
	PROCESSENTRY32 pe32;
	DWORD dwRet = 0;

	hProcess = NULL;
	// Take a snapshot of all processes in the system.
	hProcessSnap = CreateToolhelp32Snapshot( TH32CS_SNAPPROCESS, 0 );
	if( hProcessSnap == INVALID_HANDLE_VALUE ){
		return 1;
	}

	// Set the size of the structure before using it.
	pe32.dwSize = sizeof( PROCESSENTRY32 );

	// Retrieve information about the first process,
	// and exit if unsuccessful
	if( !Process32First( hProcessSnap, &pe32 ) )
	{
		CloseHandle( hProcessSnap );          // clean the snapshot object
		return 1;
	}

	// Now walk the snapshot of processes, and
	// display information about each process in turn
	do{
		if( 0 == CompareStringIgnoreCase( pe32.szExeFile, strProcessName ) ){
			hProcess = OpenProcess( PROCESS_ALL_ACCESS, FALSE, pe32.th32ProcessID );
			break;
		}
	} while( Process32Next( hProcessSnap, &pe32 ) );

	if( NULL == hProcess ){
		dwRet = 2;
	}

	CloseHandle( hProcessSnap );
	return dwRet;

}

int CompareStringIgnoreCase( const wstring &str1, const wstring &str2 )
{
	WCHAR *lpsz1 = NULL;
	WCHAR *lpsz2 = NULL;
	int nRet = 0;

	lpsz1 = new WCHAR[ str1.length() + 1 ];
	lpsz2 = new WCHAR[ str2.length() + 1 ];
	wcscpy_s( lpsz1, str1.length() + 1, str1.c_str() );
	wcscpy_s( lpsz2, str2.length() + 1, str2.c_str() );
	nRet = _wcsicmp( lpsz1, lpsz2 );

	delete lpsz1;
	delete lpsz2;
	return nRet;
}

DWORD ReadFileEx( __in HANDLE hFile, 
				 __out_opt LPVOID lpBuffer, 
				 __in DWORD nNumberOfBytesToRead, 
				 __out_opt LPDWORD lpNumberOfBytesRead,
				 __in_opt HANDLE hEvent, 
				 __in_opt DWORD dwMilliseconds )
{
	OVERLAPPED oOverlap;
	HANDLE arrEvents[2];
	BOOL bRet = TRUE;
	DWORD dwWait = 0;
	DWORD dwRet = 0;

	ZeroMemory( &oOverlap, sizeof(OVERLAPPED) );
	arrEvents[0] = CreateEvent( NULL, FALSE, FALSE, NULL );
	arrEvents[1] = hEvent;
	oOverlap.hEvent = arrEvents[0];

	do{
		if( !ReadFile( hFile, lpBuffer, nNumberOfBytesToRead, lpNumberOfBytesRead, &oOverlap ) ){
			if( ERROR_IO_PENDING == GetLastError() ){
				dwWait = WaitForMultipleObjects( 2, arrEvents, FALSE, dwMilliseconds);
				if( WAIT_TIMEOUT == dwWait ){
					dwRet = 2;
					break;
				}
				else if( WAIT_OBJECT_0 == dwWait ){
					if( GetOverlappedResult( hFile, &oOverlap, lpNumberOfBytesRead, FALSE )){
						dwRet = 0;
					}else{
						dwRet = 1;
					}
					break;
				}
				else if( WAIT_OBJECT_0 + 1 == dwWait ){
					dwRet = 3;
					break;
				}else{
					dwRet = 1;
					break;
				}
			}else{
				dwRet = 1;
				break;
			}
		}else{
			dwRet = 0;
			break;
		}
	}while (false);

	CloseHandle( arrEvents[0] );
	return dwRet;
}

BOOL IsFileExist( const wstring &strPath )
{
	if( _waccess_s(strPath.c_str(), 0) == ENOENT )
		return FALSE;
	else
		return TRUE;
}

DWORD SetRegistryValue( const wstring &strKeyPath, const wstring &strValueName, const wstring &strValue )
{
	CRegKey reg;
	LONG nRet;

	nRet = reg.Open( HKEY_LOCAL_MACHINE, NULL);
	if( nRet )
		return -2;
	nRet = reg.SetKeyValue( strKeyPath.c_str(), strValue.c_str(), strValueName.c_str() );
	if( nRet )
		return -2;

	reg.Close();

	return 0;
}
