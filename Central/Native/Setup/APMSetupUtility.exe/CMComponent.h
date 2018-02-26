#pragma once
#include "CAComponent.h"

class CCMComponent:public CCAComponent
{
public:
	CCMComponent(void);
	virtual  ~CCMComponent(void);
	virtual void Initialize(LPCTSTR lpctFile, LPCTSTR lpctSection);
	BOOL BackendExeRun();
	DWORD HandleWindowsService();
	DWORD TestServerConnection(const APMSetting &apmSetting);
	DWORD downloadPatch(HANDLE handle, Response& response);
	DWORD PatchInstall();
	void downloadD2DPatch(HANDLE handle, Response& response);
	void DoExitThings();
};