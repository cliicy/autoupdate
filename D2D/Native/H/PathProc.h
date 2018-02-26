/******************************************************************************
*
*        filename  :      PathProc.h
*        created   :      2009/12/04
*        Written by:      baide02
*        comment   :      for easily load library and get proc
*
******************************************************************************/
#ifndef _PATH_PROC_INCLUDED_
#define _PATH_PROC_INCLUDED_
#include <Windows.h>
#include <string>
using std::wstring;

#ifdef _USRDLL
extern HINSTANCE g_hInstance;
#define _HMODULE g_hInstance
#else
#define _HMODULE NULL
#endif

namespace HADT
{

//without a '\\' at tail
inline wstring ModulePath(HINSTANCE hInst)
{
    wchar_t wszPath[2048] = {0};
    GetModuleFileNameW(hInst, wszPath, _countof(wszPath));
    *(wcsrchr(wszPath, '\\')) = 0;

    return wszPath;
}

//without a '\\' at tail
inline wstring ModulePath()
{
    return ModulePath(_HMODULE);
}

inline HMODULE LoadLib(const wchar_t*  pwszLibName, HINSTANCE hInst)
{
    wstring wstrPath = ModulePath(hInst);
    wstrPath += L'\\';
    wstrPath += pwszLibName;

    return LoadLibraryW(wstrPath.c_str());
}

inline HMODULE LoadLib(const wchar_t* pwszLibName)
{
    return LoadLib(pwszLibName, _HMODULE);
}

#define LoadDll  LoadLib
#define PROC_ADDR(p,h,Name)\
    ((PVOID&)(p) = GetProcAddress(h,Name))

} //HADT

#endif //_PATH_PROC_INCLUDED_
