#include "StdAfx.h"
#include "UpdateScheduler.h"
#include "UpdateJobManager.h"

#define  TIME_INTERVAL							60000	 // every 1 mininute

static void systime_2_ulonglong( const SYSTEMTIME& sysTime, ULONGLONG& ullTime )
{
	FILETIME ft;
	::SystemTimeToFileTime( &sysTime, &ft );

	LARGE_INTEGER liTime;
	liTime.HighPart = ft.dwHighDateTime;
	liTime.LowPart = ft.dwLowDateTime;
	liTime.QuadPart -= 116444736000000000;
	liTime.QuadPart /= 10000;

	ullTime = liTime.QuadPart;
}

static void ulonglong_2_systime( ULONGLONG ullTime, SYSTEMTIME& sysTime )
{
	LARGE_INTEGER liTime;
	liTime.QuadPart = ullTime;
	liTime.QuadPart *= 10000;
	liTime.QuadPart += 116444736000000000;

	FILETIME ft;
	ZeroMemory( &ft, sizeof(ft) );
	ft.dwHighDateTime = liTime.HighPart;
	ft.dwLowDateTime = liTime.LowPart;

	::FileTimeToSystemTime( &ft, &sysTime );
}

CUpdateScheduler::CUpdateScheduler( DWORD dwProd )
	: CThreadBase( FALSE )
	, m_dwProduct( dwProd )
	, m_hStopEvent(NULL)
{
}

CUpdateScheduler::~CUpdateScheduler(void)
{
	if (m_hStopEvent != NULL)
	{
		CloseHandle(m_hStopEvent);
		m_hStopEvent = NULL;
	}
}

BOOL CUpdateScheduler::IsCanceled( DWORD dwTimeOut/*=0*/ )
{
	if( m_hStopEvent != NULL )
	{
		DWORD dwWait = ::WaitForSingleObject(m_hStopEvent, dwTimeOut);
		if( dwWait!=WAIT_TIMEOUT )
			return TRUE;
		else
			return FALSE;
	}
	return FALSE;
}

DWORD CUpdateScheduler::Stop()
{
	if (m_hStopEvent != NULL)
		::SetEvent(m_hStopEvent);
	return 0;
}

