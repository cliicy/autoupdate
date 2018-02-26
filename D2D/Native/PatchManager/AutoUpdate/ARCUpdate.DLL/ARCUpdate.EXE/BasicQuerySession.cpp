#include "stdafx.h"
#include "BasicQuerySession.h"
#include "DRCommonlib.h"
#include "PipeSessionSet.h"
/*
*	the implementation of PipeSession
*/
CBasicQuerySession::CBasicQuerySession(HANDLE hPipe, CPipeSessionSet* pSet)
	: CPipeSessionBase( hPipe, pSet)
{
}

CBasicQuerySession::~CBasicQuerySession()
{
}

DWORD CBasicQuerySession::Main()
{
	m_log.LogW(LL_INF, 0, L"%s: A new basic query session is started.", __WFUNCTION__);
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
		case REQ_QUERY_NODE_INFO:
			lRet = onQueryNodeInfo(pParams);
			break;
		case REQ_QUERY_INSTALLED_PATCHES:
			lRet = onQueryInstalledPatches(pParams);
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

LONG CBasicQuerySession::onQueryNodeInfo(void* pParams)
{
	BOOL bAgentInstalled = FALSE;
	BOOL bConsoleInstalled = FALSE;
	UDP_VERSION_INFO verAgent; ZeroMemory(&verAgent, sizeof(verAgent));
	UDP_VERSION_INFO verConsole; ZeroMemory(&verConsole, sizeof(verConsole));
	if (0 == PRODUTILS::GetProductVersion(ARCUPDATE_PRODUCT_AGENT, verAgent))
		bAgentInstalled = TRUE;
	if (0 == PRODUTILS::GetProductVersion(ARCUPDATE_PRODUCT_FULL, verConsole))
		bConsoleInstalled = TRUE;

	int nCount = 0;
	if (bAgentInstalled)
		nCount++;
	if (bConsoleInstalled)
		nCount++;
	
	DWORD dwBuffSize = sizeof(ack_param_node_info);
	if (nCount > 1)
		dwBuffSize += (sizeof(udp_product)*(nCount - 1));

	pack_param_node_info pAckParams = (pack_param_node_info)malloc(dwBuffSize);
	if (!pAckParams){
		DWORD dwError = GetLastError();
		m_log.LogW(LL_ERR, dwError, L"%s: Failed to allocate buffer with %d bytes", __WFUNCTION__, dwBuffSize);
		m_pipe.sendAck(REQ_QUERY_NODE_INFO, dwError);
		return 0;
	}
	ZeroMemory(pAckParams, dwBuffSize);
	DWORD dwSize = _countof(pAckParams->szHostname) - 1;
	::GetComputerName(pAckParams->szHostname, &dwSize);
	
	CWinVer winVer;
	pAckParams->dwOsMajorVersion = winVer.GetMajVer();
	pAckParams->dwOsMinorVersion = winVer.GetMinVer();
	
	CSysInfo sysInfo;
	pAckParams->dwCpu = sysInfo.IsX64() ? PROCESSOR_ARCHITECTURE_AMD64 : PROCESSOR_ARCHITECTURE_INTEL;
	
	pAckParams->dwNumberOfProductsInstalled = nCount;
	int nIndex = 0;
	if (bAgentInstalled){
		pAckParams->productsInstalled[nIndex].dwCode = ARCUPDATE_PRODUCT_AGENT;
		pAckParams->productsInstalled[nIndex].dwMajorVersion = verAgent.dwMajor;
		pAckParams->productsInstalled[nIndex].dwMinorVersion = verAgent.dwMinor;
		pAckParams->productsInstalled[nIndex].dwMajorBuild = verAgent.dwBuild;
		pAckParams->productsInstalled[nIndex].dwSpVersion = verAgent.dwServicePack;
		pAckParams->productsInstalled[nIndex].dwUpdateVersion = verAgent.dwUpdate;
		pAckParams->productsInstalled[nIndex].dwUpdateBuild = verAgent.dwUpBuild;
		nIndex++;
	}
	if (bConsoleInstalled){
		pAckParams->productsInstalled[nIndex].dwCode = ARCUPDATE_PRODUCT_AGENT;
		pAckParams->productsInstalled[nIndex].dwMajorVersion = verConsole.dwMajor;
		pAckParams->productsInstalled[nIndex].dwMinorVersion = verConsole.dwMinor;
		pAckParams->productsInstalled[nIndex].dwMajorBuild = verConsole.dwBuild;
		pAckParams->productsInstalled[nIndex].dwSpVersion = verConsole.dwServicePack;
		pAckParams->productsInstalled[nIndex].dwUpdateVersion = verConsole.dwUpdate;
		pAckParams->productsInstalled[nIndex].dwUpdateBuild = verConsole.dwUpBuild;
		nIndex++;
	}

	LONG lRet = m_pipe.sendAck(REQ_QUERY_NODE_INFO, 0, dwBuffSize, pAckParams);
	SAFE_FREE(pAckParams);
	return lRet;
}

LONG CBasicQuerySession::onQueryInstalledPatches(void* pParams)
{
	WCHAR* pszAckBuffer = NULL;
	DWORD  dwSizeInCharacters = 0;
	LONG   lError = 0;

	do
	{
		CRegistry reg;
		lError = reg.Open(CST_BRAND_REG_ROOT, HKEY_LOCAL_MACHINE);
		if (lError != 0)
			break;

		std::vector<wstring> vecPatches;
		reg.QueryMultiStringValue(L"InstalledPatches", vecPatches);
		for (size_t i = 0; i < vecPatches.size(); i++){
			dwSizeInCharacters += (DWORD)vecPatches[i].length() + 1;
		}
		if (dwSizeInCharacters != 0)
			dwSizeInCharacters += 1;

		pszAckBuffer = new WCHAR[dwSizeInCharacters];
		if (!pszAckBuffer)
		{
			lError = GetLastError();
			m_log.LogW(LL_ERR, lError, L"%s: Failed to allocate buffer with %d bytes", __WFUNCTION__, dwSizeInCharacters * 2);
			break;
		}
		ZeroMemory(pszAckBuffer, dwSizeInCharacters*sizeof(WCHAR));

		if (dwSizeInCharacters == 0)
			break;

		WCHAR* ptr = pszAckBuffer;
		DWORD dwSizeRemain = dwSizeInCharacters;
		for (size_t i = 0; i < vecPatches.size(); i++)
		{
			wcscpy_s(ptr, dwSizeRemain, vecPatches[i].c_str());
			ptr += vecPatches[i].length() + 1;
			dwSizeRemain = dwSizeRemain - (DWORD)vecPatches[i].length() - 1;
		}
	} while (0);

	LONG lRet = m_pipe.sendAck(REQ_QUERY_INSTALLED_PATCHES, lError, dwSizeInCharacters*sizeof(WCHAR), pszAckBuffer);
	if (pszAckBuffer)
	{
		delete[] pszAckBuffer;
		pszAckBuffer = NULL;
	}
	return lRet;
}

LONG CBasicQuerySession::onInvalidCommand(DWORD cmd, void* pParamsIn)
{
	return m_pipe.sendAck(cmd, error_pipe_unsupported_cmd);
}
