#pragma once
#include "CAComponent.h"

class CD2DComponent:public CCAComponent
{
public:
	CD2DComponent(void);
	BOOL BackendExeRun();
	virtual ~CD2DComponent(void);
	DWORD TestServerConnection(const APMSetting &apmSetting);
	DWORD downloadPatch(HANDLE handle, Response& response);
	DWORD PatchInstall();
	void DoExitThings();
	DWORD HandleWindowsService();
	virtual void Initialize(LPCTSTR lpctFile, LPCTSTR lpctSection);
};