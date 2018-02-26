//////////////////////////////////////////////////////////////////////
//    
// Implementation of Native methods for DataStore manager.
// wanwe13.  Added 02/2013. ----
//

#include "stdafx.h"
#include "com.ca.arcflash.rps.jni.RPSWSJNI.h" // JNI header created from RPSWSJNI.java
#include "RPSNativeFacade.h"
#include "..\Common\CommonJNIConv.h"							  
#include "..\Common\CommonUtils.h"							  
#include "errorcode.h"
#include "JNISigDefine.h"

#include "DataStore\Data_Store_Mgr_s_def.h"
#include "DataStore\Data_Store_Mgr_i_def.h"
#include <dbglog.h>

#include "ErrorStore\ErrorStoreHelper.h"

void ConvertComErrToDSErr(UINT unComErr, DWORD& dwDSErr);
void FormatDataStoreImportExceptionMessage(JNIEnv * env, int nErrCode, jstring& jstrExceptMsg, wstring strImportDSPath=L"", wstring strOwnerName=L"");
void FormatExpMessage(WCHAR* pszOutputMsg, DWORD dwBufSizeInChar, DWORD dwResID, ...);

extern CDbgLog* pLogObj;

// DataStore management functions will be called by loading library
//	prototypes copied from Data_Store_Mgr_i_def.h
typedef HRESULT ( WINAPI * FNDataStoreInitialize)();
typedef HRESULT ( WINAPI * FNDataStoreUninitialize)();
typedef HRESULT ( WINAPI * FNDataStoreAddInstance)(PDATASTORE_INSTANCE_ITEM , PDATASTORE_INSTANCE_INFO );
typedef HRESULT ( WINAPI * FNDataStoreAddInstanceOnExistingPath)(PDATASTORE_INSTANCE_ITEM, PDATASTORE_INSTANCE_INFO, PDATASTORE_INSTANCE_INFO);
typedef HRESULT ( WINAPI * FNDataStoreModifyInstance)(PDATASTORE_INSTANCE_ITEM , PDATASTORE_INSTANCE_INFO );
typedef HRESULT ( WINAPI * FNDataStoreGetInstance)(PDATASTORE_INSTANCE_ITEM , PDATASTORE_INSTANCE_INFO );
typedef HRESULT ( WINAPI * FNDataStoreReleaseAttribute)( PDATASTORE_INSTANCE_INFO );
typedef HRESULT ( WINAPI * FNDataStoreRemoveInstance)(PDATASTORE_INSTANCE_ITEM );
typedef HRESULT ( WINAPI * FNDataStoreGetInstanceStatus)(PDATASTORE_INSTANCE_ITEM , PDATASTORE_INSTANCE_STATUS );
//typedef HRESULT ( WINAPI * FNDataStoreInstRuningStatus)(PDATASTORE_INSTANCE_ITEM , PDATASTORE_INSTANCE_STATUS );
typedef HRESULT ( WINAPI * FNDataStoreReleaseStatus)( PDATASTORE_INSTANCE_STATUS );
typedef HRESULT ( WINAPI * FNDataStoreStartInstance)(PDATASTORE_INSTANCE_ITEM );
typedef HRESULT ( WINAPI * FNDataStoreStartInstanceReadonly)(PDATASTORE_INSTANCE_ITEM );
typedef HRESULT ( WINAPI * FNDataStoreStopInstance)(PDATASTORE_INSTANCE_ITEM );
typedef HRESULT ( WINAPI * FNDataStoreGetInstanceList)(PDATASTORE_INSTANCE_LIST );
typedef HRESULT ( WINAPI * FNDataStoreReleaseInstanceList)(PDATASTORE_INSTANCE_LIST );
typedef HRESULT ( WINAPI * FNDataStoreGetHashMachineConf)(PDATA_STORE_HASH_DETECT_INFO pHASH_Machine_Config);
typedef HRESULT ( WINAPI * FNDataStoreReleaseHashMachineConf)(PDATA_STORE_HASH_DETECT_INFO pHASH_Machine_Config);
typedef HRESULT ( WINAPI * FNDataStoreGetInfoFromDisk)(const wchar_t * strPath, OUT SHORT * pbIsEncrypted);
typedef HRESULT ( WINAPI * FNDataStoreGetNodeList)(PDATASTORE_INSTANCE_ITEM, pDATASTORE_NODE_LIST);
typedef HRESULT ( WINAPI * FNDataStoreReleaseNodeList)(pDATASTORE_NODE_LIST);

typedef HRESULT ( WINAPI * FNDataStoreGetImportDSConfig)(LPCWSTR, LPCWSTR, LPCWSTR, LPCWSTR,
			PDATASTORE_INSTANCE_INFO*, PDATASTORE_INSTANCE_ITEM, PDATASTORE_INSTANCE_INFO_STATIC);
typedef HRESULT ( WINAPI * FNDataStoreImportInstance)(PDATASTORE_INSTANCE_ITEM, PDATASTORE_INSTANCE_INFO);
typedef HRESULT ( WINAPI * FNDataStoreGetDSPathThreshold)(PDATASTORE_INSTANCE_ITEM, PDATA_STORE_PATH_THRESHOLD);

typedef DWORD ( WINAPI * FNGetDSBufMessage)(WCHAR* pszMessage, UINT unMsgBufSizeInChar);

typedef long (WINAPI * FNDataStoreGetMinMemSize)(const WCHAR*, unsigned long long *);
typedef long (WINAPI * FNDataSToreComposeDSSharedName)(const WCHAR *pszDSName, WCHAR *pszSharedName, UINT unSharedNameBufSizeInChar);

typedef long (WINAPI * FNDataStoreUpdateDSPathStatus)(const WCHAR*);
typedef long (WINAPI * FNDataStoreUpdateDSOverallStatus)(const WCHAR*);


// Functions pointers
FNDataStoreInitialize				fnDataStoreInitialize			= NULL;
FNDataStoreUninitialize				fnDataStoreUninitialize			= NULL;
FNDataStoreAddInstance				fnDataStoreAddInstance			= NULL;
FNDataStoreAddInstanceOnExistingPath fnDataStoreAddInstanceOnExistingPath = NULL;
FNDataStoreModifyInstance			fnDataStoreModifyInstance		= NULL;
FNDataStoreGetInstance				fnDataStoreGetInstance			= NULL;
FNDataStoreReleaseAttribute			fnDataStoreReleaseAttribute		= NULL;
FNDataStoreRemoveInstance			fnDataStoreRemoveInstance		= NULL;
FNDataStoreGetInstanceStatus		fnDataStoreGetInstanceStatus	= NULL;	
//FNDataStoreInstRuningStatus         fnDataStoreInstRuningStatus     = NULL;
FNDataStoreReleaseStatus			fnDataStoreReleaseStatus		= NULL;
FNDataStoreStartInstance			fnDataStoreStartInstance = NULL;
FNDataStoreStartInstanceReadonly	fnDataStoreStartInstanceReadonly = NULL;
FNDataStoreStopInstance				fnDataStoreStopInstance			= NULL;
FNDataStoreGetInstanceList			fnDataStoreGetInstanceList		= NULL;
FNDataStoreReleaseInstanceList		fnDataStoreReleaseInstanceList	= NULL;
FNDataStoreGetHashMachineConf		fnDataStoreGetHashMachineConf	= NULL;
FNDataStoreReleaseHashMachineConf	fnDataStoreReleaseHashMachineConf = NULL;
FNDataStoreGetInfoFromDisk			fnDataStoreGetInfoFromDisk	= NULL;
FNDataStoreGetNodeList				fnDataStoreGetNodeList			= NULL;
FNDataStoreReleaseNodeList			fnDataStoreReleaseNodeList			= NULL;
FNDataStoreGetImportDSConfig		fnDataStoreGetImportDSConfig	= NULL;
FNDataStoreImportInstance			fnDataStoreImportInstance		= NULL;
FNDataStoreGetDSPathThreshold		fnDataStoreGetDSPathThreshold	= NULL;
FNDataStoreGetMinMemSize			fnDataStoreGetMinMemSize = NULL;
FNDataSToreComposeDSSharedName		fnDataSToreComposeDSSharedName = NULL;

FNDataStoreUpdateDSPathStatus		fnDataStoreUpdateDSPathStatus = NULL;
FNDataStoreUpdateDSOverallStatus	fnDataStoreUpdateDSOverallStatus = NULL;

FNGetDSBufMessage					fnGetDSBufMessage = NULL;

// Data store APIs need be called exclusively
CRITICAL_SECTION _CriticalSection_ = { 0 };

class CDSLock {
	LPCRITICAL_SECTION m_cs;
public :
	CDSLock (LPCRITICAL_SECTION pcs ){
		m_cs = pcs;
		EnterCriticalSection (pcs);
	}
	~CDSLock (){
		LeaveCriticalSection (m_cs);
	}
};

// load the datastore manager DLL
static void _Init_DataStoreFunctions()
{
	HMODULE hGDDMGR = LoadLibrary(L"DataStoreManager");
	if ( hGDDMGR == NULL) return;

	if (!InitializeCriticalSectionAndSpinCount(&_CriticalSection_, 0x80000400))  return;

	fnDataStoreInitialize = (FNDataStoreInitialize)GetProcAddress(hGDDMGR, "DataStoreInitialize");
	fnDataStoreUninitialize = (FNDataStoreUninitialize)GetProcAddress(hGDDMGR, "DataStoreUninitialize");
	fnDataStoreAddInstance = (FNDataStoreAddInstance)GetProcAddress(hGDDMGR, "DataStoreAddInstance");
	fnDataStoreAddInstanceOnExistingPath = (FNDataStoreAddInstanceOnExistingPath)GetProcAddress(hGDDMGR, "DataStoreAddInstanceOnExistingPath");
	fnDataStoreModifyInstance = (FNDataStoreModifyInstance)GetProcAddress(hGDDMGR, "DataStoreModifyInstance");
	fnDataStoreGetInstance = (FNDataStoreGetInstance)GetProcAddress(hGDDMGR, "DataStoreGetInstance");
	fnDataStoreReleaseAttribute = (FNDataStoreReleaseAttribute)GetProcAddress(hGDDMGR, "DataStoreReleaseAttribute");
	fnDataStoreRemoveInstance = (FNDataStoreRemoveInstance)GetProcAddress(hGDDMGR, "DataStoreRemoveInstance");
	fnDataStoreGetInstanceStatus = (FNDataStoreGetInstanceStatus)GetProcAddress(hGDDMGR, "DataStoreGetInstanceStatus");
	//fnDataStoreInstRuningStatus = (FNDataStoreInstRuningStatus)GetProcAddress(hGDDMGR, "DataStoreInstRuningStatus");
	fnDataStoreReleaseStatus = (FNDataStoreReleaseStatus)GetProcAddress(hGDDMGR, "DataStoreReleaseStatus");
	fnDataStoreStartInstance = (FNDataStoreStartInstance)GetProcAddress(hGDDMGR, "DataStoreStartInstance");
	fnDataStoreStartInstanceReadonly = (FNDataStoreStartInstance)GetProcAddress(hGDDMGR, "DataStoreStartInstanceReadonly");
	fnDataStoreStopInstance   =  (FNDataStoreStopInstance)GetProcAddress(hGDDMGR, "DataStoreStopInstance");
	fnDataStoreGetInstanceList =  (FNDataStoreGetInstanceList)GetProcAddress(hGDDMGR, "DataStoreGetInstanceList");
	fnDataStoreReleaseInstanceList =  (FNDataStoreReleaseInstanceList)GetProcAddress(hGDDMGR, "DataStoreReleaseInstanceList");
	fnDataStoreGetHashMachineConf =  (FNDataStoreGetHashMachineConf)GetProcAddress(hGDDMGR, "DataStoreGetHashMachineConf");
	fnDataStoreReleaseHashMachineConf =  (FNDataStoreReleaseHashMachineConf)GetProcAddress(hGDDMGR, "DataStoreReleaseHashMachineConf");
	fnDataStoreGetNodeList =  (FNDataStoreGetNodeList)GetProcAddress(hGDDMGR, "DataStoreGetNodeList");
	fnDataStoreReleaseNodeList =  (FNDataStoreReleaseNodeList)GetProcAddress(hGDDMGR, "DataStoreReleaseNodeList");

	fnDataStoreGetImportDSConfig =  (FNDataStoreGetImportDSConfig)GetProcAddress(hGDDMGR, "GetImportDSConfig");
	fnDataStoreImportInstance =  (FNDataStoreImportInstance)GetProcAddress(hGDDMGR, "DataStoreImportInstance");
	fnDataStoreGetDSPathThreshold =  (FNDataStoreGetDSPathThreshold)GetProcAddress(hGDDMGR, "GetDSPathThreshold");
	fnDataStoreGetMinMemSize =  (FNDataStoreGetMinMemSize)GetProcAddress(hGDDMGR, "GetDedupeDSRequiredMinSize");
	fnDataSToreComposeDSSharedName =  (FNDataSToreComposeDSSharedName)GetProcAddress(hGDDMGR, "ComposeDSSharedName");

	fnDataStoreUpdateDSPathStatus =  (FNDataStoreUpdateDSPathStatus)GetProcAddress(hGDDMGR, "UpdateDSPathStatus");
	fnDataStoreUpdateDSOverallStatus =  (FNDataStoreUpdateDSOverallStatus)GetProcAddress(hGDDMGR, "UpdateDSOverallStatus");

	fnGetDSBufMessage =  (FNGetDSBufMessage)GetProcAddress(hGDDMGR, "GetDSBufMessage");
}


// |-- C 2 Java converting functions -->

