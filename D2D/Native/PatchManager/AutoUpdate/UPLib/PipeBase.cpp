#include "stdafx.h"
#include "PipeBase.h"
#include "UpdateDefines.h"
#include "UpLib.h"

static void standardlizeUsername(const wstring& strUsername, wstring& strDomain, wstring& strUser)
{
	wstring::size_type pos = strUsername.find_first_of(L'\\');
	if (pos != wstring::npos)
	{
		strDomain = strUsername.substr(0, pos);
		strUser = strUsername.substr(pos + 1);
		return;
	}

	pos = strUsername.find_first_of(L'@');
	if (pos != wstring::npos)
	{
		strDomain = strUsername.substr(pos + 1);
		strUser = strUsername.substr(0, pos);
		return;
	}
	strDomain = L".";
	strUser = strUsername;
	return;
}

class _CSWait
{
public:
	_CSWait(LPCRITICAL_SECTION pCS)
		: m_pCS(pCS){
		::EnterCriticalSection(m_pCS);
	}
	~_CSWait(){
		::LeaveCriticalSection(m_pCS);
	}
	void Enter(){
		::EnterCriticalSection(m_pCS);
	}
	void Leave(){
		::LeaveCriticalSection(m_pCS);
	}
protected:
	LPCRITICAL_SECTION m_pCS;
};

CPipeBase::CPipeBase(HANDLE hPipe)
	: m_hPipe(hPipe)
{
	::InitializeCriticalSection(&m_cs);
}

CPipeBase::~CPipeBase()
{
	Close();
	::DeleteCriticalSection(&m_cs);
}

LONG CPipeBase::Connect(const wstring& strServer /*= L""*/, const wstring& strAdmin /*= L""*/, const wstring& strPassword /*= L""*/)
{
	DWORD dwRet = 0;

	Close();

	_CSWait wait(&m_cs);

	wstring strPipename = COMM_PIPE_NAME; // L"\\\\.\\pipe\\8A413EF2_152D_41D1_99C6_06BA669DEF0C";
	if (!strServer.empty())
	{
		// log on user
		HANDLE hToken = NULL;
		wstring strUser, strDomain;
		standardlizeUsername(strAdmin, strDomain, strUser);
		LogonUser(strUser.c_str(), strDomain.c_str(), strPassword.c_str(), LOGON32_LOGON_NEW_CREDENTIALS, LOGON32_PROVIDER_WINNT50, &hToken);
		if (hToken == NULL){
			dwRet = GetLastError();
			return dwRet;
		}
		if (!ImpersonateLoggedOnUser(hToken))
		{
			dwRet = GetLastError();
			return dwRet;
		}
		strPipename = STRUTILS::fstr(L"\\\\%s\\pipe\\8A413EF2_152D_41D1_99C6_06BA669DEF0C", strServer.c_str());
	}

	int nTry = 0;
	while (nTry < 3)
	{
		m_hPipe = CreateFile(
			strPipename.c_str(),		// pipe name 
			GENERIC_READ | GENERIC_WRITE,
			0, NULL, OPEN_EXISTING,
			0,              // default attributes 
			NULL);          // no template file 

		// Break if the pipe handle is valid. 
		if (m_hPipe != INVALID_HANDLE_VALUE){
			break;
		}

		dwRet = GetLastError();
		// Exit if an error other than ERROR_PIPE_BUSY occurs. 

		if (dwRet != ERROR_PIPE_BUSY)
			break;

		dwRet = 0;

		// All pipe instances are busy, so wait for 20 seconds. 
		if (!WaitNamedPipe(strPipename.c_str(), 20000))
		{
			dwRet = WAIT_TIMEOUT;
			break;
		}

		nTry++;
	} while (0);

	if (dwRet != 0)
		return dwRet;

	// The pipe connected; change to message-read mode. 
	DWORD dwMode = PIPE_TYPE_BYTE;
	DWORD fSuccess = SetNamedPipeHandleState(
		m_hPipe,    // pipe handle 
		&dwMode,  // new pipe mode 
		NULL,     // don't set maximum bytes 
		NULL);    // don't set maximum time 
	if (!fSuccess)
	{
		dwRet = GetLastError();
		return dwRet;
	}

	return 0;
}

HANDLE CPipeBase::Detach()
{
	HANDLE hRet = INVALID_HANDLE_VALUE;
	_CSWait wait(&m_cs);
	hRet = m_hPipe;
	m_hPipe = INVALID_HANDLE_VALUE;
	return hRet;
}

void CPipeBase::Close()
{
	_CSWait wait(&m_cs);
	if (m_hPipe != INVALID_HANDLE_VALUE)
	{
		CloseHandle(m_hPipe);
		m_hPipe = INVALID_HANDLE_VALUE;
	}
}

void CPipeBase::Disconnect()
{
	_CSWait wait(&m_cs);
	if (m_hPipe != INVALID_HANDLE_VALUE)
	{
		FlushFileBuffers(m_hPipe);
		DisconnectNamedPipe(m_hPipe);
	}
}

