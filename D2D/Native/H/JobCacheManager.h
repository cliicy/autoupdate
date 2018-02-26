#pragma once
#include <vector>
#include <map>
#include <string>

#include "afcorefunction.h"

using namespace std;

class AFJobCacheManager
{
public:
	DWORD UpdateNewJobRecord(void* pJobInfo, const wchar_t *uniqueID); 

	DWORD UpdatePurgeJobRecord(JobIdentify jobId, const wchar_t *uniqueID);

	DWORD SwitchCacheFile();

	DWORD DeleteCacheFileForTransfer();

	DWORD FullSyncData(wstring &FullSyncCacheFileName);

	static DWORD GetD2DSysFolder(wstring& strCacheFileDir);

	BOOLEAN	IsFirstSyncCalled();

	DWORD   MarkFirstSyncCalled();

	bool IsFullSyncFinished();

	//DWORD GetAllJobInfo2CacheFile(wstring &FullSyncCacheFileName);
	DWORD GetCacheFileForTransfer(wstring &cacheFileTransfer);

public:
	virtual DWORD GetAllJobInfo2CacheFile(wstring &FullSyncCacheFileName);
//	virtual DWORD GetCacheFileForTransfer(wstring &cacheFileTransfer);

protected:
	AFJobCacheManager();
	AFJobCacheManager(int nLockID, wstring strTypeString, wstring strInfoPath, wstring strPattern);
	DWORD setCachePath();
	BOOLEAN CacheFileFullExist();
	DWORD CreateNextCacheFile4Resync();

protected:
	virtual ~AFJobCacheManager(void);
	virtual DWORD WriteJobRecordXML(wstring jobRecordFile, void* pJobInfo, const wchar_t *uniqueID) = 0;
	virtual DWORD WriteJobRecord4PurgeXML(wstring jobRecordFile, JobIdentify jobId, const wchar_t *uniqueID) = 0;
	virtual DWORD CopyJobRecord2CacheFile(wstring jobRecordFile, wstring cacheFile, wstring strSub) = 0;

private:
	BOOLEAN	CacheFileExist();
	BOOLEAN CacheFileTransExist();

	DWORD CreateNextCacheFile(); 


	DWORD CopyCacheFile2Old();

	//DWORD InsertJobRecordXML(void* pJobInfo, const wchar_t *uniqueID);
	//DWORD InsertJobRecord4PurgeXML(JobIdentify jobId, const wchar_t *uniqueID);

	DWORD GetJobInfoInSubFolder(wstring uniqueID, int *cnt);
	DWORD GetLocalJobInfo(int *cnt);

protected:
	const int m_nLockID;
	const wstring m_strDataTypeString;
	const wstring m_strInfoPath;
	const wstring m_strPattern;


	wstring m_strD2DSysDir;
	wstring m_strDir;
	wstring m_strDirHistory;
	wstring m_cache_file;
	wstring m_firstSyncCalled;
	wstring m_cache_file_trans;
	wstring m_cache_file_full;
	wstring m_fullSyncMarkFileName;
};
