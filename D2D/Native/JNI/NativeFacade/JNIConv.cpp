#include "stdafx.h"
#include "afjob.h"
#include "AFFileStoreGlobals.h"
#include "AFFileCatalog.h"
#include "JNIConv.h"
#include "Winternl.h"
#include "comutil.h"
#include <atlstr.h>
#include <iostream>
#include "NativeFacade.h"
#include "utils.h"
using namespace std;

extern CDbgLog logObj;

#define  MAKE_LONGLONG( UULong, LULong)   ((((LONGLONG)(UULong))<<32)|(LULong))

void ulonglong_2_timestr( ULONGLONG ullTime, WCHAR* pszBuffer, DWORD dwBufSize )
{
	SYSTEMTIME sysTime;
	LARGE_INTEGER liTime;
	liTime.QuadPart = ullTime;
	liTime.QuadPart *= 10000;
	liTime.QuadPart += 116444736000000000;

	FILETIME ft;
	ZeroMemory( &ft, sizeof(ft) );
	ft.dwHighDateTime = liTime.HighPart;
	ft.dwLowDateTime = liTime.LowPart;

	::FileTimeToSystemTime( &ft, &sysTime );

	swprintf_s( pszBuffer, dwBufSize, L"%04d/%02d/%02d %02d:%02d:%02d", sysTime.wYear, sysTime.wMonth, sysTime.wDay, sysTime.wHour, sysTime.wMinute, sysTime.wSecond );
}

jstring ULONGLONG2TimeJString(JNIEnv *env, ULONGLONG ulltime)
{
	WCHAR szTime[MAX_PATH] = {0};
	ZeroMemory( szTime, sizeof(szTime) );
	ulonglong_2_timestr(ulltime, szTime, _ARRAYSIZE(szTime));
	return WCHARToJString(env, szTime);
}
ULONGLONG utctime_2_local( ULONGLONG ullUtc )
{
	LARGE_INTEGER liTime;
	liTime.QuadPart = ullUtc;
	liTime.QuadPart *= 10000;
	liTime.QuadPart += 116444736000000000;

	FILETIME ft;
	ZeroMemory( &ft, sizeof(ft) );
	ft.dwHighDateTime = liTime.HighPart;
	ft.dwLowDateTime = liTime.LowPart;
	
	SYSTEMTIME utcTime, localTime;
	::FileTimeToSystemTime( &ft, &utcTime );
	SystemTimeToTzSpecificLocalTime( NULL, &utcTime, &localTime);

	ZeroMemory( &ft, sizeof(ft) );
	::SystemTimeToFileTime( &localTime, &ft );
	
	liTime.HighPart = ft.dwHighDateTime;
	liTime.LowPart = ft.dwLowDateTime;
	liTime.QuadPart -= 116444736000000000;
	liTime.QuadPart /= 10000;

	return liTime.QuadPart;
}

wchar_t* JStringToWCHAR( JNIEnv *env, jstring str){
	if (str == NULL) return NULL;

	const jchar* jchs = env->GetStringChars(str, NULL);// UCS-2 to Unicode;

	if (jchs == NULL) return NULL;

	size_t len = (wcslen((wchar_t*)jchs)+1) * sizeof(wchar_t);
	wchar_t *tmp = (wchar_t *) malloc(len);
	if(tmp)
	{
		memset(tmp, 0, len );
		wcscpy_s(tmp, len / sizeof(wchar_t), (wchar_t*)jchs);
	}
	
	env->ReleaseStringChars(str, jchs);

	return tmp;
}

wstring JStringToWString( JNIEnv *env, jstring str)
{
	if (str == NULL) return L"";

	const jchar* jchs = env->GetStringChars(str, NULL);// UCS-2 to Unicode;

	if (jchs == NULL) return L"";

	wstring wstr((wchar_t*)jchs);

	env->ReleaseStringChars(str, jchs);

	return wstr;

}

char* JStringToChar(JNIEnv *env, jstring str)
{
	if(str == NULL)
		return NULL;

	wchar_t* tmp = JStringToWCHAR(env, str);
	if(tmp == NULL)
		return NULL;

	size_t len = wcslen(tmp) + 1;

	char* ret = (char*)malloc(len);
	memset(ret,'0',len);
	
	if (WideCharToMultiByte(CP_ACP, WC_COMPOSITECHECK | WC_SEPCHARS,
		tmp, -1, ret , len, NULL, NULL))
	{
		if(tmp != NULL)
			free(tmp);
		return ret;
	}
	else
	{
		return NULL;
	}
}

jstring WCHARToJString( JNIEnv * env, const wchar_t* str)
{
	jstring rtn = 0;

	if (!env) 
		return 0;

	if (str == NULL) {
		char sTemp[2];
		strcpy_s(sTemp, "");
		rtn = env->NewStringUTF(sTemp);
		return rtn;
	}

	int slen = wcslen(str);

	rtn = env->NewString((jchar*)str, slen);

	return rtn;
}

DWORD JStringToBSTR(JNIEnv *env, jstring jIn, BSTR* pBstrOut )
{
      wchar_t* pszTemp = NULL;

      try
      {
            wchar_t* pszTemp = JStringToWCHAR(env, jIn);
            if( pszTemp )
            {
                  *pBstrOut = ::SysAllocString( pszTemp );
                  free(pszTemp);
                  pszTemp = NULL;
            }
      }
      catch (...)
      {
            if( pszTemp )
            {
                  free(pszTemp); 
                  pszTemp = NULL;
            }

            return E_OUTOFMEMORY;
      }

      return ERROR_SUCCESS;
}

jstring DWORD2String(JNIEnv* env, DWORD dwValue)
{
	long lValue = (long)dwValue;
	WCHAR str[256];
	str[0] = '\0';        
	_ltow_s(lValue, str, 256, 10);
	return WCHARToJString(env, str);
}

void AddUINT2JRWLong(JNIEnv *env, UINT fv, jobject* toRWLong)
{
	jclass class_RWLong = env->GetObjectClass(*toRWLong);
	jmethodID id_RWLong_setValue = env->GetMethodID(class_RWLong, "setValue", "(J)V");
	env->CallVoidMethod(*toRWLong, id_RWLong_setValue, (fv == -1 ? -1 :(jlong)fv));
}

void DWORDTOJRWLong(JNIEnv *env, DWORD dwWord, jobject rwLong){
	jclass class_RWLong = env->GetObjectClass(rwLong);
	jmethodID id_RWLong_setValue = env->GetMethodID(class_RWLong, "setValue", "(J)V");
	env->CallVoidMethod(rwLong, id_RWLong_setValue, dwWord);
}

void UINT64TOJRWLong(JNIEnv *env, UINT64 val, jobject rwLong){
	jclass class_RWLong = env->GetObjectClass(rwLong);
	jmethodID id_RWLong_setValue = env->GetMethodID(class_RWLong, "setValue", "(J)V");
	env->CallVoidMethod(rwLong, id_RWLong_setValue, val);
}
jlong JRWLongTojlong(JNIEnv* env, jobject* pRWLong)
{
	jclass class_RWLong = env->FindClass("com/ca/arcflash/webservice/jni/model/JRWLong");
	jfieldID field_value = env->GetFieldID(class_RWLong, "value", "J");
	jlong value = env->GetLongField(*pRWLong, field_value);
	if (class_RWLong != NULL) env->DeleteLocalRef(class_RWLong);
	return value;
}
int Add2JMountPoint(JNIEnv *env, wchar_t *szMountGUID,wchar_t *szDiskSignature, HANDLE handle, jobject jMountPoint){//added handle //20415776

	jclass class_MountPoint = env->FindClass("com/ca/arcflash/webservice/jni/model/JMountPoint");	

	jfieldID field_MountID = env->GetFieldID(class_MountPoint, "MountID", "Ljava/lang/String;");
	jfieldID field_diskSignature = env->GetFieldID(class_MountPoint, "diskSignature", "Ljava/lang/String;");
	jfieldID field_mountHandle = env->GetFieldID(class_MountPoint, "mountHandle", "J");

	/*jmethodID mid_MountPoint_constructor = env->GetMethodID(class_MountPoint, "<init>", "()V");
	*jMountPoint = env->NewObject(class_MountPoint, mid_MountPoint_constructor);*/

	jstring jstr = WCHARToJString(env, szMountGUID);
	env->SetObjectField(jMountPoint, field_MountID, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	jstr = WCHARToJString(env, szDiskSignature);
	env->SetObjectField(jMountPoint, field_diskSignature, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	if(handle!=NULL)
		env->SetLongField(jMountPoint, field_mountHandle,(jlong)handle);

	return 0;
}

int AddDetailW2List(JNIEnv *env, jobject* list, PDetailW pd){

	jclass class_CatalogDetail = env->FindClass("com/ca/arcflash/webservice/jni/model/JCatalogDetail");	

	jfieldID field_longNameID	= env->GetFieldID(class_CatalogDetail, "longNameID", "J");
	jfieldID field_pathID = env->GetFieldID(class_CatalogDetail, "pathID", "J");
	jfieldID field_sessType = env->GetFieldID(class_CatalogDetail, "sessType", "I");
	jfieldID field_dataType = env->GetFieldID(class_CatalogDetail, "dataType", "I");
	jfieldID field_fileDate	= env->GetFieldID(class_CatalogDetail, "fileDate", "J");
	jfieldID field_fileSizeHigh	= env->GetFieldID(class_CatalogDetail, "fileSizeHigh", "J");
	jfieldID field_fileSize	= env->GetFieldID(class_CatalogDetail, "fileSize", "J");
	jfieldID field_sessionNumber	= env->GetFieldID(class_CatalogDetail, "sessionNumber", "I");
	jfieldID field_subSessionNumber	= env->GetFieldID(class_CatalogDetail, "subSessionNumber", "I");
	jfieldID field_longName	= env->GetFieldID(class_CatalogDetail, "longName", "Ljava/lang/String;");
	jfieldID field_displayName	= env->GetFieldID(class_CatalogDetail, "displayName", "Ljava/lang/String;");
	jfieldID field_path	= env->GetFieldID(class_CatalogDetail, "path", "Ljava/lang/String;");

	jfieldID field_fullSessNum = env->GetFieldID(class_CatalogDetail, "fullSessNum", "J");
	jfieldID field_encryptInfo = env->GetFieldID(class_CatalogDetail, "encryptInfo", "J");
	jfieldID field_backupDest = env->GetFieldID(class_CatalogDetail, "backupDest", "Ljava/lang/String;");
	jfieldID field_jobName = env->GetFieldID(class_CatalogDetail, "jobName", "Ljava/lang/String;");
	jfieldID field_backupTime = env->GetFieldID(class_CatalogDetail, "backupTime", "J");
	jfieldID field_pwdHash = env->GetFieldID(class_CatalogDetail, "pwdHash", "Ljava/lang/String;");
	jfieldID field_sessionGuid = env->GetFieldID(class_CatalogDetail, "sessionGuid", "Ljava/lang/String;");
	jfieldID field_fullSessionGuid = env->GetFieldID(class_CatalogDetail, "fullSessionGuid", "Ljava/lang/String;");
	jfieldID field_driverLeterAttr	= env->GetFieldID(class_CatalogDetail, "driverLeterAttr", "I");

	jmethodID mid_CatalogDetail_constructor = env->GetMethodID(class_CatalogDetail, "<init>", "()V");
	jobject jCatalogDetail = env->NewObject(class_CatalogDetail, mid_CatalogDetail_constructor);

	env->SetLongField(jCatalogDetail, field_longNameID, pd->LongNameID);
	env->SetLongField(jCatalogDetail, field_pathID, pd->PathID);
	env->SetIntField(jCatalogDetail, field_sessType, pd->SessType);
	env->SetIntField(jCatalogDetail, field_dataType, pd->DataType);
	env->SetLongField(jCatalogDetail, field_fileDate, pd->FileDate);
	env->SetLongField(jCatalogDetail, field_fileSizeHigh, pd->FileSizeHigh);
	env->SetLongField(jCatalogDetail, field_fileSize, pd->FileSize);
	env->SetIntField(jCatalogDetail, field_sessionNumber, pd->SessionNumber);
	env->SetIntField(jCatalogDetail, field_subSessionNumber, pd->SubSessionNumber);

	env->SetLongField(jCatalogDetail, field_fullSessNum, pd->ulFullSessNum);
	env->SetLongField(jCatalogDetail, field_encryptInfo, pd->ulEncryptInfo);
	env->SetObjectField(jCatalogDetail, field_backupDest, WCHARToJString(env, pd->wzBKDest));
	env->SetObjectField(jCatalogDetail, field_jobName, WCHARToJString(env, pd->wzJobName));
	env->SetLongField(jCatalogDetail, field_backupTime, pd->ulBKTime);
	env->SetObjectField(jCatalogDetail, field_pwdHash, WCHARToJString(env, pd->wzPWDHash));
	env->SetObjectField(jCatalogDetail, field_sessionGuid, WCHARToJString(env, pd->wzSessGUID));
	env->SetObjectField(jCatalogDetail, field_fullSessionGuid, WCHARToJString(env, pd->wzFullSessGUID));
	env->SetIntField(jCatalogDetail, field_driverLeterAttr, pd->dwFlags);

	jmethodID mid_JCatalogDetail_setDefaultSessPwd = env->GetMethodID(class_CatalogDetail, "setDefaultSessPwd", "(Z)V");
	env->CallVoidMethod(jCatalogDetail, mid_JCatalogDetail_setDefaultSessPwd, (jboolean)0);

	jstring jstr = WCHARToJString(env, pd->LongName);
	env->SetObjectField(jCatalogDetail, field_longName, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	jstr = WCHARToJString(env, pd->DisplayName);
	env->SetObjectField(jCatalogDetail, field_displayName, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	jclass class_ArrayList = env->GetObjectClass(*list);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
	env->CallBooleanMethod(*list, id_ArrayList_add, jCatalogDetail);

	if (jCatalogDetail != NULL) env->DeleteLocalRef(jCatalogDetail);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);
	if (class_CatalogDetail != NULL) env->DeleteLocalRef(class_CatalogDetail);	

	return 0;
}

typedef	BOOLEAN (WINAPI *PFNSRtlTimeToSecondsSince1970)( PLARGE_INTEGER,PULONG);

int AddVecFileInfo2List(JNIEnv *env, jobject *retArr,std::vector<FILE_INFO> &vList)
{
	jclass class_JFileInfo = env->FindClass("com/ca/arcflash/webservice/jni/model/JFileInfo");	

	jfieldID field_dwFileAttributes	= env->GetFieldID(class_JFileInfo, "dwFileAttributes", "J");
	jfieldID field_nFileSizeHigh = env->GetFieldID(class_JFileInfo, "nFileSizeHigh", "J");
	jfieldID field_nFileSizeLow = env->GetFieldID(class_JFileInfo, "nFileSizeLow", "J");
	jfieldID field_creationDateTime = env->GetFieldID(class_JFileInfo, "creationDateTime", "J");
	jfieldID field_lastAccessDateTime	= env->GetFieldID(class_JFileInfo, "lastAccessDateTime", "J");
	jfieldID field_lastWriteDateTime	= env->GetFieldID(class_JFileInfo, "lastWriteDateTime", "J");
	jfieldID field_strName	= env->GetFieldID(class_JFileInfo, "strName", "Ljava/lang/String;");
	jfieldID field_strPath	= env->GetFieldID(class_JFileInfo, "strPath", "Ljava/lang/String;");

	jmethodID mid_FileInfo_constructor = env->GetMethodID(class_JFileInfo, "<init>", "()V");
	jclass class_ArrayList = env->GetObjectClass(*retArr);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

	HINSTANCE hNtdll = LoadLibrary( _T("Ntdll.dll"));
	PFNSRtlTimeToSecondsSince1970 lpfnRtlTimeToSecondsSince1970 = (PFNSRtlTimeToSecondsSince1970)GetProcAddress(hNtdll, "RtlTimeToSecondsSince1970");

	for(vector<FILE_INFO>::iterator itr = vList.begin(); itr != vList.end(); itr++)
	{
		jobject jFileInfo = env->NewObject(class_JFileInfo, mid_FileInfo_constructor);
		env->SetLongField(jFileInfo, field_dwFileAttributes, itr->dwFileAttributes);
		env->SetLongField(jFileInfo, field_nFileSizeHigh, itr->nFileSizeHigh);	
		env->SetLongField(jFileInfo, field_nFileSizeLow, itr->nFileSizeLow);

		LARGE_INTEGER ulnum; 
		ULONG elapsedSeconds = 0;
		ulnum.HighPart = itr->ftCreationTime.dwHighDateTime; 
		ulnum.LowPart = itr->ftCreationTime.dwLowDateTime; 		
		BOOLEAN  ret = lpfnRtlTimeToSecondsSince1970(&ulnum,&elapsedSeconds);
		if(!ret)
		{
			elapsedSeconds = 0;
		}
		env->SetLongField(jFileInfo, field_creationDateTime, elapsedSeconds);

		ulnum.HighPart = itr->ftLastAccessTime.dwHighDateTime; 
		ulnum.LowPart = itr->ftLastAccessTime.dwLowDateTime; 
		elapsedSeconds = 0;
		ret = lpfnRtlTimeToSecondsSince1970(&ulnum,&elapsedSeconds);
		if(!ret)
		{
			elapsedSeconds = 0;
		}
		env->SetLongField(jFileInfo, field_lastAccessDateTime, elapsedSeconds);	

		ulnum.HighPart = itr->ftLastWriteTime.dwHighDateTime; 
		ulnum.LowPart = itr->ftLastWriteTime.dwLowDateTime; 
		elapsedSeconds = 0;
		ret = lpfnRtlTimeToSecondsSince1970(&ulnum,&elapsedSeconds);
		if(!ret)
		{
			elapsedSeconds = 0;
		}
		env->SetLongField(jFileInfo, field_lastWriteDateTime, elapsedSeconds);

		jstring jstr = WCHARToJString(env, (wchar_t*)itr->strName.c_str());
		env->SetObjectField(jFileInfo, field_strName, jstr);
		if ( jstr!=NULL) env->DeleteLocalRef(jstr);

		jstr = WCHARToJString(env, (wchar_t*)itr->strPath.c_str());
		env->SetObjectField(jFileInfo, field_strPath, jstr);
		if ( jstr!=NULL) env->DeleteLocalRef(jstr); 

		env->CallBooleanMethod(*retArr, id_ArrayList_add, jFileInfo);

		if (jFileInfo != NULL) env->DeleteLocalRef(jFileInfo);
	}
	if(hNtdll)
	{
		FreeLibrary(hNtdll);
	}

	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);
	if (class_JFileInfo != NULL) env->DeleteLocalRef(class_JFileInfo);	
	return 0;
}


void AddJobStatusTojJS(JNIEnv* env, PJobStatus pJobStatus, jobject* tojJS)
{
	jclass class_JobStatus = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobStatus");	

	jfieldID field_liGrandTotal	= env->GetFieldID(class_JobStatus, "liGrandTotal", "J");
	jfieldID field_liVolumeTotal	= env->GetFieldID(class_JobStatus, "liVolumeTotal", "J");
	jfieldID field_liVolumeProcessed	= env->GetFieldID(class_JobStatus, "liVolumeProcessed", "J");
	jfieldID field_bJobCancled	= env->GetFieldID(class_JobStatus, "bJobCancled", "Z");
	jmethodID id_JobStatus_setLiGrandTotal = env->GetMethodID(class_JobStatus, "setLiGrandTotal", "(J)V");
	
	env->CallVoidMethod(*tojJS, id_JobStatus_setLiGrandTotal, pJobStatus->liGrandTotal.QuadPart);

	jmethodID id_JobStatus_setLiVolumeTotal = env->GetMethodID(class_JobStatus, "setLiVolumeTotal", "(J)V");
	env->CallVoidMethod(*tojJS, id_JobStatus_setLiVolumeTotal, pJobStatus->liVolumeTotal.QuadPart);

	jmethodID id_JobStatus_setLiVolumeProcessed = env->GetMethodID(class_JobStatus, "setLiVolumeProcessed", "(J)V");
	env->CallVoidMethod(*tojJS, id_JobStatus_setLiVolumeProcessed, pJobStatus->liVolumeProcessed.QuadPart);

	jmethodID id_JobStatus_setbJobCancled = env->GetMethodID(class_JobStatus, "setbJobCancled", "(Z)V");
	env->CallVoidMethod(*tojJS, id_JobStatus_setbJobCancled, pJobStatus->bJobCancled);
}

void jJSToPJobStatus(JNIEnv* env, PJobStatus pJobStatus, jobject* jJS)
{
	jclass class_JobStatus = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobStatus");	

	jfieldID field_liGrandTotal	= env->GetFieldID(class_JobStatus, "liGrandTotal", "J");
	jfieldID field_liVolumeTotal	= env->GetFieldID(class_JobStatus, "liVolumeTotal", "J");
	jfieldID field_liVolumeProcessed	= env->GetFieldID(class_JobStatus, "liVolumeProcessed", "J");
	jfieldID field_bJobCancled	= env->GetFieldID(class_JobStatus, "bJobCancled", "Z");

	pJobStatus->liGrandTotal.QuadPart = env->GetLongField(*jJS, field_liGrandTotal);
	pJobStatus->liVolumeTotal.QuadPart = env->GetLongField(*jJS, field_liVolumeTotal);
	pJobStatus->liVolumeProcessed.QuadPart = env->GetLongField(*jJS, field_liVolumeProcessed);
	pJobStatus->bJobCancled = env->GetBooleanField(*jJS, field_bJobCancled);

	if (class_JobStatus != NULL) env->DeleteLocalRef(class_JobStatus);	
}

void AddstringToList(JNIEnv *env, jclass jclz, wchar_t* str, jobject* arr)
{	
	jclass class_ArrayList = env->GetObjectClass(*arr);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
	jstring ret = WCHARToJString(env,str);				
	env->CallBooleanMethod(*arr, id_ArrayList_add,ret);

	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);	
}

void AddLongToList(JNIEnv *env, jclass jclz, jlong value, jobject* arr)
{	
	jclass class_ArrayList = env->GetObjectClass(*arr);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");			
	jclass longCls = env->FindClass("java/lang/Long");
	jmethodID mid_Constructor = env->GetMethodID(longCls,"<init>","(J)V");
	jobject obj_value = env->NewObject(longCls,mid_Constructor,value);

	env->CallBooleanMethod(*arr, id_ArrayList_add,obj_value);

	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);	
}

void AddIntToIntegerList( JNIEnv * pEnv, jobject listObject, int value )
{
	jclass integerClass = pEnv->FindClass( "java/lang/Integer" );
	jmethodID constructorMethodID = pEnv->GetMethodID( integerClass,"<init>", "(I)V" );
	jobject integerObject = pEnv->NewObject( integerClass, constructorMethodID, value );

	jclass listClass = pEnv->GetObjectClass( listObject );
	jmethodID addMethodID = pEnv->GetMethodID( listClass, "add", "(Ljava/lang/Object;)Z" );
	pEnv->CallBooleanMethod( listObject, addMethodID, integerObject );

	if (listClass != NULL)
		pEnv->DeleteLocalRef( listClass );
}

void freeAFEXCHSUBITEM(AFEXCHSUBITEM& p)
{
	SAFE_FREE( p.pwszItemName );
	SAFE_FREE( p.pwszMailboxName );
	SAFE_FREE( p.pwszExchangeObjectIDs );
	SAFE_FREE( p.pwszDescription );
}

void freeAFSPSUBITEM( AFSPSUBITEM& p )
{
	SAFE_FREE( p.pwszName );
    SAFE_FREE( p.pwszData );
}

void freeAFVOLITEMAPPCOMP(AFVOLITEMAPPCOMP& p)
{
	SAFE_FREE( p.pwszFileorDir ) ;
	SAFE_FREE( p.pwszCompRestPath );
	SAFE_FREE( p.pwszCompRestName );

	if (p.pExchSubItemList != NULL)
	{		
		for(ULONG n=0; n<p.nExchSubItemList; n++)
			freeAFEXCHSUBITEM(p.pExchSubItemList[n]);
	}
	SAFE_FREE(p.pExchSubItemList);

	if( p.pSPSubItemList != NULL )
	{
		for(ULONG n=0; n<p.nSPSubItem; n++)
			freeAFSPSUBITEM(p.pSPSubItemList[n]);
	}
	SAFE_FREE(p.pSPSubItemList);

	//AD GRT
	if (p.pADItemList != NULL)
	{		
		for(int i=0; i<p.uADItemNum; i++)
		{
			if(p.pADItemList[i].dwATTrNum >0 )
			{
				SAFE_FREE(p.pADItemList[i].pwszATTrNames);
			}
		} 
	}
	SAFE_FREE(p.pADItemList);

}

void freeAFRESTVOLAPP(AFRESTVOLAPP& p)
{
	SAFE_FREE( p.pwszPath );	
	SAFE_FREE( p.pDestVolumeName );

	if (p.pVolItemAppCompList != NULL)
	{
		for(ULONG m=0; m<p.nVolItemAppComp; m++)
			freeAFVOLITEMAPPCOMP(p.pVolItemAppCompList[m]);
	}
	SAFE_FREE( p.pVolItemAppCompList );

	if (p.pDestItemList != NULL)
	{
		for(ULONG m=0; m<p.nDestItemCount; m++)
			freeAFVOLITEMAPPCOMP(p.pDestItemList[m]);							
	}
	SAFE_FREE( p.pDestItemList );

	if( p.pRestoreOption_Exch != NULL )
	{
		SAFE_FREE( p.pRestoreOption_Exch->pwszFolder );
		SAFE_FREE( p.pRestoreOption_Exch->pwszAlternateServer )
		SAFE_FREE( p.pRestoreOption_Exch->pwszUser );
		SAFE_FREE( p.pRestoreOption_Exch->pwszUserPW );
	}
	SAFE_FREE( p.pRestoreOption_Exch );

	if( p.pRestoreDest_SP != NULL )
	{
		SAFE_FREE( p.pRestoreDest_SP->pwszDestFolder ); 
		SAFE_FREE( p.pRestoreDest_SP->pwszDestNodeUrl );  
		SAFE_FREE( p.pRestoreDest_SP->pwszDestObjId);
		SAFE_FREE( p.pRestoreDest_SP->pwszOwnerLogin);  
		SAFE_FREE( p.pRestoreDest_SP->pwszOwnerEmail);   
		SAFE_FREE( p.pRestoreDest_SP->pwszUser);	
		SAFE_FREE( p.pRestoreDest_SP->pwszUserPW);	
	}
	SAFE_FREE( p.pRestoreDest_SP );

	if(NULL ==p.pRestoreOption_AD)
	{
		SAFE_FREE( p.pRestoreOption_AD );
	}
}

void freeAFBACKUPVOL(AFBACKUPVOL& p)
{
	SAFE_FREE( p.pwszVolName );

	if (p.pVolItemAppCompList != NULL)
	{
		for(ULONG m=0; m<p.nVolItemAppComp; m++)
			freeAFVOLITEMAPPCOMP(p.pVolItemAppCompList[m]);							
	}
	SAFE_FREE( p.pVolItemAppCompList );
}

void freeAFNODE(AFNODE& p)
{
	SAFE_FREE( p.pwszNodeName );
	SAFE_FREE( p.pwszNodeAddr );	
	SAFE_FREE( p.pwszUserName );	
	SAFE_FREE( p.pwszUserPW );	
	SAFE_FREE( p.pwszSessPath );	
	SAFE_FREE( p.pwszEncryptPasswordRestore );

	if(p.pBackupVolumeList != NULL)
	{	
		for(ULONG l=0; l<p.nVolumeApp; l++)
			freeAFBACKUPVOL(p.pBackupVolumeList[l]);
	}
	SAFE_FREE( p.pBackupVolumeList );

	if(p.pRestoreVolumeAppList != NULL)
	{
		for(ULONG l=0; l<p.nVolumeApp; l++)
			freeAFRESTVOLAPP(p.pRestoreVolumeAppList[l]);
	}
	SAFE_FREE( p.pRestoreVolumeAppList );

	// free m_VCESXCredentials
	SAFE_FREE( p.m_VCESXCredentials.ServerName );
	SAFE_FREE( p.m_VCESXCredentials.Username );
	SAFE_FREE( p.m_VCESXCredentials.Password );
	SAFE_FREE( p.m_VCESXCredentials.Protocol );
	
	// free m_VMInfo
	SAFE_FREE( p.m_VMInfo.vmName );
	SAFE_FREE( p.m_VMInfo.vmUUID );
	SAFE_FREE( p.m_VMInfo.vmHost );
	SAFE_FREE( p.m_VMInfo.vmVMX );
	SAFE_FREE( p.m_VMInfo.vmESXHost );
	SAFE_FREE( p.m_VMInfo.vmInstUUID );
	SAFE_FREE( p.m_VMInfo.vmGuestOS );
	SAFE_FREE( p.m_VMInfo.vmIP );
	
	// free m_GuestHostCredentials
	SAFE_FREE( p.m_GuestHostCredentials.VMUsername );
	SAFE_FREE( p.m_GuestHostCredentials.VMPassword );
	
	// free m_VSphereRestore_JobScript
	SAFE_FREE( p.m_VSphereRestore_JobScript.m_vcName ); 
	SAFE_FREE( p.m_VSphereRestore_JobScript.m_esxName );
	SAFE_FREE( p.m_VSphereRestore_JobScript.m_vmName );
	SAFE_FREE( p.m_VSphereRestore_JobScript.m_vmResPool );
	SAFE_FREE( p.m_VSphereRestore_JobScript.m_vmConfigFile );
	SAFE_FREE( p.m_VSphereRestore_JobScript.m_vmDataStore );

	SAFE_FREE( p.m_VSphereRestore_JobScript.m_VcEsxCredentials.ServerName );
	SAFE_FREE( p.m_VSphereRestore_JobScript.m_VcEsxCredentials.Username );
	SAFE_FREE( p.m_VSphereRestore_JobScript.m_VcEsxCredentials.Password );
	SAFE_FREE( p.m_VSphereRestore_JobScript.m_VcEsxCredentials.Protocol );

	if( p.m_VSphereRestore_JobScript.m_pDiskDataStores != NULL )
	{
		SAFE_FREE( p.m_VSphereRestore_JobScript.m_pDiskDataStores->m_diskUrl );
		SAFE_FREE( p.m_VSphereRestore_JobScript.m_pDiskDataStores->m_dataStore );
	}
	SAFE_FREE( p.m_VSphereRestore_JobScript.m_pDiskDataStores );

	if( p.m_VSphereRestore_JobScript.m_pVMNetworkAdapter != NULL )
	{
		SAFE_FREE( p.m_VSphereRestore_JobScript.m_pVMNetworkAdapter->m_deviceName );
		SAFE_FREE( p.m_VSphereRestore_JobScript.m_pVMNetworkAdapter->m_label );
		SAFE_FREE( p.m_VSphereRestore_JobScript.m_pVMNetworkAdapter->m_portgroupKey );
		SAFE_FREE( p.m_VSphereRestore_JobScript.m_pVMNetworkAdapter->m_portgroupName );
		SAFE_FREE( p.m_VSphereRestore_JobScript.m_pVMNetworkAdapter->m_switchName );
		SAFE_FREE( p.m_VSphereRestore_JobScript.m_pVMNetworkAdapter->m_switchUuid );
	}
	SAFE_FREE( p.m_VSphereRestore_JobScript.m_pVMNetworkAdapter );

	if (p.m_pChildVMs != NULL)
	{
		for (ULONG jj = 0; jj < p.m_ulChildVMCount; jj++)
			freeAFNODE(p.m_pChildVMs[jj]);
	}
	SAFE_FREE(p.m_pChildVMs);

	if (p.m_pUnderlyingHypervisor != NULL)
	{
		for (int i = 0; i < p.m_UnderlyingHypervisorCount; i++)
		{
			SAFE_FREE(p.m_pUnderlyingHypervisor[i].ServerName);
			SAFE_FREE(p.m_pUnderlyingHypervisor[i].Username);
			SAFE_FREE(p.m_pUnderlyingHypervisor[i].Password);
			SAFE_FREE(p.m_pUnderlyingHypervisor[i].Protocol);
		}

		SAFE_FREE(p.m_pUnderlyingHypervisor);
		p.m_UnderlyingHypervisorCount = 0;
	}

	SAFE_FREE(p.m_storagePolicyGuid);
	SAFE_FREE(p.m_storagePolicyName);
	SAFE_FREE(p.TransportMode); //<sonmi01>2014-8-20 ###???
}

void FreeAFJOBSCRIPT(PAFJOBSCRIPT pafJS)
{
	SAFE_FREE( pafJS->pwszDestPath );
	SAFE_FREE( pafJS->pwszUserName );
	SAFE_FREE( pafJS->pwszPassword );
	SAFE_FREE( pafJS->pwszUserName_2 );
	SAFE_FREE( pafJS->pwszPassword_2 );
	SAFE_FREE( pafJS->pwszComments );
	SAFE_FREE( pafJS->pwszBeforeJob );
	SAFE_FREE( pafJS->pwszAfterJob );
	SAFE_FREE( pafJS->pwszPostSnapshotCmd );
	SAFE_FREE( pafJS->pwszPrePostUser );
	SAFE_FREE( pafJS->pwszPrePostPassword );
	SAFE_FREE( pafJS->pwszEncryptPassword );
	SAFE_FREE( pafJS->pwszVDiskPassword );
	SAFE_FREE( pafJS->pwszEncryptPasswordCopySession );
	SAFE_FREE( pafJS->launcherInstanceUUID );
	SAFE_FREE( pafJS->pRPSName );
	SAFE_FREE( pafJS->pPolicyName );
	SAFE_FREE( pafJS->pPolicyGUID );
	SAFE_FREE( pafJS->pSourceDataStore );
	SAFE_FREE( pafJS->pSourceDataStoreName);
	SAFE_FREE( pafJS->pTargetDataStore);
	SAFE_FREE( pafJS->pTargetDataStoreName);

	if(pafJS->pAFNodeList != NULL)
	{
		for(ULONG i=0;i<pafJS->nNodeItems;i++)
			freeAFNODE(pafJS->pAFNodeList[i]);
	}
	SAFE_FREE( pafJS->pAFNodeList );
}