static jobject _DedupSettingInfo_C2J (JNIEnv * env, PGDD_INSTANCE_CONFIG pGDDInfo, 
			PDATASTORE_INSTANCE_INFO_STATIC pInstStaticItem = NULL, 
			PDATA_STORE_PATH_THRESHOLD pdsThreshold = NULL)
{
	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/ds/DedupSettingInfo");	
	// construct a new java object
	jmethodID mid_Cons =env->GetMethodID(jcls, "<init>", "()V");
	jobject obj = env->NewObject (jcls, mid_Cons);
	if ( obj == NULL) return obj;

	// get field IDs
	jfieldID fid_NodeId = env->GetFieldID(jcls, "NodeId", "I");
// don't know if we still need this name
//	jfieldID fid_StoreName = env->GetFieldID(jcls, "StoreName", "Ljava/lang/String;");
	jfieldID fid_BlockSize = env->GetFieldID(jcls, "BlockSize", "I");
	
	jfieldID fid_HashStorePath = env->GetFieldID(jcls, "HashStorePath", "Ljava/lang/String;");
	jfieldID fid_HashStoreUser = env->GetFieldID(jcls, "HashStoreUser", "Ljava/lang/String;");
	jfieldID fid_HashStorePassword = env->GetFieldID(jcls, "HashStorePassword", "Ljava/lang/String;");

	jfieldID fid_IndexStorePath = env->GetFieldID(jcls, "IndexStorePath", "Ljava/lang/String;");
	jfieldID fid_IndexStoreUser = env->GetFieldID(jcls, "IndexStoreUser", "Ljava/lang/String;");
	jfieldID fid_IndexStorePassword = env->GetFieldID(jcls, "IndexStorePassword", "Ljava/lang/String;");

	jfieldID fid_DataStorePath = env->GetFieldID(jcls, "DataStorePath", "Ljava/lang/String;");
	jfieldID fid_DataStoreUser = env->GetFieldID(jcls, "DataStoreUser", "Ljava/lang/String;");
	jfieldID fid_DataStorePassword = env->GetFieldID(jcls, "DataStorePassword", "Ljava/lang/String;");
	
	jfieldID fid_MemorySize = env->GetFieldID(jcls, "MemorySize", "I");
	jfieldID fid_DataCapacity = env->GetFieldID(jcls, "DataCapacity", "I");
	jfieldID fid_HashRoleMode = env->GetFieldID(jcls, "HashRoleMode", "I");

	jfieldID fid_MemMinRequireSize = env->GetFieldID(jcls, "MemMinRequireSize", "J");

	jfieldID fid_IndexStoreWarnThreshold = env->GetFieldID(jcls, "IndexStoreWarnThreshold", "D");
	jfieldID fid_IndexStoreErrorThreshold = env->GetFieldID(jcls, "IndexStoreErrorThreshold", "D");
	
	jfieldID fid_HashStoreWarnThreshold = env->GetFieldID(jcls, "HashStoreWarnThreshold", "D");
	jfieldID fid_HashStoreErrorThreshold = env->GetFieldID(jcls, "HashStoreErrorThreshold", "D");
	jfieldID fid_MemoryWarnThreshold = env->GetFieldID(jcls, "MemoryWarnThreshold", "D");
	jfieldID fid_MemoryErrorThreshold = env->GetFieldID(jcls, "MemoryErrorThreshold", "D");

	jfieldID fid_DataStoreWarnThreshold = env->GetFieldID(jcls, "DataStoreWarnThreshold", "D");
	jfieldID fid_DataStoreErrorThreshold = env->GetFieldID(jcls, "DataStoreErrorThreshold", "D");



	/*jfieldID fid_PrimaryRolePort = env->GetFieldID(jcls, "PrimaryRolePort", "I");
	jfieldID fid_HashRolePort = env->GetFieldID(jcls, "HashRolePort", "I");
	jfieldID fid_DataRolePort = env->GetFieldID(jcls, "DataRolePort", "I");*/

	// set fields' value
	env->SetIntField (obj, fid_BlockSize, pGDDInfo->dwDedupeBlockSizeInKB ); // block size in KB
	env->SetObjectField ( obj, fid_HashStorePath, WCHARToJString(env, pGDDInfo->pHashAttrArray->pszHashFilePaths )); 
	env->SetObjectField ( obj, fid_HashStoreUser, WCHARToJString(env, pGDDInfo->pHashAttrArray->pszUser )); 
	env->SetObjectField ( obj, fid_HashStorePassword, WCHARToJString(env, pGDDInfo->pHashAttrArray->pszPWD)); 

	env->SetObjectField ( obj, fid_IndexStorePath, WCHARToJString(env, pGDDInfo->pPrimaryAttrArray->pszIndexFilePaths )); 
	env->SetObjectField ( obj, fid_IndexStoreUser, WCHARToJString(env, pGDDInfo->pPrimaryAttrArray->pszUser )); 
	env->SetObjectField ( obj, fid_IndexStorePassword, WCHARToJString(env, pGDDInfo->pPrimaryAttrArray->pszPWD )); 
	
	env->SetObjectField ( obj, fid_DataStorePath, WCHARToJString(env, pGDDInfo->pDataAttrArray->pszDataFilePath )); 
	env->SetObjectField ( obj, fid_DataStoreUser, WCHARToJString(env, pGDDInfo->pDataAttrArray->pszUser )); 
	env->SetObjectField ( obj, fid_DataStorePassword, WCHARToJString(env, pGDDInfo->pDataAttrArray->pszPWD )); 
	
	env->SetIntField (obj, fid_MemorySize, (int)pGDDInfo->pHashAttrArray->dw64AvailMemInMB ); 
	env->SetIntField (obj, fid_DataCapacity, (int)pGDDInfo->pHashAttrArray->dw64ExpectedUniqueDataInGB ); 
	env->SetIntField (obj, fid_HashRoleMode, pGDDInfo->pHashAttrArray->dwHashRoleMode); 

	if (pInstStaticItem!=NULL)	{
		env->SetLongField (obj, fid_MemMinRequireSize, (jlong)pInstStaticItem->dwUsedMemSize); 
	}

	if (pdsThreshold != NULL)
	{
		env->SetDoubleField(obj, fid_IndexStoreWarnThreshold, pdsThreshold->fIndexWarnThreshold); 
		env->SetDoubleField(obj, fid_IndexStoreErrorThreshold, pdsThreshold->fIndexErrorThreshold); 

		env->SetDoubleField(obj, fid_HashStoreWarnThreshold, pdsThreshold->fHashPathWarnThreshold); 
		env->SetDoubleField(obj, fid_HashStoreErrorThreshold, pdsThreshold->fHashPathErrorThreshold); 
		env->SetDoubleField(obj, fid_MemoryWarnThreshold, pdsThreshold->fHashMemWarnThreshold); 
		env->SetDoubleField(obj, fid_MemoryErrorThreshold, pdsThreshold->fHashMemErrorThreshold); 

		env->SetDoubleField(obj, fid_DataStoreWarnThreshold, pdsThreshold->fDataWarnThreshold); 
		env->SetDoubleField(obj, fid_DataStoreErrorThreshold, pdsThreshold->fDataErrorThreshold); 
	}

	/*env->SetIntField (obj, fid_PrimaryRolePort, (int)pGDDInfo->pPrimaryAttrArray->dwPort); 
	env->SetIntField (obj, fid_HashRolePort, (int)pGDDInfo->pHashAttrArray->dwPort ); 
	env->SetIntField (obj, fid_DataRolePort, (int)pGDDInfo->pDataAttrArray->dwPort); */

	return obj;
}

static jobject _DataStoreCommonInfo_C2J (JNIEnv * env, PCOMM_INSTANCE_CONFIG pCommon, 
			PDATA_STORE_PATH_THRESHOLD pdsThreshold = NULL)
{
	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/ds/DataStoreCommonInfo");	
	// construct a new java object
	jmethodID mid_Cons =env->GetMethodID(jcls, "<init>", "()V");
	jobject obj = env->NewObject (jcls, mid_Cons);
	if (obj == NULL ) return obj;

	jfieldID fid_Flags				= env->GetFieldID(jcls, "Flags", "J");
	jfieldID fid_StorePath			= env->GetFieldID(jcls, "StorePath", "Ljava/lang/String;");
	jfieldID fid_User				= env->GetFieldID(jcls, "User", "Ljava/lang/String;");
	jfieldID fid_Password			= env->GetFieldID(jcls, "Password", "Ljava/lang/String;");
	jfieldID fid_StoreSharedName	= env->GetFieldID(jcls, "StoreSharedName", "Ljava/lang/String;");
	jfieldID fid_WarnThreshold		= env->GetFieldID(jcls, "WarnThreshold", "D");
	jfieldID fid_ErrorThreshold		= env->GetFieldID(jcls, "ErrorThreshold", "D");

	env->SetLongField   (obj, fid_Flags, (jlong) pCommon->dwFlags); // block size in KB
	env->SetObjectField (obj, fid_StorePath, WCHARToJString(env, pCommon->pszStorePath)); 
	env->SetObjectField (obj, fid_User, WCHARToJString(env, pCommon->pszUser)); 
	env->SetObjectField (obj, fid_Password, WCHARToJString(env, pCommon->pszPWD)); 
	env->SetObjectField (obj, fid_StoreSharedName, WCHARToJString(env, pCommon->pszStoreSharedName)); 
	if (pdsThreshold!=NULL)
	{
		env->SetDoubleField (obj, fid_WarnThreshold, pdsThreshold->fCommonWarnThreshold);
		env->SetDoubleField (obj, fid_ErrorThreshold, pdsThreshold->fCommonErrorThreshold);
	}

	return obj;
}


static jobject _DataStoreSettingInfo_C2J (JNIEnv * env, PDATASTORE_INSTANCE_ITEM pInstItem, 
	PDATASTORE_INSTANCE_INFO pDSInst, PDATASTORE_INSTANCE_INFO_STATIC pInstStaticItem = NULL, 
	PDATA_STORE_PATH_THRESHOLD pdsThreshold = NULL)
{
	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/ds/DataStoreSettingInfo");	
	// construct a new java object
	jmethodID mid_Cons =env->GetMethodID(jcls, "<init>", "()V");
	jobject obj = env->NewObject (jcls, mid_Cons);
	if (obj == NULL ) return obj;

	jfieldID fid_Flags				= env->GetFieldID(jcls, "Flags", "J");
	jfieldID fid_EnableGDD			= env->GetFieldID(jcls, "EnableGDD", "J");
	jfieldID fid_EnableEncryption	= env->GetFieldID(jcls, "EnableEncryption", "J");
	jfieldID fid_EncryptionMethod	= env->GetFieldID(jcls, "EncryptionMethod", "I");
	jfieldID fid_EncryptionPwd		= env->GetFieldID(jcls, "EncryptionPwd", "Ljava/lang/String;");
	jfieldID fid_EnableCompression	= env->GetFieldID(jcls, "EnableCompression", "J");
	jfieldID fid_CompressionMethod	= env->GetFieldID(jcls, "CompressionMethod", "I");
	jfieldID fid_MaxNodeCount	= env->GetFieldID(jcls, "maxNodeCount", "I");
	jfieldID fid_dsVersion	= env->GetFieldID(jcls, "version", SIG_LONG);
	jfieldID fid_ownershipHostName= env->GetFieldID(jcls, "ownershipHostName", "Ljava/lang/String;");
	jfieldID fid_ownershipHostSID = env->GetFieldID(jcls, "ownershipHostSID", "Ljava/lang/String;");
	jfieldID fid_Datastore_name	= env->GetFieldID(jcls, "datastore_name", "Ljava/lang/String;");
	jfieldID fid_Datastore_id	= env->GetFieldID(jcls, "datastore_id", "I");
	jfieldID fid_Node_id		= env->GetFieldID(jcls, "node_id", "I");
	jfieldID fid_displayName	= env->GetFieldID(jcls, "displayName", "Ljava/lang/String;");
	jfieldID fid_DSCommSetting	= env->GetFieldID(jcls, "DSCommSetting", "Lcom/ca/arcflash/rps/webservice/data/ds/DataStoreCommonInfo;");
	jfieldID fid_GDDSetting 	= env->GetFieldID(jcls, "GDDSetting", "Lcom/ca/arcflash/rps/webservice/data/ds/DedupSettingInfo;");

	env->SetLongField   (obj, fid_Flags,    (jlong) pDSInst->dwFlags); 
	env->SetLongField   (obj, fid_EnableGDD,    (jlong) pDSInst->dwEnableGDD); 
	env->SetLongField   (obj, fid_EnableEncryption, (jlong) pDSInst->dwEnableEncry);
	env->SetIntField   (obj, fid_EncryptionMethod, (jint) pDSInst->dwEncryMethod);
	env->SetIntField   (obj, fid_MaxNodeCount, (jint) pDSInst->dwMaxNodeCount);
	env->SetLongField   (obj, fid_dsVersion, (jlong) pDSInst->dwDSVersion);
	env->SetObjectField (obj, fid_EncryptionPwd, WCHARToJString(env,pDSInst->pszEncryPwd ));
	env->SetLongField   (obj, fid_EnableCompression, (jlong) pDSInst->dwEnableCompress);
	env->SetIntField   (obj, fid_CompressionMethod, (jint) pDSInst->dwCompressMethod);
	env->SetObjectField (obj, fid_Datastore_name, WCHARToJString(env,pInstItem->InsName ));
	env->SetObjectField (obj, fid_displayName, WCHARToJString(env,pInstItem->DispName ));  
	if (pInstStaticItem!=NULL){
		env->SetObjectField (obj, fid_ownershipHostName, WCHARToJString(env,pInstStaticItem->szOwnerName ));  
		env->SetObjectField (obj, fid_ownershipHostSID, WCHARToJString(env,pInstStaticItem->szOwnerSID)); 
	}
	env->SetIntField    (obj, fid_Datastore_id, (jint) 0); //??
	env->SetIntField    (obj, fid_Node_id , (jint) 0); //??

	jobject obj0 = NULL;
	if ( pDSInst->pCommInstanceCfg != NULL){ //make sure this is a valid pointer
		obj0 = _DataStoreCommonInfo_C2J (env, pDSInst->pCommInstanceCfg, pdsThreshold);
		env->SetObjectField (obj, fid_DSCommSetting, obj0);
	}

	if ( pDSInst->pGDDInstanceCfg != NULL){ // make suer this is a valid pointer
		obj0 = _DedupSettingInfo_C2J(env, pDSInst->pGDDInstanceCfg, pInstStaticItem, pdsThreshold);
		env->SetObjectField (obj, fid_GDDSetting, obj0);
	}

	return obj;

}


