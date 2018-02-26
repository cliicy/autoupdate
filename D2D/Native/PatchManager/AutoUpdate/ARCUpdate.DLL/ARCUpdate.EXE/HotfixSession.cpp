#include "stdafx.h"
#include "HotfixSession.h"
#include "XXmlNode.h"
#include "UpdateJobManager.h"
#include "PluginManager.h"
#include "DRCommonlib.h"
#include "PipeSessionSet.h"

CHotfixSession::CHotfixSession(HANDLE hPipe, CPipeSessionSet* pSet)
	: CPipeSessionBase( hPipe, pSet )
{
}

CHotfixSession::~CHotfixSession()
{
}

DWORD CHotfixSession::Main()
{
	m_log.LogW(LL_INF, 0, L"%s: A new update session is started.", __WFUNCTION__);

	LONG lRet = 0;
	lRet = m_pipe.sendAck(REQ_UPDATE_SESSION);
	if (lRet != 0)
		goto _EXIT;

	void* pParams = NULL;
	while (1)
	{
		SAFE_FREE(pParams);
		packet_req req;
		lRet = m_pipe.readReq(&req, &pParams);
		if (lRet != 0){
			break;
		}

		switch (req.cmd)
		{
		case REQ_UPDATE_TRIGGER_JOB:
			lRet = onTriggerUpdate(pParams);
			break;
		case REQ_UPDATE_QUERY_STAQTUS:
			lRet = onQueryJobStatus(pParams);
			break;
		case REQ_UPDATE_CANCEL_JOB:
			lRet = onCancelUpdateJob(pParams);
			break;
		default:
			lRet = onInvalidCommand(req.cmd, pParams);
			break;
		}
	}
	SAFE_FREE(pParams);

_EXIT:
	m_log.LogW(LL_DET, 0, L"%s: Exit this session with error %d", __WFUNCTION__, lRet);
	m_pipe.Disconnect();
	if (m_pSessionSet)
		m_pSessionSet->RemoveSession(this);
	return (DWORD)lRet;
}

LONG CHotfixSession::onInvalidCommand(DWORD cmd, void* pParamsIn)
{
	return m_pipe.sendAck(cmd, error_pipe_unsupported_cmd);
}

LONG CHotfixSession::onTriggerUpdate(void* pParamsIn)
{
	if (!pParamsIn)
	{
		m_log.LogW(LL_ERR, 0, L"%s: oooo The update parameters is NULL", __WFUNCTION__);
		m_pipe.sendAck(REQ_UPDATE_TRIGGER_JOB, error_pipe_invalid_parameter);
		return 0;
	}

	req_param_start_job* p = (req_param_start_job*)pParamsIn;
	m_log.LogW(LL_INF, 0, L"%s:oooo Asked to trigger an update job for %d on %d p->bSvrSpecified=%d", __WFUNCTION__, p->dwProdFor, p->dwProdOn, p->bSvrSpecified);
	LONG lError = 0;
	if ( p->bSvrSpecified )
		lError = g_upJobManager.StartUpdateBIJob(p->dwProdOn, p->dwProdFor, &(p->svrInfo)); //added by cliicy.luo to single binaries updates
		//lError = g_upJobManager.StartUpdateJob(p->dwProdOn, p->dwProdFor, &(p->svrInfo));
	else
		lError = g_upJobManager.StartUpdateBIJob(p->dwProdOn, p->dwProdFor, NULL); //added by cliicy.luo to single binaries updates
		//lError = g_upJobManager.StartUpdateJob(p->dwProdOn, p->dwProdFor, NULL);
	m_pipe.sendAck(REQ_UPDATE_TRIGGER_JOB, lError);
	m_log.LogW(LL_INF, 0, L"%s: hhhhhh will exits", __WFUNCTION__);

	return 0;
}


LONG CHotfixSession::onQueryJobStatus(void* pParamsIn)
{
	if (!pParamsIn)
	{
		m_log.LogW(LL_ERR, 0, L"%s: The update parameters is NULL", __WFUNCTION__);
		m_pipe.sendAck(REQ_UPDATE_QUERY_STAQTUS, error_pipe_invalid_parameter);
		return 0;
	}
	preq_param_query_job_status pQueryParams = (preq_param_query_job_status)pParamsIn;
	m_log.LogW(LL_DBG, 0, L"%s: Asked to query job status for %d", __WFUNCTION__, pQueryParams->dwProduct);
	DWORD dwProdCode = pQueryParams->dwProduct;

	UPDATE_JOB_MONITOR lastJm;
	g_upJobManager.QueryUpdateJobStatus(dwProdCode, lastJm);
	m_pipe.sendAck(REQ_UPDATE_QUERY_STAQTUS, 0, sizeof(lastJm), &lastJm);
	return 0;
}

LONG CHotfixSession::onCancelUpdateJob(void* pParamsIn)
{
	if (!pParamsIn)
	{
		m_log.LogW(LL_ERR, 0, L"%s: The update parameters is NULL", __WFUNCTION__);
		m_pipe.sendAck(REQ_UPDATE_CANCEL_JOB, error_pipe_invalid_parameter);
		return 0;
	}

	preq_param_cancel_job pCancelParams = (preq_param_cancel_job)(pParamsIn);
	m_log.LogW(LL_DBG, 0, L"%s: Asked to cancel update job for %d", __WFUNCTION__, pCancelParams->dwProduct);
	LONG lError = g_upJobManager.CancelUpdateJob(pCancelParams->dwProduct);
	m_pipe.sendAck(REQ_UPDATE_CANCEL_JOB, lError);
	return 0;
}
