#include "stdafx.h"
#include "Firewall.h"
#define STRING_BUFFER_SIZE  500 

#define KERNEL32_DLL					_T("kernel32.dll")
BOOL typedef (WINAPI * pfn_GetProductInfo)(DWORD, DWORD, DWORD, DWORD, PDWORD);

CFirewall::CFirewall()
{
	m_hr = S_OK;
	m_comInit = E_FAIL;
	m_fwProfile = NULL;
	m_pNetFwPolicy2 = NULL;
	m_pNetFwRule = NULL;
	m_strErrMsg.Empty();
}

CFirewall::~CFirewall()
{
	// Release the firewall profile.
	if (m_fwProfile != NULL)
	{
		m_fwProfile->Release();
		m_fwProfile = NULL;
	}

	if (m_pNetFwPolicy2 != NULL)
	{
		m_pNetFwPolicy2->Release();
		m_pNetFwPolicy2 = NULL;
	}

	if (m_pNetFwRule != NULL)
	{
		m_pNetFwRule->Release();
		m_pNetFwRule = NULL;
	}

	// Uninitialize COM.
	if (SUCCEEDED(m_comInit))
	{
		CoUninitialize();
	}
}

HRESULT CFirewall::WindowsFirewallInitialize()
{
	INetFwMgr* fwMgr = NULL;
	INetFwPolicy* fwPolicy = NULL;

	m_strErrMsg.Empty();

	// Initialize COM.
	m_comInit = CoInitializeEx(
		0,
		COINIT_APARTMENTTHREADED | COINIT_DISABLE_OLE1DDE
		);

	// Ignore RPC_E_CHANGED_MODE; this just means that COM has already been
	// initialized with a different mode. Since we don't care what the mode is,
	// we'll just use the existing mode.
	if (m_comInit != RPC_E_CHANGED_MODE)
	{
		m_hr = m_comInit;
		if (FAILED(m_hr))
		{
			//CoInitializeEx failed
			m_strErrMsg.Format(_T("WindowsFirewallInitialize: CoInitializeEx failed: 0x%08lx"), m_hr);
			return m_hr;
		}
		else
		{
			//initial ok
			m_hr = S_OK;
		}
	}

	// Create an instance of the firewall settings manager.
	m_hr = CoCreateInstance(
		__uuidof(NetFwMgr),
		NULL,
		CLSCTX_INPROC_SERVER,
		__uuidof(INetFwMgr),
		(void**)&fwMgr
		);
	if (FAILED(m_hr))
	{
		//CoCreateInstance failed
		m_strErrMsg.Format(_T("WindowsFirewallInitialize: CoCreateInstance failed: 0x%08lx"), m_hr);
		return m_hr;
	}

	// Retrieve the local firewall policy.
	m_hr = fwMgr->get_LocalPolicy(&fwPolicy);
	if (FAILED(m_hr))
	{
		//get_LocalPolicy failed
		m_strErrMsg.Format(_T("get_LocalPolicy failed: 0x%08lx"), m_hr);
		return m_hr;
	}

	// Retrieve the firewall profile currently in effect.
	m_hr = fwPolicy->get_CurrentProfile(&m_fwProfile);
	if (FAILED(m_hr))
	{
		m_strErrMsg.Format(_T("get_CurrentProfile failed: 0x%08lx, root reason seems that: Windows Firewall/Internet Connection Sharing (ICS) service do NOT start,Or Firewall don't exist!"), m_hr);
		return m_hr;
	}

	// Release the local firewall policy.
	if (fwPolicy != NULL)
	{
		fwPolicy->Release();
	}

	// Release the firewall settings manager.
	if (fwMgr != NULL)
	{
		fwMgr->Release();
	}

	return m_hr;
}
HRESULT CFirewall::WindowsFirewallIsOn(BOOL &bOn)
{
	VARIANT_BOOL fwEnabled;

	if (m_fwProfile == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WindowsFirewallIsOn: you do not initialize the firewall profile"));
		return m_hr;
	}
	
	bOn = FALSE;

	// Get the current state of the firewall.
	m_hr = m_fwProfile->get_FirewallEnabled(&fwEnabled);
	if (FAILED(m_hr))
	{
		m_strErrMsg.Format(_T("WindowsFirewallIsOn: get_FirewallEnabled failed: 0x%08lx"), m_hr);
		return m_hr;
	}

	// Check to see if the firewall is on.
	if (fwEnabled != VARIANT_FALSE)
	{
		//The firewall is on
		bOn = TRUE;
	}
	else
	{
	   //The firewall is off
		m_strErrMsg = _T("WindowsFirewallIsOn: The firewall is off");
	}

	return m_hr;	
}
HRESULT CFirewall::WindowsFirewallAddApp(CString strAppFile,CString strName)
{
	BOOL fwAppEnabled = FALSE;
	BSTR fwBstrName = NULL;
	BSTR fwBstrProcessImageFileName = NULL;
	INetFwAuthorizedApplication* fwApp = NULL;
	INetFwAuthorizedApplications* fwApps = NULL;

	if (m_fwProfile == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WindowsFirewallAddApp: you do not initialize the firewall profile"));
		return m_hr;
	}

	if (strAppFile.IsEmpty())
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WindowsFirewallAddApp: Application full path is null"));
		return m_hr;
	}

	if (strName.IsEmpty())
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WindowsFirewallAddApp: Application file name is null"));
		return m_hr;
	}

	// First check to see if the application is already authorized.
	m_hr = WindowsFirewallAppIsEnabled(
		strAppFile,
		fwAppEnabled
		);
	if (FAILED(m_hr))
	{
		m_strErrMsg.Format(_T("WindowsFirewallAddApp: WindowsFirewallAppIsEnabled failed: 0x%08lx"), m_hr);
		goto Cleanup;
	}

	// Only add the application if it isn't already authorized.
	if (!fwAppEnabled)
	{
		// Retrieve the authorized application collection.
		m_hr = m_fwProfile->get_AuthorizedApplications(&fwApps);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddApp: get_AuthorizedApplications failed: 0x%08lx"), m_hr);
			goto Cleanup;
		}

		// Create an instance of an authorized application.
		m_hr = CoCreateInstance(
			__uuidof(NetFwAuthorizedApplication),
			NULL,
			CLSCTX_INPROC_SERVER,
			__uuidof(INetFwAuthorizedApplication),
			(void**)&fwApp
			);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddApp: CoCreateInstance failed: 0x%08lx"), m_hr);
			goto Cleanup;
		}

		// Allocate a BSTR for the process image file name.
		fwBstrProcessImageFileName = strAppFile.AllocSysString();
		if (SysStringLen(fwBstrProcessImageFileName) == 0)
		{
			m_hr = E_OUTOFMEMORY;
			m_strErrMsg.Format(_T("WindowsFirewallAddApp: fail to Allocate a BSTR for the process image file name(%s): 0x%08lx"), strAppFile,m_hr);
			goto Cleanup;
		}

		// Set the process image file name.
		m_hr = fwApp->put_ProcessImageFileName(fwBstrProcessImageFileName);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddApp: fail to Set the process image file name: 0x%08lx"), m_hr);
			goto Cleanup;
		}

		// Allocate a BSTR for the application friendly name.
		fwBstrName = strName.AllocSysString();
		if (SysStringLen(fwBstrName) == 0)
		{
			m_hr = E_OUTOFMEMORY;
			m_strErrMsg.Format(_T("WindowsFirewallAddApp: fail to Allocate a BSTR for the application(%s): 0x%08lx"),strName, m_hr);
			goto Cleanup;
		}

		// Set the application friendly name.
		m_hr = fwApp->put_Name(fwBstrName);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddApp: failed to Set the application(%s): 0x%08lx"),strName, m_hr);
			goto Cleanup;
		}

		// Add the application to the collection.
		m_hr = fwApps->Add(fwApp);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddApp: fail to Add the application(%s) to the collection: 0x%08lx"),strAppFile,m_hr);
			goto Cleanup;
		}
	}

