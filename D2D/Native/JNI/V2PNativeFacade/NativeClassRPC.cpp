#include "stdafx.h"
#include <assert.h>
#include "RPCProtocol.h"
#include "NativeClassRPC.h"

//#define INPUT_EVENT_INDEX 0
//#define EXIT_EVENT_HANDLE 1
//#define MAX_EVENT 2
//
//typedef struct tagTaskparamter{
//    HANDLE ahInputHandles[MAX_EVENT]; //exit and param
//    HANDLE hOutputEvent;
//
//    unsigned long inputCmd;
//    CString strInputData;
//    unsigned long outputValue;
//    CString strOutputData;
//
//}TASKPARAMTER_S;



//int RunTask(void *pp){
//    
//    HRESULT hr = CoInitializeEx(NULL, COINIT_MULTITHREADED);
//
//    CComPtr<ICNativeCmdCtrl> m_autoNativeRPCCtrl;
//    TASKPARAMTER_S *pParamter = (TASKPARAMTER_S *)pp;
//
//    do{
//
//        hr = m_autoNativeRPCCtrl.CoCreateInstance(__uuidof(CNativeCmdCtrl), NULL, CLSCTX_LOCAL_SERVER);
//        if (FAILED(hr))
//        {
//            D2DDEBUGLOG(LL_ERR, hr, _T("Failed to create com instance CNativeCmdCtrl, err=%u."), hr);
//            //m_log.writeDebugLog(_T("Failed to create com instance CNativeCmdCtrl, err=%u."), hr);
//            break;
//        }
//
//        hr = m_autoNativeRPCCtrl->Initialize();
//        if (FAILED(hr))
//        {
//            D2DDEBUGLOG(LL_ERR, hr, _T("Failed to init instance of CNativeCmdCtrl, err=%u."), hr);
//            //m_log.writeDebugLog(_T("Failed to init instance of CNativeCmdCtrl, err=%u."), hr);
//            break;
//        }
//
//        unsigned long ulProcessId = 0;
//        unsigned long ulThreadId = 0;
//        hr = m_autoNativeRPCCtrl->GetServerInfo(&ulProcessId, &ulThreadId);
//        if (SUCCEEDED(hr))
//        {
//            D2DDEBUGLOG(LL_INF, hr, _T("Succeeded to launch remote JVM. Process Id = %u, Thread Id = %u."), ulProcessId, ulThreadId);
//        }
//
//        ulProcessId = GetCurrentProcessId();
//        CString strcInfo = L"V2PNativeFacade.dll";
//        (VOID)m_autoNativeRPCCtrl->SetClientInfo((LONG)ulProcessId, CComBSTR(strcInfo));
//
//        BOOL bExit = FALSE;
//        while (!bExit)
//        {
//            DWORD dwWait = WaitForMultipleObjects(MAX_EVENT, pParamter->ahInputHandles, FALSE, INFINITE);
//            switch (dwWait)
//            {
//            case WAIT_OBJECT_0: //input ready
//            {
//                CComBSTR bstrOut;
//                HRESULT hr = m_autoNativeRPCCtrl->RPC_Invoke(pParamter->inputCmd, 
//                    CComBSTR(pParamter->strInputData), &(pParamter->outputValue), &bstrOut);
//                if (FAILED(hr))
//                {
//                    D2DDEBUGLOG(LL_ERR, hr, L"::Failed to invoke cmd %u, err=%u.", pParamter->inputCmd, hr);	
//                }
//                else
//                {
//                    pParamter->strOutputData = bstrOut;
//                    SetEvent(pParamter->hOutputEvent); //output ready
//                }
//                break;
//            }
//            case (WAIT_OBJECT_0 + 1) ://exit
//            {
//                bExit = TRUE;
//                break;
//            }
//            default:
//            {
//                break;
//            }
//            }
//        }
//    } while (FALSE);
//
//   m_autoNativeRPCCtrl.Release();
//   CoUninitialize();
//
//   return 0;
//}

NativeClassRPC::NativeClassRPC():
//m_bCoInitialized(FALSE),
m_pesxDetails(NULL),
m_pvmList(NULL),
m_pdataStoreList(NULL),
m_pdiskInfoList(NULL),
m_psnapshotInfoList(NULL),
m_padrDiskInfoList(NULL),
m_pvmInfoList(NULL),
m_pvmDataStoreInfoList(NULL),
//
m_pvcCreateVAppResult(NULL),
m_pvcImportVMResult(NULL)
{

}


NativeClassRPC::~NativeClassRPC()
{
	if (m_pesxDetails)
	{
		delete [] m_pesxDetails;
		m_pesxDetails = NULL;
	}

	if (m_pvmList)
	{
		delete[] m_pvmList;
		m_pvmList = NULL;
	}

    if (m_pdataStoreList)
    {
        delete[] m_pdataStoreList;
        m_pdataStoreList = NULL;
    }

    if (m_pdiskInfoList)
    {
        delete[] m_pdiskInfoList;
        m_pdiskInfoList = NULL;
    }

    if (m_psnapshotInfoList)
    {
        delete[] m_psnapshotInfoList;
        m_psnapshotInfoList = NULL;
    }

    if (m_padrDiskInfoList)
    {
        delete[] m_padrDiskInfoList;
        m_padrDiskInfoList = NULL;
    }

    if (m_pvmInfoList)
    {
        delete[] m_pvmInfoList;
        m_pvmInfoList = NULL;
    }

    if (m_pvmDataStoreInfoList)
    {
        delete[] m_pvmDataStoreInfoList;
        m_pvmDataStoreInfoList = NULL;
    }
    
    //
    if (m_pvcCreateVAppResult)
    {
        delete[] m_pvcCreateVAppResult;
        m_pvcCreateVAppResult = NULL;
    }
    
    if (m_pvcImportVMResult)
    {
        delete[] m_pvcImportVMResult;
        m_pvcImportVMResult = NULL;
    }
    
    m_autoNativeRPCCtrl.Release();
	//if (m_bCoInitialized)
	//{
	//	//m_autoNativeRPCCtrl.Release();
	//	//CoUninitialize();
	//	m_bCoInitialized = FALSE;
	//}	
}

BOOL NativeClassRPC::LibInit(const char* jarList/* = NULL*/, BOOL bOldVer/* = FALSE*/)
{
	HRESULT hr;

	do 
	{
		if (bOldVer)
		{
			hr = E_NOTIMPL;
			D2DDEBUGLOG(LL_ERR, hr, _T("Not support for old version, err=%u."), hr);
			//m_log.writeDebugLog(_T("Not support for old version, err=%u."), hr);
			break;
		}

		//hr = CoInitializeEx(0, COINIT_MULTITHREADED);
		//if (FAILED(hr))
		//{
		//	D2DDEBUGLOG(LL_ERR, hr, _T("Failed to init com, err=%u."), hr);
		//	//m_log.writeDebugLog(_T("Failed to init com, err=%u."), hr);
		//	break;
		//}
		//m_bCoInitialized = TRUE;

		//hr = CoInitializeSecurity(
		//	NULL,
		//	-1,								// COM authentication
		//	NULL,							// Authentication services
		//	NULL,							// Reserved
		//	//RPC_C_AUTHN_LEVEL_PKT_PRIVACY,			
		//	RPC_C_AUTHN_LEVEL_NONE,		// Default authentication
		//	RPC_C_IMP_LEVEL_IMPERSONATE,	// Default Impersonation  
		//	NULL,							// Authentication info
		//	EOAC_NONE,						// Additional capabilities 
		//	NULL							// Reserved
		//	);

		hr = m_autoNativeRPCCtrl.CoCreateInstance(__uuidof(CNativeCmdCtrl), NULL, CLSCTX_LOCAL_SERVER);
		if (FAILED(hr))
		{
			D2DDEBUGLOG(LL_ERR, hr, _T("Failed to create com instance CNativeCmdCtrl, err=%u."), hr);
			//m_log.writeDebugLog(_T("Failed to create com instance CNativeCmdCtrl, err=%u."), hr);
			break;
		}

		hr = m_autoNativeRPCCtrl->Initialize();
		if (FAILED(hr))
		{
			D2DDEBUGLOG(LL_ERR, hr, _T("Failed to init instance of CNativeCmdCtrl, err=%u."), hr);
			//m_log.writeDebugLog(_T("Failed to init instance of CNativeCmdCtrl, err=%u."), hr);
			break;
		}

		unsigned long ulProcessId = 0;
		unsigned long ulThreadId = 0;
		hr = m_autoNativeRPCCtrl->GetServerInfo(&ulProcessId, &ulThreadId);
		if (SUCCEEDED(hr))
		{
			D2DDEBUGLOG(LL_INF, hr, _T("Succeeded to launch remote JVM. Process Id = %u, Thread Id = %u."), ulProcessId, ulThreadId);
		}

		ulProcessId = GetCurrentProcessId();
		CString strcInfo = L"V2PNativeFacade.dll";
		(VOID)m_autoNativeRPCCtrl->SetClientInfo((LONG)ulProcessId, CComBSTR(strcInfo));
	} while (FALSE);

	if (FAILED(hr))
	{
		return FALSE;
	}
	
	return TRUE;
}

void NativeClassRPC::LibExit()
{
	HRESULT hr = m_autoNativeRPCCtrl->UnInitialize();
	if (FAILED(hr))
	{
		D2DDEBUGLOG(LL_ERR, hr, _T("Failed to uninit instance of CNativeCmdCtrl, err=%u."), hr);
		//m_log.writeDebugLog(_T("Failed to uninit instance of CNativeCmdCtrl, err=%u."), hr);
	}
}

int  NativeClassRPC::connectToESX(WCHAR* esxServer, WCHAR* esxUser, WCHAR* esxPwd, WCHAR* esxPro, bool bIgnoreCert, long lPort)
{
	int nRet = 0;
	CString strInputData;
	CString strOutputdata;
	unsigned long outputVal = 0;
	unsigned long inputCMD = RPC_CMD(connectToESX);

	tagConnectToESX_In In = { 
		PTR_2_STR(esxServer), 
		PTR_2_STR(esxUser),
		PTR_2_STR(esxPwd),
		PTR_2_STR(esxPro),
		bool_2_BOOL(bIgnoreCert),
		lPort
	};
	In.ToString(ROOT_ITEM(tagConnectToESX_In), strInputData);	

	HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
	if (SUCCEEDED(hr))
	{
		tagConnectToESX_Out out;
		out.FromString(ROOT_ITEM(tagConnectToESX_Out), strOutputdata);
		nRet = (INT)out.basicReturn.ulReturn;
	}
	else
	{
		nRet = hr;
	}

	return nRet;
}

int NativeClassRPC::connectToESX_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
	ASSERT_CMD(inputCmd, RPC_CMD(connectToESX));

	tagConnectToESX_In In;
	In.FromString(ROOT_ITEM(tagConnectToESX_In), inputData);

	int nRet = ::connectToESX(
		const_cast<wchar_t *>(In.esxServer.c_str()),
		const_cast<wchar_t *>(In.esxUser.c_str()),
		const_cast<wchar_t *>(In.esxPwd.c_str()),
		const_cast<wchar_t *>(In.esxPro.c_str()),
		BOOL_2_bool(In.bIgnoreCert),
		In.lPort,
		pInst
		);

	tagConnectToESX_Out Out;
	Out.basicReturn.ulReturn = nRet;
	Out.ToString(ROOT_ITEM(tagConnectToESX_Out), outputData);

	return 0;
}

int  NativeClassRPC::getVMServerType()
{
	int nRet = 0;
	CString strInputData;
	CString strOutputdata;
	unsigned long outputVal = 0;
	unsigned long inputCMD = RPC_CMD(getVMServerType);

	HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
	if (SUCCEEDED(hr))
	{
		tagGetVMServerType_Out out;
		out.FromString(ROOT_ITEM(tagGetVMServerType_Out), strOutputdata);
		nRet = (INT)out.basicReturn.ulReturn;
	}

	return nRet;
}

int NativeClassRPC::getVMServerType_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
	ASSERT_CMD(inputCmd, RPC_CMD(getVMServerType));

	int nRet = ::getVMServerType(pInst);

	tagGetVMServerType_Out Out;
	Out.basicReturn.ulReturn = nRet;

	Out.ToString(ROOT_ITEM(tagGetVMServerType_Out), outputData);

	return 0;
}

int  NativeClassRPC::checkVMServerLicense(WCHAR* esxName, WCHAR* dcName)
{
	int nRet = 0;
	CString strInputData;
	CString strOutputdata;
	unsigned long outputVal = 0;
	unsigned long inputCMD = RPC_CMD(checkVMServerLicense);

	tagcheckVMServerLicense_In In = {
		PTR_2_STR(esxName),
		PTR_2_STR(dcName)
	};
	In.ToString(ROOT_ITEM(tagcheckVMServerLicense_In), strInputData);

	HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
	if (SUCCEEDED(hr))
	{
		tagcheckVMServerLicense_Out out;
		out.FromString(ROOT_ITEM(tagcheckVMServerLicense_Out), strOutputdata);
		nRet = (INT)out.basicReturn.ulReturn;
	}
	else
	{
		nRet = -1;
	}

	return nRet;
}

int NativeClassRPC::checkVMServerLicense_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
	ASSERT_CMD(inputCmd, RPC_CMD(checkVMServerLicense));

	tagcheckVMServerLicense_In In;
	In.FromString(ROOT_ITEM(tagcheckVMServerLicense_In), inputData);

	int nRet = ::checkVMServerLicense(
		const_cast<wchar_t *>(In.esxName.c_str()),
		const_cast<wchar_t *>(In.dcName.c_str()),
		pInst);

	tagcheckVMServerLicense_Out Out;
	Out.basicReturn.ulReturn = nRet;

	Out.ToString(ROOT_ITEM(tagcheckVMServerLicense_Out), outputData);

	return 0;
}

// done by FengWei
BOOL  NativeClassRPC::checkVMServerInMaintainenceMode(WCHAR* esxName, WCHAR* dcName)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(checkVMServerInMaintainenceMode);

    tagcheckVMServerInMaintainenceMode_In In = {
        PTR_2_STR(esxName),
        PTR_2_STR(dcName)
    };
    In.ToString(ROOT_ITEM(tagcheckVMServerInMaintainenceMode_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagcheckVMServerInMaintainenceMode_Out out;
        out.FromString(ROOT_ITEM(tagcheckVMServerInMaintainenceMode_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// done by FengWei
int NativeClassRPC::checkVMServerInMaintainenceMode_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(checkVMServerInMaintainenceMode));

    tagcheckVMServerInMaintainenceMode_In In;
    In.FromString(ROOT_ITEM(tagcheckVMServerInMaintainenceMode_In), inputData);

    BOOL bRet = ::checkVMServerInMaintainenceMode(
        const_cast<wchar_t *>(In.esxName.c_str()),
        const_cast<wchar_t *>(In.dcName.c_str()),
        pInst);

    tagcheckVMServerInMaintainenceMode_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(tagcheckVMServerInMaintainenceMode_Out), outputData);

    return 0;
}

// done
WCHAR*  NativeClassRPC::getESXVersion(WCHAR* esxName, WCHAR* dcName)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getESXVersion);

    taggetESXVersion_In In = {
        PTR_2_STR(esxName),
        PTR_2_STR(dcName)
    };
    In.ToString(ROOT_ITEM(taggetESXVersion_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetESXVersion_Out out;
        out.FromString(ROOT_ITEM(taggetESXVersion_Out), strOutputdata);
        m_wstrReturn = out.esxVersion;
    }

    return (WCHAR *)(m_wstrReturn.c_str());
}
// done
int  NativeClassRPC::getESXVersion_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getESXVersion));

    taggetESXVersion_In In;
    In.FromString(ROOT_ITEM(taggetESXVersion_In), inputData);

    WCHAR* szRet = ::getESXVersion(
        const_cast<wchar_t *>(In.esxName.c_str()),
        const_cast<wchar_t *>(In.dcName.c_str()),
        pInst);

    taggetESXVersion_Out Out;
    Out.esxVersion.assign(szRet);

    Out.ToString(ROOT_ITEM(taggetESXVersion_Out), outputData);

    return 0;
}

// done, cannot pass the test, the return value is incorrect 2015-05-26
int  NativeClassRPC::getESXNumberOfProcessors(WCHAR* esxName, OUT UINT& numberOfLogicalProcessors, OUT UINT& numberOfProcessors)
{
    int nRet = -1;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getESXNumberOfProcessors);

	//<sonmi01>2015-7-23 #shared jvm review and fix
    taggetESXNumberOfProcessors_In In = {
        PTR_2_STR(esxName)/*,
        numberOfLogicalProcessors,
        numberOfProcessors*/
    };
    In.ToString(ROOT_ITEM(taggetESXNumberOfProcessors_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetESXNumberOfProcessors_Out out;
        out.FromString(ROOT_ITEM(taggetESXNumberOfProcessors_Out), strOutputdata);
		numberOfLogicalProcessors = out.numberOfLogicalProcessors;
		numberOfProcessors = out.numberOfProcessors;
        nRet = out.nRet;
    }

    return nRet;
}
// done, didn't test it yet
int  NativeClassRPC::getESXNumberOfProcessors_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getESXVersion));

    taggetESXNumberOfProcessors_In In;
    In.FromString(ROOT_ITEM(taggetESXNumberOfProcessors_In), inputData);

	//<sonmi01>2015-7-23 #shared jvm review and fix
	UINT numberOfLogicalProcessors = 0;
	UINT numberOfProcessors = 0;
    int nRet = ::getESXNumberOfProcessors(
        const_cast<wchar_t *>(In.esxName.c_str()),
        numberOfLogicalProcessors,
        numberOfProcessors,
        pInst);

    taggetESXNumberOfProcessors_Out Out;
	Out.numberOfLogicalProcessors = numberOfLogicalProcessors;
	Out.numberOfProcessors = numberOfProcessors;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(taggetESXNumberOfProcessors_Out), outputData);

    return 0;
}

void  NativeClassRPC::disconnectESX()
{
	(VOID)RPC_Invoke(RPC_CMD(disconnectESX));
}

int NativeClassRPC::disconnectESX_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
	ASSERT_CMD(inputCmd, RPC_CMD(disconnectESX));

	::disconnectESX(pInst);

	return 0;
}

ESXNode*  NativeClassRPC::getEsxNodeList(int *count)
{
	int nRet = 0;
	CString strInputData;
	CString strOutputdata;
	unsigned long outputVal = 0;
	unsigned long inputCMD = RPC_CMD(getEsxNodeList);

	if (m_pesxDetails)
	{
		delete[] m_pesxDetails;
		m_pesxDetails = NULL;
	}

	HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
	if (SUCCEEDED(hr))
	{
		taggetEsxNodeList_Out out;
		out.FromString(ROOT_ITEM(taggetEsxNodeList_Out), strOutputdata);
		int nSize = (int)out.vecEsxNodes.size();		
		if (nSize > 0)
		{
			vector<tagESXNode_internal> & vecEsxNodes = out.vecEsxNodes;
			m_pesxDetails = new ESXNode[nSize];
			for (size_t ii = 0; ii < nSize; ++ii) //<sonmi01>2015-5-13 ###???
			{
				tagESXNode_internal & esx = vecEsxNodes[ii];
				esx.__tagFromInternal(m_pesxDetails[ii]);
			}
		}
		*count = nSize;
	}

	return m_pesxDetails;
}

int NativeClassRPC::getEsxNodeList_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
	ASSERT_CMD(inputCmd, RPC_CMD(getEsxNodeList));

	int nCount = 0;
	ESXNode * aEsxNode = ::getEsxNodeList(&nCount, pInst);
	if (aEsxNode && nCount)
	{
		taggetEsxNodeList_Out Out;
		for (int ii = 0; ii < nCount; ii++)
		{
			tagESXNode_internal node;
			node.__tagToInternal(aEsxNode[ii]);
			Out.vecEsxNodes.push_back(node);
		}		
		Out.ToString(ROOT_ITEM(taggetEsxNodeList_Out), outputData);
	}

	return 0;
}

