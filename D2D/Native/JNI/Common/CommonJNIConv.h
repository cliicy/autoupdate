#ifndef _JNICONV_H
#define _JNICONV_H

#include <jni.h>
#include <atlstr.h>
#include "RpsRepJobScript.h"
#include "JobMonitor.h"
#include <BrowseFolder.h>
#include "RPSCancelJob.h"
#include "RPSCoreInterface.h"
#include "RPSCOMM\network_throttling.h"

#define SAFE_FREE(A) if(A) { free(A); (A) = NULL; }

wchar_t* JStringToWCHAR(JNIEnv* env, jstring str);
char* JStringToChar(JNIEnv *env, jstring str);
jstring WCHARToJString( JNIEnv* env, const wchar_t* str);
jstring DWORD2String(JNIEnv* env, DWORD dwValue);
wstring JStringToWString( JNIEnv *env, jstring str);
DWORD JStringToBSTR(JNIEnv *env, jstring jIn, BSTR* pBstrOut);
wchar_t* GetStringFromField(JNIEnv *env, jobject* jObj, jfieldID field);
void AddUINT2JRWLong(JNIEnv *env, UINT fv, jobject* toRWLong);
UINT JRWLongToUINT(JNIEnv* env, jobject* pRWLong);
void AddstringToList(JNIEnv *env, jclass jclz, wchar_t* str, jobject* arr);
void AddLongToList(JNIEnv *env, jclass jclz, jlong value, jobject* arr);
void AddIntToIntegerList( JNIEnv * pEnv, jobject listObject, int value );
int AddVecString2List(JNIEnv *env, jobject*retArr,std::vector<std::wstring> &vList);
void DWORDTOJRWLong(JNIEnv *env, DWORD dwWord, jobject rwLong);

void jList2Vector(JNIEnv *env, jobject jlist, std::vector<std::wstring> &vec);
void LogFile(TCHAR szMsg[]);

void AddItemToWstringVector( JNIEnv * pEnv, jobject item, LPARAM lParam );

void JReplicationJob2AFReplicationJob(JNIEnv *env, RPSRepJobScript* pJob, jobject* jJob);
void FreeAFReplicationJobScript(RPSRepJobScript* pJob);
int JOB_MONITOR2JJobMonitor(JNIEnv *env, JOB_MONITOR& aJM, jobject& jobMonitor);

int AddVecFileInfo2List(JNIEnv *env, jobject*retArr,std::vector<FILE_INFO> &vList);

void JFilterToJobFilter(JNIEnv *env, JobFilter* pFilter, jobject* jFilter);
int JNetConnInfo2NET_CONN_INFO(JNIEnv *env, jobject* jNetConnInfo, NET_CONN_INFO& netConnInfo);
void RPS_JOB_INFO2JRPSJobInfo(JNIEnv *env, RPS_JOB_INFO_S &rpsJobInfo, jobject &jRpsJobInfo);
void JRPSJobInfo2RPS_JOB_INFO(JNIEnv *env, RPS_JOB_INFO_S &rpsJobInfo, jobject &jRpsJobInfo);
void JJobScriptArchiveInfo2ArchiveInfo(JNIEnv *env, PAFARCHIVE_INFO paArchiveInfo, jobject jArchiveInfo);

void MergeControl2JMergeJobMonitor(JNIEnv *env, PST_MERGE_CTRL mergeCtrl, jobject& jmjm);
void JMergeJobScript2CMergeJS(JNIEnv *env, jobject& jmergeJS, CMergeJS& cmergeJS);
void ActiveMergeVector2JActiveMergeJobs(JNIEnv *env, jobject& jJobs, ActJobVector& jobs);
// End of GoThroughJavaList()

void AddPathsToList(JNIEnv *env, jobject pathsList, PSyncLogFiles pLogFiles, UINT count);
void JFlashJobHistoryFilter2FLASHDB_JOB_HISTORY_FILTER_COL(JNIEnv *env, jobject& jFilter, FLASHDB_JOB_HISTORY_FILTER_COL &jobFilter);
void ulonglong_2_timestr( ULONGLONG ullTime, WCHAR* pszBuffer, DWORD dwBufSize );
ULONGLONG utctime_2_local( ULONGLONG ullUtc );
void PFLASHDB_JOB_HISTORY2JFlashJobHistoryResult
	(JNIEnv *env, PFLASHDB_JOB_HISTORY pJobHistory, ULONGLONG ullCnt, ULONGLONG ullTotalCnt, jobject &jJobHistoryResult);
void Convert2JRPSCatalogScriptInfo(JNIEnv *env, wstring &wsJobQPath, WSVector &vecJobScriptList, jobject &jobj);
void ConvertJDailyScheduleDetailItemList2NETWORK_THROTTLING_POLICY(JNIEnv *env, jobject &jThrottlingSetting, NETWORK_THROTTLING_POLICY &policy);
void FreeNETWORK_THROTTLING_POLICY(NETWORK_THROTTLING_POLICY &policy);

int AddRestorePoint2List(JNIEnv *env, jobject* list, VRESTORE_POINT_ITEM::iterator item, wchar_t* date);
int AddVBACKUP_ITEM2List(JNIEnv *env, jobject jBkpItemArrList, VBACKUP_ITEM& vBkpItem);

void JJobHistory2PFLASHDB_JOB_HISTORY( JNIEnv* env, jobject& jJobHistory, PFLASHDB_JOB_HISTORY pHistory );
void JActivityLogDetail2PACTLOG_DETAILS( JNIEnv* env, jobject& jActLogDetail, PACTLOG_DETAILS pActLogDetail );
void Vector_WSTR2JStringList( JNIEnv* env, std::vector<wstring> &vecSource, jobject& jResult);
void JPurgeDataJobScript2AFPurgeDataJS(JNIEnv *env, jobject& jPurgeDataJS, PAFPURGEDATAJOBSCRIPT pAFPurgeDataJS);
//////////////////////////////////////////////////////////////////////////////

void VectorNodeSize2JArrayList(JNIEnv *env, jobject& jArrLst, VBACKUP_NODES_SIZE& vNodeSizes);
void NodeOSInfo2JAgentOSInfo(JNIEnv* env, jobject objosver, const NODE_OS_INFO& nodeInfo);

#endif