#pragma once

#include <windows.h>


#define		D2DPERF_ERR_SUCCESS				0
#define		D2DPERF_ERR_INVALID_PARA		1	// invalid input parameter - null pointer provided
#define		D2DPERF_ERR_INVALID_PATH		2	// invalid file path, no backup slash in file path
#define		D2DPERF_ERR_OPEN_EVENT			3	// failed to open / create stop event
#define		D2DPERF_ERR_WAIT_FAIL			4	// wait operation failed
#define		D2DPERF_ERR_START_THREAD_FAIL	5	// failed to create thread
#define		D2DPERF_ERR_INIT_PDH			6	// failed to load PDH library / locate PDH APIs

typedef struct _D2D_PERF_DATA
{
	DWORD		bkCpuTime;		// CPU usage of specified module, ranges from 0 ~ 100 * x, x is the count of CPU core
	LONGLONG	bkReadSpeed;	// read speed of specified module, in bytes / sec
	LONGLONG	bkWriteSpeed;	// write speed of specified module, in bytes / sec
	DWORD		sysCpuTime;		// CPU usage of the sytem, ranges from 0 ~ 100 * x, x is the count of CPU core
								// here the time occupied by "idle" process is already substracted
	LONGLONG	sysReadSpeed;	// total read speed of the system, in bytes / sec
	LONGLONG	sysWriteSpeed;	// total write speed of the system, in bytes / sec
} D2D_PERF_DATA, *PD2D_PERF_DATA;

class IPerfCounter
{
public:
	virtual ~IPerfCounter(void) {};

	virtual DWORD StartCollect() = 0;
	virtual DWORD StopCollect() = 0;
	virtual DWORD GetPerfData(PD2D_PERF_DATA pPerfData) = 0;
	virtual void  Release() = 0;
};

IPerfCounter * WINAPI GetPerfCounterInstance(
									  LPCWSTR pModuleName,	/*exe file name without extension, for example "afbackend"*/
									  DWORD dwWaitTime,		/*in millisecond, perf counter sample time interval*/
									  DWORD dwProcessID = (DWORD)(-1)
									  );//<sonmi01>2010-11-9 multiple instance of same module

inline DWORD BYTE_SEC_2_MB_MIN(LONGLONG bytesPerSec)
{
	return (DWORD) (((bytesPerSec  * 60) >> 10) >> 10);
}