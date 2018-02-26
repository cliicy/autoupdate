#pragma once
#include "dbghelp.h"
#include "DbgLog.h"
#include <string>
using namespace std;

#define	SERVICE_NAME			L"CAARCUpdateSvc"
#define	SERVICE_DISPLAY_NAME	L"Arcserve UDP Auto Update Service"
#define SERVICE_DESCRIPTION		L"Arcserve UDP Auto Update Service"

//
// service routines
//
BOOL   InstallService( );

BOOL   RemoveService( );

void WINAPI ServiceMain(DWORD argc, LPTSTR *argv);

void WINAPI ServiceCtrlHandler(DWORD Opcode);

//
// for exception handler
//
LONG HandleSEH( const EXCEPTION_POINTERS* _pEx);

void GenDumpFile(WCHAR *szDumpFileName, const EXCEPTION_POINTERS *pExcept);