Cleanup:

	// Free the BSTRs.
	SysFreeString(fwBstrName);
	SysFreeString(fwBstrProcessImageFileName);

	// Release the authorized application instance.
	if (fwApp != NULL)
	{
		fwApp->Release();
	}

	// Release the authorized application collection.
	if (fwApps != NULL)
	{
		fwApps->Release();
	}

	return m_hr;
}

//Remove the authorized application collection.
HRESULT CFirewall::WindowsFirewallRemoveApp(CString strAppFile)
{
	BOOL fwAppEnabled = FALSE;
	BSTR fwBstrProcessImageFileName = NULL;
	INetFwAuthorizedApplication* fwApp = NULL;
	INetFwAuthorizedApplications* fwApps = NULL;

	if (m_fwProfile == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WindowsFirewallAddApp: you do not initialize the firewall profile"));
		return m_hr;
	}

	if (strAppFile.IsEmpty())
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WindowsFirewallAddApp: Application full path is null"));
		return m_hr;
	}

	// First check to see if the application is already authorized.
	m_hr = WindowsFirewallAppIsEnabled(
		strAppFile,
		fwAppEnabled
		);
	if (FAILED(m_hr))
	{
		m_strErrMsg.Format(_T("WindowsFirewallAddApp: WindowsFirewallAppIsEnabled failed: 0x%08lx"), m_hr);
		goto Cleanup;
	}

	// Only remove the application if it isn't already authorized.
	if (fwAppEnabled)
	{
		// Retrieve the authorized application collection.
		m_hr = m_fwProfile->get_AuthorizedApplications(&fwApps);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddApp: get_AuthorizedApplications failed: 0x%08lx"), m_hr);
			goto Cleanup;
		}

		// Create an instance of an authorized application.
		m_hr = CoCreateInstance(
			__uuidof(NetFwAuthorizedApplication),
			NULL,
			CLSCTX_INPROC_SERVER,
			__uuidof(INetFwAuthorizedApplication),
			(void**)&fwApp
			);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddApp: CoCreateInstance failed: 0x%08lx"), m_hr);
			goto Cleanup;
		}

		// Allocate a BSTR for the process image file name.
		fwBstrProcessImageFileName = strAppFile.AllocSysString();
		if (SysStringLen(fwBstrProcessImageFileName) == 0)
		{
			m_hr = E_OUTOFMEMORY;
			m_strErrMsg.Format(_T("WindowsFirewallAddApp: fail to Allocate a BSTR for the process image file(%s): 0x%08lx"),strAppFile, m_hr);
			goto Cleanup;
		}

		m_hr = fwApps->Remove(fwBstrProcessImageFileName);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddApp: fail to Remove the application(%s) to the collection: 0x%08lx"), strAppFile,m_hr);
			goto Cleanup;
		}
	}

