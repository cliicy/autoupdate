#pragma once 
#include <Windows.h>
#include <WinIoCtl.h>
#include <WinDef.h>
#include <string>
#include <vector>
using namespace std;

#ifndef MAX_NT_PATH
#define MAX_NT_PATH	1024
#endif

#ifndef MAX_GUID_LENGTH
#define MAX_GUID_LENGTH	39
#endif

typedef struct _VMwareVolumeInfo{
	TCHAR bootVolumeGUID[MAX_PATH];
	TCHAR systemVolumeGUID[MAX_PATH];
	TCHAR rootFolder[MAX_PATH];
}VMwareVolumeInfo, *PVMwareVolumeInfo;