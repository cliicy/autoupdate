#include "stdafx.h"
#include "AFFileCatalog.h"
#include "FileCopyCommonJNI.h"

#ifdef _RPSNF_INCLUDE_
#include "RPSCoreInterface.h"
#endif

extern BOOL g_bImpersonate;

#ifdef _RPSNF_INCLUDE_
	extern CDbgLog* pLogObj;
#else
	extern CDbgLog logObj;
#endif

namespace FileCopyCommonJNI
{
	HMODULE m_hArchiveCatalogHandle = NULL;
	HMODULE m_hArchiveFileStoreHandle = NULL;
	HMODULE m_hArchiveArchiverDLLHandler = NULL;

#define DO_IMPERSONATE(x, y) DWORD dwType = 0; AFGetBakDestDriveType(y, dwType); { x = AFImpersonate();if(0 == x) g_bImpersonate = TRUE;}
#define RETWITHREVERT() if(g_bImpersonate) {AFRevertToSelf(); g_bImpersonate=FALSE;}

#define DO_IMPERSONATE_2(x) BOOL bImpersonate = FALSE; { x = AFImpersonate(); if(0 == x) bImpersonate = TRUE;}
#define RETWITHREVERT_2() if(bImpersonate) {AFRevertToSelf(); bImpersonate=FALSE;}


#define  MAKE_LONGLONG( UULong, LULong)   ((((LONGLONG)(UULong))<<32)|(LULong))

#ifdef _RPSNF_INCLUDE_
#define LogMsg(nLevel, dwError, lpszFormat, ...) if(pLogObj) pLogObj->LogW(nLevel, dwError, lpszFormat, __VA_ARGS__);	
#else
#define LogMsg(nLevel, dwError, lpszFormat, ...) logObj.LogW(nLevel, dwError, lpszFormat, __VA_ARGS__);
#endif

	jlong JNICALL _AFCanArchiveJobBeSubmitted(JNIEnv *env, jclass clz, jobject inout_jArchiveJob, DWORD(WINAPI *PFAFGetNextScheduleArchiveJob2)(const wstring& d2dMachineName, NET_CONN_INFO ConnectionInfo, vector<ARCHIVE_ITEM_INFO>& Jobs, DWORD dwJobType), DWORD tmpJobType)
	{
		LogFile(L"CanArchiveJobBeSubmitted start");
		DWORD dwRet = 0;

		jclass class_JArchiveJob = env->GetObjectClass(inout_jArchiveJob);

		jmethodID midgetbackupDestination = env->GetMethodID(class_JArchiveJob, "getbackupDestination", "()Ljava/lang/String;");
		jstring jBackupDestPath = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetbackupDestination);
		wchar_t * pwszBackupDestinationPath = JStringToWCHAR(env, jBackupDestPath);
		if (jBackupDestPath)
			env->DeleteLocalRef(jBackupDestPath);