int AddRestorePoint2JMap(JNIEnv *env, jobject* jRestMap, const RESTORE_POINT_ITEM * item, const wchar_t* vmInstUuid, const wchar_t* date)
{
	jclass class_RestorePoint = env->FindClass("com/ca/arcflash/service/jni/model/JRestorePoint");

	jmethodID mid_RestorePoint_constructor = env->GetMethodID(class_RestorePoint, "<init>", "()V");
	jobject jRestorePoint = env->NewObject(class_RestorePoint, mid_RestorePoint_constructor);

	jfieldID field_sessionID = env->GetFieldID(class_RestorePoint, "sessionID", "Ljava/lang/String;");
	jstring sessionID = WCHARToJString(env, (wchar_t*)item->strId.c_str());
	env->SetObjectField(jRestorePoint, field_sessionID, sessionID);
	if (sessionID != NULL) env->DeleteLocalRef(sessionID);

	jfieldID field_date = env->GetFieldID(class_RestorePoint, "date", "Ljava/lang/String;");
	jstring dateValue = WCHARToJString(env, date);
	env->SetObjectField(jRestorePoint, field_date, dateValue);
	if (dateValue != NULL) env->DeleteLocalRef(dateValue);

	jfieldID field_time = env->GetFieldID(class_RestorePoint, "time", "Ljava/lang/String;");
	jstring time = WCHARToJString(env, (wchar_t*)item->strDetailTime.c_str());
	env->SetObjectField(jRestorePoint, field_time, time);
	if (time != NULL) env->DeleteLocalRef(time);

	jfieldID field_status = env->GetFieldID(class_RestorePoint, "backupStatus", "Ljava/lang/String;");
	jstring status = WCHARToJString(env, (wchar_t*)item->strBakStatus.c_str());
	env->SetObjectField(jRestorePoint, field_status, status);
	if (status != NULL) env->DeleteLocalRef(status);

	jfieldID field_type = env->GetFieldID(class_RestorePoint, "backupType", "Ljava/lang/String;");
	jstring type = WCHARToJString(env, (wchar_t*)item->strBakType.c_str());
	env->SetObjectField(jRestorePoint, field_type, type);
	if (type != NULL) env->DeleteLocalRef(type);

	jfieldID field_size = env->GetFieldID(class_RestorePoint, "dataSize", "Ljava/lang/String;");
	jstring size = WCHARToJString(env, (wchar_t*)item->strDataSizeKB.c_str());
	env->SetObjectField(jRestorePoint, field_size, size);
	if (size != NULL) env->DeleteLocalRef(size);

	jfieldID field_name = env->GetFieldID(class_RestorePoint, "name", "Ljava/lang/String;");
	jstring name = WCHARToJString(env, (wchar_t*)item->strBakName.c_str());
	env->SetObjectField(jRestorePoint, field_name, name);
	if (status != NULL) env->DeleteLocalRef(name);


	jfieldID field_path = env->GetFieldID(class_RestorePoint, "path", "Ljava/lang/String;");
	jstring path = WCHARToJString(env, (wchar_t*)item->strPath.c_str());
	env->SetObjectField(jRestorePoint, field_path, path);
	if (status != NULL) env->DeleteLocalRef(path);

	jfieldID field_encryptType = env->GetFieldID(class_RestorePoint, "encryptType", "Ljava/lang/String;");
	jstring encryptType = WCHARToJString(env, (wchar_t*)item->strEncryptType.c_str());
	env->SetObjectField(jRestorePoint, field_encryptType, encryptType);

	jfieldID field_encryptPwdHash = env->GetFieldID(class_RestorePoint, "encryptPasswordHash", "Ljava/lang/String;");
	jstring encryptPwdHash = WCHARToJString(env, (wchar_t*)item->strEncryptPasswordHash.c_str());
	env->SetObjectField(jRestorePoint, field_encryptPwdHash, encryptPwdHash);

	jfieldID field_sessionGuid = env->GetFieldID(class_RestorePoint, "sessionGuid", "Ljava/lang/String;");
	jstring sessionGuid = WCHARToJString(env, (wchar_t*)item->strSessionGUID.c_str());
	env->SetObjectField(jRestorePoint, field_sessionGuid, sessionGuid);

	jfieldID field_majorVersion = env->GetFieldID(class_RestorePoint, "majorVersion", "J");
	env->SetLongField(jRestorePoint, field_majorVersion, item->dwMajVer);

	jfieldID field_minorVersion = env->GetFieldID(class_RestorePoint, "minorVersion", "J");
	env->SetLongField(jRestorePoint, field_minorVersion, item->dwMinVer);

	jfieldID field_fsCatalogStatus = env->GetFieldID(class_RestorePoint, "fsCatalogStaus", "I");
	env->SetIntField(jRestorePoint, field_fsCatalogStatus, item->dwFsCatStatus);

	jfieldID field_backupSetFlag = env->GetFieldID(class_RestorePoint, "backupSetFlag", "I");
	env->SetIntField(jRestorePoint, field_backupSetFlag, item->dwBKSetFlag);

	jfieldID field_dwBKAdvSchFlag = env->GetFieldID(class_RestorePoint, "dwBKAdvSchFlag", "I");
	env->SetIntField(jRestorePoint, field_dwBKAdvSchFlag, item->dwBKAdvSchFlag);

	jfieldID field_items = env->GetFieldID(class_RestorePoint, "items", "Ljava/util/List;");
	jobject itemList = env->GetObjectField(jRestorePoint, field_items);

	jfieldID field_ullScheduledTime = env->GetFieldID(class_RestorePoint, "ullScheduledTime", "J");
	env->SetLongField(jRestorePoint, field_ullScheduledTime, item->ullScheduledTime);

	jfieldID field_backupDest = env->GetFieldID(class_RestorePoint, "backupDest", "Ljava/lang/String;");
	jstring backupDest = WCHARToJString(env, (wchar_t*)item->strBakDest.c_str());
	env->SetObjectField(jRestorePoint, field_backupDest, backupDest);

	jfieldID field_logicalSize = env->GetFieldID(class_RestorePoint, "logicalSize", "Ljava/lang/String;");
	jstring logicalSize = WCHARToJString(env, (wchar_t*)item->strTransferDataSizeKB.c_str());
	env->SetObjectField(jRestorePoint, field_logicalSize, logicalSize);

	jmethodID mid_JRestorePoint_setDefaultSessPwd = env->GetMethodID(class_RestorePoint, "setDefaultSessPwd", "(Z)V");
	env->CallVoidMethod(jRestorePoint, mid_JRestorePoint_setDefaultSessPwd, (jboolean)0);

	// BEGIN [9/17/2014 zhahu03]
	jfieldID field_dwAgentOSType = env->GetFieldID(class_RestorePoint, "dwAgentOSType", "I");
	env->SetIntField(jRestorePoint, field_dwAgentOSType, item->dwAgentOSType);

	jfieldID field_dwAgentBackupType = env->GetFieldID(class_RestorePoint, "dwAgentBackupType", "I");
	env->SetIntField(jRestorePoint, field_dwAgentBackupType, item->dwAgentBackupType);

	jfieldID field_dwVMGuestOsType = env->GetFieldID(class_RestorePoint, "dwVMGuestOsType", "I");
	env->SetIntField(jRestorePoint, field_dwVMGuestOsType, item->dwVMGuestOsType);

	jfieldID field_dwVMHypervisor = env->GetFieldID(class_RestorePoint, "dwVMHypervisor", "I");
	env->SetIntField(jRestorePoint, field_dwVMHypervisor, item->dwVMHypervisor);
	// END [9/17/2014 zhahu03] 

	if (!item->vBakItem.empty())
	{
		AddVBACKUP_ITEM2List(env, itemList, item->vBakItem);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	//jclass mapClass = env->FindClass("java/util/Map");
	jclass mapClass = env->GetObjectClass(*jRestMap);
	jmethodID putMethodID = env->GetMethodID(mapClass, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

	jstring jstrKey = WCHARToJString(env, vmInstUuid);
	env->CallVoidMethod(*jRestMap, putMethodID, jstrKey, jRestorePoint);

	///////////////////////////////////////////////////////////////////////////////////////////////
	if (jRestorePoint != NULL) env->DeleteLocalRef(jRestorePoint);
	if (class_RestorePoint != NULL) env->DeleteLocalRef(class_RestorePoint);

	return 0;
}

int AddRestorePoint2List(JNIEnv *env, jobject* list, VRESTORE_POINT_ITEM::iterator item, wchar_t* date)
{
	jclass class_RestorePoint = env->FindClass("com/ca/arcflash/service/jni/model/JRestorePoint");

	jmethodID mid_RestorePoint_constructor = env->GetMethodID(class_RestorePoint, "<init>", "()V");
	jobject jRestorePoint = env->NewObject(class_RestorePoint, mid_RestorePoint_constructor);
	
	jfieldID field_sessionID = env->GetFieldID(class_RestorePoint, "sessionID", "Ljava/lang/String;");
	jstring sessionID = WCHARToJString(env, (wchar_t*)item->strId.c_str());
	env->SetObjectField(jRestorePoint, field_sessionID, sessionID);
	if ( sessionID!=NULL) env->DeleteLocalRef(sessionID);

	jfieldID field_date	= env->GetFieldID(class_RestorePoint, "date", "Ljava/lang/String;");
	jstring dateValue = WCHARToJString(env, date);
	env->SetObjectField(jRestorePoint, field_date, dateValue);
	if ( dateValue!=NULL) env->DeleteLocalRef(dateValue);

	jfieldID field_time	= env->GetFieldID(class_RestorePoint, "time", "Ljava/lang/String;");
	jstring time = WCHARToJString(env, (wchar_t*)item->strDetailTime.c_str());
	env->SetObjectField(jRestorePoint, field_time, time);
	if ( time!=NULL) env->DeleteLocalRef(time);

	jfieldID field_status	= env->GetFieldID(class_RestorePoint, "backupStatus", "Ljava/lang/String;");
	jstring status = WCHARToJString(env, (wchar_t*)item->strBakStatus.c_str());
	env->SetObjectField(jRestorePoint, field_status, status);
	if ( status!=NULL) env->DeleteLocalRef(status);

	jfieldID field_type	= env->GetFieldID(class_RestorePoint, "backupType", "Ljava/lang/String;");
	jstring type = WCHARToJString(env, (wchar_t*)item->strBakType.c_str());
	env->SetObjectField(jRestorePoint, field_type, type);
	if ( type!=NULL) env->DeleteLocalRef(type);

	jfieldID field_size	= env->GetFieldID(class_RestorePoint, "dataSize", "Ljava/lang/String;");
	jstring size = WCHARToJString(env, (wchar_t*)item->strDataSizeKB.c_str());
	env->SetObjectField(jRestorePoint, field_size, size);
	if ( size!=NULL) env->DeleteLocalRef(size);

	jfieldID field_name	= env->GetFieldID(class_RestorePoint, "name", "Ljava/lang/String;");
	jstring name = WCHARToJString(env, (wchar_t*)item->strBakName.c_str());
	env->SetObjectField(jRestorePoint, field_name, name);
	if ( status!=NULL) env->DeleteLocalRef(name);
	
	
	jfieldID field_path	= env->GetFieldID(class_RestorePoint, "path", "Ljava/lang/String;");
	jstring path = WCHARToJString(env, (wchar_t*)item->strPath.c_str());
	env->SetObjectField(jRestorePoint, field_path, path);
	if ( status!=NULL) env->DeleteLocalRef(path);

	jfieldID field_encryptType = env->GetFieldID(class_RestorePoint, "encryptType", "Ljava/lang/String;");
	jstring encryptType = WCHARToJString(env, (wchar_t*)item->strEncryptType.c_str());
	env->SetObjectField(jRestorePoint, field_encryptType, encryptType);

	jfieldID field_encryptPwdHash = env->GetFieldID(class_RestorePoint, "encryptPasswordHash", "Ljava/lang/String;");
	jstring encryptPwdHash = WCHARToJString(env, (wchar_t*)item->strEncryptPasswordHash.c_str());
	env->SetObjectField(jRestorePoint, field_encryptPwdHash, encryptPwdHash);

	jfieldID field_sessionGuid = env->GetFieldID(class_RestorePoint, "sessionGuid", "Ljava/lang/String;");
	jstring sessionGuid = WCHARToJString(env, (wchar_t*)item->strSessionGUID.c_str());
	env->SetObjectField(jRestorePoint, field_sessionGuid, sessionGuid);

	jfieldID field_majorVersion = env->GetFieldID(class_RestorePoint, "majorVersion", "J");
	env->SetLongField(jRestorePoint, field_majorVersion, item->dwMajVer);

	jfieldID field_minorVersion = env->GetFieldID(class_RestorePoint, "minorVersion", "J");
	env->SetLongField(jRestorePoint, field_minorVersion, item->dwMinVer);

	jfieldID field_fsCatalogStatus = env->GetFieldID(class_RestorePoint,"fsCatalogStaus", "I");
	env->SetIntField(jRestorePoint, field_fsCatalogStatus, item->dwFsCatStatus);

	jfieldID field_backupSetFlag = env->GetFieldID(class_RestorePoint, "backupSetFlag", "I");
	env->SetIntField(jRestorePoint, field_backupSetFlag, item->dwBKSetFlag);
	
	jfieldID field_dwBKAdvSchFlag = env->GetFieldID(class_RestorePoint, "dwBKAdvSchFlag", "I");
	env->SetIntField(jRestorePoint, field_dwBKAdvSchFlag, item->dwBKAdvSchFlag);

	jfieldID field_items = env->GetFieldID(class_RestorePoint, "items", "Ljava/util/List;");
	jobject itemList = env->GetObjectField(jRestorePoint, field_items);

	jfieldID field_ullScheduledTime = env->GetFieldID(class_RestorePoint, "ullScheduledTime", "J");
	env->SetLongField(jRestorePoint, field_ullScheduledTime, item->ullScheduledTime);

	jfieldID field_backupDest = env->GetFieldID(class_RestorePoint, "backupDest", "Ljava/lang/String;");
	jstring backupDest = WCHARToJString(env, (wchar_t*)item->strBakDest.c_str());
	env->SetObjectField(jRestorePoint, field_backupDest, backupDest);

	jfieldID field_logicalSize = env->GetFieldID(class_RestorePoint, "logicalSize", "Ljava/lang/String;");
	jstring logicalSize = WCHARToJString(env, (wchar_t*)item->strTransferDataSizeKB.c_str());
	env->SetObjectField(jRestorePoint, field_logicalSize, logicalSize);

	jmethodID mid_JRestorePoint_setDefaultSessPwd = env->GetMethodID(class_RestorePoint, "setDefaultSessPwd", "(Z)V");
	env->CallVoidMethod(jRestorePoint, mid_JRestorePoint_setDefaultSessPwd, (jboolean)0);

	// BEGIN [9/17/2014 zhahu03]
	jfieldID field_dwAgentOSType = env->GetFieldID(class_RestorePoint, "dwAgentOSType", "I");
	env->SetIntField(jRestorePoint, field_dwAgentOSType, item->dwAgentOSType);

	jfieldID field_dwAgentBackupType = env->GetFieldID(class_RestorePoint, "dwAgentBackupType", "I");
	env->SetIntField(jRestorePoint, field_dwAgentBackupType, item->dwAgentBackupType);

	jfieldID field_dwVMGuestOsType = env->GetFieldID(class_RestorePoint, "dwVMGuestOsType", "I");
	env->SetIntField(jRestorePoint, field_dwVMGuestOsType, item->dwVMGuestOsType);

	jfieldID field_dwVMHypervisor = env->GetFieldID(class_RestorePoint, "dwVMHypervisor", "I");
	env->SetIntField(jRestorePoint, field_dwVMHypervisor, item->dwVMHypervisor);
	// END [9/17/2014 zhahu03] 

	jfieldID field_nodeUuid = env->GetFieldID(class_RestorePoint, "nodeUuid", "Ljava/lang/String;");
	if (field_nodeUuid)
	{
		jstring nodeUuid = WCHARToJString(env, (wchar_t*)item->strNodeUuid.c_str());
		env->SetObjectField(jRestorePoint, field_nodeUuid, nodeUuid);
	}

	//added for vm recovery
	jfieldID field_vmName = env->GetFieldID(class_RestorePoint, "vmName", "Ljava/lang/String;");
	if (field_vmName)
	{
		jstring vmName = WCHARToJString(env, (wchar_t*)item->strVMName.c_str());
		env->SetObjectField(jRestorePoint, field_vmName, vmName);
	}

	jfieldID field_vmvCenter = env->GetFieldID(class_RestorePoint, "vmvCenter", "Ljava/lang/String;");
	if (field_vmvCenter)
	{
		jstring vmvCenter = WCHARToJString(env, (wchar_t*)item->strVMvCenter.c_str());
		env->SetObjectField(jRestorePoint, field_vmvCenter, vmvCenter);
	}

	jfieldID field_vmEsxHost = env->GetFieldID(class_RestorePoint, "vmEsxHost", "Ljava/lang/String;");
	if (field_vmEsxHost)
	{
		jstring vmEsxHost = WCHARToJString(env, (wchar_t*)item->strVMEsxHost.c_str());
		env->SetObjectField(jRestorePoint, field_vmEsxHost, vmEsxHost);
	}

	if (!item->vBakItem.empty())
	{
		AddVBACKUP_ITEM2List(env, itemList, item->vBakItem);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	jclass class_ArrayList = env->GetObjectClass(*list);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
	env->CallBooleanMethod(*list, id_ArrayList_add, jRestorePoint);

	////////////////////////////////////////////////////////////////////////////////////////////////
	if (jRestorePoint != NULL) env->DeleteLocalRef(jRestorePoint);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);
	if (class_RestorePoint != NULL) env->DeleteLocalRef(class_RestorePoint);

	return 0;
}

wchar_t* GetStringFromField(JNIEnv *env, jobject* jObj, jfieldID field)
{
	if (jObj == NULL) return NULL;

	if (field == NULL) return NULL;

	jstring jstr = (jstring)env->GetObjectField(*jObj, field);

	wchar_t* tmp = JStringToWCHAR(env, jstr);
	if (tmp != NULL) {		
		size_t len =(wcslen(tmp)+1) * sizeof(wchar_t);
		wchar_t* destination = (wchar_t *) malloc(len);
		wcscpy_s(destination, len/sizeof(wchar_t), tmp);
		free(tmp);
		return destination;
	}
	return NULL;
}

#define MAKE_ULONGLONG(high, low) ((((ULONGLONG)((ULONG)low)) << 32) + (ULONGLONG)((ULONG)high))
void processJExchSubItemList(JNIEnv *env, PAFVOLITEMAPPCOMP afRestoreItemAppComp, jobject nodeList)
{
	if(afRestoreItemAppComp != NULL && nodeList != NULL)
	{
		jclass class_List = env->GetObjectClass(nodeList);
		jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");
		jint volCnt = env->CallIntMethod(nodeList, method_List_size);

		if(volCnt > 0)
		{
			PAFEXCHSUBITEM pExchSubItemList = (PAFEXCHSUBITEM)malloc(sizeof(AFEXCHSUBITEM) * volCnt);
			if (pExchSubItemList != NULL)
			{
				memset(pExchSubItemList, 0, sizeof(AFEXCHSUBITEM) * volCnt);

				jclass class_Entry		= env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptExchSubItem");
				jmethodID id_List_get	= env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
				jfieldID field_ulItemType		= env->GetFieldID(class_Entry, "ulItemType", "J");
				jfieldID field_pwszItemName		= env->GetFieldID(class_Entry, "pwszItemName", "Ljava/lang/String;");
				jfieldID field_pwszMailboxName	= env->GetFieldID(class_Entry, "pwszMailboxName", "Ljava/lang/String;");
			/*	jfieldID field_ul_lMailboxID	= env->GetFieldID(class_Entry, "ul_lMailboxID", "J");
				jfieldID field_ul_hMailboxID	= env->GetFieldID(class_Entry, "ul_hMailboxID", "J");
				jfieldID field_ul_lFolderID		= env->GetFieldID(class_Entry, "ul_lFolderID", "J");
				jfieldID field_ul_hFolderID		= env->GetFieldID(class_Entry, "ul_hFolderID", "J");
				jfieldID field_ul_lMsgID		= env->GetFieldID(class_Entry, "ul_lMsgID", "J");				
				jfieldID field_ul_hMsgID		= env->GetFieldID(class_Entry, "ul_hMsgID", "J");*/
				jfieldID field_pwszDescription	= env->GetFieldID(class_Entry, "pwszDescription", "Ljava/lang/String;");
				jfieldID field_pwszExchangeObjectIDs	= env->GetFieldID(class_Entry, "pwszExchangeObjectIDs", "Ljava/lang/String;");

				PAFEXCHSUBITEM pExchSubItem = pExchSubItemList;
				for(jint i=0;i < volCnt;i++, pExchSubItem++)
				{	 
					jobject entryObject = (jobject)env->CallObjectMethod(nodeList, id_List_get,i);
					pExchSubItem->ulItemType		= (ULONG)env->GetLongField(entryObject,field_ulItemType);
					pExchSubItem->pwszItemName		= GetStringFromField(env, &entryObject, field_pwszItemName);
					pExchSubItem->pwszMailboxName	= GetStringFromField(env, &entryObject, field_pwszMailboxName);
					pExchSubItem->pwszExchangeObjectIDs	= GetStringFromField(env, &entryObject, field_pwszExchangeObjectIDs);

		/*			ULONG nHigh = 0, nLow = 0;
					nLow	= (ULONG)env->GetLongField(entryObject, field_ul_lMailboxID);
					nHigh	= (ULONG)env->GetLongField(entryObject, field_ul_hMailboxID);
					pExchSubItem->ullMailboxID = MAKE_ULONGLONG(nHigh, nLow);					

					nLow		= (ULONG)env->GetLongField(entryObject, field_ul_lFolderID);										
					nHigh		= (ULONG)env->GetLongField(entryObject, field_ul_hFolderID);
					pExchSubItem->ullFolderID = MAKE_ULONGLONG(nHigh, nLow);

					nLow			= (ULONG)env->GetLongField(entryObject, field_ul_lMsgID);															
					nHigh			= (ULONG)env->GetLongField(entryObject, field_ul_hMsgID);
					pExchSubItem->ullMsgID = MAKE_ULONGLONG(nHigh, nLow);*/

					pExchSubItem->pwszDescription	= GetStringFromField(env, &entryObject, field_pwszDescription);
				}

				if (class_Entry != NULL) 
				{
					env->DeleteLocalRef(class_Entry);
				}

				afRestoreItemAppComp->pExchSubItemList = pExchSubItemList;
			}
		}

		if (class_List != NULL) env->DeleteLocalRef(class_List);
	}
	
}

void ProcessJADItemList(JNIEnv *env, PAFVOLITEMAPPCOMP afRestoreItemAppComp, jobject ListX)
{
	jclass cls_list = NULL;
	jclass cls_ADItem = NULL;

	do 
	{
		if(NULL ==env || NULL == afRestoreItemAppComp || NULL ==ListX)
		{
			break;
		}

		cls_list = env->GetObjectClass(ListX);
		jmethodID method_List_size = env->GetMethodID(cls_list, "size", "()I");
		if(NULL == method_List_size)
		{
			break;
		}

		jint jnSize = env->CallIntMethod(ListX, method_List_size);
		if(0 ==jnSize)
		{
			logObj.LogW(LL_ERR,  0, L"%s: Empty AD Item List!!! ", __WFUNCTION__);
			break;
		} 

		PAFADSubITEM pADSubList = (PAFADSubITEM)malloc(sizeof(AFADSubITEM) * jnSize);
		if(NULL==pADSubList)
		{
			logObj.LogW(LL_ERR,  0, L"%s: Failed to malloc resource:%d", __WFUNCTION__, jnSize);
			break;
		}

		memset(pADSubList, 0, sizeof(AFADSubITEM) * jnSize);

		cls_ADItem = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptADItem");
		if(NULL ==cls_ADItem)
		{
			logObj.LogW(LL_ERR,  0, L"%s: Failed to FindCalss(com/ca/arcflash/webservice/jni/model/JJobScriptADItem).", __WFUNCTION__);
			break;
		}
		 
		jfieldID id_id;
		jfieldID id_isAllChild;
		jfieldID id_isAllATTr;
		jfieldID id_ATTrNum;
		jfieldID id_ATTrs;

		id_id         = env->GetFieldID(cls_ADItem, "id", "J");
		id_isAllChild = env->GetFieldID(cls_ADItem, "allChild", "Z");
		id_isAllATTr  = env->GetFieldID(cls_ADItem, "allAttribute", "Z");
		id_ATTrNum    = env->GetFieldID(cls_ADItem, "attrNumber", "J");
		id_ATTrs      = env->GetFieldID(cls_ADItem, "attrNames", "Ljava/lang/String;");

		if(NULL ==id_id || NULL ==id_isAllATTr || NULL == id_isAllATTr || NULL==id_ATTrNum || NULL ==id_ATTrs)
		{
			logObj.LogW(LL_ERR,  0, L"%s: can't find related field id in class(com/ca/arcflash/webservice/jni/model/JJobScriptADItem).", __WFUNCTION__);
			break;
		}

		jmethodID id_List_get	= env->GetMethodID(cls_list, "get", "(I)Ljava/lang/Object;");
		if(NULL ==id_List_get)
		{
			logObj.LogW(LL_ERR,  0, L"%s:  env->GetMethodID(get, (I)Ljava/lang/Object;)", __WFUNCTION__);
			break;
		}
		 
		for (int i=0; i < jnSize; i++)
		{
			AFADSubITEM & XItem = pADSubList[i];
			jobject objItem = (jobject)env->CallObjectMethod(ListX, id_List_get,i);

			XItem.dwDnt = (DWORD)env->GetLongField(objItem, id_id);
			XItem.IsAllChild = (BOOL) env->GetBooleanField(objItem, id_isAllChild);
			XItem.IsAllATTr  = (BOOL) env->GetBooleanField(objItem, id_isAllATTr);

			if(0 == XItem.IsAllATTr)
			{
				XItem.dwATTrNum = (DWORD) env->GetLongField(objItem, id_ATTrNum);
				if(XItem.dwATTrNum)
				{
					XItem.pwszATTrNames	= GetStringFromField(env, &objItem, id_ATTrs);
				}
			}
 		}

		//afRestoreItemAppComp->uADItemNum = jnSize;
		afRestoreItemAppComp->pADItemList = pADSubList;
	} while (0);

	if(cls_ADItem)
	{
		env->DeleteLocalRef(cls_ADItem);
		cls_ADItem = NULL;
	}

	if(cls_list)
	{
		env->DeleteLocalRef(cls_list);
		cls_list = NULL;
	}

	return;
}

void processJVolAppItemList(JNIEnv *env, PAFRESTVOLAPP afRestoreVolApp, jobject nodeList, int type)
{
	if(nodeList != NULL)
	{
		jclass class_List = env->GetObjectClass(nodeList);
		jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");
		jint volCnt = env->CallIntMethod(nodeList, method_List_size);

		if(volCnt > 0)
		{
			size_t stLen = sizeof(AFVOLITEMAPPCOMP) * volCnt;
			PAFVOLITEMAPPCOMP pafVolItemAppComp = NULL;
			if (type == 0){
				afRestoreVolApp->pVolItemAppCompList = (PAFVOLITEMAPPCOMP)malloc(stLen);
				memset(afRestoreVolApp->pVolItemAppCompList,0,stLen);
				pafVolItemAppComp = afRestoreVolApp->pVolItemAppCompList;
			}
			else
			{
				afRestoreVolApp->pDestItemList = (PAFVOLITEMAPPCOMP)malloc(stLen);
				memset(afRestoreVolApp->pDestItemList,0,stLen);
				pafVolItemAppComp = afRestoreVolApp->pDestItemList;
			}
			

			//AD GRT
			pafVolItemAppComp->uADItemNum =0;
			pafVolItemAppComp->pADItemList =NULL;

			jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptVolAppItem");
			jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
			jfieldID field_path	= env->GetFieldID(class_Entry, "pwszFileorDir", "Ljava/lang/String;");
			jfieldID field_Option = env->GetFieldID(class_Entry, "fOptions", "I");
			jfieldID field_pwszCompRestPath	= env->GetFieldID(class_Entry, "pwszCompRestPath", "Ljava/lang/String;");
			jfieldID field_pwszCompRestName	= env->GetFieldID(class_Entry, "pwszCompRestName", "Ljava/lang/String;");
			jfieldID field_nExchSubItemList	= env->GetFieldID(class_Entry, "nExchSubItemList", "J");
			jfieldID field_pExchSubItemList	= env->GetFieldID(class_Entry, "pExchSubItemList", "Ljava/util/List;");
		 
			jfieldID field_NumADList	= env->GetFieldID(class_Entry, "uADItemNum", "J");
			jfieldID field_ADSubItemList= env->GetFieldID(class_Entry, "pADItemList", "Ljava/util/List;");

			//////////////////////////////////////////////////////////////////////////
			//Demo code
			/* 
			{
				PAGRT_RESTORE_ADITEM pItems = new AGRT_RESTORE_ADITEM[2];
				WCHAR * pwszName  = new WCHAR[100];
				memset(pwszName, 0, 200);
				wcscpy(pwszName, L"cn;distinguishedname;description;");
				memset(pItems, 0, 2 * sizeof(AGRT_RESTORE_ADITEM));
				
				pItems[0].dwDnt = 4123;
				pItems[0].IsAllChild = 1;
				pItems[0].IsAllATTr = TRUE;
				pItems[0].dwATTrNum =0;
				 
				pItems[1].dwDnt = 3918;
				pItems[1].IsAllChild = 0;
				pItems[1].IsAllATTr = 0;
				pItems[1].dwATTrNum =3;
				pItems[1].pwszATTrNames = pwszName;
				pafVolItemAppComp->uADItemNum =2;
				pafVolItemAppComp->pADItemList = pItems;
				  
			} 
			*/
			//////////////////////////////////////////////////////////////////////////

			for(jint i=0;i < volCnt;i++,pafVolItemAppComp++)
			{	 
				jobject entryObject = (jobject)env->CallObjectMethod(nodeList, id_List_get,i);
				pafVolItemAppComp->pwszFileorDir = GetStringFromField(env, &entryObject,field_path);
				pafVolItemAppComp->fOptions = (ULONG)env->GetIntField(entryObject, field_Option);
				pafVolItemAppComp->pwszCompRestPath = GetStringFromField(env, &entryObject, field_pwszCompRestPath);
				pafVolItemAppComp->pwszCompRestName = GetStringFromField(env, &entryObject, field_pwszCompRestName);
				pafVolItemAppComp->nExchSubItemList = (ULONG)env->GetLongField(entryObject, field_nExchSubItemList);
				processJExchSubItemList(env, pafVolItemAppComp, env->GetObjectField(entryObject, field_pExchSubItemList) );

				if( (NULL ==field_NumADList) ||( NULL == field_ADSubItemList) )
				{
					logObj.LogW(LL_WAR, 0, L"%s can' find ad list field id %d %d", __WFUNCTION__, field_NumADList, field_ADSubItemList);
					continue;
				}

				pafVolItemAppComp->uADItemNum = (ULONG)env->GetLongField(entryObject, field_NumADList);
				if(0 < pafVolItemAppComp->uADItemNum)
				{
					ProcessJADItemList(env, pafVolItemAppComp,  env->GetObjectField(entryObject, field_ADSubItemList) );
				}
			}

			if (class_Entry != NULL) env->DeleteLocalRef(class_Entry);
		}

		if (class_List != NULL) env->DeleteLocalRef(class_List);
	}
	else
	{
		if (type==0)
			afRestoreVolApp->pVolItemAppCompList = NULL;
		else
			afRestoreVolApp->pDestItemList = NULL;
	}
}

void processJRestoreOption_Exch(JNIEnv *env, PAFRESTVOLAPP afRestoreVolApp, jobject restoreOption_Exch)
{
	if (afRestoreVolApp != NULL && restoreOption_Exch != NULL)
	{
		PAFRESTOREOPTION_EXCH pRestoreOption_Exch = (PAFRESTOREOPTION_EXCH) malloc(sizeof(AFRESTOREOPTION_EXCH));
		if (pRestoreOption_Exch != NULL)
		{
			memset(pRestoreOption_Exch, 0, sizeof(AFRESTOREOPTION_EXCH));

			jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptRestoreOptionExch");
			jfieldID field_ulOptions			= env->GetFieldID(class_Entry,"ulOptions", "J");
			jfieldID field_ulServerVersion		= env->GetFieldID(class_Entry,"ulServerVersion", "J");
			jfieldID field_pwszFolder			= env->GetFieldID(class_Entry,"pwszFolder", "Ljava/lang/String;");
			jfieldID field_pwszAlternateServer	= env->GetFieldID(class_Entry,"pwszAlternateServer", "Ljava/lang/String;");
			jfieldID field_pwszUser				= env->GetFieldID(class_Entry,"pwszUser", "Ljava/lang/String;");
			jfieldID field_pwszUserPW			= env->GetFieldID(class_Entry,"pwszUserPW", "Ljava/lang/String;");

			pRestoreOption_Exch->ulOptions			 = (ULONG)env->GetLongField(restoreOption_Exch, field_ulOptions);
			pRestoreOption_Exch->ulServerVersion	 = (ULONG)env->GetLongField(restoreOption_Exch, field_ulServerVersion);
			pRestoreOption_Exch->pwszFolder			 = GetStringFromField(env, &restoreOption_Exch, field_pwszFolder);
			pRestoreOption_Exch->pwszAlternateServer = GetStringFromField(env, &restoreOption_Exch, field_pwszAlternateServer);
			pRestoreOption_Exch->pwszUser			 = GetStringFromField(env, &restoreOption_Exch, field_pwszUser);
			pRestoreOption_Exch->pwszUserPW			 = GetStringFromField(env, &restoreOption_Exch, field_pwszUserPW);

			if (class_Entry != NULL)
			{
				env->DeleteLocalRef(class_Entry);
			}

			afRestoreVolApp->pRestoreOption_Exch = pRestoreOption_Exch;
		}
	}
}

DWORD ProcessJRestoreOption_AD(JNIEnv *env, PAFRESTVOLAPP afRestoreVolApp, jobject restoreOption_AD)
{
	DWORD dwRet =0;

	jclass cls_adOption = NULL;

	do 
	{
		afRestoreVolApp->pRestoreOption_AD = NULL;

		if(NULL ==env || NULL==afRestoreVolApp || NULL ==restoreOption_AD)
		{
			dwRet = E_INVALIDARG;
			break;
		}

		cls_adOption = env->GetObjectClass(restoreOption_AD);
		if(NULL==cls_adOption)
		{
			dwRet =2;
			break;
		}

		PAFRESTOREOPTION_AD adOption = (PAFRESTOREOPTION_AD) malloc( sizeof (AFRESTOREOPTION_AD) );

		if(NULL ==adOption)
		{
			dwRet = E_OUTOFMEMORY;
			break;
		}

		memset(adOption, 0, sizeof(AFRESTOREOPTION_AD ) );

		jfieldID id_option;
		id_option = env->GetFieldID(cls_adOption, "ulOptions", "J");

		if(NULL == id_option)
		{
			logObj.LogW(LL_ERR,  0, L"%s: Failed to GetFieldID(ulOptions).", __WFUNCTION__);
			dwRet = E_NOTIMPL;
			break;
		}

		adOption->options = (ULONG)env->GetLongField(restoreOption_AD, id_option);

		afRestoreVolApp->pRestoreOption_AD = adOption;

	} while (0);

	if(cls_adOption)
	{
		env->DeleteLocalRef(cls_adOption);
	}

	return dwRet;
}

void processJRestoreVolAppList(JNIEnv *env, PAFNODE afNode, jobject nodeList)
{
	if(nodeList != NULL)
	{
		jclass class_List = env->GetObjectClass(nodeList);
		jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");				
		jint volCnt = env->CallIntMethod(nodeList, method_List_size);

		if(volCnt > 0)
		{
			size_t stLen = sizeof(AFRESTVOLAPP) * volCnt;
			afNode->pRestoreVolumeAppList = (PAFRESTVOLAPP)malloc(stLen);
			memset(afNode->pRestoreVolumeAppList,0,stLen);
			PAFRESTVOLAPP pafresVol = afNode->pRestoreVolumeAppList;
			
			jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptRestoreVolApp");
			jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
			jfieldID field_ulFileSystem = env->GetFieldID(class_Entry, "ulFileSystem", "I");
			jfieldID field_ulSubSessNum = env->GetFieldID(class_Entry, "ulSubSessNum", "I");
			jfieldID field_pwszPath	= env->GetFieldID(class_Entry, "pwszPath", "Ljava/lang/String;");
			jfieldID field_nVolItemAppComp = env->GetFieldID(class_Entry, "nVolItemAppComp", "I");
			jfieldID field_pVolItemAppCompList = env->GetFieldID(class_Entry, "pVolItemAppCompList", "Ljava/util/List;");
			jfieldID field_nDestItemCount = env->GetFieldID(class_Entry, "nDestItemCount", "I");
			jfieldID field_pDestItemList = env->GetFieldID(class_Entry, "pDestItemList", "Ljava/util/List;");
			jfieldID field_pDestVolumeName = env->GetFieldID(class_Entry, "pDestVolumeName", "Ljava/lang/String;");
			jfieldID field_nFilterItems = env->GetFieldID(class_Entry, "nFilterItems", "I");
			jfieldID field_OnConflictMethod = env->GetFieldID(class_Entry, "OnConflictMethod", "I");
			jfieldID field_fOptions = env->GetFieldID(class_Entry, "fOptions", "I");
			jfieldID field_pRestoreOption_Exch = env->GetFieldID(class_Entry, "pRestoreOption_Exch", "Lcom/ca/arcflash/webservice/jni/model/JJobScriptRestoreOptionExch;");			
			 
			jfieldID filed_RestoreOption_AD = env->GetFieldID(class_Entry, "adOption", "Lcom/ca/arcflash/webservice/jni/model/JJobScriptRestoreOptionAD;");
			 
			for(jint i=0;i < volCnt;i++,pafresVol++)
			{	 
				jobject entryObject = env->CallObjectMethod(nodeList, id_List_get,i);
				pafresVol->ulFileSystem = (ULONG)env->GetIntField(entryObject, field_ulFileSystem);
				//Debug <
				//pafresVol->ulFileSystem = 5000 + 168; AD_GRT
				//Debug >
				pafresVol->ulSubSessNum = (ULONG)env->GetIntField(entryObject, field_ulSubSessNum);
				pafresVol->pwszPath = GetStringFromField(env, &entryObject,field_pwszPath);
				pafresVol->nVolItemAppComp = (ULONG)env->GetIntField(entryObject, field_nVolItemAppComp);
				processJVolAppItemList(env,pafresVol, env->GetObjectField(entryObject,field_pVolItemAppCompList),0);
				pafresVol->nDestItemCount = (ULONG)env->GetIntField(entryObject, field_nDestItemCount);
				processJVolAppItemList(env,pafresVol, env->GetObjectField(entryObject,field_pDestItemList),1);
				pafresVol->pDestVolumeName = GetStringFromField(env, &entryObject,field_pDestVolumeName);
				pafresVol->nFilterItems = (ULONG)env->GetIntField(entryObject, field_nFilterItems);
				pafresVol->OnConflictMethod = (ULONG)env->GetIntField(entryObject, field_OnConflictMethod);
				pafresVol->fOptions = (ULONG)env->GetIntField(entryObject, field_fOptions);
				processJRestoreOption_Exch(env, pafresVol, env->GetObjectField(entryObject, field_pRestoreOption_Exch));

				if(NULL !=filed_RestoreOption_AD)
				{
					ProcessJRestoreOption_AD(env, pafresVol, env->GetObjectField(entryObject, filed_RestoreOption_AD) );
				}
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

void processJBackupVolAppList(JNIEnv *env, PAFNODE afNode, jobject nodeList)
{
	if(nodeList != NULL)
	{
		jclass class_List = env->GetObjectClass(nodeList);
		jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");				
		jint volCnt = env->CallIntMethod(nodeList, method_List_size);

		if(volCnt > 0)
		{
			size_t stLen = sizeof(AFBACKUPVOL) * volCnt;
			afNode->pBackupVolumeList = (PAFBACKUPVOL)malloc(stLen);
			memset(afNode->pBackupVolumeList,0,stLen);
			PAFBACKUPVOL pafresVol = afNode->pBackupVolumeList;
			
			jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptBackupVol");
			jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
			jfieldID field_pwszVolName	= env->GetFieldID(class_Entry, "pwszVolName", "Ljava/lang/String;");
			jfieldID field_ulFileSystem = env->GetFieldID(class_Entry, "ulFileSystem", "I");
			jfieldID field_ulSessionMethod = env->GetFieldID(class_Entry, "ulSessionMethod", "I");
			jfieldID field_nVolItemAppComp = env->GetFieldID(class_Entry, "nVolItemAppComp", "I");
			jfieldID field_pVolItemAppCompList = env->GetFieldID(class_Entry, "pVolItemAppCompList", "Ljava/util/List;");
			jfieldID field_fOptions = env->GetFieldID(class_Entry, "fOptions", "I");

			for(jint i=0;i < volCnt;i++,pafresVol++)
			{	 
				jobject entryObject = env->CallObjectMethod(nodeList, id_List_get,i);
				pafresVol->pwszVolName = GetStringFromField(env, &entryObject,field_pwszVolName);
				pafresVol->ulFileSystem = (ULONG)env->GetIntField(entryObject, field_ulFileSystem);
				pafresVol->ulSessionMethod = (ULONG)env->GetIntField(entryObject, field_ulSessionMethod);
				pafresVol->nVolItemAppComp = (ULONG)env->GetIntField(entryObject, field_nVolItemAppComp);
				//processJVolAppItemList(env,pafresVol->pVolItemAppCompList, env->GetObjectField(entryObject,field_pVolItemAppCompList));
				pafresVol->fOptions = (ULONG)env->GetIntField(entryObject, field_fOptions);
			}

			if (class_Entry != NULL) env->DeleteLocalRef(class_Entry);
		}

		if (class_List != NULL) env->DeleteLocalRef(class_List);
	}
	else
	{
		afNode->pBackupVolumeList = NULL;
	}
}

void processJBackupOption_Exch(JNIEnv *env, PAFNODE afNode, jobject backupOption_Exch)
{
	if (afNode != NULL && backupOption_Exch != NULL)
	{
		PAFBACKUPOPTION_EXCH pBackupOption_Exch = (PAFBACKUPOPTION_EXCH) malloc(sizeof(AFBACKUPOPTION_EXCH));
		if (pBackupOption_Exch != NULL)
		{
			memset(pBackupOption_Exch, 0, sizeof(AFBACKUPOPTION_EXCH));

			jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptBackupOptionExch");
			jfieldID field_ulOptions = env->GetFieldID(class_Entry,"ulOptions", "J");	
			pBackupOption_Exch->ulOptions = (ULONG)env->GetLongField(backupOption_Exch, field_ulOptions);

			if (class_Entry != NULL)
			{
				env->DeleteLocalRef(class_Entry);
			}

			afNode->pBackupOption_Exch = pBackupOption_Exch;
		}
	}
}

void processJBackupOption_Sp(JNIEnv *env, PAFNODE afNode, jobject backupOption_Sp)
{
	if (afNode != NULL && backupOption_Sp != NULL)
	{
		PAFBACKUPOPTION_SP pBackupOption_Sp = (PAFBACKUPOPTION_SP) malloc(sizeof(AFBACKUPOPTION_SP));
		if (pBackupOption_Sp != NULL)
		{
			memset(pBackupOption_Sp, 0, sizeof(AFBACKUPOPTION_SP));

			jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptBackupOptionSp");
			jfieldID field_ulOptions = env->GetFieldID(class_Entry,"ulOptions", "J");	
			pBackupOption_Sp->ulOptions = (ULONG)env->GetLongField(backupOption_Sp, field_ulOptions);

			if (class_Entry != NULL)
			{
				env->DeleteLocalRef(class_Entry);
			}

			afNode->pAFSharePointOption = pBackupOption_Sp;
		}
	}
}

void processJJobScriptNodeList(JNIEnv *env, PAFJOBSCRIPT pafJS, jobject nodeList)
{
	if(nodeList != NULL)
	{
		jclass class_List = env->GetObjectClass(nodeList);
		jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");				
		jint volCnt = env->CallIntMethod(nodeList, method_List_size);

		if(volCnt > 0)
		{
			size_t stLen = sizeof(AFNODE) * volCnt;
			pafJS->pAFNodeList = (PAFNODE)malloc(stLen);
			memset(pafJS->pAFNodeList,0,stLen);
			PAFNODE pafresVol = pafJS->pAFNodeList;
			
			jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptNode");
			jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
			jfieldID field_pwszNodeName	= env->GetFieldID(class_Entry, "pwszNodeName", "Ljava/lang/String;");
			jfieldID field_pwszNodeAddr	= env->GetFieldID(class_Entry, "pwszNodeAddr", "Ljava/lang/String;");
			jfieldID field_pwszUserName	= env->GetFieldID(class_Entry, "pwszUserName", "Ljava/lang/String;");
			jfieldID field_pwszUserPW	= env->GetFieldID(class_Entry, "pwszUserPW", "Ljava/lang/String;");
			jfieldID field_pwszSessPath	= env->GetFieldID(class_Entry, "pwszSessPath", "Ljava/lang/String;");
			jfieldID field_ulSessNum	= env->GetFieldID(class_Entry, "ulSessNum", "I");
			jfieldID field_nVolumeApp = env->GetFieldID(class_Entry, "nVolumeApp", "I");
			jfieldID field_pBackupVolumeList = env->GetFieldID(class_Entry, "pBackupVolumeList", "Ljava/util/List;");
			jfieldID field_pRestoreVolumeAppList = env->GetFieldID(class_Entry, "pRestoreVolumeAppList", "Ljava/util/List;");
			jfieldID field_nFilterItems = env->GetFieldID(class_Entry, "nFilterItems", "I");
			jfieldID field_fOptions = env->GetFieldID(class_Entry, "fOptions", "I");
			jfieldID field_dwEncryptType = env->GetFieldID(class_Entry, "dwEncryptTypeRestore", "I");
			jfieldID field_pwszEncryptPassword = env->GetFieldID(class_Entry, "pwszEncryptPasswordRestore", "Ljava/lang/String;");
            jfieldID field_pBackupOption_Exch = env->GetFieldID(class_Entry, "pBackupOption_Exch", "Lcom/ca/arcflash/webservice/jni/model/JJobScriptBackupOptionExch;");
			jfieldID field_pBackupOption_Sp = env->GetFieldID(class_Entry, "pBackupOption_Sp", "Lcom/ca/arcflash/webservice/jni/model/JJobScriptBackupOptionSp;");

		
			for(jint i=0;i < volCnt;i++,pafresVol++)
			{	 
				jobject entryObject = env->CallObjectMethod(nodeList, id_List_get,i);
				pafresVol->pwszNodeName = GetStringFromField(env, &entryObject,field_pwszNodeName);
				pafresVol->pwszNodeAddr = GetStringFromField(env, &entryObject,field_pwszNodeAddr);
				pafresVol->pwszUserName = GetStringFromField(env, &entryObject,field_pwszUserName);
				pafresVol->pwszUserPW = GetStringFromField(env, &entryObject,field_pwszUserPW);
				pafresVol->pwszSessPath = GetStringFromField(env, &entryObject,field_pwszSessPath);
				pafresVol->ulSessNum = env->GetIntField(entryObject, field_ulSessNum);
				pafresVol->nVolumeApp = env->GetIntField(entryObject, field_nVolumeApp);
				pafresVol->nFilterItems = env->GetIntField(entryObject, field_nFilterItems);
				pafresVol->fOptions = env->GetIntField(entryObject, field_fOptions);
				pafresVol->dwEncryptTypeRestore = env->GetIntField(entryObject, field_dwEncryptType);
				pafresVol->pwszEncryptPasswordRestore =  GetStringFromField(env, &entryObject, field_pwszEncryptPassword);								
				processJBackupVolAppList(env,pafresVol,env->GetObjectField(entryObject,field_pBackupVolumeList));
				processJRestoreVolAppList(env,pafresVol,env->GetObjectField(entryObject,field_pRestoreVolumeAppList));
                processJBackupOption_Exch(env, pafresVol, env->GetObjectField(entryObject, field_pBackupOption_Exch));
				processJBackupOption_Sp(env, pafresVol, env->GetObjectField(entryObject, field_pBackupOption_Sp));
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

// october sprint 
void processJJobScriptStorageApplianceList(JNIEnv *env, PAFJOBSCRIPT pafJS, jobject storageApplianceList)
{
	if (storageApplianceList != NULL)
	{
		jclass class_List = env->GetObjectClass(storageApplianceList);
		jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");
		jint ApplianceCnt = env->CallIntMethod(storageApplianceList, method_List_size);

		if (ApplianceCnt > 0)
		{
			size_t stLen = sizeof(AFSTORAGEAPPLIANCE)* ApplianceCnt;
			pafJS->pAFStorageApplianceList = (PAFSTORAGEAPPLIANCE)malloc(stLen);
			memset(pafJS->pAFStorageApplianceList, 0, stLen);
			PAFSTORAGEAPPLIANCE pStorageAppliances = pafJS->pAFStorageApplianceList;

			jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptStorageAppliance");
			jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
			//jfieldID field_pwszNodeName = env->GetFieldID(class_Entry, "pwszNodeName", "Ljava/lang/String;");
			
			//Dec sprint
			jfieldID field_pwszSystemMode = env->GetFieldID(class_Entry, "pwszSystemMode", "Ljava/lang/String;");
			jfieldID field_pwszDataIP = env->GetFieldID(class_Entry, "pwszDataIP", "Ljava/lang/String;");
			jfieldID field_pwszNodeName = env->GetFieldID(class_Entry, "pwszNodeName", "Ljava/lang/String;");
			jfieldID field_pwszUserName = env->GetFieldID(class_Entry, "pwszUserName", "Ljava/lang/String;");
			jfieldID field_pwszPassword = env->GetFieldID(class_Entry, "pwszPassword", "Ljava/lang/String;");
			jfieldID field_pwszProtocol = env->GetFieldID(class_Entry, "pwszProtocol", "Ljava/lang/String;");
			jfieldID field_pwszPort = env->GetFieldID(class_Entry, "pwszPort", "Ljava/lang/String;");
			
			for (jint i = 0; i < ApplianceCnt; i++, pStorageAppliances++)
			{
				jobject entryObject = env->CallObjectMethod(storageApplianceList, id_List_get, i);
				//Dec sprint
				pStorageAppliances->pwszSystemMode = GetStringFromField(env, &entryObject, field_pwszSystemMode);
				pStorageAppliances->pwszDataIP = GetStringFromField(env, &entryObject, field_pwszDataIP);
				pStorageAppliances->pwszNodeName = GetStringFromField(env, &entryObject, field_pwszNodeName);
				pStorageAppliances->pwszUserName = GetStringFromField(env, &entryObject, field_pwszUserName);
				pStorageAppliances->pwszPassword = GetStringFromField(env, &entryObject, field_pwszPassword);
				pStorageAppliances->pwszProtocol = GetStringFromField(env, &entryObject, field_pwszProtocol);
				pStorageAppliances->pwszPort = GetStringFromField(env, &entryObject, field_pwszPort);
				
				if (entryObject != NULL) env->DeleteLocalRef(entryObject);
			}

			if (class_Entry != NULL) env->DeleteLocalRef(class_Entry);
		}

		if (class_List != NULL) env->DeleteLocalRef(class_List);
	}
	else
	{
		pafJS->pAFStorageApplianceList = NULL;
	}
}

void processDiskDataStore(JNIEnv *env, PAFNODE afNode,jobject diskDataStorelist){
	if(diskDataStorelist != NULL)
	{
		jclass class_List = env->GetObjectClass(diskDataStorelist);
		jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");				
		jint volCnt = env->CallIntMethod(diskDataStorelist, method_List_size);

		if(volCnt > 0)
		{
			size_t stLen = sizeof(VSphereRestore_DiskDataStore) * volCnt;
			afNode->m_VSphereRestore_JobScript.m_pDiskDataStores = (PVSphereRestore_DiskDataStore)malloc(stLen);
			memset(afNode->m_VSphereRestore_JobScript.m_pDiskDataStores,0,stLen);
			PVSphereRestore_DiskDataStore m_pDiskDataStores = afNode->m_VSphereRestore_JobScript.m_pDiskDataStores;
			
			jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptDiskDataStore");
			jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");

			jfieldID field_disk	= env->GetFieldID(class_Entry, "disk", "Ljava/lang/String;");
			jfieldID field_dataStore = env->GetFieldID(class_Entry, "dataStore", "Ljava/lang/String;");

			//<huvfe01>2014-7-3
			jfieldID field_diskType	= env->GetFieldID(class_Entry, "ulDiskType", "J");
			jfieldID field_quickRecovery = env->GetFieldID(class_Entry, "ulQuickRecovery", "J");

			for(jint i=0;i < volCnt;i++,m_pDiskDataStores++)
			{	 
				jobject entryObject = env->CallObjectMethod(diskDataStorelist, id_List_get,i);
				m_pDiskDataStores->m_dataStore = GetStringFromField(env, &entryObject,field_dataStore);
				m_pDiskDataStores->m_diskUrl = GetStringFromField(env, &entryObject,field_disk);

				//<huvfe01>2014-7-3
				m_pDiskDataStores->m_ulDiskType = (ULONG)env->GetLongField(entryObject, field_diskType);
				m_pDiskDataStores->m_ulQuickRecovery = (ULONG)env->GetLongField(entryObject, field_quickRecovery);
				
				if (entryObject != NULL) env->DeleteLocalRef(entryObject);
			}			
			
			if (class_Entry != NULL) env->DeleteLocalRef(class_Entry);
		}

		if (class_List != NULL) env->DeleteLocalRef(class_List);

		
	}else{
		afNode->m_VSphereRestore_JobScript.m_pDiskDataStores = NULL;
	}
	
}


ULONG processVMNetworkConfigInfo(JNIEnv *env, PAFNODE afNode,jobject VMNetworkConfigInfoList){

	jint volCnt = 0;
	if(VMNetworkConfigInfoList != NULL)
	{
		jclass class_List = env->GetObjectClass(VMNetworkConfigInfoList);
		jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");				
		volCnt = env->CallIntMethod(VMNetworkConfigInfoList, method_List_size);

		if(volCnt > 0)
		{
			size_t stLen = sizeof(VSphereRestore_VMNetworkAdapter) * volCnt;
			afNode->m_VSphereRestore_JobScript.m_pVMNetworkAdapter = (VSphereRestore_VMNetworkAdapter *)malloc(stLen);
			memset(afNode->m_VSphereRestore_JobScript.m_pVMNetworkAdapter,0,stLen);
			VSphereRestore_VMNetworkAdapter * pVMNetworkConfigInfo = afNode->m_VSphereRestore_JobScript.m_pVMNetworkAdapter;
			
			jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptVMNetworkConfigInfo");
			jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");

			jfieldID label	= env->GetFieldID(class_Entry, "label", "Ljava/lang/String;");
			jfieldID deviceName = env->GetFieldID(class_Entry, "deviceName", "Ljava/lang/String;");
			jfieldID switchName = env->GetFieldID(class_Entry, "switchName", "Ljava/lang/String;");
			jfieldID portgroupName = env->GetFieldID(class_Entry, "portgroupName", "Ljava/lang/String;");
			jfieldID switchUUID = env->GetFieldID(class_Entry, "switchUUID", "Ljava/lang/String;");
			jfieldID portgroupKey = env->GetFieldID(class_Entry, "portgroupKey", "Ljava/lang/String;");

			for(jint i=0;i < volCnt;i++,pVMNetworkConfigInfo++)
			{	 
				jobject entryObject = env->CallObjectMethod(VMNetworkConfigInfoList, id_List_get,i);
				pVMNetworkConfigInfo->m_label = GetStringFromField(env, &entryObject,label);
				pVMNetworkConfigInfo->m_deviceName = GetStringFromField(env, &entryObject,deviceName);
				pVMNetworkConfigInfo->m_switchName = GetStringFromField(env, &entryObject,switchName);
				pVMNetworkConfigInfo->m_portgroupKey = GetStringFromField(env, &entryObject,portgroupKey);
				pVMNetworkConfigInfo->m_portgroupName = GetStringFromField(env, &entryObject,portgroupName);
				pVMNetworkConfigInfo->m_switchUuid = GetStringFromField(env, &entryObject,switchUUID);
				
				if (entryObject != NULL) env->DeleteLocalRef(entryObject);
			}			
			
			if (class_Entry != NULL) env->DeleteLocalRef(class_Entry);
		}

		if (class_List != NULL) env->DeleteLocalRef(class_List);

		
	}else{
		afNode->m_VSphereRestore_JobScript.m_pVMNetworkAdapter = NULL;
	}
	
	return volCnt;
}


void processRestoreVC(JNIEnv *env,PAFNODE afNode,jobject vc){
	if(vc!=NULL){
		jclass class_vc = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptBackupVC");
		
		//VC_ESX_CREDENTIALS_JobScript m_VCESXCredentials;
		//memset(&m_VCESXCredentials, 0, sizeof(VC_ESX_CREDENTIALS_JobScript));
		//afNode->m_VCESXCredentials = m_VCESXCredentials;

		jfieldID field_vcName = env->GetFieldID(class_vc,"vcName","Ljava/lang/String;");
		jfieldID field_username = env->GetFieldID(class_vc,"username","Ljava/lang/String;");
		jfieldID field_password = env->GetFieldID(class_vc,"password","Ljava/lang/String;");
		jfieldID field_protocol = env->GetFieldID(class_vc,"protocol","Ljava/lang/String;");
		jfieldID field_port = env->GetFieldID(class_vc,"port","I");
		jfieldID field_ignoreCertificate = env->GetFieldID(class_vc,"ignoreCertificate","I");

		afNode->m_VSphereRestore_JobScript.m_VcEsxCredentials.ServerName = GetStringFromField(env, &vc,field_vcName);
		afNode->m_VSphereRestore_JobScript.m_VcEsxCredentials.Username = GetStringFromField(env, &vc,field_username);
		afNode->m_VSphereRestore_JobScript.m_VcEsxCredentials.Password = GetStringFromField(env, &vc,field_password);
		afNode->m_VSphereRestore_JobScript.m_VcEsxCredentials.Protocol = GetStringFromField(env, &vc,field_protocol);
		afNode->m_VSphereRestore_JobScript.m_VcEsxCredentials.ignoreCertificate = true;
		afNode->m_VSphereRestore_JobScript.m_VcEsxCredentials.VIport = env->GetIntField(vc, field_port);
		if(class_vc!=NULL){
			env->DeleteLocalRef(class_vc);
		}

	}else{
		//afNode->m_VCESXCredentials = NULL;
		memset(&(afNode->m_VSphereRestore_JobScript.m_VcEsxCredentials),0,sizeof(afNode->m_VSphereRestore_JobScript.m_VcEsxCredentials));
	}
}

void processVM(JNIEnv *env, PAFNODE afNode,jobject vm){
	if(vm!=NULL){
		jclass class_vm = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptBackupVM");
		
		//VM_Info_JobScript m_VMInfo;
		//memset(&m_VMInfo, 0, sizeof(VM_Info_JobScript));
		//afNode->m_VMInfo = m_VMInfo;

		//GuestHost_Credentials_JobScript m_GuestHostCredentials;
		//memset(&m_GuestHostCredentials, 0, sizeof(GuestHost_Credentials_JobScript));
		//afNode->m_GuestHostCredentials = m_GuestHostCredentials;

		jfieldID field_vmName = env->GetFieldID(class_vm,"vmName","Ljava/lang/String;");
		jfieldID field_vmUUID = env->GetFieldID(class_vm,"vmUUID","Ljava/lang/String;");
		jfieldID field_vmInstanceUUID = env->GetFieldID(class_vm,"vmInstanceUUID","Ljava/lang/String;");
		jfieldID field_vmHostName = env->GetFieldID(class_vm,"vmHostName","Ljava/lang/String;");
		jfieldID field_vmVMX = env->GetFieldID(class_vm,"vmVMX","Ljava/lang/String;");
		jfieldID field_osUsername = env->GetFieldID(class_vm,"osUsername","Ljava/lang/String;");
		jfieldID field_osPassword = env->GetFieldID(class_vm,"osPassword","Ljava/lang/String;");

		afNode->m_VMInfo.vmName = GetStringFromField(env,&vm,field_vmName);
		afNode->m_VMInfo.vmUUID = GetStringFromField(env,&vm,field_vmUUID);
		afNode->m_VMInfo.vmInstUUID = GetStringFromField(env,&vm,field_vmInstanceUUID);
		afNode->m_VMInfo.vmHost = GetStringFromField(env,&vm,field_vmHostName);
		afNode->m_VMInfo.vmVMX = GetStringFromField(env,&vm,field_vmVMX);
		afNode->m_VMInfo.powerState = false;

		afNode->m_GuestHostCredentials.VMPassword = GetStringFromField(env,&vm,field_osPassword);
		afNode->m_GuestHostCredentials.VMUsername = GetStringFromField(env,&vm,field_osUsername);

		if(class_vm!=NULL){
			env->DeleteLocalRef(class_vm);
		}

	}else{
		memset(&(afNode->m_VMInfo),0,sizeof(afNode->m_VMInfo));
		memset(&(afNode->m_GuestHostCredentials),0,sizeof(afNode->m_GuestHostCredentials));
	}
}


void processVC(JNIEnv *env, PAFNODE afNode,jobject vc){
	if(vc!=NULL){
		jclass class_vc = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptBackupVC");
		
		//VC_ESX_CREDENTIALS_JobScript m_VCESXCredentials;
		//memset(&m_VCESXCredentials, 0, sizeof(VC_ESX_CREDENTIALS_JobScript));
		//afNode->m_VCESXCredentials = m_VCESXCredentials;

		jfieldID field_vcName = env->GetFieldID(class_vc,"vcName","Ljava/lang/String;");
		jfieldID field_username = env->GetFieldID(class_vc,"username","Ljava/lang/String;");
		jfieldID field_password = env->GetFieldID(class_vc,"password","Ljava/lang/String;");
		jfieldID field_protocol = env->GetFieldID(class_vc,"protocol","Ljava/lang/String;");
		jfieldID field_port = env->GetFieldID(class_vc,"port","I");
		jfieldID field_ignoreCertificate = env->GetFieldID(class_vc,"ignoreCertificate","I");

		afNode->m_VCESXCredentials.ServerName = GetStringFromField(env, &vc,field_vcName);
		afNode->m_VCESXCredentials.Username = GetStringFromField(env, &vc,field_username);
		afNode->m_VCESXCredentials.Password = GetStringFromField(env, &vc,field_password);
		afNode->m_VCESXCredentials.Protocol = GetStringFromField(env, &vc,field_protocol);
		afNode->m_VCESXCredentials.ignoreCertificate = true;
		afNode->m_VCESXCredentials.VIport = env->GetIntField(vc, field_port);
		if(class_vc!=NULL){
			env->DeleteLocalRef(class_vc);
		}

	}else{
		//afNode->m_VCESXCredentials = NULL;
		memset(&(afNode->m_VCESXCredentials),0,sizeof(afNode->m_VCESXCredentials));
	}
}

void processVCloudVCList(JNIEnv *env, PAFNODE afNode, int vAppVCCount, jobject vAppVCList)
{
	afNode->m_UnderlyingHypervisorCount = 0;
	afNode->m_pUnderlyingHypervisor = NULL;

	if (vAppVCList != NULL)
	{
		jclass class_List = env->GetObjectClass(vAppVCList);
		jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");
		jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
		jint vcCount = env->CallIntMethod(vAppVCList, method_List_size);
		afNode->m_UnderlyingHypervisorCount = vcCount;

		if (vcCount > 0)
		{
			size_t stLen = sizeof(VC_ESX_CREDENTIALS_JobScript) * vcCount;
			afNode->m_pUnderlyingHypervisor = (VC_ESX_CREDENTIALS_JobScript*) malloc(stLen);
			memset(afNode->m_pUnderlyingHypervisor, 0, stLen);
			VC_ESX_CREDENTIALS_JobScript* pVCCredential = afNode->m_pUnderlyingHypervisor;

			jclass class_vc = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptBackupVC");

			jfieldID field_vcName = env->GetFieldID(class_vc, "vcName", "Ljava/lang/String;");
			jfieldID field_username = env->GetFieldID(class_vc, "username", "Ljava/lang/String;");
			jfieldID field_password = env->GetFieldID(class_vc, "password", "Ljava/lang/String;");
			jfieldID field_protocol = env->GetFieldID(class_vc, "protocol", "Ljava/lang/String;");
			jfieldID field_port = env->GetFieldID(class_vc, "port", "I");
			jfieldID field_ignoreCertificate = env->GetFieldID(class_vc, "ignoreCertificate", "I");

			for (jint i = 0; i < vcCount; i++, pVCCredential++)
			{
				jobject vc = env->CallObjectMethod(vAppVCList, id_List_get, i);

				pVCCredential->ServerName = GetStringFromField(env, &vc, field_vcName);
				pVCCredential->Username = GetStringFromField(env, &vc, field_username);
				pVCCredential->Password = GetStringFromField(env, &vc, field_password);
				pVCCredential->Protocol = GetStringFromField(env, &vc, field_protocol);
				pVCCredential->ignoreCertificate = true;
				pVCCredential->VIport = env->GetIntField(vc, field_port);
			}

			if (class_vc != NULL) env->DeleteLocalRef(class_vc);
		}
		
		if (class_List != NULL)
		{
			env->DeleteLocalRef(class_List);
		}
	}
}

void processJJobScriptRecoverVMNodeList(JNIEnv *env, jobject nodeList, OUT _AFNODE **ppNodeList)//<huvfe01>2014-7-21  For vApp node, there are serval underlying vms
{
	jclass class_List = NULL;
	jclass class_Entry = NULL;
	PAFNODE pNodeList = NULL;

	do
	{
		if (NULL == ppNodeList)
		{
			break;
		}
		*ppNodeList = NULL;

		if ((NULL == env) || (NULL == nodeList))
		{
			break;
		}

		//class list
		class_List = env->GetObjectClass(nodeList);
		if (NULL == class_List)
		{
			break;
		}
		jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");
		jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
		jint nodeCnt = env->CallIntMethod(nodeList, method_List_size);
		if (0 == nodeCnt)
		{
			break;
		}

		//class entry
		class_Entry = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptRecoverVMNode");
		if (NULL == class_Entry)
		{
			break;
		}
		jfieldID field_pwszNodeName = env->GetFieldID(class_Entry, "pwszNodeName", "Ljava/lang/String;");
		jfieldID field_encryptionPassword = env->GetFieldID(class_Entry, "encryptionPassword", "Ljava/lang/String;");
		jfieldID field_pwszNodeAddr = env->GetFieldID(class_Entry, "pwszNodeAddr", "Ljava/lang/String;");
		jfieldID field_pwszUserName = env->GetFieldID(class_Entry, "pwszUserName", "Ljava/lang/String;");
		jfieldID field_pwszUserPW = env->GetFieldID(class_Entry, "pwszUserPW", "Ljava/lang/String;");
		jfieldID field_pwszSessPath = env->GetFieldID(class_Entry, "pwszSessPath", "Ljava/lang/String;");
		jfieldID field_ulSessNum = env->GetFieldID(class_Entry, "ulSessNum", "I");
		jfieldID field_nVolumeApp = env->GetFieldID(class_Entry, "nVolumeApp", "I");
		jfieldID field_vmName = env->GetFieldID(class_Entry, "vmName", "Ljava/lang/String;");
		jfieldID field_vcName = env->GetFieldID(class_Entry, "vcName", "Ljava/lang/String;");
		jfieldID field_esxServerName = env->GetFieldID(class_Entry, "esxServerName", "Ljava/lang/String;");
		jfieldID field_vmDataStore = env->GetFieldID(class_Entry, "vmDataStore", "Ljava/lang/String;");
		jfieldID field_vmDataStoreId = env->GetFieldID(class_Entry, "vmDataStoreId", "Ljava/lang/String;");
		jfieldID field_resourcePoolName = env->GetFieldID(class_Entry, "resourcePoolName", "Ljava/lang/String;");
		jfieldID field_originalLocation = env->GetFieldID(class_Entry, "originalLocation", "I");
		jfieldID field_poweronAfterRestore = env->GetFieldID(class_Entry, "poweronAfterRestore", "I");
		jfieldID field_overwriteExistingVM = env->GetFieldID(class_Entry, "overwriteExistingVM", "I");
		jfieldID field_vmDiskCount = env->GetFieldID(class_Entry, "vmDiskCount", "I");
		jfieldID field_diskDataStore = env->GetFieldID(class_Entry, "diskDataStore", "Ljava/util/List;");
		jfieldID field_vmNetworkConfig = env->GetFieldID(class_Entry, "vmNetworkConfig", "Ljava/util/List;");
		//jfieldID field_pBackupVolumeList = env->GetFieldID(class_Entry, "pBackupVolumeList", "Ljava/util/List;");
		//jfieldID field_pRestoreVolumeAppList = env->GetFieldID(class_Entry, "pRestoreVolumeAppList", "Ljava/util/List;");
		jfieldID field_vc = env->GetFieldID(class_Entry, "vc", "Lcom/ca/arcflash/webservice/jni/model/JJobScriptBackupVC;");
		jfieldID field_nFilterItems = env->GetFieldID(class_Entry, "nFilterItems", "I");
		jfieldID field_fOptions = env->GetFieldID(class_Entry, "fOptions", "I");
		
		jfieldID field_cpuCount = env->GetFieldID(class_Entry, "cpuCount", "I");
		jfieldID field_memorySizeInKB = env->GetFieldID(class_Entry, "memorySize", "J");
		jfieldID field_storagePolicyId = env->GetFieldID(class_Entry, "storagePolicyId", "Ljava/lang/String;");
		jfieldID field_storagePolicyName = env->GetFieldID(class_Entry, "storagePolicyName", "Ljava/lang/String;");
		jfieldID field_childVMs = env->GetFieldID(class_Entry, "childVMNodeList", "Ljava/util/List;");
		jfieldID field_jobId = env->GetFieldID(class_Entry, "jobId", "J");
		jfieldID field_networkMap = env->GetFieldID(class_Entry, "networkMappingInfo", "Ljava/lang/String;");

		size_t stLen = sizeof(AFNODE) * nodeCnt;
		pNodeList = (PAFNODE)malloc(stLen);
		if (NULL != pNodeList)
		{
			memset(pNodeList, 0, stLen);
			for (size_t ii = 0; ii < nodeCnt; ii++)
			{
				PAFNODE pNode = &pNodeList[ii];
				jobject entryObject = env->CallObjectMethod(nodeList, id_List_get, ii);

				pNode->pwszNodeName = GetStringFromField(env, &entryObject, field_pwszNodeName);
				pNode->pwszEncryptPasswordRestore = GetStringFromField(env, &entryObject, field_encryptionPassword);
				pNode->pwszNodeAddr = GetStringFromField(env, &entryObject, field_pwszNodeAddr);
				pNode->pwszUserName = GetStringFromField(env, &entryObject, field_pwszUserName);
				pNode->pwszUserPW = GetStringFromField(env, &entryObject, field_pwszUserPW);
				pNode->pwszSessPath = GetStringFromField(env, &entryObject, field_pwszSessPath);
				pNode->ulSessNum = env->GetIntField(entryObject, field_ulSessNum);
				pNode->nVolumeApp = env->GetIntField(entryObject, field_nVolumeApp);
				pNode->nFilterItems = env->GetIntField(entryObject, field_nFilterItems);
				pNode->fOptions = env->GetIntField(entryObject, field_fOptions);
				pNode->m_VSphereRestore_JobScript.m_bOverwriteVM = env->GetIntField(entryObject, field_overwriteExistingVM);
				pNode->m_VSphereRestore_JobScript.m_PowerOnVM = env->GetIntField(entryObject, field_poweronAfterRestore);
				pNode->m_VSphereRestore_JobScript.m_RestoreToOriginal = env->GetIntField(entryObject, field_originalLocation);
				pNode->m_VSphereRestore_JobScript.m_vmDiskCount = env->GetIntField(entryObject, field_vmDiskCount);
				pNode->m_VSphereRestore_JobScript.m_vmName = GetStringFromField(env, &entryObject, field_vmName);
				pNode->m_VSphereRestore_JobScript.m_vcName = GetStringFromField(env, &entryObject, field_vcName);
				pNode->m_VSphereRestore_JobScript.m_esxName = GetStringFromField(env, &entryObject, field_esxServerName);
				pNode->m_VSphereRestore_JobScript.m_vmDataStore = GetStringFromField(env, &entryObject, field_vmDataStore);
				pNode->m_VSphereRestore_JobScript.m_vmDataStoreId = GetStringFromField(env, &entryObject, field_vmDataStoreId);
				pNode->m_VSphereRestore_JobScript.m_vmResPool = GetStringFromField(env, &entryObject, field_resourcePoolName);
				pNode->m_VSphereRestore_JobScript.m_nVmdkPort = 0;
				//processJBackupVolAppList(env,pafresVol,env->GetObjectField(entryObject,field_pBackupVolumeList));
				//processJRestoreVolAppList(env,pafresVol,env->GetObjectField(entryObject,field_pRestoreVolumeAppList));
				processRestoreVC(env, pNode, env->GetObjectField(entryObject, field_vc));
				processDiskDataStore(env, pNode, env->GetObjectField(entryObject, field_diskDataStore));
				pNode->m_VSphereRestore_JobScript.m_VMNetworkAdapterCount = processVMNetworkConfigInfo(env, pNode, env->GetObjectField(entryObject, field_vmNetworkConfig));

				///////////////////////////////////////////////////////////////////////////////////////////////////
				pNode->m_storagePolicyGuid = GetStringFromField(env, &entryObject, field_storagePolicyId);
				pNode->m_storagePolicyName = GetStringFromField(env, &entryObject, field_storagePolicyName);
				pNode->m_VSphereRestore_JobScript.m_ulCPUCount = env->GetIntField(entryObject, field_cpuCount);
				pNode->m_VSphereRestore_JobScript.m_ulMemSizeInKB = env->GetLongField(entryObject, field_memorySizeInKB); //KB
				pNode->ulJobId = env->GetLongField(entryObject, field_jobId);
				pNode->strNetworkMapXml = GetStringFromField(env, &entryObject, field_networkMap);

				pNode->m_ulChildVMCount = 0;
				pNode->m_pChildVMs = NULL;
				jobject child_node_list = env->GetObjectField(entryObject, field_childVMs);
				if (NULL != child_node_list)
				{
					jclass child_class_List = env->GetObjectClass(child_node_list);
					if (NULL != child_class_List)
					{
						jmethodID method_child_List_size = env->GetMethodID(child_class_List, "size", "()I");
						pNode->m_ulChildVMCount = env->CallIntMethod(child_node_list, method_child_List_size);
						if (pNode->m_ulChildVMCount > 0)
						{
							processJJobScriptRecoverVMNodeList(env, child_node_list, &(pNode->m_pChildVMs));
						}
						env->DeleteLocalRef(child_class_List);
					}
				}
			}
		}
	} while (FALSE);

	if (NULL != class_Entry)
	{
		env->DeleteLocalRef(class_Entry);
		class_Entry = NULL;
	}

	if (NULL != class_List)
	{
		env->DeleteLocalRef(class_List);
		class_List = NULL;
	}

	if (NULL != ppNodeList)
	{
		*ppNodeList = pNodeList;
	}

	return;
}

void processJJobScriptRecoverVMNodeListForRestoreFileToOriginal(JNIEnv *env, PAFJOBSCRIPT pafJS, jobject nodeList){
	if(nodeList != NULL)
	{
		jclass class_List = env->GetObjectClass(nodeList);
		jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");				
		jint volCnt = env->CallIntMethod(nodeList, method_List_size);

		if(volCnt > 0)
		{
			PAFNODE pafresVol = pafJS->pAFNodeList;
			
			jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptRecoverVMNode");
			jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
			jfieldID field_pwszNodeName	= env->GetFieldID(class_Entry, "pwszNodeName", "Ljava/lang/String;");
			jfieldID field_encryptionPassword	= env->GetFieldID(class_Entry, "encryptionPassword", "Ljava/lang/String;");
			jfieldID field_pwszNodeAddr	= env->GetFieldID(class_Entry, "pwszNodeAddr", "Ljava/lang/String;");
			jfieldID field_pwszUserName	= env->GetFieldID(class_Entry, "pwszUserName", "Ljava/lang/String;");
			jfieldID field_pwszUserPW	= env->GetFieldID(class_Entry, "pwszUserPW", "Ljava/lang/String;");
			jfieldID field_pwszSessPath	= env->GetFieldID(class_Entry, "pwszSessPath", "Ljava/lang/String;");
			jfieldID field_ulSessNum	= env->GetFieldID(class_Entry, "ulSessNum", "I");
			jfieldID field_nVolumeApp = env->GetFieldID(class_Entry, "nVolumeApp", "I");
			jfieldID field_vmName = env->GetFieldID(class_Entry, "vmName", "Ljava/lang/String;");
			jfieldID field_vmUsername = env->GetFieldID(class_Entry, "vmUsername", "Ljava/lang/String;");
			jfieldID field_vmPassword = env->GetFieldID(class_Entry, "vmPassword", "Ljava/lang/String;");
			jfieldID field_vcName = env->GetFieldID(class_Entry, "vcName", "Ljava/lang/String;");
			jfieldID field_esxServerName = env->GetFieldID(class_Entry, "esxServerName", "Ljava/lang/String;");
			jfieldID field_vmDataStore = env->GetFieldID(class_Entry, "vmDataStore", "Ljava/lang/String;");
			jfieldID field_resourcePoolName = env->GetFieldID(class_Entry, "resourcePoolName", "Ljava/lang/String;");
			jfieldID field_originalLocation	= env->GetFieldID(class_Entry, "originalLocation", "I");
			jfieldID field_poweronAfterRestore	= env->GetFieldID(class_Entry, "poweronAfterRestore", "I");
			jfieldID field_overwriteExistingVM	= env->GetFieldID(class_Entry, "overwriteExistingVM", "I");
			jfieldID field_vmDiskCount	= env->GetFieldID(class_Entry, "vmDiskCount", "I");
			jfieldID field_diskDataStore	= env->GetFieldID(class_Entry, "diskDataStore", "Ljava/util/List;");
			//jfieldID field_pBackupVolumeList = env->GetFieldID(class_Entry, "pBackupVolumeList", "Ljava/util/List;");
			//jfieldID field_pRestoreVolumeAppList = env->GetFieldID(class_Entry, "pRestoreVolumeAppList", "Ljava/util/List;");
			jfieldID field_vc = env->GetFieldID(class_Entry,"vc","Lcom/ca/arcflash/webservice/jni/model/JJobScriptBackupVC;");
			jfieldID field_nFilterItems = env->GetFieldID(class_Entry, "nFilterItems", "I");
			jfieldID field_fOptions = env->GetFieldID(class_Entry, "fOptions", "I");
			 

			for(jint i=0;i < volCnt;i++,pafresVol++)
			{	 
				jobject entryObject = env->CallObjectMethod(nodeList, id_List_get,i);
				pafresVol->m_VMInfo.vmName = GetStringFromField(env, &entryObject,field_vmName);
				pafresVol->m_VMInfo.vmInstUUID = GetStringFromField(env, &entryObject,field_pwszNodeName);
				pafresVol->m_VSphereRestore_JobScript.m_bOverwriteVM = env->GetIntField(entryObject,field_overwriteExistingVM);
				pafresVol->m_VSphereRestore_JobScript.m_PowerOnVM = env->GetIntField(entryObject,field_poweronAfterRestore);
				pafresVol->m_VSphereRestore_JobScript.m_RestoreToOriginal = env->GetIntField(entryObject,field_originalLocation);
				pafresVol->m_VSphereRestore_JobScript.m_vmDiskCount = env->GetIntField(entryObject,field_vmDiskCount);
				pafresVol->m_VSphereRestore_JobScript.m_vmName = GetStringFromField(env, &entryObject,field_vmName);
				pafresVol->m_VSphereRestore_JobScript.m_vcName = GetStringFromField(env, &entryObject,field_vcName);
				pafresVol->m_VSphereRestore_JobScript.m_esxName = GetStringFromField(env, &entryObject,field_esxServerName);
				pafresVol->m_VSphereRestore_JobScript.m_vmDataStore = GetStringFromField(env, &entryObject,field_vmDataStore);
				pafresVol->m_VSphereRestore_JobScript.m_vmResPool = GetStringFromField(env, &entryObject,field_resourcePoolName);
				pafresVol->m_VSphereRestore_JobScript.m_nVmdkPort = 0;
				pafresVol->m_GuestHostCredentials.VMPassword = GetStringFromField(env, &entryObject,field_vmPassword);
				pafresVol->m_GuestHostCredentials.VMUsername = GetStringFromField(env, &entryObject,field_vmUsername);
				//processJBackupVolAppList(env,pafresVol,env->GetObjectField(entryObject,field_pBackupVolumeList));
				//processJRestoreVolAppList(env,pafresVol,env->GetObjectField(entryObject,field_pRestoreVolumeAppList));
				processRestoreVC(env,pafresVol,env->GetObjectField(entryObject,field_vc));
				processDiskDataStore(env,pafresVol,env->GetObjectField(entryObject,field_diskDataStore));

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

void processJJobScriptVSphereNodeList(JNIEnv *env, PAFJOBSCRIPT pafJS, jobject nodeList, int vAppVCCount, jobject vAppVCList)
{

#define processJJobScriptVSphereNodeList_Get_ID_I(token)			jfieldID field_##token = env->GetFieldID(class_Entry, #token, "I");
#define processJJobScriptVSphereNodeList_Get_ID_J(token)			jfieldID field_##token = env->GetFieldID(class_Entry, #token, "J");
#define processJJobScriptVSphereNodeList_Get_ID_S(token)			jfieldID field_##token = env->GetFieldID(class_Entry, #token, "Ljava/lang/String;");

#define processJJobScriptVSphereNodeList_Get_VALUE_I(token)		pafresVol->##token = env->GetIntField(entryObject, field_##token);
#define processJJobScriptVSphereNodeList_Get_VALUE_J(token)		pafresVol->##token = env->GetLongField(entryObject, field_##token);
#define processJJobScriptVSphereNodeList_Get_VALUE_S(token)		pafresVol->##token = GetStringFromField(env, &entryObject, field_##token);

	if(nodeList != NULL)
	{
		jclass class_List = env->GetObjectClass(nodeList);
		jmethodID method_List_size = env->GetMethodID(class_List, "size", "()I");				
		jint volCnt = env->CallIntMethod(nodeList, method_List_size);

		if(volCnt > 0)
		{
			size_t stLen = sizeof(AFNODE) * volCnt;
			pafJS->pAFNodeList = (PAFNODE)malloc(stLen);
			memset(pafJS->pAFNodeList,0,stLen);
			PAFNODE pafresVol = pafJS->pAFNodeList;
			
			jclass class_Entry = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobScriptVSphereNode");
			jmethodID id_List_get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
			jfieldID field_pwszNodeName	= env->GetFieldID(class_Entry, "pwszNodeName", "Ljava/lang/String;");
			jfieldID field_pwszNodeAddr	= env->GetFieldID(class_Entry, "pwszNodeAddr", "Ljava/lang/String;");
			jfieldID field_pwszUserName	= env->GetFieldID(class_Entry, "pwszUserName", "Ljava/lang/String;");
			jfieldID field_pwszUserPW	= env->GetFieldID(class_Entry, "pwszUserPW", "Ljava/lang/String;");
			jfieldID field_pwszSessPath	= env->GetFieldID(class_Entry, "pwszSessPath", "Ljava/lang/String;");
			jfieldID field_ulSessNum	= env->GetFieldID(class_Entry, "ulSessNum", "I");
			jfieldID field_nVolumeApp = env->GetFieldID(class_Entry, "nVolumeApp", "I");
			//jfieldID field_pBackupVolumeList = env->GetFieldID(class_Entry, "pBackupVolumeList", "Ljava/util/List;");
			jfieldID field_pRestoreVolumeAppList = env->GetFieldID(class_Entry, "pRestoreVolumeAppList", "Ljava/util/List;"); //<sonmi01>2014-3-13 ###???
			jfieldID field_vc = env->GetFieldID(class_Entry,"vc","Lcom/ca/arcflash/webservice/jni/model/JJobScriptBackupVC;");
            jfieldID field_vm = env->GetFieldID(class_Entry,"vm","Lcom/ca/arcflash/webservice/jni/model/JJobScriptBackupVM;");
			jfieldID field_pBackupOption_Exch = env->GetFieldID(class_Entry,"pBackupOption_Exch","Lcom/ca/arcflash/webservice/jni/model/JJobScriptBackupOptionExch;");
			jfieldID field_nFilterItems = env->GetFieldID(class_Entry, "nFilterItems", "I");
			jfieldID field_fOptions = env->GetFieldID(class_Entry, "fOptions", "I");
			 
			//processJJobScriptVSphereNodeList_Get_ID_S(TransportMode);
			//jfieldID field_ xxx	= env->GetFieldID(class_Entry, "xxxx", "Ljava/lang/String;");
			jfieldID field_TransportMode = env->GetFieldID(class_Entry, "TransportMode", "Ljava/lang/String;");
			jfieldID field_SnapshotMethod = env->GetFieldID(class_Entry, "HyperVSnapshotConsistencyType", "I");
			jfieldID field_UseDedicatedStub = env->GetFieldID(class_Entry, "HyperVSnapshotSeparationIndividually", "Z");

            jfieldID field_bRunCommandEvenFailed = env->GetFieldID(class_Entry, "runCommandEvenFailed", "Z");

			for(jint i=0;i < volCnt;i++,pafresVol++)
			{	 
				jobject entryObject = env->CallObjectMethod(nodeList, id_List_get,i);
				pafresVol->pwszNodeName = GetStringFromField(env, &entryObject,field_pwszNodeName);
				pafresVol->pwszNodeAddr = GetStringFromField(env, &entryObject,field_pwszNodeAddr);
				pafresVol->pwszUserName = GetStringFromField(env, &entryObject,field_pwszUserName);
				pafresVol->pwszUserPW = GetStringFromField(env, &entryObject,field_pwszUserPW);
				pafresVol->pwszSessPath = GetStringFromField(env, &entryObject,field_pwszSessPath);
				pafresVol->ulSessNum = env->GetIntField(entryObject, field_ulSessNum);
				pafresVol->nVolumeApp = env->GetIntField(entryObject, field_nVolumeApp);
				pafresVol->nFilterItems = env->GetIntField(entryObject, field_nFilterItems);
				pafresVol->fOptions = env->GetIntField(entryObject, field_fOptions);
				//processJBackupVolAppList(env,pafresVol,env->GetObjectField(entryObject,field_pBackupVolumeList));
				processJRestoreVolAppList(env,pafresVol,env->GetObjectField(entryObject,field_pRestoreVolumeAppList)); //<sonmi01>2014-3-13 ###???
				processVC(env,pafresVol,env->GetObjectField(entryObject,field_vc));
				processVM(env,pafresVol,env->GetObjectField(entryObject,field_vm));
				processVCloudVCList(env, pafresVol, vAppVCCount, vAppVCList);
				processJBackupOption_Exch(env, pafresVol, env->GetObjectField(entryObject, field_pBackupOption_Exch));

				//processJJobScriptVSphereNodeList_Get_VALUE_S(TransportMode);
				//pafresVol->xxx = GetStringFromField(env, &entryObject,field_ xxx);
				pafresVol->TransportMode = GetStringFromField(env, &entryObject,field_TransportMode);
				pafresVol->ulSnapshotMethod = env->GetIntField(entryObject, field_SnapshotMethod);

				pafresVol->bUseDedicatedStub = env->GetBooleanField(entryObject, field_UseDedicatedStub);

                pafresVol->bRunCommandEvenFailed = env->GetBooleanField(entryObject, field_bRunCommandEvenFailed);

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

void JJobScriptArchiveInfo2ArchiveInfo(JNIEnv *env, PAFARCHIVE_INFO paArchiveInfo, jobject jArchiveInfo)
{
	if (paArchiveInfo)
		ZeroMemory(paArchiveInfo, sizeof(AFARCHIVE_INFO));

	if (jArchiveInfo)
	{
		jclass class_JArchiveInfo = env->GetObjectClass(jArchiveInfo);

		// 		private int dwFileCpySchType;
		// 		private boolean bDailyBackup;
		// 		private boolean bWeeklyBackup;
		// 		private boolean bMonthlyBackup;
		// 		private int dwSubmitArchiveAfterNBackups;
		// 		private boolean bFileCopyFeatureEnabled;

		jfieldID field_dwFileCpySchType = env->GetFieldID(class_JArchiveInfo, "dwFileCpySchType", "I");
		jfieldID field_bDailyBackup = env->GetFieldID(class_JArchiveInfo, "bDailyBackup", "Z");
		jfieldID field_bWeeklyBackup = env->GetFieldID(class_JArchiveInfo, "bWeeklyBackup", "Z");
		jfieldID field_bMonthlyBackup = env->GetFieldID(class_JArchiveInfo, "bMonthlyBackup", "Z");
		jfieldID field_dwSubmitArchiveAfterNBackups = env->GetFieldID(class_JArchiveInfo, "dwSubmitArchiveAfterNBackups", "I");
		jfieldID field_bFileCopyFeatureEnabled = env->GetFieldID(class_JArchiveInfo, "bFileCopyFeatureEnabled", "Z");

		if (paArchiveInfo)
		{
			paArchiveInfo->dwFileCpySchType = env->GetIntField(jArchiveInfo, field_dwFileCpySchType);
			paArchiveInfo->bDailyBackup = env->GetBooleanField(jArchiveInfo, field_bDailyBackup);
			paArchiveInfo->bWeeklyBackup = env->GetBooleanField(jArchiveInfo, field_bWeeklyBackup);
			paArchiveInfo->bMonthlyBackup = env->GetBooleanField(jArchiveInfo, field_bMonthlyBackup);
			paArchiveInfo->dwSubmitArchiveAfterNBackups = env->GetIntField(jArchiveInfo, field_dwSubmitArchiveAfterNBackups);
			paArchiveInfo->bFileCopyFeatureEnabled = env->GetBooleanField(jArchiveInfo, field_bFileCopyFeatureEnabled);
		}
	}
}

void JJobScript2AFJOBSCRIPT(JNIEnv *env, PAFJOBSCRIPT pafJS, jobject* jJobScript)
{

#define JJobScript2AFJOBSCRIPT_Get_ID_I(token)			jfieldID field_##token = env->GetFieldID(class_JJobScript, #token, "I");
#define JJobScript2AFJOBSCRIPT_Get_ID_J(token)			jfieldID field_##token = env->GetFieldID(class_JJobScript, #token, "J");
#define JJobScript2AFJOBSCRIPT_Get_ID_S(token)			jfieldID field_##token = env->GetFieldID(class_JJobScript, #token, "Ljava/lang/String;");

#define JJobScript2AFJOBSCRIPT_Get_VALUE_I(token)		pafJS->##token = env->GetIntField(*jJobScript, field_##token);
#define JJobScript2AFJOBSCRIPT_Get_VALUE_J(token)		pafJS->##token = env->GetLongField(*jJobScript, field_##token);
#define JJobScript2AFJOBSCRIPT_Get_VALUE_S(token)		pafJS->##token = GetStringFromField(env, jJobScript, field_##token);

#define JJobScript2AFJOBSCRIPT_Get_filed_I(token)		JJobScript2AFJOBSCRIPT_Get_ID_I(token);  JJobScript2AFJOBSCRIPT_Get_VALUE_I(token);
#define JJobScript2AFJOBSCRIPT_Get_filed_J(token)		JJobScript2AFJOBSCRIPT_Get_ID_J(token);  JJobScript2AFJOBSCRIPT_Get_VALUE_J(token);
#define JJobScript2AFJOBSCRIPT_Get_filed_S(token)		JJobScript2AFJOBSCRIPT_Get_ID_S(token);  JJobScript2AFJOBSCRIPT_Get_VALUE_S(token);


	jclass class_JJobScript = env->GetObjectClass(*jJobScript);
	
	jfieldID field_ulVersion = env->GetFieldID(class_JJobScript, "ulVersion", "I");
	jfieldID field_ulJobID = env->GetFieldID(class_JJobScript, "ulJobID", "J");
	jfieldID field_usJobType = env->GetFieldID(class_JJobScript, "usJobType", "I");

	//<sonmi01>2014-8-21 ###???
	//JJobScript2AFJOBSCRIPT_Get_filed_I(ulChildJobId);
	//JJobScript2AFJOBSCRIPT_Get_filed_I(ulChildCount);
	//JJobScript2AFJOBSCRIPT_Get_filed_I(ulSubJobType);
	//JJobScript2AFJOBSCRIPT_Get_filed_I(MasterJobId);
	//JJobScript2AFJOBSCRIPT_Get_filed_I(MasterJobType);
	//JJobScript2AFJOBSCRIPT_Get_filed_I(MasterJobFlag);
	//JJobScript2AFJOBSCRIPT_Get_filed_I(MasterJobEnum);


	jfieldID field_nNodeItems = env->GetFieldID(class_JJobScript, "nNodeItems", "I");
	jfieldID field_pwszDestPath = env->GetFieldID(class_JJobScript, "pwszDestPath", "Ljava/lang/String;");
	jfieldID field_pwszUserName = env->GetFieldID(class_JJobScript, "pwszUserName", "Ljava/lang/String;");
	jfieldID field_pwszPassword = env->GetFieldID(class_JJobScript, "pwszPassword", "Ljava/lang/String;");
	jfieldID field_pwszUserName_2 = env->GetFieldID(class_JJobScript, "pwszUserName_2", "Ljava/lang/String;");
	jfieldID field_pwszPassword_2 = env->GetFieldID(class_JJobScript, "pwszPassword_2", "Ljava/lang/String;");
	jfieldID field_pwszComments = env->GetFieldID(class_JJobScript, "pwszComments", "Ljava/lang/String;");
	jfieldID field_pwszBeforeJob = env->GetFieldID(class_JJobScript, "pwszBeforeJob", "Ljava/lang/String;");
	jfieldID field_pwszAfterJob = env->GetFieldID(class_JJobScript, "pwszAfterJob", "Ljava/lang/String;");
	jfieldID field_pwszPostSnapshotCmd = env->GetFieldID(class_JJobScript, "pwszPostSnapshotCmd", "Ljava/lang/String;");
	jfieldID field_pwszPrePostUser = env->GetFieldID(class_JJobScript, "pwszPrePostUser", "Ljava/lang/String;");
	jfieldID field_pwszPrePostPassword = env->GetFieldID(class_JJobScript, "pwszPrePostPassword", "Ljava/lang/String;");
	jfieldID field_usPreExitCode = env->GetFieldID(class_JJobScript, "usPreExitCode", "I");
	jfieldID field_usJobMethod = env->GetFieldID(class_JJobScript, "usJobMethod", "I");
	jfieldID field_usRestPoint = env->GetFieldID(class_JJobScript, "usRestPoint", "I");
	jfieldID field_fOptions = env->GetFieldID(class_JJobScript, "fOptions", "I");
	jfieldID field_pAFNodeList = env->GetFieldID(class_JJobScript, "pAFNodeList", "Ljava/util/List;");
	jfieldID field_dwCompressionLevel = env->GetFieldID(class_JJobScript, "dwCompressionLevel", "J");
	jfieldID field_dwEncryptType = env->GetFieldID(class_JJobScript, "dwEncryptType", "J");
	jfieldID field_pwszEncryptPassword = env->GetFieldID(class_JJobScript, "pwszEncryptPassword", "Ljava/lang/String;");	
	jfieldID field_dwEncryptTypeCopySession = env->GetFieldID(class_JJobScript, "dwEncryptTypeCopySession", "J");
	jfieldID field_bRetainEncryptionAsSource = env->GetFieldID(class_JJobScript, "bRetainEncryptionAsSource", "Z");
	jfieldID field_dwEncryptPasswordCopySession = env->GetFieldID(class_JJobScript, "pwszEncryptPasswordCopySession", "Ljava/lang/String;");
	jfieldID field_dwJobHistoryDays = env->GetFieldID(class_JJobScript, "dwJobHistoryDays", "J");
	jfieldID field_dwSqlLogDays = env->GetFieldID(class_JJobScript, "dwSqlLogDays", "J");
	jfieldID field_dwExchangeLogDays = env->GetFieldID(class_JJobScript, "dwExchangeLogDays", "J");
	jfieldID field_dwThrottlingByKB = env->GetFieldID(class_JJobScript, "dwThrottlingByKB", "J");
	jfieldID field_ulJobAttribute = env->GetFieldID(class_JJobScript, "ulJobAttribute", "J");
	jfieldID field_launcherInstacnUUID = env->GetFieldID(class_JJobScript, "launcherInstanceUUID", "Ljava/lang/String;");
	jfieldID field_pAFVSphereNodeList = env->GetFieldID(class_JJobScript, "pVSphereNodeList", "Ljava/util/List;");
	jfieldID field_pRecoverVMNodeList = env->GetFieldID(class_JJobScript, "pRecoverVMNodeList", "Ljava/util/List;");
    jfieldID field_ulPreAllocationSpace = env->GetFieldID( class_JJobScript, "preAllocationSpace", "I"); //<huvfe01>2012-11-7 for defect#102889
	jfieldID field_pwszRPSPolicyName = env->GetFieldID(class_JJobScript, "rpsPolicyName", "Ljava/lang/String;");	
	jfieldID field_pwszRPSHostName = env->GetFieldID(class_JJobScript, "rpsHostname", "Ljava/lang/String;");
	jfieldID field_pwszRPSSID = env->GetFieldID(class_JJobScript, "rpsSID", "Ljava/lang/String;");
	jfieldID field_pwszRpsPolicyID = env->GetFieldID(class_JJobScript, "rpsPolicyID", "Ljava/lang/String;");	
	jfieldID field_pwszRPSDataStore = env->GetFieldID(class_JJobScript, "RPSDataStoreName", "Ljava/lang/String;");	
	jfieldID field_pwszRPSDataStoreName = env->GetFieldID(class_JJobScript, "RPSDataStoreDisplayName", "Ljava/lang/String;");	
	jfieldID field_pwszVDiskPassword = env->GetFieldID(class_JJobScript, "pwszVDiskPassword", "Ljava/lang/String;");
	jfieldID field_ullScheduledTime = env->GetFieldID(class_JJobScript, "ullScheduledTime", "J");
	jfieldID field_bUseHardwareProvider = env->GetFieldID(class_JJobScript, "softwareOrHardwareSnapshotType", "Z");
	jfieldID field_bFallBackToSoftProvider = env->GetFieldID(class_JJobScript, "failoverToSoftwareSnapshot", "Z");
	jfieldID field_bUseTransportable = env->GetFieldID(class_JJobScript, "useTrasportableSnapshot", "Z");
	jfieldID field_vAppVCInfos = env->GetFieldID(class_JJobScript, "vAppVCInfos", "Ljava/util/List;");
	jfieldID field_vAppVCCount = env->GetFieldID(class_JJobScript, "vAppVCCount", "I");
	jfieldID field_dwMasterJobId = env->GetFieldID(class_JJobScript, "dwMasterJobId", "J");
	jfieldID field_generatedDestination = env->GetFieldID(class_JJobScript, "generatedDestination", "Ljava/lang/String;");
	// October sprint
	jfieldID field_nStorageApplianceItems = env->GetFieldID(class_JJobScript, "nStorageApplianceItems", "I");
	jfieldID field_pAFStorageApplianceList = env->GetFieldID(class_JJobScript, "pAFStorageApplianceList", "Ljava/util/List;");

	//For File Copy 
	jfieldID field_archiveInfo = env->GetFieldID(class_JJobScript, "archiveInfo", "Lcom/ca/arcflash/webservice/data/archive/JJobScriptArchiveInfo;");
	//pafJS->stArchiveInfo

	pafJS->ulVersion = env->GetIntField(*jJobScript, field_ulVersion);
	pafJS->ulShrMemID = env->GetLongField(*jJobScript, field_ulJobID);
	pafJS->usJobType = env->GetIntField(*jJobScript, field_usJobType);
	pafJS->nNodeItems = env->GetIntField(*jJobScript, field_nNodeItems);
	pafJS->usPreExitCode = env->GetIntField(*jJobScript, field_usPreExitCode);
	pafJS->usJobMethod = env->GetIntField(*jJobScript, field_usJobMethod);
	pafJS->usRestPoint = env->GetIntField(*jJobScript, field_usRestPoint);
	pafJS->fOptions = env->GetIntField(*jJobScript, field_fOptions);
	pafJS->pwszDestPath = GetStringFromField(env,jJobScript,field_pwszDestPath);
	pafJS->pwszUserName = GetStringFromField(env,jJobScript,field_pwszUserName);
	pafJS->pwszPassword = GetStringFromField(env,jJobScript,field_pwszPassword);
	pafJS->pwszUserName_2 = GetStringFromField(env,jJobScript,field_pwszUserName_2);
	pafJS->pwszPassword_2 = GetStringFromField(env,jJobScript,field_pwszPassword_2);
	pafJS->pwszComments = GetStringFromField(env,jJobScript,field_pwszComments);
	pafJS->pwszBeforeJob = GetStringFromField(env,jJobScript,field_pwszBeforeJob);
	pafJS->pwszAfterJob = GetStringFromField(env,jJobScript,field_pwszAfterJob);
	pafJS->pwszPostSnapshotCmd = GetStringFromField(env, jJobScript, field_pwszPostSnapshotCmd);
	pafJS->pwszPrePostUser = GetStringFromField(env,jJobScript,field_pwszPrePostUser);
	pafJS->pwszPrePostPassword = GetStringFromField(env,jJobScript,field_pwszPrePostPassword);
	pafJS->dwCompressionLevel = env->GetLongField(*jJobScript, field_dwCompressionLevel);
	pafJS->dwEncryptType = env->GetLongField(*jJobScript, field_dwEncryptType);
	pafJS->pwszEncryptPassword = GetStringFromField(env, jJobScript, field_pwszEncryptPassword);
	pafJS->dwEncryptTypeCopySession = env->GetLongField(*jJobScript, field_dwEncryptTypeCopySession);
	if (field_bRetainEncryptionAsSource)
		pafJS->bRetainEncryptionAsSource = env->GetBooleanField(*jJobScript, field_bRetainEncryptionAsSource);
	else
		pafJS->bRetainEncryptionAsSource = FALSE;
	pafJS->pwszEncryptPasswordCopySession = GetStringFromField(env, jJobScript, field_dwEncryptPasswordCopySession);
	pafJS->dwJobHistoryDays = env->GetLongField(*jJobScript, field_dwJobHistoryDays);
	pafJS->dwSqlLogDays = env->GetLongField(*jJobScript, field_dwSqlLogDays);
	pafJS->dwExchangeLogDays = env->GetLongField(*jJobScript, field_dwExchangeLogDays);
	pafJS->dwThroughoutThrottling = env->GetLongField(*jJobScript, field_dwThrottlingByKB);
	pafJS->ulJobAttribute = env->GetLongField(*jJobScript, field_ulJobAttribute);
	pafJS->launcherInstanceUUID = GetStringFromField(env,jJobScript,field_launcherInstacnUUID);
    pafJS->ulPreAllocationSpace = env->GetIntField(*jJobScript, field_ulPreAllocationSpace); //<huvfe01>2012-11-7 for defect#102889
	pafJS->pRPSName = GetStringFromField(env,jJobScript,field_pwszRPSHostName);
	pafJS->pwzRPSSvrSID = GetStringFromField(env, jJobScript, field_pwszRPSSID);
	pafJS->pPolicyName = GetStringFromField(env,jJobScript,field_pwszRPSPolicyName);
	pafJS->pPolicyGUID = GetStringFromField(env,jJobScript,field_pwszRpsPolicyID);
	pafJS->pSourceDataStore = GetStringFromField(env,jJobScript,field_pwszRPSDataStore);
	pafJS->pSourceDataStoreName = GetStringFromField(env,jJobScript,field_pwszRPSDataStoreName);
	pafJS->pwszVDiskPassword = GetStringFromField(env, jJobScript, field_pwszVDiskPassword);
	pafJS->ullScheduledTime = env->GetLongField(*jJobScript, field_ullScheduledTime);
	
	//October sprint
	pafJS->nStorageApplianceItems = env->GetIntField(*jJobScript, field_nStorageApplianceItems);

	// For File Copy
	JJobScriptArchiveInfo2ArchiveInfo(env, &(pafJS->stArchiveInfo), env->GetObjectField(*jJobScript, field_archiveInfo));

	// java sets false when hardware provider is selected, hence changing it to true while sending it to backend
	BOOL bUseSoftwareProvider = env->GetBooleanField(*jJobScript, field_bUseHardwareProvider);
	if (!bUseSoftwareProvider)
	{
		pafJS->bUseHardwareProvider = true;
		pafJS->bFallBackToSoftProvider = env->GetBooleanField(*jJobScript, field_bFallBackToSoftProvider);
		pafJS->bUseTransportable = env->GetBooleanField(*jJobScript, field_bUseTransportable);
		// October sprint
		processJJobScriptStorageApplianceList(env, pafJS, env->GetObjectField(*jJobScript, field_pAFStorageApplianceList));
	}
	else
	{
		pafJS->bUseHardwareProvider = false;
		// Setting the below parameters to false as the backend should not consider them irrespective of the selection
		pafJS->bFallBackToSoftProvider = false;
		pafJS->bUseTransportable = false;
	}
	pafJS->pGeneratedDestinationPath = GetStringFromField(env,jJobScript,field_generatedDestination);
	pafJS->MasterJobId = env->GetLongField(*jJobScript, field_dwMasterJobId);

	if((AF_JOBTYPE_BACKUP_VMWARE == pafJS->usJobType) || (AF_JOBTYPE_BACKUP_HYPERV == pafJS->usJobType) || (AF_JOBTYPE_BACKUP_VMWARE_APP == pafJS->usJobType)){ 
		processJJobScriptVSphereNodeList(env, pafJS, env->GetObjectField(*jJobScript, field_pAFVSphereNodeList), env->GetIntField(*jJobScript, field_vAppVCCount), env->GetObjectField(*jJobScript, field_vAppVCInfos));
	}
	else if ((AF_JOBTYPE_RESTORE_VMWARE == pafJS->usJobType) || (AF_JOBTYPE_RESTORE_HYPERV == pafJS->usJobType) || (AF_JOBTYPE_RESTORE_VMWARE_APP == pafJS->usJobType))
	{
		pafJS->pAFNodeList = NULL;
		processJJobScriptRecoverVMNodeList(env, env->GetObjectField(*jJobScript, field_pRecoverVMNodeList), &(pafJS->pAFNodeList));
	}else if (pafJS->usJobType == 17){
		processJJobScriptNodeList(env,pafJS,env->GetObjectField(*jJobScript,field_pAFNodeList));
		processJJobScriptRecoverVMNodeListForRestoreFileToOriginal(env,pafJS,env->GetObjectField(*jJobScript,field_pRecoverVMNodeList));
	}else{
		processJJobScriptNodeList(env,pafJS,env->GetObjectField(*jJobScript,field_pAFNodeList));
	}
	if (class_JJobScript != NULL) env->DeleteLocalRef(class_JJobScript);
}

int JNetConnInfo2NET_CONN_INFO(JNIEnv *env, jobject* jNetConnInfo, NET_CONN_INFO& netConnInfo)
{
	jclass class_JNetConnInfo = env->FindClass("com/ca/arcflash/service/jni/model/JNetConnInfo");		
	jfieldID field_szDomain	= env->GetFieldID(class_JNetConnInfo, "szDomain", "Ljava/lang/String;");
	jfieldID field_szUsr	= env->GetFieldID(class_JNetConnInfo, "szUsr", "Ljava/lang/String;");
	jfieldID field_szPwd	= env->GetFieldID(class_JNetConnInfo, "szPwd", "Ljava/lang/String;");
	jfieldID field_szDir = env->GetFieldID(class_JNetConnInfo, "szDir", "Ljava/lang/String;");
	
	wchar_t* pDomain = GetStringFromField(env,jNetConnInfo,field_szDomain);
	wchar_t* pUser = GetStringFromField(env,jNetConnInfo,field_szUsr);
	wchar_t* pPwd = GetStringFromField(env,jNetConnInfo,field_szPwd);
	wchar_t* pDir = GetStringFromField(env,jNetConnInfo,field_szDir);

	if (pDomain != NULL)
	{
		wcscpy_s(netConnInfo.szDomain, pDomain);
		free(pDomain);
	}

	if (pUser != NULL)
	{
		wcscpy_s(netConnInfo.szUsr, pUser);
		free(pUser);
	}

	if (pPwd != NULL)
	{
		wcscpy_s(netConnInfo.szPwd, pPwd);
		free(pPwd);
	}

	if (pDir!=NULL)
	{
		wcscpy_s(netConnInfo.szDir, pDir);
		free(pDir);
	}

	return 0;
}

int ConnInfo2JConnInfo(JNIEnv* env, jobject& jConnInfo, NET_CONN_INFO& connInfo)
{
	jclass class_JNetConnInfo = env->FindClass("com/ca/arcflash/service/jni/model/JNetConnInfo");
	jmethodID methodSetDomain = env->GetMethodID(class_JNetConnInfo, "setSzDomain", "(Ljava/lang/String;)V");
	jmethodID methodSetUser = env->GetMethodID(class_JNetConnInfo, "setSzUsr", "(Ljava/lang/String;)V");
	jmethodID methodSetPass = env->GetMethodID(class_JNetConnInfo, "setSzPwd", "(Ljava/lang/String;)V");
	jmethodID methodSetDest = env->GetMethodID(class_JNetConnInfo, "setSzDir", "(Ljava/lang/String;)V");

	env->CallVoidMethod(jConnInfo, methodSetDomain,WCHARToJString(env, connInfo.szDomain));
	env->CallVoidMethod(jConnInfo, methodSetUser,WCHARToJString(env, connInfo.szUsr));
	env->CallVoidMethod(jConnInfo, methodSetPass,WCHARToJString(env, connInfo.szPwd));
	env->CallVoidMethod(jConnInfo, methodSetDest,WCHARToJString(env, connInfo.szDir));

	return 0;
}

int AddVecString2List(JNIEnv *env, jobject*retArr,std::vector<std::wstring> &vList)
{
	jclass class_ArrayList = env->GetObjectClass(*retArr);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

	for(vector<std::wstring>::iterator itr = vList.begin(); itr != vList.end(); itr++)
	{
		jstring jstr = WCHARToJString(env, (wchar_t*)itr->c_str());

		env->CallBooleanMethod(*retArr, id_ArrayList_add, jstr);

		if ( jstr!=NULL) env->DeleteLocalRef(jstr);
	}

	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);

	return 0;

}

int JOB_MONITOR2JJobMonitor(JNIEnv *env, JOB_MONITOR& aJM, jobject& jobMonitor)
{
	jclass class_JJobMonitor = env->GetObjectClass(jobMonitor);
	jmethodID id_JJobMonitor_setUlSessionID = env->GetMethodID(class_JJobMonitor, "setUlSessionID", "(J)V");
	jmethodID id_JJobMonitor_setUlFlags = env->GetMethodID(class_JJobMonitor, "setUlFlags", "(J)V");
	jmethodID id_JJobMonitor_setUlJobPhase = env->GetMethodID(class_JJobMonitor, "setUlJobPhase", "(J)V");
	jmethodID id_JJobMonitor_setUlJobStatus = env->GetMethodID(class_JJobMonitor, "setUlJobStatus", "(J)V");
	jmethodID id_JJobMonitor_setUlJobType = env->GetMethodID(class_JJobMonitor, "setUlJobType", "(J)V");
	jmethodID id_JJobMonitor_setUlJobMethod = env->GetMethodID(class_JJobMonitor, "setUlJobMethod", "(J)V");
	jmethodID id_JJobMonitor_setUlVolMethod = env->GetMethodID(class_JJobMonitor, "setUlVolMethod", "(J)V");
	jmethodID id_JJobMonitor_setUlEstBytesJob = env->GetMethodID(class_JJobMonitor, "setUlEstBytesJob", "(J)V");
	jmethodID id_JJobMonitor_setUlXferBytesJob = env->GetMethodID(class_JJobMonitor, "setUlXferBytesJob", "(J)V");
	jmethodID id_JJobMonitor_setUlEstBytesDisk = env->GetMethodID(class_JJobMonitor, "setUlEstBytesDisk", "(J)V");
	jmethodID id_JJobMonitor_setUlXferBytesDisk = env->GetMethodID(class_JJobMonitor, "setUlXferBytesDisk", "(J)V");
	jmethodID id_JJobMonitor_setWszDiskName = env->GetMethodID(class_JJobMonitor, "setWszDiskName", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setUlBackupStartTime = env->GetMethodID(class_JJobMonitor, "setUlBackupStartTime", "(J)V");
	jmethodID id_JJobMonitor_setUlElapsedTime = env->GetMethodID(class_JJobMonitor, "setUlElapsedTime", "(J)V");
	jmethodID id_jJobMonitor_setUlUniqueData = env->GetMethodID(class_JJobMonitor, "setUlUniqueData", "(J)V");

	jmethodID id_JJobMonitor_setnProgramCPU = env->GetMethodID(class_JJobMonitor, "setnProgramCPU","(J)V");
	jmethodID id_JJobMonitor_setnSystemCPU = env->GetMethodID(class_JJobMonitor, "setnSystemCPU", "(J)V");
	jmethodID id_JJobMonitor_setnReadSpeed = env->GetMethodID(class_JJobMonitor, "setnReadSpeed", "(J)V");
	jmethodID id_JJobMonitor_setnWriteSpeed = env->GetMethodID(class_JJobMonitor, "setnWriteSpeed", "(J)V");
	jmethodID id_JJobMonitor_setnSystemReadSpeed = env->GetMethodID(class_JJobMonitor, "setnSystemReadSpeed", "(J)V");
	jmethodID id_JJobMonitor_setnSystemWriteSpeed = env->GetMethodID(class_JJobMonitor, "setnSystemWriteSpeed", "(J)V");
	jmethodID id_JJobMonitor_setUlThrottling = env->GetMethodID(class_JJobMonitor, "setUlThrottling", "(J)V");
	jmethodID id_JJobMonitor_setUlEncInfoStatus = env->GetMethodID(class_JJobMonitor, "setUlEncInfoStatus", "(J)V");
	jmethodID id_JJobMonitor_setUlTotalSizeRead = env->GetMethodID(class_JJobMonitor, "setUlTotalSizeRead", "(J)V");
	jmethodID id_JJobMonitor_setUlTotalSizeWritten = env->GetMethodID(class_JJobMonitor, "setUlTotalSizeWritten", "(J)V");
	jmethodID id_JJobMonitor_setWzCurVolMntPoint = env->GetMethodID(class_JJobMonitor, "setWzCurVolMntPoint", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setUlCompressLevel = env->GetMethodID(class_JJobMonitor, "setUlCompressLevel", "(J)V");
	jmethodID id_JJobMonitor_setTransferMode = env->GetMethodID(class_JJobMonitor, "setTransferMode", "(J)V");
	//For FS catalog
	jmethodID id_JJobMonitor_setCtDWBKJobID = env->GetMethodID(class_JJobMonitor, "setCtDWBKJobID", "(J)V");
	jmethodID id_JJobMonitor_setCtBKJobName = env->GetMethodID(class_JJobMonitor, "setCtBKJobName", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setCtBKStartTime = env->GetMethodID(class_JJobMonitor, "setCtBKStartTime", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setCtCurCatVol = env->GetMethodID(class_JJobMonitor, "setCtCurCatVol", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setDwBKSessNum = env->GetMethodID(class_JJobMonitor, "setDwBKSessNum", "(J)V");
	jmethodID id_JJobMonitor_setWzBKBackupDest = env->GetMethodID(class_JJobMonitor, "setWzBKBackupDest", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setWzBKDestUsrName = env->GetMethodID(class_JJobMonitor, "setWzBKDestUsrName", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setWzBKDestPassword = env->GetMethodID(class_JJobMonitor, "setWzBKDestPassword", "(Ljava/lang/String;)V");
	//For GRT catalog
	jmethodID id_JJobMonitor_setUlTotalFolder = env->GetMethodID(class_JJobMonitor, "setUlTotalFolder", "(J)V");
	jmethodID id_JJobMonitor_setWszEDB = env->GetMethodID(class_JJobMonitor, "setWszEDB", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setUlProcessedFolder = env->GetMethodID(class_JJobMonitor, "setUlProcessedFolder", "(J)V");
	jmethodID id_JJobMonitor_setWszMailFolder = env->GetMethodID(class_JJobMonitor, "setWszMailFolder", "(Ljava/lang/String;)V");
	//For Merge sessions
	jmethodID id_JJobMonitor_setUlMergedSession = env->GetMethodID(class_JJobMonitor, "setUlMergedSession", "(J)V");
	jmethodID id_JJobMonitor_setUlTotalMergedSessions = env->GetMethodID(class_JJobMonitor, "setUlTotalMergedSessions", "(J)V");

	//For vApp
	jmethodID id_JJobMonitor_setUlTotalVMJobCount = env->GetMethodID(class_JJobMonitor, "setUlTotalVMJobCount", "(J)V");
	jmethodID id_JJobMonitor_setUlFinishedVMJobCount = env->GetMethodID(class_JJobMonitor, "setUlFinishedVMJobCount", "(J)V");
	jmethodID id_JJobMonitor_setUlCanceledVMJobCount = env->GetMethodID(class_JJobMonitor, "setUlCanceledVMJobCount", "(J)V");
	jmethodID id_JJobMonitor_setUlFailedVMJobCount = env->GetMethodID(class_JJobMonitor, "setUlFailedVMJobCount", "(J)V");

	jmethodID id_JJobMonitor_setVmHostName  = env->GetMethodID(class_JJobMonitor, "setVmHostName", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setJobSubStatus = env->GetMethodID(class_JJobMonitor, "setJobSubStatus", "(J)V");

	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlSessionID, (jlong)aJM.ulSessionID);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlFlags, (jlong)aJM.ulFlags);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlJobPhase, (jlong)aJM.ulJobPhase);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlJobStatus, (jlong)aJM.ulJobStatus);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlJobType, (jlong)aJM.ulJobType);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlJobMethod, (jlong)aJM.ulJobMethod);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlVolMethod, (jlong)aJM.ulVolMethod);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlEstBytesJob, (jlong)aJM.ulEstBytesJob);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlXferBytesJob, (jlong)aJM.ulXferBytesJob);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlEstBytesDisk, (jlong)aJM.ulEstBytesDisk);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlXferBytesDisk, (jlong)aJM.ulXferBytesDisk);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setWszDiskName, WCHARToJString(env, aJM.wszDiskName));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlBackupStartTime, (jlong)aJM.ulBackupStartTime);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlElapsedTime, (jlong)aJM.ulElapsedTime);

	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setnProgramCPU, (jlong)aJM.nProgramCPU);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setnSystemCPU, (jlong)aJM.nSystemCPU);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setnReadSpeed, (jlong)aJM.nReadSpeed);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setnWriteSpeed, (jlong)aJM.nWriteSpeed);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setnSystemReadSpeed, (jlong)aJM.nSystemReadSpeed);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setnSystemWriteSpeed, (jlong)aJM.nSystemWriteSpeed);
	long temp = (long)aJM.ulThrottling; // if ulThrottling is -1, must cast it(from "unsigend long" to "long").
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlThrottling, (jlong)temp);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlEncInfoStatus, (jlong)aJM.ulEncInfoStatus);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlTotalSizeRead, (jlong)aJM.ulTotalSizeRead);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlTotalSizeWritten, (jlong)aJM.ulTotalSizeWritten);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setWzCurVolMntPoint, WCHARToJString(env, aJM.wzCurVolMntPoint));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlCompressLevel, (jlong)aJM.ulCompressLevel);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setTransferMode, (jlong)aJM.ulVMDiskTransportMode);

	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setCtDWBKJobID, (jlong)aJM.stCatalogField.dwBKJobID);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setCtBKJobName, WCHARToJString(env, aJM.stCatalogField.wzBKJobName));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setCtBKStartTime, WCHARToJString(env, aJM.stCatalogField.wzBKStartTime));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setCtCurCatVol, WCHARToJString(env, aJM.stCatalogField.wzCurCatVol));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setDwBKSessNum, (jlong)aJM.stCatalogField.dwBKSessNum);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setWzBKBackupDest, WCHARToJString(env, aJM.stCatalogField.wzBKBackupDest));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setWzBKDestUsrName, WCHARToJString(env, aJM.stCatalogField.wzBKDestUsrName));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setWzBKDestPassword, WCHARToJString(env, aJM.stCatalogField.wzBKDestPassword));

	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlTotalFolder, (jlong)aJM.ulTotalFolder);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlProcessedFolder, (jlong)aJM.ulProcessedFolder);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setWszMailFolder, WCHARToJString(env, aJM.wszMailFolder));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setWszEDB, WCHARToJString(env, aJM.wszEDB));

	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlMergedSession, (jlong)aJM.ulMergedSessions);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlTotalMergedSessions, (jlong)aJM.ulTotalMegedSessions);

	env->CallVoidMethod(jobMonitor, id_jJobMonitor_setUlUniqueData, aJM.ulTotalUniqueSize);

	//For vApp
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlTotalVMJobCount, (jlong)aJM.ulTotalVMJobCount);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlFinishedVMJobCount, (jlong)aJM.ulFinishedVMJobCount);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlCanceledVMJobCount, (jlong)aJM.ulCanceledVMJobCount);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlFailedVMJobCount, (jlong)aJM.ulFailedVMJobCount);

	if (id_JJobMonitor_setVmHostName != NULL)
	    env->CallVoidMethod(jobMonitor, id_JJobMonitor_setVmHostName, WCHARToJString(env, aJM.wszVMHostName));

	if (id_JJobMonitor_setJobSubStatus != NULL)
        env->CallVoidMethod(jobMonitor, id_JJobMonitor_setJobSubStatus, (jlong)aJM.ulSubJobStatus);

	return 0;
}
int D2DREGCONF2JDeployUpgrade(JNIEnv *env, D2DREGCONF& d2dConfig, jobject& deployUpgrade)
{
	/*String hostname;
	String installPath;
	long port;
	long build;
	long majVer;
	long minVer;*/
	jclass class_JDeployUpgrade = env->GetObjectClass(deployUpgrade);
	jmethodID 	setHostname = env->GetMethodID(class_JDeployUpgrade, "setHostname", "(Ljava/lang/String;)V");
	jmethodID	setInstallPath = env->GetMethodID(class_JDeployUpgrade, "setInstallPath", "(Ljava/lang/String;)V");
	jmethodID	setPort = env->GetMethodID(class_JDeployUpgrade, "setPort", "(J)V");
	jmethodID	setBuild= env->GetMethodID(class_JDeployUpgrade, "setBuild", "(J)V");
	jmethodID	setMajVer= env->GetMethodID(class_JDeployUpgrade, "setMajVer", "(J)V");
	jmethodID	setMinVer= env->GetMethodID(class_JDeployUpgrade, "setMinVer", "(J)V");
	jmethodID	setUuid = env->GetMethodID(class_JDeployUpgrade, "setUuid", "(Ljava/lang/String;)V");

	env->CallVoidMethod(deployUpgrade, setHostname, WCHARToJString(env, d2dConfig.wszHostName));
	env->CallVoidMethod(deployUpgrade, setInstallPath, WCHARToJString(env, d2dConfig.wszInstedPath));
	env->CallVoidMethod(deployUpgrade, setPort, (jlong)d2dConfig.dwPort);
	env->CallVoidMethod(deployUpgrade, setBuild, (jlong)d2dConfig.dwBuild);
	env->CallVoidMethod(deployUpgrade, setMajVer, (jlong)d2dConfig.dwMajVer);
	env->CallVoidMethod(deployUpgrade, setMinVer, (jlong)d2dConfig.dwMinVer);
	env->CallVoidMethod(deployUpgrade, setUuid, WCHARToJString(env, (wchar_t*)d2dConfig.pwsUUID->c_str()));

	return 0;
}

