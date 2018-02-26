#pragma once

#include <windows.h>



extern WCHAR				g_pLogFile[MAX_PATH];

VOID DebugWriteStringV(CONST WCHAR * pFormat, ...);

#define DEBUG_LOG(pFormat, ...)		DebugWriteStringV(pFormat, __VA_ARGS__)



