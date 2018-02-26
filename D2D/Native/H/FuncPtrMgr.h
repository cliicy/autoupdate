#pragma once

#include <stdio.h>
#include <vector>
#include <string>
using namespace std;

#define FUNC_PTR_NAME(FuncPtr)              pfn ## FuncPtr
#define FUNC_PTR_VAR_NAME(FuncPtr)          m_p ## FuncPtr
#define FUNC_PTR_DECLARE(FuncPtr)           FUNC_PTR_NAME(FuncPtr) FUNC_PTR_VAR_NAME(FuncPtr)
#define FUNC_NAME(FuncPtr)                  #FuncPtr

#ifndef L
#define _L(x)                               L ## x
#define L(x)                                _L(x)
#endif

#define  FUNC_ADDR_INIT(FuncName)                                                               \
    FUNC_PTR_VAR_NAME(FuncName) = (FUNC_PTR_NAME(FuncName))GetFuncPtr(FUNC_NAME(FuncName));     \
    if (NULL == FUNC_PTR_VAR_NAME(FuncName))                                                    \
        OutPutDbgStr(L(FUNC_NAME(FuncName)) L" => Unable to find function address.");

#ifndef _OUTPUTDBGSTR_ZZ_
#define _OUTPUTDBGSTR_ZZ_
inline void OutPutDbgStr(WCHAR* pwzFormat, ...)
{
    va_list vaArgList = NULL;
    va_start(vaArgList, pwzFormat);
    DWORD dwDBGStrLen = _vscwprintf(pwzFormat, vaArgList); //ZZ: Not include '\0'
    WCHAR* pwzDBGStr = new WCHAR[dwDBGStrLen + 1];
    vswprintf_s(pwzDBGStr, dwDBGStrLen + 1, pwzFormat, vaArgList);
    OutputDebugStringW(pwzDBGStr);
    va_end(vaArgList);
    if (pwzDBGStr)
        delete []pwzDBGStr;
}
#endif

class CFuncPtrMgr
{
public:
    //ZZ: Derived class should overwrite this function to initialize function pointer.
    virtual long InitFuncPtr() = 0;

protected:
    CFuncPtrMgr(const WCHAR* pwzModuleName) : m_hResModule(NULL)
    {
        memset(m_wzModuleName, 0, sizeof(m_wzModuleName));
        if (pwzModuleName)
            wcsncpy_s(m_wzModuleName, _countof(m_wzModuleName), pwzModuleName, _TRUNCATE);
        LoadResource();
    }

    ~CFuncPtrMgr() { FreeResource(); }

    FARPROC GetFuncPtr(const CHAR* pzFuncName)
    {
        if (m_hResModule && pzFuncName)
            return GetProcAddress(m_hResModule, pzFuncName);
        return NULL;
    }

private:
    long LoadResource()
    {
        if (NULL == m_hResModule)
        {
            m_hResModule = LoadLibraryW(m_wzModuleName);
            if (NULL == m_hResModule)
            {
                //ZZ: Write some Log.
                DWORD dwLastErr = GetLastError();
                OutPutDbgStr(L"Unable to load module dll. (Name=[%s], EC=[%#08x])", m_wzModuleName, dwLastErr);
            }
            else
            {
                OutPutDbgStr(L"Succeed to load module dll. (Name=[%s])", m_wzModuleName);
            }
        }
        return 0;
    }

    void FreeResource()
    {
        if (m_hResModule)
        {
            FreeLibrary(m_hResModule);
            m_hResModule = NULL;
            OutPutDbgStr(L"Succeed to free module dll. (Name=[%s])", m_wzModuleName);
        }
    }

protected:
    HMODULE       m_hResModule;
    WCHAR         m_wzModuleName[MAX_PATH];
};