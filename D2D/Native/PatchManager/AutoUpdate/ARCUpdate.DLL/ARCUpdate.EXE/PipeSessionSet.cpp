#include "stdafx.h"
#include "PipeSessionSet.h"
#include "BasicQuerySession.h"
#include "FileMgrSession.h"
#include "PluginSession.h"
#include "UpdateSession.h"

CPipeSessionSet::CPipeSessionSet()
{
	::InitializeCriticalSection(&m_cs);
	m_vecSessions.clear();
}

CPipeSessionSet::~CPipeSessionSet()
{
	::DeleteCriticalSection(&m_cs);
}

CPipeSessionBase* CPipeSessionSet::CreateSession(HANDLE hPipe)
{
	if (hPipe == INVALID_HANDLE_VALUE){
		m_log.LogW(LL_ERR, 0, L"%s: invalid parameters", __WFUNCTION__);
		return NULL;
	}
		
	CPipeSessionBase* pSession = NULL;
	CPipeBase pipe(hPipe);
	packet_req req;
	void* pReqParams = NULL;
	LONG lRet = pipe.readReq(&req, &pReqParams);
	if (lRet == 0)
	{
		switch (req.cmd)
		{
			case REQ_UPDATE_SESSION:
			{
				HANDLE h = pipe.Detach();
				pSession = static_cast<CPipeSessionBase*>(new CUpdateSession(h, this));
				break;
			}
			case REQ_PLUGIN_MANAGER_SESSION:
			{
				HANDLE h = pipe.Detach();
				pSession = static_cast<CPipeSessionBase*>(new CPluginSession(h, this));
				break;
			}
			case REQ_QUERY_SESSION:
			{
				HANDLE h = pipe.Detach();
				pSession = static_cast<CPipeSessionBase*>(new CBasicQuerySession(h, this));
				break;
			}
			case REG_FILE_MGR_SESSION:
			{
				HANDLE h = pipe.Detach();
				pSession = static_cast<CPipeSessionBase*>(new CFileMgrSession(h, this));
				break;
			}
			default:
			{
				m_log.LogW(LL_ERR, 0, L"%s: invalid request %d", __WFUNCTION__, req.cmd );
				break;
			}
		}
	}
	else
	{
		m_log.LogW(LL_ERR, 0, L"%s: Failed to read request from the client", __WFUNCTION__ );
	}

	if (pSession)
	{
		::EnterCriticalSection(&m_cs);
		m_vecSessions.push_back(pSession);
		::LeaveCriticalSection(&m_cs);
	}
	return pSession;
}

void CPipeSessionSet::RemoveSession(CPipeSessionBase* pSession)
{
	::EnterCriticalSection(&m_cs);
	std::vector<CPipeSessionBase*>::iterator it;
	for (it = m_vecSessions.begin(); it != m_vecSessions.end(); it++)
	{
		if ((LPVOID)pSession == (LPVOID)(*it))
		{
			m_vecSessions.erase(it);
			break;
		}
	}
	::LeaveCriticalSection(&m_cs);
}

void CPipeSessionSet::StopAllSessions()
{
	::EnterCriticalSection(&m_cs);
	std::vector<CPipeSessionBase*>::iterator it;
	for (it = m_vecSessions.begin(); it != m_vecSessions.end(); it++)
		(*it)->Stop();
	::LeaveCriticalSection(&m_cs);

	while (!m_vecSessions.empty())
		Sleep(1000);
}
