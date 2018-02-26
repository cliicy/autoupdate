#pragma once

#include <string>

using namespace std;

#define ERRORLOG			1
#define INFOLOG				2
#define WARNINGLOG			3
#define MSGLOG				4
#define DEBUGLOG			5
		

#define LogError(format, ...)				LogFile(ERRORLOG, format, __VA_ARGS__ )
#define LogInfo(format, ...)				LogFile(INFOLOG, format, __VA_ARGS__ )
#define LogWarning(format, ...)				LogFile(WARNINGLOG, format, __VA_ARGS__ )
#define LogDebug(format, ...)				LogFile(DEBUGLOG, format, __VA_ARGS__ )

void initLogModule( wstring logPath );

void LogFile(int level, PWCHAR format, ...);