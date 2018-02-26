#include "stdafx.h"
#include "CommonJNIConv.h"
#include <atlstr.h>
#include <iostream>
#include "CommonUtils.h"
#include "RPSRepJobScript.h"
#include "JobMonitor.h"
#include "RPSCoreInterface.h"

using namespace std;

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

	wcscpy_s(tmp, len / sizeof(wchar_t), (wchar_t*)jchs);

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

jlong JRWLongTojlong(JNIEnv* env, jobject* pRWLong)
{
	jclass class_RWLong = env->FindClass("com/ca/arcflash/webservice/jni/model/JRWLong");
	jfieldID field_value = env->GetFieldID(class_RWLong, "value", "J");
	jlong value = env->GetLongField(*pRWLong, field_value);
	if (class_RWLong != NULL) env->DeleteLocalRef(class_RWLong);
	return value;
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

wchar_t* GetStringFromField(JNIEnv *env, jobject* jObj, jfieldID field)
{
	if (jObj == NULL) return NULL;

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

void AddItemToWstringVector( JNIEnv * pEnv, jobject item, LPARAM lParam )
{
	vector<wstring> * pStrVector = (vector<wstring> *)lParam;
	pStrVector->push_back( JStringToWString( pEnv, (jstring)item ) );
}

jstring ULONGLONG2TimeJString(JNIEnv *env, ULONGLONG ulltime)
{
	WCHAR szTime[MAX_PATH] = {0};
	ZeroMemory( szTime, sizeof(szTime) );
	ulonglong_2_timestr(ulltime, szTime, _ARRAYSIZE(szTime));
	return WCHARToJString(env, szTime);
}

void ProcessEcryCmpAttribute(JNIEnv *env, RPSDsAttr* pAttribute, jobject jAttr)
{
	jclass class_EncCmpAttribute = env->GetObjectClass(jAttr);

	jfieldID field_EnableEncrypt = env->GetFieldID(class_EncCmpAttribute, "enableEncrypt", "J");
	jfieldID field_EncyptAlg = env->GetFieldID(class_EncCmpAttribute, "encyptAlg", "J");
	jfieldID field_DataPassword = env->GetFieldID(class_EncCmpAttribute, "dataPassword", "Ljava/lang/String;");
	jfieldID field_EnableCmprs = env->GetFieldID(class_EncCmpAttribute, "enableCmprs", "J");
	jfieldID field_CmprsAlg = env->GetFieldID(class_EncCmpAttribute, "cmprsAlg", "J");
	jfieldID field_EnableGDD = env->GetFieldID(class_EncCmpAttribute, "enableGDD", "J");
	jfieldID field_DSName = env->GetFieldID(class_EncCmpAttribute, "dsName", "Ljava/lang/String;");
	jfieldID field_DSDisplayName = env->GetFieldID(class_EncCmpAttribute, "dsDisplayName", "Ljava/lang/String;");

	pAttribute->EnableEncrypt = (ULONG)env->GetLongField(jAttr, field_EnableEncrypt);
	pAttribute->EncyptAlg = (ULONG)env->GetLongField(jAttr, field_EncyptAlg);
	pAttribute->DataPassword = GetStringFromField(env, &jAttr, field_DataPassword);
	pAttribute->EnableCmprs =  (ULONG)env->GetLongField(jAttr, field_EnableCmprs);
	pAttribute->CmprsAlg =  (ULONG)env->GetLongField(jAttr, field_CmprsAlg);
	pAttribute->Flags = 0;
	pAttribute->DSName = GetStringFromField(env, &jAttr, field_DSName);
	pAttribute->DisplayName = GetStringFromField(env, &jAttr, field_DSDisplayName);

	BOOL bEnableGDD = (ULONG)env->GetLongField(jAttr, field_EnableGDD);
	if(bEnableGDD)
	{
		pAttribute->Flags |= REP_DS_ATTR_FLAGS_GDD_ENABLED;
	}

	if(class_EncCmpAttribute != NULL)
	{
		env->DeleteLocalRef(class_EncCmpAttribute);
	}
}

int ProcessProxyList(JNIEnv *env, RPSRepProxy** pList, jobject jList)
{
	int nRetCount = 0;

	if(jList == NULL)
	{
		*pList = NULL;

		return nRetCount;
	}

	jclass class_List = env->GetObjectClass(jList);
	jmethodID method_Size = env->GetMethodID(class_List, "size", "()I");

	jint nListSize = env->CallIntMethod(jList, method_Size);
	if(nListSize > 0)
	{
		*pList = (RPSRepProxy*)malloc(sizeof(RPSRepProxy) * nListSize);
		if(*pList)
		{
			memset(*pList, 0, sizeof(RPSRepProxy) * nListSize);

			jclass class_RPSRepProxy = env->FindClass("com/ca/arcflash/rps/jni/model/RPSRepProxy");
			jmethodID method_Get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");

			jfieldID field_serverName = env->GetFieldID(class_RPSRepProxy, "serverName", "Ljava/lang/String;");
			jfieldID field_serverPort = env->GetFieldID(class_RPSRepProxy, "serverPort", "J");
			jfieldID field_username = env->GetFieldID(class_RPSRepProxy, "username", "Ljava/lang/String;");
			jfieldID field_password = env->GetFieldID(class_RPSRepProxy, "password", "Ljava/lang/String;");
			jfieldID field_useSSL = env->GetFieldID(class_RPSRepProxy, "useSSL", "Z");
			jfieldID field_shareSocket = env->GetFieldID(class_RPSRepProxy, "shareSocket", "Z");

			for(jint i = 0; i < nListSize; i++)
			{
				jobject objRPSRepProxy = env->CallObjectMethod(jList, method_Get, i);

				(*pList)[i].Index = i;
				(*pList)[i].IsSSL = env->GetBooleanField(objRPSRepProxy, field_useSSL);
				(*pList)[i].Password = GetStringFromField(env, &objRPSRepProxy, field_password);
				(*pList)[i].Port = env->GetLongField(objRPSRepProxy, field_serverPort);
				(*pList)[i].SrvName = GetStringFromField(env, &objRPSRepProxy, field_serverName);
				(*pList)[i].UserName = GetStringFromField(env, &objRPSRepProxy, field_username);
				(*pList)[i].IsSharedSocket = env->GetBooleanField(objRPSRepProxy, field_shareSocket);
			}

			env->DeleteLocalRef(class_RPSRepProxy);

			nRetCount = nListSize;
		}
	}
	else
	{
		*pList = NULL;
	}

	env->DeleteLocalRef(class_List);

	return nRetCount;
}

int ProcessMigrationRPInfoList(JNIEnv *env, RPSMigrationRPInfo **migrationRPInfo, jobject jMigrationRPInfo)
{
	int nRetCount = 0;

	if(jMigrationRPInfo == NULL)
	{
		*migrationRPInfo = NULL;
		return nRetCount;
	}

	jclass class_List = env->GetObjectClass(jMigrationRPInfo);
	jmethodID method_Size = env->GetMethodID(class_List, "size", "()I");

	jint nListSize = env->CallIntMethod(jMigrationRPInfo, method_Size);
	if(nListSize > 0)
	{
		*migrationRPInfo = (RPSMigrationRPInfo*)malloc(sizeof(RPSMigrationRPInfo) * nListSize);

		if(*migrationRPInfo)
		{
			memset(*migrationRPInfo, 0, sizeof(RPSMigrationRPInfo) * nListSize);
			jclass class_JDataMigrationRPInfo = env->FindClass("com/ca/arcflash/rps/jni/model/JDataMigrationRPInfo");
			jmethodID method_Get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");

			jfieldID field_sessionGuid = env->GetFieldID(class_JDataMigrationRPInfo, "sessionGuid", "Ljava/lang/String;");
			jfieldID field_sessionPwd = env->GetFieldID(class_JDataMigrationRPInfo, "sessionPwd", "Ljava/lang/String;");
			jfieldID field_targetSessionPwd = env->GetFieldID(class_JDataMigrationRPInfo, "targetSessionPwd", "Ljava/lang/String;");

			for(jint i = 0; i < nListSize; i++)
			{
				jobject objMigrationRPInfo = env->CallObjectMethod(jMigrationRPInfo, method_Get, i);
				
				(*migrationRPInfo)[i].sessionGuid = GetStringFromField(env, &objMigrationRPInfo, field_sessionGuid);
				(*migrationRPInfo)[i].sessionPwd = GetStringFromField(env, &objMigrationRPInfo, field_sessionPwd);
				(*migrationRPInfo)[i].targetSessionPwd = GetStringFromField(env, &objMigrationRPInfo, field_targetSessionPwd);
			}

			env->DeleteLocalRef(class_JDataMigrationRPInfo);

			nRetCount = nListSize;
		}
	}
	else
	{
		*migrationRPInfo = NULL;
	}

	env->DeleteLocalRef(class_List);

	return nRetCount;
}

void JReplicationJob2AFReplicationJob(JNIEnv *env, RPSRepJobScript* pJob, jobject* jJob)
{
	jclass class_JReplicationJobScript = env->GetObjectClass(*jJob);

	jfieldID field_Version = env->GetFieldID(class_JReplicationJobScript, "Version", "J");
	jfieldID field_JobID = env->GetFieldID(class_JReplicationJobScript, "JobID", "J");
	jfieldID field_JobType = env->GetFieldID(class_JReplicationJobScript, "JobType", "J");

	jfieldID field_JobTypeString = env->GetFieldID(class_JReplicationJobScript, "JobTypeString", "Ljava/lang/String;");
	jfieldID field_JobName = env->GetFieldID(class_JReplicationJobScript, "JobName", "Ljava/lang/String;");

	jfieldID field_D2DNodeName = env->GetFieldID(class_JReplicationJobScript, "D2DNodeName", "Ljava/lang/String;");
	jfieldID field_RPSNodeName = env->GetFieldID(class_JReplicationJobScript, "RPSNodeName", "Ljava/lang/String;");
	jfieldID field_D2DNodeGUID = env->GetFieldID(class_JReplicationJobScript, "D2DNodeGUID", "Ljava/lang/String;");
	jfieldID field_PolicyGUID = env->GetFieldID(class_JReplicationJobScript, "PolicyGUID", "Ljava/lang/String;");
	jfieldID field_PolicyName = env->GetFieldID(class_JReplicationJobScript, "PolicyName", "Ljava/lang/String;");

	jfieldID field_DestinationRootpath = env->GetFieldID(class_JReplicationJobScript, "DestinationRootPath", "Ljava/lang/String;");
	jfieldID field_DestinationUserName = env->GetFieldID(class_JReplicationJobScript, "DestinationUserName", "Ljava/lang/String;");
	jfieldID field_DestinationPassword = env->GetFieldID(class_JReplicationJobScript, "DestinationPassword", "Ljava/lang/String;");

	jfieldID field_BeginSession = env->GetFieldID(class_JReplicationJobScript, "BeginSession", "J");
	jfieldID field_EndSession = env->GetFieldID(class_JReplicationJobScript, "EndSession", "J");

	jfieldID field_JobEndFlag = env->GetFieldID(class_JReplicationJobScript, "JobEndFlag", "J");
	jfieldID field_MaxMackupRetryTimes = env->GetFieldID(class_JReplicationJobScript, "MaxMackupRetryTimes", "J");

	jfieldID field_RemotePolicyGUID = env->GetFieldID(class_JReplicationJobScript, "RemotePolicyGUID", "Ljava/lang/String;");
	jfieldID field_RemotePolicyName = env->GetFieldID(class_JReplicationJobScript, "RemotePolicyName", "Ljava/lang/String;");
	jfieldID field_RemoteTransportMode = env->GetFieldID(class_JReplicationJobScript, "RemoteTransportMode", "Ljava/lang/String;");
	jfieldID field_RemoteServer = env->GetFieldID(class_JReplicationJobScript, "RemoteServer", "Ljava/lang/String;");
	jfieldID field_RemotePort = env->GetFieldID(class_JReplicationJobScript, "RemotePort", "J");

	jfieldID field_StreamNumber = env->GetFieldID(class_JReplicationJobScript, "StreamNumber", "J");
	jfieldID field_BandWidthThrotting = env->GetFieldID(class_JReplicationJobScript, "BandWidthThrotting", "J");

	jfieldID field_RemoteDestinationRootPath = env->GetFieldID(class_JReplicationJobScript, "RemoteDestinationRootPath", "Ljava/lang/String;");
	jfieldID field_RemoteDestinationUserName = env->GetFieldID(class_JReplicationJobScript, "RemoteDestinationUserName", "Ljava/lang/String;");
	jfieldID field_RemoteDestinationPassword = env->GetFieldID(class_JReplicationJobScript, "RemoteDestinationPassword", "Ljava/lang/String;");

	jfieldID field_SourceEncCmpAttribute = env->GetFieldID(class_JReplicationJobScript, "sourceEncCmpAttribute", "Lcom/ca/arcflash/rps/jni/model/RPSRepEncCmpAttribute;");
	jfieldID field_DestEncCmpAttribute = env->GetFieldID(class_JReplicationJobScript, "destEncCmpAttribute", "Lcom/ca/arcflash/rps/jni/model/RPSRepEncCmpAttribute;");

	jfieldID field_ProxyList = env->GetFieldID(class_JReplicationJobScript, "proxyList", "Ljava/util/List;");
	jfieldID field_MigrationRP = env->GetFieldID(class_JReplicationJobScript, "dataMigrationRPInfo", "Ljava/util/List;");

	jfieldID field_RPSNodeID = env->GetFieldID(class_JReplicationJobScript, "rpsNodeID", "Ljava/lang/String;");
	jfieldID field_RemoteRPSID = env->GetFieldID(class_JReplicationJobScript, "remoteRPSID", "Ljava/lang/String;");

	jfieldID field_srcPlanUUID = env->GetFieldID(class_JReplicationJobScript, "srcPlanUUID", "Ljava/lang/String;");
	jfieldID field_targetPlanUUID = env->GetFieldID(class_JReplicationJobScript, "targetPlanUUID", "Ljava/lang/String;");

	jfieldID field_isMspUser = env->GetFieldID(class_JReplicationJobScript, "isMspUser", "Z");
	jfieldID field_userName = env->GetFieldID(class_JReplicationJobScript, "userName", "Ljava/lang/String;");
	jfieldID field_password = env->GetFieldID(class_JReplicationJobScript, "password", "Ljava/lang/String;");
	jfieldID field_domain = env->GetFieldID(class_JReplicationJobScript, "domain", "Ljava/lang/String;");
	jfieldID field_jsonExtendsInfo = env->GetFieldID(class_JReplicationJobScript, "jsonExtendsInfo", "Ljava/lang/String;");

	jfieldID field_targetJobType = env->GetFieldID(class_JReplicationJobScript, "targetJobType", "J");
	jfieldID field_targetPlanName = env->GetFieldID(class_JReplicationJobScript, "targetPlanName", "Ljava/lang/String;");
	jfieldID field_isMigrateFromShare = env->GetFieldID(class_JReplicationJobScript, "isMigrateFromShare", "Z");

	
	pJob->Version = (ULONG)env->GetLongField(*jJob, field_Version);
	pJob->JobID = (ULONG)env->GetLongField(*jJob, field_JobID);
	pJob->JobType = (ULONG)env->GetLongField(*jJob, field_JobType);

	pJob->JobTypeString = GetStringFromField(env, jJob, field_JobTypeString);
	pJob->JobName = GetStringFromField(env, jJob, field_JobName);

	pJob->D2DNodeName = GetStringFromField(env, jJob, field_D2DNodeName);
	pJob->pRPSName = GetStringFromField(env, jJob, field_RPSNodeName);
	pJob->D2DNodeGUID= GetStringFromField(env, jJob, field_D2DNodeGUID);
	pJob->PolicyGUID = GetStringFromField(env, jJob, field_PolicyGUID);
	pJob->PolicyName = GetStringFromField(env, jJob, field_PolicyName);

	pJob->DestinationRootPath = GetStringFromField(env, jJob, field_DestinationRootpath);
	pJob->DestinationUserName = GetStringFromField(env, jJob, field_DestinationUserName);
	pJob->DestinationPassword = GetStringFromField(env, jJob, field_DestinationPassword);

	pJob->BeginSession = (ULONG)env->GetLongField(*jJob, field_BeginSession);
	pJob->EndSession = (ULONG)env->GetLongField(*jJob, field_EndSession);

	pJob->JobEndFlag = (ULONG)env->GetLongField(*jJob, field_JobEndFlag);
	pJob->MaxMackupRetryTimes = (ULONG)env->GetLongField(*jJob, field_MaxMackupRetryTimes);

	pJob->RemotePolicyGUID = GetStringFromField(env, jJob, field_RemotePolicyGUID);
    pJob->RemotePolicyName = GetStringFromField(env, jJob, field_RemotePolicyName);
	pJob->RemoteTransportMode = GetStringFromField(env, jJob, field_RemoteTransportMode);
	pJob->RemoteServer = GetStringFromField(env, jJob, field_RemoteServer);
	pJob->RemotePort = (ULONG)env->GetLongField(*jJob, field_RemotePort);

	pJob->StreamNumber = (ULONG)env->GetLongField(*jJob, field_StreamNumber);
	pJob->BandWidthThrotting = (ULONG)env->GetLongField(*jJob, field_BandWidthThrotting);

	pJob->RemoteDestinationRootPath = GetStringFromField(env, jJob, field_RemoteDestinationRootPath);
	pJob->RemoteDestinationUserName = GetStringFromField(env, jJob, field_RemoteDestinationUserName);
	pJob->RemoteDestinationPassword = GetStringFromField(env, jJob, field_RemoteDestinationPassword);

	ProcessEcryCmpAttribute(env, &pJob->SrcDs, env->GetObjectField(*jJob, field_SourceEncCmpAttribute));
	ProcessEcryCmpAttribute(env, &pJob->DesDs, env->GetObjectField(*jJob, field_DestEncCmpAttribute));

	pJob->ProxyCnt = ProcessProxyList(env, &(pJob->ProxyList), env->GetObjectField(*jJob, field_ProxyList));


	pJob->IsMigration = env->GetBooleanField(*jJob, field_isMigrateFromShare);
	pJob->MigrationRPCount = ProcessMigrationRPInfoList(env, &(pJob->MigrationRPInfo), env->GetObjectField(*jJob, field_MigrationRP));


	//This two fields need set to pJob
	pJob->RPSNodeID = GetStringFromField(env, jJob, field_RPSNodeID);
	pJob->RemoteRPSNodeSID = GetStringFromField(env, jJob, field_RemoteRPSID);

	//need set to job script
	pJob->PlanUUID = GetStringFromField(env, jJob, field_srcPlanUUID);
	pJob->TargetPlanUUID = GetStringFromField(env, jJob, field_targetPlanUUID);

	//<huvfe01>2013-11-29 support MSP authentication
	pJob->IsMspUser = (ULONG)env->GetBooleanField(*jJob, field_isMspUser);
	pJob->UserName = GetStringFromField(env, jJob, field_userName);
	pJob->Password = GetStringFromField(env, jJob, field_password);
	pJob->Domain = GetStringFromField(env, jJob, field_domain);	
	pJob->TargetPlanName = GetStringFromField(env, jJob, field_targetPlanName);	
	//shuzh02 2014-02-21 need set to job script
	pJob->TargetJobType = env->GetLongField(*jJob, field_targetJobType);
	//This field used to send information to target side.
	pJob->ExtendsInfo = GetStringFromField(env, jJob, field_jsonExtendsInfo);

	if (class_JReplicationJobScript != NULL) 
	{
		env->DeleteLocalRef(class_JReplicationJobScript);
	}
}

void FreeAFReplicationJobScript(RPSRepJobScript* pJob)
{
	if(pJob->JobTypeString != NULL)
	{
		free(pJob->JobTypeString);
	}

	if(pJob->JobName != NULL)
	{
		free(pJob->JobName);
	}

	if(pJob->D2DNodeName != NULL)
	{
		free(pJob->D2DNodeName);
	}

	if(pJob->D2DNodeGUID != NULL)
	{
		free(pJob->D2DNodeGUID);
	}

	if(pJob->PolicyGUID != NULL)
	{
		free(pJob->PolicyGUID);
	}

	if(pJob->DestinationRootPath != NULL)
	{
		free(pJob->DestinationRootPath);
	}

	if(pJob->DestinationUserName != NULL)
	{
		free(pJob->DestinationUserName);
	}

	if(pJob->DestinationPassword != NULL)
	{
		free(pJob->DestinationPassword);
	}

	if(pJob->RemotePolicyGUID != NULL)
	{
		free(pJob->RemotePolicyGUID);
	}

	if(pJob->RemoteTransportMode != NULL)
	{
		free(pJob->RemoteTransportMode);
	}

	if(pJob->RemoteServer != NULL)
	{
		free(pJob->RemoteServer);
	}

	if(pJob->RemoteDestinationRootPath != NULL)
	{
		free(pJob->RemoteDestinationRootPath);
	}

	if(pJob->RemoteDestinationUserName != NULL)
	{
		free(pJob->RemoteDestinationUserName);
	}

	if(pJob->RemoteDestinationPassword != NULL)
	{
		free(pJob->RemoteDestinationPassword);
	}

	if(pJob->SrcDs.DataPassword != NULL)
	{
		free(pJob->SrcDs.DataPassword);
	}

	if(pJob->DesDs.DataPassword != NULL)
	{
		free(pJob->DesDs.DataPassword);
	}

	if(pJob->SrcDs.DSName != NULL)
	{
		free(pJob->SrcDs.DSName);
	}

	if(pJob->DesDs.DSName != NULL)
	{
		free(pJob->DesDs.DSName);
	}

	if(pJob->ProxyList != NULL)
	{
		free(pJob->ProxyList);
	}

	if (pJob->TargetPlanName != NULL)
	{
		free(pJob->TargetPlanName);
	}

	if(pJob->ExtendsInfo != NULL)
	{
		free(pJob->ExtendsInfo);
	}
}

int JOB_MONITOR2JJobMonitor(JNIEnv *env, JOB_MONITOR& aJM, jobject& jobMonitor)
{
	jclass class_JJobMonitor = env->GetObjectClass(jobMonitor);
	jmethodID id_JJobMonitor_setUlSessionID = env->GetMethodID(class_JJobMonitor, "setUlSessionID", "(J)V");
    jmethodID id_JJobMonitor_setUlBeginSessID = env->GetMethodID(class_JJobMonitor, "setUlBeginSessID", "(J)V");
    jmethodID id_JJobMonitor_setUlEndSessID = env->GetMethodID(class_JJobMonitor, "setUlEndSessID", "(J)V");
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

	jmethodID id_JJobMonitor_setnProgramCPU = env->GetMethodID(class_JJobMonitor, "setnProgramCPU","(J)V");
	jmethodID id_JJobMonitor_setnSystemCPU = env->GetMethodID(class_JJobMonitor, "setnSystemCPU", "(J)V");
	jmethodID id_JJobMonitor_setnReadSpeed = env->GetMethodID(class_JJobMonitor, "setnReadSpeed", "(J)V");
	jmethodID id_JJobMonitor_setnWriteSpeed = env->GetMethodID(class_JJobMonitor, "setnWriteSpeed", "(J)V");
	jmethodID id_JJobMonitor_setnSystemReadSpeed = env->GetMethodID(class_JJobMonitor, "setnSystemReadSpeed", "(J)V");
	jmethodID id_JJobMonitor_setnSystemWriteSpeed = env->GetMethodID(class_JJobMonitor, "setnSystemWriteSpeed", "(J)V");
	jmethodID id_JJobMonitor_setUlThrottling = env->GetMethodID(class_JJobMonitor, "setUlThrottling", "(J)V");
	jmethodID id_JJobMonitor_setnLogicSpeed = env->GetMethodID(class_JJobMonitor, "setnLogicSpeed", "(J)V");
	jmethodID id_JJobMonitor_setUlEncInfoStatus = env->GetMethodID(class_JJobMonitor, "setUlEncInfoStatus", "(J)V");
	jmethodID id_JJobMonitor_setUlTotalSizeRead = env->GetMethodID(class_JJobMonitor, "setUlTotalSizeRead", "(J)V");
	jmethodID id_JJobMonitor_setUlTotalSizeWritten = env->GetMethodID(class_JJobMonitor, "setUlTotalSizeWritten", "(J)V");
	jmethodID id_JJobMonitor_setReplicationSavedBandWidth = env->GetMethodID(class_JJobMonitor, "setReplicationSavedBandWidth", "(J)V");
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

	jmethodID id_JJobMonitor_setServerPolicyUUID = env->GetMethodID(class_JJobMonitor, "setServerPolicyUUID", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setD2dNodeName = env->GetMethodID(class_JJobMonitor, "setD2dNodeName", "(Ljava/lang/String;)V");
	// For RPS merge job
	/*jmethodID id_JJobMonitor_setUlTotalDiskCnt = env->GetMethodID(class_JJobMonitor, "setUlTotalDiskCnt", "(J)V");
	jmethodID id_JJobMonitor_setUlCurrDiskIdx = env->GetMethodID(class_JJobMonitor, "setUlCurrDiskIdx", "(J)V");
	jmethodID id_JJobMonitor_setWszRPSName = env->GetMethodID(class_JJobMonitor, "setWszRPSName", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setWszNodeName = env->GetMethodID(class_JJobMonitor, "setWszNodeName", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setWszPolicyGuid = env->GetMethodID(class_JJobMonitor, "setWszPolicyGuid", "(Ljava/lang/String;)V");*/

	///For RPS replication job
	//jmethodID id_JJobMonitor_setUlReplicationType = env->GetMethodID(class_JJobMonitor, "setUlReplicationType", "(J)V");


	//For RPS replication job
	jmethodID id_JJobMonitor_setSrcRPS = env->GetMethodID(class_JJobMonitor, "setSrcRPS", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setDestRPS = env->GetMethodID(class_JJobMonitor, "setDestRPS", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setSrcDataStore = env->GetMethodID(class_JJobMonitor, "setSrcDataStore", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setDestDataStore = env->GetMethodID(class_JJobMonitor, "setDestDataStore", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setUlRemainingTime = env->GetMethodID(class_JJobMonitor, "setUlRemainingTime", "(J)V");

	//For RPS delete nodes job
	jmethodID id_JJobMonitor_setTotalNodesNeedPurge = env->GetMethodID(class_JJobMonitor, "setTotalNodesNeedPurge", "(J)V");
	jmethodID id_JJobMonitor_setPurgedNodes = env->GetMethodID(class_JJobMonitor, "setPurgedNodes", "(J)V");
	jmethodID id_JJobMonitor_setPurgingNodeName = env->GetMethodID(class_JJobMonitor, "setPurgingNodeName", "(Ljava/lang/String;)V");
	jmethodID id_JJobMonitor_setTotalSessionsOfPurgingNode = env->GetMethodID(class_JJobMonitor, "setTotalSessionsOfPurgingNode", "(J)V");
	jmethodID id_JJobMonitor_setPurgedSessionsOfPurgingNode = env->GetMethodID(class_JJobMonitor, "setPurgedSessionsOfPurgingNode", "(J)V");

	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlSessionID, (jlong)aJM.ulSessionID);
    env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlBeginSessID, (jlong)aJM.ulBeginSessID);
    env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlEndSessID, (jlong)aJM.ulEndSessID);
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
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setnLogicSpeed, (jlong)aJM.nLogicSpeed);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlEncInfoStatus, (jlong)aJM.ulEncInfoStatus);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlTotalSizeRead, (jlong)aJM.ulTotalSizeRead);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlTotalSizeWritten, (jlong)aJM.ulTotalSizeWritten);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setReplicationSavedBandWidth, (jlong)aJM.ulSavedBandwidthPercent);
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
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setServerPolicyUUID, WCHARToJString(env, aJM.wszPolicyGuid));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setD2dNodeName, WCHARToJString(env, aJM.wszNodeName));

	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setSrcRPS, WCHARToJString(env, aJM.wszRPSName));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setDestRPS, WCHARToJString(env, aJM.wszRemoteRPSName));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setSrcDataStore, WCHARToJString(env, aJM.wszSourceDataStoreName));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setDestDataStore, WCHARToJString(env, aJM.wszTargetDataStoreName));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlRemainingTime, aJM.ulRemainingTime);

	/*env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlTotalDiskCnt, (jlong)aJM.ulTotalDiskCnt);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlCurrDiskIdx, (jlong)aJM.ulCurrDiskIdx);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setWszRPSName, WCHARToJString(env, aJM.wszRPSName));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setWszNodeName, WCHARToJString(env, aJM.wszNodeName));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setWszPolicyGuid, WCHARToJString(env, aJM.wszPolicyGuid));

	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setUlReplicationType, (jlong)aJM.JobEndFlagForRpsRep);*/

	// For delete nodes job
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setTotalNodesNeedPurge, (jlong)aJM.purgejobfield.dwTotalNodes2Purge);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setPurgedNodes, (jlong)aJM.purgejobfield.dwCurrentNode2Purge);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setPurgingNodeName, WCHARToJString(env, aJM.purgejobfield.wszCurrentNodeName));
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setTotalSessionsOfPurgingNode, (jlong)aJM.purgejobfield.dwTotalSessOfCurNode);
	env->CallVoidMethod(jobMonitor, id_JJobMonitor_setPurgedSessionsOfPurgingNode, (jlong)aJM.purgejobfield.dwCurSess2Purge);

	return 0;
}