Cleanup:

	// Free the BSTRs.
	SysFreeString(fwBstrProcessImageFileName);

	// Release the authorized application instance.
	if (fwApp != NULL)
	{
		fwApp->Release();
	}

	// Release the authorized application collection.
	if (fwApps != NULL)
	{
		fwApps->Release();
	}

	return m_hr;
}

//Add port to list of globally open ports.
HRESULT CFirewall::WindowsFirewallAddPort(UINT nPort,CString strName, NET_FW_IP_PROTOCOL ipProtocol /*= NET_FW_IP_PROTOCOL_TCP*/)
{
	BOOL fwPortEnabled = FALSE;
	BSTR fwBstrName = NULL;
	INetFwOpenPort* fwOpenPort = NULL;
	INetFwOpenPorts* fwOpenPorts = NULL;

	if (m_fwProfile == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WindowsFirewallAddPort: you do not initialize the firewall profile"));
		goto Cleanup;
	}

	if (strName.IsEmpty())
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WindowsFirewallAddPort: Application name is null"));
		goto Cleanup;
	}

	// First check to see if the port is already added.
	m_hr = WindowsFirewallPortIsEnabled(
		nPort,
		ipProtocol,
		fwPortEnabled
		);
	if (FAILED(m_hr))
	{
		m_strErrMsg.Format(_T("WindowsFirewallAddPort: fail to check first to see if the port is already added : 0x%08lx"),m_hr);
		goto Cleanup;
	}

	// Only add the port if it isn't already added.
	if (!fwPortEnabled)
	{
		// Retrieve the collection of globally open ports.
		m_hr = m_fwProfile->get_GloballyOpenPorts(&fwOpenPorts);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddPort: fail to Retrieve the collection of globally open ports : 0x%08lx"),m_hr);
			goto Cleanup;
		}

		// Create an instance of an open port.
		m_hr = CoCreateInstance(
			__uuidof(NetFwOpenPort),
			NULL,
			CLSCTX_INPROC_SERVER,
			__uuidof(INetFwOpenPort),
			(void**)&fwOpenPort
			);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddPort: fail to create an instance of an open port : 0x%08lx"),m_hr);
			goto Cleanup;
		}

		// Set the port number.
		m_hr = fwOpenPort->put_Port(nPort);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddPort: fail to set the port number(%d) : 0x%08lx"),nPort,m_hr);
			goto Cleanup;
		}

		// Set the IP protocol.
		m_hr = fwOpenPort->put_Protocol(ipProtocol);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddPort: fail to set the IP protocol : 0x%08lx"),nPort,m_hr);
			goto Cleanup;
		}

		// Allocate a BSTR for the friendly name of the port.
		fwBstrName = strName.AllocSysString();
		if (SysStringLen(fwBstrName) == 0)
		{
			m_hr = E_OUTOFMEMORY;
			m_strErrMsg.Format(_T("WindowsFirewallAddPort: fail to lllocate a BSTR for the friendly name of the port(%s) : 0x%08lx"),strName,m_hr);
			goto Cleanup;
		}

		// Set the friendly name of the port.
		m_hr = fwOpenPort->put_Name(fwBstrName);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddPort: fail to set the friendly name of the port : 0x%08lx"),m_hr);
			goto Cleanup;
		}

		// Opens the port and adds it to the collection.
		m_hr = fwOpenPorts->Add(fwOpenPort);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddPort: fail to opens the port and adds it to the collection : 0x%08lx"),m_hr);
			goto Cleanup;
		}
	}

Cleanup:

	// Free the BSTR.
	SysFreeString(fwBstrName);

	// Release the open port instance.
	if (fwOpenPort != NULL)
	{
		fwOpenPort->Release();
	}

	// Release the globally open ports collection.
	if (fwOpenPorts != NULL)
	{
		fwOpenPorts->Release();
	}

	return m_hr;
}

//Remove port to list of globally open ports.
HRESULT CFirewall::WindowsFirewallRemovePort(UINT nPort, NET_FW_IP_PROTOCOL ipProtocol /*= NET_FW_IP_PROTOCOL_TCP*/)
{
	BOOL fwPortEnabled = FALSE;
	INetFwOpenPort* fwOpenPort = NULL;
	INetFwOpenPorts* fwOpenPorts = NULL;

	if (m_fwProfile == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WindowsFirewallAddPort: you do not initialize the firewall profile"));
		goto Cleanup;
	}

	// First check to see if the port is already added.
	m_hr = WindowsFirewallPortIsEnabled(
		nPort,
		ipProtocol,
		fwPortEnabled
		);
	if (FAILED(m_hr))
	{
		m_strErrMsg.Format(_T("WindowsFirewallAddPort: fail to check first to see if the port is already added : 0x%08lx"),m_hr);
		goto Cleanup;
	}

	// Only remove the port if it isn't already added.
	if (fwPortEnabled)
	{
		// Retrieve the collection of globally open ports.
		m_hr = m_fwProfile->get_GloballyOpenPorts(&fwOpenPorts);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddPort: fail to Retrieve the collection of globally open ports : 0x%08lx"),m_hr);
			goto Cleanup;
		}

		// Remove the port and adds it to the collection.
		fwOpenPorts->Remove(nPort,ipProtocol);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAddPort: fail to remove the port(%d) and adds it to the collection : 0x%08lx"),nPort,m_hr);
			goto Cleanup;
		}
	}

