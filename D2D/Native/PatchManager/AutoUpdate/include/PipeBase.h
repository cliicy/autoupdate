#pragma once
#include "PipeDefines.h"
#include <string>
using namespace std;

//
// the base class to communicate through named pipe
//
class CPipeBase
{
public:
	CPipeBase(HANDLE hPipe);

	virtual ~CPipeBase();

public:
	virtual LONG	Connect(const wstring& strServer = L"", const wstring& strAdmin = L"", const wstring& strPassword = L"");

	virtual void	Close();

	virtual	void	Disconnect();

	virtual HANDLE	Detach();

public:
	//
	// read request from client.
	// "pReqParams" is allocated in this fucntion, but need to free outside.
	//
	virtual LONG	readReq(ppacket_req pReq, void** pReqParams=NULL);

	//
	// send request to the server
	//
	virtual LONG	sendReq(DWORD dwCmd, DWORD dwSizeOfParams = 0, void* pReqParams = 0);

	//
	// read ack from server
	// "pAckParams" is allocated in this fucntion, but need to free outside.
	//
	virtual LONG	readAck(ppacket_ack pAck, void** pAckParams=NULL);

	//
	// send request to the client
	//
	virtual LONG	sendAck(DWORD dwCommand, LONG lError = 0, DWORD dwDataSize = 0, void* pData = 0);
protected:
	//
	// read fixed size of data from pipe
	//
	virtual LONG	readData(void* pData, DWORD dwSizeToRead);

	//
	// write fixed size of data to pipe
	//
	virtual LONG	writeData(void* pData, DWORD dwSizeToWrite);

protected:
	HANDLE				m_hPipe;
	CRITICAL_SECTION	m_cs;
};