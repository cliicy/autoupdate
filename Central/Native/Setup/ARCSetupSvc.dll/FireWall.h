#pragma once
#include "stdafx.h"
#include "firewallsetup.h"

class CFirewall
{
public:
	CFirewall();
	~CFirewall();

	//get the error message
	void GetErrorMessage(LPTSTR lpMsg,DWORD ccBuffer);

	//Initialize the firewall profile currently in effect.
	HRESULT WindowsFirewallInitialize();

	//Get the current state of the firewall.
	HRESULT WindowsFirewallIsOn(BOOL &bOn);

	//Add the authorized application collection.
	HRESULT WindowsFirewallAddApp(CString strAppFile,CString strName = _T(""));

	//Add port to list of globally open ports.
	HRESULT WindowsFirewallAddPort(UINT nPort,CString strName = _T(""), NET_FW_IP_PROTOCOL ipProtocol = NET_FW_IP_PROTOCOL_TCP);

	//Remove the authorized application collection.
	HRESULT WindowsFirewallRemoveApp(CString strAppFile);

	//Remove port to list of globally open ports.
	HRESULT WindowsFirewallRemovePort(UINT nPort, NET_FW_IP_PROTOCOL ipProtocol = NET_FW_IP_PROTOCOL_TCP);

	//check if the application is added
	HRESULT WindowsFirewallAppIsEnabled(CString strAppFile,BOOL &bEnabled);

	//check if the port is added
	HRESULT WindowsFirewallPortIsEnabled(UINT nPort, NET_FW_IP_PROTOCOL ipProtocol,BOOL &bEnabled);

	//add the following API for windows7 and Windows 2008 R2 above

	//check if the WFCOM support
	BOOL IsWFCOMPermited();

	struct ProfileMapElement 
	{
		NET_FW_PROFILE_TYPE2 Id;
		LPCWSTR Name;
	};
	//Initialize the firewall profile currently in effect.

	HRESULT WFCOMInitialize();

	//Add the authorized application collection.
	HRESULT WFCOMAddAppFirewallRule(CString strAppFile,CString strName,long lProfileTypesBitmask = NET_FW_PROFILE2_PRIVATE,CString strDescription = _T(""));

	//check if the rule enable
	HRESULT WFCOMAppFirewallRuleEnabled(CString strAppFile,CString strName,BOOL& bEnabled,long lProfileTypesBitmask = NET_FW_PROFILE2_PRIVATE);

	//check if the rule exist
	BOOL IsWFCOMAppFirewallRuleExist(CString strAppFile,CString strName);

	//Remove the authorized application collection.
	HRESULT WFCOMRemoveAppFirewallRule(CString strAppFile,CString strName);
	
	HRESULT WFCOMGetCurrentProfileType(long &CurrentProfilesType);
	
	//end new API

private:
	CString m_strErrMsg;
	HRESULT m_hr;
	HRESULT m_comInit;
	INetFwProfile* m_fwProfile;	

	//add new items for windows7 and Windows 2008 R2 above
	INetFwPolicy2* m_pNetFwPolicy2;
	INetFwRule* m_pNetFwRule;
	//end new items

};