// done, didn't test it yet
DataStore*  NativeClassRPC::getESXHostDataStoreList(ESXNode esxNode, int *count)
{
    DataStore* pRet = NULL;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getESXNumberOfProcessors);

    if (m_pdataStoreList)
    {
        delete[] m_pdataStoreList;
        m_pdataStoreList = NULL;
    }

    taggetESXHostDataStoreList_In In;
    In.esxNode_internal.__tagToInternal(esxNode);

    In.ToString(ROOT_ITEM(taggetESXHostDataStoreList_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetESXHostDataStoreList_Out out;
        out.FromString(ROOT_ITEM(taggetESXHostDataStoreList_Out), strOutputdata);
       
        size_t nSize = out.vecDSs.size();
        if (nSize > 0)
        {
            vector<tagDataStore_internal> & vecDataStore = out.vecDSs;
            m_pdataStoreList = new DataStore[nSize];
            for (size_t ii = 0; ii < nSize; ++ii)
            {
                tagDataStore_internal & ds = vecDataStore[ii];
                ds.__tagFromInternal(m_pdataStoreList[ii]);
            }
        }
        *count = nSize;
    }

    return m_pdataStoreList;
}
// unfineshed
int NativeClassRPC::getESXHostDataStoreList_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    //ASSERT_CMD(inputCmd, RPC_CMD(getESXHostDataStoreList));

    //int nCount = 0;
    ////ESXNode * aEsxNode = ::getEsxNodeList(&nCount, pInst);


    //DataStore* pDataStore = ::getESXHostDataStoreList(, &nCount, pInst);
    //if (aEsxNode && nCount)
    //{
    //    taggetEsxNodeList_Out Out;
    //    for (int ii = 0; ii < nCount; ii++)
    //    {
    //        tagESXNode_internal node;
    //        node.__tagToInternal(aEsxNode[ii]);
    //        Out.vecEsxNodes.push_back(node);
    //    }
    //    Out.ToString(ROOT_ITEM(taggetEsxNodeList_Out), outputData);
    //}

    return 0;
}

VM_BasicInfo*  NativeClassRPC::getVMList(ESXNode esxNode, int *count)
{
	int nRet = 0;
	CString strInputData;
	CString strOutputdata;
	unsigned long outputVal = 0;
	unsigned long inputCMD = RPC_CMD(getVMList);

	if (m_pvmList)
	{
		delete[] m_pvmList;
		m_pvmList = NULL;
	}
	
	taggetVMList_In In;
	In.esxNode.__tagToInternal(esxNode);
	In.ToString(ROOT_ITEM(taggetVMList_Out), strInputData);

	HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
	if (SUCCEEDED(hr))
	{
		taggetVMList_Out out;
		out.FromString(ROOT_ITEM(taggetVMList_Out), strOutputdata);
		int nSize = (int)out.vecVMs.size();
		if (nSize > 0)
		{
			vector<tagVM_BasicInfo_internal> & vecVMs = out.vecVMs;
			m_pvmList = new VM_BasicInfo[nSize];
			for (size_t ii = 0; ii <nSize; ++ii) //<sonmi01>2015-5-13 ###???
			{
				tagVM_BasicInfo_internal & vm = vecVMs[ii];				
				vm.__tagFromInternal(m_pvmList[ii]);
			}
		}
		*count = nSize;
	}

	return m_pvmList;
}

int NativeClassRPC::getVMList_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
	ASSERT_CMD(inputCmd, RPC_CMD(getVMList));

	int nCount = 0;

	taggetVMList_In In;
	In.FromString(ROOT_ITEM(taggetVMList_In), inputData);

	ESXNode esxNode;
	In.esxNode.__tagFromInternal(esxNode);

	VM_BasicInfo * avm = ::getVMList(
		esxNode,
		&nCount, 
		pInst);
	if (avm && nCount)
	{
		taggetVMList_Out Out;
		for (int ii = 0; ii < nCount; ii++)
		{
			tagVM_BasicInfo_internal vm;
			vm.__tagToInternal(avm[ii]);
			Out.vecVMs.push_back(vm);
		}
		Out.ToString(ROOT_ITEM(taggetVMList_Out), outputData);
	}

	return 0;
}

//done
BOOL NativeClassRPC::checkResPool(ESXNode esxNode, WCHAR* resPool)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVMList);


    tagcheckResPool_In In;
    In.esxNode.__tagToInternal(esxNode);
    In.resPool.assign(resPool);
    In.ToString(ROOT_ITEM(tagcheckResPool_Out), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagcheckResPool_Out out;
        out.FromString(ROOT_ITEM(tagcheckResPool_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
//unfinished
int NativeClassRPC::checkResPool_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    //ASSERT_CMD(inputCmd, RPC_CMD(getESXVersion));

    //tagcheckResPool_In In;
    //In.FromString(ROOT_ITEM(tagcheckResPool_In), inputData);

    //BOOL bRet = ::checkResPool(In.esxNode,
    //    In.resPool,
    //    pInst);
    ////int nRet = ::getESXNumberOfProcessors(
    ////    const_cast<wchar_t *>(In.esxName.c_str()),
    ////    In.numberOfLogicalProcessors,
    ////    In.numberOfProcessors,
    ////    pInst);

    //tagcheckResPool_Out Out;
    //Out.bRet = bRet;

    //Out.ToString(ROOT_ITEM(tagcheckResPool_Out), outputData);

    return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::setInstanceUUID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* newInstanceUUID)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(setInstanceUUID);

    tagsetInstanceUUID_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(newInstanceUUID)
    };
    In.ToString(ROOT_ITEM(tagsetInstanceUUID_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagsetInstanceUUID_Out out;
        out.FromString(ROOT_ITEM(tagsetInstanceUUID_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
//done didn't test it yet//return value?
int NativeClassRPC::setInstanceUUID_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(setInstanceUUID));

    tagsetInstanceUUID_In In;
    In.FromString(ROOT_ITEM(tagsetInstanceUUID_In), inputData);

    int nRet = ::setInstanceUUID(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.newInstanceUUID.c_str()),
        pInst);

    tagsetInstanceUUID_Out Out;
    Out.bRet = nRet;
    Out.ToString(ROOT_ITEM(tagsetInstanceUUID_Out), outputData);

    return 0;
}
//done didn't test it yet//return value?
int NativeClassRPC::checkDSBlockSize(ESXNode esxNode, WCHAR* dataStore)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(setInstanceUUID);

    tagcheckDSBlockSize_In In;
    In.esxNode.__tagToInternal(esxNode);
    In.dataStore.assign(dataStore);
    In.ToString(ROOT_ITEM(tagcheckDSBlockSize_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagcheckDSBlockSize_Out out;
        out.FromString(ROOT_ITEM(tagcheckDSBlockSize_Out), strOutputdata);
        nRet = out.nRet;
    }

    return nRet;
}
//done didn't test it yet, FengWei help me to do the convert from ESXNode to ESXNodeInternal
int NativeClassRPC::checkDSBlockSize_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(checkDSBlockSize));

    tagcheckDSBlockSize_In In;
    In.FromString(ROOT_ITEM(tagcheckDSBlockSize_In), inputData);

    ESXNode esxNode;
    In.esxNode.__tagFromInternal(esxNode);
    int nRet = ::checkDSBlockSize(
        esxNode,
        const_cast<wchar_t *>(In.dataStore.c_str()),
        pInst);

    tagcheckDSBlockSize_Out Out;
    Out.nRet = nRet;
    Out.ToString(ROOT_ITEM(tagcheckDSBlockSize_Out), outputData);

    return 0;
}
// done, didn't test it yet!!!
WCHAR* NativeClassRPC::takeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName, error_Info* info, bool quiesce/* = true*/)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(takeSnapShot);

    tagtakeSnapShot_In In;
    In.vmName.assign(vmName);
    In.vmUUID.assign(vmUUID);
    In.snapshotName.assign(snapshotName);
    //In.errorInfo.__tagToInternal(*info);
    In.bQuiesce = quiesce;
    In.ToString(ROOT_ITEM(tagtakeSnapShot_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagtakeSnapShot_Out out;
        out.FromString(ROOT_ITEM(tagtakeSnapShot_Out), strOutputdata);
        m_wstrReturn = out.szRet;
        out.errorInfo.__tagFromInternal(*info);
    }

    return (WCHAR *)(m_wstrReturn.c_str());

}
// done, didn't test it yet!!!!
int NativeClassRPC::takeSnapShot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(takeSnapShot));

    tagtakeSnapShot_In In;
    In.FromString(ROOT_ITEM(tagtakeSnapShot_In), inputData);

    error_Info errorInfo;
    //In.errorInfo.__tagFromInternal(errorInfo);
    WCHAR* szRet = ::takeSnapShotEx(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.snapshotName.c_str()),
        &errorInfo,
		In.bQuiesce, //<sonmi01>2015-8-24 ###???
        pInst);

    tagtakeSnapShot_Out Out;
    //Out.nRet = nRet;
    Out.errorInfo.__tagToInternal(errorInfo);
    Out.szRet.assign(szRet);
    Out.ToString(ROOT_ITEM(tagtakeSnapShot_Out), outputData);

    return 0;
}

// done, didnt' test it yet
WCHAR* NativeClassRPC::checkandtakeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(checkandtakeSnapShot);

    tagcheckandtakeSnapShot_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotName)
    };
    In.ToString(ROOT_ITEM(tagcheckandtakeSnapShot_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagcheckandtakeSnapShot_Out out;
        out.FromString(ROOT_ITEM(tagcheckandtakeSnapShot_Out), strOutputdata);
        m_wstrReturn = out.szRet;
    }

    return (WCHAR*)(m_wstrReturn.c_str());
}
// done, didnt' test it yet
int NativeClassRPC::checkandtakeSnapShot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(checkandtakeSnapShot));

    tagcheckandtakeSnapShot_In In;
    In.FromString(ROOT_ITEM(tagcheckandtakeSnapShot_In), inputData);

    WCHAR* szRet = ::checkandtakeSnapShot(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.snapshotName.c_str()),
        pInst);

    tagcheckandtakeSnapShot_Out Out;
    Out.szRet.assign(szRet);

    Out.ToString(ROOT_ITEM(tagcheckandtakeSnapShot_Out), outputData);

    return 0;
}
// done, didnt' test it yet
WCHAR* NativeClassRPC::getVMMoref(WCHAR* vmName, WCHAR* vmUUID)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVMMoref);

    taggetVMMoref_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
    };
    In.ToString(ROOT_ITEM(taggetVMMoref_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVMMoref_Out out;
        out.FromString(ROOT_ITEM(taggetVMMoref_Out), strOutputdata);
        m_wstrReturn = out.szRet;
    }

    return (WCHAR *)(m_wstrReturn.c_str());
}
// done, didnt' test it yet
int NativeClassRPC::getVMMoref_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVMMoref));

    taggetVMMoref_In In;
    In.FromString(ROOT_ITEM(taggetVMMoref_In), inputData);

    WCHAR* szRet = ::getVMMoref(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        pInst);

    taggetVMMoref_Out Out;
    Out.szRet.assign(szRet);

    Out.ToString(ROOT_ITEM(taggetVMMoref_Out), outputData);

    return 0;
}
// done, didnt' test it yet
WCHAR* NativeClassRPC::getVMVersion(WCHAR* vmName, WCHAR* vmUUID)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVMVersion);

    taggetVMVersion_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
    };
    In.ToString(ROOT_ITEM(taggetVMVersion_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVMVersion_Out out;
        out.FromString(ROOT_ITEM(taggetVMVersion_Out), strOutputdata);
        m_wstrReturn = out.szRet;
    }

    return (WCHAR *)(m_wstrReturn.c_str());
}
// done, didnt' test it yet
int NativeClassRPC::getVMVersion_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVMVersion));

    taggetVMVersion_In In;
    In.FromString(ROOT_ITEM(taggetVMVersion_In), inputData);

    WCHAR* szRet = ::getVMVersion(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        pInst);

    taggetVMVersion_Out Out;
    Out.szRet.assign(szRet);

    Out.ToString(ROOT_ITEM(taggetVMVersion_Out), outputData);

    return 0;
}

/////
// done, didnt' test it yet
BOOL NativeClassRPC::revertSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(revertSnapShot);

    tagrevertSnapShot_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotURL)
    };
    In.ToString(ROOT_ITEM(tagrevertSnapShot_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagrevertSnapShot_Out out;
        out.FromString(ROOT_ITEM(tagrevertSnapShot_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::revertSnapShot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(revertSnapShot));

    tagrevertSnapShot_In In;
    In.FromString(ROOT_ITEM(tagrevertSnapShot_In), inputData);

    int nRet = ::setInstanceUUID(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.snapshotURL.c_str()),
        pInst);

    tagrevertSnapShot_Out Out;
    Out.bRet = nRet;
    Out.ToString(ROOT_ITEM(tagrevertSnapShot_Out), outputData);

    return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::removeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(removeSnapShot);

    tagremoveSnapShot_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotURL)
    };
    In.ToString(ROOT_ITEM(tagremoveSnapShot_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagremoveSnapShot_Out out;
        out.FromString(ROOT_ITEM(tagremoveSnapShot_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::removeSnapShot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(removeSnapShot));

    tagremoveSnapShot_In In;
    In.FromString(ROOT_ITEM(tagremoveSnapShot_In), inputData);

    int nRet = ::setInstanceUUID(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.snapshotURL.c_str()),
        pInst);

    tagremoveSnapShot_Out Out;
    Out.bRet = nRet;
    Out.ToString(ROOT_ITEM(tagremoveSnapShot_Out), outputData);

    return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::removeSnapShotAsync(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(removeSnapShotAsync);

    tagremoveSnapShotAsync_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotURL)
    };
    In.ToString(ROOT_ITEM(tagremoveSnapShotAsync_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagremoveSnapShotAsync_Out out;
        out.FromString(ROOT_ITEM(tagremoveSnapShotAsync_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::removeSnapShotAsync_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(removeSnapShotAsync));

    tagremoveSnapShotAsync_In In;
    In.FromString(ROOT_ITEM(tagremoveSnapShotAsync_In), inputData);

    BOOL bRet = ::setInstanceUUID(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.snapshotURL.c_str()),
        pInst);

    tagremoveSnapShotAsync_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(tagremoveSnapShotAsync_Out), outputData);

    return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::removeSnapShotByName(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(removeSnapShotByName);

    tagremoveSnapShotByName_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotName)
    };
    In.ToString(ROOT_ITEM(tagremoveSnapShotByName_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagremoveSnapShotByName_Out out;
        out.FromString(ROOT_ITEM(tagremoveSnapShotByName_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::removeSnapShotByName_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(removeSnapShotByName));

    tagremoveSnapShotByName_In In;
    In.FromString(ROOT_ITEM(tagremoveSnapShotByName_In), inputData);

    int nRet = ::removeSnapShotByName(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.snapshotName.c_str()),
        pInst);

    tagremoveSnapShotByName_Out Out;
    Out.bRet = nRet;
    Out.ToString(ROOT_ITEM(tagremoveSnapShotByName_Out), outputData);

    return 0;
}
// done, didnt' test it yet
WCHAR* NativeClassRPC::getsnapshotCTF(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getsnapshotCTF);

    taggetsnapshotCTF_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotURL)
    };
    In.ToString(ROOT_ITEM(taggetsnapshotCTF_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetsnapshotCTF_Out out;
        out.FromString(ROOT_ITEM(taggetsnapshotCTF_Out), strOutputdata);
        m_wstrReturn = out.szRet;
    }

    return (WCHAR *)(m_wstrReturn.c_str());
}
// done, didnt' test it yet
int NativeClassRPC::getsnapshotCTF_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getsnapshotCTF));

    taggetsnapshotCTF_In In;
    In.FromString(ROOT_ITEM(taggetsnapshotCTF_In), inputData);

    
    WCHAR* pRet = ::getsnapshotCTF(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.snapshotURL.c_str()),
        pInst);

    taggetsnapshotCTF_Out Out;
    Out.szRet.assign(pRet);
    Out.ToString(ROOT_ITEM(taggetsnapshotCTF_Out), outputData);

    return 0;
}
// done, didnt' test it yet
WCHAR* NativeClassRPC::getparentSnapshot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getparentSnapshot);

    taggetparentSnapshot_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotURL)
    };
    In.ToString(ROOT_ITEM(taggetparentSnapshot_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetparentSnapshot_Out out;
        out.FromString(ROOT_ITEM(taggetparentSnapshot_Out), strOutputdata);
        m_wstrReturn = out.szRet;
    }

    return (WCHAR *)(m_wstrReturn.c_str());
}
// done, didnt' test it yet
int NativeClassRPC::getparentSnapshot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{

    ASSERT_CMD(inputCmd, RPC_CMD(getparentSnapshot));

    taggetparentSnapshot_In In;
    In.FromString(ROOT_ITEM(taggetparentSnapshot_In), inputData);


    WCHAR* pRet = ::getparentSnapshot(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.snapshotURL.c_str()),
        pInst);

    taggetparentSnapshot_Out Out;
    Out.szRet.assign(pRet);
    Out.ToString(ROOT_ITEM(taggetparentSnapshot_Out), outputData);

    return 0;
}
// done, didnt' test it yet
Disk_Info* NativeClassRPC::getVMDiskURLs(WCHAR* vmName, WCHAR* vmUUID, int *count)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVMDiskURLs);

    if (m_pdiskInfoList)
    {
        delete[] m_pdiskInfoList;
        m_pdiskInfoList = NULL;
    }

    taggetVMDiskURLs_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
    };
    In.ToString(ROOT_ITEM(taggetVMDiskURLs_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVMDiskURLs_Out out;
        out.FromString(ROOT_ITEM(taggetVMDiskURLs_Out), strOutputdata);
        

        int nSize = (int)out.vecDIs.size();
        if (nSize > 0)
        {
            vector<tagDisk_Info_internal> & vecVMs = out.vecDIs;
            m_pdiskInfoList = new Disk_Info[nSize];
			ZeroMemory(m_pdiskInfoList, sizeof(Disk_Info) * nSize);

            for (size_t ii = 0; ii <nSize; ++ii)
            {
                tagDisk_Info_internal & vm = vecVMs[ii];
                vm.__tagFromInternal(m_pdiskInfoList[ii]);
            }
        }
        *count = nSize;
    }

    return m_pdiskInfoList;
}
// done, didnt' test it yet
int NativeClassRPC::getVMDiskURLs_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    //ASSERT_CMD(inputCmd, RPC_CMD(getVMList));

    //int nCount = 0;

    //taggetVMList_In In;
    //In.FromString(ROOT_ITEM(taggetVMList_In), inputData);

    //ESXNode esxNode;
    //In.esxNode.__tagFromInternal(esxNode);

    //VM_BasicInfo * avm = ::getVMList(
    //    esxNode,
    //    &nCount,
    //    pInst);
    //if (avm && nCount)
    //{
    //    taggetVMList_Out Out;
    //    for (int ii = 0; ii < nCount; ii++)
    //    {
    //        tagVM_BasicInfo_internal vm;
    //        vm.__tagToInternal(avm[ii]);
    //        Out.vecVMs.push_back(vm);
    //    }
    //    Out.ToString(ROOT_ITEM(taggetVMList_Out), outputData);
    //}
    ASSERT_CMD(inputCmd, RPC_CMD(getVMDiskURLs));

    int nCount = 0;
    taggetVMDiskURLs_In In;
    In.FromString(ROOT_ITEM(taggetVMDiskURLs_In), inputData);


    Disk_Info* pDiskInfo = ::getVMDiskURLs(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        &nCount,
        pInst);

    if (pDiskInfo && nCount)
    {
        taggetVMDiskURLs_Out Out;
        for (size_t i = 0; i < nCount; i++)
        {
            tagDisk_Info_internal vm;
            vm.__tagToInternal(pDiskInfo[i]);
            Out.vecDIs.push_back(vm);
        }
        Out.ToString(ROOT_ITEM(taggetVMDiskURLs_Out), outputData);
    }


    return 0;
}
// done, didnt' test it yet
Snapshot_Info* NativeClassRPC::getVMSnapshotList(WCHAR* vmName, WCHAR* vmUUID, int *count)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVMSnapshotList);

    if (m_psnapshotInfoList)
    {
        delete[] m_psnapshotInfoList;
        m_psnapshotInfoList = NULL;
    }

    taggetVMSnapshotList_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
    };
    In.ToString(ROOT_ITEM(taggetVMSnapshotList_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVMSnapshotList_Out out;
        out.FromString(ROOT_ITEM(taggetVMSnapshotList_Out), strOutputdata);


        int nSize = (int)out.vecSSIs.size();
        if (nSize > 0)
        {
            vector<tagSnapshot_Info_internal> & vecVMs = out.vecSSIs;
            m_psnapshotInfoList = new Snapshot_Info[nSize];
            for (size_t ii = 0; ii <nSize; ++ii)
            {
                tagSnapshot_Info_internal & vm = vecVMs[ii];
                vm.__tagFromInternal(m_psnapshotInfoList[ii]);
            }
        }
        *count = nSize;
    }

    return m_psnapshotInfoList;
}
int NativeClassRPC::getVMSnapshotList_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    //ASSERT_CMD(inputCmd, RPC_CMD(getVMList));

    //int nCount = 0;

    //taggetVMList_In In;
    //In.FromString(ROOT_ITEM(taggetVMList_In), inputData);

    //ESXNode esxNode;
    //In.esxNode.__tagFromInternal(esxNode);

    //VM_BasicInfo * avm = ::getVMList(
    //    esxNode,
    //    &nCount,
    //    pInst);
    //if (avm && nCount)
    //{
    //    taggetVMList_Out Out;
    //    for (int ii = 0; ii < nCount; ii++)
    //    {
    //        tagVM_BasicInfo_internal vm;
    //        vm.__tagToInternal(avm[ii]);
    //        Out.vecVMs.push_back(vm);
    //    }
    //    Out.ToString(ROOT_ITEM(taggetVMList_Out), outputData);
    //}
    ASSERT_CMD(inputCmd, RPC_CMD(getVMSnapshotList));

    int nCount = 0;
    taggetVMSnapshotList_In In;
    In.FromString(ROOT_ITEM(taggetVMSnapshotList_In), inputData);


    Snapshot_Info* pSnapshotInfo = ::getVMSnapshotList(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        &nCount,
        pInst);

    if (pSnapshotInfo && nCount)
    {
        taggetVMSnapshotList_Out Out;
        for (size_t i = 0; i < nCount; i++)
        {
            tagSnapshot_Info_internal vm;
            vm.__tagToInternal(pSnapshotInfo[i]);
            Out.vecSSIs.push_back(vm);
        }
        Out.ToString(ROOT_ITEM(taggetVMSnapshotList_Out), outputData);
    }


    return 0;
}

