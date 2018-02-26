#pragma once

#include <string>
using std::wstring;

#include "AFDefine.h"
#include "afjob.h"


struct MergeJobHistoryXml
{
	ULONG		Version;

	ULONG		JobType;
	ULONG		JobID;

	wstring		JobName;
	wstring		JobGUID;

	wstring		ClientNode;
	wstring		PolicyUUID;

	wstring		StartTime;
	wstring		EndTime;
	wstring		JobStatus;
	LONGLONG	ProcessedSize; //in bytes
		
	wstring		SessionRootPath;
	ULONG		StartSession;
	ULONG		EndSession;

	wstring       wsPolicyGUID;
	wstring       wsPolicyName;
	
	wstring     wsSourceRPS;
	wstring     wsTargetRPS;
	wstring     wsSourceDataStore;
	wstring     wsSourceDataStoreName;
	wstring     wsTargetDataStore;
	wstring     wsTargetDataStoreName;

	GDDInformation gInfo;
};

//exported API
DWORD WINAPI AFWriteMergeJobHistoryXml(MergeJobHistoryXml & HistoryXml/*, LPCWSTR szXmlFileName*/);
DWORD WINAPI AFReadMergeJobHistoryXml(LPCWSTR pszXmlFileName, MergeJobHistoryXml & HistoryXml);