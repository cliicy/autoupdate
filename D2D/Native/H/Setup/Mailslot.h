/////////////////////////////////////////////////////////////////////////////
// Mailslot.h		CRemoteMailslot Interface
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.

#ifndef _MAILSLOT
#define _MAILSLOT

#define MAILSLOT_NAME_FORMAT		_T("\\\\%s\\mailslot\\Brightstor_Setup_Status")

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CRemoteMailslot
#else
class __declspec(dllimport) CRemoteMailslot
#endif
{
private:
    CString m_sHost;

public:
    // Constructor
    CRemoteMailslot(LPCTSTR lpszHost);

    // Operations
    BOOL SendStatus(UINT nPhase, UINT nStatus, DWORD dwErrorCode);
    static BOOL SendStatus(LPCTSTR lpszHostServer, UINT nPhase, UINT nStatus, DWORD dwErrorCode);

    BOOL SendStatus(UINT nPhase, UINT nStatus, DWORD dwErrorCode, LPCTSTR lpszStatus);
    static BOOL SendStatus(LPCTSTR lpszHostServer, UINT nPhase, UINT nStatus, DWORD dwErrorCode, LPCTSTR lpszStatus);
	
	//Added by Wang Chong, 2002/1/30 
	//This function is used to communicate between the MasterSetup and product's setup program. 
	static BOOL SendStatus(LPCTSTR lpszHostServer, UINT nPhase, UINT nStatus, DWORD dwErrorCode, LPCTSTR lpszProdName, LPCTSTR lpszMsg);

	//Added by Wang Chong, 2002/1/30 This fuction is only used by master setup.
	static BOOL SendStatus(LPCTSTR lpszHostServer, UINT nPhase, UINT nStatus, DWORD dwErrorCode, LPCTSTR lpszProdName, LPCTSTR lpszMsg, UINT nTotalInst, UINT nCurProdInst);

};
#endif