// done, didnt' test it yet
Disk_Info* NativeClassRPC::getSnapShotDiskInfo(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getSnapShotDiskInfo);

    if (m_pdiskInfoList)
    {
        delete[] m_pdiskInfoList;
        m_pdiskInfoList = NULL;
    }

    taggetSnapShotDiskInfo_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotURL)
    };
    In.ToString(ROOT_ITEM(taggetSnapShotDiskInfo_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetSnapShotDiskInfo_Out out;
        out.FromString(ROOT_ITEM(taggetSnapShotDiskInfo_Out), strOutputdata);


        int nSize = (int)out.vecDIs.size();
        if (nSize > 0)
        {
            vector<tagDisk_Info_internal> & vecVMs = out.vecDIs;
            m_pdiskInfoList = new Disk_Info[nSize];
            for (size_t ii = 0; ii <nSize; ++ii)
            {
                tagDisk_Info_internal & vm = vecVMs[ii];
                vm.__tagFromInternal(m_pdiskInfoList[ii]);
            }
        }
        *count = nSize;
    }

    return m_pdiskInfoList;
}
// done, didnt' test it yet
int NativeClassRPC::getSnapShotDiskInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    //ASSERT_CMD(inputCmd, RPC_CMD(getVMList));

    //int nCount = 0;

    //taggetVMList_In In;
    //In.FromString(ROOT_ITEM(taggetVMList_In), inputData);

    //ESXNode esxNode;
    //In.esxNode.__tagFromInternal(esxNode);

    //VM_BasicInfo * avm = ::getVMList(
    //    esxNode,
    //    &nCount,
    //    pInst);
    //if (avm && nCount)
    //{
    //    taggetVMList_Out Out;
    //    for (int ii = 0; ii < nCount; ii++)
    //    {
    //        tagVM_BasicInfo_internal vm;
    //        vm.__tagToInternal(avm[ii]);
    //        Out.vecVMs.push_back(vm);
    //    }
    //    Out.ToString(ROOT_ITEM(taggetVMList_Out), outputData);
    //}
    ASSERT_CMD(inputCmd, RPC_CMD(getSnapShotDiskInfo));

    int nCount = 0;
    taggetSnapShotDiskInfo_In In;
    In.FromString(ROOT_ITEM(taggetSnapShotDiskInfo_In), inputData);


    Disk_Info* pDiskInfo = ::getSnapShotDiskInfo(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.snapshotURL.c_str()),
        &nCount,
        pInst);

    if (pDiskInfo && nCount)
    {
        taggetSnapShotDiskInfo_Out Out;
        for (size_t i = 0; i < nCount; i++)
        {
            tagDisk_Info_internal vm;
            vm.__tagToInternal(pDiskInfo[i]);
            Out.vecDIs.push_back(vm);
        }
        Out.ToString(ROOT_ITEM(taggetSnapShotDiskInfo_Out), outputData);
    }


    return 0;
}
// done, didnt' test it yet
AdrDisk_Info* NativeClassRPC::getSnapShotAdrDiskInfo(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getSnapShotAdrDiskInfo);

    if (m_padrDiskInfoList)
    {
        delete[] m_padrDiskInfoList;
        m_padrDiskInfoList = NULL;
    }

    taggetSnapShotAdrDiskInfo_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotURL)
    };
    In.ToString(ROOT_ITEM(taggetSnapShotAdrDiskInfo_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetSnapShotAdrDiskInfo_Out out;
        out.FromString(ROOT_ITEM(taggetSnapShotAdrDiskInfo_Out), strOutputdata);


        int nSize = (int)out.vecADIs.size();
        if (nSize > 0)
        {
            vector<tagAdrDisk_Info_internal> & vecVMs = out.vecADIs;
            m_padrDiskInfoList = new AdrDisk_Info[nSize];
            for (size_t ii = 0; ii <nSize; ++ii)
            {
                tagAdrDisk_Info_internal & vm = vecVMs[ii];
                vm.__tagFromInternal(m_padrDiskInfoList[ii]);
            }
        }
        *count = nSize;
    }

    return m_padrDiskInfoList;
}
// done, didnt' test it yet
int NativeClassRPC::getSnapShotAdrDiskInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getSnapShotAdrDiskInfo));

    int nCount = 0;
    taggetSnapShotAdrDiskInfo_In In;
    In.FromString(ROOT_ITEM(taggetSnapShotAdrDiskInfo_In), inputData);


    AdrDisk_Info* pAdrDiskInfo = ::getSnapShotAdrDiskInfo(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.snapshotURL.c_str()),
        &nCount,
        pInst);

    if (pAdrDiskInfo && nCount)
    {
        taggetSnapShotAdrDiskInfo_Out Out;
        for (size_t i = 0; i < nCount; i++)
        {
            tagAdrDisk_Info_internal vm;
            vm.__tagToInternal(pAdrDiskInfo[i]);
            Out.vecADIs.push_back(vm);
        }
        Out.ToString(ROOT_ITEM(taggetSnapShotAdrDiskInfo_Out), outputData);
    }

    return 0;
}
// done, didnt' test it yet
AdrDisk_Info* NativeClassRPC::getVMAdrDiskInfo(WCHAR* vmName, WCHAR* vmUUID, int *count)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVMAdrDiskInfo);

    if (m_padrDiskInfoList)
    {
        delete[] m_padrDiskInfoList;
        m_padrDiskInfoList = NULL;
    }

    taggetVMAdrDiskInfo_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
    };
    In.ToString(ROOT_ITEM(taggetVMAdrDiskInfo_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVMAdrDiskInfo_Out out;
        out.FromString(ROOT_ITEM(taggetVMAdrDiskInfo_Out), strOutputdata);


        int nSize = (int)out.vecADIs.size();
        if (nSize > 0)
        {
            vector<tagAdrDisk_Info_internal> & vecVMs = out.vecADIs;
            m_padrDiskInfoList = new AdrDisk_Info[nSize];
            for (size_t ii = 0; ii <nSize; ++ii)
            {
                tagAdrDisk_Info_internal & vm = vecVMs[ii];
                vm.__tagFromInternal(m_padrDiskInfoList[ii]);
            }
        }
        *count = nSize;
    }

    return m_padrDiskInfoList;
}
// done, didnt' test it yet
int NativeClassRPC::getVMAdrDiskInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVMAdrDiskInfo));

    int nCount = 0;
    taggetVMAdrDiskInfo_In In;
    In.FromString(ROOT_ITEM(taggetVMAdrDiskInfo_In), inputData);


    AdrDisk_Info* pAdrDiskInfo = ::getVMAdrDiskInfo(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        &nCount,
        pInst);

    if (pAdrDiskInfo && nCount)
    {
        taggetVMAdrDiskInfo_Out Out;
        for (size_t i = 0; i < nCount; i++)
        {
            tagAdrDisk_Info_internal vm;
            vm.__tagToInternal(pAdrDiskInfo[i]);
            Out.vecADIs.push_back(vm);
        }
        Out.ToString(ROOT_ITEM(taggetVMAdrDiskInfo_Out), outputData);
    }

    return 0;
}
// done, didnt' test it yet
VM_Info* NativeClassRPC::getVMInfo(WCHAR* vmName, WCHAR* vmInstUUID)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVMInfo);

    if (m_pvmInfoList)
    {
        delete[] m_pvmInfoList;
        m_pvmInfoList = NULL;
    }

    taggetVMInfo_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmInstUUID),
    };
    In.ToString(ROOT_ITEM(taggetVMInfo_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVMInfo_Out out;
        out.FromString(ROOT_ITEM(taggetVMInfo_Out), strOutputdata);

        m_pvmInfoList = new VM_Info;
        out.vmInfo.__tagFromInternal(*m_pvmInfoList);
    }

    return m_pvmInfoList;
}
// done, didnt' test it yet
int NativeClassRPC::getVMInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVMInfo));

    //int nCount = 0;
    taggetVMInfo_In In;
    In.FromString(ROOT_ITEM(taggetVMInfo_In), inputData);


    VM_Info* pVMInfo = ::getVMInfo(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmInstUUID.c_str()),
        pInst);

    if (pVMInfo)
    {
        tagVM_Info_internal vm;
        vm.__tagToInternal(*pVMInfo);

        taggetVMInfo_Out Out;
        Out.vmInfo = vm;
        Out.ToString(ROOT_ITEM(taggetVMAdrDiskInfo_Out), outputData);
    }

    return 0;
}

//
int NativeClassRPC::getVMInfoUnderDataCenter(const WCHAR* vmName, const WCHAR* dcName, VM_Info* pVMInfo)
{
    int nRet = -1;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVMInfoUnderDataCenter);

    taggetVMInfoUnderDataCenter_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(dcName)
    };
    In.ToString(ROOT_ITEM(taggetVMInfoUnderDataCenter_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVMInfoUnderDataCenter_Out out;
        out.FromString(ROOT_ITEM(taggetVMInfoUnderDataCenter_Out), strOutputdata);

        out.vmInfo.__tagFromInternal(*pVMInfo);
        nRet = out.nRet;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::getVMInfoUnderDataCenter_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVMInfoUnderDataCenter));

    int nCount = 0;
    taggetVMInfoUnderDataCenter_In In;
    In.FromString(ROOT_ITEM(taggetVMInfoUnderDataCenter_In), inputData);

    VM_Info vmInfo;
    int nRet = ::getVMInfoUnderDataCenter(
        &vmInfo,
        const_cast<wchar_t *>(In.dcName.c_str()),
        const_cast<wchar_t *>(In.vmName.c_str()),
        pInst
        );

    taggetVMInfoUnderDataCenter_Out Out;
    Out.nRet = nRet;
    Out.vmInfo.__tagToInternal(vmInfo);
    Out.ToString(ROOT_ITEM(taggetVMInfoUnderDataCenter_Out), outputData);

    return 0;
}