int D2DREGCONF2JDeployUpgradeWithDriver(JNIEnv *env, D2DREGCONF2& d2dConfig, jobject& deployUpgrade)
{
	/*String hostname;
	String installPath;
	long port;
	long build;
	long majVer;
	long minVer;*/
	jclass class_JDeployUpgrade = env->GetObjectClass(deployUpgrade);
	jmethodID 	setHostname = env->GetMethodID(class_JDeployUpgrade, "setHostname", "(Ljava/lang/String;)V");
	jmethodID	setInstallPath = env->GetMethodID(class_JDeployUpgrade, "setInstallPath", "(Ljava/lang/String;)V");
	jmethodID	setPort = env->GetMethodID(class_JDeployUpgrade, "setPort", "(J)V");
	jmethodID	setBuild= env->GetMethodID(class_JDeployUpgrade, "setBuild", "(J)V");
	jmethodID	setMajVer= env->GetMethodID(class_JDeployUpgrade, "setMajVer", "(J)V");
	jmethodID	setMinVer= env->GetMethodID(class_JDeployUpgrade, "setMinVer", "(J)V");
	jmethodID	setUuid = env->GetMethodID(class_JDeployUpgrade, "setUuid", "(Ljava/lang/String;)V");
	jmethodID   setInstallDriver = env->GetMethodID(class_JDeployUpgrade, "setInstallDriver", "(Z)V");
	jmethodID   setUseHttps = env->GetMethodID(class_JDeployUpgrade, "setUseHttps", "(Z)V");

	env->CallVoidMethod(deployUpgrade, setHostname, WCHARToJString(env, d2dConfig.wszHostName));
	env->CallVoidMethod(deployUpgrade, setInstallPath, WCHARToJString(env, d2dConfig.wszInstedPath));
	env->CallVoidMethod(deployUpgrade, setPort, (jlong)d2dConfig.dwPort);
	env->CallVoidMethod(deployUpgrade, setBuild, (jlong)d2dConfig.dwBuild);
	env->CallVoidMethod(deployUpgrade, setMajVer, (jlong)d2dConfig.dwMajVer);
	env->CallVoidMethod(deployUpgrade, setMinVer, (jlong)d2dConfig.dwMinVer);
	env->CallVoidMethod(deployUpgrade, setUuid, WCHARToJString(env, (wchar_t*)d2dConfig.pwsUUID->c_str()));
	env->CallVoidMethod(deployUpgrade, setInstallDriver, (jboolean)d2dConfig.bDriverInstalled);
	if(d2dConfig.nCommType == 0)
		env->CallVoidMethod(deployUpgrade, setUseHttps, false);
	else
		env->CallVoidMethod(deployUpgrade, setUseHttps, true);

	return 0;
}

