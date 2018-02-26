#pragma once
#include <string>
#include <vector>
#include "D2dbaseDef.h"

class IRangeFile 
{
public:
	virtual ~IRangeFile() = 0 {};
	virtual bool UseMem() = 0;
	virtual DWORD Size() = 0;
	virtual __int64 DataSize() = 0;
	virtual bool GetRange(DWORD index, LOCAL_ALLOCATED_RANGE& range) = 0;
	virtual bool AddRange(const LOCAL_ALLOCATED_RANGE& range) = 0;
	virtual bool FindItemByOffset(__int64 offset, LOCAL_ALLOCATED_RANGE& range) = 0;
	virtual bool GetDataSizeByOffset(__int64 llOffset, __int64& llSize) = 0;
};

#ifdef RANGEFILE_EXPORT
#define RANGEFILE_API __declspec(dllexport)
#else
#define RANGEFILE_API __declspec(dllimport)
#endif

RANGEFILE_API IRangeFile* CreateIRangeFile();


