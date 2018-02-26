#include "stdafx.h"
#include "UpLib.h"
#include "DbgLog.h"
#include "Log.h"
#include "..\UpdateRes\ARCUpdateRes.h"

namespace ACTLOGUTILS
{
	static void systime_2_ulonglong(const SYSTEMTIME& sysTime, ULONGLONG& ullTime)
	{
		FILETIME ft;
		::SystemTimeToFileTime(&sysTime, &ft);

		LARGE_INTEGER liTime;
		liTime.HighPart = ft.dwHighDateTime;
		liTime.LowPart = ft.dwLowDateTime;
		liTime.QuadPart -= 116444736000000000;
		liTime.QuadPart /= 10000;

		ullTime = liTime.QuadPart;
	}

	typedef DWORD(*PFUNC_EActLogMessage) (DWORD Flags, LPCWSTR lpszMsg);
	typedef DWORD(*PFUNC_DataSyncRecord) (const DATASYNC_ARGS &, const WCHAR*);

	class CLogWrapper
	{
	public:
		CLogWrapper();

		~CLogWrapper();

		DWORD Log(DWORD dwFlags, LPCWSTR pszMessage);

		DWORD DataSyncRecord(const DATASYNC_ARGS &dataSyncArgs, const WCHAR* szRecord);

	protected:
		HMODULE					m_hLogDll;
		PFUNC_EActLogMessage	m_pFuncLog;
		PFUNC_DataSyncRecord	m_pFuncDataSyncRecord;
	};

	CLogWrapper::CLogWrapper()
		: m_hLogDll(NULL)
		, m_pFuncLog(NULL)
		, m_pFuncDataSyncRecord(NULL)
	{
		wstring strLogDll = PRODUTILS::GetUpdateManagerHome();
		strLogDll = PATHUTILS::path_join(strLogDll, L"Log.dll");
		m_hLogDll = ::LoadLibrary(strLogDll.c_str());
		if (m_hLogDll != NULL)
		{
			m_pFuncLog = (PFUNC_EActLogMessage)::GetProcAddress(m_hLogDll, "EActLogMessage");
			m_pFuncDataSyncRecord = (PFUNC_DataSyncRecord)::GetProcAddress(m_hLogDll, "DataSyncRecord");
		}
	}

	CLogWrapper::~CLogWrapper()
	{
		if (m_hLogDll != NULL)
			FreeLibrary(m_hLogDll);
		m_hLogDll = NULL;
	}

	DWORD CLogWrapper::Log(DWORD dwFlags, LPCWSTR pszMessage)
	{
		if (m_pFuncLog)
			m_pFuncLog(dwFlags, pszMessage);
		return 0;
	}

	DWORD CLogWrapper::DataSyncRecord(const DATASYNC_ARGS &dataSyncArgs, const WCHAR* szRecord)
	{
		if (m_pFuncDataSyncRecord)
			m_pFuncDataSyncRecord(dataSyncArgs, szRecord);
		return 0;
	}