DWORD CheckAdminAccountValid(JNIEnv *env, jobject& account)
{
	jclass class_account = env->GetObjectClass(account);

	jfieldID username_field = env->GetFieldID(class_account, "userName","Ljava/lang/String;");
	jfieldID password_field = env->GetFieldID(class_account, "password","Ljava/lang/String;");

	wchar_t* strUserp = GetStringFromField(env, &account, username_field);
	std::wstring strUse = strUserp;
    wchar_t* strPasswdp = GetStringFromField(env, &account, password_field);
	std::wstring strPasswd = strPasswdp;

	DWORD dwRet = AFCheckAdminAccountValid(strUse, strPasswd);

	if(strUserp != NULL)
		free(strUserp);
	if(strPasswdp != NULL)
		free(strPasswdp);
    if (class_account!= NULL) env->DeleteLocalRef(class_account);

	return dwRet;
}

DWORD AFSaveAdminAccount(JNIEnv *env, jobject& account)
{
	jclass class_account = env->GetObjectClass(account);

	jfieldID username_field = env->GetFieldID(class_account, "userName","Ljava/lang/String;");
	jfieldID password_field = env->GetFieldID(class_account, "password","Ljava/lang/String;");

	wchar_t* strUserp = GetStringFromField(env, &account, username_field);
	std::wstring strUse = strUserp;
    wchar_t* strPasswdp = GetStringFromField(env, &account, password_field);
	std::wstring strPasswd = strPasswdp;

	DWORD dwRet = AFSaveAdminAccount(strUse, strPasswd);

	if(strUserp != NULL)
		free(strUserp);
	if(strPasswdp != NULL)
		free(strPasswdp);
    if (class_account!= NULL) env->DeleteLocalRef(class_account);

	return dwRet;
}