static jobject _DataStoreNodeInfo_C2J (JNIEnv * env, pDATASTORE_NODE_ITEM pNodeInfo, const WCHAR* pszDataStorPath)
{
	jclass jcls = env->FindClass("com/ca/arcflash/webservice/data/restore/BackupDataStoreD2D");
	
	// construct a new java object
	jmethodID mid_Cons =env->GetMethodID(jcls, "<init>", "()V");
	jobject obj = env->NewObject (jcls, mid_Cons);
	if ( !obj  ) return NULL;

	jfieldID fid_hostname			= env->GetFieldID(jcls, "hostname", "Ljava/lang/String;");
	jfieldID fid_D2DSid				= env->GetFieldID(jcls, "D2DSid", "Ljava/lang/String;");
	jfieldID fid_vmUUID				= env->GetFieldID(jcls, "vmUUID", SIG_STRING);
	jfieldID fid_IsVM				= env->GetFieldID(jcls, "vm", SIG_BOOLEAN);
	jfieldID fid_IsIntegrity		= env->GetFieldID(jcls, "isIntegrity", SIG_BOOLEAN);
	jfieldID fid_fullBackupDestination	= env->GetFieldID(jcls, "fullBackupDestination", "Ljava/lang/String;");
	jfieldID fid_LastBackupTime  = env->GetFieldID(jcls, "lastBackupTime", "J");
	jfieldID fid_MSPusername		= env->GetFieldID(jcls, "username", SIG_STRING);
	jfieldID fid_destPlanName		= env->GetFieldID(jcls, "destPlanName", SIG_STRING);
	jfieldID fid_sourceRPSServerName= env->GetFieldID(jcls, "sourceRPSServerName", SIG_STRING);

	jfieldID fid_rawDataSizeB= env->GetFieldID(jcls, "rawDataSizeB", SIG_LONG);
	jfieldID fid_dataSizeB= env->GetFieldID(jcls, "dataSizeB", SIG_LONG);
	jfieldID fid_metadataSizeB= env->GetFieldID(jcls, "metadataSizeB", SIG_LONG);
	jfieldID fid_catalogSizeB= env->GetFieldID(jcls, "catalogSizeB", SIG_LONG);
	jfieldID fid_grtCatalogSizeB= env->GetFieldID(jcls, "grtCatalogSizeB", SIG_LONG);
	
	wstring strNodeFullPath; 

	if (pszDataStorPath != NULL)
	{
		strNodeFullPath = pszDataStorPath;
		strNodeFullPath += L"\\";
		strNodeFullPath += pNodeInfo->szNodeName;
		if (wcslen(pNodeInfo->szNodeID)>0)
		{
			strNodeFullPath += L"[";
			strNodeFullPath += pNodeInfo->szNodeID;
			strNodeFullPath += L"]";
		}
	}
	
	env->SetObjectField (obj, fid_hostname, WCHARToJString(env, pNodeInfo->szNodeName));
	env->SetObjectField (obj, fid_D2DSid, WCHARToJString(env, pNodeInfo->szNodeID));
	env->SetObjectField (obj, fid_vmUUID, WCHARToJString(env, pNodeInfo->szVMInstanceID));
	env->SetObjectField (obj, fid_fullBackupDestination, WCHARToJString(env, strNodeFullPath.c_str()));
	env->SetBooleanField(obj, fid_IsVM, (jboolean)pNodeInfo->dwIsVM);
	env->SetLongField ( obj, fid_LastBackupTime, (jlong)pNodeInfo->dwLastBackupTime); 

	if(fid_IsIntegrity)
		env->SetBooleanField (obj, fid_IsIntegrity, (jboolean)pNodeInfo->dwIntegrity);

	env->SetObjectField (obj, fid_MSPusername, WCHARToJString(env, pNodeInfo->szMSPUser));
	env->SetObjectField (obj, fid_destPlanName, WCHARToJString(env, pNodeInfo->szDstPlanName));
	env->SetObjectField (obj, fid_sourceRPSServerName, WCHARToJString(env, pNodeInfo->szSrcRPSName));

	env->SetLongField ( obj, fid_rawDataSizeB, (jlong)pNodeInfo->dwTransferDataSize); 
	env->SetLongField ( obj, fid_dataSizeB, (jlong)pNodeInfo->dwDataSize); 
	env->SetLongField ( obj, fid_metadataSizeB, (jlong)pNodeInfo->dwCommonPathSize); 
	env->SetLongField ( obj, fid_catalogSizeB, (jlong)pNodeInfo->dwCatalogSize); 
	env->SetLongField ( obj, fid_grtCatalogSizeB, (jlong)pNodeInfo->dwGrtCatalogSize); 
	
	return obj;
}


static jobject _DataRoleStatus_C2J (JNIEnv * env, PGDD_INST_DATA_ROLE_STATUS pStat )
{
	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/datastore/DedupDataRoleStatus");	
	// get field IDs
	jfieldID fid_state     = env->GetFieldID(jcls, "RoleCurrentState", "I");
	jfieldID fid_totalSize = env->GetFieldID(jcls, "DataVolumeTotalSize", "J");
	jfieldID fid_freeSize  = env->GetFieldID(jcls, "DataVolumeFreeSize", "J");
	jfieldID fid_usedSize  = env->GetFieldID(jcls, "DataVolumeUsedSize", "J");
	jfieldID fid_dirSize   = env->GetFieldID(jcls, "DataDirSize", "J");
	// construct a new java object
	jmethodID mid_Cons =env->GetMethodID(jcls, "<init>", "()V");
	jobject obj = env->NewObject (jcls, mid_Cons);
	if ( !obj  ) return NULL;
	// set field value
	env->SetIntField  ( obj, fid_state,  (jint)pStat->dwRoleCurrentState); // block size in KB
	env->SetLongField ( obj, fid_totalSize,(jlong)pStat->dw64DataVolumeTotalSize); 
	env->SetLongField ( obj, fid_freeSize, (jlong)pStat->dw64DataVolumeFreeSize); 
	env->SetLongField ( obj, fid_usedSize, (jlong)pStat->dw64DataVolumeUsedSize); 
	env->SetLongField ( obj, fid_dirSize, (jlong)pStat->dw64DataDirSize); 

	return obj;
}

static jobject _HashRoleStatus_C2J (JNIEnv * env, PGDD_INST_HASH_ROLE_STATUS pStat )
{
	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/datastore/DedupHashRoleStatus");	
	// get field IDs
	jfieldID fid_state     = env->GetFieldID(jcls, "RoleCurrentState", "I");
	jfieldID fid_totalSize = env->GetFieldID(jcls, "HashVolumeTotalSize", "J");
	jfieldID fid_freeSize  = env->GetFieldID(jcls, "HashVolumeFreeSize", "J");
	jfieldID fid_usedSize  = env->GetFieldID(jcls, "HashVolumeUsedSize", "J");
	jfieldID fid_dirSize   = env->GetFieldID(jcls, "HashDirSize", "J");
	jfieldID fid_dirMinReqSize = env->GetFieldID(jcls, "HashDirMinRequireSize", "J");
	jfieldID fid_memTotal  = env->GetFieldID(jcls, "MemTotalSize", "J");
	jfieldID fid_memAvail  = env->GetFieldID(jcls, "MemAvailSize", "J");
	jfieldID fid_memUsed   = env->GetFieldID(jcls, "MemUsedByHashRole", "J");
	jfieldID fid_memMinReqSize = env->GetFieldID(jcls, "MemMinRequireSize", "J");
	// construct a new java object
	jmethodID mid_Cons =env->GetMethodID(jcls, "<init>", "()V");
	jobject obj = env->NewObject (jcls, mid_Cons);
	if ( !obj  ) return NULL;
	// set field value
	env->SetIntField  ( obj, fid_state,    (jint)pStat->dwRoleCurrentState); 
	env->SetLongField ( obj, fid_totalSize,(jlong)pStat->dw64HashVolumeTotalSize); 
	env->SetLongField ( obj, fid_freeSize, (jlong)pStat->dw64HashVolumeFreeSize); 
	env->SetLongField ( obj, fid_usedSize, (jlong)pStat->dw64HashVolumeUsedSize); 
	env->SetLongField ( obj, fid_dirSize,  (jlong)pStat->dw64HashDirSize); 
	env->SetLongField ( obj, fid_dirMinReqSize,  (jlong)pStat->dw64HashDirMinRequireSize); 
	env->SetLongField ( obj, fid_memTotal, (jlong)pStat->dw64MemTotalSize); 
	env->SetLongField ( obj, fid_memAvail, (jlong)pStat->dw64MemAvailSize); 
	env->SetLongField ( obj, fid_memUsed,  (jlong)pStat->dw64MemUsedByHashRole); 
	env->SetLongField ( obj, fid_memMinReqSize,  (jlong)pStat->dw64MemMinRequireSize); 

	return obj;
}
	
static jobject _PrimaryRoleStatus_C2J (JNIEnv * env, PGDD_INST_PRIMARY_ROLE_STATUS pStat )
{
	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/datastore/DedupPrimaryRoleStatus");	
	// get field IDs
	jfieldID fid_state     = env->GetFieldID(jcls, "RoleCurrentState", "I");
	jfieldID fid_jobStatus = env->GetFieldID(jcls, "JobStatus", "I");
	jfieldID fid_totalSize  = env->GetFieldID(jcls, "IndexVolumeTotalSize", "J");
	jfieldID fid_freeSize   = env->GetFieldID(jcls, "IndexVolumeFreeSize", "J");
	jfieldID fid_usedSize   = env->GetFieldID(jcls, "IndexVolumeUsedSize", "J");
	jfieldID fid_dirSize   = env->GetFieldID(jcls, "IndexDirSize", "J");
	// construct a new java object
	jmethodID mid_Cons =env->GetMethodID(jcls, "<init>", "()V");
	jobject obj = env->NewObject (jcls, mid_Cons);
	if ( !obj  ) return NULL;
	// set field value
	env->SetIntField  ( obj, fid_state,     (jint)pStat->dwRoleCurrentState); 
	env->SetIntField  ( obj, fid_jobStatus, (jint)pStat->dwJobStatus); 
	env->SetLongField ( obj, fid_totalSize, (jlong)pStat->dw64IndexVolumeTotalSize); 
	env->SetLongField ( obj, fid_freeSize,  (jlong)pStat->dw64IndexVolumeFreeSize); 
	env->SetLongField ( obj, fid_usedSize,  (jlong)pStat->dw64IndexVolumeUsedSize); 
	env->SetLongField ( obj, fid_dirSize,   (jlong)pStat->dw64IndexDirSize); 

	return obj;
}

static jobject _DedupInstanceStatus_C2J (JNIEnv * env, PDATA_STORE_GDD_INST_STATUS pStat )
{
	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/datastore/DedupInstanceStatus");	
	// get field IDs
	jfieldID fid_flags     = env->GetFieldID(jcls, "Flags", "J");
	jfieldID fid_primaryRole   = env->GetFieldID(jcls, "PrimaryRoleStatus", "Lcom/ca/arcflash/rps/webservice/data/datastore/DedupPrimaryRoleStatus;");
	jfieldID fid_hashRole   = env->GetFieldID(jcls, "HashRoleStatusArray", "[Lcom/ca/arcflash/rps/webservice/data/datastore/DedupHashRoleStatus;");
	jfieldID fid_dataRole   = env->GetFieldID(jcls, "DataRoleStatusArray", "[Lcom/ca/arcflash/rps/webservice/data/datastore/DedupDataRoleStatus;");
	// construct a new java object
	jmethodID mid_Cons =env->GetMethodID(jcls, "<init>", "()V");
	jobject obj = env->NewObject (jcls, mid_Cons);
	if ( !obj  ) return NULL;
	// set field value
	env->SetLongField ( obj, fid_flags,     (jlong)pStat->dwFlags);

	// set primary role;
	jobject jrole = _PrimaryRoleStatus_C2J ( env, pStat->pPrimaryRoleStatus);
	env->SetObjectField ( obj, fid_primaryRole, jrole );

	DWORD index;
	// set hash role;
	jclass jcls_hashrole = env->FindClass("com/ca/arcflash/rps/webservice/data/datastore/DedupHashRoleStatus");	
	jobjectArray jarray_hashrole = env->NewObjectArray( pStat->dwHashRoleNumber, jcls_hashrole, NULL);
	for ( index=0; index<pStat->dwHashRoleNumber; index++)
	{
		jrole = _HashRoleStatus_C2J (env, pStat->pHashRoleStatusArray + index );
		env->SetObjectArrayElement (jarray_hashrole, index, jrole );
	}
	env->SetObjectField ( obj, fid_hashRole, jarray_hashrole );


	// set data role;
	jclass jcls_datarole = env->FindClass("com/ca/arcflash/rps/webservice/data/datastore/DedupDataRoleStatus");	
	jobjectArray jarray_datarole = env->NewObjectArray( pStat->dwDataRoleNumber, jcls_datarole, NULL);
	for ( index=0; index<pStat->dwDataRoleNumber; index++)
	{
		jrole = _DataRoleStatus_C2J (env, pStat->pDataRoleStatusArray + index );
		env->SetObjectArrayElement (jarray_datarole, index, jrole );
	}
	env->SetObjectField ( obj, fid_dataRole, jarray_datarole );

	return obj;
}