		jmethodID midgetbackupDestinationDomain = env->GetMethodID(class_JArchiveJob, "getbackupDestinationDomain", "()Ljava/lang/String;");
		jstring jBackupDestDomain = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetbackupDestinationDomain);
		wchar_t * pwszBackupDestinationDomain = JStringToWCHAR(env, jBackupDestDomain);
		if (jBackupDestDomain)
			env->DeleteLocalRef(jBackupDestDomain);

		jmethodID midgetbackupDestinationUsername = env->GetMethodID(class_JArchiveJob, "getbackupDestinationUsername", "()Ljava/lang/String;");
		jstring jBackupDestUsername = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetbackupDestinationUsername);
		wchar_t * pwszBackupDestinationUser = JStringToWCHAR(env, jBackupDestUsername);
		if (jBackupDestUsername)
			env->DeleteLocalRef(jBackupDestUsername);

		jmethodID midgetbackupDestinationPassword = env->GetMethodID(class_JArchiveJob, "getbackupDestinationPassword", "()Ljava/lang/String;");
		jstring jBackupDestPassword = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetbackupDestinationPassword);
		wchar_t * pwszBackupDestinationPassword = JStringToWCHAR(env, jBackupDestPassword);
		if (jBackupDestPassword)
			env->DeleteLocalRef(jBackupDestPassword);
		jmethodID midgetD2dHostName = env->GetMethodID(class_JArchiveJob, "getD2dHostName", "()Ljava/lang/String;");
		jstring jD2dHostName = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetD2dHostName);
		wchar_t * pwszjD2dHostName = JStringToWCHAR(env, jD2dHostName);
		if (jD2dHostName)
			env->DeleteLocalRef(jD2dHostName);
		wstring wsD2DHostName = pwszjD2dHostName;

		NET_CONN_INFO destInfo;
		memset(&destInfo, 0, sizeof(NET_CONN_INFO));
		_tcscpy(destInfo.szDir, pwszBackupDestinationPath);
		_tcscpy(destInfo.szDomain, pwszBackupDestinationDomain);
		_tcscpy(destInfo.szUsr, pwszBackupDestinationUser);
		_tcscpy(destInfo.szPwd, pwszBackupDestinationPassword);

		vector<ARCHIVE_ITEM_INFO> out_archiveJobs;
		jfieldID field_jobType = env->GetFieldID(class_JArchiveJob, "jobType", "J");
		DWORD JobType = env->GetLongField(inout_jArchiveJob, field_jobType);
		dwRet = (*PFAFGetNextScheduleArchiveJob2)(wsD2DHostName, destInfo, out_archiveJobs, tmpJobType);
		if (!dwRet)
		{
			LogFile(L"AFGetNextScheduleArchiveJob success");
			jmethodID midsetsubmitArchive = env->GetMethodID(class_JArchiveJob, "setsubmitArchive", "(Z)V");
			BOOL bSubmitArchive = FALSE;
			if (out_archiveJobs.size() != 0)
				bSubmitArchive = TRUE;
			else
				bSubmitArchive = FALSE;

			env->CallVoidMethod(inout_jArchiveJob, midsetsubmitArchive, (jboolean)bSubmitArchive);

			if (bSubmitArchive)
			{
				jmethodID midsetbackupSessionPath = env->GetMethodID(class_JArchiveJob, "setbackupSessionPath", "(Ljava/lang/String;)V");
				vector<ARCHIVE_ITEM_INFO>::iterator archiveJobIterator = out_archiveJobs.begin();
				jstring jbackupSessionPath = WCHARToJString(env, (wchar_t*)(archiveJobIterator->strSubPath.c_str()));
				env->CallVoidMethod(inout_jArchiveJob, midsetbackupSessionPath, jbackupSessionPath);
				if (jbackupSessionPath)
					env->DeleteLocalRef(jbackupSessionPath);

				TCHAR szMessage[MAX_PATH * 3] = { _T('\0') };
				_stprintf(szMessage, L"return values from CanArchiveJobBeSubmitted SessionPath %s", (wchar_t*)(archiveJobIterator->strSubPath.c_str()));
				LogFile(szMessage);

				jmethodID midsetbackupSessionId = env->GetMethodID(class_JArchiveJob, "setbackupSessionId", "(Ljava/lang/String;)V");
				jstring jbackupSessionId = WCHARToJString(env, (wchar_t*)(archiveJobIterator->strId.c_str()));
				env->CallVoidMethod(inout_jArchiveJob, midsetbackupSessionId, jbackupSessionId);
				if (jbackupSessionId)
					env->DeleteLocalRef(jbackupSessionId);

				_stprintf(szMessage, L"return values from CanArchiveJobBeSubmitted SessionNum: %s", (wchar_t*)(archiveJobIterator->strId.c_str()));
				LogFile(szMessage);

				jmethodID midsetbackupSessionGUID = env->GetMethodID(class_JArchiveJob, "setBackupSessionGUID", "(Ljava/lang/String;)V");
				if (midsetbackupSessionGUID != nullptr)
				{
					jstring jbackupSessionGUID = WCHARToJString(env, (wchar_t*)(archiveJobIterator->strBackupSessGUID.c_str()));
					env->CallVoidMethod(inout_jArchiveJob, midsetbackupSessionGUID, jbackupSessionGUID);
					if (midsetbackupSessionGUID)
						env->DeleteLocalRef(jbackupSessionGUID);

					_stprintf(szMessage, L"return values from CanArchiveJobBeSubmitted SessionGUID: %s", (wchar_t*)(archiveJobIterator->strBackupSessGUID.c_str()));
					LogFile(szMessage);
				}
			}

			if (class_JArchiveJob != NULL) env->DeleteLocalRef(class_JArchiveJob);
		}
		else
		{
			TCHAR szMessage[260] = { _T('\0') };
			_stprintf(szMessage, L"AFGetArchiveJobByScheduleStatus failed with error: dwRet %d", dwRet);
			LogFile(szMessage);
		}

		if (pwszBackupDestinationPath != NULL)
			free(pwszBackupDestinationPath);

		if (pwszBackupDestinationDomain != NULL)
			free(pwszBackupDestinationDomain);

		if (pwszBackupDestinationUser != NULL)
			free(pwszBackupDestinationUser);

		if (pwszBackupDestinationPassword != NULL)
			free(pwszBackupDestinationPassword);
		if (pwszjD2dHostName)
			free(pwszjD2dHostName);

		LogFile(L"CanArchiveJobBeSubmitted End");
		return (jlong)dwRet;
	}
	jlong JNICALL AFCanArchiveJobBeSubmitted(JNIEnv *env, jclass clz, jobject inout_jArchiveJob)
	{
		return _AFCanArchiveJobBeSubmitted(env, clz, inout_jArchiveJob, AFGetNextScheduleArchiveJob2, AF_JOBTYPE_FILECOPY_BACKUP);
	}
	jlong JNICALL AFCanArchiveSourceDeleteJobBeSubmitted(JNIEnv *env, jclass clz, jobject inout_jArchiveJob)
	{
		return _AFCanArchiveJobBeSubmitted(env, clz, inout_jArchiveJob, AFGetNextFCSourceDeleteJob2, AF_JOBTYPE_FILECOPY_SOURCEDELETE);
	}

	jlong AFArchive(JNIEnv *env, jclass jclz, jobject in_archiveJobScript)
	{
		LogFile(L"AFArchive Start");
		DWORD dwRet = 0;
		AFARCHIVEJOBSCRIPT afArchiveJS;
		memset(&afArchiveJS, 0, sizeof(AFARCHIVEJOBSCRIPT));
		ArchiveJobScript2AFJOBSCRIPT(env, &afArchiveJS, &in_archiveJobScript);
#ifdef _RPSNF_INCLUDE_
		afArchiveJS.dwproductType = PRODUCT_TYPE::PRODUCT_RPS;
		dwRet = RPS_AFArchiveJob(&afArchiveJS, NULL, NULL);
#else
		afArchiveJS.dwproductType = PRODUCT_TYPE::PRODUCT_D2D;
		if (afArchiveJS.usJobType == AF_JOBTYPE_ARCHIVE)
			dwRet = AFArchive(&afArchiveJS, NULL, NULL);
		else
			dwRet = AFFileCopyBackup(&afArchiveJS, NULL, NULL);
#endif
		TCHAR szErrMsg[MAX_PATH] = { _T('\0') };
		_stprintf(szErrMsg, L"AFArchive returned dwRet [%d]", dwRet);
		LogFile(szErrMsg);
		FreeArchiveJobScript(&afArchiveJS);
		LogFile(L"AFArchive End");
		return dwRet;
	}

	jlong DeleteAllPendingFileCopyJobs(JNIEnv* env, jclass this_class, jstring strDest, jstring strDestDomain, jstring strDestUserName, jstring strDestPassword)
	{
		wchar_t* pDest = JStringToWCHAR(env, strDest);
		wchar_t* pDomain = JStringToWCHAR(env, strDestDomain);
		wchar_t* pUser = JStringToWCHAR(env, strDestUserName);
		wchar_t* pPwd = JStringToWCHAR(env, strDestPassword);

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

		DWORD dwError = AFClearPendingArchiveJobs(info, AF_JOBTYPE_FILECOPY_BACKUP);
		return dwError;
	}

	jlong DisableFileCopy(JNIEnv* env, jclass this_class, jstring strDest, jstring strDestDomain, jstring strDestUserName, jstring strDestPassword)
	{
		wchar_t* pDest = JStringToWCHAR(env, strDest);
		wchar_t* pDomain = JStringToWCHAR(env, strDestDomain);
		wchar_t* pUser = JStringToWCHAR(env, strDestUserName);
		wchar_t* pPwd = JStringToWCHAR(env, strDestPassword);

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

		DWORD dwError = AFDisableFilecopy(info);
		return dwError;
	}
	/*ARCHIVE */
	// nodeList: List<ArchiveSourceFilter>
	DWORD processJArchiveSourceFilter(JNIEnv *env, PARCHIVE_FILTER_FILEFOLDER& pFilters, jobject nodeList)
	{
		jint nFilterCnt = 0;
		DWORD dwValidCnt = 0;
		try
		{
			if (nodeList != NULL)
			{
				jclass class_List = env->GetObjectClass(nodeList);
				jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");
				nFilterCnt = env->CallIntMethod(nodeList, method_List_size);

				jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");

				if (nFilterCnt > 0)
				{
					size_t stLen = sizeof(ARCHIVE_FILTER_FILEFOLDER)* nFilterCnt;
					PARCHIVE_FILTER_FILEFOLDER pFilterItem = (PARCHIVE_FILTER_FILEFOLDER)malloc(stLen);

					memset(pFilterItem, 0, stLen);
					pFilters = pFilterItem;

					jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/data/archive/ArchiveSourceFilter");
					jfieldID field_pszFilterDisplayName = env->GetFieldID(class_Entry, "pszFilterDisplayName", "Ljava/lang/String;");
					jfieldID field_pszFilterName = env->GetFieldID(class_Entry, "pszFilterName", "Ljava/lang/String;");
					jfieldID field_nInclExcl = env->GetFieldID(class_Entry, "nInclExcl", "I");

					for (int i = 0; i < nFilterCnt; i++, pFilterItem++)
					{
						jobject entryObject = env->CallObjectMethod(nodeList, id_List_get, i);
						if (entryObject == NULL) continue;

						pFilterItem->pszFilterDisplayName = GetStringFromField(env, &entryObject, field_pszFilterDisplayName);
						pFilterItem->pszFilterDisplayNameSize = (wcslen(pFilterItem->pszFilterDisplayName) + 1) *sizeof(wchar_t);

						pFilterItem->pszFilterName = GetStringFromField(env, &entryObject, field_pszFilterName);
						pFilterItem->pszFilterNameSize = (wcslen(pFilterItem->pszFilterName) + 1) *sizeof(wchar_t);

						pFilterItem->nInclExcl = env->GetIntField(entryObject, field_nInclExcl);
						dwValidCnt++;

						if (entryObject != NULL) env->DeleteLocalRef(entryObject);
					}

					if (class_Entry != NULL) env->DeleteLocalRef(class_Entry);
				}

				if (nodeList != NULL) env->DeleteLocalRef(nodeList);
			}
			else
			{
				pFilters = NULL;
				nFilterCnt = 0;
			}
		}
		catch (...)
		{
			LogFile(L"Exception occurred in " __FUNCTIONW__);
			return 0;
		}
		return dwValidCnt;
	}
	// nodeList: List<ArchiveSizeFilter>
	DWORD processJArchiveSizeFilter(JNIEnv *env, PARCHIVE_ASFILTER_SIZE& pFilters, jobject nodeList)
	{
		jint nFilterCnt = 0;
		DWORD dwValidCnt = 0;

		try
		{
			if (nodeList != NULL)
			{
				jclass class_List = env->GetObjectClass(nodeList);
				jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");
				nFilterCnt = env->CallIntMethod(nodeList, method_List_size);

				jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");

				if (nFilterCnt > 0)
				{
					size_t stLen = sizeof(ARCHIVE_ASFILTER_SIZE)* nFilterCnt;
					PARCHIVE_ASFILTER_SIZE pFilterItem = (PARCHIVE_ASFILTER_SIZE)malloc(stLen);

					memset(pFilterItem, 0, stLen);
					pFilters = pFilterItem;

					jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/data/archive/ArchiveSizeFilter");
					jfieldID field_nInclExcl = env->GetFieldID(class_Entry, "nInclExcl", "I");
					jfieldID field_nCompareType = env->GetFieldID(class_Entry, "nCompareType", "I");
					jfieldID field_FileSize1 = env->GetFieldID(class_Entry, "FileSize1", "J");
					jfieldID field_FileSize2 = env->GetFieldID(class_Entry, "FileSize2", "J");

					for (int i = 0; i < nFilterCnt; i++, pFilterItem++)
					{
						jobject entryObject = env->CallObjectMethod(nodeList, id_List_get, i);
						if (entryObject == NULL) continue;

						pFilterItem->nInclExcl = env->GetIntField(entryObject, field_nInclExcl);
						pFilterItem->nCompareType = env->GetIntField(entryObject, field_nCompareType);
						pFilterItem->FileSize1 = env->GetLongField(entryObject, field_FileSize1);
						pFilterItem->FileSize2 = env->GetLongField(entryObject, field_FileSize2);

						dwValidCnt++;

						if (entryObject != NULL) env->DeleteLocalRef(entryObject);
					}

					if (class_Entry != NULL) env->DeleteLocalRef(class_Entry);
				}

				if (nodeList != NULL) env->DeleteLocalRef(nodeList);
			}
			else
			{
				pFilters = NULL;
				nFilterCnt = 0;
			}
		}
		catch (...)
		{
			LogFile(L"Exception occurred in " __FUNCTIONW__);
			return 0;
		}

		return dwValidCnt;
	}
	// nodeList: List<ArchiveTimeRangeFilter>
	DWORD processJArchiveTimeRangeFilter(JNIEnv *env, PARCHIVE_ASFILTER_TIME& pFilters, jobject nodeList)
	{
		jint nFilterCnt = 0;
		DWORD dwValidCnt = 0;

		try
		{
			if (nodeList != NULL)
			{
				jclass class_List = env->GetObjectClass(nodeList);
				jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");
				nFilterCnt = env->CallIntMethod(nodeList, method_List_size);

				jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");

				if (nFilterCnt > 0)
				{
					size_t stLen = sizeof(ARCHIVE_ASFILTER_TIME)* nFilterCnt;
					PARCHIVE_ASFILTER_TIME pFilterItem = (PARCHIVE_ASFILTER_TIME)malloc(stLen);

					memset(pFilterItem, 0, stLen);
					pFilters = pFilterItem;

					jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/data/archive/ArchiveTimeRangeFilter");

					jfieldID field_nInclExcl = env->GetFieldID(class_Entry, "nInclExcl", "I");
					jfieldID field_nDateType = env->GetFieldID(class_Entry, "nDateType", "I");
					jfieldID field_nCompareType = env->GetFieldID(class_Entry, "nCompareType", "I");
					jfieldID field_nTimeUnit = env->GetFieldID(class_Entry, "nTimeUnit", "I");

					jfieldID field_ulStartTime = env->GetFieldID(class_Entry, "ulStartTime", "J");
					jfieldID field_ulEndTime = env->GetFieldID(class_Entry, "ulEndTime", "J");
					jfieldID field_llTimeElapsed = env->GetFieldID(class_Entry, "llTimeElapsed", "J");

					for (int i = 0; i < nFilterCnt; i++, pFilterItem++)
					{
						jobject entryObject = env->CallObjectMethod(nodeList, id_List_get, i);

						pFilterItem->nInclExcl = env->GetIntField(entryObject, field_nInclExcl);
						pFilterItem->nDateType = env->GetIntField(entryObject, field_nDateType);
						pFilterItem->nCompareType = env->GetIntField(entryObject, field_nCompareType);
						pFilterItem->nTimeUnit = env->GetIntField(class_Entry, field_nTimeUnit);

						pFilterItem->ulStartTime = env->GetLongField(entryObject, field_ulStartTime);
						pFilterItem->ulEndTime = env->GetLongField(entryObject, field_ulEndTime);
						pFilterItem->llTimeElapsed = env->GetLongField(entryObject, field_llTimeElapsed);
						dwValidCnt++;

						if (entryObject != NULL) env->DeleteLocalRef(entryObject);
					}

					if (class_Entry != NULL) env->DeleteLocalRef(class_Entry);
				}

				if (nodeList != NULL) env->DeleteLocalRef(nodeList);
			}
			else
			{
				pFilters = NULL;
				nFilterCnt = 0;
			}
		}
		catch (...)
		{
			LogFile(L"Exception occurred in " __FUNCTIONW__);
			return 0;
		}

		return dwValidCnt;
	}
	// nodeList : ArchiveJobScript::List<ArchivePolicy>
	void processJArchivePolicy(JNIEnv *env, PAFARCHIVEJOBSCRIPT pafJS, jobject nodeList)
	{
		try
		{
			if (nodeList != NULL)
			{
				jclass class_List = env->GetObjectClass(nodeList);
				jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");
				jint archivePolicyCnt = env->CallIntMethod(nodeList, method_List_size);
				jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");

				if (archivePolicyCnt > 0)
				{
					size_t stLen = sizeof(AFARCHIVEPOLICY)* archivePolicyCnt;
					PAFARCHIVEPOLICY pArcPolicy = (PAFARCHIVEPOLICY)malloc(stLen);

					memset(pArcPolicy, 0, stLen);
					pafJS->pArchivePolicy = pArcPolicy;
					pafJS->nArchivePolicyCount = archivePolicyCnt;

					/*
					jfieldID field_pArchivePolicy = env->GetFieldID(class_JJobScript, "pArchivePolicy", "Ljava/util/List;");
					processJArchivePolicy(env, pafJS, env->GetObjectField(*jJobScript, field_pArchivePolicy));
					*/

					jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/data/archive/ArchivePolicy");
					jfieldID field_szSourceFolder = env->GetFieldID(class_Entry, "szSourceFolder", "Ljava/lang/String;");

					jfieldID field_pFileFilters = env->GetFieldID(class_Entry, "pFileFilters", "Ljava/util/List;");
					jfieldID field_pFolderFilters = env->GetFieldID(class_Entry, "pFolderFilters", "Ljava/util/List;");
					jfieldID field_pFileSizeFilters = env->GetFieldID(class_Entry, "pFileSizeFilters", "Ljava/util/List;");
					jfieldID field_pFileTimeFilters = env->GetFieldID(class_Entry, "pFileTimeFilters", "Ljava/util/List;");

					for (jint i = 0; i < archivePolicyCnt; i++, pArcPolicy++)
					{
						jobject entryObject = env->CallObjectMethod(nodeList, id_List_get, i);

						//szSourceFolder
						pArcPolicy->szSourceFolder = GetStringFromField(env, &entryObject, field_szSourceFolder);

						pArcPolicy->nFileFilters = processJArchiveSourceFilter(env, pArcPolicy->pFileFilters, env->GetObjectField(entryObject, field_pFileFilters));
						pArcPolicy->nFolderFilters = processJArchiveSourceFilter(env, pArcPolicy->pFolderFilters, env->GetObjectField(entryObject, field_pFolderFilters));
						pArcPolicy->nFileSizeFilters = processJArchiveSizeFilter(env, pArcPolicy->pFileSizeFilters, env->GetObjectField(entryObject, field_pFileSizeFilters));
						pArcPolicy->nFileTimeFilters = processJArchiveTimeRangeFilter(env, pArcPolicy->pFileTimeFilters, env->GetObjectField(entryObject, field_pFileTimeFilters));

						if (entryObject != NULL) env->DeleteLocalRef(entryObject);
					}

					if (class_Entry != NULL) env->DeleteLocalRef(class_Entry);
				}

				if (class_List != NULL) env->DeleteLocalRef(class_List);
			}
			else
			{
				pafJS->pArchivePolicy = NULL;
			}
		}
		catch (...)
		{
			LogFile(L"Exception occurred in " __FUNCTIONW__);
		}
	}
	// nodeList : ArchiveJobScript::List<FileCopyPolicy> pFileCopyPolicy
	void processJFileCopyPolicy(JNIEnv *env, PAFARCHIVEJOBSCRIPT pafJS, jobject nodeList)
	{
		try
		{
			if (nodeList != NULL)
			{
				jclass class_List = env->GetObjectClass(nodeList);
				jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");
				jint fcPolicyCnt = env->CallIntMethod(nodeList, method_List_size);
				jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");

				if (fcPolicyCnt > 0)
				{
					DWORD dwValidCnt = 0;
					size_t stLen = sizeof(AFFILECOPYPOLICY)* fcPolicyCnt;
					PAFFILECOPYPOLICY pFCPolicy = (PAFFILECOPYPOLICY)malloc(stLen);

					memset(pFCPolicy, 0, stLen);
					pafJS->pFileCopyPolicy = pFCPolicy;
					pafJS->nFileCopyPolicyCount = fcPolicyCnt;

					jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/data/archive/FileCopyPolicy");
					jfieldID field_szSourceFolder = env->GetFieldID(class_Entry, "szSourceFolder", "Ljava/lang/String;");

					jfieldID field_pFileFilters = env->GetFieldID(class_Entry, "pFileFilters", "Ljava/util/List;");
					jfieldID field_pFolderFilters = env->GetFieldID(class_Entry, "pFolderFilters", "Ljava/util/List;");
					jfieldID field_pFileSizeFilters = env->GetFieldID(class_Entry, "pFileSizeFilters", "Ljava/util/List;");

					for (jint i = 0; i < fcPolicyCnt; i++, pFCPolicy++)
					{
						jobject entryObject = env->CallObjectMethod(nodeList, id_List_get, i);
						if (entryObject == NULL) continue;

						//szSourceFolder
						pFCPolicy->szSourceFolder = GetStringFromField(env, &entryObject, field_szSourceFolder);

						pFCPolicy->nFileFilters = processJArchiveSourceFilter(env, pFCPolicy->pFileFilters, env->GetObjectField(entryObject, field_pFileFilters));
						pFCPolicy->nFolderFilters = processJArchiveSourceFilter(env, pFCPolicy->pFolderFilters, env->GetObjectField(entryObject, field_pFolderFilters));
						pFCPolicy->nFileSizeFilters = processJArchiveSizeFilter(env, pFCPolicy->pFileSizeFilters, env->GetObjectField(entryObject, field_pFileSizeFilters));

						dwValidCnt++;

						if (entryObject != NULL) env->DeleteLocalRef(entryObject);
					}

					pafJS->nFileCopyPolicyCount = dwValidCnt;
					if (class_Entry != NULL) env->DeleteLocalRef(class_Entry);
				}

				if (class_List != NULL) env->DeleteLocalRef(class_List);
			}
			else
			{
				pafJS->pArchivePolicy = NULL;
			}
		}
		catch (...)
		{
			LogFile(L"Exception occurred in " __FUNCTIONW__);
		}
	}

	void processJArchiveNodeList(JNIEnv *env, PAFARCHIVEJOBSCRIPT pafJS, jobject nodeList)
	{
		try
		{
			if (nodeList != NULL)
			{
				jclass class_List = env->GetObjectClass(nodeList);
				jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");
				jint volCnt = env->CallIntMethod(nodeList, method_List_size);

				if (volCnt > 0)
				{
					size_t stLen = sizeof(AFARCHIVENODE)* volCnt;
					pafJS->pAFNodeList = (PAFARCHIVENODE)malloc(stLen);
					memset(pafJS->pAFNodeList, 0, stLen);
					PAFARCHIVENODE pafresVol = pafJS->pAFNodeList;

					jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/data/archive/JobScriptArchiveNode");
					jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
					jfieldID field_pwszNodeName = env->GetFieldID(class_Entry, "pwszNodeName", "Ljava/lang/String;");
					jfieldID field_pwszNodeAddr = env->GetFieldID(class_Entry, "pwszNodeAddr", "Ljava/lang/String;");
					jfieldID field_pwszUserName = env->GetFieldID(class_Entry, "pwszUserName", "Ljava/lang/String;");
					jfieldID field_pwszUserPW = env->GetFieldID(class_Entry, "pwszUserPW", "Ljava/lang/String;");
					jfieldID field_pwszSessPath = env->GetFieldID(class_Entry, "pwszSessPath", "Ljava/lang/String;");
					jfieldID field_ulSessNum = env->GetFieldID(class_Entry, "ulSessNum", "I");
					jfieldID field_nVolumeApp = env->GetFieldID(class_Entry, "nVolumeApp", "I");
					jfieldID field_pArchiveVolumeList = env->GetFieldID(class_Entry, "pArchiveVolumeList", "Ljava/util/List;");
					jfieldID field_pRestoreArchiveVolumeList = env->GetFieldID(class_Entry, "pRestoreArchiveVolumesList", "Ljava/util/List;");
					jfieldID field_nFilterItems = env->GetFieldID(class_Entry, "nFilterItems", "I");
					jfieldID field_fOptions = env->GetFieldID(class_Entry, "fOptions", "I");

					for (jint i = 0; i < volCnt; i++, pafresVol++)
					{
						jobject entryObject = env->CallObjectMethod(nodeList, id_List_get, i);
						pafresVol->pwszNodeName = GetStringFromField(env, &entryObject, field_pwszNodeName);
						pafresVol->pwszNodeAddr = GetStringFromField(env, &entryObject, field_pwszNodeAddr);
						pafresVol->pwszUserName = GetStringFromField(env, &entryObject, field_pwszUserName);
						pafresVol->pwszUserPW = GetStringFromField(env, &entryObject, field_pwszUserPW);
						pafresVol->pwszSessPath = GetStringFromField(env, &entryObject, field_pwszSessPath);
						pafresVol->ulSessNum = env->GetIntField(entryObject, field_ulSessNum);
						pafresVol->nVolumeApp = env->GetIntField(entryObject, field_nVolumeApp);
						pafresVol->nFilterItems = env->GetIntField(entryObject, field_nFilterItems);
						pafresVol->fOptions = env->GetIntField(entryObject, field_fOptions);
						processJArchiveSource(env, pafresVol, env->GetObjectField(entryObject, field_pArchiveVolumeList));
						processJRestoreArchiveVolumeList(env, pafresVol, env->GetObjectField(entryObject, field_pRestoreArchiveVolumeList));
						if (entryObject != NULL) env->DeleteLocalRef(entryObject);
					}

					if (class_Entry != NULL) env->DeleteLocalRef(class_Entry);
				}

				if (class_List != NULL) env->DeleteLocalRef(class_List);
			}
			else
			{
				pafJS->pAFNodeList = NULL;
			}
		}
		catch (...)
		{
			LogFile(L"Exception occurred in processJArchiveNodeList");
		}
	}
	void processJRestoreArchiveVolumeList(JNIEnv *env, PAFARCHIVENODE afNode, jobject volList)
	{
		try
		{
			if (volList != NULL)
			{
				jclass class_List = env->GetObjectClass(volList);
				jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");
				jint volCnt = env->CallIntMethod(volList, method_List_size);

				if (volCnt > 0)
				{
					size_t stLen = sizeof(AFARCHIVERESTVOLAPP)* volCnt;
					afNode->pRestoreVolumeAppList = (PAFARCHIVERESTVOLAPP)malloc(stLen);
					memset(afNode->pRestoreVolumeAppList, 0, stLen);
					PAFARCHIVERESTVOLAPP pafresVol = afNode->pRestoreVolumeAppList;

					jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/data/archive/JobScriptArchiveRestoreVolApp");
					jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
					jfieldID field_fOption = env->GetFieldID(class_Entry, "fOption", "J");
					jfieldID field_onConflictMethod = env->GetFieldID(class_Entry, "onConflictMethod", "J");
					jfieldID field_fileSystem = env->GetFieldID(class_Entry, "fileSystem", "J");
					jfieldID field_subSessionNum = env->GetFieldID(class_Entry, "subSessionNum", "J");
					jfieldID field_path = env->GetFieldID(class_Entry, "path", "Ljava/lang/String;");
					jfieldID field_destVolumName = env->GetFieldID(class_Entry, "destVolumName", "Ljava/lang/String;");
					jfieldID field_destItemCount = env->GetFieldID(class_Entry, "destItemCount", "J");
					jfieldID field_destItemList = env->GetFieldID(class_Entry, "destItemList", "Ljava/util/List;");
					jfieldID field_volItemAppComp = env->GetFieldID(class_Entry, "volItemAppComp", "J");
					jfieldID field_volItemAppCompList = env->GetFieldID(class_Entry, "volItemAppCompList", "Ljava/util/List;");

					jfieldID field_filterCount = env->GetFieldID(class_Entry, "filterCount", "J");
					jfieldID field_filterList = env->GetFieldID(class_Entry, "filterList", "Ljava/util/List;");

					for (jint i = 0; i < volCnt; i++, pafresVol++)
					{
						jobject entryObject = env->CallObjectMethod(volList, id_List_get, i);
						pafresVol->fOptions = (ULONG)env->GetLongField(entryObject, field_fOption);
						pafresVol->OnConflictMethod = (ULONG)env->GetLongField(entryObject, field_onConflictMethod);
						pafresVol->ulFileSystem = (ULONG)env->GetLongField(entryObject, field_fileSystem);
						pafresVol->ulSubSessNum = (ULONG)env->GetLongField(entryObject, field_subSessionNum);
						pafresVol->pwszPath = GetStringFromField(env, &entryObject, field_path);
						pafresVol->pDestVolumeName = GetStringFromField(env, &entryObject, field_destVolumName);
						pafresVol->nDestItemCount = (ULONG)env->GetLongField(entryObject, field_destItemCount);
						//processJDestItemList(env,pafresVol, env->GetObjectField(entryObject,field_destItemList),0);// not required for archive restore
						pafresVol->nVolItemAppComp = (ULONG)env->GetLongField(entryObject, field_volItemAppComp);
						processJArchiveRestoreVolumeItems(env, pafresVol, env->GetObjectField(entryObject, field_volItemAppCompList), 1);
						pafresVol->nFilterItems = (ULONG)env->GetLongField(entryObject, field_filterCount);
						//processJArchiveRestoreVolumeFilters(env,pafresVol, env->GetObjectField(entryObject,field_filterList),1);
					}

					if (class_Entry != NULL) env->DeleteLocalRef(class_Entry);
				}

				if (class_List != NULL) env->DeleteLocalRef(class_List);
			}
			else
			{
				afNode->pRestoreVolumeAppList = NULL;
			}
		}
		catch (...)
		{
			LogFile(L"Exception caught in processJRestoreArchiveVolumeList");
		}
		printf("reach end of processJRestoreArchiveVolumeList");
	}
	void processJArchiveRestoreVolumeItems(JNIEnv *env, PAFARCHIVERESTVOLAPP afarchiveVolume, jobject itemList, int type)
	{
		if (itemList != NULL)
		{
			jclass class_List = env->GetObjectClass(itemList);
			jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");
			jint itemsCount = env->CallIntMethod(itemList, method_List_size);

			if (itemsCount > 0)
			{
				size_t stLen = sizeof(AFARCHIVEVOLITEMAPPCOMP)* itemsCount; //size_t stLen = sizeof(PAFARCHIVEVOLITEMAPPCOMP) * itemsCount;
				PAFARCHIVEVOLITEMAPPCOMP pafVolItemAppComp = NULL;
				if (type == 0)
				{
					afarchiveVolume->pDestItemList = (PAFARCHIVEVOLITEMAPPCOMP)malloc(stLen);
					memset(afarchiveVolume->pDestItemList, 0, stLen);
					pafVolItemAppComp = afarchiveVolume->pDestItemList;
				}
				else
				{
					afarchiveVolume->pVolItemAppCompList = (PAFARCHIVEVOLITEMAPPCOMP)malloc(stLen);
					memset(afarchiveVolume->pVolItemAppCompList, 0, stLen);
					pafVolItemAppComp = afarchiveVolume->pVolItemAppCompList;
				}

				jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/data/archive/ArchiveVolItemAppComp");
				jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
				jfieldID field_fileorDirFullPath = env->GetFieldID(class_Entry, "fileorDirFullPath", "Ljava/lang/String;");
				jfieldID field_fOptions = env->GetFieldID(class_Entry, "fOptions", "I");
				jfieldID field_fileVersion = env->GetFieldID(class_Entry, "fileVersion", "I");

				for (jint i = 0; i < itemsCount; i++, pafVolItemAppComp++)
				{
					jobject entryObject = (jobject)env->CallObjectMethod(itemList, id_List_get, i);
					pafVolItemAppComp->pwszFileorDir = GetStringFromField(env, &entryObject, field_fileorDirFullPath);
					pafVolItemAppComp->fOptions = (ULONG)env->GetLongField(entryObject, field_fOptions);
					pafVolItemAppComp->ulFileVersion = (ULONG)env->GetLongField(entryObject, field_fileVersion);
				}

				if (class_Entry != NULL) env->DeleteLocalRef(class_Entry);
			}

			if (class_List != NULL) env->DeleteLocalRef(class_List);
		}
		else
		{
			if (type == 1)
				afarchiveVolume->pVolItemAppCompList = NULL;
			else
				afarchiveVolume->pDestItemList = NULL;
		}
	}
	void processJArchiveSource(JNIEnv *env, PAFARCHIVENODE afArchiveNode, jobject nodeList)
	{
		try
		{
			if (nodeList != NULL)
			{
				jclass class_List = env->GetObjectClass(nodeList);
				jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");
				jint volCnt = env->CallIntMethod(nodeList, method_List_size);

				if (volCnt > 0)
				{
					size_t stLen = sizeof(AFARCHIVESOURCE)* volCnt;//size_t stLen = sizeof(PAFARCHIVESOURCE) * volCnt;
					afArchiveNode->pArchiveVolumeAppList = (PAFARCHIVESOURCE)malloc(stLen);
					memset(afArchiveNode->pArchiveVolumeAppList, 0, stLen);
					PAFARCHIVESOURCE pafresVol = afArchiveNode->pArchiveVolumeAppList;

					jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/data/archive/ArchiveSource");
					jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
					jfieldID field_VolName = env->GetFieldID(class_Entry, "volName", "Ljava/lang/String;");
					jfieldID field_VolItemAppComp = env->GetFieldID(class_Entry, "VolItemAppComp", "J");
					jfieldID field_fOptions = env->GetFieldID(class_Entry, "fOptions", "J");
					//jfieldID field_pBackupVolumeList = env->GetFieldID(class_Entry, "pBackupVolumeList", "Ljava/util/List;");

					for (jint i = 0; i < volCnt; i++, pafresVol++)
					{
						jobject entryObject = env->CallObjectMethod(nodeList, id_List_get, i);
						pafresVol->pwszVolName = GetStringFromField(env, &entryObject, field_VolName);
						pafresVol->fOptions = (ULONG)env->GetLongField(entryObject, field_fOptions);
						pafresVol->nVolItemAppComp = (ULONG)env->GetLongField(entryObject, field_VolItemAppComp);
						//processJBackupVolumeList(env,pafresVol->pVolItemAppCompList, env->GetObjectField(entryObject,field_pBackupVolumeList));
					}

					if (class_Entry != NULL) env->DeleteLocalRef(class_Entry);
				}

				if (class_List != NULL) env->DeleteLocalRef(class_List);
			}
			else
			{
				afArchiveNode->pArchiveVolumeAppList = NULL;
			}
		}
		catch (...)
		{
			LogFile(L"Exception occurred in processJArchiveSource");
		}

		printf("reach processJArchiveSource \n");
	}
	void ArchiveJobScript2AFJOBSCRIPT(JNIEnv *env, PAFARCHIVEJOBSCRIPT pafJS, jobject* jJobScript)
	{
		jclass class_JJobScript = env->GetObjectClass(*jJobScript);

		jfieldID field_ulVersion = env->GetFieldID(class_JJobScript, "ulVersion", "I");
		pafJS->ulVersion = env->GetIntField(*jJobScript, field_ulVersion);

		jfieldID field_ulShrMemID = env->GetFieldID(class_JJobScript, "ulShrMemID", "J");
		pafJS->ulShrMemID = env->GetLongField(*jJobScript, field_ulShrMemID);

		jfieldID field_usJobType = env->GetFieldID(class_JJobScript, "usJobType", "I");
		pafJS->usJobType = env->GetIntField(*jJobScript, field_usJobType);

		jfieldID field_nNodeItems = env->GetFieldID(class_JJobScript, "nNodeItems", "I");
		pafJS->nNodeItems = env->GetIntField(*jJobScript, field_nNodeItems);

		jfieldID field_backupDestinationPath = env->GetFieldID(class_JJobScript, "backupDestinationPath", "Ljava/lang/String;");
		pafJS->pwszBackupSessionPath = GetStringFromField(env, jJobScript, field_backupDestinationPath);

		jfieldID field_backupUserName = env->GetFieldID(class_JJobScript, "backupUserName", "Ljava/lang/String;");
		pafJS->pwszBackupDestUserName = GetStringFromField(env, jJobScript, field_backupUserName);

		jfieldID field_backupPassword = env->GetFieldID(class_JJobScript, "backupPassword", "Ljava/lang/String;");
		pafJS->pwszBackupDestPassword = GetStringFromField(env, jJobScript, field_backupPassword);

		jfieldID field_dwArchiveDestType = env->GetFieldID(class_JJobScript, "dwArchiveDestType", "J");
		pafJS->dwDestType = env->GetLongField(*jJobScript, field_dwArchiveDestType);

		jclass class_ArchiveDiskDestInfo = env->FindClass("com/ca/arcflash/webservice/data/archive/ArchiveDiskDestInfo");
		if (pafJS->dwDestType == 4)//disk
		{
			size_t stDiskInfoLen = sizeof(ARCHIVEDISKDESTINFO)* 1;
			pafJS->pDiskDest = (PARCHIVEDISKDESTINFO)malloc(stDiskInfoLen);
			memset(pafJS->pDiskDest, 0, stDiskInfoLen);
			PARCHIVEDISKDESTINFO pDiskInfo = pafJS->pDiskDest;

			jfieldID field_diskDestInfo = env->GetFieldID(class_JJobScript, "diskDestInfo", "Lcom/ca/arcflash/webservice/data/archive/ArchiveDiskDestInfo;");
			jobject jdiskDestConfig = env->GetObjectField(*jJobScript, field_diskDestInfo);

			jmethodID midgetArchiveDiskDestPath = env->GetMethodID(class_ArchiveDiskDestInfo, "getArchiveDiskDestPath", "()Ljava/lang/String;");
			jstring jarchiveDest = (jstring)env->CallObjectMethod(jdiskDestConfig, midgetArchiveDiskDestPath);
			pDiskInfo->pwszDestpath = JStringToWCHAR(env, jarchiveDest);
			if (jarchiveDest)
				env->DeleteLocalRef(jarchiveDest);

			jmethodID midgetArchiveDiskUserName = env->GetMethodID(class_ArchiveDiskDestInfo, "getArchiveDiskUserName", "()Ljava/lang/String;");
			jstring jarchiveDestUserName = (jstring)env->CallObjectMethod(jdiskDestConfig, midgetArchiveDiskUserName);
			pDiskInfo->pwszUserName = JStringToWCHAR(env, jarchiveDestUserName);
			if (jarchiveDestUserName)
				env->DeleteLocalRef(jarchiveDestUserName);

			jmethodID midgetArchiveDiskPassword = env->GetMethodID(class_ArchiveDiskDestInfo, "getArchiveDiskPassword", "()Ljava/lang/String;");
			jstring jarchiveDestPassword = (jstring)env->CallObjectMethod(jdiskDestConfig, midgetArchiveDiskPassword);
			pDiskInfo->pwszPassword = JStringToWCHAR(env, jarchiveDestPassword);
			if (jarchiveDestPassword)
				env->DeleteLocalRef(jarchiveDestPassword);
		}
		else
		{
			pafJS->pDiskDest = NULL;
		}
		//

		jclass class_ArchiveCloudDestInfo = env->FindClass("com/ca/arcflash/webservice/data/archive/ArchiveCloudDestInfo");
		if (pafJS->dwDestType != 4)//Not Disk, Any cloud vendor
		{
			//archive cloud info
			size_t stCloudInfoLen = sizeof(ARCHIVECLOUDDESTINFO)* 1;
			pafJS->pCloudDest = (PARCHIVECLOUDDESTINFO)malloc(stCloudInfoLen);
			memset(pafJS->pCloudDest, 0, stCloudInfoLen);
			PARCHIVECLOUDDESTINFO pCloudInfo = pafJS->pCloudDest;

			jfieldID field_cloudDestInfo = env->GetFieldID(class_JJobScript, "cloudDestInfo", "Lcom/ca/arcflash/webservice/data/archive/ArchiveCloudDestInfo;");
			jobject jCloudDestConfig = env->GetObjectField(*jJobScript, field_cloudDestInfo);

			jmethodID midgetcloudVendorType = env->GetMethodID(class_ArchiveCloudDestInfo, "getcloudVendorType", "()J");
			pCloudInfo->dwCloudVendorType = (jlong)env->CallLongMethod(jCloudDestConfig, midgetcloudVendorType);

			jmethodID midgetSubVendorType = env->GetMethodID(class_ArchiveCloudDestInfo, "getCloudSubVendorType", "()J");
			pCloudInfo->dwCloudVendorSubType = (jlong)env->CallObjectMethod(jCloudDestConfig, midgetSubVendorType);

			jmethodID midgetcloudVendorURL = env->GetMethodID(class_ArchiveCloudDestInfo, "getcloudVendorURL", "()Ljava/lang/String;");
			jstring jCloudVendorURL = (jstring)env->CallObjectMethod(jCloudDestConfig, midgetcloudVendorURL);
			pCloudInfo->pwszCloudVendorURL = JStringToWCHAR(env, jCloudVendorURL);
			if (jCloudVendorURL)
				env->DeleteLocalRef(jCloudVendorURL);

			jmethodID midgetcloudDisplayBucketName = env->GetMethodID(class_ArchiveCloudDestInfo, "getcloudBucketName", "()Ljava/lang/String;");
			jstring jCloudDisplayBucketName = (jstring)env->CallObjectMethod(jCloudDestConfig, midgetcloudDisplayBucketName);
			pCloudInfo->pwszCloudDisplayBucketName = JStringToWCHAR(env, jCloudDisplayBucketName);
			if (jCloudDisplayBucketName)
				env->DeleteLocalRef(jCloudDisplayBucketName);

			jmethodID midgetCloudBucketName = env->GetMethodID(class_ArchiveCloudDestInfo, "getEncodedCloudBucketName", "()Ljava/lang/String;");
			jstring jCloudBucketName = (jstring)env->CallObjectMethod(jCloudDestConfig, midgetCloudBucketName);
			pCloudInfo->pwszCloudBucketName = JStringToWCHAR(env, jCloudBucketName);
			if (jCloudBucketName)
				env->DeleteLocalRef(jCloudBucketName);

			jmethodID midgetcloudBucketRegion = env->GetMethodID(class_ArchiveCloudDestInfo, "getcloudBucketRegionName", "()Ljava/lang/String;");
			jstring jCloudBucketRegion = (jstring)env->CallObjectMethod(jCloudDestConfig, midgetcloudBucketRegion);
			pCloudInfo->pwszCloudBucketRegionName = JStringToWCHAR(env, jCloudBucketRegion);
			if (jCloudBucketRegion)
				env->DeleteLocalRef(jCloudBucketRegion);

			jmethodID midgetRrrsFlag = env->GetMethodID(class_ArchiveCloudDestInfo, "getRRSFlag", "()J");
			pCloudInfo->dwDestinationProperties = (jlong)env->CallLongMethod(jCloudDestConfig, midgetRrrsFlag);

			jmethodID midgetcloudVendorUserName = env->GetMethodID(class_ArchiveCloudDestInfo, "getcloudVendorUserName", "()Ljava/lang/String;");
			jstring jCloudVendorUserName = (jstring)env->CallObjectMethod(jCloudDestConfig, midgetcloudVendorUserName);
			pCloudInfo->pwszVendorUsername = JStringToWCHAR(env, jCloudVendorUserName);
			if (jCloudVendorUserName)
				env->DeleteLocalRef(jCloudVendorUserName);

			jmethodID midgetcloudVendorPassword = env->GetMethodID(class_ArchiveCloudDestInfo, "getcloudVendorPassword", "()Ljava/lang/String;");
			jstring jCloudVendorPassword = (jstring)env->CallObjectMethod(jCloudDestConfig, midgetcloudVendorPassword);
			pCloudInfo->pwszVendorPassword = JStringToWCHAR(env, jCloudVendorPassword);
			if (jCloudVendorPassword)
				env->DeleteLocalRef(jCloudVendorPassword);

			/*jmethodID midgetcloudCertificatePath = env->GetMethodID(class_ArchiveCloudDestInfo,"getcloudCertificatePath","()Ljava/lang/String;");
			jstring jCloudVendorCertPath = (jstring)env->CallObjectMethod(jCloudDestConfig,midgetcloudCertificatePath);
			pCloudInfo->pwszVendorCertificatePath = JStringToWCHAR(env, jCloudVendorCertPath);*/

			/*jmethodID midgetcloudCertificatePassword = env->GetMethodID(class_ArchiveCloudDestInfo,"getcloudCertificatePassword","()Ljava/lang/String;");
			jstring jCloudVendorCertPassword = (jstring)env->CallObjectMethod(jCloudDestConfig,midgetcloudCertificatePassword);
			pCloudInfo->pwszCertificatePassword = JStringToWCHAR(env, jCloudVendorCertPassword);*/

			//jmethodID midgetcloudVendorHostName = env->GetMethodID(class_ArchiveCloudDestInfo,"getcloudVendorHostName","()Ljava/lang/String;");
			//jstring jCloudVendorHostName = (jstring)env->CallObjectMethod(jCloudDestConfig,midgetcloudVendorHostName);
			//pCloudInfo->pwszVendorHostname = JStringToWCHAR(env, jCloudVendorHostName);

			jmethodID midgetcloudVendorPort = env->GetMethodID(class_ArchiveCloudDestInfo, "getcloudVendorPort", "()J");
			pCloudInfo->dwVendorPort = (jlong)env->CallLongMethod(jCloudDestConfig, midgetcloudVendorPort);

			jmethodID midgetcloudUseProxy = env->GetMethodID(class_ArchiveCloudDestInfo, "iscloudUseProxy", "()Z");
			pCloudInfo->bUseProxy = (jboolean)env->CallBooleanMethod(jCloudDestConfig, midgetcloudUseProxy);

			jmethodID midgetcloudProxyServerName = env->GetMethodID(class_ArchiveCloudDestInfo, "getcloudProxyServerName", "()Ljava/lang/String;");
			jstring jCloudProxyServerName = (jstring)env->CallObjectMethod(jCloudDestConfig, midgetcloudProxyServerName);
			pCloudInfo->pwszProxyServerName = JStringToWCHAR(env, jCloudProxyServerName);
			if (jCloudProxyServerName)
				env->DeleteLocalRef(jCloudProxyServerName);

			jmethodID midgetcloudProxyPort = env->GetMethodID(class_ArchiveCloudDestInfo, "getcloudProxyPort", "()J");
			pCloudInfo->dwProxyServerPort = (jlong)env->CallLongMethod(jCloudDestConfig, midgetcloudProxyPort);

			jmethodID midgetcloudProxyRequireAuth = env->GetMethodID(class_ArchiveCloudDestInfo, "iscloudProxyRequireAuth", "()Z");
			pCloudInfo->bproxyRequiresAuth = (jboolean)env->CallBooleanMethod(jCloudDestConfig, midgetcloudProxyRequireAuth);

			jmethodID midgetcloudProxyUserName = env->GetMethodID(class_ArchiveCloudDestInfo, "getcloudProxyUserName", "()Ljava/lang/String;");
			jstring jCloudProxyUserName = (jstring)env->CallObjectMethod(jCloudDestConfig, midgetcloudProxyUserName);
			pCloudInfo->pwszproxyUserName = JStringToWCHAR(env, jCloudProxyUserName);
			if (jCloudProxyUserName)
				env->DeleteLocalRef(jCloudProxyUserName);

			jmethodID midgetcloudProxyPassword = env->GetMethodID(class_ArchiveCloudDestInfo, "getcloudProxyPassword", "()Ljava/lang/String;");
			jstring jCloudProxyPassword = (jstring)env->CallObjectMethod(jCloudDestConfig, midgetcloudProxyPassword);
			pCloudInfo->pwszProxyPassword = JStringToWCHAR(env, jCloudProxyPassword);
			if (jCloudProxyPassword)
				env->DeleteLocalRef(jCloudProxyPassword);
		}
		else
		{
			pafJS->pCloudDest = NULL;
		}

		jfieldID field_lProductType = env->GetFieldID(class_JJobScript, "productType", "J");
		pafJS->dwproductType = env->GetLongField(*jJobScript, field_lProductType);

		//Session Password
		jfieldID field_BackupEncryptionPassword = env->GetFieldID(class_JJobScript, "pwszBackupEncrptionPassword", "Ljava/lang/String;");
		pafJS->pwszBackupEncrptionPassword = GetStringFromField(env, jJobScript, field_BackupEncryptionPassword);

		//encryption password
		jfieldID field_EncryptionPassword = env->GetFieldID(class_JJobScript, "EncryptionPassword", "Ljava/lang/String;");
		pafJS->pwszEncrptionPassword = GetStringFromField(env, jJobScript, field_EncryptionPassword);

		jfieldID field_lEncryption = env->GetFieldID(class_JJobScript, "lEncryption", "J");
		pafJS->dwEncryptionEnabled = env->GetLongField(*jJobScript, field_lEncryption);

		jfieldID field_pwszComments = env->GetFieldID(class_JJobScript, "pwszComments", "Ljava/lang/String;");
		pafJS->pwszComments = GetStringFromField(env, jJobScript, field_pwszComments);
		jfieldID field_pwszBeforeJob = env->GetFieldID(class_JJobScript, "pwszBeforeJob", "Ljava/lang/String;");
		pafJS->pwszBeforeJob = GetStringFromField(env, jJobScript, field_pwszBeforeJob);
		jfieldID field_pwszAfterJob = env->GetFieldID(class_JJobScript, "pwszAfterJob", "Ljava/lang/String;");
		pafJS->pwszAfterJob = GetStringFromField(env, jJobScript, field_pwszAfterJob);
		jfieldID field_pwszPrePostUser = env->GetFieldID(class_JJobScript, "pwszPrePostUser", "Ljava/lang/String;");
		pafJS->pwszPrePostUser = GetStringFromField(env, jJobScript, field_pwszPrePostUser);
		jfieldID field_pwszPrePostPassword = env->GetFieldID(class_JJobScript, "pwszPrePostPassword", "Ljava/lang/String;");
		pafJS->pwszPrePostPassword = GetStringFromField(env, jJobScript, field_pwszPrePostPassword);
		jfieldID field_usPreExitCode = env->GetFieldID(class_JJobScript, "usPreExitCode", "I");
		pafJS->usPreExitCode = env->GetIntField(*jJobScript, field_usPreExitCode);
		jfieldID field_usJobMethod = env->GetFieldID(class_JJobScript, "usJobMethod", "I");
		pafJS->usJobMethod = env->GetIntField(*jJobScript, field_usJobMethod);
		jfieldID field_usRestPoint = env->GetFieldID(class_JJobScript, "usRestPoint", "I");
		pafJS->usRestPoint = env->GetIntField(*jJobScript, field_usRestPoint);
		jfieldID field_fOptions = env->GetFieldID(class_JJobScript, "fOptions", "I");
		pafJS->fOptions = env->GetIntField(*jJobScript, field_fOptions);
		jfieldID field_dwCompressionLevel = env->GetFieldID(class_JJobScript, "dwCompressionLevel", "J");
		pafJS->dwCompressionLevel = env->GetLongField(*jJobScript, field_dwCompressionLevel);
		jfieldID field_dwJobHistoryDays = env->GetFieldID(class_JJobScript, "dwJobHistoryDays", "J");
		pafJS->dwJobHistoryDays = env->GetLongField(*jJobScript, field_dwJobHistoryDays);
		jfieldID field_purgeFileBeforeLowDate = env->GetFieldID(class_JJobScript, "purgeFileBeforeLowDate", "J");
		pafJS->ftPurgeFileBeforeThisDate = env->GetLongField(*jJobScript, field_purgeFileBeforeLowDate);
		jfieldID field_purgeFileBeforeHighDate = env->GetFieldID(class_JJobScript, "purgeFileBeforeHighDate", "J");

		jfieldID field_pAFNodeList = env->GetFieldID(class_JJobScript, "pAFNodeList", "Ljava/util/List;");
		processJArchiveNodeList(env, pafJS, env->GetObjectField(*jJobScript, field_pAFNodeList));

		/*File Copy*/
		{
			jfieldID field_ftFileMergeDate = env->GetFieldID(class_JJobScript, "ftFileMergeDate", "J");
			pafJS->ftFileMergeDate = env->GetLongField(*jJobScript, field_ftFileMergeDate);

			jfieldID field_pwszCatalogDirPath = env->GetFieldID(class_JJobScript, "pwszCatalogDirPath", "Ljava/lang/String;");
			pafJS->pwszCatalogDirPath = GetStringFromField(env, jJobScript, field_pwszCatalogDirPath);

			jfieldID field_pwszCatalogDirUserName = env->GetFieldID(class_JJobScript, "pwszCatalogDirUserName", "Ljava/lang/String;");
			pafJS->pwszCatalogDirUserName = GetStringFromField(env, jJobScript, field_pwszCatalogDirUserName);

			jfieldID field_pwszCatalogDirPassword = env->GetFieldID(class_JJobScript, "pwszCatalogDirPassword", "Ljava/lang/String;");
			pafJS->pwszCatalogDirPassword = GetStringFromField(env, jJobScript, field_pwszCatalogDirPassword);

			jfieldID field_BackupDestType = env->GetFieldID(class_JJobScript, "backupDestType", "I");
			pafJS->dwDatastoreType = env->GetIntField(*jJobScript, field_BackupDestType);

			jfieldID field_pArchivePolicy = env->GetFieldID(class_JJobScript, "pArchivePolicy", "Ljava/util/List;");
			processJArchivePolicy(env, pafJS, env->GetObjectField(*jJobScript, field_pArchivePolicy));

			jfieldID field_pFileCopyPolicy = env->GetFieldID(class_JJobScript, "pFileCopyPolicy", "Ljava/util/List;");
			processJFileCopyPolicy(env, pafJS, env->GetObjectField(*jJobScript, field_pFileCopyPolicy));

			jfieldID field_rpsServerName = env->GetFieldID(class_JJobScript, "rpsServerName", "Ljava/lang/String;");
			if (field_rpsServerName) pafJS->rpsServerName = GetStringFromField(env, jJobScript, field_rpsServerName);

			jfieldID field_rpsDatastoreName = env->GetFieldID(class_JJobScript, "rpsDatastoreName", "Ljava/lang/String;");
			if (field_rpsDatastoreName) pafJS->rpsDatastoreName = GetStringFromField(env, jJobScript, field_rpsDatastoreName);
		}

		if (class_JJobScript != NULL) env->DeleteLocalRef(class_JJobScript);
		if (class_ArchiveDiskDestInfo != NULL) env->DeleteLocalRef(class_ArchiveDiskDestInfo);
		if (class_ArchiveCloudDestInfo != NULL) env->DeleteLocalRef(class_ArchiveCloudDestInfo);
	}

	int JArchiveDestConfig2ArchiveDiskDestInfo(JNIEnv *env, PARCHIVEDISKDESTINFO pArchiveDiskInfo, jobject* jDestInfo)
	{
		jclass class_JDestInfo = env->GetObjectClass(*jDestInfo);

		jfieldID field_strArchiveToDrivePath = env->GetFieldID(class_JDestInfo, "strArchiveToDrivePath", "Ljava/lang/String;");
		jfieldID field_StrArchiveDestinationUserName = env->GetFieldID(class_JDestInfo, "StrArchiveDestinationUserName", "Ljava/lang/String;");
		jfieldID field_StrArchiveDestinationPassword = env->GetFieldID(class_JDestInfo, "StrArchiveDestinationPassword", "Ljava/lang/String;");

		pArchiveDiskInfo->pwszDestpath = GetStringFromField(env, jDestInfo, field_strArchiveToDrivePath);
		pArchiveDiskInfo->pwszUserName = GetStringFromField(env, jDestInfo, field_StrArchiveDestinationUserName);
		pArchiveDiskInfo->pwszPassword = GetStringFromField(env, jDestInfo, field_StrArchiveDestinationPassword);

		if (class_JDestInfo != NULL) env->DeleteLocalRef(class_JDestInfo);

		return 1;
	}
	int JArchiveDestConfig2ArchiveCloudDestInfo(JNIEnv *env, PARCHIVECLOUDDESTINFO parchiveCloudInfo, jobject* jDestInfo)
	{
		jclass class_JDestInfo = env->GetObjectClass(*jDestInfo);

		jclass class_JCloudConfig = env->FindClass("com/ca/arcflash/webservice/data/archive/ArchiveCloudDestInfo");

		jfieldID field_JCloudConfig = env->GetFieldID(class_JDestInfo, "CloudConfig", "Lcom/ca/arcflash/webservice/data/archive/ArchiveCloudDestInfo;");

		jobject jCloudConfig = env->GetObjectField(*jDestInfo, field_JCloudConfig);

		jmethodID midgetVendorType = env->GetMethodID(class_JCloudConfig, "getcloudVendorType", "()J");
		parchiveCloudInfo->dwCloudVendorType = (jlong)env->CallObjectMethod(jCloudConfig, midgetVendorType);

		jmethodID midgetSubVendorType = env->GetMethodID(class_JCloudConfig, "getCloudSubVendorType", "()J");
		parchiveCloudInfo->dwCloudVendorSubType = (jlong)env->CallObjectMethod(jCloudConfig, midgetSubVendorType);

		jmethodID midgetcloudVendorURL = env->GetMethodID(class_JCloudConfig, "getcloudVendorURL", "()Ljava/lang/String;");
		jstring jCloudVendorURL = (jstring)env->CallObjectMethod(jCloudConfig, midgetcloudVendorURL);
		parchiveCloudInfo->pwszCloudVendorURL = JStringToWCHAR(env, jCloudVendorURL);
		if (jCloudVendorURL)
			env->DeleteLocalRef(jCloudVendorURL);

		jmethodID midgetcloudDisplayBucketName = env->GetMethodID(class_JCloudConfig, "getcloudBucketName", "()Ljava/lang/String;");
		jstring jCloudDisplayBucketName = (jstring)env->CallObjectMethod(jCloudConfig, midgetcloudDisplayBucketName);
		parchiveCloudInfo->pwszCloudDisplayBucketName = JStringToWCHAR(env, jCloudDisplayBucketName);
		if (jCloudDisplayBucketName)
			env->DeleteLocalRef(jCloudDisplayBucketName);

		jmethodID midgetCloudBucketName = env->GetMethodID(class_JCloudConfig, "getEncodedCloudBucketName", "()Ljava/lang/String;");
		jstring jCloudBucketName = (jstring)env->CallObjectMethod(jCloudConfig, midgetCloudBucketName);
		parchiveCloudInfo->pwszCloudBucketName = JStringToWCHAR(env, jCloudBucketName);
		if (jCloudBucketName)
			env->DeleteLocalRef(jCloudBucketName);

		jmethodID midgetcloudUsername = env->GetMethodID(class_JCloudConfig, "getcloudVendorUserName", "()Ljava/lang/String;");
		jstring jCloudUsername = (jstring)env->CallObjectMethod(jCloudConfig, midgetcloudUsername);
		parchiveCloudInfo->pwszVendorUsername = JStringToWCHAR(env, jCloudUsername);
		if (jCloudUsername)
			env->DeleteLocalRef(jCloudUsername);

		jmethodID midgetcloudPassword = env->GetMethodID(class_JCloudConfig, "getcloudVendorPassword", "()Ljava/lang/String;");
		jstring jCloudPassword = (jstring)env->CallObjectMethod(jCloudConfig, midgetcloudPassword);
		parchiveCloudInfo->pwszVendorPassword = JStringToWCHAR(env, jCloudPassword);
		if (jCloudPassword)
			env->DeleteLocalRef(jCloudPassword);

		jmethodID midgetcloudVendorPort = env->GetMethodID(class_JCloudConfig, "getcloudVendorPort", "()J");
		parchiveCloudInfo->dwVendorPort = (jlong)env->CallObjectMethod(jCloudConfig, midgetcloudVendorPort);

		jmethodID midiscloudUseProxy = env->GetMethodID(class_JCloudConfig, "iscloudUseProxy", "()Z");
		parchiveCloudInfo->bUseProxy = (jlong)env->CallBooleanMethod(jCloudConfig, midiscloudUseProxy);

		if (parchiveCloudInfo->bUseProxy)
		{
			jmethodID midgetcloudProxyServerName = env->GetMethodID(class_JCloudConfig, "getcloudProxyServerName", "()Ljava/lang/String;");
			jstring jCloudProxyServerName = (jstring)env->CallObjectMethod(jCloudConfig, midgetcloudProxyServerName);
			parchiveCloudInfo->pwszProxyServerName = JStringToWCHAR(env, jCloudProxyServerName);
			if (jCloudProxyServerName)
				env->DeleteLocalRef(jCloudProxyServerName);

			jmethodID midgetcloudProxyPort = env->GetMethodID(class_JCloudConfig, "getcloudProxyPort", "()J");
			parchiveCloudInfo->dwProxyServerPort = (jlong)env->CallObjectMethod(jCloudConfig, midgetcloudProxyPort);

			jmethodID midiscloudProxyRequireAuth = env->GetMethodID(class_JCloudConfig, "iscloudProxyRequireAuth", "()Z");
			parchiveCloudInfo->bproxyRequiresAuth = env->CallBooleanMethod(jCloudConfig, midiscloudProxyRequireAuth);

			jmethodID midgetcloudProxyUserName = env->GetMethodID(class_JCloudConfig, "getcloudProxyUserName", "()Ljava/lang/String;");
			jstring jCloudProxyUsername = (jstring)env->CallObjectMethod(jCloudConfig, midgetcloudProxyUserName);
			parchiveCloudInfo->pwszproxyUserName = JStringToWCHAR(env, jCloudProxyUsername);
			if (jCloudProxyUsername)
				env->DeleteLocalRef(jCloudProxyUsername);

			jmethodID midgetcloudProxyPassword = env->GetMethodID(class_JCloudConfig, "getcloudProxyPassword", "()Ljava/lang/String;");
			jstring jCloudProxyPassword = (jstring)env->CallObjectMethod(jCloudConfig, midgetcloudProxyPassword);
			parchiveCloudInfo->pwszProxyPassword = JStringToWCHAR(env, jCloudProxyPassword);
			if (jCloudProxyPassword)
				env->DeleteLocalRef(jCloudProxyPassword);
		}

		if (class_JDestInfo != NULL) env->DeleteLocalRef(class_JDestInfo);
		if (class_JCloudConfig != NULL) env->DeleteLocalRef(class_JCloudConfig);

		return 1;
	}

	int JArchiveCloudConfig2ArchiveCloudDestInfo(JNIEnv *env, PARCHIVECLOUDDESTINFO parchiveCloudInfo, jobject* jCloudConfig)
	{
		jclass class_JCloudConfig = env->GetObjectClass(*jCloudConfig);

		//jclass class_JCloudConfig = env->FindClass("com/ca/arcflash/webservice/data/archive/ArchiveCloudDestInfo");

		//jfieldID field_JCloudConfig = env->GetFieldID(class_JDestInfo, "CloudConfig", "Lcom/ca/arcflash/webservice/data/archive/ArchiveCloudDestInfo;");

		//jobject jCloudConfig = env->GetObjectField(*jDestInfo,field_JCloudConfig);

		jmethodID midgetVendorType = env->GetMethodID(class_JCloudConfig, "getcloudVendorType", "()J");
		parchiveCloudInfo->dwCloudVendorType = (jlong)env->CallObjectMethod(*jCloudConfig, midgetVendorType);

		jmethodID midgetcloudVendorURL = env->GetMethodID(class_JCloudConfig, "getcloudVendorURL", "()Ljava/lang/String;");
		jstring jCloudVendorURL = (jstring)env->CallObjectMethod(*jCloudConfig, midgetcloudVendorURL);
		parchiveCloudInfo->pwszCloudVendorURL = JStringToWCHAR(env, jCloudVendorURL);
		if (jCloudVendorURL)
			env->DeleteLocalRef(jCloudVendorURL);

		jmethodID midgetcloudDisplayBucketName = env->GetMethodID(class_JCloudConfig, "getcloudBucketName", "()Ljava/lang/String;");
		jstring jCloudDisplayBucketName = (jstring)env->CallObjectMethod(*jCloudConfig, midgetcloudDisplayBucketName);
		parchiveCloudInfo->pwszCloudDisplayBucketName = JStringToWCHAR(env, jCloudDisplayBucketName);
		if (jCloudDisplayBucketName)
			env->DeleteLocalRef(jCloudDisplayBucketName);

		jmethodID midgetCloudBucketName = env->GetMethodID(class_JCloudConfig, "getEncodedCloudBucketName", "()Ljava/lang/String;");
		jstring jCloudBucketName = (jstring)env->CallObjectMethod(*jCloudConfig, midgetCloudBucketName);
		parchiveCloudInfo->pwszCloudBucketName = JStringToWCHAR(env, jCloudBucketName);
		if (jCloudBucketName)
			env->DeleteLocalRef(jCloudBucketName);

		jmethodID midgetcloudBucketRegion = env->GetMethodID(class_JCloudConfig, "getcloudBucketRegionName", "()Ljava/lang/String;");
		jstring jCloudBucketRegion = (jstring)env->CallObjectMethod(*jCloudConfig, midgetcloudBucketRegion);
		parchiveCloudInfo->pwszCloudBucketRegionName = JStringToWCHAR(env, jCloudBucketRegion);
		if (jCloudBucketRegion)
			env->DeleteLocalRef(jCloudBucketRegion);

		jmethodID midgetcloudUsername = env->GetMethodID(class_JCloudConfig, "getcloudVendorUserName", "()Ljava/lang/String;");
		jstring jCloudUsername = (jstring)env->CallObjectMethod(*jCloudConfig, midgetcloudUsername);
		parchiveCloudInfo->pwszVendorUsername = JStringToWCHAR(env, jCloudUsername);
		if (jCloudUsername)
			env->DeleteLocalRef(jCloudUsername);

		jmethodID midgetcloudPassword = env->GetMethodID(class_JCloudConfig, "getcloudVendorPassword", "()Ljava/lang/String;");
		jstring jCloudPassword = (jstring)env->CallObjectMethod(*jCloudConfig, midgetcloudPassword);
		parchiveCloudInfo->pwszVendorPassword = JStringToWCHAR(env, jCloudPassword);
		if (jCloudPassword)
			env->DeleteLocalRef(jCloudPassword);

		jmethodID midgetcloudVendorPort = env->GetMethodID(class_JCloudConfig, "getcloudVendorPort", "()J");
		parchiveCloudInfo->dwVendorPort = (jlong)env->CallObjectMethod(*jCloudConfig, midgetcloudVendorPort);

		jmethodID midiscloudUseProxy = env->GetMethodID(class_JCloudConfig, "iscloudUseProxy", "()Z");
		parchiveCloudInfo->bUseProxy = (jlong)env->CallBooleanMethod(*jCloudConfig, midiscloudUseProxy);

		if (parchiveCloudInfo->bUseProxy)
		{
			jmethodID midgetcloudProxyServerName = env->GetMethodID(class_JCloudConfig, "getcloudProxyServerName", "()Ljava/lang/String;");
			jstring jCloudProxyServerName = (jstring)env->CallObjectMethod(*jCloudConfig, midgetcloudProxyServerName);
			parchiveCloudInfo->pwszProxyServerName = JStringToWCHAR(env, jCloudProxyServerName);
			if (jCloudProxyServerName)
				env->DeleteLocalRef(jCloudProxyServerName);

			jmethodID midgetcloudProxyPort = env->GetMethodID(class_JCloudConfig, "getcloudProxyPort", "()J");
			parchiveCloudInfo->dwProxyServerPort = (jlong)env->CallObjectMethod(*jCloudConfig, midgetcloudProxyPort);

			jmethodID midiscloudProxyRequireAuth = env->GetMethodID(class_JCloudConfig, "iscloudProxyRequireAuth", "()Z");
			parchiveCloudInfo->bproxyRequiresAuth = env->CallBooleanMethod(*jCloudConfig, midiscloudProxyRequireAuth);

			jmethodID midgetcloudProxyUserName = env->GetMethodID(class_JCloudConfig, "getcloudProxyUserName", "()Ljava/lang/String;");
			jstring jCloudProxyUsername = (jstring)env->CallObjectMethod(*jCloudConfig, midgetcloudProxyUserName);
			parchiveCloudInfo->pwszproxyUserName = JStringToWCHAR(env, jCloudProxyUsername);
			if (jCloudProxyUsername)
				env->DeleteLocalRef(jCloudProxyUsername);

			jmethodID midgetcloudProxyPassword = env->GetMethodID(class_JCloudConfig, "getcloudProxyPassword", "()Ljava/lang/String;");
			jstring jCloudProxyPassword = (jstring)env->CallObjectMethod(*jCloudConfig, midgetcloudProxyPassword);
			parchiveCloudInfo->pwszProxyPassword = JStringToWCHAR(env, jCloudProxyPassword);
			if (jCloudProxyPassword)
				env->DeleteLocalRef(jCloudProxyPassword);
		}

		//if (class_JDestInfo != NULL) env->DeleteLocalRef(class_JDestInfo);
		if (class_JCloudConfig != NULL) env->DeleteLocalRef(class_JCloudConfig);

		return 1;
	}

	int ConvertArchiveJobsInformation2JObject(JNIEnv *env, jclass clz, vector<ARCHIVE_ITEM_INFO>  & archiveJobs, jobject & out_archiveJobsList)
	{
		jclass list_class = env->GetObjectClass(out_archiveJobsList);
		jmethodID addMethod = env->GetMethodID(list_class, "add", "(Ljava/lang/Object;)Z");

		jclass m_jclsJArchiveJobInfo;
		m_jclsJArchiveJobInfo = env->FindClass("com/ca/arcflash/webservice/data/archive/ArchiveJobInfo");

		/*jclass arrayListClass = env->FindClass("java/util/ArrayList");
		jmethodID arrayListConstr = env->GetMethodID(arrayListClass, "<init>", "()V");
		jmethodID listAddMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");*/

		jmethodID midsetbackupSessionPath = env->GetMethodID(m_jclsJArchiveJobInfo, "setbackupSessionPath", "(Ljava/lang/String;)V");
		jmethodID midsetbackupSessionId = env->GetMethodID(m_jclsJArchiveJobInfo, "setbackupSessionId", "(Ljava/lang/String;)V");
		jmethodID midsetArchiveJobStatus = env->GetMethodID(m_jclsJArchiveJobInfo, "setarchiveJobStatus", "(I)V");

		jmethodID midsetArchiveDataSize = env->GetMethodID(m_jclsJArchiveJobInfo, "setArchiveDataSize", "(Ljava/lang/String;)V");
		jmethodID midsetCopyDataSize = env->GetMethodID(m_jclsJArchiveJobInfo, "setCopyDataSize", "(Ljava/lang/String;)V");

		jmethodID midsetHour = env->GetMethodID(m_jclsJArchiveJobInfo, "setHour", "(J)V");
		jmethodID midsetMin = env->GetMethodID(m_jclsJArchiveJobInfo, "setMin", "(J)V");
		jmethodID midsetSec = env->GetMethodID(m_jclsJArchiveJobInfo, "setSec", "(J)V");
		jmethodID midsetDay = env->GetMethodID(m_jclsJArchiveJobInfo, "setDay", "(J)V");
		jmethodID midsetMonth = env->GetMethodID(m_jclsJArchiveJobInfo, "setMonth", "(J)V");
		jmethodID midsetYear = env->GetMethodID(m_jclsJArchiveJobInfo, "setYear", "(J)V");

		//to set job method
		jmethodID midsetJobMethod = env->GetMethodID(m_jclsJArchiveJobInfo, "setJobMethod", "(J)V");
		//to set compression flag
		jmethodID midsetCompression = env->GetMethodID(m_jclsJArchiveJobInfo, "setCompression", "(J)V");
		jmethodID midsetscheduleCount = env->GetMethodID(m_jclsJArchiveJobInfo, "setscheduleCount", "(J)V");
		jmethodID midsetEncryptionStatus = env->GetMethodID(m_jclsJArchiveJobInfo, "setEncryptionStatus", "(J)V");
		jmethodID midsetarchiveJobId = env->GetMethodID(m_jclsJArchiveJobInfo, "setarchiveJobId", "(J)V");

		//Api;s to set archive destination details
		jmethodID midsetArchiveDestinationType = env->GetMethodID(m_jclsJArchiveJobInfo, "setArchiveDestinationType", "(J)V");
		jmethodID midsetArchiveDestinationPath = env->GetMethodID(m_jclsJArchiveJobInfo, "setArchiveDestinationPath", "(Ljava/lang/String;)V");
		jmethodID midsetArchiveDestinationUser = env->GetMethodID(m_jclsJArchiveJobInfo, "setArchiveDestinationUser", "(Ljava/lang/String;)V");

		if (archiveJobs.size() != 0)
		{
			for (vector<ARCHIVE_ITEM_INFO>::iterator itrarchiveJob = archiveJobs.begin(); itrarchiveJob != archiveJobs.end(); itrarchiveJob++)
			{
				jmethodID jJArchiveJobInfoConstructor = env->GetMethodID(m_jclsJArchiveJobInfo, "<init>", "()V");
				jobject objArchiveJobInfo = env->NewObject(m_jclsJArchiveJobInfo, jJArchiveJobInfoConstructor);
				env->CallNonvirtualVoidMethod(objArchiveJobInfo, m_jclsJArchiveJobInfo, jJArchiveJobInfoConstructor);

				env->CallVoidMethod(objArchiveJobInfo, midsetArchiveJobStatus, (*itrarchiveJob).dwStatus);

				jstring jBackupSessionPath = WCHARToJString(env, (wchar_t*)((*itrarchiveJob).strSubPath.c_str()));
				env->CallVoidMethod(objArchiveJobInfo, midsetbackupSessionPath, jBackupSessionPath);

				jstring jBackupSessionId = WCHARToJString(env, (wchar_t*)((*itrarchiveJob).strId.c_str()));
				env->CallVoidMethod(objArchiveJobInfo, midsetbackupSessionId, jBackupSessionId);

				jstring jArchiveSize = WCHARToJString(env, (wchar_t*)((*itrarchiveJob).strArchiveDataSizeB.c_str()));
				env->CallVoidMethod(objArchiveJobInfo, midsetArchiveDataSize, jArchiveSize);

				jstring jfilecopySize = WCHARToJString(env, (wchar_t*)((*itrarchiveJob).strFileCopyDataSizeB.c_str()));
				env->CallVoidMethod(objArchiveJobInfo, midsetCopyDataSize, jfilecopySize);

				SYSTEMTIME ucdTime;
				SYSTEMTIME localTime = { 0 };
				ucdTime.wHour = (*itrarchiveJob).dwHour;
				ucdTime.wMinute = (*itrarchiveJob).dwMin;
				ucdTime.wSecond = (*itrarchiveJob).dwSec;
				ucdTime.wDay = (*itrarchiveJob).dwDay;
				ucdTime.wMonth = (*itrarchiveJob).dwMonth;
				ucdTime.wYear = (*itrarchiveJob).dwYear;
				ucdTime.wMilliseconds = 0;

				TIME_ZONE_INFORMATION timeZoneInfo;

				GetTimeZoneInformation(&timeZoneInfo);

				if (!SystemTimeToTzSpecificLocalTime(&timeZoneInfo, &ucdTime, &localTime))
				{
					LogFile(L"Failed to get the local time");
					ZeroMemory(&localTime, sizeof(SYSTEMTIME));
				}
				env->CallVoidMethod(objArchiveJobInfo, midsetHour, (jlong)(localTime.wHour));
				env->CallVoidMethod(objArchiveJobInfo, midsetMin, (jlong)(localTime.wMinute));
				env->CallVoidMethod(objArchiveJobInfo, midsetSec, (jlong)(localTime.wSecond));
				env->CallVoidMethod(objArchiveJobInfo, midsetDay, (jlong)(localTime.wDay));
				env->CallVoidMethod(objArchiveJobInfo, midsetMonth, (jlong)(localTime.wMonth));
				env->CallVoidMethod(objArchiveJobInfo, midsetYear, (jlong)(localTime.wYear));

				//setting job method
				env->CallVoidMethod(objArchiveJobInfo, midsetJobMethod, (jlong)(*itrarchiveJob).dwJobMethod);
				env->CallVoidMethod(objArchiveJobInfo, midsetCompression, (jlong)(*itrarchiveJob).CompressionType);
				env->CallVoidMethod(objArchiveJobInfo, midsetscheduleCount, (jlong)(*itrarchiveJob).dwScheduleCount);
				env->CallVoidMethod(objArchiveJobInfo, midsetEncryptionStatus, (jlong)(*itrarchiveJob).dwEncryptedStatus);
				env->CallVoidMethod(objArchiveJobInfo, midsetarchiveJobId, (jlong)(*itrarchiveJob).dwArchiveJobID);

				//Settings archive dest details
				env->CallVoidMethod(objArchiveJobInfo, midsetArchiveDestinationType, (jlong)(*itrarchiveJob).dwArchiveDestinationType);

				jstring jArchiveDestinationPath = WCHARToJString(env, (wchar_t*)((*itrarchiveJob).stArchiveDestinationPath.c_str()));
				env->CallVoidMethod(objArchiveJobInfo, midsetArchiveDestinationPath, jArchiveDestinationPath);

				if (jArchiveDestinationPath)
				{
					env->DeleteLocalRef(jArchiveDestinationPath);
				}

				jstring jArchiveDestinationUser = WCHARToJString(env, (wchar_t*)((*itrarchiveJob).stArchiveDestinationUser.c_str()));
				env->CallVoidMethod(objArchiveJobInfo, midsetArchiveDestinationUser, jArchiveDestinationUser);

				if (jArchiveDestinationUser)
				{
					env->DeleteLocalRef(jArchiveDestinationUser);
				}

				//////

				env->CallBooleanMethod(out_archiveJobsList, addMethod, objArchiveJobInfo);

				if (jfilecopySize)
					env->DeleteLocalRef(jfilecopySize);

				if (jArchiveSize)
					env->DeleteLocalRef(jArchiveSize);

				if (jBackupSessionId)
					env->DeleteLocalRef(jBackupSessionId);

				if (jBackupSessionPath)
					env->DeleteLocalRef(jBackupSessionPath);

				if (objArchiveJobInfo != NULL)
					env->DeleteLocalRef(objArchiveJobInfo);
			}
		}

		/*if(arrayListClass != NULL)
		env->DeleteLocalRef(arrayListClass);*/

		if (m_jclsJArchiveJobInfo != NULL)
			env->DeleteLocalRef(m_jclsJArchiveJobInfo);

		if (list_class != NULL)
			env->DeleteLocalRef(list_class);
		return 1;
	}
	void FreeArchiveDiskInfo(ARCHIVEDISKDESTINFO * archiveDestDiskInfo)
	{
		if (archiveDestDiskInfo == NULL) return;

		if (archiveDestDiskInfo->pwszDestpath != NULL)
			free(archiveDestDiskInfo->pwszDestpath);
		if (archiveDestDiskInfo->pwszUserName != NULL)
			free(archiveDestDiskInfo->pwszUserName);
		if (archiveDestDiskInfo->pwszPassword != NULL)
			free(archiveDestDiskInfo->pwszPassword);
	}

	void FreeArchiveCloudInfo(ARCHIVECLOUDDESTINFO * archiveDestCloudInfo)
	{
		if (archiveDestCloudInfo == NULL) return;

		if (archiveDestCloudInfo->pwszCloudBucketName != NULL)
			free(archiveDestCloudInfo->pwszCloudBucketName);
		if (archiveDestCloudInfo->pwszCloudDisplayBucketName != NULL)
			free(archiveDestCloudInfo->pwszCloudDisplayBucketName);
		if (archiveDestCloudInfo->pwszCloudVendorURL != NULL)
			free(archiveDestCloudInfo->pwszCloudVendorURL);
		if (archiveDestCloudInfo->pwszProxyPassword != NULL)
			free(archiveDestCloudInfo->pwszProxyPassword);
		if (archiveDestCloudInfo->pwszProxyServerName != NULL)
			free(archiveDestCloudInfo->pwszProxyServerName);
		if (archiveDestCloudInfo->pwszproxyUserName != NULL)
			free(archiveDestCloudInfo->pwszproxyUserName);
		if (archiveDestCloudInfo->pwszVendorHostname != NULL)
			free(archiveDestCloudInfo->pwszVendorHostname);
		if (archiveDestCloudInfo->pwszVendorPassword != NULL)
			free(archiveDestCloudInfo->pwszVendorPassword);
		if (archiveDestCloudInfo->pwszVendorUsername != NULL)
			free(archiveDestCloudInfo->pwszVendorUsername);
	}

	/* ARCHIVE */
	jlong JNICALL AFArchivePurge(JNIEnv *env, jclass jclz, jobject in_archiveJobScript)
	{
		LogFile(L"AFArchivePurge Start");
		DWORD dwRet = 0;
		AFARCHIVEJOBSCRIPT afArchiveJS;
		memset(&afArchiveJS, 0, sizeof(AFARCHIVEJOBSCRIPT));
		ArchiveJobScript2AFJOBSCRIPT(env, &afArchiveJS, &in_archiveJobScript);
#ifdef _RPSNF_INCLUDE_
		afArchiveJS.dwproductType = PRODUCT_TYPE::PRODUCT_RPS;
		afArchiveJS.usJobType = AF_JOBTYPE_ARCHIVE_PURGE;
		dwRet = RPS_AFArchiveJob(&afArchiveJS, NULL, NULL);
#else
		afArchiveJS.dwproductType = PRODUCT_TYPE::PRODUCT_D2D;
		dwRet = AFArchivePurge(&afArchiveJS, NULL, NULL);
#endif
		FreeArchiveJobScript(&afArchiveJS);
		LogFile(L"AFArchivePurge End");
		return dwRet;
	}

	jlong JNICALL AFArchiveRestore
		(JNIEnv *env, jclass jclz, jobject bkpJS)
	{
		LogFile(L"AFArchive restore start");
		AFARCHIVEJOBSCRIPT afArchiveJS;
		memset(&afArchiveJS, 0, sizeof(AFARCHIVEJOBSCRIPT));//memset(&afArchiveJS, 0, sizeof(PAFARCHIVEJOBSCRIPT));
		ArchiveJobScript2AFJOBSCRIPT(env, &afArchiveJS, &bkpJS);
		LogFile(L"triggering backend");
		DWORD dwRet;
		try
		{
#ifdef _RPSNF_INCLUDE_
			afArchiveJS.dwproductType = PRODUCT_TYPE::PRODUCT_RPS;
			afArchiveJS.usJobType = AF_JOBTYPE_ARCHIVE_RESTORE;
			dwRet = RPS_AFArchiveJob(&afArchiveJS, NULL, NULL);
#else
			afArchiveJS.dwproductType = PRODUCT_TYPE::PRODUCT_D2D;
			dwRet = AFArchiveRestore(&afArchiveJS, NULL, NULL);
#endif
		}
		catch (...)
		{
			LogFile(L"Exception occurred in adarchive restore");
		}
		try
		{
			FreeArchiveJobScript(&afArchiveJS);
		}
		catch (...)
		{
			LogFile(L"Exception occurred in freeing job script");
		}
		LogFile(L"AFArchive restore End");
		return dwRet;
	}

	jlong JNICALL AFArchiveCatalogSync
		(JNIEnv *env, jclass jclz, jobject in_archiveCatalogSyncJobScript)
	{
		LogFile(L"AFArchiveCatalogSync Start");
		DWORD dwRet = 0;
		AFARCHIVEJOBSCRIPT afArchiveJS;
		memset(&afArchiveJS, 0, sizeof(AFARCHIVEJOBSCRIPT));
		ArchiveJobScript2AFJOBSCRIPT(env, &afArchiveJS, &in_archiveCatalogSyncJobScript);
#ifdef _RPSNF_INCLUDE_
		afArchiveJS.dwproductType = PRODUCT_TYPE::PRODUCT_RPS;
		afArchiveJS.usJobType = AF_JOBTYPE_ARCHIVE_CATALOGRESYNC;
		dwRet = RPS_AFArchiveJob(&afArchiveJS, NULL, NULL);
#else
		afArchiveJS.dwproductType = PRODUCT_TYPE::PRODUCT_D2D;
		dwRet = AFArchiveCatalogSync(&afArchiveJS, NULL, NULL);
#endif
		TCHAR szErrMsg[MAX_PATH] = { _T('\0') };
		_stprintf(szErrMsg, L"AFArchiveCatalogSync returned dwRet [%d]", dwRet);
		LogFile(szErrMsg);
		FreeArchiveJobScript(&afArchiveJS);
		LogFile(L"AFArchiveCatalogSync End");
		return dwRet;
	}

	//madra04 modifications..
	jlong JNICALL validateEncryptionSettings(JNIEnv *env, jclass clz, jobject in_archiveJobScript, jobject out_jErrorcode, jobject out_jCCIErrorCode)
	{
		InitFileCopyLogging();

		if (m_hArchiveFileStoreHandle == NULL)
		{
			m_hArchiveFileStoreHandle = LoadLibrary(L"AFFileStore.dll");
		}

		PFCreateCldAPIsPtr pfCreateCldAPIsPtr = NULL;
		pfCreateCldAPIsPtr = (PFCreateCldAPIsPtr)GetProcAddress(m_hArchiveFileStoreHandle, "CreateCldAPIsPtr");
		if (NULL == pfCreateCldAPIsPtr)
		{
			return -1;
		}
		ICloudAPIs* cldAPIsPtr = (*pfCreateCldAPIsPtr)();
		if (NULL == cldAPIsPtr)
		{
			return -1;
		}
		// PKTC :here throw an error if the affilestore module is not available
		// PKTC: CALL IMPERSONATE AND REVERT IN THE CODE

		//validateEncryptionSettings fundtion needs to call..
		DWORD dwRet = 0; //
		DWORD dwCCIRet = 0; //

		AFARCHIVEJOBSCRIPT afArchiveJS;
		memset(&afArchiveJS, 0, sizeof(AFARCHIVEJOBSCRIPT));
		ArchiveJobScript2AFJOBSCRIPT(env, &afArchiveJS, &in_archiveJobScript);

		//INITCLOUDSESSIONEX  pfnInitCloudSession = NULL;
		//ISFILECOPYDESTINATIONINITEDFORENCRYPTION pfnIsFilecopyDestinationInitedForEncryption = NULL;
		//INITFILECOPYDESTINATIONFORENCRYPTION pfnInitFileCopyDestinationforEncryption = NULL;
		//VALIDATEFILECOPYDESTENCRPASSWORD pfnValidateFilecopyDestEncrPassword = NULL;
		//AFCCICLOUDDISABLEASYNCMODE pfnAFCCICloudDisableAsyncMode = NULL;

		//pfnInitCloudSession = (INITCLOUDSESSIONEX)GetProcAddress(m_hArchiveFileStoreHandle, "InitCloudSessionEx");
		//pfnIsFilecopyDestinationInitedForEncryption = (ISFILECOPYDESTINATIONINITEDFORENCRYPTION)GetProcAddress(m_hArchiveFileStoreHandle, "IsFileCopyDestinationInitedForEncryption");
		//pfnInitFileCopyDestinationforEncryption = (INITFILECOPYDESTINATIONFORENCRYPTION)GetProcAddress(m_hArchiveFileStoreHandle, "InitFileCopyDestinationForEncryption");
		//pfnValidateFilecopyDestEncrPassword = (VALIDATEFILECOPYDESTENCRPASSWORD)GetProcAddress(m_hArchiveFileStoreHandle, "ValidateFileCopyDestEncrPassword");
		//pfnAFCCICloudDisableAsyncMode = (AFCCICLOUDDISABLEASYNCMODE)GetProcAddress(m_hArchiveFileStoreHandle, "AFCCICloudDisableASYNCMODE");

		//CLOSECLOUDSESSION pfnCloseCloudSession = NULL;
		//pfnCloseCloudSession = (CLOSECLOUDSESSION)GetProcAddress(m_hArchiveFileStoreHandle, "CloseCloudSession");

		BOOL bInitCloudSuccess = FALSE;

		/*if (pfnInitCloudSession!=NULL && pfnIsFilecopyDestinationInitedForEncryption!= NULL
		&& pfnInitFileCopyDestinationforEncryption!= NULL && pfnValidateFilecopyDestEncrPassword!= NULL
		&& pfnCloseCloudSession!=NULL
		&& pfnAFCCICloudDisableAsyncMode!=NULL)*/
		{
			// if it is disk or n/w share then call the impersonate code - the macro finds out if it is a n/w share
			if (afArchiveJS.dwDestType == 4)
			{
				 NET_CONN_INFO ConnectionInfo;
				 ZeroMemory(&ConnectionInfo, sizeof(NET_CONN_INFO));
				 if (afArchiveJS.pDiskDest->pwszUserName) { _tcscpy_s(ConnectionInfo.szUsr, afArchiveJS.pDiskDest->pwszUserName); }
				 if (afArchiveJS.pDiskDest->pwszPassword) { _tcscpy_s(ConnectionInfo.szPwd, afArchiveJS.pDiskDest->pwszPassword); }
				 if (afArchiveJS.pDiskDest->pwszDestpath) { _tcscpy_s(ConnectionInfo.szDir, afArchiveJS.pDiskDest->pwszDestpath); }

				DO_IMPERSONATE(dwRet, ConnectionInfo)
			}
			//bInitCloudSuccess = pfnInitCloudSession (&afArchiveJS, dwCCIRet );
			HANDLE handle;
			bInitCloudSuccess = cldAPIsPtr->InitCloudSessionEx(&afArchiveJS, dwCCIRet, handle, true);
			if (bInitCloudSuccess)
			{
				BOOL bInited = FALSE;
				BOOL bEncrypted = FALSE;

				// here make a call to CCI to set the ASYNC MODE to OFF
				//dwRet = pfnAFCCICloudDisableAsyncMode (TRUE);
				dwRet = cldAPIsPtr->AFCCICloudDisableASYNCMODE(handle, TRUE);
				if (dwRet == 0)
				{
					//dwRet = pfnIsFilecopyDestinationInitedForEncryption(dwCCIRet, bInited,bEncrypted);
					dwRet = cldAPIsPtr->IsFileCopyDestinationInitedForEncryption(handle, dwCCIRet, bInited, bEncrypted);
					if (dwRet == 0)
					{
						if (bInited)
						{
							if (bEncrypted)
							{
								if (afArchiveJS.dwEncryptionEnabled == true)  // he is not modifying anything
								{
									//dwRet = pfnValidateFilecopyDestEncrPassword(wstring(afArchiveJS.pwszEncrptionPassword), dwCCIRet,E_CRYPTOAPI_TYPE_ECAT_BY_OS,E_ENCALG_TYPE_EEAT_AES_256BIT);
									dwRet = cldAPIsPtr->ValidateFileCopyDestEncrPassword(handle, wstring(afArchiveJS.pwszEncrptionPassword), dwCCIRet, E_CRYPTOAPI_TYPE_ECAT_BY_OS, E_ENCALG_TYPE_EEAT_AES_256BIT);
									if (dwRet == 0)
									{
										//password is validated successfully..
									}
								}
								else
								{
									dwRet = 1;
									dwCCIRet = 0;
								}
							}
							else
							{
								if (afArchiveJS.dwEncryptionEnabled == true)
								{
									dwRet = 1;
									dwCCIRet = 1;
								}
								else
								{
									dwRet = 0;
								}
							}
						}
						else   // Encryption/password is not intailaized.
						{
							BOOL bEncrypt = (BOOL)afArchiveJS.dwEncryptionEnabled;
							dwRet = cldAPIsPtr->InitFileCopyDestinationForEncryption(handle, afArchiveJS.pAFNodeList->pwszNodeName, dwCCIRet, bEncrypt, wstring(afArchiveJS.pwszEncrptionPassword), E_CRYPTOAPI_TYPE_ECAT_BY_OS, E_ENCALG_TYPE_EEAT_AES_256BIT);
						}
					}
				}
			}

			if (bInitCloudSuccess)
			{
				//pfnCloseCloudSession ();
				cldAPIsPtr->CloseCloudSession(handle);
				cldAPIsPtr->Release(handle);
			}

			RETWITHREVERT();
			//Library is not intialized.
		}

		if (dwCCIRet == GL_ERROR_BUCKET_NOT_EXIST) //If bucket doesn't exist then we convert the error code to success
			dwCCIRet = GL_ERROR_NO_ERROR;

		//
		AddUINT2JRWLong(env, dwRet, &out_jErrorcode);
		AddUINT2JRWLong(env, dwCCIRet, &out_jCCIErrorCode);

		FreeArchiveJobScript(&afArchiveJS);

		return (jlong)0;
	}

	// this will initialise the arciving logs for the webservice
	void InitFileCopyLogging()
	{
		static  BOOL bFCLogInited = FALSE;
		DWORD	dwArchLogLevel, dwMaxLogLinesPerFile;

		if (!bFCLogInited)
		{
			//if(m_hArchiveFileStoreHandle == NULL){
			//	m_hArchiveFileStoreHandle = LoadLibrary(L"AFFileStore.dll");
			//}

			if (m_hArchiveArchiverDLLHandler == NULL)
			{
				m_hArchiveArchiverDLLHandler = LoadLibrary(L"AFArchiver.dll");
			}

			// if either of the DLLs are not available then return
			if (m_hArchiveArchiverDLLHandler == NULL)
			{
				return;
			}

			INITAFARCHLOG  pfnInitAFRCHLog = NULL;
			QUERYAFARCHLOGPARAMETERS pfnQueryAFARCHLogParameters = NULL;
			SETAFARCHLOGPARAMETERS pfnSetAFARCHLogParameters = NULL;

			pfnInitAFRCHLog = (INITAFARCHLOG)GetProcAddress(m_hArchiveArchiverDLLHandler, "InitAFARCHLog");
			pfnQueryAFARCHLogParameters = (QUERYAFARCHLOGPARAMETERS)GetProcAddress(m_hArchiveArchiverDLLHandler, "QueryAFARCHLogParameters");
			pfnSetAFARCHLogParameters = (SETAFARCHLOGPARAMETERS)GetProcAddress(m_hArchiveArchiverDLLHandler, "SetAFARCHLogParameters");

			// if we fail to get any of the functions - then return
			if (!(pfnInitAFRCHLog && pfnQueryAFARCHLogParameters && pfnSetAFARCHLogParameters))
			{
				return;
			}

			wstring nodename = L"";
			// here init the logging - set the job number as 0 as this is for the webservice
			pfnInitAFRCHLog(L"Webservice_Filecopy", nodename, 0);
			// query the parameters from the registry
			pfnQueryAFARCHLogParameters(dwArchLogLevel, dwMaxLogLinesPerFile);
			// set the log parameters
			pfnSetAFARCHLogParameters(dwArchLogLevel, dwMaxLogLinesPerFile);

			bFCLogInited = TRUE;
		}
	}

	jlong JNICALL browseArchiveCatalogChildrenEx(JNIEnv *env, jclass clz, jlong lVolumeHandle, jstring inCatalogFilePath, jlong in_lIndex, jlong in_lCount, jobject archiveCatalogItems)
	{
		InitFileCopyLogging();

		try
		{
			LogFile(L"browseArchiveCatalogChildren start");
			DWORD dwChildCount = 0;
			pfnGetChildrenEx GetChildrenEx = NULL;
			HANDLE hVolumeHandle = (HANDLE)lVolumeHandle;
			wchar_t* szCatalogFilePath = JStringToWCHAR(env, inCatalogFilePath);

			TCHAR strMessage[MAX_PATH * 3] = { _T('\0') };
			_stprintf(strMessage, _T("Browsing Catalog Path [%s]"), szCatalogFilePath);
			LogFile(strMessage);

			vector <pCatalogChildItem> vecCatalogList;
			if (m_hArchiveCatalogHandle != NULL)
			{
				GetChildrenEx = (pfnGetChildrenEx)GetProcAddress(m_hArchiveCatalogHandle, "GetChildrenEx");
				if (GetChildrenEx != NULL)
				{
					LogFile(L"Calling native method GetChildren");
					dwChildCount = (GetChildrenEx)(hVolumeHandle, szCatalogFilePath, vecCatalogList, in_lIndex, in_lCount);
					LogFile(L"converting catalog items");
					convertCVersionToJniVersion(env, clz, inCatalogFilePath, vecCatalogList, archiveCatalogItems);

					//free the items received from catalog
					FREECATALOGITEMS pfnFreeCatalogItems = NULL;
					pfnFreeCatalogItems = (FREECATALOGITEMS)GetProcAddress(m_hArchiveCatalogHandle, "FreeCatalogItems");
					if (pfnFreeCatalogItems != NULL)
					{
						LogFile(L"browseArchiveCatalogChildren Free");
						pfnFreeCatalogItems(vecCatalogList);
						LogFile(L"browseArchiveCatalogChildren Free done");
					}
				}
			}

			if (szCatalogFilePath != NULL)
				free(szCatalogFilePath);
			LogFile(L"browseArchiveCatalogChildren end");
		}
		catch (...)
		{
			LogFile(L"exception occurred in browseArchiveCatalogChildren");
		}
		return 1;
	}

	jlong JNICALL searchArchiveCatalogChildren(JNIEnv *env, jclass clz, jstring inFileName, jstring inHostName, jstring inSearchpath, jobject inArchiveDestConfig,
		jlong in_lSearchOptions, jlong in_lIndex, jlong in_lCount, jobject archiveCatalogItems)
	{
		DWORD dwRet = 0;
		InitFileCopyLogging();
		if (m_hArchiveCatalogHandle == NULL)
		{
			m_hArchiveCatalogHandle = LoadLibrary(L"AFFileCatalog.dll");
		}

		jclass list_class = env->GetObjectClass(archiveCatalogItems);
		jmethodID addMethod = env->GetMethodID(list_class, "add", "(Ljava/lang/Object;)Z");

		pfnGetChildrenbySearch GetChildrenbySearch = NULL;

		wchar_t* szFileName = JStringToWCHAR(env, inFileName);
		wchar_t* szHostName = JStringToWCHAR(env, inHostName);
		wchar_t* szSearchPath = JStringToWCHAR(env, inSearchpath);

		jclass class_JDestInfo = env->GetObjectClass(inArchiveDestConfig);
		jfieldID field_bArchiveToDrive = env->GetFieldID(class_JDestInfo, "bArchiveToDrive", "Z");
		BOOL bUseDisk = env->GetBooleanField(inArchiveDestConfig, field_bArchiveToDrive);

		jfieldID field_bArchiveToCloud = env->GetFieldID(class_JDestInfo, "bArchiveToCloud", "Z");
		BOOL bUseCloud = env->GetBooleanField(inArchiveDestConfig, field_bArchiveToCloud);

		DWORD dwDestType = -1;
		ARCHIVEDISKDESTINFO archiveDestDiskInfo;
		if (bUseDisk)
		{
			dwDestType = 4;
			memset(&archiveDestDiskInfo, 0, sizeof(ARCHIVEDISKDESTINFO));
			JArchiveDestConfig2ArchiveDiskDestInfo(env, &archiveDestDiskInfo, &inArchiveDestConfig);
		}

		ARCHIVECLOUDDESTINFO archiveDestCloudInfo;
		if (bUseCloud)
		{
			//dwDestType = 0;
			memset(&archiveDestCloudInfo, 0, sizeof(ARCHIVECLOUDDESTINFO));
			JArchiveDestConfig2ArchiveCloudDestInfo(env, &archiveDestCloudInfo, &inArchiveDestConfig);
			dwDestType = archiveDestCloudInfo.dwCloudVendorType;
		}

		if (m_hArchiveFileStoreHandle == NULL)
		{
			m_hArchiveFileStoreHandle = LoadLibrary(L"AFFileStore.dll");
		}

		if (m_hArchiveFileStoreHandle != NULL)
		{
			PAFARCHIVEJOBSCRIPT pAFJobScript = (PAFARCHIVEJOBSCRIPT)malloc(sizeof(AFARCHIVEJOBSCRIPT));
			if (!pAFJobScript)
			{
				// mem allocation failure
				return -1;
			}

			// PKTC : also pass the correct hostname for this scenario - here the hostname will depend on the destination selected
			// for disk/nwshare - it will be picked from the path and for cloud it will be picked from the bucket
			pAFJobScript->dwDestType = dwDestType;
			pAFJobScript->pCloudDest = &archiveDestCloudInfo;
			pAFJobScript->pDiskDest = &archiveDestDiskInfo;
			pAFJobScript->ulShrMemID = 0;						// init the jobnumber to 0

			// if it is disk or n/w share then call the impersonate code - the macro finds out if it is a n/w share
			if (bUseDisk)
			{
				//DWORD dwRet = 0;

				 NET_CONN_INFO ConnectionInfo;
				 ZeroMemory(&ConnectionInfo, sizeof(NET_CONN_INFO));
				 if (pAFJobScript->pDiskDest->pwszUserName) { _tcscpy_s(ConnectionInfo.szUsr, pAFJobScript->pDiskDest->pwszUserName); }
				 if (pAFJobScript->pDiskDest->pwszPassword) { _tcscpy_s(ConnectionInfo.szPwd, pAFJobScript->pDiskDest->pwszPassword); }
				 if (pAFJobScript->pDiskDest->pwszDestpath) { _tcscpy_s(ConnectionInfo.szDir, pAFJobScript->pDiskDest->pwszDestpath); }

				DO_IMPERSONATE(dwRet, ConnectionInfo)
			}

			INITCLOUDSESSIONEX  pfnInitCloudSession = NULL;
			pfnInitCloudSession = (INITCLOUDSESSIONEX)GetProcAddress(m_hArchiveFileStoreHandle, "InitCloudSessionEx");

			BOOL bInitCloudSuccess = FALSE;
			DWORD dwCCIErrCode = 0;

			if (pfnInitCloudSession != NULL)
			{
				bInitCloudSuccess = pfnInitCloudSession(pAFJobScript, dwCCIErrCode);
			}

			// HERE FREE UP THE MEMORY
			free(pAFJobScript);

			if (bInitCloudSuccess)
			{
				//DWORD dwRet = 0;
				vector <pCatalogChildItem> vecCatalogList;
				if (m_hArchiveCatalogHandle != NULL)
				{
					GetChildrenbySearch = (pfnGetChildrenbySearch)GetProcAddress(m_hArchiveCatalogHandle, "SearchChildrens");
					if (GetChildrenbySearch != NULL)
					{
						dwRet = (GetChildrenbySearch)(szFileName, szHostName, szSearchPath, in_lSearchOptions, in_lIndex, in_lCount, vecCatalogList);
						convertCVersionToJniVersion(env, clz, inSearchpath, vecCatalogList, archiveCatalogItems);

						//free the items received from catalog
						FREECATALOGITEMS pfnFreeCatalogItems = NULL;
						pfnFreeCatalogItems = (FREECATALOGITEMS)GetProcAddress(m_hArchiveCatalogHandle, "FreeCatalogItems");
						if (pfnFreeCatalogItems != NULL)
						{
							LogFile(L"searchArchiveCatalogChildren Free");
							pfnFreeCatalogItems(vecCatalogList);
							LogFile(L"searchArchiveCatalogChildren Free done");
						}
					}
				}
			}

			// close the connection to the cloud if it was successful
			if (bInitCloudSuccess)
			{
				CLOSECLOUDSESSION pfnCloseCloudSession = NULL;
				pfnCloseCloudSession = (CLOSECLOUDSESSION)GetProcAddress(m_hArchiveFileStoreHandle, "CloseCloudSession");

				if (pfnCloseCloudSession != NULL)
					pfnCloseCloudSession();
			}

			if (bUseDisk)
			{
				RETWITHREVERT()
			}
		}
		if (szFileName != NULL)
			free(szFileName);

		if (szHostName != NULL)
			free(szHostName);

		if (szSearchPath != NULL)
			free(szSearchPath);

		//delete disk info
		if (bUseDisk)
		{
			//FreeArchiveDiskInfo(&archiveDestDiskInfo);
			if (archiveDestDiskInfo.pwszDestpath != NULL)
				free(archiveDestDiskInfo.pwszDestpath);
			if (archiveDestDiskInfo.pwszUserName != NULL)
				free(archiveDestDiskInfo.pwszUserName);
			if (archiveDestDiskInfo.pwszPassword != NULL)
				free(archiveDestDiskInfo.pwszPassword);
		}

		if (bUseCloud)
		{
			//FreeArchiveCloudInfo(&archiveDestCloudInfo);
			if (archiveDestCloudInfo.pwszCloudBucketName != NULL)
				free(archiveDestCloudInfo.pwszCloudBucketName);
			if (archiveDestCloudInfo.pwszCloudDisplayBucketName != NULL)
				free(archiveDestCloudInfo.pwszCloudDisplayBucketName);
			if (archiveDestCloudInfo.pwszCloudVendorURL != NULL)
				free(archiveDestCloudInfo.pwszCloudVendorURL);
			if (archiveDestCloudInfo.pwszProxyPassword != NULL)
				free(archiveDestCloudInfo.pwszProxyPassword);
			if (archiveDestCloudInfo.pwszProxyServerName != NULL)
				free(archiveDestCloudInfo.pwszProxyServerName);
			if (archiveDestCloudInfo.pwszproxyUserName != NULL)
				free(archiveDestCloudInfo.pwszproxyUserName);
			if (archiveDestCloudInfo.pwszVendorHostname != NULL)
				free(archiveDestCloudInfo.pwszVendorHostname);
			if (archiveDestCloudInfo.pwszVendorPassword != NULL)
				free(archiveDestCloudInfo.pwszVendorPassword);
			if (archiveDestCloudInfo.pwszVendorUsername != NULL)
				free(archiveDestCloudInfo.pwszVendorUsername);
		}
		return dwRet;
	}

	jlong JNICALL AFGetArchiveDestinationVolumes
		(JNIEnv *env, jclass clz, jstring hostName, jobject jDestInfo, jobject volumeList)
	{
		InitFileCopyLogging();
		LogFile(L"in AFGetArchiveDestinationVolumes");
		DWORD dwRet = 0;
		try
		{
			if (m_hArchiveCatalogHandle == NULL)
			{
				m_hArchiveCatalogHandle = LoadLibrary(L"AFFileCatalog.dll");
			}

			jclass list_class = env->GetObjectClass(volumeList);
			jmethodID addMethod = env->GetMethodID(list_class, "add", "(Ljava/lang/Object;)Z");

			wchar_t* pHostName = JStringToWCHAR(env, hostName);

			jclass class_JDestInfo = env->GetObjectClass(jDestInfo);
			jfieldID field_bArchiveToDrive = env->GetFieldID(class_JDestInfo, "bArchiveToDrive", "Z");
			BOOL bUseDisk = env->GetBooleanField(jDestInfo, field_bArchiveToDrive);

			jfieldID field_bArchiveToCloud = env->GetFieldID(class_JDestInfo, "bArchiveToCloud", "Z");
			BOOL bUseCloud = env->GetBooleanField(jDestInfo, field_bArchiveToCloud);

			DWORD dwDestType = -1;
			ARCHIVEDISKDESTINFO archiveDestDiskInfo;
			memset(&archiveDestDiskInfo, 0, sizeof(ARCHIVEDISKDESTINFO));
			if (bUseDisk)
			{
				dwDestType = 4;
				LogFile(L"selected disk");
				JArchiveDestConfig2ArchiveDiskDestInfo(env, &archiveDestDiskInfo, &jDestInfo);
			}

			ARCHIVECLOUDDESTINFO archiveDestCloudInfo;
			memset(&archiveDestCloudInfo, 0, sizeof(ARCHIVECLOUDDESTINFO));
			if (bUseCloud)
			{
				LogFile(L"selected cloud");
				//dwDestType = 0;

				JArchiveDestConfig2ArchiveCloudDestInfo(env, &archiveDestCloudInfo, &jDestInfo);
				dwDestType = archiveDestCloudInfo.dwCloudVendorType;
			}

			if (m_hArchiveFileStoreHandle == NULL)
			{
				m_hArchiveFileStoreHandle = LoadLibrary(L"AFFileStore.dll");
			}

			if (m_hArchiveFileStoreHandle != NULL)
			{
				PAFARCHIVEJOBSCRIPT pAFJobScript = (PAFARCHIVEJOBSCRIPT)malloc(sizeof(AFARCHIVEJOBSCRIPT));
				if (!pAFJobScript)
				{
					// mem allocation failure
					return FALSE;
				}

				pAFJobScript->dwDestType = dwDestType;
				pAFJobScript->pCloudDest = &archiveDestCloudInfo;
				pAFJobScript->pDiskDest = &archiveDestDiskInfo;
				pAFJobScript->ulShrMemID = 0;						// init the jobnumber to 0

				// if it is disk or n/w share then call the impersonate code - the macro finds out if it is a n/w share
				if (bUseDisk)
				{
					DWORD dwRet = 0;

					 NET_CONN_INFO ConnectionInfo;
					 ZeroMemory(&ConnectionInfo, sizeof(NET_CONN_INFO));
					 if (pAFJobScript->pDiskDest->pwszUserName) { _tcscpy_s(ConnectionInfo.szUsr, pAFJobScript->pDiskDest->pwszUserName); }
					 if (pAFJobScript->pDiskDest->pwszPassword) { _tcscpy_s(ConnectionInfo.szPwd, pAFJobScript->pDiskDest->pwszPassword); }
					 if (pAFJobScript->pDiskDest->pwszDestpath) { _tcscpy_s(ConnectionInfo.szDir, pAFJobScript->pDiskDest->pwszDestpath); }

					DO_IMPERSONATE(dwRet, ConnectionInfo)
				}

				INITCLOUDSESSIONEX  pfnInitCloudSession = NULL;
				pfnInitCloudSession = (INITCLOUDSESSIONEX)GetProcAddress(m_hArchiveFileStoreHandle, "InitCloudSessionEx");

				BOOL bInitCloudSuccess = FALSE;
				DWORD dwCCIErrCode = 0;

				if (pfnInitCloudSession != NULL)
				{
					bInitCloudSuccess = pfnInitCloudSession(pAFJobScript, dwCCIErrCode);
				}

				// HERE FREE UP THE MEMORY
				free(pAFJobScript);

				std::vector<PTCHAR> vVolumesList;
				if (bInitCloudSuccess)
				{
					pfnGetArchiveDestinationVolumes pGetArchiveDestinationVolumes = NULL;

					if (m_hArchiveCatalogHandle != NULL)
					{
						pGetArchiveDestinationVolumes = (pfnGetArchiveDestinationVolumes)GetProcAddress(m_hArchiveCatalogHandle, "AFGetArchiveDestinationVolumeList");
						if (pGetArchiveDestinationVolumes != NULL)
						{
							LogFile(L"calling AFGetArchiveDestinationVolumeList");
							dwRet = (pGetArchiveDestinationVolumes)(pHostName, vVolumesList);
							TCHAR szMessage[MAX_PATH * 3] = { _T('\0') };
							_stprintf(szMessage, L"End AFGetArchiveDestinationVolumeList return [%d] items found [%d] ", dwRet, vVolumesList.size());
							LogFile(szMessage);
							for (vector<PTCHAR>::iterator volume = vVolumesList.begin(); volume != vVolumesList.end(); volume++)
							{
								jstring volume_JStr = WCHARToJString(env, (wchar_t*)(*volume));
								env->CallBooleanMethod(volumeList, addMethod, volume_JStr);
								if (volume_JStr != NULL)
									env->DeleteLocalRef(volume_JStr);
							}
						}
					}
				}

				if (bInitCloudSuccess)
				{
					CLOSECLOUDSESSION pfnCloseCloudSession = NULL;
					pfnCloseCloudSession = (CLOSECLOUDSESSION)GetProcAddress(m_hArchiveFileStoreHandle, "CloseCloudSession");

					if (pfnCloseCloudSession != NULL)
						pfnCloseCloudSession();
				}

				if (bUseDisk)
				{
					RETWITHREVERT()
				}

				if (list_class != NULL)
					env->DeleteLocalRef(list_class);

				if (pHostName != NULL)
					free(pHostName);

				FREEVOLUMEITEMS pfnFreeVolumeItems = (FREEVOLUMEITEMS)GetProcAddress(m_hArchiveCatalogHandle, "FreeVolumeItems");

				if (pfnFreeVolumeItems != NULL)
				{
					LogFile(L"freeing volumes");
					pfnFreeVolumeItems(vVolumesList);
				}
			}

			//delete disk info
			if (bUseDisk)
			{
				if (archiveDestDiskInfo.pwszDestpath != NULL)
					free(archiveDestDiskInfo.pwszDestpath);
				if (archiveDestDiskInfo.pwszUserName != NULL)
					free(archiveDestDiskInfo.pwszUserName);
				if (archiveDestDiskInfo.pwszPassword != NULL)
					free(archiveDestDiskInfo.pwszPassword);

				//memset(&archiveDestDiskInfo,0,sizeof(ARCHIVEDISKDESTINFO));
			}

			if (bUseCloud)
			{
				if (archiveDestCloudInfo.pwszCloudBucketName != NULL)
					free(archiveDestCloudInfo.pwszCloudBucketName);
				if (archiveDestCloudInfo.pwszCloudDisplayBucketName != NULL)
					free(archiveDestCloudInfo.pwszCloudDisplayBucketName);
				if (archiveDestCloudInfo.pwszCloudVendorURL != NULL)
					free(archiveDestCloudInfo.pwszCloudVendorURL);
				if (archiveDestCloudInfo.pwszProxyPassword != NULL)
					free(archiveDestCloudInfo.pwszProxyPassword);
				if (archiveDestCloudInfo.pwszProxyServerName != NULL)
					free(archiveDestCloudInfo.pwszProxyServerName);
				if (archiveDestCloudInfo.pwszproxyUserName != NULL)
					free(archiveDestCloudInfo.pwszproxyUserName);
				/*	if(archiveDestCloudInfo.pwszVendorHostname != NULL)
				free(archiveDestCloudInfo.pwszVendorHostname);*/
				if (archiveDestCloudInfo.pwszVendorPassword != NULL)
					free(archiveDestCloudInfo.pwszVendorPassword);
				if (archiveDestCloudInfo.pwszVendorUsername != NULL)
					free(archiveDestCloudInfo.pwszVendorUsername);

				//memset(&archiveDestCloudInfo,0,sizeof(ARCHIVECLOUDDESTINFO));
			}
		}
		catch (...)
		{
			LogFile(L"Exception occurred");
		}

		return (jlong)dwRet;
	}

	jlong JNICALL GetArchivedVolumeHandle(JNIEnv *env, jclass clz, jstring strVolume)
	{
		HANDLE hArchiveVolumeHandle = NULL;
		pfnOpenFileCatalogFile OpenArchiveFileCatalog = NULL;
		wchar_t* szArchiveVolume = JStringToWCHAR(env, strVolume);

		InitFileCopyLogging();

		if (m_hArchiveCatalogHandle != NULL)
		{
			OpenArchiveFileCatalog = (pfnOpenFileCatalogFile)GetProcAddress(m_hArchiveCatalogHandle, "OpenAFFileCatalogFile");
			if (OpenArchiveFileCatalog != NULL)
			{
				hArchiveVolumeHandle = (OpenArchiveFileCatalog)(szArchiveVolume);
			}
		}

		if (szArchiveVolume != NULL)
			free(szArchiveVolume);
		return (jlong)hArchiveVolumeHandle;
	}

	jlong JNICALL GetArchiveChildrenCount(JNIEnv *env, jclass clz, jlong lVolumeHandle, jstring strCatalogPath, jobject childCount)
	{
		DWORD dwChildCount = 0;
		pfnGetChildrenCount GetChildrenCount = NULL;
		HANDLE hVolumeHandle = (HANDLE)lVolumeHandle;

		InitFileCopyLogging();

		wchar_t* szCatalogPath = JStringToWCHAR(env, strCatalogPath);

		if (m_hArchiveCatalogHandle != NULL)
		{
			GetChildrenCount = (pfnGetChildrenCount)GetProcAddress(m_hArchiveCatalogHandle, "GetChildrenCount");
			if (GetChildrenCount != NULL)
			{
				dwChildCount = (GetChildrenCount)(hVolumeHandle, szCatalogPath);
			}
		}

		if (szCatalogPath != NULL)
			free(szCatalogPath);
		AddUINT2JRWLong(env, dwChildCount, &childCount);
		return (jlong)dwChildCount;
	}

	jlong JNICALL browseArchiveCatalogChildren(JNIEnv *env, jclass clz, jlong lVolumeHandle, jstring inCatalogFilePath, jobject archiveCatalogItems)
	{
		InitFileCopyLogging();

		try
		{
			LogFile(L"browseArchiveCatalogChildren start");
			DWORD dwChildCount = 0;
			pfnGetChildren GetChildren = NULL;
			HANDLE hVolumeHandle = (HANDLE)lVolumeHandle;
			wchar_t* szCatalogFilePath = JStringToWCHAR(env, inCatalogFilePath);

			TCHAR strMessage[MAX_PATH * 3] = { _T('\0') };
			_stprintf(strMessage, _T("Browsing Catalog Path [%s]"), szCatalogFilePath);
			LogFile(strMessage);

			vector <pCatalogChildItem> vecCatalogList;
			if (m_hArchiveCatalogHandle != NULL)
			{
				GetChildren = (pfnGetChildren)GetProcAddress(m_hArchiveCatalogHandle, "GetChildren");
				if (GetChildren != NULL)
				{
					LogFile(L"Calling native method GetChildren");
					dwChildCount = (GetChildren)(hVolumeHandle, szCatalogFilePath, vecCatalogList);
					LogFile(L"converting catalog items");
					convertCVersionToJniVersion(env, clz, inCatalogFilePath, vecCatalogList, archiveCatalogItems);

					//free the items received from catalog
					FREECATALOGITEMS pfnFreeCatalogItems = NULL;
					pfnFreeCatalogItems = (FREECATALOGITEMS)GetProcAddress(m_hArchiveCatalogHandle, "FreeCatalogItems");
					if (pfnFreeCatalogItems != NULL)
					{
						LogFile(L"browseArchiveCatalogChildren Free");
						pfnFreeCatalogItems(vecCatalogList);
						LogFile(L"browseArchiveCatalogChildren Free done");
					}
				}
			}

			if (szCatalogFilePath != NULL)
				free(szCatalogFilePath);
			LogFile(L"browseArchiveCatalogChildren end");
		}
		catch (...)
		{
			LogFile(L"exception occurred in browseArchiveCatalogChildren");
		}
		return 1;
	}

	jlong JNICALL verifyBucketName(JNIEnv *env, jclass clz, jobject jCloudDestInfo)
	{
		DWORD dwRet = 0;
		InitFileCopyLogging();

		if (m_hArchiveFileStoreHandle == NULL)
		{
			m_hArchiveFileStoreHandle = LoadLibrary(L"AFFileStore.dll");
		}
		VALIDATEBUCKET pfnVALIDATEBUCKET = NULL;
		pfnVALIDATEBUCKET = (VALIDATEBUCKET)GetProcAddress(m_hArchiveFileStoreHandle, "ValidateBucket");
		CREATEBUCKET pfnCREATEBUCKET = NULL;
		pfnCREATEBUCKET = (CREATEBUCKET)GetProcAddress(m_hArchiveFileStoreHandle, "CreateBucket");
		if (pfnVALIDATEBUCKET == NULL || pfnCREATEBUCKET == NULL)
		{
			/* return ERROR_FAILED_TO_VERIFY_BUCKET_NAME;*/
		}

		ARCHIVECLOUDDESTINFO archiveDestCloudInfo;
		memset(&archiveDestCloudInfo, 0, sizeof(ARCHIVECLOUDDESTINFO));
		JArchiveCloudConfig2ArchiveCloudDestInfo(env, &archiveDestCloudInfo, &jCloudDestInfo);

		BOOL bValid = FALSE;
		dwRet = pfnVALIDATEBUCKET(&archiveDestCloudInfo, bValid);
		if (bValid == FALSE)
		{
			FreeArchiveCloudInfo(&archiveDestCloudInfo);
			return dwRet;
		}

		if ((bValid == TRUE) && (dwRet == 419))
		{
			FreeArchiveCloudInfo(&archiveDestCloudInfo);
			dwRet = 500;//custom error to mention that bucket already existed in current account
			return dwRet;
		}

		dwRet = pfnCREATEBUCKET(&archiveDestCloudInfo);
		/*VENKATTODO: Need to check whether the API is succesfull or not based on the  dwRet*/
		FreeArchiveCloudInfo(&archiveDestCloudInfo);
		return (jlong)dwRet;
	}

	jlong JNICALL getCloudBuckets(JNIEnv *env, jclass clz, jobject jCloudDestInfo, jobject out_Buckets)
	{
		DWORD dwRet = 0;
		InitFileCopyLogging();

		if (m_hArchiveFileStoreHandle == NULL)
		{
			m_hArchiveFileStoreHandle = LoadLibrary(L"AFFileStore.dll");
		}
		GETBUCKETLIST pfnGETBUCKETLIST = NULL;
		pfnGETBUCKETLIST = (GETBUCKETLIST)GetProcAddress(m_hArchiveFileStoreHandle, "GetBucketList");
		/*extern "C" AFSTOREAPI LONG GetBucketList (PARCHIVECLOUDDESTINFO in_pARCHCloudDestInfo, LPWSTR** out_listOfContainers, long* out_itemCount); */
		FREEBUCKETLIST pfnFREEBUCKETLIST = NULL;
		pfnFREEBUCKETLIST = (FREEBUCKETLIST)GetProcAddress(m_hArchiveFileStoreHandle, "FreeBucketList");
		/*extern "C" AFSTOREAPI LONG FreeBucketList (LPWSTR** listOfContainers, long itemCount);*/
		jclass list_class = env->GetObjectClass(out_Buckets);
		jmethodID addMethod = env->GetMethodID(list_class, "add", "(Ljava/lang/Object;)Z");

		ARCHIVECLOUDDESTINFO archiveDestCloudInfo;
		memset(&archiveDestCloudInfo, 0, sizeof(ARCHIVECLOUDDESTINFO));
		JArchiveCloudConfig2ArchiveCloudDestInfo(env, &archiveDestCloudInfo, &jCloudDestInfo);

		LPWSTR * out_listOfContainers = NULL;
		long out_itemCount = 0;
		dwRet = pfnGETBUCKETLIST(&archiveDestCloudInfo, &out_listOfContainers, &out_itemCount);

		for (int iContainerIndex = 0; iContainerIndex < out_itemCount; iContainerIndex++)
		{
			jstring Container_JStr = WCHARToJString(env, out_listOfContainers[iContainerIndex]);
			env->CallBooleanMethod(out_Buckets, addMethod, Container_JStr);
			if (Container_JStr != NULL)
				env->DeleteLocalRef(Container_JStr);
		}
		if (list_class != NULL)
			env->DeleteLocalRef(list_class);
		//pfnFREEBUCKETLIST(&out_listOfContainers,&out_itemCount);
		FreeArchiveCloudInfo(&archiveDestCloudInfo);
		return (jlong)dwRet;
	}

	jlong JNICALL getCloudRegions(JNIEnv *env, jclass clz, jobject jCloudDestInfo, jobject out_CloudRegions)
	{
		DWORD dwRet = 0;
		InitFileCopyLogging();

		if (m_hArchiveFileStoreHandle == NULL)
		{
			m_hArchiveFileStoreHandle = LoadLibrary(L"AFFileStore.dll");
		}
		jclass list_class = env->GetObjectClass(out_CloudRegions);
		jmethodID addMethod = env->GetMethodID(list_class, "add", "(Ljava/lang/Object;)Z");

		GETREGIONLIST pfnGETREGIONLIST = NULL;
		pfnGETREGIONLIST = (GETREGIONLIST)GetProcAddress(m_hArchiveFileStoreHandle, "GetRegionList");

		FREEBUCKETLIST pfnFREEBUCKETLIST = NULL;
		pfnFREEBUCKETLIST = (FREEBUCKETLIST)GetProcAddress(m_hArchiveFileStoreHandle, "FreeBucketList");

		ARCHIVECLOUDDESTINFO archiveDestCloudInfo;
		memset(&archiveDestCloudInfo, 0, sizeof(ARCHIVECLOUDDESTINFO));
		JArchiveCloudConfig2ArchiveCloudDestInfo(env, &archiveDestCloudInfo, &jCloudDestInfo);

		LPWSTR * out_listOfContainers = NULL;
		long out_itemCount = 0;
		dwRet = pfnGETREGIONLIST(&archiveDestCloudInfo, &out_listOfContainers, &out_itemCount);

		for (int iContainerIndex = 0; iContainerIndex < out_itemCount; iContainerIndex++)
		{
			jstring Container_JStr = WCHARToJString(env, (out_listOfContainers[iContainerIndex]));
			env->CallBooleanMethod(out_CloudRegions, addMethod, Container_JStr);
			if (Container_JStr != NULL)
				env->DeleteLocalRef(Container_JStr);
		}
		if (list_class != NULL)
			env->DeleteLocalRef(list_class);
		//pfnFREEBUCKETLIST(&out_listOfContainers,&out_itemCount);

		FreeArchiveCloudInfo(&archiveDestCloudInfo);
		return (jlong)dwRet;
	}

	jstring JNICALL getRegionForBucket(JNIEnv *env, jclass clz, jobject jCloudDestInfo, jobject out_jrwRet)
	{
		InitFileCopyLogging();
		DWORD dwRet = 0;
		if (m_hArchiveFileStoreHandle == NULL)
		{
			m_hArchiveFileStoreHandle = LoadLibrary(L"AFFileStore.dll");
		}
		GETREGIONFORBUCKET pfnGETREGIONFORBUCKET = NULL;
		pfnGETREGIONFORBUCKET = (GETREGIONFORBUCKET)GetProcAddress(m_hArchiveFileStoreHandle, "GetRegionForBucket");

		ARCHIVECLOUDDESTINFO archiveDestCloudInfo;
		memset(&archiveDestCloudInfo, 0, sizeof(ARCHIVECLOUDDESTINFO));
		JArchiveCloudConfig2ArchiveCloudDestInfo(env, &archiveDestCloudInfo, &jCloudDestInfo);

		LPWSTR szRegion;
		dwRet = pfnGETREGIONFORBUCKET(&archiveDestCloudInfo, &szRegion);
		jstring jstrRegion = WCHARToJString(env, (wchar_t*)szRegion);

		AddUINT2JRWLong(env, dwRet, &out_jrwRet);

		TCHAR szMessage[MAX_PATH] = { _T('\0') };
		_stprintf(szMessage, _T("return code from CCI for getRegion for bucket %s is %d"), archiveDestCloudInfo.pwszCloudBucketName, dwRet);
		LogFile(szMessage);
		//out_BucketRegion = Region_JStr;
		//env->DeleteLocalRef(Region_JStr);
		FreeArchiveCloudInfo(&archiveDestCloudInfo);
		return jstrRegion;
	}

	jlong testConnection(JNIEnv *env, jclass clz, jobject jCloudDestInfo)
	{
		InitFileCopyLogging();
		DWORD dwRet = 0;
		if (m_hArchiveFileStoreHandle == NULL)
		{
			m_hArchiveFileStoreHandle = LoadLibrary(L"AFFileStore.dll");
		}
		TESTCLOUDCONNECTION pfnTESTCLOUDCONNECTION = NULL;
		pfnTESTCLOUDCONNECTION = (TESTCLOUDCONNECTION)GetProcAddress(m_hArchiveFileStoreHandle, "TestCloudConnection");

		ARCHIVECLOUDDESTINFO archiveDestCloudInfo;
		memset(&archiveDestCloudInfo, 0, sizeof(ARCHIVECLOUDDESTINFO));
		JArchiveCloudConfig2ArchiveCloudDestInfo(env, &archiveDestCloudInfo, &jCloudDestInfo);

		  dwRet = pfnTESTCLOUDCONNECTION(&archiveDestCloudInfo, -1);
		  FreeArchiveCloudInfo(&archiveDestCloudInfo);
		  return (jlong)dwRet;
	  }

	//added by cliicy.luo
	jlong testBIConnection(JNIEnv *env, jclass clz, jobject jCloudDestInfo)
	{
		InitFileCopyLogging();
		DWORD dwRet = 0;
		if (m_hArchiveFileStoreHandle == NULL)
		{
			m_hArchiveFileStoreHandle = LoadLibrary(L"AFFileStore.dll");
		}
		TESTCLOUDCONNECTION pfnTESTCLOUDCONNECTION = NULL;
		pfnTESTCLOUDCONNECTION = (TESTCLOUDCONNECTION)GetProcAddress(m_hArchiveFileStoreHandle, "TestCloudConnection");

		ARCHIVECLOUDDESTINFO archiveDestCloudInfo;
		memset(&archiveDestCloudInfo, 0, sizeof(ARCHIVECLOUDDESTINFO));
		JArchiveCloudConfig2ArchiveCloudDestInfo(env, &archiveDestCloudInfo, &jCloudDestInfo);

		dwRet = pfnTESTCLOUDCONNECTION(&archiveDestCloudInfo, -1);
		FreeArchiveCloudInfo(&archiveDestCloudInfo);
		return (jlong)dwRet;
	}
	//added by cliicy.luo

	BOOL bNFConnectToCloud(JNIEnv *env, jclass clz, jobject jDestInfo, DWORD &dwCCIErrCode, HANDLE& handle, ICloudAPIs** cldAPIsPtr)
	{
		BOOL bRet = FALSE;
		InitFileCopyLogging();

		jclass class_JDestInfo = env->GetObjectClass(jDestInfo);
		jfieldID field_bArchiveToDrive = env->GetFieldID(class_JDestInfo, "bArchiveToDrive", "Z");
		BOOL bUseDisk = env->GetBooleanField(jDestInfo, field_bArchiveToDrive);

		jfieldID field_bArchiveToCloud = env->GetFieldID(class_JDestInfo, "bArchiveToCloud", "Z");
		BOOL bUseCloud = env->GetBooleanField(jDestInfo, field_bArchiveToCloud);

		DWORD dwDestType = -1;
		ARCHIVEDISKDESTINFO archiveDestDiskInfo;
		memset(&archiveDestDiskInfo, 0, sizeof(ARCHIVEDISKDESTINFO));
		if (bUseDisk)
		{
			dwDestType = 4;
			LogFile(L"selected disk");
			JArchiveDestConfig2ArchiveDiskDestInfo(env, &archiveDestDiskInfo, &jDestInfo);
		}

		ARCHIVECLOUDDESTINFO archiveDestCloudInfo;
		memset(&archiveDestCloudInfo, 0, sizeof(ARCHIVECLOUDDESTINFO));
		if (bUseCloud)
		{
			LogFile(L"selected cloud");
			//dwDestType = 0;

			JArchiveDestConfig2ArchiveCloudDestInfo(env, &archiveDestCloudInfo, &jDestInfo);
			dwDestType = archiveDestCloudInfo.dwCloudVendorType;
		}

		if (m_hArchiveFileStoreHandle == NULL)
		{
			m_hArchiveFileStoreHandle = LoadLibrary(L"AFFileStore.dll");
		}

		PFCreateCldAPIsPtr pfCreateCldAPIsPtr = NULL;
		pfCreateCldAPIsPtr = (PFCreateCldAPIsPtr)GetProcAddress(m_hArchiveFileStoreHandle, "CreateCldAPIsPtr");
		if (NULL == pfCreateCldAPIsPtr)
		{
			LogMsg(LL_ERR, GetLastError(), L"Failed to get CreateCldAPIsPtr proc address. WinErrorCode: %u", GetLastError());
			return FALSE;
		}
		*cldAPIsPtr = (*pfCreateCldAPIsPtr)();
		if (NULL == *cldAPIsPtr)
		{
			LogMsg(LL_ERR, 1, L"Failed to get CreateCldAPIsPtr pointer");
			return FALSE;
		}

		if (m_hArchiveFileStoreHandle != NULL)
		{
			PAFARCHIVEJOBSCRIPT pAFJobScript = (PAFARCHIVEJOBSCRIPT)malloc(sizeof(AFARCHIVEJOBSCRIPT));
			if (!pAFJobScript)
			{
				LogMsg(LL_ERR, 1, L"Failed to allocate memory AFARCHIVEJOBSCRIPT. SizeReq: %u", sizeof(AFARCHIVEJOBSCRIPT));
				return FALSE;
			}

			// PKTC : also pass the correct hostname for this scenario - here the hostname will depend on the destination selected
			// for disk/nwshare - it will be picked from the path and for cloud it will be picked from the bucket
			pAFJobScript->dwDestType = dwDestType;
			pAFJobScript->pCloudDest = &archiveDestCloudInfo;
			pAFJobScript->pDiskDest = &archiveDestDiskInfo;
			pAFJobScript->ulShrMemID = 0;						// init the jobnumber to 0

			// if it is disk or n/w share then call the impersonate code - the macro finds out if it is a n/w share
			if (bUseDisk)
			{
				DWORD dwRet = 0;

				NET_CONN_INFO ConnectionInfo;
				ZeroMemory(&ConnectionInfo, sizeof(NET_CONN_INFO));
				  if (pAFJobScript->pDiskDest->pwszUserName) { _tcscpy_s(ConnectionInfo.szUsr, pAFJobScript->pDiskDest->pwszUserName); }
				  if (pAFJobScript->pDiskDest->pwszPassword) { _tcscpy_s(ConnectionInfo.szPwd, pAFJobScript->pDiskDest->pwszPassword); }
				  if (pAFJobScript->pDiskDest->pwszDestpath) { _tcscpy_s(ConnectionInfo.szDir, pAFJobScript->pDiskDest->pwszDestpath); }

				DO_IMPERSONATE(dwRet, ConnectionInfo)
			}

			//INITCLOUDSESSIONEX  pfnInitCloudSession = NULL;
			//pfnInitCloudSession = (INITCLOUDSESSIONEX)GetProcAddress(m_hArchiveFileStoreHandle, "InitCloudSessionEx");

			//if (pfnInitCloudSession!=NULL){
			//	bRet = pfnInitCloudSession (pAFJobScript, dwCCIErrCode);

			bRet = (*cldAPIsPtr)->InitCloudSessionEx(pAFJobScript, dwCCIErrCode, handle, false);
			// HERE FREE UP THE MEMORY
			free(pAFJobScript);
		}
		return bRet;
	}

	jlong JNICALL GetLastArchiveCatalogUpdateTime(JNIEnv *env, jclass clz, jobject jDestInfo, jobject out_CatalogDetails)
	{
		DWORD dwError = 0;
		BOOL bInitCloudSession;
		FILETIME stLastUpdateTimeStamp = { 0 };
		wstring wsHostName = L"";
		DWORD dwStatus = 1;
		DWORD dwCCIErrCode = 0;

		ICloudAPIs* cldAPIsPtr;
		HANDLE handle;
		InitFileCopyLogging();
		if (m_hArchiveFileStoreHandle == NULL)
		{
			m_hArchiveFileStoreHandle = LoadLibrary(L"AFFileStore.dll");
		}
		
		bInitCloudSession = bNFConnectToCloud(env, clz, jDestInfo, dwCCIErrCode, handle, &cldAPIsPtr);
		if (bInitCloudSession)
		{
			do
			{
				wstring destGUID;
				BOOL bRet = cldAPIsPtr->GetGUIDFromArchiveDest(handle, destGUID);
				if (bRet == FALSE)
				{
					LogMsg(LL_ERR, 0, L"Failed to get FileCopy\FileArchive Destination signature file");
					dwCCIErrCode = GL_ERROR_INVALID_DESTINATION_PATH;
					break;
				}

				wstring tmpPath = L"";
				bRet = cldAPIsPtr->GetLastArchiveCatalogUpdateTime(handle, tmpPath, wsHostName, stLastUpdateTimeStamp, dwStatus);
				if (bRet == FALSE)
				{
					LogMsg(LL_ERR, 1, L"Failed to get last FileCopy Catalog update time. StatusCode: %u", dwStatus);
					dwCCIErrCode = GL_ERROR_FILE_NOT_FOUND;
					break;
				}
			} while (0);

			FillCatalogLastUpdateTime2JObject(env, clz, wsHostName, stLastUpdateTimeStamp, dwStatus, out_CatalogDetails);
			cldAPIsPtr->CloseCloudSession(handle);
			cldAPIsPtr->Release(handle);

			RETWITHREVERT()
		}
		else
		{
			LogMsg(LL_ERR, 1, L"Failed to connect to FileCopy\FileArchive destinaton. CCIErrorCode: %u", dwCCIErrCode);
			if (dwCCIErrCode == 0)  dwCCIErrCode = GL_ERROR_INTERNAL_ERROR; //Error 15 is provider error
			return dwCCIErrCode;
		}
		

		return dwCCIErrCode;
	}

	jstring JNICALL GetFileCopyCatalogPathy(JNIEnv *env, jclass, jstring MachineName, jlong ProductType)

	{
		DWORD dwError = ERROR_SUCCESS;
		jstring jszCatalogPath = NULL;

		if (m_hArchiveArchiverDLLHandler == NULL)
		{
			m_hArchiveArchiverDLLHandler = LoadLibrary(L"AFArchiver.dll");
		}

		// if either of the DLLs are not available then return
		if (m_hArchiveArchiverDLLHandler == NULL)
		{
			return jszCatalogPath;
		}

		GETFILECOPYCATALOGPATH  pfnGetfilecopycatalogpath = NULL;
		pfnGetfilecopycatalogpath = (GETFILECOPYCATALOGPATH)GetProcAddress(m_hArchiveArchiverDLLHandler, "GetFileCopyCatalogPath");
		if (pfnGetfilecopycatalogpath == NULL)
		{
			return  jszCatalogPath;
		}

		wstring machineName = JStringToWString(env, MachineName);

		PTCHAR szCatalogPath = new TCHAR[MAX_PATH * 2];

		if (szCatalogPath)
		{
			dwError = pfnGetfilecopycatalogpath((PTCHAR)machineName.c_str(), ProductType, szCatalogPath);
			if (dwError == ERROR_SUCCESS)
			{
				jszCatalogPath = WCHARToJString(env, szCatalogPath);
			}
			delete[] szCatalogPath;
		}

		return jszCatalogPath;
	}

	jlong JNICALL GetLastArchiveCatalogUpdateTime2(JNIEnv *env, jclass clz, jstring catalogDirBasePath, jobject jDestInfo, jobject out_CatalogDetails)
	{
		DWORD dwError = 0;
		BOOL bInitCloudSession;
		FILETIME stLastUpdateTimeStamp = { 0 };
		wstring wsHostName = L"";
		DWORD dwStatus = 1;
		DWORD dwCCIErrCode = 0;

		wstring pCatalogBaseDirPath = JStringToWCHAR(env, catalogDirBasePath);

		ICloudAPIs* cldAPIsPtr;
		HANDLE handle;
		InitFileCopyLogging();
		if (m_hArchiveFileStoreHandle == NULL)
		{
			m_hArchiveFileStoreHandle = LoadLibrary(L"AFFileStore.dll");
		}

		bInitCloudSession = bNFConnectToCloud(env, clz, jDestInfo, dwCCIErrCode, handle, &cldAPIsPtr);
		if (bInitCloudSession)
		{
			do
			{
				wstring destGUID;
				BOOL bRet = cldAPIsPtr->GetGUIDFromArchiveDest(handle, destGUID);
				if (bRet == FALSE)
				{
					LogMsg(LL_ERR, 0, L"Failed to get FileCopy\FileArchive Destination signature file");
					dwCCIErrCode = GL_ERROR_INVALID_DESTINATION_PATH;
					break;
				}

				wstring destHostName;
				cldAPIsPtr->GetCCIHostName(handle, destHostName);

				bRet = cldAPIsPtr->GetLastArchiveCatalogUpdateTime(handle, pCatalogBaseDirPath, destHostName, stLastUpdateTimeStamp, dwStatus);
				if (bRet == FALSE)
				{
					LogMsg(LL_ERR, 1, L"Failed to get last FileCopy Catalog update time. StatusCode: %u", dwStatus);
					dwCCIErrCode = GL_ERROR_FILE_NOT_FOUND;
					break;
				}
			} while (0);

			FillCatalogLastUpdateTime2JObject(env, clz, wsHostName, stLastUpdateTimeStamp, dwStatus, out_CatalogDetails);
			cldAPIsPtr->CloseCloudSession(handle);
			cldAPIsPtr->Release(handle);

			RETWITHREVERT()
		}
		else
		{
			LogMsg(LL_ERR, 1, L"Failed to connect to FileCopy\FileArchive destinaton. CCIErrorCode: %u", dwCCIErrCode);
			if (dwCCIErrCode == 0)  dwCCIErrCode = GL_ERROR_INTERNAL_ERROR; //Error 15 is provider error
			return dwCCIErrCode;
		}
		return dwCCIErrCode;
	}

	jlong JNICALL searchArchiveCatalogChildren2(JNIEnv *env, jclass clz, jstring catalogDirBasePath, jstring inFileName, jstring inHostName, jstring inSearchpath, jobject inArchiveDestConfig,
		jlong in_lSearchOptions, jlong in_lIndex, jlong in_lCount, jobject archiveCatalogItems)
	{
		//while (1)
		//{
		//	Sleep(100);
		//}
		DWORD dwRet = 0;
		InitFileCopyLogging();
		CDbgLog logObj(L"NativeFacade");

		DWORD tmpRet = 0;
		DO_IMPERSONATE_2(tmpRet);
		//////////////Convert Java variables to C++ variables

		jclass list_class = env->GetObjectClass(archiveCatalogItems);
		jmethodID addMethod = env->GetMethodID(list_class, "add", "(Ljava/lang/Object;)Z");

		wstring pCatalogBaseDirPath = JStringToWCHAR(env, catalogDirBasePath);
		wchar_t* szFileName = JStringToWCHAR(env, inFileName);
		//wchar_t* szHostName = JStringToWCHAR(env, inHostName);
		wchar_t* szSearchPath = JStringToWCHAR(env, inSearchpath);

		jclass class_JDestInfo = env->GetObjectClass(inArchiveDestConfig);
		jfieldID field_bArchiveToDrive = env->GetFieldID(class_JDestInfo, "bArchiveToDrive", "Z");
		BOOL bUseDisk = env->GetBooleanField(inArchiveDestConfig, field_bArchiveToDrive);

		jfieldID field_bArchiveToCloud = env->GetFieldID(class_JDestInfo, "bArchiveToCloud", "Z");
		BOOL bUseCloud = env->GetBooleanField(inArchiveDestConfig, field_bArchiveToCloud);

		DWORD dwDestType = -1;
		ARCHIVEDISKDESTINFO archiveDestDiskInfo;
		if (bUseDisk)
		{
			dwDestType = 4;
			memset(&archiveDestDiskInfo, 0, sizeof(ARCHIVEDISKDESTINFO));
			JArchiveDestConfig2ArchiveDiskDestInfo(env, &archiveDestDiskInfo, &inArchiveDestConfig);
		}

		ARCHIVECLOUDDESTINFO archiveDestCloudInfo;
		if (bUseCloud)
		{
			//dwDestType = 0;
			memset(&archiveDestCloudInfo, 0, sizeof(ARCHIVECLOUDDESTINFO));
			JArchiveDestConfig2ArchiveCloudDestInfo(env, &archiveDestCloudInfo, &inArchiveDestConfig);
			dwDestType = archiveDestCloudInfo.dwCloudVendorType;
		}

		////////////////////////////////////////////////////////////////////

		//////////////Load Archive libraries
		IFileCopyCatalogMgr* fileCopyCatalogMgr = nullptr;
		dwRet = CreateIFileCopyCatalogMgrObject(&logObj, &fileCopyCatalogMgr);
		if ((dwRet != 0) || (fileCopyCatalogMgr == nullptr)) return dwRet;

		ICloudAPIs* cldAPIsPtr = nullptr;
		dwRet = CreateICloudAPIsObject(&logObj, &cldAPIsPtr);
		if ((dwRet != 0) || (cldAPIsPtr == nullptr)) return dwRet;
		////////////////////////////////////////////////////////////////////////////////

		if (m_hArchiveFileStoreHandle != NULL)
		{
			PAFARCHIVEJOBSCRIPT pAFJobScript = nullptr;
			dwRet = PrepareAFArchiveJS(dwDestType, &archiveDestDiskInfo, &archiveDestCloudInfo, &pAFJobScript);
			if (dwRet != 0)
			{
				logObj.LogW(LL_ERR, dwRet, L"%S: Failed to create ArchiveJS", __FUNCTION__);
				return dwRet;
			}

			BOOL bInitCloudSuccess = FALSE;
			DWORD dwCCIErrCode = 0;

			HANDLE cldSessHandle;
			bInitCloudSuccess = cldAPIsPtr->InitCloudSessionEx(pAFJobScript, dwCCIErrCode, cldSessHandle, false);

			// HERE FREE UP THE MEMORY
			free(pAFJobScript);

			if (bInitCloudSuccess)
			{
				wstring destGUID;
				BOOL bRet = cldAPIsPtr->GetGUIDFromArchiveDest(cldSessHandle, destGUID);
				if (bRet == FALSE)
				{
					logObj.LogW(LL_ERR, 0, L"Failed to get dest GUID");
					return -1;
				}

				wstring destHostName;
				cldAPIsPtr->GetCCIHostName(cldSessHandle, destHostName);
				if (destHostName.empty())
				{
					logObj.LogW(LL_ERR, 0, L"Failed to get dest Hostname");
					return -1;
				}

				logObj.LogW(LL_DBG, 0, L"%S: HostName: %s GUID %s", __FUNCTION__, destHostName.c_str(), destGUID.c_str());

				//DWORD dwRet = 0;
				//wstring catalogDirBasePath = L"";
				vector <pCatalogChildItem> vecCatalogList;
				dwRet = fileCopyCatalogMgr->SearchChildItems(pCatalogBaseDirPath, (PTCHAR)destHostName.c_str(), destGUID, szFileName, szSearchPath, in_lSearchOptions, in_lIndex, in_lCount, vecCatalogList);
				if (dwRet != 0)
				{
					logObj.LogW(LL_ERR, dwRet, L"Failed to search children. CatalogBasePath: %s, HostName: %s destGUID: %s szFileName: %s szSearchPath: %s SearchOptions: %u Index: %u Count: %u",
						catalogDirBasePath, destHostName.c_str(), destGUID, szFileName, szSearchPath, in_lSearchOptions, in_lIndex, in_lCount);
					return -1;
				}
				convertCVersionToJniVersion(env, clz, catalogDirBasePath, vecCatalogList, archiveCatalogItems);

				fileCopyCatalogMgr->FreeCatalogChildItems(vecCatalogList);

				cldAPIsPtr->CloseCloudSession(cldSessHandle);
				cldAPIsPtr->Release(cldSessHandle);
			}

			//if (bUseDisk)
			{
				RETWITHREVERT_2()
			}
		}
		if (szFileName != NULL)
			free(szFileName);

		//if (szHostName != NULL)
		//	free(szHostName);

		if (szSearchPath != NULL)
			free(szSearchPath);

		//delete disk info
		if (bUseDisk)
		{
			//FreeArchiveDiskInfo(&archiveDestDiskInfo);
			if (archiveDestDiskInfo.pwszDestpath != NULL)
				free(archiveDestDiskInfo.pwszDestpath);
			if (archiveDestDiskInfo.pwszUserName != NULL)
				free(archiveDestDiskInfo.pwszUserName);
			if (archiveDestDiskInfo.pwszPassword != NULL)
				free(archiveDestDiskInfo.pwszPassword);
		}

		if (bUseCloud)
		{
			//FreeArchiveCloudInfo(&archiveDestCloudInfo);
			if (archiveDestCloudInfo.pwszCloudBucketName != NULL)
				free(archiveDestCloudInfo.pwszCloudBucketName);
			if (archiveDestCloudInfo.pwszCloudDisplayBucketName != NULL)
				free(archiveDestCloudInfo.pwszCloudDisplayBucketName);
			if (archiveDestCloudInfo.pwszCloudVendorURL != NULL)
				free(archiveDestCloudInfo.pwszCloudVendorURL);
			if (archiveDestCloudInfo.pwszProxyPassword != NULL)
				free(archiveDestCloudInfo.pwszProxyPassword);
			if (archiveDestCloudInfo.pwszProxyServerName != NULL)
				free(archiveDestCloudInfo.pwszProxyServerName);
			if (archiveDestCloudInfo.pwszproxyUserName != NULL)
				free(archiveDestCloudInfo.pwszproxyUserName);
			if (archiveDestCloudInfo.pwszVendorHostname != NULL)
				free(archiveDestCloudInfo.pwszVendorHostname);
			if (archiveDestCloudInfo.pwszVendorPassword != NULL)
				free(archiveDestCloudInfo.pwszVendorPassword);
			if (archiveDestCloudInfo.pwszVendorUsername != NULL)
				free(archiveDestCloudInfo.pwszVendorUsername);
		}
		return dwRet;
	}

	jlong JNICALL ArchiveOpenMachine(JNIEnv *env, jclass clz, jstring catalogDirBasePath, jstring catalogDirUserName, jstring catalogDirPassword, jstring hostName, jobject jDestInfo, jobject pMachineHandle)
	{
		DWORD dwRet = 0;
		InitFileCopyLogging();
		CDbgLog logObj(L"NativeFacade");

		wstring pCatalogBaseDirPath = JStringToWCHAR(env, catalogDirBasePath);
		//wchar_t* pHostName = JStringToWCHAR(env, hostName);

		wstring pCatalogDirUserName = JStringToWCHAR(env, catalogDirUserName);
		wstring pCatalogDirPassword = JStringToWCHAR(env, catalogDirPassword);

		jclass class_JDestInfo = env->GetObjectClass(jDestInfo);
		jfieldID field_bArchiveToDrive = env->GetFieldID(class_JDestInfo, "bArchiveToDrive", "Z");
		BOOL bUseDisk = env->GetBooleanField(jDestInfo, field_bArchiveToDrive);

		jfieldID field_bArchiveToCloud = env->GetFieldID(class_JDestInfo, "bArchiveToCloud", "Z");
		BOOL bUseCloud = env->GetBooleanField(jDestInfo, field_bArchiveToCloud);

		DWORD dwDestType = -1;
		ARCHIVEDISKDESTINFO archiveDestDiskInfo;
		memset(&archiveDestDiskInfo, 0, sizeof(ARCHIVEDISKDESTINFO));
		if (bUseDisk)
		{
			LogFile(L"selected disk");
			JArchiveDestConfig2ArchiveDiskDestInfo(env, &archiveDestDiskInfo, &jDestInfo);
			dwDestType = 4;
		}

		ARCHIVECLOUDDESTINFO archiveDestCloudInfo;
		memset(&archiveDestCloudInfo, 0, sizeof(ARCHIVECLOUDDESTINFO));
		if (bUseCloud)
		{
			LogFile(L"selected cloud");
			JArchiveDestConfig2ArchiveCloudDestInfo(env, &archiveDestCloudInfo, &jDestInfo);
			dwDestType = archiveDestCloudInfo.dwCloudVendorType;
		}

		//////////////Load Archive libraries///////////////////////////////////////////////
		IFileCopyCatalogMgr* fileCopyCatalogMgr = nullptr;
		dwRet = CreateIFileCopyCatalogMgrObject(&logObj, &fileCopyCatalogMgr);
		if ((dwRet != 0) || (fileCopyCatalogMgr == nullptr)) return dwRet;

		ICloudAPIs* cldAPIsPtr = nullptr;
		dwRet = CreateICloudAPIsObject(&logObj, &cldAPIsPtr);
		if ((dwRet != 0) || (cldAPIsPtr == nullptr)) return dwRet;
		////////////////////////////////////////////////////////////////////////////////

		//////////////////Prepare Archive JS/////////////////////////////
		PAFARCHIVEJOBSCRIPT pAFJobScript = nullptr;
		dwRet = PrepareAFArchiveJS(dwDestType, &archiveDestDiskInfo, &archiveDestCloudInfo, &pAFJobScript);
		if (dwRet != 0)
		{
			logObj.LogW(LL_ERR, dwRet, L"%S: Failed to create ArchiveJS", __FUNCTION__);
			return dwRet;
		}
		////////////////////////////////////////////////////////////////////////////////

		BOOL bInitCloudSuccess = FALSE;
		DWORD dwCCIErrCode = 0;

		HANDLE cldSessHandle;
		bInitCloudSuccess = cldAPIsPtr->InitCloudSessionEx(pAFJobScript, dwCCIErrCode, cldSessHandle, false);

		// HERE FREE UP THE MEMORY
		free(pAFJobScript);

		if (bInitCloudSuccess)
		{
			wstring destGUID;
			BOOL bRet = cldAPIsPtr->GetGUIDFromArchiveDest(cldSessHandle, destGUID);
			if (bRet == FALSE)
			{
				logObj.LogW(LL_ERR, 0, L"Failed to get dest GUID");
				return -1;
			}

			wstring destHostName;
			cldAPIsPtr->GetCCIHostName(cldSessHandle, destHostName);
			if (destHostName.empty())
			{
				logObj.LogW(LL_ERR, 0, L"Failed to get dest Hostname");
				return -1;
			}

			logObj.LogW(LL_DBG, 0, L"%S: HostName: %s GUID %s", __FUNCTION__, destHostName.c_str(), destGUID.c_str());

			//DWORD dwRet = 0;
			wstring catalogDirBasePath = pCatalogBaseDirPath;
			wstring catalogDirUserName = pCatalogDirUserName;
			wstring catalogDirPassword = pCatalogDirPassword;

			HANDLE machineCatalogHandle;
			dwRet = fileCopyCatalogMgr->OpenMachine(catalogDirBasePath, catalogDirUserName, catalogDirPassword, (PTCHAR)destHostName.c_str(), destGUID, machineCatalogHandle);
			if (dwRet != 0)
			{
				logObj.LogW(LL_ERR, dwRet, L"Failed to open machine. CatalogBasePath: %s, HostName: %s destGUID: %s ",
					catalogDirBasePath, destHostName.c_str(), destGUID);
				return -1;
			}

			UINT64TOJRWLong(env, (UINT64)machineCatalogHandle, pMachineHandle);

			cldAPIsPtr->CloseCloudSession(cldSessHandle);
			cldAPIsPtr->Release(cldSessHandle);
		}

		/////////////////////////////////Release resources
		if (bUseDisk)
		{
			RETWITHREVERT()
		}

		//if (pHostName != NULL)
		//	free(pHostName);

		//delete disk info
		if (bUseDisk)
		{
			//FreeArchiveDiskInfo(&archiveDestDiskInfo);
			if (archiveDestDiskInfo.pwszDestpath != NULL)
				free(archiveDestDiskInfo.pwszDestpath);
			if (archiveDestDiskInfo.pwszUserName != NULL)
				free(archiveDestDiskInfo.pwszUserName);
			if (archiveDestDiskInfo.pwszPassword != NULL)
				free(archiveDestDiskInfo.pwszPassword);
		}

		if (bUseCloud)
		{
			//FreeArchiveCloudInfo(&archiveDestCloudInfo);
			if (archiveDestCloudInfo.pwszCloudBucketName != NULL)
				free(archiveDestCloudInfo.pwszCloudBucketName);
			if (archiveDestCloudInfo.pwszCloudDisplayBucketName != NULL)
				free(archiveDestCloudInfo.pwszCloudDisplayBucketName);
			if (archiveDestCloudInfo.pwszCloudVendorURL != NULL)
				free(archiveDestCloudInfo.pwszCloudVendorURL);
			if (archiveDestCloudInfo.pwszProxyPassword != NULL)
				free(archiveDestCloudInfo.pwszProxyPassword);
			if (archiveDestCloudInfo.pwszProxyServerName != NULL)
				free(archiveDestCloudInfo.pwszProxyServerName);
			if (archiveDestCloudInfo.pwszproxyUserName != NULL)
				free(archiveDestCloudInfo.pwszproxyUserName);
			if (archiveDestCloudInfo.pwszVendorHostname != NULL)
				free(archiveDestCloudInfo.pwszVendorHostname);
			if (archiveDestCloudInfo.pwszVendorPassword != NULL)
				free(archiveDestCloudInfo.pwszVendorPassword);
			if (archiveDestCloudInfo.pwszVendorUsername != NULL)
				free(archiveDestCloudInfo.pwszVendorUsername);
		}
		return dwRet;
	}

	jlong JNICALL GetArchiveVolumeList(JNIEnv *env, jclass clz, jlong pMachineHandle, jobject volumeList)
	{
		DWORD dwRet = 0;
		DWORD tmpRet = 0;
		CDbgLog logObj(L"NativeFacade");

		jclass list_class = env->GetObjectClass(volumeList);
		jmethodID addMethod = env->GetMethodID(list_class, "add", "(Ljava/lang/Object;)Z");

		DO_IMPERSONATE_2(tmpRet)

		IFileCopyCatalogMgr* fileCopyCatalogMgr = nullptr;
		dwRet = CreateIFileCopyCatalogMgrObject(&logObj, &fileCopyCatalogMgr);
		if ((dwRet != 0) || (fileCopyCatalogMgr == nullptr)) return dwRet;

		std::vector<PTCHAR> vVolumesList;
		dwRet = fileCopyCatalogMgr->GetVolumeList((HANDLE)pMachineHandle, vVolumesList);
		if (dwRet != 0)
		{
			logObj.LogW(LL_ERR, dwRet, L"Failed to get ArchiveVolumeList");
			return dwRet;
		}

		for (vector<PTCHAR>::iterator volume = vVolumesList.begin(); volume != vVolumesList.end(); volume++)
		{
			jstring volume_JStr = WCHARToJString(env, (wchar_t*)(*volume));
			env->CallBooleanMethod(volumeList, addMethod, volume_JStr);
			if (volume_JStr != NULL)
				env->DeleteLocalRef(volume_JStr);
		}

		fileCopyCatalogMgr->FreeVolumeListItems(vVolumesList);
		
		RETWITHREVERT_2(tmpRet)

		return dwRet;
	}

	jlong JNICALL ArchiveOpenVolume(JNIEnv *env, jclass clz, jlong pMachineHandle, jstring strVolume)
	{
		DWORD tmpRet = 0;
		HANDLE hArchiveVolumeHandle = NULL;
		pfnOpenFileCatalogFile OpenArchiveFileCatalog = NULL;
		CDbgLog logObj(L"NativeFacade");

		DO_IMPERSONATE_2(tmpRet)

		wchar_t* szArchiveVolume = JStringToWCHAR(env, strVolume);

		IFileCopyCatalogMgr* fileCopyCatalogMgr = nullptr;
		DWORD dwRet = CreateIFileCopyCatalogMgrObject(&logObj, &fileCopyCatalogMgr);
		if ((dwRet != 0) || (fileCopyCatalogMgr == nullptr)) return (jlong)INVALID_HANDLE_VALUE;

		dwRet = fileCopyCatalogMgr->OpenVolume((HANDLE)pMachineHandle, szArchiveVolume, hArchiveVolumeHandle);
		if ((dwRet != 0)) return (jlong)INVALID_HANDLE_VALUE;

		if (szArchiveVolume != NULL)
			free(szArchiveVolume);

		RETWITHREVERT_2(tmpRet)

		return (jlong)hArchiveVolumeHandle;
	}

	jlong JNICALL GetArchiveChildItemCount(JNIEnv *env, jclass clz, jlong pVolumeHandle, jstring strPath, jobject childCount)
	{
		DWORD tmpRet = 0;
		DWORD dwRet = 0;
		CDbgLog logObj(L"NativeFacade");

		DO_IMPERSONATE_2(tmpRet)

		wchar_t* szPath = JStringToWCHAR(env, strPath);

		IFileCopyCatalogMgr* fileCopyCatalogMgr = nullptr;
		dwRet = CreateIFileCopyCatalogMgrObject(&logObj, &fileCopyCatalogMgr);
		if ((dwRet != 0) || (fileCopyCatalogMgr == nullptr)) return dwRet;

		DWORD dwCount = 0;
		dwRet = fileCopyCatalogMgr->GetChildItemCount((HANDLE)pVolumeHandle, szPath, dwCount);
		if (dwRet != 0)
		{
			logObj.LogW(LL_ERR, dwRet, L"Failed to call GetChildItemCount");
			return dwRet;
		}

		if (szPath != NULL)
			free(szPath);

		DWORDTOJRWLong(env, dwCount, childCount);

		RETWITHREVERT_2(tmpRet)

		return dwRet;
	}

	jlong JNICALL GetArchiveChildItems(JNIEnv *env, jclass clz, jlong pVolumeHandle, jstring strPath, jobject archiveCatalogItems)
	{
		DWORD tmpRet = 0;
		DWORD dwRet = 0;
		CDbgLog logObj(L"NativeFacade");

		wchar_t* szPath = JStringToWCHAR(env, strPath);

		DO_IMPERSONATE_2(tmpRet)

		IFileCopyCatalogMgr* fileCopyCatalogMgr = nullptr;
		dwRet = CreateIFileCopyCatalogMgrObject(&logObj, &fileCopyCatalogMgr);
		if ((dwRet != 0) || (fileCopyCatalogMgr == nullptr)) return dwRet;

		vector <pCatalogChildItem> vecCatalogList;
		dwRet = fileCopyCatalogMgr->GetChildItems((HANDLE)pVolumeHandle, szPath, vecCatalogList);
		if (dwRet != 0)
		{
			logObj.LogW(LL_ERR, dwRet, L"Failed to call GetChildItems");
			return dwRet;
		}

		convertCVersionToJniVersion(env, clz, strPath, vecCatalogList, archiveCatalogItems);

		fileCopyCatalogMgr->FreeCatalogChildItems(vecCatalogList);

		if (szPath != NULL)
			free(szPath);

		RETWITHREVERT_2(tmpRet)

		return dwRet;
	}

	jlong JNICALL GetArchiveChildItemsEx(JNIEnv *env, jclass clz, jlong pVolumeHandle, jstring strPath, jlong in_lIndex, jlong in_lCount, jobject archiveCatalogItems)
	{
		DWORD tmpRet = 0;
		DWORD dwRet = 0;
		CDbgLog logObj(L"NativeFacade");

		DO_IMPERSONATE_2(tmpRet)

		wchar_t* szPath = JStringToWCHAR(env, strPath);

		IFileCopyCatalogMgr* fileCopyCatalogMgr = nullptr;
		dwRet = CreateIFileCopyCatalogMgrObject(&logObj, &fileCopyCatalogMgr);
		if ((dwRet != 0) || (fileCopyCatalogMgr == nullptr)) return dwRet;

		vector <pCatalogChildItem> vecCatalogList;
		dwRet = fileCopyCatalogMgr->GetChildItemsEx((HANDLE)pVolumeHandle, szPath, vecCatalogList, in_lIndex, in_lCount);
		if (dwRet != 0)
		{
			logObj.LogW(LL_ERR, dwRet, L"Failed to call GetChildItemsEx");
			return dwRet;
		}

		convertCVersionToJniVersion(env, clz, strPath, vecCatalogList, archiveCatalogItems);

		fileCopyCatalogMgr->FreeCatalogChildItems(vecCatalogList);

		if (szPath != NULL)
			free(szPath);

		RETWITHREVERT_2(tmpRet)

		return dwRet;
	}

	DWORD CreateIFileCopyCatalogMgrObject(CDbgLog* log, IFileCopyCatalogMgr** ptr)
	{
		DWORD dwRet = 0;
		if (m_hArchiveCatalogHandle == NULL)
		{
			m_hArchiveCatalogHandle = LoadLibrary(L"AFFileCatalog.dll");
			if (m_hArchiveCatalogHandle == NULL)
			{
				dwRet = GetLastError();
				log->LogW(LL_ERR, dwRet, L"Failed to load AFFileCatalogLibrary");
				return dwRet;
			}
		}

		PFCreateInstanceFileCopyCatalog pfreateInstanceFileCopyCatalog = (PFCreateInstanceFileCopyCatalog)GetProcAddress(m_hArchiveCatalogHandle, "CreateInstanceFileCopyCatalog");
		if (pfreateInstanceFileCopyCatalog == NULL)
		{
			dwRet = GetLastError();
			log->LogW(LL_ERR, dwRet, L"Failed to load AFFileCatalogLibrary procedure CreateInstanceFileCopyCatalog");
			return dwRet;
		}

		*ptr = (*pfreateInstanceFileCopyCatalog)();
		if (*ptr == NULL)
		{
			dwRet = 1;
			log->LogW(LL_ERR, dwRet, L"Failed to execute AFFileCatalogLibrary procedure CreateInstanceFileCopyCatalog");
			return dwRet;
		}

		return dwRet;
	}

	jlong JNICALL ArchiveCloseVolume(JNIEnv *env, jclass clz, jlong pVolumeHandle)
	{
		DWORD dwRet = 0;
		CDbgLog logObj(L"NativeFacade");

		IFileCopyCatalogMgr* fileCopyCatalogMgr = nullptr;
		dwRet = CreateIFileCopyCatalogMgrObject(&logObj, &fileCopyCatalogMgr);
		if ((dwRet != 0) || (fileCopyCatalogMgr == nullptr)) return dwRet;

		fileCopyCatalogMgr->CloseVolumeHandle((HANDLE)pVolumeHandle);

		return dwRet;
	}

	jlong JNICALL ArchiveCloseMachine(JNIEnv *env, jclass clz, jlong pMachineHandle)
	{
		DWORD dwRet = 0;
		CDbgLog logObj(L"NativeFacade");

		IFileCopyCatalogMgr* fileCopyCatalogMgr = nullptr;
		dwRet = CreateIFileCopyCatalogMgrObject(&logObj, &fileCopyCatalogMgr);
		if ((dwRet != 0) || (fileCopyCatalogMgr == nullptr)) return dwRet;

		fileCopyCatalogMgr->CloseHandle((HANDLE)pMachineHandle);

		return dwRet;
	}

	DWORD CreateICloudAPIsObject(CDbgLog* log, ICloudAPIs** ptr)
	{
		DWORD dwRet = 0;
		if (m_hArchiveFileStoreHandle == NULL)
		{
			m_hArchiveFileStoreHandle = LoadLibrary(L"AFFileStore.dll");
			if (m_hArchiveFileStoreHandle == NULL)
			{
				dwRet = GetLastError();
				log->LogW(LL_ERR, dwRet, L"Failed to load AFFileStore Library");
				return -1;
			}
		}

		PFCreateCldAPIsPtr pfCreateCldAPIsPtr = NULL;
		pfCreateCldAPIsPtr = (PFCreateCldAPIsPtr)GetProcAddress(m_hArchiveFileStoreHandle, "CreateCldAPIsPtr");

		if (NULL == pfCreateCldAPIsPtr)
		{
			dwRet = GetLastError();
			log->LogW(LL_ERR, dwRet, L"Failed to load AFFileStore Library procedure CreateCldAPIsPtr");
			return -1;
		}

		*ptr = (*pfCreateCldAPIsPtr)();
		if (nullptr == *ptr)
		{
			dwRet = 1;
			log->LogW(LL_ERR, dwRet, L"Failed to execute AFFileStore Library procedure CreateCldAPIsPtr");
			return -1;
		}

		return dwRet;
	}

	DWORD PrepareAFArchiveJS(DWORD dwDestType, ARCHIVEDISKDESTINFO* archiveDestDiskInfo, ARCHIVECLOUDDESTINFO* archiveDestCloudInfo, AFARCHIVEJOBSCRIPT** ppAFJobScript)
	{
		DWORD dwRet = 0;
		*ppAFJobScript = (PAFARCHIVEJOBSCRIPT)malloc(sizeof(AFARCHIVEJOBSCRIPT));
		if (!*ppAFJobScript)
		{
			// mem allocation failure
			return -1;
		}

		// PKTC : also pass the correct hostname for this scenario - here the hostname will depend on the destination selected
		// for disk/nwshare - it will be picked from the path and for cloud it will be picked from the bucket
		(*ppAFJobScript)->dwDestType = dwDestType;
		(*ppAFJobScript)->pCloudDest = archiveDestCloudInfo;
		(*ppAFJobScript)->pDiskDest = archiveDestDiskInfo;
		(*ppAFJobScript)->ulShrMemID = 0;						// init the jobnumber to 0

		// if it is disk or n/w share then call the impersonate code - the macro finds out if it is a n/w share
		if (dwDestType == 4)
		{
			NET_CONN_INFO ConnectionInfo;
			ZeroMemory(&ConnectionInfo, sizeof(NET_CONN_INFO));
			  if ((*ppAFJobScript)->pDiskDest->pwszUserName) { _tcscpy_s(ConnectionInfo.szUsr, (*ppAFJobScript)->pDiskDest->pwszUserName); }
			  if ((*ppAFJobScript)->pDiskDest->pwszPassword) { _tcscpy_s(ConnectionInfo.szPwd, (*ppAFJobScript)->pDiskDest->pwszPassword); }
			  if ((*ppAFJobScript)->pDiskDest->pwszDestpath) { _tcscpy_s(ConnectionInfo.szDir, (*ppAFJobScript)->pDiskDest->pwszDestpath); }

			DO_IMPERSONATE(dwRet, ConnectionInfo)
		}

		return dwRet;
	}

	int convertCVersionToJniVersion(JNIEnv *env, jclass clz, jstring in_jCatPath, vector <pCatalogChildItem> &CatalogList, jobject & archiveCatalogItems)
	{
		jclass list_class = env->GetObjectClass(archiveCatalogItems);
		jmethodID addMethod = env->GetMethodID(list_class, "add", "(Ljava/lang/Object;)Z");

		jclass m_jclsJArchiveCatalogDetail;
#ifdef _RPSNF_INCLUDE_
		m_jclsJArchiveCatalogDetail = env->FindClass("com/ca/arcflash/rps/jni/model/JArchiveCatalogDetail");
#else
		m_jclsJArchiveCatalogDetail = env->FindClass("com/ca/arcflash/webservice/jni/model/JArchiveCatalogDetail");
#endif
		jclass arrayListClass = env->FindClass("java/util/ArrayList");
		jmethodID arrayListConstr = env->GetMethodID(arrayListClass, "<init>", "()V");
		jmethodID listAddMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");

		//3. Create JArchiveFileVersionDetail object
		jclass m_jclsJArchiveFileVersionDetail;
#ifdef _RPSNF_INCLUDE_
		m_jclsJArchiveFileVersionDetail = env->FindClass("com/ca/arcflash/rps/jni/model/JArchiveFileVersionDetail");
#else
		m_jclsJArchiveFileVersionDetail = env->FindClass("com/ca/arcflash/webservice/jni/model/JArchiveFileVersionDetail");
#endif
		jmethodID jJArchiveFileVersionDetailConstructor = env->GetMethodID(m_jclsJArchiveFileVersionDetail,
			"<init>", "()V");
		jmethodID midsetVersion = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setVersion", "(Ljava/lang/String;)V");
		jmethodID midsetFileSizeLow = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setFileSizeLow", "(J)V");
		jmethodID midsetFileSizeHigh = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setFileSizeHigh", "(J)V");

		jmethodID midsetmodifiedHour = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setmodifiedHour", "(J)V");
		jmethodID midsetmodifiedMin = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setmodifiedMin", "(J)V");
		jmethodID midsetmodifiedSec = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setmodifiedSec", "(J)V");
		jmethodID midsetmodifiedDay = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setmodifiedDay", "(J)V");
		jmethodID midsetmodifiedMonth = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setmodifiedMonth", "(J)V");
		jmethodID midsetmodifiedYear = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setmodifiedYear", "(J)V");

		jmethodID midsetarchivedHour = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setarchivedHour", "(J)V");
		jmethodID midsetarchivedMin = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setarchivedMin", "(J)V");
		jmethodID midsetarchivedSec = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setarchivedSec", "(J)V");
		jmethodID midsetarchivedDay = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setarchivedDay", "(J)V");
		jmethodID midsetarchivedMonth = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setarchivedMonth", "(J)V");
		jmethodID midsetarchivedYear = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setarchivedYear", "(J)V");
		jmethodID midsetModifiedTime = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setmodDateTime", "(J)V");
		jmethodID midsetArchivedDateTime = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setarchiveDateTime", "(J)V");

		jmethodID midsetFileType = env->GetMethodID(m_jclsJArchiveFileVersionDetail, "setFileType", "(I)V");

		jmethodID midsetPath = env->GetMethodID(m_jclsJArchiveCatalogDetail, "setPath", "(Ljava/lang/String;)V");
		jmethodID midsetType = env->GetMethodID(m_jclsJArchiveCatalogDetail, "setType", "(I)V");
		jmethodID midsetName = env->GetMethodID(m_jclsJArchiveCatalogDetail, "setName", "(Ljava/lang/String;)V");
		jmethodID midsetsetFullPath = env->GetMethodID(m_jclsJArchiveCatalogDetail, "setFullPath", "(Ljava/lang/String;)V");

		jmethodID midsetsetVersionsCount = env->GetMethodID(m_jclsJArchiveCatalogDetail, "setVersionsCount", "(Ljava/lang/String;)V");
		jmethodID midsetFileVersionsList = env->GetMethodID(m_jclsJArchiveCatalogDetail, "setFileVersionsList", "(Ljava/util/List;)V");

		if (CatalogList.size() > 0)
		{
			//1. Create JArchiveCatalogDetail object
			for (vector<pCatalogChildItem>::iterator itrCatalogList = CatalogList.begin(); itrCatalogList != CatalogList.end(); itrCatalogList++)
			{
				jmethodID jJArchiveCatalogDetailConstructor = env->GetMethodID(m_jclsJArchiveCatalogDetail,
					"<init>", "()V");
				jobject objArchiveCatalogDetail = env->NewObject(m_jclsJArchiveCatalogDetail, jJArchiveCatalogDetailConstructor);
				env->CallNonvirtualVoidMethod(objArchiveCatalogDetail, m_jclsJArchiveCatalogDetail,
					jJArchiveCatalogDetailConstructor);
				//2. Update the  JArchiveCatalogDetail with its native fields

				jstring jName = WCHARToJString(env, (wchar_t*)((*itrCatalogList)->szName));
				int type = 7;//File
				if ((*itrCatalogList)->dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
				{
					type = 6;
				}
				jstring jCatPath = WCHARToJString(env, (wchar_t*)((*itrCatalogList)->szFullPath));

				env->CallVoidMethod(objArchiveCatalogDetail, midsetPath, jCatPath);

				env->CallVoidMethod(objArchiveCatalogDetail, midsetType, type);

				env->CallVoidMethod(objArchiveCatalogDetail, midsetName, jName);

				env->CallVoidMethod(objArchiveCatalogDetail, midsetsetFullPath, jCatPath);

				if (type == 7)
				{
					DWORD dwVersion;
					dwVersion = (*itrCatalogList)->dwVersionCount;
					TCHAR szVersionCount[20];
					_ltot(dwVersion, szVersionCount, 10);
					jstring jVersionCount = WCHARToJString(env, (wchar_t*)szVersionCount);
					env->CallVoidMethod(objArchiveCatalogDetail, midsetsetVersionsCount, jVersionCount);

					pFileVersionInfo pArchiveFileVersionDetailList = (*itrCatalogList)->versions;
					jobject jObjFileVesrionsList = env->NewObject(arrayListClass, arrayListConstr);
					for (int iVersionIndex = 0; iVersionIndex < (*itrCatalogList)->dwVersionCount; iVersionIndex++)
					{
						jobject objJArchiveFileVersionDetail = env->NewObject(m_jclsJArchiveFileVersionDetail, jJArchiveFileVersionDetailConstructor);
						env->CallNonvirtualVoidMethod(objJArchiveFileVersionDetail, m_jclsJArchiveFileVersionDetail,
							jJArchiveFileVersionDetailConstructor);

						//4. Update the JArchiveFileVersionDetail object with its native fields

						//version
						TCHAR szVersion[10] = { _T('\0') };
						_itot(iVersionIndex + 1, szVersion, 10);
						//_itot(pArchiveFileVersionDetailList[iVersionIndex].m_dwDataFileVersion,szVersion,10);
						jstring jVersion = WCHARToJString(env, (wchar_t*)szVersion);
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetVersion, jVersion);

						//file size
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetFileSizeLow, (jlong)pArchiveFileVersionDetailList[iVersionIndex].m_dwFileSizeInBytesLOW);
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetFileSizeHigh, (jlong)pArchiveFileVersionDetailList[iVersionIndex].m_dwFileSizeInBytesHIGH);

						SYSTEMTIME stModtime;
						FileTimeToSystemTime((const FILETIME *)&pArchiveFileVersionDetailList[iVersionIndex].m_stModifiedDateTime, &stModtime);
						LONGLONG dwModDate = MAKE_LONGLONG((pArchiveFileVersionDetailList[iVersionIndex].m_stModifiedDateTime.dwHighDateTime), (pArchiveFileVersionDetailList[iVersionIndex].m_stModifiedDateTime.dwLowDateTime));
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetModifiedTime, (jlong)dwModDate);

						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetmodifiedHour, (jlong)(stModtime.wHour));
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetmodifiedMin, (jlong)(stModtime.wMinute));
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetmodifiedSec, (jlong)(stModtime.wSecond));
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetmodifiedDay, (jlong)(stModtime.wDay));
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetmodifiedMonth, (jlong)(stModtime.wMonth));
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetmodifiedYear, (jlong)(stModtime.wYear));

						SYSTEMTIME stArchivetime;
						FileTimeToSystemTime((const FILETIME *)&pArchiveFileVersionDetailList[iVersionIndex].m_stArchivedTime, &stArchivetime);
						LONGLONG dwArchiveDate = MAKE_LONGLONG((pArchiveFileVersionDetailList[iVersionIndex].m_stArchivedTime.dwHighDateTime), (pArchiveFileVersionDetailList[iVersionIndex].m_stArchivedTime.dwLowDateTime));
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetArchivedDateTime, (jlong)dwArchiveDate);

						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetarchivedHour, (jlong)(stArchivetime.wHour));
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetarchivedMin, (jlong)(stArchivetime.wMinute));
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetarchivedSec, (jlong)(stArchivetime.wSecond));
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetarchivedDay, (jlong)(stArchivetime.wDay));
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetarchivedMonth, (jlong)(stArchivetime.wMonth));
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetarchivedYear, (jlong)(stArchivetime.wYear));

						//file type - archived/copied
						env->CallVoidMethod(objJArchiveFileVersionDetail, midsetFileType, pArchiveFileVersionDetailList[iVersionIndex].m_FileType);

						//5. Add to FileVersion list
						jboolean bret;
						bret = env->CallBooleanMethod(jObjFileVesrionsList, listAddMethod, objJArchiveFileVersionDetail);
						if (objJArchiveFileVersionDetail != NULL)
							env->DeleteLocalRef(objJArchiveFileVersionDetail);

						if (jVersion)
							env->DeleteLocalRef(jVersion);
					}

					////6.ArchiveCatalogDetail.add(FileVersion);
					env->CallVoidMethod(objArchiveCatalogDetail, midsetFileVersionsList, jObjFileVesrionsList);

					if (jObjFileVesrionsList != NULL)
						env->DeleteLocalRef(jObjFileVesrionsList);

					if (jVersionCount)
						env->DeleteLocalRef(jVersionCount);
				}

				//7.archiveCatalogItems.add(JArchiveCatalogDetail);
				env->CallBooleanMethod(archiveCatalogItems, addMethod, objArchiveCatalogDetail);
				if (objArchiveCatalogDetail != NULL)
					env->DeleteLocalRef(objArchiveCatalogDetail);

				if (jName)
					env->DeleteLocalRef(jName);

				if (jCatPath)
					env->DeleteLocalRef(jCatPath);
			}
		}

		if (m_jclsJArchiveFileVersionDetail != NULL)
			env->DeleteLocalRef(m_jclsJArchiveFileVersionDetail);

		if (arrayListClass != NULL)
			env->DeleteLocalRef(arrayListClass);

		if (m_jclsJArchiveCatalogDetail != NULL)
			env->DeleteLocalRef(m_jclsJArchiveCatalogDetail);

		if (list_class != NULL)
			env->DeleteLocalRef(list_class);

		return 1;
	}

	int FillCatalogLastUpdateTime2JObject(JNIEnv *env, jclass clz, wstring szHostName, FILETIME & stLastUpdateTimeStamp, DWORD dwStatus, jobject & out_CatalogDetails)
	{
		jclass CatalogDetails_class;
		CatalogDetails_class = env->FindClass("com/ca/arcflash/webservice/data/archive/ArchiveDestinationDetailsConfig");

		jmethodID midsetLastSyncHour = env->GetMethodID(CatalogDetails_class, "setLastSyncHour", "(J)V");
		jmethodID midsetLastSyncMin = env->GetMethodID(CatalogDetails_class, "setLastSyncMin", "(J)V");
		jmethodID midsetLastSyncSec = env->GetMethodID(CatalogDetails_class, "setLastSyncSec", "(J)V");

		jmethodID midsetLastSyncDay = env->GetMethodID(CatalogDetails_class, "setLastSyncDay", "(J)V");
		jmethodID midsetLastSyncMonth = env->GetMethodID(CatalogDetails_class, "setLastSyncMonth", "(J)V");
		jmethodID midsetLastSyncYear = env->GetMethodID(CatalogDetails_class, "setLastSyncYear", "(J)V");
		jmethodID midsetLastSyncTime = env->GetMethodID(CatalogDetails_class, "setLastSyncTime", "(J)V");

		jmethodID midsetCatalogAvailable = env->GetMethodID(CatalogDetails_class, "setCatalogAvailable", "(Z)V");
		jmethodID midsetreturnCode = env->GetMethodID(CatalogDetails_class, "setreturnCode", "(J)V");
		jmethodID midsethostName = env->GetMethodID(CatalogDetails_class, "sethostName", "(Ljava/lang/String;)V");

		SYSTEMTIME stCatalogSystemTime;
		FileTimeToSystemTime((const FILETIME *)&stLastUpdateTimeStamp, &stCatalogSystemTime);
		LONGLONG dwModDate = MAKE_LONGLONG(stLastUpdateTimeStamp.dwHighDateTime, stLastUpdateTimeStamp.dwLowDateTime);
		env->CallVoidMethod(out_CatalogDetails, midsetLastSyncTime, (jlong)dwModDate);

		env->CallVoidMethod(out_CatalogDetails, midsetLastSyncHour, (jlong)(stCatalogSystemTime.wHour));
		env->CallVoidMethod(out_CatalogDetails, midsetLastSyncMin, (jlong)(stCatalogSystemTime.wMinute));
		env->CallVoidMethod(out_CatalogDetails, midsetLastSyncSec, (jlong)(stCatalogSystemTime.wSecond));
		env->CallVoidMethod(out_CatalogDetails, midsetLastSyncDay, (jlong)(stCatalogSystemTime.wDay));
		env->CallVoidMethod(out_CatalogDetails, midsetLastSyncMonth, (jlong)(stCatalogSystemTime.wMonth));
		env->CallVoidMethod(out_CatalogDetails, midsetLastSyncYear, (jlong)(stCatalogSystemTime.wYear));

		jstring jHostName = WCHARToJString(env, (wchar_t*)(szHostName.c_str()));
		env->CallVoidMethod(out_CatalogDetails, midsethostName, jHostName);

		env->CallVoidMethod(out_CatalogDetails, midsetreturnCode, (jlong)dwStatus);

		if (dwStatus != 0)
		{
			env->CallVoidMethod(out_CatalogDetails, midsetCatalogAvailable, 0);//false
		}
		else
		{
			env->CallVoidMethod(out_CatalogDetails, midsetCatalogAvailable, 1);//true
		}

		if (CatalogDetails_class != NULL)
			env->DeleteLocalRef(CatalogDetails_class);

		return 1;
	}

	jlong JNICALL GetArchiveJobsInfo(JNIEnv *env, jclass clz, jobject inout_jArchiveJob, jobject out_archiveJobsList)
	{
		LogFile(L"GetArchiveJobsInfo start");
		DWORD dwRet = 0;

		/*jclass list_class = env->GetObjectClass(out_archiveJobsList);
		jmethodID addMethod = env->GetMethodID(list_class, "add", "(Ljava/lang/Object;)Z");*/

		jclass class_JArchiveJob = env->GetObjectClass(inout_jArchiveJob);

		jmethodID midgetbackupDestination = env->GetMethodID(class_JArchiveJob, "getbackupDestination", "()Ljava/lang/String;");
		jstring jBackupDestPath = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetbackupDestination);
		wchar_t * pwszBackupDestinationPath = JStringToWCHAR(env, jBackupDestPath);
		if (jBackupDestPath)
			env->DeleteLocalRef(jBackupDestPath);

		jmethodID midgetbackupDestinationDomain = env->GetMethodID(class_JArchiveJob, "getbackupDestinationDomain", "()Ljava/lang/String;");
		jstring jBackupDestDomain = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetbackupDestinationDomain);
		wchar_t * pwszBackupDestinationDomain = JStringToWCHAR(env, jBackupDestDomain);
		if (jBackupDestDomain)
			env->DeleteLocalRef(jBackupDestDomain);

		jmethodID midgetbackupDestinationUsername = env->GetMethodID(class_JArchiveJob, "getbackupDestinationUsername", "()Ljava/lang/String;");
		jstring jBackupDestUsername = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetbackupDestinationUsername);
		wchar_t * pwszBackupDestinationUser = JStringToWCHAR(env, jBackupDestUsername);
		if (jBackupDestUsername)
			env->DeleteLocalRef(jBackupDestUsername);

		jmethodID midgetbackupDestinationPassword = env->GetMethodID(class_JArchiveJob, "getbackupDestinationPassword", "()Ljava/lang/String;");
		jstring jBackupDestPassword = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetbackupDestinationPassword);
		wchar_t * pwszBackupDestinationPassword = JStringToWCHAR(env, jBackupDestPassword);
		if (jBackupDestPassword)
			env->DeleteLocalRef(jBackupDestPassword);

		jmethodID midgetD2dHostName = env->GetMethodID(class_JArchiveJob, "getD2dHostName", "()Ljava/lang/String;");
		jstring jD2dHostName = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetD2dHostName);
		wchar_t * pwszjD2dHostName = JStringToWCHAR(env, jD2dHostName);
		if (jD2dHostName)
			env->DeleteLocalRef(jD2dHostName);
		wstring wsD2DHostName = pwszjD2dHostName;
		jfieldID field_ScheduleType = env->GetFieldID(class_JArchiveJob, "ScheduleType", "I");
		DWORD dwScheduleType = env->GetIntField(inout_jArchiveJob, field_ScheduleType);
		TCHAR szMessage[260] = { _T('\0') };
		_stprintf(szMessage, L"schedule type: %d", dwScheduleType);
		LogFile(szMessage);
		//jmethodID midgetScheduleType = env->GetMethodID(class_JArchiveJob,"getScheduleType","()I");
		//DWORD dwScheduleType = (jlong)env->CallIntMethod(inout_jArchiveJob,midgetScheduleType);

		jfieldID field_bOnlyOneSession = env->GetFieldID(class_JArchiveJob, "bOnlyOneSession", "Z");
		BOOL bOnlyOneSession = env->GetBooleanField(inout_jArchiveJob, field_bOnlyOneSession);
		//jmethodID midisbOnlyOneSession = env->GetMethodID(class_JArchiveJob,"isbOnlyOneSession","()Z");
		//BOOL bOnlyOneSession = env->CallBooleanMethod(inout_jArchiveJob,midisbOnlyOneSession);

		//LogFile(L"topNItems");
		//jfieldID field_nTopItems = env->GetFieldID(class_JArchiveJob, "topNItems", "I");
		//DWORD dwnTopItems = env->GetIntField(inout_jArchiveJob, field_nTopItems);

		NET_CONN_INFO destInfo;
		memset(&destInfo, 0, sizeof(NET_CONN_INFO));
		_tcscpy(destInfo.szDir, pwszBackupDestinationPath);
		_tcscpy(destInfo.szDomain, pwszBackupDestinationDomain);
		_tcscpy(destInfo.szUsr, pwszBackupDestinationUser);
		_tcscpy(destInfo.szPwd, pwszBackupDestinationPassword);

		vector<ARCHIVE_ITEM_INFO> archiveJobs;

		jfieldID field_jobType = env->GetFieldID(class_JArchiveJob, "jobType", "J");
		DWORD JobType = env->GetLongField(inout_jArchiveJob, field_jobType);
		if (dwScheduleType == LastJobDetails)
		{
			dwRet = AFGetLastArchiveJobStatus2(wsD2DHostName, destInfo, archiveJobs, JobType);
		}
		else
		{
			dwRet = AFGetArchiveJobByScheduleStatus2(wsD2DHostName, destInfo, dwScheduleType, archiveJobs, bOnlyOneSession, JobType);
		}

		if (!dwRet)
		{
			//LogFile(L"AFGetArchiveJobByScheduleStatus success");
			////jclass class_JArchiveJob = env->FindClass("com/ca/arcflash/webservice/jni/model/JArchiveJob");
			jmethodID midsetsubmitArchive = env->GetMethodID(class_JArchiveJob, "setsubmitArchive", "(Z)V");
			BOOL bSubmitArchive = FALSE;
			if (archiveJobs.size() != 0)
				bSubmitArchive = TRUE;
			else
				bSubmitArchive = FALSE;

			env->CallVoidMethod(inout_jArchiveJob, midsetsubmitArchive, (jboolean)bSubmitArchive);

			if (archiveJobs.size() != 0)
			{
				ConvertArchiveJobsInformation2JObject(env, clz, archiveJobs, out_archiveJobsList);
			}
		}
		else
		{
			TCHAR szMessage[260] = { _T('\0') };
			_stprintf(szMessage, L"AFGetArchiveJobByScheduleStatus failed with error: dwRet %d", dwRet);
			LogFile(szMessage);
		}

		if (pwszBackupDestinationPath != NULL)
			free(pwszBackupDestinationPath);

		if (pwszBackupDestinationDomain != NULL)
			free(pwszBackupDestinationDomain);

		if (pwszBackupDestinationUser != NULL)
			free(pwszBackupDestinationUser);

		if (pwszBackupDestinationPassword != NULL)
			free(pwszBackupDestinationPassword);
		if (pwszjD2dHostName)
			free(pwszjD2dHostName);

		if (class_JArchiveJob != NULL) env->DeleteLocalRef(class_JArchiveJob);

		LogFile(L"GetArchiveJobsInfo End");
		return (jlong)dwRet;
	}

	jlong JNICALL GetArchiveJobInfoCount(JNIEnv *env, jclass clz, jobject inout_jArchiveJob, jobject jobCount)
	{
		DWORD dwJobCount = 0;
		DWORD dwRet = 0;

		jclass class_JArchiveJob = env->GetObjectClass(inout_jArchiveJob);

		jmethodID midgetbackupDestination = env->GetMethodID(class_JArchiveJob, "getbackupDestination", "()Ljava/lang/String;");
		jstring jBackupDestPath = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetbackupDestination);
		wchar_t * pwszBackupDestinationPath = JStringToWCHAR(env, jBackupDestPath);
		if (jBackupDestPath)
			env->DeleteLocalRef(jBackupDestPath);

		jmethodID midgetbackupDestinationDomain = env->GetMethodID(class_JArchiveJob, "getbackupDestinationDomain", "()Ljava/lang/String;");
		jstring jBackupDestDomain = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetbackupDestinationDomain);
		wchar_t * pwszBackupDestinationDomain = JStringToWCHAR(env, jBackupDestDomain);
		if (jBackupDestDomain)
			env->DeleteLocalRef(jBackupDestDomain);

		jmethodID midgetbackupDestinationUsername = env->GetMethodID(class_JArchiveJob, "getbackupDestinationUsername", "()Ljava/lang/String;");
		jstring jBackupDestUsername = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetbackupDestinationUsername);
		wchar_t * pwszBackupDestinationUser = JStringToWCHAR(env, jBackupDestUsername);
		if (jBackupDestUsername)
			env->DeleteLocalRef(jBackupDestUsername);

		jmethodID midgetbackupDestinationPassword = env->GetMethodID(class_JArchiveJob, "getbackupDestinationPassword", "()Ljava/lang/String;");
		jstring jBackupDestPassword = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetbackupDestinationPassword);
		wchar_t * pwszBackupDestinationPassword = JStringToWCHAR(env, jBackupDestPassword);
		if (jBackupDestPassword)
			env->DeleteLocalRef(jBackupDestPassword);
		jmethodID midgetD2dHostName = env->GetMethodID(class_JArchiveJob, "getD2dHostName", "()Ljava/lang/String;");
		jstring jD2dHostName = (jstring)env->CallObjectMethod(inout_jArchiveJob, midgetD2dHostName);
		wchar_t * pwszjD2dHostName = JStringToWCHAR(env, jD2dHostName);
		if (jD2dHostName)
			env->DeleteLocalRef(jD2dHostName);
		wstring wsD2DHostName = pwszjD2dHostName;

		NET_CONN_INFO destInfo;
		memset(&destInfo, 0, sizeof(NET_CONN_INFO));
		_tcscpy(destInfo.szDir, pwszBackupDestinationPath);
		_tcscpy(destInfo.szDomain, pwszBackupDestinationDomain);
		_tcscpy(destInfo.szUsr, pwszBackupDestinationUser);
		_tcscpy(destInfo.szPwd, pwszBackupDestinationPassword);

		vector<ARCHIVE_ITEM_INFO> out_archiveJobs;
		jfieldID field_jobType = env->GetFieldID(class_JArchiveJob, "jobType", "J");
		DWORD JobType = env->GetLongField(inout_jArchiveJob, field_jobType);

		dwRet = AFGetScheduleArchiveJobInfoCount2(wsD2DHostName, destInfo, JobType, dwJobCount);
		DWORDTOJRWLong(env, dwJobCount, jobCount);

		return 0;
	}
	//API's to check the archive jobs status
	jboolean JNICALL IsArchiveJobRunning(JNIEnv *env, jclass clz)
	{
		LogFile(L"IsArchiveJobRunning start");
		BOOL bRet = AFCheckArchivebackupJobExist();

		if (bRet)
			LogFile(L"Archive job running");
		else
			LogFile(L"Archive job is not running");

		return bRet;
	}
	jboolean JNICALL IsArchiveRestoreJobRunning(JNIEnv *env, jclass clz)
	{
		LogFile(L"IsArchiveRestoreJobRunning start");
		BOOL bRet = AFCheckArchiveRestoreJobExist();

		if (bRet)
			LogFile(L"Archive restore job running");
		else
			LogFile(L"Archive restore job is not running");

		return bRet;
	}
	jboolean JNICALL IsArchivePurgeJobRunning(JNIEnv *env, jclass clz)
	{
		LogFile(L"IsArchivePurgeJobRunning start");
		BOOL bRet = AFCheckArchivePurgeJobExist();

		if (bRet)
			LogFile(L"Archive purge job running");
		else
			LogFile(L"Archive purge job is not running");

		return bRet;
	}

	jboolean JNICALL IsArchiveCatalogSyncJobRunning(JNIEnv *env, jclass clz)
	{
		LogFile(L"IsArchiveCatalogSyncJobRunning start");
		BOOL bRet = AFCheckArchiveCatalogSyncJobExist();

		if (bRet)
			LogFile(L"Archive catalog sync job running");
		else
			LogFile(L"Archive catalog sync job is not running");

		return bRet;
	}

	jstring JNICALL GetArchiveHostName(JNIEnv *env, jclass jclz)
	{
		TCHAR  szComputerName[MAX_PATH] = { 0 };
		DWORD  bufCharCount = MAX_PATH;
		if (!AFArchiveGetDNSHostName(szComputerName))
		{
			GetComputerName(szComputerName, &bufCharCount);
		}
		jstring jarchiveDNSHostName = WCHARToJString(env, szComputerName);
		return jarchiveDNSHostName;
	}
	jstring JNICALL GetArchiveDNSHostName(JNIEnv *env, jclass jclz)
	{
		TCHAR szArchiveDNSHostName[MAX_PATH] = { _T('\0') };
		BOOL bRet = AFArchiveGetDNSHostName(szArchiveDNSHostName);

		jstring jarchiveDNSHostName = WCHARToJString(env, szArchiveDNSHostName);

		return jarchiveDNSHostName;
		/*if(bRet)
		return JNI_OK;
		else
		return JNI_ERR;*/
	}

	ARCHIVECLOUDDESTINFO ConvertCloudDestinationInfo(JNIEnv *env, jclass clz, jobject jcloudDestInfo)
	{
		jclass class_JDestInfo = env->GetObjectClass(jcloudDestInfo);
		jfieldID field_bArchiveToDrive = env->GetFieldID(class_JDestInfo, "bArchiveToDrive", "Z");
		BOOL bUseDisk = env->GetBooleanField(jcloudDestInfo, field_bArchiveToDrive);

		jfieldID field_bArchiveToCloud = env->GetFieldID(class_JDestInfo, "bArchiveToCloud", "Z");
		BOOL bUseCloud = env->GetBooleanField(jcloudDestInfo, field_bArchiveToCloud);

		DWORD dwDestType = -1;
		ARCHIVECLOUDDESTINFO archiveDestCloudInfo;
		memset(&archiveDestCloudInfo, 0, sizeof(ARCHIVECLOUDDESTINFO));
		if (bUseCloud)
		{
			LogFile(L"selected cloud");
			dwDestType = 0;

			JArchiveDestConfig2ArchiveCloudDestInfo(env, &archiveDestCloudInfo, &jcloudDestInfo);
		}

		return archiveDestCloudInfo;
	}

	void UINT64TOJRWLong(JNIEnv *env, UINT64 val, jobject rwLong)
	{
		jclass class_RWLong = env->GetObjectClass(rwLong);
		jmethodID id_RWLong_setValue = env->GetMethodID(class_RWLong, "setValue", "(J)V");
		env->CallVoidMethod(rwLong, id_RWLong_setValue, val);
	}

	void FreeArchiveJobScript(PAFARCHIVEJOBSCRIPT pafJS)
	{
		try
		{
			// why not __Free()
			// This function is more security
			AFARCHIVEJOBSCRIPT::__Free(*pafJS);
			return;

			if (pafJS->pwszBackupSessionPath != NULL) free(pafJS->pwszBackupSessionPath);
			if (pafJS->pwszBackupDestUserName != NULL) free(pafJS->pwszBackupDestUserName);
			if (pafJS->pwszBackupDestPassword != NULL) free(pafJS->pwszBackupDestPassword);

			if (pafJS->dwDestType == 4)
			{
				if (pafJS->pDiskDest->pwszDestpath != NULL)
					free(pafJS->pDiskDest->pwszDestpath);
				if (pafJS->pDiskDest->pwszUserName != NULL)
					free(pafJS->pDiskDest->pwszUserName);
				if (pafJS->pDiskDest->pwszPassword != NULL)
					free(pafJS->pDiskDest->pwszPassword);
			}
			else
			{
				if (pafJS->pCloudDest->pwszCloudBucketName != NULL)
					free(pafJS->pCloudDest->pwszCloudBucketName);
				if (pafJS->pCloudDest->pwszCloudDisplayBucketName != NULL)
					free(pafJS->pCloudDest->pwszCloudDisplayBucketName);
				if (pafJS->pCloudDest->pwszCloudVendorURL != NULL)
					free(pafJS->pCloudDest->pwszCloudVendorURL);
				if (pafJS->pCloudDest->pwszProxyPassword != NULL)
					free(pafJS->pCloudDest->pwszProxyPassword);
				if (pafJS->pCloudDest->pwszProxyServerName != NULL)
					free(pafJS->pCloudDest->pwszProxyServerName);
				if (pafJS->pCloudDest->pwszproxyUserName != NULL)
					free(pafJS->pCloudDest->pwszproxyUserName);
				//	if(pafJS->pCloudDest->pwszVendorHostname != NULL)
				//		free(pafJS->pCloudDest->pwszVendorHostname);
				if (pafJS->pCloudDest->pwszVendorPassword != NULL)
					free(pafJS->pCloudDest->pwszVendorPassword);
				if (pafJS->pCloudDest->pwszVendorUsername != NULL)
					free(pafJS->pCloudDest->pwszVendorUsername);
			}

			if (pafJS->pwszEncrptionPassword != NULL) free(pafJS->pwszEncrptionPassword);

			if (pafJS->pwszComments != NULL)	free(pafJS->pwszComments);
			if (pafJS->pwszBeforeJob != NULL) free(pafJS->pwszBeforeJob);
			if (pafJS->pwszAfterJob != NULL)	free(pafJS->pwszAfterJob);
			if (pafJS->pwszPrePostUser != NULL) free(pafJS->pwszPrePostUser);
			if (pafJS->pwszPrePostPassword != NULL) free(pafJS->pwszPrePostPassword);

			if (pafJS->pAFNodeList != NULL)
			{
				for (ULONG i = 0; i < pafJS->nNodeItems; i++)
				{
					if (pafJS->pAFNodeList[i].pwszNodeName != NULL) free(pafJS->pAFNodeList[i].pwszNodeName);
					if (pafJS->pAFNodeList[i].pwszNodeAddr != NULL)	free(pafJS->pAFNodeList[i].pwszNodeAddr);
					if (pafJS->pAFNodeList[i].pwszUserName != NULL)	free(pafJS->pAFNodeList[i].pwszUserName);
					if (pafJS->pAFNodeList[i].pwszUserPW != NULL) free(pafJS->pAFNodeList[i].pwszUserPW);
					if (pafJS->pAFNodeList[i].pwszSessPath != NULL) free(pafJS->pAFNodeList[i].pwszSessPath);

					if (pafJS->pAFNodeList[i].pArchiveVolumeAppList != NULL)
					{
						for (ULONG l = 0; l < pafJS->pAFNodeList[i].nVolumeApp; l++)
						{
							if (pafJS->pAFNodeList[i].pArchiveVolumeAppList[l].pwszVolName)
							{
								free(pafJS->pAFNodeList[i].pArchiveVolumeAppList[l].pwszVolName);
							}
							if (pafJS->pAFNodeList[i].pArchiveVolumeAppList[l].pVolItemAppCompList != NULL)
							{
								for (ULONG m = 0; m < pafJS->pAFNodeList[i].pArchiveVolumeAppList[l].nVolItemAppComp; m++)
								{
									if (pafJS->pAFNodeList[i].pArchiveVolumeAppList[l].pVolItemAppCompList[m].pwszFileorDir != NULL)
									{
										free(pafJS->pAFNodeList[i].pArchiveVolumeAppList[l].pVolItemAppCompList[m].pwszFileorDir);
									}
								}
								free(pafJS->pAFNodeList[i].pArchiveVolumeAppList[l].pVolItemAppCompList);
							}
						}

						free(pafJS->pAFNodeList[i].pArchiveVolumeAppList);
					}

					if (pafJS->pAFNodeList[i].pRestoreVolumeAppList != NULL)
					{
						for (ULONG l = 0; l < pafJS->pAFNodeList[i].nVolumeApp; l++)
						{
							if (pafJS->pAFNodeList[i].pRestoreVolumeAppList[l].pwszPath != NULL)
							{
								free(pafJS->pAFNodeList[i].pRestoreVolumeAppList[l].pwszPath);
							}
							if (pafJS->pAFNodeList[i].pRestoreVolumeAppList[l].pDestVolumeName != NULL)
							{
								free(pafJS->pAFNodeList[i].pRestoreVolumeAppList[l].pDestVolumeName);
							}
							if (pafJS->pAFNodeList[i].pRestoreVolumeAppList[l].pVolItemAppCompList != NULL)
							{
								for (ULONG m = 0; m < pafJS->pAFNodeList[i].pRestoreVolumeAppList[l].nVolItemAppComp; m++)
								{
									if (pafJS->pAFNodeList[i].pRestoreVolumeAppList[l].pVolItemAppCompList[m].pwszFileorDir != NULL)
									{
										free(pafJS->pAFNodeList[i].pRestoreVolumeAppList[l].pVolItemAppCompList[m].pwszFileorDir);
									}
								}
							}
							if (pafJS->pAFNodeList[i].pRestoreVolumeAppList[l].pDestItemList != NULL)
							{
								for (ULONG m = 0; m < pafJS->pAFNodeList[i].pRestoreVolumeAppList[l].nDestItemCount; m++)
								{
									if (pafJS->pAFNodeList[i].pRestoreVolumeAppList[l].pDestItemList[m].pwszFileorDir != NULL)
									{
										free(pafJS->pAFNodeList[i].pRestoreVolumeAppList[l].pDestItemList[m].pwszFileorDir);
									}
								}
								free(pafJS->pAFNodeList[i].pRestoreVolumeAppList[l].pDestItemList);
							}
						}

						free(pafJS->pAFNodeList[i].pRestoreVolumeAppList);
					}
				}

				free(pafJS->pAFNodeList);
			}

			if (pafJS->pwszCatalogDirPath)
			{
				free(pafJS->pwszCatalogDirPath);
				pafJS->pwszCatalogDirPath = NULL;
			}

			if (pafJS->pArchivePolicy != NULL)
			{
				for (ULONG idx = 0; idx < pafJS->nArchivePolicyCount; idx++)
					AFARCHIVEPOLICY::__Free(pafJS->pArchivePolicy[idx]);

				free(pafJS->pArchivePolicy);
				pafJS->pArchivePolicy = NULL;
			}

			if (pafJS->pFileCopyPolicy != NULL)
			{
				for (ULONG idx = 0; idx < pafJS->nFileCopyPolicyCount; idx++)
					AFFILECOPYPOLICY::__Free(pafJS->pFileCopyPolicy[idx]);

				free(pafJS->pFileCopyPolicy);
				pafJS->pFileCopyPolicy = NULL;
			}
		}
		catch (...)
		{
			LogFile(L"Exception occured in free archiv e restore job script.");
		}
	}
	jboolean JNICALL IsFileCopyJobRunning2(JNIEnv *env, jclass clz, jstring d2dMachineName)
	{
		wchar_t* pwszD2DMachineName = JStringToWCHAR(env, d2dMachineName);
		return AFCheckFileCopyBackupJobExist2(pwszD2DMachineName);
	}
	jboolean JNICALL IsFileArchiveJobRunning2(JNIEnv *env, jclass clz, jstring d2dMachineName)
	{
		wchar_t* pwszD2DMachineName = JStringToWCHAR(env, d2dMachineName);
		return AFCheckFileArchiveBackupJobExist2(pwszD2DMachineName);
	}
	jboolean JNICALL IsArchiveRestoreJobRunning2(JNIEnv *env, jclass clz, jstring d2dMachineName)
	{
		wchar_t* pwszD2DMachineName = JStringToWCHAR(env, d2dMachineName);
		return AFCheckArchiveRestoreJobExist2(pwszD2DMachineName);
	}
	jboolean JNICALL IsArchivePurgeJobRunning2(JNIEnv *env, jclass clz, jstring d2dMachineName)
	{
		wchar_t* pwszD2DMachineName = JStringToWCHAR(env, d2dMachineName);
		return AFCheckArchivePurgeJobExist2(pwszD2DMachineName);
	}
	jboolean JNICALL IsArchiveCatalogSyncJobRunning2(JNIEnv *env, jclass clz, jstring d2dMachineName)
	{
		wchar_t* pwszD2DMachineName = JStringToWCHAR(env, d2dMachineName);
		return AFCheckArchiveCatalogSyncJobExist2(pwszD2DMachineName);
	}
}