int convertWriterVector2AppList(JNIEnv *env, ExcludedWriterVector& excludedList, jobject& appList)
{
	jclass list_class = env->GetObjectClass(appList);	
	jmethodID addMethod = env->GetMethodID(list_class, "add", "(Ljava/lang/Object;)Z");

	jclass appWriterClass = env->FindClass("com/ca/arcflash/webservice/jni/model/JApplicationWriter");
	jmethodID appWriterConstr = env->GetMethodID(appWriterClass, "<init>", "()V");
	jfieldID appWriterNameField = env->GetFieldID(appWriterClass, "appName", "Ljava/lang/String;");
	jmethodID appMntMethod = env->GetMethodID(appWriterClass, "setAffectedMnt", "(Ljava/util/List;)V");
	jmethodID appWriterCompMethod = env->GetMethodID(appWriterClass, "setComponentList", "(Ljava/util/List;)V");

	jclass componentClass = env->FindClass("com/ca/arcflash/webservice/jni/model/JApplicationComponect");
	jmethodID componentConstr = env->GetMethodID(componentClass, "<init>", "()V");
	jfieldID componentNameField = env->GetFieldID(componentClass, "name", "Ljava/lang/String;");
	jmethodID componentMntMethod = env->GetMethodID(componentClass, "setAffectedMnt", "(Ljava/util/List;)V");
	jmethodID componentFileMethod = env->GetMethodID(componentClass, "setFileList", "(Ljava/util/List;)V");

	jclass arrayListClass = env->FindClass("java/util/ArrayList");
	jmethodID arrayListConstr = env->GetMethodID(arrayListClass, "<init>", "()V");
	jmethodID listAddMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");

	if(excludedList.size() > 0)
	{
		for(vector<CExcludedWriter>::iterator appWriter = excludedList.begin(); appWriter != excludedList.end(); appWriter++)
		{		
			jobject newAppWriter = env->NewObject(appWriterClass, appWriterConstr);
			
			std::wstring appName = appWriter->m_wsWriterName;
			jstring appName_JStr = WCHARToJString(env, (wchar_t*)appName.c_str());
			env->SetObjectField(newAppWriter, appWriterNameField, appName_JStr);
			if(appName_JStr != NULL)
				env->DeleteLocalRef(appName_JStr);

			jobject appMntList = env->NewObject(arrayListClass, arrayListConstr);
			std::vector<std::wstring> appAffectMntVector = appWriter->m_vecAffectMnt;
			if(appAffectMntVector.size() > 0)
			{
				for(vector<std::wstring>::iterator appMntIter = appAffectMntVector.begin(); appMntIter != appAffectMntVector.end(); appMntIter++)
				{
					jstring appMnt_JStr = WCHARToJString(env, (wchar_t*)appMntIter->c_str());
					env->CallBooleanMethod(appMntList, listAddMethod, appMnt_JStr);
					if(appMnt_JStr != NULL)
						env->DeleteLocalRef(appMnt_JStr);
				}
			}
			env->CallVoidMethod(newAppWriter, appMntMethod, appMntList);

			jobject newComponentList = env->NewObject(arrayListClass, arrayListConstr);
			ExcludedCompVector compVector = appWriter->m_vecExcludedComp;
			if(compVector.size() > 0)
			{				
				for(vector<CExcludedComponent>::iterator iter = compVector.begin(); iter != compVector.end(); iter++)
				{
					jobject newComponent = env->NewObject(componentClass, componentConstr);
					std::wstring compName = iter->m_wsCompDesc;
					jstring compName_JStr = WCHARToJString(env, (wchar_t*)compName.c_str());
					env->SetObjectField(newComponent, componentNameField, compName_JStr);
					if(compName_JStr != NULL)
						env->DeleteLocalRef(compName_JStr);

					jobject mntList = env->NewObject(arrayListClass, arrayListConstr);
					std::vector<std::wstring> affectMntVector = iter->m_vecAffectMnt;
					if(affectMntVector.size() > 0)
					{
						for(vector<std::wstring>::iterator mntIter = affectMntVector.begin(); mntIter != affectMntVector.end(); mntIter++)
						{
							jstring mnt_JStr = WCHARToJString(env, (wchar_t*)mntIter->c_str());
							env->CallBooleanMethod(mntList, listAddMethod, mnt_JStr);
							if(mnt_JStr != NULL)
								env->DeleteLocalRef(mnt_JStr);
						}
					}
					env->CallVoidMethod(newComponent, componentMntMethod, mntList);

					jobject fileList = env->NewObject(arrayListClass, arrayListConstr);
					std::vector<std::wstring> fileVector = iter->m_vecExcludedFile;
					if(fileVector.size() > 0)
					{
						for(vector<std::wstring>::iterator fileIter = fileVector.begin(); fileIter != fileVector.end(); fileIter++)
						{
							jstring fileName_JStr = WCHARToJString(env, (wchar_t*)fileIter->c_str());
							env->CallBooleanMethod(fileList, listAddMethod, fileName_JStr);
							if(fileName_JStr != NULL)
								env->DeleteLocalRef(fileName_JStr);
						}
					}
					env->CallVoidMethod(newComponent, componentFileMethod, fileList);
					env->CallBooleanMethod(newComponentList, listAddMethod, newComponent);
					if(fileList != NULL)
						env->DeleteLocalRef(fileList);
					if(newComponent != NULL)
						env->DeleteLocalRef(newComponent);
				}
			}
			env->CallVoidMethod(newAppWriter, appWriterCompMethod, newComponentList);
			env->CallBooleanMethod(appList,addMethod,newAppWriter);

			if(newComponentList != NULL)
				env->DeleteLocalRef(newComponentList);
			if(newAppWriter != NULL)
				env->DeleteLocalRef(newAppWriter);
		}
	}

	if(list_class != NULL)
		env->DeleteLocalRef(list_class);
	
	if(appWriterClass != NULL)
		env->DeleteLocalRef(appWriterClass);

	return 0;
}

