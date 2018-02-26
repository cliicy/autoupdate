#pragma once
#include "XXmlNode.h"
#include <string>
#include <vector>
#include "..\UpdateJob\AvailableUpdateInfo.h"
//
// the information of each file to download
//


//
// class: CAvailableBIUpdateDll
// desc : The class to represent an single binaries update for UDP agent or consle. The content is got from "AvailableBIUpdateDll.dll" or "AvailableBIUpdateDll.dll.xml"
//
class CAvailableBIUpdateDll
{
public:
	CAvailableBIUpdateDll();

	virtual ~CAvailableBIUpdateDll();

	virtual DWORD		LoadFromDllFile(const wstring& strDllFile);

	virtual	DWORD		LoadFromXmlFile(const wstring& strXmlFile);

	virtual void		GetVersion( UDP_VERSION_INFO& version );

	virtual BOOL		IsRebootRequired() { return m_bRebootRequried;  }

	virtual int			GetTheLastRebootUpdate() {	return m_iLastRebootUpdate;	}

	virtual BOOL		ValidateUpdate(const wstring& strBaseDir);

	virtual DWORD		GetRequiredVersionOfAutoUpdate() { return m_dwRequiredVersionOfAutoUpdate; }

	virtual ULONGLONG	GetSizeOfThisUpdate();

	virtual void		GetFilesToDownload(std::vector<PUP_FILE_INFO>& vecFiles);

	virtual void		GetPostDownloadActions(std::vector<PUP_POST_ACTION>& vecActions);

protected:
	CDbgLog							m_log;

protected:
	UDP_VERSION_INFO				m_version;
	BOOL							m_bRebootRequried;
	int								m_iLastRebootUpdate;
	DWORD							m_dwRequiredVersionOfAutoUpdate;
	std::vector<PUP_FILE_INFO>		m_vecFilesToDownload;
	std::vector<PUP_POST_ACTION>	m_vecActionsToRun;
};

// ----------------------------------------------------------------------------------------------
// function: GenerateBIUpdateStatusXMLFile
// desc : Generate 'status.xml' from given 'CAvailableBIUpdateDll.dll'
// ----------------------------------------------------------------------------------------------
DWORD GenerateBIUpdateStatusXMLFile(DWORD dwProduct, const wstring& strAvailableUpdateInfoDLL, const wstring& strTargetXMLFile);