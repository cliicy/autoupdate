#pragma once
#include "AFDebugFlag.h"
#include "AFDebugStep.h"
/*
*Purpose: call this function to set debug information and this function will
* check the AFDebug.xml under the same folder with AFDebug.dll whether the debug should work.
*/
void WINAPI AFDebug(DWORD dwStep);

typedef void (WINAPI *_pfn_AFDebug)(DWORD dwStep);

extern _pfn_AFDebug g_pfnAFDebug;

extern HMODULE g_hDebugMod;

#define AF_DEBUG_DLL L"AFDebug.dll"

#define AF_DEBUG(X) if(g_hDebugMod && g_pfnAFDebug) g_pfnAFDebug(X);

class AFDebugCtrl
{
public:
    AFDebugCtrl()
    {
    }
    ~AFDebugCtrl()
    {
    }

    static void Init()
    {
        g_hDebugMod = LoadLibraryW(AF_DEBUG_DLL);
        if(g_hDebugMod)
        {
            g_pfnAFDebug = (_pfn_AFDebug)GetProcAddress(g_hDebugMod, "AFDebug");
            if(!g_pfnAFDebug)
            {
                FreeLibrary(g_hDebugMod);
                g_hDebugMod = NULL;
            }
        }
    }

    static void Unit()
    {
        if(g_hDebugMod)
        {
            FreeLibrary(g_hDebugMod);
            g_hDebugMod = NULL;
            g_pfnAFDebug = NULL;
        }
    }
};