// RPSNativeFacade.cpp : Defines the entry point for the DLL application.
#include "stdafx.h"
#include "com.ca.arcflash.rps.jni.RPSWSJNI.h" // JNI header created from RPSWSJNI.java
#include "..\Common\CommonJNIConv.h"							  
#include "..\Common\CommonUtils.h"
#include "RPSCoreInterface.h"
#include "RPSNativeFacade.h"
#include "drcommonlib.h"
#include "Log.h"
#include "stdio.h"
#include "FlashDB.h"
#include "D2DSwitch.h"
#include "..\..\FlashCore\NodeDataPurgeJob\PurgeNodeDataDll\PurgeNodeDataDll.h"
#include "RPSCoreFunction.h"
#include "..\Common\JNIMessageID.h"
#include "CommFunction.h"
#include "AFFileCatalog.h"
#include "FileCopyCommonJNI.h"
BOOL g_bImpersonate = FALSE;

extern CDbgLog* pLogObj;

BOOL APIENTRY DllMain(HMODULE hModule,
	DWORD  ul_reason_for_call,
	LPVOID lpReserved
	)
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH:
		InitialCommonUtils(L"RPSNativeFacade");
		setExceptionHandler();
		break;

	case DLL_THREAD_ATTACH:
		break;

	case DLL_THREAD_DETACH:
		break;

	case DLL_PROCESS_DETACH:
		UnInitialCommonUtils();
		revertExceptionHandler();
		break;
	}
	return TRUE;
}



JNIEXPORT jstring JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSEncryptString
(JNIEnv *env, jclass clz, jstring jstr)
{
	wchar_t* pszStr = JStringToWCHAR(env, jstr);
	if (NULL == pszStr)
		goto error;

	jstring retValue = NULL;
	DWORD pBufLen = 0;
	wchar_t *pszBuf = NULL;
	BOOL ret = RPSEncryptString(pszStr, pszBuf, &pBufLen);
	pBufLen = pBufLen + 1;
	pszBuf = new wchar_t[pBufLen];
	memset(pszBuf, 0, pBufLen * sizeof(wchar_t));
	if (pszBuf != NULL)
	{
		ret = RPSEncryptString(pszStr, pszBuf, &pBufLen);
		retValue = WCHARToJString(env, pszBuf);
		delete[] pszBuf;
		//return retValue;
	}
	if (pszStr != NULL)
	{
		free(pszStr);
	}
	return retValue;

error:
	return WCHARToJString(env, NULL);
}