// done, didnt' test it yet
void NativeClassRPC::deleteCTKFiles(WCHAR* esxName, WCHAR* dcName, WCHAR* vmName, WCHAR* vmUUID)
{
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(deleteCTKFiles);

    tagdeleteCTKFiles_In In = {
        PTR_2_STR(esxName),
        PTR_2_STR(dcName),
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID)
    };
    In.ToString(ROOT_ITEM(tagdeleteCTKFiles_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {

    }

    return;
}
// done, didnt' test it yet
int NativeClassRPC::deleteCTKFiles_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(deleteCTKFiles));

    tagdeleteCTKFiles_In In;
    In.FromString(ROOT_ITEM(tagdeleteCTKFiles_In), inputData);


    ::deleteCTKFiles(
        const_cast<wchar_t *>(In.esxName.c_str()),
        const_cast<wchar_t *>(In.dcName.c_str()),
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        pInst);

    return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::hasSufficientPermission() //<sonmi01>2014-1-9 #87330: With upgrade to VDDK 5.5, we need to Check permissions when importing VM from VC
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(hasSufficientPermission);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taghasSufficientPermission_Out out;
        out.FromString(ROOT_ITEM(taghasSufficientPermission_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::hasSufficientPermission_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(hasSufficientPermission));

    BOOL bRet = ::hasSufficientPermission(
        pInst);

    taghasSufficientPermission_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(taghasSufficientPermission_Out), outputData);

    return 0;
}
// done, didnt' test it yet
VMDataStoreInfo* NativeClassRPC::getVMDataStoreDetails(ESXNode nodeDetails, WCHAR* dsName)
{
    VMDataStoreInfo* pRet = NULL;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVMDataStoreDetails);

    if (m_pvmDataStoreInfoList)
    {
        delete[] m_pvmDataStoreInfoList;
        m_pvmDataStoreInfoList = NULL;
    }

    taggetVMDataStoreDetails_In In;
    In.nodeDetails.__tagToInternal(nodeDetails);
    In.dsName.assign(dsName);
    In.ToString(ROOT_ITEM(taggetVMDataStoreDetails_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVMDataStoreDetails_Out out;
        out.FromString(ROOT_ITEM(taggetVMDataStoreDetails_Out), strOutputdata);

        m_pvmDataStoreInfoList = new VMDataStoreInfo;
        out.vmDataStoreInfo.__tagFromInternal(*m_pvmDataStoreInfoList);
    }

    return m_pvmDataStoreInfoList;

}
// done, didnt' test it yet
int NativeClassRPC::getVMDataStoreDetails_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVMDataStoreDetails));

    taggetVMDataStoreDetails_In In;
    In.FromString(ROOT_ITEM(taggetVMDataStoreDetails_In), inputData);

    ESXNode esxNode;
    In.nodeDetails.__tagFromInternal(esxNode);

    VMDataStoreInfo* pVMDataStoreInfo = ::getVMDataStoreDetails(
        esxNode,
        const_cast<wchar_t *>(In.dsName.c_str()),
        pInst);

    if (pVMDataStoreInfo)
    {
        tagVMDataStoreInfo_internal vm;
        vm.__tagToInternal(*pVMDataStoreInfo);

        taggetVMDataStoreDetails_Out Out;
        Out.vmDataStoreInfo = vm;
        Out.ToString(ROOT_ITEM(taggetVMDataStoreDetails_Out), outputData);
    }

    return 0;
}
// done, didnt' test it yet
void NativeClassRPC::rescanallHBA(ESXNode nodeDetails, BOOL rescanVC)
{
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(rescanallHBA);

    tagrescanallHBA_In In;
    In.nodeDetails.__tagToInternal(nodeDetails);
	In.rescanVC = rescanVC;
    In.ToString(ROOT_ITEM(tagrescanallHBA_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {

    }

    return;
}
// done, didnt' test it yet
int NativeClassRPC::rescanallHBA_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(rescanallHBA));

    tagrescanallHBA_In In;
    In.FromString(ROOT_ITEM(tagrescanallHBA_In), inputData);

    ESXNode esxNode;
    In.nodeDetails.__tagFromInternal(esxNode);

    ::rescanallHBA(
        esxNode,
		In.rescanVC,
        pInst);

    return 0;
}
// done, didnt' test it yet
WCHAR* NativeClassRPC::addCloneDataStore(ESXNode nodeDetails, WCHAR* dsGUID)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(addCloneDataStore);

    tagaddCloneDataStore_In In;
    In.nodeDetails.__tagToInternal(nodeDetails);
    In.dsGUID.assign(dsGUID);
    In.ToString(ROOT_ITEM(tagaddCloneDataStore_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagaddCloneDataStore_Out out;
        out.FromString(ROOT_ITEM(tagaddCloneDataStore_Out), strOutputdata);

        m_wstrReturn = out.szRet;
    }

    return (WCHAR *)(m_wstrReturn.c_str());
}
// done, didnt' test it yet
int NativeClassRPC::addCloneDataStore_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(addCloneDataStore));

    tagaddCloneDataStore_In In;
    In.FromString(ROOT_ITEM(tagaddCloneDataStore_In), inputData);

    ESXNode esxNode;
    In.nodeDetails.__tagFromInternal(esxNode);

    WCHAR* pRet = ::addCloneDataStore(
        esxNode,
        const_cast<wchar_t *>(In.dsGUID.c_str()),
        pInst);

    if (pRet)
    {
        tagaddCloneDataStore_Out Out;
        Out.szRet.assign(pRet);
        Out.ToString(ROOT_ITEM(tagaddCloneDataStore_Out), outputData);
    }

    return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::destroyandDeleteClone(ESXNode nodeDetails, WCHAR* dsName)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(destroyandDeleteClone);

    tagdestroyandDeleteClone_In In;
    In.nodeDetails.__tagToInternal(nodeDetails);
    In.dsName.assign(dsName);
    In.ToString(ROOT_ITEM(tagdestroyandDeleteClone_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagdestroyandDeleteClone_Out out;
        out.FromString(ROOT_ITEM(tagdestroyandDeleteClone_Out), strOutputdata);

        bRet = out.bRet;
    }

    return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::destroyandDeleteClone_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(destroyandDeleteClone));

    tagdestroyandDeleteClone_In In;
    In.FromString(ROOT_ITEM(tagdestroyandDeleteClone_In), inputData);

    ESXNode esxNode;
    In.nodeDetails.__tagFromInternal(esxNode);

    BOOL bRet = ::destroyandDeleteClone(
        esxNode,
        const_cast<wchar_t *>(In.dsName.c_str()),
        pInst);


    tagdestroyandDeleteClone_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(tagdestroyandDeleteClone_Out), outputData);

    return 0;
}
// done, didnt' test it yet
WCHAR* NativeClassRPC::createApplianceVM(ESXNode nodeDetails, WCHAR* vmName)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(createApplianceVM);

    tagcreateApplianceVM_In In;
    In.nodeDetails.__tagToInternal(nodeDetails);
    In.vmName.assign(vmName);
    In.ToString(ROOT_ITEM(tagcreateApplianceVM_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagcreateApplianceVM_Out out;
        out.FromString(ROOT_ITEM(tagcreateApplianceVM_Out), strOutputdata);

        m_wstrReturn = out.szRet;
    }

    return (WCHAR *)(m_wstrReturn.c_str());
}
// done, didnt' test it yet
int NativeClassRPC::createApplianceVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(createApplianceVM));

    tagcreateApplianceVM_In In;
    In.FromString(ROOT_ITEM(tagcreateApplianceVM_In), inputData);

    ESXNode esxNode;
    In.nodeDetails.__tagFromInternal(esxNode);

    WCHAR* pRet = ::createApplianceVM(
        esxNode,
        const_cast<wchar_t *>(In.vmName.c_str()),
        pInst);

    if (pRet)
    {
        tagcreateApplianceVM_Out Out;
        Out.szRet.assign(pRet);
        Out.ToString(ROOT_ITEM(tagcreateApplianceVM_Out), outputData);
    }

    return 0;
}
// done, didnt' test it yet
WCHAR* NativeClassRPC::createStndAloneApplianceVM(ESXNode nodeDetails)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(createStndAloneApplianceVM);

    tagcreateStndAloneApplianceVM_In In;
    In.nodeDetails.__tagToInternal(nodeDetails);
    In.ToString(ROOT_ITEM(tagcreateStndAloneApplianceVM_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagcreateStndAloneApplianceVM_Out out;
        out.FromString(ROOT_ITEM(tagcreateStndAloneApplianceVM_Out), strOutputdata);

        m_wstrReturn = out.szRet;
    }

    return (WCHAR *)(m_wstrReturn.c_str());
}
// done, didnt' test it yet
int NativeClassRPC::createStndAloneApplianceVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(createStndAloneApplianceVM));

    tagcreateStndAloneApplianceVM_In In;
    In.FromString(ROOT_ITEM(tagcreateStndAloneApplianceVM_In), inputData);

    ESXNode esxNode;
    In.nodeDetails.__tagFromInternal(esxNode);

    WCHAR* pRet = ::createStndAloneApplianceVM(
        esxNode,
        pInst);

    if (pRet)
    {
        tagcreateStndAloneApplianceVM_Out Out;
        Out.szRet.assign(pRet);
        Out.ToString(ROOT_ITEM(tagcreateStndAloneApplianceVM_Out), outputData);
    }

    return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::deleteApplianceVM(ESXNode nodeDetails, WCHAR* vmName)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(deleteApplianceVM);

    tagdeleteApplianceVM_In In;
    In.nodeDetails.__tagToInternal(nodeDetails);
    In.vmName.assign(vmName);
    In.ToString(ROOT_ITEM(tagdeleteApplianceVM_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagdeleteApplianceVM_Out out;
        out.FromString(ROOT_ITEM(tagdeleteApplianceVM_Out), strOutputdata);

        bRet = out.bRet;
    }

    return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::deleteApplianceVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(deleteApplianceVM));

    tagdeleteApplianceVM_In In;
    In.FromString(ROOT_ITEM(tagdeleteApplianceVM_In), inputData);

    ESXNode esxNode;
    In.nodeDetails.__tagFromInternal(esxNode);

    BOOL bRet = ::destroyandDeleteClone(
        esxNode,
        const_cast<wchar_t *>(In.vmName.c_str()),
        pInst);


    tagdeleteApplianceVM_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(tagdeleteApplianceVM_Out), outputData);

    return 0;
}
// done, didnt' test it yet
WCHAR* NativeClassRPC::attachDiskToVM(WCHAR* vmName, WCHAR* diskURL, WCHAR* esxName, WCHAR* diskType)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(attachDiskToVM);

    tagattachDiskToVM_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(diskURL),
        PTR_2_STR(esxName),
        PTR_2_STR(diskType)
    };
    In.ToString(ROOT_ITEM(tagattachDiskToVM_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagattachDiskToVM_Out out;
        out.FromString(ROOT_ITEM(tagattachDiskToVM_Out), strOutputdata);
        m_wstrReturn = out.szRet;
    }

    return (WCHAR *)(m_wstrReturn.c_str());
}
// done, didnt' test it yet
int NativeClassRPC::attachDiskToVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(attachDiskToVM));

    tagattachDiskToVM_In In;
    In.FromString(ROOT_ITEM(tagattachDiskToVM_In), inputData);


    WCHAR* pRet = ::attachDiskToVM(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.diskURL.c_str()),
        const_cast<wchar_t *>(In.esxName.c_str()),
        const_cast<wchar_t *>(In.diskType.c_str()),
        pInst);

    tagattachDiskToVM_Out Out;
    Out.szRet.assign(pRet);
    Out.ToString(ROOT_ITEM(tagattachDiskToVM_Out), outputData);

    return 0;
}
// done, didnt' test it yet
WCHAR* NativeClassRPC::detachDiskFromVM(WCHAR* vmName, WCHAR* diskURL)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(detachDiskFromVM);

    tagdetachDiskFromVM_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(diskURL),
    };
    In.ToString(ROOT_ITEM(tagdetachDiskFromVM_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagdetachDiskFromVM_Out out;
        out.FromString(ROOT_ITEM(tagdetachDiskFromVM_Out), strOutputdata);
        m_wstrReturn = out.szRet;
    }

    return (WCHAR *)(m_wstrReturn.c_str());
}
// done, didnt' test it yet
int NativeClassRPC::detachDiskFromVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(detachDiskFromVM));

    tagdetachDiskFromVM_In In;
    In.FromString(ROOT_ITEM(tagdetachDiskFromVM_In), inputData);


    WCHAR* pRet = ::detachDiskFromVM(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.diskURL.c_str()),
        pInst);

    tagdetachDiskFromVM_Out Out;
    Out.szRet.assign(pRet);
    Out.ToString(ROOT_ITEM(tagdetachDiskFromVM_Out), outputData);

    return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::isESXunderVC(ESXNode nodeDetails)
{
	BOOL bRet = FALSE;
	CString strInputData;
	CString strOutputdata;
	unsigned long outputVal = 0;
	unsigned long inputCMD = RPC_CMD(isESXunderVC);

	tagisESXunderVC_In In;
	In.nodeDetails.__tagToInternal(nodeDetails);
	In.ToString(ROOT_ITEM(tagisESXunderVC_In), strInputData);

	HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
	if (SUCCEEDED(hr))
	{
		tagisESXunderVC_Out out;
		out.FromString(ROOT_ITEM(tagisESXunderVC_Out), strOutputdata);

		bRet = out.bRet;
	}

	return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::isESXunderVC_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
	ASSERT_CMD(inputCmd, RPC_CMD(isESXunderVC));

	tagisESXunderVC_In In;
	In.FromString(ROOT_ITEM(tagisESXunderVC_In), inputData);

	ESXNode esxNode;
	In.nodeDetails.__tagFromInternal(esxNode);

	BOOL bRet = ::isESXunderVC(
		esxNode,
		pInst);

	tagisESXunderVC_Out Out;
	Out.bRet = bRet;
	Out.ToString(ROOT_ITEM(tagisESXunderVC_Out), outputData);

	return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::isESXinCluster(ESXNode nodeDetails)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(isESXinCluster);

    tagisESXinCluster_In In;
    In.nodeDetails.__tagToInternal(nodeDetails);
    In.ToString(ROOT_ITEM(tagisESXinCluster_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagisESXinCluster_Out out;
        out.FromString(ROOT_ITEM(tagisESXinCluster_Out), strOutputdata);

        bRet = out.bRet;
    }

    return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::isESXinCluster_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(isESXinCluster));

    tagisESXinCluster_In In;
    In.FromString(ROOT_ITEM(tagisESXinCluster_In), inputData);

    ESXNode esxNode;
    In.nodeDetails.__tagFromInternal(esxNode);

    BOOL bRet = ::isESXinCluster(
        esxNode,
        pInst);

    tagisESXinCluster_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(tagisESXinCluster_Out), outputData);

    return 0;
}
// done, didnt' test it yet
WCHAR* NativeClassRPC::checkandtakeApplianceSnapShot(WCHAR* vmName, WCHAR* snapshotName)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(checkandtakeApplianceSnapShot);

    tagcheckandtakeApplianceSnapShot_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(snapshotName)
    };
    In.ToString(ROOT_ITEM(tagcheckandtakeApplianceSnapShot_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagcheckandtakeApplianceSnapShot_Out out;
        out.FromString(ROOT_ITEM(tagcheckandtakeApplianceSnapShot_Out), strOutputdata);
        m_wstrReturn = out.szRet;
    }

    return (WCHAR *)(m_wstrReturn.c_str());
}
// done, didnt' test it yet
int NativeClassRPC::checkandtakeApplianceSnapShot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(checkandtakeApplianceSnapShot));

    tagcheckandtakeApplianceSnapShot_In In;
    In.FromString(ROOT_ITEM(tagcheckandtakeApplianceSnapShot_In), inputData);


    WCHAR* pRet = ::checkandtakeApplianceSnapShot(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.snapshotName.c_str()),
        pInst);

    tagcheckandtakeApplianceSnapShot_Out Out;
    Out.szRet.assign(pRet);
    Out.ToString(ROOT_ITEM(tagcheckandtakeApplianceSnapShot_Out), outputData);

    return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::removeSnapshotFromAppliance(WCHAR* vmName, WCHAR*  snapRef)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(removeSnapshotFromAppliance);

    tagremoveSnapshotFromAppliance_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(snapRef)
    };
    In.ToString(ROOT_ITEM(tagremoveSnapshotFromAppliance_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagremoveSnapshotFromAppliance_Out out;
        out.FromString(ROOT_ITEM(tagremoveSnapshotFromAppliance_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::removeSnapshotFromAppliance_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(removeSnapshotFromAppliance));

    tagremoveSnapshotFromAppliance_In In;
    In.FromString(ROOT_ITEM(tagremoveSnapshotFromAppliance_In), inputData);

    int nRet = ::removeSnapshotFromAppliance(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.snapRef.c_str()),
        pInst);

    tagremoveSnapshotFromAppliance_Out Out;
    Out.bRet = nRet;
    Out.ToString(ROOT_ITEM(tagremoveSnapshotFromAppliance_Out), outputData);

    return 0;
}

////done, need to check
error_Info NativeClassRPC::createVMwareVirtualMachine(wchar_t* configFilePath,
	wchar_t* vcName,
	wchar_t* esxHost,
	wchar_t* esxDC,
	wchar_t* vmResPool,
	wchar_t* datastoreOfVM,
	wchar_t* vmNewName,
	int numDisks,
	wchar_t** diskUrlList,
	wchar_t** datastoreList,
	BOOL overwriteVM,
	BOOL recoverToOriginal,
	VM_Info* pVMInfo //out									
	)
{
    error_Info err = { 0 };
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(createVMwareVirtualMachine);

    tagcreateVMwareVirtualMachine_In In;
    In.configFilePath.assign(configFilePath);
    In.vcName.assign(vcName);
    In.esxHost.assign(esxHost);
    In.esxDC.assign(esxDC);
    if (vmResPool == NULL)
    {
        In.vmResPool = L"";
    }
    else
    {
        In.vmResPool.assign(vmResPool);
    }
    In.datastoreOfVM.assign(datastoreOfVM);
    In.vmNewName.assign(vmNewName);
    In.numDisks = numDisks;

    for (size_t i = 0; i < numDisks; i++)
    {
        //wchar_t* wstr = diskUrlList[i];
        In.diskUrlList.push_back(diskUrlList[i]);
        In.datastoreList.push_back(datastoreList[i]);
    }
    In.overwriteVM = overwriteVM;
    In.recoverToOriginal = recoverToOriginal;
    In.ToString(ROOT_ITEM(tagcreateVMwareVirtualMachine_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagcreateVMwareVirtualMachine_Out out;
        out.FromString(ROOT_ITEM(tagcreateVMwareVirtualMachine_Out), strOutputdata);

        out.errorInfoRet.__tagFromInternal(err);
        out.vmInfo.__tagFromInternal(*pVMInfo);
    }

    return err;
}
//done, need to check
int NativeClassRPC::createVMwareVirtualMachine_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(createVMwareVirtualMachine));

    tagcreateVMwareVirtualMachine_In In;
    In.FromString(ROOT_ITEM(tagcreateVMwareVirtualMachine_In), inputData);

    wchar_t** diskUrlList = NULL;
    const size_t c_uSizeUrlDisk = In.diskUrlList.size();
    diskUrlList = new wchar_t*[c_uSizeUrlDisk];
    for (size_t i = 0; i < c_uSizeUrlDisk; i++)
    {
        diskUrlList[i] = In.diskUrlList[i];
    }

    wchar_t** datastoreList = NULL;
    const size_t c_udatastoreList = In.datastoreList.size();
    datastoreList = new wchar_t*[c_udatastoreList];
    for (size_t i = 0; i < c_udatastoreList; i++)
    {
        datastoreList[i] = In.datastoreList[i];
    }
    VM_Info vmInfo;
    error_Info errInfo = ::VM_Create(
        const_cast<wchar_t *>(In.configFilePath.c_str()),
        const_cast<wchar_t *>(In.vcName.c_str()),
        const_cast<wchar_t *>(In.esxHost.c_str()),
        const_cast<wchar_t *>(In.esxDC.c_str()),
        const_cast<wchar_t *>(In.vmResPool.c_str()),
        const_cast<wchar_t *>(In.datastoreOfVM.c_str()),
        const_cast<wchar_t *>(In.vmNewName.c_str()),
        In.numDisks,
        diskUrlList,
        datastoreList,
        In.overwriteVM,
        In.recoverToOriginal,
        &vmInfo,
        pInst
        );



    tagcreateVMwareVirtualMachine_Out Out;
    Out.errorInfoRet.__tagToInternal(errInfo);
    Out.vmInfo.__tagToInternal(vmInfo);
    Out.ToString(ROOT_ITEM(tagcreateVMwareVirtualMachine_Out), outputData);


    return 0;
}

// done, didnt' test it yet
BOOL NativeClassRPC::getSnapshotConfigInfo(WCHAR* vmName, WCHAR* vmUUID, wchar_t* snapshotId, wchar_t* pathToSave)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getSnapshotConfigInfo);

    taggetSnapshotConfigInfo_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotId),
        PTR_2_STR(pathToSave)
    };
    In.ToString(ROOT_ITEM(taggetSnapshotConfigInfo_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetSnapshotConfigInfo_Out out;
        out.FromString(ROOT_ITEM(taggetSnapshotConfigInfo_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// no interface
int NativeClassRPC::getSnapshotConfigInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getSnapshotConfigInfo));

    taggetSnapshotConfigInfo_In In;
    In.FromString(ROOT_ITEM(taggetSnapshotConfigInfo_In), inputData);

    VM_Info vmInfo;
    wcscpy_s(vmInfo.vmName, NAME_SIZE, const_cast<wchar_t *>(In.vmName.c_str()));
    wcscpy_s(vmInfo.vmInstUUID, NAME_SIZE, const_cast<wchar_t *>(In.vmInstUUID.c_str()));
    BOOL bRet = ::VM_GetSnapshotConfigInfo(
        &vmInfo,
        const_cast<wchar_t *>(In.snapshotId.c_str()),
        const_cast<wchar_t *>(In.pathToSave.c_str()),
        pInst);

    taggetSnapshotConfigInfo_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(taggetSnapshotConfigInfo_Out), outputData);

    return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::getVSSwriterfiles(WCHAR* vmName, WCHAR* vmUUID, wchar_t* snapshotId, wchar_t* pathToSave)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVSSwriterfiles);

    taggetVSSwriterfiles_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotId),
        PTR_2_STR(pathToSave)
    };
    In.ToString(ROOT_ITEM(taggetVSSwriterfiles_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVSSwriterfiles_Out out;
        out.FromString(ROOT_ITEM(taggetVSSwriterfiles_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// no interface
int NativeClassRPC::getVSSwriterfiles_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVSSwriterfiles));

    taggetVSSwriterfiles_In In;
    In.FromString(ROOT_ITEM(taggetVSSwriterfiles_In), inputData);

    VM_Info vmInfo;
    wcscpy_s(vmInfo.vmName, NAME_SIZE, const_cast<wchar_t *>(In.vmName.c_str()));
    wcscpy_s(vmInfo.vmInstUUID, NAME_SIZE, const_cast<wchar_t *>(In.vmInstUUID.c_str()));
    BOOL bRet = ::VM_GetVSSwriterfiles(
        &vmInfo,
        const_cast<wchar_t *>(In.snapshotId.c_str()),
        const_cast<wchar_t *>(In.pathToSave.c_str()),
        pInst);

    taggetVSSwriterfiles_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(taggetVSSwriterfiles_Out), outputData);

    return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::setVMNVRamFile(WCHAR* vmName, WCHAR* vmUUID, wchar_t* nvRamFile)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(setVMNVRamFile);

    tagsetVMNVRamFile_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(nvRamFile)
    };
    In.ToString(ROOT_ITEM(tagsetVMNVRamFile_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagsetVMNVRamFile_Out out;
        out.FromString(ROOT_ITEM(tagsetVMNVRamFile_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// no interface
int NativeClassRPC::setVMNVRamFile_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(setVMNVRamFile));

    tagsetVMNVRamFile_In In;
    In.FromString(ROOT_ITEM(tagsetVMNVRamFile_In), inputData);

    VM_Info vmInfo;
    wcscpy_s(vmInfo.vmName, NAME_SIZE, const_cast<wchar_t *>(In.vmName.c_str()));
    wcscpy_s(vmInfo.vmInstUUID, NAME_SIZE, const_cast<wchar_t *>(In.vmInstUUID.c_str()));
    BOOL bRet = ::VM_SetVMnvramfiles(
        &vmInfo,
        const_cast<wchar_t *>(In.nvRamFile.c_str()),
        pInst);

    tagsetVMNVRamFile_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(tagsetVMNVRamFile_Out), outputData);

    return 0;
}
// done, didnt' test it yet
WCHAR* NativeClassRPC::getVMNVRAMFile(WCHAR* vmName, WCHAR* vmUUID, wchar_t* pathToSave)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVMNVRAMFile);

    taggetVMNVRAMFile_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(pathToSave)
    };
    In.ToString(ROOT_ITEM(taggetVMNVRAMFile_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVMNVRAMFile_Out out;
        out.FromString(ROOT_ITEM(taggetVMNVRAMFile_Out), strOutputdata);
        m_wstrReturn = out.szRet;
    }

    return (WCHAR *)(m_wstrReturn.c_str());
}
// no interface
int NativeClassRPC::getVMNVRAMFile_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{

    ASSERT_CMD(inputCmd, RPC_CMD(getVMNVRAMFile));

    taggetVMNVRAMFile_In In;
    In.FromString(ROOT_ITEM(taggetVMNVRAMFile_In), inputData);

    VM_Info vmInfo;
    wcscpy_s(vmInfo.vmName, NAME_SIZE, const_cast<wchar_t *>(In.vmName.c_str()));
    wcscpy_s(vmInfo.vmInstUUID, NAME_SIZE, const_cast<wchar_t *>(In.vmInstUUID.c_str()));
    WCHAR* pRet = ::VM_GetVMNVRAMFile(
        &vmInfo,
        const_cast<wchar_t *>(In.pathToSave.c_str()),
        pInst);

    taggetVMNVRAMFile_Out Out;
    Out.szRet.assign(pRet? pRet : L""); //<sonmi01>2015-7-23 #shared jvm review and fix
    Out.ToString(ROOT_ITEM(taggetVMNVRAMFile_Out), outputData);

    return 0;
}

