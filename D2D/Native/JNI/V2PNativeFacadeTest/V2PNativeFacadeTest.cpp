// V2PNativeFacadeTest.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "..\V2PNativeFacade\V2PNativeFacade.h"
#include "..\V2PNativeFacade\RPCProtocol.h"
#include "SwitchDefines.h"
#include "D2DSwitch.h"
#include <iostream>

//#pragma comment(lib, "..\\V2PNativeFacade\\amd64.rel\\V2PNativeFacade.lib")

#if defined(_WIN64) //<sonmi01>2013-3-25 #oolong Java update for D2D
//#define JRE_PATH L"TOMCAT\\JRE\\jre7\\bin\\server"
#define JRE_PATH L"TOMCAT\\JRE\\bin\\server"
#else
//#define JRE_PATH L"TOMCAT\\JRE\\jre7\\bin\\client"
#define JRE_PATH L"TOMCAT\\JRE\\bin\\client"
#endif
static DWORD AppendToEnvPath(TCHAR *appendPath)
{
	DWORD  dwPathBufSize = 0;
	size_t  appPathLen = 0;
	TCHAR  *pPathBuf = NULL;
	DWORD	pathBufSize = 0;
	DWORD  dwErrCode = 0;

	appPathLen = _tcslen(appendPath);

	dwPathBufSize = GetEnvironmentVariable(_T("Path"), NULL, 0);

	if (dwPathBufSize)
	{

		pathBufSize = (DWORD)(dwPathBufSize + appPathLen + 4);
		pPathBuf = (TCHAR *)malloc(pathBufSize * sizeof(TCHAR));
		if (!pPathBuf)
		{
			return 0xffffffff;
		}
		memset(pPathBuf, 0, pathBufSize * sizeof(TCHAR));

		_tcscat_s(pPathBuf, pathBufSize, appendPath);
		pPathBuf[appPathLen] = _T(';');

		GetEnvironmentVariable(_T("PATH"),
			pPathBuf + appPathLen + 1,
			dwPathBufSize + 1);

		if (!SetEnvironmentVariable(_T("Path"), pPathBuf))
		{
			dwErrCode = GetLastError();
		}

		if (pPathBuf)
			free(pPathBuf);
	}
	else
	{
		dwErrCode = GetLastError();
		return dwErrCode;
	}

	return dwErrCode;
}

//<sonmi01>2012-9-6 ###???
static INT DebugGetJreDirectory(OUT CString & strJreDirectory)
{
	WCHAR JreDir[512] = { 0 };
	ULONG ulBytes = _countof(JreDir);
	LONG lRet = GetSwitchStringFromReg(SWT_AFBACKEND_KEYNAME_JREDIRECTORY, JreDir, &ulBytes);
	if (ERROR_SUCCESS == lRet)
	{
		strJreDirectory = JreDir;
	}
	return lRet;
}

static INT D2D_AddJrePath()
{
	TCHAR modulePath[512] = { 0 };
	int length = GetModuleFileName(NULL, modulePath, 512);
	if (length == 0)
	{
		return -1;
	}
	TCHAR* lastSlash = wcsrchr(modulePath, L'\\');
	if (lastSlash != NULL)
	{
		*lastSlash = 0;
		lastSlash = wcsrchr(modulePath, L'\\');
	}

	if (lastSlash != NULL)
	{
		CString strJreDirectory;
		DebugGetJreDirectory(strJreDirectory);

		if (strJreDirectory.GetLength() > 3)
		{
			return AppendToEnvPath(strJreDirectory.GetBuffer());
		}
		else
		{
			wcscpy_s(lastSlash + 1, 510 - (wcslen(modulePath) - wcslen(lastSlash)), JRE_PATH);
			return AppendToEnvPath(modulePath);
		}
	}
	return -1;
}


//switch (inputCmd)
//{
//case RPC_CMD(connectToESX):
//	nRet = connectToESX_Proc(pInst, inputCmd, inputData, outputValue, outputData);
//	break;
//case RPC_CMD(disconnectESX):
//	nRet = disconnectESX_Proc(pInst, inputCmd, inputData, outputValue, outputData);
//	break;
//case RPC_CMD(getVMServerType):
//	nRet = getVMServerType_Proc(pInst, inputCmd, inputData, outputValue, outputData);
//	break;
//case RPC_CMD(checkVMServerLicense):
//	nRet = checkVMServerLicense_Proc(pInst, inputCmd, inputData, outputValue, outputData);
//	break;
//case RPC_CMD(getVMList):
//	nRet = getVMList_Proc(pInst, inputCmd, inputData, outputValue, outputData);
//	break;
//case RPC_CMD(getEsxNodeList):
//	nRet = getEsxNodeList_Proc(pInst, inputCmd, inputData, outputValue, outputData);
//	break;
//default:
//	break;
//}



