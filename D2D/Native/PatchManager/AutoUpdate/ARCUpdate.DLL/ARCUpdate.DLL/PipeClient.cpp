#include "stdafx.h"
#include "..\include\PipeDefines.h"
#include "..\include\UpdateDefines.h"
#include "PipeClient.h"
#include "XXmlNode.h"

CPipeClient::CPipeClient()
	: m_pipe( INVALID_HANDLE_VALUE )
{

}

CPipeClient::~CPipeClient()
{
	m_pipe.Close();
}

LONG CPipeClient::Connect(const wstring& strServer /*= L""*/, const wstring& strAdmin /*= L""*/, const wstring& strPassword /*= L""*/)
{
	LONG lRet = m_pipe.Connect(strServer, strAdmin, strPassword);
	if ( lRet != 0 )
	{
		m_log.LogW(LL_ERR, lRet, L"%s: Failed to connect to update service", __WFUNCTION__);
		return lRet;
	}

	lRet = m_pipe.sendReq(REQ_UPDATE_SESSION);
	if (lRet != 0)
	{
		m_log.LogW(LL_ERR, lRet, L"%s: Failed to send request to update service", __WFUNCTION__);
		return lRet;
	}

	packet_ack ack;
	lRet = m_pipe.readAck(&ack);
	if (lRet!=0 || ack.errCode!=0 )
	{
		m_log.LogW(LL_ERR, lRet, L"%s: Failed to get reploy from update service. lRet=%d, ack.errCode=%d", __WFUNCTION__, lRet, ack.errCode );
		lRet = (lRet == 0) ? ack.errCode : lRet;
	}

	return lRet;
}


LONG CPipeClient::triggerUpdate(DWORD dwProdOn, DWORD dwProdFor, PARCUPDATE_SERVER_INFO pSvrInfo,BOOL bHotfix)
{
	LONG lRet = 0;

	req_param_start_job reqParams;
	ZeroMemory(&reqParams, sizeof(reqParams));
	reqParams.dwProdOn = dwProdOn;
	reqParams.dwProdFor = dwProdFor;
	reqParams.bHotfix = bHotfix;
	if (pSvrInfo)
	{
		reqParams.bSvrSpecified = TRUE;
		memcpy_s(&(reqParams.svrInfo), sizeof(ARCUPDATE_SERVER_INFO), pSvrInfo, sizeof(ARCUPDATE_SERVER_INFO));
	}
	else
	{
		reqParams.bSvrSpecified = FALSE;
	}

	lRet = m_pipe.sendReq(REQ_UPDATE_TRIGGER_JOB, sizeof(req_param_start_job), &reqParams);
	if (lRet != 0)
	{
		m_log.LogW(LL_ERR, lRet, L"%s: Failed to trigger update job. ProdOn[%d], ProdFor[%d]", __WFUNCTION__, dwProdOn, dwProdFor );
		return lRet;
	}

	packet_ack ack;
	lRet = m_pipe.readAck(&ack);
	if (lRet != 0)
	{
		m_log.LogW(LL_ERR, lRet, L"%s: Failed to get response from update service. lRet = %d, ack.errCode = %d", __WFUNCTION__, lRet, ack.errCode);
		lRet = lRet == 0 ? ack.errCode : lRet;
	}

	return lRet;
}
	

LONG CPipeClient::cancelUpdateJob(DWORD dwProd)
{
	LONG lRet = 0;

	req_param_cancel_job reqParams;
	reqParams.dwProduct = dwProd;
	lRet = m_pipe.sendReq(REQ_UPDATE_CANCEL_JOB, sizeof(reqParams), &reqParams);
	if (lRet != 0)
	{
		m_log.LogW(LL_ERR, lRet, L"%s: Failed to cancel update job of %d", __WFUNCTION__, dwProd);
		return lRet;
	}

	packet_ack ack;
	lRet = m_pipe.readAck(&ack);
	if (lRet != 0)
	{
		m_log.LogW(LL_ERR, lRet, L"%s: Failed to get response from update service. lRet = %d, ack.errCode = %d", __WFUNCTION__, lRet, ack.errCode);
		lRet = lRet == 0 ? ack.errCode : lRet;
	}

	return lRet;

}

LONG CPipeClient::queryUpdateStatus(DWORD dwProd, UPDATE_JOB_MONITOR* pJM)
{
	LONG lRet = 0;
	if (pJM == NULL)
		return ERROR_INVALID_PARAMETER;
	
	req_param_query_job_status reqParams;
	reqParams.dwProduct = dwProd;
	lRet = m_pipe.sendReq(REQ_UPDATE_QUERY_STAQTUS, sizeof(reqParams), &reqParams);
	if (lRet != 0)
	{
		m_log.LogW(LL_ERR, lRet, L"%s: Failed to cancel update job of %d", __WFUNCTION__, dwProd);
		return lRet;
	}

	packet_ack ack;
	void* pAckParams = NULL;
	lRet = m_pipe.readAck(&ack, &pAckParams);
	if (lRet != 0 || ack.errCode!=0 )
	{
		m_log.LogW(LL_ERR, lRet, L"%s: Failed to get response from update service. lRet = %d, ack.errCode = %d", __WFUNCTION__, lRet, ack.errCode);
		lRet = lRet == 0 ? ack.errCode : lRet;
		SAFE_FREE(pAckParams);
		return lRet;
	}

	if (ack.dataSize != sizeof(UPDATE_JOB_MONITOR)){
		SAFE_FREE(pAckParams);
		lRet = ERROR_INTERNAL_ERROR;
		m_log.LogW(LL_ERR, lRet, L"%s: The received job monitor is not expected.", __WFUNCTION__);
		return lRet;
	}
	else{
		memcpy_s(pJM, sizeof(UPDATE_JOB_MONITOR), pAckParams, ack.dataSize);
		SAFE_FREE(pAckParams);
		return 0;
	}
}

LONG CPipeClient::isUpdateBusy(DWORD dwProd, PBOOL pbBusy)
{
	if (!pbBusy)
		return ERROR_INVALID_PARAMETER;

	UPDATE_JOB_MONITOR jm; ZeroMemory(&jm, sizeof(jm));
	LONG lRet = queryUpdateStatus(dwProd, &jm);
	if ( lRet==0 )
	{
		(*pbBusy) = jm.dwJobStatus == AJS_RUNNING ? TRUE : FALSE;
	}
	return lRet;
}