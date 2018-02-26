#include "stdafx.h"
#include "UpdateJobMonitor.h"

CUpdateJobMonitor::CUpdateJobMonitor(DWORD dwJobID)
	: m_pSm(NULL)
	, m_dwJobID(dwJobID)
{

}

CUpdateJobMonitor::~CUpdateJobMonitor()
{
	if (m_pSm){
		m_pSm->CloseSM();
	}
	SAFE_DELETE(m_pSm);
}

void CUpdateJobMonitor::Release()
{
	delete this;
}

DWORD CUpdateJobMonitor::InitJobMonitor()
{
	if (m_pSm){
		m_pSm->CloseSM();
	}
	SAFE_DELETE(m_pSm);

	m_pSm = new CTShareMemory<UPDATE_JOB_MONITOR>(BASENAME_UPDATE_JOB_MONITOR);
	DWORD dwRet = m_pSm->CreateSM(m_dwJobID);
	if (dwRet != 0)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to create job monitor share memory of %d", __WFUNCTION__, m_dwJobID);
		return dwRet;
	}

	UPDATE_JOB_MONITOR jm;
	ZeroMemory(&jm, sizeof(jm));
	dwRet = m_pSm->UpdateSM(&jm);
	if (dwRet != 0)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to write data to job monitor of ", __WFUNCTION__, m_dwJobID);
		return dwRet;
	}
	return dwRet;
}

DWORD CUpdateJobMonitor::OpenJobMonitor()
{
	if (m_pSm){
		m_pSm->CloseSM();
	}
	SAFE_DELETE(m_pSm);

	m_pSm = new CTShareMemory<UPDATE_JOB_MONITOR>(BASENAME_UPDATE_JOB_MONITOR);
	DWORD dwRet = m_pSm->OpenSM(m_dwJobID);
	if (dwRet != 0)
	{
		if ( dwRet != ERROR_FILE_NOT_FOUND )
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to open job monitor share memory of %d", __WFUNCTION__, m_dwJobID);
		return dwRet;
	}

	return dwRet;
}

void CUpdateJobMonitor::CancelUpdateJob()
{
	if (!m_pSm)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Share memory is NULL", __WFUNCTION__ );
		return;
	}

	UPDATE_JOB_MONITOR jm;
	ZeroMemory(&jm, sizeof(jm));

	DWORD dwRet = m_pSm->GetData(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to read data of job monitor", __WFUNCTION__);
		return;
	}

	jm.dwCancelFlag = 1;
	dwRet = m_pSm->UpdateSM(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to write data of job monitor", __WFUNCTION__);
		return;
	}
}

BOOL CUpdateJobMonitor::IsJobCanceled()
{
	if (!m_pSm)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Share memory is NULL", __WFUNCTION__);
		return FALSE;
	}

	UPDATE_JOB_MONITOR jm;
	ZeroMemory(&jm, sizeof(jm));

	DWORD dwRet = m_pSm->GetData(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to read data of job monitor", __WFUNCTION__);
		return FALSE;
	}

	return jm.dwCancelFlag == 0 ? FALSE : TRUE;
}

void CUpdateJobMonitor::UpdateProcessID(DWORD dwProcID)
{
	if (!m_pSm)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Share memory is NULL", __WFUNCTION__);
		return;
	}

	UPDATE_JOB_MONITOR jm;
	ZeroMemory(&jm, sizeof(jm));

	DWORD dwRet = m_pSm->GetData(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to read data of job monitor", __WFUNCTION__);
		return;
	}

	jm.dwProcessID = dwProcID;
	dwRet = m_pSm->UpdateSM(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Failed to write data of job monitor", __WFUNCTION__);
		return;
	}
}

void CUpdateJobMonitor::UpdateJobPhase(DWORD dwJobPhase)
{
	if (!m_pSm)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Share memory is NULL", __WFUNCTION__);
		return;
	}

	UPDATE_JOB_MONITOR jm;
	ZeroMemory(&jm, sizeof(jm));

	DWORD dwRet = m_pSm->GetData(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to read data of job monitor", __WFUNCTION__);
		return;
	}

	jm.dwJobPhase = dwJobPhase;
	dwRet = m_pSm->UpdateSM(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Failed to write data of job monitor", __WFUNCTION__);
		return;
	}
}

void CUpdateJobMonitor::UpdateTotalSize(ULONGLONG ullSize)
{
	if (!m_pSm)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Share memory is NULL", __WFUNCTION__);
		return;
	}

	UPDATE_JOB_MONITOR jm;
	ZeroMemory(&jm, sizeof(jm));

	DWORD dwRet = m_pSm->GetData(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to read data of job monitor", __WFUNCTION__);
		return;
	}

	jm.ullTotalSize = ullSize;
	dwRet = m_pSm->UpdateSM(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to write data of job monitor", __WFUNCTION__);
		return;
	}
}

void CUpdateJobMonitor::InitDownloadedSize(ULONGLONG ullSize)
{
	if (!m_pSm)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Share memory is NULL", __WFUNCTION__);
		return;
	}

	UPDATE_JOB_MONITOR jm;
	ZeroMemory(&jm, sizeof(jm));

	DWORD dwRet = m_pSm->GetData(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to read data of job monitor", __WFUNCTION__);
		return;
	}

	jm.ullDownloadedSize = ullSize;
	dwRet = m_pSm->UpdateSM(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to write data of job monitor", __WFUNCTION__);
		return;
	}
}

