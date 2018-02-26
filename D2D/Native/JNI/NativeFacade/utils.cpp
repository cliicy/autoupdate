#include "stdafx.h"
#include <eh.h>
#include "utils.h"
#include "JNIConv.h"
#include <drcommonlib.h>
#include <dbglog.h>
#include "HaCommonFunc.h"
#include "..\..\Virtualization\VirtualStandby\HaUtility\ComEnvironment.h"
#include "brandname.h"
#include "HypervInterface.h" //<sonmi01>2012-11-29 #edit vm in a editable VM state

#define STRING(x)		L##x 
#define WSTRING(x)		STRING(x)
#define __WFUNCTION__	WSTRING(__FUNCTION__)

TCHAR	szCAHome[] = CST_REG_HEADER_T;
TCHAR	szD2DHome[] = CST_PRODUCT_REG_NAME_T;

CDbgLog logObj(L"NativeFacade");

wstring Utility_JStringToWCHAR( JNIEnv *env, jstring str)
{
	if (str == NULL) return L""; //<sonmi01>2013-8-29 #66663: D2D agent service stopped, got dump file

	const jchar* jchs = env->GetStringChars(str, NULL);// UCS-2 to Unicode;

	if (jchs == NULL) return L"";

	wstring wstr((wchar_t*)jchs);

	env->ReleaseStringChars(str, jchs);

	return wstr;

}

jstring WCHARToJString( JNIEnv * env, const wstring& str)
{
	jstring rtn = 0;

	if (!env) 
		return 0;

	if (str.empty()) {
		char sTemp[2] = {0};
		strcpy_s(sTemp, "");
		rtn = env->NewStringUTF(sTemp);
		return rtn;
	}

	rtn = env->NewString((jchar*)str.c_str(), str.length());

	return rtn;
}

void ThrowHyperVException(JNIEnv * env, jclass klass, jstring message, jint code)
{
	jmethodID methodID=env->GetStaticMethodID(klass, "throwHyperVException", "(Ljava/lang/String;I)V");
	env->CallStaticVoidMethod(klass, methodID, message, code);	
}

void ThrowVMwareException(JNIEnv * env,jstring message, jint code)
{
	jclass clazz = env->FindClass("com/ca/ha/webservice/jni/VMwareException");
	jmethodID constructorID = env->GetMethodID(clazz,"<init>","(Ljava/lang/String;I)V");
	jobject vmwareExcp = env->NewObject(clazz,constructorID,message,code);
	env->Throw((jthrowable)vmwareExcp);
}
void ThrowWSJNIException(JNIEnv * env,jclass klass,jstring message, jint code)
{
	jmethodID methodID=env->GetStaticMethodID(klass, "throwWSJNIException", "(Ljava/lang/String;I)V");
	env->CallStaticVoidMethod(klass, methodID, message, code);	

}

void JStrListToCVector(JNIEnv *env,jclass klass, jobject jList,vector<wstring>& strVec){

	if(!jList || !env){
		return;
	}
	try{
		//get jobject meta info
		jclass jListCls = env->GetObjectClass(jList);
		jmethodID ipSizeMethodID = env->GetMethodID(jListCls,"size","()I");
		jmethodID ipGetMethodID = env->GetMethodID(jListCls,"get","(I)Ljava/lang/Object;");
		jint jListLen = env->CallIntMethod(jList,ipSizeMethodID);
		//populate vector
		jstring jstrElement;
		wstring wstrElement;
		for(int i=0; i<jListLen; ++i){
			jstrElement = static_cast<jstring>(env->CallObjectMethod(jList,ipGetMethodID,i));
			wstrElement = Utility_JStringToWCHAR(env,jstrElement);
			strVec.push_back(wstrElement);
		}
	}catch(...){
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in CreateVm."), 1);
	}
}

void JIntListToCVector(JNIEnv *env,jclass klass,jobject jList,vector<unsigned short>& uShortVec){

	if(!jList || !env){
		return;
	}

	try{
		//Get jobject meta info
		jclass jListCls = env->GetObjectClass(jList);
		jmethodID ipSizeMethodID = env->GetMethodID(jListCls,"size","()I");
		jmethodID ipGetMethodID = env->GetMethodID(jListCls,"get","(I)Ljava/lang/Object;");
		jint jListLen = env->CallIntMethod(jList,ipSizeMethodID);
		//populate vector
		jclass integerClass = env->FindClass("java/lang/Integer");
		jobject jShortObj;
		jfieldID fieldID;
		for(jint i=0; i<jListLen; ++i){
			jShortObj = env->CallObjectMethod(jList,ipGetMethodID,i);
			fieldID = env->GetFieldID(integerClass,"value","I");
			unsigned short filedValue = (unsigned short)(env->GetShortField(jShortObj,fieldID));
			uShortVec.push_back(filedValue);
		}

	}catch(...){
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in JIntListToCVector."), 1);
	}
}

