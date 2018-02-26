#pragma once
#ifndef _SRMFACADE_H_
#define _SRMFACADE_H_
#include <cwctype>
#include <string>
#include <vector>
using namespace std;


/************************************************************************/
/* Get current module path, then return the full path by combine input 
/* file name.
/* ARGS:
/  szFileName - file name which isn't include path information
/* RETURN:
/* Always return full path string.
/************************************************************************/
wstring hlpGetModuleFullPath(wstring szFileName);

/************************************************************************/
/* Find a specfic process by name
/* ARGS:
/* strProcessName - indicated process name
/* RETURN:
/* return 0 means can't find the process; else find succeed.
/************************************************************************/
typedef struct _tag_ProcessMapItem {
    wstring procName;
    DWORD   procID;
}ProcessMapItem, *PProcessMapItem;

DWORD hlpFindProcesses(vector<ProcessMapItem> procList);

/************************************************************************/
/* convert wchar_t string to utf8 string
/************************************************************************/
std::string hlpUnicodeToUtf8(const std::wstring& widestring);
#endif