	void ActivityLog(DWORD dwProduct, DWORD dwLevel, DWORD dwMsgID, ...)
	{
		DWORD dwUpdate = 0; DWORD dwBuild = 0; DWORD dwPatch = 0; DWORD dwSvrRoles = 0;
		UPUTILS::GetUpdateInternalInfo(dwUpdate, dwBuild, dwPatch, dwSvrRoles);

		wstring strUpdateTitle = UPUTILS::GetUpdateResourceString(ARCUPDATE_ACTLOG_TITLE);
		va_list pArgList = NULL;
		va_start(pArgList, dwMsgID);
		wstring strMsg = UPUTILS::GetUpdateResourceStringEx(dwMsgID, &pArgList);
		va_end(pArgList);
		if (strMsg.empty())
			return;

		CLogWrapper logWrapper;
		strMsg = strUpdateTitle + L" " + strMsg;
		if (dwProduct == ARCUPDATE_PRODUCT_AGENT || dwProduct == ARCUPDATE_PRODUCT_SELFUPDATE)
		{
			if (0 != (dwSvrRoles & 1)) // if agent installed, output activity log to agent and then sync up to console
			{
				logWrapper.Log(dwLevel, strMsg.c_str());
				return;
			}
		}

		SYSTEMTIME utcTime, localTime;
		ZeroMemory(&utcTime, sizeof(utcTime));
		ZeroMemory(&localTime, sizeof(localTime));
		::GetSystemTime(&utcTime);
		::GetLocalTime(&localTime);
		ULONGLONG ullUtcTime = 0, ullLocalTime = 0;
		systime_2_ulonglong(utcTime, ullUtcTime);
		systime_2_ulonglong(localTime, ullLocalTime);

		WCHAR* pwszMessage = new WCHAR[4096];
		ZeroMemory(pwszMessage, 4096 * sizeof(WCHAR));
		_snwprintf_s(pwszMessage, 4096, _TRUNCATE, SYNC_ACTIVITY_LOG_FORMAT, ACTLOG_VERSION, APT_CPM, ullUtcTime, ullLocalTime, 0,
			dwLevel, AJT_COMMON, L"", L"", L"", L"", L"", L"", L"", L"", strMsg.c_str());

		DATASYNC_ARGS syncArgs;
		syncArgs.dwSyncFrom = SYNC_DATA_FROM_CPM;
		syncArgs.dwSyncTo = SYNC_DATA_TO_CPM;
		syncArgs.dwSyncType = SYNC_DATA_UPDATE_ACTLOG;
		logWrapper.DataSyncRecord(syncArgs, pwszMessage);
		delete[] pwszMessage;
	}

	void ActivityLogEx(DWORD dwProduct, DWORD dwLevel, const wstring& strLog)
	{
		DWORD dwUpdate = 0; DWORD dwBuild = 0; DWORD dwPatch = 0; DWORD dwSvrRoles = 0;
		UPUTILS::GetUpdateInternalInfo(dwUpdate, dwBuild, dwPatch, dwSvrRoles);

		wstring strUpdateTitle = UPUTILS::GetUpdateResourceString(ARCUPDATE_ACTLOG_TITLE);

		CLogWrapper logWrapper;
		wstring strMsg = strUpdateTitle + L" " + strLog;
		if (dwProduct == ARCUPDATE_PRODUCT_AGENT || dwProduct == ARCUPDATE_PRODUCT_SELFUPDATE)
		{
			if (0 != (dwSvrRoles & 1)) // if agent installed, output activity log to agent and then sync up to console
			{
				logWrapper.Log(dwLevel, strMsg.c_str());
				return;
			}
		}

		SYSTEMTIME utcTime, localTime;
		ZeroMemory(&utcTime, sizeof(utcTime));
		ZeroMemory(&localTime, sizeof(localTime));
		::GetSystemTime(&utcTime);
		::GetLocalTime(&localTime);
		ULONGLONG ullUtcTime = 0, ullLocalTime = 0;
		systime_2_ulonglong(utcTime, ullUtcTime);
		systime_2_ulonglong(localTime, ullLocalTime);

		WCHAR* pwszMessage = new WCHAR[4096];
		ZeroMemory(pwszMessage, 4096 * sizeof(WCHAR));
		_snwprintf_s(pwszMessage, 4096, _TRUNCATE, SYNC_ACTIVITY_LOG_FORMAT, ACTLOG_VERSION, APT_CPM, ullUtcTime, ullLocalTime, 0,
			dwLevel, AJT_COMMON, L"", L"", L"", L"", L"", L"", L"", L"", strMsg.c_str());

		DATASYNC_ARGS syncArgs;
		syncArgs.dwSyncFrom = SYNC_DATA_FROM_CPM;
		syncArgs.dwSyncTo = SYNC_DATA_TO_CPM;
		syncArgs.dwSyncType = SYNC_DATA_UPDATE_ACTLOG;
		logWrapper.DataSyncRecord(syncArgs, pwszMessage);
		delete[] pwszMessage;
	}
}