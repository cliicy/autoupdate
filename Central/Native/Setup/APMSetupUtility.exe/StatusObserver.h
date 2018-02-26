#include "ApmBackendStatus.h"
#include <string>

using namespace std;

class CStatusObserver
{
public:
	CStatusObserver(const wstring &runningGuid, const wstring &busyGuid);
	~CStatusObserver(void);

	ApmBackendSatus GetStatus() const;

	//************************************
	// Parameter: int seconds must be > 0
	//return 0: success; 2:Timeout
	//************************************
	DWORD WaitForOk(int seconds) const;

private:
	BOOL IsPatchManagerRunning() const;
	BOOL IsPatchManagerBusy() const;

private:
	wstring m_runningGuid; 
	wstring m_busyGuid;
};