void CUpdateJobMonitor::UpdateDownloadedSize(ULONGLONG ullSize)
{
	if (!m_pSm)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Share memory is NULL", __WFUNCTION__);
		return;
	}

	UPDATE_JOB_MONITOR jm;
	ZeroMemory(&jm, sizeof(jm));

	DWORD dwRet = m_pSm->GetData(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to read data of job monitor", __WFUNCTION__);
		return;
	}

	jm.ullDownloadedSize += ullSize;
	dwRet = m_pSm->UpdateSM(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to write data of job monitor", __WFUNCTION__);
		return;
	}
}

void CUpdateJobMonitor::StartUpdateJob()
{
	if (!m_pSm)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Share memory is NULL", __WFUNCTION__);
		return;
	}

	UPDATE_JOB_MONITOR jm;
	ZeroMemory(&jm, sizeof(jm));

	DWORD dwRet = m_pSm->GetData(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to read data of job monitor", __WFUNCTION__);
		return;
	}

	SYSTEMTIME sysTime;
	ZeroMemory(&sysTime, sizeof(sysTime));
	::GetSystemTime(&sysTime);
	FILETIME ft;
	::SystemTimeToFileTime(&sysTime, &ft);
	LARGE_INTEGER li;
	li.HighPart = ft.dwHighDateTime;
	li.LowPart = ft.dwLowDateTime;

	jm.ullStartTime = li.QuadPart;
	jm.dwJobPhase = AJP_INITIALIZE;
	jm.dwJobStatus = AJS_RUNNING;
	dwRet = m_pSm->UpdateSM(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to write data of job monitor", __WFUNCTION__);
		return;
	}
}

void CUpdateJobMonitor::EndUpdateJob(DWORD dwStatus, LONG lLastError)
{
	if (!m_pSm)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Share memory is NULL", __WFUNCTION__);
		return;
	}

	UPDATE_JOB_MONITOR jm;
	ZeroMemory(&jm, sizeof(jm));

	DWORD dwRet = m_pSm->GetData(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to read data of job monitor", __WFUNCTION__);
		return;
	}

	SYSTEMTIME sysTime;
	ZeroMemory(&sysTime, sizeof(sysTime));
	::GetSystemTime(&sysTime);
	FILETIME ft;
	::SystemTimeToFileTime(&sysTime, &ft);
	LARGE_INTEGER li;
	li.HighPart = ft.dwHighDateTime;
	li.LowPart = ft.dwLowDateTime;

	jm.ullEndTime = li.QuadPart;
	jm.dwJobPhase = AJP_END;
	jm.dwJobStatus = dwStatus;
	jm.lLastError = lLastError;
	dwRet = m_pSm->UpdateSM(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to write data of job monitor", __WFUNCTION__);
		return;
	}
}

DWORD CUpdateJobMonitor::GetJobStatus()
{
	if (!m_pSm)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Share memory is NULL", __WFUNCTION__);
		return AJS_UNKNOWN;
	}

	UPDATE_JOB_MONITOR jm;
	ZeroMemory(&jm, sizeof(jm));

	DWORD dwRet = m_pSm->GetData(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to read data of job monitor", __WFUNCTION__);
		return AJS_UNKNOWN;
	}
	return jm.dwJobStatus;
}

DWORD CUpdateJobMonitor::GetJobPhase()
{
	if (!m_pSm)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Share memory is NULL", __WFUNCTION__);
		return AJS_UNKNOWN;
	}

	UPDATE_JOB_MONITOR jm;
	ZeroMemory(&jm, sizeof(jm));

	DWORD dwRet = m_pSm->GetData(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to read data of job monitor", __WFUNCTION__);
		return AJS_UNKNOWN;
	}
	return jm.dwJobPhase;
}

LONG CUpdateJobMonitor::GetLastError()
{
	if (!m_pSm)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Share memory is NULL", __WFUNCTION__);
		return AJS_UNKNOWN;
	}

	UPDATE_JOB_MONITOR jm;
	ZeroMemory(&jm, sizeof(jm));

	DWORD dwRet = m_pSm->GetData(&jm);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to read data of job monitor", __WFUNCTION__);
		return AJS_UNKNOWN;
	}
	return jm.lLastError;
}

DWORD CUpdateJobMonitor::GetDataOfJobMonitor(UPDATE_JOB_MONITOR& jmData)
{
	if (!m_pSm)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Share memory is NULL", __WFUNCTION__);
		return ERROR_INVALID_PARAMETER;
	}
	
	DWORD dwRet = m_pSm->GetData(&jmData);
	if (0 != dwRet)
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to read data of job monitor", __WFUNCTION__);
		return ERROR_INVALID_PARAMETER;
	}
	return 0;
}

DWORD CreateIUpdateJobMonitor(DWORD dwJobID, IUpdateJobMonitor** ppJobMonitor)
{
	CUpdateJobMonitor* pMonitor = new CUpdateJobMonitor(dwJobID);
	DWORD dwRet = pMonitor->InitJobMonitor();
	if (dwRet != 0){
		SAFE_RELEASE(pMonitor);
		(*ppJobMonitor) = NULL;
	}
	else{
		(*ppJobMonitor) = static_cast<IUpdateJobMonitor*> (pMonitor);
	}
	return dwRet;
}

DWORD OpenIUpdateJobMonitor(DWORD dwJobID, IUpdateJobMonitor** ppJobMonitor)
{
	CUpdateJobMonitor* pMonitor = new CUpdateJobMonitor(dwJobID);
	DWORD dwRet = pMonitor->OpenJobMonitor();
	if (dwRet != 0){
		SAFE_RELEASE(pMonitor);
		(*ppJobMonitor) = NULL;
	}
	else{
		(*ppJobMonitor) = static_cast<IUpdateJobMonitor*> (pMonitor);
	}
	return dwRet;
}

