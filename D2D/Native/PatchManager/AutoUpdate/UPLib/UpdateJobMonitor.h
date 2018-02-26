#pragma once
#include "UpLib.h"
#include "DbgLog.h"
#include "TShareMemory.h"
class CUpdateJobMonitor : public IUpdateJobMonitor
{
public:
	CUpdateJobMonitor(DWORD dwProduct);

	virtual ~CUpdateJobMonitor();

public:
	virtual void	Release();

	virtual void	CancelUpdateJob();

	virtual BOOL	IsJobCanceled();

	virtual void	UpdateJobPhase(DWORD dwJobPhase);

	virtual DWORD	GetJobPhase();

	virtual void	UpdateTotalSize(ULONGLONG ullSize);

	virtual void	InitDownloadedSize(ULONGLONG ullSize);

	virtual void	UpdateDownloadedSize(ULONGLONG ullSize);

	virtual DWORD	GetJobStatus();

	virtual void	StartUpdateJob();

	virtual void	EndUpdateJob(DWORD dwStatus, LONG lLastError);

	virtual DWORD	GetDataOfJobMonitor(UPDATE_JOB_MONITOR& pData);

	virtual LONG	GetLastError();

	virtual void	UpdateProcessID(DWORD dwProcID);

public:
	virtual DWORD	InitJobMonitor();

	virtual DWORD	OpenJobMonitor();
protected:
	DWORD								m_dwJobID;
	CDbgLog								m_log;
	CTShareMemory<UPDATE_JOB_MONITOR>*	m_pSm;
};