////done , need to check
int NativeClassRPC::getUsedDiskBlocks(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, WCHAR* diskChangeId, int DiskDeviceKey, PLARGE_INTEGER pbitmapSize, PLARGE_INTEGER pUsedSectorCount, WCHAR* bitmapFilePath, int chunkSize, int sectorSize)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getUsedDiskBlocks);

    taggetUsedDiskBlocks_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotURL),
        PTR_2_STR(diskChangeId),
        DiskDeviceKey,
        PTR_2_STR(bitmapFilePath),
        chunkSize,
        sectorSize
    };
    In.ToString(ROOT_ITEM(taggetUsedDiskBlocks_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetUsedDiskBlocks_Out out;
        out.FromString(ROOT_ITEM(taggetUsedDiskBlocks_Out), strOutputdata);
        nRet = out.nRet;
        pbitmapSize->QuadPart = out.bitmapSize;
        pUsedSectorCount->QuadPart = out.UsedSectorCount;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
//done , need to check
int NativeClassRPC::getUsedDiskBlocks_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getUsedDiskBlocks));

    taggetUsedDiskBlocks_In In;
    In.FromString(ROOT_ITEM(taggetUsedDiskBlocks_In), inputData);

    VM_Info vmInfo;
    wcscpy_s(vmInfo.vmName, NAME_SIZE, const_cast<wchar_t *>(In.vmName.c_str()));
    wcscpy_s(vmInfo.vmInstUUID, NAME_SIZE, const_cast<wchar_t *>(In.vmInstUUID.c_str()));
    Disk_Info diskInfo;
    wcscpy_s(diskInfo.chageID, NAME_SIZE, const_cast<wchar_t *>(In.diskChangeId.c_str()));
    diskInfo.deviceKey = In.DiskDeviceKey;

    LARGE_INTEGER bitmapSize;
    LARGE_INTEGER UsedSectorCount;
	
	int nRet = 0;
	if ( L'*' == diskInfo.chageID[0] && 0 == diskInfo.chageID[1])
	{
		nRet = ::VM_GetDiskBitmap(
			&vmInfo,
			&diskInfo,
			const_cast<wchar_t *>(In.snapshotURL.c_str()),
			In.sectorSize,
			&bitmapSize,
			&UsedSectorCount,
			const_cast<wchar_t *>(In.bitmapFilePath.c_str()),
			pInst);
	}
	else
	{
		nRet = ::VM_GetDiskChangesBitmap(
			&vmInfo,
			&diskInfo,
			const_cast<wchar_t *>(In.snapshotURL.c_str()),
			In.sectorSize,
			&bitmapSize,
			&UsedSectorCount,
			const_cast<wchar_t *>(In.bitmapFilePath.c_str()),
			pInst);
	}

    taggetUsedDiskBlocks_Out Out;
    Out.nRet = nRet;
    Out.bitmapSize = bitmapSize.QuadPart; //<sonmi01>2015-7-23 #shared jvm review and fix
    Out.UsedSectorCount = UsedSectorCount.QuadPart;

    Out.ToString(ROOT_ITEM(taggetUsedDiskBlocks_Out), outputData);

    return 0;
}

// done, didnt' test it yet
int NativeClassRPC::enableChangeBlockTracking(WCHAR* vmName, WCHAR* vmUUID, BOOL bEnable)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(enableChangeBlockTracking);

    tagenableChangeBlockTracking_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        bEnable
    };
    In.ToString(ROOT_ITEM(tagenableChangeBlockTracking_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagenableChangeBlockTracking_Out out;
        out.FromString(ROOT_ITEM(tagenableChangeBlockTracking_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::enableChangeBlockTracking_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(enableChangeBlockTracking));

    tagenableChangeBlockTracking_In In;
    In.FromString(ROOT_ITEM(tagenableChangeBlockTracking_In), inputData);

    VM_Info vmInfo;
    wcscpy_s(vmInfo.vmName, NAME_SIZE, const_cast<wchar_t *>(In.vmName.c_str()));
    wcscpy_s(vmInfo.vmInstUUID, NAME_SIZE, const_cast<wchar_t *>(In.vmInstUUID.c_str()));
    int nRet = ::VM_EnableChangeTracking(
        &vmInfo,
        pInst);

    tagenableChangeBlockTracking_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagenableChangeBlockTracking_Out), outputData);

    return 0;
}
// done, didnt' test it yet
Disk_Info* NativeClassRPC::generateDiskBitMapForSnapshot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(generateDiskBitMapForSnapshot);

    if (m_pdiskInfoList)
    {
        delete[] m_pdiskInfoList;
        m_pdiskInfoList = NULL;
    }

    taggenerateDiskBitMapForSnapshot_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotURL)
    };
    In.ToString(ROOT_ITEM(taggenerateDiskBitMapForSnapshot_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggenerateDiskBitMapForSnapshot_Out out;
        out.FromString(ROOT_ITEM(taggenerateDiskBitMapForSnapshot_Out), strOutputdata);


        int nSize = (int)out.vecDIs.size();
        if (nSize > 0)
        {
            vector<tagDisk_Info_internal> & vecVMs = out.vecDIs;
            m_pdiskInfoList = new Disk_Info[nSize];
            for (size_t ii = 0; ii <nSize; ++ii)
            {
                tagDisk_Info_internal & vm = vecVMs[ii];
                vm.__tagFromInternal(m_pdiskInfoList[ii]);
            }
        }
        *count = nSize;
    }

    return m_pdiskInfoList;
}
// done, didnt' test it yet
int NativeClassRPC::generateDiskBitMapForSnapshot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(generateDiskBitMapForSnapshot));

    int nCount = 0;
    taggenerateDiskBitMapForSnapshot_In In;
    In.FromString(ROOT_ITEM(taggenerateDiskBitMapForSnapshot_In), inputData);


    Disk_Info* pDiskInfo = ::getSnapShotDiskInfo(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.snapshotURL.c_str()),
        &nCount,
        pInst);

    if (pDiskInfo && nCount)
    {
        taggenerateDiskBitMapForSnapshot_Out Out;
        for (size_t i = 0; i < nCount; i++)
        {
            tagDisk_Info_internal vm;
            vm.__tagToInternal(pDiskInfo[i]);
            Out.vecDIs.push_back(vm);
        }
        Out.ToString(ROOT_ITEM(taggenerateDiskBitMapForSnapshot_Out), outputData);
    }


    return 0;
}
// done, didnt' test it yet
int NativeClassRPC::checkAndEnableChangeBlockTracking(WCHAR* vmName, WCHAR* vmUUID)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(checkAndEnableChangeBlockTracking);

    tagcheckAndEnableChangeBlockTracking_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID)
    };
    In.ToString(ROOT_ITEM(tagcheckAndEnableChangeBlockTracking_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagcheckAndEnableChangeBlockTracking_Out out;
        out.FromString(ROOT_ITEM(tagcheckAndEnableChangeBlockTracking_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::checkAndEnableChangeBlockTracking_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(checkAndEnableChangeBlockTracking));

    tagcheckAndEnableChangeBlockTracking_In In;
    In.FromString(ROOT_ITEM(tagcheckAndEnableChangeBlockTracking_In), inputData);

    VM_Info vmInfo;
    wcscpy_s(vmInfo.vmName, NAME_SIZE, const_cast<wchar_t *>(In.vmName.c_str()));
    wcscpy_s(vmInfo.vmInstUUID, NAME_SIZE, const_cast<wchar_t *>(In.vmInstUUID.c_str()));
    int nRet = ::VM_CheckAndEnableChangeTracking(
        &vmInfo,
        pInst);

    tagcheckAndEnableChangeBlockTracking_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagcheckAndEnableChangeBlockTracking_Out), outputData);

    return 0;
}

// done, didnt' test it yet
BOOL NativeClassRPC::getFile(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  fileName, WCHAR*  localPath)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getFile);

    taggetFile_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(fileName),
        PTR_2_STR(localPath)
    };
    In.ToString(ROOT_ITEM(taggetFile_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetFile_Out out;
        out.FromString(ROOT_ITEM(taggetFile_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// // done, didnt' test it yet
int NativeClassRPC::getFile_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getFile));

    taggetFile_In In;
    In.FromString(ROOT_ITEM(taggetFile_In), inputData);

    BOOL bRet = ::getFile(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.fileName.c_str()),
        const_cast<wchar_t *>(In.localPath.c_str()),
        pInst);

    taggetFile_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(taggetFile_Out), outputData);

    return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::getDiskBitMap(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  diskURL, int deviceKey)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getDiskBitMap);

    taggetDiskBitMap_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(diskURL),
        deviceKey
    };
    In.ToString(ROOT_ITEM(taggetDiskBitMap_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetDiskBitMap_Out out;
        out.FromString(ROOT_ITEM(taggetDiskBitMap_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::getDiskBitMap_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getDiskBitMap));

    taggetDiskBitMap_In In;
    In.FromString(ROOT_ITEM(taggetDiskBitMap_In), inputData);

    BOOL bRet = ::getDiskBitMap(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.diskURL.c_str()),
        In.deviceKey,
        pInst);

    taggetDiskBitMap_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(taggetDiskBitMap_Out), outputData);

    return 0;
}

// done, didnt' test it yet
void NativeClassRPC::deleteDiskBitMap(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  snapshotURL)
{
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(deleteDiskBitMap);

    tagdeleteDiskBitMap_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotURL)
    };
    In.ToString(ROOT_ITEM(tagdeleteDiskBitMap_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {

    }

    return;
}
// done, didnt' test it yet
int NativeClassRPC::deleteDiskBitMap_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(deleteDiskBitMap));

    tagdeleteDiskBitMap_In In;
    In.FromString(ROOT_ITEM(tagdeleteDiskBitMap_In), inputData);


    ::deleteDiskBitMap(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.snapshotURL.c_str()),
        pInst);

    return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::setFileStream(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  fileName)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(setFileStream);

    tagsetFileStream_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(fileName)
    };
    In.ToString(ROOT_ITEM(tagsetFileStream_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagsetFileStream_Out out;
        out.FromString(ROOT_ITEM(tagsetFileStream_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::setFileStream_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(setFileStream));

    tagsetFileStream_In In;
    In.FromString(ROOT_ITEM(tagsetFileStream_In), inputData);

    BOOL bRet = ::setFileStream(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.fileName.c_str()),
        pInst);

    tagsetFileStream_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(tagsetFileStream_Out), outputData);

    return 0;
}
int NativeClassRPC::readFileStream(BYTE* pBuff, __int64 offSet, int length, int* bytesRead)
{
    int ret = 0;
    HRESULT hr = m_autoNativeRPCCtrl->readStream(offSet, length, pBuff, bytesRead, &ret);
    if (SUCCEEDED(hr))
    {
        return ret;
    }

    return -1;
}
//int NativeClassRPC::readFileStream_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
//{
//    ASSERT_CMD(inputCmd, RPC_CMD(powerOnVM));
//
//    return 0;
//}

// done, didnt' test it yet
BOOL NativeClassRPC::powerOnVM(WCHAR* vmName, WCHAR* vmUUID)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(powerOnVM);

    tagpowerOnVM_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
    };
    In.ToString(ROOT_ITEM(tagpowerOnVM_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagpowerOnVM_Out out;
        out.FromString(ROOT_ITEM(tagpowerOnVM_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::powerOnVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(powerOnVM));

    tagpowerOnVM_In In;
    In.FromString(ROOT_ITEM(tagpowerOnVM_In), inputData);

    BOOL bRet = ::powerOnVM(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        pInst);

    tagpowerOnVM_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(tagpowerOnVM_Out), outputData);

    return 0;
}

void NativeClassRPC::powerOffVM(WCHAR* vmName, WCHAR* vmUUID)
{
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(powerOffVM);

    tagpowerOffVM_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID)
    };
    In.ToString(ROOT_ITEM(tagpowerOffVM_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {

    }

    return;
}
// done, didnt' test it yet
int NativeClassRPC::powerOffVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(powerOffVM));

    tagpowerOffVM_In In;
    In.FromString(ROOT_ITEM(tagpowerOffVM_In), inputData);

    VM_Info vmInfo;
    wcscpy_s(vmInfo.vmName, NAME_SIZE, const_cast<wchar_t *>(In.vmName.c_str()));
    wcscpy_s(vmInfo.vmInstUUID, NAME_SIZE, const_cast<wchar_t *>(In.vmInstUUID.c_str()));
    ::VM_PowerOff(
        &vmInfo,
        pInst);

    return 0;
}
int NativeClassRPC::getVMPowerState(WCHAR* vmName, WCHAR*  vmUUID)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVMPowerState);

    taggetVMPowerState_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID)
    };
    In.ToString(ROOT_ITEM(taggetVMPowerState_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVMPowerState_Out out;
        out.FromString(ROOT_ITEM(taggetVMPowerState_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::getVMPowerState_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVMPowerState));

    taggetVMPowerState_In In;
    In.FromString(ROOT_ITEM(taggetVMPowerState_In), inputData);

    int nRet = ::getVMPowerState(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        pInst);

    taggetVMPowerState_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(taggetVMPowerState_Out), outputData);

    return 0;
}

int NativeClassRPC::getVMToolsState(WCHAR* vmName, WCHAR*  vmUUID)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVMToolsState);

    taggetVMToolsState_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID)
    };
    In.ToString(ROOT_ITEM(taggetVMToolsState_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVMToolsState_Out out;
        out.FromString(ROOT_ITEM(taggetVMToolsState_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::getVMToolsState_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVMToolsState));

    taggetVMToolsState_In In;
    In.FromString(ROOT_ITEM(taggetVMToolsState_In), inputData);

    int nRet = ::getVMToolsState(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        pInst);

    taggetVMToolsState_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(taggetVMToolsState_Out), outputData);

    return 0;
}

WCHAR* NativeClassRPC::getVmdkFilePath(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, DWORD dwDiskSignaure)
{
    m_wstrReturn.clear();
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVmdkFilePath);

    taggetVmdkFilePath_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotURL),
        dwDiskSignaure
    };
    In.ToString(ROOT_ITEM(taggetVmdkFilePath_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVmdkFilePath_Out out;
        out.FromString(ROOT_ITEM(taggetVmdkFilePath_Out), strOutputdata);
        m_wstrReturn = out.szRet;
    }

    return (WCHAR *)(m_wstrReturn.c_str());
}
// done, didnt' test it yet
int NativeClassRPC::getVmdkFilePath_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVmdkFilePath));

    taggetVmdkFilePath_In In;
    In.FromString(ROOT_ITEM(taggetVmdkFilePath_In), inputData);

    WCHAR* pRet = ::getVmdkFilePath(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.snapshotURL.c_str()),
        In.dwDiskSignaure,
        pInst);

    taggetVmdkFilePath_Out Out;
    Out.szRet.assign(pRet);
    Out.ToString(ROOT_ITEM(taggetVmdkFilePath_Out), outputData);

    return 0;
}

// done, didnt' test it yet
BOOL NativeClassRPC::removeAllSnapshots(WCHAR* vmName, WCHAR*  vmUUID)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(removeAllSnapshots);

    tagremoveAllSnapshots_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
    };
    In.ToString(ROOT_ITEM(tagremoveAllSnapshots_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagremoveAllSnapshots_Out out;
        out.FromString(ROOT_ITEM(tagremoveAllSnapshots_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::removeAllSnapshots_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(removeAllSnapshots));

    tagremoveAllSnapshots_In In;
    In.FromString(ROOT_ITEM(tagremoveAllSnapshots_In), inputData);

    BOOL bRet = ::removeAllSnapshots(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        pInst);

    tagremoveAllSnapshots_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(tagremoveAllSnapshots_Out), outputData);

    return 0;
}

int NativeClassRPC::deleteVM(WCHAR* vmName, WCHAR* vmUUID)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(deleteVM);

    tagdeleteVM_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID)
    };
    In.ToString(ROOT_ITEM(tagdeleteVM_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagdeleteVM_Out out;
        out.FromString(ROOT_ITEM(tagdeleteVM_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::deleteVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(deleteVM));

    tagdeleteVM_In In;
    In.FromString(ROOT_ITEM(tagdeleteVM_In), inputData);


    VM_Info vmInfo;
    wcscpy_s(vmInfo.vmName, NAME_SIZE, const_cast<wchar_t *>(In.vmName.c_str()));
    wcscpy_s(vmInfo.vmInstUUID, NAME_SIZE, const_cast<wchar_t *>(In.vmInstUUID.c_str()));
    int nRet = ::VM_Delete(
        &vmInfo,
        pInst);

    tagdeleteVM_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagdeleteVM_Out), outputData);

    return 0;
}
int NativeClassRPC::renameVM(WCHAR* vmName, WCHAR* vmUUID, WCHAR* vmNewname)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(renameVM);

    tagrenameVM_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(vmNewname)
    };
    In.ToString(ROOT_ITEM(tagrenameVM_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagrenameVM_Out out;
        out.FromString(ROOT_ITEM(tagrenameVM_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::renameVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(renameVM));

    tagrenameVM_In In;
    In.FromString(ROOT_ITEM(tagrenameVM_In), inputData);

    VM_Info vmInfo;
    wcscpy_s(vmInfo.vmName, NAME_SIZE, const_cast<wchar_t *>(In.vmName.c_str()));
    wcscpy_s(vmInfo.vmInstUUID, NAME_SIZE, const_cast<wchar_t *>(In.vmInstUUID.c_str()));
    int nRet = ::VM_Rename(
        &vmInfo,
        const_cast<wchar_t *>(In.vmNewname.c_str()),
        pInst);

    tagrenameVM_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagrenameVM_Out), outputData);

    return 0;
}


//////
BOOL NativeClassRPC::revertSnapShotByID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(revertSnapShotByID);

    tagrevertSnapShotByID_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotURL)
    };
    In.ToString(ROOT_ITEM(tagrevertSnapShotByID_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagrevertSnapShotByID_Out out;
        out.FromString(ROOT_ITEM(tagrevertSnapShotByID_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;
}
// done, didnt' test it yet
int NativeClassRPC::revertSnapShotByID_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(revertSnapShotByID));

    tagrevertSnapShotByID_In In;
    In.FromString(ROOT_ITEM(tagrevertSnapShotByID_In), inputData);

    VM_Info vmInfo;
    wcscpy_s(vmInfo.vmName, NAME_SIZE, const_cast<wchar_t *>(In.vmName.c_str()));
    wcscpy_s(vmInfo.vmInstUUID, NAME_SIZE, const_cast<wchar_t *>(In.vmInstUUID.c_str()));
    BOOL bRet = ::VM_RevertSnapshot(
        &vmInfo,
        const_cast<wchar_t *>(In.snapshotURL.c_str()),
        pInst);

    tagrevertSnapShotByID_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(tagrevertSnapShotByID_Out), outputData);

    return 0;
}