jobject _DataStoreCommonStatus_C2J (JNIEnv * env, PDATA_STORE_COMMPATH_STATUS pStat )
{
	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/datastore/DataStoreCommonStatus");	
	// construct a new java object
	jmethodID mid_Cons =env->GetMethodID(jcls, "<init>", "()V");
	jobject obj = env->NewObject (jcls, mid_Cons);
	if ( !obj  ) return NULL;
	// get field IDs
	jfieldID fid_DataVolumeTotalSize = env->GetFieldID(jcls, "DataVolumeTotalSize", "J");
	jfieldID fid_DataVolumeFreeSize  = env->GetFieldID(jcls, "DataVolumeFreeSize", "J");
	jfieldID fid_DataVolumeUsedSize  = env->GetFieldID(jcls, "DataVolumeUsedSize", "J");
	jfieldID fid_DataDirSize         = env->GetFieldID(jcls, "DataDirSize", "J");
	// set field value
	env->SetLongField ( obj, fid_DataVolumeTotalSize, (jlong)pStat->dw64CommPathVolumeTotalSize); 
	env->SetLongField ( obj, fid_DataVolumeFreeSize,  (jlong)pStat->dw64CommPathVolumeFreeSize); 
	env->SetLongField ( obj, fid_DataVolumeUsedSize,  (jlong)pStat->dw64CommPathVolumeUsedSize); 
	env->SetLongField ( obj, fid_DataDirSize,		  (jlong)pStat->dw64CommPathDirSize);
	
	return obj;
}

jobject _DataStoreStatus_C2J  (JNIEnv * env, PDATASTORE_INSTANCE_STATUS pStat )
{
	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/datastore/DataStoreStatus");	
	// construct a new java object
	jmethodID mid_Cons =env->GetMethodID(jcls, "<init>", "()V");
	jobject obj = env->NewObject (jcls, mid_Cons);
	if ( !obj  ) return NULL;

	// get field IDs
	jfieldID fid_Flags			= env->GetFieldID(jcls, "Flags", "J");
	jfieldID fid_EnableGDD		= env->GetFieldID(jcls, "EnableGDD", "J");
	jfieldID fid_OverallStatus  = env->GetFieldID(jcls, "OverallStatus", "J");
	jfieldID fid_StatusErrorCode  = env->GetFieldID(jcls, "StatusErrorCode", "J");
	jfieldID fid_TimeStamp  = env->GetFieldID(jcls, "TimeStamp", "J");

	jfieldID fid_statusErrMsg = env->GetFieldID(jcls, "StatusErrorMessage", "Ljava/lang/String;");
	jfieldID fid_elapsedTime  = env->GetFieldID(jcls, "elapsedTimeInSec", "J");
	jfieldID fid_remainTime  = env->GetFieldID(jcls, "estRemainTimeInSec", "J");

	jfieldID fid_totalSrcDataSize  = env->GetFieldID(jcls, "totalSrcDataSize", "J");
	jfieldID fid_uniqueDataSize  = env->GetFieldID(jcls, "uniqueDataSize", "J");
	jfieldID fid_compressDataSize  = env->GetFieldID(jcls, "compressDataSize", "J");

	jfieldID fid_CommonStoreStatus   = env->GetFieldID(jcls, "CommonStoreStatus", "Lcom/ca/arcflash/rps/webservice/data/datastore/DataStoreCommonStatus;");
	jfieldID fid_GDDStoreStatus      = env->GetFieldID(jcls, "GDDStoreStatus", "Lcom/ca/arcflash/rps/webservice/data/datastore/DedupInstanceStatus;");

	// set field value
	env->SetLongField ( obj, fid_Flags,			(jlong)pStat->dwFlags); 
	env->SetLongField ( obj, fid_EnableGDD,		(jlong)pStat->dwEnableGDD); 
	env->SetLongField ( obj, fid_OverallStatus,	(jlong)pStat->dwOverallStatus); 
	env->SetLongField ( obj, fid_StatusErrorCode,	(jlong)pStat->dwStatusErrorCode); 
	env->SetLongField ( obj, fid_TimeStamp,	(jlong)pStat->dwTimeStamp); 

	env->SetObjectField ( obj, fid_statusErrMsg, WCHARToJString(env, L"")); 
	env->SetLongField ( obj, fid_elapsedTime,	(jlong)pStat->stInitProgTime.dwElapsedTimeInSec); 
	env->SetLongField ( obj, fid_remainTime,	(jlong)pStat->stInitProgTime.dwEstRemainTimeInSec); 

	env->SetLongField ( obj, fid_totalSrcDataSize, (jlong)pStat->dwTotalSrcDataSize); 
	env->SetLongField ( obj, fid_uniqueDataSize, (jlong)pStat->dwUniqueDataSize); 
	env->SetLongField ( obj, fid_compressDataSize, (jlong)pStat->dwCompressDataSize); 

	jobject obj0;
	if ( pStat->pCommStoreStatus ){
		obj0 = _DataStoreCommonStatus_C2J (env, pStat->pCommStoreStatus);
		env->SetObjectField (obj, fid_CommonStoreStatus, obj0);
	}

	if (pStat->pGDDStoreStatus){
		obj0 = _DedupInstanceStatus_C2J (env, pStat->pGDDStoreStatus);
		env->SetObjectField (obj, fid_GDDStoreStatus, obj0);
	}

	return obj;

}

// <-- C 2 Java converting functions --|

// |-- Java 2 C converting functions -->

// please ensure 'env', 'obj', 'jstr', 'wstr' are available when using this macro
// string will be stored in  wstr;
#define GET_STRING_FIELD( fid ) \
	jstr = env->GetObjectField ( obj, (fid)); \
	wstr = JStringToWString(env, (jstring)jstr);

static long _DedupSettingInfo_J2C(JNIEnv * env, jobject obj, PGDD_INSTANCE_CONFIG pGDDInfo )
{
	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/ds/DedupSettingInfo");	

	// get field IDs
	jfieldID fid_NodeId = env->GetFieldID(jcls, "NodeId", "I");
	jfieldID fid_StoreName = env->GetFieldID(jcls, "StoreName", "Ljava/lang/String;");
	jfieldID fid_BlockSize = env->GetFieldID(jcls, "BlockSize", "I");
	jfieldID fid_HashStorePath = env->GetFieldID(jcls, "HashStorePath", "Ljava/lang/String;");
	jfieldID fid_HashStoreUser = env->GetFieldID(jcls, "HashStoreUser", "Ljava/lang/String;");
	jfieldID fid_HashStorePassword = env->GetFieldID(jcls, "HashStorePassword", "Ljava/lang/String;");

	jfieldID fid_IndexStorePath = env->GetFieldID(jcls, "IndexStorePath", "Ljava/lang/String;");
	jfieldID fid_IndexStoreUser = env->GetFieldID(jcls, "IndexStoreUser", "Ljava/lang/String;");
	jfieldID fid_IndexStorePassword = env->GetFieldID(jcls, "IndexStorePassword", "Ljava/lang/String;");

	jfieldID fid_DataStorePath = env->GetFieldID(jcls, "DataStorePath", "Ljava/lang/String;");
	jfieldID fid_DataStoreUser = env->GetFieldID(jcls, "DataStoreUser", "Ljava/lang/String;");
	jfieldID fid_DataStorePassword = env->GetFieldID(jcls, "DataStorePassword", "Ljava/lang/String;");

	jfieldID fid_MemorySize = env->GetFieldID(jcls, "MemorySize", "I");
	jfieldID fid_DataCapacity = env->GetFieldID(jcls, "DataCapacity", "I");
	jfieldID fid_HashRoleMode = env->GetFieldID(jcls, "HashRoleMode", "I");

	jobject jstr = NULL;
	wstring wstr;

//	GET_STRING_FIELD (fid_StoreName, pInstance->InsName, sizeof(pInstance->InsName));
	pGDDInfo->dwDedupeBlockSizeInKB = env->GetIntField (obj, fid_BlockSize);
	
	// need to be freed by caller
	pGDDInfo->pHashAttrArray = (PGDD_HASH_ROLE_ATTr)calloc ( 1, sizeof (GDD_HASH_ROLE_ATTr) ); 
	pGDDInfo->dwHashRoleNumber = 1;

	GET_STRING_FIELD (fid_HashStorePath);
	pGDDInfo->pHashAttrArray->pszHashFilePaths = (PWCHAR)calloc ( wstr.size() + 2, sizeof(WCHAR));
	std::copy(wstr.begin(), wstr.end(), pGDDInfo->pHashAttrArray->pszHashFilePaths);
	pGDDInfo->pHashAttrArray->dwlengthHashFilePath = (DWORD)wstr.size() + 2;
	
	GET_STRING_FIELD (fid_HashStoreUser);
	pGDDInfo->pHashAttrArray->pszUser = _wcsdup(wstr.c_str());
	GET_STRING_FIELD (fid_HashStorePassword);
	pGDDInfo->pHashAttrArray->pszPWD = _wcsdup(wstr.c_str());

	pGDDInfo->pHashAttrArray->dw64AvailMemInMB = env->GetIntField (obj, fid_MemorySize); 
	pGDDInfo->pHashAttrArray->dw64ExpectedUniqueDataInGB = env->GetIntField (obj, fid_DataCapacity); 
	pGDDInfo->pHashAttrArray->dwHashRoleMode = env->GetIntField (obj, fid_HashRoleMode); 

	pGDDInfo->pPrimaryAttrArray = (PGDD_PRIMARY_ROLE_ATTr)calloc ( 1, sizeof (GDD_PRIMARY_ROLE_ATTr) ); // need to free by caller
	pGDDInfo->dwPrimayRoleNumber = 1;

	GET_STRING_FIELD (fid_IndexStorePath);
	pGDDInfo->pPrimaryAttrArray->pszIndexFilePaths = (PWCHAR)calloc ( wstr.size() + 2, sizeof(WCHAR));
	std::copy(wstr.begin(), wstr.end(), pGDDInfo->pPrimaryAttrArray->pszIndexFilePaths);
	pGDDInfo->pPrimaryAttrArray->dwlengthIndexFilePath = (DWORD)(wstr.size() + 2);

	GET_STRING_FIELD (fid_IndexStoreUser);
	pGDDInfo->pPrimaryAttrArray->pszUser = _wcsdup(wstr.c_str());
	GET_STRING_FIELD (fid_IndexStorePassword);
	pGDDInfo->pPrimaryAttrArray->pszPWD = _wcsdup(wstr.c_str());

	pGDDInfo->pDataAttrArray = (PGDD_DATA_ROLE_ATTr) calloc ( 1, sizeof (GDD_DATA_ROLE_ATTr) ); // need to free by caller
	pGDDInfo->dwDataRoleNumber = 1;

	GET_STRING_FIELD (fid_DataStorePath);
	pGDDInfo->pDataAttrArray->pszDataFilePath = (PWCHAR)calloc ( wstr.size() + 2, sizeof(WCHAR));
	std::copy(wstr.begin(), wstr.end(), pGDDInfo->pDataAttrArray->pszDataFilePath);
	pGDDInfo->pDataAttrArray->dwlengthDataFilePath = (DWORD)(wstr.size() + 2);

	GET_STRING_FIELD (fid_DataStoreUser);
	pGDDInfo->pDataAttrArray->pszUser = _wcsdup(wstr.c_str());
	GET_STRING_FIELD (fid_DataStorePassword);
	pGDDInfo->pDataAttrArray->pszPWD = _wcsdup(wstr.c_str());



	return 0;

}

// string pointer fields in pCommon will be allocated new buff, 
// need to be freed in caller
static long _DataStoreCommonInfo_J2C (JNIEnv * env, jobject obj, PCOMM_INSTANCE_CONFIG pCommon)
{
	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/ds/DataStoreCommonInfo");	

	jfieldID fid_Flags		= env->GetFieldID(jcls, "Flags", "J");
	jfieldID fid_StorePath	= env->GetFieldID(jcls, "StorePath", "Ljava/lang/String;");
	jfieldID fid_User		= env->GetFieldID(jcls, "User", "Ljava/lang/String;");
	jfieldID fid_Password	= env->GetFieldID(jcls, "Password", "Ljava/lang/String;");
	jfieldID fid_SharedName	= env->GetFieldID(jcls, "StoreSharedName", "Ljava/lang/String;");

	jobject jstr = NULL;
	wstring wstr;

	pCommon->dwFlags = (DWORD) env->GetLongField (obj, fid_Flags); 
	
	GET_STRING_FIELD (fid_StorePath);
	pCommon->pszStorePath = _wcsdup(wstr.c_str());

	GET_STRING_FIELD (fid_SharedName);
	pCommon->pszStoreSharedName = _wcsdup(wstr.c_str());

	GET_STRING_FIELD (fid_User);
	if (wstr.size() > 0)
		pCommon->pszUser = _wcsdup(wstr.c_str());
	else
		pCommon->pszUser = _wcsdup(L"fakename");// it will be freed by free()

	GET_STRING_FIELD (fid_Password);
	if (wstr.size() > 0)
		pCommon->pszPWD = _wcsdup(wstr.c_str());
	else
		pCommon->pszPWD = _wcsdup(L"fakepwd"); // it will be freed by free()

	return 0;
}