// End of GoThroughJavaList()
//////////////////////////////////////////////////////////////////////////////



typedef	BOOLEAN (WINAPI *PFNSRtlTimeToSecondsSince1970)( PLARGE_INTEGER,PULONG);
int AddVecFileInfo2List(JNIEnv *env, jobject *retArr,std::vector<FILE_INFO> &vList)
{
	jclass class_JFileInfo = env->FindClass("com/ca/arcflash/service/jni/model/JFileInfo");	

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

int ProcessDWORDList(JNIEnv *env, DWORD** pList, jobject jList)
{
	int nRetCount = 0;

	if(jList == NULL)
	{
		*pList = NULL;
		return nRetCount;
	}


	jclass class_List = env->GetObjectClass(jList);
	jmethodID method_Size = env->GetMethodID(class_List, "size", "()I");

	jint nListSize = env->CallIntMethod(jList, method_Size);
	if(nListSize > 0)
	{
		*pList = (DWORD*)JobFilter::AllocMemeber(sizeof(DWORD) * nListSize);
		if(*pList)
		{
			JobFilter::ClearMember(*pList, sizeof(DWORD) * nListSize);

			jclass class_Long = env->FindClass("java/lang/Long");
			jmethodID method_Get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");
			jmethodID method_GetLong = env->GetMethodID(class_Long, "longValue", "()J");


			for(jint i = 0; i < nListSize; i++)
			{
				jobject objLong = env->CallObjectMethod(jList, method_Get, i);
				
				(*pList)[i] = (DWORD)env->CallObjectMethod(objLong, method_GetLong);
			}

			env->DeleteLocalRef(class_Long);

			nRetCount = nListSize;
		}
	}

	env->DeleteLocalRef(class_List);

	return nRetCount;
}

int ProcessStringList(JNIEnv *env, wstring** pList, jobject jList)
{
	int nRetCount = 0;

	if(jList == NULL)
	{
		*pList = NULL;
		return nRetCount;
	}

	jclass class_List = env->GetObjectClass(jList);
	jmethodID method_Size = env->GetMethodID(class_List, "size", "()I");

	jint nListSize = env->CallIntMethod(jList, method_Size);
	if(nListSize > 0)
	{
		*pList = (wstring*)JobFilter::AllocMemeber(sizeof(wstring) * nListSize);
		if(*pList)
		{
			JobFilter::ClearMember(*pList, sizeof(wstring) * nListSize);

			jmethodID method_Get = env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");

			for(jint i = 0; i < nListSize; i++)
			{
				(*pList)[i] = JStringToWString(env, (jstring)env->CallObjectMethod(jList, method_Get, i));
			}

			nRetCount = nListSize;
		}
	}

	env->DeleteLocalRef(class_List);

	return nRetCount;
}

void JFilterToJobFilter(JNIEnv *env, JobFilter* pFilter, jobject* jFilter)
{
	memset(pFilter, 0, sizeof(JobFilter));
	
	jclass class_Filter = env->GetObjectClass(*jFilter);

	jfieldID field_JobType = env->GetFieldID(class_Filter, "jobType", "J");
	jfieldID field_JobID = env->GetFieldID(class_Filter, "jobID", "Ljava/util/ArrayList;");
	jfieldID field_PolicyID = env->GetFieldID(class_Filter, "policyID", "Ljava/util/ArrayList;");
	jfieldID field_RPSName = env->GetFieldID(class_Filter, "rpsName", "Ljava/util/ArrayList;");
	jfieldID field_D2DName = env->GetFieldID(class_Filter, "d2dName", "Ljava/util/ArrayList;");

	pFilter->dwJobType = (ULONG)env->GetLongField(*jFilter, field_JobType);

	pFilter->nJobIDCount = ProcessDWORDList(env, &(pFilter->pdwJobID), env->GetObjectField(*jFilter, field_JobID));
	pFilter->nPolicyIDCount = ProcessStringList(env, &(pFilter->pszPolicyID), env->GetObjectField(*jFilter, field_PolicyID));
	pFilter->nRPSNameCount = ProcessStringList(env, &(pFilter->pszRPSName), env->GetObjectField(*jFilter, field_RPSName));
	pFilter->nD2DNameCount = ProcessStringList(env, &(pFilter->pszD2DName), env->GetObjectField(*jFilter, field_D2DName));
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

void RPS_JOB_INFO2JRPSJobInfo(JNIEnv *env, RPS_JOB_INFO_S &rpsJobInfo, jobject &jRpsJobInfo)
{
	jclass class_JRPSJobInfo = env->GetObjectClass(jRpsJobInfo);
	jmethodID id_JRPSJobInfo_setJobId = env->GetMethodID(class_JRPSJobInfo, "setJobId", "(J)V");
	jmethodID id_JRPSJobInfo_setD2duuid = env->GetMethodID(class_JRPSJobInfo, "setD2duuid", "(Ljava/lang/String;)V");
	jmethodID id_JRPSJobInfo_setD2dNodeName = env->GetMethodID(class_JRPSJobInfo, "setD2dNodeName", "(Ljava/lang/String;)V");
	jmethodID id_JRPSJobInfo_setProcessId = env->GetMethodID(class_JRPSJobInfo, "setProcessId", "(J)V");
	jmethodID id_JRPSJobInfo_setSourceRpsUUID = env->GetMethodID(class_JRPSJobInfo, "setSourceRpsUUID", "(Ljava/lang/String;)V");
	jmethodID id_JRPSJobInfo_setTargetPlanUUID = env->GetMethodID(class_JRPSJobInfo, "setTargetPlanUUID", "(Ljava/lang/String;)V");
	jmethodID id_JRPSJobInfo_setSrcPlanUUID = env->GetMethodID(class_JRPSJobInfo, "setSrcPlanUUID", "(Ljava/lang/String;)V");
	jmethodID id_JRPSJobInfo_setMspUser = env->GetMethodID(class_JRPSJobInfo, "setMspUser", "(Z)V");
	jmethodID id_JRPSJobInfo_setUserName = env->GetMethodID(class_JRPSJobInfo, "setUserName", "(Ljava/lang/String;)V");
	jmethodID id_JRPSJobInfo_setPassword = env->GetMethodID(class_JRPSJobInfo, "setPassword", "(Ljava/lang/String;)V");
	jmethodID id_JRPSJobInfo_setDomain = env->GetMethodID(class_JRPSJobInfo, "setDomain", "(Ljava/lang/String;)V");
	jmethodID id_JRPSJobInfo_setExtendsInfo = env->GetMethodID(class_JRPSJobInfo, "setExtendsInfo", "(Ljava/lang/String;)V");

	env->CallVoidMethod(jRpsJobInfo, id_JRPSJobInfo_setJobId, rpsJobInfo.dwJobId);
	env->CallVoidMethod(jRpsJobInfo, id_JRPSJobInfo_setD2duuid, WCHARToJString(env, rpsJobInfo.wszD2DNodeGuid));
	env->CallVoidMethod(jRpsJobInfo, id_JRPSJobInfo_setD2dNodeName, WCHARToJString(env, rpsJobInfo.wszD2DNodeName));
	env->CallVoidMethod(jRpsJobInfo, id_JRPSJobInfo_setProcessId, rpsJobInfo.dwProcessId);
	env->CallVoidMethod(jRpsJobInfo, id_JRPSJobInfo_setSourceRpsUUID, WCHARToJString(env, rpsJobInfo.wszSrcRPSGuid));
	env->CallVoidMethod(jRpsJobInfo, id_JRPSJobInfo_setSrcPlanUUID, WCHARToJString(env, rpsJobInfo.wszSrcPlanUUID));
	env->CallVoidMethod(jRpsJobInfo, id_JRPSJobInfo_setTargetPlanUUID, WCHARToJString(env, rpsJobInfo.wszTargetPlanUUID));
	env->CallVoidMethod(jRpsJobInfo, id_JRPSJobInfo_setMspUser, rpsJobInfo.bMspUser);
	env->CallVoidMethod(jRpsJobInfo, id_JRPSJobInfo_setUserName, WCHARToJString(env, rpsJobInfo.wszUserName));
	env->CallVoidMethod(jRpsJobInfo, id_JRPSJobInfo_setPassword, WCHARToJString(env, rpsJobInfo.wszPassword));
	env->CallVoidMethod(jRpsJobInfo, id_JRPSJobInfo_setDomain, WCHARToJString(env, rpsJobInfo.wszDomain));
	env->CallVoidMethod(jRpsJobInfo, id_JRPSJobInfo_setExtendsInfo, WCHARToJString(env, rpsJobInfo.wszRepExtends));
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

void JRPSJobInfo2RPS_JOB_INFO(JNIEnv *env, RPS_JOB_INFO_S &rpsJobInfo, jobject &jRpsJobInfo)
{
	jclass class_JRPSJobInfo = env->GetObjectClass(jRpsJobInfo);
	jmethodID id_JRPSJobInfo_getJobId = env->GetMethodID(class_JRPSJobInfo, "getJobId", "()J");
	jmethodID id_JRPSJobInfo_getD2duuid = env->GetMethodID(class_JRPSJobInfo, "getD2duuid", "()Ljava/lang/String;");
	jmethodID id_JRPSJobInfo_getD2dNodeName = env->GetMethodID(class_JRPSJobInfo, "getD2dNodeName", "()Ljava/lang/String;");
	jmethodID id_JRPSJobInfo_getProcessId = env->GetMethodID(class_JRPSJobInfo, "getProcessId", "()J");
	jmethodID id_JRPSJobInfo_getSourceRpsUUID = env->GetMethodID(class_JRPSJobInfo, "getSourceRpsUUID", "()Ljava/lang/String;");
	jmethodID id_JRPSJobInfo_getSrcPlanUUID = env->GetMethodID(class_JRPSJobInfo, "getSrcPlanUUID", "()Ljava/lang/String;");
	jmethodID id_JRPSJobInfo_getTargetPlanUUID = env->GetMethodID(class_JRPSJobInfo, "getTargetPlanUUID", "()Ljava/lang/String;");
	jmethodID id_JRPSJobInfo_getMspUser = env->GetMethodID(class_JRPSJobInfo, "isMspUser", "()Z");
	jmethodID id_JRPSJobInfo_getUserName = env->GetMethodID(class_JRPSJobInfo, "getUserName", "()Ljava/lang/String;");
	jmethodID id_JRPSJobInfo_getPassword = env->GetMethodID(class_JRPSJobInfo, "getPassword", "()Ljava/lang/String;");
	jmethodID id_JRPSJobInfo_getDomain = env->GetMethodID(class_JRPSJobInfo, "getDomain", "()Ljava/lang/String;");
	jmethodID id_JRPSJobInfo_getExtendsInfo = env->GetMethodID(class_JRPSJobInfo, "getExtendsInfo", "()Ljava/lang/String;");

	// For File Copy
	jmethodID id_JRPSJobInfo_getArchiveInfo = env->GetMethodID(class_JRPSJobInfo, "getArchiveInfo", "()Lcom/ca/arcflash/webservice/data/archive/JJobScriptArchiveInfo;");
	JJobScriptArchiveInfo2ArchiveInfo(env, &rpsJobInfo.stArchiveSchInfo, env->CallObjectMethod(jRpsJobInfo, id_JRPSJobInfo_getArchiveInfo));

	rpsJobInfo.dwJobId = env->CallLongMethod(jRpsJobInfo, id_JRPSJobInfo_getJobId);
	wcsncpy_s(rpsJobInfo.wszD2DNodeGuid, 
		_countof(rpsJobInfo.wszD2DNodeGuid), 
		JStringToWString(env, (jstring)env->CallObjectMethod(jRpsJobInfo, id_JRPSJobInfo_getD2duuid)).c_str(), _TRUNCATE);
	wcsncpy_s(rpsJobInfo.wszD2DNodeName, 
		_countof(rpsJobInfo.wszD2DNodeName),
		JStringToWString(env, (jstring)env->CallObjectMethod(jRpsJobInfo, id_JRPSJobInfo_getD2dNodeName)).c_str(), _TRUNCATE);
	rpsJobInfo.dwProcessId = env->CallLongMethod(jRpsJobInfo, id_JRPSJobInfo_getProcessId);
	wcsncpy_s(rpsJobInfo.wszSrcRPSGuid, _countof(rpsJobInfo.wszSrcRPSGuid),
		JStringToWString(env, (jstring)env->CallObjectMethod(jRpsJobInfo, id_JRPSJobInfo_getSourceRpsUUID)).c_str(), _TRUNCATE);
	wcsncpy_s(rpsJobInfo.wszSrcPlanUUID, _countof(rpsJobInfo.wszSrcPlanUUID),
		JStringToWString(env, (jstring)env->CallObjectMethod(jRpsJobInfo, id_JRPSJobInfo_getSrcPlanUUID)).c_str(), _TRUNCATE);
	wcsncpy_s(rpsJobInfo.wszTargetPlanUUID, _countof(rpsJobInfo.wszTargetPlanUUID),
		JStringToWString(env, (jstring)env->CallObjectMethod(jRpsJobInfo, id_JRPSJobInfo_getTargetPlanUUID)).c_str(), _TRUNCATE);

	rpsJobInfo.bMspUser = env->CallBooleanMethod(jRpsJobInfo, id_JRPSJobInfo_getMspUser);
	wcsncpy_s(rpsJobInfo.wszUserName, _countof(rpsJobInfo.wszUserName),
		JStringToWString(env, (jstring)env->CallObjectMethod(jRpsJobInfo, id_JRPSJobInfo_getUserName)).c_str(), _TRUNCATE);
	wcsncpy_s(rpsJobInfo.wszPassword, _countof(rpsJobInfo.wszPassword),
		JStringToWString(env, (jstring)env->CallObjectMethod(jRpsJobInfo, id_JRPSJobInfo_getPassword)).c_str(), _TRUNCATE);
	wcsncpy_s(rpsJobInfo.wszDomain, _countof(rpsJobInfo.wszDomain),
		JStringToWString(env, (jstring)env->CallObjectMethod(jRpsJobInfo, id_JRPSJobInfo_getDomain)).c_str(), _TRUNCATE);
	wcsncpy_s(rpsJobInfo.wszRepExtends, _countof(rpsJobInfo.wszRepExtends),
		JStringToWString(env, (jstring)env->CallObjectMethod(jRpsJobInfo, id_JRPSJobInfo_getExtendsInfo)).c_str(), _TRUNCATE);
	
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
	jmethodID setfMergePercentage = env->GetMethodID(jcls, "setfMergePercentage", "(F)V");
	jmethodID setDwSessRangeCnt = env->GetMethodID(jcls, "setDwSessRangeCnt", "(I)V");
	jmethodID setDwSessRangeDoneCnt = env->GetMethodID(jcls, "setDwSessRangeDoneCnt", "(I)V");
	jmethodID setCurrentMergeRangeStart = env->GetMethodID(jcls, "setCurrentMergeRangeStart", "(I)V");
	jmethodID setCurrentMergeRangeEnd = env->GetMethodID(jcls, "setCurrentMergeRangeEnd", "(I)V");
	jmethodID setStartTime = env->GetMethodID(jcls, "setUllStartTime", "(J)V");

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
	env->CallVoidMethod(jmjm, setfMergePercentage, mergeCtrl->stWField.stMergeStatus.fMergePercentage);
	env->CallVoidMethod(jmjm, setDwSessRangeCnt, mergeCtrl->stWField.stMergeStatus.dwSessRangeCnt);
	env->CallVoidMethod(jmjm, setDwSessRangeDoneCnt, mergeCtrl->stWField.stMergeStatus.dwSessRangeDoneCnt);	
	env->CallVoidMethod(jmjm, setCurrentMergeRangeStart, mergeCtrl->stWField.stMergeStatus.stSessRangeList[0].dwStartSess);	
	env->CallVoidMethod(jmjm, setCurrentMergeRangeEnd, mergeCtrl->stWField.stMergeStatus.stSessRangeList[0].dwEndSess);
	env->CallVoidMethod(jmjm, setStartTime, mergeCtrl->stWField.stMergeStatus.ullJobStartTime*1000); // for job monitor, web service expects milliseconds, but backend sets seconds in job monitor, so we convert it here.
}

void JMergeJobScript2CMergeJS(JNIEnv *env, jobject& jmergeJS, CMergeJS& cmergeJS)
{
	jclass jcls = env->GetObjectClass(jmergeJS);

	jmethodID getDwJobID = env->GetMethodID(jcls, "getDwJobID", "()J");
	jmethodID getDwMergeOpt = env->GetMethodID(jcls, "getDwMergeOpt", "()I");
	jmethodID getDwMergeMethod = env->GetMethodID(jcls, "getDwMergeMethod", "()I");
	jmethodID getDwRetentionCnt = env->GetMethodID(jcls, "getDwRetentionCnt", "()I");
	jmethodID getDwDailyCnt = env->GetMethodID(jcls, "getDwDailyCnt", "()I");
	jmethodID getDwWeeklyCnt = env->GetMethodID(jcls, "getDwWeeklyCnt", "()I");
	jmethodID getDwMonthlyCnt = env->GetMethodID(jcls, "getDwMonthlyCnt", "()I");
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
	jmethodID getMergeFlag = env->GetMethodID(jcls, "getMergeFlag", "()I");
	jmethodID getProductType = env->GetMethodID(jcls, "getProductType", "()I");
	jmethodID getWsD2DID = env->GetMethodID(jcls, "getD2dID", "()Ljava/lang/String;");
	jmethodID getWsD2DAgentName = env->GetMethodID(jcls, "getWsD2DAgentName", "()Ljava/lang/String;");	
	jmethodID getRpsSvrName = env->GetMethodID(jcls, "getRpsSvrName", "()Ljava/lang/String;");
	jmethodID getRpsSvrGUID = env->GetMethodID(jcls, "getRpsSvrGUID", "()Ljava/lang/String;");
	jmethodID getDsDisplayName = env->GetMethodID(jcls, "getDsDisplayName", "()Ljava/lang/String;");
	jmethodID getDsGUID = env->GetMethodID(jcls, "getDsGUID", "()Ljava/lang/String;");
	jmethodID getCfTime = env->GetMethodID(jcls, "getArchiveConfigTime", "()J");		// New, For ArchiveToTape
	jmethodID getSrcSel = env->GetMethodID(jcls, "getArchiveSourceSelection", "()J");	// New, For ArchiveToTape
	//jmethodID getDayArr = env->GetMethodID(jcls, "getArchiveDailySelectedDays", "()[Z");// New, For ArchiveToTape. No use now

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
	cmergeJS.dwDailyCnt = env->CallIntMethod(jmergeJS, getDwDailyCnt);
	cmergeJS.dwWeeklyCnt = env->CallIntMethod(jmergeJS, getDwWeeklyCnt);
	cmergeJS.dwMonthlyCnt = env->CallIntMethod(jmergeJS, getDwMonthlyCnt);
	cmergeJS.dwStartSess = env->CallLongMethod(jmergeJS, getDwStartSess);
	cmergeJS.wsVMGUID = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getWsVMGUID));
	cmergeJS.dwSessType = env->CallIntMethod(jmergeJS, getDwSessionType);
	cmergeJS.dwMergeFlags = env->CallIntMethod(jmergeJS, getMergeFlag);
	cmergeJS.dwPrdType = env->CallIntMethod(jmergeJS, getProductType);
	cmergeJS.wsD2DIdentity = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getWsD2DID));
	cmergeJS.wsD2DAgentName = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getWsD2DAgentName));	
	cmergeJS.wsRpsSvrName = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getRpsSvrName));
	cmergeJS.wsRpsSvrGUID = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getRpsSvrGUID));
	cmergeJS.wsDsDisplayName = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getDsDisplayName));
	cmergeJS.wsDsGUID = JStringToWString(env, (jstring)env->CallObjectMethod(jmergeJS, getDsGUID));	
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
	jmethodID setVmInstanceUUID = env->GetMethodID(activeMergeJobClass, "setVmInstanceUUID", "(Ljava/lang/String;)V");
	jmethodID setJsPath = env->GetMethodID(activeMergeJobClass, "setJsPath", "(Ljava/lang/String;)V");
	jmethodID setD2DID = env->GetMethodID(activeMergeJobClass, "setD2dID", "(Ljava/lang/String;)V");

	ActJobVector::iterator iter;
	for(iter = jobs.begin(); iter != jobs.end(); iter ++) 
	{	
		jobject job = env->NewObject(activeMergeJobClass, jobConstructor);
		env->CallVoidMethod(job, setJobID, iter->dwJobID);
		env->CallVoidMethod(job, setProcessId, iter->dwProcID);
		env->CallVoidMethod(job, setVmInstanceUUID, WCHARToJString(env, iter->wzVMGUID));
		env->CallVoidMethod(job, setJsPath, WCHARToJString(env, iter->wzJSPath));
		env->CallVoidMethod(job, setD2DID, WCHARToJString(env, iter->wzD2DIdentity));

		env->CallBooleanMethod(jJobs, addMethod, job);
	}

	if(activeMergeJobClass != NULL)
		env->DeleteLocalRef(activeMergeJobClass);
}

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

