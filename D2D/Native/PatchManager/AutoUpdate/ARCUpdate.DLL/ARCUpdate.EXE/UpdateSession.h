#pragma once
#include "..\include\UpLib.h"
#include "PipeSession.h"
#include <string>
#include <vector>
using namespace std;

class CUpdateSession : public CPipeSessionBase
{
public:
	CUpdateSession(HANDLE hPipe, CPipeSessionSet* pSet);

	virtual ~CUpdateSession();

	virtual DWORD Main();

protected:
	virtual LONG  onInvalidCommand(DWORD cmd, void* pParams);
	
	virtual LONG  onTriggerUpdate(void* pParams);
	
	virtual LONG  onQueryJobStatus(void* pParams);
	
	virtual LONG  onCancelUpdateJob(void* pParams);	
};
