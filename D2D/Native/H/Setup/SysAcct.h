/////////////////////////////////////////////////////////////////////////////
// SysAcct.h		CSystemAccount Interface
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.

#ifndef _WIN95AGENT

#ifndef _SYSACCT
#define _SYSACCT

#ifdef __cplusplus
extern "C"
{
#endif
#include <ntdef.h>
#include <ntlsa.h>
#ifdef __cplusplus
}
#endif

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CSystemAccount
#else
class __declspec(dllimport) CSystemAccount
#endif
{
private:
    CString m_sComputer;
    CString m_sDomain;
    CString m_sUser;
    CString m_sPassword;
	LPWSTR  m_lpwszPrimaryDomainController;
    BOOL    m_bLocal;


public:
    // Constructor/destructor
    CSystemAccount(LPCTSTR lpszComputer, LPCTSTR lpszDomain, LPCTSTR lpszUser, LPCTSTR lpszPassword);
    ~CSystemAccount();

// Add by calvin chen
	BOOL AssignGroups();

    BOOL Create();
    BOOL Exists();
    BOOL ValidPassword();
    BOOL Delete();
    BOOL Register();

	static BOOL AssignGroups(LPCWSTR lpwszPrimaryDomainController, LPCTSTR lpszComputer, LPCTSTR lpszDomain, LPCTSTR lpszUser, LPCTSTR lpszPassword);

    static BOOL Create(LPCWSTR lpwszPrimaryDomainController, LPCTSTR lpszComputer, LPCTSTR lpszDomain, LPCTSTR lpszUser, LPCTSTR lpszPassword);
    static BOOL Exists(LPCWSTR lpwszPrimaryDomainController, LPCTSTR lpszUser);
    static BOOL ValidPassword(LPCWSTR lpwszPrimaryDomainController, LPCTSTR lpszMachine, LPCTSTR lpszDomain, LPCTSTR lpszUser, LPCTSTR lpszPassword);
    static BOOL Delete(LPCWSTR lpwszPrimaryDomainController, LPCTSTR lpszUser);
    static BOOL Register(LPCTSTR lpszComputer, LPCTSTR lpszDomain, LPCTSTR lpszUser, LPCTSTR lpszPassword);

protected:
    static BOOL AddMemberToGroup(UINT rid, LPCWSTR lpwszPrimaryDomainController, PSID pSID);
    static BOOL GetGroupName(UINT rid, LPCWSTR lpwszPrimaryDomainController, LPTSTR lpszGroup, UINT groupLimit, LPTSTR lpszDomain, UINT domainLimit);
    static BOOL AddPrivilege(LSA_HANDLE hAccount, LPCTSTR lpszPrimaryDomainController, LPCTSTR lpszPrivName);
    static BOOL AddARCserveUserPrivileges(LPCWSTR lpwszPrimaryDomainController);

};
#endif

#endif	// _WIN95AGENT
