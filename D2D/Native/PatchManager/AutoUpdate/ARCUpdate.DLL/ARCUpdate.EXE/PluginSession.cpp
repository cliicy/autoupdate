#include "stdafx.h"
#include "PluginSession.h"
#include "XXmlNode.h"
#include "UpdateJobManager.h"
#include "PluginManager.h"
#include "DRCommonlib.h"
#include "PipeSessionSet.h"

/*
*	the implementation of PipeSession
*/
CPluginSession::CPluginSession(HANDLE hPipe, CPipeSessionSet* pSet)
	: CPipeSessionBase( hPipe, pSet )
{
}

CPluginSession::~CPluginSession()
{
}

DWORD CPluginSession::Main()
{
	m_log.LogW(LL_INF, 0, L"%s: A new plugin manager session is started.", __WFUNCTION__);
	LONG lRet = 0;
	lRet = m_pipe.sendAck(REQ_PLUGIN_MANAGER_SESSION);
	if (lRet != 0)
		goto _EXIT;

	void* pParams = NULL;
	while (1)
	{
		SAFE_FREE(pParams);
		packet_req req;
		lRet = m_pipe.readReq(&req, &pParams);
		if (lRet != 0)
			break;
		switch (req.cmd)
		{
		case REQ_PLUGIN_INSTALL:
			lRet = onInstallPlugin(pParams);
			break;
		case REQ_PLUGIN_REMOVE:
			lRet = onRemovePlugin(pParams);
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

LONG CPluginSession::onInvalidCommand(DWORD cmd, void* pParamsIn)
{
	return m_pipe.sendAck(cmd, error_pipe_unsupported_cmd);
}

LONG CPluginSession::onInstallPlugin(void* pParamsIn)
{
	if (!pParamsIn)
	{
		m_log.LogW(LL_ERR, 0, L"%s: The plugin parameters is NULL", __WFUNCTION__);
		m_pipe.sendAck(REQ_PLUGIN_INSTALL, error_pipe_invalid_parameter);
		return 0;
	}

	preq_param_install_plugin pPluginParams = (preq_param_install_plugin)(pParamsIn);
	m_log.LogW(LL_DBG, 0, L"%s: Asked to install plugin of %d", __WFUNCTION__, pPluginParams->dwCommand);

	HANDLE  hFile = INVALID_HANDLE_VALUE;
	DWORD	dwError = 0;	
	wstring strFilePath = PATHUTILS::path_join(PATHUTILS::home_dir(), pPluginParams->szModuleName);
	do
	{
		//
		// Open the local file for writing
		//		
		hFile = ::CreateFile(strFilePath.c_str(), GENERIC_WRITE, FILE_SHARE_READ, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
		if (hFile == INVALID_HANDLE_VALUE){
			dwError = GetLastError();
			m_log.LogW(LL_ERR, dwError, L"%s: Failed to create file %s", __WFUNCTION__, strFilePath.c_str());
			m_pipe.sendAck(REQ_PLUGIN_INSTALL, dwError);
			break;
		}

		dwError = m_pipe.sendAck(REQ_PLUGIN_INSTALL, 0);
		if (dwError != 0){
			m_log.LogW(LL_ERR, dwError, L"%s: Failed to send ack(REQ_PLUGIN_INSTALL) to the client", __WFUNCTION__);
			break;
		}

		void*  pDataBuf = NULL;
		while (1)
		{
			//
			// read file data from pipe
			//
			SAFE_FREE(pDataBuf);
			packet_ack ack;
			dwError = m_pipe.readAck(&ack, &pDataBuf);
			if (dwError != 0 || ack.errCode != 0){
				m_log.LogW(LL_ERR, dwError, L"%s: ack error. dwError=%d, ack.errCode=%d", __WFUNCTION__, dwError, ack.errCode );
				dwError = (dwError == 0) ? ack.errCode : dwError;
				break;
			}

			//
			// write data to local file
			//
			DWORD dwWroteError = 0;
			if (ack.dataSize>0)
			{
				DWORD dwWrote = 0;
				if (!WriteFile(hFile, pDataBuf, ack.dataSize, &dwWrote, NULL)){
					dwWroteError = GetLastError();
					m_log.LogW(LL_ERR, dwError, L"%s: Failed to write %d bytes data to file %s", __WFUNCTION__, ack.dataSize, strFilePath.c_str());
				}
			}

			dwError = m_pipe.sendAck(REQ_PLUGIN_INSTALL, dwWroteError);
			if (dwError != 0 || dwWroteError!=0){
				dwError = (dwError == 0) ? dwWroteError : dwError;
				m_log.LogW(LL_ERR, dwError, L"%s: Failed to send ACK(REQ_PLUGIN_INSTALL) to the client", __WFUNCTION__);
				break;
			}

			if (ack.dataSize < COM_BLOCK_DATA_SIZE)
				break;
		}
		if (hFile != INVALID_HANDLE_VALUE)
		{
			CloseHandle(hFile);
			hFile = INVALID_HANDLE_VALUE;
		}

		if (dwError != 0)
			break;

		g_pluginManager.InstallPlugin(pPluginParams->dwCommand, pPluginParams->szModuleName);
		
	} while (0);

	if (hFile != INVALID_HANDLE_VALUE){
		CloseHandle(hFile);
		hFile = INVALID_HANDLE_VALUE;
	}

	if (dwError != 0)
		::DeleteFile(strFilePath.c_str());
	return dwError;
}

LONG CPluginSession::onRemovePlugin(void* pParamsIn)
{
	if (!pParamsIn)
	{
		m_log.LogW(LL_ERR, 0, L"%s: The plugin parameters is NULL", __WFUNCTION__);
		m_pipe.sendAck( REQ_PLUGIN_REMOVE, error_pipe_invalid_parameter );
		return 0;
	}

	preq_param_remove_plugin pPlugParams = (preq_param_remove_plugin)(pParamsIn);
	m_log.LogW(LL_DBG, 0, L"%s: Asked to remove plugin of %d", __WFUNCTION__, pPlugParams->dwCommand );

	LONG lRet = g_pluginManager.RemovePlugin(pPlugParams->dwCommand);
	m_pipe.sendAck(REQ_PLUGIN_REMOVE, lRet);
	return 0;
}
