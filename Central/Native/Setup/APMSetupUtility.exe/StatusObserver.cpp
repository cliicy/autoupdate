#include "StdAfx.h"
#include "StatusObserver.h"
#include "Golbals.h"

using namespace std;

CStatusObserver::CStatusObserver(const wstring &runningGuid, const wstring &busyGuid){
	m_runningGuid = runningGuid;
	m_busyGuid = busyGuid;
}

CStatusObserver::~CStatusObserver(void){
}

BOOL CStatusObserver::IsPatchManagerRunning() const{
	HANDLE hMutex = OpenMutex(READ_CONTROL,FALSE,m_runningGuid.c_str());

	if(hMutex == NULL)
		return FALSE;

	CloseHandle(hMutex);
	return TRUE;
}

BOOL CStatusObserver::IsPatchManagerBusy() const {
	HANDLE hMutex = OpenMutex(READ_CONTROL,FALSE,m_busyGuid.c_str());

	if(hMutex == NULL)
		return FALSE;

	CloseHandle(hMutex);
	return TRUE;
}

ApmBackendSatus CStatusObserver::GetStatus() const{
	if( !IsPatchManagerRunning() )
		return ApmBackendSatus::NOT_RUNNING;
	if( IsPatchManagerBusy() )
		return ApmBackendSatus::BUSY;
	return ApmBackendSatus::OK;
}

DWORD CStatusObserver::WaitForOk(int seconds) const{
	while( seconds > 0){
		ApmBackendSatus t = GetStatus();
		if( t == ApmBackendSatus::OK )
			break;
		if( t == ApmBackendSatus::BUSY )
			WriteLog( L"back-end is busy, wait for one second" );
		if( t == ApmBackendSatus::NOT_RUNNING )
			WriteLog( L"back-end is not running, wait for one second" );
		Sleep(1000);
		seconds--;
	}
	if( seconds <= 0 )
		return 2;
	return 0;
}

