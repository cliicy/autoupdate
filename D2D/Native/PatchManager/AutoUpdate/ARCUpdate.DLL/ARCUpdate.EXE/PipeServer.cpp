#include "stdafx.h"
#include "PipeServer.h"
/*
*	the implementation of PipeServer
*/
CPipeServer::CPipeServer()
	: CThreadBase( FALSE )
	, m_bStopped(FALSE)
	, m_hPipe(INVALID_HANDLE_VALUE)
	, m_strPipeName(COMM_PIPE_NAME)
{
	::InitializeCriticalSection(&m_cs);
}

CPipeServer::~CPipeServer()
{
	_closeNamedPipe();
	::DeleteCriticalSection(&m_cs);
}

DWORD CPipeServer::Main()
{
	DWORD dwRet = 0;
	while (!m_bStopped)
	{
		dwRet = _createNamedPipe();
		if (0 != dwRet)
			return dwRet;
		// Wait for the client to connect; if it succeeds, 
		// the function returns a nonzero value. If the function
		// returns zero, GetLastError returns ERROR_PIPE_CONNECTED. 
		BOOL bConnected = ConnectNamedPipe(m_hPipe, NULL) ? TRUE : (GetLastError() == ERROR_PIPE_CONNECTED);
		if (!bConnected)
		{
			dwRet = GetLastError();
			if (dwRet == ERROR_PIPE_CONNECTED)
				bConnected = TRUE;
			m_log.LogW(LL_DET, dwRet, L"%s: ConnectNamedPipe returned %d", __WFUNCTION__, bConnected);
		}
		
		if (m_bStopped)
		{
			_closeNamedPipe();
			break;
		}

		if (bConnected)
		{
			m_log.LogW(LL_DET, dwRet, L"%s: Client connected, creating a processing session.", __WFUNCTION__);

			CPipeSessionBase* pSession = _createPipeSession();
			if (pSession)
				pSession->Start();
		}
		else
		{
			// The client could not connect, so close the pipe. 
			_closeNamedPipe();
		}
	}
	return 0;
}

DWORD CPipeServer::Stop()
{
	m_log.LogW(LL_INF, 0, L"%s: Stop the pipe server", __WFUNCTION__);
	m_bStopped = TRUE;
	_closeNamedPipe();
	m_sessions.StopAllSessions();
	return 0;
}

DWORD CPipeServer::_createNamedPipe()
{
	DWORD dwRet = 0;
	EnterCriticalSection(&m_cs);

	SECURITY_DESCRIPTOR sd;
	SECURITY_ATTRIBUTES sa;
	InitializeSecurityDescriptor(&sd, SECURITY_DESCRIPTOR_REVISION);
	SetSecurityDescriptorDacl(&sd, TRUE, (PACL)NULL, FALSE); //set all the user can access the object      
	sa.nLength = sizeof(SECURITY_ATTRIBUTES);
	sa.bInheritHandle = FALSE;
	sa.lpSecurityDescriptor = &sd;

	m_hPipe = CreateNamedPipe(
		COMM_PIPE_NAME,					// pipe name 
		PIPE_ACCESS_DUPLEX |			// read/write access 
		FILE_FLAG_OVERLAPPED,			// overlapped mode 
		PIPE_TYPE_MESSAGE |				// message type pipe 
		PIPE_READMODE_MESSAGE |			// message-read mode 
		PIPE_WAIT,						// blocking mode 
		PIPE_UNLIMITED_INSTANCES,		// max. instances  
		COMM_BUFFER_SIZE_IN,			// output buffer size 
		COMM_BUFFER_SIZE_OUT,			// input buffer size 
		0,								// client time-out 
		&sa);
	if (m_hPipe == INVALID_HANDLE_VALUE)
	{
		dwRet = GetLastError();
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to create named pipe %s", __WFUNCTION__, m_strPipeName.c_str());
	}
	::LeaveCriticalSection(&m_cs);

	return dwRet;
}

void CPipeServer::_closeNamedPipe()
{
	EnterCriticalSection(&m_cs);
	if (m_hPipe != INVALID_HANDLE_VALUE){
		CloseHandle(m_hPipe);
		m_hPipe = INVALID_HANDLE_VALUE;
	}
	::LeaveCriticalSection(&m_cs);
}

CPipeSessionBase* CPipeServer::_createPipeSession()
{
	CPipeSessionBase* pSession = NULL;
	EnterCriticalSection(&m_cs);
	if (m_hPipe != INVALID_HANDLE_VALUE){
		pSession = m_sessions.CreateSession(m_hPipe);
	}
	::LeaveCriticalSection(&m_cs);
	return pSession;
}