int AddVBACKUP_ITEM2List(JNIEnv *env, jobject jBkpItemArrList, const VBACKUP_ITEM& vBkpItem)
{	
	jclass class_ArrayList = env->GetObjectClass(jBkpItemArrList);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");	
	jclass class_BackupItem = env->FindClass("com/ca/arcflash/service/jni/model/JBackupItem");
	jmethodID mid_BackupItem_constructor = env->GetMethodID(class_BackupItem, "<init>", "()V");
	for(VBACKUP_ITEM::const_iterator backupItem = vBkpItem.begin(); backupItem != vBkpItem.end(); backupItem++)
	{		
		jobject jBackupItem = env->NewObject(class_BackupItem, mid_BackupItem_constructor);

		jfieldID field_type	= env->GetFieldID(class_BackupItem, "type", "Ljava/lang/String;");
		jstring type = WCHARToJString(env, (wchar_t*)backupItem->strType.c_str());
		env->SetObjectField(jBackupItem, field_type, type);
		if ( type!=NULL) env->DeleteLocalRef(type);

		jfieldID field_displayName	= env->GetFieldID(class_BackupItem, "displayName", "Ljava/lang/String;");
		jstring displayName = WCHARToJString(env, (wchar_t*)backupItem->strDisplayName.c_str());
		env->SetObjectField(jBackupItem, field_displayName, displayName);
		if ( displayName!=NULL) env->DeleteLocalRef(displayName);

		jfieldID field_guid	= env->GetFieldID(class_BackupItem, "guid", "Ljava/lang/String;");
		jstring guid = WCHARToJString(env, (wchar_t*)backupItem->strGuid.c_str());
		env->SetObjectField(jBackupItem, field_guid, guid);
		if ( guid!=NULL) env->DeleteLocalRef(guid);

        jfieldID field_volDataSizeB	= env->GetFieldID(class_BackupItem, "volDataSizeB", "Ljava/lang/String;");
		jstring volDataSizeB = WCHARToJString(env, (wchar_t*)backupItem->strVolDataSizeB.c_str());
		env->SetObjectField(jBackupItem, field_volDataSizeB, volDataSizeB);
		if ( volDataSizeB!=NULL) env->DeleteLocalRef(volDataSizeB);

		jfieldID field_subSessionID	= env->GetFieldID(class_BackupItem, "subSessionID", "Ljava/lang/String;");
		jstring subSessionID = WCHARToJString(env, (wchar_t*)backupItem->strSubSesId.c_str());
		env->SetObjectField(jBackupItem, field_subSessionID, subSessionID);
		if ( subSessionID!=NULL) env->DeleteLocalRef(subSessionID);

		jfieldID field_catalog	= env->GetFieldID(class_BackupItem, "catalogFilePath", "Ljava/lang/String;");
		jstring catalog = WCHARToJString(env, (wchar_t*)backupItem->strCalalogFile.c_str());
		env->SetObjectField(jBackupItem, field_catalog, catalog);
		if ( catalog!=NULL) env->DeleteLocalRef(catalog);

		jmethodID mid_JBackupItem_setBootVol = env->GetMethodID(class_BackupItem, "setBootVol", "(Z)V");
		env->CallVoidMethod(jBackupItem, mid_JBackupItem_setBootVol, (jboolean)backupItem->bIsBootVol);

		jmethodID mid_JBackupItem_setSysVol = env->GetMethodID(class_BackupItem, "setSysVol", "(Z)V");
		env->CallVoidMethod(jBackupItem, mid_JBackupItem_setSysVol, (jboolean)backupItem->bIsSysVol);

		jmethodID mid_JBackupItem_setRefsVol = env->GetMethodID(class_BackupItem, "setRefsVol", "(Z)V");
		env->CallVoidMethod(jBackupItem, mid_JBackupItem_setRefsVol, (jboolean)backupItem->bIsRefsVol);

		jmethodID mid_JBackupItem_setNtfsVol = env->GetMethodID(class_BackupItem, "setNTFSVol", "(Z)V");
		env->CallVoidMethod(jBackupItem, mid_JBackupItem_setNtfsVol, (jboolean)backupItem->bIsNTFSVol);

		jmethodID mid_JBackupItem_setDedupVol = env->GetMethodID(class_BackupItem, "setDedupVol", "(Z)V");
		env->CallVoidMethod(jBackupItem, mid_JBackupItem_setDedupVol, (jboolean)backupItem->bIsDedupVol);

		jmethodID mid_JBackupItem_setHasReplicaDB = env->GetMethodID(class_BackupItem, "setHasReplicaDB", "(Z)V");
		env->CallVoidMethod(jBackupItem, mid_JBackupItem_setHasReplicaDB, (jboolean)backupItem->bContainReplica);

		env->CallBooleanMethod(jBkpItemArrList, id_ArrayList_add, jBackupItem);
		if (jBackupItem != NULL) env->DeleteLocalRef(jBackupItem);			
	}

	if (class_BackupItem != NULL) env->DeleteLocalRef(class_BackupItem);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);	

	return 0;
}

void AddBackupVMToList(JNIEnv *env,jobject backupVMList,VMInfoList& vmList){
	jclass class_ArrayList = env->GetObjectClass(backupVMList);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");	
	jclass class_JBackupVM = env->FindClass("com/ca/arcflash/webservice/jni/model/JBackupVM");
	jmethodID mid_JBackupVM_constructor = env->GetMethodID(class_JBackupVM, "<init>", "()V");


	for(VMInfoList::iterator backupVM = vmList.begin(); backupVM != vmList.end(); backupVM++)
	{
		jobject jBackupVM = env->NewObject(class_JBackupVM, mid_JBackupVM_constructor);

		jfieldID field_vmName	= env->GetFieldID(class_JBackupVM, "vmName", "Ljava/lang/String;");
		jstring vmName = WCHARToJString(env, (wchar_t*)backupVM->vmName.c_str());
		env->SetObjectField(jBackupVM, field_vmName, vmName);
		if ( vmName!=NULL) env->DeleteLocalRef(vmName);

		jfieldID field_username	= env->GetFieldID(class_JBackupVM, "username", "Ljava/lang/String;");
		jstring username = WCHARToJString(env, (wchar_t*)backupVM->userName.c_str());
		env->SetObjectField(jBackupVM, field_username, username);
		if ( username!=NULL) env->DeleteLocalRef(username);

		jfieldID field_uuid	= env->GetFieldID(class_JBackupVM, "uuid", "Ljava/lang/String;");
		jstring uuid = WCHARToJString(env, (wchar_t*)backupVM->uuid.c_str());
		env->SetObjectField(jBackupVM, field_uuid, uuid);
		if ( uuid!=NULL) env->DeleteLocalRef(uuid);

		jfieldID field_instanceUUID	= env->GetFieldID(class_JBackupVM, "instanceUUID", "Ljava/lang/String;");
		jstring instanceUUID = WCHARToJString(env, (wchar_t*)backupVM->instanceUuid.c_str());
		env->SetObjectField(jBackupVM, field_instanceUUID, instanceUUID);
		if ( instanceUUID!=NULL) env->DeleteLocalRef(instanceUUID);

		jfieldID field_destination	= env->GetFieldID(class_JBackupVM, "destination", "Ljava/lang/String;");
		jstring destination = WCHARToJString(env, (wchar_t*)backupVM->destionation.c_str());
		env->SetObjectField(jBackupVM, field_destination, destination);
		if ( destination!=NULL) env->DeleteLocalRef(destination);

		jfieldID field_browse_destination	= env->GetFieldID(class_JBackupVM, "browseDestination", "Ljava/lang/String;");
		jstring browseDestination = WCHARToJString(env, (wchar_t*)backupVM->browsedDestionation.c_str());
		env->SetObjectField(jBackupVM, field_browse_destination, browseDestination);
		if ( browseDestination!=NULL) env->DeleteLocalRef(browseDestination);

		jfieldID field_vmVMX	= env->GetFieldID(class_JBackupVM, "vmVMX", "Ljava/lang/String;");
		jstring vmVMX = WCHARToJString(env, (wchar_t*)backupVM->vmx.c_str());
		env->SetObjectField(jBackupVM, field_vmVMX, vmVMX);
		if ( vmVMX!=NULL) env->DeleteLocalRef(vmVMX);

		jfieldID field_vmHostName	= env->GetFieldID(class_JBackupVM, "vmHostName", "Ljava/lang/String;");
		jstring vmHostName = WCHARToJString(env, (wchar_t*)backupVM->hostName.c_str());
		env->SetObjectField(jBackupVM, field_vmHostName, vmHostName);
		if ( vmHostName!=NULL) env->DeleteLocalRef(vmHostName);

		jfieldID field_esxServerName	= env->GetFieldID(class_JBackupVM, "esxServerName", "Ljava/lang/String;");
		jstring esxServerName = WCHARToJString(env, (wchar_t*)backupVM->esxInfo.serverName.c_str());
		env->SetObjectField(jBackupVM, field_esxServerName, esxServerName);
		if ( esxServerName!=NULL) env->DeleteLocalRef(esxServerName);

		jfieldID field_protocol	= env->GetFieldID(class_JBackupVM, "protocol", "Ljava/lang/String;");
		jstring protocol = WCHARToJString(env, (wchar_t*)backupVM->esxInfo.protocol.c_str());
		env->SetObjectField(jBackupVM, field_protocol, protocol);
		if ( protocol!=NULL) env->DeleteLocalRef(protocol);

		jfieldID field_port	= env->GetFieldID(class_JBackupVM, "port", "I");
		env->SetIntField(jBackupVM, field_port, backupVM->esxInfo.viPort);

		jfieldID field_hypervisorType = env->GetFieldID(class_JBackupVM, "hypervisorType", "I");
		env->SetIntField(jBackupVM, field_hypervisorType, backupVM->hypervisorType);

		env->CallBooleanMethod(backupVMList, id_ArrayList_add, jBackupVM);
		if (jBackupVM != NULL) env->DeleteLocalRef(jBackupVM);
	}

	if (class_JBackupVM != NULL) env->DeleteLocalRef(class_JBackupVM);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);
}

void AddVolumeToDisk(JNIEnv *env,jobject volumeList,std::vector<VMVolume_Info> &vols){
	jclass class_ArrayList = env->GetObjectClass(volumeList);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");	
	jclass class_JVolume = env->FindClass("com/ca/arcflash/webservice/jni/model/JVolume");
	jmethodID mid_JVolume_constructor = env->GetMethodID(class_JVolume, "<init>", "()V");


	for(std::vector<VMVolume_Info>::iterator vmVolume = vols.begin(); vmVolume != vols.end(); vmVolume++)
	{		
		jobject jVolume = env->NewObject(class_JVolume, mid_JVolume_constructor);

		jfieldID field_driverLetter	= env->GetFieldID(class_JVolume, "driverLetter", "Ljava/lang/String;");
		jstring driverLetter = WCHARToJString(env, (wchar_t*)vmVolume->driverLetter.c_str());
		env->SetObjectField(jVolume, field_driverLetter, driverLetter);
		if ( driverLetter!=NULL) env->DeleteLocalRef(driverLetter);

		env->CallBooleanMethod(volumeList, id_ArrayList_add, jVolume);
		if (jVolume != NULL) env->DeleteLocalRef(jVolume);			
	}

	if (class_JVolume != NULL) env->DeleteLocalRef(class_JVolume);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);
}

void AddHypervVMbackupNetworkAdaptersToList(JNIEnv *env, jobject adapterList, vector<VMPersistConfiguration::CHyperVVMNetworkAdapter>& vecAdapters)
{
	using namespace VMPersistConfiguration;

	jclass class_ArrayList = env->GetObjectClass(adapterList);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");	
	jclass class_JAdapter = env->FindClass("com/ca/arcflash/webservice/jni/model/JVMNetworkConfig");
	jmethodID mid_JAdapter_constructor = env->GetMethodID(class_JAdapter, "<init>", "()V");

	for(vector<CHyperVVMNetworkAdapter>::iterator vmAdapter = vecAdapters.begin(); vmAdapter != vecAdapters.end(); vmAdapter++)
	{
		jobject jAdapter = env->NewObject(class_JAdapter, mid_JAdapter_constructor);

		//the adapter Id
		jfieldID field_adapterId	= env->GetFieldID(class_JAdapter, "id", "I");
		env->SetIntField(jAdapter, field_adapterId, vmAdapter->m_dwID);

		//the adapter type
		jfieldID field_adapterType	= env->GetFieldID(class_JAdapter, "adapterType", "I");
		env->SetIntField(jAdapter, field_adapterType, vmAdapter->m_dwResourceType);

		//the adapter friendly name
		jfieldID field_adapterFriendlyName = env->GetFieldID(class_JAdapter, "adapterFriendlyName", "Ljava/lang/String;");
		jstring adapterFriendlyName = WCHARToJString(env, (wchar_t*)(vmAdapter->m_strFriendlyName.c_str()));
		env->SetObjectField(jAdapter, field_adapterFriendlyName, adapterFriendlyName);
		if ( adapterFriendlyName !=NULL) env->DeleteLocalRef(adapterFriendlyName);

		//the switch uuid
		jfieldID field_switchUUID = env->GetFieldID(class_JAdapter, "switchUUID", "Ljava/lang/String;");
		jstring switchUUID = WCHARToJString(env, (wchar_t*)(vmAdapter->m_strSwitchName.c_str()));
		env->SetObjectField(jAdapter, field_switchUUID, switchUUID);
		if ( switchUUID !=NULL) env->DeleteLocalRef(switchUUID);

		//add the adapter to list
		env->CallBooleanMethod(adapterList, id_ArrayList_add, jAdapter);
		if (jAdapter != NULL) env->DeleteLocalRef(jAdapter);
	}

	if (class_JAdapter != NULL) env->DeleteLocalRef(class_JAdapter);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);
}

void AddHypervVMbackupDiskToList(JNIEnv *env,jobject diskList,HyperVVMDiskList& vmDiskList)
{
	jclass class_ArrayList = env->GetObjectClass(diskList);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");	
	jclass class_JDisk = env->FindClass("com/ca/arcflash/webservice/jni/model/JDisk");
	jmethodID mid_JDisk_constructor = env->GetMethodID(class_JDisk, "<init>", "()V");

	for(HyperVVMDiskList::iterator vmDisk = vmDiskList.begin(); vmDisk != vmDiskList.end(); vmDisk++)
	{
		jobject jDisk = env->NewObject(class_JDisk, mid_JDisk_constructor);

		jfieldID field_diskNumber	= env->GetFieldID(class_JDisk, "diskNumber", "I");
		env->SetIntField(jDisk, field_diskNumber, vmDisk->dwDiskNo);

		jfieldID field_signature = env->GetFieldID(class_JDisk, "signature", "I");
		env->SetIntField(jDisk, field_signature, vmDisk->stPersistInfo.dwSignature);

		jfieldID field_size = env->GetFieldID(class_JDisk, "size", "J");
		env->SetLongField(jDisk, field_size, vmDisk->stPersistInfo.ullSize);

		//the disk type
		jfieldID field_diskType	= env->GetFieldID(class_JDisk, "diskType", "I");
		env->SetIntField(jDisk, field_diskType, vmDisk->stPersistInfo.ulStorageType);

		//the disk path
		jfieldID field_diskUrl	= env->GetFieldID(class_JDisk, "diskUrl", "Ljava/lang/String;");
		jstring diskUrl = WCHARToJString(env, (wchar_t*)vmDisk->stPersistInfo.wszFullPath);
		env->SetObjectField(jDisk, field_diskUrl, diskUrl);
		if ( diskUrl!=NULL) env->DeleteLocalRef(diskUrl);

		//the disk name
		CString strDiskName = vmDisk->stPersistInfo.wszFullPath;
		INT nPos = strDiskName.ReverseFind(TEXT('\\'));
		strDiskName = strDiskName.Right(strDiskName.GetLength() - nPos - 1);
		jfieldID field_diskDataStore	= env->GetFieldID(class_JDisk, "diskDataStore", "Ljava/lang/String;");
		jstring diskDataStore = WCHARToJString(env, (wchar_t*)strDiskName.GetString());
		env->SetObjectField(jDisk, field_diskDataStore, diskDataStore);
		if ( diskDataStore!=NULL) env->DeleteLocalRef(diskDataStore);

		//volume info
		jfieldID field_volumes	= env->GetFieldID(class_JDisk, "volumes", "Ljava/util/List;");
		AddVolumeToDisk(env,env->GetObjectField(jDisk,field_volumes),vmDisk->vols);

		env->CallBooleanMethod(diskList, id_ArrayList_add, jDisk);
		if (jDisk != NULL) env->DeleteLocalRef(jDisk);
	}

	if (class_JDisk != NULL) env->DeleteLocalRef(class_JDisk);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);	
}

void AddBackupVMDiskToList(JNIEnv *env,jobject diskList,VMDiskList& vmDiskList){
	jclass class_ArrayList = env->GetObjectClass(diskList);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");	
	jclass class_JDisk = env->FindClass("com/ca/arcflash/webservice/jni/model/JDisk");
	jmethodID mid_JDisk_constructor = env->GetMethodID(class_JDisk, "<init>", "()V");


	for(VMDiskList::iterator vmDisk = vmDiskList.begin(); vmDisk != vmDiskList.end(); vmDisk++)
	{		
		jobject jDisk = env->NewObject(class_JDisk, mid_JDisk_constructor);

		jfieldID field_diskNumber	= env->GetFieldID(class_JDisk, "diskNumber", "I");
		env->SetIntField(jDisk, field_diskNumber, vmDisk->dwDiskNo);

		jfieldID field_signature = env->GetFieldID(class_JDisk, "signature", "I");
		env->SetIntField(jDisk, field_signature, vmDisk->dwSignature);

        jfieldID field_size = env->GetFieldID(class_JDisk, "size", "J");
		env->SetLongField(jDisk, field_size, vmDisk->diskTotalSize);

		jfieldID field_diskUrl	= env->GetFieldID(class_JDisk, "diskUrl", "Ljava/lang/String;");
		jstring diskUrl = WCHARToJString(env, (wchar_t*)vmDisk->diskUrl.c_str());
		env->SetObjectField(jDisk, field_diskUrl, diskUrl);
		if ( diskUrl!=NULL) env->DeleteLocalRef(diskUrl);

		jfieldID field_diskDataStore	= env->GetFieldID(class_JDisk, "diskDataStore", "Ljava/lang/String;");
		jstring diskDataStore = WCHARToJString(env, (wchar_t*)vmDisk->diskDataStor.c_str());
		env->SetObjectField(jDisk, field_diskDataStore, diskDataStore);
		if ( diskDataStore!=NULL) env->DeleteLocalRef(diskDataStore);

		jfieldID field_volumes	= env->GetFieldID(class_JDisk, "volumes", "Ljava/util/List;");
		AddVolumeToDisk(env,env->GetObjectField(jDisk,field_volumes),vmDisk->vols);

		env->CallBooleanMethod(diskList, id_ArrayList_add, jDisk);
		if (jDisk != NULL) env->DeleteLocalRef(jDisk);			
	}

	if (class_JDisk != NULL) env->DeleteLocalRef(class_JDisk);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);	


}


void jList2Vector(JNIEnv *env, jobject jlist, std::vector<std::wstring> &vec)
{
	jclass Class_ArrayList = env->GetObjectClass(jlist);
	jmethodID mid_size = env->GetMethodID(Class_ArrayList, "size", "()I");
	jmethodID mid_get = env->GetMethodID(Class_ArrayList, "get", "(I)Ljava/lang/Object;");

	jint size = env->CallIntMethod(jlist,mid_size);
	for(jint i=0;i < size;i++)
	{
		jstring jstr = (jstring)env->CallObjectMethod(jlist,mid_get, i);
		wchar_t* pvol = JStringToWCHAR(env, jstr);
		if(pvol != NULL)
		{
			std::wstring str = pvol;
			vec.push_back(str);
			free(pvol);
		}
		if(jstr != NULL){
			env->DeleteLocalRef(jstr);
		}
	}

	if (Class_ArrayList != NULL) env->DeleteLocalRef(Class_ArrayList);	
}

jobject LICENSE_INFO2JLicInfo(JNIEnv *env, LICENSE_INFO& licInfo)
{	
	jclass Class_LicInfo = env->FindClass("com/ca/arcflash/webservice/data/LicInfo");
	jmethodID mid_constructor = env->GetMethodID(Class_LicInfo, "<init>", "()V");
	jobject jlicInfo = env->NewObject(Class_LicInfo, mid_constructor);
	jfieldID field_base = env->GetFieldID(Class_LicInfo, "base", "I");
	jfieldID field_bLI = env->GetFieldID(Class_LicInfo, "bLI", "I");
	jfieldID field_allowBMR = env->GetFieldID(Class_LicInfo, "allowBMR", "I");
	jfieldID field_allowBMRAlt = env->GetFieldID(Class_LicInfo, "allowBMRAlt", "I");
	jfieldID field_protectSql = env->GetFieldID(Class_LicInfo, "protectSql", "I");
	jfieldID field_protectExchange = env->GetFieldID(Class_LicInfo, "protectExchange", "I");
	jfieldID field_protectHyperV = env->GetFieldID(Class_LicInfo, "protectHyperV", "I");

	jfieldID field_encryption = env->GetFieldID(Class_LicInfo, "dwEncryption", "I");
	jfieldID field_scheduledExport = env->GetFieldID(Class_LicInfo, "dwScheduledExport", "I");
	jfieldID field_exchangeDB = env->GetFieldID(Class_LicInfo, "dwExchangeDB", "I");
	jfieldID field_exchangeGR = env->GetFieldID(Class_LicInfo, "dwExchangeGR", "I");
	jfieldID field_D2D2D = env->GetFieldID(Class_LicInfo, "dwD2D2D", "I");


	env->SetIntField(jlicInfo, field_base, licInfo.dwBase);
	env->SetIntField(jlicInfo, field_bLI, licInfo.dwBLI);
	env->SetIntField(jlicInfo, field_allowBMR, licInfo.dwAllowBMR);
	env->SetIntField(jlicInfo, field_allowBMRAlt, licInfo.dwAllowBMRAlt);
	env->SetIntField(jlicInfo, field_protectSql, licInfo.dwProtectSql);
	env->SetIntField(jlicInfo, field_protectExchange, licInfo.dwProtectExchange);
	env->SetIntField(jlicInfo, field_protectHyperV, licInfo.dwProtectHyperV);

	env->SetIntField(jlicInfo, field_encryption, licInfo.dwEncryption);
	env->SetIntField(jlicInfo, field_scheduledExport, licInfo.dwScheduledExport);
	env->SetIntField(jlicInfo, field_exchangeDB, licInfo.dwExchangeDB);
	env->SetIntField(jlicInfo, field_exchangeGR, licInfo.dwExchangeGR);
	env->SetIntField(jlicInfo, field_D2D2D, licInfo.dwD2D2D);

	return jlicInfo;
}

jobject Get_TestConnectionStatus(JNIEnv *env, jint iFinalStatus,_DOWNLOAD_REQUEST_STATUS_INFO & out_stDownloadRequestStatusInfo)
{
	jclass Class_TestConnectionStatus = env->FindClass("com/ca/arcflash/webservice/jni/model/JTestConnectionStatus");
	jmethodID mid_constructor = env->GetMethodID(Class_TestConnectionStatus, "<init>", "()V");
	jobject jTestConnectionStatus = env->NewObject(Class_TestConnectionStatus, mid_constructor);
	
	jfieldID field_ErrorMsg = env->GetFieldID(Class_TestConnectionStatus, "ErrorMsg", "Ljava/lang/String;");
	jstring jErrorMsg = WCHARToJString(env,(LPWSTR)out_stDownloadRequestStatusInfo.wsLastErrorsMessage.c_str());
	env->SetObjectField(jTestConnectionStatus, field_ErrorMsg, jErrorMsg);
	if ( jErrorMsg!=NULL) 
		env->DeleteLocalRef(jErrorMsg);

	jfieldID field_FinalStatus = env->GetFieldID(Class_TestConnectionStatus, "FinalStatus", "Ljava/lang/String;");
	jstring wsFinalStatus = WCHARToJString(env,(LPWSTR)out_stDownloadRequestStatusInfo.wsFinalStatus.c_str());
	env->SetObjectField(jTestConnectionStatus, field_FinalStatus, wsFinalStatus);
	if ( wsFinalStatus!=NULL) 
		env->DeleteLocalRef(wsFinalStatus);

	jfieldID field_LastErrorCode= env->GetFieldID(Class_TestConnectionStatus, "LastErrorCode", "J");
	env->SetLongField(jTestConnectionStatus, field_LastErrorCode, out_stDownloadRequestStatusInfo.dwLastErrorCode);
	/*if ( field_LastErrorCode!=NULL) 
		env->DeleteLocalRef(field_LastErrorCode);*/

	jfieldID field_ServerStatus= env->GetFieldID(Class_TestConnectionStatus, "ServerStatus", "I");
	env->SetIntField(jTestConnectionStatus, field_ServerStatus, iFinalStatus);
	/*if ( field_ServerStatus!=NULL) 
		env->DeleteLocalRef(field_ServerStatus);*/

	jfieldID field_LastModifiedDate= env->GetFieldID(Class_TestConnectionStatus, "LastModifiedDate", "Ljava/lang/String;");
	jstring jLastModifiedDate = WCHARToJString(env,(LPWSTR)out_stDownloadRequestStatusInfo.stLastModifiedDateTime.wsLastModifiedDate.c_str());
	env->SetObjectField(jTestConnectionStatus, field_FinalStatus, jLastModifiedDate);
	if ( jLastModifiedDate!=NULL) 
		env->DeleteLocalRef(jLastModifiedDate);

	jfieldID field_LastModifiedTime= env->GetFieldID(Class_TestConnectionStatus, "LastModifiedTime", "Ljava/lang/String;");
	jstring jLastModifiedTime = WCHARToJString(env,(LPWSTR)out_stDownloadRequestStatusInfo.stLastModifiedDateTime.wsLastModifiedTime.c_str());
	env->SetObjectField(jTestConnectionStatus, field_FinalStatus, jLastModifiedTime);
	if ( jLastModifiedTime!=NULL) 
		env->DeleteLocalRef(jLastModifiedTime);

	return jTestConnectionStatus;
}
jobject _makeInteger (JNIEnv *jenv, int val) 
{
	jclass classobj = jenv->FindClass ("java/lang/Integer");
	jmethodID constructor = 
		jenv->GetMethodID (classobj, "<init>", "(I)V");
	return jenv->NewObject (classobj, constructor, (jint) val);
}


// the filetime is an UTC time
__time64_t _FILETIME_To_time64_t(FILETIME fileTime)
{
	__time64_t tRet = 0;

	FILETIME ft;
	if (FileTimeToLocalFileTime(&fileTime, &ft))
	{
		SYSTEMTIME sysTime;
		if (FileTimeToSystemTime(&ft, &sysTime))
		{
			if (sysTime.wYear >= 1900
				&& sysTime.wMonth >= 1 && sysTime.wMonth <= 12
				&& sysTime.wDay >= 1 && sysTime.wDay <= 31
				&& sysTime.wHour >= 0 && sysTime.wHour <= 23
				&& sysTime.wMinute >= 0 && sysTime.wMinute <= 59
				&& sysTime.wSecond >= 0 && sysTime.wSecond <= 59
				)
			{
				struct tm atm;

				atm.tm_sec = sysTime.wSecond;
				atm.tm_min = sysTime.wMinute;
				atm.tm_hour = sysTime.wHour;
				atm.tm_mday = sysTime.wDay;
				atm.tm_mon = sysTime.wMonth - 1;        // tm_mon is 0 based
				atm.tm_year = sysTime.wYear - 1900;     // tm_year is 1900 based
				atm.tm_isdst = -1;

				__time64_t temp = _mktime64(&atm);
				if (temp != -1);       // indicates an illegal input time
				{
					tRet = temp;
				}			
			}
		}
	}

	

	return tRet;

}

__time64_t _DWORD64_To_time64_t(DWORD64 dw64)
{
	ULARGE_INTEGER large;
	large.QuadPart = dw64;

	FILETIME ft;
	ft.dwHighDateTime = large.HighPart;
	ft.dwLowDateTime  = large.LowPart;

	return _FILETIME_To_time64_t(ft);

}

void MsgRecW2JMsgRec(JNIEnv *env, jobject& jmsgRec, PMsgRecW pd)
{
	jclass class_JMsgRec = env->FindClass("com/ca/arcflash/webservice/jni/model/JMsgRec");	

	jfieldID field_objType	= env->GetFieldID(class_JMsgRec, "objType", "J");
	jfieldID field_objDate = env->GetFieldID(class_JMsgRec, "objDate", "J");
	jfieldID field_objFlags = env->GetFieldID(class_JMsgRec, "objFlags", "J");
	jfieldID field_lowObjSize = env->GetFieldID(class_JMsgRec, "lowObjSize", "J");
	jfieldID field_highObjSize	= env->GetFieldID(class_JMsgRec, "highObjSize", "J");

	jfieldID field_objName	= env->GetFieldID(class_JMsgRec, "objName", "Ljava/lang/String;");
	jfieldID field_objInfo	= env->GetFieldID(class_JMsgRec, "objInfo", "Ljava/lang/String;");

	jfieldID field_lowObjSelfid	= env->GetFieldID(class_JMsgRec, "lowObjSelfid", "J");
	jfieldID field_highObjSelfid = env->GetFieldID(class_JMsgRec, "highObjSelfid", "J");
	jfieldID field_lowObjParentid	= env->GetFieldID(class_JMsgRec, "lowObjParentid", "J");
	jfieldID field_highObjParentid	= env->GetFieldID(class_JMsgRec, "highObjParentid", "J");	
	jfieldID field_lowObjBody	= env->GetFieldID(class_JMsgRec, "lowObjBody", "J");
	jfieldID field_highObjBody	= env->GetFieldID(class_JMsgRec, "highObjBody", "J");
	jfieldID field_cp_Flag	= env->GetFieldID(class_JMsgRec, "cp_Flag", "J");	

	jfieldID field_sender	= env->GetFieldID(class_JMsgRec, "sender", "Ljava/lang/String;");
	jfieldID field_receiver	= env->GetFieldID(class_JMsgRec, "receiver", "Ljava/lang/String;");
	jfieldID field_sentTime = env->GetFieldID(class_JMsgRec, "sentTime", "J");
	jfieldID field_receivedTime = env->GetFieldID(class_JMsgRec, "receivedTime", "J");
	jfieldID field_flag = env->GetFieldID(class_JMsgRec, "flag", "J");
	jfieldID field_itemSize = env->GetFieldID(class_JMsgRec, "itemSize", "J");

	jmethodID mid_JMsgRec_constructor = env->GetMethodID(class_JMsgRec, "<init>", "()V");
	jmsgRec = env->NewObject(class_JMsgRec, mid_JMsgRec_constructor);

	env->SetLongField(jmsgRec, field_objType, pd->objtype);
	env->SetLongField(jmsgRec, field_objDate, pd->objdate);
	env->SetLongField(jmsgRec, field_objFlags, pd->objflags);
	env->SetLongField(jmsgRec, field_lowObjSize, pd->lobjsize);
	env->SetLongField(jmsgRec, field_highObjSize, pd->hobjsize);
	env->SetLongField(jmsgRec, field_lowObjSelfid, pd->lobjselfid);
	env->SetLongField(jmsgRec, field_highObjSelfid, pd->hobjselfid);
	env->SetLongField(jmsgRec, field_lowObjParentid, pd->lobjparentid);
	env->SetLongField(jmsgRec, field_highObjParentid, pd->hobjparentid);
	env->SetLongField(jmsgRec, field_lowObjBody, pd->lobjbody);
	env->SetLongField(jmsgRec, field_highObjBody, pd->hobjbody);
	env->SetLongField(jmsgRec, field_cp_Flag, pd->cp_flag);

	env->SetLongField(jmsgRec, field_sentTime, _DWORD64_To_time64_t(pd->senttime));
	env->SetLongField(jmsgRec, field_receivedTime, _DWORD64_To_time64_t(pd->receivedtime));
	env->SetLongField(jmsgRec, field_flag, pd->flag);
	env->SetLongField(jmsgRec, field_itemSize, pd->itemSize);

	jstring jstr = WCHARToJString(env, pd->objname);
	env->SetObjectField(jmsgRec, field_objName, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	jstr = WCHARToJString(env, pd->objinfo);
	env->SetObjectField(jmsgRec, field_objInfo, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);	

	jstr = WCHARToJString(env, pd->sender);
	env->SetObjectField(jmsgRec, field_sender, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);	

	jstr = WCHARToJString(env, pd->receiver);
	env->SetObjectField(jmsgRec, field_receiver, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);	

	if (class_JMsgRec != NULL) env->DeleteLocalRef(class_JMsgRec);		
}

void AddMsgRecW2List(JNIEnv *env, jobject* list, PMsgRecW pd)
{
	jobject jMsgRec = NULL;
		
	MsgRecW2JMsgRec(env,jMsgRec,pd);

	jclass class_ArrayList = env->GetObjectClass(*list);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
	env->CallBooleanMethod(*list, id_ArrayList_add, jMsgRec);

	if (jMsgRec != NULL) env->DeleteLocalRef(jMsgRec);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);	
}