JNIEXPORT jstring JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSDecryptString
(JNIEnv *env, jclass clz, jstring jstr)
{
	wchar_t* pszStr = JStringToWCHAR(env, jstr);
	if (NULL == pszStr)
		goto error;

	jstring retValue = NULL;
	DWORD pBufLen = 0;
	wchar_t *pszBuf = NULL;
	BOOL ret = RPSDecryptString(pszStr, pszBuf, &pBufLen);
	if (ret)
	{
		pBufLen = pBufLen + 1;
		pszBuf = new wchar_t[pBufLen];
		memset(pszBuf, 0, pBufLen * sizeof(wchar_t));
		if (pszBuf != NULL)
		{
			ret = RPSDecryptString(pszStr, pszBuf, &pBufLen);
			retValue = WCHARToJString(env, pszBuf);
			delete[] pszBuf;
			//return retValue;
		}
		if (pszStr != NULL)
		{
			free(pszStr);
		}
		return retValue;
	}
	else
	{
		return jstr;
	}

error:
	return WCHARToJString(env, NULL);
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSDecryptStringWithEarlyVersion
(JNIEnv *env, jclass clz, jstring jstr)
{
	wchar_t* pszStr = JStringToWCHAR(env, jstr);
	if (NULL == pszStr)
		goto error;

	jstring retValue = NULL;
	DWORD pBufLen = 0;
	wchar_t *pszBuf = NULL;
	BOOL ret = RPSDecryptStrWithEarlyVersion(pszStr, pszBuf, &pBufLen);
	pBufLen = pBufLen;
	pszBuf = new wchar_t[pBufLen];
	memset(pszBuf, 0, pBufLen * sizeof(wchar_t));
	if (pszBuf != NULL)
	{
		ret = RPSDecryptStrWithEarlyVersion(pszStr, pszBuf, &pBufLen);
		retValue = WCHARToJString(env, pszBuf);
		delete[] pszBuf;
		//return retValue;
	}
	if (pszStr != NULL)
	{
		free(pszStr);
	}
	return retValue;


error:
	return WCHARToJString(env, NULL);
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSEcryptStringWithEarlyVersion(JNIEnv *env, jclass clz, jstring jstr)
{
	wchar_t* pszStr = JStringToWCHAR(env, jstr);
	jstring retValue = NULL;
	DWORD pBufLen = 0;
	wchar_t *pszBuf = NULL;
	BOOL ret = RPSEcryptStrWithEarlyVersion(pszStr, pszBuf, &pBufLen);
	pszBuf = new wchar_t[pBufLen];
	memset(pszBuf, 0, pBufLen * sizeof(wchar_t));
	if (pszBuf != NULL)
	{
		ret = RPSEcryptStrWithEarlyVersion(pszStr, pszBuf, &pBufLen);
		retValue = WCHARToJString(env, pszBuf);
		delete[] pszBuf;
		//return retValue;
	}
	if (pszStr != NULL)
	{
		free(pszStr);
	}
	return retValue;
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFReplicate
(JNIEnv *env, jclass jclz, jobject jobScript)
{
	CDbgLog log(L"RPSNativeFacade");
	RPSRepJobScript rpsRepJobScript;

	memset(&rpsRepJobScript, 0, sizeof(RPSRepJobScript));

	JReplicationJob2AFReplicationJob(env, &rpsRepJobScript, &jobScript);

#define SAFE_PRINT(A) (A) == NULL ? L"NULL" : (A)
	log.LogW(LL_INF, 0, L"jobScript jobid: %d -- %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
		rpsRepJobScript.JobID,
		SAFE_PRINT(rpsRepJobScript.D2DNodeName),
		SAFE_PRINT(rpsRepJobScript.DestinationRootPath),
		SAFE_PRINT(rpsRepJobScript.RemoteDestinationRootPath),
		SAFE_PRINT(rpsRepJobScript.PolicyName),
		SAFE_PRINT(rpsRepJobScript.PolicyGUID),
		SAFE_PRINT(rpsRepJobScript.pRPSName),
		SAFE_PRINT(rpsRepJobScript.SrcDs.DSName),
		SAFE_PRINT(rpsRepJobScript.SrcDs.DisplayName),
		SAFE_PRINT(rpsRepJobScript.DesDs.DSName),
		SAFE_PRINT(rpsRepJobScript.DesDs.DisplayName),
		SAFE_PRINT(rpsRepJobScript.RemotePolicyName),
		SAFE_PRINT(rpsRepJobScript.RemotePolicyGUID),
		SAFE_PRINT(rpsRepJobScript.RemoteServer));


	DWORD dwRet = AFRPSRepJob(&rpsRepJobScript, NULL, NULL);

	FreeAFReplicationJobScript(&rpsRepJobScript);

	return dwRet;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetRPSJobID
(JNIEnv *env, jclass clz, jobject jobID)
{
	DWORD dwJobId = 0;
	BOOL returnValue = RPSGetJobId(&dwJobId);

	AddUINT2JRWLong(env, dwJobId, &jobID);

	return returnValue;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_CreateRPSJobMonitor
(JNIEnv *env, jclass clz, jlong shrMemId)
{
	CDbgLog log(L"RPSNativeFacade");

	IJobMonitor *pIJM = NULL;

	DWORD dwRet = CreateRPSJobMonitor(shrMemId, &pIJM);
	log.LogW(LL_INF, 0, L"%s: pIJM = %X", __WFUNCTION__, pIJM);

	if (!dwRet)
	{
		return (jlong)pIJM;
	}

	return 0;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetRPSJobMonitor
(JNIEnv *env, jclass clz, jlong address, jobject jobMonitor)
{
	wprintf(L"Address:[%d]\n", address);
	IJobMonitor *pIJM = (IJobMonitor *)address;
	JOB_MONITOR    aJM = { 0 };
	memset(&aJM, 0, sizeof(JOB_MONITOR));

	DWORD dwRead = 0;
	if (pIJM)
	{

		dwRead = pIJM->Read(&aJM);
		wprintf(L"dwRead[%d]\n", dwRead);
		if (!dwRead)
		{
			JOB_MONITOR2JJobMonitor(env, aJM, jobMonitor);
		}
		else
		{
			wprintf(L"pAFJM->Read failed[%d]\n", dwRead);
			return dwRead;
		}
	}

	return dwRead;
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_ReleaseRPSJobMonitor
(JNIEnv *env, jclass clz, jlong address){

	CDbgLog log(L"RPSNativeFacade");

	IJobMonitor *pIJM = (IJobMonitor *)address;
	if (pIJM)
	{
		log.LogW(LL_INF, 0, L"%s: pIJM = %X, &pIJM = %X", __WFUNCTION__, pIJM, &pIJM);
		DestroyRPSJobMonitor(&pIJM);
	}
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_BrowseFileFolderItemEx(
	JNIEnv *env,
	jclass clz,
	jstring path,
	jint type,	// 0: both files / folder; 1: folders only; 2: files only
	jint maxCount,
	jstring domain,
	jstring user,
	jstring pwd,
	jobject retArr
	)
{
	wchar_t* pszPath = JStringToWCHAR(env, path);
	wchar_t* pszDomain = JStringToWCHAR(env, domain);
	wchar_t* pszUser = JStringToWCHAR(env, user);
	wchar_t* pszPwd = JStringToWCHAR(env, pwd);

	IFileListHandler *pList = NULL;

	DWORD dwRet = CreateIFileListHandler(&pList);
	if (dwRet)
	{
		wprintf(L"get handler failed[%d]\n", dwRet);
	}
	else
	{
		vector<FILE_INFO> vList;

		NET_CONN_INFO conn;
		ZeroMemory(&conn, sizeof(NET_CONN_INFO));

		if (pszDomain)
		{
			wcscpy_s(conn.szDomain, _countof(conn.szDomain), pszDomain);
		}
		if (pszUser){
			wcscpy_s(conn.szUsr, _countof(conn.szUsr), pszUser);
		}
		if (pszPwd){
			wcscpy_s(conn.szPwd, _countof(conn.szPwd), pszPwd);
		}
		if (pszPath)
		{
			wcscpy_s(conn.szDir, _countof(conn.szDir), pszPath);
		}

		int iNum = maxCount;	// retrieve how many number of folders / files
		dwRet = pList->GetFileListEx(vList, type, iNum, conn);
		if (dwRet)
		{
			wprintf(L"get file list failed[%d]\n", dwRet);
		}
		else
		{
			AddVecFileInfo2List(env, &retArr, vList);
		}

		pList->Release();
	}

	if (pszPath != NULL)
	{
		free(pszPath);
		pszPath = NULL;
	}

	if (pszDomain != NULL)
	{
		free(pszDomain);
		pszDomain = NULL;
	}

	if (pszUser != NULL)
	{
		free(pszUser);
		pszUser = NULL;
	}

	if (pszPwd != NULL)
	{
		free(pszPwd);
		pszPwd = NULL;
	}
	return dwRet;
}


BOOL WINAPI AFGetErrorMsg(DWORD dwErr, std::wstring &strMsg);

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFGetErrorMsg
(JNIEnv *env, jclass clz, jlong errorCode)
{
	std::wstring errMsg;

	AFGetErrorMsg((DWORD)errorCode, errMsg);

	jstring jstr = WCHARToJString(env, (wchar_t*)errMsg.c_str());
	return jstr;
}


DWORD WINAPI ReleaseBrowseInformation(IN LPWSTR* ppBrowseInfo);


DWORD WINAPI RPSBrowseVolumeInforamtion(IN OUT LPWSTR* ppBrowseInfo, IN OUT DWORD* pdwBrowseInfoSize, BOOL bSaveAsFile, BOOL bBrowseDetail = FALSE, WCHAR* pwzBackupDest = NULL);

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_BrowseVolumeInforamtion
(JNIEnv *env, jclass jclz, jobject arr, jboolean details, jstring backupdest)
{
	jclass class_ArrayList = env->GetObjectClass(arr);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
	wchar_t* pBrowseInfo = NULL;
	DWORD dwBrowseInfoSize = 0;
	wchar_t* pDestPath = NULL;
	if (backupdest != NULL)
		pDestPath = JStringToWCHAR(env, backupdest);

	DWORD dwRet = RPSBrowseVolumeInforamtion(&pBrowseInfo, &dwBrowseInfoSize, FALSE, (BOOL)details, pDestPath);
	if (dwRet == 0)
	{
		jstring ret = env->NewString((jchar*)pBrowseInfo, dwBrowseInfoSize);
		env->CallBooleanMethod(arr, id_ArrayList_add, ret);
		ReleaseBrowseInformation(&pBrowseInfo);
	}

	if (pDestPath != NULL)
		free(pDestPath);

	return dwRet;

}


DWORD WINAPI AFCGRTSkipDisk(IN const PWCHAR pwzVolName, OUT BOOL * pbSkipped);

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFCGRTSkipDisk
(JNIEnv *env, jclass clz, jstring jvolName, jobject jskipped)
{
	wchar_t* pwzVolName = JStringToWCHAR(env, jvolName);

	BOOL bSkipped = FALSE;

	// check if the volume should be skipped because if is a mounted disk
	DWORD dwRet = AFCGRTSkipDisk(pwzVolName, &bSkipped);

	if (dwRet == 0)
	{
		AddUINT2JRWLong(env, bSkipped, &jskipped);
	}

	if (pwzVolName != NULL)
	{
		free(pwzVolName);
	}

	return dwRet;
}


DWORD WINAPI AFCreateDir(const std::wstring &strParent, const std::wstring &strSubFolder);

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFCreateDir
(JNIEnv *env, jclass clz, jstring parentdir, jstring subdir)
{
	wchar_t* pPath = JStringToWCHAR(env, parentdir);
	std::wstring strParentPath = pPath;

	wchar_t* subPath = JStringToWCHAR(env, subdir);
	std::wstring strSubPath = subPath;

	DWORD dwRet = AFCreateDir(strParentPath, strSubPath);

	if (pPath != NULL)
	{
		free(pPath);
	}

	if (subPath != NULL)
	{
		free(subPath);
	}

	if (dwRet)
	{
		wprintf(L"get failed[%d]\n", dwRet);
	}

	return (jlong)dwRet;
}

DWORD WINAPI AFCreateAllDirs(const std::wstring &strFullFolder);

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFCreateAllDirs
(JNIEnv *env, jclass clz, jstring fullFolder)
{
	wchar_t* pPath = JStringToWCHAR(env, fullFolder);
	std::wstring strFullFolder = pPath;

	DWORD dwRet = AFCreateAllDirs(strFullFolder);

	if (pPath != NULL)
	{
		free(pPath);
	}

	if (dwRet)
	{
		wprintf(L"get failed[%d]\n", dwRet);
	}

	return (jlong)dwRet;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetRegRootPathByProduct
(JNIEnv *env, jclass clz)
{
	wstring wstr;

	BOOL bRet = GetRPSRegRootPathByProduct(wstr);

	jstring jRegRootPath = WCHARToJString(env, (wchar_t*)wstr.c_str());

	return jRegRootPath;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetControlRPSJobHandle
(JNIEnv *env, jclass clz, jlong flag, jboolean isAsync, jobject jFilter)
{
	JobFilter filter;
	JFilterToJobFilter(env, &filter, &jFilter);

	return (jlong)GetControlRPSJobHandle((DWORD)flag, (BOOL)isAsync, filter, env, NULL);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_ControlRPSJobs(JNIEnv * env, jclass clz, jlong handle)
{
	return ControlRPSJobs((HANDLE)handle);
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_FreeControlRPSJobHandle(JNIEnv * env, jclass clz, jlong handle)
{
	return FreeControlRPSJobHandle((HANDLE)handle);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetControlRPSJobStatus(JNIEnv * env, jclass clz, jlong handle)
{
	return GetControlRPSJobStatus((HANDLE)handle);
}

//
// read and write activity log of RPS
//
JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSLogActivity
(JNIEnv *env, jclass clz, jlong level, jlong jobID, jlong resourceID, jobjectArray parameters, jstring vmIndentification)
{
	jclass class_ArrayList = env->FindClass("java/util/List");
	jmethodID id_ArrayList_get = env->GetMethodID(class_ArrayList, "get", "(I)Ljava/lang/Object;");

	jstring jstr1 = (jstring)env->GetObjectArrayElement(parameters, 0);
	wchar_t* param1 = JStringToWCHAR(env, jstr1);

	jstring jstr2 = (jstring)env->GetObjectArrayElement(parameters, 1);
	wchar_t* param2 = JStringToWCHAR(env, jstr2);

	jstring jstr3 = (jstring)env->GetObjectArrayElement(parameters, 2);
	wchar_t* param3 = JStringToWCHAR(env, jstr3);

	jstring jstr4 = (jstring)env->GetObjectArrayElement(parameters, 3);
	wchar_t* param4 = JStringToWCHAR(env, jstr4);

	jstring jstr5 = (jstring)env->GetObjectArrayElement(parameters, 4);
	wchar_t* param5 = JStringToWCHAR(env, jstr5);

	wchar_t* vmInstanceUUID = JStringToWCHAR(env, vmIndentification);

	DWORD dwMessageID = jni_get_mapped_messsage_id(resourceID);
	DWORD ret = LogActivityWithDetails(level, APT_RPS, AJT_COMMON, jobID, vmInstanceUUID, dwMessageID, param1, param2, param3, param4, param5);

	if (param1 != NULL)
		free(param1);
	if (param2 != NULL)
		free(param2);
	if (param3 != NULL)
		free(param3);
	if (param4 != NULL)
		free(param4);
	if (param5 != NULL)
		free(param5);
	if (vmInstanceUUID != NULL)
		free(vmInstanceUUID);
	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);

	return 0;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSLogActivityDetail2
(JNIEnv *env, jclass clz, jobject jActLogDetail, jlong resourceID, jobjectArray parameters)
{
	jstring jstr1 = (jstring)env->GetObjectArrayElement(parameters, 0);
	wchar_t* param1 = JStringToWCHAR(env, jstr1);

	jstring jstr2 = (jstring)env->GetObjectArrayElement(parameters, 1);
	wchar_t* param2 = JStringToWCHAR(env, jstr2);

	jstring jstr3 = (jstring)env->GetObjectArrayElement(parameters, 2);
	wchar_t* param3 = JStringToWCHAR(env, jstr3);

	jstring jstr4 = (jstring)env->GetObjectArrayElement(parameters, 3);
	wchar_t* param4 = JStringToWCHAR(env, jstr4);

	jstring jstr5 = (jstring)env->GetObjectArrayElement(parameters, 4);
	wchar_t* param5 = JStringToWCHAR(env, jstr5);


	ACTLOG_DETAILS logDetails;

	JActivityLogDetail2PACTLOG_DETAILS(env, jActLogDetail, &logDetails);
	DWORD dwMessageID = jni_get_mapped_messsage_id(resourceID);
	DWORD rc = LogActivityWithDetails2(&logDetails, dwMessageID, param1, param2, param3, param4, param5);

	SAFE_FREE(param1);
	SAFE_FREE(param2);
	SAFE_FREE(param3);
	SAFE_FREE(param4);
	SAFE_FREE(param5);

	return 0;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSGetLogActivity
(JNIEnv *env, jclass clz, jlong jobID, jstring vmIndentification, jint startFrom, jint requestCount, jobject activityLogResult)
{
	wchar_t* vmInstanceUUID = JStringToWCHAR(env, vmIndentification);
	ULONGLONG nCnt, ntotalCount = 0;
	PFLASHDB_ACTIVITY_LOG p = NULL;
	DWORD result = GetLog(APT_RPS, jobID, startFrom, requestCount, &p, &nCnt, &ntotalCount, vmInstanceUUID);
	if (result == 0)
	{
		jclass class_ArrayList = env->FindClass("java/util/List");
		jclass class_ActivityLogResult = env->FindClass("com/ca/arcflash/service/jni/model/JActivityLogResult");
		jclass class_ActivityLog = env->FindClass("com/ca/arcflash/service/jni/model/JActivityLog");

		jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
		jmethodID jActivityLog_constructor = env->GetMethodID(class_ActivityLog, "<init>", "()V");

		jfieldID field_totalCount = env->GetFieldID(class_ActivityLogResult, "totalCount", "J");
		jfieldID field_logs = env->GetFieldID(class_ActivityLogResult, "logs", "Ljava/util/List;");
		jfieldID field_message = env->GetFieldID(class_ActivityLog, "message", "Ljava/lang/String;");
		jfieldID field_time = env->GetFieldID(class_ActivityLog, "time", "Ljava/lang/String;");
		jfieldID field_level = env->GetFieldID(class_ActivityLog, "level", "J");
		jfieldID field_jobID = env->GetFieldID(class_ActivityLog, "jobID", "J");
		jobject jActivityLog = NULL;

		env->SetLongField(activityLogResult, field_totalCount, ntotalCount);
		jobject list = env->GetObjectField(activityLogResult, field_logs);

		WCHAR szTime[MAX_PATH] = { 0 };
		for (ULONGLONG i = 0; i < nCnt; i++) {
			jActivityLog = env->NewObject(class_ActivityLog, jActivityLog_constructor);

			jstring jstr = WCHARToJString(env, p[i].wszLogMessage);
			env->SetObjectField(jActivityLog, field_message, jstr);
			if (jstr != NULL) env->DeleteLocalRef(jstr);

			ZeroMemory(szTime, sizeof(szTime));
			ulonglong_2_timestr(p[i].ullTimeUTC, szTime, _ARRAYSIZE(szTime));
			jstr = WCHARToJString(env, szTime);
			env->SetObjectField(jActivityLog, field_time, jstr);
			if (jstr != NULL) env->DeleteLocalRef(jstr);

			env->SetLongField(jActivityLog, field_level, p[i].dwLogLevel);
			env->SetLongField(jActivityLog, field_jobID, p[i].ullJobID);

			env->CallBooleanMethod(list, id_ArrayList_add, jActivityLog);
			if (jActivityLog != NULL) env->DeleteLocalRef(jActivityLog);
		}

		if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);
		if (class_ActivityLog != NULL) env->DeleteLocalRef(class_ActivityLog);
		if (class_ActivityLogResult != NULL) env->DeleteLocalRef(class_ActivityLogResult);
	}

	FreeLog(p);

	if (vmInstanceUUID != NULL)
		free(vmInstanceUUID);

	return result;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFCutConnection
(JNIEnv *env, jclass clz, jobject jConn, jboolean force)
{
	NET_CONN_INFO connInfo;
	memset(&connInfo, 0, sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env, &jConn, connInfo);

	DWORD dwRet = AFCutConnection(connInfo, (BOOL)force);
	if (dwRet)
	{
		wprintf(L"get failed[%d]\n", dwRet);
	}

	return (jlong)dwRet;
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFCreateConnection
(JNIEnv * env, jclass jcls, jobject jConn)
{
	NET_CONN_INFO connInfo;
	memset(&connInfo, 0, sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env, &jConn, connInfo);

	DWORD dwRet = AFCreateConnection(connInfo);
	if (dwRet)
	{
		wprintf(L"get failed[%d]\n", dwRet);
	}

	return (jlong)dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFRetrieveConnections(JNIEnv *env, jclass clz, jobject retConns)
{
	vector<NET_CONN> cConns;

	DWORD dwRet = AFRetrieveConnections(cConns);
	if (dwRet == 0)
	{
		jclass class_netconn = env->FindClass("com/ca/arcflash/service/jni/model/JNetConnInfo");
		jmethodID jConn_constructor = env->GetMethodID(class_netconn, "<init>", "()V");

		jclass class_ArrayList = env->GetObjectClass(retConns);
		jmethodID addMethod = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

		for (vector<NET_CONN>::iterator iter = cConns.begin(); iter != cConns.end(); iter++)
		{
			jobject jConn = env->NewObject(class_netconn, jConn_constructor);
			jmethodID methodSetUser = env->GetMethodID(class_netconn, "setSzUsr", "(Ljava/lang/String;)V");
			jmethodID methodSetDest = env->GetMethodID(class_netconn, "setSzDir", "(Ljava/lang/String;)V");

			env->CallVoidMethod(jConn, methodSetUser, WCHARToJString(env, iter->strUser));
			env->CallVoidMethod(jConn, methodSetDest, WCHARToJString(env, iter->strConn));
			env->CallBooleanMethod(retConns, addMethod, jConn);
		}

		if (class_netconn != NULL) env->DeleteLocalRef(class_netconn);
	}

	return dwRet;
}

BOOL IsAdmin(HANDLE hToken)
{
	SID_IDENTIFIER_AUTHORITY NtAuthority = SECURITY_NT_AUTHORITY;
	PSID AdministratorsGroup;
	BOOL b = AllocateAndInitializeSid(
		&NtAuthority,
		2,
		SECURITY_BUILTIN_DOMAIN_RID,
		DOMAIN_ALIAS_RID_ADMINS,
		0, 0, 0, 0, 0, 0,
		&AdministratorsGroup);
	if (b)
	{
		if (!CheckTokenMembership(hToken, AdministratorsGroup, &b))
		{
			b = FALSE;
		}
		FreeSid(AdministratorsGroup);
	}

	return(b);
}

int Validate(LPTSTR lpszUsername, LPTSTR lpszDomain, LPTSTR lpszPassword)
{
	int ret = 0;

	LPTSTR lpUserName = lpszUsername;
	LPTSTR lpDomain = lpszDomain;
	size_t iLen = _tcslen(lpUserName);
	if (iLen == 0)
	{
		return 1;
	}

	LPTSTR lpDomainName = NULL;
	for (size_t i = 0; i<iLen; i++)
	{
		TCHAR cChar = lpszUsername[i];
		if ((cChar == '\\') || (cChar == '/'))
		{
			//if the name format:domain\user, we always use the domain name 
			//and don't care lpszDomain is NULL or not
			lpDomainName = (LPTSTR)malloc(sizeof(TCHAR)*(i + 1));
			ZeroMemory(lpDomainName, sizeof(TCHAR)*(i + 1));
			_tcsncpy_s(lpDomainName, sizeof(TCHAR)*i, lpszUsername, i);
			if (i + 1 < iLen)
			{
				lpUserName = lpszUsername + i + 1;
			}
			lpDomain = lpDomainName;
			break;
		}
		else if (cChar == '@')
		{
			//if the user name is UPN format
			lpDomain = NULL;
			break;
		}
	}

	HANDLE hToken;

	BOOL isValidUser = LogonUser(
		lpUserName,
		lpDomain,
		lpszPassword,
		LOGON32_LOGON_NETWORK,
		LOGON32_PROVIDER_DEFAULT,
		&hToken
		);

	if (!isValidUser)
	{
		ret = 1;
	}
	else
	{
		BOOL isAdmin = IsAdmin(hToken);
		if (!isAdmin)
		{
			ret = 2;
		}
	}

	if (lpDomainName)
	{
		free(lpDomainName);
	}
	CloseHandle(hToken);
	return ret;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_validate
(JNIEnv *env, jclass jobject, jstring username, jstring domainname, jstring password)
{
	wchar_t* sname = JStringToWCHAR(env, username);
	wchar_t* domain = JStringToWCHAR(env, domainname);
	wchar_t* pwd = JStringToWCHAR(env, password);
	int ret = Validate((LPTSTR)sname, (LPTSTR)domain, (LPTSTR)pwd);

	if (sname != NULL)
	{
		free(sname);
	}

	if (domain != NULL)
	{
		free(domain);
	}

	if (pwd != NULL)
	{
		free(pwd);
	}
	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFCheckFolderAccess
(JNIEnv *env, jclass clz, jobject jConn, jobject jArrFileinfo)
{
	NET_CONN_INFO connInfo;
	memset(&connInfo, 0, sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env, &jConn, connInfo);

	FILE_INFO fileInfo;
	memset(&fileInfo, 0, sizeof(FILE_INFO));

	DWORD dwRet = AFCheckFolderAccess(&connInfo, fileInfo);
	if (dwRet)
	{
		wprintf(L"get failed[%d]\n", dwRet);
	}
	else
	{
		std::vector<FILE_INFO> vec;
		vec.push_back(fileInfo);
		AddVecFileInfo2List(env, &jArrFileinfo, vec);
	}

	return dwRet;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSCancelJob
(JNIEnv *env, jclass clz, jlong jobID)
{
	return RPSCancelJob(jobID);
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSCheckNewSessionsToReplicate
(JNIEnv *env, jclass clz, jstring pSessionRootPath, jstring pUserName, jstring pPassword)
{
	wchar_t *pPath = JStringToWCHAR(env, pSessionRootPath);
	wchar_t *pUser = JStringToWCHAR(env, pUserName);
	wchar_t *pPass = JStringToWCHAR(env, pPassword);

	bool bRet = CheckNewSessionsToReplicate(pPath, pUser, pPass);

	if (pPath) free(pPath);
	if (pUser) free(pUser);
	if (pPass) free(pPass);

	return bRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFGetPathMaxLength
(JNIEnv *env, jclass)
{
	DWORD returnValue = 0;
	AFGetPathMaxLength(&returnValue);
	return returnValue;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSCreateClientJobSharedQueue
(JNIEnv *env, jclass clz)
{
	void *handle = RPSCreateClientJobSharedQueue();
	return (jlong)handle;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSWaitForNewClientJobArrival
(JNIEnv *env, jclass clz, jlong handle, jobject jRpsJobInfo)
{
	RPS_JOB_INFO_S rpsJobInfo;
	memset(&rpsJobInfo, 0, sizeof(RPS_JOB_INFO_S));
	BOOL bResult = RPSWaitForNewClientJobArrival((void *)handle, &rpsJobInfo);

	if (bResult)
	{
		RPS_JOB_INFO2JRPSJobInfo(env, rpsJobInfo, jRpsJobInfo);
	}

	return bResult;
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSDestroyClientJobSharedQueue
(JNIEnv *env, jclass clz, jlong handle)
{
	RPSDestroyClientJobSharedQueue((void *)handle);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getActiveJobs(JNIEnv *env, jclass clz, jobject jretArray)
{
	vector<JOB_CONTEXT> jobs;
	DWORD dwRet = RPSRetrieveActiveJobs(jobs);
	if (dwRet == 0)
	{
		jclass class_JJobContext = env->FindClass("com/ca/arcflash/service/jni/model/JJobContext");
		jmethodID jJobContext_constructor = env->GetMethodID(class_JJobContext, "<init>", "()V");

		jclass class_ArrayList = env->GetObjectClass(jretArray);
		jmethodID addMethod = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

		for (vector<JOB_CONTEXT>::iterator jobIter = jobs.begin(); jobIter != jobs.end(); jobIter++)
		{
			jobject jobContext = env->NewObject(class_JJobContext, jJobContext_constructor);
			//JOB_CONTEXT2JobContext(env, jobIter, jobContext, class_JJobContext);
			jmethodID setJobIDMethod = env->GetMethodID(class_JJobContext, "setDwJobId", "(J)V");
			jmethodID setQueueTypeMethod = env->GetMethodID(class_JJobContext, "setDwQueueType", "(J)V");
			jmethodID setJobTypeMethod = env->GetMethodID(class_JJobContext, "setDwJobType", "(J)V");
			jmethodID setProcessIDMethod = env->GetMethodID(class_JJobContext, "setDwProcessId", "(J)V");
			jmethodID setShrmemIDMethod = env->GetMethodID(class_JJobContext, "setDwJMShmId", "(J)V");
			jmethodID setLauncherInstanceUUIDMethod = env->GetMethodID(class_JJobContext, "setLauncherInstanceUUID", "(Ljava/lang/String;)V");
			jmethodID setExecuterInstanceUUIDMethod = env->GetMethodID(class_JJobContext, "setExecuterInstanceUUID", "(Ljava/lang/String;)V");
			jmethodID setDwLauncherMethod = env->GetMethodID(class_JJobContext, "setDwLauncher", "(J)V");
			jmethodID setD2dUUIDMethod = env->GetMethodID(class_JJobContext, "setD2dUUID", "(Ljava/lang/String;)V");
			jmethodID setPolicyGUID = env->GetMethodID(class_JJobContext, "setPolicyGUID", "(Ljava/lang/String;)V");
			jmethodID setRpsUUID = env->GetMethodID(class_JJobContext, "setRpsUUID", "(Ljava/lang/String;)V");

			env->CallVoidMethod(jobContext, setJobIDMethod, (jlong)jobIter->dwJobId);
			env->CallVoidMethod(jobContext, setQueueTypeMethod, (jlong)jobIter->dwQueueType);
			env->CallVoidMethod(jobContext, setJobTypeMethod, (jlong)jobIter->dwJobType);
			env->CallVoidMethod(jobContext, setProcessIDMethod, (jlong)jobIter->dwProcessId);
			env->CallVoidMethod(jobContext, setShrmemIDMethod, (jlong)jobIter->dwJMShmId);
			env->CallVoidMethod(jobContext, setDwLauncherMethod, (jlong)jobIter->ulJobAttribute);
			env->CallVoidMethod(jobContext, setLauncherInstanceUUIDMethod, WCHARToJString(env, jobIter->wstrLauncherInstanceUUID));
			env->CallVoidMethod(jobContext, setExecuterInstanceUUIDMethod, WCHARToJString(env, jobIter->wstrNodeName));
			env->CallVoidMethod(jobContext, setD2dUUIDMethod, WCHARToJString(env, jobIter->wstrD2DNodeGuid));
			env->CallVoidMethod(jobContext, setPolicyGUID, WCHARToJString(env, jobIter->wstrPolicyGuid));
			env->CallVoidMethod(jobContext, setRpsUUID, WCHARToJString(env, jobIter->wstrRPSNodeGuid));
			env->CallBooleanMethod(jretArray, addMethod, jobContext);
		}

		if (class_JJobContext != NULL) env->DeleteLocalRef(class_JJobContext);
	}
	return dwRet;
}



JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_startService(JNIEnv *env, jclass clz, jstring serviceName)
{
	STARTUPINFO si = { 0 };
	si.cb = sizeof(si);
	PROCESS_INFORMATION pi;

	//start data store service
	wstring strCMDLine = L"net start CASDatastoreSVC";

	BOOL bRet = CreateProcess(NULL, (wchar_t *)strCMDLine.c_str(),
		NULL, NULL, FALSE, 0, NULL, NULL, &si, &pi);

	//do not handle the result of the process.

	return 1;
}

/*
* Class:     com_ca_arcflash_rps_jni_RPSWSJNI
* Method:    startMerge
* Signature: (Lcom/ca/arcflash/service/jni/model/MergeJobScript;Ljava/lang/String;)J
*/
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_startMerge
(JNIEnv *env, jclass jcls, jobject jjobscript, jstring jsPath)
{
	CMergeJS mergeJS;
	JMergeJobScript2CMergeJS(env, jjobscript, mergeJS);

	WCHAR* cjspath = JStringToWCHAR(env, jsPath);
	RPSISaveMergeJS(mergeJS, cjspath);
	long ret = RPSIStartJob(cjspath);
	if (cjspath != NULL)
		free(cjspath);
	return ret;
}

/*
* Class:     com_ca_arcflash_rps_jni_RPSWSJNI
* Method:    stopMerge
* Signature: (J)J
*/
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_stopMerge
(JNIEnv *env, jclass jcls, jlong jJobId)
{
	return RPSIStopMergeJob(jJobId);
}

/*
* Class:     com_ca_arcflash_rps_jni_RPSWSJNI
* Method:    getMergeJobMonitor
* Signature: (JLcom/ca/arcflash/webservice/data/merge/MergeJobMonitor;)J
*/
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getMergeJobMonitor
(JNIEnv *env, jclass jcls, jlong address, jobject jJobMonitor)
{
	IJobMonInterface *pJobM = (IJobMonInterface *)address;
	PST_MERGE_CTRL mergeJM = NULL;

	if (pJobM)
	{
		mergeJM = pJobM->MergeJobMon();
		if (mergeJM){
			MergeControl2JMergeJobMonitor(env, mergeJM, jJobMonitor);
			return 0;
		}
	}

	return -1;
}

/*
* Class:     com_ca_arcflash_rps_jni_RPSWSJNI
* Method:    createMergeJobMonitor
* Signature: (J)J
*/
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_createMergeJobMonitor
(JNIEnv *env, jclass jcls, jlong jjobId)
{
	IJobMonInterface *pAFJM = NULL;
	DWORD dwRet = 0;
	pAFJM = RPSICreateMergeJM(jjobId, &dwRet);
	if (!dwRet)
	{
		return (jlong)pAFJM;
	}

	return 0;
}

/*
* Class:     com_ca_arcflash_rps_jni_RPSWSJNI
* Method:    releaseMergeJobMonitor
* Signature: (J)J
*/
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_releaseMergeJobMonitor
(JNIEnv *env, jclass jcls, jlong address)
{
	IJobMonInterface *pJobM = (IJobMonInterface *)address;
	RPSIReleaseMergeJM(&pJobM);
	return 0;
}

/*
* Class:     com_ca_arcflash_rps_jni_RPSWSJNI
* Method:    isMergeJobAvailable
* Signature: (ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)J
*/
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_isMergeJobAvailable
(JNIEnv * env, jclass jcls, jint retentionCount, jstring jDestination, jstring jServerUUID,
jstring jUserName, jstring jPasswrod, jstring jRepDataStore)
{
	WCHAR* wdest = JStringToWCHAR(env, jDestination);
	WCHAR* wvmuuid = JStringToWCHAR(env, jServerUUID);
	WCHAR* wdestuser = JStringToWCHAR(env, jUserName);
	WCHAR* wdestPwd = JStringToWCHAR(env, jPasswrod);
	WCHAR* wdatastore = JStringToWCHAR(env, jRepDataStore);

	long ret = RPSIIsMergeJobAvailable(retentionCount, wdest, wvmuuid, wdestuser, wdestPwd, wdatastore);

	if (wdest != NULL)
		free(wdest);
	if (wvmuuid != NULL)
		free(wvmuuid);
	if (wdestuser != NULL)
		free(wdestuser);
	if (wdestPwd != NULL)
		free(wdestPwd);
	if (wdatastore != NULL)
		free(wdatastore);
	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSIIsMergeJobAvailableEx
(JNIEnv *env, jclass jclzz, jstring jdest, jint retentionCount, jint dailyCount, jint weeklyCount, jint monthlyCount, jstring jVMUUID, jstring ds4Replication, jstring jDestUser, jstring jDestPwd)
{
	WCHAR* wdest = JStringToWCHAR(env, jdest);
	WCHAR* wvmuuid = JStringToWCHAR(env, jVMUUID);
	WCHAR* wDS4Replication = JStringToWCHAR(env, ds4Replication);
	WCHAR* wdestuser = JStringToWCHAR(env, jDestUser);
	WCHAR* wdestPwd = JStringToWCHAR(env, jDestPwd);

	long ret = RPSIIsMergeJobAvailableEx(wdest, retentionCount, dailyCount, weeklyCount, monthlyCount, wvmuuid, wDS4Replication, wdestuser, wdestPwd);

	if (wdest != NULL)
		free(wdest);

	if (wvmuuid != NULL)
		free(wvmuuid);

	if (wDS4Replication != NULL)
		free(wDS4Replication);

	if (wdestuser != NULL)
		free(wdestuser);

	if (wdestPwd != NULL)
		free(wdestPwd);

	return ret;

}

/*
* Class:     com_ca_arcflash_rps_jni_RPSWSJNI
* Method:    isMergeJobAvailableExt
* Signature: (Lcom/ca/arcflash/service/jni/model/JMergeData;)J
*/
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_isMergeJobAvailableExt
(JNIEnv *env, jclass jcls, jobject jMergeData)
{
	WCHAR* wDkupDest = NULL;
	WCHAR* wVmUUIDs = NULL;
	DWORD  retentionCount = 0;
	DWORD  dailyCount = 0;
	DWORD  weeklyCount = 0;
	DWORD  monthlyCount = 0;
	WCHAR* wDsForRep = 0;
	WCHAR* wDestUser = NULL;
	WCHAR* wDestPwd = NULL;
	DWORD  dwArchiveDailySel = 0;
	DWORD  dwArchiveSourceSelection = 0;
	LONGLONG  llArchiveConfigTime = 0;
	DWORD  dwRet = 0;

	// Get data from Java object start...
	jclass mergeClass = env->GetObjectClass(jMergeData);

	jmethodID getBkDest = env->GetMethodID(mergeClass, "getBackupDest", "()Ljava/lang/String;");
	jmethodID getRetCnt = env->GetMethodID(mergeClass, "getCustomRetentionCnt", "()I");
	jmethodID getDayCnt = env->GetMethodID(mergeClass, "getDailyCnt", "()I");
	jmethodID getWekCnt = env->GetMethodID(mergeClass, "getWeeklyCnt", "()I");
	jmethodID getMthCnt = env->GetMethodID(mergeClass, "getMonthlyCnt", "()I");
	jmethodID getVMGuid = env->GetMethodID(mergeClass, "getVmGUID", "()Ljava/lang/String;");
	jmethodID getDs4Rep = env->GetMethodID(mergeClass, "getDataStore4Replication", "()Ljava/lang/String;");
	jmethodID getBkUser = env->GetMethodID(mergeClass, "getBackupUser", "()Ljava/lang/String;");
	jmethodID getbkPawd = env->GetMethodID(mergeClass, "getBackupPassword", "()Ljava/lang/String;");
	jmethodID getCfTime = env->GetMethodID(mergeClass, "getArchiveConfigTime", "()J");
	jmethodID getSrcSel = env->GetMethodID(mergeClass, "getArchiveSourceSelection", "()J");
	jmethodID getDayArr = env->GetMethodID(mergeClass, "getArchiveDailySelectedDays", "()[Z");

	wDkupDest = JStringToWCHAR(env, (jstring)env->CallObjectMethod(jMergeData, getBkDest));
	wVmUUIDs = JStringToWCHAR(env, (jstring)env->CallObjectMethod(jMergeData, getVMGuid));
	wDsForRep = JStringToWCHAR(env, (jstring)env->CallObjectMethod(jMergeData, getDs4Rep));
	wDestUser = JStringToWCHAR(env, (jstring)env->CallObjectMethod(jMergeData, getBkUser));
	wDestPwd = JStringToWCHAR(env, (jstring)env->CallObjectMethod(jMergeData, getbkPawd));

	retentionCount = env->CallIntMethod(jMergeData, getRetCnt);
	dailyCount = env->CallIntMethod(jMergeData, getDayCnt);
	weeklyCount = env->CallIntMethod(jMergeData, getWekCnt);
	monthlyCount = env->CallIntMethod(jMergeData, getMthCnt);

	dwArchiveSourceSelection = env->CallLongMethod(jMergeData, getSrcSel);
	llArchiveConfigTime = (DWORD)env->CallLongMethod(jMergeData, getCfTime);

	jobject jBoolArrObj = env->CallObjectMethod(jMergeData, getDayArr);
	if (NULL != jBoolArrObj)
	{
		jbooleanArray* jData = reinterpret_cast<jbooleanArray*>(&jBoolArrObj);
		jboolean* bDaySels = env->GetBooleanArrayElements(*jData, NULL);
		if (NULL != bDaySels)
		{
			jsize nLen = env->GetArrayLength(*jData);
			for (jsize idx = 0; idx < nLen; idx++)
			{
				if (0 != bDaySels[idx])
					dwArchiveDailySel |= (1 << idx);		//bit0:bit6 <--> sun:sat
			}

			env->ReleaseBooleanArrayElements(*jData, bDaySels, 0);
		}
		else
		{
			dwRet = GetLastError();
			pLogObj->LogW(LL_WAR, dwRet, __WFUNCTION__ L"env->GetBooleanArrayElements() return NULL.");
		}
	}
	else
	{
		dwRet = GetLastError();
		pLogObj->LogW(LL_WAR, dwRet, __WFUNCTION__ L" JMergeData::getArchiveDailySelectedDays() return NULL.");
	}
	// Get data from Java object end.

	pLogObj->LogW(LL_INF, 0, __WFUNCTION__ L" [Enable: %u, SelectedDays: %u, ConfigTime: %llu]\n", dwArchiveSourceSelection, dwArchiveDailySel, llArchiveConfigTime);

	// call native method
	long ret = RPSIIsMergeJobAvailableEx(wDkupDest, retentionCount, dailyCount, weeklyCount, monthlyCount, wVmUUIDs, wDsForRep, wDestUser, wDestPwd,
		dwArchiveDailySel, dwArchiveSourceSelection, llArchiveConfigTime);

	if (wDkupDest){ free(wDkupDest); wDkupDest = NULL; }
	if (wVmUUIDs) { free(wVmUUIDs); wVmUUIDs = NULL; }
	if (wDsForRep){ free(wDsForRep); wDsForRep = NULL; }
	if (wDestUser){ free(wDestUser); wDestUser = NULL; }
	if (wDestPwd) { free(wDestPwd); wDestPwd = NULL; }

	return ret;
}
/*
* Class:     com_ca_arcflash_rps_jni_RPSWSJNI
* Method:    AFIRetrieveMergeJM
* Signature: (Ljava/util/List;)J
*/
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFIRetrieveMergeJM
(JNIEnv *env, jclass jcls, jobject jobList)
{
	ActJobVector vecActiveJob;
	long ret = RPSIRetrieveMergeJM(vecActiveJob);

	if (ret == 0) {
		ActiveMergeVector2JActiveMergeJobs(env, jobList, vecActiveJob);
	}
	return ret;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSSendClientJobRunCmdOnJobArrival
(JNIEnv* env, jclass clz, jobject jRpstJobInfo)
{
	RPS_JOB_INFO_S rpsJobInfo;
	memset(&rpsJobInfo, 0, sizeof(RPS_JOB_INFO_S));
	JRPSJobInfo2RPS_JOB_INFO(env, rpsJobInfo, jRpstJobInfo);

	return RPSSendClientJobRunCmdOnJobArrival(&rpsJobInfo);
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSSendClientJobStopCmdOnJobArrival
(JNIEnv* env, jclass clz, jobject jRpstJobInfo, jint jStopReason)
{
	RPS_JOB_INFO_S rpsJobInfo;
	memset(&rpsJobInfo, 0, sizeof(RPS_JOB_INFO_S));
	JRPSJobInfo2RPS_JOB_INFO(env, rpsJobInfo, jRpstJobInfo);

	return RPSSendClientJobStopCmdOnJobArrival(&rpsJobInfo, jStopReason);
}

//
// Insert a new Job History for RPS
// jobID:				the job ID
// jobType:				the job type
// startTimeUTC:		the job start time in UTC
// startTimeLocal:		the job strat time in Local
// rpsNodeUUID:			the RPS node UUID
// d2dNodeUUID:			the D2D node UUID, it might also be the vm instance UUID
// d2dNodeName:			the D2D node name, it might also be the vm instance name
// repSrcUUID:			for replicate job only - it the replication source RPS node UUID
// repDstUUID:			for replicate job only - it the replication target RPS node UUID
// datastoreUUID:		the datastore UUID
// targetDatastoreUUID: the datastore UUID for destination RPS Server, only for replication job
// jobName :			the job name for specify job, currently only backup job have job name
JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_updateRPSJobHistory
(JNIEnv *env, jclass clz, jobject jJobHistory)
{
	PFLASHDB_JOB_HISTORY pHistory = new FLASHDB_JOB_HISTORY();
	ZeroMemory(pHistory, sizeof(FLASHDB_JOB_HISTORY));
	JJobHistory2PFLASHDB_JOB_HISTORY(env, jJobHistory, pHistory);
	pHistory->dwStatus = JS_CRASHED;

	WCHAR szHostName[MAX_PATH] = { 0 };
	DWORD dwSize = _ARRAYSIZE(szHostName);
	::GetComputerName(szHostName, &dwSize);

	if (wcslen(pHistory->wszRunningNodeUUID) == 0)
		wcsncpy_s(pHistory->wszRunningNode, _ARRAYSIZE(pHistory->wszRunningNode), szHostName, _TRUNCATE);

	if (wcslen(pHistory->wszDisposeNodeUUID) == 0 && wcslen(pHistory->wszRunningNodeUUID) != 0)
		wcsncpy_s(pHistory->wszDisposeNodeUUID, _ARRAYSIZE(pHistory->wszDisposeNodeUUID), pHistory->wszRunningNodeUUID, _TRUNCATE);

	if (wcslen(pHistory->wszDisposeNode) == 0 && wcslen(pHistory->wszDisposeNodeUUID) == 0)
		wcsncpy_s(pHistory->wszDisposeNode, _ARRAYSIZE(pHistory->wszDisposeNode), szHostName, _TRUNCATE);

	StartNewJob(APT_RPS, pHistory);
	delete pHistory;
	pHistory = NULL;

	return 0;

}

//
// Insert a new Job History for RPS
// jobID:				the job ID
// jobType:				the job type
// startTimeUTC:		the job start time in UTC
// startTimeLocal:		the job strat time in Local
// rpsNodeUUID:			the RPS node UUID
// d2dNodeUUID:			the D2D node UUID, it might also be the vm instance UUID
// d2dNodeName:			the D2D node name, it might also be the vm instance name
// repSrcUUID:			for replicate job only - it the replication source RPS node UUID
// repDstUUID:			for replicate job only - it the replication target RPS node UUID
// datastoreUUID:		the datastore UUID
// targetDatastoreUUID: the datastore UUID for destination RPS Server, only for replication job
// jobName :			the job name for specify job, currently only backup job have job name
JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_addMissedRPSJobHistory
(JNIEnv *env, jclass clz, jobject jJobHistory)
{

	PFLASHDB_JOB_HISTORY pHistory = new FLASHDB_JOB_HISTORY();
	ZeroMemory(pHistory, sizeof(FLASHDB_JOB_HISTORY));
	JJobHistory2PFLASHDB_JOB_HISTORY(env, jJobHistory, pHistory);
	pHistory->dwStatus = JS_CRASHED;

	WCHAR szHostName[MAX_PATH] = { 0 };
	DWORD dwSize = _ARRAYSIZE(szHostName);
	::GetComputerName(szHostName, &dwSize);

	if (wcslen(pHistory->wszRunningNodeUUID) == 0)
		wcsncpy_s(pHistory->wszRunningNode, _ARRAYSIZE(pHistory->wszRunningNode), szHostName, _TRUNCATE);

	if (wcslen(pHistory->wszDisposeNodeUUID) == 0 && wcslen(pHistory->wszRunningNodeUUID) != 0)
		wcsncpy_s(pHistory->wszDisposeNodeUUID, _ARRAYSIZE(pHistory->wszDisposeNodeUUID), pHistory->wszRunningNodeUUID, _TRUNCATE);

	if (wcslen(pHistory->wszDisposeNode) == 0 && wcslen(pHistory->wszDisposeNodeUUID) == 0)
		wcsncpy_s(pHistory->wszDisposeNode, _ARRAYSIZE(pHistory->wszDisposeNode), szHostName, _TRUNCATE);


	StartNewJob(APT_RPS, pHistory);
	MarkJobEnd(APT_RPS, pHistory->ullJobId, pHistory->dwStatus);

	delete pHistory;
	pHistory = NULL;

	return 0;

}

/*
* Class:     com_ca_arcflash_rps_jni_RPSWSJNI
* Method:    getRPSServerSID
* Signature: ()Ljava/lang/String;
*/
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getRPSServerSID
(JNIEnv * env, jclass jcls)
{
	wstring sid;
	DWORD ret = RPSIGetUserSID(sid);
	if (ret == 0)
		return WCHARToJString(env, sid);
	else
		return NULL;
}

//
// mark job as end for rps
// jobID:          the job ID
// jobStatus:      the job status - crashed, finished....
JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_markRPSJobEnd
(JNIEnv *env, jclass clz, jlong jobID, jlong jobStatus)
{
	return MarkJobEnd(APT_RPS, jobID, jobStatus);
}

/*
* Class:     com_ca_arcflash_rps_jni_RPSWSJNI
* Method:    getActivityLogPaths4Sync
* Signature: (ILjava/lang/String;Ljava/util/ArrayList;)J
*/
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getActivityLogPaths4Sync
(JNIEnv *env, jclass jcls, jint logType, jstring vmInstanceUUID, jobject pathsList)
{
	wchar_t* vmUUID = JStringToWCHAR(env, vmInstanceUUID);
	PSyncLogFiles logFiles = NULL;
	UINT count = 0;
	int ret = 0;
	if (logType == 1)
	{
		//for CPM
		ret = PrepareSyncLogOfCPM(APT_RPS, vmUUID, &logFiles, &count);
	}
	else if (logType == 2)
	{
		//for VCM
		ret = PrepareSyncLogOfVCM(APT_RPS, vmUUID, &logFiles, &count);
	}

	AddPathsToList(env, pathsList, logFiles, count);

	FreeSyncLogFiles(logFiles);

	if (vmUUID != NULL)
	{
		free(vmUUID);

	}
	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getActivityLogPaths4FullSync
(JNIEnv *env, jclass jcls, jint logType, jstring vmInstanceUUID, jobject pathsList)
{
	wchar_t* vmUUID = JStringToWCHAR(env, vmInstanceUUID);
	PSyncLogFiles logFiles = NULL;
	UINT count = 0;
	int ret = 0;
	if (logType == 1)
	{
		//for CPM
		ret = PrepareFullSyncLogOfCPM(APT_RPS, vmUUID, &logFiles, &count);
	}
	else if (logType == 2)
	{
		//for VCM
		ret = PrepareFullSyncLogOfVCM(APT_RPS, vmUUID, &logFiles, &count);
	}

	AddPathsToList(env, pathsList, logFiles, count);

	FreeSyncLogFiles(logFiles);

	if (vmUUID != NULL)
	{
		free(vmUUID);

	}
	return ret;
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getJobHistoryPaths4IncrementalSync
(JNIEnv *env, jclass jcls, jobject pathsList)
{
	PSyncLogFiles logFiles = NULL;
	UINT count = 0;
	int ret = 0;

	ret = PrepareSyncJobHistoryOfCPM(APT_RPS, &logFiles, &count);

	AddPathsToList(env, pathsList, logFiles, count);

	FreeSyncLogFiles(logFiles);

	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getJobHistoryPaths4FullSync
(JNIEnv *env, jclass jcls, jobject pathsList)
{
	PSyncLogFiles logFiles = NULL;
	UINT count = 0;
	int ret = 0;

	ret = PrepareFullSyncJobHistoryOfCPM(APT_RPS, &logFiles, &count);

	AddPathsToList(env, pathsList, logFiles, count);

	FreeSyncLogFiles(logFiles);

	return ret;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getJobHistory
(JNIEnv *env, jclass jcls, jlong start, jlong request, jobject jFilter, jobject jJobHistoryResult)
{
	PFLASHDB_JOB_HISTORY pHistories;
	ULONGLONG ullCnt = 0, ullTotal = 0;
	DWORD dwRet = 0;

	pHistories = (PFLASHDB_JOB_HISTORY)HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, sizeof(FLASHDB_JOB_HISTORY)*request);
	if (pHistories == NULL)
	{
		CDbgLog dbgLog;
		dbgLog.LogW(LL_INF, 0, L"GetJobHistories cann't allocate memory for : %d job history", request);
	}

	if (jFilter != NULL)
	{
		FLASHDB_JOB_HISTORY_FILTER_COL jobFilter;
		JFlashJobHistoryFilter2FLASHDB_JOB_HISTORY_FILTER_COL(env, jFilter, jobFilter);
		dwRet = GetJobHistories(APT_RPS, start, request, &jobFilter, &ullCnt, &ullTotal, &pHistories);
	}
	else
	{
		dwRet = GetJobHistories(APT_RPS, start, request, NULL, &ullCnt, &ullTotal, &pHistories);
	}

	PFLASHDB_JOB_HISTORY2JFlashJobHistoryResult(env, pHistories, ullCnt, ullTotal, jJobHistoryResult);

	if (pHistories) HeapFree(GetProcessHeap(), 0, pHistories);

	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getFilePaths4Sync
(JNIEnv *env, jclass jcls, jlong jlSyncTo, jlong jlSyncFrom, jlong jlSyncType, jstring vmInstanceUUID, jobject pathsList)
{
	PSyncLogFiles logFiles = NULL;
	UINT count = 0;

	DATASYNC_ARGS dataSyncArgs;
	dataSyncArgs.dwSyncTo = jlSyncTo;
	dataSyncArgs.dwSyncFrom = jlSyncFrom;
	dataSyncArgs.dwSyncType = jlSyncType;

	wchar_t* vmUUID = JStringToWCHAR(env, vmInstanceUUID);
	if (vmUUID)
	{
		wcsncpy_s(dataSyncArgs.szNodeUUID, _countof(dataSyncArgs.szNodeUUID), vmUUID, _TRUNCATE);
		free(vmUUID);
	}

	int ret = 0;

	ret = PrepareSyncData(dataSyncArgs, &logFiles, &count);

	AddPathsToList(env, pathsList, logFiles, count);

	FreeSyncLogFiles(logFiles);

	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_launchCatalogEx
(JNIEnv * env, jclass jcls, jobject jobj)
{
	jclass context = env->FindClass("com/ca/arcflash/rps/jni/model/JRPSCatalogJobContext");
	jmethodID getIDM = env->GetMethodID(context, "getId", "()J");
	jmethodID getTypeM = env->GetMethodID(context, "getType", "()J");
	jmethodID getVmIndentification = env->GetMethodID(context, "getVmIndentification", "()Ljava/lang/String;");
	jmethodID getConnInfo = env->GetMethodID(context, "getConnInfo", "()Lcom/ca/arcflash/service/jni/model/JNetConnInfo;");
	jmethodID getRPSIdentification = env->GetMethodID(context, "getRpsServerIdentification", "()Ljava/lang/String;");

	jlong jobid = env->CallLongMethod(jobj, getIDM);
	jlong type = env->CallLongMethod(jobj, getTypeM);
	jstring vminstanceUUID = (jstring)env->CallObjectMethod(jobj, getVmIndentification);
	jobject jconn = env->CallObjectMethod(jobj, getConnInfo);
	jstring rpsIdentification = (jstring)env->CallObjectMethod(jobj, getRPSIdentification);

	NET_CONN_INFO conn;
	memset(&conn, 0, sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env, &jconn, conn);
	WCHAR* pwzInstanceUUID = NULL;
	WCHAR* pwzRpsIdentification = NULL;

	if (vminstanceUUID != NULL)
		pwzInstanceUUID = JStringToWCHAR(env, vminstanceUUID);

	if (rpsIdentification != NULL)
		pwzRpsIdentification = JStringToWCHAR(env, rpsIdentification);

	DWORD ret = RPSStartCatalogGenerator(type, jobid, NULL, NULL, NULL, pwzInstanceUUID, &conn, pwzRpsIdentification);

	if (pwzInstanceUUID) free(pwzInstanceUUID);
	if (pwzRpsIdentification) free(pwzRpsIdentification);

	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSSaveJS4FSOndemand
(JNIEnv *env, jclass cls, jstring backupDest, jstring username, jstring pass,
jstring domain, jlong sessionNum, jstring vmInstanceID, jlong subSession, jstring sessionPass)
{
	wchar_t* pDest = JStringToWCHAR(env, backupDest);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	wchar_t* pUser = JStringToWCHAR(env, username);
	wchar_t* pPwd = JStringToWCHAR(env, pass);
	wchar_t* pVMInstanceID = JStringToWCHAR(env, vmInstanceID);
	wchar_t* sessionPwd = JStringToWCHAR(env, sessionPass);
	DWORD ret = -1;

	NET_CONN_INFO info;
	memset(&info, 0, sizeof(NET_CONN_INFO));

	if (pDest != NULL)
	{
		wcsncpy_s(info.szDir, _countof(info.szDir), pDest, _TRUNCATE);
		SAFE_FREE(pDest);
	}
	if (pDomain != NULL)
	{
		wcsncpy_s(info.szDomain, _countof(info.szDomain), pDomain, _TRUNCATE);
		SAFE_FREE(pDomain);
	}
	if (pUser != NULL)
	{
		wcsncpy_s(info.szUsr, _countof(info.szUsr), pUser, _TRUNCATE);
		SAFE_FREE(pUser);
	}
	if (pPwd != NULL)
	{
		wcsncpy_s(info.szPwd, _countof(info.szPwd), pPwd, _TRUNCATE);
		SAFE_FREE(pPwd);
	}
	//save grt catalog information
	ret = RPSSaveJS4FSOndemand(info, sessionNum, pVMInstanceID, subSession, sessionPwd);

	SAFE_FREE(sessionPwd);

	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_moveCatalogJobScript
(JNIEnv * env, jclass jcls, jlong jJobQType, jstring jBKDest, jlong jSessNum, jstring jJobQID, jstring jCatalogNodeID)
{
	wchar_t* wszBKDest = JStringToWCHAR(env, jBKDest);
	wchar_t* wszJobQID = JStringToWCHAR(env, jJobQID);
	wchar_t* wszCatalogNodeID = JStringToWCHAR(env, jCatalogNodeID);

	DWORD dwRet = RPSMoveJobScript(jJobQType, wszBKDest, jSessNum, wszJobQID, wszCatalogNodeID);

	SAFE_FREE(wszBKDest);
	SAFE_FREE(wszJobQID);
	SAFE_FREE(wszCatalogNodeID);

	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_rpsQueryJobQueue
(JNIEnv *env, jclass jcls, jlong jJobQType, jstring jJobQIdentity,
jobject jJobScriptList, jboolean jCreateJobQFolder, jstring jCatalogModeID)

{
	wchar_t *pwzJobQIdentity = JStringToWCHAR(env, jJobQIdentity);
	wchar_t *pwzCatalogModeID = JStringToWCHAR(env, jCatalogModeID);

	wstring wsJobQPath;
	WSVector vecJobScriptList;

	DWORD dwRet = RPSIQueryJobQueue(jJobQType, pwzJobQIdentity, &wsJobQPath, &vecJobScriptList, jCreateJobQFolder, pwzCatalogModeID);

	Convert2JRPSCatalogScriptInfo(env, wsJobQPath, vecJobScriptList, jJobScriptList);

	if (pwzJobQIdentity) free(pwzJobQIdentity);
	if (pwzCatalogModeID) free(pwzCatalogModeID);

	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_isCatalogAvailable
(JNIEnv *env, jclass jcls, jlong jQueueType, jstring jJobQIdentity, jstring rpsSvrIdentity)
{
	wchar_t *pwzJobQIdentity = JStringToWCHAR(env, jJobQIdentity);
	wchar_t *pwzRPSSvrIdentity = JStringToWCHAR(env, rpsSvrIdentity);

	DWORD dwRet = RPSIsCatalogAvailable(jQueueType, pwzJobQIdentity, pwzRPSSvrIdentity);

	if (pwzJobQIdentity) free(pwzJobQIdentity);
	if (pwzRPSSvrIdentity) free(pwzRPSSvrIdentity);

	return dwRet;
}

void GetCLSIDFromString(wchar_t *pwzGUID, GUID *pGUID)
{
	HRESULT hret;

	if (!pwzGUID || !pGUID)
		return;

	hret = CoInitialize(NULL);
	if (!SUCCEEDED(hret))
		return;

	int nLen = wcslen(pwzGUID) + 3;

	wchar_t *ptemp = (wchar_t*)malloc(sizeof(wchar_t)*nLen);

	_snwprintf_s(ptemp, nLen, _TRUNCATE, L"{%s}", pwzGUID);

	hret = CLSIDFromString(ptemp, pGUID);

	SAFE_FREE(ptemp);

	CoUninitialize();
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_rpsRepNetworkThrottling
(JNIEnv *env, jclass cls, jlong jOperation, jstring jPolicyUUID, jobject jThrottlingSetting)
{
	long ret = -1;

	NETWORK_THROTTLING_POLICY policy;
	memset(&policy, 0, sizeof(NETWORK_THROTTLING_POLICY));

	wchar_t* pwzPolicyGUID = JStringToWCHAR(env, jPolicyUUID);
	GetCLSIDFromString(pwzPolicyGUID, &policy.GuidPolicy);
	SAFE_FREE(pwzPolicyGUID);

	if (jOperation == ENTO_DELETE)
	{
		return RPSNTP_RemovePolicy(&policy.GuidPolicy);
	}


	if (jThrottlingSetting == NULL)
		return 0;

	ConvertJDailyScheduleDetailItemList2NETWORK_THROTTLING_POLICY(env, jThrottlingSetting, policy);
	if (jOperation == ENTO_CREATE)
	{
		ret = RPSNTP_AddPolicy(&policy);
	}
	else if (jOperation == ENTO_MODIFY)
	{
		ret = RPSNTP_UpdatePolicy(&policy);
	}

	FreeNETWORK_THROTTLING_POLICY(policy);

	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_rpsXVerifyDestUser
(JNIEnv * env, jclass, jstring jstrUser, jstring jstrPSW, jstring jstrPath)
{
	wchar_t* wpszPath = JStringToWCHAR(env, jstrPath);
	wchar_t* wpszUser = JStringToWCHAR(env, jstrUser);
	wchar_t* wpszPSW = JStringToWCHAR(env, jstrPSW);

	DWORD dwRet = AFVerifyDestUser(wpszPath, wpszUser, wpszPSW);

	if (wpszPath) free(wpszPath);
	if (wpszUser) free(wpszUser);
	if (wpszPSW) free(wpszPSW);

	return dwRet;
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSGetNetworkPathForMappedDrive
(JNIEnv *env, jclass clz, jstring userName, jobject mapList)
{
	jclass mapListClass = env->GetObjectClass(mapList);
	jmethodID addMethodID = env->GetMethodID(mapListClass, "add", "(Ljava/lang/Object;)Z");

	jclass networkPathClass = env->FindClass("com/ca/arcflash/webservice/data/NetworkPath");
	jmethodID constructor = env->GetMethodID(networkPathClass, "<init>", "()V");
	jfieldID driverLetterField = env->GetFieldID(networkPathClass, "driverletter", "Ljava/lang/String;");
	jfieldID remotePathField = env->GetFieldID(networkPathClass, "remotePath", "Ljava/lang/String;");

	wchar_t* pDest = JStringToWCHAR(env, userName);
	std::vector<MAPPED_DRV_PATH> mapVector;
	DWORD ret = RPSIGetAllMappedDrvPath(pDest, mapVector);

	for (std::vector<MAPPED_DRV_PATH>::iterator driverMapPath = mapVector.begin(); driverMapPath != mapVector.end(); driverMapPath++)
	{
		jobject mapPathObject = env->NewObject(networkPathClass, constructor);
		jstring mnt = WCHARToJString(env, (wchar_t*)driverMapPath->strMnt.c_str());
		env->SetObjectField(mapPathObject, driverLetterField, mnt);
		if (mnt != NULL)
			env->DeleteLocalRef(mnt);

		jstring remotePath = WCHARToJString(env, (wchar_t*)driverMapPath->strPath.c_str());
		env->SetObjectField(mapPathObject, remotePathField, remotePath);
		if (remotePath != NULL)
			env->DeleteLocalRef(remotePath);

		env->CallBooleanMethod(mapList, addMethodID, mapPathObject);
		if (mapPathObject != NULL)
			env->DeleteLocalRef(mapPathObject);
	}

	if (pDest != NULL)
		free(pDest);

	return (jlong)ret;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetCatalogStatus(JNIEnv *env, jclass jclz, jstring jsessionPath)
{
	wchar_t* sessionPath = JStringToWCHAR(env, jsessionPath);
	jint jRet = JNI_ERR;

	DWORD dwRet = RPSGetCatalogStatus(sessionPath);

	if (sessionPath != NULL)
	{
		free(sessionPath);
		sessionPath = NULL;
	}
	return (jint)dwRet;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getSwitchIntFromFile
(JNIEnv *env, jclass className, jstring strAppName, jstring strKeyName, jint nDefault, jstring strFullPathOfFile)
{
	wchar_t *pwszAppName = JStringToWCHAR(env, strAppName);
	wchar_t *pwszKeyName = JStringToWCHAR(env, strKeyName);
	wchar_t *pwszFilepath = JStringToWCHAR(env, strFullPathOfFile);
	int nRet = GetSwitchIntFromIni(pwszAppName, pwszKeyName, nDefault, pwszFilepath);
	SAFE_FREE(pwszAppName);
	SAFE_FREE(pwszKeyName);
	SAFE_FREE(pwszFilepath);
	return nRet;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getSwitchIntFromReg
(JNIEnv *env, jclass className, jstring strValueName, jint nDefault, jstring strSubkey)
{
	wchar_t *pwszValueName = JStringToWCHAR(env, strValueName);
	wchar_t *pwszSubKey = JStringToWCHAR(env, strSubkey);

	DWORD dwValue = nDefault;
	GetSwitchDWORDFromReg(pwszValueName, dwValue, pwszSubKey, nDefault);

	SAFE_FREE(pwszValueName);
	SAFE_FREE(pwszSubKey);
	return (jint)dwValue;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getSwitchStringFromFile
(JNIEnv *env, jclass className, jstring strAppName, jstring strKeyName, jstring strDefault, jstring strFullPathOfFile)
{
	wchar_t *pwszAppName = JStringToWCHAR(env, strAppName);
	wchar_t *pwszKeyName = JStringToWCHAR(env, strKeyName);
	wchar_t *pwszFilepath = JStringToWCHAR(env, strFullPathOfFile);
	wchar_t *pwszDefault = JStringToWCHAR(env, strDefault);

	WCHAR szRetValue[1024] = { 0 }; // assume 1024 is enough
	GetSwitchStringFromIni(pwszAppName, pwszKeyName, szRetValue, _ARRAYSIZE(szRetValue), pwszDefault, pwszFilepath);

	SAFE_FREE(pwszAppName);
	SAFE_FREE(pwszKeyName);
	SAFE_FREE(pwszFilepath);
	SAFE_FREE(pwszDefault);

	jstring retValue = WCHARToJString(env, szRetValue);
	return retValue;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getSwitchStringFromReg
(JNIEnv *env, jclass className, jstring strValueName, jstring strDefault, jstring strSubKey)
{
	wchar_t *pwszValueName = JStringToWCHAR(env, strValueName);
	wchar_t *pwszSubKey = JStringToWCHAR(env, strSubKey);
	wchar_t *pwszDefault = JStringToWCHAR(env, strDefault);

	WCHAR szRetValue[1024] = { 0 }; // assume 1024 is enough
	DWORD dwSize = _ARRAYSIZE(szRetValue);
	GetSwitchStringFromReg(pwszValueName, szRetValue, &dwSize, pwszSubKey, pwszDefault);

	SAFE_FREE(pwszValueName);
	SAFE_FREE(pwszSubKey);
	SAFE_FREE(pwszDefault);

	jstring retValue = WCHARToJString(env, szRetValue);
	return retValue;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getCatalogStatusEx
(JNIEnv *env, jclass className, jobject objCatalogStatusItem, jstring strJobQIdentity)
{
	wchar_t *pwzJobQIdentity = JStringToWCHAR(env, strJobQIdentity);

	jobject jstr = NULL;
	wstring wstr;
	jclass jcls = env->FindClass("com/ca/arcflash/rps/webservice/data/catalog/RpsCatalogJobQueryItem");

	jfieldID fid_backupDest = env->GetFieldID(jcls, "backupDest", "Ljava/lang/String;");

	jstr = env->GetObjectField(objCatalogStatusItem, fid_backupDest);
	wstr = JStringToWString(env, (jstring)jstr);
	wchar_t*pszbackupDest = _wcsdup(wstr.c_str());

	jclass clsCatalogStatusItem = env->GetObjectClass(objCatalogStatusItem);
	jmethodID midGetStatusItem = env->GetMethodID(clsCatalogStatusItem, "getStatusItem", "()[Lcom/ca/arcflash/rps/webservice/data/catalog/RpsCatalogStatusItem;");
	jobjectArray jobjSIArr = (jobjectArray)env->CallObjectMethod(objCatalogStatusItem, midGetStatusItem);

	jsize statusCnt = env->GetArrayLength(jobjSIArr);

	jclass jclsStatusItem = env->FindClass("com/ca/arcflash/rps/webservice/data/catalog/RpsCatalogStatusItem");
	jfieldID fid_sessnum = env->GetFieldID(jclsStatusItem, "sessnum", "J");
	jfieldID fid_catalogStatus = env->GetFieldID(jclsStatusItem, "catalogStatus", "J");

	long ret = -1;
	PST_CATALOG_STATUS pcsAry = NULL;
	if (statusCnt > 0)
	{
		pcsAry = new ST_CATALOG_STATUS[statusCnt];
		memset(pcsAry, 0, sizeof(ST_CATALOG_STATUS)* statusCnt);

		for (jsize i = 0; i < statusCnt; i++)
		{
			jobject objStatusItem = env->GetObjectArrayElement(jobjSIArr, i);
			pcsAry[i].dwSessNum = (DWORD)env->GetLongField(objStatusItem, fid_sessnum);
		}

		ret = AFIGetCatalogStatus(pszbackupDest, pcsAry, statusCnt, pwzJobQIdentity);

		for (jsize i = 0; i < statusCnt; i++)
		{
			jobject objStatusItem = env->GetObjectArrayElement(jobjSIArr, i);
			env->SetLongField(objStatusItem, fid_catalogStatus, pcsAry[i].dwCatalogStatus);
		}
	}

	SAFE_FREE(pwzJobQIdentity);
	SAFE_FREE(pszbackupDest);
	if (pcsAry != NULL)
		delete[]pcsAry;
	pcsAry = NULL;

	return ret;
}

/*
* Class:     com_ca_arcflash_rps_jni_RPSWSJNI
* Method:    getDateTimeFormat
* Signature: (Lcom/ca/arcflash/webservice/data/DateFormat;)I
*/
JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getDateTimeFormat
(JNIEnv *env, jclass jcls, jobject jDateTimeFormat)
{
	int bufLen = 50;
	LPWSTR pstrShortDate = new WCHAR[bufLen];
	LPWSTR pstrShotTime = new WCHAR[bufLen];
	LPWSTR pstrLongTime = new WCHAR[bufLen];

	wmemset(pstrShortDate, 0, bufLen);
	wmemset(pstrShotTime, 0, bufLen);
	wmemset(pstrLongTime, 0, bufLen);

	GetShortDatePattern(pstrShortDate, bufLen);
	GetShortTimePattern(pstrShotTime, bufLen);
	GetLongTimePattern(pstrLongTime, bufLen);

	jclass class_DateFormat = env->FindClass("com/ca/arcflash/webservice/data/DateFormat");

	jfieldID field_shortTimeFormat = env->GetFieldID(class_DateFormat, "shortTimeFormat", "Ljava/lang/String;");
	jfieldID field_timeFormat = env->GetFieldID(class_DateFormat, "timeFormat", "Ljava/lang/String;");
	jfieldID field_dateFormat = env->GetFieldID(class_DateFormat, "dateFormat", "Ljava/lang/String;");

	jstring jstrShotTime = WCHARToJString(env, pstrShotTime);
	env->SetObjectField(jDateTimeFormat, field_shortTimeFormat, jstrShotTime);

	jstring jstrLongTime = WCHARToJString(env, pstrLongTime);
	env->SetObjectField(jDateTimeFormat, field_timeFormat, jstrLongTime);

	jstring jstrShortDate = WCHARToJString(env, pstrShortDate);
	env->SetObjectField(jDateTimeFormat, field_dateFormat, jstrShortDate);

	delete[]pstrShortDate;
	pstrShortDate = NULL;

	delete[]pstrShotTime;
	pstrShotTime = NULL;

	delete[]pstrLongTime;
	pstrLongTime = NULL;

	return 0;
}

E_QUEUE_TYPE long2E_QUEUE_TYPE(long lQueueType)
{
	E_QUEUE_TYPE eQueueType = EQT_MAKEUP;

	if (lQueueType == 1)
	{
		eQueueType = EQT_REGULAR;
	}
	else if (lQueueType == 2)
	{
		eQueueType = EQT_ONDEMAND;
	}
	else if (lQueueType == 3)
	{
		eQueueType = EQT_MAKEUP;
	}

	return eQueueType;
}

DWORD WINAPI RPSRemoveCatalogJS(E_QUEUE_TYPE eJobQType,
	const WCHAR* pwzDataStoreGUID,
	const WCHAR* pwzJobQIdentity /* = NULL */
	);

JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFIRemoveCatalogJS
(JNIEnv *env, jclass jcls, jlong jlJobQType, jstring jsDataStoreGUID, jstring jsJobQIdentity)
{

	wchar_t* pDataStoreGUID = JStringToWCHAR(env, jsDataStoreGUID);
	std::wstring strDataStoreGUID = pDataStoreGUID;

	wchar_t* pJobQIdentity = JStringToWCHAR(env, jsJobQIdentity);
	std::wstring strJobQIdentity = pJobQIdentity;

	E_QUEUE_TYPE eQueueType = long2E_QUEUE_TYPE(jlJobQType);

	DWORD dwRet = RPSRemoveCatalogJS(eQueueType, strDataStoreGUID.c_str(), strJobQIdentity.c_str());

	if (pDataStoreGUID != NULL)
	{
		free(pDataStoreGUID);
	}

	if (pJobQIdentity != NULL)
	{
		free(pJobQIdentity);
	}

	if (dwRet)
	{
		wprintf(L"get failed[%d]\n", dwRet);
	}

	return (jint)dwRet;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetProtectionInformation
(JNIEnv *env, jclass clz, jstring destination, jstring domain, jstring user, jstring pwd, jobject list)
{
	IBackupSumm *pSumm = NULL;
	wchar_t* pDest = JStringToWCHAR(env, destination);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	wchar_t* pUser = JStringToWCHAR(env, user);
	wchar_t* pPwd = JStringToWCHAR(env, pwd);

	NET_CONN_INFO info;
	memset(&info, 0, sizeof(NET_CONN_INFO));

	if (pDest != NULL)
	{
		wcscpy_s(info.szDir, pDest);
		free(pDest);
	}
	if (pDomain != NULL)
	{
		wcscpy_s(info.szDomain, pDomain);
		free(pDomain);
	}
	if (pUser != NULL)
	{
		wcscpy_s(info.szUsr, pUser);
		free(pUser);
	}
	if (pPwd != NULL)
	{
		wcscpy_s(info.szPwd, pPwd);
		free(pPwd);
	}

	DWORD dwRet = CreateIBackupSumm(info, &pSumm);  //first, create the interface
	if (dwRet)
	{
		wprintf(L"failed to CreateIBackupSumm[%d]\n", dwRet);
		return dwRet;
	}

	jclass class_ArrayList = env->FindClass("java/util/List");
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

	jclass class_ProtectionInformation = env->FindClass("com/ca/arcflash/service/jni/model/JProtectionInfo");
	jmethodID jProtectionInfo_constructor = env->GetMethodID(class_ProtectionInformation, "<init>", "()V");
	jmethodID jmethod_setOsInfo = env->GetMethodID(class_ProtectionInformation, "setLatestOsInfo", "(Lcom/ca/arcflash/service/jni/model/JAgentOSInfo;)V");

	jclass class_JAgentOSInfo = env->FindClass("com/ca/arcflash/service/jni/model/JAgentOSInfo");
	jmethodID JAgentOSInfo_init = env->GetMethodID(class_JAgentOSInfo, "<init>", "()V");

	SUMM_FILTER filter;
	filter.dwType = BACKUP_TYPE_ALL;
	filter.dwStatus = BACKUP_STATUS_SUCCESS;

	VDATA_PROTECTION vProtect;
	VDATA_PROTECTION::iterator itrPrt;
	pSumm->GetDataProtectionSumm(filter, vProtect);

	NODE_OS_INFO osInfo;
	pSumm->GetNodeOSInfo(osInfo);
	pLogObj->LogW(LL_INF, 0, L"GetNodeOSInfo: dwAgentOSType=%d, dwAgentBackupType=%d, dwVMGuestOsType=%d, dwVMHypervisor=%d.",
		osInfo.dwAgentOSType, osInfo.dwAgentBackupType, osInfo.dwVMGuestOsType, osInfo.dwVMHypervisor);

	for (itrPrt = vProtect.begin(); itrPrt != vProtect.end(); itrPrt++)
	{
		jobject jProectionInfo = env->NewObject(class_ProtectionInformation, jProtectionInfo_constructor);
		jobject jNodeOsInfo = env->NewObject(class_JAgentOSInfo, JAgentOSInfo_init);

		NodeOSInfo2JAgentOSInfo(env, jNodeOsInfo, osInfo);
		env->CallVoidMethod(jProectionInfo, jmethod_setOsInfo, jNodeOsInfo);
		if (jNodeOsInfo != NULL) env->DeleteLocalRef(jNodeOsInfo);

		SetStringValue2Field(env, jProectionInfo, "type", itrPrt->strType);
		SetStringValue2Field(env, jProectionInfo, "count", itrPrt->strCount);
		SetStringValue2Field(env, jProectionInfo, "totalLogicalSize", itrPrt->strTotalLogicalSize);
		SetStringValue2Field(env, jProectionInfo, "totalSize", itrPrt->strTotalSize);
		SetStringValue2Field(env, jProectionInfo, "lastBackupTime", itrPrt->strLastBackupTime);
		SetStringValue2Field(env, jProectionInfo, "firstBackupTime", itrPrt->strFirstBackupTime);

		env->CallBooleanMethod(list, id_ArrayList_add, jProectionInfo);
		if (jProectionInfo != NULL) env->DeleteLocalRef(jProectionInfo);
	}

	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);
	if (class_ProtectionInformation != NULL) env->DeleteLocalRef(class_ProtectionInformation);

	pSumm->Release();

	return 0;
}

/*
*  This API is used in ASBU copy to tape only. 
*  The purpose of this API is to reduce disk IO, it only read backupinfodb.xml to get recovery points list.
*  The destination is like ...\<machine name>\
*/
JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_ASBUGetRecoveryPoint
(JNIEnv *env, jclass clz, jstring destination, jstring jStringBeginDate, jstring jStringEndDate, jobject list, jboolean jQueryDetail)
{
	wchar_t* pDest = JStringToWCHAR(env, destination);
	wchar_t* pBeginDate = JStringToWCHAR(env, jStringBeginDate);
	wchar_t* pEndDate = JStringToWCHAR(env, jStringEndDate);

	DWORD dwRet = 0;
	IRestorePoint *pIRest = NULL;
	dwRet = ASBU_CreateIRestorePoint(pDest, &pIRest);
	if (!dwRet)
	{
		VRESTORE_POINT vRest;
		VRESTORE_POINT::iterator itrRest;
		VRESTORE_POINT_ITEM::iterator itrItem;

		pIRest->GetRestorePoints(pBeginDate, pEndDate, vRest, (BOOL)jQueryDetail);
		for (itrRest = vRest.begin(); itrRest != vRest.end(); itrRest++)
		{
			for (itrItem = itrRest->vRestPointItem.begin(); itrItem != itrRest->vRestPointItem.end(); itrItem++)
			{
				AddRestorePoint2List(env, &list, itrItem, (wchar_t*)itrRest->strDate.c_str());
			}
		}
		pIRest->Release();
	}
	else
	{
		wprintf(L"create restore point[%s] failed\n", destination);

	}

	if (pBeginDate != NULL)
	{
		free(pBeginDate);
	}

	if (pEndDate != NULL)
	{
		free(pEndDate);
	}

	if (pDest != NULL)
	{
		free(pDest);
	}


	return dwRet;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetRecoveryPoint
(JNIEnv *env, jclass clz, jstring destination, jstring domain, jstring user, jstring pwd, jstring jStringBeginDate, jstring jStringEndDate, jobject list, jboolean jQueryDetail)
{
	wchar_t* pDest = JStringToWCHAR(env, destination);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	wchar_t* pUser = JStringToWCHAR(env, user);
	wchar_t* pPwd = JStringToWCHAR(env, pwd);
	wchar_t* pBeginDate = JStringToWCHAR(env, jStringBeginDate);
	wchar_t* pEndDate = JStringToWCHAR(env, jStringEndDate);

	DWORD dwRet = 0;
	IRestorePoint *pIRest = NULL;

	NET_CONN_INFO info;
	memset(&info, 0, sizeof(NET_CONN_INFO));

	if (pDest != NULL)
	{
		wcscpy_s(info.szDir, pDest);
		free(pDest);
	}
	if (pDomain != NULL)
	{
		wcscpy_s(info.szDomain, pDomain);
		free(pDomain);
	}
	if (pUser != NULL)
	{
		wcscpy_s(info.szUsr, pUser);
		free(pUser);
	}
	if (pPwd != NULL)
	{
		wcscpy_s(info.szPwd, pPwd);
		free(pPwd);
	}

	dwRet = CreateIRestorePoint(info, &pIRest);

	if (!dwRet)
	{
		VRESTORE_POINT vRest;
		VRESTORE_POINT::iterator itrRest;
		VRESTORE_POINT_ITEM::iterator itrItem;

		pIRest->GetRestorePoints(pBeginDate, pEndDate, vRest, (BOOL)jQueryDetail);
		for (itrRest = vRest.begin(); itrRest != vRest.end(); itrRest++)
		{
			for (itrItem = itrRest->vRestPointItem.begin(); itrItem != itrRest->vRestPointItem.end(); itrItem++)
			{
				AddRestorePoint2List(env, &list, itrItem, (wchar_t*)itrRest->strDate.c_str());
			}
		}
		pIRest->Release();
	}
	else
	{
		wprintf(L"create restore point[%s] failed\n", destination);

	}

	if (pBeginDate != NULL)
	{
		free(pBeginDate);
	}

	if (pEndDate != NULL)
	{
		free(pEndDate);
	}

	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFGetSystemInfo
(JNIEnv *env, jclass clz, jobject jSystemInfo)
{
	D2D_SYSINFO sysInfo;
	int ret = AFCollectSystemInfo(&sysInfo);
	if (ret == 0){
		jclass class_jSystemInfo = env->GetObjectClass(jSystemInfo);

		jmethodID set_DedupInstalled = env->GetMethodID(class_jSystemInfo, "setDedupInstalled", "(Z)V");
		env->CallVoidMethod(jSystemInfo, set_DedupInstalled, (jboolean)sysInfo.isDedupeInstalled);

		jmethodID set_win8 = env->GetMethodID(class_jSystemInfo, "setWin8", "(Z)V");
		env->CallVoidMethod(jSystemInfo, set_win8, (jboolean)sysInfo.isWin8);

		jmethodID setReFsSupported = env->GetMethodID(class_jSystemInfo, "setReFsSupported", "(Z)V");
		env->CallVoidMethod(jSystemInfo, setReFsSupported, (jboolean)sysInfo.isSupportedRefs);

		if (class_jSystemInfo != NULL) env->DeleteLocalRef(class_jSystemInfo);
	}
	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFCheckDirPathValid
(JNIEnv *env, jclass clz, jobject jConn)
{
	NET_CONN_INFO connInfo;
	memset(&connInfo, 0, sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env, &jConn, connInfo);

	//wstring path = JStringToWString(env, connInfo.szDir);
	jlong ret;

	DWORD dwRet = AFCreateConnection(connInfo);
	if (dwRet)
	{
		wprintf(L"get failed[%d]\n", dwRet);
		return (jint)GetLastError();
	}
	else {
		BOOL isExist = CheckFileFolderExist(connInfo.szDir);
		DWORD errorNumber = GetLastError();

		DWORD dwRet = AFCutConnection(connInfo);
		if (dwRet)
		{
			wprintf(L"get failed[%d]\n", dwRet);
		}

		if (isExist)
		{
			return 0;
		}
		else {
			return -1;
		}
	}

	return (jint)GetLastError();
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_PurgeDataStore
(JNIEnv *env, jclass clz, jobject jJobScript)
{
	long ret = ERROR_SUCCESS;

	PAFPURGEDATAJOBSCRIPT pJS = new AFPURGEDATAJOBSCRIPT[1];

	if (pJS == NULL)
	{
		return ERROR_OUTOFMEMORY;
	}
	memset(pJS, 0, sizeof(AFPURGEDATAJOBSCRIPT));

	JPurgeDataJobScript2AFPurgeDataJS(env, jJobScript, pJS);

	wchar_t wszXML[MAX_PATH] = { 0 };
	wchar_t	wszJSName[MAX_PATH] = { 0 };

	do
	{
		GUID	connGUID;
		ret = UuidCreate(&connGUID);
		if (ret != RPC_S_OK)
		{
			break;
		}

		RPC_WSTR	rwstrGUID;
		ret = UuidToStringW(&connGUID, &rwstrGUID);
		if (ret != RPC_S_OK)
		{
			break;
		}


		pJS->pwszMonitorEventName = new wchar_t[wcslen((wchar_t*)rwstrGUID) + 1];
		if (pJS->pwszMonitorEventName == NULL)
		{
			break;
		}

		wcscpy(pJS->pwszMonitorEventName, (wchar_t*)rwstrGUID);

		RpcStringFreeW(&rwstrGUID);

		ret = RPSGetInstallPath(wszXML, _countof(wszXML));
		if (ret != ERROR_SUCCESS)
		{
			break;
		}
		swprintf_s(wszJSName, _countof(wszJSName), TEXT("\\AFPurgeDataJobScript-%08u.xml"), pJS->ulJobNum);
		wcscat_s(wszXML, _countof(wszXML), wszJSName);
		ret = ConvertPurgeDataJS2XML(pJS, wszXML);
		if (ret != ERROR_SUCCESS)
		{
			break;
		}
		ret = TriggerPurgeDataJob(pJS, wszXML);
		if (ret != ERROR_SUCCESS)
		{
			break;
		}
	} while (0);

	if (pJS)
	{
		if (pJS->pAFNodeList)
		{
			delete[] pJS->pAFNodeList;
		}
		if (pJS->pwszDestPath)
		{
			delete[] pJS->pwszDestPath;
		}
		if (pJS->pwszUserName)
		{
			delete[] pJS->pwszUserName;
		}
		if (pJS->pwszPassword)
		{
			delete[] pJS->pwszPassword;
		}
		if (pJS->pwszComments)
		{
			delete[] pJS->pwszComments;
		}
		if (pJS->pRPSName)
		{
			delete[] pJS->pRPSName;
		}
		if (pJS->pwzRPSSvrSID)
		{
			delete[] pJS->pwzRPSSvrSID;
		}
		if (pJS->pPolicyName)
		{
			delete[] pJS->pPolicyName;
		}
		if (pJS->pPolicyGUID)
		{
			delete[] pJS->pPolicyGUID;
		}
		if (pJS->pDataStore)
		{
			delete[] pJS->pDataStore;
		}
		if (pJS->pDataStoreName)
		{
			delete[] pJS->pDataStoreName;
		}
		if (pJS->pwszMonitorEventName)
		{
			delete[] pJS->pwszMonitorEventName;
		}
		delete[] pJS;
		pJS = NULL;
	}

	return ret;
}


JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSValidateSessPasswordByHash
(JNIEnv *env, jclass clz, jstring password, jstring hashvalue)
{
	wchar_t* pPassword = JStringToWCHAR(env, password);
	wchar_t* pHashvalue = JStringToWCHAR(env, hashvalue);
	BOOL isValid = AFValidateSessPasswordByHash(pPassword, wcslen(pPassword), pHashvalue, wcslen(pHashvalue));
	SAFE_FREE(pPassword);
	SAFE_FREE(pHashvalue);

	return (jboolean)isValid;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSEnumBackupDestinations
(JNIEnv *env, jclass clz, jobject jConn, jobject jResult)
{
	NET_CONN_INFO connInfo;
	std::vector<wstring> vecBackupDestinations;

	memset(&connInfo, 0, sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env, &jConn, connInfo);

	DWORD dwResult = RPSAFIEnumBackupDestinations(connInfo, vecBackupDestinations);

	Vector_WSTR2JStringList(env, vecBackupDestinations, jResult);

	return dwResult;
}

unsigned int GetCrc32(char* InStr, unsigned int len)
{
	unsigned int Crc32Table[256];
	int i, j;
	unsigned int Crc;
	for (i = 0; i < 256; i++){
		Crc = i;
		for (j = 0; j < 8; j++){
			if (Crc & 1)
				Crc = (Crc >> 1) ^ 0xEDB88320;
			else
				Crc >>= 1;
		}
		Crc32Table[i] = Crc;
	}


	Crc = 0xffffffff;
	for (int i = 0; i<len; i++){
		Crc = (Crc >> 8) ^ Crc32Table[(Crc ^ InStr[i]) & 0xFF];
	}

	Crc ^= 0xFFFFFFFF;
	return Crc;
}

typedef DWORD(*PFN_GETLASTSESSIONNOTOTAPE)(LPCWSTR lpBasePath, LPCWSTR lpUserName, LPCWSTR lpPassword, DWORD dwLastSesNo[], DWORD dwErrorSesNo[], DWORD dwRetryCount[], DWORD dwMaxRetryCount[], DWORD dwSize);


JNIEXPORT jintArray JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getLastArchiveToTapeSession(JNIEnv *env, jclass jcls, jstring backupDest, jstring userName, jstring passWord, jstring  keyPath)
{
	WCHAR* wcBackupDest = JStringToWCHAR(env, backupDest);
	WCHAR* wcKeyPath = JStringToWCHAR(env, keyPath);
	WCHAR* wcUserName = JStringToWCHAR(env, userName);
	WCHAR* wcPassWord = JStringToWCHAR(env, passWord);
	
	DWORD dwLastSessToTape[4] = { -1, -1, -1, -1 };

	CDbgLog logObj;

	NET_CONN_INFO netConn = { 0 };
	
	HMODULE _hModule = NULL;
	BOOL ret = FALSE;
	_hModule = ::LoadLibrary(L"afstor.dll");

	jintArray lastSesNo = env->NewIntArray(4);

	if (_hModule)
	{
		DWORD dwErrorSesNo[4], dwRetryCount[4], dwMaxRetryCount[4];

		PFN_GETLASTSESSIONNOTOTAPE getLastSessionNoToTape = (PFN_GETLASTSESSIONNOTOTAPE)GetProcAddress(_hModule, "GetLastSessionNoToTape");
		if (getLastSessionNoToTape)
			ret = getLastSessionNoToTape(wcBackupDest, wcUserName, wcPassWord, dwLastSessToTape, dwErrorSesNo, dwRetryCount, dwMaxRetryCount, 4);
		else
			logObj.LogW(LL_ERR, 0, L"Failed to get address of function GetLastSessionNoToTape, error code = %d", GetLastError());

		::FreeLibrary(_hModule);
		if (ret)
		{
			for (int i = 0; i < 4; i++)
			{
				if (dwLastSessToTape[i] < dwErrorSesNo[i] && dwErrorSesNo[i] > 0)
				{
					if (dwRetryCount[i] >= dwMaxRetryCount[i])
					{
						dwLastSessToTape[i] = dwErrorSesNo[i];
					}
					else
					{
						dwLastSessToTape[i] = dwErrorSesNo[i] - 1;
					}
				}
			}


			WCHAR wzEvent[256], Temp[MAX_PATH];

			ULONG ulLen = wcslen(wcKeyPath);

			for (int i = 0; i < ulLen; i++)
			{
				if (wcKeyPath[i] >= L'A' && wcKeyPath[i] <= L'Z')
				{
					Temp[i] = wcKeyPath[i] - L'A' + L'a';
				}
				else
				{
					Temp[i] = wcKeyPath[i];
				}
			}

			Temp[ulLen] = 0;
			if (Temp[ulLen - 1] == L'\\')
			{
				Temp[ulLen - 1] = 0;
				ulLen--;
			}


			unsigned int CRC = GetCrc32((char*)Temp, ulLen * 2);


			for (int nSch = 0; nSch < 4; nSch++)
			{
				for (int i = dwLastSessToTape[nSch]; i < dwLastSessToTape[nSch] + 64; i++)
				{
					swprintf_s(wzEvent, _countof(wzEvent), L"Global\\%x_%010u", CRC, i);

					HANDLE hEvent = OpenEvent(EVENT_ALL_ACCESS, 0, wzEvent);
					if (hEvent != NULL)
					{
						CloseHandle(hEvent);
						
						logObj.LogW(LL_ERR, 0, L"The RPS session %d in path %s is backing up by client agent, ignore the trigger by force.", i, wcBackupDest);

						dwLastSessToTape[nSch] = -2;
					}
				}
			}
		}
		else
		{
			logObj.LogW(LL_ERR, 0, L"Failed to get the last session number to tape");
		}
	}

	env->SetIntArrayRegion(lastSesNo, 0, 4, (const jint*)dwLastSessToTape);	

	return lastSesNo;

}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSIsASBUJobExisted
(JNIEnv *env, jclass clz, jstring strBKDest) {

	wchar_t* pwzBKDest = JStringToWCHAR(env, strBKDest);

	vector<LCK_ERR_INFO> vLockLst;

	RPSAFGetAFLockListByOpType(vLockLst, pwzBKDest, OP_LITE_INTEGRATION);

	SAFE_FREE(pwzBKDest);

	if (vLockLst.empty())
		return false;
	return true;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_RPSAFIsPathUnderDatastore
(JNIEnv *env, jclass clz, jstring strPath) {

	wchar_t *pwzPath = JStringToWCHAR(env, strPath);

	BOOL bResult = RPSAFIsPathUnderDatastore(pwzPath);

	SAFE_FREE(pwzPath);

	return bResult;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetNodesIntegrityInDataStore
(JNIEnv *env, jclass clz, jobject nodelist)
{
	jlong lRetCode = 0;
	jclass jcls_ArrayList = env->GetObjectClass(nodelist);
	//ZZ: Methods in class ArrayList  
	jmethodID jidMethod_Get = env->GetMethodID(jcls_ArrayList, "get", "(I)Ljava/lang/Object;");
	jmethodID jidMethod_Set = env->GetMethodID(jcls_ArrayList, "set", "(ILjava/lang/Object;)Ljava/lang/Object;");
	jmethodID jidMethod_Size = env->GetMethodID(jcls_ArrayList, "size", "()I");
	jint jiNodeListSize = env->CallIntMethod(nodelist, jidMethod_Size);

	if (pLogObj)
		pLogObj->LogW(LL_INF, lRetCode, __WFUNCTION__ L" : Node list size: %u", jiNodeListSize);

	//ZZ: [2014/10/28 14:58] Traverse all nodes regardless previous operation result.
	for (UINT uiIdx = 0; (uiIdx < jiNodeListSize); uiIdx++)
	{
		jobject jobj_NodeInfoWithIntegral = env->CallObjectMethod(nodelist, jidMethod_Get, uiIdx);
		jclass jcls_NodeInfoWithIntegral = env->GetObjectClass(jobj_NodeInfoWithIntegral);

		jfieldID jidField_d2dSid = env->GetFieldID(jcls_NodeInfoWithIntegral, "d2dSid", "Ljava/lang/String;");
		jfieldID jidField_fullBackupDestination = env->GetFieldID(jcls_NodeInfoWithIntegral, "fullBackupDestination", "Ljava/lang/String;");
		jfieldID jidField_isIntegral = env->GetFieldID(jcls_NodeInfoWithIntegral, "isIntegral", "Z");

		WCHAR* pwzD2DSID = GetStringFromField(env, &jobj_NodeInfoWithIntegral, jidField_d2dSid);
		WCHAR* pwzBKDest = GetStringFromField(env, &jobj_NodeInfoWithIntegral, jidField_fullBackupDestination);

		if (pwzBKDest)
		{
			vector<LCK_ERR_INFO> vecLockInfo;
			//
			// first to check if the specified foler exists
			//
			DWORD dwAttri = ::GetFileAttributes(pwzBKDest);
			if ((dwAttri == INVALID_FILE_ATTRIBUTES) || (dwAttri & FILE_ATTRIBUTE_DIRECTORY) == 0)
			{
				lRetCode = GetLastError();
				if (pLogObj)
					pLogObj->LogW(LL_ERR, lRetCode, __WFUNCTION__ L" : Did not find the backup destination. BK=[%s]", pwzBKDest);
				lRetCode = ERROR_PATH_NOT_FOUND;
			}
			else
			{
				lRetCode = RPSGetAFLockListByOpType(vecLockInfo, pwzBKDest, OP_DELETE);
			}
			if ((0 != lRetCode) && (ERROR_PATH_NOT_FOUND != lRetCode) && (ERROR_FILE_NOT_FOUND != lRetCode))
			{
				if (pLogObj)
					pLogObj->LogW(LL_ERR, lRetCode, L"Failed to get delete lock information. Status is not changed by this API. BK=[%s]", pwzBKDest);
			}
			else
			{
				bool bIsIntegrityNode = vecLockInfo.empty();
				if ((ERROR_PATH_NOT_FOUND == lRetCode) || (ERROR_FILE_NOT_FOUND == lRetCode))
				{
					if (pLogObj)
						pLogObj->LogW(LL_WAR, lRetCode, L"Destination %s may be purged or removed. Consider as deleting", pwzBKDest);
					bIsIntegrityNode = false;
					lRetCode = 0;
				}

				env->SetBooleanField(jobj_NodeInfoWithIntegral, jidField_isIntegral, bIsIntegrityNode);
				if (jidMethod_Set)
					env->CallObjectMethod(nodelist, jidMethod_Set, uiIdx, jobj_NodeInfoWithIntegral);
				else
				{
					if (pLogObj)
					{
						pLogObj->LogW(LL_ERR, lRetCode, __WFUNCTION__ L" : unable to find set method of ArrayList");
					}
				}

				if (pLogObj)
				{
					pLogObj->LogW(LL_INF, lRetCode, __WFUNCTION__ L" : [Node %u] %s : %s : %s",
						uiIdx + 1, pwzD2DSID ? pwzD2DSID : L"NoNodeSID", pwzBKDest ? pwzBKDest : L"nul",
						bIsIntegrityNode ? L"Normal" : L"Deleting");
				}
			}
		}
		else
		{
			lRetCode = ERROR_INVALID_PARAMETER;
			if (pLogObj)
			{
				pLogObj->LogW(LL_ERR, lRetCode, __WFUNCTION__ L" : Backup destination is empty. [Node %u] %s : %s",
					uiIdx + 1, pwzD2DSID ? pwzD2DSID : L"NoNodeSID", pwzBKDest ? pwzBKDest : L"nul");
			}
		}

		if (pwzD2DSID)
		{
			free(pwzD2DSID);
			pwzD2DSID = NULL;
		}

		if (pwzBKDest)
		{
			free(pwzBKDest);
			pwzBKDest;
		}
	}

	return lRetCode;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_generateBackupInfoDB
(JNIEnv *env, jclass clz, jstring strBkDest, jstring usrName, jstring usrPwd)
{
	wchar_t* pszBkDest = JStringToWCHAR(env, strBkDest);
	wchar_t* pszUsrPwd = JStringToWCHAR(env, usrPwd);
	wchar_t* pszUsrName = JStringToWCHAR(env, usrName);

	bool bRet = false;
	DWORD dwErrCode = 0;
	NET_CONN_INFO netConn = { 0 };

	if (pszBkDest == NULL)
		goto SAFERET;

	netConn = CreateNetConnInfo(pszBkDest, pszUsrName, pszUsrPwd);
	dwErrCode = AFCreateConnection(netConn);
	if (dwErrCode)
	{
		pLogObj->LogW(LL_ERR, dwErrCode, L"Failed connect to %s [User:%s, Domain:%s, Pwd:****].\n", netConn.szDir, netConn.szUsr, netConn.szDomain);
		bRet = false;
	}
	else
	{
		BOOL bFileExist = FALSE;
		wstring strFile = pszBkDest;
		if (strFile.rfind(L"\\") != strFile.length() - 1)
			strFile += L"\\";
		strFile += AF_BACKUP_INFO_DB_FILE;

		bRet = true;
		if (INVALID_FILE_ATTRIBUTES != GetFileAttributesW(strFile.c_str())){
			pLogObj->LogW(LL_INF, 0, L"BackupInfoDb.xml exist, no need to rebuild. [%s]\n", strFile.c_str());
		}
		else
		{
			dwErrCode = RPSAFBuildBackupInfoDBWrap(pszBkDest, TRUE);
			if (dwErrCode){
				pLogObj->LogW(LL_ERR, dwErrCode, L"Generate BackupInfoDB.xml failed for [%s]\n", strFile.c_str());
				bRet = false;
			}
		}

		AFCutConnection(netConn, FALSE);
	}

SAFERET:
	SAFE_FREE(pszBkDest);
	SAFE_FREE(pszUsrName);
	SAFE_FREE(pszUsrPwd);

	return bRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_getDataSizesFromStorage
(JNIEnv *env, jclass clz, jobject jRetArrLst, jstring strSrc, jstring usrName, jstring usrPwd)
{
	long lRetCode = 0;
	wchar_t* pszSource = JStringToWCHAR(env, strSrc);
	wchar_t* pszUsrPwd = JStringToWCHAR(env, usrPwd);
	wchar_t* pszUsrName = JStringToWCHAR(env, usrName);

	VBACKUP_NODES_SIZE vNodesBkSize;
	NET_CONN_INFO netConn = CreateNetConnInfo(pszSource, pszUsrName, pszUsrPwd);

	lRetCode = RPSAFGetDataSizeFromShareFolder(netConn, vNodesBkSize);
	if (lRetCode)
	{
		pLogObj->LogW(LL_ERR, lRetCode, L"Get data size from share folder [%s] failed. EC:%#08x\n", netConn.szDir, lRetCode);
		lRetCode = -1;	// Error code not specified at now, 
	}
	else
	{
		// Add item to Java array list
		VectorNodeSize2JArrayList(env, jRetArrLst, vNodesBkSize);
	}

	SAFE_FREE(pszSource);
	SAFE_FREE(pszUsrPwd);
	SAFE_FREE(pszUsrName);

	return lRetCode;
}

void LogFile(TCHAR szMsg[])
{
	pLogObj->LogW(LL_DBG, 0, L"%s\n", szMsg);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFGetMaxStorageCapacity
(JNIEnv *env, jclass clz, jobject folderList, jobject storageSize)
{
	DWORD ret = 0;
	std::vector<wstring> vFolders;
	jList2Vector(env, folderList, vFolders);

	BK_STORAGE_MAX_CAP storageCap;
	ret = RPSGetMaxStorageCapacity(vFolders, storageCap);

	jclass dest_class = env->GetObjectClass(storageSize);
	jmethodID midCapacity = env->GetMethodID(dest_class, "setUlCapacitySize", "(J)V");
	env->CallVoidMethod(storageSize, midCapacity, (jlong)storageCap.ullCapacityB);

	jmethodID midUsedSize = env->GetMethodID(dest_class, "setUlUsedSpace", "(J)V");
	env->CallVoidMethod(storageSize, midUsedSize, (jlong)storageCap.ullUsedSizeB);

	return ret;
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_CanArchiveJobBeSubmitted(JNIEnv *env, jclass clz, jobject inout_jArchiveJob)
{
	return FileCopyCommonJNI::AFCanArchiveJobBeSubmitted(env, clz, inout_jArchiveJob);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_CanArchiveSourceDeleteJobBeSubmitted(JNIEnv *env, jclass clz, jobject inout_jArchiveJob)
{
	return FileCopyCommonJNI::AFCanArchiveSourceDeleteJobBeSubmitted(env, clz, inout_jArchiveJob);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFArchive(JNIEnv *env, jclass jclz, jobject in_archiveJobScript)
{
	return FileCopyCommonJNI::AFArchive(env, jclz, in_archiveJobScript);
}

JNIEXPORT jlong JNICALL  Java_com_ca_arcflash_rps_jni_RPSWSJNI_DeleteAllPendingFileCopyJobs(JNIEnv* env, jclass this_class, jstring strDest, jstring strDestDomain, jstring strDestUserName, jstring strDestPassword)
{
	return FileCopyCommonJNI::DeleteAllPendingFileCopyJobs(env, this_class, strDest, strDestDomain, strDestUserName, strDestPassword);
}

JNIEXPORT jlong JNICALL  Java_com_ca_arcflash_rps_jni_RPSWSJNI_DisableFileCopy(JNIEnv* env, jclass this_class, jstring strDest, jstring strDestDomain, jstring strDestUserName, jstring strDestPassword)
{
	return FileCopyCommonJNI::DisableFileCopy(env, this_class, strDest, strDestDomain, strDestUserName, strDestPassword);
}
JNIEXPORT jlong JNICALL  Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFArchivePurge(JNIEnv *env, jclass jclz, jobject in_archiveJobScript)
{
	return FileCopyCommonJNI::AFArchivePurge(env, jclz, in_archiveJobScript);
}
JNIEXPORT jstring JNICALL  Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetFileCopyCatalogPath(JNIEnv *env, jclass, jstring MachineName, jlong ProductType)
{
	return FileCopyCommonJNI::GetFileCopyCatalogPathy(env, 0, MachineName, ProductType);
}
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_IsArchiveJobRunning(JNIEnv *env, jclass clz, jstring d2dMachineName)
{
	return FileCopyCommonJNI::IsFileCopyJobRunning2(env, clz, d2dMachineName);
}
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_IsFileArchiveJobRunning(JNIEnv *env, jclass clz, jstring d2dMachineName)
{
	return FileCopyCommonJNI::IsFileArchiveJobRunning2(env, clz, d2dMachineName);
}
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_IsArchiveRestoreJobRunning(JNIEnv *env, jclass clz, jstring d2dMachineName)
{
	return FileCopyCommonJNI::IsArchiveRestoreJobRunning2(env, clz, d2dMachineName);
}
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_IsArchivePurgeJobRunning(JNIEnv *env, jclass clz, jstring d2dMachineName)
{
	return FileCopyCommonJNI::IsArchivePurgeJobRunning2(env, clz, d2dMachineName);
}
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_IsArchiveCatalogSyncJobRunning(JNIEnv *env, jclass clz, jstring d2dMachineName)
{
	return FileCopyCommonJNI::IsArchiveCatalogSyncJobRunning2(env, clz, d2dMachineName);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetLastArchiveCatalogUpdateTime2(JNIEnv *env, jclass clz, jstring catalogDirBasePath, jobject jDestInfo, jobject out_CatalogDetails)
{
	return FileCopyCommonJNI::GetLastArchiveCatalogUpdateTime2(env, clz, catalogDirBasePath, jDestInfo, out_CatalogDetails);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_searchArchiveCatalogChildren2(JNIEnv *env, jclass clz, jstring catalogDirBasePath, jstring inFileName, jstring inHostName, jstring inSearchpath, jobject inArchiveDestConfig,
	jlong in_lSearchOptions, jlong in_lIndex, jlong in_lCount, jobject archiveCatalogItems)
{
	return FileCopyCommonJNI::searchArchiveCatalogChildren2(env, clz, catalogDirBasePath, inFileName, inHostName, inSearchpath, inArchiveDestConfig, in_lSearchOptions, in_lIndex, in_lCount, archiveCatalogItems);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_ArchiveOpenMachine(JNIEnv *env, jclass clz, jstring catalogDirBasePath, jstring catalogDirUserName, jstring catalogDirPassword, jstring hostName, jobject jDestInfo, jobject pMachineHandle)
{
	return FileCopyCommonJNI::ArchiveOpenMachine(env, clz, catalogDirBasePath, catalogDirUserName, catalogDirPassword, hostName, jDestInfo, pMachineHandle);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetArchiveVolumeList(JNIEnv *env, jclass clz, jlong pMachineHandle, jobject volumeList)
{
	return FileCopyCommonJNI::GetArchiveVolumeList(env, clz, pMachineHandle, volumeList);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_ArchiveOpenVolume(JNIEnv *env, jclass clz, jlong pMachineHandle, jstring strVolume)
{
	return FileCopyCommonJNI::ArchiveOpenVolume(env, clz, pMachineHandle, strVolume);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetArchiveChildItemCount(JNIEnv *env, jclass clz, jlong pVolumeHandle, jstring strPath, jobject childCount)
{
	return FileCopyCommonJNI::GetArchiveChildItemCount(env, clz, pVolumeHandle, strPath, childCount);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetArchiveChildItems(JNIEnv *env, jclass clz, jlong pVolumeHandle, jstring strPath, jobject archiveCatalogItems)
{
	return FileCopyCommonJNI::GetArchiveChildItems(env, clz, pVolumeHandle, strPath, archiveCatalogItems);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetArchiveChildItemsEx(JNIEnv *env, jclass clz, jlong pVolumeHandle, jstring strPath, jlong in_lIndex, jlong in_lCount, jobject archiveCatalogItems)
{
	return  FileCopyCommonJNI::GetArchiveChildItemsEx(env, clz, pVolumeHandle, strPath, in_lIndex, in_lCount, archiveCatalogItems);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_ArchiveCloseVolume(JNIEnv *env, jclass clz, jlong pVolumeHandle)
{
	return FileCopyCommonJNI::ArchiveCloseVolume(env, clz, pVolumeHandle);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_ArchiveCloseMachine(JNIEnv *env, jclass clz, jlong pMachineHandle)
{
	return FileCopyCommonJNI::ArchiveCloseMachine(env, clz, pMachineHandle);
}
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetRPSDatastoreHashKey(JNIEnv *env, jclass clz, jstring bkDestPath, jstring dataStorePwd)
{
	jstring retStr = NULL;
	wchar_t* pwzBKDest = JStringToWCHAR(env, bkDestPath);
	wchar_t* pwzDSwd = JStringToWCHAR(env, dataStorePwd);

	wstring hashKey;
	long retVal = RPSGetDataStoreHashKey(hashKey, pwzBKDest, 0, pwzDSwd);

	SAFE_FREE(pwzBKDest);
	SAFE_FREE(pwzDSwd);

	if (retVal == 0)
		retStr = WCHARToJString(env, hashKey.c_str());
	return retStr;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFGetArchiveJobInfoCount(JNIEnv *env, jclass clz, jobject inout_jArchiveJob, jobject jobCount)
{
	return FileCopyCommonJNI::GetArchiveJobInfoCount(env, clz, inout_jArchiveJob, jobCount);
}

// BEGIN [4/3/2015 zhahu03]
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFSaveAdminAccount(JNIEnv *env, jclass clz, jobject account)
{
	jclass class_account = env->GetObjectClass(account);

	jfieldID username_field = env->GetFieldID(class_account, "userName", "Ljava/lang/String;");
	jfieldID password_field = env->GetFieldID(class_account, "password", "Ljava/lang/String;");

	wchar_t* strUserp = GetStringFromField(env, &account, username_field);
	std::wstring strUse = strUserp;
	wchar_t* strPasswdp = GetStringFromField(env, &account, password_field);
	std::wstring strPasswd = strPasswdp;

	jlong jRet = (jlong)AFSaveAdminAccount(strUse, strPasswd);

	if (strUserp != NULL)
		free(strUserp);
	if (strPasswdp != NULL)
		free(strPasswdp);
	if (class_account != NULL) env->DeleteLocalRef(class_account);

	return jRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_AFReadAdminAccount(JNIEnv *env, jclass clz, jobject account)
{
	jclass class_account = env->GetObjectClass(account);

	jmethodID set_username = env->GetMethodID(class_account, "setUserName", "(Ljava/lang/String;)V");
	jmethodID set_password = env->GetMethodID(class_account, "setPassword", "(Ljava/lang/String;)V");

	std::wstring userName;
	std::wstring password;

	DWORD dwRet = AFReadAdminAccount(userName, password);

	if (dwRet == 0)
	{
		jstring userNameStr = WCHARToJString(env, (wchar_t*)userName.c_str());
		jstring passwordStr = WCHARToJString(env, (wchar_t*)password.c_str());

		env->CallVoidMethod(account, set_username, userNameStr);
		env->CallVoidMethod(account, set_password, passwordStr);
	}
	if (class_account != NULL) env->DeleteLocalRef(class_account);

	return (jlong)dwRet;
}
// END [4/3/2015 zhahu03] 


JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_CheckASBUAgent(JNIEnv *env, jclass clz)
{
	BOOL bInstallAgent = FALSE;
	int nPort = 6050;

	WSADATA wsdat;
	memset(&wsdat, 0, sizeof(wsdat));

	if (WSAStartup(MAKEWORD(2, 2), &wsdat))
		return FALSE;

	struct sockaddr_in sock_info;

	memset(&sock_info, 0, sizeof(sock_info));

	SOCKET s = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (s == INVALID_SOCKET)
		return FALSE;

	sock_info.sin_family = AF_INET;
	sock_info.sin_addr.s_addr = inet_addr("127.0.0.1");
	sock_info.sin_port = htons(nPort);

	int ret = connect(s, (struct sockaddr*)&sock_info, sizeof(struct sockaddr));
	if (ret == SOCKET_ERROR)
	{
		bInstallAgent = FALSE;
	}
	else
	{
		bInstallAgent = TRUE;
	}

	closesocket(s);

	WSACleanup();

	if (bInstallAgent)
	{
		DWORD dwType;
		WCHAR wzValue[128] = L"";
		DWORD cb = _countof(wzValue);

		ret = RegGetValue(HKEY_LOCAL_MACHINE,
			L"SOFTWARE\\ComputerAssociates\\CA ARCServe Backup\\UniversalClientAgent\\Common\\",
			L"Version",
			RRF_RT_REG_SZ,
			&dwType,
			&wzValue,
			&cb
			);

		if (ret == ERROR_SUCCESS)
		{
			if (_wtof(wzValue) < 17.0)
			{
				bInstallAgent = FALSE;

				pLogObj->LogW(LL_ERR, 0, L"%s: client agent version[%s] is not match.\n", __FUNCTIONW__, wzValue);
			}
		}
	}

	return bInstallAgent;
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetSessionOsVer(JNIEnv *env, jclass, jstring backupDest, jobject objosver)
{
	CDbgLog logObj;
	DWORD dwRet = 0;
	FuncLoader loader(&logObj);

	typedef DWORD(*FunDef)(const wchar_t* pszSesspath, BACKUP_ITEM_INFO &infoItem);
	FunDef pGetBackupInfoDBItemInfo = (FunDef)loader.GetFuncPtr(L"DRCore.dll", "GetBackupInfoDBItemInfo");
	if (pGetBackupInfoDBItemInfo == NULL)
	{
		dwRet = GetLastError();
		logObj.LogW(LL_ERR, dwRet, L"Failed to get function pointer.");
		return dwRet;
	}

	BACKUP_INFO_DB infoDb;
	WCHAR* pDest = JStringToWCHAR(env, backupDest);
	if (pDest == NULL) return ERROR_INVALID_PARAMETER;
	logObj.LogW(LL_DET, 0, __FUNCTIONW__ L": Destination => %s", pDest);

	wstring strLatestSess;
	BACKUP_ITEM_INFO latestItem;

	IAFSess* pAFSess = NULL;
	dwRet = AFCreateIAFSess(&logObj, &pAFSess);
	if (dwRet)
	{
		logObj.LogW(LL_ERR, dwRet, L"Failed to create AFSess interface.");
		goto RETPT;
	}

	dwRet = pAFSess->GetLastSessPath(pDest, strLatestSess);
	if (dwRet || strLatestSess.empty())
	{
		logObj.LogW(LL_ERR, dwRet, L"Failed to get latest session path under [%s].", pDest);
		goto RETPT;
	}

	dwRet = pGetBackupInfoDBItemInfo(strLatestSess.c_str(), latestItem);
	if (dwRet)
	{
		logObj.LogW(LL_ERR, dwRet, L"Failed to get backup info db item from [%s]", strLatestSess.c_str());
		goto RETPT;
	}

	NODE_OS_INFO osInfo;
	osInfo.dwAgentBackupType = latestItem.dwAgentBackupType;
	osInfo.dwAgentOSType = latestItem.dwAgentOSType;
	osInfo.dwVMGuestOsType = latestItem.dwVMGuestOsType;
	osInfo.dwVMHypervisor = latestItem.dwVMHypervisor;
	NodeOSInfo2JAgentOSInfo(env, objosver, osInfo);

	/*
	jclass objclass = env->GetObjectClass(objosver);
	jfieldID fld_dwAgentOSType = env->GetFieldID(objclass, "dwAgentOSType", "J");
	jfieldID fld_dwAgentBackupType = env->GetFieldID(objclass, "dwAgentBackupType", "J");
	jfieldID fld_dwVMGuestOsType = env->GetFieldID(objclass, "dwVMGuestOsType", "J");
	jfieldID fld_dwVMHypervisor = env->GetFieldID(objclass, "dwVMHypervisor", "J");

	if (fld_dwAgentOSType)
	env->SetLongField(objosver, fld_dwAgentOSType, latestItem.dwAgentOSType);
	if (fld_dwAgentOSType)
	env->SetLongField(objosver, fld_dwAgentBackupType, latestItem.dwAgentBackupType);
	if (fld_dwAgentOSType)
	env->SetLongField(objosver, fld_dwVMGuestOsType, latestItem.dwVMGuestOsType);
	if (fld_dwAgentOSType)
	env->SetLongField(objosver, fld_dwVMHypervisor, latestItem.dwVMHypervisor);
	*/

	/* Map: C++ value <==> Java vlue

	DWORD dwAgentOSType;		// E_NODE_OS_TYPE: Windows/Linux/Unix/Mac...
	DWORD dwAgentBackupType;	// E_BACKUP_TYPEE: BT_LOCAL_D2D / EBT_HBBU
	DWORD dwVMGuestOsType;		// E_NODE_OS_TYPE, just valid when dwAgentBackupType==EBT_HBBU.
	DWORD dwVMHypervisor;		// HYPERVISOR_TYPE_ESX/HYPERVISOR_TYPE_HYPERV/HYPERVISOR_TYPE_VCLOUD, just valid when dwAgentBackupType==EBT_HBBU.

	enum { HYPERVISOR_TYPE_ESX = 0, HYPERVISOR_TYPE_HYPERV, HYPERVISOR_TYPE_VCLOUD = 3 };

	// Node OS type
	enum E_NODE_OS_TYPE
	{
	ENOT_UNKNOWN = 0,
	ENOT_WINDOWS,
	ENOT_LINUX,
	ENOT_UNIX,
	ENOT_MAC
	};

	// Agent Backup type, LocalD2D or HBBU
	enum E_BACKUP_TYPE
	{
	EBT_UNKNOWN = 0,
	EBT_LOCALD2D_BK,
	EBT_HBBU_BK
	};
	*/

RETPT:
	if (pAFSess)
	{
		AFReleaseIAFSess(pAFSess);
		pAFSess = NULL;
	}

	SAFE_FREE(pDest);
	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_validateEncryptionSettings(JNIEnv *env, jclass clz, jobject in_archiveJobScript, jobject out_jErrorcode, jobject out_jCCIErrorCode)
{
	return FileCopyCommonJNI::validateEncryptionSettings(env, clz, in_archiveJobScript, out_jErrorcode, out_jCCIErrorCode);
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_GetWindowsTempDir(JNIEnv *env, jclass clz)
{
	DWORD dwRet = 0;
	WCHAR szBuf[1024 + 1] = { 0 };
	if (0 == GetTempPath(1024, szBuf))
	{
		dwRet = GetLastError();
		CDbgLog logObj;
		logObj.LogW(LL_ERR, dwRet, L"Failed to get windows temp folder. EC: %#08x", dwRet);
	}

	return WCHARToJString(env, wstring(szBuf));
}