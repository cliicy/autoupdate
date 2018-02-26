#pragma once

#include "DbgLog.h"

extern CDbgLog g_DebugLog;

#define D2DDEBUGLOG(nLevel, dwError, szFormat, ...) g_DebugLog.LogW(nLevel, dwError, szFormat, __VA_ARGS__)
#define D2D_DEBUG_LOG_FILE(bFileNameOnly)			g_DebugLog.GetGlobalLogFileName(bFileNameOnly)