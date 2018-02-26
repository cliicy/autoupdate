#pragma once
#include "PipeServer.h"
#include "UpdateScheduler.h"
/*
the thread to clean self update
*/
class CThreadClean : public CThreadBase
{
public:
	CThreadClean();

	virtual ~CThreadClean();

	virtual DWORD	Main();

	virtual DWORD	Stop();

protected:
	DWORD clean_Folder(const wstring strFolder, bool bIncludeSelf = true);

protected:
	CDbgLog			m_log;
	BOOL			m_bStopFlag;

};

/*
the class of update service
*/
class CUpService
{
public:
	CUpService();

	~CUpService();

	DWORD Run();

	DWORD Stop();

protected:
	CDbgLog		m_log;

protected:
	CPipeServer*			m_pPipeServer;
	CThreadClean*			m_pThreadClean;

protected:
	CUpdateScheduler*		m_pAgentSchedular;
	CUpdateScheduler*		m_pConsoleSchedular;
};

