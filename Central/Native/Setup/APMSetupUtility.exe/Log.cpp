#include "stdafx.h"

#include <time.h>
#include "Log.h"

#define MAX_MESSAGE_SIZE		1024

static wstring logFileName;

void initLogModule( wstring logPath ){
	logFileName = logPath;
}

void LogFile(int level, const PWCHAR format, ...)
{
	if ( format == NULL || wcslen(format) == 0 )
		return;

	WCHAR message[MAX_MESSAGE_SIZE] = {0};

	va_list list;
	va_start(list, format);
	_vsnwprintf_s(message, _countof(message), _TRUNCATE, format, list);
	va_end(list);

	if ( logFileName.empty() )
	{
		return;
	}

	FILE *fp=NULL;
	if ( _wfopen_s(&fp, logFileName.c_str(), L"at,ccs=UTF-16LE") != 0 )
	{
		return;
	}

	WCHAR currentTime[MAX_PATH] = {0};
	struct tm timeStruct = {0}; 
	time_t curTime;
	time(&curTime);
	localtime_s( &timeStruct, &curTime );
	wcsftime(currentTime, _countof(currentTime), L"%c", &timeStruct);

	fwprintf_s(fp, L"%s  [%d]:  %s\n", currentTime, level, message);
	fclose(fp);
}