Cleanup:

	// Release the open port instance.
	if (fwOpenPort != NULL)
	{
		fwOpenPort->Release();
	}

	// Release the globally open ports collection.
	if (fwOpenPorts != NULL)
	{
		fwOpenPorts->Release();
	}

	return m_hr;
}

HRESULT CFirewall::WindowsFirewallAppIsEnabled(CString strAppFile,BOOL &bEnabled)
{
	BSTR fwBstrProcessImageFileName = NULL;
	VARIANT_BOOL fwEnabled;
	INetFwAuthorizedApplication* fwApp = NULL;
	INetFwAuthorizedApplications* fwApps = NULL;

	if (m_fwProfile == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WindowsFirewallAppIsEnabled: you do not initialize the firewall profile"));
		return m_hr;
	}

	if (strAppFile.IsEmpty())
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WindowsFirewallAppIsEnabled: Application name is null"));
		return m_hr;
	}


	// Retrieve the authorized application collection.
	m_hr = m_fwProfile->get_AuthorizedApplications(&fwApps);
	if (FAILED(m_hr))
	{
		m_strErrMsg.Format(_T("WindowsFirewallAppIsEnabled: fail to retrieve the authorized application collection : 0x%08lx"),m_hr);
		goto Cleanup;
	}

	// Allocate a BSTR for the process image file name.
	fwBstrProcessImageFileName = strAppFile.AllocSysString();
	if (SysStringLen(fwBstrProcessImageFileName) == 0)
	{
		m_hr = E_OUTOFMEMORY;
		m_strErrMsg.Format(_T("WindowsFirewallAppIsEnabled: fail to Allocate a BSTR for the process image file name(%s) : 0x%08lx"),strAppFile,m_hr);
		goto Cleanup;
	}

	// Attempt to retrieve the authorized application.
	m_hr = fwApps->Item(fwBstrProcessImageFileName, &fwApp);
	if (SUCCEEDED(m_hr))
	{
		// Find out if the authorized application is enabled.
		m_hr = fwApp->get_Enabled(&fwEnabled);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallAppIsEnabled: fail to find out if the authorized application is enabled : 0x%08lx"),m_hr);
			goto Cleanup;
		}

		if (fwEnabled != VARIANT_FALSE)
		{
			// The authorized application is enabled.
			bEnabled = TRUE;
			m_strErrMsg.Format(_T("WindowsFirewallAppIsEnabled: Authorized application %s is enabled in the firewall : 0x%08lx"),strAppFile,m_hr);
		}
		else
		{
			m_strErrMsg.Format(_T("WindowsFirewallAppIsEnabled: Authorized application %s is disabled in the firewall : 0x%08lx"),strAppFile,m_hr);
		}
	}
	else
	{
		// The authorized application was not in the collection.
		m_hr = S_OK;
		m_strErrMsg.Format(_T("WindowsFirewallAppIsEnabled: Authorized application %s is disabled in the firewall : 0x%08lx"),strAppFile,m_hr);
	}

Cleanup:

	// Free the BSTR.
	SysFreeString(fwBstrProcessImageFileName);

	// Release the authorized application instance.
	if (fwApp != NULL)
	{
		fwApp->Release();
	}

	// Release the authorized application collection.
	if (fwApps != NULL)
	{
		fwApps->Release();
	}

	return m_hr;
}
HRESULT CFirewall::WindowsFirewallPortIsEnabled(UINT nPort, NET_FW_IP_PROTOCOL ipProtocol,BOOL &bEnabled)
{
	VARIANT_BOOL fwEnabled;
	INetFwOpenPort* fwOpenPort = NULL;
	INetFwOpenPorts* fwOpenPorts = NULL;

	if (m_fwProfile == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WindowsFirewallPortIsEnabled: you do not initialize the firewall profile"));
		return m_hr;
	}

	bEnabled = FALSE;

	// Retrieve the globally open ports collection.
	m_hr = m_fwProfile->get_GloballyOpenPorts(&fwOpenPorts);
	if (FAILED(m_hr))
	{
		m_strErrMsg.Format(_T("WindowsFirewallPortIsEnabled: fail to retrieve the globally open ports collection : 0x%08lx"),m_hr);
		goto Cleanup;
	}

	// Attempt to retrieve the globally open port.
	m_hr = fwOpenPorts->Item(nPort, ipProtocol, &fwOpenPort);
	if (SUCCEEDED(m_hr))
	{
		// Find out if the globally open port is enabled.
		m_hr = fwOpenPort->get_Enabled(&fwEnabled);
		if (FAILED(m_hr))
		{
			m_strErrMsg.Format(_T("WindowsFirewallPortIsEnabled: fail to find out if the globally open port is enabled : 0x%08lx"),m_hr);
			goto Cleanup;
		}

		if (fwEnabled != VARIANT_FALSE)
		{
			// The globally open port is enabled.
			bEnabled = TRUE;	
			m_strErrMsg.Format(_T("WindowsFirewallPortIsEnabled: Port %ld is open in the firewall : 0x%08lx"),nPort,m_hr);
		}
		else
		{
			m_strErrMsg.Format(_T("WindowsFirewallPortIsEnabled: Port %ld is not open in the firewall : 0x%08lx"),nPort,m_hr);
		}
	}
	else
	{
		// The globally open port was not in the collection.
		m_hr = S_OK;

		m_strErrMsg.Format(_T("WindowsFirewallPortIsEnabled: Port %ld is not open in the firewall : 0x%08lx"),nPort,m_hr);
	}

Cleanup:

	// Release the globally open port.
	if (fwOpenPort != NULL)
	{
		fwOpenPort->Release();
	}

	// Release the globally open ports collection.
	if (fwOpenPorts != NULL)
	{
		fwOpenPorts->Release();
	}

	return m_hr;
}