void ConvertJThrottleItem2NETWORK_THROTTLING_ITEM(JNIEnv *env, jobject &throttleItem, PNETWORK_THROTTLING_ITEM item)
{
	jclass class_throttleItem = env->GetObjectClass(throttleItem);

	jmethodID id_getStartTime = env->GetMethodID(class_throttleItem, "getStartTime", "()Lcom/ca/arcflash/webservice/data/DayTime;");
	jmethodID id_getEndTime = env->GetMethodID(class_throttleItem, "getEndTime", "()Lcom/ca/arcflash/webservice/data/DayTime;");
	jmethodID id_getThrottleValue = env->GetMethodID(class_throttleItem, "getThrottleValue", "()J");

	jobject jobj_startTime = env->CallObjectMethod(throttleItem, id_getStartTime);
	jobject jobj_endTime = env->CallObjectMethod(throttleItem, id_getEndTime);

	jclass class_dayTime = env->GetObjectClass(jobj_startTime);

	jmethodID id_getHour = env->GetMethodID(class_dayTime, "getHour", "()I");
	jmethodID id_getMinute = env->GetMethodID(class_dayTime, "getMinute", "()I");
	
	item->nStartHour = env->CallIntMethod(jobj_startTime, id_getHour);
	item->nStartMinute = env->CallIntMethod(jobj_startTime, id_getMinute);

	item->nEndHour = env->CallIntMethod(jobj_endTime, id_getHour);
	item->nEndMinute = env->CallIntMethod(jobj_endTime, id_getMinute);

	item->nBandwidth = env->CallIntMethod(throttleItem, id_getThrottleValue);
}

