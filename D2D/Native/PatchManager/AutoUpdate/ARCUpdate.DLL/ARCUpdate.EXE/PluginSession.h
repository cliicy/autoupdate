#pragma once
#include "..\include\PipeBase.h"
#include "..\include\UpLib.h"
#include "PipeSession.h"
#include <string>
#include <vector>
using namespace std;

class CPipeSessionSet;

class CPluginSession : public CPipeSessionBase
{
public:
	CPluginSession(HANDLE hPipe, CPipeSessionSet* pSet);

	virtual ~CPluginSession();

	virtual DWORD Main();

protected:
	virtual LONG  onInvalidCommand(DWORD cmd, void* pParams);
	
	virtual LONG  onRemovePlugin(void* pParams);

	virtual LONG  onInstallPlugin(void* pParamsIn);
};