void CFirewall::GetErrorMessage(LPTSTR lpMsg,DWORD ccBuffer)
{
	if (!m_strErrMsg.IsEmpty())
	{
		memset(lpMsg,0,ccBuffer);
		_tcscpy_s(lpMsg,ccBuffer-1,m_strErrMsg);
	}
}

HRESULT CFirewall::WFCOMInitialize()
{
	m_strErrMsg.Empty();
	// Initialize COM.
	m_comInit = CoInitializeEx(
		0,
		COINIT_APARTMENTTHREADED
		);

	// Ignore RPC_E_CHANGED_MODE; this just means that COM has already been
	// initialized with a different mode. Since we don't care what the mode is,
	// we'll just use the existing mode.
	if (m_comInit != RPC_E_CHANGED_MODE)
	{
		m_hr = m_comInit;
		if (FAILED(m_hr))
		{
			//CoInitializeEx failed
			m_strErrMsg.Format(_T("WFCOMInitialize: CoInitializeEx failed: 0x%08lx"), m_hr);
			goto Cleanup; 
		}
		else
		{
			//initial ok
			m_hr = S_OK;
		}
	}

	// Create an instance of the firewall settings Plocy.
	m_hr = CoCreateInstance(
		__uuidof(NetFwPolicy2), 
		NULL, 
		CLSCTX_INPROC_SERVER, 
		__uuidof(INetFwPolicy2), 
		(void**)&m_pNetFwPolicy2);
	if (FAILED(m_hr))
	{
		//CoCreateInstance failed
		m_strErrMsg.Format(_T("WFCOMInitialize: CoCreateInstance failed: 0x%08lx"), m_hr);
		goto Cleanup; 
	}

	// Create an instance of the firewall settings rule.
	m_hr = CoCreateInstance(
		__uuidof(NetFwRule),    //CLSID of the class whose object is to be created
		NULL, 
		CLSCTX_INPROC_SERVER, 
		__uuidof(INetFwRule),   // Identifier of the Interface used for communicating with the object
		(void**)&m_pNetFwRule);

	if (FAILED(m_hr))
	{
		m_strErrMsg.Format(_T("CoCreateInstance for INetFwRule failed: 0x%08lx\n"), m_hr);
		goto Cleanup;        
	}
Cleanup:
	return m_hr;
}

