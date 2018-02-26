#ifndef _JNICONV_H
#define _JNICONV_H

#include <jni.h>
#include "Catalog.h"
#include "AFFileCatalog.h"
#include "comutil.h"
#include <atlstr.h>
#include "NativeFacade.h"
#include "AFCoreAPIInterface.h"
#include "AFMergeMgrInterface.h"
#include "setup/AgentDeploy.h"
wchar_t* JStringToWCHAR(JNIEnv* env, jstring str);
char* JStringToChar(JNIEnv *env, jstring str);
jstring WCHARToJString( JNIEnv* env, const wchar_t* str);
jstring DWORD2String(JNIEnv* env, DWORD dwValue);
wstring JStringToWString( JNIEnv *env, jstring str);
DWORD JStringToBSTR(JNIEnv *env, jstring jIn, BSTR* pBstrOut);
wchar_t* GetStringFromField(JNIEnv *env, jobject* jObj, jfieldID field);
int AddDetailW2List(JNIEnv *env, jobject* list, PDetailW pd);
void AddUINT2JRWLong(JNIEnv *env, UINT fv, jobject* toRWLong);
UINT JRWLongToUINT(JNIEnv* env, jobject* pRWLong);
void AddJobStatusTojJS(JNIEnv* env, PJobStatus pJobStatus, jobject* tojJS);
void jJSToPJobStatus(JNIEnv* env, PJobStatus pJobStatus, jobject* jJS);
void AddstringToList(JNIEnv *env, jclass jclz, wchar_t* str, jobject* arr);
void AddLongToList(JNIEnv *env, jclass jclz, jlong value, jobject* arr);
void AddIntToIntegerList( JNIEnv * pEnv, jobject listObject, int value );
void FreeAFJOBSCRIPT(PAFJOBSCRIPT pafJS);
void FreeAFCatalogJob(PAFCATALOGJOB pafJS);
int AddRestorePoint2JMap(JNIEnv *env, jobject* jRestMap, const RESTORE_POINT_ITEM * item, const wchar_t* vmInstUuid, const wchar_t* date);
int AddRestorePoint2List(JNIEnv *env, jobject* list, VRESTORE_POINT_ITEM::iterator item, wchar_t* date);
void JJobScript2AFJOBSCRIPT(JNIEnv *env, PAFJOBSCRIPT pafJS, jobject* jJobScript);
void JCatalogJob2AFCatalogJob(JNIEnv *env, PAFCATALOGJOB pafJS, jobject* jcatalogJob);
int AddVecFileInfo2List(JNIEnv *env, jobject*retArr,std::vector<FILE_INFO> &vList);
int JNetConnInfo2NET_CONN_INFO(JNIEnv *env, jobject* jNetConnInfo, NET_CONN_INFO& netConnInfo);
int AddVecString2List(JNIEnv *env, jobject*retArr,std::vector<std::wstring> &vList);
int JOB_MONITOR2JJobMonitor(JNIEnv *env, JOB_MONITOR& aJM, jobject& jobMonitor);
int D2DREGCONF2JDeployUpgrade(JNIEnv *env, D2DREGCONF& d2dConfig, jobject& deployUpgrade);
int D2DREGCONF2JDeployUpgradeWithDriver(JNIEnv *env, D2DREGCONF2& d2dConfig, jobject& deployUpgrade);
void DWORDTOJRWLong(JNIEnv *env, DWORD dwWord, jobject rwLong);
void UINT64TOJRWLong(JNIEnv *env, UINT64 val, jobject rwLong);

DWORD CheckAdminAccountValid(JNIEnv *env, jobject& account);
DWORD AFSaveAdminAccount(JNIEnv *env, jobject& account);
int convertWriterVector2AppList(JNIEnv *env, ExcludedWriterVector& excludedList, jobject& appList);
int AddVBACKUP_ITEM2List(JNIEnv *env, jobject jBkpItemArrList, const VBACKUP_ITEM& vBkpItem);
jobject LICENSE_INFO2JLicInfo(JNIEnv *env, LICENSE_INFO& licInfo);
void jList2Vector(JNIEnv *env, jobject jlist, std::vector<std::wstring> &vec);
void LogFile(TCHAR szMsg[]);
BOOL GetD2DHomePath(TCHAR * out_szD2DHomePath);
jobject Get_TestConnectionStatus(JNIEnv *env,jint iFinalStatus,_DOWNLOAD_REQUEST_STATUS_INFO & out_stDownloadRequestStatusInfo);
void AddBackupVMToList(JNIEnv *env,jobject backupVMList,VMInfoList& vmList);
void AddBackupVMDiskToList(JNIEnv *env,jobject diskList,VMDiskList& vmDiskList);
void AddHypervVMbackupDiskToList(JNIEnv *env,jobject diskList,HyperVVMDiskList& vmDiskList);
void AddHypervVMbackupNetworkAdaptersToList(JNIEnv *env, jobject adapterList, vector<VMPersistConfiguration::CHyperVVMNetworkAdapter>& vecAdapters);

void AddMsgRecW2List(JNIEnv *env, jobject* list, PMsgRecW pd);
void AddPMsgSearchRecW2List(JNIEnv *env, jobject* list, PMsgSearchRecW pd);
void MsgRecW2JMsgRec(JNIEnv *env, jobject& jmsgRec, PMsgRecW pd);