void AddPMsgSearchRecW2List(JNIEnv *env, jobject* list, PMsgSearchRecW pd)
{
	jclass class_JMsgSearchRec = env->FindClass("com/ca/arcflash/webservice/jni/model/JMsgSearchRec");	
	jmethodID mid_class_JMsgSearchRec_constructor = env->GetMethodID(class_JMsgSearchRec, "<init>", "()V");
	jobject jMsgSearchRec = env->NewObject(class_JMsgSearchRec, mid_class_JMsgSearchRec_constructor);

	jfieldID field_SessionNumber	= env->GetFieldID(class_JMsgSearchRec, "SessionNumber", "J");
	jfieldID field_SubSessionNumber = env->GetFieldID(class_JMsgSearchRec, "SubSessionNumber", "J");
	jfieldID field_edbType = env->GetFieldID(class_JMsgSearchRec, "edbType", "J");

	jfieldID field_mailboxOrSameLevelName	= env->GetFieldID(class_JMsgSearchRec, "mailboxOrSameLevelName", "Ljava/lang/String;");
	jfieldID field_edbFullPath	= env->GetFieldID(class_JMsgSearchRec, "edbFullPath", "Ljava/lang/String;");
    jfieldID field_edbDisplayName	= env->GetFieldID(class_JMsgSearchRec, "edbDisplayName", "Ljava/lang/String;");
	jfieldID field_mailFullDisplayPath	= env->GetFieldID(class_JMsgSearchRec, "mailFullDisplayPath", "Ljava/lang/String;");
	jfieldID field_msgRec	= env->GetFieldID(class_JMsgSearchRec, "msgRec", "Lcom/ca/arcflash/webservice/jni/model/JMsgRec;");	
	
	// fields related to the recovery point, for Encryption use
	jfieldID field_ulFullSessNum = env->GetFieldID(class_JMsgSearchRec, "ulFullSessNum", "J");
	jfieldID field_ulEncryptInfo = env->GetFieldID(class_JMsgSearchRec, "ulEncryptInfo", "J");
	jfieldID field_ulBKTime = env->GetFieldID(class_JMsgSearchRec, "ulBKTime", "J");
	jfieldID field_wzBKDest	= env->GetFieldID(class_JMsgSearchRec, "wzBKDest", "Ljava/lang/String;");
	jfieldID field_wzJobName	= env->GetFieldID(class_JMsgSearchRec, "wzJobName", "Ljava/lang/String;");
	jfieldID field_wzPWDHash	= env->GetFieldID(class_JMsgSearchRec, "wzPWDHash", "Ljava/lang/String;");
	jfieldID field_wzSessGUID	= env->GetFieldID(class_JMsgSearchRec, "wzSessGUID", "Ljava/lang/String;");
	jfieldID field_wzFullSessGUID	= env->GetFieldID(class_JMsgSearchRec, "wzFullSessGUID", "Ljava/lang/String;");
	
	env->SetLongField(jMsgSearchRec, field_SessionNumber, pd->SessionNumber);
	env->SetLongField(jMsgSearchRec, field_SubSessionNumber, pd->SubSessionNumber);
//	env->SetLongField(jMsgSearchRec, field_edbType, pd->edbType);

	env->SetLongField(jMsgSearchRec, field_ulFullSessNum, pd->ulFullSessNum);
	env->SetLongField(jMsgSearchRec, field_ulEncryptInfo, pd->ulEncryptInfo);
	env->SetLongField(jMsgSearchRec, field_ulBKTime, pd->ulBKTime);

	jstring jstr = WCHARToJString(env, pd->mailboxOrSameLevelName);
	env->SetObjectField(jMsgSearchRec, field_mailboxOrSameLevelName, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	jstr = WCHARToJString(env, pd->edbLogicalPath);
	env->SetObjectField(jMsgSearchRec, field_edbFullPath, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	/*jstr = WCHARToJString(env, pd->edbDisplayName);
	env->SetObjectField(jMsgSearchRec, field_edbDisplayName, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);*/

	jstr = WCHARToJString(env, pd->mailFullDisplayPath);
	env->SetObjectField(jMsgSearchRec, field_mailFullDisplayPath, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	jstr = WCHARToJString(env, pd->wzBKDest);
	env->SetObjectField(jMsgSearchRec, field_wzBKDest, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	jstr = WCHARToJString(env, pd->wzJobName);
	env->SetObjectField(jMsgSearchRec, field_wzJobName, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	jstr = WCHARToJString(env, pd->wzPWDHash);
	env->SetObjectField(jMsgSearchRec, field_wzPWDHash, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	jstr = WCHARToJString(env, pd->wzSessGUID);
	env->SetObjectField(jMsgSearchRec, field_wzSessGUID, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	jstr = WCHARToJString(env, pd->wzFullSessGUID);
	env->SetObjectField(jMsgSearchRec, field_wzFullSessGUID, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	jobject jMsgRec = NULL;		
	MsgRecW2JMsgRec(env, jMsgRec, &pd->msgRec);
	env->SetObjectField(jMsgSearchRec, field_msgRec, jMsgRec);

	if (jMsgRec != NULL) env->DeleteLocalRef(jMsgRec);	

	jclass class_ArrayList = env->GetObjectClass(*list);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
	env->CallBooleanMethod(*list, id_ArrayList_add, jMsgSearchRec);

	if (jMsgSearchRec != NULL) env->DeleteLocalRef(jMsgSearchRec);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);
	if (class_JMsgSearchRec != NULL) env->DeleteLocalRef(class_JMsgSearchRec);
}


void Add_AOEServer2List(JNIEnv *env, jobject* list, PEAOBJ_SERVER pServer)
{
	jobject jExchDiscItem = NULL;

	AOEServer2Item(env, jExchDiscItem, pServer);

	jclass class_ArrayList = env->GetObjectClass(*list);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
	env->CallBooleanMethod(*list, id_ArrayList_add, jExchDiscItem);

	if (jExchDiscItem != NULL) env->DeleteLocalRef(jExchDiscItem);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);
}

void Add_AOEExchangeItem2List(JNIEnv *env, jobject* list, st_exchange_item* pExchangeItem)
{
	jobject jExchDiscItem = NULL;

	AOEItem2Item(env, jExchDiscItem, pExchangeItem);

	jclass class_ArrayList = env->GetObjectClass(*list);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
	env->CallBooleanMethod(*list, id_ArrayList_add, jExchDiscItem);

	if (jExchDiscItem != NULL) env->DeleteLocalRef(jExchDiscItem);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);
}

void Add_AOEMailbox2List(JNIEnv *env, jobject* list, PEAOBJ_MAILBOX pMailbox)
{
	jobject jExchDiscItem = NULL;

	AOEMailbox2Item(env, jExchDiscItem, pMailbox);

	jclass class_ArrayList = env->GetObjectClass(*list);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
	env->CallBooleanMethod(*list, id_ArrayList_add, jExchDiscItem);

	if (jExchDiscItem != NULL) env->DeleteLocalRef(jExchDiscItem);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);
}

void AOEServer2Item(JNIEnv *env, jobject& jExchItem, PEAOBJ_SERVER pd)
{
	jclass class_JExchItem = env->FindClass("com/ca/arcflash/webservice/jni/model/JExchangeDiscoveryItem");	

	jfieldID field_pwszDN	= env->GetFieldID(class_JExchItem, "pwszDN", "Ljava/lang/String;");
	jfieldID field_pwszName	= env->GetFieldID(class_JExchItem, "pwszName", "Ljava/lang/String;");
	jfieldID field_pwszGUID	= env->GetFieldID(class_JExchItem, "pwszGUID", "Ljava/lang/String;");
	jfieldID field_nExVersion	= env->GetFieldID(class_JExchItem, "nExVersion", "I");
	jfieldID field_pwszObjClass	= env->GetFieldID(class_JExchItem, "pwszObjClass", "Ljava/lang/String;");
	jfieldID field_pwszVersion	= env->GetFieldID(class_JExchItem, "pwszVersion", "Ljava/lang/String;");

	jmethodID mid_JJExchItem_constructor = env->GetMethodID(class_JExchItem, "<init>", "()V");
	jExchItem = env->NewObject(class_JExchItem, mid_JJExchItem_constructor);

	jstring jstr = WCHARToJString(env, pd->pwszDN);
	env->SetObjectField(jExchItem, field_pwszDN, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);	

	jstr = WCHARToJString(env, pd->pwszName);
	env->SetObjectField(jExchItem, field_pwszName, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);	

	jstr = WCHARToJString(env, pd->pwszGuid);
	env->SetObjectField(jExchItem, field_pwszGUID, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	env->SetIntField(jExchItem, field_nExVersion, (int)(pd->nExVersion));

	jstr = WCHARToJString(env, pd->pwszobjClass);
	env->SetObjectField(jExchItem, field_pwszObjClass, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);	

	jstr = WCHARToJString(env, pd->pwszVersion);
	env->SetObjectField(jExchItem, field_pwszVersion, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);


	if (class_JExchItem != NULL) env->DeleteLocalRef(class_JExchItem);
}

void AOEItem2Item(JNIEnv *env, jobject& jExchItem, st_exchange_item* pd)
{
	jclass class_JExchItem = env->FindClass("com/ca/arcflash/webservice/jni/model/JExchangeDiscoveryItem");	

	jfieldID field_pwszDN	= env->GetFieldID(class_JExchItem, "pwszDN", "Ljava/lang/String;");
	jfieldID field_pwszName	= env->GetFieldID(class_JExchItem, "pwszName", "Ljava/lang/String;");
	jfieldID field_pwszGUID	= env->GetFieldID(class_JExchItem, "pwszGUID", "Ljava/lang/String;");

	jfieldID field_pwszOwnerSVR	= env->GetFieldID(class_JExchItem, "pwszOwnerSVR", "Ljava/lang/String;");
	jfieldID field_pwszLogPath	= env->GetFieldID(class_JExchItem, "pwszLogPath", "Ljava/lang/String;");
	jfieldID field_pwszSysPath	= env->GetFieldID(class_JExchItem, "pwszSysPath", "Ljava/lang/String;");
	jfieldID field_pwszEDBFile	= env->GetFieldID(class_JExchItem, "pwszEDBFile", "Ljava/lang/String;");

	jfieldID field_isRecovery	= env->GetFieldID(class_JExchItem, "isRecovery", "I");
	jfieldID field_isPublic		= env->GetFieldID(class_JExchItem, "isPublic", "I");


	jmethodID mid_JJExchItem_constructor = env->GetMethodID(class_JExchItem, "<init>", "()V");
	jExchItem = env->NewObject(class_JExchItem, mid_JJExchItem_constructor);

	jstring jstr = WCHARToJString(env, pd->pwszDN);
	env->SetObjectField(jExchItem, field_pwszDN, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);	

	jstr = WCHARToJString(env, pd->pwszName);
	env->SetObjectField(jExchItem, field_pwszName, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);	

	jstr = WCHARToJString(env, pd->pwszGUID);
	env->SetObjectField(jExchItem, field_pwszGUID, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);
	
	jstr = WCHARToJString(env, pd->pwszOwnerSVR);
	env->SetObjectField(jExchItem, field_pwszOwnerSVR, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);	

	jstr = WCHARToJString(env, pd->pwszLogPath);
	env->SetObjectField(jExchItem, field_pwszLogPath, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	jstr = WCHARToJString(env, pd->pwszSysPath);
	env->SetObjectField(jExchItem, field_pwszSysPath, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);	

	jstr = WCHARToJString(env, pd->pwszEDBFile);
	env->SetObjectField(jExchItem, field_pwszEDBFile, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);

	env->SetIntField(jExchItem, field_isRecovery, pd->IsRecovery);
	env->SetIntField(jExchItem, field_isPublic, pd->IsPublic);


	if (class_JExchItem != NULL) env->DeleteLocalRef(class_JExchItem);
}

void AOEMailbox2Item(JNIEnv *env, jobject& jExchItem, PEAOBJ_MAILBOX pd)
{
	jclass class_JExchItem = env->FindClass("com/ca/arcflash/webservice/jni/model/JExchangeDiscoveryItem");	

	jfieldID field_pwszDN	= env->GetFieldID(class_JExchItem, "pwszDN", "Ljava/lang/String;");
	jfieldID field_pwszName	= env->GetFieldID(class_JExchItem, "pwszName", "Ljava/lang/String;");

	jmethodID mid_JJExchItem_constructor = env->GetMethodID(class_JExchItem, "<init>", "()V");
	jExchItem = env->NewObject(class_JExchItem, mid_JJExchItem_constructor);

	jstring jstr = WCHARToJString(env, pd->pwszDN);
	env->SetObjectField(jExchItem, field_pwszDN, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);	

	jstr = WCHARToJString(env, pd->pwszName);
	env->SetObjectField(jExchItem, field_pwszName, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);	

	if (class_JExchItem != NULL) env->DeleteLocalRef(class_JExchItem);
}

void JCatalogJob2AFCatalogJob(JNIEnv *env, PAFCATALOGJOB pafJS, jobject* jcatalogJob)
{
	jclass class_JCatalogJob = env->GetObjectClass(*jcatalogJob);

	jfieldID field_ulShrMemID = env->GetFieldID(class_JCatalogJob, "ulShrMemID", "J");
	jfieldID field_ulSessNum = env->GetFieldID(class_JCatalogJob, "ulSessNum", "J");
	jfieldID field_ulSubSessNum = env->GetFieldID(class_JCatalogJob, "ulSubSessNum", "J");
	jfieldID field_pwszDestPath = env->GetFieldID(class_JCatalogJob, "pwszDestPath", "Ljava/lang/String;");
	jfieldID field_pwszUserName = env->GetFieldID(class_JCatalogJob, "pwszUserName", "Ljava/lang/String;");
	jfieldID field_pwszPassword = env->GetFieldID(class_JCatalogJob, "pwszPassword", "Ljava/lang/String;");

	pafJS->ulShrMemID = (ULONG)env->GetLongField(*jcatalogJob, field_ulShrMemID);
	pafJS->ulSessNum = (ULONG)env->GetLongField(*jcatalogJob, field_ulSessNum);
	pafJS->ulSubSessNum = (ULONG)env->GetLongField(*jcatalogJob, field_ulSubSessNum);
	pafJS->pwszDestPath = GetStringFromField(env, jcatalogJob, field_pwszDestPath);
	pafJS->pwszUserName = GetStringFromField(env, jcatalogJob, field_pwszUserName);
	pafJS->pwszPassword = GetStringFromField(env, jcatalogJob, field_pwszPassword);

	if (class_JCatalogJob != NULL) 
	{
		env->DeleteLocalRef(class_JCatalogJob);
	}
}

void FreeAFCatalogJob(PAFCATALOGJOB pafJS)
{
	if(pafJS->pwszDestPath != NULL)
	{
		free(pafJS->pwszDestPath);
	}

	if(pafJS->pwszUserName != NULL)
	{
		free(pafJS->pwszUserName);
	}

	if(pafJS->pwszPassword != NULL)
	{
		free(pafJS->pwszPassword);
	}
}




void Add_EdbCatalogInfo2List(JNIEnv *env, jobject* list, wstring edbName, BOOL edbFlag)
{
	jobject jEdbCatalogInfo = NULL;

	jclass class_JCatalogInfo_EDB = env->FindClass("com/ca/arcflash/webservice/jni/model/JCatalogInfo_EDB");
	jmethodID mid_JJCatalogInfo_EDB_constructor = env->GetMethodID(class_JCatalogInfo_EDB, "<init>", "()V");
	jEdbCatalogInfo = env->NewObject(class_JCatalogInfo_EDB, mid_JJCatalogInfo_EDB_constructor);

	jfieldID field_edbName	= env->GetFieldID(class_JCatalogInfo_EDB, "edbName", "Ljava/lang/String;");
	jfieldID field_isCatalogCreated	= env->GetFieldID(class_JCatalogInfo_EDB, "isCatalogCreated", "Z");

	jstring jstr = WCHARToJString(env, (wchar_t*)edbName.c_str());
	env->SetObjectField(jEdbCatalogInfo, field_edbName, jstr);
	if ( jstr!=NULL) env->DeleteLocalRef(jstr);	

	env->SetBooleanField(jEdbCatalogInfo, field_isCatalogCreated, edbFlag);	
	if (class_JCatalogInfo_EDB != NULL) env->DeleteLocalRef(class_JCatalogInfo_EDB);
	

	jclass class_ArrayList = env->GetObjectClass(*list);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
	env->CallBooleanMethod(*list, id_ArrayList_add, jEdbCatalogInfo);

	if (jEdbCatalogInfo != NULL) env->DeleteLocalRef(jEdbCatalogInfo);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);
}

void CatalogInfo2JCatalogInfo(JNIEnv *env, jobject& jCatalogInfo, PCATALOG_INFO_EX pCatalogInfo)
{
	jclass class_JCatalogInfo = env->FindClass("com/ca/arcflash/webservice/jni/model/JCatalogInfo");

	jmethodID mid_JJCatalogInfo_constructor = env->GetMethodID(class_JCatalogInfo, "<init>", "()V");
	jCatalogInfo = env->NewObject(class_JCatalogInfo, mid_JJCatalogInfo_constructor);

	jfieldID field_dwSubSessNo	= env->GetFieldID(class_JCatalogInfo, "dwSubSessNo", "J");
	jfieldID field_dwFlag	= env->GetFieldID(class_JCatalogInfo, "dwFlag", "J");
	jfieldID field_edbCatalogInfoList= env->GetFieldID(class_JCatalogInfo, "edbCatalogInfoList", "Ljava/util/List;");

	env->SetLongField(jCatalogInfo, field_dwSubSessNo, pCatalogInfo->dwSubSessNo);
	env->SetLongField(jCatalogInfo, field_dwFlag, pCatalogInfo->dwFlag);

	jclass  class_ArrayList = env->FindClass("java/util/ArrayList");
	jmethodID arrayList_constructor = env->GetMethodID(class_ArrayList, "<init>", "()V");
	jobject jArray = env->NewObject(class_ArrayList, arrayList_constructor);	

	for(map<wstring, BOOL>::iterator iter = pCatalogInfo->mapEDBCatalogStatus.begin();
		iter != pCatalogInfo->mapEDBCatalogStatus.end();
		iter ++)
	{
		Add_EdbCatalogInfo2List(env, &jArray, iter->first, iter->second);
	}

	env->SetObjectField(jCatalogInfo, field_edbCatalogInfoList, jArray);

	if(class_ArrayList != NULL)
		env->DeleteLocalRef(class_ArrayList);

	if (class_JCatalogInfo != NULL) env->DeleteLocalRef(class_JCatalogInfo);
}

void Add_CatalogInfo2List(JNIEnv *env, jobject* list, PCATALOG_INFO_EX pCatalogInfo)
{
	jobject jCatalogInfo = NULL;

	CatalogInfo2JCatalogInfo(env, jCatalogInfo, pCatalogInfo);

	jclass class_ArrayList = env->GetObjectClass(*list);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
	env->CallBooleanMethod(*list, id_ArrayList_add, jCatalogInfo);

	if (jCatalogInfo != NULL) env->DeleteLocalRef(jCatalogInfo);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);
}




int Convert2ApplicationStatus(JNIEnv *env,vector<CVMWriterMetadataItem> &appInfo,jobject appStatus){
	CONST TCHAR SQLWriterID[] = TEXT("a65faa63-5ea8-4ebc-9dbd-a0c4db26912a");  
	CONST TCHAR ExchangeWriterID[] = TEXT("76fe1ac4-15f7-4bcd-987e-8e1acb462fb7");
	bool isSqlInstalled = false;
	bool isExchangeInstalled = false;
	for(vector<CVMWriterMetadataItem>::iterator app = appInfo.begin(); app != appInfo.end(); app ++){
		bool retSql = (0 == _tcsicmp(app->m_wsWriterID.c_str(), SQLWriterID));
		//bool retEx = (0 == _tcsicmp(app->m_wsWriterID, ExchangeWriterID));

		if(retSql){
			isSqlInstalled = true;
		}else{
			isExchangeInstalled=true;
		}
		
	}
	jclass class_JApplicationStatus = env->FindClass("com/ca/arcflash/webservice/jni/model/JApplicationStatus");	
	
	jfieldID field_bSqlInstalled = env->GetFieldID(class_JApplicationStatus, "sqlInstalled", "Z");
	jfieldID field_bExchangeInstalled = env->GetFieldID(class_JApplicationStatus, "exchangeInstalled", "Z");

	jmethodID id_setSqlInstalled = env->GetMethodID(class_JApplicationStatus, "setSqlInstalled", "(Z)V");
	jmethodID id_setExchangeInstalled = env->GetMethodID(class_JApplicationStatus, "setExchangeInstalled", "(Z)V");

	env->CallVoidMethod(appStatus, id_setSqlInstalled, isSqlInstalled);
	env->CallVoidMethod(appStatus, id_setExchangeInstalled, isExchangeInstalled);

	if (class_JApplicationStatus != NULL) env->DeleteLocalRef(class_JApplicationStatus);
	return 0;
}

int Convert2MountSession(JNIEnv *env, jobject sessionList, vector<MNT_SESS> &sessios)
{
	jclass class_JMountSession = env->FindClass("com/ca/arcflash/webservice/jni/model/JMountSession");	
	jmethodID jMntSession_constructor = env->GetMethodID(class_JMountSession, "<init>", "()V");
	
	jclass class_ArrayList = env->GetObjectClass(sessionList);
	jmethodID addMethod = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
		
	for(vector<MNT_SESS>::iterator session = sessios.begin(); session != sessios.end(); session ++)
	{	
		jobject mntSession = env->NewObject(class_JMountSession, jMntSession_constructor);
		//JOB_CONTEXT2JobContext(env, jobIter, jobContext, class_JJobContext);
		jmethodID setSessNumMethod = env->GetMethodID(class_JMountSession, "setSessionNum", "(J)V");
		jmethodID setSessPathMethod = env->GetMethodID(class_JMountSession, "setSessionPath", "(Ljava/lang/String;)V");
	
		env->CallVoidMethod(mntSession, setSessNumMethod, (jlong)session->dwNo);
		
		env->CallVoidMethod(mntSession, setSessPathMethod, WCHARToJString(env,session->strSessFolder));
		env->CallBooleanMethod(sessionList, addMethod, mntSession);
	}

	if(class_JMountSession != NULL) env->DeleteLocalRef(class_JMountSession);

	return 0;
}

int AddVMOUNT_ITEM2List(JNIEnv *env, jobject jMountItemArrList, AFMOUNTMGR::AFMOUNTINFO *pInfoList, DWORD count)
{	
	jclass class_ArrayList = env->GetObjectClass(jMountItemArrList);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");	
	jclass class_MountItem = env->FindClass("com/ca/arcflash/webservice/jni/model/JMountedRecoveryPointItem");
	jmethodID mid_MountItem_constructor = env->GetMethodID(class_MountItem, "<init>", "()V");
	for(int i=0; i<count ; i++)
	{		
		jobject jMountItem = env->NewObject(class_MountItem, mid_MountItem_constructor);
		
		wstring sessionID=L"";
		CIxStr::Guid2Str(pInfoList[i].sessionId, sessionID);
		jfieldID field_sesionGuid= env->GetFieldID(class_MountItem, "sesionGuid", "Ljava/lang/String;");
		jstring sesionGuid = WCHARToJString(env, sessionID.c_str());
		env->SetObjectField(jMountItem, field_sesionGuid, sesionGuid);
		if ( sesionGuid!=NULL) env->DeleteLocalRef(sesionGuid);

		jfieldID field_sessionID= env->GetFieldID(class_MountItem, "sessionID", "J");
		env->SetLongField(jMountItem, field_sessionID, pInfoList[i].dwSessNo);

		jfieldID field_volumeSize = env->GetFieldID(class_MountItem, "volumeSize", "J");
		env->SetLongField(jMountItem, field_volumeSize, pInfoList[i].ullVolSize);

		jfieldID field_volumePath= env->GetFieldID(class_MountItem, "volumePath", "Ljava/lang/String;");
		jstring volumePath = WCHARToJString(env, (wchar_t*)pInfoList[i].szSourceMnt);
		env->SetObjectField(jMountItem, field_volumePath, volumePath);
		if ( volumePath!=NULL) env->DeleteLocalRef(volumePath);

		jfieldID field_volumeGuid= env->GetFieldID(class_MountItem, "volumeGuid", "Ljava/lang/String;");
		jstring volumeGuid = WCHARToJString(env, (wchar_t*)pInfoList[i].szSourceGuid);
		env->SetObjectField(jMountItem, field_volumeGuid, volumeGuid);
		if ( volumeGuid!=NULL) env->DeleteLocalRef(volumeGuid);

		jfieldID field_mountDiskSignature= env->GetFieldID(class_MountItem, "mountDiskSignature", "I");
		env->SetIntField(jMountItem, field_mountDiskSignature,pInfoList[i].dwDiskSig);

		jfieldID field_mountPath = env->GetFieldID(class_MountItem, "mountPath", "Ljava/lang/String;");
		jstring mountPath = WCHARToJString(env, (wchar_t*)pInfoList[i].szMnt);
		env->SetObjectField(jMountItem, field_mountPath, mountPath);
		if ( mountPath!=NULL) env->DeleteLocalRef(mountPath);
		
		jfieldID field_mountFlag= env->GetFieldID(class_MountItem, "mountFlag", "I");
		env->SetIntField(jMountItem, field_mountFlag, pInfoList[i].dwMntFlag);

		jfieldID field_date = env->GetFieldID(class_MountItem, "date", "Ljava/lang/String;");
		jstring date = WCHARToJString(env, (wchar_t*)pInfoList[i].szDate);
		env->SetObjectField(jMountItem, field_date, date);
		if ( date!=NULL) env->DeleteLocalRef(date);

		jfieldID field_time = env->GetFieldID(class_MountItem, "time", "Ljava/lang/String;");
		jstring time = WCHARToJString(env, (wchar_t*)pInfoList[i].szDetailTime);
		env->SetObjectField(jMountItem, field_time, time);
		if ( time!=NULL) env->DeleteLocalRef(time);

		jfieldID field_recoveryPointPath = env->GetFieldID(class_MountItem, "recoveryPointPath", "Ljava/lang/String;");
		jstring recoveryPointPath = WCHARToJString(env, (wchar_t*)pInfoList[i].szDest);
		env->SetObjectField(jMountItem, field_recoveryPointPath, recoveryPointPath);
		if ( recoveryPointPath!=NULL) env->DeleteLocalRef(recoveryPointPath);

		env->CallBooleanMethod(jMountItemArrList, id_ArrayList_add, jMountItem);
		if (jMountItem != NULL) env->DeleteLocalRef(jMountItem);			
	}

	if (class_MountItem != NULL) env->DeleteLocalRef(class_MountItem);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);

	return 0;
}

int AddVWSTRING_ITEM2List(JNIEnv *env, jobject jArrList, std::vector<wstring> vectorList)
{
	jclass list_class = env->GetObjectClass(jArrList);
	jmethodID addMethod = env->GetMethodID(list_class, "add", "(Ljava/lang/Object;)Z");

	for(vector<std::wstring>::iterator item = vectorList.begin(); item != vectorList.end(); item++)
	{
		jstring item_JStr = WCHARToJString(env, (wchar_t*)item->c_str());
		env->CallBooleanMethod(jArrList, addMethod, item_JStr);
		if(item_JStr != NULL)
			env->DeleteLocalRef(item_JStr);
	}

	if(list_class != NULL)
		env->DeleteLocalRef(list_class);

	return 0;
}

void MergeControl2JMergeJobMonitor(JNIEnv *env, PST_MERGE_CTRL mergeCtrl, jobject& jmjm)
{
	jclass jcls = env->GetObjectClass(jmjm);
	jmethodID setStatusMethod = env->GetMethodID(jcls, "setJobStatus", "(I)V");
	jmethodID setIDMethod = env->GetMethodID(jcls, "setDwJobID", "(I)V");
	jmethodID setOptMethod = env->GetMethodID(jcls, "setDwMergeOpt", "(I)V");
	jmethodID setDwMergeMethod = env->GetMethodID(jcls, "setDwMergeMethod", "(I)V");
	jmethodID setDwRetentionCnt = env->GetMethodID(jcls, "setDwRetentionCnt", "(I)V");
	jmethodID setDwSessStart = env->GetMethodID(jcls, "setDwSessStart", "(I)V");
	jmethodID setDwEndStart = env->GetMethodID(jcls, "setDwEndStart", "(I)V");
	jmethodID setDwMergePhase = env->GetMethodID(jcls, "setDwMergePhase", "(I)V");
	jmethodID setDwSessCnt2Merge = env->GetMethodID(jcls, "setDwSessCnt2Merge", "(I)V");
	jmethodID setDwSessCntMerged = env->GetMethodID(jcls, "setDwSessCntMerged", "(I)V");
	jmethodID setDwCurSess2Merge = env->GetMethodID(jcls, "setDwCurSess2Merge", "(I)V");
	jmethodID setDwDiskCnt2Merge = env->GetMethodID(jcls, "setDwDiskCnt2Merge", "(I)V");
	jmethodID setDwDiskCntMerged = env->GetMethodID(jcls, "setDwDiskCntMerged", "(I)V");
	jmethodID setDwCurDiskSig2Merge = env->GetMethodID(jcls, "setDwCurDiskSig2Merge", "(I)V");
	jmethodID setUllDiskBytes2Merge = env->GetMethodID(jcls, "setUllDiskBytes2Merge", "(J)V");
	jmethodID setUllDiskBytesMerged = env->GetMethodID(jcls, "setUllDiskBytesMerged", "(J)V");
	jmethodID setUllSessBytes2Merge = env->GetMethodID(jcls, "setUllSessBytes2Merge", "(J)V");
	jmethodID setUllSessBytesMerged = env->GetMethodID(jcls, "setUllSessBytesMerged", "(J)V");
	jmethodID setUllTotalBytes2Merge = env->GetMethodID(jcls, "setUllTotalBytes2Merge", "(J)V");
	jmethodID setUllTotalBytesMerged = env->GetMethodID(jcls, "setUllTotalBytesMerged", "(J)V");
	jmethodID setDwSessRangeCnt = env->GetMethodID(jcls, "setDwSessRangeCnt", "(I)V");
	jmethodID setDwSessRangeDoneCnt = env->GetMethodID(jcls, "setDwSessRangeDoneCnt", "(I)V");
	jmethodID setfMergePercentage = env->GetMethodID(jcls, "setfMergePercentage", "(F)V");
	jmethodID setStartTime = env->GetMethodID(jcls, "setUllStartTime", "(J)V");

	jmethodID currentMergeRangeStart = env->GetMethodID(jcls, "setCurrentMergeRangeStart", "(I)V");
	jmethodID currentMergeRangeEnd = env->GetMethodID(jcls, "setCurrentMergeRangeEnd", "(I)V");

	env->CallVoidMethod(jmjm, setStatusMethod, mergeCtrl->stWField.stMergeStatus.dwJobStatus);
	env->CallVoidMethod(jmjm, setIDMethod, mergeCtrl->stRField.dwJobID);
	env->CallVoidMethod(jmjm, setOptMethod, mergeCtrl->stRField.dwMergeOpt);
	env->CallVoidMethod(jmjm, setDwMergeMethod, mergeCtrl->stRField.dwMergeMethod);
	env->CallVoidMethod(jmjm, setDwSessStart, mergeCtrl->stRField.dwSessStart);
	env->CallVoidMethod(jmjm, setDwRetentionCnt, mergeCtrl->stRField.dwRetentionCnt);
	env->CallVoidMethod(jmjm, setDwEndStart, mergeCtrl->stRField.dwEndStart);
	env->CallVoidMethod(jmjm, setDwMergePhase, mergeCtrl->stWField.stMergeStatus.dwMergePhase);
	env->CallVoidMethod(jmjm, setDwSessCnt2Merge, mergeCtrl->stWField.stMergeStatus.dwSessCnt2Merge);
	env->CallVoidMethod(jmjm, setDwSessCntMerged, mergeCtrl->stWField.stMergeStatus.dwSessCntMerged);
	env->CallVoidMethod(jmjm, setDwCurSess2Merge, mergeCtrl->stWField.stMergeStatus.dwCurSess2Merge);
	env->CallVoidMethod(jmjm, setDwDiskCnt2Merge, mergeCtrl->stWField.stMergeStatus.dwDiskCnt2Merge);
	env->CallVoidMethod(jmjm, setDwDiskCntMerged, mergeCtrl->stWField.stMergeStatus.dwDiskCntMerged);
	env->CallVoidMethod(jmjm, setDwCurDiskSig2Merge, mergeCtrl->stWField.stMergeStatus.dwCurDiskSig2Merge);
	env->CallVoidMethod(jmjm, setUllDiskBytes2Merge, mergeCtrl->stWField.stMergeStatus.ullDiskBytes2Merge);
	env->CallVoidMethod(jmjm, setUllDiskBytesMerged, mergeCtrl->stWField.stMergeStatus.ullDiskBytesMerged);
	env->CallVoidMethod(jmjm, setUllSessBytes2Merge, mergeCtrl->stWField.stMergeStatus.ullSessBytes2Merge);
	env->CallVoidMethod(jmjm, setUllSessBytesMerged, mergeCtrl->stWField.stMergeStatus.ullSessBytesMerged);
	env->CallVoidMethod(jmjm, setUllTotalBytes2Merge, mergeCtrl->stWField.stMergeStatus.ullTotalBytes2Merge);
	env->CallVoidMethod(jmjm, setUllTotalBytesMerged, mergeCtrl->stWField.stMergeStatus.ullTotalBytesMerged);
	env->CallVoidMethod(jmjm, setDwSessRangeCnt, mergeCtrl->stWField.stMergeStatus.dwSessRangeCnt);
	env->CallVoidMethod(jmjm, setDwSessRangeDoneCnt, mergeCtrl->stWField.stMergeStatus.dwSessRangeDoneCnt);	
	env->CallVoidMethod(jmjm, setfMergePercentage, mergeCtrl->stWField.stMergeStatus.fMergePercentage);
	env->CallVoidMethod(jmjm, setStartTime, mergeCtrl->stWField.stMergeStatus.ullJobStartTime*1000); // for job monitor, web service expects milliseconds, but backend sets seconds in job monitor, so we convert it here.

	env->CallVoidMethod(jmjm, currentMergeRangeStart, mergeCtrl->stWField.stMergeStatus.stSessRangeList[0].dwStartSess);
	env->CallVoidMethod(jmjm, currentMergeRangeEnd, mergeCtrl->stWField.stMergeStatus.stSessRangeList[0].dwEndSess);
}

void JMergeJobScript2CMergeJS(JNIEnv *env, jobject& jmergeJS, CMergeJS& cmergeJS)
{
	jclass jcls = env->GetObjectClass(jmergeJS);
	
	jmethodID getDwJobID = env->GetMethodID(jcls, "getDwJobID", "()J");
	jmethodID getDwMergeOpt = env->GetMethodID(jcls, "getDwMergeOpt", "()I");
	jmethodID getDwMergeMethod = env->GetMethodID(jcls, "getDwMergeMethod", "()I");
	jmethodID getDwRetentionCnt = env->GetMethodID(jcls, "getDwRetentionCnt", "()I");
	jmethodID getDwStartSess = env->GetMethodID(jcls, "getDwStartSess", "()J");
	jmethodID getDwEndSess = env->GetMethodID(jcls, "getDwEndSess", "()J");
	jmethodID getDwCryptoInfo = env->GetMethodID(jcls, "getDwCryptoInfo", "()I");
	jmethodID getDwCompressInfo = env->GetMethodID(jcls, "getDwCompressInfo", "()I");
	jmethodID getWsSessPWD = env->GetMethodID(jcls, "getWsSessPWD", "()Ljava/lang/String;");
	jmethodID getWsFolderPath = env->GetMethodID(jcls, "getWsFolderPath", "()Ljava/lang/String;");
	jmethodID getWsDomainName = env->GetMethodID(jcls, "getWsDomainName", "()Ljava/lang/String;");
	jmethodID getWsUserName = env->GetMethodID(jcls, "getWsUserName", "()Ljava/lang/String;");
	jmethodID getWsUserPWD = env->GetMethodID(jcls, "getWsUserPWD", "()Ljava/lang/String;");
	jmethodID getWsVMGUID = env->GetMethodID(jcls, "getWsVMGUID", "()Ljava/lang/String;");
	jmethodID getDwSessionType = env->GetMethodID(jcls, "getDwSessionType", "()I");
	jmethodID getUsingCompress = env->GetMethodID(jcls, "getUsingCompress", "()I");
	jmethodID getSupportCompress = env->GetMethodID(jcls, "getSupportCompress", "()I");
	jmethodID getEncAlgType = env->GetMethodID(jcls, "getEncAlgType", "()I");
	jmethodID getEncLibtype = env->GetMethodID(jcls, "getEncLibtype", "()I");
	jmethodID getIsEncrypted = env->GetMethodID(jcls, "getIsEncrypted", "()I");
	jmethodID getProductType = env->GetMethodID(jcls, "getProductType", "()I");
	jmethodID getDwDailyCnt = env->GetMethodID(jcls, "getDwDailyCnt", "()I");
	jmethodID getDwWeeklyCnt = env->GetMethodID(jcls, "getDwWeeklyCnt", "()I");	
	jmethodID getDwMonthlyCnt = env->GetMethodID(jcls, "getDwMonthlyCnt", "()I");
	jmethodID getRpsSvrName = env->GetMethodID(jcls, "getRpsSvrName", "()Ljava/lang/String;");
	jmethodID getRpsSvrGUID = env->GetMethodID(jcls, "getRpsSvrGUID", "()Ljava/lang/String;");
	jmethodID getDsDisplayName = env->GetMethodID(jcls, "getDsDisplayName", "()Ljava/lang/String;");
	jmethodID getDsGUID = env->GetMethodID(jcls, "getDsGUID", "()Ljava/lang/String;");
	jmethodID getWsD2DAgentName = env->GetMethodID(jcls, "getWsD2DAgentName", "()Ljava/lang/String;");	
	jmethodID getCfTime = env->GetMethodID(jcls, "getArchiveConfigTime", "()J");		// New, For ArchiveToTape
	jmethodID getSrcSel = env->GetMethodID(jcls, "getArchiveSourceSelection", "()J");	// New, For ArchiveToTape
	//jmethodID getDayArr = env->GetMethodID(jcls, "getArchiveDailySelectedDays", "()[Z");// New, For ArchiveToTape, No use now

	cmergeJS.BackupDest.wsDomainName = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getWsDomainName));
	cmergeJS.BackupDest.wsFolderPath = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getWsFolderPath));
	cmergeJS.BackupDest.wsUserName = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getWsUserName));
	cmergeJS.BackupDest.wsUserPWD = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getWsUserPWD));
	cmergeJS.dwCompressInfo = env->CallIntMethod(jmergeJS, getDwCompressInfo);
	cmergeJS.dwCryptoInfo = env->CallIntMethod(jmergeJS, getDwCryptoInfo);
	cmergeJS.dwEndSess = env->CallLongMethod(jmergeJS, getDwEndSess);
	cmergeJS.dwJobID = env->CallLongMethod(jmergeJS, getDwJobID);
	cmergeJS.dwMergeMethod = env->CallIntMethod(jmergeJS, getDwMergeMethod);
	cmergeJS.dwMergeOpt = env->CallIntMethod(jmergeJS, getDwMergeOpt);
	cmergeJS.dwRetentionCnt = env->CallIntMethod(jmergeJS, getDwRetentionCnt);
	cmergeJS.dwStartSess = env->CallLongMethod(jmergeJS, getDwStartSess);
	cmergeJS.wsVMGUID = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getWsVMGUID));
	cmergeJS.dwSessType = env->CallIntMethod(jmergeJS, getDwSessionType);
	cmergeJS.dwPrdType = env->CallIntMethod(jmergeJS, getProductType);
	cmergeJS.dwDailyCnt = env->CallIntMethod(jmergeJS, getDwDailyCnt);
	cmergeJS.dwWeeklyCnt = env->CallIntMethod(jmergeJS, getDwWeeklyCnt);
	cmergeJS.dwMonthlyCnt = env->CallIntMethod(jmergeJS, getDwMonthlyCnt);
	cmergeJS.wsRpsSvrName = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getRpsSvrName));
	cmergeJS.wsRpsSvrGUID = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getRpsSvrGUID));
	cmergeJS.wsDsDisplayName = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getDsDisplayName));
	cmergeJS.wsDsGUID = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getDsGUID));
	cmergeJS.wsD2DAgentName = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getWsD2DAgentName));	
	cmergeJS.dwArchiveTypeFlag = (DWORD)env->CallLongMethod(jmergeJS, getSrcSel);			// New, For ArchiveToTape
	cmergeJS.llArchiveScheduleTime = (LONGLONG)env->CallLongMethod(jmergeJS, getCfTime);	// New, For ArchiveToTape
}