BOOL NativeClassRPC::removeSnapShotByID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(removeSnapShotByID);

    tagremoveSnapShotByID_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotURL)
    };
    In.ToString(ROOT_ITEM(tagremoveSnapShotByID_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagremoveSnapShotByID_Out out;
        out.FromString(ROOT_ITEM(tagremoveSnapShotByID_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;

}
// done, didnt' test it yet
int NativeClassRPC::removeSnapShotByID_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(removeSnapShotByID));

    tagremoveSnapShotByID_In In;
    In.FromString(ROOT_ITEM(tagremoveSnapShotByID_In), inputData);

    VM_Info vmInfo;
    wcscpy_s(vmInfo.vmName, NAME_SIZE, const_cast<wchar_t *>(In.vmName.c_str()));
    wcscpy_s(vmInfo.vmInstUUID, NAME_SIZE, const_cast<wchar_t *>(In.vmInstUUID.c_str()));
    BOOL bRet = ::VM_RemoveSnapshot(
        &vmInfo,
        const_cast<wchar_t *>(In.snapshotURL.c_str()),
        pInst);

    tagremoveSnapShotByID_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(tagremoveSnapShotByID_Out), outputData);

    return 0;
}
Disk_Info* NativeClassRPC::getSnapShotDiskInfoByID(WCHAR* vmName, WCHAR*  vmUUID, WCHAR* snapshotURL, int *count)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getSnapShotDiskInfoByID);

    if (m_pdiskInfoList)
    {
        delete[] m_pdiskInfoList;
        m_pdiskInfoList = NULL;
    }

    taggetSnapShotDiskInfoByID_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(snapshotURL)
    };
    In.ToString(ROOT_ITEM(taggetSnapShotDiskInfoByID_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetSnapShotDiskInfoByID_Out out;
        out.FromString(ROOT_ITEM(taggetSnapShotDiskInfoByID_Out), strOutputdata);


        int nSize = (int)out.vecDIs.size();
        if (nSize > 0)
        {
            vector<tagDisk_Info_internal> & vecVMs = out.vecDIs;
            m_pdiskInfoList = new Disk_Info[nSize];
            for (size_t ii = 0; ii <nSize; ++ii)
            {
                tagDisk_Info_internal & vm = vecVMs[ii];
                vm.__tagFromInternal(m_pdiskInfoList[ii]);
            }
        }
        *count = nSize;
    }

    return m_pdiskInfoList;
}
// done, didnt' test it yet
int NativeClassRPC::getSnapShotDiskInfoByID_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getSnapShotDiskInfoByID));

    int nCount = 0;
    taggetSnapShotDiskInfoByID_In In;
    In.FromString(ROOT_ITEM(taggetSnapShotDiskInfoByID_In), inputData);


    Disk_Info* pDiskInfo = ::getSnapShotDiskInfoByID(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.snapshotURL.c_str()),
        &nCount,
        pInst);

    if (pDiskInfo && nCount)
    {
        taggetSnapShotDiskInfoByID_Out Out;
        for (size_t i = 0; i < nCount; i++)
        {
            tagDisk_Info_internal vm;
            vm.__tagToInternal(pDiskInfo[i]);
            Out.vecDIs.push_back(vm);
        }
        Out.ToString(ROOT_ITEM(taggetSnapShotDiskInfoByID_Out), outputData);
    }


    return 0;
}

int NativeClassRPC::enableDiskUUIDForVM(WCHAR* vmName, WCHAR* vmUUID, BOOL bEnable)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(enableDiskUUIDForVM);

    tagenableDiskUUIDForVM_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        bEnable
    };
    In.ToString(ROOT_ITEM(tagenableDiskUUIDForVM_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagenableDiskUUIDForVM_Out out;
        out.FromString(ROOT_ITEM(tagenableDiskUUIDForVM_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::enableDiskUUIDForVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(enableDiskUUIDForVM));

    tagenableDiskUUIDForVM_In In;
    In.FromString(ROOT_ITEM(tagenableDiskUUIDForVM_In), inputData);

    VM_Info vmInfo;
    wcscpy_s(vmInfo.vmName, NAME_SIZE, const_cast<wchar_t *>(In.vmName.c_str()));
    wcscpy_s(vmInfo.vmInstUUID, NAME_SIZE, const_cast<wchar_t *>(In.vmInstUUID.c_str()));
    int nRet = ::VM_EnableDiskUUID(
        &vmInfo,
        pInst);

    tagenableDiskUUIDForVM_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagenableDiskUUIDForVM_Out), outputData);

    return 0;
}
// done, didnt' test it yet
void NativeClassRPC::logUserEvent(WCHAR* vmName, WCHAR* vmUUID, WCHAR* eventMessage)
{
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(logUserEvent);

    taglogUserEvent_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
        PTR_2_STR(eventMessage)
    };
    In.ToString(ROOT_ITEM(taglogUserEvent_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {

    }

    return;
}
// done, didnt' test it yet
int NativeClassRPC::logUserEvent_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(logUserEvent));

    taglogUserEvent_In In;
    In.FromString(ROOT_ITEM(taglogUserEvent_In), inputData);


    ::logUserEvent(
        const_cast<wchar_t *>(In.vmName.c_str()),
        const_cast<wchar_t *>(In.vmUUID.c_str()),
        const_cast<wchar_t *>(In.eventMessage.c_str()),
        pInst);

    return 0;
}

int NativeClassRPC::isVMNameUsed(wchar_t* vmname)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(isVMNameUsed);

    tagisVMNameUsed_In In = {
        PTR_2_STR(vmname)
    };
    In.ToString(ROOT_ITEM(tagisVMNameUsed_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagisVMNameUsed_Out out;
        out.FromString(ROOT_ITEM(tagisVMNameUsed_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::isVMNameUsed_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(isVMNameUsed));

    tagisVMNameUsed_In In;
    In.FromString(ROOT_ITEM(tagisVMNameUsed_In), inputData);

    int nRet = ::VM_IsVMNameUsed(
        const_cast<wchar_t *>(In.vmname.c_str()),
        pInst);

    tagisVMNameUsed_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagisVMNameUsed_Out), outputData);

    return 0;
}
// done, didnt' test it yet
BOOL NativeClassRPC::VMHasSnapshot(WCHAR* vmName, WCHAR* vmUUID)
{
    BOOL bRet = FALSE;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(VMHasSnapshot);

    tagVMHasSnapshot_In In = {
        PTR_2_STR(vmName),
        PTR_2_STR(vmUUID),
    };
    In.ToString(ROOT_ITEM(tagVMHasSnapshot_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagVMHasSnapshot_Out out;
        out.FromString(ROOT_ITEM(tagVMHasSnapshot_Out), strOutputdata);
        bRet = out.bRet;
    }

    return bRet;

}
// done, didnt' test it yet
int NativeClassRPC::VMHasSnapshot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(VMHasSnapshot));

    tagVMHasSnapshot_In In;
    In.FromString(ROOT_ITEM(tagVMHasSnapshot_In), inputData);

    VM_Info vmInfo;
    wcscpy_s(vmInfo.vmName, NAME_SIZE, const_cast<wchar_t *>(In.vmName.c_str()));
    wcscpy_s(vmInfo.vmInstUUID, NAME_SIZE, const_cast<wchar_t *>(In.vmInstUUID.c_str()));
    BOOL bRet = ::VM_HasSnapshot(
        &vmInfo,
        pInst);

    tagVMHasSnapshot_Out Out;
    Out.bRet = bRet;
    Out.ToString(ROOT_ITEM(tagVMHasSnapshot_Out), outputData);

    return 0;
}

////done need to check
int NativeClassRPC::SetvDSNetworkInfoEx(CONST VMNetworkAdapter_Info * pVMNetworkAdapter_Info, LONG Count) //<sonmi01>2013-6-5 #vds support
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(SetvDSNetworkInfoEx);

    tagSetvDSNetworkInfoEx_In In;
    for (size_t i = 0; i < Count; i++)
    {
        tagVMNetworkAdapter_Info_internal vmNetworkInfo_internal;
        vmNetworkInfo_internal.__tagToInternal(pVMNetworkAdapter_Info[i]);
        In.vecNetworkAdapterInfo.push_back(vmNetworkInfo_internal);
    }
    In.ToString(ROOT_ITEM(tagSetvDSNetworkInfoEx_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagSetvDSNetworkInfoEx_Out out;
        out.FromString(ROOT_ITEM(tagSetvDSNetworkInfoEx_Out), strOutputdata);
        nRet = out.nRet;
    }

    return nRet;
}
//done need to check
int NativeClassRPC::SetvDSNetworkInfoEx_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(SetvDSNetworkInfoEx));

    tagSetvDSNetworkInfoEx_In In;
    In.FromString(ROOT_ITEM(tagSetvDSNetworkInfoEx_In), inputData);

    VMNetworkAdapter_Info* pVMNetworkAdapter_Info = new VMNetworkAdapter_Info[In.Count];
    for (size_t i = 0; i < In.Count; i++)
    {
        In.vecNetworkAdapterInfo[i].__tagFromInternal(pVMNetworkAdapter_Info[i]);
    }
    int nRet = ::SetvDSNetworkInfoEx(
        pVMNetworkAdapter_Info,
        In.Count,
        pInst);

    tagSetvDSNetworkInfoEx_Out Out;
    Out.nRet = nRet;
    Out.ToString(ROOT_ITEM(tagSetvDSNetworkInfoEx_Out), outputData);

    return 0;
}
INT64 NativeClassRPC::GetESXVFlashResource(wchar_t* esxHost, wchar_t* esxDC)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(GetESXVFlashResource);

    tagGetESXVFlashResource_In In = {
        PTR_2_STR(esxHost),
        PTR_2_STR(esxDC)
    };
    In.ToString(ROOT_ITEM(tagGetESXVFlashResource_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagGetESXVFlashResource_Out out;
        out.FromString(ROOT_ITEM(tagGetESXVFlashResource_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::GetESXVFlashResource_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(GetESXVFlashResource));

    tagGetESXVFlashResource_In In;
    In.FromString(ROOT_ITEM(tagGetESXVFlashResource_In), inputData);

    INT64 nRet = ::GetESXVFlashResource(
        const_cast<wchar_t *>(In.esxHost.c_str()),
        const_cast<wchar_t *>(In.esxDC.c_str()),
        pInst);

    tagGetESXVFlashResource_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagGetESXVFlashResource_Out), outputData);

    return 0;
}

INT64 NativeClassRPC::GetVMVFlashReadCache(wchar_t* configFilePath)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(GetVMVFlashReadCache);

    tagGetVMVFlashReadCache_In In = {
        PTR_2_STR(configFilePath)
    };
    In.ToString(ROOT_ITEM(tagGetVMVFlashReadCache_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagGetVMVFlashReadCache_Out out;
        out.FromString(ROOT_ITEM(tagGetVMVFlashReadCache_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::GetVMVFlashReadCache_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(GetVMVFlashReadCache));

    tagGetVMVFlashReadCache_In In;
    In.FromString(ROOT_ITEM(tagGetVMVFlashReadCache_In), inputData);

    INT64 nRet = ::GetVMFlashReadCache(
        const_cast<wchar_t *>(In.configFilePath.c_str()),
        pInst);

    tagGetVMVFlashReadCache_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagGetVMVFlashReadCache_Out), outputData);

    return 0;
}
//done, need to check
int NativeClassRPC::cleanupHotAddedDisksAndConsolidateSnapshot(const wstring& esxHost, const vector<wstring>& proxyVMIPList, const vector<wstring>& proxyHotAddedDiskURLs, const wstring& protectedVMInstanceUUID)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(cleanupHotAddedDisksAndConsolidateSnapshot);

    tagcleanupHotAddedDisksAndConsolidateSnapshot_In In;
    In.esxHost.assign(esxHost);
    const size_t c_uSizeProxyVMIPList = proxyVMIPList.size();
    for (size_t i = 0; i < c_uSizeProxyVMIPList; i++)
    {
        In.proxyVMIPList.push_back(proxyVMIPList[i]);
    }
    const size_t c_uSizeproxyHotAddedDiskURLs = proxyHotAddedDiskURLs.size();
    for (size_t i = 0; i < c_uSizeproxyHotAddedDiskURLs; i++)
    {
        In.proxyHotAddedDiskURLs.push_back(proxyHotAddedDiskURLs[i]);
    }
    In.protectedVMInstanceUUID.assign(protectedVMInstanceUUID);
    In.ToString(ROOT_ITEM(tagcleanupHotAddedDisksAndConsolidateSnapshot_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagcleanupHotAddedDisksAndConsolidateSnapshot_Out out;
        out.FromString(ROOT_ITEM(tagcleanupHotAddedDisksAndConsolidateSnapshot_Out), strOutputdata);
        nRet = out.nRet;
    }

    return nRet;
}
//done, need to check
int NativeClassRPC::cleanupHotAddedDisksAndConsolidateSnapshot_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(cleanupHotAddedDisksAndConsolidateSnapshot));

    tagcleanupHotAddedDisksAndConsolidateSnapshot_In In;
    In.FromString(ROOT_ITEM(tagcleanupHotAddedDisksAndConsolidateSnapshot_In), inputData);

    std::vector<wstring> vecproxyVMIPList;
    const size_t c_uvecproxyVMIPList = In.proxyVMIPList.size();
    for (size_t i = 0; i < c_uvecproxyVMIPList; i++)
    {
        vecproxyVMIPList.push_back(In.proxyVMIPList[i]);
    }
    std::vector<wstring> vecproxyHotAddedDiskURLs;
    const size_t c_uvecproxyHotAddedDiskURLs = In.proxyHotAddedDiskURLs.size();
    for (size_t i = 0; i < c_uvecproxyHotAddedDiskURLs; i++)
    {
        vecproxyHotAddedDiskURLs.push_back(In.proxyHotAddedDiskURLs[i]);
    }

    int nRet = ::cleanupHotAddedDisksAndConsolidateSnapshot(
        pInst,
        In.esxHost,
        In.proxyVMIPList,
        In.proxyHotAddedDiskURLs,
        In.protectedVMInstanceUUID
        );

    tagcleanupHotAddedDisksAndConsolidateSnapshot_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagcleanupHotAddedDisksAndConsolidateSnapshot_Out), outputData);

    return 0;
}
VM_Info* NativeClassRPC::getVMInfoByMoId(WCHAR* vmMoId)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVMInfoByMoId);

    if (m_pvmInfoList)
    {
        delete[] m_pvmInfoList;
        m_pvmInfoList = NULL;
    }

    taggetVMInfoByMoId_In In = {
        PTR_2_STR(vmMoId)
    };
    In.ToString(ROOT_ITEM(taggetVMInfoByMoId_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVMInfoByMoId_Out out;
        out.FromString(ROOT_ITEM(taggetVMInfoByMoId_Out), strOutputdata);

        m_pvmInfoList = new VM_Info;
        out.vmInfo.__tagFromInternal(*m_pvmInfoList);
    }

    return m_pvmInfoList;
}
// done, didnt' test it yet
int NativeClassRPC::getVMInfoByMoId_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVMInfoByMoId));

    //int nCount = 0;
    taggetVMInfoByMoId_In In;
    In.FromString(ROOT_ITEM(taggetVMInfoByMoId_In), inputData);


    VM_Info* pVMInfo = ::getVMInfoByMoId(
        pInst,
        const_cast<wchar_t *>(In.vmMoId.c_str())
        );

    if (pVMInfo)
    {
        tagVM_Info_internal vm;
        vm.__tagToInternal(*pVMInfo);

        taggetVMInfoByMoId_Out Out;
        Out.vmInfo = vm;
        Out.ToString(ROOT_ITEM(taggetVMInfoByMoId_Out), outputData);
    }

    return 0;
}

int NativeClassRPC::SetVMDiskInfo(CONST Disk_Info* pVMDiskInfo, LONG Count)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(SetVMDiskInfo);

    tagSetVMDiskInfo_In In;
    for (size_t i = 0; i < Count; i++)
    {
        tagDisk_Info_internal vmDisk_Info_internal;
        vmDisk_Info_internal.__tagToInternal(pVMDiskInfo[i]);
        In.vecVMDiskInfo.push_back(vmDisk_Info_internal);
    }
    In.Count = Count;
    In.ToString(ROOT_ITEM(tagSetVMDiskInfo_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagSetVMDiskInfo_Out out;
        out.FromString(ROOT_ITEM(tagSetVMDiskInfo_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}

//caution!!!need to check
int NativeClassRPC::SetVMDiskInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(SetVMDiskInfo));

    tagSetVMDiskInfo_In In;
    In.FromString(ROOT_ITEM(tagSetVMDiskInfo_In), inputData);


    Disk_Info* pVMDiskInfo = new Disk_Info[In.Count];
    for (size_t i = 0; i < In.Count; i++)
    {
        In.vecVMDiskInfo[i].__tagFromInternal(pVMDiskInfo[i]);
    }
    int nRet = ::SetVMDiskInfo(
        pVMDiskInfo,
        In.Count,
        pInst
        );

    tagSetVMDiskInfo_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagSetVMDiskInfo_Out), outputData);

    return 0;
}
//done, need to check
int NativeClassRPC::getESXHostListByDatastoreMoRef(const wchar_t* datastoreMoRef, vector<wstring>& esxHostList)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getESXHostListByDatastoreMoRef);

    taggetESXHostListByDatastoreMoRef_In In = {
        PTR_2_STR(datastoreMoRef)
    };
    In.ToString(ROOT_ITEM(taggetESXHostListByDatastoreMoRef_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetESXHostListByDatastoreMoRef_Out out;
        out.FromString(ROOT_ITEM(taggetESXHostListByDatastoreMoRef_Out), strOutputdata);
        nRet = out.nRet;
        //esxHostList.assign(out.esxHostList);
        const size_t c_uSize = out.esxHostList.size();
        for (size_t i = 0; i < c_uSize; i++)
        {
            esxHostList.push_back(out.esxHostList[i]);
        }
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
//done, need to check
int NativeClassRPC::getESXHostListByDatastoreMoRef_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getESXHostListByDatastoreMoRef));

    taggetESXHostListByDatastoreMoRef_In In;
    In.FromString(ROOT_ITEM(taggetESXHostListByDatastoreMoRef_In), inputData);

    std::vector<wstring> esxHostList;
    int nRet = ::getESXHostListByDatastoreMoRef(
        const_cast<wchar_t *>(In.datastoreMoRef.c_str()),
        esxHostList,
        pInst
        );

    taggetESXHostListByDatastoreMoRef_Out Out;
    Out.nRet = nRet;
    const size_t c_uSize = esxHostList.size();
    for (size_t i = 0; i < c_uSize; i++)
    {
        Out.esxHostList.push_back(esxHostList[i]);
    }

    Out.ToString(ROOT_ITEM(taggetESXHostListByDatastoreMoRef_Out), outputData);

    return 0;
}
int NativeClassRPC::setVMCpuMemory(int numCPU, int numCoresPerSocket, long memoryMB)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(setVMCpuMemory);

    tagsetVMCpuMemory_In In = {
        numCPU,
        numCoresPerSocket,
        memoryMB
    };
    In.ToString(ROOT_ITEM(tagsetVMCpuMemory_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagsetVMCpuMemory_Out out;
        out.FromString(ROOT_ITEM(tagsetVMCpuMemory_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::setVMCpuMemory_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(setVMCpuMemory));

    tagsetVMCpuMemory_In In;
    In.FromString(ROOT_ITEM(tagsetVMCpuMemory_In), inputData);

    int nRet = ::setVMCpuMemory(
        In.numCPU,
        In.numCoresPerSocket,
        In.memoryMB,
        pInst);

    tagsetVMCpuMemory_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagsetVMCpuMemory_Out), outputData);

    return 0;
}

wstring NativeClassRPC::getThumbprint()
{
    //WCHAR szRet[MAX_PATH] = { 0 };
    wstring strRet;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getThumbprint);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetThumbprint_Out out;
        out.FromString(ROOT_ITEM(taggetThumbprint_Out), strOutputdata);
        //wcscpy_s(szRet, MAX_PATH, out.szRet.c_str());
        strRet = out.szRet;
    }

    return strRet;
}
// done, didnt' test it yet
int NativeClassRPC::getThumbprint_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getThumbprint));


    wstring strRet = ::getThumbprint(
        pInst);

    taggetThumbprint_Out Out;
    Out.szRet.assign(strRet);
    Out.ToString(ROOT_ITEM(taggetThumbprint_Out), outputData);

    return 0;
}


int NativeClassRPC::consolidateVMDisks(const wchar_t* vmUuid)
{
	int nRet = 0;
	CString strInputData;
	CString strOutputdata;
	unsigned long outputVal = 0;
	unsigned long inputCMD = RPC_CMD(consolidateVMDisks);

	tagconsolidateVMDisks_In In = {
		PTR_2_STR(vmUuid)
	};
	In.ToString(ROOT_ITEM(tagconsolidateVMDisks_In), strInputData);

	HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
	if (SUCCEEDED(hr))
	{
		tagconsolidateVMDisks_Out out;
		out.FromString(ROOT_ITEM(tagconsolidateVMDisks_Out), strOutputdata);
		nRet = out.nRet;
	}
	else
	{
		nRet = -1;
	}

	return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::consolidateVMDisks_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
	ASSERT_CMD(inputCmd, RPC_CMD(consolidateVMDisks));

	tagconsolidateVMDisks_In In;
	In.FromString(ROOT_ITEM(tagconsolidateVMDisks_In), inputData);

	INT64 nRet = ::consolidateVMDisks(
		const_cast<wchar_t *>(In.vmUuid.c_str()),
		pInst);

	tagconsolidateVMDisks_Out Out;
	Out.nRet = nRet;

	Out.ToString(ROOT_ITEM(tagconsolidateVMDisks_Out), outputData);

	return 0;
}

