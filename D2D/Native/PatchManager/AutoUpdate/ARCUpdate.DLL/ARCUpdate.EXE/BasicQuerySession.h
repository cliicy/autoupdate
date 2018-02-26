#pragma once
#include "..\include\PipeBase.h"
#include "..\include\UpLib.h"
#include "PipeSession.h"
#include <string>
#include <vector>
using namespace std;

class CPipeSessionSet;

class CBasicQuerySession : public CPipeSessionBase
{
public:
	CBasicQuerySession(HANDLE hPipe, CPipeSessionSet* pSet);

	virtual ~CBasicQuerySession();

	virtual DWORD Main();

protected:
	virtual LONG  onInvalidCommand(DWORD cmd, void* pParams);	

	virtual LONG  onQueryNodeInfo( void* pParams);

	virtual LONG  onQueryInstalledPatches(void* pParams);
};
