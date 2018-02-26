#pragma once

#include <vector>
#include "RPSCoreAPIInterface.h"
#include "AFDefine.h"

#include <jni.h>

using namespace std;

class IXmlParser;


#define CONTROL_TYPE_CANCEL_LOCAL 0x10
#define CONTROL_TYPE_CANCEL_LOCAL_ABSOLUTE (CONTROL_TYPE_CANCEL_LOCAL | 1)

#define CONTROL_TYPE_CANCEL_REMOTE 0x20
#define CONTROL_TYPE_RESUME_REMOTE 0x40


#define CANCEL_JOB_STATUS_NOT_AVAILABLE -1
#define CANCEL_JOB_STATUS_FINISHED 1
#define CANCEL_JOB_STATUS_ON_GOING 0

typedef struct _RemoteHost
{
	wstring m_strHostName;
	wstring m_strProtocol;
	wstring m_strLoginUUID;
	wstring m_strUsername;
	wstring m_strPassword;
	wstring m_strPolicy;
	long m_nPort;
} RemoteHost;

typedef struct _JobFilter
{
	DWORD dwJobType;

	DWORD* pdwJobID;
	int nJobIDCount;

	wstring* pszPolicyID;
	int nPolicyIDCount;

	wstring* pszRPSName;
	int nRPSNameCount;

	wstring* pszD2DName;
	int nD2DNameCount;

	static void* AllocMemeber(size_t size){return malloc(size);}
	static void FreeMember(void* mem){if(mem){free(mem);}}
	static void ClearMember(void* mem, size_t size){if(mem){memset(mem, 0, size);}}
} JobFilter;

class ILogger
{
public:
	virtual void WriteLog(int nLevel, int nCode, LPCTSTR lpszMessage) = 0;
};

class CRPSCancelJob
{
public:
	CRPSCancelJob(DWORD dwFlag, BOOL bAsync, JobFilter filter, JNIEnv* env, ILogger* pLogger);
	~CRPSCancelJob();

public:
	long ControlJobs();
	long GetCancelJobStatus();

	void FreeFilter();

public:
	const RemoteHost* GetOneHost();
	const JOB_CONTEXT* GetOneJob();

private:
	void EnumRemoteD2D();
	void EnumRemoteRPS();

	void EnumLocalJobs();

	long CancelJob(const JOB_CONTEXT& job);
	long TerminateJob(const JOB_CONTEXT& job);
	long TerminateJob(HANDLE jobProc);

	long CancelRemoteJobs();
	long CancelLocalJobs();
	long ResumeRemoteJobs();

	BOOL InitVM();
	void DeInitVM();

	IXmlParser* GetXmlParser();

	BOOL FilterJob(const JOB_CONTEXT& job);
	BOOL FilterRPS(const RemoteHost& host);
	BOOL FilterD2D(const RemoteHost& host);
	wstring DumpFilter();

	void Log(DWORD errNo, int level, wchar_t *Str, ... );

	static DWORD GetInstallFolder(wstring& dir);

	static BOOL IsJobCancellable(const JOB_CONTEXT& job);
	static DWORD WINAPI CancelRemoteJobProc(LPVOID pParam);
	static DWORD WINAPI CancelLocalJobProc(LPVOID pParam);
	static DWORD WINAPI ResumeRemoteJobProc(LPVOID pParam);

private:
	int m_nHostIndex;
	int m_nJobIndex;

	vector<RemoteHost> m_vRemoteHosts;
	vector<JOB_CONTEXT> m_vLocalJobs;

	CRITICAL_SECTION m_csHost;
	CRITICAL_SECTION m_csJob;

	HMODULE m_hXml;

	HMODULE m_hJvm;
	JavaVM* m_pVM;
	JNIEnv* m_pOrgEnv;

	vector<HANDLE> m_vhThread;

	ILogger* m_pOutLogger;

	JobFilter m_JobFilter;

	DWORD m_dwControlFlag;

	BOOL m_bAsync;
};


HANDLE GetControlRPSJobHandle(DWORD dwFlag, BOOL bAsync, JobFilter filter, JNIEnv* env, ILogger* pLogger);
DWORD ControlRPSJobs(HANDLE handle);
void FreeControlRPSJobHandle(HANDLE handle);
DWORD GetControlRPSJobStatus(HANDLE handle);