void ConvertJDailyScheduleDetailItem2NETWORK_THROTTLING_DAY(JNIEnv *env, jobject &jDailyScheduleDetailItem, NETWORK_THROTTLING_DAY &day)
{
	jclass class_dailyScheduleDetailItem = env->GetObjectClass(jDailyScheduleDetailItem);

	jmethodID id_getThrottleItems = env->GetMethodID(class_dailyScheduleDetailItem, "getThrottleItems", "()Ljava/util/ArrayList;");

	jobject jobj_ThrottleItem_list = env->CallObjectMethod(jDailyScheduleDetailItem, id_getThrottleItems);

	jclass class_arrayList = env->GetObjectClass(jobj_ThrottleItem_list);

	jmethodID id_arrayList_size = env->GetMethodID(class_arrayList, "size", "()I");
	jmethodID id_arrayList_get = env->GetMethodID(class_arrayList, "get", "(I)Ljava/lang/Object;");

	int size = env->CallIntMethod(jobj_ThrottleItem_list, id_arrayList_size);

	if(size == 0)
		return;

	day.nItemCount = size;
	day.pItemArray = (PNETWORK_THROTTLING_ITEM)malloc(sizeof(NETWORK_THROTTLING_ITEM)*size);

	for(int i = 0; i < size; i++) 
	{
		jobject obj = env->CallObjectMethod(jobj_ThrottleItem_list, id_arrayList_get, i);
		ConvertJThrottleItem2NETWORK_THROTTLING_ITEM(env, obj, day.pItemArray+i);
	}
	
}
void ConvertJDailyScheduleDetailItemList2NETWORK_THROTTLING_POLICY(JNIEnv *env, jobject &jThrottlingSetting, NETWORK_THROTTLING_POLICY &policy)
{
	jclass class_arrayList = env->GetObjectClass(jThrottlingSetting);

	jmethodID id_arrayList_size = env->GetMethodID(class_arrayList, "size", "()I");
	jmethodID id_arrayList_get = env->GetMethodID(class_arrayList, "get", "(I)Ljava/lang/Object;");

	int size = env->CallIntMethod(jThrottlingSetting, id_arrayList_size);
	for(int i = 0; i < size && i < 7; i++) 
	{
		jobject obj = env->CallObjectMethod(jThrottlingSetting, id_arrayList_get, i);
		jclass class_JDailyScheduleDetailItem = env->GetObjectClass(obj);
		jmethodID id_getDayofWeek = env->GetMethodID(class_JDailyScheduleDetailItem, "getDayofWeek", "()I");
		int index = env->CallIntMethod(obj, id_getDayofWeek);
		if(index >=1)
			index -= 1;
		else
			continue;
		ConvertJDailyScheduleDetailItem2NETWORK_THROTTLING_DAY(env, obj, policy.aDayArray[index]);
	}
}