/// VIX is replaced by vSphere  -- begin --	E_NOTIMPL
int NativeClassRPC::ClearVMResult(IN int vmHandle)
{
	return E_NOTIMPL;
}
int NativeClassRPC::ClearAllVMResult()
{
	return E_NOTIMPL;
}
int NativeClassRPC::Vix_ConnectToHost(IN const char *pszHostName, IN int nHostPort, IN const char *pszUserName, IN const char *pszPassword,
	IN VixMgr_HostOptions nOptions, IN VixMgr_Handle hPropertyListHandle, IN VixEventProc *fnCallbackProc, IN void *pvClientData)
{
	return E_NOTIMPL;
}
void  NativeClassRPC::Vix_DisconnectFromHost(IN int iHostHandle)
{
	return;
}
int  NativeClassRPC::Vix_GetESXVersionVM(IN VixMgr_Handle hHostHandle, IN const char* pszVMName)
{
	return E_NOTIMPL;
}
int  NativeClassRPC::VixVM_Open(IN int iHostHandle, IN const char *pszVMName)
{
	return E_NOTIMPL;
}
int  NativeClassRPC::VixVM_LoginInGuest(IN int iHostHandle, IN const char *pszUserName, IN const char *pszPassword, IN int nOptions, IN VixEventProc *fnCallbackProc, void *pvClientData)
{
	return E_NOTIMPL;
}
int  NativeClassRPC::VixVM_LogoutFromGuest(IN int vmHandle, IN VixEventProc *callbackProc, IN void *clientData)
{
	return E_NOTIMPL;
}
int  NativeClassRPC::VixVM_RunProgramInGuest(IN int vmHandle, IN const char *pszGuestProgramName, IN const char *pszCommandLineArgs,
	IN VixMgr_RunProgramOptions nOptions, IN VixMgr_Handle hPropertyListHandle, IN VixEventProc *fnCallbackProc, IN void *pvClientData)
{
	return E_NOTIMPL;
}
int  NativeClassRPC::VixVM_CopyFileFromHostToGuest(IN int vmHandle, const char *hostPathName, const char *guestPathName, int options, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData)
{
	return E_NOTIMPL;
}
int  NativeClassRPC::VixVM_CopyFileFromGuestToHost(IN int vmHandle, const char *guestPathName, const char *hostPathName, int options, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData)
{
	return E_NOTIMPL;
}
int  NativeClassRPC::VixVM_DeleteFileInGuest(IN int vmHandle, const char *guestPathName, VixEventProc *callbackProc, void *clientData)
{
	return E_NOTIMPL;
}
int  NativeClassRPC::VixVM_FileExistsInGuest(IN int vmHandle, const char *guestPathName, VixEventProc *callbackProc, void *clientData)
{
	return E_NOTIMPL;
}
PGUEST_FILE_INFO  NativeClassRPC::VixVM_ListFileInGuest(IN int vmHandle, const char *pathName, int options, VixEventProc *callbackProc, void *clientData)
{
	return NULL;
}
int  NativeClassRPC::VixVM_FilterOutFile(IN int vmHandle, IN bool bRemoveFile)
{
	return E_NOTIMPL;
}
int  NativeClassRPC::VixVM_CreateDirectoryInGuest(IN int vmHandle, const char *pathName, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData)
{
	return E_NOTIMPL;
}
int  NativeClassRPC::VixVM_DeleteDirectoryInGuest(IN int vmHandle, const char *pathName, int options, VixEventProc *callbackProc, void *clientData)
{
	return E_NOTIMPL;
}
int  NativeClassRPC::VixVM_DirectoryExistsInGuest(IN int vmHandle, const char *pathName, VixEventProc *callbackProc, void *clientData)
{
	return E_NOTIMPL;
}
int  NativeClassRPC::VixJob_GetNumProperties(IN int vmHandle, int resultPropertyID)
{
	return E_NOTIMPL;
}
int  NativeClassRPC::VixJob_GetNthProperties(IN int vmHandle, int index, int propertyID, va_list args)
{
	return E_NOTIMPL;
}
PGUEST_PROCESS_INFO  NativeClassRPC::VixVM_ListProcessesInGuest(IN int vmHandle, int options, VixEventProc *callbackProc, void *clientData)
{
	return NULL;
}
/// \fn VixMgr_Error VixJob_Wait( ... )
/// \brief
/// \param vmHandle that can be returned by calling function VixHost_Connect(), VixVM_Open(), VixVM_LoginInGuest(), ... any above function that return type is VixMgr_Handle.
int  NativeClassRPC::VixJob_Wait(IN int vmHandle, VixMgr_PropertyID firstPropertyID, ...)
{
	return E_NOTIMPL;
}
int  NativeClassRPC::VixJob_CheckCompletion(IN int vmHandle, OUT bool *complete)
{
	return E_NOTIMPL;
}
BOOL  NativeClassRPC::VixVM_GetResultBOOL(IN int vmHandle)
{
	return FALSE;
}
void NativeClassRPC::VixVM_setResultBOOL(IN int vmHandle, IN BOOL bResult)
{
	return;
}
int  NativeClassRPC::VixVM_GetLastError(IN int iHandle, OUT wchar_t* pszError, IN size_t ccError)
{
	return E_NOTIMPL;
}
/// VIX is replaced by vSphere  -- end -- E_NOTIMPL



//////////////////////////////////////////////////////////////////////////
// for vcloudmanager
//////////////////////////////////////////////////////////////////////////
int NativeClassRPC::connectVCloud(const wchar_t* vcloudDirectorServerName, const wchar_t* username, const wchar_t* password, const wchar_t* protocol/* = L"https"*/, const int port/* = 443*/, const bool ignoreCert/* = true*/)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(connectVCloud);

    tagconnectVCloud_In In = {
        PTR_2_STR(vcloudDirectorServerName),
        PTR_2_STR(username),
        PTR_2_STR(password),
        PTR_2_STR(protocol),
        port,
        ignoreCert
    };
    In.ToString(ROOT_ITEM(tagconnectVCloud_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagconnectVCloud_Out out;
        out.FromString(ROOT_ITEM(tagconnectVCloud_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::connectVCloud_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(connectVCloud));

    tagconnectVCloud_In In;
    In.FromString(ROOT_ITEM(tagconnectVCloud_In), inputData);

    int nRet = ::connectVCloud(
        pInst,
        const_cast<wchar_t *>(In.vcloudDirectorServerName.c_str()),
        const_cast<wchar_t *>(In.username.c_str()),
        const_cast<wchar_t *>(In.password.c_str()),
        const_cast<wchar_t *>(In.protocol.c_str()),
        In.port,
        In.ignoreCert
        );

    tagconnectVCloud_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagconnectVCloud_Out), outputData);

    return 0;
}

void NativeClassRPC::disconnectVCloud()
{
    RPC_Invoke(RPC_CMD(disconnectVCloud));
}

int NativeClassRPC::disconnectVCloud_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(disconnectVCloud));

    ::disconnectVCloud(pInst);

    return 0;
}
int NativeClassRPC::saveVAppInfo(const wchar_t* vAppId, const wchar_t* vAppInfoFilePath)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(saveVAppInfo);

    tagsaveVAppInfo_In In = {
        PTR_2_STR(vAppId),
        PTR_2_STR(vAppInfoFilePath)
    };
    In.ToString(ROOT_ITEM(tagsaveVAppInfo_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagsaveVAppInfo_Out out;
        out.FromString(ROOT_ITEM(tagsaveVAppInfo_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::saveVAppInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(saveVAppInfo));

    tagsaveVAppInfo_In In;
    In.FromString(ROOT_ITEM(tagsaveVAppInfo_In), inputData);

    int nRet = ::saveVAppInfo(
        pInst,
        const_cast<wchar_t *>(In.vAppId.c_str()),
        const_cast<wchar_t *>(In.vAppInfoFilePath.c_str())
        );

    tagsaveVAppInfo_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagsaveVAppInfo_Out), outputData);

    return 0;
}

//unfinished, done, need to check
int NativeClassRPC::getVMListOfVApp(const wchar_t* vAppId, VCloudVM_Info** vmList, int* vmCount)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVMListOfVApp);

    //if (m_padrDiskInfoList)
    //{
    //    delete[] m_padrDiskInfoList;
    //    m_padrDiskInfoList = NULL;
    //}
    //taggetESXHostDataStoreList_In In;
    //In.esxNode_internal.__tagToInternal(esxNode);

    //In.ToString(ROOT_ITEM(taggetESXHostDataStoreList_In), strInputData);

    taggetVMListOfVApp_In In;
    In.vAppId = PTR_2_STR(vAppId);
    In.ToString(ROOT_ITEM(taggetVMListOfVApp_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVMListOfVApp_Out out;
        out.FromString(ROOT_ITEM(taggetVMListOfVApp_Out), strOutputdata);


        nRet = out.nRet;
        size_t uSize = out.vmList.size();
        *vmCount = (int)uSize;
        if (uSize > 0)
        {
            vmList = new VCloudVM_Info*;
            *vmList = new VCloudVM_Info[uSize];
            for (size_t i = 0; i < uSize; i++)
            {
                tagVCloudVM_Info_internal vCloudVMInfo_internal = out.vmList[i];
                vCloudVMInfo_internal.__tagFromInternal(*vmList[i]);
            }
        }
    //    int nSize = (int)out.vecADIs.size();
    //    if (nSize > 0)
    //    {
    //        vector<tagAdrDisk_Info_internal> & vecVMs = out.vecADIs;
    //        m_padrDiskInfoList = new AdrDisk_Info[nSize];
    //        for (size_t ii = 0; ii <nSize; ++ii)
    //        {
    //            tagAdrDisk_Info_internal & vm = vecVMs[ii];
    //            vm.__tagFromInternal(m_padrDiskInfoList[ii]);
    //        }
    //    }
    //    *count = nSize;
    }

    return 0;
}
//unfinished, done, need to check
int NativeClassRPC::getVMListOfVApp_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVMListOfVApp));

    taggetVMListOfVApp_In In;
    In.FromString(ROOT_ITEM(taggetVMListOfVApp_In), inputData);

    VCloudVM_Info** vmList;
    int vmCount;
    int nRet = ::getVMListOfVApp(
        pInst,
        const_cast<wchar_t *>(In.vAppId.c_str()),
        vmList,
        &vmCount
        );

    taggetVMListOfVApp_Out Out;
    Out.nRet = nRet;
    Out.vmCount = vmCount;
    
    Out.vmList.resize(vmCount);
    for (size_t i = 0; i < vmCount; i++)
    {
        Out.vmList[i].__tagToInternal(*vmList[i]);
    }

    Out.ToString(ROOT_ITEM(taggetVMListOfVApp_Out), outputData);

    return 0;
}
VCloud_CreateVAppResult* NativeClassRPC::createVApp(const VCloud_CreateVAppParams* createVAppParams)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(createVApp);

    if (m_pvcCreateVAppResult)
    {
        delete[] m_pvcCreateVAppResult;
        m_pvcCreateVAppResult = NULL;
    }

    tagcreateVApp_In In;
    In.vCloudCreateVAppPara.__tagToInternal(*createVAppParams);
    In.ToString(ROOT_ITEM(tagcreateVApp_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagcreateVApp_Out out;
        out.FromString(ROOT_ITEM(tagcreateVApp_Out), strOutputdata);

        m_pvcCreateVAppResult = new VCloud_CreateVAppResult;
        out.vCloudCreateVAppResult.__tagFromInternal(*m_pvcCreateVAppResult);
    }

    return m_pvcCreateVAppResult;
}
int NativeClassRPC::createVApp_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(createVApp));

    int nCount = 0;
    tagcreateVApp_In In;
    In.FromString(ROOT_ITEM(tagcreateVApp_In), inputData);

    VCloud_CreateVAppParams* pVCloudCreateVAppParams = NULL;
    In.vCloudCreateVAppPara.__tagFromInternal(*pVCloudCreateVAppParams);

    VCloud_CreateVAppResult* pResult = ::createVApp(
        pInst,
        pVCloudCreateVAppParams
        );

    if (pResult)
    {
        tagVCloud_CreateVAppResult_internal vCloudCreateVAppResult_internal;
        vCloudCreateVAppResult_internal.__tagToInternal(*pResult);

        tagcreateVApp_Out Out;
        Out.vCloudCreateVAppResult = vCloudCreateVAppResult_internal;
        Out.ToString(ROOT_ITEM(tagcreateVApp_Out), outputData);
    }

    return 0;
}

VCloud_ImportVMResult* NativeClassRPC::importVM(const VCloud_ImportVMParams* importVMParams)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(importVM);

    if (m_pvcImportVMResult)
    {
        delete[] m_pvcImportVMResult;
        m_pvcImportVMResult = NULL;
    }

    tagimportVM_In In;
    In.vCloudImportVMPara.__tagToInternal(*importVMParams);
    In.ToString(ROOT_ITEM(tagimportVM_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagimportVM_Out out;
        out.FromString(ROOT_ITEM(tagimportVM_Out), strOutputdata);

        m_pvcImportVMResult = new VCloud_ImportVMResult;
        out.vCloudImportVMResult.__tagFromInternal(*m_pvcImportVMResult);
    }

    return m_pvcImportVMResult;
}

int NativeClassRPC::importVM_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(importVM));

    int nCount = 0;
    tagimportVM_In In;
    In.FromString(ROOT_ITEM(tagimportVM_In), inputData);

    VCloud_ImportVMParams* pVCloudImportVMParams = NULL;
    In.vCloudImportVMPara.__tagFromInternal(*pVCloudImportVMParams);

    VCloud_ImportVMResult* pResult = ::importVM(
        pInst,
        pVCloudImportVMParams
        );

    if (pResult)
    {
        tagVCloud_ImportVMResult_internal vCloudImportVMResult_internal;
        vCloudImportVMResult_internal.__tagToInternal(*pResult);

        tagimportVM_Out Out;
        Out.vCloudImportVMResult = vCloudImportVMResult_internal;
        Out.ToString(ROOT_ITEM(tagimportVM_Out), outputData);
    }

    return 0;
}

// done, didnt' test it yet
// delete vApp and children VMs
int NativeClassRPC::deleteVApp(const wchar_t* vAppId)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(deleteVApp);

    tagdeleteVApp_In In = {
        PTR_2_STR(vAppId),
    };
    In.ToString(ROOT_ITEM(tagdeleteVApp_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagdeleteVApp_Out out;
        out.FromString(ROOT_ITEM(tagdeleteVApp_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
int NativeClassRPC::deleteVApp_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(deleteVApp));

    tagdeleteVApp_In In;
    In.FromString(ROOT_ITEM(tagdeleteVApp_In), inputData);

    int nRet = ::deleteVApp(
        pInst,
        const_cast<wchar_t *>(In.vAppId.c_str())
        );

    tagdeleteVApp_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagdeleteVApp_Out), outputData);

    return 0;
}
// rename vApp and children VMs
// append: true - append suffix, false - remove suffix; 
// renameVM: true - rename vApp and VMs, false - rename vApp only;
int NativeClassRPC::renameVAppEx(const wchar_t* vAppId, const wchar_t* suffix, const bool append, const bool renameVM)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(renameVAppEx);

    tagrenameVAppEx_In In = {
        PTR_2_STR(vAppId),
        PTR_2_STR(suffix),
        append,
        renameVM
    };
    In.ToString(ROOT_ITEM(tagrenameVAppEx_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagrenameVAppEx_Out out;
        out.FromString(ROOT_ITEM(tagrenameVAppEx_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::renameVAppEx_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(renameVAppEx));

    tagrenameVAppEx_In In;
    In.FromString(ROOT_ITEM(tagrenameVAppEx_In), inputData);

    int nRet = ::renameVAppEx(
        pInst,
        const_cast<wchar_t *>(In.vAppId.c_str()),
        const_cast<wchar_t *>(In.suffix.c_str()),
        In.append,
        In.renameVM
        );

    tagrenameVAppEx_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagrenameVAppEx_Out), outputData);

    return 0;
}

int NativeClassRPC::getVApp(const wchar_t* vAppId, VCloudVApp_Info* vAppInfo)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVApp);

    taggetVApp_In In;
    In.vAppId = PTR_2_STR(vAppId);
    In.ToString(ROOT_ITEM(taggetVApp_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVApp_Out out;
        out.FromString(ROOT_ITEM(taggetVApp_Out), strOutputdata);
        nRet = out.nRet;
        out.vAppInfo.__tagFromInternal(*vAppInfo);
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// caution!!done, didnt' test it yet
int NativeClassRPC::getVApp_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVApp));

    taggetVApp_In In;
    In.FromString(ROOT_ITEM(taggetVApp_In), inputData);

    VCloudVApp_Info vCloudVAppInfo;
    int nRet = ::getVApp(
        pInst,
        const_cast<wchar_t *>(In.vAppId.c_str()),
        &vCloudVAppInfo
        );

    taggetVApp_Out Out;
    Out.nRet = nRet;
    Out.vAppInfo.__tagFromInternal(vCloudVAppInfo);
    Out.ToString(ROOT_ITEM(taggetVApp_Out), outputData);

    return 0;
}
// caution, done, need to check
int NativeClassRPC::getVAppListOfVDC(const wchar_t* vdcId, VCloudVApp_Info** vAppList, int* count)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVAppListOfVDC);

    taggetVAppListOfVDC_In In;
    In.vdcId = PTR_2_STR(vdcId);
    In.ToString(ROOT_ITEM(taggetVAppListOfVDC_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVAppListOfVDC_Out out;
        out.FromString(ROOT_ITEM(taggetVAppListOfVDC_Out), strOutputdata);
        nRet = out.nRet;
        const size_t c_uSize = out.count;
        *count = (int)c_uSize;
        if (c_uSize > 0)
        {
            vAppList = new VCloudVApp_Info*;
            *vAppList = new VCloudVApp_Info[c_uSize];
            for (size_t i = 0; i < c_uSize; i++)
            {
                tagVCloudVApp_Info_internal vCloudVAppInfo_internal = out.vAppList[i];
                vCloudVAppInfo_internal.__tagFromInternal(*vAppList[i]);
            }
        }
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// caution, done, need to check
int NativeClassRPC::getVAppListOfVDC_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVAppListOfVDC));

    taggetVAppListOfVDC_In In;
    In.FromString(ROOT_ITEM(taggetVAppListOfVDC_In), inputData);

    VCloudVApp_Info** vmList;
    int count;
    int nRet = ::getVAppListOfVDC(
        pInst,
        const_cast<wchar_t *>(In.vdcId.c_str()),
        vmList,
        &count
        );

    taggetVAppListOfVDC_Out Out;
    Out.nRet = nRet;
    Out.count = count;
    if (count > 0)
    {
        for (size_t i = 0; i < count; i++)
        {
            tagVCloudVApp_Info_internal vCloudVAppInfo_internal;
            vCloudVAppInfo_internal.__tagToInternal(*vmList[i]);
            Out.vAppList.push_back(vCloudVAppInfo_internal);
        }
    }

    Out.ToString(ROOT_ITEM(taggetVAppListOfVDC_Out), outputData);

    return 0;
}