void ActiveMergeVector2JActiveMergeJobs(JNIEnv *env, jobject& jJobs, ActJobVector& jobs)
{	
	jclass list_class = env->GetObjectClass(jJobs);
	jmethodID addMethod = env->GetMethodID(list_class, "add", "(Ljava/lang/Object;)Z");

	jclass activeMergeJobClass = env->FindClass("com/ca/arcflash/service/jni/model/JMergeActiveJob");
	jmethodID jobConstructor = env->GetMethodID(activeMergeJobClass, "<init>", "()V");
 	
	jmethodID setJobID = env->GetMethodID(activeMergeJobClass, "setJobId", "(I)V");
	jmethodID setProcessId = env->GetMethodID(activeMergeJobClass, "setProcessId", "(I)V");
    jmethodID setJobType = env->GetMethodID(activeMergeJobClass, "setJobType", "(I)V");
	jmethodID setVmInstanceUUID = env->GetMethodID(activeMergeJobClass, "setVmInstanceUUID", "(Ljava/lang/String;)V");
	jmethodID setJsPath = env->GetMethodID(activeMergeJobClass, "setJsPath", "(Ljava/lang/String;)V");

	ActJobVector::iterator iter;
	for(iter = jobs.begin(); iter != jobs.end(); iter ++) 
	{	
		jobject job = env->NewObject(activeMergeJobClass, jobConstructor);
		env->CallVoidMethod(job, setJobID, iter->dwJobID);
		env->CallVoidMethod(job, setProcessId, iter->dwProcID);
        env->CallVoidMethod(job, setJobType, (iter->dwJobType & EJT_RPS_SIDE_JOB));
		env->CallVoidMethod(job, setVmInstanceUUID, WCHARToJString(env, iter->wzVMGUID));
		env->CallVoidMethod(job, setJsPath, WCHARToJString(env, iter->wzJSPath));

		env->CallBooleanMethod(jJobs, addMethod, job);
	}

	if(activeMergeJobClass != NULL)
		env->DeleteLocalRef(activeMergeJobClass);
}

//////////////////////////////////////////////////////////////////////////////
// Begin of GoThroughJavaList()

void GoThroughJavaList( JNIEnv * pEnv, jobject javaList, PFN_LISTITEMPROCESSOR pfnProcessListItem, LPARAM lParam )
{
	if (pfnProcessListItem == NULL)
		return;

	jclass classList = pEnv->GetObjectClass( javaList );
	jmethodID midListSize = pEnv->GetMethodID( classList, "size", "()I" );
	jmethodID midListGet = pEnv->GetMethodID( classList, "get", "(I)Ljava/lang/Object;" );

	jint nListSize = pEnv->CallIntMethod( javaList, midListSize );
	for (jint i = 0; i < nListSize; i ++)
	{
		jobject item = pEnv->CallObjectMethod( javaList, midListGet, i );
		pfnProcessListItem( pEnv, item, lParam );
	}
}

void AddItemToWstringVector( JNIEnv * pEnv, jobject item, LPARAM lParam )
{
	vector<wstring> * pStrVector = (vector<wstring> *)lParam;
	pStrVector->push_back( JStringToWString( pEnv, (jstring)item ) );
}

// End of GoThroughJavaList()

void AddPathsToList(JNIEnv *env, jobject pathsList, PSyncLogFiles pLogFiles, UINT count)
{
	if(pLogFiles != NULL)
	{
		jclass listClass = env->GetObjectClass(pathsList);
		jmethodID addMethod = env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");
		for(int i = 0; i < count; i++)
		{
			jstring logPath = WCHARToJString(env, pLogFiles[i].szFilePath);
			env->CallBooleanMethod(pathsList, addMethod, logPath);
		}
	}
}

void JFlashJobHistoryFilter2FLASHDB_JOB_HISTORY_FILTER_COL(JNIEnv *env, jobject& jFilter, FLASHDB_JOB_HISTORY_FILTER_COL &jobFilter)
{	
	jclass jcls = env->GetObjectClass(jFilter); 
	
	jmethodID getJobID = env->GetMethodID(jcls, "getJobID", "()J");
	jmethodID getJobType = env->GetMethodID(jcls, "getJobType", "()J");
	jmethodID getJobStatus = env->GetMethodID(jcls, "getJobStatus", "()I");
	jmethodID getAgentNodeUUID = env->GetMethodID(jcls, "getAgentNodeUUID", "()Ljava/lang/String;");
	jmethodID getStartTime = env->GetMethodID(jcls, "getStartTimeValue", "()J");
	jmethodID getEndTime = env->GetMethodID(jcls, "getEndTimeValue", "()J");

	jobFilter.ullJobId = env->CallLongMethod(jFilter, getJobID);
	jobFilter.dwJobType = env->CallLongMethod(jFilter, getJobType);
	jobFilter.dwStatus = env->CallIntMethod(jFilter, getJobStatus);
	wcsncpy_s(jobFilter.wszDisposeNodeUUID, 
		_countof(jobFilter.wszDisposeNodeUUID), 
		JStringToWCHAR(env, (jstring)env->CallObjectMethod(jFilter, getAgentNodeUUID)),
		_TRUNCATE);
	jobFilter.dwFilterFlags = JOBHISTORY_QUERY_BY_AGENTNODE;
	jobFilter.ullAfterUTCStartTime = env->CallLongMethod(jFilter, getStartTime);
	jobFilter.ullAfterUTCEndTime = env->CallLongMethod(jFilter, getEndTime);
	
}

void PFLASHDB_JOB_HISTORY2JFlashJobHistoryReult(JNIEnv *env, PFLASHDB_JOB_HISTORY pJobHistory, jobject &jJobHistory)
{
	jclass jcls = env->GetObjectClass(jJobHistory);

	jmethodID setId = env->GetMethodID(jcls, "setId", "(I)V");
	jmethodID setJobID = env->GetMethodID(jcls, "setJobId", "(J)V");
	jmethodID setJobType = env->GetMethodID(jcls, "setJobType", "(I)V");
	jmethodID setJobStatus = env->GetMethodID(jcls, "setJobStatus", "(I)V");
	jmethodID setJobMethod = env->GetMethodID(jcls, "setJobMethod", "(I)V");
	jmethodID setJobUTCStartTime = env->GetMethodID(jcls, "setJobUTCStartTime", "(Ljava/lang/String;)V");
	jmethodID setJobLocalStartTime = env->GetMethodID(jcls, "setJobLocalStartTime", "(Ljava/lang/String;)V");
	jmethodID setJobUTCEndTime = env->GetMethodID(jcls, "setJobUTCEndTime", "(Ljava/lang/String;)V");
	jmethodID setJobLocalEndTime = env->GetMethodID(jcls, "setJobLocalEndTime", "(Ljava/lang/String;)V");
	jmethodID setRunningNode = env->GetMethodID(jcls, "setJobRunningNode", "(Ljava/lang/String;)V");
	jmethodID setRunningNodeUUID = env->GetMethodID(jcls, "setJobRunningNodeUUID", "(Ljava/lang/String;)V");
	jmethodID setDisposeNode = env->GetMethodID(jcls, "setJobDisposeNode", "(Ljava/lang/String;)V");
	jmethodID setDisposeNodeUUID = env->GetMethodID(jcls, "setJobDisposeNodeUUID", "(Ljava/lang/String;)V");
	jmethodID setTargetUUID = env->GetMethodID(jcls, "setTargetUUID", "(Ljava/lang/String;)V");
	jmethodID setSourceUUID = env->GetMethodID(jcls, "setSourceUUID", "(Ljava/lang/String;)V");
	jmethodID setDatastoreUUID = env->GetMethodID(jcls, "setDatastoreUUID", "(Ljava/lang/String;)V");
	jmethodID setTargetDatastoreUUID = env->GetMethodID(jcls, "setTargetDatastoreUUID", "(Ljava/lang/String;)V");
	jmethodID setJobName = env->GetMethodID(jcls, "setJobName", "(Ljava/lang/String;)V");
	jmethodID setPlanUUID = env->GetMethodID(jcls, "setPlanUUID", "(Ljava/lang/String;)V");
	jmethodID setTargetPlanUUID = env->GetMethodID(jcls, "setTargetPlanUUID", "(Ljava/lang/String;)V");


	env->CallVoidMethod(jJobHistory, setId, pJobHistory->dwID);
	env->CallVoidMethod(jJobHistory, setJobID, pJobHistory->ullJobId);
	env->CallVoidMethod(jJobHistory, setJobType, pJobHistory->dwJobType);
	env->CallVoidMethod(jJobHistory, setJobStatus, pJobHistory->dwStatus);
	env->CallVoidMethod(jJobHistory, setJobMethod, pJobHistory->dwJobMethod);

	
	env->CallVoidMethod(jJobHistory, setJobUTCStartTime, ULONGLONG2TimeJString(env, pJobHistory->ullUTCStartTime));
	env->CallVoidMethod(jJobHistory, setJobLocalStartTime, ULONGLONG2TimeJString(env, pJobHistory->ullLocalStartTime));
	env->CallVoidMethod(jJobHistory, setJobUTCEndTime, ULONGLONG2TimeJString(env, pJobHistory->ullUTCEndTime));
	env->CallVoidMethod(jJobHistory, setJobLocalEndTime, ULONGLONG2TimeJString(env, pJobHistory->ullLocalEndTime));

	env->CallVoidMethod(jJobHistory, setRunningNode, WCHARToJString(env, pJobHistory->wszRunningNode));
	env->CallVoidMethod(jJobHistory, setRunningNodeUUID, WCHARToJString(env, pJobHistory->wszRunningNodeUUID));
	env->CallVoidMethod(jJobHistory, setDisposeNode, WCHARToJString(env, pJobHistory->wszDisposeNode));
	env->CallVoidMethod(jJobHistory, setDisposeNodeUUID, WCHARToJString(env, pJobHistory->wszDisposeNodeUUID));
	env->CallVoidMethod(jJobHistory, setTargetUUID, WCHARToJString(env, pJobHistory->wszTargetUUID));
	env->CallVoidMethod(jJobHistory, setSourceUUID, WCHARToJString(env, pJobHistory->wszSourceUUID));
	env->CallVoidMethod(jJobHistory, setDatastoreUUID, WCHARToJString(env, pJobHistory->wszDataStoreUUID));
	env->CallVoidMethod(jJobHistory, setTargetDatastoreUUID, WCHARToJString(env, pJobHistory->wszTargetDataStoreUUID));
	env->CallVoidMethod(jJobHistory, setJobName, WCHARToJString(env, pJobHistory->wszJobName));
	env->CallVoidMethod(jJobHistory, setPlanUUID, WCHARToJString(env, pJobHistory->wszPlanUUID));
	env->CallVoidMethod(jJobHistory, setTargetPlanUUID, WCHARToJString(env, pJobHistory->wszTargetPlanUUID));
}

void PFLASHDB_JOB_HISTORY2JFlashJobHistoryResult
	(JNIEnv *env, PFLASHDB_JOB_HISTORY pJobHistory, ULONGLONG ullCnt, ULONGLONG ullTotalCnt, jobject &jJobHistoryResult)
{

	jclass class_JobHistoryResult = env->GetObjectClass(jJobHistoryResult);
	jclass class_ArrayList = env->FindClass("java/util/List");
	jclass class_JobHistory = env->FindClass("com/ca/arcflash/jni/common/JJobHistory");

	jmethodID jobConstructor = env->GetMethodID(class_JobHistory, "<init>", "()V");
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");


	jmethodID setTotalCount = env->GetMethodID(class_JobHistoryResult, "setTotalCount", "(J)V");
	jmethodID getJobHistory = env->GetMethodID(class_JobHistoryResult, "getJobHistory", "()Ljava/util/List;");

	env->CallLongMethod(jJobHistoryResult, setTotalCount, ullTotalCnt);
	jobject list = env->CallObjectMethod(jJobHistoryResult, getJobHistory);

	for(int i = 0; i < ullCnt; i++)
	{
		jobject jJobHistory = env->NewObject(class_JobHistory, jobConstructor);
		PFLASHDB_JOB_HISTORY2JFlashJobHistoryReult(env, (pJobHistory+i), jJobHistory);

		env->CallBooleanMethod(list, id_ArrayList_add, jJobHistory);
	}
}

void JJobHistory2PFLASHDB_JOB_HISTORY( JNIEnv* env, jobject& jJobHistory, PFLASHDB_JOB_HISTORY pHistory )
{
	jclass class_JJobHistory			= env->FindClass("com/ca/arcflash/jni/common/JJobHistory");	
	jfieldID field_jobId				= env->GetFieldID( class_JJobHistory, "jobId",				"J" );
	jfieldID field_jobType				= env->GetFieldID( class_JJobHistory, "jobType",			"I" );
	jfieldID field_jobStatus			= env->GetFieldID( class_JJobHistory, "jobStatus",			"I" );
	jfieldID field_jobMethod			= env->GetFieldID( class_JJobHistory, "jobMethod",			"I" );
    jfieldID field_periodRetentionFlag 	= env->GetFieldID( class_JJobHistory, "periodRetentionFlag", "I" );
	jfieldID field_jobUTCStartTime		= env->GetFieldID( class_JJobHistory, "jobUTCStartTime",	"Ljava/lang/String;" );
	jfieldID field_jobRunningNode		= env->GetFieldID( class_JJobHistory, "jobRunningNode",		"Ljava/lang/String;" );
	jfieldID field_jobRunningNodeUUID	= env->GetFieldID( class_JJobHistory, "jobRunningNodeUUID", "Ljava/lang/String;" );
	jfieldID field_jobDisposeNode		= env->GetFieldID( class_JJobHistory, "jobDisposeNode",		"Ljava/lang/String;" );
	jfieldID field_jobDisposeNodeUUID	= env->GetFieldID( class_JJobHistory, "jobDisposeNodeUUID", "Ljava/lang/String;" );
	jfieldID field_datastoreUUID		= env->GetFieldID( class_JJobHistory, "datastoreUUID",		"Ljava/lang/String;" );
	jfieldID field_datastoreVersion		= env->GetFieldID( class_JJobHistory, "datastoreVersion",	"J" );	
	jfieldID field_targetDatastoreUUID	= env->GetFieldID( class_JJobHistory, "targetDatastoreUUID","Ljava/lang/String;" );
	jfieldID field_targetDatastoreVersion	= env->GetFieldID( class_JJobHistory, "targetDatastoreVersion",	"J" );	
	jfieldID field_jobName				= env->GetFieldID( class_JJobHistory, "jobName",			"Ljava/lang/String;" );
	jfieldID field_targetRPSUUID		= env->GetFieldID( class_JJobHistory, "targetUUID",			"Ljava/lang/String;" );
	jfieldID field_planUUID				= env->GetFieldID( class_JJobHistory, "planUUID",			"Ljava/lang/String;" );

	if( field_jobId )
		pHistory->ullJobId = env->GetLongField( jJobHistory, field_jobId );
	if( field_jobType )
		pHistory->dwJobType = env->GetIntField( jJobHistory, field_jobType );
	if( field_jobStatus )
		pHistory->dwStatus = env->GetIntField( jJobHistory, field_jobStatus );
	if( field_jobMethod )
		pHistory->dwJobMethod = env->GetIntField( jJobHistory, field_jobMethod );
    if( field_periodRetentionFlag )
        pHistory->dwAdvSchFlag = env->GetIntField( jJobHistory, field_periodRetentionFlag );
	
	wchar_t* ptr = NULL;
	if( field_jobUTCStartTime )
	{
		ptr = GetStringFromField( env, &jJobHistory, field_jobUTCStartTime );
	}
	if( ptr )
		pHistory->ullUTCStartTime = _wtoi64( ptr );		
	pHistory->ullLocalStartTime = utctime_2_local( pHistory->ullUTCStartTime );

	
	SAFE_FREE(ptr);
	if( field_jobRunningNode )
		ptr = GetStringFromField( env, &jJobHistory, field_jobRunningNode );
	if( ptr )
		wcsncpy_s( pHistory->wszRunningNode, _ARRAYSIZE(pHistory->wszRunningNode), ptr, _TRUNCATE );

	SAFE_FREE(ptr);
	if( field_jobRunningNodeUUID )
		ptr = GetStringFromField( env, &jJobHistory, field_jobRunningNodeUUID );
	if( ptr )
		wcsncpy_s( pHistory->wszRunningNodeUUID, _ARRAYSIZE(pHistory->wszRunningNodeUUID), ptr, _TRUNCATE );

	SAFE_FREE(ptr);
	if( field_jobDisposeNode )
		ptr = GetStringFromField( env, &jJobHistory, field_jobDisposeNode );
	if( ptr )
		wcsncpy_s( pHistory->wszDisposeNode, _ARRAYSIZE(pHistory->wszDisposeNode), ptr, _TRUNCATE );

	SAFE_FREE(ptr);
	if( field_jobDisposeNodeUUID )
		ptr = GetStringFromField( env, &jJobHistory, field_jobDisposeNodeUUID );
	if( ptr )
		wcsncpy_s( pHistory->wszDisposeNodeUUID, _ARRAYSIZE(pHistory->wszDisposeNodeUUID), ptr, _TRUNCATE );

	SAFE_FREE(ptr);
	if( field_datastoreUUID )
		ptr = GetStringFromField( env, &jJobHistory, field_datastoreUUID );
	if( ptr )
		wcsncpy_s( pHistory->wszDataStoreUUID, _ARRAYSIZE(pHistory->wszDataStoreUUID), ptr, _TRUNCATE );

	if( field_datastoreVersion )
		pHistory->ullDSVersion = env->GetLongField( jJobHistory, field_datastoreVersion );

	SAFE_FREE(ptr);
	if( field_targetDatastoreUUID )
		ptr = GetStringFromField( env, &jJobHistory, field_targetDatastoreUUID );
	if( ptr )
		wcsncpy_s( pHistory->wszTargetDataStoreUUID, _ARRAYSIZE(pHistory->wszTargetDataStoreUUID), ptr, _TRUNCATE );

	if( field_targetDatastoreVersion )
		pHistory->ullDstDSVersion = env->GetLongField( jJobHistory, field_targetDatastoreVersion );

	SAFE_FREE(ptr)
	if( field_jobName )
		ptr = GetStringFromField( env, &jJobHistory, field_jobName );
	if( ptr )
		wcsncpy_s( pHistory->wszJobName, _ARRAYSIZE(pHistory->wszJobName), ptr, _TRUNCATE );

	SAFE_FREE(ptr)
	if( field_targetRPSUUID )
		ptr = GetStringFromField( env, &jJobHistory, field_targetRPSUUID );
	if( ptr )
		wcsncpy_s( pHistory->wszTargetUUID, _ARRAYSIZE(pHistory->wszTargetUUID), ptr, _TRUNCATE );

	SAFE_FREE(ptr)
	if( field_planUUID )
		ptr = GetStringFromField( env, &jJobHistory, field_planUUID );
	if( ptr )
		wcsncpy_s( pHistory->wszPlanUUID, _ARRAYSIZE(pHistory->wszPlanUUID), ptr, _TRUNCATE );

	SAFE_FREE(ptr);
}

void JActLogDetails2ACTLOG_DETAILS(JNIEnv *env, ACTLOG_DETAILS &pLD, jobject &jLogDetails)
{
	jclass class_JLogDetails = env->GetObjectClass(jLogDetails);
	
	jfieldID field_productType = env->GetFieldID(class_JLogDetails, "productType", "I");
	pLD.dwProductType = env->GetIntField(jLogDetails, field_productType);
	
	jfieldID field_jobID = env->GetFieldID(class_JLogDetails, "jobID", "I");
	pLD.dwJobID = env->GetLongField(jLogDetails, field_jobID);

	jfieldID field_jobType = env->GetFieldID(class_JLogDetails, "jobType", "I");
	pLD.dwJobType = env->GetIntField(jLogDetails, field_jobType);

	jfieldID field_jobMethod = env->GetFieldID(class_JLogDetails, "jobMethod", "I");
	pLD.dwJobMethod = env->GetIntField(jLogDetails, field_jobMethod);

	jfieldID field_logLevel = env->GetFieldID(class_JLogDetails, "logLevel", "I");
	pLD.dwLogLevel = env->GetIntField(jLogDetails, field_logLevel);

	jfieldID field_isVMInstance = env->GetFieldID(class_JLogDetails, "isVMInstance", "Z");
	pLD.bIsVMInstance = env->GetBooleanField(jLogDetails, field_isVMInstance);

	jfieldID field_svrNodeName = env->GetFieldID(class_JLogDetails, "svrNodeName", "Ljava/lang/String;");
	wchar_t* pwszSvrNodeName = GetStringFromField(env,&jLogDetails,field_svrNodeName);
	if (pwszSvrNodeName != NULL)
	{
		wcscpy_s(pLD.wszSvrNodeName, pwszSvrNodeName);
		free(pwszSvrNodeName);
	}

	jfieldID field_svrNodeID = env->GetFieldID(class_JLogDetails, "svrNodeID", "Ljava/lang/String;");
	wchar_t* pwszSvrNodeID = GetStringFromField(env,&jLogDetails,field_svrNodeID);
	if (pwszSvrNodeID != NULL)
	{
		wcscpy_s(pLD.wszSvrNodeID, pwszSvrNodeID);
		free(pwszSvrNodeID);
	}

	jfieldID field_agentNodeName = env->GetFieldID(class_JLogDetails, "agentNodeName", "Ljava/lang/String;");
	wchar_t* pwszAgentNodeName = GetStringFromField(env,&jLogDetails,field_agentNodeName);
	if (pwszAgentNodeName != NULL)
	{
		wcscpy_s(pLD.wszAgentNodeName, pwszAgentNodeName);
		free(pwszAgentNodeName);
	}

	jfieldID field_agentNodeID = env->GetFieldID(class_JLogDetails, "agentNodeID", "Ljava/lang/String;");
	wchar_t* pwszAgentNodeID = GetStringFromField(env,&jLogDetails,field_agentNodeID);
	if (pwszAgentNodeID != NULL)
	{
		wcscpy_s(pLD.wszAgentNodeID, pwszAgentNodeID);
		free(pwszAgentNodeID);
	}

	jfieldID field_sourceRPSID = env->GetFieldID(class_JLogDetails, "sourceRPSID", "Ljava/lang/String;");
	wchar_t* pwszSourceRPSID = GetStringFromField(env,&jLogDetails,field_sourceRPSID);
	if (pwszSourceRPSID != NULL)
	{
		wcscpy_s(pLD.wszSourceRPSID, pwszSourceRPSID);
		free(pwszSourceRPSID);
	}

	jfieldID field_targetRPSID = env->GetFieldID(class_JLogDetails, "targetRPSID", "Ljava/lang/String;");
	wchar_t* pwszTargetRPSID = GetStringFromField(env,&jLogDetails,field_targetRPSID);
	if (pwszTargetRPSID != NULL)
	{
		wcscpy_s(pLD.wszTargetRPSID, pwszTargetRPSID);
		free(pwszTargetRPSID);
	}

	jfieldID field_dsUUID = env->GetFieldID(class_JLogDetails, "dsUUID", "Ljava/lang/String;");
	wchar_t* pwszDSUUID = GetStringFromField(env,&jLogDetails,field_dsUUID);
	if (pwszDSUUID != NULL)
	{
		wcscpy_s(pLD.wszDSUUID, pwszDSUUID);
		free(pwszDSUUID);
	}

	jfieldID field_targetDSUUID = env->GetFieldID(class_JLogDetails, "targetDSUUID", "Ljava/lang/String;");
	wchar_t* pwszTargetDSUUID = GetStringFromField(env,&jLogDetails,field_targetDSUUID);
	if (pwszTargetDSUUID != NULL)
	{
		wcscpy_s(pLD.wszTargetDSUUID, pwszTargetDSUUID);
		free(pwszTargetDSUUID);
	}
}

void Convert2JRPSCatalogScriptInfo(JNIEnv *env, wstring &wsJobQPath, WSVector &vecJobScriptList, jobject &jobj)
{
	jclass class_catalogScriptInfo = env->GetObjectClass(jobj);
	jclass class_ArrayList = env->FindClass("java/util/List");
	
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

	jmethodID setCatalogScriptPath = env->GetMethodID(class_catalogScriptInfo, "setCatalogScriptPath", "(Ljava/lang/String;)V");
	jmethodID getCatalogJobScript = env->GetMethodID(class_catalogScriptInfo, "getCatalogJobScript", "()Ljava/util/List;");

	env->CallVoidMethod(jobj, setCatalogScriptPath, WCHARToJString(env, wsJobQPath));

	jobject list = env->CallObjectMethod(jobj, getCatalogJobScript);
	WSVector::iterator iter;
	
	for(iter = vecJobScriptList.begin(); iter != vecJobScriptList.end(); iter++) 
	{
		env->CallBooleanMethod(list, id_ArrayList_add, WCHARToJString(env, (*iter).c_str()));
	}
}

void VectorNodeSize2JArrayList(JNIEnv *env, jobject& jArrLst, VBACKUP_NODES_SIZE& vNodeSizes)
{
	if (env == NULL || jArrLst == NULL || vNodeSizes.size() < 1)
		return;

	jclass class_ArrayList = env->GetObjectClass(jArrLst);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

	jclass class_JDataSizes = env->FindClass("com/ca/arcflash/service/jni/model/JDataSizesFromStorage");
	jmethodID JDataSizes_init = env->GetMethodID(class_JDataSizes, "<init>", "()V");

	for (VBACKUP_NODES_SIZE::const_iterator itr = vNodeSizes.begin(); itr != vNodeSizes.end(); itr++)
	{
		jobject jItem = env->NewObject(class_JDataSizes, JDataSizes_init);

		jfieldID field_nodeUUID = env->GetFieldID(class_JDataSizes, "nodeUUID", "Ljava/lang/String;");
		jstring nodeUUID = WCHARToJString(env, (wchar_t*)itr->strNodeUUID.c_str());
		env->SetObjectField(jItem, field_nodeUUID, nodeUUID);
		if (nodeUUID != NULL) env->DeleteLocalRef(nodeUUID);

		jfieldID field_nodeName = env->GetFieldID(class_JDataSizes, "nodeName", "Ljava/lang/String;");
		jstring nodeName = WCHARToJString(env, (wchar_t*)itr->strNodeName.c_str());
		env->SetObjectField(jItem, field_nodeName, nodeName);
		if (nodeName != NULL) env->DeleteLocalRef(nodeName);

		jfieldID field_vmName = env->GetFieldID(class_JDataSizes, "vmName", "Ljava/lang/String;");
		jstring vmName = WCHARToJString(env, (wchar_t*)itr->strvmName.c_str());
		env->SetObjectField(jItem, field_vmName, vmName);
		if (vmName != NULL) env->DeleteLocalRef(vmName);

		jfieldID field_destination = env->GetFieldID(class_JDataSizes, "destination", "Ljava/lang/String;");
		jstring destination = WCHARToJString(env, (wchar_t*)itr->strDest.c_str());
		env->SetObjectField(jItem, field_destination, destination);
		if (destination != NULL) env->DeleteLocalRef(destination);

		jmethodID setDiskSize = env->GetMethodID(class_JDataSizes, "setDataStorageSize", "(J)V");
		env->CallVoidMethod(jItem, setDiskSize, (jlong)itr->ullDiskOccupied);

		jmethodID setRawSize = env->GetMethodID(class_JDataSizes, "setRawDataSize", "(J)V");
		env->CallVoidMethod(jItem, setRawSize, (jlong)itr->ullRawDataSize);

		jmethodID setRestoreSize = env->GetMethodID(class_JDataSizes, "setRestorableDataSize", "(J)V");
		env->CallVoidMethod(jItem, setRestoreSize, (jlong)itr->ullRestorableSize);

		env->CallBooleanMethod(jArrLst, id_ArrayList_add, jItem);
		if (jItem != NULL) env->DeleteLocalRef(jItem);
	}

	if (class_JDataSizes != NULL) env->DeleteLocalRef(class_JDataSizes);
	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);
}

void NodeOSInfo2JAgentOSInfo(JNIEnv* env, jobject objosver, const NODE_OS_INFO& nodeInfo)
{
	jclass objclass = env->GetObjectClass(objosver);
	jfieldID fld_dwAgentOSType = env->GetFieldID(objclass, "dwAgentOSType", "J");
	jfieldID fld_dwAgentBackupType = env->GetFieldID(objclass, "dwAgentBackupType", "J");
	jfieldID fld_dwVMGuestOsType = env->GetFieldID(objclass, "dwVMGuestOsType", "J");
	jfieldID fld_dwVMHypervisor = env->GetFieldID(objclass, "dwVMHypervisor", "J");

	if (fld_dwAgentOSType)
		env->SetLongField(objosver, fld_dwAgentOSType, nodeInfo.dwAgentOSType);
	if (fld_dwAgentOSType)
		env->SetLongField(objosver, fld_dwAgentBackupType, nodeInfo.dwAgentBackupType);
	if (fld_dwAgentOSType)
		env->SetLongField(objosver, fld_dwVMGuestOsType, nodeInfo.dwVMGuestOsType);
	if (fld_dwAgentOSType)
		env->SetLongField(objosver, fld_dwVMHypervisor, nodeInfo.dwVMHypervisor);

	if (objclass != NULL) env->DeleteLocalRef(objclass);
}
//////////////////////////////////////////////////////////////////////////////