static long _DataStoreSettingInfo_J2C (JNIEnv * env, jobject obj, PDATASTORE_INSTANCE_ITEM pInstItem, 
									  PDATASTORE_INSTANCE_INFO pDSInst)
{
	jobject jstr = NULL;
	wstring wstr;	
	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/ds/DataStoreSettingInfo");	

	jfieldID fid_Flags				= env->GetFieldID(jcls, "Flags", "J");
	jfieldID fid_EnableGDD			= env->GetFieldID(jcls, "EnableGDD", "J");
	jfieldID fid_EnableEncryption	= env->GetFieldID(jcls, "EnableEncryption", "J");
	jfieldID fid_EncryptionMethod	= env->GetFieldID(jcls, "EncryptionMethod", "I");
	jfieldID fid_EncryptionPwd		= env->GetFieldID(jcls, "EncryptionPwd", "Ljava/lang/String;");
	jfieldID fid_EnableCompression	= env->GetFieldID(jcls, "EnableCompression", "J");
	jfieldID fid_CompressionMethod	= env->GetFieldID(jcls, "CompressionMethod", "I");
	jfieldID fid_MaxNodeCount	= env->GetFieldID(jcls, "maxNodeCount", "I");
	jfieldID fid_dsVersion	= env->GetFieldID(jcls, "version", SIG_LONG);
	jfieldID fid_Datastore_name	= env->GetFieldID(jcls, "datastore_name", "Ljava/lang/String;");
	//jfieldID fid_Datastore_id	= env->GetFieldID(jcls, "Datastore_id", "I");
	//jfieldID fid_Node_id		= env->GetFieldID(jcls, "Dode_id", "I");
	jfieldID fid_displayName	= env->GetFieldID(jcls, "displayName", "Ljava/lang/String;");
	jfieldID fid_DSCommSetting	= env->GetFieldID(jcls, "DSCommSetting", "Lcom/ca/arcflash/rps/webservice/data/ds/DataStoreCommonInfo;");
	jfieldID fid_GDDSetting 	= env->GetFieldID(jcls, "GDDSetting", "Lcom/ca/arcflash/rps/webservice/data/ds/DedupSettingInfo;");

	pDSInst->dwFlags			= (DWORD) env->GetLongField (obj, fid_Flags); 
	pDSInst->dwEnableGDD		= (DWORD) env->GetLongField (obj, fid_EnableGDD); 
	pDSInst->dwEnableEncry		= (DWORD) env->GetLongField (obj, fid_EnableEncryption); 
	pDSInst->dwEncryMethod		= (DWORD) env->GetIntField (obj, fid_EncryptionMethod);  
	pDSInst->dwMaxNodeCount		= (DWORD) env->GetIntField (obj, fid_MaxNodeCount); 
	pDSInst->dwDSVersion		= (DWORD) env->GetLongField (obj, fid_dsVersion);
	
	GET_STRING_FIELD(fid_EncryptionPwd);
	pDSInst->pszEncryPwd = _wcsdup(wstr.c_str());

	pDSInst->dwEnableCompress	= (DWORD) env->GetLongField (obj, fid_EnableCompression); 
	pDSInst->dwCompressMethod	= (DWORD) env->GetIntField (obj, fid_CompressionMethod); 
		
	GET_STRING_FIELD(fid_Datastore_name);
	wcsncpy_s(pInstItem->InsName, _countof(pInstItem->InsName), wstr.c_str(), _TRUNCATE);	

	GET_STRING_FIELD(fid_displayName);	
	wcsncpy_s(pInstItem->DispName, _countof(pInstItem->DispName), wstr.c_str(), _TRUNCATE);		

	// convert common setting info from java to C
	pDSInst->pCommInstanceCfg = (PCOMM_INSTANCE_CONFIG)calloc (1, sizeof(COMM_INSTANCE_CONFIG));// need to be freed
	jobject obj0 = env->GetObjectField(obj, fid_DSCommSetting);
	_DataStoreCommonInfo_J2C (env, obj0, pDSInst->pCommInstanceCfg);

	// convert common setting info from java to C
	pDSInst->pGDDInstanceCfg = (PGDD_INSTANCE_CONFIG)calloc (1, sizeof(GDD_INSTANCE_CONFIG));// need to be freed
	obj0 = env->GetObjectField(obj, fid_GDDSetting);
	if ( obj0 ) { // GDD dedupe is enabled.
		_DedupSettingInfo_J2C (env, obj0, pDSInst->pGDDInstanceCfg);
	}

	return 0;

}

// <-- Java 2 C converting functions --|

// if the setting info is converted from java object, 
// we need to free its pointer fields
void _Free_DataStoreSetting (PDATASTORE_INSTANCE_INFO pInfo)
{
	if ( pInfo->pCommInstanceCfg ){
		if (pInfo->pCommInstanceCfg->pszPWD) free (pInfo->pCommInstanceCfg->pszPWD);
		if (pInfo->pCommInstanceCfg->pszStorePath) free (pInfo->pCommInstanceCfg->pszStorePath);
		if (pInfo->pCommInstanceCfg->pszUser) free (pInfo->pCommInstanceCfg->pszUser);

		free (pInfo->pCommInstanceCfg);
		pInfo->pCommInstanceCfg = NULL;
	}

	if ( pInfo->pGDDInstanceCfg ){
		if (pInfo->pGDDInstanceCfg->pHashAttrArray ){ // currently contain 1 elem in array
			if ( pInfo->pGDDInstanceCfg->pHashAttrArray->pszHashFilePaths ){
				free (pInfo->pGDDInstanceCfg->pHashAttrArray->pszHashFilePaths );
			}
			free ( pInfo->pGDDInstanceCfg->pHashAttrArray );
		}

		if (pInfo->pGDDInstanceCfg->pPrimaryAttrArray ){ // currently contain 1 elem in array
			if (pInfo->pGDDInstanceCfg->pPrimaryAttrArray->pszIndexFilePaths)
				free (pInfo->pGDDInstanceCfg->pPrimaryAttrArray->pszIndexFilePaths);
			free ( pInfo->pGDDInstanceCfg->pPrimaryAttrArray );
		}

		if (pInfo->pGDDInstanceCfg->pDataAttrArray){
			if ( pInfo->pGDDInstanceCfg->pDataAttrArray->pszDataFilePath)
				free ( pInfo->pGDDInstanceCfg->pDataAttrArray->pszDataFilePath);
			free (pInfo->pGDDInstanceCfg->pDataAttrArray);
		}

		free (pInfo->pGDDInstanceCfg);
		pInfo->pGDDInstanceCfg = NULL;
	}

	if ( pInfo->pszEncryPwd)
		free (pInfo->pszEncryPwd);	
}

// for UT
void _init_DS_INST (PDATASTORE_INSTANCE_INFO inst )
{
	inst->dwCompressMethod = 2;
	inst->dwEnableCompress =1 ;
	inst->dwEnableEncry = 1;
	inst->dwEnableGDD = 1;
	inst->dwEncryMethod = 2;
	inst->dwFlags = 0;
	inst->pszEncryPwd = L"testpwd";
	inst->pCommInstanceCfg = (PCOMM_INSTANCE_CONFIG ) calloc ( 1, sizeof (COMM_INSTANCE_CONFIG) );
	inst->pCommInstanceCfg->dwFlags = 0;
	inst->pCommInstanceCfg->pszPWD = L"commPWD";
	inst->pCommInstanceCfg->pszUser = L"commUSR";
	inst->pCommInstanceCfg->pszStorePath = L"c\\store";

	inst->pGDDInstanceCfg = (PGDD_INSTANCE_CONFIG) calloc ( 1, sizeof (GDD_INSTANCE_CONFIG) );
	inst->pGDDInstanceCfg->dwDataRoleNumber = 1;
	inst->pGDDInstanceCfg->dwDedupeBlockSizeInKB = 4;
	inst->pGDDInstanceCfg->dwFlags = 0;
	inst->pGDDInstanceCfg->dwHashRoleNumber = 1;
	inst->pGDDInstanceCfg->dwPrimayRoleNumber =1;
	inst->pGDDInstanceCfg->pDataAttrArray = (PGDD_DATA_ROLE_ATTr)calloc ( 1, sizeof (GDD_DATA_ROLE_ATTr) );
	inst->pGDDInstanceCfg->pDataAttrArray->pszDataFilePath = L"C:\\datapath";
	inst->pGDDInstanceCfg->pHashAttrArray = (PGDD_HASH_ROLE_ATTr)calloc ( 1, sizeof (GDD_HASH_ROLE_ATTr) );
	inst->pGDDInstanceCfg->pHashAttrArray->dw64AvailMemInMB = 12;
	inst->pGDDInstanceCfg->pHashAttrArray->dw64ExpectedUniqueDataInGB = 2;
	inst->pGDDInstanceCfg->pHashAttrArray->dwHashRoleMode = 2;
	inst->pGDDInstanceCfg->pHashAttrArray->dwlengthHashFilePath = 100;
	inst->pGDDInstanceCfg->pHashAttrArray->dwPort =10;
	inst->pGDDInstanceCfg->pHashAttrArray->pszHashFilePaths = L"C:\\hashpath";
	inst->pGDDInstanceCfg->pPrimaryAttrArray = (PGDD_PRIMARY_ROLE_ATTr)calloc ( 1, sizeof (GDD_PRIMARY_ROLE_ATTr) );
	inst->pGDDInstanceCfg->pPrimaryAttrArray->dwlengthIndexFilePath = 10;
	inst->pGDDInstanceCfg->pPrimaryAttrArray->dwPort = 11;
	inst->pGDDInstanceCfg->pPrimaryAttrArray->pszIndexFilePaths = L"C:\\indexpah";
}

// for UT
void _init_DS_STATUS (PDATASTORE_INSTANCE_STATUS stat )
{
	stat->dwFlags = 0;
	stat->dwEnableGDD = 1;
	stat->dwOverallStatus = 10;
	stat->dwTotalSrcDataSize = 0;
	stat->dwCompressDataSize = 0;
	stat->dwUniqueDataSize = 0;
	stat->pCommStoreStatus = (PDATA_STORE_COMMPATH_STATUS) calloc (1, sizeof (DATA_STORE_COMMPATH_STATUS));
	stat->pCommStoreStatus->dw64CommPathDirSize = 100001;
	stat->pCommStoreStatus->dw64CommPathVolumeFreeSize = 2000000;
	stat->pCommStoreStatus->dw64CommPathVolumeTotalSize = 2002000;
	stat->pCommStoreStatus->dw64CommPathVolumeUsedSize = 2000;
	stat->pCommStoreStatus->dwFlags = 1;
	stat->pGDDStoreStatus  = (PDATA_STORE_GDD_INST_STATUS) calloc (1, sizeof (DATA_STORE_GDD_INST_STATUS));
	stat->pGDDStoreStatus->dwDataRoleNumber = 1;
	stat->pGDDStoreStatus->dwFlags = 1;
	stat->pGDDStoreStatus->dwHashRoleNumber = 1;
	stat->pGDDStoreStatus->dwPrimaryRoleNumber = 1;
	stat->pGDDStoreStatus->pDataRoleStatusArray = (PGDD_INST_DATA_ROLE_STATUS) calloc (1, sizeof (GDD_INST_DATA_ROLE_STATUS));
	stat->pGDDStoreStatus->pDataRoleStatusArray->dw64DataDirSize = 100;
	stat->pGDDStoreStatus->pDataRoleStatusArray->dw64DataVolumeFreeSize = 1000;
	stat->pGDDStoreStatus->pDataRoleStatusArray->dw64DataVolumeTotalSize = 10000;
	stat->pGDDStoreStatus->pDataRoleStatusArray->dwPort = 1;
	stat->pGDDStoreStatus->pDataRoleStatusArray->dwRoleCurrentState = 1;
	stat->pGDDStoreStatus->pHashRoleStatusArray = (PGDD_INST_HASH_ROLE_STATUS) calloc (1, sizeof (GDD_INST_HASH_ROLE_STATUS));
	stat->pGDDStoreStatus->pHashRoleStatusArray->dw64HashDirSize =100;
	stat->pGDDStoreStatus->pHashRoleStatusArray->dw64HashVolumeFreeSize = 100;
	stat->pGDDStoreStatus->pHashRoleStatusArray->dw64HashVolumeTotalSize = 100;
	stat->pGDDStoreStatus->pHashRoleStatusArray->dw64MemAvailSize = 1000000;
	stat->pGDDStoreStatus->pHashRoleStatusArray->dw64MemTotalSize = 1000000;
	stat->pGDDStoreStatus->pHashRoleStatusArray->dw64MemUsedByHashRole = 120020;
	stat->pGDDStoreStatus->pHashRoleStatusArray->dwPort = 11;
	stat->pGDDStoreStatus->pHashRoleStatusArray->dwRoleCurrentState = 1;

	stat->pGDDStoreStatus->pPrimaryRoleStatus = (PGDD_INST_PRIMARY_ROLE_STATUS) calloc ( 1, sizeof (GDD_INST_PRIMARY_ROLE_STATUS));
	stat->pGDDStoreStatus->pPrimaryRoleStatus ->dw64IndexDirSize = 10;
	stat->pGDDStoreStatus->pPrimaryRoleStatus ->dw64IndexVolumeTotalSize =10000;


}

// add instance
JNIEXPORT void JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreAddInstance
(JNIEnv * env, jclass clz, jobject obj)
{
	long lRet = 0;
	
	DATASTORE_INSTANCE_ITEM instItem = {0};  
	DATASTORE_INSTANCE_INFO instInfo = {0};
	
	CDSLock lock (&_CriticalSection_);// 

	if (fnDataStoreAddInstance == NULL) {lRet = -1; goto error;}// DataStore manager initializing failed

//	printf ("test JNI Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreAddInstance\n");

	lRet = _DataStoreSettingInfo_J2C (env, obj, &instItem, &instInfo);

	if (lRet == 0)
	{
		lRet = fnDataStoreAddInstance(&instItem, &instInfo);
	}

	// _DataStoreSettingInfo_J2C can allocate 
	//   new memory block for DATASTORE_INSTANCE_INFO's pointer fields
	_Free_DataStoreSetting (&instInfo);


error:
	if ( lRet != 0) ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);	
}


