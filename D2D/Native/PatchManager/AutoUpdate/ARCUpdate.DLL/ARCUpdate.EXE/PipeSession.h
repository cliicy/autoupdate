#pragma once
#include "..\include\PipeBase.h"
#include "DbgLog.h"
#include <string>
#include <vector>
using namespace std;

class CPipeSessionSet;

/* 
*  Class: CPipeSessionBase
*  Description: the base class of pipe session
*/

class CPipeSessionBase : public CThreadBase
{
public:
	CPipeSessionBase(HANDLE hPipe, CPipeSessionSet* pSet);

	virtual ~CPipeSessionBase();

	virtual DWORD Main() = 0;

	virtual DWORD Stop();

protected:
	CDbgLog				m_log;
	CPipeBase			m_pipe;

protected:
	CPipeSessionSet*	m_pSessionSet;
};
