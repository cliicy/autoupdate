#ifndef __CA_D2D_JNI_CALLBACK_id2dproxydef__h
#define __CA_D2D_JNI_CALLBACK_id2dproxydef__h
#pragma once 
#include "iunknown.h"
#include <comdef.h>
#include "jni.h"

#define  XIN
#define  XOUT
#define  XINOUT

class IComItem;

class __declspec(uuid("{0864705C-07D5-42f9-8798-802C241AD234}")) ID2DProxyServer;
class ID2DProxyServer: public IUnknown
{
public:
	virtual HRESULT Init(JNIEnv *pJnienv) =0;
	virtual HRESULT UnInit() =0;
	virtual HRESULT Run() =0;
};

class __declspec(uuid("{04AA3DB0-BD2C-4042-997B-DFA5D34DE40A}")) ITaskItem;
class ITaskItem: public IUnknown
{
protected:
	ITaskItem()
	{
		m_hEvent =NULL;
		m_RetVal =S_OK;
		m_pJnienv =NULL;
		m_pvCOMIns =NULL;
	}

public:
	virtual HRESULT DoWork() =0;
	virtual HRESULT SetJNIItem(JNIEnv *pJnienv)
	{
		if(NULL== pJnienv)
		{
			return E_INVALIDARG;
		}
		m_pJnienv = pJnienv;
		return S_OK;
	}
	virtual HRESULT SetCOMItem(IComItem *pvCOM)
	{
		if (NULL ==pvCOM)
		{
			return E_INVALIDARG;
		}

		m_pvCOMIns = pvCOM;
		return S_OK;
	}
	virtual HRESULT SetNotifier(HANDLE hEvent)
	{
		m_RetVal=0;
		m_hEvent = hEvent;
		return S_OK;
	}

	virtual void Notify()
	{
		if(m_hEvent)
		{ 
			SetEvent(m_hEvent);
			m_hEvent = NULL;
		}
	}

	virtual HRESULT GetRetValue()
	{
		return m_RetVal;
	}

protected:
	HANDLE  m_hEvent;
	HRESULT  m_RetVal;
	JNIEnv * m_pJnienv;
	IComItem * m_pvCOMIns;

};

class __declspec(uuid("{A508B890-34CA-4b93-A429-B78D27777498}")) IMSGCallback;
class IMSGCallback: public IUnknown
{
public:
	virtual HRESULT OnMessage( XIN BYTE * pinBUF, XIN DWORD ninSIZE, XOUT BYTE * poutBUF, XINOUT DWORD & noutSIZE) =0;
};

class __declspec(uuid("{A508B890-AAAA-4b93-A429-B78D27777498}")) IComItem;
class IComItem: public IUnknown
{
public:
	virtual HRESULT Init(IMSGCallback * pvCBK) =0;
	virtual HRESULT UnInit() =0;
	virtual void Run() =0;
};

#define	D2D_PROXY_CMD_PIPE_EXIT	"D2D_PROXY_EXIT_{EE618D59-WANMI12-9527-9528-07ED12CE753F}"
/*
class IJNISendMail: public IUnknown
{
public:
	virtual HRESULT Init(JNIEnv  * pJNIenv) =0;
	virtual HRESULT UnInit() =0;
	virtual HRESULT MergeFailed() =0;

};

*/
#endif//__CA_D2D_JNI_CALLBACK_id2dCallbackdef__h