int NativeClassRPC::getVAppInOrg(const wchar_t* vdcId, const wchar_t* vAppName, VCloudVApp_Info* vAppInfo)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getVAppInOrg);

    taggetVAppInOrg_In In;
    In.vdcId = PTR_2_STR(vdcId);
    In.vAppName = PTR_2_STR(vAppName);
    In.ToString(ROOT_ITEM(taggetVAppInOrg_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetVAppInOrg_Out out;
        out.FromString(ROOT_ITEM(taggetVAppInOrg_Out), strOutputdata);
        out.vAppInfo.__tagFromInternal(*vAppInfo);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// caution!!
int NativeClassRPC::getVAppInOrg_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getVAppInOrg));

    taggetVAppInOrg_In In;
    In.FromString(ROOT_ITEM(taggetVAppInOrg_In), inputData);

    VCloudVApp_Info vCloudVAppInfo;
    int nRet = ::getVAppInOrg(
        pInst,
        const_cast<wchar_t *>(In.vdcId.c_str()),
        const_cast<wchar_t *>(In.vAppName.c_str()),
        &vCloudVAppInfo
        );

    taggetVAppInOrg_Out Out;
    Out.nRet = nRet;
    Out.vAppInfo.__tagFromInternal(vCloudVAppInfo);
    Out.ToString(ROOT_ITEM(taggetVAppInOrg_Out), outputData);

    return 0;
}

int NativeClassRPC::powerOnVApp(const wchar_t* vAppId)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(powerOnVApp);

    tagpowerOnVApp_In In = {
        PTR_2_STR(vAppId)
    };
    In.ToString(ROOT_ITEM(tagpowerOnVApp_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagpowerOnVApp_Out out;
        out.FromString(ROOT_ITEM(tagpowerOnVApp_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::powerOnVApp_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(powerOnVApp));

    tagpowerOnVApp_In In;
    In.FromString(ROOT_ITEM(tagpowerOnVApp_In), inputData);

    int nRet = ::powerOnVApp(
        pInst,
        const_cast<wchar_t *>(In.vAppId.c_str())
        );

    tagpowerOnVApp_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagpowerOnVApp_Out), outputData);

    return 0;
}

int NativeClassRPC::powerOffVApp(const wchar_t* vAppId)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(powerOffVApp);

    tagpowerOffVApp_In In = {
        PTR_2_STR(vAppId)
    };
    In.ToString(ROOT_ITEM(tagpowerOffVApp_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagpowerOffVApp_Out out;
        out.FromString(ROOT_ITEM(tagpowerOffVApp_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
// done, didnt' test it yet
int NativeClassRPC::powerOffVApp_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(powerOffVApp));

    tagpowerOffVApp_In In;
    In.FromString(ROOT_ITEM(tagpowerOffVApp_In), inputData);

    int nRet = ::powerOffVApp(
        pInst,
        const_cast<wchar_t *>(In.vAppId.c_str())
        );

    tagpowerOffVApp_Out Out;
    Out.nRet = nRet;

    Out.ToString(ROOT_ITEM(tagpowerOffVApp_Out), outputData);

    return 0;
}
//done need to check
int NativeClassRPC::getDatastore(const wchar_t* datastoreId, VCloudDatastore_Info* datastoreInfo)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getDatastore);

    taggetDatastore_In In = {
        PTR_2_STR(datastoreId)
    };
    In.ToString(ROOT_ITEM(taggetDatastore_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetDatastore_Out out;
        out.FromString(ROOT_ITEM(taggetDatastore_Out), strOutputdata);
        nRet = out.nRet;
        out.datastoreInfo.__tagFromInternal(*datastoreInfo);
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
//done need to check
int NativeClassRPC::getDatastore_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getDatastore));

    taggetDatastore_In In;
    In.FromString(ROOT_ITEM(taggetDatastore_In), inputData);

    VCloudDatastore_Info vCloudDatastoreInfo;
    int nRet = ::getDatastore(
        pInst,
        const_cast<wchar_t *>(In.datastoreId.c_str()),
        &vCloudDatastoreInfo
        );

    taggetDatastore_Out Out;
    Out.nRet = nRet;
    Out.datastoreInfo.__tagToInternal(vCloudDatastoreInfo);
    Out.ToString(ROOT_ITEM(taggetDatastore_Out), outputData);

    return 0;
}
//done need to check
int NativeClassRPC::getESXHostListOfVDC(const wchar_t* vdcId, VCloudESXHost_Info** esxHostList, int* count)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getESXHostListOfVDC);

    taggetESXHostListOfVDC_In In;
    In.vdcId = PTR_2_STR(vdcId);
    In.ToString(ROOT_ITEM(taggetESXHostListOfVDC_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetESXHostListOfVDC_Out out;
        out.FromString(ROOT_ITEM(taggetESXHostListOfVDC_Out), strOutputdata);
        nRet = out.nRet;
        const size_t c_uSize = out.count;
        *count = (int)c_uSize;
        if (c_uSize > 0)
        {
            esxHostList = new VCloudESXHost_Info*;
            *esxHostList = new VCloudESXHost_Info[c_uSize];
            for (size_t i = 0; i < c_uSize; i++)
            {
                tagVCloudESXHost_Info_internal vCloudVAppInfo_internal = out.esxHostList[i];
                vCloudVAppInfo_internal.__tagFromInternal(*esxHostList[i]);
            }
        }
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
//done need to check
int NativeClassRPC::getESXHostListOfVDC_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getESXHostListOfVDC));

    taggetESXHostListOfVDC_In In;
    In.FromString(ROOT_ITEM(taggetESXHostListOfVDC_In), inputData);

    VCloudESXHost_Info** esxHostList;
    int count;
    int nRet = ::getESXHostListOfVDC(
        pInst,
        const_cast<wchar_t *>(In.vdcId.c_str()),
        esxHostList,
        &count
        );

    taggetESXHostListOfVDC_Out Out;
    Out.nRet = nRet;
    Out.count = count;
    if (count > 0)
    {
        for (size_t i = 0; i < count; i++)
        {
            tagVCloudESXHost_Info_internal vCloudESXHostInfo_internal;
            vCloudESXHostInfo_internal.__tagToInternal(*esxHostList[i]);
            Out.esxHostList.push_back(vCloudESXHostInfo_internal);
        }
    }

    Out.ToString(ROOT_ITEM(taggetESXHostListOfVDC_Out), outputData);


    return 0;
}
//done need to check
int NativeClassRPC::getStorageProfileListOfVDC(const wchar_t* vdcId, VCloud_StorageProfile** storageProfileList, int* count)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(getStorageProfileListOfVDC);

    taggetStorageProfileListOfVDC_In In;
    In.vdcId = PTR_2_STR(vdcId);
    In.ToString(ROOT_ITEM(taggetStorageProfileListOfVDC_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        taggetStorageProfileListOfVDC_Out out;
        out.FromString(ROOT_ITEM(taggetStorageProfileListOfVDC_Out), strOutputdata);
        nRet = out.nRet;
        const size_t c_uSize = out.count;
        *count = (int)c_uSize;
        if (c_uSize > 0)
        {
            storageProfileList = new VCloud_StorageProfile*;
            *storageProfileList = new VCloud_StorageProfile[c_uSize];
            for (size_t i = 0; i < c_uSize; i++)
            {
                tagVCloud_StorageProfile_internal vCloudStorageProfile_internal = out.storageProfileList[i];
                vCloudStorageProfile_internal.__tagFromInternal(*storageProfileList[i]);
            }
        }
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
//done need to check
int NativeClassRPC::getStorageProfileListOfVDC_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(getStorageProfileListOfVDC));

    taggetStorageProfileListOfVDC_In In;
    In.FromString(ROOT_ITEM(taggetStorageProfileListOfVDC_In), inputData);

    VCloud_StorageProfile** storageProfileList;
    int count;
    int nRet = ::getStorageProfileListOfVDC(
        pInst,
        const_cast<wchar_t *>(In.vdcId.c_str()),
        storageProfileList,
        &count
        );

    taggetStorageProfileListOfVDC_Out Out;
    Out.nRet = nRet;
    Out.count = count;
    if (count > 0)
    {
        for (size_t i = 0; i < count; i++)
        {
            tagVCloud_StorageProfile_internal vCloudStorageProfile_internal;
            vCloudStorageProfile_internal.__tagToInternal(*storageProfileList[i]);
            Out.storageProfileList.push_back(vCloudStorageProfile_internal);
        }
    }

    Out.ToString(ROOT_ITEM(taggetStorageProfileListOfVDC_Out), outputData);

    return 0;
}
//done need to check
int NativeClassRPC::verifyVCloudInfo(VCloud_VerifyInfo* vCloudInfo)
{
    int nRet = 0;
    CString strInputData;
    CString strOutputdata;
    unsigned long outputVal = 0;
    unsigned long inputCMD = RPC_CMD(verifyVCloudInfo);

    tagverifyVCloudInfo_In In;
    In.vCloudInfo.__tagToInternal(*vCloudInfo);
    In.ToString(ROOT_ITEM(tagverifyVCloudInfo_In), strInputData);

    HRESULT hr = RPC_Invoke(inputCMD, strInputData, &outputVal, &strOutputdata);
    if (SUCCEEDED(hr))
    {
        tagverifyVCloudInfo_Out out;
        out.FromString(ROOT_ITEM(tagverifyVCloudInfo_Out), strOutputdata);
        nRet = out.nRet;
    }
    else
    {
        nRet = -1;
    }

    return nRet;
}
int NativeClassRPC::verifyVCloudInfo_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
    ASSERT_CMD(inputCmd, RPC_CMD(verifyVCloudInfo));

    tagverifyVCloudInfo_In In;
    In.FromString(ROOT_ITEM(tagverifyVCloudInfo_In), inputData);

    VCloud_VerifyInfo vCloudVerifyInfo;
    In.vCloudInfo.__tagFromInternal(vCloudVerifyInfo);

    int nRet = ::verifyVCloudInfo(
        pInst,
        &vCloudVerifyInfo
        );

    tagverifyVCloudInfo_Out Out;
    Out.nRet = nRet;
    Out.ToString(ROOT_ITEM(tagverifyVCloudInfo_Out), outputData);

    return 0;
}


HRESULT NativeClassRPC::RPC_Invoke(IN unsigned long inputCmd, IN const CString& inputData/* = L""*/, OUT unsigned long* outputValue/* = NULL*/, OUT CString* outputData /*= NULL*/)
{
    //EnterCriticalSection(&m_cs);

    //setinput

    //m_taskParam.inputCmd = inputCmd;



    //SetEvent(m_taskParam.ahInputHandles[INPUT_EVENT_INDEX]);

    //WaitForSingleObject(m_taskParam.hOutputEvent);

    //read output
    //outputValue = m_taskParam.outputValue;

	CComBSTR bstrOut;
	HRESULT hr = m_autoNativeRPCCtrl->RPC_Invoke(inputCmd, CComBSTR(inputData), outputValue, &bstrOut);
	if (FAILED(hr))
	{
		D2DDEBUGLOG(LL_ERR, hr, L"::Failed to invoke cmd %u, err=%u.", inputCmd, hr);
		//m_log.writeDebugLog(L"::Failed to invoke cmd %u, err=%u.", inputCmd, hr);			
	}
	else if (outputData)
	{
		*outputData = bstrOut;
	}

    //unLock();


	return hr;
}

INT NativeClassRPC::RPC_Proc(IN void* pInst, IN unsigned long inputCmd, IN const CString& inputData, OUT unsigned long* outputValue, OUT CString& outputData)
{
	INT nRet = 0;

	switch (inputCmd)
	{
	case RPC_CMD(connectToESX):
		nRet = connectToESX_Proc(pInst, inputCmd, inputData, outputValue, outputData);
		break;
	case RPC_CMD(getVMServerType):
		nRet = getVMServerType_Proc(pInst, inputCmd, inputData, outputValue, outputData);
		break;
	case RPC_CMD(checkVMServerLicense):
		nRet = checkVMServerLicense_Proc(pInst, inputCmd, inputData, outputValue, outputData);
		break;
    case RPC_CMD(checkVMServerInMaintainenceMode):
        nRet = checkVMServerInMaintainenceMode_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getESXVersion):
        nRet = getESXVersion_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getESXNumberOfProcessors):
        nRet = getESXNumberOfProcessors_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(disconnectESX):
        nRet = disconnectESX_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
	case RPC_CMD(getEsxNodeList):
		nRet = getEsxNodeList_Proc(pInst, inputCmd, inputData, outputValue, outputData);
		break;
    case RPC_CMD(getESXHostDataStoreList):
        nRet = getESXHostDataStoreList_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVMList):
        nRet = getVMList_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(checkResPool):
        nRet = checkResPool_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(setInstanceUUID):
        nRet = setInstanceUUID_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(checkDSBlockSize):
        nRet = checkDSBlockSize_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(takeSnapShot):
        nRet = takeSnapShot_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(checkandtakeSnapShot):
        nRet = checkandtakeSnapShot_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVMMoref):
        nRet = getVMMoref_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVMVersion):
        nRet = getVMVersion_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(revertSnapShot):
        nRet = revertSnapShot_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(removeSnapShot):
        nRet = removeSnapShot_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(removeSnapShotAsync):
        nRet = removeSnapShotAsync_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(removeSnapShotByName):
        nRet = removeSnapShotByName_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getsnapshotCTF):
        nRet = getsnapshotCTF_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getparentSnapshot):
        nRet = getparentSnapshot_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
        //2015-05-27
    case RPC_CMD(getVMDiskURLs):
        nRet = getVMDiskURLs_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVMSnapshotList):
        nRet = getVMSnapshotList_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getSnapShotDiskInfo):
        nRet = getSnapShotDiskInfo_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getSnapShotAdrDiskInfo):
        nRet = getSnapShotAdrDiskInfo_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVMAdrDiskInfo):
        nRet = getVMAdrDiskInfo_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVMInfo):
        nRet = getVMInfo_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
        //2015-06-15 begin
    case RPC_CMD(getVMInfoUnderDataCenter):
        nRet = getVMInfoUnderDataCenter_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
        //2015-06-15 end
    case RPC_CMD(deleteCTKFiles):
        nRet = deleteCTKFiles_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(hasSufficientPermission):
        nRet = hasSufficientPermission_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVMDataStoreDetails):
        nRet = getVMDataStoreDetails_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(rescanallHBA):
        nRet = rescanallHBA_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(addCloneDataStore):
        nRet = addCloneDataStore_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(destroyandDeleteClone):
        nRet = destroyandDeleteClone_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(createApplianceVM):
        nRet = createApplianceVM_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(createStndAloneApplianceVM):
        nRet = createStndAloneApplianceVM_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(deleteApplianceVM):
        nRet = deleteApplianceVM_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(attachDiskToVM):
        nRet = attachDiskToVM_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(detachDiskFromVM):
        nRet = detachDiskFromVM_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(isESXinCluster):
        nRet = isESXinCluster_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(checkandtakeApplianceSnapShot):
        nRet = checkandtakeApplianceSnapShot_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(removeSnapshotFromAppliance):
        nRet = removeSnapshotFromAppliance_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
        //2015-05-28
    case RPC_CMD(createVMwareVirtualMachine):
        nRet = createVMwareVirtualMachine_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getSnapshotConfigInfo):
        nRet = getSnapshotConfigInfo_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVSSwriterfiles):
        nRet = getVSSwriterfiles_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(setVMNVRamFile):
        nRet = setVMNVRamFile_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVMNVRAMFile):
        nRet = getVMNVRAMFile_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getUsedDiskBlocks):
        nRet = getUsedDiskBlocks_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(enableChangeBlockTracking):
        nRet = enableChangeBlockTracking_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(generateDiskBitMapForSnapshot):
        nRet = generateDiskBitMapForSnapshot_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(checkAndEnableChangeBlockTracking):
        nRet = checkAndEnableChangeBlockTracking_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getFile):
        nRet = getFile_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getDiskBitMap):
        nRet = getDiskBitMap_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(deleteDiskBitMap):
        nRet = deleteDiskBitMap_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(setFileStream):
        nRet = setFileStream_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    //case RPC_CMD(readFileStream):
    //    nRet = readFileStream_Proc(pInst, inputCmd, inputData, outputValue, outputData);
    //    break;

    case RPC_CMD(powerOnVM):
        nRet = powerOnVM_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(powerOffVM):
        nRet = powerOffVM_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVMPowerState):
        nRet = getVMPowerState_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVMToolsState):
        nRet = getVMToolsState_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVmdkFilePath):
        nRet = getVmdkFilePath_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(removeAllSnapshots):
        nRet = removeAllSnapshots_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(deleteVM):
        nRet = deleteVM_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(renameVM):
        nRet = renameVM_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
        //2015-05-29
    case RPC_CMD(revertSnapShotByID):
        nRet = revertSnapShotByID_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(removeSnapShotByID):
        nRet = removeSnapShotByID_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getSnapShotDiskInfoByID):
        nRet = getSnapShotDiskInfoByID_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(enableDiskUUIDForVM):
        nRet = enableDiskUUIDForVM_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(logUserEvent): //<sonmi01>2015-7-23 #shared jvm review and fix
		nRet = logUserEvent_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(isVMNameUsed):
        nRet = isVMNameUsed_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(VMHasSnapshot):
        nRet = VMHasSnapshot_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(SetvDSNetworkInfoEx):
        nRet = SetvDSNetworkInfoEx_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(GetESXVFlashResource):
        nRet = GetESXVFlashResource_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(GetVMVFlashReadCache):
        nRet = GetVMVFlashReadCache_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(cleanupHotAddedDisksAndConsolidateSnapshot):
        nRet = cleanupHotAddedDisksAndConsolidateSnapshot_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVMInfoByMoId):
        nRet = getVMInfoByMoId_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(SetVMDiskInfo):
        nRet = SetVMDiskInfo_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getESXHostListByDatastoreMoRef):
        nRet = getESXHostListByDatastoreMoRef_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(setVMCpuMemory):
        nRet = setVMCpuMemory_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getThumbprint):
        nRet = getThumbprint_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
        //2015-06-01
    case RPC_CMD(connectVCloud):
        nRet = connectVCloud_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(disconnectVCloud):
        nRet = disconnectVCloud_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(saveVAppInfo):
        nRet = saveVAppInfo_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVMListOfVApp):
        nRet = getVMListOfVApp_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(createVApp):
        nRet = createVApp_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(importVM):
        nRet = importVM_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(deleteVApp):
        nRet = deleteVApp_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
        //2015-06-02
    case RPC_CMD(renameVAppEx):
        nRet = renameVAppEx_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVApp):
        nRet = getVApp_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVAppListOfVDC):
        nRet = getVAppListOfVDC_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getVAppInOrg):
        nRet = getVAppInOrg_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(powerOnVApp):
        nRet = powerOnVApp_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(powerOffVApp):
        nRet = powerOffVApp_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getDatastore):
        nRet = getDatastore_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getESXHostListOfVDC):
        nRet = getESXHostListOfVDC_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(getStorageProfileListOfVDC):
        nRet = getStorageProfileListOfVDC_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
    case RPC_CMD(verifyVCloudInfo):
        nRet = verifyVCloudInfo_Proc(pInst, inputCmd, inputData, outputValue, outputData);
        break;
	case RPC_CMD(consolidateVMDisks):
		nRet = consolidateVMDisks_Proc(pInst, inputCmd, inputData, outputValue, outputData);
		break;
	case RPC_CMD(isESXunderVC):
		nRet = isESXunderVC_Proc(pInst, inputCmd, inputData, outputValue, outputData);
		break;
	default:
		break;
	}

	return nRet;
}

//Com server will invoke this function
////////////////////////////////////////////////////////////////////////////////////////////////////////////
V2PNF_API int V2PNativeFacadeRPC_Proc(IN void* pInst, IN unsigned long inputCmd, IN const wstring& inputData, OUT unsigned long* outputValue, OUT wstring& outputData)
{
	CString stroutputData;
	INT nRet = NativeClassRPC::RPC_Proc(pInst, inputCmd, inputData.c_str(), outputValue, stroutputData);
	if (0 == nRet)
	{
		outputData = stroutputData.GetString();
	}

	return nRet;
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////
BOOL V2PNFGlobals::m_isSharedJVM = FALSE; //<sonmi01>2015-9-6 ###???