//Add the authorized application collection.
HRESULT CFirewall::WFCOMAddAppFirewallRule(CString strAppFile,CString strName,long lProfileTypesBitmask,CString strDescription)
{
	m_hr = S_OK;
	INetFwRules *pNetFwRules = NULL;
	INetFwRule2 *pNetFwRule2 = NULL;

	BSTR RuleName = NULL;
	BSTR RuleGroupName = NULL;
	BSTR RuleDescription = NULL;
	BSTR RuleAppPath = NULL;

	if (m_pNetFwPolicy2 == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMAddAppFirewallRule: you do not initialize NetFwPolicy2"));
		return m_hr;
	}

	if (m_pNetFwRule == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMAddAppFirewallRule: you do not initialize INetFwRule"));
		return m_hr;
	}

	if (strAppFile.IsEmpty())
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMAddAppFirewallRule: Application full path is null"));
		return m_hr;
	}

	if (strName.IsEmpty())
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMAddAppFirewallRule: the rule name is null"));
		return m_hr;
	}

	RuleName = strName.AllocSysString();
	if (NULL == RuleName)
	{
		m_strErrMsg.Format(_T("WFCOMAddAppFirewallRule: Insufficient memory for rule name to compose."));
		goto Cleanup;
	}
	
	RuleDescription = strDescription.AllocSysString();
	if (NULL == RuleDescription)
	{
		m_strErrMsg.Format(_T("WFCOMAddAppFirewallRule: Insufficient memory for decription to compose."));
		goto Cleanup;
	}

	RuleAppPath = strAppFile.AllocSysString();
	if (NULL == RuleAppPath)
	{
		m_strErrMsg.Format(_T("WFCOMAddAppFirewallRule: Insufficient memory for app file to compose."));
		goto Cleanup;
	}

	m_hr = m_pNetFwPolicy2->get_Rules(&pNetFwRules);

	if (FAILED(m_hr))
	{
		m_strErrMsg.Format(_T("WFCOMAddAppFirewallRule: Failed to retrieve firewall rules collection : 0x%08lx\n"), m_hr);
		goto Cleanup;        
	}

	m_hr = 

	m_hr = m_pNetFwRule->put_Name(RuleName);
	if ( FAILED(m_hr) )
	{
		m_strErrMsg.Format(_T("Failed INetFwRule::put_Name failed with Cleanup: 0x %x.\n"), m_hr);
		goto Cleanup;
	}

	m_hr = m_pNetFwRule->put_Description(RuleDescription);
	if ( FAILED(m_hr) )
	{
		m_strErrMsg.Format(_T("Failed INetFwRule::put_Description failed with Cleanup: 0x %x.\n"), m_hr);
		goto Cleanup;
	}

	m_hr = m_pNetFwRule->put_Direction(NET_FW_RULE_DIR_IN);
	if ( FAILED(m_hr) )
	{
		m_strErrMsg.Format(_T("Failed INetFwRule::put_Direction failed with Cleanup: 0x %x.\n"), m_hr);
		goto Cleanup;
	}

	m_hr = m_pNetFwRule->put_Action(NET_FW_ACTION_ALLOW);
	if ( FAILED(m_hr) )
	{
		m_strErrMsg.Format(_T("Failed INetFwRule::put_Action failed with Cleanup: 0x %x.\n"), m_hr);
		goto Cleanup;
	}

	m_hr = m_pNetFwRule->put_ApplicationName(RuleAppPath);
	if ( FAILED(m_hr) )
	{
		m_strErrMsg.Format(_T("Failed INetFwRule::put_ApplicationName failed with Cleanup: 0x %x.\n"), m_hr);
		goto Cleanup;
	}

	m_hr = m_pNetFwRule->put_Profiles(lProfileTypesBitmask);
	if ( FAILED(m_hr) )
	{
		m_strErrMsg.Format(_T("Failed INetFwRule::put_Profiles failed with Cleanup: 0x %x.\n"), m_hr);
		goto Cleanup;
	}

	m_hr = m_pNetFwRule->put_Enabled(VARIANT_TRUE);
	if ( FAILED(m_hr) )
	{
		m_strErrMsg.Format(_T("Failed INetFwRule::put_Enabled failed with Cleanup: 0x %x.\n"), m_hr);
		goto Cleanup;
	}

	// Check if INetFwRule2 interface is available (i.e Windows7+)
	// If supported, then use EdgeTraversalOptions
	// Else use the EdgeTraversal boolean flag.

	if (SUCCEEDED(m_pNetFwRule->QueryInterface(__uuidof(INetFwRule2), (void**)&pNetFwRule2)))
	{
		m_hr = pNetFwRule2->put_EdgeTraversalOptions(NET_FW_EDGE_TRAVERSAL_TYPE_DEFER_TO_APP);
		if ( FAILED(m_hr) )
		{
			m_strErrMsg.Format(_T("Failed INetFwRule::put_EdgeTraversalOptions failed with Cleanup: 0x %x.\n"), m_hr);
			goto Cleanup;
		}
	}
	else
	{
		m_hr = m_pNetFwRule->put_EdgeTraversal(VARIANT_TRUE);
		if ( FAILED(m_hr) )
		{
			m_strErrMsg.Format(_T("Failed INetFwRule::put_EdgeTraversal failed with Cleanup: 0x %x.\n"), m_hr);
			goto Cleanup;
		}
	}

	m_hr = pNetFwRules->Add(m_pNetFwRule);
	if (FAILED(m_hr))
	{
		m_strErrMsg.Format(_T("Failed to add firewall rule to the firewall rules collection : 0x%08lx\n"), m_hr);
		goto Cleanup;        
	}

	m_strErrMsg.Format(_T("Successfully added firewall rule !\n"));

Cleanup:
	SysFreeString(RuleName);
	SysFreeString(RuleGroupName);
	SysFreeString(RuleDescription);
	SysFreeString(RuleAppPath);

	if (pNetFwRule2 != NULL)
	{
		pNetFwRule2->Release();
	}

	if (pNetFwRules != NULL)
	{
		pNetFwRules->Release();
	}

	return m_hr;
}

//Remove the authorized application collection.
HRESULT CFirewall::WFCOMRemoveAppFirewallRule(CString strAppFile,CString strName)
{
	INetFwRules *pNetFwRules = NULL;
	INetFwRule2 *pNetFwRule2 = NULL;

	BSTR RuleName = NULL;
	BSTR RuleGroupName = NULL;
	BSTR RuleDescription = NULL;
	BSTR RuleAppPath = NULL;

	m_hr = S_OK;

	if (m_pNetFwPolicy2 == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMRemoveAppFirewallRule: you do not initialize NetFwPolicy2"));
		return m_hr;
	}

	if (m_pNetFwRule == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMRemoveAppFirewallRule: you do not initialize INetFwRule"));
		return m_hr;
	}

	if (strAppFile.IsEmpty())
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMRemoveAppFirewallRule: Application full path is null"));
		return m_hr;
	}

	if (strName.IsEmpty())
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMRemoveAppFirewallRule: the rule name is null"));
		return m_hr;
	}

	RuleName = strName.AllocSysString();
	if (NULL == RuleName)
	{
		m_strErrMsg.Format(_T("WFCOMAddAppFirewallRule: Insufficient memory for rule name to compose."));
		goto Cleanup;
	}

	RuleAppPath = strAppFile.AllocSysString();
	if (NULL == RuleAppPath)
	{
		m_strErrMsg.Format(_T("WFCOMAddAppFirewallRule: Insufficient memory for app file to compose."));
		goto Cleanup;
	}

	m_hr = m_pNetFwPolicy2->get_Rules(&pNetFwRules);

	if (FAILED(m_hr))
	{
		m_strErrMsg.Format(_T("WFCOMAddAppFirewallRule: Failed to retrieve firewall rules collection : 0x%08lx\n"), m_hr);
		goto Cleanup;        
	}

	while(IsWFCOMAppFirewallRuleExist(strAppFile, strName))
	{
		m_hr = pNetFwRules->Remove(RuleName);
	}
	
	if (FAILED(m_hr))
	{
		m_strErrMsg.Format(_T("Failed to remove firewall rule to the firewall rules collection : 0x%08lx\n"), m_hr);
		goto Cleanup;        
	}