// for Exchange Discovery
void AOEServer2Item(JNIEnv *env, jobject& jExchItem, PEAOBJ_SERVER pd);
void AOEItem2Item(JNIEnv *env, jobject& jExchItem, st_exchange_item* pd);
void AOEMailbox2Item(JNIEnv *env, jobject& jExchItem, PEAOBJ_MAILBOX pd);

void Add_AOEServer2List(JNIEnv *env, jobject* list, PEAOBJ_SERVER pServer);
void Add_AOEExchangeItem2List(JNIEnv *env, jobject* list, st_exchange_item* pExchangeItem);
void Add_AOEMailbox2List(JNIEnv *env, jobject* list, PEAOBJ_MAILBOX pMailbox);


//October Sprint
void processJJobScriptStorageApplianceList(JNIEnv *env, PAFJOBSCRIPT pafJS, jobject storageApplianceList);

void FreeArchiveDiskInfo(ARCHIVEDISKDESTINFO * archiveDestDiskInfo);
void FreeArchiveCloudInfo(ARCHIVECLOUDDESTINFO * archiveDestCloudInfo);


void CatalogInfo2JCatalogInfo(JNIEnv *env, jobject& jCatalogInfo, PCATALOG_INFO pCatalogInfo);
void Add_CatalogInfo2List(JNIEnv *env, jobject* list, PCATALOG_INFO_EX pCatalogInfo);
int Convert2ApplicationStatus(JNIEnv *env,vector<CVMWriterMetadataItem> &appInfo,jobject appStatus);
int Convert2MountSession(JNIEnv *env, jobject sessionList, vector<MNT_SESS> &sessios);
int ConnInfo2JConnInfo(JNIEnv* env, jobject& jConnInfo, NET_CONN_INFO& connInfo);

int AddVMOUNT_ITEM2List(JNIEnv *env, jobject jMountItemArrList, AFMOUNTMGR::AFMOUNTINFO *pInfoList, DWORD count);
int AddVWSTRING_ITEM2List(JNIEnv *env, jobject jArrList, std::vector<wstring> vectorList);
//on demand catalog

int Add2JMountPoint(JNIEnv *env, wchar_t *szMountGUID,wchar_t *szDiskSignature, HANDLE handle,jobject jMountPoint); //added handle //20415776



void MergeControl2JMergeJobMonitor(JNIEnv *env, PST_MERGE_CTRL mergeCtrl, jobject& jmjm);
void JMergeJobScript2CMergeJS(JNIEnv *env, jobject& jmergeJS, CMergeJS& cmergeJS);
void ActiveMergeVector2JActiveMergeJobs(JNIEnv *env, jobject& jJobs, ActJobVector& jobs);

//////////////////////////////////////////////////////////////////////////////
// GoThroughJavaList() is a common procedure to go through a java list object.
// You can define your own item processor function and pass it in to handle
// each list item while going through the list.
//
// And we also provided a item processor, AddItemToWstringVector(), which adds
// items in a java List<String> object to a C++ vector<wstring> object.
//
// Pang, Bo (panbo01)
// 2012-08-23

typedef void (* PFN_LISTITEMPROCESSOR)( JNIEnv * pEnv, jobject item, LPARAM lParam );

void GoThroughJavaList( JNIEnv * pEnv, jobject javaList, PFN_LISTITEMPROCESSOR pfnProcessListItem, LPARAM lParam );
void AddItemToWstringVector( JNIEnv * pEnv, jobject item, LPARAM lParam );

// End of GoThroughJavaList()
//////////////////////////////////////////////////////////////////////////////

void AddPathsToList(JNIEnv *env, jobject pathsList, PSyncLogFiles pLogFiles, UINT count);
void JFlashJobHistoryFilter2FLASHDB_JOB_HISTORY_FILTER_COL(JNIEnv *env, jobject& jFilter, FLASHDB_JOB_HISTORY_FILTER_COL &jobFilter);
void ulonglong_2_timestr( ULONGLONG ullTime, WCHAR* pszBuffer, DWORD dwBufSize );
ULONGLONG utctime_2_local( ULONGLONG ullUtc );
void PFLASHDB_JOB_HISTORY2JFlashJobHistoryResult
	(JNIEnv *env, PFLASHDB_JOB_HISTORY pJobHistory, ULONGLONG ullCnt, ULONGLONG ullTotalCnt, jobject &jJobHistoryResult);
void JJobHistory2PFLASHDB_JOB_HISTORY( JNIEnv* env, jobject& jJobHistory, PFLASHDB_JOB_HISTORY pJobHistory );
void JActLogDetails2ACTLOG_DETAILS(JNIEnv *env, ACTLOG_DETAILS &pLD, jobject &jLogDetails);
void Convert2JRPSCatalogScriptInfo(JNIEnv *env, wstring &wsJobQPath, WSVector &vecJobScriptList, jobject &jobj);
void VectorNodeSize2JArrayList(JNIEnv *env, jobject& jArrLst, VBACKUP_NODES_SIZE& vNodeSizes);

void NodeOSInfo2JAgentOSInfo(JNIEnv* env, jobject objosver, const NODE_OS_INFO& nodeInfo);

#endif