struct v2pApiTable
{
	//////////////////////////////////////////////////////////////////////////
	V2PNF_HANDLE (*V2PNativeFacadeInit_RPC)(const char* jarList/* = NULL*/, BOOL bOldVer/* = FALSE*/);
	int (*connectToESX)(WCHAR* esxServer, WCHAR* esxUser, WCHAR* esxPwd, WCHAR* esxPro, bool bIgnoreCert, long lPort, V2PNF_HANDLE pHandle);
	int (*getVMServerType)(V2PNF_HANDLE pHandle);
	ESXNode* (*getEsxNodeList)(int *count, V2PNF_HANDLE pHandle);
	VM_BasicInfo* (*getVMList)(ESXNode esxNode, int *count, V2PNF_HANDLE pHandle);
	void (*V2PNativeFacadeExit)(V2PNF_HANDLE pHandle);
    BOOL(*checkVMServerInMaintainenceMode)(WCHAR* esxName, WCHAR* dcName, V2PNF_HANDLE pHandle);
    WCHAR*(*getESXVersion)(WCHAR* esxName, WCHAR* dcName, V2PNF_HANDLE pHandle);
    int(*getESXNumberOfProcessors)(WCHAR* esxName, OUT UINT& numberOfLogicalProcessors, OUT UINT& numberOfProcessors, V2PNF_HANDLE pHandle);

    V2PNF_HANDLE(*V2PNativeFacadeInit_Ex)(void* pJVM, const char* jarList, BOOL bOldVer);
	//////////////////////////////////////////////////////////////////////////
	void Init()
	{
		D2D_AddJrePath();

		static CONST TCHAR dll_name[] = L"V2PNativeFacade.dll";
		m_hdll = LoadLibrary(dll_name);

#define v2p_api_table_getproc(proc) proc = (decltype(proc))GetProcAddress(m_hdll, #proc)


		v2p_api_table_getproc(V2PNativeFacadeInit_RPC);
		v2p_api_table_getproc(connectToESX);
		v2p_api_table_getproc(getVMServerType);
		v2p_api_table_getproc(getEsxNodeList);
		v2p_api_table_getproc(getVMList);
		v2p_api_table_getproc(V2PNativeFacadeExit);
        v2p_api_table_getproc(checkVMServerInMaintainenceMode);
        v2p_api_table_getproc(getESXVersion);
        v2p_api_table_getproc(getESXNumberOfProcessors);

        v2p_api_table_getproc(V2PNativeFacadeInit_Ex);
	}

	~v2pApiTable()
	{
		if (nullptr != m_hdll)
		{
			FreeLibrary(m_hdll);
			m_hdll = nullptr;
		}
	}
	//////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////
private:
	HMODULE m_hdll;
};


int _tmain(int argc, _TCHAR* argv[])
{
	//D2D_AddJrePath();

	v2pApiTable v2pTable;
	ZeroMemory(&v2pTable, sizeof(v2pTable));
	v2pTable.Init();

	void* pInst = v2pTable.V2PNativeFacadeInit_RPC(NULL, FALSE);
    //void* pInst = v2pTable.V2PNativeFacadeInit_Ex(NULL, NULL, FALSE);

	v2pTable.connectToESX(L"shuli02-vc60", L"administrator", L"c@123456", L"https", true, 443, pInst);

	int type = v2pTable.getVMServerType(pInst);

	tagAllEsx allesx;

	int count = 0;
	ESXNode * pNodes = v2pTable.getEsxNodeList(&count, pInst);
	for (int ii = 0; ii < count; ++ii)
	{
		tagEsxVMs esxVM;

		//tagESXNode_internal esxInternal;
		esxVM.esx.__tagToInternal(pNodes[ii]);

		wcout << endl;
		wcout << L"ESX["<< ii <<"] begin: [" << pNodes[ii].esxName << L"]." << endl;
		wcout << L"=========================================================================" << endl;

        BOOL bMatainMode = v2pTable.checkVMServerInMaintainenceMode(pNodes[ii].esxName, pNodes[ii].dcName, pInst);
        //wcout << L"ESX[" << ii << "] : [" << pNodes[ii].esxName << L"]. IsInMatain:" << (ULONG)bMatainMode << endl;
        WCHAR* szRet = v2pTable.getESXVersion(pNodes[ii].esxName, pNodes[ii].dcName, pInst);
        UINT nOut1 = 2;
        UINT nOut2 = 3;
        int nRet = v2pTable.getESXNumberOfProcessors(pNodes[ii].esxName, nOut1, nOut2, pInst);

		int vmCount = 0;
		VM_BasicInfo* pVMs = v2pTable.getVMList(pNodes[ii], &vmCount, pInst);
		for (int jj = 0; jj < vmCount; jj++)
		{
			
			tagVM_BasicInfo_internal vmInternal;
			vmInternal.__tagToInternal(pVMs[jj]);

			esxVM.vms.push_back(vmInternal);

			wcout << L"VM[" << jj << "]:   " << vmInternal.vmName << endl;

			//wcout << L"VM[" << jj << "], UUID: [" << vmInternal.vmInstUUID << L"], Name: [" << vmInternal.vmName << L"]." << endl;
		}

		//wcout << L"ESX[" << ii << "] end: [" << pNodes[ii].esxName << L"]." << endl;

		wcout << L"=========================================================================" << endl;

		allesx.allEsx.push_back(esxVM);
	}


	CString strFileName;
	strFileName.Format(L"allesx.pid%010d.xml", ::GetCurrentProcessId());

	allesx.ToFile(ROOT_ITEM(tagEsxVMs), strFileName.GetString());

	v2pTable.V2PNativeFacadeExit(pInst);


	return 0;
}

