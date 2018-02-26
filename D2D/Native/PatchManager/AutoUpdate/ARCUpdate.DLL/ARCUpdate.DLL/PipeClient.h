#pragma once
#include "..\include\pipedefines.h"
#include "..\include\PipeBase.h"
class CPipeClient
{
public:
	CPipeClient();

	virtual ~CPipeClient();

	virtual LONG Connect(const wstring& strServer = L"", const wstring& strAdmin = L"", const wstring& strPassword = L"");

	LONG triggerUpdate(DWORD dwProdOn, DWORD dwProdFor, PARCUPDATE_SERVER_INFO pSvrInfo, BOOL bHotfix=false);


	LONG queryUpdateStatus(DWORD dwProd, UPDATE_JOB_MONITOR* pJM );

	LONG cancelUpdateJob(DWORD dwProd);

	LONG isUpdateBusy(DWORD dwProd, PBOOL pbBusy);

protected:
	CPipeBase		m_pipe;
	CDbgLog			m_log;
};