#pragma once
 
class ComItemImpl: public IComItem
{
	ComItemImpl(void);
	virtual ~ComItemImpl(void);
public:
	CA_RELEASE_UNDEL();
	CA_BEGIN_INTERFACE_MAP()
		CA_INTERFACE(IComItem)
	CA_END_INTERFACE_MAP()
public:
	static ComItemImpl * GetInstance();
	virtual HRESULT Init(IMSGCallback * pvCBK);
	virtual HRESULT UnInit();
	virtual void Run();

protected:
	static DWORD  WINAPI _proc(void * pParam);
	HRESULT ExitPipeThread();
	BOOL IsExitCMD(BYTE * pbuf, DWORD dwbufsize);

protected:
	static ComItemImpl g_objCOMItem;
	IMSGCallback *m_pvCallback;
	HANDLE m_pipe;
	HANDLE m_hThread;
	volatile BOOL m_isQuit;
	volatile BOOL m_isReady;
	BYTE *	m_pBuffer;

};
