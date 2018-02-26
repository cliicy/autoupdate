#include "stdafx.h"
#include "PipeSession.h"
#include "XXmlNode.h"
#include "UpdateJobManager.h"
#include "PluginManager.h"
#include "DRCommonlib.h"

/*
*	the implementation of PipeSession
*/
CPipeSessionBase::CPipeSessionBase(HANDLE hPipe, CPipeSessionSet* pSet)
	: CThreadBase(TRUE)	
	, m_pipe(hPipe)
	, m_pSessionSet(pSet)
{
}

CPipeSessionBase::~CPipeSessionBase()
{
	m_pipe.Close();
}

DWORD CPipeSessionBase::Stop()
{
	m_pipe.Disconnect();
	return 0;
}