DWORD CUpdateScheduler::Main( )
{
	DWORD dwRet = 0;

	m_hStopEvent = ::CreateEvent(NULL, TRUE, FALSE, NULL);
	//
	// read the file last write date
	//
	wstring strPreferenceFile = UPUTILS::GetUpdateSettingXmlFile( m_dwProduct, TRUE );
	m_log.LogW(LL_INF, 0, L"%s: oooo strPreferenceFile=%s", __WFUNCTION__, strPreferenceFile.c_str());

	FILETIME initFTime;
	ZeroMemory( &initFTime, sizeof(initFTime) );

	WIN32_FILE_ATTRIBUTE_DATA wfd;
	ZeroMemory(&wfd, sizeof(wfd) );
	if (!GetFileAttributesEx(strPreferenceFile.c_str(), GetFileExInfoStandard, &wfd))
		m_log.LogW(LL_DET, GetLastError(), L"%s: Failed to get attributes of file %s", __WFUNCTION__, strPreferenceFile.c_str());
	else
		initFTime = wfd.ftLastWriteTime;
	
	UDP_UPDATE_SETTINGS upSettings;
	UPUTILS::ReadUpdateSettingsFromFile(strPreferenceFile, upSettings);
	int nWeekday = upSettings.scheduler.nDay;
	int nHour = upSettings.scheduler.nHour;
	int nMin = upSettings.scheduler.nMinute;
	BOOL  bDisabled = upSettings.scheduler.bDisabled;

	// dwWeekday: 0-every day, 1-sunday, 2-monday, .....7-saturday
	// dwHour: 0-12:00AM .....23-11:00PM
	nWeekday = nWeekday-1;
	DWORD dwTimeOut = TIME_INTERVAL;
	while(TRUE)
	{
		if( IsCanceled(dwTimeOut) )
		{
			m_log.LogW(LL_INF, dwRet, L"%s: Received a stop event from service. Break the loop", __WFUNCTION__ );	
			break;
		}

		//
		// if the file change since last time
		//
		strPreferenceFile = UPUTILS::GetUpdateSettingXmlFile(m_dwProduct, TRUE);

		FILETIME curFTime; 
		ZeroMemory( &curFTime, sizeof(curFTime) );
		ZeroMemory(&wfd, sizeof(wfd) );
		if( !GetFileAttributesEx( strPreferenceFile.c_str(), GetFileExInfoStandard, &wfd ) )
			m_log.LogW(LL_DET, GetLastError(), L"%s: Failed to get attributes of file %s", __WFUNCTION__, strPreferenceFile.c_str() );
		else
			curFTime = wfd.ftLastWriteTime;
		if( curFTime.dwHighDateTime!=initFTime.dwHighDateTime || curFTime.dwLowDateTime!=initFTime.dwLowDateTime )
		{
			// read scheduler
			initFTime = curFTime;

			UDP_UPDATE_SETTINGS tUPSettings;
			UPUTILS::ReadUpdateSettingsFromFile(strPreferenceFile, tUPSettings);
			nWeekday = tUPSettings.scheduler.nDay;
			nHour = tUPSettings.scheduler.nHour;
			nMin = tUPSettings.scheduler.nMinute;
			bDisabled = tUPSettings.scheduler.bDisabled;
			nWeekday = nWeekday-1;
		}

		//
		// if schedule is diabled continue to wait
		//
		if( bDisabled )
		{
			dwTimeOut = TIME_INTERVAL;
			continue;
		}

		//
		// triger an update
		//
		SYSTEMTIME ltS, ltE;
		ZeroMemory(&ltS, sizeof(ltS));
		ZeroMemory(&ltE, sizeof(ltE));
		::GetLocalTime(&ltS);

		ULONGLONG ull = 0;
		systime_2_ulonglong( ltS, ull );
		ull += TIME_INTERVAL;
		ulonglong_2_systime( ull, ltE );
		if( ( (ltS.wDayOfWeek==nWeekday || nWeekday==-1) && ltS.wHour==nHour && ltS.wMinute==nMin ) ||
			( (ltE.wDayOfWeek==nWeekday || nWeekday==-1) && ltE.wHour==nHour && ltE.wMinute==nMin) )
		{
			// triger an update
			if (m_dwProduct == ARCUPDATE_PRODUCT_AGENT)
			{
				//g_upJobManager.StartUpdateJob( ARCUPDATE_PRODUCT_AGENT, ARCUPDATE_PRODUCT_AGENT, NULL);
				m_log.LogW(LL_INF, 0, L"%s: oooo m_dwProduct == ARCUPDATE_PRODUCT_AGENT", __WFUNCTION__);
				g_upJobManager.StartUpdateBIJob(ARCUPDATE_PRODUCT_AGENT, ARCUPDATE_PRODUCT_AGENT, NULL);//added by cliicy.luo to single binaries updates
			}
			else if (m_dwProduct == ARCUPDATE_PRODUCT_FULL)
			{
				//g_upJobManager.StartUpdateJob( ARCUPDATE_PRODUCT_FULL, ARCUPDATE_PRODUCT_AGENT, NULL ); marked by cliicy.luo for test hotfix update
				//g_upJobManager.StartUpdateJob( ARCUPDATE_PRODUCT_FULL, ARCUPDATE_PRODUCT_FULL, NULL ); marked by cliicy.luo for test hotfix update
				m_log.LogW(LL_INF, 0, L"%s: oooo m_dwProduct == ARCUPDATE_PRODUCT_FULL", __WFUNCTION__);
				//this line below will download hotfix or pathes of all of the prodducts like r6.0  added by cliicy.luo 
				//the command below will not be triggered if you just click the menue item to check the update 
				g_upJobManager.StartUpdateBIJob(ARCUPDATE_PRODUCT_FULL, ARCUPDATE_PRODUCT_AGENT, NULL);//added by cliicy.luo to hotfix updates
				//10_26 marked to test
				g_upJobManager.StartUpdateBIJob(ARCUPDATE_PRODUCT_FULL, ARCUPDATE_PRODUCT_FULL, NULL);//added by cliicy.luo to hotfix updates
			}
			else if (m_dwProduct == ARCUPDATE_PRODUCT_GATEWAY)
			{
				m_log.LogW(LL_INF, 0, L"%s: oooo m_dwProduct == ARCUPDATE_PRODUCT_GATEWAY", __WFUNCTION__);
				g_upJobManager.StartUpdateJob(ARCUPDATE_PRODUCT_FULL, ARCUPDATE_PRODUCT_AGENT, NULL);
				g_upJobManager.StartUpdateJob(ARCUPDATE_PRODUCT_FULL, ARCUPDATE_PRODUCT_GATEWAY, NULL);
			}
			dwTimeOut = 5*TIME_INTERVAL;
		}
		else
		{
			dwTimeOut = TIME_INTERVAL;
		}
	}
	m_log.LogW(LL_DET, 0, L"%s: after sleep 10000 will exit", __WFUNCTION__);
	Sleep(60000);
	return 0;
}