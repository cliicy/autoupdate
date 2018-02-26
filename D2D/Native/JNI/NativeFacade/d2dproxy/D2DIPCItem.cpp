#include "./../stdafx.h"
#include "D2DIPCItem.h"

extern CDbgLog logObj;
ComItemImpl ComItemImpl::g_objCOMItem;
ComItemImpl::ComItemImpl(void)
{
	m_isQuit = FALSE;
	m_isReady =FALSE;
}

ComItemImpl::~ComItemImpl(void)
{
}

ComItemImpl * ComItemImpl::GetInstance()
{
	return &g_objCOMItem;
}

HRESULT ComItemImpl::Init(IMSGCallback * pvCBK)
{
	if(NULL !=pvCBK)
	{
		m_pvCallback = pvCBK;
		m_pvCallback->AddRef();
	}

	HRESULT hr = S_OK;

	DWORD dwTimeout =D2D_PROXY_TIMEOUT;

	SECURITY_DESCRIPTOR sd;
	SECURITY_ATTRIBUTES sa;

	InitializeSecurityDescriptor(&sd,SECURITY_DESCRIPTOR_REVISION);
	SetSecurityDescriptorDacl(&sd,TRUE,(PACL)NULL,FALSE); //set all the user can access the object
	sa.nLength=sizeof(SECURITY_ATTRIBUTES);
	sa.bInheritHandle=FALSE;
	sa.lpSecurityDescriptor=&sd;

	do 
	{
		m_pBuffer  = new BYTE[D2D_PROXY_BUF_SIZE];

		if(NULL==m_pBuffer)
		{
			hr = E_OUTOFMEMORY;
			break;
		}

		memset(m_pBuffer, 0, D2D_PROXY_BUF_SIZE);

		m_pipe = CreateNamedPipe(D2D_PROXY_IPC_PIPE_NAME_BASE, PIPE_ACCESS_DUPLEX,
			PIPE_TYPE_MESSAGE|PIPE_READMODE_MESSAGE,
			D2D_PROXY_NP_INS_NUMBER, 0, 0,
			dwTimeout,&sa);


		if(INVALID_HANDLE_VALUE ==m_pipe)
		{
			hr = E_INVALIDARG;
			break;
		} 
		DWORD dwID;
		m_hThread = CreateThread(&sa, 0, _proc, this, 0, &dwID);

		if(NULL==m_hThread)
		{
			DWORD dwErr = GetLastError();
			logObj.LogW(LL_INF, dwErr, L"Failed to CreateThread");
			hr = dwErr;
			break;
		}
		m_isReady = TRUE;
		hr = S_OK;

	} while(0);
	 
	return hr;
 }

DWORD ComItemImpl::_proc(void * pParam)
{
	if(pParam)
	{
		ComItemImpl * pThis = (ComItemImpl *)pParam;
		if(pThis)
		{
			pThis->Run();
		}
	}

	return 0;
}

HRESULT ComItemImpl::UnInit()
{
	ExitPipeThread();

	if(m_pvCallback)
	{
		m_pvCallback->Release(); 
	}
	return S_OK;
}



HRESULT ComItemImpl::ExitPipeThread()
{
	if(NULL !=m_pipe && INVALID_HANDLE_VALUE!=m_pipe)
	{
		BYTE szBuf[128] = {0};
		DWORD dwRead=0;
		BOOL bRet= CallNamedPipe(D2D_PROXY_IPC_PIPE_NAME_BASE, (LPVOID) D2D_PROXY_CMD_PIPE_EXIT, strlen(D2D_PROXY_CMD_PIPE_EXIT),
			szBuf, sizeof(szBuf),&dwRead,D2D_PROXY_TIMEOUT);

		if(FALSE==bRet)
		{
			DWORD dwRet = GetLastError();
		} 
	}

	return S_OK;
}

BOOL ComItemImpl::IsExitCMD(BYTE * pbuf, DWORD dwbufsize)
{
	if(NULL==pbuf || 0==dwbufsize)  
	{
		return FALSE;
	}
	BOOL isExit= FALSE;

	if(0==memicmp(m_pBuffer, D2D_PROXY_CMD_PIPE_EXIT, sizeof(D2D_PROXY_CMD_PIPE_EXIT ) ) )
	{
		logObj.LogW(LL_WAR, 0,   L"IsExitCMD RECEIVE EXIT CMD...");
		isExit = TRUE;
	} 

	return isExit;
}


void ComItemImpl::Run()
{
	DWORD dwRet =0;
	BOOL isOK = FALSE;

	logObj.LogW(LL_INF , 0, L"Enter NPItemImpl::Run");
	while (1)
	{

		if(TRUE==m_isQuit)
		{
			break;
		}

		BOOL isConnnected =FALSE;
		memset(m_pBuffer, 0, D2D_PROXY_BUF_SIZE);

		isConnnected = ::ConnectNamedPipe(m_pipe, NULL);
		DWORD dwRet = GetLastError();

		if( (FALSE== isConnnected) )
		{
			if(ERROR_PIPE_CONNECTED != dwRet)
			{
				logObj.LogW(LL_ERR, 0, L"Run:Connect NP failed 0x%08x...", dwRet);
				break;
			} 
		}

		do 
		{  
			DWORD dwReaded =0;
			isOK = ReadFile(m_pipe, m_pBuffer,	D2D_PROXY_BUF_SIZE-10, &dwReaded, NULL);
			if(FALSE==isOK)
			{
				dwRet = GetLastError();
				logObj.LogW(LL_ERR, 0,  L"Run:ReadFile failed 0x%08x...", dwRet);
				break;
			}

			if(dwReaded > 0)
			{
				if(TRUE==IsExitCMD(m_pBuffer, dwReaded) )
				{
					DWORD dwWritten;
					WriteFile(m_pipe, L"OK", 4,& dwWritten, NULL );
					m_isQuit = TRUE;
					break;
				}
			}

			if(dwReaded < sizeof(D2DIPC_MSG_HEADER))
			{
				DWORD dwWritten;
				WriteFile(m_pipe, L"INVALID CMD", 4,& dwWritten, NULL );
				dwReaded =0;
			}

			if(dwReaded>0)
			{  
				if(m_pvCallback)
				{ 
					DWORD dwBufsize = D2D_PROXY_BUF_SIZE -2;
					try
					{
						dwRet =m_pvCallback->OnMessage(m_pBuffer, dwReaded, m_pBuffer,dwBufsize);
					}
					catch (...)
					{
						logObj.LogW(LL_ERR, 0,  L"m_pvCallback->OnMessage cause exception...");
					}

					DWORD dwToWrite =0, dwWritten =0;
					dwToWrite =dwBufsize;
					isOK= WriteFile(m_pipe, m_pBuffer, dwToWrite,& dwWritten, NULL );
					if(FALSE ==isOK || dwToWrite!=dwWritten)
					{
						dwRet = GetLastError();
						logObj.LogW(LL_ERR, 0,  L"Run:ReadFile failed 0x%08x...", dwRet);
					} 
				} 
			}
			else
			{
				DWORD dwWritten;
				WriteFile(m_pipe, CMD_RESINVALID_DATA, sizeof(CMD_RESINVALID_DATA),& dwWritten, NULL );
			}


		} while(0);

		if(TRUE==m_isQuit)
		{
			break;
		}

		if(isConnnected)
		{
			FlushFileBuffers(m_pipe);
			isOK = ::DisconnectNamedPipe(m_pipe);
		}

	}

	logObj.LogW(LL_INF , 0, L"Leave NPItemImpl::Run");
 
	return;
}