Cleanup:

	SysFreeString(RuleName);
	SysFreeString(RuleAppPath);

	if (pNetFwRule2 != NULL)
	{
		pNetFwRule2->Release();
	}

	if (pNetFwRules != NULL)
	{
		pNetFwRules->Release();
	}

	return m_hr;
}

HRESULT CFirewall::WFCOMAppFirewallRuleEnabled(CString strAppFile,CString strName,BOOL& bEnabled,long lProfileTypesBitmask)
{
	INetFwRules *pFwRules = NULL;
	INetFwRule *pFwRule = NULL;
	IUnknown *pEnumerator = NULL;
	IEnumVARIANT* pVariant = NULL;
	long fwRuleCount;
	long lProfileBitmask = 0;
	ULONG cFetched = 0; 
	CComVariant var;
	BSTR bstrRuleName,bstrAppName;

	if (m_pNetFwPolicy2 == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMRemoveAppFirewallRule: you do not initialize NetFwPolicy2"));
		return m_hr;
	}

	if (m_pNetFwRule == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMRemoveAppFirewallRule: you do not initialize INetFwRule"));
		return m_hr;
	}

	if (strAppFile.IsEmpty())
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMRemoveAppFirewallRule: Application full path is null"));
		return m_hr;
	}

	// Retrieve INetFwRules
	m_hr = m_pNetFwPolicy2->get_Rules(&pFwRules);
	if (FAILED(m_hr))
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMAppFirewallRuleExist: get_Rules failed: 0x%08lx\n"), m_hr);
		goto Cleanup;
	}

	// Obtain the number of Firewall rules
	m_hr = pFwRules->get_Count(&fwRuleCount);
	if (FAILED(m_hr))
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMAppFirewallRuleExist: get_Count failed: 0x%08lx\n"), m_hr);
		goto Cleanup;
	}

	// Iterate through all of the rules in pFwRules
	pFwRules->get__NewEnum(&pEnumerator);

	if (pEnumerator)
	{
		m_hr = pEnumerator->QueryInterface(__uuidof(IEnumVARIANT), (void **) &pVariant);
	}

	while(SUCCEEDED(m_hr) && m_hr != S_FALSE)
	{
		var.Clear();
		if (pVariant)
			m_hr = pVariant->Next(1, &var, &cFetched);

		if (S_FALSE != m_hr)
		{
			if (SUCCEEDED(m_hr))
			{
				m_hr = var.ChangeType(VT_DISPATCH);
			}

			if (SUCCEEDED(m_hr))
			{
				m_hr = (V_DISPATCH(&var))->QueryInterface(__uuidof(INetFwRule), reinterpret_cast<void**>(&pFwRule));
			}

			if (SUCCEEDED(m_hr))
			{
				// Output the properties of this rule
				ProfileMapElement ProfileMap[3];
				ProfileMap[0].Id = NET_FW_PROFILE2_DOMAIN;
				ProfileMap[0].Name = L"Domain";
				ProfileMap[1].Id = NET_FW_PROFILE2_PRIVATE;
				ProfileMap[1].Name = L"Private";
				ProfileMap[2].Id = NET_FW_PROFILE2_PUBLIC;
				ProfileMap[2].Name = L"Public";
				
				//get the rule name
				pFwRule->get_Name(&bstrRuleName);
				
				//get the application file
				pFwRule->get_ApplicationName(&bstrAppName);
				
				//check the rule
				if (strName.CompareNoCase((CString)bstrRuleName) != 0
					|| strAppFile.CompareNoCase((CString)bstrAppName) != 0
					)
				{
					continue;
				}

				if (SUCCEEDED(pFwRule->get_Profiles(&lProfileBitmask)))
				{
					// The returned bitmask can have more than 1 bit set if multiple profiles 
					//   are active or current at the same time
					int nPfrofileCount = 0;
					for (int i=0; i<3; i++)
					{
						if ( lProfileBitmask & ProfileMap[i].Id  )
						{
							nPfrofileCount++;
						}
					}

					//the rule exist only when all profile types are set
					if (nPfrofileCount == 3)
					{
						bEnabled = TRUE;
					}
				}

				break;
			}
		}
	}

Cleanup:
	// Release pFwRule
	if (pFwRule != NULL)
	{
		pFwRule->Release();
	}

	if (pFwRules != NULL)
	{
		pFwRules->Release();
	}

	if (pEnumerator != NULL)
	{
		pEnumerator->Release();
	}

	return m_hr;
}


