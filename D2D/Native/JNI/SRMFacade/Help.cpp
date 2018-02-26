#include "stdafx.h"
#include "Help.h"
#include <Windows.h>
#include <Psapi.h>

wstring hlpGetModuleFullPath(wstring szFileName)
{
    wchar_t szModuleName[MAX_PATH + 1] = {0};
    wchar_t szFilePath[MAX_PATH + 1] = {0};
    GetModuleFileNameW(NULL, szModuleName, _countof(szModuleName));
    wchar_t *p = wcsrchr(szModuleName, L'\\');
    if(NULL != p) {
        *(p+1) = L'\0';
    }
    wcsncpy_s(szFilePath, _countof(szFilePath), szModuleName, _TRUNCATE);
    wcsncat_s(szFilePath, _countof(szFilePath), szFileName.c_str(), _TRUNCATE);
    return szFilePath;
}


DWORD hlpFindProcesses(vector<ProcessMapItem> procList)
{
    DWORD dwProcesses[1024] = {0}, cbNeeded = 0, cbMNeeded = 0;
    HMODULE hMods[1024]= {0};
    HANDLE hProcess = NULL;
    wchar_t szProcessName[MAX_PATH] = {0};

    if (!EnumProcesses( dwProcesses, sizeof(dwProcesses), &cbNeeded)) {
        return 1;
    }

    for(int i=0; i< (int) (cbNeeded / sizeof(DWORD)); i++) {
        //_tprintf(_T("%d\t"), aProcesses[i]);
        hProcess = OpenProcess(  PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, FALSE, dwProcesses[i]);
        EnumProcessModules(hProcess, hMods, sizeof(hMods), &cbMNeeded);
        GetModuleFileNameEx( hProcess, hMods[0], szProcessName,sizeof(szProcessName));
        
        vector<ProcessMapItem>::iterator it;
        for (it = procList.begin(); it != procList.end(); it++) {
            if(wcsstr(szProcessName, it->procName.c_str())) {
                it->procID = dwProcesses[i];
            }
        }
    }

    vector<ProcessMapItem>::iterator it;
    for (it = procList.begin(); it != procList.end(); it++) {
        if(it->procID == 0) {
            return 2; // no all process name is matched.
        }
    }

    return 0;
}

std::string hlpUnicodeToUtf8(const std::wstring& widestring)
{
#if  0
    size_t widesize = widestring.length();

    if (sizeof(wchar_t) == 2)
    {
        size_t utf8size = 3 * widesize + 1;
        std::string resultstring;
        resultstring.resize(utf8size, '\0');
        const UTF16* sourcestart = 
            reinterpret_cast<const UTF16*>(widestring.c_str());
        const UTF16* sourceend = sourcestart + widesize;
        UTF8* targetstart = reinterpret_cast<UTF8*>(&resultstring[0]);
        UTF8* targetend = targetstart + utf8size;
        ConversionResult res = ConvertUTF16toUTF8
            (&sourcestart, sourceend, &targetstart, targetend, strictConversion);
        if (res != conversionOK)
        {
            return "";
        }
        *targetstart = 0;
        return resultstring;
    }
    else if (sizeof(wchar_t) == 4)
    {
        size_t utf8size = 4 * widesize + 1;
        std::string resultstring;
        resultstring.resize(utf8size, '\0');
        const UTF32* sourcestart = 
            reinterpret_cast<const UTF32*>(widestring.c_str());
        const UTF32* sourceend = sourcestart + widesize;
        UTF8* targetstart = reinterpret_cast<UTF8*>(&resultstring[0]);
        UTF8* targetend = targetstart + utf8size;
        ConversionResult res = ConvertUTF32toUTF8
            (&sourcestart, sourceend, &targetstart, targetend, strictConversion);
        if (res != conversionOK)
        {
            return "";
        }
        *targetstart = 0;
        return resultstring;
    }
    else
    {
        return "";
    }
    return "";
#else
    int bufLen = (int)widestring.length() * sizeof(wchar_t);
    char* buf = new char[bufLen + 1];
    memset(buf, 0, bufLen+1);

    WideCharToMultiByte(CP_UTF8, 0, widestring.c_str(), (int)widestring.length(),
        buf, bufLen+1, NULL, NULL);
    string strString;
    strString.assign(buf);
    delete []buf;
    return strString;
#endif
}