JNIEXPORT void JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreModifyInstance
  (JNIEnv * env, jclass clz, jobject obj) 
{
	long lRet = 0;
	
	DATASTORE_INSTANCE_ITEM instItem = {0};  
	DATASTORE_INSTANCE_INFO instInfo = {0};
	CDSLock lock (&_CriticalSection_);// 

	if (fnDataStoreModifyInstance == NULL) {lRet = -1; goto error;}// DataStore manager initializing failed
	
	lRet = _DataStoreSettingInfo_J2C (env, obj, &instItem, &instInfo);
	if (lRet == 0)
	{
		lRet = fnDataStoreModifyInstance(&instItem, &instInfo);
	}

	WCHAR szMsg[1024] = {0};
	if (lRet != 0)	{
		fnGetDSBufMessage(szMsg, _countof(szMsg));
	}

	_Free_DataStoreSetting (&instInfo);


error:
	if (lRet != 0) 
	{
		if (wcslen(szMsg)>0)
			ThrowWSJNIExceptionEx(env, clz, WCHARToJString(env, szMsg), (jint)lRet);
		else
			ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);
	}
}

JNIEXPORT jobject JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreGetInstance
  (JNIEnv * env, jclass clz, jstring jstr) 
{
	jlong lRet = 0;
	wstring wstr;
	DATASTORE_INSTANCE_ITEM instItem = {0};  
	DATASTORE_INSTANCE_INFO instInfo = {0};
	DATA_STORE_PATH_THRESHOLD stDSThreshold = {0};

	CDSLock lock (&_CriticalSection_);// 

	if (fnDataStoreGetInstance == NULL) {lRet = -1; goto error;};
	if (fnDataStoreReleaseAttribute == NULL) {lRet = -1; goto error;};
	if (fnDataStoreGetDSPathThreshold == NULL) {lRet = -1; goto error;};

	wstr = JStringToWString(env, (jstring)jstr); 
	wcsncpy_s(instItem.InsName, _countof(instItem.InsName), wstr.c_str(), _TRUNCATE);

	lRet = fnDataStoreGetInstance (&instItem, &instInfo);
	fnDataStoreGetDSPathThreshold(&instItem, &stDSThreshold);

	if ( lRet == 0){
		jobject jobj = _DataStoreSettingInfo_C2J(env, &instItem, &instInfo, NULL, &stDSThreshold);

		fnDataStoreReleaseAttribute (&instInfo); 
		return jobj;
	}

error:
	ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);

	return NULL;
}


JNIEXPORT void JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreRemoveInstance
  (JNIEnv * env, jclass clz, jstring jstr, jboolean forceDelete)
{
	jlong lRet;
	wstring wstr ;
	DATASTORE_INSTANCE_ITEM instItem = {0};  
	CDSLock lock (&_CriticalSection_);// 

	if (fnDataStoreRemoveInstance == NULL) { lRet = -1; goto error; }// DataStore manager initializing failed

	wstr = JStringToWString(env, (jstring)jstr); 
	//wcscpy_s(instItem.InsName, sizeof (instItem.InsName), wstr.c_str());
	wcsncpy_s(instItem.InsName, _countof(instItem.InsName), wstr.c_str(), _TRUNCATE);

	lRet = (jlong)fnDataStoreRemoveInstance (&instItem);

error:
	if ( lRet != 0) ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreStartInstance
(JNIEnv * env, jclass clz, jstring jstr)
{
	jlong lRet;
	wstring wstr;
	DATASTORE_INSTANCE_ITEM instItem = {0};  
	CDSLock lock (&_CriticalSection_);// 

	if (fnDataStoreStartInstance == NULL ) { lRet = -1; goto error; }

	wstr = JStringToWString(env, (jstring)jstr); 
	//wcscpy_s(instItem.InsName, sizeof (instItem.InsName), wstr.c_str());
	wcsncpy_s(instItem.InsName, _countof(instItem.InsName), wstr.c_str(), _TRUNCATE);

	// try to start the instance !
	lRet = fnDataStoreStartInstance (&instItem );

	WCHAR szMsg[1024] = {0};
	if (lRet != 0)	{
		fnGetDSBufMessage(szMsg, _countof(szMsg));
	}
	
error:
	if (lRet != 0) 
	{
		if (wcslen(szMsg)>0)
			ThrowWSJNIExceptionEx(env, clz, WCHARToJString(env, szMsg), (jint)lRet);
		else
			ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);
	}
}



JNIEXPORT void JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreStartInstanceReadonly(JNIEnv * env, jclass clz, jstring jstr)
{
	jlong lRet;
	wstring wstr;
	DATASTORE_INSTANCE_ITEM instItem = { 0 };
	CDSLock lock(&_CriticalSection_);// 

	if (fnDataStoreStartInstanceReadonly == NULL) { lRet = -1; goto error; }

	wstr = JStringToWString(env, (jstring)jstr);
	wcsncpy_s(instItem.InsName, _countof(instItem.InsName), wstr.c_str(), _TRUNCATE);

	lRet = fnDataStoreStartInstanceReadonly(&instItem);

	WCHAR szMsg[1024] = { 0 };
	if (lRet != 0)	{
		fnGetDSBufMessage(szMsg, _countof(szMsg));
	}

error:
	if (lRet != 0)
	{
		if (wcslen(szMsg)>0)
			ThrowWSJNIExceptionEx(env, clz, WCHARToJString(env, szMsg), (jint)lRet);
		else
			ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);
	}
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreStopInstance
(JNIEnv * env, jclass clz, jstring jstr)
{
	long lRet;
	wstring wstr;
	DATASTORE_INSTANCE_ITEM instItem = {0};  
	CDSLock lock (&_CriticalSection_);// 

	if (fnDataStoreStopInstance == NULL ){ lRet = -1; goto error; }

	wstr = JStringToWString(env, (jstring)jstr); 
	//wcscpy_s(instItem.InsName, sizeof (instItem.InsName), wstr.c_str());
	wcsncpy_s(instItem.InsName, _countof(instItem.InsName), wstr.c_str(), _TRUNCATE);
	
	lRet = fnDataStoreStopInstance (&instItem );

error:
	if ( lRet != 0) ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);

}

JNIEXPORT jobjectArray JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreGetInstanceList
  (JNIEnv * env, jclass clz)
{
	DATASTORE_INSTANCE_LIST instList;
	DATASTORE_INSTANCE_ITEM instItem = {0};  
	DATASTORE_INSTANCE_INFO instInfo = {0};
	DATA_STORE_PATH_THRESHOLD stDSThreshold = {0};

	CDSLock lock (&_CriticalSection_);// 

	long lRet = 0;
	jsize index =0;
	jobject obj = NULL;
	DWORD dwActualSucAttrTotalNum = 0, dwSucAttrIndex = 0;

	if ( NULL == fnDataStoreGetInstanceList) { lRet = -1; goto error; } // check if DS API is ready
	if ( NULL == fnDataStoreGetInstance) { lRet = -1; goto error; }
	if ( NULL == fnDataStoreReleaseAttribute) { lRet = -1; goto error; }
	if ( NULL == fnDataStoreReleaseInstanceList) { lRet = -1; goto error; }
	if ( NULL == fnDataStoreGetDSPathThreshold) { lRet = -1; goto error; } 
	
	lRet = fnDataStoreGetInstanceList (&instList);
	if (lRet !=0 ) {
		// fail to get instance list
		// to do: output a log message
		goto error;
	}

	if ( instList.InsCount ==0){ // This should be a valid case
		// to do: output a log message
		return NULL;
	}

	//First make sure how many items are needed
	//Not sure how many data store can getting successful attribute, so have to 
	//get instance attribute first. Bad Performance.
	for ( index=0; index<(jsize)instList.InsCount; index++)
	{
		instItem = instList.pInstance[index];
		memset (&instInfo, 0, sizeof (instInfo));
		lRet = fnDataStoreGetInstance (&instItem, &instInfo);
		if ( lRet == 0 ){
			dwActualSucAttrTotalNum++;
			fnDataStoreReleaseAttribute ( &instInfo );
		}
	}

	// create a array object can contain instList.InsCount elements
	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/ds/DataStoreSettingInfo");	
	jobjectArray jarray = env->NewObjectArray( dwActualSucAttrTotalNum, jcls, NULL);

	for ( index=0; index<(jsize)instList.InsCount; index++)
	{
		instItem = instList.pInstance[index];
		memset (&instInfo, 0, sizeof (instInfo));

		//When get data store attribute failed, do not set it to web service. 
		//This is different with GetDataStoreStatusList -- Guohua -- 130807
		lRet = fnDataStoreGetInstance (&instItem, &instInfo);

		ZeroMemory(&stDSThreshold, sizeof(stDSThreshold));
		fnDataStoreGetDSPathThreshold(&instItem, &stDSThreshold);
		if ( lRet == 0 ){
			obj = _DataStoreSettingInfo_C2J(env, &instItem, &instInfo, NULL, &stDSThreshold);
			env->SetObjectArrayElement (jarray, dwSucAttrIndex++, obj);
			fnDataStoreReleaseAttribute ( &instInfo );
		}
	}

	// release the list;
	fnDataStoreReleaseInstanceList (&instList);

error:

	//Get Instance list will always return ERROR_SUCCEED; If get data store instance attribute failed,
	//DO not set its value to buffer to web service, so do not need throw the exception.
	//if (lRet != 0)
	//{
	//	ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);
	//	return NULL;
	//}

	return jarray;
}


JNIEXPORT jobjectArray JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreGetNodeList
	(JNIEnv * env, jclass clz, jstring jstr)
{
	DATASTORE_NODE_LIST nodeList = {0};	
	DATASTORE_INSTANCE_ITEM instItem = {0}; 
	wstring wstr; 
	CDSLock lock (&_CriticalSection_);// 
	DATASTORE_INSTANCE_INFO instInfo = {0};

	long lRet = 0;
	jsize index =0;

	DWORD dwStartCounter = GetTickCount();

	if (NULL == fnDataStoreGetInstance) {lRet = -1; goto error;};
	if ( NULL == fnDataStoreGetNodeList) { lRet = -1; goto error; } // check if DS API is ready
	if ( NULL == fnDataStoreReleaseNodeList) { lRet = -1; goto error; }
	if ( NULL == fnDataStoreReleaseAttribute) { lRet = -1; goto error; }

		
	wstr = JStringToWString(env, (jstring)jstr); 
	wcsncpy_s(instItem.InsName, _countof(instItem.InsName), wstr.c_str(), _TRUNCATE);

	lRet = fnDataStoreGetNodeList (&instItem, &nodeList);
	if (lRet !=0 ) {
		// fail to get node list
		// to do: output a log message
		goto error;
	}
	
	lRet = fnDataStoreGetInstance (&instItem, &instInfo);
	if (lRet !=0 ) {
		// fail to get node list
		// to do: output a log message
		goto error;
	}
	
	DWORD dwNodeCount = nodeList.dwNodeCount;

	// create a array object can contain instList.InsCount elements
	jclass jcls = env->FindClass("com/ca/arcflash/webservice/data/restore/BackupDataStoreD2D");
	jobjectArray jarray = env->NewObjectArray(dwNodeCount, jcls, NULL);
	
	const WCHAR* pDSPath = (instInfo.pCommInstanceCfg) ? (instInfo.pCommInstanceCfg->pszStorePath) : NULL;
	for ( index=0; index<(jsize)dwNodeCount; index++)
	{
		jobject obj_nodeInfo = _DataStoreNodeInfo_C2J(env, &nodeList.pNodeItem[index], pDSPath);
		env->SetObjectArrayElement (jarray, index, obj_nodeInfo);
	}

	fnDataStoreReleaseNodeList(&nodeList);
	fnDataStoreReleaseAttribute(&instInfo); 
	
error:

	CDbgLog logger;
	logger.LogW(LL_WAR, 8888, L"RPSWSJNI_DataStoreGetNodeList(): Cost time: %u ms", (GetTickCount() - dwStartCounter));

	//Get Instance list will always return ERROR_SUCCEED; If get data store instance attribute failed,
	//DO not set its value to buffer to web service, so do not need throw the exception.
	if (lRet != 0)
	{
		ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);
		return NULL;
	}

	return jarray;
}


