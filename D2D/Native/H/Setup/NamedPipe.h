#ifndef __NAMEDPIPE_H__
#define __NAMEDPIPE_H__
#include <afxmt.h>
#define PIPENAME_FORMAT_STR _T("\\\\%s\\PIPE\\%s")
#define WM_RECV_PIPE_MESSAGE	WM_USER+110

#define PIPE_MUTEXT   _T("ARCSERVE_SETUP_PIPE_MUTEX")

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CNamedPipe
#else
class __declspec(dllimport) CNamedPipe
#endif
{
public:
	CNamedPipe();
	~CNamedPipe();

	// attributes
	void SetSize(INT);
	INT GetSize() const;
	HANDLE GetInputPipeHandle() const;	
	void GetClientName(LPTSTR lpClient);
	void SetClientName(LPCTSTR lpClient);
	// operations
	BOOL Initialize(BOOL,LPCTSTR,LPCTSTR);
	void Listen( LPCTSTR lpClient, DWORD dwThreadID);
	BOOL Write(LPCTSTR lpData);
	DWORD m_dwTargetThreadID; 
	CString m_strPipeName;	
	HANDLE m_hOutputPipeHandle;
	HANDLE m_hInputPipeHandle;
	SECURITY_ATTRIBUTES m_saPipe;
	CMutex m_PipeMutex;
	HANDLE m_hKillPipeEvent;
	HANDLE m_hPipeKilledNotify;
	HANDLE  m_hListenThread;
	inline void SetInUse(BOOL bInUse){m_bInUse = bInUse;};
	inline BOOL IsInUse(){return m_bInUse;};
	void GetRedirectHost(LPTSTR lpRedirectHost);
	void SetRedirectHost(LPCTSTR lpHost);
	inline BOOL NeedRedirect(){return m_bRedirect;};
	inline void SetNeedRedirect(BOOL bRedirect){m_bRedirect = bRedirect;};
private:
	INT m_nSize;
	BOOL m_nIsServer;
	CString m_strServerName;
	CString m_strClientName;
	BOOL m_bInUse;
	CString m_strRedirectHost;
	CString m_strRedirectPipeName;
	BOOL m_bRedirect;
};

#endif //__NAMEDPIPE_H__