LONG CPipeBase::readReq(ppacket_req pReq, void** pReqParams/*=NULL*/)
{
	if (pReq == NULL)
		return ERROR_INVALID_PARAMETER;

	ZeroMemory(pReq, sizeof(packet_req));
	LONG lRet = readData(pReq, sizeof(packet_req));
	if (lRet != 0){
		return lRet;
	}

	if (pReq->dataSize == 0){
		if (pReqParams)
			*pReqParams = NULL;
		return 0;
	}

	// read params data
	void* pBuf = malloc(pReq->dataSize);
	ZeroMemory(pBuf, pReq->dataSize);
	lRet = readData(pBuf, pReq->dataSize);
	if (lRet != 0)
	{
		SAFE_FREE(pBuf);
	}
	else
	{
		if (!pReqParams){
			SAFE_FREE(pBuf);
		}
		else{
			*pReqParams = pBuf;
		}
	}

	return lRet;
}

LONG CPipeBase::sendReq(DWORD dwCmd, DWORD dwSizeOfParams/* = 0*/, void* pReqParams/* = 0*/)
{
	if (dwSizeOfParams > 0 && pReqParams == NULL)
		return ERROR_INVALID_PARAMETER;

	packet_req req; init_packet_req(req);
	req.cmd = dwCmd;
	req.dataSize = dwSizeOfParams;
	LONG lRet = writeData(&req, sizeof(req));
	if (lRet != 0)
		return lRet;

	if (req.dataSize > 0){
		lRet = writeData(pReqParams, req.dataSize);
	}
	return lRet;
}

LONG CPipeBase::readAck(ppacket_ack pAck, void** pAckParams/*=NULL*/)
{
	if (pAck == NULL)
		return ERROR_INVALID_PARAMETER;

	ZeroMemory(pAck, sizeof(packet_ack));
	LONG lRet = readData(pAck, sizeof(packet_ack));
	if (lRet != 0){
		return lRet;
	}

	if (pAck->dataSize == 0){
		if (pAckParams)
			*pAckParams = NULL;
		return 0;
	}

	// read params data
	void* pBuf = malloc(pAck->dataSize);
	ZeroMemory(pBuf, pAck->dataSize);
	lRet = readData(pBuf, pAck->dataSize);
	if (lRet != 0)
	{
		SAFE_FREE(pBuf);
	}
	else
	{
		if (!pAckParams){
			SAFE_FREE(pBuf);
		}
		else{
			*pAckParams = pBuf;
		}
	}
	
	return lRet;
}

LONG CPipeBase::sendAck(DWORD dwCommand, LONG lError/* = 0*/, DWORD dwDataSize/* = 0*/, void* pData/* = 0*/)
{
	packet_ack ack;
	init_packet_ack(ack);
	ack.cmd = dwCommand;
	ack.dataSize = dwDataSize;
	ack.errCode = lError;
	if (ack.dataSize != 0 && pData == NULL)
		return ERROR_INVALID_PARAMETER;

	DWORD dwRet = writeData(&ack, sizeof(ack));
	if (dwRet != 0)
		return dwRet;

	if (ack.dataSize > 0)
		dwRet = writeData(pData, ack.dataSize);
	return dwRet;
}

LONG CPipeBase::readData(void* pData, DWORD dwSizeToRead)
{
	_CSWait wait(&m_cs);

	BOOL fSuccess = TRUE;
	if (dwSizeToRead == 0)
		return 0;
	if (m_hPipe == INVALID_HANDLE_VALUE)
		return ERROR_INVALID_PARAMETER;

	DWORD dwRet = 0;
	DWORD dwRemain = dwSizeToRead;
	char* pDest = (char*)pData;
	while (dwRemain > 0)
	{
		DWORD dwReaded = 0;
		DWORD dw = min(dwRemain, COMM_BUFFER_SIZE_IN);
		fSuccess = ::ReadFile(m_hPipe, pDest, dw, &dwReaded, NULL);
		if (!fSuccess)
		{
			dwRet = GetLastError();
			break;
		}
		dwRemain -= dwReaded;
		pDest += dwReaded;
	}

	return dwRet;
}

LONG CPipeBase::writeData(void* pData, DWORD dwSizeToWrite)
{
	_CSWait wait(&m_cs);

	BOOL fSuccess = TRUE;
	if (dwSizeToWrite == 0)
		return 0;
	if (m_hPipe == INVALID_HANDLE_VALUE)
		return ERROR_INVALID_PARAMETER;

	DWORD dwRet = 0;
	DWORD dwRemain = dwSizeToWrite;
	char* pSrc = (char*)pData;
	DWORD dwBlockSize = 512;
	while (dwRemain > 0)
	{
		DWORD dwWrote = 0;
		DWORD dw = min(dwRemain, dwBlockSize);
		fSuccess = ::WriteFile(m_hPipe, pSrc, dw, &dwWrote, NULL);
		if (!fSuccess)
		{
			dwRet = GetLastError();
			break;
		}
		dwRemain -= dwWrote;
		pSrc += dwWrote;
	}
	return dwRet;
}