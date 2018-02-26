// ASRemote.cpp : header file 
// 
// This file contains the declaration of the global variable/functions 
// used in the project
#ifndef _WIN95AGENT

#ifndef	__ASREMOTE_H
#define __ASREMOTE_H

#include <lm.h>

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CASRemote
#else
class __declspec(dllimport) CASRemote
#endif
{
private:
	TCHAR m_szRemoteMachine[MAX_PATH];

	TCHAR m_szUserName[_MAX_PATH];

	TCHAR m_szPassword[25];

	BOOL m_bIsRemoteMachine;

	BOOL m_bConnect;

	DWORD m_dwShareCount;

	DWORD m_dwNetShareSize;

    NETRESOURCE m_NetResource;

    NETRESOURCE m_NetShare[1024];

	SHARE_INFO_1* m_ShareInfo; 

public:
	HKEY m_hMachineRegKey;

private:
	void GetLocalMachineName();

public:
	CASRemote (LPTSTR lpMahineName = NULL,
			   LPTSTR lpUserName = _T("Administrator"),
			   LPTSTR lpPassword = _T(""));

	~CASRemote ();

	DWORD Connect (LPTSTR lpShare = _T("ADMIN$"), BOOL bDisconnect = FALSE);

	void Disconnect();

	HKEY GetMachineRegKey();

	BOOL EnumerateShares(CComboBox* pShCombo);

	BOOL IsConnect(){return m_bConnect;}

	BOOL IsRemoteMachine(){return m_bIsRemoteMachine;}

	LPTSTR	MachineName(){return m_szRemoteMachine;}

	LPTSTR  UserName(){return m_szUserName;}

	LPTSTR  Password(){return m_szPassword;}

	DWORD GetShareCount(){return m_dwShareCount;}

	SHARE_INFO_1* GetAllSahres(){return m_ShareInfo;}

	static  BOOL IsLocalMachine(LPTSTR lpMachineName);
};
#endif  //__ASREMOTE_H

#endif //_WIN95AGENT


