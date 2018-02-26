#pragma once
#include "PipeSession.h"

/*
*	Class: CPipeSessionSet
*   Description: This class is used to manage the pipe sessions. 
*		For each connect to the pipe server, it will create a pipe session to handle the request from client.
*/

class CPipeSessionSet
{
public:
	CPipeSessionSet();

	~CPipeSessionSet();

	//
	// create a pipe session to handle to request from client
	//
	CPipeSessionBase* CreateSession(HANDLE hPipe);

	//
	// remove the pipe session
	//
	void RemoveSession(CPipeSessionBase* pSession);

	//
	// stop all pipe sessions
	//
	void StopAllSessions();

protected:
	std::vector<CPipeSessionBase*>	m_vecSessions;

protected:
	CRITICAL_SECTION				m_cs;
	CDbgLog							m_log;
};
