#pragma once
#include "..\include\UpLib.h"
#include "PipeSession.h"
#include <string>
#include <vector>
using namespace std;

class CHotfixSession : public CPipeSessionBase
{
public:
	CHotfixSession(HANDLE hPipe, CPipeSessionSet* pSet);

	virtual ~CHotfixSession();

	virtual DWORD Main();

protected:
	virtual LONG  onInvalidCommand(DWORD cmd, void* pParams);
	
	virtual LONG  onTriggerUpdate(void* pParams);
	
	virtual LONG  onQueryJobStatus(void* pParams);
	
	virtual LONG  onCancelUpdateJob(void* pParams);	
};