void FreeNETWORK_THROTTLING_POLICY(NETWORK_THROTTLING_POLICY &policy)
{
	for(int i = 0; i < _ARRAYSIZE(policy.aDayArray); i++) 
	{
		policy.aDayArray[i].nItemCount = 0;
		SAFE_FREE(policy.aDayArray[i].pItemArray);
	}
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

	jclass class_ArrayList = env->GetObjectClass(*list);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
	env->CallBooleanMethod(*list, id_ArrayList_add, jRestorePoint);

	jfieldID field_items= env->GetFieldID(class_RestorePoint, "items", "Ljava/util/List;");
	jobject itemList = env->GetObjectField(jRestorePoint,field_items);

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

	if(!item->vBakItem.empty())
	{
		AddVBACKUP_ITEM2List(env,itemList,item->vBakItem);	
	}

	if (jRestorePoint != NULL) env->DeleteLocalRef(jRestorePoint);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);
	if (class_RestorePoint != NULL) env->DeleteLocalRef(class_RestorePoint);	

	return 0;
}

int AddVBACKUP_ITEM2List(JNIEnv *env, jobject jBkpItemArrList, VBACKUP_ITEM& vBkpItem)
{	
	jclass class_ArrayList = env->GetObjectClass(jBkpItemArrList);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");	
	jclass class_BackupItem = env->FindClass("com/ca/arcflash/service/jni/model/JBackupItem");
	jmethodID mid_BackupItem_constructor = env->GetMethodID(class_BackupItem, "<init>", "()V");
	for(VBACKUP_ITEM::iterator backupItem = vBkpItem.begin(); backupItem != vBkpItem.end(); backupItem++)
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

void JJobHistory2PFLASHDB_JOB_HISTORY( JNIEnv* env, jobject& jJobHistory, PFLASHDB_JOB_HISTORY pHistory )
{
	jclass class_JJobHistory			= env->FindClass("com/ca/arcflash/jni/common/JJobHistory");	
	jfieldID field_jobId				= env->GetFieldID( class_JJobHistory, "jobId",				"J" );
	jfieldID field_jobType				= env->GetFieldID( class_JJobHistory, "jobType",			"I" );
	jfieldID field_jobStatus			= env->GetFieldID( class_JJobHistory, "jobStatus",			"I" );
	jfieldID field_jobMethod			= env->GetFieldID( class_JJobHistory, "jobMethod",			"I" );
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


void JActivityLogDetail2PACTLOG_DETAILS( JNIEnv* env, jobject& jActLogDetail, PACTLOG_DETAILS pActLogDetail )
{
	jclass class_JActLogDetails			= env->FindClass("com/ca/arcflash/service/jni/model/JActLogDetails");
	jfieldID field_dwProductType		= env->GetFieldID( class_JActLogDetails, "productType",			"I");
	jfieldID field_dwJobID				= env->GetFieldID( class_JActLogDetails, "jobID",					"I");
	jfieldID field_dwJobType			= env->GetFieldID( class_JActLogDetails, "jobType",				"I");
	jfieldID field_dwJobMethod			= env->GetFieldID( class_JActLogDetails, "jobMethod",				"I");
	jfieldID field_dwLogLevel			= env->GetFieldID( class_JActLogDetails, "logLevel",				"I");
	jfieldID field_bIsVMInstance		= env->GetFieldID( class_JActLogDetails, "isVMInstance",			"Z");
	
	jfieldID field_wszSvrNodeName		= env->GetFieldID( class_JActLogDetails, "svrNodeName",		"Ljava/lang/String;" );
	jfieldID field_wszSvrNodeID			= env->GetFieldID( class_JActLogDetails, "svrNodeID",		"Ljava/lang/String;" );
	jfieldID field_wszAgentNodeName		= env->GetFieldID( class_JActLogDetails, "agentNodeName",	"Ljava/lang/String;" );
	jfieldID field_wszAgentNodeID		= env->GetFieldID( class_JActLogDetails, "agentNodeID",		"Ljava/lang/String;" );
	jfieldID field_wszSourceRPSID		= env->GetFieldID( class_JActLogDetails, "sourceRPSID",		"Ljava/lang/String;" );
	jfieldID field_wszTargetRPSID		= env->GetFieldID( class_JActLogDetails, "targetRPSID",		"Ljava/lang/String;" );
	jfieldID field_wszDSUUID			= env->GetFieldID( class_JActLogDetails, "dsUUID",			"Ljava/lang/String;" );
	jfieldID field_wszTargetDSUUID		= env->GetFieldID( class_JActLogDetails, "targetDSUUID",		"Ljava/lang/String;" );
	jfieldID field_wszPlanUUID			= env->GetFieldID( class_JActLogDetails, "planUUID",			"Ljava/lang/String;" );
	jfieldID field_wszTargetPlanUUID	= env->GetFieldID( class_JActLogDetails, "targetPlanUUID",	"Ljava/lang/String;" );

	if(field_dwProductType)
		pActLogDetail->dwProductType = env->GetIntField(jActLogDetail, field_dwProductType);

	if(field_dwJobID)
		pActLogDetail->dwJobID = env->GetIntField(jActLogDetail, field_dwJobID);

	if(field_dwJobType)
		pActLogDetail->dwJobType = env->GetIntField(jActLogDetail, field_dwJobType);

	if(field_dwJobMethod)
		pActLogDetail->dwJobMethod = env->GetIntField(jActLogDetail, field_dwJobMethod);

	if(field_dwLogLevel)
		pActLogDetail->dwLogLevel = env->GetIntField(jActLogDetail, field_dwLogLevel);

	if(field_bIsVMInstance)
		pActLogDetail->bIsVMInstance = env->GetBooleanField(jActLogDetail, field_bIsVMInstance);

	wchar_t *ptr = NULL;
	if(field_wszSvrNodeName)
		ptr = GetStringFromField(env, &jActLogDetail, field_wszSvrNodeName);
	if( ptr )
		wcsncpy_s( pActLogDetail->wszSvrNodeName, _ARRAYSIZE(pActLogDetail->wszSvrNodeName), ptr, _TRUNCATE );
	SAFE_FREE(ptr);

	if(field_wszSvrNodeID)
		ptr = GetStringFromField(env, &jActLogDetail, field_wszSvrNodeID);
	if( ptr )
		wcsncpy_s( pActLogDetail->wszSvrNodeID, _ARRAYSIZE(pActLogDetail->wszSvrNodeID), ptr, _TRUNCATE );
	SAFE_FREE(ptr);

	if(field_wszAgentNodeName)
		ptr = GetStringFromField(env, &jActLogDetail, field_wszAgentNodeName);
	if( ptr )
		wcsncpy_s( pActLogDetail->wszAgentNodeName, _ARRAYSIZE(pActLogDetail->wszAgentNodeName), ptr, _TRUNCATE );
	SAFE_FREE(ptr);

	if(field_wszAgentNodeID)
		ptr = GetStringFromField(env, &jActLogDetail, field_wszAgentNodeID);
	if( ptr )
		wcsncpy_s( pActLogDetail->wszAgentNodeID, _ARRAYSIZE(pActLogDetail->wszAgentNodeID), ptr, _TRUNCATE );
	SAFE_FREE(ptr);

	if(field_wszSourceRPSID)
		ptr = GetStringFromField(env, &jActLogDetail, field_wszSourceRPSID);
	if( ptr )
		wcsncpy_s( pActLogDetail->wszSourceRPSID, _ARRAYSIZE(pActLogDetail->wszSourceRPSID), ptr, _TRUNCATE );
	SAFE_FREE(ptr);

	if(field_wszTargetRPSID)
		ptr = GetStringFromField(env, &jActLogDetail, field_wszTargetRPSID);
	if( ptr )
		wcsncpy_s( pActLogDetail->wszTargetRPSID, _ARRAYSIZE(pActLogDetail->wszTargetRPSID), ptr, _TRUNCATE );
	SAFE_FREE(ptr);

	if(field_wszDSUUID)
		ptr = GetStringFromField(env, &jActLogDetail, field_wszDSUUID);
	if( ptr )
		wcsncpy_s( pActLogDetail->wszDSUUID, _ARRAYSIZE(pActLogDetail->wszDSUUID), ptr, _TRUNCATE );
	SAFE_FREE(ptr);

	if(field_wszTargetDSUUID)
		ptr = GetStringFromField(env, &jActLogDetail, field_wszTargetDSUUID);
	if( ptr )
		wcsncpy_s( pActLogDetail->wszTargetDSUUID, _ARRAYSIZE(pActLogDetail->wszTargetDSUUID), ptr, _TRUNCATE );
	SAFE_FREE(ptr);

	if(field_wszPlanUUID)
		ptr = GetStringFromField(env, &jActLogDetail, field_wszPlanUUID);
	if( ptr )
		wcsncpy_s( pActLogDetail->wszPlanUUID, _ARRAYSIZE(pActLogDetail->wszPlanUUID), ptr, _TRUNCATE );
	SAFE_FREE(ptr);

	if(field_wszTargetPlanUUID)
		ptr = GetStringFromField(env, &jActLogDetail, field_wszTargetPlanUUID);
	if( ptr )
		wcsncpy_s( pActLogDetail->wszTargetPlanUUID, _ARRAYSIZE(pActLogDetail->wszTargetPlanUUID), ptr, _TRUNCATE );
	SAFE_FREE(ptr);
}

void Vector_WSTR2JStringList( JNIEnv* env, std::vector<wstring> &vecSource, jobject& jResult)
{
	jclass class_ArrayList = env->GetObjectClass(jResult);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

	for(std::vector<std::wstring>::iterator iter = vecSource.begin(); iter != vecSource.end(); iter++)
	{
		jstring value = WCHARToJString(env, (wchar_t*)iter->c_str());
		env->CallBooleanMethod(jResult, id_ArrayList_add, value);
	}
	
}
void JPurgeDataJobScript2AFPurgeDataJS(JNIEnv *env, jobject& jPurgeDataJS, PAFPURGEDATAJOBSCRIPT pAFPurgeDataJS)
{

	jclass jPurgeDatacls = env->GetObjectClass(jPurgeDataJS);


	jmethodID	getVersion		= env->GetMethodID(jPurgeDatacls, "getVersion", "()J");
	jmethodID	getJobID		= env->GetMethodID(jPurgeDatacls, "getJobID", "()J");
	jmethodID	getJobType		= env->GetMethodID(jPurgeDatacls, "getJobType", "()J");
	jmethodID	getNodeItems	= env->GetMethodID(jPurgeDatacls, "getNodeItems", "()J");
	jmethodID	getNodeList		= env->GetMethodID(jPurgeDatacls, "getNodeList", "()Ljava/util/List;");

	jmethodID	getDestPath		= env->GetMethodID(jPurgeDatacls, "getDestinationPath", "()Ljava/lang/String;");
	jmethodID	getDestUser		= env->GetMethodID(jPurgeDatacls, "getDestinationUserName", "()Ljava/lang/String;");
	jmethodID	getDestPWD		= env->GetMethodID(jPurgeDatacls, "getDestinationPassword", "()Ljava/lang/String;");
	jmethodID	getComments		= env->GetMethodID(jPurgeDatacls, "getComments", "()Ljava/lang/String;");
	jmethodID	getOptions		= env->GetMethodID(jPurgeDatacls, "getOptions", "()J");
	jmethodID	getJobAttribute	= env->GetMethodID(jPurgeDatacls, "getJobAttribute", "()J");
	jmethodID	getRPSName		= env->GetMethodID(jPurgeDatacls, "getRPSName", "()Ljava/lang/String;");
	jmethodID	getRPSUUID		= env->GetMethodID(jPurgeDatacls, "getRPSUUID", "()Ljava/lang/String;");
	jmethodID	getPolicyName	= env->GetMethodID(jPurgeDatacls, "getPolicyName", "()Ljava/lang/String;");
	jmethodID	getPolicyGUID	= env->GetMethodID(jPurgeDatacls, "getPolicyGUID", "()Ljava/lang/String;");
	jmethodID	getDataStoreUUID= env->GetMethodID(jPurgeDatacls, "getDataStoreUUID", "()Ljava/lang/String;");
	jmethodID	getDataStoreName= env->GetMethodID(jPurgeDatacls, "getDataStoreName", "()Ljava/lang/String;");
	jmethodID	getScheduledTime= env->GetMethodID(jPurgeDatacls, "getScheduledTime", "()J");



	pAFPurgeDataJS->ulVersion		= env->CallLongMethod(jPurgeDataJS,getVersion);
	pAFPurgeDataJS->ulJobNum		= env->CallLongMethod(jPurgeDataJS,getJobID);
	pAFPurgeDataJS->usJobType		= env->CallLongMethod(jPurgeDataJS,getJobType);
	pAFPurgeDataJS->nNodeItems		= env->CallLongMethod(jPurgeDataJS,getNodeItems);

	wstring		wsContent			= JStringToWString(env, (jstring)env->CallObjectMethod(jPurgeDataJS, getDestPath));		
	if (wsContent.length())
	{
		pAFPurgeDataJS->pwszDestPath	= 	new wchar_t[wsContent.length()+1];
		wcscpy_s(pAFPurgeDataJS->pwszDestPath,wsContent.length()+1,wsContent.c_str());
	}

	wsContent			= JStringToWString(env, (jstring)env->CallObjectMethod(jPurgeDataJS, getDestUser));		
	if (wsContent.length())
	{
		pAFPurgeDataJS->pwszUserName	= 	new wchar_t[wsContent.length()+1];
		wcscpy_s(pAFPurgeDataJS->pwszUserName,wsContent.length()+1,wsContent.c_str());
	}

	wsContent			= JStringToWString(env, (jstring)env->CallObjectMethod(jPurgeDataJS, getDestPWD));		
	if (wsContent.length())
	{
		pAFPurgeDataJS->pwszPassword	= 	new wchar_t[wsContent.length()+1];
		wcscpy_s(pAFPurgeDataJS->pwszPassword,wsContent.length()+1,wsContent.c_str());
	}

	wsContent			= JStringToWString(env, (jstring)env->CallObjectMethod(jPurgeDataJS, getComments));		
	if (wsContent.length())
	{
		pAFPurgeDataJS->pwszComments	= 	new wchar_t[wsContent.length()+1];
		wcscpy_s(pAFPurgeDataJS->pwszComments,wsContent.length()+1,wsContent.c_str());
	}

	pAFPurgeDataJS->fOptions		= env->CallLongMethod(jPurgeDataJS,getOptions);
	pAFPurgeDataJS->ulJobAttribute	= env->CallLongMethod(jPurgeDataJS,getJobAttribute);

	wsContent			= JStringToWString(env, (jstring)env->CallObjectMethod(jPurgeDataJS, getRPSName));		
	if (wsContent.length())
	{
		pAFPurgeDataJS->pRPSName	= 	new wchar_t[wsContent.length()+1];
		wcscpy_s(pAFPurgeDataJS->pRPSName,wsContent.length()+1,wsContent.c_str());
	}

	wsContent			= JStringToWString(env, (jstring)env->CallObjectMethod(jPurgeDataJS, getRPSUUID));		
	if (wsContent.length())
	{
		pAFPurgeDataJS->pwzRPSSvrSID	= 	new wchar_t[wsContent.length()+1];
		wcscpy_s(pAFPurgeDataJS->pwzRPSSvrSID,wsContent.length()+1,wsContent.c_str());
	}

	wsContent			= JStringToWString(env, (jstring)env->CallObjectMethod(jPurgeDataJS, getPolicyName));		
	if (wsContent.length())
	{
		pAFPurgeDataJS->pPolicyName	= 	new wchar_t[wsContent.length()+1];
		wcscpy_s(pAFPurgeDataJS->pPolicyName,wsContent.length()+1,wsContent.c_str());
	}

	wsContent			= JStringToWString(env, (jstring)env->CallObjectMethod(jPurgeDataJS, getPolicyGUID));		
	if (wsContent.length())
	{
		pAFPurgeDataJS->pPolicyGUID	= 	new wchar_t[wsContent.length()+1];
		wcscpy_s(pAFPurgeDataJS->pPolicyGUID,wsContent.length()+1,wsContent.c_str());
	}


	wsContent			= JStringToWString(env, (jstring)env->CallObjectMethod(jPurgeDataJS, getDataStoreUUID));		
	if (wsContent.length())
	{
		pAFPurgeDataJS->pDataStore	= 	new wchar_t[wsContent.length()+1];
		wcscpy_s(pAFPurgeDataJS->pDataStore,wsContent.length()+1,wsContent.c_str());
	}

	wsContent			= JStringToWString(env, (jstring)env->CallObjectMethod(jPurgeDataJS, getDataStoreName));		
	if (wsContent.length())
	{
		pAFPurgeDataJS->pDataStoreName	= 	new wchar_t[wsContent.length()+1];
		wcscpy_s(pAFPurgeDataJS->pDataStoreName,wsContent.length()+1,wsContent.c_str());
	}

	pAFPurgeDataJS->ullScheduledTime= env->CallLongMethod(jPurgeDataJS,getScheduledTime);

	jobject		nodelist			= env->CallObjectMethod(jPurgeDataJS, getNodeList);
	jclass		class_List			= env->GetObjectClass(nodelist);
	jmethodID	method_List_size	= env->GetMethodID(class_List, "size", "()I");
	jmethodID	id_List_get			= env->GetMethodID(class_List, "get", "(I)Ljava/lang/Object;");

	pAFPurgeDataJS->nNodeItems		= env->CallIntMethod(nodelist, method_List_size);
	if (pAFPurgeDataJS->nNodeItems)
	{
		pAFPurgeDataJS->pAFNodeList		= new AFPURGENODEINFO[pAFPurgeDataJS->nNodeItems];
		jobject		entryObject			= env->CallObjectMethod(nodelist, id_List_get,0);
		jclass		class_NodeInfo		= env->GetObjectClass(entryObject);
		jmethodID	getNodeName			= env->GetMethodID(class_NodeInfo, "getNodeName", "()Ljava/lang/String;");
		jmethodID	getNodeID			= env->GetMethodID(class_NodeInfo, "getNodeID", "()Ljava/lang/String;");
		jmethodID	getIsVM				= env->GetMethodID(class_NodeInfo,	"isVM","()Z");
		jmethodID	getHostName			= env->GetMethodID(class_NodeInfo,	"getHostName","()Ljava/lang/String;");
		

		for (ULONG nNode = 0; nNode < pAFPurgeDataJS->nNodeItems; ++nNode)
		{
			jobject entryObject	= env->CallObjectMethod(nodelist, id_List_get,nNode);
			wstring wsNode		= JStringToWString(env, (jstring)env->CallObjectMethod(entryObject, getNodeName));	
			wcscpy_s(pAFPurgeDataJS->pAFNodeList[nNode].wszNodeName,_countof(pAFPurgeDataJS->pAFNodeList[nNode].wszNodeName),wsNode.c_str());

			wsNode		= JStringToWString(env, (jstring)env->CallObjectMethod(entryObject, getNodeID));	
			wcscpy_s(pAFPurgeDataJS->pAFNodeList[nNode].wszNodeID,_countof(pAFPurgeDataJS->pAFNodeList[nNode].wszNodeID),wsNode.c_str());
			wsNode		= JStringToWString(env, (jstring)env->CallObjectMethod(entryObject, getHostName));
			wcscpy_s(pAFPurgeDataJS->pAFNodeList[nNode].wszHostName, _countof(pAFPurgeDataJS->pAFNodeList[nNode].wszHostName), wsNode.c_str());
			pAFPurgeDataJS->pAFNodeList[nNode].bIsVMNode = env->CallBooleanMethod(entryObject,getIsVM);
			

		}

		if (class_NodeInfo != NULL)
		{
			env->DeleteLocalRef(class_NodeInfo);
		}
	}


	if (class_List != NULL)
	{
		env->DeleteLocalRef(class_List);
	}

	if (jPurgeDataJS != NULL)
	{
		env->DeleteLocalRef(jPurgeDatacls);
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