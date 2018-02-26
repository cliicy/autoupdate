#pragma once
#include "PipeSession.h"
#include "PipeSessionSet.h"
class CPipeServer : public CThreadBase
{
public:
	CPipeServer();

	virtual ~CPipeServer();

	virtual DWORD Main();

	virtual DWORD Stop();

protected:
	virtual DWORD _createNamedPipe();

	virtual void  _closeNamedPipe();

	virtual CPipeSessionBase* _createPipeSession();

protected:
	CDbgLog				m_log;
	CRITICAL_SECTION	m_cs;
	wstring				m_strPipeName;
	HANDLE				m_hPipe;
	BOOL				m_bStopped;
protected:
	CPipeSessionSet     m_sessions;
};