//check if the rule exist
BOOL CFirewall::IsWFCOMAppFirewallRuleExist(CString strAppFile, CString strName)
{
	BOOL bRet = FALSE;
	INetFwRules *pFwRules = NULL;
	INetFwRule *pFwRule = NULL;
	IUnknown *pEnumerator = NULL;
	IEnumVARIANT* pVariant = NULL;
	long fwRuleCount;
	ULONG cFetched = 0; 
	CComVariant var;
	BSTR bstrRuleName, bstrAppName;

	if (m_pNetFwPolicy2 == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMAppFirewallRuleExist: you do not initialize NetFwPolicy2"));
		return FALSE;
	}

	if (m_pNetFwRule == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMAppFirewallRuleExist: you do not initialize INetFwRule"));
		return FALSE;
	}

	if (strAppFile.IsEmpty())
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMAppFirewallRuleExist: Application full path is null"));
		return FALSE;
	}

	// Retrieve INetFwRules
	m_hr = m_pNetFwPolicy2->get_Rules(&pFwRules);
	if (FAILED(m_hr))
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMAppFirewallRuleExist: get_Rules failed: 0x%08lx\n"), m_hr);
		goto Cleanup;
	}

	// Obtain the number of Firewall rules
	m_hr = pFwRules->get_Count(&fwRuleCount);
	if (FAILED(m_hr))
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("WFCOMAppFirewallRuleExist: get_Count failed: 0x%08lx\n"), m_hr);
		goto Cleanup;
	}

	// Iterate through all of the rules in pFwRules
	pFwRules->get__NewEnum(&pEnumerator);
	if (pEnumerator)
	{
		m_hr = pEnumerator->QueryInterface(__uuidof(IEnumVARIANT), (void **) &pVariant);
	}

	while(SUCCEEDED(m_hr) && m_hr != S_FALSE)
	{
		var.Clear();
		if (pVariant)
			m_hr = pVariant->Next(1, &var, &cFetched);

		if (S_FALSE != m_hr)
		{
			if (SUCCEEDED(m_hr))
			{
				m_hr = var.ChangeType(VT_DISPATCH);
			}
			if (SUCCEEDED(m_hr))
			{
				m_hr = (V_DISPATCH(&var))->QueryInterface(__uuidof(INetFwRule), reinterpret_cast<void**>(&pFwRule));
			}

			if (SUCCEEDED(m_hr))
			{
				//get the rule name
				pFwRule->get_Name(&bstrRuleName);

				//get the application file
				pFwRule->get_ApplicationName(&bstrAppName);

				//check the rule
				if (strName.CompareNoCase((CString)bstrRuleName) == 0
					&& strAppFile.CompareNoCase((CString)bstrAppName) == 0)
				{
					//rule exist
					bRet = TRUE;
					break;
				}
				else
				{
					//do not exist, check next rule.
					continue;
				}
			}
		}
	}

Cleanup:
	// Release pFwRule
	if (pFwRule != NULL)
	{
		pFwRule->Release();
	}

	if (pFwRules != NULL)
	{
		pFwRules->Release();
	}

	if (pEnumerator != NULL)
	{
		pEnumerator->Release();
	}

	return bRet;
}

HRESULT CFirewall::WFCOMGetCurrentProfileType(long &CurrentProfilesType)
{
	long    CurrentProfilesBitMask = 0;
	VARIANT_BOOL bActualFirewallEnabled = VARIANT_FALSE;

	ProfileMapElement ProfileMap[3];
	ProfileMap[0].Id = NET_FW_PROFILE2_DOMAIN;
	ProfileMap[0].Name = L"Domain";
	ProfileMap[1].Id = NET_FW_PROFILE2_PRIVATE;
	ProfileMap[1].Name = L"Private";
	ProfileMap[2].Id = NET_FW_PROFILE2_PUBLIC;
	ProfileMap[2].Name = L"Public";

	m_hr = S_OK;

	if (m_pNetFwPolicy2 == NULL)
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("GetCurrentProfile: NetFwPolicy2 do not initialize."));
		return m_hr;
	}

	m_hr = m_pNetFwPolicy2->get_CurrentProfileTypes(&CurrentProfilesBitMask);
	if (FAILED(m_hr))
	{
		m_hr = S_FALSE;
		m_strErrMsg.Format(_T("GetCurrentProfile: Failed to call NetFwPolicy2.get_CurrentProfileTypes : 0x%08lx\n"), m_hr);
		goto CLEANUP;
	}

	// get the current profile types
	CurrentProfilesType = CurrentProfilesBitMask;
	

CLEANUP:
	return m_hr;
}

BOOL CFirewall::IsWFCOMPermited()
{
	BOOL bRet = FALSE;
	OSVERSIONINFOEX VersionInfo;

	DWORD dwReturnedProductType = 0;

	HMODULE hndl;
	pfn_GetProductInfo GetProductInfo = NULL;

	memset(&VersionInfo, 0, sizeof(VersionInfo));
	VersionInfo.dwOSVersionInfoSize = sizeof(VersionInfo);

	GetVersionEx((LPOSVERSIONINFO)&VersionInfo);

	hndl = LoadLibrary(KERNEL32_DLL);
	if (!hndl)
	{
		return FALSE;
	}

	GetProductInfo = (pfn_GetProductInfo)::GetProcAddress(hndl, "GetProductInfo");

	if (GetProductInfo != NULL)
	{
		if (GetProductInfo(VersionInfo.dwMajorVersion, VersionInfo.dwMinorVersion, VersionInfo.wServicePackMajor, VersionInfo.wServicePackMinor, &dwReturnedProductType))
		{
			if (VersionInfo.dwMajorVersion > 6 || (VersionInfo.dwMajorVersion == 6 && VersionInfo.dwMinorVersion >= 1))
			{
				//windows 6.1 or above.
				bRet = TRUE;
			}
		}
	}

	FreeLibrary(hndl);

	return bRet;
}

