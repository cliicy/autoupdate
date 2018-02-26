// CNativeCmdCtrl.h : Declaration of the CCNativeCmdCtrl

#pragma once
#include "resource.h"       // main symbols

#include <map>
#include "V2PNativeFacadeSrv_i.h"
#include "..\V2PNativeFacade\V2PNativeFacade.h"


using namespace ATL;
using namespace std;

// CCNativeCmdCtrl

class ATL_NO_VTABLE CCNativeCmdCtrl :
	public CComObjectRootEx<CComMultiThreadModel>,
	public CComCoClass<CCNativeCmdCtrl, &CLSID_CNativeCmdCtrl>,
	public ICNativeCmdCtrl
{
public:
	CCNativeCmdCtrl():
		m_hV2PInstant(NULL)
	{
	}

DECLARE_REGISTRY_RESOURCEID(IDR_CNATIVECMDCTRL)


BEGIN_COM_MAP(CCNativeCmdCtrl)
	COM_INTERFACE_ENTRY(ICNativeCmdCtrl)
END_COM_MAP()



	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
		return S_OK;
	}

	void FinalRelease()
	{
	}


public:
	virtual HRESULT STDMETHODCALLTYPE Initialize(void);

	virtual HRESULT STDMETHODCALLTYPE UnInitialize(void);

	virtual HRESULT STDMETHODCALLTYPE RPC_Invoke(
		/* [in] */ unsigned long inputCmd,
		/* [in] */ BSTR inputData,
		/* [out] */ unsigned long *outputValue,
		/* [out] */ BSTR *outputData);

	virtual HRESULT STDMETHODCALLTYPE GetServerInfo(
		/* [out] */ unsigned long *processId,
		/* [out] */ unsigned long *threadId);

	virtual HRESULT STDMETHODCALLTYPE SetClientInfo(
		/* [in] */ LONG cid,
		/* [in] */ BSTR cinfo);

    virtual HRESULT STDMETHODCALLTYPE readStream(
        /* [in] */ long long llOffset,
        /* [in] */ int lengthToRead,
        /* [size_is][out] */ byte *stream,
        /* [out] */ int *lengthRead,
        /* [out] */ int *ret);

private:
	V2PNF_HANDLE m_hV2PInstant;

//global var;
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public:
	static HRESULT InitV2PNativeSrv();
	static HRESULT UnInitV2PNativeSrv();

private:
	static void * m_pJavaVM;

	//V2PNF_API V2PNF_HANDLE V2PNativeFacadeInit(const char* jarList = NULL, BOOL bOldVer=FALSE);
	typedef V2PNF_HANDLE (*pfn_V2PNativeFacadeInit)(const char* jarList, BOOL bOldVer);
	static pfn_V2PNativeFacadeInit mfn_V2PNativeFacadeInit;

	typedef V2PNF_HANDLE(*pfn_V2PNativeFacadeInit_Ex)(void* pJVM, const char* jarList, BOOL bOldVer);
	static pfn_V2PNativeFacadeInit_Ex mfn_V2PNativeFacadeInit_Ex;

	//V2PNF_API void V2PNativeFacadeExit(V2PNF_HANDLE pHandle);
	typedef void (*pfn_V2PNativeFacadeExit)(V2PNF_HANDLE pHandle);
	static pfn_V2PNativeFacadeExit mfn_V2PNativeFacadeExit;
	
	typedef int(*pfn_V2PNativeFacadeRPC_Proc)(IN void* pInst, IN unsigned long inputCmd, IN const wstring& inputData, OUT unsigned long* outputValue, OUT wstring& outputData);
	static pfn_V2PNativeFacadeRPC_Proc mfn_V2PNativeFacadeRPC_Proc;

	typedef VOID (*pfn_V2PNFSet_isSharedJVM)(BOOL isSharedJVM); //<sonmi01>2015-9-6 ###???
	static pfn_V2PNFSet_isSharedJVM mpfn_V2PNFSet_isSharedJVM;

	typedef void* (*pfn_getJVM)(V2PNF_HANDLE pHandle);
	static pfn_getJVM mfn_getJVM;

    typedef int(*pfn_readFileStream)(BYTE* pBuff, int length, int* bytesRead, __int64 offSet, V2PNF_HANDLE pHandle);
    static pfn_readFileStream mfn_readFileStream;

	static HMODULE m_hVMAPILib; /***INTERNAL***//***CLEANUP***/
	static V2PNF_HANDLE m_hV2PNF; /***INTERNAL***/ /***CLEANUP***/
};

OBJECT_ENTRY_AUTO(__uuidof(CNativeCmdCtrl), CCNativeCmdCtrl)