JNIEXPORT jobjectArray JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreGetNodeListByShareFolder
	(JNIEnv * env, jclass clz, jobject jobjConnInfo)
{
	CDbgLog log;
	NET_CONN_INFO connInfo;
	jobjectArray jarray = NULL;

	DWORD dwStartCounter = GetTickCount();

	memset(&connInfo, 0, sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env, &jobjConnInfo, connInfo);

	DWORD dwRet = AFCreateConnection(connInfo);
	if (dwRet)
	{
		log.LogW(LL_ERR, dwRet, L"Failed to connect to [%s] by [%s@%s:%d]\n", connInfo.szDir, connInfo.szUsr, connInfo.szDomain, wcslen(connInfo.szPwd));
		return NULL;
	}

	// Get node list under data store
	std::vector<UDP_NODE_INFO> nodeList;
	dwRet = RPSAFEnumNodesUnderDatastore(connInfo.szDir, nodeList);
	if (dwRet)
	{
		log.LogW(LL_ERR, dwRet, L"Failed to get node list under [%s].", connInfo.szDir);
		goto RETPT;
	}

	// create a array object can contain instList.InsCount elements
	jclass jcls = env->FindClass("com/ca/arcflash/webservice/data/restore/BackupDataStoreD2D");
	jarray = env->NewObjectArray(nodeList.size(), jcls, NULL);

	for (jsize index = 0; index < (jsize)nodeList.size(); index++)
	{
		DATASTORE_NODE_ITEM dsNodeItem = { 0 };
		wcsncpy_s(dsNodeItem.szNodeName, _countof(dsNodeItem.szNodeName), nodeList[index].nodeName.c_str(), _TRUNCATE);
		wcsncpy_s(dsNodeItem.szNodeID, _countof(dsNodeItem.szNodeID), nodeList[index].nodeId.c_str(), _TRUNCATE);
		wcsncpy_s(dsNodeItem.szVMInstanceID, _countof(dsNodeItem.szVMInstanceID), nodeList[index].vmInstanceID.c_str(), _TRUNCATE);
		wcsncpy_s(dsNodeItem.szMSPUser, _countof(dsNodeItem.szMSPUser), nodeList[index].mspUser.c_str(), _TRUNCATE);
		wcsncpy_s(dsNodeItem.szSrcRPSName, _countof(dsNodeItem.szSrcRPSName), nodeList[index].srcRpsName.c_str(), _TRUNCATE);
		wcsncpy_s(dsNodeItem.szDstPlanName, _countof(dsNodeItem.szDstPlanName), nodeList[index].dstPlanName.c_str(), _TRUNCATE);

		dsNodeItem.dwLastBackupTime = nodeList[index].lastBackupTime;
		dsNodeItem.dwIsVM = (nodeList[index].bIsVM ? 1 : 0);
		dsNodeItem.dwIntegrity = (nodeList[index].bIntegrity ? 1 : 0);

		dsNodeItem.dwTransferDataSize = nodeList[index].ullTransferDataSize;
		dsNodeItem.dwDataSize = nodeList[index].ullDataSize;

		// Java side will calculate dwCommomPathSize (Java: metadataSizeB)
		if (nodeList[index].ullTransferDataSize>0)
			dsNodeItem.dwCommonPathSize = nodeList[index].ullCommonPathSize + 0/*dwAveIndexFolderSize*/;
		else
			dsNodeItem.dwCommonPathSize = nodeList[index].ullCommonPathSize;

		dsNodeItem.dwCatalogSize = nodeList[index].ullCatalogSize;
		dsNodeItem.dwGrtCatalogSize = nodeList[index].ullGrtCatalogSize;

		jobject obj_nodeInfo = _DataStoreNodeInfo_C2J(env, &dsNodeItem, connInfo.szDir);
		env->SetObjectArrayElement(jarray, index, obj_nodeInfo);
	}

RETPT:
	AFCutConnection(connInfo);

	log.LogW(LL_WAR, 8888, L" RPSWSJNI_DataStoreGetNodeListByShareFolder() cost time: %u ms.", (GetTickCount() - dwStartCounter));
	return jarray;
}


DWORD __DataStoreGetInstanceList(std::vector<DATASTORE_INFO>& vDataStoreInfo)
{
	vDataStoreInfo.clear();

	CDbgLog logger(L"RPSNativeFacade");

	DATASTORE_INSTANCE_LIST instList;
	DATASTORE_INSTANCE_ITEM instItem = {0};  
	DATASTORE_INSTANCE_INFO instInfo = {0};
	CDSLock lock (&_CriticalSection_);// 

	long lRet = 0;
	DWORD index =0;

	if ( NULL == fnDataStoreGetInstanceList) { lRet = -1; goto error; } // check if DS API is ready
	if ( NULL == fnDataStoreGetInstance) { lRet = -1; goto error; }
	if ( NULL == fnDataStoreReleaseAttribute) { lRet = -1; goto error; }
	if ( NULL == fnDataStoreReleaseInstanceList) { lRet = -1; goto error; }
	
	lRet = fnDataStoreGetInstanceList (&instList);
	if (lRet !=0 ) {
		// fail to get instance list
		// to do: output a log message
		goto error;
	}

	if ( instList.InsCount ==0){ // This should be a valid case
		// to do: output a log message
		return 0;
	}
	
	for ( index=0; index< instList.InsCount; index++)
	{
		instItem = instList.pInstance[index];
		memset (&instInfo, 0, sizeof (instInfo));

		lRet = fnDataStoreGetInstance (&instItem, &instInfo);
		if ( lRet == 0 && instInfo.pCommInstanceCfg != NULL && instInfo.pCommInstanceCfg->pszStorePath != NULL)
		{
			DATASTORE_INFO DataStoreInfo;

			DataStoreInfo.strDSName = instItem.InsName;
			DataStoreInfo.strDir = instInfo.pCommInstanceCfg->pszStorePath;

			if(instInfo.pCommInstanceCfg->pszUser != NULL)
			{
				DataStoreInfo.strUsername = instInfo.pCommInstanceCfg->pszUser;
			}

			if(instInfo.pCommInstanceCfg->pszPWD != NULL)
			{
				DataStoreInfo.strPassword = instInfo.pCommInstanceCfg->pszPWD;
			}

			vDataStoreInfo.push_back(DataStoreInfo);

			fnDataStoreReleaseAttribute (&instInfo);
		}
		else
		{
			logger.LogW(LL_WAR, lRet, L"%s: Failed to get DataStore info %s", __FUNCTION__, instItem.InsName);
			lRet = 0;
		}
	}

	// release the list;
	fnDataStoreReleaseInstanceList (&instList);

error:
	return lRet;
}
/****************************************************************************
This function only return data store static info and data store running status.
It don't return the disk usage status
****************************************************************************/
JNIEXPORT jobjectArray JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreGetStatusList
  (JNIEnv * env, jclass clz)
{
	long lRet = 0;
	jsize index =0;
	jobject obj, obj_settings, obj_status ;

	DATASTORE_INSTANCE_LIST		instList = {0};
	DATASTORE_INSTANCE_ITEM		instItem = {0};  
	DATASTORE_INSTANCE_INFO		instInfo = {0};
	DATASTORE_INSTANCE_STATUS	instStatus = {0};
	CDSLock lock (&_CriticalSection_);// 

	if ( NULL == fnDataStoreGetInstanceList) return NULL; 
	if ( NULL == fnDataStoreReleaseInstanceList) return NULL;
	if ( NULL == fnDataStoreGetInstance) return NULL;
	if ( NULL == fnDataStoreReleaseAttribute) return NULL;
	if ( NULL == fnDataStoreGetInstanceStatus) return NULL;
	if ( NULL == fnDataStoreReleaseStatus) return NULL;
	
	lRet = fnDataStoreGetInstanceList (&instList);

	if (lRet !=0 ) {
		// fail to get instance list
		// to do: output a log message
		goto error;
	}

	if ( instList.InsCount ==0){
		// to do: output a log message
		return NULL;
	}

	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/ds/DataStoreStatusListElem");	

	jmethodID mid_Cons =env->GetMethodID(jcls, "<init>", "()V");
	jfieldID fid_DataStoreSetting	= env->GetFieldID(jcls, "DataStoreSetting", "Lcom/ca/arcflash/rps/webservice/data/ds/DataStoreSettingInfo;");
	jfieldID fid_DataStoreStatus	= env->GetFieldID(jcls, "DataStoreStatus", "Lcom/ca/arcflash/rps/webservice/data/datastore/DataStoreStatus;");

	// create a array object can contain instList.InsCount elements
	jobjectArray jarray = env->NewObjectArray( instList.InsCount, jcls, NULL);
	
	for ( index=0; index<(jsize)instList.InsCount; index++)
	{
		// construct a new array element
		obj = env->NewObject (jcls, mid_Cons);
		if (obj != NULL )
		{
			instItem = instList.pInstance[index];
			memset (&instInfo, 0, sizeof (instInfo));
			lRet = fnDataStoreGetInstance (&instItem, &instInfo);
			//If failed to get the data store attributes, such as data store configuration has been deleted by user, 
			//the return value should be like 20415. 
			//In status structure, there is no UUID or name to identify data store, so still need set attribute buffer, 
			//in fact, we only use data store name and GUID.

			//Note: The behavior is different with GetInstanceList() API, in that API, after getting data store list, 
			//if get attribute failed, do not set it to buffer. -- Guohua -- 130807

			//if ( lRet == 0 ){
				
				obj_settings = _DataStoreSettingInfo_C2J(env, &instItem, &instInfo);
				if ( obj_settings ){
					env->SetObjectField (obj, fid_DataStoreSetting, obj_settings);
				}

				fnDataStoreReleaseAttribute ( &instInfo );
			//}

			memset (&instStatus, 0, sizeof (instStatus));
			lRet = fnDataStoreGetInstanceStatus(&instItem, &instStatus);
			if ( lRet == 0){
				obj_status = _DataStoreStatus_C2J (env, &instStatus );
				if ( obj_status ){
					env->SetObjectField (obj, fid_DataStoreStatus, obj_status);
				}

				fnDataStoreReleaseStatus (&instStatus);	
			}

			env->SetObjectArrayElement (jarray, index, obj);
		}


	}

	fnDataStoreReleaseInstanceList (&instList);

error:
	if ( lRet != 0) ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);

	return jarray;

}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreGetFreeMemory
  (JNIEnv *env, jclass clz) 
{
	MEMORYSTATUSEX msex;
	msex.dwLength = sizeof ( MEMORYSTATUSEX );
	if ( GlobalMemoryStatusEx(  &msex ) ){
		return msex.ullAvailPhys;
	}

	ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)ERROR_DATA_STORE_GET_MEMORY_STATUS);
}


