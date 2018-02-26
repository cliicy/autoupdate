#pragma once

#include "StdAfx.h"
#include "EdgeAPM.h"
//#include "ARCSetupSvc.h"

using namespace  EdgeAPM;

enum COMPONENT_TYPE{D2D=1, CM=2};
const DWORD dwMilliseconds = 300000; //time-out 5 mins

class CCAComponent
{
public:
	CCAComponent(void);
	//CCAComponent(int nType);
	virtual ~CCAComponent(void);
public:
	void SetComponentId(LPCTSTR strId)
	{
		m_strID = strId;
	}
	CString & GetComponentId()
	{
		return m_strID;
	}

private:
	CString m_strID;
public:
	int m_nMajor;
	int m_nMinor;
	int m_nBuild;
public:
	virtual void Initialize(LPCTSTR lpctFile, LPCTSTR lpctSection);
	virtual BOOL BackendExeRun();
	BOOL CheckInstallStatus();
	void initProductInfo(LPCTSTR lpctFile, LPCTSTR lpctSection);
	static CCAComponent *CreateComponent(int nType);
	BOOL InitInstallDir(LPCTSTR lpctRegKey);
	BOOL InitInstallVersion(LPCTSTR lpctRegKey);
	void KillPossibleProcesses();
	virtual DWORD TestServerConnection(const APMSetting &apmSetting);
	virtual DWORD downloadPatch(HANDLE handle, Response& response);
	virtual DWORD PatchInstall();
	virtual void DoExitThings();
	wstring generateRequestId();
	virtual DWORD HandleWindowsService();
	void SaveApmSetting(EdgeAPM::APMSettingModel & apmSetting);
	BOOL LoadApmSetting(EdgeAPM::APMSettingModel & apmSetting);
	void SetEnvPath();
public:
	CString m_strProductCode;
	CString m_strIniPath;
	EdgeAPM::APMSetting m_apmSetting;
	CString m_strInstallPath;
	HANDLE hPipe;
	BOOL bServerRuningBeforeUpdate;
};