jobject CMapToJMap(JNIEnv *env,jclass klass, HyperVObjectMap hyperVMap){

	if(!env ){
		return NULL;
	}
	try{
		//get hashmap meta info and construct a hashmap object
		jclass mapClass = env->FindClass("java/util/HashMap");
		jmethodID constructorID = env->GetMethodID(mapClass,"<init>","()V");
		jobject mapObj = env->NewObject(mapClass,constructorID);
		jmethodID putMethodID = env->GetMethodID(mapClass,"put","(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

		//populate hashmap from hyperVMap 
		HyperVObjectMap::iterator end = hyperVMap.end();
		for(HyperVObjectMap::iterator it=hyperVMap.begin();it!=end;++it){
			jstring jstrKey = WCHARToJString(env,it->first);
			jstring jstrValue = WCHARToJString(env,it->second);
			env->CallVoidMethod(mapObj,putMethodID,jstrKey,jstrValue);
		}
		return mapObj;
	}catch(...){
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in CMapToJMap."), 1);
		return NULL;
	}
}

//
// please use HandleSEH instead of below API
//
int CreateDumpFile( PEXCEPTION_POINTERS pInfo, wstring dicPath, wstring &savePath )
{
	time_t tm;
	HANDLE hFile;
	MINIDUMP_EXCEPTION_INFORMATION ExInfo;
	WCHAR path[MAX_PATH] = {0};
	int ret = 0;

	time(&tm);
	_snwprintf_s( path, MAX_PATH, L"%s\\%d.dmp", dicPath.c_str(), tm );
	hFile=CreateFile(path, GENERIC_WRITE, FILE_SHARE_READ, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
	if (hFile == INVALID_HANDLE_VALUE)
	{
		if(GetLastError() == ERROR_PATH_NOT_FOUND)
		{
			if(CreatePathFold(dicPath) < 0 )
				return -1;
			hFile=CreateFile(path, GENERIC_WRITE, FILE_SHARE_READ, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
			if (hFile == INVALID_HANDLE_VALUE)
				return -1;
		}
		else
			return -1;
	}

	ExInfo.ThreadId = GetCurrentThreadId();
	ExInfo.ExceptionPointers = pInfo;
	ExInfo.ClientPointers = FALSE;

	if (MiniDumpWriteDump( GetCurrentProcess(), GetCurrentProcessId(), hFile, MiniDumpNormal, &ExInfo, NULL, NULL ))
	{
		printf( "success to write dump file\n" );
		ret = -1;
	}
	else
		printf( "failed to write dump file\n" );
	CloseHandle(hFile);
	savePath = path;
	return ret;
}

void HandleDllMissed( const WCHAR* _dllName)
{
	DWORD dwErr = GetLastError(); // this is only valid when HandleDllMissed is called right after LoadLibrary
	logObj.LogW(LL_ERR, dwErr, __WFUNCTION__ L": fail to load DLL: [%s]", _dllName);
}

void HandleProcMissed( const WCHAR* _dllName, const char* _procName )
{
	DWORD dwErr = GetLastError(); // this is only valid when HandleProcMissed is called right after GetProcAddress
	logObj.LogW(LL_ERR, dwErr, __WFUNCTION__  L": fail to find proc [%S] in DLL: [%s]", _procName, _dllName);
}

void HandleSEH( const WCHAR* _dllName, const char* _procName, const EXCEPTION_POINTERS* _pEx, unsigned int errCode)
{
	// stack overflow exception can not be handled
    TLocker<AutoCS> csLocker;

	if (_pEx->ExceptionRecord->ExceptionCode == EXCEPTION_STACK_OVERFLOW)
	{
		logObj.LogW(LL_ERR, -1, __WFUNCTION__  L": stack overflow caught @ [%S] in DLL: [%s]", _procName, _dllName);
		return;
	}

	WCHAR szEptName[MAX_PATH * 2] = {0}, szDllName[MAX_PATH] = {0}, *backslash = NULL;

	logObj.LogW(LL_ERR, -1, __WFUNCTION__  L": exception caught @ [%S] in DLL: [%s]", _procName, _dllName);

	if(_dllName && wcslen(_dllName))
		wcscpy_s(szDllName, _dllName);
	else
		wcscpy_s(szDllName, L"NativeFacade");

	DWORD dwSizeInCharacters = _countof(szEptName);
	if(0 != CDbgLog::GetDefaultLogFolder( szEptName, &dwSizeInCharacters ) )
	{
		GetModuleFileNameW(NULL, szEptName, _countof(szEptName));
		if (szEptName[0] == 0)
			return;

		backslash = wcsrchr(szEptName, L'\\');
		if (backslash == NULL)
			return;

		*backslash = L'\0';
	}

	wcscat_s(szEptName, L"\\");
	wcscat_s(szEptName, szDllName);

	WCHAR szTempPath[MAX_PATH * 2] = {0};
	
	int iIndex = 0;

	// name form: dllname0000x.mdp
	do
	{
		swprintf_s(szTempPath, L"%s_%05d.dmp", szEptName, iIndex);
		iIndex++;
	}
	while(PathFileExistsW(szTempPath));
	    
	GenDumpFile(szTempPath, _pEx);
}

int CreatePathFold( wstring dicPath )
{
	wstring path;
	size_t idx = 0, c = 0;

	dicPath += L'\\';
	while( (idx = dicPath.find(L'\\', idx)) != wstring::npos )
	{
		path = dicPath.substr(0, idx);
		if( _waccess( path.c_str(), 0 ) != 0 )
		{
			if( 0 == CreateDirectoryW( path.c_str(), NULL ) )
				return -1;
		}
		idx++;
	}
	return 0;
}

HANDLE DynGetProcAddress( const WCHAR* dllName, const char* procName )
{
	HMODULE dll = GetModuleHandle( dllName );
	HANDLE proc = NULL;
	if( dll == NULL )
	{
		dll = LoadLibrary( dllName );
		if( dll == NULL )
		{
			HandleDllMissed( dllName );
			return NULL;
		}
	}
	proc = GetProcAddress( dll, procName);
	if( proc == NULL )
	{
		HandleProcMissed( dllName, procName );
		return NULL;
	}
	return proc;

}

CDllException::CDllException()
{

}

CDllException::CDllException(int _type, const wstring &_dllName, const string &_funName):m_type(_type), m_dllName(_dllName), m_funName(_funName)
{

}

CDllException::CDllException(int _type, const wstring &_dllName, const string &_funName, const wstring &_message):m_type(_type), m_dllName(_dllName), m_funName(_funName), m_message(_message)
{

}


DWORD SetStringValue2Field(JNIEnv *env, jobject object, char* name, std::wstring targetValue){

	jclass class_value= env->GetObjectClass(object);
	jfieldID field = env->GetFieldID(class_value, name, "Ljava/lang/String;");
	jstring value = WCHARToJString(env, (wchar_t*)targetValue.c_str());
	env->SetObjectField(object, field, value);

	if ( value!=NULL) env->DeleteLocalRef(value);
	if (class_value != NULL) env->DeleteLocalRef(class_value);
	return 0;
}


DWORD SetBoolValue2Field(JNIEnv *env, jobject object, char* name, bool bValue)
{
	jclass class_value= env->GetObjectClass(object);
	jmethodID id_setBoolValue = env->GetMethodID(class_value, name, "(Z)V");
	env->CallVoidMethod(object, id_setBoolValue, bValue);

	if (class_value != NULL) env->DeleteLocalRef(class_value);
	return 0;
}


DWORD SetLongValue2Field( JNIEnv *env, jobject object, char* name, __int64 targetValue )
{
	jclass class_value= env->GetObjectClass(object);
	jfieldID field = env->GetFieldID(class_value, name, "J");

	env->SetLongField(object, field, targetValue);

	if (class_value != NULL) env->DeleteLocalRef(class_value);
	return 0;
}

DWORD NewJavaObject( JNIEnv *env, char* className, jobject &obj )
{
	jclass jcls = NULL;
	jmethodID jmethod = NULL;
	obj = NULL;

	jcls = env->FindClass(className);
	if (NULL == jcls)
		return -1;

	jmethod = env->GetMethodID(jcls, "<init>", "()V");
	if (NULL == jmethod)
		return -1;

	obj = env->NewObject(jcls, jmethod);
	return 0;
}

DWORD AddJobject2Vector( JNIEnv *env, jobject jvec, jobject obj )
{
	jclass jcls = NULL;
	jmethodID jadd = NULL;

	jcls = env->FindClass("java/util/Vector");
	jadd = env->GetMethodID(jcls, "add", "(Ljava/lang/Object;)Z");
	if(JNI_FALSE == env->CallBooleanMethod(jvec, jadd, obj))
		return -1;
	else
		return 0;
}

DWORD ConstructGetSubRootParams(JNIEnv* env,jobject& javaParam,ST_HASRV_INFO* pstSrvInfo,
								wstring& wstrProductNode,wstring& pwszLastDesRoot,HA_SRC_ITEM* pwszSrcItem, wchar_t**& ppwszDesRootList,int &destCount)
{

    ppwszDesRootList = NULL;
    destCount = 0;
	jclass classRepModel = env->GetObjectClass(javaParam);

	/*
	public String getPwszProductNode() {
	return pwszProductNode;
	}
	*/
	jmethodID methodid_RepClass = env->GetMethodID(classRepModel, "getPwszProductNode", "()Ljava/lang/String;");
	jobject objResult = env->CallObjectMethod(javaParam, methodid_RepClass);
	wstrProductNode = JStringToWCHAR(env,(jstring)objResult);

	/*
	public String getPwszOldDesFolder() {
	return pwszOldDesFolder;
	}
	*/
	methodid_RepClass = env->GetMethodID(classRepModel, "getPwszOldDesFolder", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(javaParam, methodid_RepClass);
	wchar_t* tmp = JStringToWCHAR(env,(jstring)objResult);
	if(tmp != NULL)
	{
		pwszLastDesRoot = tmp;
        free(tmp); tmp = NULL;
	}else
	{
		pwszLastDesRoot = L"";
	}

	/*
	public String getPwszDesHostName() {
	return pwszDesHostName;
	}
	*/
	methodid_RepClass = env->GetMethodID(classRepModel, "getPwszDesHostName", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(javaParam, methodid_RepClass);
	pstSrvInfo->pwszName = JStringToWCHAR(env,(jstring)objResult);
	/*
	public String getPwszDesPort() {
	return pwszDesPort;
	}
	*/
	methodid_RepClass = env->GetMethodID(classRepModel, "getPwszDesPort", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(javaParam, methodid_RepClass);
	pstSrvInfo->pwszPort = JStringToWCHAR(env,(jstring)objResult);

	/*
	public String getPwszUserName() {
	return pwszUserName;
	}
	*/
	methodid_RepClass = env->GetMethodID(classRepModel, "getPwszUserName", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(javaParam, methodid_RepClass);
	pstSrvInfo->pwszUser = JStringToWCHAR(env,(jstring)objResult);
	/*
	public String getPwszPassword() {
	return pwszPassword;
	}
	*/
	methodid_RepClass = env->GetMethodID(classRepModel, "getPwszPassword", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(javaParam, methodid_RepClass);
	pstSrvInfo->pwszPwd = JStringToWCHAR(env,(jstring)objResult);
	/*
	public long getUlSrcItemCnt() {
	return ulSrcItemCnt;
	}
	*/
	methodid_RepClass = env->GetMethodID(classRepModel, "getUlSrcItemCnt", "()J");
	jlong longResuslt = env->CallLongMethod(javaParam, methodid_RepClass);
	/*
	public ArrayList<SourceItemModel> getpSrcItemList() {
	return pSrcItemList;
	}
	*/
	if(longResuslt > 0)
	{
		methodid_RepClass = env->GetMethodID(classRepModel, "getpSrcItemList", "()Ljava/util/ArrayList;");
		objResult = env->CallObjectMethod(javaParam, methodid_RepClass);
		jclass class_list = env->GetObjectClass(objResult);
		jmethodID id_array_get = env->GetMethodID(class_list, "get", "(I)Ljava/lang/Object;");

		jobject src_item = env->CallObjectMethod(objResult,id_array_get,0);
		//jclass itemclass = env->FindClass("com/ca/arcflash/webservice/jni/SourceItemModel");
		jclass itemclass = env->GetObjectClass(src_item);
		/*
		public String getPwszPath() {
		return pwszPath;
		}
		*/
		jmethodID item_id_JJobMonitor_method = env->GetMethodID(itemclass, "getPwszPath", "()Ljava/lang/String;");
		jobject item_objResult = env->CallObjectMethod(src_item, item_id_JJobMonitor_method);
		pwszSrcItem->pwszPath = JStringToWCHAR(env,(jstring)item_objResult);
		/*
		public String getPwszSFUsername() {
		return pwszSFUsername;
		}
		*/
		item_id_JJobMonitor_method = env->GetMethodID(itemclass, "getPwszSFUsername", "()Ljava/lang/String;");
		item_objResult = env->CallObjectMethod(src_item, item_id_JJobMonitor_method);
		pwszSrcItem->pwszSFUsername = JStringToWCHAR(env,(jstring)item_objResult);
		/*
		public String getPwszSFPassword() {
		return pwszSFPassword;
		}
		*/
		item_id_JJobMonitor_method = env->GetMethodID(itemclass, "getPwszSFPassword", "()Ljava/lang/String;");
		item_objResult = env->CallObjectMethod(src_item, item_id_JJobMonitor_method);
		pwszSrcItem->pwszSFPassword = JStringToWCHAR(env,(jstring)item_objResult);

		jmethodID methodid_src_item = env->GetMethodID(itemclass,"getFiles","()Ljava/util/List;");
		jobject object_file_list = env->CallObjectMethod(src_item,methodid_src_item);

		jclass class_file_list = env->GetObjectClass(object_file_list);
		jmethodID methodid_list_size = env->GetMethodID(class_file_list,"size","()I");
		jint list_size = env->CallIntMethod(object_file_list,methodid_list_size);

		destCount = list_size;

		wchar_t** pp = new wchar_t* [list_size];
        ZeroMemory(pp, list_size * sizeof(wchar_t*));

		wchar_t** base = pp;

		jmethodID methodid_list_get = env->GetMethodID(class_file_list,"get","(I)Ljava/lang/Object;");

		for(int index = 0 ; index < list_size; index++){

			jobject object_file_item = env->CallObjectMethod(object_file_list,methodid_list_get,index);
			jclass class_file_item = env->GetObjectClass(object_file_item);

			jmethodID methodid_file_item = env->GetMethodID(class_file_item,"getFileDestination","()Ljava/lang/String;");
			jobject object_ret_value = env->CallObjectMethod(object_file_item,methodid_file_item);
			base[index] = JStringToWCHAR(env,(jstring)object_ret_value);

		}

		ppwszDesRootList = pp;

	}

	return 0;
}

typedef BOOL (WINAPI * PFN_MINIDUMPWRITEDUMP)( \
											  IN HANDLE hProcess, \
											  IN DWORD ProcessId, \
											  IN HANDLE hFile, \
											  IN MINIDUMP_TYPE DumpType, \
											  IN CONST PMINIDUMP_EXCEPTION_INFORMATION ExceptionParam, OPTIONAL \
											  IN CONST PMINIDUMP_USER_STREAM_INFORMATION UserStreamParam, OPTIONAL \
											  IN CONST PMINIDUMP_CALLBACK_INFORMATION CallbackParam OPTIONAL \
											  );

void  GenDumpFile(WCHAR *szDumpFileName, const EXCEPTION_POINTERS *pExcept)
{
	DWORD dwExceptCode = 0;
	DWORD dwProcId = GetCurrentProcessId();
	HANDLE hProc = INVALID_HANDLE_VALUE;
	HANDLE hFile = INVALID_HANDLE_VALUE;
	MINIDUMP_EXCEPTION_INFORMATION exceptInfo;

	HMODULE	hDbgHelp = NULL;
	PFN_MINIDUMPWRITEDUMP pfnMiniDumpWriteDump = NULL;

	hDbgHelp = LoadLibraryW(L"dbghelp.dll");
	if(NULL != hDbgHelp)
	{
		pfnMiniDumpWriteDump = (PFN_MINIDUMPWRITEDUMP)GetProcAddress(hDbgHelp, "MiniDumpWriteDump");
		if(NULL != pfnMiniDumpWriteDump)
		{
			hProc = ::OpenProcess(PROCESS_ALL_ACCESS,FALSE,GetCurrentProcessId());
			if(NULL == hProc)
				hProc = GetCurrentProcess(); // hanl got here is a pseudo handle to current process

			hFile = CreateFileW(szDumpFileName, GENERIC_WRITE, FILE_SHARE_READ, NULL,
				CREATE_ALWAYS, 0, NULL);
			if(INVALID_HANDLE_VALUE != hFile)

			{
				//static AutoCS AutoSEHCS;
				//EnterCriticalSection(AutoSEHCS);
				exceptInfo.ThreadId = GetCurrentThreadId();
				exceptInfo.ExceptionPointers = (EXCEPTION_POINTERS *)pExcept;
				exceptInfo.ClientPointers = TRUE;
				MINIDUMP_TYPE DumpType = MiniDumpNormal;
				pfnMiniDumpWriteDump( hProc, dwProcId, hFile, DumpType, 
					&exceptInfo, NULL, NULL);
				CloseHandle(hFile);
				//LeaveCriticalSection(AutoSEHCS);
			}
		}
		FreeLibrary(hDbgHelp);
	}
}

LONG globalExceptionHandler(EXCEPTION_POINTERS *ExInfo)
{
    HandleSEH(L"NativeFacade", "tomcat6.exe", ExInfo, 0);
	
	return( EXCEPTION_CONTINUE_SEARCH );
}


LPTOP_LEVEL_EXCEPTION_FILTER oldExceptionHandler = NULL;
void setExceptionHandler()
{
	oldExceptionHandler = SetUnhandledExceptionFilter( (LPTOP_LEVEL_EXCEPTION_FILTER)globalExceptionHandler );
}

void revertExceptionHandler()
{
	SetUnhandledExceptionFilter(oldExceptionHandler);
}

NET_CONN_INFO getConnectionInfo(JNIEnv *env, jstring dest,jstring domain,jstring user,jstring pwd)
{
	wchar_t* pDest = JStringToWCHAR(env, dest);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	wchar_t* pUser = JStringToWCHAR(env, user);
	wchar_t* pPwd = JStringToWCHAR(env, pwd);

	NET_CONN_INFO info;
	memset(&info, 0, sizeof(NET_CONN_INFO));
	if (pDest!=NULL)
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
	return info;
}

DWORD getAvailableMountDriveLetters(std::vector<wstring>& avaliableLetters)
{
	DWORD dwRet = GetLogicalDrives();
	if(dwRet == 0){
		return GetLastError();
	}
	
	for(int i=0; i<26; i++)
	{
		if(!(dwRet & (1<<i)))
		{
			wchar_t szChar[3] = {0};
			szChar[0] = L'A' + i;
			szChar[1] = L':';
			avaliableLetters.push_back(szChar);
		}
	}
	return dwRet;
}

DWORD getWindowsTempDir(wstring& strTempDir)
{
	WCHAR szBuf[1024+1] = {0};
	DWORD dwRet = 0;

	dwRet = GetTempPath(1024, szBuf);
	if(dwRet == 0)
	{
		return GetLastError();
	}

	strTempDir.append(szBuf);
	return 0;
}

DWORD readRegDWORDValue(const WCHAR* pwzRegKeyName, const WCHAR* pwzRegValName, const WCHAR* pwzD2DRootReg, DWORD& dwValue)
{
	DWORD dwRet = 0;
	DWORD dwTempValue = 0;
	LPBYTE lpBuffer = (LPBYTE)&dwTempValue;
	DWORD dwBufferSize = sizeof(DWORD);
	DWORD dwRegType = REG_DWORD;
	
	dwRet = AFIReadD2DReg(lpBuffer,dwBufferSize, dwRegType, pwzRegKeyName, pwzRegValName,pwzD2DRootReg);
	if(0 == dwRet)
	{
		dwValue = dwTempValue;
	}
	return dwRet;
}

DWORD writeRegDWORDValue(const WCHAR* pwzRegKeyName, const WCHAR* pwzRegValName, const WCHAR* pwzD2DRootReg, DWORD dwValue)
{
	DWORD dwRet = 0;
	LPBYTE lpBuffer = (LPBYTE)&dwValue;
	DWORD dwBufferSize = sizeof(DWORD);
	dwRet = AFIWriteD2DReg(lpBuffer, dwBufferSize,REG_DWORD, pwzRegKeyName,pwzRegValName, pwzD2DRootReg);
	return dwRet;
}

DWORD readRegMultiStringValue(const WCHAR* pwzRegKeyName, const WCHAR* pwzRegValName, const WCHAR* pwzD2DRootReg,vector<wstring>& vecValue)
{
	DWORD dwRet = 0;
	LPBYTE lpBuffer = NULL;
	DWORD dwBufSize = 0;
	DWORD dwRegType = REG_MULTI_SZ;
	dwRet = AFIReadD2DReg(lpBuffer, dwBufSize, dwRegType,  pwzRegKeyName,pwzRegValName, pwzD2DRootReg);
	if(0 != dwRet)
		return dwRet;

	lpBuffer = new BYTE[dwBufSize];
	memset(lpBuffer, 0, dwBufSize);
	dwRet = AFIReadD2DReg(lpBuffer, dwBufSize, dwRegType,  pwzRegKeyName,pwzRegValName, pwzD2DRootReg);
	if(0 != dwRet)
	{
		delete []lpBuffer;
		return dwRet;
	}

	WCHAR* p = (WCHAR*)lpBuffer;
	while (wcslen(p)>0)
	{
		vecValue.push_back(p);
		p = p +wcslen(p)+1;
	}
	delete []lpBuffer;
	return dwRet;
}

DWORD writeRegMultiStringValue(const WCHAR* pwzRegKeyName, const WCHAR* pwzRegValName, const WCHAR* pwzD2DRootReg, vector<wstring> vecValue)
{
	DWORD dwRet = 0;
	DWORD dwTotalSize = 0;
	
	for (int i = 0; i< vecValue.size(); i++)
	{
		dwTotalSize += vecValue.at(i).size() +1;
	}
	dwTotalSize += 1;

	WCHAR* buffer = new WCHAR[dwTotalSize];
	DWORD offset = 0;
	memset(buffer, 0 ,dwTotalSize*sizeof(WCHAR));
	for (int i = 0;i <vecValue.size(); i++)
	{
		wstring wsValue = vecValue.at(i);
		wcscpy(buffer+offset, wsValue.c_str());
		offset += wsValue.size()+1;
	}
	LPBYTE lpBuffer = (LPBYTE)buffer;
	dwRet = AFIWriteD2DReg(lpBuffer, dwTotalSize*sizeof(WCHAR),REG_MULTI_SZ,pwzRegKeyName,pwzRegValName, pwzD2DRootReg);
	delete []buffer;

	return dwRet;
}


BOOL isAMD64Platform()
{
	HaUtility::ComEnvironment::Instance().Start();
	WORD cpuArc = GetHostCPUArchitecture();
	HaUtility::ComEnvironment::Instance().Shutdown();

	if(cpuArc == 9)
	{
		return TRUE;
	}
	else
	{
		return FALSE;
	}
}

wstring getApacheParametersRegPath()
{
	wstring strRegPath =L"";
	if(isAMD64Platform())
	{
		strRegPath.append(L"SOFTWARE\\Wow6432Node\\Apache Software Foundation\\Procrun 2.0\\CASAD2DWebSvc\\Parameters");
	}
	else
	{
		strRegPath.append(L"SOFTWARE\\Apache Software Foundation\\Procrun 2.0\\CASAD2DWebSvc\\Parameters");
	}
	return strRegPath;
}

BOOL IsMsVmSnapshotApplicable(LONG state)
{
	using namespace HyperVManipulation;
	MSVM_STATE vmState = (MSVM_STATE)state;
	return (vmState == MSVM_STATE_DISABLED || MSVM_STATE_ENABLED == vmState);
}

//////////////////////////////////////////////////////////////////////////
BOOL IsOK_MSVM_STATE(LONG state)
{
	using namespace HyperVManipulation;

	switch ((MSVM_STATE)state)
	{
	case MSVM_STATE_DISABLED:
	case MSVM_STATE_ENABLED:
		return TRUE;
	case MSVM_STATE_UNKNOWN:
	case MSVM_STATE_RESET:
	case MSVM_STATE_RESET_V2:
	case MSVM_STATE_REBOOT_PAUSED:
	case MSVM_STATE_REBOOT_SUSPENDED:
	case MSVM_STATE_STARTING:
	case MSVM_STATE_SNAPSHOTTING:
	case MSVM_STATE_SAVING:
	case MSVM_STATE_STOPPING:
	case MSVM_STATE_PAUSING:
	case MSVM_STATE_RESUMING:
		return FALSE;
	default:
		return FALSE;
	}
}

BOOL IsOK_MSVM_HEALTH_STATE(LONG HealthState)
{
	using namespace HyperVManipulation;

	switch ((MSVM_HEALTH_STATE)HealthState)
	{
	case MSVM_HEALTH_STATE_OK:
		return TRUE;
	case MSVM_HEALTH_STATE_MAJOR_FAILURE:
	case MSVM_HEALTH_STATE_CRITICAL_FAILURE:
		return FALSE;
	default:
		return FALSE;
	}
}

//<huvfe01>2012-12-7 #VM operational status
BOOL IsOK_MSVM_OperationalStatus(CONST vector<LONG>& vOperationalStatus)
{
    using namespace HyperVManipulation;

    size_t nSize = vOperationalStatus.size();
    BOOL bOK = FALSE;

    switch ((MSVM_OPERATIONAL_STATUS_INDEX_0)(vOperationalStatus[0]))
    {
    case MSVM_OPERATIONAL_STATUS_OK:
        {
            bOK = TRUE;
            break;
        }
    case MSVM_OPERATIONAL_STATUS_DEGRADED:
    case MSVM_OPERATIONAL_STATUS_PREDICTIVE_FAILURE:
    case MSVM_OPERATIONAL_STATUS_STOPPED:
    case MSVM_OPERATIONAL_STATUS_INSERVICE:
    case MSVM_OPERATIONAL_STATUS_DORMANT:
    default:
        {
            bOK = FALSE;
            break;
        }
    }    
   
    return bOK;
}

BOOL IsHypervVMEditable(LONG State, LONG HealthState, CONST vector<LONG>& vOperationalStatus)
{
	return ((IsOK_MSVM_STATE(State)) && 
            (IsOK_MSVM_HEALTH_STATE(HealthState)) && 
            ((vOperationalStatus.size() > 0) ? (IsOK_MSVM_OperationalStatus(vOperationalStatus)) : FALSE));
}

BOOL IsHyperVMSnapshotApplicable(LONG State, LONG HealthState, CONST vector<LONG>& vOperationalStatus)
{
	return ((IsMsVmSnapshotApplicable(State)) && 
            (IsOK_MSVM_HEALTH_STATE(HealthState)) && 
            ((vOperationalStatus.size() > 0) ? (IsOK_MSVM_OperationalStatus(vOperationalStatus)) : FALSE));
}


BOOL CheckHyperVMEditableWithRetry(VOID* pVirtualMachine, DWORD dwRetryInterval, DWORD dwLoopCount)
{
    using namespace HyperVManipulation;

    IVirtualMachine* pVm = (IVirtualMachine*)pVirtualMachine;

    BOOL bVMEditable = FALSE;
    vector<LONG> vOperationalStatus;
    for (DWORD ii = 0; ii < dwLoopCount; ii++)
    {
        vOperationalStatus.clear();
        pVm->Get_MsvmOperationalStatus(vOperationalStatus);
        if (IsHypervVMEditable(pVm->GetState(), pVm->GetHealthState(), vOperationalStatus))
        {
            bVMEditable = TRUE;
            break;
        }
        else
        {
            Sleep(dwRetryInterval);
        }
    }

    return bVMEditable;
}

BOOL CheckHyperVmSnapshotApplicableWithRetry(VOID* pVirtualMachine, DWORD dwRetryInterval, DWORD dwLoopCount)
{
	 using namespace HyperVManipulation;

    IVirtualMachine* pVm = (IVirtualMachine*)pVirtualMachine;

    BOOL isSnapshotApplicable = FALSE;
    vector<LONG> vOperationalStatus;
    for (DWORD ii = 0; ii < dwLoopCount; ii++)
    {
        vOperationalStatus.clear();
        pVm->Get_MsvmOperationalStatus(vOperationalStatus);
        if (IsHyperVMSnapshotApplicable(pVm->GetState(), pVm->GetHealthState(), vOperationalStatus))
        {
            isSnapshotApplicable = TRUE;
            break;
        }
        else
        {
            Sleep(dwRetryInterval);
        }
    }

    return isSnapshotApplicable;
}

/////////////////////////////////////////////////////////////////////////////////////
DWORD OpenRegistryKey(PTCHAR szKey, HKEY* phKey, BOOL bCreate)
{
	DWORD	rc = ERROR_SUCCESS;
	DWORD	dwDisposition;

	if (bCreate)
		rc = RegCreateKeyEx(HKEY_LOCAL_MACHINE,
		szKey,
		0,
		NULL,
		REG_OPTION_NON_VOLATILE,
		KEY_ALL_ACCESS,
		NULL,
		phKey,
		&dwDisposition);
	else
		rc = RegOpenKeyEx(HKEY_LOCAL_MACHINE,
		szKey,
		0,
		KEY_ALL_ACCESS,
		phKey);

	return rc;	
}


DWORD GetRegistryValue(PTCHAR szKey,  PTCHAR szValName, PDWORD  pdwValue,
					 BOOL bCreate, DWORD dwDefaultValue)
{
	HKEY	hKey;
	DWORD	dwValueType, dwBufSize;
	DWORD	rc = ERROR_SUCCESS;

	rc = OpenRegistryKey(szKey, &hKey, bCreate);
	if (rc != ERROR_SUCCESS) 
	{
		*pdwValue = dwDefaultValue;
		return rc;
	}

	// no matter if the key exists, query the value, if failed, create the value

	dwBufSize = sizeof(DWORD);
	rc = RegQueryValueEx(hKey,
		szValName,
		0,
		&dwValueType,
		(LPBYTE) pdwValue,
		&dwBufSize);

	if (rc != ERROR_SUCCESS && bCreate) 
	{
		rc = RegSetValueEx(hKey,
			szValName,
			0,
			REG_DWORD,
			(LPBYTE) &dwDefaultValue,
			sizeof(dwDefaultValue));

		*pdwValue = dwDefaultValue;
	}

	RegCloseKey(hKey);
	return rc;
}

DWORD QueryRegistryValue( PTCHAR szKey,
						  PTCHAR szSubKey,
						  PTCHAR szValName,
						  PDWORD pdwValue,
						  DWORD	 dwDefaultValue)
{
	TCHAR	szTempKey[MAX_PATH];
	_sntprintf_s(szTempKey,_countof(szTempKey),_TRUNCATE, _T("%s\\%s"), szKey, szSubKey);
	return GetRegistryValue(szTempKey, szValName, pdwValue, TRUE, dwDefaultValue);
}

DWORD QueryVDDKRegKey(PTCHAR szKeyName)
{
	DWORD dwValue = 0;
	
	if(!QueryRegistryValue(szCAHome, szD2DHome, szKeyName, &dwValue, 0))
	{
		return dwValue;
	}

	return 0;
}

BOOL DeleteDirectoryHelper(CString strPath) //<huvfe01>2013-1-17 recursively delete folder
{
    TCHAR files[MAX_NT_PATH] = {0};
    TCHAR pTemp[MAX_NT_PATH] = {0};
    HANDLE hFile = INVALID_HANDLE_VALUE;
    WIN32_FIND_DATA fdata = {0};

    strPath.TrimRight(TEXT("\\/"));

    _stprintf_s(files, MAX_NT_PATH, _T("%s\\*"), strPath.GetString());

    hFile = FindFirstFile(files, &fdata);
    if(hFile == INVALID_HANDLE_VALUE)
    {
        return FALSE;
    }

    do
    {
        if (!STRCMP(fdata.cFileName, _T(".")) || !STRCMP(fdata.cFileName, _T("..")))
        {
            continue;
        }

        _stprintf_s(pTemp, MAX_NT_PATH, _T("%s\\%s"), strPath.GetString(), fdata.cFileName);

        if( fdata.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY )
        {
            DeleteDirectoryHelper(pTemp);
        }

        DeleteFile(pTemp);

    }while(FindNextFile(hFile, &fdata) == TRUE);

    FindClose(hFile);

    return RemoveDirectory(strPath.GetString());
}

// Check if given backup item is Exchange writer or not
// by comparing 1) type=Application; 2) DisplayName starts
// with "Microsoft Exchange"
BOOL isExchangeWriter(BACKUP_ITEM bakItem)
{
	BOOL bRet = FALSE;
	WCHAR szType[] = L"APPLICATION";
	WCHAR szExchWriter[] = L"MICROSOFT EXCHANGE";
	WCHAR szCompType[MAX_PATH] = {0};
	WCHAR szCompWriter[MAX_PATH] = {0};

	if(bakItem.strType.size() > 0 && bakItem.strDisplayName.size() > 0)
	{
		wcsncpy(szCompType, bakItem.strType.c_str(), wcslen(szType));
		wcsncpy(szCompWriter, bakItem.strDisplayName.c_str(), wcslen(szExchWriter));

		if(0 == _wcsicmp(szCompType, szType) 
			&& 0 == wcsicmp(szCompWriter, szExchWriter))
		{
			bRet = TRUE;
		}
	}

	return bRet;
}

// Check if the given backup item contains any DB
BOOL hasExchangeDB(BACKUP_ITEM bakItem)
{
	BOOL bHasChild = FALSE;
	HANDLE hCat = INVALID_HANDLE_VALUE;
	list<UINT> queue;
	WCHAR catFile[MAX_PATH*2] = {0};

	if(!isExchangeWriter(bakItem))
	{
		return bHasChild;
	}

	if(bakItem.strCalalogFile.size() > 0)
	{
		wcscpy(catFile, bakItem.strCalalogFile.c_str());
		hCat = OpenCatalogFile(catFile);
	}

	if(NULL != hCat && INVALID_HANDLE_VALUE != hCat)
	{
		UINT parentID = -1;	// root item ID is -1
		queue.push_back(parentID);	// push the root item ID into queue;

		PDetailW pDet = NULL;

		while(!bHasChild && queue.size() > 0)
		{
			parentID = queue.front();
			queue.pop_front();

			UINT chdCount = GetChildrenCount(hCat, parentID);
			UINT nStart = 0;
			UINT nRetrieved = 0;
			WCHAR szBuf[MAX_PATH] = {0};
			if(chdCount > 0)
			{
				pDet = GetChildrenEx(hCat, parentID, nStart, chdCount, &nRetrieved);

				if(pDet)
				{
					for(UINT i = 0; i < nRetrieved; i++)
					{
						if(OT_VSS_EXCH_DBAEXSIS_MBSDB == pDet[i].DataType 
							|| OT_VSS_EXCH_DBAEXSIS_PUBLIC_FOLDERS == pDet[i].DataType)
						{
							bHasChild = TRUE;
							break;
						}						

						queue.push_back(pDet[i].LongNameID);
					}
					HeapFree(GetProcessHeap(), 0, pDet);
				}
			}
		}

		CloseCatalogFile(hCat);
	}

	return bHasChild;
}

NET_CONN_INFO CreateNetConnInfo(const WCHAR* pszSource, const WCHAR* pszUsrName, const WCHAR* pszUsrPwd)
{
	NET_CONN_INFO netInfo = { 0 };

	if (pszSource)
		wcsncpy_s(netInfo.szDir, ARRAYSIZE(netInfo.szDir), pszSource, _TRUNCATE);
	if (pszUsrPwd)
		wcsncpy_s(netInfo.szPwd, ARRAYSIZE(netInfo.szPwd), pszUsrPwd, _TRUNCATE);

	if (pszUsrName != NULL)	// Following analyze username / domain.
	{
		wstring strName = pszUsrName;
		wstring strJustName = L"";
		wstring strDomain = L"";
		wstring::size_type nPos = strName.find(L'\\');
		if (nPos == wstring::npos)	// Format: user@domain
		{
			nPos = strName.find(L'@');
			if (nPos == wstring::npos){
				strJustName = strName;
			}
			else{
				strJustName = strName.substr(0, nPos);
				strDomain = strName.substr(nPos + 1);
			}
		}
		else if (nPos < 2)		// Format: \administrator or .\administrator 
		{
			strJustName = strName.substr(nPos + 1);
		}
		else					// Format: domain\user
		{
			strJustName = strName.substr(nPos + 1);
			strDomain = strName.substr(0, nPos);
		}

		if (strJustName.length() > 0)
			wcsncpy_s(netInfo.szUsr, ARRAYSIZE(netInfo.szUsr), strJustName.c_str(), _TRUNCATE);

		if (strDomain.length() > 0)
			wcsncpy_s(netInfo.szDomain, ARRAYSIZE(netInfo.szDomain), strDomain.c_str(), _TRUNCATE);
	}

	return netInfo;
}