JNIEXPORT jobject JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreGetInstanceStatus
  (JNIEnv * env, jclass clz, jstring jstr)
{
	long lRet = 0;
	jsize index =0;
	jobject obj, obj_settings, obj_status ;
	long instanceAmount = 1;
	wstring wstr;

	DATASTORE_INSTANCE_ITEM		instItem = {0};  
	DATASTORE_INSTANCE_INFO		instInfo = {0};
	DATASTORE_INSTANCE_STATUS	instStatus = {0};
	CDSLock lock (&_CriticalSection_);// 

	if ( NULL == fnDataStoreGetInstance) return NULL;
	if ( NULL == fnDataStoreReleaseAttribute) return NULL;
	if ( NULL == fnDataStoreGetInstanceStatus) return NULL;
	if ( NULL == fnDataStoreReleaseStatus) return NULL;


	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/ds/DataStoreStatusListElem");	

	jmethodID mid_Cons =env->GetMethodID(jcls, "<init>", "()V");
	jfieldID fid_DataStoreSetting	= env->GetFieldID(jcls, "DataStoreSetting", "Lcom/ca/arcflash/rps/webservice/data/ds/DataStoreSettingInfo;");
	jfieldID fid_DataStoreStatus	= env->GetFieldID(jcls, "DataStoreStatus", "Lcom/ca/arcflash/rps/webservice/data/datastore/DataStoreStatus;");
	

	// new an element
	obj = env->NewObject (jcls, mid_Cons);
	if (obj != NULL )
	{
		wstr = JStringToWString(env, (jstring)jstr); 
		//wcscpy_s(instItem.InsName, sizeof (instItem.InsName), wstr.c_str());
		wcsncpy_s(instItem.InsName, _countof(instItem.InsName), wstr.c_str(), _TRUNCATE);
		memset (&instInfo, 0, sizeof (instInfo));
		
		//If failed to get the data store attributes, such as data store configuration has been deleted by user, 
		//the return value should be like 20415. In this case, still need transfer the buffer to web service, especially
		//the data store name and GUID.
		lRet = fnDataStoreGetInstance (&instItem, &instInfo);		
		obj_settings = _DataStoreSettingInfo_C2J(env, &instItem, &instInfo);
		if ( obj_settings ){
			env->SetObjectField (obj, fid_DataStoreSetting, obj_settings);
		}
		fnDataStoreReleaseAttribute ( &instInfo );


		memset (&instStatus, 0, sizeof (instStatus));
		lRet = fnDataStoreGetInstanceStatus (&instItem, &instStatus);
		if ( lRet == 0){
			obj_status = _DataStoreStatus_C2J (env, &instStatus );
			if ( obj_status ){
				env->SetObjectField (obj, fid_DataStoreStatus, obj_status);
			}

			fnDataStoreReleaseStatus (&instStatus);	
		}

	}

	if ( lRet != 0) ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);

	return obj;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreGetHashMachineConf
  (JNIEnv * env, jclass clz)
{
	long lRet = 0;	
	DATA_STORE_HASH_DETECT_INFO		info = {0};  	
	CDSLock lock (&_CriticalSection_);// 

	if ( NULL == fnDataStoreGetHashMachineConf) {lRet = -1; goto error;}
	if ( NULL == fnDataStoreReleaseHashMachineConf) {lRet = -1; goto error;}

	lRet = fnDataStoreGetHashMachineConf (&info);

	if (lRet == 0){
		jstring obj = WCHARToJString(env,info.pszHash_MachineConf);
		fnDataStoreReleaseHashMachineConf(&info);
		return obj;
	}

error:
	if ( lRet != 0) ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);

	return NULL;
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreInitialize
  (JNIEnv * env, jclass clz)
{
	_Init_DataStoreFunctions (); 

	long lRet = 0;	

	if ( NULL == fnDataStoreInitialize) {lRet = -1; goto error;}

	lRet = fnDataStoreInitialize();

error:
	if ( lRet != 0) ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreUninitialize
  (JNIEnv * env, jclass clz) 
{
	DeleteCriticalSection (&_CriticalSection_ );

	long lRet = 0;	
	ERRORSTORE_INIT;
	if ( NULL == fnDataStoreUninitialize) {lRet = -1; SetErrorMessage(-1,L"DataStoreUninitialize is null. This may be caused by initializing Data Store failed"); goto error;}

	lRet = fnDataStoreUninitialize();
	
error:
	ERRORSTORE_CHECKERROR(env, clz,lRet);
	//if ( lRet != 0) ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);
}




JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreGetDedupeRequiredMinMemSize
	(JNIEnv * env, jclass clz, jstring jstrDSGUID)
{
	long lRet = 0;	
	wstring wstrDSGUID;
	unsigned long long llDSRequiredMemSize = 0;
	CDSLock lock (&_CriticalSection_);

	wstrDSGUID = JStringToWString(env, (jstring)jstrDSGUID); 
	lRet = fnDataStoreGetMinMemSize(wstrDSGUID.c_str(), &llDSRequiredMemSize);
	if (lRet != 0)
	{
		//if failed to get the size, throw the exception.
		ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager GetDedupeRequiredMinMemSize Error"), (jint)lRet);
	}

	return (jlong)llDSRequiredMemSize;

}


JNIEXPORT jobject JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreGetInfoFromDisk
	(JNIEnv * env, jclass clz, jstring jstrPath, jstring jstrPathUser, jstring jstrPathPsw, jstring jstrTryDSPsw)
{
	long lRet = 0;
	wstring strPath = L"", strPathUser = L"", strPathPsw = L"", strTryDSPsw = L"";
	DATASTORE_INSTANCE_ITEM instItem = {0}; 
	PDATASTORE_INSTANCE_INFO pInstInfo = NULL;
	DATASTORE_INSTANCE_INFO_STATIC instStaticInfo = {0};
	jobject jobj = NULL;
	jstring jstrExceptMsg;
	jint  jnRet = 0;

	CDSLock lock (&_CriticalSection_);

	strPath = JStringToWString(env, (jstring)jstrPath);
	strPathUser = JStringToWString(env, (jstring)jstrPathUser);
	strPathPsw = JStringToWString(env, (jstring)jstrPathPsw);
	strTryDSPsw = JStringToWString(env, (jstring)jstrTryDSPsw);
		
	//get data store information and lock it.
	HRESULT hr = fnDataStoreGetImportDSConfig(strPath.c_str(), strPathUser.c_str(), 
						strPathPsw.c_str(), strTryDSPsw.c_str(), 
						&pInstInfo, &instItem, &instStaticInfo);
	if (hr==E_DATA_STORE_NAME_EXIST || hr==E_DATA_STORE_GUID_EXIST || hr==S_OK || hr==E_DATA_STORE_GUID_NAME_ALL_EXIST)
	{
		//convert the data store information to java setting
		jobj = _DataStoreSettingInfo_C2J(env, &instItem, pInstInfo, &instStaticInfo);
		fnDataStoreReleaseAttribute (pInstInfo);
	}
	else
	{
		DWORD dwDSErr = 0;
		ConvertComErrToDSErr(hr, dwDSErr);
		FormatDataStoreImportExceptionMessage(env, dwDSErr, jstrExceptMsg, strPath, instStaticInfo.szOwnerName);


		if (env->GetStringLength(jstrExceptMsg) == 0)
			ThrowWSJNIException(env, clz, jstrExceptMsg, (jint)dwDSErr); //using old message format, web service check the error code.
		else
			ThrowWSJNIExceptionEx(env, clz, jstrExceptMsg, (jint)dwDSErr); //return format message to web service.

		return NULL;
	}

	return jobj;	
}



JNIEXPORT void JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreAddInstanceOnExistingPath
	(JNIEnv * env, jclass clz, jobject obj)
{
	long lRet = 0;
	jstring jstrExceptMsg;
	DATASTORE_INSTANCE_ITEM instItem = {0};  
	DATASTORE_INSTANCE_INFO instInfo = {0};

	CDSLock lock (&_CriticalSection_);

	lRet = _DataStoreSettingInfo_J2C (env, obj, &instItem, &instInfo);
	if (lRet == 0)
	{
		lRet = fnDataStoreImportInstance (&instItem, &instInfo);
	}

	if (instInfo.pCommInstanceCfg!=NULL && instInfo.pCommInstanceCfg->pszStorePath!=NULL)
		FormatDataStoreImportExceptionMessage(env, lRet, jstrExceptMsg, instInfo.pCommInstanceCfg->pszStorePath);
	else
		FormatDataStoreImportExceptionMessage(env, lRet, jstrExceptMsg);
	fnDataStoreReleaseAttribute (&instInfo); 
	
	if ( lRet != 0)
	{
		if (env->GetStringLength(jstrExceptMsg) == 0)
			ThrowWSJNIException(env, clz, jstrExceptMsg, (jint)lRet);
		else
			ThrowWSJNIExceptionEx(env, clz, jstrExceptMsg, (jint)lRet);
	}
}

void ConvertComErrToDSErr(UINT unComErr, DWORD& dwDSErr)
{
	switch (unComErr)
	{
	case E_DATA_STORE_FAIL_CONNECT_PATH:
		dwDSErr = ERROR_DATA_STORE_IMPORT_FAIL_CONNECT_PATH;
		break;
	case E_DATA_STORE_FAIL_GET_CFG_FROMPATH:
		dwDSErr = ERROR_DATA_STORE_IMPORT_FAIL_GET_CFG_FROMPATH;
		break;
	case E_DATA_STORE_TRY_DSPSW_FAIL:
		dwDSErr = ERROR_DATA_STORE_IMPORT_FAIL_TRY_DSPSW;
		break;
	case E_DATA_STORE_FAIL_LOCK_PATH:
		dwDSErr = ERROR_DATA_STORE_IMPORT_FAIL_LOCK_PATH;
		break;
	case E_DATA_STORE_GDD_ACTIVE:
		dwDSErr = ERROR_DATA_STORE_IMPORT_FAIL_GDD_ACTIVE;
		break;
	default:
		dwDSErr = unComErr;
		break;
	}
}


void FormatDataStoreImportExceptionMessage(JNIEnv * env, int nErrCode, jstring& jstrExceptMsg, wstring strImportDSPath, wstring strOwnerName)
{
	WCHAR szExceptMsg[1024] = {0};

	switch (nErrCode)
	{
	case ERROR_DATA_STORE_IMPORT_FAIL_CONNECT_PATH:
		FormatExpMessage(szExceptMsg, _countof(szExceptMsg), IDS_DATASTORE_IMPORT_DS_PATH_INACCESS);
		break;
	case ERROR_DATA_STORE_IMPORT_FAIL_GET_CFG_FROMPATH:
		FormatExpMessage(szExceptMsg, _countof(szExceptMsg), IDS_DATASTORE_IMPORT_DS_GET_CONF_FAIL);
		break;
	case ERROR_DATA_STORE_IMPORT_FAIL_TRY_DSPSW:
		FormatExpMessage(szExceptMsg, _countof(szExceptMsg), IDS_DATASTORE_IMPORT_INVALID_DS_PASSWORD);
		break;
	case ERROR_DATA_STORE_IMPORT_FAIL_LOCK_PATH:
		FormatExpMessage(szExceptMsg, _countof(szExceptMsg), IDS_DATASTORE_IMPORT_DS_HAS_LOCK, strOwnerName.c_str());
		break;
	case ERROR_DATA_STORE_IMPORT_FAIL_GDD_ACTIVE:
		FormatExpMessage(szExceptMsg, _countof(szExceptMsg), IDS_DATASTORE_IMPORT_GDDDS_IN_USE);
		break;
	case ERROR_DATA_STORE_IMPORT_VERIFY_PATH_FAIL:
		FormatExpMessage(szExceptMsg, _countof(szExceptMsg), IDS_DATASTORE_IMPORT_VERIFY_FAIL);
		break;
	case ERROR_DATA_STORE_IMPORT_VERIFY_TIMESTAMP_FAIL:
		FormatExpMessage(szExceptMsg, _countof(szExceptMsg), IDS_DATASTORE_IMPORT_VERIFY_TIMESTAMP_FAIL);
		break;
	case ERROR_DATA_STORE_IMPORT_WRONG_PATH_INDEX:
		FormatExpMessage(szExceptMsg, _countof(szExceptMsg), IDS_DATASTORE_IMPORT_WRONG_PATH_INDEX);
		break;
	case ERROR_DATA_STORE_IMPORT_WRONG_PATH_DATA:
		FormatExpMessage(szExceptMsg, _countof(szExceptMsg), IDS_DATASTORE_IMPORT_WRONG_PATH_DATA);
		break;
	case ERROR_DATA_STORE_IMPORT_WRONG_PATH_HASH:
		FormatExpMessage(szExceptMsg, _countof(szExceptMsg), IDS_DATASTORE_IMPORT_WRONG_PATH_HASH);
		break;
	case ERROR_DATA_STORE_IMPORT_MEMORY_TOO_SMALL:
		FormatExpMessage(szExceptMsg, _countof(szExceptMsg), IDS_DATASTORE_IMPORT_MEM_TOO_SMALL);
		break;
	case ERROR_DATA_STORE_LOCK_NEW_DS_FAIL:
		FormatExpMessage(szExceptMsg, _countof(szExceptMsg), IDS_DATASTORE_IMPORT_LOCK_DS_FAIL);
		break;
	default:
		//using empty string
		break;
	}
	jstrExceptMsg = WCHARToJString(env, szExceptMsg);
}




JNIEXPORT jstring JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreGetNewDSSharedName
	(JNIEnv * env, jclass clz, jstring jstrDSName)
{
	long lRet = 0;
	
	CDSLock lock (&_CriticalSection_);// 
	
	if( pLogObj )
		pLogObj->LogW(LL_INF, 0, L"JNI--ComposeDSSharedName: Begin..." );

	wstring szDSName = JStringToWString(env, (jstring)jstrDSName);	
	wchar_t szShardName[260] = {0};

	if ( NULL == fnDataSToreComposeDSSharedName) {
		if( pLogObj )
			pLogObj->LogW(LL_ERR, -1, L"JNI--ComposeDSSharedName: fnDataSToreComposeDSSharedName is NULL." );
		lRet = -1; 
		goto error;
	}
	if( pLogObj )
		pLogObj->LogW(LL_INF, 0, L"JNI--ComposeDSSharedName: fnDataSToreComposeDSSharedName is valid, DS[%s].", szDSName.c_str());

	lRet = fnDataSToreComposeDSSharedName(szDSName.c_str(), szShardName, _countof(szShardName));

	if( pLogObj )
		pLogObj->LogW(LL_INF, lRet, L"JNI--ComposeDSSharedName: fnDataSToreComposeDSSharedName finish, SName[%s], RET[%d].",
			szShardName, lRet);

	if (lRet == 0){
		jstring obj = WCHARToJString(env, szShardName);
		return obj;
	}

error:
	if ( lRet != 0) ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager SHARED NAME Error"), (jint)lRet);

	return NULL;
}



void FormatExpMessage(WCHAR* pszOutputMsg, DWORD dwBufSizeInChar, DWORD dwResID, ...)
{
	WCHAR szTmpStr[1024]={0}; 
	DWORD dwRet = JobLoadString(dwResID, szTmpStr, _countof(szTmpStr));
	if (dwRet != 0)	{
		return;
	}

	LPWSTR pszMessage =NULL;
	try
	{
		va_list args;
		va_start(args, dwResID);
		dwRet = ::FormatMessageW(FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_STRING,
			szTmpStr, 0, 0,(LPTSTR)&pszMessage, 0, &args);
		if(dwRet >0)
		{ 
			wcsncpy_s(pszOutputMsg, dwBufSizeInChar, pszMessage, dwRet );
			pszOutputMsg[wcslen(pszOutputMsg)]=0;
			LocalFree((HLOCAL)pszMessage);
		}
		va_end(args);
	}
	catch (...)
	{
		swprintf_s(pszOutputMsg, dwBufSizeInChar, L"Exception that format exception message id 0x%08x in DMS.", dwResID);
	} 

}


JNIEXPORT void JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreUpdateDSPathStatus
	(JNIEnv * env, jclass clz, jstring jstr)
{
	long lRet;
	wstring wstrDSGUID;
	CDSLock lock (&_CriticalSection_);

	if (fnDataStoreUpdateDSPathStatus == NULL ){ 
		lRet = -1; 
		goto error; 
	}

	wstrDSGUID = JStringToWString(env, (jstring)jstr); 
	lRet = fnDataStoreUpdateDSPathStatus(wstrDSGUID.c_str());

error:
	if ( lRet != 0) 
		ThrowWSJNIException(env, clz, env->NewStringUTF("Update DataStore Path Status Error"), (jint)lRet);

}


JNIEXPORT void JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreUpdateDSStatus
	(JNIEnv * env, jclass clz, jstring jstr)
{
	long lRet1 = 0, lRet2 = 0, lRetFinal=0;
	wstring wstrDSGUID;
	CDSLock lock (&_CriticalSection_);

	if (fnDataStoreUpdateDSPathStatus == NULL || fnDataStoreUpdateDSOverallStatus == NULL ){ 
		lRet1 = -1; 
		goto error; 
	}

	wstrDSGUID = JStringToWString(env, (jstring)jstr); 
	lRet1 = fnDataStoreUpdateDSPathStatus(wstrDSGUID.c_str());
	lRet2 = fnDataStoreUpdateDSOverallStatus(wstrDSGUID.c_str());

error:
	if ( lRet1!=0 || lRet2!=0) {
		lRetFinal = lRet1!=0? lRet1 : lRet2!=0? lRet2:0;
		ThrowWSJNIException(env, clz, env->NewStringUTF("Update DataStore All Status Error"), (jint)lRetFinal);
	}

}


JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_DataStoreVerifyPathValidFreeSpace
	(JNIEnv * env, jclass clz, jstring jstrPath, jstring jstrUserName, jstring jstrPsw, jlong InitMinFreeDiskSpaceForDS)
{
	wchar_t* wpszPath = JStringToWCHAR( env, jstrPath);
	wchar_t* wpszUserName = JStringToWCHAR( env, jstrUserName);
	wchar_t* wpszPsw = JStringToWCHAR( env, jstrPsw );

	DWORD dwRet = AFVerifyDSDestFreeSpace(wpszPath, wpszUserName, wpszPsw, InitMinFreeDiskSpaceForDS);
	
	if(wpszPath) free(wpszPath);
	if(wpszUserName) free(wpszUserName);
	if(wpszPsw) free(wpszPsw);

	//if (dwRet!=0 && dwRet!=1)
	//	ThrowWSJNIException(env, clz, env->NewStringUTF("Verify enough free space errors"), (jint)dwRet);

	return (dwRet == 0)? true: false;
}