#pragma once
#include "..\include\UpLib.h"
#include "XXmlNode.h"
#include <map>
#include <algorithm>
using namespace std;

class CDaemonThread : public CThreadBase
{
public:
	CDaemonThread(DWORD dwProdOn, DWORD dwProdFor, PARCUPDATE_SERVER_INFO pSvrInfo, IUpdateJobMonitor* pJobMonitor, HANDLE hJobProcess);

	virtual ~CDaemonThread();

public:
	DWORD	Main();

protected:
	CDbgLog					m_log;
	IUpdateJobMonitor*		m_pJobMonitor;
	HANDLE					m_hUpProcess;
	DWORD					m_dwProdOn;
	DWORD					m_dwProdFor;
	PARCUPDATE_SERVER_INFO	m_pSvrInfo;
};

class CUpdateJobManager
{
public:
	CUpdateJobManager();

	~CUpdateJobManager();

	LONG	StartUpdateJob( DWORD dwProdOn, DWORD dwPordFor, PARCUPDATE_SERVER_INFO pSvrInfo );

	//added by cliicy.luo to single binaries updates
	LONG	StartUpdateBIJob(DWORD dwProdOn, DWORD dwPordFor, PARCUPDATE_SERVER_INFO pSvrInfo);
	//added by cliicy.luo to single binaries updates

	void	StopAllUpdateJobs();

	void	CacheLastJobStatus(DWORD dwProd, const UPDATE_JOB_MONITOR& lastJm );

	DWORD	QueryUpdateJobStatus(DWORD dwProd, UPDATE_JOB_MONITOR& lastJm);

	DWORD	CancelUpdateJob(DWORD dwProd);

protected:
	CXXmlNode*	AddChildNode(CXXmlNode* pParentNode, const wstring& strTag, const wstring& strText);

protected:
	CRITICAL_SECTION		m_cs;
	BOOL					m_stopFlag;

	std::map<DWORD, PUPDATE_JOB_MONITOR> m_mapLastJobStatus;
protected:
	CDbgLog					m_log;

};

extern CUpdateJobManager g_upJobManager;