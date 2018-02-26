#include "stdafx.h"
#include "windows.h"
#include "iostream"
#include "wchar.h"
#include "jni.h"
#include "drcommonlib.h"
#include "V2PNativeFacade.h"
#include "NativeClass.h"
#include "JVM_Wrapper.h"
#include "StringUtil.h"
#include "V2PNF_Log.h"
#include "D2DSwitch.h" //<sonmi01>2013-11-26 ###???
#include <atlstr.h>


namespace { //start namespace

/***********************************
example of xxx.exe.jvm.ini

[jvm]
HeapSize=128
MaxPermSize=64
Debug=0
***********************************/

struct JVMSettings 
{
	int HeapSize;
	int MaxPermSize;
	int Debug;
};

static CONST TCHAR APP_NAME[] = TEXT("jvm");
static CONST TCHAR HEAPSIZE_NAME[] = TEXT("HeapSize");
static CONST TCHAR MAXPERMSIZE_NAME[] = TEXT("MaxPermSize");
static CONST TCHAR DEBUG_NAME[] = TEXT("Debug");

static CONST TCHAR EXT[] = TEXT(".jvm.ini");

VOID GetJVMSettings(JVMSettings & jvmSettings)
{
	TCHAR IniFile[MAX_PATH + MAX_PATH] = {0};

	DWORD dwLen = GetModuleFileName(
		NULL,//_In_opt_  HMODULE hModule,
		IniFile, //_Out_     LPTSTR lpFilename,
		_countof(IniFile)//_In_      DWORD nSize
		);

	
	_tcscat_s(IniFile, EXT);

	const int DEFAULT_HEAPSIZE = 512;
	const int DEFAULT_MAXPERMSIZE = 64;

	jvmSettings.HeapSize = (DWORD)GetSwitchIntFromIni(APP_NAME, HEAPSIZE_NAME, DEFAULT_HEAPSIZE, IniFile);
	if (jvmSettings.HeapSize < 64)
	{
		jvmSettings.HeapSize = 64;
	}
	if (jvmSettings.HeapSize > 512)
	{
		jvmSettings.HeapSize = 512;
	}

	jvmSettings.MaxPermSize = (DWORD)GetSwitchIntFromIni(APP_NAME, MAXPERMSIZE_NAME, DEFAULT_MAXPERMSIZE, IniFile);

	if (jvmSettings.MaxPermSize < 32)
	{
		jvmSettings.MaxPermSize = 32;
	}
	if (jvmSettings.MaxPermSize > 256)
	{
		jvmSettings.MaxPermSize = 256;
	}

	jvmSettings.Debug = (DWORD)GetSwitchIntFromIni(APP_NAME, DEBUG_NAME, 0, IniFile);
}

} //end namespace

//#if 1

JNIEnv* NativeClass::create_vm(JavaVM ** jvm, const char* jarList, BOOL bOldVer) {

	V2PNFlog log;
	log.writeDebugLog(_T("Attempting to create JVM."));

	CStringA fileList = "";
	string strJrePath = "";
	string strClassPath = "";
	if (0 != GetJREEnvironmentVariables("V2PNativeFacade", strJrePath, strClassPath) || strClassPath.empty())
	{
		fileList = "-Djava.class.path="; //<sonmi01>2014-9-29 ###???
		//add by zhepa02 at 2015-05-04 to verify the current operation is bmr or hbbu
		BOOL bWinPE = false;
		HKEY hKey;
		//add end

		static LPSTR new_jars[] =
		{
			"vmwaremanager.jar;",
			"axis.jar;",
			"commons-discovery-0.2.jar;",
			"commons-logging-1.0.4.jar;",
			"jaxrpc.jar;",
			"log4j-1.2.8.jar;",
			"saaj.jar;",
			"vim25.jar;",
			"wsdl4j-1.5.1.jar;",
			"vcloudmanager.jar;",
			"log4j-1.2.15.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/vcloud-java-sdk-5.5.0.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/rest-api-schemas-5.5.0.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/amqp-client-2.8.6.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/commons-codec-1.6.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/commons-logging-1.1.1.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/httpclient-4.2.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/httpcore-4.2.jar;"
		};


		static LPSTR old_jars[] =
		{
			"VmwareManager_AXIS.jar;",
			"axis.jar;",
			"commons-discovery-0.2.jar;",
			"commons-logging-1.0.4.jar;",
			"jaxrpc.jar;",
			"log4j-1.2.8.jar;",
			"saaj.jar;",
			"vim25_AXIS.jar;",
			"wsdl4j-1.5.1.jar;",
			"vcloudmanager.jar;",
			"log4j-1.2.15.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/vcloud-java-sdk-5.5.0.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/rest-api-schemas-5.5.0.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/amqp-client-2.8.6.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/commons-codec-1.6.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/commons-logging-1.1.1.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/httpclient-4.2.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/httpcore-4.2.jar;"
		};

		//////////////////////////////////////////////////////////////////////////
		char szPath[MAX_PATH] = "";
		::GetModuleFileNameA(NULL, szPath, sizeof(szPath)); // could be: C:\Program Files\CA\arcserve Unified Data Protection\Management\BIN\VixGetVolumeDetails.exe

		//add by zhepa02 at 2015-05-05 , verify the current OS
		if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, WIN_PE, 0, KEY_READ, &hKey) == ERROR_SUCCESS)
		{
			bWinPE = TRUE;
		}
		//add end

		char* pb = strrchr(szPath, '\\');
		*pb = '\0';		// trancate to: C:\Program Files\CA\arcserve Unified Data Protection\Management\BIN
		pb = strrchr(szPath, '\\');
		*pb = '\0';		// trancate to: C:\Program Files\CA\arcserve Unified Data Protection\Management
		pb = strrchr(szPath, '\\');
		*pb = '\0';		// trancate to: C:\Program Files\CA\arcserve Unified Data Protection

		//add by zhepa02 at 2015-05-04 to get the correct path of the jars for the current operation
		if (!bWinPE)
			strncat_s(szPath, sizeof(szPath), "\\Engine\\BIN\\", _TRUNCATE);
		else
			strncat_s(szPath, sizeof(szPath), "\\system32\\dr\\", _TRUNCATE);
		//add end

		if (!bOldVer)
		{
			for (int ii = 0; ii < _countof(new_jars); ++ii)
			{
				fileList += szPath;
				fileList += new_jars[ii];
			}
		}
		else
		{
			for (int ii = 0; ii < _countof(old_jars); ++ii)
			{
				fileList += szPath;
				fileList += old_jars[ii];
			}
		}
	}
	else
	{
		fileList = strClassPath.c_str();
	}

	
	if (jarList != NULL)
	{
		fileList += jarList;
	}
	
	JavaVMInitArgs vm_args;
	JavaVMOption options[5];
	ZeroMemory(options, sizeof(options)); //<sonmi01>2013-11-26 ###???

	JVMSettings jvmSettings = {0};
	GetJVMSettings(jvmSettings);

	CHAR HeapSize[64] = {0};
	CHAR MaxPermSize[64] = {0};

	sprintf_s(HeapSize, "-Xmx%dm", jvmSettings.HeapSize);
	sprintf_s(MaxPermSize, "-XX:MaxPermSize=%dm", jvmSettings.MaxPermSize);

	options[0].optionString = HeapSize;//"-Xmx128m"; 
	options[1].optionString = MaxPermSize; //"-XX:MaxPermSize=64m";
	options[2].optionString = "-XX:+HeapDumpOnOutOfMemoryError";
	options[3].optionString = "-XX:HeapDumpPath=c:\\";
	options[4].optionString = fileList.GetBuffer(); //<sonmi01>2014-9-29 ###???

	vm_args.version = JNI_VERSION_1_6; //JDK version. This indicates version 1.6
	vm_args.nOptions = 5;
	vm_args.options = options;
	vm_args.ignoreUnrecognized = 0;

#ifdef __OLD_JNI_ENV__
	;
#else
	JNIEnv * env = nullptr;
#endif

	int ret = JNI_CreateJavaVM(jvm, (void**)&env, &vm_args);
	if(ret < 0)
		log.writeDebugLog(_T("Unable to Launch JVM. JVM may be running."));
	return env;

}

//#else 

// for debug java code
JNIEnv* NativeClass::create_vm_debug(JavaVM ** jvm, const char* jarList, BOOL bOldVer) {

	V2PNFlog log;
	log.writeDebugLog(_T("Attempting to create JVM."));

	CStringA fileList = "";
	string strJrePath = "";
	string strClassPath = "";
	if ( 0!=GetJREEnvironmentVariables("V2PNativeFacade", strJrePath, strClassPath ) || strClassPath.empty() )
	{
		fileList = "-Djava.class.path="; //<sonmi01>2014-9-29 ###???
	
		//add by zhepa02 at 2015-05-04 to verify the current operation is bmr or hbbu
		BOOL bWinPE = false;
		HKEY hKey;
		//add end

		static LPSTR new_jars[] =
		{
			"vmwaremanager.jar;",
			"axis.jar;",
			"commons-discovery-0.2.jar;",
			"commons-logging-1.0.4.jar;",
			"jaxrpc.jar;",
			"log4j-1.2.8.jar;",
			"saaj.jar;",
			"vim25.jar;",
			"wsdl4j-1.5.1.jar;",
			"vcloudmanager.jar;",
			"log4j-1.2.15.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/vcloud-java-sdk-5.5.0.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/rest-api-schemas-5.5.0.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/amqp-client-2.8.6.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/commons-codec-1.6.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/commons-logging-1.1.1.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/httpclient-4.2.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/httpcore-4.2.jar;"
		};


		static LPSTR old_jars[] =
		{
			"VmwareManager_AXIS.jar;",
			"axis.jar;",
			"commons-discovery-0.2.jar;",
			"commons-logging-1.0.4.jar;",
			"jaxrpc.jar;",
			"log4j-1.2.8.jar;",
			"saaj.jar;",
			"vim25_AXIS.jar;",
			"wsdl4j-1.5.1.jar;",
			"vcloudmanager.jar;",
			"log4j-1.2.15.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/vcloud-java-sdk-5.5.0.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/rest-api-schemas-5.5.0.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/amqp-client-2.8.6.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/commons-codec-1.6.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/commons-logging-1.1.1.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/httpclient-4.2.jar;",
			"VMwarevCloudDirectorSDK/5.5.0/httpcore-4.2.jar;"
		};

		//////////////////////////////////////////////////////////////////////////
		char szPath[MAX_PATH] = "";
		::GetModuleFileNameA(NULL, szPath, sizeof(szPath)); // could be: C:\Program Files\CA\arcserve Unified Data Protection\Management\BIN\VixGetVolumeDetails.exe

		//add by zhepa02 at 2015-05-05 , verify the current OS
		if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, WIN_PE, 0, KEY_READ, &hKey) == ERROR_SUCCESS)
		{
			bWinPE = TRUE;
		}
		//add end

		char* pb = strrchr(szPath, '\\');
		*pb = '\0';		// trancate to: C:\Program Files\CA\arcserve Unified Data Protection\Management\BIN
		pb = strrchr(szPath, '\\');
		*pb = '\0';		// trancate to: C:\Program Files\CA\arcserve Unified Data Protection\Management
		pb = strrchr(szPath, '\\');
		*pb = '\0';		// trancate to: C:\Program Files\CA\arcserve Unified Data Protection

		//add by zhepa02 at 2015-05-04 to get the correct path of the jars for the current operation
		if (!bWinPE)
			strncat_s(szPath, sizeof(szPath), "\\Engine\\BIN\\", _TRUNCATE);
		else
			strncat_s(szPath, sizeof(szPath), "\\system32\\dr\\", _TRUNCATE);
		//add end

		if (!bOldVer)
		{
			for (int ii = 0; ii < _countof(new_jars); ++ii)
			{
				fileList += szPath;
				fileList += new_jars[ii];
			}
		}
		else
		{
			for (int ii = 0; ii < _countof(old_jars); ++ii)
			{
				fileList += szPath;
				fileList += old_jars[ii];
			}
		}
	}
	else
	{
		fileList = strClassPath.c_str();
	}
	
	// This is for monitoring JVM inside AFBackend.exe. Do not uncomment it if there are multiple AFBackend.exe running in parallel
	// Otherwise only one JVM can run becase share all share port 8086
	//	options[0].optionString = "-Dcom.sun.management.jmxremote";
	//	options[1].optionString = "-Dcom.sun.management.jmxremote.port=8086";
	//	options[2].optionString = "-Dcom.sun.management.jmxremote.ssl=false";
	//	options[3].optionString = "-Dcom.sun.management.jmxremote.authenticate=false";
	if (jarList != NULL)
	{
		fileList += jarList;
	}

	JavaVMInitArgs vm_args;
	JavaVMOption options[7];
	ZeroMemory(options, sizeof(options)); //<sonmi01>2013-11-26 ###???

	JVMSettings jvmSettings = {0};
	GetJVMSettings(jvmSettings);

	CHAR HeapSize[64] = {0};
	CHAR MaxPermSize[64] = {0};

	sprintf_s(HeapSize, "-Xmx%dm", jvmSettings.HeapSize);
	sprintf_s(MaxPermSize, "-XX:MaxPermSize=%dm", jvmSettings.MaxPermSize);

	options[0].optionString = HeapSize;//"-Xmx128m"; 
	options[1].optionString = MaxPermSize; //"-XX:MaxPermSize=64m";
	options[2].optionString = "-XX:+HeapDumpOnOutOfMemoryError";
	options[3].optionString = "-XX:HeapDumpPath=c:\\";
	options[4].optionString = fileList.GetBuffer(); //<sonmi01>2014-9-29 ###???
	options[5].optionString = "-Xdebug";
	options[6].optionString = "-Xrunjdwp:transport=dt_socket,address=25600,server=y,suspend=y";

	vm_args.version = JNI_VERSION_1_6; //JDK version. This indicates version 1.6
	vm_args.nOptions = 7;
	vm_args.options = options;
	vm_args.ignoreUnrecognized = 0;

#ifdef __OLD_JNI_ENV__
	;
#else
	JNIEnv * env = nullptr;
#endif

	int ret = JNI_CreateJavaVM(jvm, (void**)&env, &vm_args);
	if(ret < 0)
		log.writeDebugLog(_T("Unable to Launch JVM. JVM may be running."));   	
	return env;

}

//#endif

NativeClass::NativeClass()
{
	jvm = NULL;
	jvcloudInstance = NULL;


#ifdef __OLD_JNI_ENV__
	env = NULL;
#else
	;
#endif

	

	memset(szserverName, 0,sizeof(szserverName));
	memset(szuserName, 0, sizeof(szuserName));
	memset(szPassword, 0, sizeof(szPassword));
	memset(szPro, 0, sizeof(szPro));
	Port = 0;
	bCertificate = FALSE;
	esxDetails = NULL;
	esxDS = NULL;
	vmList = NULL;
	vmInf = NULL;
	dsInf = NULL;
	diskList = NULL;
	adrDiskList = NULL;
	memset(snapShot_URL, 0, sizeof(snapShot_URL));
	memset(vmMoref, 0, sizeof(vmMoref));
	memset(snapshotCTF, 0, sizeof(snapshotCTF));
	memset(serverVersion, 0, sizeof(serverVersion));
	memset(parentSnapshot, 0, sizeof(parentSnapshot));
	memset(diskURL, 0, sizeof(diskURL));
	memset(nvramFile, 0, sizeof(nvramFile));
	b_Attached = FALSE;
	objectInstance = NULL;
}

NativeClass::~NativeClass()
{
	if(esxDetails != NULL)
	{
		delete [] esxDetails;
		esxDetails = NULL;
	}
	if(esxDS != NULL)
	{
		delete [] esxDS;
		esxDS = NULL;
	}
	if(vmList != NULL)
	{
		delete [] vmList;
		vmList = NULL;
	}
	if(diskList != NULL)
	{
		delete [] diskList;
		diskList = NULL;
	}
	if(adrDiskList != NULL)
	{
		delete [] adrDiskList;
		adrDiskList = NULL;
	}
	if(vmInf != NULL)
	{
		delete vmInf;
		vmInf = NULL;
	}
	if (dsInf != NULL)
	{
		delete dsInf;
		dsInf = NULL;
	}
	/// VIX is replaced by vSphere  -- begin --
	ClearAllVMResult();
	/// VIX is replaced by vSphere  -- end --
}

BOOL NativeClass::LibInit(const char* jarList, BOOL bOldVer)
{

#ifdef __OLD_JNI_ENV__
	;
#else
	JNIEnv * env = nullptr;
#endif


	JVMSettings jvmSettings = {0};
	GetJVMSettings(jvmSettings);
	if (jvmSettings.Debug)
	{
		env = create_vm_debug(&jvm, jarList, bOldVer);
	}
	else
	{
		env = create_vm(&jvm, jarList, bOldVer);
	}
	
	V2PNFlog log;
	if(env == NULL)
	{
		log.writeDebugLog(_T("Failed to create JVM."));
		return FALSE;
	}
	else
		return TRUE;
}

void NativeClass::LibExit()
{
	if (jvm!=NULL)
		jvm->DestroyJavaVM();
	jvm = NULL;
}



void NativeClass::SetContextClassLoader(JNIEnv* env)
{
	V2PNFlog log;
	//We're going to upgrade JRE 7 to JRE 8, as you know when C++ use JNI AttachCurrentThread attach JVM in multiple threads may have problem.
	// You can use following way to solve the problem :
	jclass clsV2P = NULL;
	jclass clsThread = NULL;
	jclass clsClass = NULL;
	jobject currentThread = NULL;
	jobject contextClassLoader = NULL;
	
	do 
	{
		// Following code is used to get current thread and set context class loader to current thread

		// Thread	
		clsThread = env->FindClass("java/lang/Thread");
		if (!clsThread)
		{
			log.writeDebugLog(L"%s: Fail to find class [java/lang/Thread]", _TEXT(__FUNCTION__));
			break;
		}

		// currentThread()
		jmethodID midcurrentThread = env->GetStaticMethodID(clsThread, "currentThread", "()Ljava/lang/Thread;");
		if (!midcurrentThread) 
		{
			log.writeDebugLog(L"%s: Cannot get method ID for currentThread", _TEXT(__FUNCTION__));
			break;
		}

		// Thread.currentThread()
		currentThread = env->CallStaticObjectMethod(clsThread, midcurrentThread);
		if (!currentThread) 
		{
			log.writeDebugLog(L"%s: Fail to get currentThread", _TEXT(__FUNCTION__));
			break;
		}

		// getContextClassLoader()
		jmethodID midgetContextClassLoader = env->GetMethodID(clsThread, "getContextClassLoader", "()Ljava/lang/ClassLoader;");
		if (!midgetContextClassLoader) 
		{
			log.writeDebugLog(L"%s: Cannot get method ID for getContextClassLoader", _TEXT(__FUNCTION__));
			break;
		}

		// Thread.currentThread().getContextClassLoader()
		contextClassLoader = env->CallObjectMethod(currentThread, midgetContextClassLoader);

		// Only need set when the contextClassloader is NULL, in most of the time 
		// the context thread in Main thread is Ext/APP class loader
		if (!contextClassLoader)
		{
			// Class
			clsClass = env->FindClass("java/lang/Class");
			if (!clsClass)
			{
				log.writeDebugLog(L"%s: Fail to find class [java/lang/Class]", _TEXT(__FUNCTION__));
				break;
			}

			// getClassLoader()
			jmethodID midgetClassLoader = env->GetMethodID(clsClass, "getClassLoader", "()Ljava/lang/ClassLoader;");
			if (!midgetClassLoader)
			{
				log.writeDebugLog(L"%s: Cannot get method ID for getClassLoader", _TEXT(__FUNCTION__));
				break;
			}

			// First get class loader from the class which you're going to call.
			// Most of the time this will get Ext/APP class loader.

			// V2P_Export
			jclass clsV2P = env->FindClass("com/ca/arcflash/ha/vmwaremanager/V2P_Export");

			// V2P_Export.getClassLoader()
			jobject currentClassloader = env->CallObjectMethod(clsV2P, midgetClassLoader);
			if (!currentClassloader) 
			{
				log.writeDebugLog(L"%s: Fail to currentClassloader", _TEXT(__FUNCTION__));
				break;
			}

			// setContextClassLoader()
			jmethodID midsetContextClassLoader = env->GetMethodID(clsThread, "setContextClassLoader", "(Ljava/lang/ClassLoader;)V");
			if (!midsetContextClassLoader) 
			{
				log.writeDebugLog(L"%s: Cannot get method ID for setContextClassLoader", _TEXT(__FUNCTION__));
				break;
			}

			// Thread.currentThread.setContextClassLoader()
			env->CallVoidMethod(currentThread, midsetContextClassLoader, currentClassloader);
		}

	} while (FALSE);
	
	SafeDeleteLocalRef(env, clsThread);
	SafeDeleteLocalRef(env, clsV2P);
	SafeDeleteLocalRef(env, currentThread);
	SafeDeleteLocalRef(env, contextClassLoader);
	SafeDeleteLocalRef(env, clsClass);
	
}

#ifdef __OLD_JNI_ENV__
BOOL NativeClass::AttachToJVM()
{
	jint jRet = 0;
	b_Attached = FALSE;
	V2PNFlog log;

	jRet = jvm->GetEnv((void**) &env, JNI_VERSION_1_6);

	if((jRet != JNI_OK) || (env == NULL))
	{
		jint jRes = jvm->AttachCurrentThread((void **)&env, NULL);
		if(jRes != JNI_OK)
		{
			log.writeDebugLog(_T("Failed to attach to JVM."));
			return FALSE;
		}
		else
		{
			b_Attached = TRUE;
			return TRUE;
		}
	}
	else
	{
		//b_Attached = TRUE; //<sonmi01>2015-7-27 ###???
		return TRUE;
	}
}

#else

//<sonmi01>2015-9-9 ###???
JNIEnv * NativeClass::AttachToJVMThread()
{
	jint jRet = 0;
	V2PNFlog log;
	JNIEnv * envlocal = nullptr;
	jRet = jvm->GetEnv((void**)&envlocal, JNI_VERSION_1_6);

	if ((jRet != JNI_OK) || (envlocal == NULL))
	{
		jint jRes = jvm->AttachCurrentThread((void **)&envlocal, NULL);
		if (jRes != JNI_OK)
		{
			log.writeDebugLog(_T("Failed to attach to JVM."));
		}
	}

	SetContextClassLoader(envlocal);

	return envlocal;
}
#endif

void NativeClass::DetachJVM()
{
	if (!V2PNFGlobals::Get_isSharedJVM()) //<sonmi01>2015-9-6 ###???
	{
		if (b_Attached)
		{
			//<sonmi01>2015-7-24 ###???
			//jvm will be detached when DLL_THREAD_DETACH
				//many attach/detach cause high CPU and resource leak or jvm.dll crash (jvm is not stable enough)
				//jvm->DetachCurrentThread();
		}
	}
}


int NativeClass::connectToESX(WCHAR* esxServer, WCHAR* esxUser, WCHAR* esxPwd, WCHAR* esxPro, bool bIgnoreCert, long lPort)
{
	jclass clsV2P = NULL;
	jobject resultL;
	jobject resultB;
	jmethodID midconstructor = NULL;
	jobject dataObject = NULL;
	char* buff = NULL;
	string aesxServer;
	string aesxUserName;
	string aesxPassword;
	string aesxPro;
	jstring StringEsxName;
	jstring StringEsxUser;
	jstring StringEsxPwd;
	jstring StringEsxPro;
	int result = 1;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	jfieldID fieldConn = NULL;
	V2PNFlog log;

	wcscpy_s(szserverName, PATH_SIZE, esxServer);
	wcscpy_s(szuserName, PATH_SIZE, esxUser);
	wcscpy_s(szPassword, PATH_SIZE, esxPwd);
	wcscpy_s(szPro, PATH_SIZE, esxPro);
	bCertificate = bIgnoreCert;
	Port = lPort;

	//log.writeDebugLog(_T("Attempting to get filestream of file %ls of VM %ls."), fileName, vmName);
	log.writeDebugLog(_T("Attempting to connect to esx server %ls."), szserverName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return result;
	}

	clsV2P = env->FindClass("com/ca/arcflash/ha/vmwaremanager/V2P_Export");

	if(clsV2P != NULL)
	{
		midconstructor = env->GetMethodID(clsV2P,"<init>","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Boolean;Ljava/lang/Long;)V");
	}
	else
	{
		log.writeDebugLog(_T("Failed to get the class."));
	}

	if(midconstructor != NULL)
	{
		UnicodeToUTF8(szserverName, aesxServer);
		UnicodeToUTF8(szuserName, aesxUserName);
		UnicodeToUTF8(szPassword, aesxPassword);
		UnicodeToUTF8(szPro, aesxPro);
		
		StringEsxName = env->NewStringUTF(aesxServer.c_str());
		StringEsxUser = env->NewStringUTF(aesxUserName.c_str());
		StringEsxPwd = env->NewStringUTF(aesxPassword.c_str());
		StringEsxPro = env->NewStringUTF(aesxPro.c_str());
		resultL = _makeLong(env, Port);
		resultB = _makeBoolean(env, bCertificate);
		dataObject = env->NewObject(clsV2P, midconstructor,StringEsxName,StringEsxUser,StringEsxPwd,StringEsxPro,resultB,resultL);
		if(dataObject != NULL)
		{
			objectInstance = dataObject;
			clsV2P = env->GetObjectClass(objectInstance);
			fieldConn = env->GetFieldID(clsV2P,"connected","I");

			if(fieldConn != NULL)
			{
				result = (int)env->GetIntField(dataObject, fieldConn);
				
			}
		}
		else
		{
			objectInstance = NULL;
			result = 1;
			log.writeDebugLog(_T("Failed to get the V2P_Export class instance."));
		}

	}
	return result;
}

int NativeClass::getVMServerType()
{
	jclass clsV2P = NULL;
	jmethodID midGetServerType = NULL;
	int result = 0;
	V2PNFlog log;
	
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif


	log.writeDebugLog(_T("Attempting to get VM server Type."));

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	
	if (clsV2P != NULL)
	{
		midGetServerType  = env->GetMethodID(clsV2P,"getVMServerType","()I");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetServerType != NULL)
	{
		jint bResult = (jint) env->CallObjectMethod(objectInstance,midGetServerType);

		if(bResult == 2)
		{
			result = 2;
		}
		else if(bResult == 1)
		{
			result = 1;
		}
		else
		{
			result = 0;
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return result;
}


//<sonmi01>2014-1-9 #87330: With upgrade to VDDK 5.5, we need to Check permissions when importing VM from VC
int NativeClass::hasSufficientPermission()
{
	jclass clsV2P = NULL;
	jmethodID midhasSufficientPermission = NULL;
	BOOL result = FALSE;
	V2PNFlog log;

#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

	log.writeDebugLog(_T("Attempting to get VM server Type."));

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		midhasSufficientPermission  = env->GetMethodID(clsV2P,"hasSufficientPermission","()Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midhasSufficientPermission != NULL)
	{
		jboolean bResult = (jboolean) env->CallObjectMethod(objectInstance, midhasSufficientPermission);

		if (bResult)
		{
			result = TRUE;
		}
		else
		{
			result = FALSE;
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return result;
}

VMDataStoreInfo*  NativeClass::getVMDataStoreDetails(ESXNode esxNode, WCHAR* dataStore)
{
	jclass clsV2P = NULL;
	jclass clsDsInfo = NULL;
	jobject jObject = NULL;
	jmethodID midGetDSInfo = NULL;
	jfieldID fieldvendorName = NULL;
	jfieldID fieldwwnID = NULL;
	jfieldID fieldhbaID = NULL;
	jfieldID fielddiskID = NULL;
	jfieldID fieldsrvID = NULL;
	jfieldID fielddsType = NULL;
	jfieldID fielddsGUID = NULL;
	jfieldID fieldlunNumber = NULL;
	jfieldID fieldisExternalStorage = NULL;
	string esxHost;
	string esxDC;
	string adatastore;
	int blockSize = 0;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;
	log.writeDebugLog(_T("Attempting to get details for datastore %ls."), dataStore);
	if (dsInf != NULL)
		delete(dsInf);
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}
	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	clsDsInfo = env->FindClass("com/ca/arcflash/ha/vmwaremanager/DatastoreConfig");
	if (clsV2P != NULL && clsDsInfo != NULL)
	{
		midGetDSInfo = env->GetMethodID(clsV2P, "getDataStoreDetails", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/ca/arcflash/ha/vmwaremanager/DatastoreConfig;");
		fieldvendorName = env->GetFieldID(clsDsInfo, "vendorName", "Ljava/lang/String;");
		fieldwwnID = env->GetFieldID(clsDsInfo, "wwnID", "Ljava/lang/String;");
		fieldhbaID = env->GetFieldID(clsDsInfo, "hbaAdapterID", "Ljava/lang/String;");
		fielddiskID = env->GetFieldID(clsDsInfo, "diskName", "Ljava/lang/String;");
		fieldsrvID = env->GetFieldID(clsDsInfo, "srvAddress", "Ljava/lang/String;");
		fielddsType = env->GetFieldID(clsDsInfo, "dsType", "Ljava/lang/String;");
		fielddsGUID = env->GetFieldID(clsDsInfo, "dsGUID", "Ljava/lang/String;");
		fieldlunNumber = env->GetFieldID(clsDsInfo, "lunNumber", "I");
		fieldisExternalStorage = env->GetFieldID(clsDsInfo, "isExternalStorage", "Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class"));
		goto invokejavavirtualmachine_error;
	}
	if (midGetDSInfo != NULL)
	{
		UnicodeToUTF8(esxNode.esxName, esxHost);
		UnicodeToUTF8(esxNode.dcName, esxDC);
		UnicodeToUTF8(dataStore, adatastore);
		jstring StringEsxHost = env->NewStringUTF(esxHost.c_str());
		jstring StringEsxDC = env->NewStringUTF(esxDC.c_str());
		jstring StringrdatStore = env->NewStringUTF(adatastore.c_str());
		jObject = env->CallObjectMethod(objectInstance, midGetDSInfo, StringEsxHost, StringEsxDC, StringrdatStore);
		if (jObject)
		{
			jboolean isDSInfo = env->IsInstanceOf(jObject, clsDsInfo);
			if (!isDSInfo)
			{
				log.writeDebugLog(_T("This is not a DS object."));
				goto invokejavavirtualmachine_error;
			}
			dsInf = new VMDataStoreInfo;
			if (NULL == dsInf)
			{
				goto invokejavavirtualmachine_error;
			}
			ZeroMemory(dsInf, sizeof(VMDataStoreInfo));
			jstring vendorName = (jstring)env->GetObjectField(jObject, fieldvendorName);
			jstring wwnID = (jstring)env->GetObjectField(jObject, fieldwwnID);
			jstring hbaID = (jstring)env->GetObjectField(jObject, fieldhbaID);
			jstring diskID = (jstring)env->GetObjectField(jObject, fielddiskID);
			jstring srvID = (jstring)env->GetObjectField(jObject, fieldsrvID);
			jstring dsType = (jstring)env->GetObjectField(jObject, fielddsType);
			jstring dsGUID = (jstring)env->GetObjectField(jObject, fielddsGUID);
			jint lunNumber = (jint)env->GetIntField(jObject, fieldlunNumber);
			jboolean isExternalStorage = (jboolean)env->GetBooleanField(jObject, fieldisExternalStorage);
			jboolean isVndrName = FALSE, iswwnID = FALSE, ishbaID = FALSE, isdiskID = FALSE, issrvID = FALSE, isdsType = FALSE, isdsGUID = FALSE;
			const jchar *strdsType = env->GetStringChars(dsType, &isdsType);
			const jchar *strvndrName = env->GetStringChars(vendorName, &isVndrName);
			const jchar *strwwnID = env->GetStringChars(wwnID, &iswwnID);
			const jchar *strhbaID = env->GetStringChars(hbaID, &ishbaID);
			const jchar *strdiskID = env->GetStringChars(diskID, &isdiskID);
			const jchar *strsrvID = env->GetStringChars(srvID, &issrvID);
			const jchar *strdsGUID = env->GetStringChars(dsGUID, &isdsGUID);
			if (isVndrName && iswwnID && ishbaID && isdiskID && issrvID && isdsType && isdsGUID)
			{
				wcscpy_s(dsInf->m_vendorName, NAME_SIZE, (const WCHAR *)strvndrName);
				wcscpy_s(dsInf->m_wwnID, NAME_SIZE, (const WCHAR *)strwwnID);
				wcscpy_s(dsInf->m_hbaAdapterID, NAME_SIZE, (const WCHAR *)strhbaID);
				wcscpy_s(dsInf->m_diskName, NAME_SIZE, (const WCHAR *)strdiskID);
				wcscpy_s(dsInf->m_srvAddress, NAME_SIZE, (const WCHAR *)strsrvID);
				wcscpy_s(dsInf->m_dsType, NAME_SIZE, (const WCHAR *)strdsType);
				wcscpy_s(dsInf->m_dsGUID, NAME_SIZE, (const WCHAR *)strdsGUID);
				dsInf->isExternalStorage = isExternalStorage;
				if (isExternalStorage)
					log.writeDebugLog(_T("Datastore %s is external storage."), dsInf->m_dsGUID);
				else
					log.writeDebugLog(_T("Datastore %s is internal storage."), dsInf->m_dsGUID);
				dsInf->lunNumber = lunNumber;
				wcscpy_s(dsInf->m_dataStoreName, NAME_SIZE, dataStore);
			}
			env->ReleaseStringChars(vendorName, strvndrName);
			env->ReleaseStringChars(wwnID, strwwnID);
			env->ReleaseStringChars(hbaID, strhbaID);
			env->ReleaseStringChars(diskID, strdiskID);
			env->ReleaseStringChars(srvID, strsrvID);
			env->ReleaseStringChars(dsType, strdsType);
			env->ReleaseStringChars(dsType, strdsGUID);
		}
		else
		{
			dsInf = NULL;
			log.writeDebugLog(_T("Failed to query block size of the datstore."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}
invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return dsInf;
}
void NativeClass::rescanallHBA(ESXNode nodeDetails, BOOL rescanVC)
{
	jclass clsV2P = NULL;
	jmethodID midRescanHBA = NULL;
	V2PNFlog log;
	string aesxServer;
	string aesxDC;
	jstring StringEsxName;
	jstring StringEsxDC;
	jobject resultB = NULL;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	log.writeDebugLog(_T("Attempting to rescan HBA of the hosts."));
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return;
	}
	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	if (clsV2P != NULL)
	{
		midRescanHBA = env->GetMethodID(clsV2P, "rescanAllHBA", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Boolean;)V");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class."));
		goto invokejavavirtualmachine_error;
	}
	if (midRescanHBA != NULL)
	{
		resultB = _makeBoolean(env, rescanVC);
		UnicodeToUTF8(nodeDetails.esxName, aesxServer);
		UnicodeToUTF8(nodeDetails.dcName, aesxDC);
		StringEsxName = env->NewStringUTF(aesxServer.c_str());
		StringEsxDC = env->NewStringUTF(aesxDC.c_str());
		env->CallObjectMethod(objectInstance, midRescanHBA, StringEsxName, StringEsxDC, resultB);
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}
invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return;
}
WCHAR* NativeClass::addCloneDataStore(ESXNode nodeDetails, WCHAR* dsGUID)
{
#ifdef __OLD_JNI_ENV__
	;
#else
	BOOL bRet = FALSE;
#endif

	jclass clsV2P = NULL;
	jmethodID midCloneDS = NULL;
	V2PNFlog log;
	string aesxServer;
	string aesxDC;
	string adsGUID;
	jstring StringEsxName;
	jstring StringEsxDC;
	jstring StringdsGUID;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	log.writeDebugLog(_T("Attempting to mount clone of datastore whose GUID is %s on the ESX server %s."), dsGUID, nodeDetails.esxName);
	memset(cloneDS, 0, sizeof(cloneDS));
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}
	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	if (clsV2P != NULL)
	{
		midCloneDS = env->GetMethodID(clsV2P, "addCloneDataStore", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class."));
		goto invokejavavirtualmachine_error;
	}
	if (midCloneDS != NULL)
	{
		UnicodeToUTF8(nodeDetails.esxName, aesxServer);
		UnicodeToUTF8(nodeDetails.dcName, aesxDC);
		UnicodeToUTF8(dsGUID, adsGUID);
		StringEsxName = env->NewStringUTF(aesxServer.c_str());
		StringEsxDC = env->NewStringUTF(aesxDC.c_str());
		StringdsGUID = env->NewStringUTF(adsGUID.c_str());
		jstring tempObj = (jstring)env->CallObjectMethod(objectInstance, midCloneDS, StringEsxName, StringEsxDC, StringdsGUID);
		if (tempObj != NULL)
		{
			jboolean isObj = FALSE;
			const jchar *strnewDS = env->GetStringChars(tempObj, &isObj);
			if (isObj)
			{
				wcscpy_s(cloneDS, PATH_SIZE, (const WCHAR *)strnewDS);
				bRet = true;
			}
			env->ReleaseStringChars(tempObj, strnewDS);
		}
		else
		{
			log.writeDebugLog(_T("addCloneDataStore API returned NULL for ds guid %s : %s."), nodeDetails.esxName, dsGUID);
			bRet = false;
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}
invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	if (bRet)
		return cloneDS;
	else
		return NULL;

}
BOOL NativeClass::destroyandDeleteClone(ESXNode nodeDetails, WCHAR* dsName)
{
	jclass clsV2P = NULL;
	jmethodID midDelCloneDS = NULL;
	V2PNFlog log;
	string aesxServer;
	string aesxDC;
	string adsNAME;
	jstring StringEsxName;
	jstring StringEsxDC;
	jstring StringdsName;
	BOOL isLunDestroyed = FALSE;
#ifdef __OLD_JNI_ENV__
	;
#else
	BOOL bRet = FALSE;
#endif
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	log.writeDebugLog(_T("Attempting to destroy clone datastore."));
	memset(cloneDS, 0, sizeof(cloneDS));
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}
	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	if (clsV2P != NULL)
	{
		midDelCloneDS = env->GetMethodID(clsV2P, "UnmountDatastore", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class."));
		goto invokejavavirtualmachine_error;
	}
	if (midDelCloneDS != NULL)
	{
		bRet = FALSE;
		UnicodeToUTF8(nodeDetails.esxName, aesxServer);
		UnicodeToUTF8(nodeDetails.dcName, aesxDC);
		UnicodeToUTF8(dsName, adsNAME);
		StringEsxName = env->NewStringUTF(aesxServer.c_str());
		StringEsxDC = env->NewStringUTF(aesxDC.c_str());
		StringdsName = env->NewStringUTF(adsNAME.c_str());
		jboolean jobj = (jboolean)env->CallObjectMethod(objectInstance, midDelCloneDS, StringEsxName, StringEsxDC, StringdsName);
		if (jobj)
			isLunDestroyed = TRUE;
		if (!isLunDestroyed)
		{
			log.writeDebugLog(_T("Failed to delete clone data store."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}
invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return isLunDestroyed;
}
WCHAR* NativeClass::createApplianceVM(ESXNode nodeDetails, WCHAR* vmName)
{
	jclass clsV2P = NULL;
	jmethodID midCreateAppVM = NULL;
	V2PNFlog log;
	string aesxServer;
	string aesxDC;
	string avmName;
	jstring StringEsxName;
	jstring StringEsxDC;
	jstring StringVmName;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	log.writeDebugLog(_T("Attempting to create appliance VM."));
	memset(vmMoref, 0, sizeof(vmMoref));
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}
	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	if (clsV2P != NULL)
	{
		midCreateAppVM = env->GetMethodID(clsV2P, "createApplianceVM", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class."));
		goto invokejavavirtualmachine_error;
	}
	if (midCreateAppVM != NULL)
	{
		UnicodeToUTF8(nodeDetails.esxName, aesxServer);
		UnicodeToUTF8(nodeDetails.dcName, aesxDC);
		UnicodeToUTF8(vmName, avmName);
		StringEsxName = env->NewStringUTF(aesxServer.c_str());
		StringEsxDC = env->NewStringUTF(aesxDC.c_str());
		StringVmName = env->NewStringUTF(avmName.c_str());
		jstring tempObj = (jstring)env->CallObjectMethod(objectInstance, midCreateAppVM, StringEsxName, StringEsxDC, StringVmName);
		if (tempObj != NULL)
		{
			jboolean isObj = FALSE;
			const jchar *strVM = env->GetStringChars(tempObj, &isObj);
			if (isObj)
			{
				wcscpy_s(vmMoref, PATH_SIZE, (const WCHAR *)strVM);
			}
			env->ReleaseStringChars(tempObj, strVM);
		}
		else
		{
			log.writeDebugLog(_T("Create Appliance VM API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}
invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return vmMoref;
}
WCHAR* NativeClass::createStndAloneApplianceVM(ESXNode nodeDetails)
{
	jclass clsV2P = NULL;
	jmethodID midCreateAppVM = NULL;
	V2PNFlog log;
	string aesxServer;
	string aesxDC;
	jstring StringEsxName;
	jstring StringEsxDC;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	log.writeDebugLog(_T("Attempting to create standalone appliance VM."));
	memset(vmMoref, 0, sizeof(vmMoref));
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}
	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	if (clsV2P != NULL)
	{
		midCreateAppVM = env->GetMethodID(clsV2P, "createApplianceStandAloneVM", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class."));
		goto invokejavavirtualmachine_error;
	}
	if (midCreateAppVM != NULL)
	{
		UnicodeToUTF8(nodeDetails.esxName, aesxServer);
		UnicodeToUTF8(nodeDetails.dcName, aesxDC);
		StringEsxName = env->NewStringUTF(aesxServer.c_str());
		StringEsxDC = env->NewStringUTF(aesxDC.c_str());
		jstring tempObj = (jstring)env->CallObjectMethod(objectInstance, midCreateAppVM, StringEsxName, StringEsxDC);
		if (tempObj != NULL)
		{
			jboolean isObj = FALSE;
			const jchar *strVM = env->GetStringChars(tempObj, &isObj);
			if (isObj)
			{
				wcscpy_s(vmMoref, PATH_SIZE, (const WCHAR *)strVM);
			}
			env->ReleaseStringChars(tempObj, strVM);
		}
		else
		{
			log.writeDebugLog(_T("Create standalone Appliance VM API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}
invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return vmMoref;
}
BOOL NativeClass::deleteApplianceVM(ESXNode nodeDetails, WCHAR* vmName)
{
	jclass clsV2P = NULL;
	jmethodID midCreateAppVM = NULL;
	V2PNFlog log;
	string aesxServer;
	string aesxDC;
	string avmName;
	jstring StringEsxName;
	jstring StringEsxDC;
	jstring StringVmName;
#ifdef __OLD_JNI_ENV__
	;
#else
	BOOL bRet = FALSE;
#endif
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	log.writeDebugLog(_T("Attempting to create appliance VM."));
	memset(vmMoref, 0, sizeof(vmMoref));
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}
	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	if (clsV2P != NULL)
	{
		midCreateAppVM = env->GetMethodID(clsV2P, "deleteApplianceVM", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class."));
		goto invokejavavirtualmachine_error;
	}
	if (midCreateAppVM != NULL)
	{
		UnicodeToUTF8(nodeDetails.esxName, aesxServer);
		UnicodeToUTF8(nodeDetails.dcName, aesxDC);
		UnicodeToUTF8(vmName, avmName);
		StringEsxName = env->NewStringUTF(aesxServer.c_str());
		StringEsxDC = env->NewStringUTF(aesxDC.c_str());
		StringVmName = env->NewStringUTF(avmName.c_str());
		jboolean tempObj = (jboolean)env->CallObjectMethod(objectInstance, midCreateAppVM, StringEsxName, StringEsxDC, StringVmName);
		if (tempObj != NULL)
		{
			if (tempObj)
				bRet = TRUE;
			else
				bRet = FALSE;
		}
		else
		{
			log.writeDebugLog(_T("Create Appliance VM API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}
invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return bRet;
}

WCHAR* NativeClass::attachDiskToVM(WCHAR* vmName, WCHAR* diskURL, WCHAR* esxName, WCHAR* diskType)
{
	jclass clsV2P = NULL;
	jmethodID midAttachDiskToVM = NULL;
	V2PNFlog log;
	string avmName;
	string adiskURL;
	string acntrlrType;
	string aesxServer;
	string adiskType;
	jstring StringvmName;
	jstring StringdiskURL;
	jstring StringEsxName;
	jstring StringDiskType;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	BOOL bError = FALSE;
	log.writeDebugLog(_T("Attempting to attach disk %s to appliance VM."), diskURL);
	memset(vmMoref, 0, sizeof(vmMoref));
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		bError = TRUE;
	}
	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		bError = TRUE;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	if (clsV2P != NULL)
	{
		midAttachDiskToVM = env->GetMethodID(clsV2P, "attachVirtualDisk", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class."));
		bError = TRUE;
		goto invokejavavirtualmachine_error;
	}
	if (midAttachDiskToVM != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(diskURL, adiskURL);
		UnicodeToUTF8(esxName, aesxServer);
		UnicodeToUTF8(diskType, adiskType);
		StringvmName = env->NewStringUTF(avmName.c_str());
		StringdiskURL = env->NewStringUTF(adiskURL.c_str());
		StringEsxName = env->NewStringUTF(aesxServer.c_str());
		StringDiskType = env->NewStringUTF(adiskType.c_str());
		jstring tempObj = (jstring)env->CallObjectMethod(objectInstance, midAttachDiskToVM, StringvmName, StringdiskURL, StringEsxName, StringDiskType);
		if (tempObj != NULL)
		{
			jboolean isObj = FALSE;
			const jchar *strVM = env->GetStringChars(tempObj, &isObj);
			if (isObj)
			{
				wcscpy_s(vmMoref, PATH_SIZE, (const WCHAR *)strVM);
			}
			env->ReleaseStringChars(tempObj, strVM);
		}
		else
		{
			log.writeDebugLog(_T("Attach disk API returned NULL."));
			bError = TRUE;
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
		bError = TRUE;
	}
invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	if (bError)
		return NULL;
	else
		return vmMoref;
}
WCHAR* NativeClass::detachDiskFromVM(WCHAR* vmName, WCHAR* diskURL)
{
	jclass clsV2P = NULL;
	jmethodID midDettachDiskFrmVM = NULL;
	V2PNFlog log;
	string avmName;
	string adiskURL;
	jstring StringvmName;
	jstring StringdiskURL;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	log.writeDebugLog(_T("Attempting to detach disks from VM."));
	memset(vmMoref, 0, sizeof(vmMoref));
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}
	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	if (clsV2P != NULL)
	{
		midDettachDiskFrmVM = env->GetMethodID(clsV2P, "detachVirtualDisk", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class."));
		goto invokejavavirtualmachine_error;
	}
	if (midDettachDiskFrmVM != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(diskURL, adiskURL);
		StringvmName = env->NewStringUTF(avmName.c_str());
		StringdiskURL = env->NewStringUTF(adiskURL.c_str());
		jstring tempObj = (jstring)env->CallObjectMethod(objectInstance, midDettachDiskFrmVM, StringvmName, StringdiskURL);
		if (tempObj != NULL)
		{
			jboolean isObj = FALSE;
			const jchar *strVM = env->GetStringChars(tempObj, &isObj);
			if (isObj)
			{
				wcscpy_s(vmMoref, PATH_SIZE, (const WCHAR *)strVM);
			}
			env->ReleaseStringChars(tempObj, strVM);
		}
		else
		{
			log.writeDebugLog(_T("detach disk from VM API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}
invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return vmMoref;
}
BOOL NativeClass::isESXinCluster(ESXNode nodeDetails)
{
	jclass clsV2P = NULL;
	jmethodID midisESXinCluster = NULL;
	V2PNFlog log;
	string aesxServer;
	string aesxDC;
	jstring StringEsxName;
	jstring StringEsxDC;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	BOOL result = FALSE;
	log.writeDebugLog(_T("Attempting to rescan HBA of the hosts."));
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}
	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	if (clsV2P != NULL)
	{
		midisESXinCluster = env->GetMethodID(clsV2P, "isESXinCluster", "(Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class."));
		goto invokejavavirtualmachine_error;
	}
	if (midisESXinCluster != NULL)
	{
		UnicodeToUTF8(nodeDetails.esxName, aesxServer);
		UnicodeToUTF8(nodeDetails.dcName, aesxDC);
		StringEsxName = env->NewStringUTF(aesxServer.c_str());
		StringEsxDC = env->NewStringUTF(aesxDC.c_str());
		jboolean bResult = (jboolean)env->CallObjectMethod(objectInstance, midisESXinCluster, StringEsxName, StringEsxDC);
		if (bResult)
		{
			result = TRUE;
		}
		else
		{
			result = FALSE;
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}
invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return result;
}

BOOL NativeClass::isESXunderVC(ESXNode nodeDetails)
{
	jclass clsV2P = NULL;
	jmethodID midisESXinVC = NULL;
	V2PNFlog log;
	string aesxServer;
	string aesxDC;
	jstring StringEsxName;
	jstring StringEsxDC;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	BOOL result = FALSE;
	log.writeDebugLog(_T("Attempting to rescan HBA of the hosts."));
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}
	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	if (clsV2P != NULL)
	{
		midisESXinVC = env->GetMethodID(clsV2P, "isESXPartofVC", "(Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class."));
		goto invokejavavirtualmachine_error;
	}
	if (midisESXinVC != NULL)
	{
		UnicodeToUTF8(nodeDetails.esxName, aesxServer);
		UnicodeToUTF8(nodeDetails.dcName, aesxDC);
		StringEsxName = env->NewStringUTF(aesxServer.c_str());
		StringEsxDC = env->NewStringUTF(aesxDC.c_str());
		jboolean bResult = (jboolean)env->CallObjectMethod(objectInstance, midisESXinVC, StringEsxName, StringEsxDC);
		if (bResult)
		{
			result = TRUE;
		}
		else
		{
			result = FALSE;
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}
invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return result;
}

WCHAR* NativeClass::checkandtakeApplianceSnapShot(WCHAR* vmName, WCHAR* snapshotName)
{
	jclass clsV2P = NULL;
	jmethodID midtakeSnapShot = NULL;
	jfieldID fieldESX = NULL;
	jfieldID fieldDC = NULL;
	string avmName;
	string asnapshotName;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to take snapshot of VM %ls."), vmName);
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}


	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midtakeSnapShot = env->GetMethodID(clsV2P, "checkandTakeAppSnapshot", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class"));
		goto invokejavavirtualmachine_error;
	}

	if (midtakeSnapShot != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(snapshotName, asnapshotName);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringsnapshotName = env->NewStringUTF(asnapshotName.c_str());

		wcscpy_s(snapShot_URL, PATH_SIZE, L"\0");
		jstring tempObj = (jstring)env->CallObjectMethod(objectInstance, midtakeSnapShot, StringvmName, StringsnapshotName);
		if (tempObj != NULL)
		{
			jboolean isObj = FALSE;
			const jchar *strDS = env->GetStringChars(tempObj, &isObj);
			if (isObj)
			{
				wcscpy_s(snapShot_URL, PATH_SIZE, (const WCHAR *)strDS);
			}
			env->ReleaseStringChars(tempObj, strDS);
		}
		else
		{
			log.writeDebugLog(_T("takeSnapShot API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}

invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return snapShot_URL;

}


int NativeClass::checkVMServerLicense(WCHAR* esxName, WCHAR* dcName)
{
	jclass clsV2P = NULL;
	jmethodID midGetServerType = NULL;
	int result = 0;
	V2PNFlog log;
	string aesxServer;
	string aesxDC;
	jstring StringEsxName;
	jstring StringEsxDC;

#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

	log.writeDebugLog(_T("Attempting to check VM server license."));

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		midGetServerType  = env->GetMethodID(clsV2P,"checkServerLicense","(Ljava/lang/String;Ljava/lang/String;)I");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetServerType != NULL)
	{
		UnicodeToUTF8(esxName, aesxServer);
		UnicodeToUTF8(dcName, aesxDC);

		StringEsxName = env->NewStringUTF(aesxServer.c_str());
		StringEsxDC = env->NewStringUTF(aesxDC.c_str());

		jint bResult = (jint) env->CallObjectMethod(objectInstance,midGetServerType, StringEsxName, StringEsxDC);

		if(bResult == 1)
		{
			result = 1;
		}
		else if(bResult == 0)
		{
			result = 0;
		}
		else
		{
			result = -1;
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return result;
}

BOOL NativeClass::checkVMServerInMaintainenceMode(WCHAR* esxName, WCHAR* dcName)
{
	jclass clsV2P = NULL;
	jmethodID midGetServerModeType = NULL;
	BOOL result = FALSE;
	V2PNFlog log;
	string aesxServer;
	string aesxDC;
	jstring StringEsxName;
	jstring StringEsxDC;

#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

	log.writeDebugLog(_T("Attempting to check VM server maintainenece mode."));

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		midGetServerModeType  = env->GetMethodID(clsV2P,"isServerinMaintaineneceMode","(Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetServerModeType != NULL)
	{
		UnicodeToUTF8(esxName, aesxServer);
		UnicodeToUTF8(dcName, aesxDC);

		StringEsxName = env->NewStringUTF(aesxServer.c_str());
		StringEsxDC = env->NewStringUTF(aesxDC.c_str());

		jboolean bResult = (jboolean) env->CallObjectMethod(objectInstance,midGetServerModeType, StringEsxName, StringEsxDC);

		if(bResult)
		{
			result = TRUE;
			log.writeDebugLog(_T("In maintainenece mode."));
		}
		else
		{
			result = FALSE;
			log.writeDebugLog(_T("Not in maintainenece mode."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return result;
}

WCHAR* NativeClass::getESXVersion(WCHAR* esxName, WCHAR* dcName)
{
	jclass clsV2P = NULL;
	jmethodID midGetServerType = NULL;
	int result = 0;
	V2PNFlog log;
	string aesxServer;
	string aesxDC;
	jstring StringEsxName;
	jstring StringEsxDC;

#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

	log.writeDebugLog(_T("Attempting to check VM server license."));

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		midGetServerType  = env->GetMethodID(clsV2P,"getESXHostVersion","(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetServerType != NULL)
	{
		UnicodeToUTF8(esxName, aesxServer);
		UnicodeToUTF8(dcName, aesxDC);

		StringEsxName = env->NewStringUTF(aesxServer.c_str());
		StringEsxDC = env->NewStringUTF(aesxDC.c_str());

		jstring tempObj = (jstring)env->CallObjectMethod(objectInstance,midGetServerType, StringEsxName, StringEsxDC);
		wcscpy_s(serverVersion,PATH_SIZE,L"\0");
		if(tempObj != NULL)
		{
			jboolean isObj = FALSE;
			const jchar *strDS = env->GetStringChars(tempObj, &isObj);
			if(isObj)
			{
				wcscpy_s(serverVersion,PATH_SIZE,(const WCHAR *)strDS);
			}
			env->ReleaseStringChars(tempObj, strDS);
		}
		else
		{
			log.writeDebugLog(_T("getESXHostVersion API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return serverVersion;
}

int NativeClass::getESXNumberOfProcessors(WCHAR* esxName, OUT UINT& numberOfLogicalProcessors, OUT UINT& numberOfProcessors)
{
	jclass clsV2P = NULL;
	jmethodID midGetServerPhysicalCPUSockets = NULL;
	jmethodID midGetServerLogicalCPUSockets = NULL;
	int result = 0;
	V2PNFlog log;
	string aesxServer;
	jstring StringEsxName;
	BOOL bRet = FALSE;

	do{	
		log.writeDebugLog(_T("Attempting to getESXNumberOfProcessors."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_T("Failed to attach to JVM."));
			result = -1;
			break;
		}

		if(objectInstance==NULL)
		{
			log.writeDebugLog(_T("Failed to connect to ESX."));
			result = -1;
			break;
		}
		clsV2P = env->GetObjectClass(objectInstance);

		if (clsV2P != NULL)
		{
			midGetServerPhysicalCPUSockets  = env->GetMethodID(clsV2P,"getESXPhysicalCPUSockets","(Ljava/lang/String;)I");
			midGetServerLogicalCPUSockets =env->GetMethodID(clsV2P,"getESXLogicalCPUSockets","(Ljava/lang/String;)I");
		}
		else
		{
			log.writeDebugLog(_T("Unable to find the requested class."));
			result = -1;
			goto invokejavavirtualmachine_error;
		}

		if((NULL != midGetServerPhysicalCPUSockets) && (NULL != midGetServerLogicalCPUSockets))
		{
			UnicodeToUTF8(esxName, aesxServer);
			StringEsxName = env->NewStringUTF(aesxServer.c_str());

			jint jnumberOfProcessors = (jint)env->CallObjectMethod(objectInstance,midGetServerPhysicalCPUSockets, StringEsxName);
			jint jnumberOfLogicalProcessors = (jint)env->CallObjectMethod(objectInstance,midGetServerLogicalCPUSockets, StringEsxName);

			numberOfLogicalProcessors = jnumberOfLogicalProcessors;
			numberOfProcessors = jnumberOfProcessors;
			log.writeDebugLog(_T("getESXNumberOfProcessors for esx %s, numberOfLogicalProcessors = %u, numberOfProcessors = %u."), 
				              esxName, numberOfLogicalProcessors, numberOfProcessors);
		}
		else
		{
			result = -1;
			log.writeDebugLog(_T("Unable to find the requested methods."));
		}

invokejavavirtualmachine_error:
		if ( env->ExceptionOccurred()) {
			env->ExceptionDescribe();
		}		

	}while(FALSE);

	//if (bJVMAttached)
	{
		DetachJVM();
	}

	return result;
}


void NativeClass::deleteCTKFiles(WCHAR* esxName, WCHAR* dcName, WCHAR* vmName, WCHAR* vmUUID)
{
	jclass clsV2P = NULL;
	jmethodID midGetServerType = NULL;
	V2PNFlog log;
	string aesxServer;
	string aesxDC;
	string avmName;
	string avmUUId;
	jstring StringEsxName;
	jstring StringEsxDC;
	jstring StringVMName;
	jstring StringVMUUID;

#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

	log.writeDebugLog(_T("Attempting to check VM server license."));

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return;
	}
	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		midGetServerType  = env->GetMethodID(clsV2P,"delCTKFileForVM","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetServerType != NULL)
	{
		UnicodeToUTF8(esxName, aesxServer);
		UnicodeToUTF8(dcName, aesxDC);
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUId);

		StringEsxName = env->NewStringUTF(aesxServer.c_str());
		StringEsxDC = env->NewStringUTF(aesxDC.c_str());
		StringVMName = env->NewStringUTF(avmName.c_str());
		StringVMUUID = env->NewStringUTF(avmUUId.c_str());

		env->CallObjectMethod(objectInstance,midGetServerType, StringEsxName, StringEsxDC, StringVMName, StringVMUUID);
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return;
}

void NativeClass::disconnectESX()
{
	jclass clsV2P = NULL;
	jmethodID midDisconnect = NULL;
	BOOL result = FALSE;
	V2PNFlog log;
	log.writeDebugLog(_T("Attempting to get Disconnect."));	
	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return;
	}
	clsV2P = env->GetObjectClass(objectInstance);

	if(clsV2P != NULL)
	{
		midDisconnect = env->GetMethodID(clsV2P,"disconnectESX","()V");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midDisconnect != NULL)
	{
		env->CallObjectMethod(objectInstance,midDisconnect);
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}
invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return;

}

ESXNode* NativeClass::getEsxNodeList(int *count)
{
	jclass clsV2P = NULL;
	jclass clsESX = NULL;
	V2PNFlog log;
	jobjectArray jobjWOArr = NULL;
	jmethodID midGetESXList = NULL;
	jfieldID fieldESX = NULL;
	jfieldID fieldDC = NULL;
	jfieldID fieldCluster = NULL; //<huvfe01>2015-6-9 Bug#415647 identify the cluster, empty name means the the esx is not in a cluster
	
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

	log.writeDebugLog(_T("Attempting to get esx node list for server %ls."), szserverName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}
	
	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	clsESX = env->FindClass("com/ca/arcflash/ha/vmwaremanager/ESXNode");


	if (clsV2P != NULL && clsESX != NULL)
	{
		midGetESXList  = env->GetMethodID(clsV2P,"getESXNodeList","()[Lcom/ca/arcflash/ha/vmwaremanager/ESXNode;");
		fieldESX = env->GetFieldID(clsESX,"esxName", "Ljava/lang/String;");
		fieldDC = env->GetFieldID(clsESX,"dcName", "Ljava/lang/String;");
		fieldCluster = env->GetFieldID(clsESX, "clusterName", "Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetESXList != NULL)
	{
		
		jobjWOArr = (jobjectArray) env->CallObjectMethod(objectInstance,midGetESXList);
		if(esxDetails != NULL)
		{
			delete[] (esxDetails);
			esxDetails = NULL;
		}
		if(jobjWOArr)
		{

			jsize esxNodeLength =  env->GetArrayLength(jobjWOArr);
			cout << "Number of node is " << esxNodeLength << endl;

			*count = esxNodeLength;

			esxDetails = new ESXNode[esxNodeLength];

			for(jsize i = 0; i < esxNodeLength; i++)
			{
				jobject tempObj = env->GetObjectArrayElement(jobjWOArr, i);
				jboolean isESXNode = env->IsInstanceOf(tempObj, clsESX);

				if(!isESXNode)
				{
					log.writeDebugLog(_T("This is not an ESXNode class."));
					goto invokejavavirtualmachine_error;
				}

				jstring esxName = (jstring)env->GetObjectField(tempObj, fieldESX);
				jstring dcName = (jstring)env->GetObjectField(tempObj, fieldDC);
				jstring clusterName = (jstring)env->GetObjectField(tempObj, fieldCluster);

				jboolean isESX = FALSE, isDC = FALSE, isCluster = FALSE;

				const jchar *strESX = env->GetStringChars(esxName, &isESX);
				const jchar *strDC = env->GetStringChars(dcName, &isDC);
				const jchar *strCluster = env->GetStringChars(clusterName, &isCluster);

				if(isDC)
				{
					wcscpy_s(esxDetails[i].dcName, NAME_SIZE, (const WCHAR *)strDC);
				}

				if (isESX)
				{
					wcscpy_s(esxDetails[i].esxName, NAME_SIZE, (const WCHAR *)strESX);
				}

				if (isCluster)
				{
					wcscpy_s(esxDetails[i].clusterName, NAME_SIZE, (const WCHAR *)strCluster);
				}

				env->ReleaseStringChars(esxName, strESX);
				env->ReleaseStringChars(dcName, strDC);
				env->ReleaseStringChars(clusterName, strCluster);

			}
		}
		else
		{
			log.writeDebugLog(_T("getEsxNodeList API returned NULL."));
		}

	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return esxDetails;
}

DataStore*  NativeClass::getESXHostDataStoreList(ESXNode esxNode, int *count)
{
	jclass clsV2P = NULL;
	jclass clsESX = NULL;
	jobjectArray jobjWOArr = NULL;
	jmethodID midGetESXStoreList = NULL;
	string esxHost;
	string esxDC;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to get data store list for esx %ls."), esxNode.esxName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}
	
	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midGetESXStoreList  = env->GetMethodID(clsV2P,"getESXHostDataStoreList","(Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetESXStoreList != NULL)
	{
		
		UnicodeToUTF8(esxNode.esxName, esxHost);
		UnicodeToUTF8(esxNode.dcName, esxDC);
		jstring StringEsxHost = env->NewStringUTF(esxHost.c_str());
		jstring StringEsxDC = env->NewStringUTF(esxDC.c_str());

		jobjWOArr = (jobjectArray) env->CallObjectMethod(objectInstance,midGetESXStoreList,StringEsxHost,StringEsxDC);
		if(esxDS != NULL)
		{
			delete[] (esxDS);
			esxDS = NULL;
		}
		if(jobjWOArr)
		{

			jsize esxStoreLength =  env->GetArrayLength(jobjWOArr);
			cout << "Number of DStrs is " << esxStoreLength << endl;
			*count = esxStoreLength;

			esxDS = new DataStore[esxStoreLength];

			for(jsize i = 0; i < esxStoreLength; i++)
			{
				jboolean isObj = FALSE;
				jstring tempObj = (jstring)env->GetObjectArrayElement(jobjWOArr, i);
				const jchar *strDS = env->GetStringChars(tempObj, &isObj);
				if(isObj)
				{
					wcscpy_s(esxDS[i].esxName,NAME_SIZE,esxNode.esxName);
					wcscpy_s(esxDS[i].dcName,NAME_SIZE,esxNode.dcName);
					wcscpy_s(esxDS[i].dataStore,NAME_SIZE,(const WCHAR *)strDS);
				}
			}
		}
		else
		{
			log.writeDebugLog(_T("getESXHostDataStoreList API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return esxDS;
}


VM_BasicInfo*  NativeClass::getVMList(ESXNode esxNode, int *count)
{
	jclass clsV2P = NULL;
	jclass clsVM = NULL;
	jobjectArray jobjWOArr = NULL;
	jmethodID midGetVMList = NULL;
	jfieldID fieldvmName = NULL;
	jfieldID fieldvmInstUUID = NULL;
	jfieldID fieldisVCM = NULL;
	jfieldID fieldisIVM = NULL;
	string esxHost;
	string esxDC;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;
	
	log.writeDebugLog(_T("Attempting to get vmList for ESX %ls."), esxNode.esxName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	clsVM = env->FindClass("com/ca/arcflash/ha/vmwaremanager/VM_Info");


	if (clsV2P != NULL && clsVM != NULL)
	{
		midGetVMList  = env->GetMethodID(clsV2P,"getVMNames","(Ljava/lang/String;Ljava/lang/String;)[Lcom/ca/arcflash/ha/vmwaremanager/VM_Info;");
		fieldvmName = env->GetFieldID(clsVM,"vmName", "Ljava/lang/String;");
		fieldvmInstUUID = env->GetFieldID(clsVM, "vmInstanceUUID", "Ljava/lang/String;");
		fieldisVCM = env->GetFieldID(clsVM, "isVCM", "Z");
		fieldisIVM = env->GetFieldID(clsVM, "isIVM", "Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetVMList != NULL)
	{
		UnicodeToUTF8(esxNode.esxName, esxHost);
		UnicodeToUTF8(esxNode.dcName, esxDC);
		jstring StringEsxHost = env->NewStringUTF(esxHost.c_str());
		jstring StringEsxDC = env->NewStringUTF(esxDC.c_str());
		if(vmList != NULL)
		{
			delete[] (vmList);
			vmList = NULL;
		}

		jobjWOArr = (jobjectArray) env->CallObjectMethod(objectInstance,midGetVMList,StringEsxHost,StringEsxDC);
		if(jobjWOArr)
		{
			jsize esxVMLength =  env->GetArrayLength(jobjWOArr);
			*count = esxVMLength;

			vmList = new VM_BasicInfo[esxVMLength];

			for(jsize i = 0; i < esxVMLength; i++)
			{
				jobject tempObj = env->GetObjectArrayElement(jobjWOArr, i);
				jboolean isESXVM = env->IsInstanceOf(tempObj, clsVM);

				if(!isESXVM)
				{
					log.writeDebugLog(_T("This is not a VM object."));
					goto invokejavavirtualmachine_error;
				}

				jstring vmName = (jstring)env->GetObjectField(tempObj, fieldvmName);
				jstring vminstUUID = (jstring)env->GetObjectField(tempObj, fieldvmInstUUID);
				jboolean isVCM = env->GetBooleanField(tempObj, fieldisVCM);
				jboolean isIVM = env->GetBooleanField(tempObj, fieldisIVM);
				jboolean isName = FALSE, isInstUUID = FALSE;

				const jchar *strVM = env->GetStringChars(vmName, &isName);
				const jchar *strVMInstUUID = env->GetStringChars(vminstUUID, &isInstUUID);
				if(isName && isInstUUID)
				{
					wcscpy_s(vmList[i].vmName,NAME_SIZE,(const WCHAR *)strVM);
					wcscpy_s(vmList[i].vmInstUUID,NAME_SIZE,(const WCHAR *)strVMInstUUID);
					vmList[i].isVCM = isVCM;
					vmList[i].isIVM = isIVM;
				}

				env->ReleaseStringChars(vmName, strVM);
				env->ReleaseStringChars(vminstUUID, strVMInstUUID);
			}
		}
		else
		{
			log.writeDebugLog(_T("getVMList API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return vmList;
}

BOOL  NativeClass::checkResPool(ESXNode esxNode, WCHAR* resPool)
{
	jclass clsV2P = NULL;
	jboolean jobjWOArr = NULL;
	jmethodID midGetVMList = NULL;
	jfieldID fieldvmName = NULL;
	jfieldID fieldvmInstUUID = NULL;
	string esxHost;
	string esxDC;
	string aresPool;
	BOOL bpoolExists = FALSE;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to get vmList for ESX %ls."), esxNode.esxName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		midGetVMList  = env->GetMethodID(clsV2P,"checkResourcePool","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetVMList != NULL)
	{
		UnicodeToUTF8(esxNode.esxName, esxHost);
		UnicodeToUTF8(esxNode.dcName, esxDC);
		UnicodeToUTF8(resPool, aresPool);
		jstring StringEsxHost = env->NewStringUTF(esxHost.c_str());
		jstring StringEsxDC = env->NewStringUTF(esxDC.c_str());
		jstring StringrResPool = env->NewStringUTF(aresPool.c_str());

		jobjWOArr = (jboolean) env->CallObjectMethod(objectInstance,midGetVMList,StringEsxHost,StringEsxDC, StringrResPool);
		if(jobjWOArr)
		{
			bpoolExists = TRUE;
		}
		else
		{
			bpoolExists = FALSE;
			log.writeDebugLog(_T("Failed to find the resource pool."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return bpoolExists;
}


BOOL  NativeClass::setInstanceUUID (WCHAR* vmName, WCHAR* vmUUID, WCHAR* newInstanceUUID)
{
	jclass clsV2P = NULL;
	jboolean jobjWOArr = FALSE;
	jmethodID midGetVMList = NULL;
	jfieldID fieldvmName = NULL;
	jfieldID fieldvmInstUUID = NULL;
	string avmName;
	string aInstUUID;
	string anewInstUUID;
	BOOL bResult = FALSE;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to set InstanceUUID for VM %ls."), vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		midGetVMList  = env->GetMethodID(clsV2P,"setInstanceUUID","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetVMList != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, aInstUUID);
		UnicodeToUTF8(newInstanceUUID, anewInstUUID);
		jstring StringVMName = env->NewStringUTF(avmName.c_str());
		jstring StringInstUUID = env->NewStringUTF(aInstUUID.c_str());
		jstring StringNewInstUUID = env->NewStringUTF(anewInstUUID.c_str());

		jobjWOArr = (jboolean) env->CallObjectMethod(objectInstance,midGetVMList,StringVMName,StringInstUUID, StringNewInstUUID);
		if(jobjWOArr)
		{
			bResult = TRUE;
		}
		else
		{
			bResult = FALSE;
			log.writeDebugLog(_T("Failed to set the instance UUID for VM."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return bResult;
}


int  NativeClass::checkDSBlockSize(ESXNode esxNode, WCHAR* dataStore)
{
	jclass clsV2P = NULL;
	jint jObject = NULL;
	jmethodID midGetDSSize = NULL;
	jfieldID fieldvmName = NULL;
	jfieldID fieldvmInstUUID = NULL;
	string esxHost;
	string esxDC;
	string adatastore;
	int blockSize = 0;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to get vmList for ESX %ls."), esxNode.esxName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		midGetDSSize  = env->GetMethodID(clsV2P,"getDatstoreBlockSize","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetDSSize != NULL)
	{
		UnicodeToUTF8(esxNode.esxName, esxHost);
		UnicodeToUTF8(esxNode.dcName, esxDC);
		UnicodeToUTF8(dataStore, adatastore);
		jstring StringEsxHost = env->NewStringUTF(esxHost.c_str());
		jstring StringEsxDC = env->NewStringUTF(esxDC.c_str());
		jstring StringrdatStore = env->NewStringUTF(adatastore.c_str());

		jObject = (jint) env->CallObjectMethod(objectInstance,midGetDSSize,StringEsxHost,StringEsxDC, StringrdatStore);
		if(jObject)
		{
			blockSize = jObject;
		}
		else
		{
			blockSize = 0;
			log.writeDebugLog(_T("Failed to query block size of the datstore."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return blockSize;
}

VM_Info* NativeClass::getVMInfo(WCHAR* wvmName, WCHAR* wvmUUID)
{
	jclass clsV2P = NULL;
	jclass clsVM = NULL;
	jobject jobj = NULL;
	jmethodID midGetVMList = NULL;
	jfieldID fieldvmName = NULL;
	jfieldID fieldvmUUID = NULL;
	jfieldID fieldvmHost = NULL;
	jfieldID fieldvmCluster = NULL;//<huvfe01>2015-6-9 Bug#415647 identify the cluster, empty name means the the vm is not in a cluster
	jfieldID fieldvmVMX = NULL;
	jfieldID fieldvmState = NULL;
	jfieldID fieldvmESXHost = NULL;
	jfieldID fieldvmInstUUID = NULL;
	jfieldID fieldvmgstOS = NULL;
	jfieldID fieldvmIP = NULL;
	jfieldID fieldvmresPool = NULL;
	jfieldID fieldVMMemInMB = NULL; //<huvfe01>2-14-7-14
	jfieldID fieldVmMoRef = NULL;
	string avmName;
	string avmUUId;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;
	
	
	log.writeDebugLog(_T("Attempting to get vmInfo for VM %ls."), wvmName);
	if(vmInf != NULL)
	{
		delete(vmInf);
		vmInf = NULL;
	}
		

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return vmInf;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return vmInf;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	clsVM = env->FindClass("com/ca/arcflash/ha/vmwaremanager/VM_Info");


	if (clsV2P != NULL && clsVM != NULL)
	{
		midGetVMList  = env->GetMethodID(clsV2P,"getVMInfo","(Ljava/lang/String;Ljava/lang/String;)Lcom/ca/arcflash/ha/vmwaremanager/VM_Info;");
		fieldvmName = env->GetFieldID(clsVM,"vmName", "Ljava/lang/String;");
		fieldvmUUID = env->GetFieldID(clsVM,"vmUUID", "Ljava/lang/String;");
		fieldvmHost = env->GetFieldID(clsVM, "vmHostName", "Ljava/lang/String;");
		fieldvmCluster = env->GetFieldID(clsVM, "vmCluster", "Ljava/lang/String;");
		fieldvmVMX = env->GetFieldID(clsVM, "vmVMX", "Ljava/lang/String;");
		fieldvmInstUUID = env->GetFieldID(clsVM, "vmInstanceUUID", "Ljava/lang/String;");
		fieldvmESXHost = env->GetFieldID(clsVM, "vmEsxHost", "Ljava/lang/String;");
		fieldvmgstOS = env->GetFieldID(clsVM, "vmGuestOS", "Ljava/lang/String;");
		fieldvmIP = env->GetFieldID(clsVM, "vmIP", "Ljava/lang/String;");
		fieldvmState = env->GetFieldID(clsVM,"powerState","Z");
		fieldvmresPool = env->GetFieldID(clsVM, "vmresPool", "Ljava/lang/String;");
		fieldVMMemInMB = env->GetFieldID(clsVM, "memoryMB", "I");//<huvfe01>2-14-7-14
		fieldVmMoRef = env->GetFieldID(clsVM, "vmMoRef", "Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetVMList != NULL)
	{
		UnicodeToUTF8(wvmName, avmName);
		UnicodeToUTF8(wvmUUID, avmUUId);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUId = env->NewStringUTF(avmUUId.c_str());
		jobj = env->CallObjectMethod(objectInstance,midGetVMList,StringvmName,StringvmUUId);
		if(jobj)
		{
				jboolean isESXVM = env->IsInstanceOf(jobj, clsVM);

				if(!isESXVM)
				{
					log.writeDebugLog(_T("This is not a VM object."));
					goto invokejavavirtualmachine_error;
				}
				vmInf = new VM_Info;
				if (NULL == vmInf)
				{
					goto invokejavavirtualmachine_error;
				}
				ZeroMemory(vmInf, sizeof(VM_Info));

				jstring vmName = (jstring)env->GetObjectField(jobj, fieldvmName);
				jstring vmUUID = (jstring)env->GetObjectField(jobj, fieldvmUUID);
				jstring vmHstName = (jstring)env->GetObjectField(jobj, fieldvmHost);
				jstring vmVMXPath = (jstring)env->GetObjectField(jobj, fieldvmVMX);
				jstring vmesxHost = (jstring)env->GetObjectField(jobj, fieldvmESXHost);
				jstring vmCluster = (jstring)env->GetObjectField(jobj, fieldvmCluster);
				jstring vminstUUID = (jstring)env->GetObjectField(jobj, fieldvmInstUUID);
				jstring vmgstOS = (jstring)env->GetObjectField(jobj, fieldvmgstOS);
				jstring vmVMIP = (jstring)env->GetObjectField(jobj, fieldvmIP);
				jboolean vmPwrState = (jboolean)env->GetBooleanField(jobj, fieldvmState);
				jstring vmResPool = (jstring)env->GetObjectField(jobj, fieldvmresPool);
				jint vmMemInMB = (jint)env->GetIntField(jobj, fieldVMMemInMB);//<huvfe01>2-14-7-14
				jstring vmMoRef = (jstring) env->GetObjectField(jobj, fieldVmMoRef);

				jboolean isName = FALSE, isUUID = FALSE, isHstName = FALSE, isVMXPath = FALSE, isESXHost = FALSE, isInstUUID = FALSE, isgstOS = FALSE, isVMIP = FALSE, isresPool = FALSE, isVmMoRef = FALSE, isCluster = FALSE;

				const jchar *strVM = env->GetStringChars(vmName, &isName);
				const jchar *strVMUUID = env->GetStringChars(vmUUID, &isUUID);
				const jchar *strVMHstName = env->GetStringChars(vmHstName, &isHstName);
				const jchar *strVMVMX = env->GetStringChars(vmVMXPath, &isVMXPath);
				const jchar *strVMESXHost = env->GetStringChars(vmesxHost, &isESXHost);
				const jchar *strVMCluster = env->GetStringChars(vmCluster, &isCluster);
				const jchar *strVMInstUUID = env->GetStringChars(vminstUUID, &isInstUUID);
				const jchar *strVMGstOS = env->GetStringChars(vmgstOS, &isgstOS);
				const jchar *strVMIP = env->GetStringChars(vmVMIP, &isVMIP);
				const jchar *strVMResPool = env->GetStringChars(vmResPool, &isresPool);
				const jchar *strVmMoRef = env->GetStringChars(vmMoRef, &isVmMoRef);

				if (isName && isUUID && isHstName && isVMXPath && isESXHost && isInstUUID && isgstOS && isresPool && isVmMoRef && isCluster)
				{
					wcscpy_s(vmInf->vmName,NAME_SIZE,(const WCHAR *)strVM);
					wcscpy_s(vmInf->vmUUID,NAME_SIZE,(const WCHAR *)strVMUUID);
					wcscpy_s(vmInf->vmHost,NAME_SIZE,(const WCHAR *)strVMHstName);
					wcscpy_s(vmInf->vmVMX,NAME_SIZE,(const WCHAR *)strVMVMX);
					wcscpy_s(vmInf->vmESXHost,NAME_SIZE,(const WCHAR *)strVMESXHost);
					wcscpy_s(vmInf->vmClusterName, NAME_SIZE, (const WCHAR *)strVMCluster);
					wcscpy_s(vmInf->vmInstUUID,NAME_SIZE,(const WCHAR *)strVMInstUUID);
					wcscpy_s(vmInf->vmGuestOS,NAME_SIZE,(const WCHAR *)strVMGstOS);
					vmInf->powerState = vmPwrState;
					wcscpy_s(vmInf->vmIP,NAME_SIZE,(const WCHAR *)strVMIP);
					wcscpy_s(vmInf->vmresPool,NAME_SIZE,(const WCHAR *)strVMResPool);
					vmInf->dwVMMemInMB = (DWORD)vmMemInMB;//<huvfe01>2-14-7-14
					wcscpy_s(vmInf->vmMoRef, NAME_SIZE, (const WCHAR *) strVmMoRef);
				}

				env->ReleaseStringChars(vmName, strVM);
				env->ReleaseStringChars(vmUUID, strVMUUID);
				env->ReleaseStringChars(vmHstName, strVMHstName);
				env->ReleaseStringChars(vmVMXPath, strVMVMX);
				env->ReleaseStringChars(vmesxHost, strVMESXHost);
				env->ReleaseStringChars(vmCluster, strVMCluster);
				env->ReleaseStringChars(vminstUUID, strVMInstUUID);
				env->ReleaseStringChars(vmgstOS, strVMGstOS);
				env->ReleaseStringChars(vmVMIP, strVMIP);
				env->ReleaseStringChars(vmResPool, strVMResPool);
				env->ReleaseStringChars(vmMoRef, strVmMoRef);
		}
		else
		{
			log.writeDebugLog(_T("getVMInfo API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return vmInf;
}

int NativeClass::getVMInfoUnderDataCenter(const WCHAR* vmName, const WCHAR* dcName, OUT VM_Info* pVmInfo)
{
	jclass clsV2P = NULL;
	jclass clsVM = NULL;
	jobject jobj = NULL;
	jmethodID midGetVMInfo = NULL;
	jfieldID fieldvmName = NULL;
	jfieldID fieldvmUUID = NULL;
	jfieldID fieldvmHost = NULL;
	jfieldID fieldvmCluster = NULL;//<huvfe01>2015-6-9 Bug#415647 identify the cluster, empty name means the the vm is not in a cluster
	jfieldID fieldvmVMX = NULL;
	jfieldID fieldvmState = NULL;
	jfieldID fieldvmESXHost = NULL;
	jfieldID fieldvmInstUUID = NULL;
	jfieldID fieldvmgstOS = NULL;
	jfieldID fieldvmIP = NULL;
	jfieldID fieldvmresPool = NULL;
	jfieldID fieldVMMemInMB = NULL; //<huvfe01>2-14-7-14
	jfieldID fieldVmMoRef = NULL;
	string strVmName;
	string strDcName;
	
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to get vmInfo for VM %ls, dcName %ls."), vmName, dcName);

	int nRet = -1;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return nRet;
	}


	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return nRet;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	clsVM = env->FindClass("com/ca/arcflash/ha/vmwaremanager/VM_Info");


	if (clsV2P != NULL && clsVM != NULL)
	{
		midGetVMInfo = env->GetMethodID(clsV2P, "getVMInfoUnderDataCenter", "(Ljava/lang/String;Ljava/lang/String;)Lcom/ca/arcflash/ha/vmwaremanager/VM_Info;");
		fieldvmName = env->GetFieldID(clsVM, "vmName", "Ljava/lang/String;");
		fieldvmUUID = env->GetFieldID(clsVM, "vmUUID", "Ljava/lang/String;");
		fieldvmHost = env->GetFieldID(clsVM, "vmHostName", "Ljava/lang/String;");
		fieldvmCluster = env->GetFieldID(clsVM, "vmCluster", "Ljava/lang/String;");
		fieldvmVMX = env->GetFieldID(clsVM, "vmVMX", "Ljava/lang/String;");
		fieldvmInstUUID = env->GetFieldID(clsVM, "vmInstanceUUID", "Ljava/lang/String;");
		fieldvmESXHost = env->GetFieldID(clsVM, "vmEsxHost", "Ljava/lang/String;");
		fieldvmgstOS = env->GetFieldID(clsVM, "vmGuestOS", "Ljava/lang/String;");
		fieldvmIP = env->GetFieldID(clsVM, "vmIP", "Ljava/lang/String;");
		fieldvmState = env->GetFieldID(clsVM, "powerState", "Z");
		fieldvmresPool = env->GetFieldID(clsVM, "vmresPool", "Ljava/lang/String;");
		fieldVMMemInMB = env->GetFieldID(clsVM, "memoryMB", "I");//<huvfe01>2-14-7-14
		fieldVmMoRef = env->GetFieldID(clsVM, "vmMoRef", "Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class"));
		goto invokejavavirtualmachine_error;
	}

	if (midGetVMInfo != NULL)
	{
		UnicodeToUTF8(vmName, strVmName);
		UnicodeToUTF8(dcName, strDcName);
		jstring StringvmName = env->NewStringUTF(strVmName.c_str());
		jstring StringdcName = env->NewStringUTF(strDcName.c_str());
		jobj = env->CallObjectMethod(objectInstance, midGetVMInfo, StringdcName, StringvmName);
		if (jobj)
		{
			jboolean isESXVM = env->IsInstanceOf(jobj, clsVM);

			if (!isESXVM)
			{
				log.writeDebugLog(_T("This is not a VM object."));
				goto invokejavavirtualmachine_error;
			}
			vmInf = new VM_Info;
			if (NULL == vmInf)
			{
				goto invokejavavirtualmachine_error;
			}
			ZeroMemory(vmInf, sizeof(VM_Info));

			jstring vmName = (jstring)env->GetObjectField(jobj, fieldvmName);
			jstring vmUUID = (jstring)env->GetObjectField(jobj, fieldvmUUID);
			jstring vmHstName = (jstring)env->GetObjectField(jobj, fieldvmHost);
			jstring vmVMXPath = (jstring)env->GetObjectField(jobj, fieldvmVMX);
			jstring vmesxHost = (jstring)env->GetObjectField(jobj, fieldvmESXHost);
			jstring vmCluster = (jstring)env->GetObjectField(jobj, fieldvmCluster);
			jstring vminstUUID = (jstring)env->GetObjectField(jobj, fieldvmInstUUID);
			jstring vmgstOS = (jstring)env->GetObjectField(jobj, fieldvmgstOS);
			jstring vmVMIP = (jstring)env->GetObjectField(jobj, fieldvmIP);
			jboolean vmPwrState = (jboolean)env->GetBooleanField(jobj, fieldvmState);
			jstring vmResPool = (jstring)env->GetObjectField(jobj, fieldvmresPool);
			jint vmMemInMB = (jint)env->GetIntField(jobj, fieldVMMemInMB);//<huvfe01>2-14-7-14
			jstring vmMoRef = (jstring)env->GetObjectField(jobj, fieldVmMoRef);

			jboolean isName = FALSE, isUUID = FALSE, isHstName = FALSE, isVMXPath = FALSE, isESXHost = FALSE, isInstUUID = FALSE, isgstOS = FALSE, isVMIP = FALSE, isresPool = FALSE, isVmMoRef = FALSE, isCluster = FALSE;

			const jchar *strVM = env->GetStringChars(vmName, &isName);
			const jchar *strVMUUID = env->GetStringChars(vmUUID, &isUUID);
			const jchar *strVMHstName = env->GetStringChars(vmHstName, &isHstName);
			const jchar *strVMVMX = env->GetStringChars(vmVMXPath, &isVMXPath);
			const jchar *strVMESXHost = env->GetStringChars(vmesxHost, &isESXHost);
			const jchar *strVMCluster = env->GetStringChars(vmCluster, &isCluster);
			const jchar *strVMInstUUID = env->GetStringChars(vminstUUID, &isInstUUID);
			const jchar *strVMGstOS = env->GetStringChars(vmgstOS, &isgstOS);
			const jchar *strVMIP = env->GetStringChars(vmVMIP, &isVMIP);
			const jchar *strVMResPool = env->GetStringChars(vmResPool, &isresPool);
			const jchar *strVmMoRef = env->GetStringChars(vmMoRef, &isVmMoRef);

			if (isName && isUUID && isHstName && isVMXPath && isESXHost && isInstUUID && isgstOS && isresPool && isVmMoRef && isCluster)
			{
				wcscpy_s(pVmInfo->vmName, NAME_SIZE, (const WCHAR *)strVM);
				wcscpy_s(pVmInfo->vmUUID, NAME_SIZE, (const WCHAR *)strVMUUID);
				wcscpy_s(pVmInfo->vmHost, NAME_SIZE, (const WCHAR *)strVMHstName);
				wcscpy_s(pVmInfo->vmVMX, NAME_SIZE, (const WCHAR *)strVMVMX);
				wcscpy_s(pVmInfo->vmESXHost, NAME_SIZE, (const WCHAR *)strVMESXHost);
				wcscpy_s(pVmInfo->vmClusterName, NAME_SIZE, (const WCHAR *)strVMCluster);
				wcscpy_s(pVmInfo->vmInstUUID, NAME_SIZE, (const WCHAR *)strVMInstUUID);
				wcscpy_s(pVmInfo->vmGuestOS, NAME_SIZE, (const WCHAR *)strVMGstOS);
				pVmInfo->powerState = vmPwrState;
				wcscpy_s(pVmInfo->vmIP, NAME_SIZE, (const WCHAR *)strVMIP);
				wcscpy_s(pVmInfo->vmresPool, NAME_SIZE, (const WCHAR *)strVMResPool);
				pVmInfo->dwVMMemInMB = (DWORD)vmMemInMB;//<huvfe01>2-14-7-14
				wcscpy_s(pVmInfo->vmMoRef, NAME_SIZE, (const WCHAR *)strVmMoRef);
			}

			env->ReleaseStringChars(vmName, strVM);
			env->ReleaseStringChars(vmUUID, strVMUUID);
			env->ReleaseStringChars(vmHstName, strVMHstName);
			env->ReleaseStringChars(vmVMXPath, strVMVMX);
			env->ReleaseStringChars(vmesxHost, strVMESXHost);
			env->ReleaseStringChars(vmCluster, strVMCluster);
			env->ReleaseStringChars(vminstUUID, strVMInstUUID);
			env->ReleaseStringChars(vmgstOS, strVMGstOS);
			env->ReleaseStringChars(vmVMIP, strVMIP);
			env->ReleaseStringChars(vmResPool, strVMResPool);
			env->ReleaseStringChars(vmMoRef, strVmMoRef);
			nRet = 0;
		}
		else
		{
			log.writeDebugLog(_T("getVMInfo API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}

invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return nRet;
}

Disk_Info*  NativeClass::getVMDiskURLs(WCHAR* vmName, WCHAR*  vmUUID, int *count)
{
	jclass clsV2P = NULL;
	jclass clsDsk = NULL;
	jobjectArray jobjWOArr = NULL;
	jmethodID midGetDiskList = NULL;
	jfieldID fieldDiskURL = NULL;
	jfieldID fieldchageID = NULL;
	jfieldID fieldDeviceKey = NULL;
	jfieldID fieldModeKey = NULL;
	jfieldID fieldCModeKey = NULL;
	jfieldID fieldDiskType = NULL;
	jfieldID fieldIsIDEDisk = NULL;
	jfieldID fieldIsSATADisk = NULL;
	string avmName;
	string avmUUID;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;
	
	log.writeDebugLog(_T("Attempting to get disk URL's of VM %ls."), vmName);
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	clsDsk = env->FindClass("com/ca/arcflash/ha/vmwaremanager/Disk_Info");


	if (clsV2P != NULL && clsDsk != NULL)
	{
		midGetDiskList  = env->GetMethodID(clsV2P,"getCurrentDiskURLs","(Ljava/lang/String;Ljava/lang/String;)[Lcom/ca/arcflash/ha/vmwaremanager/Disk_Info;");
		fieldDiskURL = env->GetFieldID(clsDsk,"diskURL", "Ljava/lang/String;");
		fieldchageID = env->GetFieldID(clsDsk,"changeID", "Ljava/lang/String;");
		fieldModeKey = env->GetFieldID(clsDsk,"diskMode", "Ljava/lang/String;");
		fieldCModeKey = env->GetFieldID(clsDsk,"compMode", "Ljava/lang/String;");
		fieldDeviceKey = env->GetFieldID(clsDsk,"deviceKey","I");
		fieldDiskType = env->GetFieldID(clsDsk,"diskType","I");
		fieldIsIDEDisk = env->GetFieldID(clsDsk,"isIDEDisk","Z");
		fieldIsSATADisk = env->GetFieldID(clsDsk,"isSATADisk","Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetDiskList != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());

		jobjWOArr = (jobjectArray) env->CallObjectMethod(objectInstance,midGetDiskList,StringvmName, StringvmUUID);
		if(diskList != NULL)
		{
			delete[] (diskList);
			diskList = NULL;
		}
		if(jobjWOArr != NULL)
		{

			jsize vmDiskLength =  env->GetArrayLength(jobjWOArr);
			*count = vmDiskLength;

			diskList = new Disk_Info[vmDiskLength];
			ZeroMemory(diskList, sizeof(Disk_Info) * vmDiskLength);

			for(jsize i = 0; i < vmDiskLength; i++)
			{
				jobject tempObj = env->GetObjectArrayElement(jobjWOArr, i);
				jboolean isESXVM = env->IsInstanceOf(tempObj, clsDsk);

				if(!isESXVM)
				{
					log.writeDebugLog(_T("This is not a disk object"));
					goto invokejavavirtualmachine_error;
				}

				jstring diskURL = (jstring)env->GetObjectField(tempObj, fieldDiskURL);
				jstring changeID = (jstring)env->GetObjectField(tempObj, fieldchageID);
				jstring diskMode = (jstring)env->GetObjectField(tempObj, fieldModeKey);
				jstring dCompMode = (jstring)env->GetObjectField(tempObj, fieldCModeKey);
				jint diskKey = (jint)env->GetIntField(tempObj, fieldDeviceKey);
				jint diskType = (jint)env->GetIntField(tempObj, fieldDiskType);
				jboolean isIDEDisk = (jboolean)env->GetBooleanField(tempObj, fieldIsIDEDisk);
				jboolean isSATADisk = (jboolean)env->GetBooleanField(tempObj, fieldIsSATADisk);
				jboolean isdiskURL = FALSE, isChangeID = FALSE, isDiskMode = FALSE, isCMode =FALSE;
				const jchar *strDiskURL = NULL , *strChangeID = NULL, *strDMode = NULL, *strCMode = NULL;
				if(changeID)
				{
					strChangeID = env->GetStringChars(changeID, &isChangeID);
					if (isChangeID)
						wcscpy_s(diskList[i].chageID,NAME_SIZE,(const WCHAR *)strChangeID);
					else
						wcscpy_s(diskList[i].chageID,NAME_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(changeID, strChangeID);
				}
				else
					wcscpy_s(diskList[i].chageID,NAME_SIZE,(const WCHAR *)L"");
				if(diskURL)
				{
					strDiskURL = env->GetStringChars(diskURL, &isdiskURL);
					if(isdiskURL)
				     	wcscpy_s(diskList[i].vmDisk,DISKURL_SIZE,(const WCHAR *)strDiskURL);
					else
						wcscpy_s(diskList[i].vmDisk,DISKURL_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(diskURL, strDiskURL);		
				}
				else
					wcscpy_s(diskList[i].vmDisk,DISKURL_SIZE,(const WCHAR *)L"");
				if(diskMode)
				{
					strDMode = env->GetStringChars(diskMode, &isDiskMode);
					if(isDiskMode)
						wcscpy_s(diskList[i].diskMode,NAME_SIZE,(const WCHAR *)strDMode);
					else
						wcscpy_s(diskList[i].diskMode,NAME_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(diskMode, strDMode);
				}
				else
					wcscpy_s(diskList[i].diskMode,NAME_SIZE,(const WCHAR *)L"");

				if(dCompMode)
				{
					strCMode = env->GetStringChars(dCompMode, &isCMode);
					if(isCMode)
						wcscpy_s(diskList[i].diskCompMode,NAME_SIZE,(const WCHAR *)strCMode);
					else
						wcscpy_s(diskList[i].diskCompMode,NAME_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(dCompMode, strCMode);
				}
				else
					wcscpy_s(diskList[i].diskCompMode,NAME_SIZE,(const WCHAR *)L"");

				diskList[i].deviceKey = diskKey;	
				diskList[i].diskType = diskType;
				diskList[i].bIsIDEDisk = isIDEDisk;//(jboolean)env->GetBooleanField(tempObj, fieldIsIDEDisk);
				diskList[i].bIsSATADisk = isSATADisk;// (jboolean)env->GetBooleanField(tempObj, fieldIsSATADisk);
				diskList[i].bUseSWSnapshot = true;
			}
		}
		else
		{
			log.writeDebugLog(_T("getVMDiskURLs API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return diskList;
}


Snapshot_Info*  NativeClass::getVMSnapshotList(WCHAR* vmName, WCHAR*  vmUUID, int *count)
{
	jclass clsV2P = NULL;
	jclass clsSnp = NULL;
	jobjectArray jobjWOArr = NULL;
	jmethodID midGetSnapList = NULL;
	jfieldID fieldURL = NULL;
	jfieldID fieldName = NULL;
	string avmName;
	string avmUUID;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to get snapshot list of VM %ls."), vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}
	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	clsSnp = env->FindClass("com/ca/arcflash/ha/vmwaremanager/Snapshot_Info");


	if (clsV2P != NULL && clsSnp != NULL)
	{
		midGetSnapList  = env->GetMethodID(clsV2P,"getSnapshotList","(Ljava/lang/String;Ljava/lang/String;)[Lcom/ca/arcflash/ha/vmwaremanager/Snapshot_Info;");
		fieldURL = env->GetFieldID(clsSnp,"snapshotURL", "Ljava/lang/String;");
		fieldName = env->GetFieldID(clsSnp,"snapshotName", "Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetSnapList != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());

		jobjWOArr = (jobjectArray) env->CallObjectMethod(objectInstance,midGetSnapList,StringvmName, StringvmUUID);
		if(snapList != NULL)
		{
			delete[] (snapList);
			snapList = NULL;
		}
		if(jobjWOArr != NULL)
		{

			jsize vmSnapLength =  env->GetArrayLength(jobjWOArr);
			*count = vmSnapLength;

			snapList = new Snapshot_Info[vmSnapLength];

			for(jsize i = 0; i < vmSnapLength; i++)
			{
				jobject tempObj = env->GetObjectArrayElement(jobjWOArr, i);
				jboolean isSnapshot = env->IsInstanceOf(tempObj, clsSnp);

				if(!isSnapshot)
				{
					log.writeDebugLog(_T("This is not a disk object."));
					goto invokejavavirtualmachine_error;
				}

				jstring snapURL = (jstring)env->GetObjectField(tempObj, fieldURL);
				jstring snapName = (jstring)env->GetObjectField(tempObj, fieldName);
				jboolean issnapURL = FALSE, issnapName = FALSE;

				const jchar *strsnapURL = env->GetStringChars(snapURL, &issnapURL);
				const jchar *strsnapName = env->GetStringChars(snapName, &issnapName);

				if(issnapURL && issnapName)
				{
					wcscpy_s(snapList[i].snapURL, DISKURL_SIZE,(const WCHAR *)strsnapURL);
					wcscpy_s(snapList[i].snapshotName,NAME_SIZE,(const WCHAR *)strsnapName);
				}

				env->ReleaseStringChars(snapURL, strsnapURL);
				env->ReleaseStringChars(snapName, strsnapName);

			}
		}
		else
		{
			log.writeDebugLog(_T("getVMSnapshotList API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return snapList;
}


Disk_Info*  NativeClass::generateDiskBitMapForSnapshot(WCHAR* vmName, WCHAR*  vmUUID, WCHAR* snapshotURL, int *count)
{
	jclass clsV2P = NULL;
	jclass clsDsk = NULL;
	jobjectArray jobjWOArr = NULL;
	jmethodID midGetsnapDiskList = NULL;
	jfieldID fieldDiskURL = NULL;
	jfieldID fieldchageID = NULL;
	jfieldID fieldDeviceKey = NULL;
	string avmName;
	string avmUUID;
	string avmsnapURL;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to get snap disk info of VM %ls for snapshot URL %ls."), vmName, snapshotURL);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	clsDsk = env->FindClass("com/ca/arcflash/ha/vmwaremanager/Disk_Info");


	if (clsV2P != NULL && clsDsk != NULL)
	{
		midGetsnapDiskList  = env->GetMethodID(clsV2P,"generateDiskBitMapforSnapshot","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)[Lcom/ca/arcflash/ha/vmwaremanager/Disk_Info;");
		fieldDiskURL = env->GetFieldID(clsDsk,"diskURL", "Ljava/lang/String;");
		fieldchageID = env->GetFieldID(clsDsk,"changeID", "Ljava/lang/String;");
		fieldDeviceKey = env->GetFieldID(clsDsk,"deviceKey","I");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetsnapDiskList != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotURL, avmsnapURL);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringSnapURL = env->NewStringUTF(avmsnapURL.c_str());

		jobjWOArr = (jobjectArray) env->CallObjectMethod(objectInstance,midGetsnapDiskList,StringvmName, StringvmUUID, StringSnapURL);
		if(diskList != NULL)
		{
			delete[] (diskList);
			diskList = NULL;
		}
		if(jobjWOArr != NULL)
		{

			jsize vmDiskLength =  env->GetArrayLength(jobjWOArr);
			cout << "Number of disks " << vmDiskLength << endl;

			*count = vmDiskLength;

			diskList = new Disk_Info[vmDiskLength];

			for(jsize i = 0; i < vmDiskLength; i++)
			{
				jobject tempObj = env->GetObjectArrayElement(jobjWOArr, i);
				jboolean isESXVM = env->IsInstanceOf(tempObj, clsDsk);

				if(!isESXVM)
				{
					log.writeDebugLog(_T("This is not a disk object"));
					goto invokejavavirtualmachine_error;
				}

				jstring diskURL = (jstring)env->GetObjectField(tempObj, fieldDiskURL);
				jstring changeID = (jstring)env->GetObjectField(tempObj, fieldchageID);
				jint diskKey = (jint)env->GetIntField(tempObj, fieldDeviceKey);
				jboolean isdiskURL = FALSE, isChangeID = FALSE;

				const jchar *strDiskURL = env->GetStringChars(diskURL, &isdiskURL);
				const jchar *strChangeID = env->GetStringChars(changeID, &isChangeID);

				if(isdiskURL && isChangeID)
				{
					wcscpy_s(diskList[i].vmDisk,DISKURL_SIZE,(const WCHAR *)strDiskURL);
					wcscpy_s(diskList[i].chageID,NAME_SIZE,(const WCHAR *)strChangeID);
					diskList[i].deviceKey = diskKey;
				}

				env->ReleaseStringChars(diskURL, strDiskURL);
				env->ReleaseStringChars(changeID, strChangeID);
				diskList[i].bUseSWSnapshot = true;
			}
		}
		else
		{
			log.writeDebugLog(_T("getSnapShotDiskInfo returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return diskList;
}

Disk_Info*  NativeClass::getSnapShotDiskInfo(WCHAR* vmName, WCHAR*  vmUUID, WCHAR* snapshotURL, int *count)
{
	jclass clsV2P = NULL;
	jclass clsDsk = NULL;
	jobjectArray jobjWOArr = NULL;
	jmethodID midGetsnapDiskList = NULL;
	jfieldID fieldDiskURL = NULL;
	jfieldID fieldchageID = NULL;
	jfieldID fieldDeviceKey = NULL;
	jfieldID fieldModeKey = NULL;
	jfieldID fieldCModeKey = NULL;
	jfieldID fieldIsIDEDisk = NULL;
	jfieldID fieldIsSATADisk = NULL;
	string avmName;
	string avmUUID;
	string avmsnapURL;

	V2PNFlog log;
	
	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif	

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	clsDsk = env->FindClass("com/ca/arcflash/ha/vmwaremanager/Disk_Info");


	if (clsV2P != NULL && clsDsk != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId...")); 
		midGetsnapDiskList  = env->GetMethodID(clsV2P,"getsnapShotDiskInfo","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)[Lcom/ca/arcflash/ha/vmwaremanager/Disk_Info;");
		fieldDiskURL = env->GetFieldID(clsDsk,"diskURL", "Ljava/lang/String;");
		fieldchageID = env->GetFieldID(clsDsk,"changeID", "Ljava/lang/String;");
		fieldModeKey = env->GetFieldID(clsDsk,"diskMode", "Ljava/lang/String;");
		fieldCModeKey = env->GetFieldID(clsDsk,"compMode", "Ljava/lang/String;");
		fieldDeviceKey = env->GetFieldID(clsDsk,"deviceKey","I");
		fieldIsIDEDisk = env->GetFieldID(clsDsk, "isIDEDisk", "Z");
		fieldIsSATADisk = env->GetFieldID(clsDsk, "isSATADisk", "Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetsnapDiskList != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotURL, avmsnapURL);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringSnapURL = env->NewStringUTF(avmsnapURL.c_str());

		log.writeDebugLog(_T("Calling the JAVA method...")); 
		jobjWOArr = (jobjectArray) env->CallObjectMethod(objectInstance,midGetsnapDiskList,StringvmName, StringvmUUID, StringSnapURL);
		if(diskList != NULL)
		{
			delete[] (diskList);
			diskList = NULL;
		}
		if(jobjWOArr != NULL)
		{

			jsize vmDiskLength =  env->GetArrayLength(jobjWOArr);
			cout << "Number of disks " << vmDiskLength << endl;

			*count = vmDiskLength;

			diskList = new Disk_Info[vmDiskLength];

			for(jsize i = 0; i < vmDiskLength; i++)
			{
				jobject tempObj = env->GetObjectArrayElement(jobjWOArr, i);
				jboolean isESXVM = env->IsInstanceOf(tempObj, clsDsk);

				if(!isESXVM)
				{
					log.writeDebugLog(_T("This is not a disk object"));
					goto invokejavavirtualmachine_error;
				}

				jstring diskURL = (jstring)env->GetObjectField(tempObj, fieldDiskURL);
				jstring changeID = (jstring)env->GetObjectField(tempObj, fieldchageID);
				jstring diskMode = (jstring)env->GetObjectField(tempObj, fieldModeKey);
				jstring dCompMode = (jstring)env->GetObjectField(tempObj, fieldCModeKey);
				jint diskKey = (jint)env->GetIntField(tempObj, fieldDeviceKey);
				jboolean isdiskURL = FALSE, isChangeID = FALSE, isDiskMode = FALSE, isCMode =FALSE;
				const jchar *strDiskURL = NULL , *strChangeID = NULL, *strDMode = NULL, *strCMode = NULL;
				if(changeID)
				{
					strChangeID = env->GetStringChars(changeID, &isChangeID);
					if (isChangeID)
						wcscpy_s(diskList[i].chageID,NAME_SIZE,(const WCHAR *)strChangeID);
					else
						wcscpy_s(diskList[i].chageID,NAME_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(changeID, strChangeID);
				}
				else
					wcscpy_s(diskList[i].chageID,NAME_SIZE,(const WCHAR *)L"");
				if(diskURL)
				{
					strDiskURL = env->GetStringChars(diskURL, &isdiskURL);
					if(isdiskURL)
						wcscpy_s(diskList[i].vmDisk,DISKURL_SIZE,(const WCHAR *)strDiskURL);
					else
						wcscpy_s(diskList[i].vmDisk,DISKURL_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(diskURL, strDiskURL);		
				}
				else
					wcscpy_s(diskList[i].vmDisk,DISKURL_SIZE,(const WCHAR *)L"");
				if(diskMode)
				{
					strDMode = env->GetStringChars(diskMode, &isDiskMode);
					if(isDiskMode)
						wcscpy_s(diskList[i].diskMode,NAME_SIZE,(const WCHAR *)strDMode);
					else
						wcscpy_s(diskList[i].diskMode,NAME_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(diskMode, strDMode);
				}
				else
					wcscpy_s(diskList[i].diskMode,NAME_SIZE,(const WCHAR *)L"");

				if(dCompMode)
				{
					strCMode = env->GetStringChars(dCompMode, &isCMode);
					if(isCMode)
						wcscpy_s(diskList[i].diskCompMode,NAME_SIZE,(const WCHAR *)strCMode);
					else
						wcscpy_s(diskList[i].diskCompMode,NAME_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(dCompMode, strCMode);
				}
				else
					wcscpy_s(diskList[i].diskCompMode,NAME_SIZE,(const WCHAR *)L"");

				diskList[i].deviceKey = diskKey;														
				diskList[i].bUseSWSnapshot = true;
				jboolean isIDEDisk = (jboolean)env->GetBooleanField(tempObj, fieldIsIDEDisk);
				jboolean isSATADisk = (jboolean)env->GetBooleanField(tempObj, fieldIsSATADisk);
				diskList[i].bIsIDEDisk = isIDEDisk;//(jboolean)env->GetBooleanField(tempObj, fieldIsIDEDisk);
				diskList[i].bIsSATADisk = isSATADisk;// (jboolean)env->GetBooleanField(tempObj, fieldIsSATADisk);
			}
		}
		else
		{
			log.writeDebugLog(_T("getSnapShotDiskInfo returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	log.writeDebugLog(_T("Returning from getSnapShotDiskInfo."));
	return diskList;
}

AdrDisk_Info*  NativeClass::getSnapShotAdrDiskInfo(WCHAR* vmName, WCHAR*  vmUUID, WCHAR* snapshotURL, int *count)
{
	jclass clsV2P = NULL;
	jclass clsDsk = NULL;
	jobjectArray jobjWOArr = NULL;
	jmethodID midGetsnapDiskList = NULL;
	jfieldID fieldDiskURL = NULL;
	jfieldID fieldunitNo = NULL;
	jfieldID fieldDiskSize = NULL;
	jfieldID fieldDiskType = NULL;
	jfieldID fieldDiskBusDesc = NULL;
	jfieldID fieldDiskControllerId = NULL;
	jfieldID fieldDiskPosition = NULL;
	string avmName;
	string avmUUID;
	string avmsnapURL;

	V2PNFlog log;

	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif	

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	clsDsk = env->FindClass("com/ca/arcflash/ha/vmwaremanager/AdrDisk_Info");


	if (clsV2P != NULL && clsDsk != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId...")); 
		midGetsnapDiskList  = env->GetMethodID(clsV2P,"getsnapShotADRDiskInfo","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)[Lcom/ca/arcflash/ha/vmwaremanager/AdrDisk_Info;");
		fieldDiskURL = env->GetFieldID(clsDsk,"diskURL", "Ljava/lang/String;");
		fieldunitNo = env->GetFieldID(clsDsk,"unitNumber", "Ljava/lang/String;");
		fieldDiskSize = env->GetFieldID(clsDsk,"diskSize","J");
		fieldDiskType = env->GetFieldID(clsDsk,"diskType","Ljava/lang/String;");
		fieldDiskBusDesc = env->GetFieldID(clsDsk,"diskSummary","Ljava/lang/String;");
		fieldDiskControllerId = env->GetFieldID(clsDsk,"diskControllerId","J");
		fieldDiskPosition = env->GetFieldID(clsDsk,"diskPosition","J");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetsnapDiskList != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotURL, avmsnapURL);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringSnapURL = env->NewStringUTF(avmsnapURL.c_str());

		log.writeDebugLog(_T("Calling the JAVA method...")); 
		jobjWOArr = (jobjectArray) env->CallObjectMethod(objectInstance,midGetsnapDiskList,StringvmName, StringvmUUID, StringSnapURL);
		if(adrDiskList != NULL)
		{
			delete[] (adrDiskList);
			adrDiskList = NULL;
		}
		if(jobjWOArr != NULL)
		{

			jsize vmDiskLength =  env->GetArrayLength(jobjWOArr);
			cout << "Number of disks " << vmDiskLength << endl;

			*count = vmDiskLength;

			adrDiskList = new AdrDisk_Info[vmDiskLength];

			for(jsize i = 0; i < vmDiskLength; i++)
			{
				jobject tempObj = env->GetObjectArrayElement(jobjWOArr, i);
				jboolean isESXVM = env->IsInstanceOf(tempObj, clsDsk);

				if(!isESXVM)
				{
					log.writeDebugLog(_T("This is not a disk object"));
					goto invokejavavirtualmachine_error;
				}

				jstring diskURL = (jstring)env->GetObjectField(tempObj, fieldDiskURL);
				jstring unitNo = (jstring)env->GetObjectField(tempObj, fieldunitNo);
				jlong diskSize = (jlong)env->GetIntField(tempObj, fieldDiskSize);
				jstring diskType = (jstring)env->GetObjectField(tempObj, fieldDiskType);
				jstring diskBusDesc = (jstring)env->GetObjectField(tempObj, fieldDiskBusDesc);
				jlong diskControllerId = (jlong)env->GetIntField(tempObj, fieldDiskControllerId);
				jlong diskPosition = (jlong)env->GetIntField(tempObj, fieldDiskPosition);
				jboolean isdiskURL = FALSE, isUnitNo = FALSE, isDiskType = FALSE, isBusDesc = FALSE;
				const jchar *strDiskURL = NULL , *strUnitNo = NULL, *strDType = NULL, *strBusDesc = NULL;
				if(unitNo)
				{
					strUnitNo = env->GetStringChars(unitNo, &isUnitNo);
					if (isUnitNo)
						wcscpy_s(adrDiskList[i].diskUnitNumber,NAME_SIZE,(const WCHAR *)strUnitNo);
					else
						wcscpy_s(adrDiskList[i].diskUnitNumber,NAME_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(unitNo, strUnitNo);
				}
				else
					wcscpy_s(adrDiskList[i].diskUnitNumber,NAME_SIZE,(const WCHAR *)L"");
				if(diskURL)
				{
					strDiskURL = env->GetStringChars(diskURL, &isdiskURL);
					if(isdiskURL)
						wcscpy_s(adrDiskList[i].vmDisk,DISKURL_SIZE,(const WCHAR *)strDiskURL);
					else
						wcscpy_s(adrDiskList[i].vmDisk,DISKURL_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(diskURL, strDiskURL);		
				}
				else
					wcscpy_s(adrDiskList[i].vmDisk,DISKURL_SIZE,(const WCHAR *)L"");
				adrDiskList[i].diskSize = diskSize;
				if(diskType)
				{
					strDType = env->GetStringChars(diskType, &isDiskType);
					if(isdiskURL)
						wcscpy_s(adrDiskList[i].diskType,DISKURL_SIZE,(const WCHAR *)strDType);
					else
						wcscpy_s(adrDiskList[i].diskType,DISKURL_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(diskType, strDType);		
				}
				else
					wcscpy_s(adrDiskList[i].diskType,DISKURL_SIZE,(const WCHAR *)L"");
				if(diskBusDesc)
				{
					strBusDesc = env->GetStringChars(diskBusDesc, &isBusDesc);
					if(isBusDesc)
						wcscpy_s(adrDiskList[i].diskBusDesc,NAME_SIZE,(const WCHAR *)strBusDesc);
					else
						wcscpy_s(adrDiskList[i].diskBusDesc,NAME_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(diskBusDesc, strBusDesc);
				}
				else
					wcscpy_s(adrDiskList[i].diskBusDesc,NAME_SIZE,(const WCHAR *)L"");
				adrDiskList[i].diskControllerId = diskControllerId;
				adrDiskList[i].diskPosition = diskPosition;
			}
		}
		else
		{
			log.writeDebugLog(_T("getSnapShotDiskInfo returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	log.writeDebugLog(_T("Returning from getSnapShotDiskInfo."));
	return adrDiskList;
}

AdrDisk_Info*  NativeClass::getVMAdrDiskInfo(WCHAR* vmName, WCHAR*  vmUUID, int *count)
{
	jclass clsV2P = NULL;
	jclass clsDsk = NULL;
	jobjectArray jobjWOArr = NULL;
	jmethodID midGetsnapDiskList = NULL;
	jfieldID fieldDiskURL = NULL;
	jfieldID fieldunitNo = NULL;
	jfieldID fieldDiskSize = NULL;
	jfieldID fieldDiskType = NULL;
	jfieldID fieldDiskBusDesc = NULL;
	jfieldID fieldDiskControllerId = NULL;
	jfieldID fieldDiskPosition = NULL;
	string avmName;
	string avmUUID;

	V2PNFlog log;

	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}

	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	clsV2P = env->GetObjectClass(objectInstance);
	clsDsk = env->FindClass("com/ca/arcflash/ha/vmwaremanager/AdrDisk_Info");


	if (clsV2P != NULL && clsDsk != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId..."));
		midGetsnapDiskList = env->GetMethodID(clsV2P, "getVMADRDiskInfo", "(Ljava/lang/String;Ljava/lang/String;)[Lcom/ca/arcflash/ha/vmwaremanager/AdrDisk_Info;");
		fieldDiskURL = env->GetFieldID(clsDsk, "diskURL", "Ljava/lang/String;");
		fieldunitNo = env->GetFieldID(clsDsk, "unitNumber", "Ljava/lang/String;");
		fieldDiskSize = env->GetFieldID(clsDsk, "diskSize", "J");
		fieldDiskType = env->GetFieldID(clsDsk, "diskType", "Ljava/lang/String;");
		fieldDiskBusDesc = env->GetFieldID(clsDsk, "diskSummary", "Ljava/lang/String;");
		fieldDiskControllerId = env->GetFieldID(clsDsk, "diskControllerId", "J");
		fieldDiskPosition = env->GetFieldID(clsDsk, "diskPosition", "J");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class"));
		goto invokejavavirtualmachine_error;
	}

	if (midGetsnapDiskList != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());

		log.writeDebugLog(_T("Calling the JAVA method..."));
		jobjWOArr = (jobjectArray)env->CallObjectMethod(objectInstance, midGetsnapDiskList, StringvmName, StringvmUUID);
		if (adrDiskList != NULL)
		{
			delete[] (adrDiskList);
			adrDiskList = NULL;
		}
		if (jobjWOArr != NULL)
		{

			jsize vmDiskLength = env->GetArrayLength(jobjWOArr);
			cout << "Number of disks " << vmDiskLength << endl;

			*count = vmDiskLength;

			adrDiskList = new AdrDisk_Info[vmDiskLength];

			for (jsize i = 0; i < vmDiskLength; i++)
			{
				jobject tempObj = env->GetObjectArrayElement(jobjWOArr, i);
				jboolean isESXVM = env->IsInstanceOf(tempObj, clsDsk);

				if (!isESXVM)
				{
					log.writeDebugLog(_T("This is not a disk object"));
					goto invokejavavirtualmachine_error;
				}

				jstring diskURL = (jstring)env->GetObjectField(tempObj, fieldDiskURL);
				jstring unitNo = (jstring)env->GetObjectField(tempObj, fieldunitNo);
				jlong diskSize = (jlong)env->GetIntField(tempObj, fieldDiskSize);
				jstring diskType = (jstring)env->GetObjectField(tempObj, fieldDiskType);
				jstring diskBusDesc = (jstring)env->GetObjectField(tempObj, fieldDiskBusDesc);
				jlong diskControllerId = (jlong)env->GetIntField(tempObj, fieldDiskControllerId);
				jlong diskPosition = (jlong)env->GetIntField(tempObj, fieldDiskPosition);
				jboolean isdiskURL = FALSE, isUnitNo = FALSE, isDiskType = FALSE, isBusDesc = FALSE;
				const jchar *strDiskURL = NULL, *strUnitNo = NULL, *strDType = NULL, *strBusDesc = NULL;
				if (unitNo)
				{
					strUnitNo = env->GetStringChars(unitNo, &isUnitNo);
					if (isUnitNo)
						wcscpy_s(adrDiskList[i].diskUnitNumber, NAME_SIZE, (const WCHAR *)strUnitNo);
					else
						wcscpy_s(adrDiskList[i].diskUnitNumber, NAME_SIZE, (const WCHAR *)L"");
					env->ReleaseStringChars(unitNo, strUnitNo);
				}
				else
					wcscpy_s(adrDiskList[i].diskUnitNumber, NAME_SIZE, (const WCHAR *)L"");
				if (diskURL)
				{
					strDiskURL = env->GetStringChars(diskURL, &isdiskURL);
					if (isdiskURL)
						wcscpy_s(adrDiskList[i].vmDisk, DISKURL_SIZE, (const WCHAR *)strDiskURL);
					else
						wcscpy_s(adrDiskList[i].vmDisk, DISKURL_SIZE, (const WCHAR *)L"");
					env->ReleaseStringChars(diskURL, strDiskURL);
				}
				else
					wcscpy_s(adrDiskList[i].vmDisk, DISKURL_SIZE, (const WCHAR *)L"");
				adrDiskList[i].diskSize = diskSize;
				if (diskType)
				{
					strDType = env->GetStringChars(diskType, &isDiskType);
					if (isdiskURL)
						wcscpy_s(adrDiskList[i].diskType, DISKURL_SIZE, (const WCHAR *)strDType);
					else
						wcscpy_s(adrDiskList[i].diskType, DISKURL_SIZE, (const WCHAR *)L"");
					env->ReleaseStringChars(diskType, strDType);
				}
				else
					wcscpy_s(adrDiskList[i].diskType, DISKURL_SIZE, (const WCHAR *)L"");
				if (diskBusDesc)
				{
					strBusDesc = env->GetStringChars(diskBusDesc, &isBusDesc);
					if (isBusDesc)
						wcscpy_s(adrDiskList[i].diskBusDesc, NAME_SIZE, (const WCHAR *)strBusDesc);
					else
						wcscpy_s(adrDiskList[i].diskBusDesc, NAME_SIZE, (const WCHAR *)L"");
					env->ReleaseStringChars(diskBusDesc, strBusDesc);
				}
				else
					wcscpy_s(adrDiskList[i].diskBusDesc, NAME_SIZE, (const WCHAR *)L"");
				adrDiskList[i].diskControllerId = diskControllerId;
				adrDiskList[i].diskPosition = diskPosition;
			}
		}
		else
		{
			log.writeDebugLog(_T("getVMAdrDiskInfo returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}

invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	log.writeDebugLog(_T("Returning from getVMAdrDiskInfo."));
	return adrDiskList;
}

WCHAR* NativeClass::takeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName, error_Info* info, bool quiesce /*= true*/)
{
	jclass clsV2P = NULL;
	jclass clserriInfo = NULL;
	jmethodID midtakeSnapShot = NULL;
	jfieldID fieldsnapURL = NULL;
	jfieldID fielderrorID = NULL;
	jfieldID fielderrorString = NULL;
	string avmName;
	string avmUUID;
	string asnapshotName;
	V2PNFlog log;

    //initialize default return value
    wcscpy_s(snapShot_URL, PATH_SIZE, L"\0");

    info->errorCode = -1;
    wcscpy_s(info->erroString, ERROR_STRING_SIZE, L"\0");
    info->messageCode = 0;

	log.writeDebugLog(_T("Attempting to take snapshot of VM %ls. isQuiesce=%u."), vmName, (quiesce ? TRUE : FALSE));
	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
		
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	clserriInfo = env->FindClass("com/ca/arcflash/ha/vmwaremanager/snaperrorInfo");

	
	if (clsV2P != NULL && clserriInfo != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId..."));
		midtakeSnapShot  = env->GetMethodID(clsV2P,"takeSnapShotEx","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lcom/ca/arcflash/ha/vmwaremanager/snaperrorInfo;");
		fieldsnapURL = env->GetFieldID(clserriInfo,"snapURL", "Ljava/lang/String;");
		fielderrorString = env->GetFieldID(clserriInfo,"errorString", "Ljava/lang/String;");
		fielderrorID = env->GetFieldID(clserriInfo,"errorCode","I");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midtakeSnapShot != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotName, asnapshotName);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringsnapshotName = env->NewStringUTF(asnapshotName.c_str());
		jboolean bQuiesce = quiesce ? TRUE : FALSE;

		log.writeDebugLog(_T("Calling the JAVA method...")); 
		jstring tempObj = (jstring) env->CallObjectMethod(objectInstance,midtakeSnapShot,StringvmName,StringvmUUID,StringsnapshotName, bQuiesce);
		if(tempObj != NULL)
		{
			jboolean isvalidObject = env->IsInstanceOf(tempObj, clserriInfo);

			if(!isvalidObject)
			{
				log.writeDebugLog(_T("This is not a valid disk object"));
				goto invokejavavirtualmachine_error;
			}

			jstring errorstring = (jstring)env->GetObjectField(tempObj, fielderrorString);
			jstring snapURL = (jstring)env->GetObjectField(tempObj, fieldsnapURL);
			jint errorCode = (jint)env->GetIntField(tempObj, fielderrorID);
			jboolean issnapURL = FALSE, iserrorCode = FALSE, iserrorString = FALSE;
			const jchar *strsnapURL = NULL, *strEString = NULL;
			if(snapURL)
			{
				strsnapURL = env->GetStringChars(snapURL, &issnapURL);
				if(issnapURL)
					wcscpy_s(snapShot_URL,DISKURL_SIZE,(const WCHAR *)strsnapURL);
				else
					wcscpy_s(snapShot_URL,DISKURL_SIZE,(const WCHAR *)L"");
				env->ReleaseStringChars(snapURL, strsnapURL);		
			}
			else
				wcscpy_s(snapShot_URL,DISKURL_SIZE,(const WCHAR *)L"");
			if(errorstring)
			{
				strEString = env->GetStringChars(errorstring, &iserrorString);
				if(iserrorString)
					wcscpy_s(info->erroString, _countof(info->erroString),(const WCHAR *)strEString); //<sonmi01>2014-3-10 #102153_v2p_dll_crash error string is too long
				else
					wcscpy_s(info->erroString,NAME_SIZE,(const WCHAR *)L"");
				env->ReleaseStringChars(errorstring, strEString);
			}
			else
				wcscpy_s(info->erroString,NAME_SIZE,(const WCHAR *)L"");

			info->errorCode = errorCode;														
		}
		else
		{
			log.writeDebugLog(_T("takeSnapShot API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	log.writeDebugLog(_T("Returning from takeSnapShot with %ls"),snapShot_URL);
	return snapShot_URL;

}

WCHAR* NativeClass::checkandtakeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName)
{
	jclass clsV2P = NULL;
	jmethodID midtakeSnapShot = NULL;
	jfieldID fieldESX = NULL;
	jfieldID fieldDC = NULL;
	string avmName;
	string avmUUID;
	string asnapshotName;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to take snapshot of VM %ls."), vmName);
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midtakeSnapShot  = env->GetMethodID(clsV2P,"checkandTakeSnapshot","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midtakeSnapShot != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotName, asnapshotName);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringsnapshotName = env->NewStringUTF(asnapshotName.c_str());

		wcscpy_s(snapShot_URL,PATH_SIZE,L"\0");
		jstring tempObj = (jstring) env->CallObjectMethod(objectInstance,midtakeSnapShot,StringvmName,StringvmUUID,StringsnapshotName);
		if(tempObj != NULL)
		{
			jboolean isObj = FALSE;
			const jchar *strDS = env->GetStringChars(tempObj, &isObj);
			if(isObj)
			{
				wcscpy_s(snapShot_URL, PATH_SIZE, (const WCHAR *)strDS);
			}
			env->ReleaseStringChars(tempObj, strDS);
		}
		else
		{
			log.writeDebugLog(_T("takeSnapShot API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return snapShot_URL;

}

WCHAR* NativeClass::getsnapshotCTF(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL)
{
	jclass clsV2P = NULL;

	jmethodID midgetSnapShot = NULL;
	jfieldID fieldESX = NULL;
	jfieldID fieldDC = NULL;
	string avmName;
	string avmUUID;
	string asnapshotURL;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to take snapshot CTF file of VM %ls."), vmName);
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midgetSnapShot  = env->GetMethodID(clsV2P,"getsnapshotCTF","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midgetSnapShot != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotURL, asnapshotURL);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringsnapshotURL = env->NewStringUTF(asnapshotURL.c_str());

		wcscpy_s(snapshotCTF,PATH_SIZE,L"\0");
		jstring tempObj = (jstring) env->CallObjectMethod(objectInstance,midgetSnapShot,StringvmName,StringvmUUID,StringsnapshotURL);
		if(tempObj != NULL)
		{
			jboolean isObj = FALSE;
			const jchar *strDS = env->GetStringChars(tempObj, &isObj);
			if(isObj)
			{
				wcscpy_s(snapshotCTF,PATH_SIZE,(const WCHAR *)strDS);
			}
			env->ReleaseStringChars(tempObj, strDS);
		}
		else
		{
			log.writeDebugLog(_T("getsnapshotCTF API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return snapshotCTF;

}

WCHAR* NativeClass::getparentSnapshot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL)
{
	jclass clsV2P = NULL;

	jmethodID midgetSnapShot = NULL;
	jfieldID fieldESX = NULL;
	jfieldID fieldDC = NULL;
	string avmName;
	string avmUUID;
	string asnapshotURL;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to take snapshot CTF file of VM %ls."), vmName);
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midgetSnapShot  = env->GetMethodID(clsV2P,"getparentSnapshot","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midgetSnapShot != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotURL, asnapshotURL);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringsnapshotURL = env->NewStringUTF(asnapshotURL.c_str());

		wcscpy_s(parentSnapshot,PATH_SIZE,L"\0");
		jstring tempObj = (jstring) env->CallObjectMethod(objectInstance,midgetSnapShot,StringvmName,StringvmUUID,StringsnapshotURL);
		if(tempObj != NULL)
		{
			jboolean isObj = FALSE;
			const jchar *strDS = env->GetStringChars(tempObj, &isObj);
			if(isObj)
			{
				wcscpy_s(parentSnapshot,PATH_SIZE,(const WCHAR *)strDS);
			}
			env->ReleaseStringChars(tempObj, strDS);
		}
		else
		{
			log.writeDebugLog(_T("getparentSnapshot API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return parentSnapshot;

}

WCHAR* NativeClass::getVMMoref(WCHAR* vmName, WCHAR* vmUUID)
{
	jclass clsV2P = NULL;
	jmethodID midgetVMMoref = NULL;
	string avmName;
	string avmUUID;
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to get moref of VM %ls."), vmName);
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}
	memset(vmMoref, 0, sizeof(vmMoref));
	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId...")); 
		midgetVMMoref  = env->GetMethodID(clsV2P,"getVMMoref","(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midgetVMMoref != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());

		wcscpy_s(vmMoref,PATH_SIZE,L"\0");
		log.writeDebugLog(_T("Calling the JAVA method...")); 
		jstring tempObj = (jstring) env->CallObjectMethod(objectInstance,midgetVMMoref,StringvmName,StringvmUUID);
		if(tempObj != NULL)
		{
			jboolean isObj = FALSE;
			const jchar *strDS = env->GetStringChars(tempObj, &isObj);
			if(isObj)
			{
				wcscpy_s(vmMoref,PATH_SIZE,(const WCHAR *)strDS);
			}
			env->ReleaseStringChars(tempObj, strDS);
		}
		else
		{
			log.writeDebugLog(_T("getVMMoref API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	log.writeDebugLog(_T("Returning from getVMMoref with %ls"),vmMoref);
	return vmMoref;

}

WCHAR* NativeClass::getVMVersion(WCHAR* vmName, WCHAR* vmUUID)
{
	jclass clsV2P = NULL;
	jmethodID midgetVMMoref = NULL;
	string avmName;
	string avmUUID;
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to get version of VM %ls."), vmName);
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId...")); 
		midgetVMMoref  = env->GetMethodID(clsV2P,"getVMVersion","(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midgetVMMoref != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());

		wcscpy_s(vmMoref,PATH_SIZE,L"\0");
		log.writeDebugLog(_T("Calling the JAVA method...")); 
		jstring tempObj = (jstring) env->CallObjectMethod(objectInstance,midgetVMMoref,StringvmName,StringvmUUID);
		if(tempObj != NULL)
		{
			jboolean isObj = FALSE;
			const jchar *strDS = env->GetStringChars(tempObj, &isObj);
			if(isObj)
			{
				wcscpy_s(vmMoref,PATH_SIZE,(const WCHAR *)strDS);
			}
			env->ReleaseStringChars(tempObj, strDS);
		}
		else
		{
			log.writeDebugLog(_T("getVMMoref API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	log.writeDebugLog(_T("Returning from getVMMoref with %ls"),vmMoref);
	return vmMoref;

}

BOOL NativeClass::revertSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL)
{
	jclass clsV2P = NULL;
	jmethodID midrevertSnapShot = NULL;
	jfieldID fieldESX = NULL;
	jfieldID fieldDC = NULL;
	string avmName;
	string avmUUID;
	string asnapshotURL;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;
	BOOL bResult = FALSE;
	jboolean jobj = NULL;
		
	log.writeDebugLog(_T("Attempting to revert the VM %ls to snapshot %ls."), vmName, snapshotURL);
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);



	if (clsV2P != NULL)
	{
		midrevertSnapShot  = env->GetMethodID(clsV2P,"revertSnapShot","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midrevertSnapShot != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotURL, asnapshotURL);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringsnapshotURL = env->NewStringUTF(asnapshotURL.c_str());

		jobj = (jboolean)env->CallObjectMethod(objectInstance,midrevertSnapShot,StringvmName,StringvmUUID,StringsnapshotURL);
		if(jobj)
			bResult = TRUE;

	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return bResult;
}

BOOL NativeClass::removeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL)
{
	jclass clsV2P = NULL;
	jmethodID midremoveSnapShot = NULL;
	jfieldID fieldESX = NULL;
	jfieldID fieldDC = NULL;
	string avmName;
	string avmUUID;
	string asnapshotURL;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;
	BOOL bResult = FALSE;
	jboolean jobj = NULL;

	log.writeDebugLog(_T("Attempting to remove snapshot %ls of VM %ls."), snapshotURL, vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM.\n"));
		return FALSE;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midremoveSnapShot  = env->GetMethodID(clsV2P,"removeSnapShot","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midremoveSnapShot != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotURL, asnapshotURL);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringsnapshotURL = env->NewStringUTF(asnapshotURL.c_str());

		jobj = (jboolean) env->CallObjectMethod(objectInstance,midremoveSnapShot,StringvmName,StringvmUUID,StringsnapshotURL);
		if(jobj)
			bResult =  TRUE;

	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return bResult;
}

BOOL NativeClass::removeSnapShotAsync(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL)
{
	jclass clsV2P = NULL;
	jmethodID midremoveSnapShot = NULL;
	jfieldID fieldESX = NULL;
	jfieldID fieldDC = NULL;
	string avmName;
	string avmUUID;
	string asnapshotURL;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;
	BOOL bResult = FALSE;
	jboolean jobj = NULL;

	log.writeDebugLog(_T("Attempting to remove snapshot %ls of VM %ls."), snapshotURL, vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM.\n"));
		return FALSE;
	}


	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midremoveSnapShot = env->GetMethodID(clsV2P, "removeSnapShotAsync", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class"));
		goto invokejavavirtualmachine_error;
	}

	if (midremoveSnapShot != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotURL, asnapshotURL);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringsnapshotURL = env->NewStringUTF(asnapshotURL.c_str());

		jobj = (jboolean)env->CallObjectMethod(objectInstance, midremoveSnapShot, StringvmName, StringvmUUID, StringsnapshotURL);
		if (jobj)
			bResult = TRUE;

	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}

invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return bResult;
}

BOOL NativeClass::removeSnapShotByName(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName)
{
	jclass clsV2P = NULL;
	jmethodID midremoveSnapShot = NULL;
	jfieldID fieldESX = NULL;
	jfieldID fieldDC = NULL;
	string avmName;
	string avmUUID;
	string asnapshotName;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;
	BOOL bResult = FALSE;
	jboolean jobj = NULL;

	log.writeDebugLog(_T("Attempting to remove snapshot %ls of VM %ls."), snapshotName, vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM.\n"));
		return FALSE;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midremoveSnapShot  = env->GetMethodID(clsV2P,"removeSnapShotByName","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midremoveSnapShot != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotName, asnapshotName);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringsnapshotName = env->NewStringUTF(asnapshotName.c_str());

		jobj = (jboolean) env->CallObjectMethod(objectInstance,midremoveSnapShot,StringvmName,StringvmUUID,StringsnapshotName);
		if(jobj)
			bResult =  TRUE;

	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return bResult;
}

BOOL NativeClass::powerOnVM(WCHAR* vmName, WCHAR* vmUUID)
{
	jclass clsV2P = NULL;
	jmethodID midpowerVM = NULL;
	jfieldID fieldESX = NULL;
	jfieldID fieldDC = NULL;
	string avmName;
	string avmUUID;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	BOOL pResult = FALSE;
	V2PNFlog log;
	log.writeDebugLog(_T("Attempting to power on VM %ls."), vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midpowerVM  = env->GetMethodID(clsV2P,"powerONVM","(Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midpowerVM != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		
		jboolean bResult = (jboolean)env->CallObjectMethod(objectInstance,midpowerVM,StringvmName,StringvmUUID);
		if(bResult)
			pResult = TRUE;
		else
			pResult = FALSE;

	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return pResult;
}

void NativeClass::powerOffVM(WCHAR* vmName, WCHAR* vmUUID)
{
	jclass clsV2P = NULL;
	jmethodID midpowerVM = NULL;
	jfieldID fieldESX = NULL;
	jfieldID fieldDC = NULL;
	string avmName;
	string avmUUID;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to power off VM %ls."), vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midpowerVM  = env->GetMethodID(clsV2P,"powerOFFVM","(Ljava/lang/String;Ljava/lang/String;)V");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midpowerVM != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		
		env->CallObjectMethod(objectInstance,midpowerVM,StringvmName,StringvmUUID);

	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return ;
}


int NativeClass::getVMPowerState(WCHAR* vmName, WCHAR*  vmUUID)
{
	jclass clsV2P = NULL;
	jmethodID miPState = NULL;
	string avmName;
	string avmUUID;
	int result = -1;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to get power state of VM %ls."), vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		miPState  = env->GetMethodID(clsV2P,"getVMPowerState","(Ljava/lang/String;Ljava/lang/String;)I");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(miPState != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		
		jint jObject = (jint)env->CallObjectMethod(objectInstance,miPState,StringvmName,StringvmUUID);
		result = jObject;
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return result;
}

int NativeClass::getVMToolsState(WCHAR* vmName, WCHAR*  vmUUID)
{
	jclass clsV2P = NULL;
	jmethodID miTState = NULL;
	string avmName;
	string avmUUID;
	int result = -1;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to get power state of VM %ls."), vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		miTState  = env->GetMethodID(clsV2P,"checkVMToolsVersion","(Ljava/lang/String;Ljava/lang/String;)I");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(miTState != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());

		jint jObject = (jint)env->CallObjectMethod(objectInstance,miTState,StringvmName,StringvmUUID);
		result = jObject;
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return result;
}

BOOL NativeClass::getFile(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  fileName, WCHAR*  localPath)
{
	jclass clsV2P = NULL;
	jmethodID midgetFile = NULL;
	string avmName;
	string avmUUID;
	BOOL result = FALSE;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to get file %ls from VM %ls."), fileName, vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midgetFile  = env->GetMethodID(clsV2P,"getFile","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midgetFile != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringfileName = _makeWCHARToJString(env, fileName);
		jstring StringlocalPath =  _makeWCHARToJString(env, localPath);

		jboolean bResult = (jboolean) env->CallObjectMethod(objectInstance,midgetFile,StringvmName,StringvmUUID,StringfileName,StringlocalPath);

		if(bResult)
		{
			result = TRUE;
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return result;

}

BOOL NativeClass::getDiskBitMap(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  diskURL, int deviceKey)
{
	jclass clsV2P = NULL;
	jmethodID midgetBitMap = NULL;
	string avmName;
	string avmUUID;
	string adiskURL;
	BOOL result = FALSE;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to get disk bit map for %ls from VM %ls."), diskURL, vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midgetBitMap  = env->GetMethodID(clsV2P,"getDiskBitMap","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midgetBitMap != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(diskURL, adiskURL);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringdiskURL = env->NewStringUTF(adiskURL.c_str());
		jint resultI = deviceKey;
		
		env->CallObjectMethod(objectInstance,midgetBitMap,StringvmName,StringvmUUID,StringdiskURL,resultI);
		result = TRUE;
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return result;

}

void NativeClass::deleteDiskBitMap(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  snapshotURL)
{
	jclass clsV2P = NULL;
	jmethodID middelBitMap = NULL;
	string avmName;
	string avmUUID;
	string asnapURL;
	BOOL result = FALSE;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to delete disk bit map of snapshot %ls from VM %ls."), snapshotURL, vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		middelBitMap  = env->GetMethodID(clsV2P,"deleteDiskBitMap","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(middelBitMap != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotURL, asnapURL);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringSnapURL = env->NewStringUTF(asnapURL.c_str());
		
		env->CallObjectMethod(objectInstance,middelBitMap,StringvmName,StringvmUUID,StringSnapURL);
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
}


BOOL NativeClass::setFileStream(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  fileName)
{
	jclass clsV2P = NULL;
	HANDLE jHandle = NULL;
	jmethodID midgetFile = NULL;
	string avmName;
	string avmUUID;
	jstring StringvmName;
	jstring StringvmUUID;
	jstring StringfileName;
	BOOL result = FALSE;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		midgetFile  = env->GetMethodID(clsV2P,"getFileStream","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}
	if(midgetFile != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);

		StringvmName = env->NewStringUTF(avmName.c_str());
		StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		StringfileName = _makeWCHARToJString(env, fileName);
		env->CallObjectMethod(objectInstance,midgetFile, StringvmName, StringvmUUID, StringfileName);
		result = TRUE;
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return result;

}

int NativeClass::readFileStream(BYTE* pBuff, __int64 offSet, int length, int* bytesRead)
{
	jclass clsV2P = NULL;
	jmethodID midgetFile = NULL;
	char* buff = NULL;
	int result = 0;
	jobject resultO;
	jobject resultL;
	jbyteArray jbArray; 
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;
	//jobject Object = (jobject)objectHandle;
	jbyte* pMin;

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}

	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midgetFile  = env->GetMethodID(clsV2P,"readFileStream","([BLjava/lang/Long;Ljava/lang/Integer;)I");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midgetFile != NULL)
	{
		jbArray = env->NewByteArray(length);
		resultL = _makeInteger(env, length);
		resultO = _makeLong(env, offSet);

		jint jObject = (jint)env->CallObjectMethod(objectInstance,midgetFile, jbArray, resultO, resultL);

		if(jObject > 0)
		{
			*bytesRead = jObject;
			result = 0;
			int byteArraySize= env->GetArrayLength(jbArray);
			pMin =(jbyte*)env->GetByteArrayElements(jbArray,JNI_FALSE);
			memcpy(pBuff, (BYTE*)pMin, byteArraySize);
			env->ReleaseByteArrayElements(jbArray, pMin, 0);
		}
		else
		{
			if(jObject == -1)
			{
				result = 1;
			}
			else if(jObject == -2)
			{
				result = -1;
			}
			else if(jObject == -3)
			{
				result = -2;
			}
			*bytesRead = 0;
			log.writeDebugLog(_T("Failed to read the file."));
		}

	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}
	env->DeleteLocalRef(jbArray);

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return result;
}
BOOL NativeClass::getVSSwriterfiles(WCHAR* vmName, WCHAR* vmUUID, wchar_t* snapshotId, wchar_t* pathToSave)
{
	V2PNFlog log;
	jclass clsCBM = NULL;
	jmethodID midgetVSSwriterfiles = NULL;
	
	string avmName;
	string avmUUID;
	string asnapshotId;

	log.writeDebugLog(_T("Attempting to get VSS writer files for VM %ls."), vmName);
	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	;
#else
	BOOL bRet = FALSE;
#endif
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
		
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsCBM = env->GetObjectClass(objectInstance);

	//clsCBM = env->FindClass("com/ca/arcflash/ha/vmwaremanager/ChangeBlockManager_Export");
	if (clsCBM != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId...")); 
		midgetVSSwriterfiles  = env->GetMethodID(clsCBM,"getVMManifestFile","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");																			 																	
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}
		
	if(midgetVSSwriterfiles != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());		
		UnicodeToUTF8(snapshotId, asnapshotId);
		jstring StringsnapshotId = env->NewStringUTF(asnapshotId.c_str());
		jstring StringpathToSave = _makeWCHARToJString(env, pathToSave);		

		log.writeDebugLog(_T("Calling the JAVA method...")); 
		jboolean tempObj = (jboolean) env->CallObjectMethod(objectInstance,midgetVSSwriterfiles,StringvmName,StringvmUUID,StringsnapshotId,StringpathToSave);		
		if(tempObj != NULL)
		{
			bRet = tempObj;			
		}
		else
		{
			bRet = FALSE;
			log.writeDebugLog(_T("Call to Java API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	log.writeDebugLog(_T("Returning from getVSSWriteFiles with %d."),bRet);
	return bRet;
}

BOOL NativeClass::setVMNVRamFile(WCHAR* vmName, WCHAR* vmUUID, wchar_t* nvRamFile)
{
	V2PNFlog log;
	jclass clsCBM = NULL;
	jmethodID midsetnvRamFile = NULL;

	string avmName;
	string avmUUID;

	log.writeDebugLog(_T("Attempting to set nvram file for VM %ls."), vmName);
	log.writeDebugLog(_T("Attaching to JVM..."));

#ifdef __OLD_JNI_ENV__
	;
#else
	BOOL bRet = FALSE;
#endif
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsCBM = env->GetObjectClass(objectInstance);

	//clsCBM = env->FindClass("com/ca/arcflash/ha/vmwaremanager/ChangeBlockManager_Export");
	if (clsCBM != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId...")); 
		midsetnvRamFile  = env->GetMethodID(clsCBM,"setVMNVRamFile","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");																			 																	
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midsetnvRamFile != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());		
		jstring StringpathNVRam = _makeWCHARToJString(env, nvRamFile);		

		log.writeDebugLog(_T("Calling the JAVA method...")); 
		jboolean tempObj = (jboolean) env->CallObjectMethod(objectInstance,midsetnvRamFile,StringvmName,StringvmUUID,StringpathNVRam);		
		if(tempObj != NULL)
		{
			bRet = tempObj;			
		}
		else
		{
			bRet = FALSE;
			log.writeDebugLog(_T("Call to Java API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	log.writeDebugLog(_T("Returning from setnvRamFile with %d."),bRet);
	return bRet;
}

WCHAR* NativeClass::getVMNVRAMFile(WCHAR* vmName, WCHAR* vmUUID,wchar_t* pathToSave)
{
	V2PNFlog log;
	jclass clsCBM = NULL;
	jmethodID midgetVMNVRAM = NULL;

	string avmName;
	string avmUUID;

	log.writeDebugLog(_T("Attempting to get NVRAM for VM %ls."), vmName);
	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsCBM = env->GetObjectClass(objectInstance);

	if (clsCBM != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId...")); 
		midgetVMNVRAM  = env->GetMethodID(clsCBM,"getVMNVRAMFile","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");																			 																	
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midgetVMNVRAM != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());		
		jstring StringpathToSave = _makeWCHARToJString(env, pathToSave);		

		log.writeDebugLog(_T("Calling the JAVA method...")); 
		jstring tempObj = (jstring)env->CallObjectMethod(objectInstance,midgetVMNVRAM,StringvmName,StringvmUUID,StringpathToSave);		
		if(tempObj != NULL)
		{
			jboolean isObj = FALSE;
			const jchar *strDS = env->GetStringChars(tempObj, &isObj);
			if(isObj)
			{
				wcscpy_s(nvramFile, PATH_SIZE, (const WCHAR *)strDS);
			}
			env->ReleaseStringChars(tempObj, strDS);
		}
		else
		{
			log.writeDebugLog(_T("Failed to get nvram file."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	if(wcslen(nvramFile) > 0)
		return nvramFile;
	else
		return NULL;
}

BOOL NativeClass::getSnapshotConfigInfo(WCHAR* vmName, WCHAR* vmUUID, wchar_t* snapshotId, wchar_t* pathToSave)
{
	V2PNFlog log;
	jclass clsCBM = NULL;
	jmethodID midgetSnapshotConfigInfo = NULL;
	
	string avmName;
	string avmUUID;
	string asnapshotId;

	log.writeDebugLog(_T("Attempting to get SnapshotConfigInfo for VM %ls."), vmName);
	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	;
#else
	BOOL bRet = FALSE;
#endif
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
		
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsCBM = env->GetObjectClass(objectInstance);

	//clsCBM = env->FindClass("com/ca/arcflash/ha/vmwaremanager/ChangeBlockManager_Export");
	if (clsCBM != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId...")); 
		midgetSnapshotConfigInfo  = env->GetMethodID(clsCBM,"getSnapshotConfigInfo","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");																			 																	
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}
		
	if(midgetSnapshotConfigInfo != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());		
		UnicodeToUTF8(snapshotId, asnapshotId);
		jstring StringsnapshotId = env->NewStringUTF(asnapshotId.c_str());
		jstring StringpathToSave = _makeWCHARToJString(env, pathToSave);		

		log.writeDebugLog(_T("Calling the JAVA method...")); 
		jboolean tempObj = (jboolean) env->CallObjectMethod(objectInstance,midgetSnapshotConfigInfo,StringvmName,StringvmUUID,StringsnapshotId,StringpathToSave);		
		if(tempObj != NULL)
		{
			bRet = tempObj;			
		}
		else
		{
			bRet = FALSE;
			log.writeDebugLog(_T("Call to Java API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	log.writeDebugLog(_T("Returning from getSnapshotConfigInfo with %d."),bRet);
	return bRet;
}

int NativeClass::checkAndEnableChangeBlockTracking(WCHAR* vmName, WCHAR* vmUUID)
{
	V2PNFlog log;
	jclass clsCBM = NULL;
	jmethodID midenableChangeBlockTracking = NULL;
	int iRet = 0;
	
	string avmName;
	string avmUUID;
	
	log.writeDebugLog(_T("Attempting to check and enable CBT for VM %ls."), vmName);
	
	log.writeDebugLog(_T("Attaching to JVM..."));
	
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
		
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return iRet;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return iRet;
	}
	clsCBM = env->GetObjectClass(objectInstance);

	//clsCBM = env->FindClass("com/ca/arcflash/ha/vmwaremanager/ChangeBlockManager_Export");
	if (clsCBM != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId...")); 
		midenableChangeBlockTracking  = env->GetMethodID(clsCBM,"CheckAndEnableCBT","(Ljava/lang/String;Ljava/lang/String;)I");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}
	
	if(midenableChangeBlockTracking != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());		

		log.writeDebugLog(_T("Calling the JAVA method...")); 
		jint tempObj = (jint) env->CallObjectMethod(objectInstance,midenableChangeBlockTracking,StringvmName,StringvmUUID);		
		if(tempObj != NULL)
		{
			iRet = tempObj;			
		}
		else
		{
			log.writeDebugLog(_T("Call to Java API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	log.writeDebugLog(_T("Returning from enableChangeBlockTracking with %d"),iRet);
	return iRet;
}
BOOL NativeClass::enableDiskUUIDForVM(WCHAR* vmName, WCHAR* vmUUID, BOOL bEnable)
{
	V2PNFlog log;
	jclass clsCBM = NULL;
	jmethodID midenableDiskUUIDForVM = NULL;
	
	string avmName;
	string avmUUID;
	
	if (bEnable)	
		log.writeDebugLog(_T("Attempting to enable disk UUID for VM %ls."), vmName);
	else
		log.writeDebugLog(_T("Attempting to disable disk UUID for VM %ls."), vmName);

	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	;
#else
	BOOL bRet = FALSE;
#endif
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
		
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsCBM = env->GetObjectClass(objectInstance);

	//clsCBM = env->FindClass("com/ca/arcflash/ha/vmwaremanager/ChangeBlockManager_Export");
	if (clsCBM != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId...")); 
		if(bEnable)
			midenableDiskUUIDForVM  = env->GetMethodID(clsCBM,"enableDiskUUIDForVM","(Ljava/lang/String;Ljava/lang/String;)Z");
		else
			midenableDiskUUIDForVM  = env->GetMethodID(clsCBM,"disableDiskUUIDForVM","(Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}
	
	if(midenableDiskUUIDForVM != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());		

		log.writeDebugLog(_T("Calling the JAVA method...")); 
		jboolean tempObj = (jboolean) env->CallObjectMethod(objectInstance,midenableDiskUUIDForVM,StringvmName,StringvmUUID);		
		if(tempObj != NULL)
		{
			bRet = tempObj;			
		}
		else
		{
			bRet = FALSE;
			log.writeDebugLog(_T("Call to Java API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	log.writeDebugLog(_T("Returning from enableDiskUUIDForVM with %d"),bRet);
	return bRet;
}

int NativeClass::enableChangeBlockTracking(WCHAR* vmName, WCHAR* vmUUID, BOOL bEnable)
{
	V2PNFlog log;
	jclass clsCBM = NULL;
	jmethodID midenableChangeBlockTracking = NULL;
	int iRet = 0;
	
	string avmName;
	string avmUUID;
	
	if (bEnable)	
		log.writeDebugLog(_T("Attempting to enable CBT for VM %ls."), vmName);
	else
		log.writeDebugLog(_T("Attempting to disable CBT for VM %ls."), vmName);

	log.writeDebugLog(_T("Attaching to JVM..."));

#ifdef __OLD_JNI_ENV__
	;
#else
	BOOL bRet = FALSE;
#endif

	
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
		
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return iRet;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return iRet;
	}
	clsCBM = env->GetObjectClass(objectInstance);

	//clsCBM = env->FindClass("com/ca/arcflash/ha/vmwaremanager/ChangeBlockManager_Export");
	if (clsCBM != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId...")); 
		if(bEnable)
			midenableChangeBlockTracking  = env->GetMethodID(clsCBM,"enableChangeTracking","(Ljava/lang/String;Ljava/lang/String;)I");
		else
			midenableChangeBlockTracking  = env->GetMethodID(clsCBM,"disableChangeTracking","(Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}
	
	if(midenableChangeBlockTracking != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());		

		log.writeDebugLog(_T("Calling the JAVA method...")); 
		jint tempObj = (jint) env->CallObjectMethod(objectInstance,midenableChangeBlockTracking,StringvmName,StringvmUUID);		
		if(tempObj != NULL)
		{
			iRet = tempObj;
			if(bEnable)
				log.writeDebugLog(_T("Enabling CBT returned %d."), iRet);
			else
				log.writeDebugLog(_T("Disabling CBT returned %d."), iRet);
		}
		else
		{
			iRet = 0;
			log.writeDebugLog(_T("Call to Java API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	log.writeDebugLog(_T("Returning from enableChangeBlockTracking with %d"),iRet);
	return iRet;
}

int NativeClass::getUsedDiskBlocks(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, WCHAR* diskChangeId,int DiskDeviceKey,PLARGE_INTEGER pbitmapSize, PLARGE_INTEGER pUsedSectorCount,WCHAR* bitmapFilePath,int chunkSize, int sectorSize)
{
	jclass clsCBM = NULL ;
	jclass clsDiskSectorInfo = NULL;
	
	jfieldID fieldTotalNumSectors = NULL;
	jfieldID fieldUsedNumSectors = NULL;
	jobject resultI;	
	jobject resultI1;	
	jobject resultI2;	
	jmethodID midgetUsedDiskBlocks = NULL;
	string avmName;
	string avmUUID;
	string avmsnapURL;
	string avmDiskChangeId;	
	
	V2PNFlog log;
	int iResult = -1;
	
	log.writeDebugLog(_T("Attempting to get used/changed disk blocks of VM %ls."), vmName);

	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsCBM = env->GetObjectClass(objectInstance);
	clsDiskSectorInfo = env->FindClass("com/ca/arcflash/ha/vmwaremanager/Disk_Sector_Info");
	
	if (clsCBM != NULL && clsDiskSectorInfo != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId...")); 
		//midgetUsedDiskBlocks  = env->GetMethodID(clsCBM,"getUsedDiskBlocks","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;)J");
		midgetUsedDiskBlocks  = env->GetMethodID(clsCBM,"getUsedDiskBlocks","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;)Lcom/ca/arcflash/ha/vmwaremanager/Disk_Sector_Info;");
		fieldTotalNumSectors = env->GetFieldID(clsDiskSectorInfo,"totalNumSectors","J");
		fieldUsedNumSectors = env->GetFieldID(clsDiskSectorInfo,"usedNumSectors","J");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midgetUsedDiskBlocks != NULL && fieldTotalNumSectors != NULL && fieldUsedNumSectors != NULL)
	{		
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotURL, avmsnapURL);
		UnicodeToUTF8(diskChangeId, avmDiskChangeId);	
					
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringSnapURL = env->NewStringUTF(avmsnapURL.c_str());
		jstring StringVMDiskChangeId = env->NewStringUTF(avmDiskChangeId.c_str());
		jstring StringvmBitmapFilePath = _makeWCHARToJString(env, bitmapFilePath);
				
		resultI = _makeInteger(env, DiskDeviceKey);	
		resultI1 = _makeInteger(env, chunkSize);	
		resultI2 = _makeInteger(env, sectorSize);

		log.writeDebugLog(_T("Calling the JAVA method...")); 
		/*jbyteWOArr = (jbyteArray )*/
		jobject tempObj =  (jobject) env->CallObjectMethod
						(objectInstance,midgetUsedDiskBlocks, StringvmName, StringvmUUID,
						StringSnapURL,StringVMDiskChangeId,resultI,StringvmBitmapFilePath,
						resultI1,resultI2);
		if(tempObj != NULL)
		{
			//jsize arrayLength =  env->GetArrayLength(jbyteWOArr);
			//cout << "Number of bytes " << arrayLength << endl;
			//jboolean jbCopy = 0;

			//jbyte* tempObj = env->GetByteArrayElements/*GetObjectArrayElement*/(jbyteWOArr, &jbCopy);
			//
			//if ( tempObj!= NULL )
			//{
			//	VM_BITMAP_BUFFER *pDiskBitmap = (VM_BITMAP_BUFFER*)malloc(sizeof(VM_BITMAP_BUFFER) + arrayLength*sizeof(BYTE));
			//	/*BYTE* bitmap = new BYTE[arrayLength];
			//	memset(bitmap,0,arrayLength*sizeof(BYTE));
			//	memcpy(bitmap,tempObj,arrayLength*sizeof(BYTE));*/
			//	pDiskBitmap->StartingLcn.QuadPart = 0;
			//	pDiskBitmap->BitmapSize.QuadPart = arrayLength * 8;
			//	ZeroMemory(pDiskBitmap->Buffer,(sizeof(BYTE)*arrayLength));
			//	memcpy(pDiskBitmap->Buffer,tempObj,arrayLength*sizeof(BYTE));
			//	*ppDiskBitmap = pDiskBitmap;
			//	env->ReleaseByteArrayElements(jbyteWOArr,tempObj,JNI_ABORT);
			//	//if(bitmap)
			//	//{
			//	//	//DWORD dwBytes = ((DiskBitmap->BitmapSize.QuadPart)%8) ? (DiskBitmap->BitmapSize.QuadPart)/8+1 : (DiskBitmap->BitmapSize.QuadPart)/8;
			//	//	HANDLE hBitMap =  CreateFile(TEXT("c:\\offhost-bitmap1"), GENERIC_WRITE, FILE_SHARE_READ, NULL, CREATE_ALWAYS, 0, NULL);
			//	//	if (hBitMap != INVALID_HANDLE_VALUE)
			//	//	{
			//	//		DWORD dwWritten = 0;
			//	//		BOOL bRC = WriteFile(hBitMap, bitmap, arrayLength, &dwWritten, NULL);
			//	//		CloseHandle(hBitMap);
			//	//	}				
			//	//}
			//	return 1;
			//}
			//else
			//	return NULL;
			jboolean isObj = env->IsInstanceOf(tempObj, clsDiskSectorInfo);

			if(!isObj)
			{
				log.writeDebugLog(_T("This is not a disk sector info object"));
				goto invokejavavirtualmachine_error;
			}

			jlong totalNumSectors = (jlong)env->GetLongField(tempObj, fieldTotalNumSectors);
			jlong usedNumSectors = (jlong)env->GetLongField(tempObj, fieldUsedNumSectors);

			pbitmapSize->QuadPart = totalNumSectors;
			pUsedSectorCount->QuadPart = usedNumSectors;
			iResult = 1;
		}
		else
		{
			iResult = 0;
			log.writeDebugLog(_T("Call to Java API returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	log.writeDebugLog(_T("Returning from getUsedDiskBlocks with %d"),iResult);
	return iResult;
}

error_Info NativeClass::createVMwareVirtualMachine (wchar_t* configFilePath,
								wchar_t* vcName,
								wchar_t* esxHost,
								wchar_t* esxDC,
								wchar_t* vmResPool,
								wchar_t* vmNewName,
								wchar_t* datastoreOfVM,
								int numDisks,
								wchar_t** diskUrlList, 
								wchar_t** datastoreList,
								BOOL overwriteVM,
								BOOL recoverToOriginal,
								VM_Info* pVMInfo //out								
								)
{
	V2PNFlog log;
	jclass clsV2P = NULL;
	jclass clsVM = NULL;
	jmethodID midCreateVMwareVM = NULL;
	jobjectArray diskList = NULL;
	jobjectArray dstoreList = NULL;
	string aesxHost;
	string aesxDC;
	string avmresPool;
	string avcName;
	string avmNewName;	
	string adatastoreOfVM;	
	jfieldID fieldvmName = NULL;
	jfieldID fieldvmUUID = NULL;
	jfieldID fieldvmHost = NULL;
	jfieldID fieldvmVMX = NULL;
	jfieldID fieldvmState = NULL;
	jfieldID fieldvmESXHost = NULL;
	jfieldID fieldvmInstUUID = NULL;
	jfieldID fieldvmgstOS = NULL;
	jfieldID fieldvmIP = NULL;
	jfieldID fielderroString = NULL;
	jfieldID fieldmessageCode = NULL;
	jobject resultB = NULL;
	jobject resultB1 = NULL;
	jobject resultB2 = NULL;
	jobject resultB3 = NULL;	
	jobject jobj = NULL;
	BOOL useVmNewName = TRUE;
	BOOL useDatastoreList = FALSE;
	error_Info err = {0};
	err.errorCode  = -1;
	wcscpy_s(err.erroString, DISKURL_SIZE, L"");

	int iResult = -1;
	
	log.writeDebugLog(_T("Attempting to create VM from the config file %ls."), configFilePath);

	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return err;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return err;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	clsVM = env->FindClass("com/ca/arcflash/ha/vmwaremanager/VM_Info");
	
	
	if (clsV2P != NULL && clsVM != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId...")); 
		midCreateVMwareVM  = env->GetMethodID(clsV2P,"createVMwareVM","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;Ljava/lang/Boolean;Ljava/lang/String;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;)Lcom/ca/arcflash/ha/vmwaremanager/VM_Info;");
		fieldvmName = env->GetFieldID(clsVM,"vmName", "Ljava/lang/String;");
		fieldvmUUID = env->GetFieldID(clsVM,"vmUUID", "Ljava/lang/String;");
		fieldvmHost = env->GetFieldID(clsVM, "vmHostName", "Ljava/lang/String;");
		fieldvmVMX = env->GetFieldID(clsVM, "vmVMX", "Ljava/lang/String;");
		fieldvmInstUUID = env->GetFieldID(clsVM, "vmInstanceUUID", "Ljava/lang/String;");
		fieldvmESXHost = env->GetFieldID(clsVM, "vmEsxHost", "Ljava/lang/String;");
		fieldvmgstOS = env->GetFieldID(clsVM, "vmGuestOS", "Ljava/lang/String;");
		fieldvmState = env->GetFieldID(clsVM,"powerState","Z");
		fieldvmIP = env->GetFieldID(clsVM, "vmIP", "Ljava/lang/String;");
		fielderroString = env->GetFieldID(clsVM,"errorString", "Ljava/lang/String;");
		fieldmessageCode = env->GetFieldID(clsVM,"messageCode", "J");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class.")); 
		goto invokejavavirtualmachine_error;
	}

	if(midCreateVMwareVM != NULL && fieldvmName!= NULL && fieldvmUUID!= NULL && fieldvmHost!= NULL && fieldvmVMX!= NULL && fieldvmState!= NULL )
	{
		UnicodeToUTF8(esxHost, aesxHost);
		UnicodeToUTF8(esxDC, aesxDC);
		UnicodeToUTF8(vcName, avcName);
		UnicodeToUTF8(vmResPool, avmresPool);
		if (vmNewName == NULL)
		{
			useVmNewName = FALSE;
		}
		UnicodeToUTF8(vmNewName, avmNewName);
		UnicodeToUTF8(datastoreOfVM, adatastoreOfVM);		

		jstring StringconfigFilePath = _makeWCHARToJString(env, configFilePath);
		jstring StringesxHost = env->NewStringUTF(aesxHost.c_str());
		jstring StringesxDC = env->NewStringUTF(aesxDC.c_str());
		jstring StringvcName = env->NewStringUTF(avcName.c_str());
		jstring StringvmNewName = env->NewStringUTF(avmNewName.c_str());
		jstring StringdatastoreOfVM = env->NewStringUTF(adatastoreOfVM.c_str());
		jstring StringvmResPool = env->NewStringUTF(avmresPool.c_str());
		

		if(numDisks == 0)
		{
			dstoreList = (jobjectArray)_makeNativeObjectToJniStringArrayArray(env, 0, NULL);
			diskList = (jobjectArray)_makeNativeObjectToJniStringArrayArray(env, 0, NULL);
		}
		else
		{
			if(recoverToOriginal)
				recoverToOriginal = FALSE;
			if(datastoreList==NULL || diskUrlList==NULL) 
			{
				iResult = -1;
				goto invokejavavirtualmachine_error;
			}
			dstoreList = (jobjectArray)_makeNativeObjectToJniStringArrayArray(env, numDisks, datastoreList);
			diskList = (jobjectArray)_makeNativeObjectToJniStringArrayArray(env, numDisks, diskUrlList);
			if (dstoreList != NULL && diskList != NULL)
				useDatastoreList = TRUE;
			else
			{
				iResult = -1;
				goto invokejavavirtualmachine_error;
			}
		}
								
		resultB = _makeBoolean(env, overwriteVM);	
		resultB1 = _makeBoolean(env, useVmNewName);
		resultB2 = _makeBoolean(env,recoverToOriginal);		
		resultB3 = _makeBoolean(env,useDatastoreList);		

		log.writeDebugLog(_T("Calling the JAVA method...")); 
		jobj = (jobject) env->CallObjectMethod
						(objectInstance,midCreateVMwareVM, StringconfigFilePath, 
						StringvcName, StringesxHost, StringesxDC, StringvmResPool,StringdatastoreOfVM, //resultI,
						diskList, dstoreList,resultB, StringvmNewName,resultB1,resultB2,resultB3);
						
		if(jobj != NULL)
		{
			jboolean isESXVM = env->IsInstanceOf(jobj, clsVM);

			if(!isESXVM)
			{
				log.writeDebugLog(_T("This is not a VM object."));
				goto invokejavavirtualmachine_error;
			}

			jstring vmName = (jstring)env->GetObjectField(jobj, fieldvmName);
			jstring vmUUID = (jstring)env->GetObjectField(jobj, fieldvmUUID);
			jstring vmHstName = (jstring)env->GetObjectField(jobj, fieldvmHost);
			jstring vmVMXPath = (jstring)env->GetObjectField(jobj, fieldvmVMX);
			jstring vmesxHost = (jstring)env->GetObjectField(jobj, fieldvmESXHost);
			jstring vminstUUID = (jstring)env->GetObjectField(jobj, fieldvmInstUUID);
			jstring vmgstOS = (jstring)env->GetObjectField(jobj, fieldvmgstOS);
			jboolean vmPwrState = (jboolean)env->GetBooleanField(jobj, fieldvmState);
			jstring vmVMIP = (jstring)env->GetObjectField(jobj, fieldvmIP);
			jstring errorString = (jstring)env->GetObjectField(jobj, fielderroString);
			jlong messageCode = (jlong)env->GetLongField(jobj, fieldmessageCode);
			jboolean isName = FALSE, isUUID = FALSE, isHstName = FALSE, isVMXPath = FALSE, isESXHost = FALSE, isInstUUID = FALSE, isgstOS = FALSE, isVMIP = FALSE, isError = FALSE;

			const jchar *strVM = env->GetStringChars(vmName, &isName);
			const jchar *strVMUUID = env->GetStringChars(vmUUID, &isUUID);
			const jchar *strVMHstName = env->GetStringChars(vmHstName, &isHstName);
			const jchar *strVMVMX = env->GetStringChars(vmVMXPath, &isVMXPath);
			const jchar *strVMESXHost = env->GetStringChars(vmesxHost, &isESXHost);
			const jchar *strVMInstUUID = env->GetStringChars(vminstUUID, &isInstUUID);
			const jchar *strVMGstOS = env->GetStringChars(vmgstOS, &isgstOS);
			const jchar *strVMIP = env->GetStringChars(vmVMIP, &isVMIP);
			const jchar *strErrorString;
			if(errorString)
				 strErrorString = env->GetStringChars(errorString, &isError);

			if(isName && isUUID && isHstName && isVMXPath && isESXHost && isInstUUID && isgstOS && isVMIP)
			{
				wcscpy_s(pVMInfo->vmName, NAME_SIZE,(const WCHAR *)strVM);
				wcscpy_s(pVMInfo->vmUUID, NAME_SIZE,(const WCHAR *)strVMUUID);
				wcscpy_s(pVMInfo->vmHost, NAME_SIZE,(const WCHAR *)strVMHstName);
				wcscpy_s(pVMInfo->vmVMX, NAME_SIZE, (const WCHAR *)strVMVMX);
				wcscpy_s(pVMInfo->vmESXHost, NAME_SIZE,(const WCHAR *)strVMESXHost);
				wcscpy_s(pVMInfo->vmInstUUID,NAME_SIZE, (const WCHAR *)strVMInstUUID);
				wcscpy_s(pVMInfo->vmGuestOS, NAME_SIZE, (const WCHAR *)strVMGstOS);
				wcscpy_s(pVMInfo->vmIP, NAME_SIZE,(const WCHAR *)strVMIP);
			}
			if(isError)
			{
				wcscpy_s(err.erroString, ERROR_STRING_SIZE,(const WCHAR *)strErrorString);
			}
			err.messageCode = messageCode;
;
			pVMInfo->powerState = vmPwrState;			
			env->ReleaseStringChars(vmName, strVM);
			env->ReleaseStringChars(vmUUID, strVMUUID);
			env->ReleaseStringChars(vmHstName, strVMHstName);
			env->ReleaseStringChars(vmVMXPath, strVMVMX);
			env->ReleaseStringChars(vmesxHost, strVMESXHost);
			env->ReleaseStringChars(vminstUUID, strVMInstUUID);
			env->ReleaseStringChars(vmgstOS, strVMGstOS);
			env->ReleaseStringChars(vmVMIP, strVMIP);
			if(isError)
			{
				env->ReleaseStringChars(errorString, strErrorString);
			}			
			if(wcslen(pVMInfo->vmName) > 0)
				iResult = 1;
			else
				iResult = -1;
		}	
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	log.writeDebugLog(_T("Detaching from JVM..."));
	DetachJVM();
	log.writeDebugLog(_T("Returning from createVMwareVirtualMachine with messageCode %ld"),err.messageCode);
	log.writeDebugLog(_T("Returning from createVMwareVirtualMachine with %d"),iResult);
	err.errorCode = iResult;
	return err;
}
BOOL NativeClass::renameVM(WCHAR* vmName, WCHAR* vmUUID, WCHAR* vmNewName)
{
	V2PNFlog log;
	jclass clsV2P = NULL;
	jmethodID midrenameVM = NULL;
	string avmName;
	string avmUUID;
	string avmNewName;

	log.writeDebugLog(_T("Attempting to rename VM from %ls to %s."), vmName,vmNewName);

	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	int iResult = -1;

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}

	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		midrenameVM  = env->GetMethodID(clsV2P,"renameVM","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midrenameVM != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(vmNewName, avmNewName);		
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringvmNewName = env->NewStringUTF(avmNewName.c_str());
		
		jboolean tempObj = (jboolean)env->CallObjectMethod(objectInstance,midrenameVM,StringvmName,StringvmUUID,StringvmNewName);
		if(tempObj != NULL)
		{
			iResult = 1;
		}
		else
		{
			iResult = 0;
			log.writeDebugLog(_T("Call to Java API returned NULL."));
		}		
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	log.writeDebugLog(_T("Returning from renameVM with %d"),iResult);
	return iResult;
}

BOOL NativeClass::deleteVM(WCHAR* vmName, WCHAR* vmUUID)
{
	V2PNFlog log;
	jclass clsV2P = NULL;
	jmethodID middeleteVM = NULL;
	string avmName;
	string avmUUID;

	log.writeDebugLog(_T("Attempting to delete VM %ls."), vmName);

	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	int iResult = -1;
	
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}

	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		middeleteVM  = env->GetMethodID(clsV2P,"deleteVM","(Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(middeleteVM != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		
		jboolean tempObj = (jboolean)env->CallObjectMethod(objectInstance,middeleteVM,StringvmName,StringvmUUID);
		if(tempObj != NULL)
		{
			iResult = 1;
		}
		else
		{
			iResult = 0;
			log.writeDebugLog(_T("Call to Java API returned NULL."));
		}		
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	log.writeDebugLog(_T("Returning from deleteVM with %d"),iResult);
	return iResult;
}


WCHAR* NativeClass::getVmdkFilePath(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, DWORD dwDiskSignaure)
{
	PFN_GetSignature pfnGetSignature = NULL;
	WCHAR newServerName[PATH_SIZE] = L"\0";
	string aSignature;
	WCHAR wSignature[MAX_GUID_LENGTH] = L"\0";
	TCHAR szJob[MAX_PATH] = _T("\0");
	CHAR* tempSig = NULL;
	BOOL foundDisk = FALSE;
	DWORD dRet, calSignature = 0;
	wstring strESX, strUser, strPwd, strMorefID, strGUID, strVMDKURL, strJob, strSnapMoref; 
	Disk_Info* dskInfo = NULL;
	int count = 0, i;
	V2PNFlog log;
	CHAR port_Value[20];
	wstring wport_Value;
	int serverType = 0;

	serverType =  getVMServerType();
	
	HMODULE hMountVMMgr = NULL;
	hMountVMMgr = LoadLibrary(L"vmJob.dll");
	if( hMountVMMgr == NULL )
	{
		return NULL;
	}

	strMorefID = getVMMoref(vmName, vmUUID);
	dskInfo = getSnapShotDiskInfo(vmName, vmUUID, snapshotURL, &count);

	if((dskInfo != NULL) && (strMorefID.length() != 0))
	{
		for(i=0; i<count; i++ )
		{
			pfnGetSignature = (PFN_GetSignature)GetProcAddress(hMountVMMgr, "GetVMDKSignature");
			if(pfnGetSignature == NULL)
			{
				log.writeDebugLog(_T("Failed to find GetVMDKSignature proc.\n"));
			}
			_tcscpy_s(newServerName, PATH_SIZE, szserverName);
			if(serverType == 2)
			{
				_tcscat_s(newServerName, PATH_SIZE, L":");
				ltoa(Port, port_Value, 10);
				UTF8ToUnicode(port_Value, wport_Value);
				_tcscat_s(newServerName, PATH_SIZE, wport_Value.c_str());
			}
			log.writeDebugLog(_T("Attempting to connect to Server %ls.\n"),newServerName);
			//add by zhepa02 at 2015-04-28, get the thumbprint for vmdk connection
			wstring szThumbprint = getThumbprint();
			VMDK_CONNECT_MORE_PARAMS moreParams;
			char thumbprint[THUMBPRINT_LENGTH];
			UnicodeToAnsi((PWCHAR)szThumbprint.c_str(), thumbprint, 0);
			moreParams.thumbprint = thumbprint;
			//end
			dRet = pfnGetSignature(newServerName, szuserName, szPassword, strMorefID, Port, moreParams, strSnapMoref, dskInfo[i].vmDisk, strGUID, strJob, L"");
			if(!dRet)
			{
				if(strGUID.length() < MAX_GUID_LENGTH-1)
				{
					wcscpy_s(wSignature, MAX_GUID_LENGTH, strGUID.c_str());
					UnicodeToUTF8(wSignature, aSignature);
					calSignature = _atoi64(aSignature.c_str());
					if(calSignature == dwDiskSignaure)
					{
						log.writeDebugLog(_T("The target vmdk file is %ls."), dskInfo[i].vmDisk);
						foundDisk = TRUE;
						wcscpy_s(diskURL, PATH_SIZE, dskInfo[i].vmDisk);
						break;
					}
				}
				else
				{
					wcscpy_s(wSignature, MAX_GUID_LENGTH, strGUID.c_str());
					UnicodeToUTF8(wSignature, aSignature);
					char *bufSignature = new char[strlen(aSignature.c_str())+1];
					strcpy(bufSignature, aSignature.c_str());
					tempSig = strrchr(bufSignature, '}');
					*tempSig = '\0';
					tempSig = strchr(bufSignature,'{');
					tempSig++;
					if(tempSig!=NULL)
					{
						strcpy_s(bufSignature, MAX_GUID_LENGTH, tempSig);
						GUID strGUID = OutputStringToGUID(bufSignature);
						ASAG_CRC crc;
						crc.CSSCheckSum((PCHAR)(&strGUID),sizeof(GUID),&calSignature);
						if(calSignature == dwDiskSignaure)
						{
							log.writeDebugLog(_T("The target vmdk file is %ls."), dskInfo[i].vmDisk);
							wcscpy_s(diskURL, PATH_SIZE, dskInfo[i].vmDisk);
							foundDisk = TRUE;
							if(bufSignature)
								delete[] bufSignature;
							break;
						}
					}
					else
					{
						log.writeDebugLog(_T("Invalid GUID signature for vmdk file %ls."), dskInfo[i].vmDisk);
					}
					if(bufSignature)
						delete[] bufSignature;
				}
			}
			else
			{
				log.writeDebugLog(_T("Failed to get the disk signature of disk %ls."), dskInfo[i].vmDisk);
			}
		}
	}
	else
	{
		log.writeDebugLog(_T("Failed to get vm information."));
	}

	if(hMountVMMgr != NULL)
	{
		FreeLibrary(hMountVMMgr);
		hMountVMMgr = NULL;
	}
	if(diskList != NULL)
	{
		delete(diskList);
		diskList = NULL;
	}

	if(foundDisk)
		return diskURL;
	else
	{
		log.writeDebugLog(_T("Unable to find a match diskURL with the given signature."));
		return NULL;
	}
}

/*
VOID ASAG_CRC::BuildCRCTable()
{
	INT i, j;
	DWORD crc;

	for (i = 0; i <= 255; i++)
	{
		crc = i;
		for (j = 8; j > 0; j--)
		{
			if (crc & 1)
				crc = (crc >> 1) ^ CRC32_POLYNOMIAL;
			else
				crc >>= 1;
		}
		CRCTable[i] = crc;
	}
}

VOID ASAG_CRC::CSSCheckSum(PCHAR buffer, DWORD count)
{
	PUCHAR p;
	DWORD crc = chkSum;
	DWORD temp1, temp2;

	if (!CRCTableInitialized)
	{
		BuildCRCTable();
		CRCTableInitialized = TRUE;
	}

	p = (PUCHAR)buffer;
	while (count-- != 0)
	{
		temp1 = (crc >> 8) & 0x00FFFFFFL;
		temp2 = CRCTable[(crc ^ *p++) & 0xff];
		crc = temp1 ^ temp2;
	}
	chkSum = crc;
	return;
}

VOID ASAG_CRC::CSSCheckSum(PCHAR buffer, DWORD count, DWORD *pdwCRC)
{
	PUCHAR p;
	DWORD crc = count;
	DWORD temp1, temp2;

	if (!CRCTableInitialized)
	{
		BuildCRCTable();
		CRCTableInitialized = TRUE;
	}

	p = (PUCHAR)buffer;
	while (count-- != 0)
	{
		temp1 = (crc >> 8) & 0x00FFFFFFL;
		temp2 = CRCTable[(crc ^ *p++) & 0xff];
		crc = temp1 ^ temp2;
	}
	*pdwCRC = crc;
	return;
}
*/

GUID OutputStringToGUID(CHAR* lpszGUID)
{
	GUID Guid = {0};;
	CHAR *strEnd;
	CHAR *lpszContext = NULL;
	V2PNFlog log;

	try
	{
		if(lpszGUID != NULL)
		{
			CHAR *szTemp;

			szTemp = strtok_s(lpszGUID, "-", &lpszContext);
			Guid.Data1 = (DWORD)strtoul(szTemp, &strEnd, 16);

			szTemp = strtok_s(NULL, "-", &lpszContext);
			Guid.Data2 = (WORD)strtoul(szTemp, &strEnd, 16);

			szTemp = strtok_s(NULL, "-", &lpszContext);
			Guid.Data3 = (WORD)strtoul(szTemp, &strEnd, 16);

			szTemp = strtok_s(NULL, "-", &lpszContext);
			CHAR szBuff[2];

			szBuff[0] = szTemp[0];
			szBuff[1] = szTemp[1];
			Guid.Data4[0] = (unsigned char)strtoul(szBuff, &strEnd, 16);

			szBuff[0] = szTemp[2];
			szBuff[1] = szTemp[3];
			Guid.Data4[1] = (unsigned char)strtoul(szBuff, &strEnd, 16);

			szTemp = strtok_s(NULL, "-", &lpszContext);

			int i,j;
			for(i=2,j=0; i<8; i++, j+=2)
			{
				szBuff[0] = szTemp[j];
				szBuff[1] = szTemp[j+1];
				Guid.Data4[i]= (unsigned char)strtoul(szBuff, &strEnd, 16);
			}
		}
	}catch(...)
	{
		log.writeDebugLog(_T("Exception in OutputStringToGUID"));
	}
	return Guid;
}

BOOL NativeClass::removeAllSnapshots(WCHAR* vmName, WCHAR*  vmUUID)
{
	jclass clsV2P = NULL;
	jmethodID midRemSnaps = NULL;
	string avmName;
	string avmUUID;
	BOOL result = FALSE;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to remove all snapshots from VM %ls."), vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midRemSnaps  = env->GetMethodID(clsV2P,"removeAllSnapshots","(Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midRemSnaps != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		
		jboolean bResult = (jboolean) env->CallBooleanMethod(objectInstance,midRemSnaps,StringvmName,StringvmUUID);

		if(bResult)
		{
			result = TRUE;
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return result;

}

BOOL NativeClass::removeSnapshotFromAppliance(WCHAR* vmName, WCHAR*  snapRef)
{
	jclass clsV2P = NULL;
	jmethodID midRemSnaps = NULL;
	string avmName;
	string asnapRef;
	BOOL result = FALSE;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;

	log.writeDebugLog(_T("Attempting to remove all snapshots from VM %ls."), vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}


	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midRemSnaps = env->GetMethodID(clsV2P, "removeSnapshotForAppliance", "(Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class"));
		goto invokejavavirtualmachine_error;
	}

	if (midRemSnaps != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(snapRef, asnapRef);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(asnapRef.c_str());

		jboolean bResult = (jboolean)env->CallBooleanMethod(objectInstance, midRemSnaps, StringvmName, StringvmUUID);

		if (bResult)
		{
			result = TRUE;
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}

invokejavavirtualmachine_error:
	if (env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return result;

}


Disk_Info*  NativeClass::getSnapShotDiskInfoByID(WCHAR* vmName, WCHAR*  vmUUID, WCHAR* snapshotURL, int *count)
{
	jclass clsV2P = NULL;
	jclass clsDsk = NULL;
	jobjectArray jobjWOArr = NULL;
	jmethodID midGetsnapDiskList = NULL;
	jfieldID fieldDiskURL = NULL;
	jfieldID fieldchageID = NULL;
	jfieldID fieldDeviceKey = NULL;
	jfieldID fieldsize = NULL;
	jfieldID fieldModeKey = NULL;
	jfieldID fieldCModeKey = NULL;
	jfieldID fieldDatastoreType = NULL;
	jfieldID fieldIsIDEDisk = NULL;
	jfieldID fieldIsSATADisk = NULL;
	string avmName;
	string avmUUID;
	string avmsnapURL;

	V2PNFlog log;
	
	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif	

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return NULL;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return NULL;
	}
	clsV2P = env->GetObjectClass(objectInstance);
	clsDsk = env->FindClass("com/ca/arcflash/ha/vmwaremanager/Disk_Info");


	if (clsV2P != NULL && clsDsk != NULL)
	{
		log.writeDebugLog(_T("Attempting to get the MethodId...")); 
		midGetsnapDiskList  = env->GetMethodID(clsV2P,"getSnapShotDiskInfoByID","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)[Lcom/ca/arcflash/ha/vmwaremanager/Disk_Info;");
		fieldDiskURL = env->GetFieldID(clsDsk,"diskURL", "Ljava/lang/String;");
		fieldchageID = env->GetFieldID(clsDsk,"changeID", "Ljava/lang/String;");
		fieldModeKey = env->GetFieldID(clsDsk,"diskMode", "Ljava/lang/String;");
		fieldCModeKey = env->GetFieldID(clsDsk,"compMode", "Ljava/lang/String;");
		fieldDatastoreType = env->GetFieldID(clsDsk,"datastoreType", "Ljava/lang/String;");
		fieldDeviceKey = env->GetFieldID(clsDsk,"deviceKey","I");
		fieldsize = env->GetFieldID(clsDsk,"sizeinKB","J");
		fieldIsIDEDisk = env->GetFieldID(clsDsk, "isIDEDisk", "Z");
		fieldIsSATADisk = env->GetFieldID(clsDsk, "isSATADisk", "Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midGetsnapDiskList != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotURL, avmsnapURL);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringSnapURL = env->NewStringUTF(avmsnapURL.c_str());

		log.writeDebugLog(_T("Calling the JAVA method...")); 
		jobjWOArr = (jobjectArray) env->CallObjectMethod(objectInstance,midGetsnapDiskList,StringvmName, StringvmUUID, StringSnapURL);
		if(diskList != NULL)
		{
			delete[] (diskList);
			diskList = NULL;
		}
		if(jobjWOArr != NULL)
		{

			jsize vmDiskLength =  env->GetArrayLength(jobjWOArr);
			cout << "Number of disks " << vmDiskLength << endl;

			*count = vmDiskLength;

			diskList = new Disk_Info[vmDiskLength];

			for(jsize i = 0; i < vmDiskLength; i++)
			{
				jobject tempObj = env->GetObjectArrayElement(jobjWOArr, i);
				jboolean isESXVM = env->IsInstanceOf(tempObj, clsDsk);

				if(!isESXVM)
				{
					log.writeDebugLog(_T("This is not a disk object"));
					goto invokejavavirtualmachine_error;
				}

				jstring diskURL = (jstring)env->GetObjectField(tempObj, fieldDiskURL);
				jstring changeID = (jstring)env->GetObjectField(tempObj, fieldchageID);
				jstring diskMode = (jstring)env->GetObjectField(tempObj, fieldModeKey);
				jstring dCompMode = (jstring)env->GetObjectField(tempObj, fieldCModeKey);
				jstring datastoreType = (jstring)env->GetObjectField(tempObj, fieldDatastoreType);
				jint diskKey = (jint)env->GetIntField(tempObj, fieldDeviceKey);
				jlong diskSize = (jlong)env->GetLongField(tempObj, fieldsize); //<sonmi01>2013-12-27 #int to int 64
				jboolean isdiskURL = FALSE, isChangeID = FALSE, isDiskMode = FALSE, isCMode =FALSE, isDatastoreType = FALSE;
				const jchar *strDiskURL = NULL , *strChangeID = NULL, *strDMode = NULL, *strCMode = NULL, *strDatastoreType = NULL;

				if(changeID)
				{
					strChangeID = env->GetStringChars(changeID, &isChangeID);
					if (isChangeID)
						wcscpy_s(diskList[i].chageID,NAME_SIZE,(const WCHAR *)strChangeID);
					else
						wcscpy_s(diskList[i].chageID,NAME_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(changeID, strChangeID);
				}
				else
					wcscpy_s(diskList[i].chageID,NAME_SIZE,(const WCHAR *)L"");
				if(diskURL)
				{
					strDiskURL = env->GetStringChars(diskURL, &isdiskURL);
					if(isdiskURL)
						wcscpy_s(diskList[i].vmDisk,DISKURL_SIZE,(const WCHAR *)strDiskURL);
					else
						wcscpy_s(diskList[i].vmDisk,DISKURL_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(diskURL, strDiskURL);		
				}
				else
					wcscpy_s(diskList[i].vmDisk,DISKURL_SIZE,(const WCHAR *)L"");
				if(diskMode)
				{
					strDMode = env->GetStringChars(diskMode, &isDiskMode);
					if(isDiskMode)
						wcscpy_s(diskList[i].diskMode,NAME_SIZE,(const WCHAR *)strDMode);
					else
						wcscpy_s(diskList[i].diskMode,NAME_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(diskMode, strDMode);
				}
				else
					wcscpy_s(diskList[i].diskMode,NAME_SIZE,(const WCHAR *)L"");

				if(dCompMode)
				{
					strCMode = env->GetStringChars(dCompMode, &isCMode);
					if(isCMode)
						wcscpy_s(diskList[i].diskCompMode,NAME_SIZE,(const WCHAR *)strCMode);
					else
						wcscpy_s(diskList[i].diskCompMode,NAME_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(dCompMode, strCMode);
				}
				else
					wcscpy_s(diskList[i].diskCompMode,NAME_SIZE,(const WCHAR *)L"");

				if(datastoreType)
				{
					strDatastoreType = env->GetStringChars(datastoreType, &isDatastoreType);
					if(isDatastoreType)
						wcscpy_s(diskList[i].datastoreType,NAME_SIZE,(const WCHAR *)strDatastoreType);
					else
						wcscpy_s(diskList[i].datastoreType,NAME_SIZE,(const WCHAR *)L"");
					env->ReleaseStringChars(datastoreType, strDatastoreType);
				}
				else
					wcscpy_s(diskList[i].datastoreType,NAME_SIZE,(const WCHAR *)L"");
				
				diskList[i].deviceKey = diskKey;
				diskList[i].sizeinKB = diskSize;
				diskList[i].bUseSWSnapshot = true;
				//diskList[i].bIsIDEDisk = (jboolean)env->GetBooleanField(tempObj, fieldIsIDEDisk);
				//diskList[i].bIsSATADisk = (jboolean)env->GetBooleanField(tempObj, fieldIsSATADisk);
				jboolean isIDEDisk = (jboolean)env->GetBooleanField(tempObj, fieldIsIDEDisk);
				jboolean isSATADisk = (jboolean)env->GetBooleanField(tempObj, fieldIsSATADisk);
				diskList[i].bIsIDEDisk = isIDEDisk;//(jboolean)env->GetBooleanField(tempObj, fieldIsIDEDisk);
				diskList[i].bIsSATADisk = isSATADisk;// (jboolean)env->GetBooleanField(tempObj, fieldIsSATADisk);
			}
		}
		else
		{
			log.writeDebugLog(_T("getSnapShotDiskInfo returned NULL."));
		}
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return diskList;
}
BOOL NativeClass::removeSnapShotByID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL)
{
	jclass clsV2P = NULL;
	jmethodID midremoveSnapShot = NULL;
	jfieldID fieldESX = NULL;
	jfieldID fieldDC = NULL;
	string avmName;
	string avmUUID;
	string asnapshotURL;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;
	BOOL bResult = FALSE;
	jboolean jobj = NULL;

	log.writeDebugLog(_T("Attempting to remove snapshot %ls of VM %ls."), snapshotURL, vmName);

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM.\n"));
		return FALSE;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);


	if (clsV2P != NULL)
	{
		midremoveSnapShot  = env->GetMethodID(clsV2P,"removeSnapShotByID","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midremoveSnapShot != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotURL, asnapshotURL);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringsnapshotURL = env->NewStringUTF(asnapshotURL.c_str());

		jobj = (jboolean)env->CallObjectMethod(objectInstance,midremoveSnapShot,StringvmName,StringvmUUID,StringsnapshotURL);
		if(jobj)
			bResult = TRUE;

	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return bResult;
}

BOOL NativeClass::revertSnapShotByID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL)
{
	jclass clsV2P = NULL;
	jmethodID midrevertSnapShot = NULL;
	jfieldID fieldESX = NULL;
	jfieldID fieldDC = NULL;
	string avmName;
	string avmUUID;
	string asnapshotURL;
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	V2PNFlog log;
	BOOL bResult = FALSE;
	jboolean jobj = NULL;
		
	log.writeDebugLog(_T("Attempting to revert the VM %ls to snapshot %ls."), vmName, snapshotURL);
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return FALSE;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return FALSE;
	}
	clsV2P = env->GetObjectClass(objectInstance);



	if (clsV2P != NULL)
	{
		midrevertSnapShot  = env->GetMethodID(clsV2P,"revertSnapShotByID","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midrevertSnapShot != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		UnicodeToUTF8(vmUUID, avmUUID);
		UnicodeToUTF8(snapshotURL, asnapshotURL);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		jstring StringsnapshotURL = env->NewStringUTF(asnapshotURL.c_str());

		jobj = (jboolean)env->CallObjectMethod(objectInstance,midrevertSnapShot,StringvmName,StringvmUUID,StringsnapshotURL);
		if(jobj)
			bResult =  TRUE;

	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	return bResult;
}
BOOL NativeClass::VMHasSnapshot(WCHAR* vmName, WCHAR* vmUUID)
{
	V2PNFlog log;
	jclass clsV2P = NULL;
	jmethodID midVMHasSnapshot = NULL;
	string avmName;
	string avmUUID;	

	log.writeDebugLog(_T("Attempting to check if VM %ls has Snapshot."),vmName);

	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	BOOL bResult = TRUE;
	jboolean jobj = NULL;
	
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return -1;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return -1;
	}

	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		midVMHasSnapshot  = env->GetMethodID(clsV2P,"VMHasSnapshot","(Ljava/lang/String;Ljava/lang/String;)Z");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midVMHasSnapshot != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());	
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());
		
		jobj = (jboolean)env->CallObjectMethod(objectInstance,midVMHasSnapshot,StringvmName,StringvmUUID);
		if(jobj)
		{
			bResult = TRUE;
		}
		else
		{
			bResult = FALSE;
			//log.writeDebugLog(_T("Call to Java API returned NULL."));
		}		
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	log.writeDebugLog(_T("Returning from midVMHasSnapshot with %d"),bResult);
	return bResult;
	
}

//<sonmi01>2013-6-5 #vds support
//VMNetworkAdapter_Info * g_pVMNetworkAdapter_Info = NULL;
//LONG g_VMNetworkAdapter_Info_Count = 0;
int NativeClass::SetvDSNetworkInfoEx( CONST VMNetworkAdapter_Info * pVMNetworkAdapter_Info, LONG Count )
{
	int nResult = 0;
	V2PNFlog log;

	log.writeDebugLog(_T("SetvDSNetworkInfoEx operation in progress"));

	// check parameters	
	if(!pVMNetworkAdapter_Info || Count <= 0)
	{
		log.writeDebugLog(_T("No data to set"));
		return -2;
	}

	// attaching to JVM
	log.writeDebugLog(_T("Attaching to JVM..."));

#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return -1;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return -1;
	}

	jclass clsV2P = env->GetObjectClass(objectInstance);

	jmethodID midSetVMNetworkConfigInfo = NULL;
	if (clsV2P != NULL)
	{
		midSetVMNetworkConfigInfo  = env->GetMethodID(clsV2P,"setVMNetworkConfigInfo","(Ljava/util/ArrayList;)I");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midSetVMNetworkConfigInfo != NULL)
	{
		// ArrayList
		jclass  class_ArrayList = env->FindClass("java/util/ArrayList");
		jmethodID arrayList_constructor = env->GetMethodID(class_ArrayList, "<init>", "()V");
		jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

		jobject jArrayVMNetworkConfigInfo = env->NewObject(class_ArrayList, arrayList_constructor);		
		
		// VMNetworkConfigInfo
		jclass class_VMNetworkConfigInfo = env->FindClass("com/ca/arcflash/ha/vmwaremanager/VMNetworkConfigInfo");	
		jmethodID mid_VMNetworkConfigInfo_constructor = env->GetMethodID(class_VMNetworkConfigInfo, "<init>", "()V");

		jfieldID field_label	= env->GetFieldID(class_VMNetworkConfigInfo, "label", "Ljava/lang/String;");
		jfieldID field_deviceName	= env->GetFieldID(class_VMNetworkConfigInfo, "deviceName", "Ljava/lang/String;");		
		jfieldID field_switchName	= env->GetFieldID(class_VMNetworkConfigInfo, "switchName", "Ljava/lang/String;");	
		jfieldID field_switchUUID	= env->GetFieldID(class_VMNetworkConfigInfo, "switchUUID", "Ljava/lang/String;");	
		jfieldID field_portgroupName	= env->GetFieldID(class_VMNetworkConfigInfo, "portgroupName", "Ljava/lang/String;");	
		jfieldID field_portgroupKey	= env->GetFieldID(class_VMNetworkConfigInfo, "portgroupKey", "Ljava/lang/String;");	

		for (int i=0; i<Count; i++)
		{
			jobject jVMNetworkConfigInfo = env->NewObject(class_VMNetworkConfigInfo, mid_VMNetworkConfigInfo_constructor);
			
			jstring jstr = WCHARToJString(env, pVMNetworkAdapter_Info[i].m_label);
			env->SetObjectField(jVMNetworkConfigInfo, field_label, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			jstr = WCHARToJString(env, pVMNetworkAdapter_Info[i].m_deviceName);
			env->SetObjectField(jVMNetworkConfigInfo, field_deviceName, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			jstr = WCHARToJString(env, pVMNetworkAdapter_Info[i].m_switchName);
			env->SetObjectField(jVMNetworkConfigInfo, field_switchName, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			jstr = WCHARToJString(env, pVMNetworkAdapter_Info[i].m_switchUuid);
			env->SetObjectField(jVMNetworkConfigInfo, field_switchUUID, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			jstr = WCHARToJString(env, pVMNetworkAdapter_Info[i].m_portgroupName);
			env->SetObjectField(jVMNetworkConfigInfo, field_portgroupName, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			jstr = WCHARToJString(env, pVMNetworkAdapter_Info[i].m_portgroupKey);
			env->SetObjectField(jVMNetworkConfigInfo, field_portgroupKey, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);
			
			env->CallBooleanMethod(jArrayVMNetworkConfigInfo, id_ArrayList_add, jVMNetworkConfigInfo);

			if (jVMNetworkConfigInfo != NULL) 
			{
				env->DeleteLocalRef(jVMNetworkConfigInfo);
			}
		}

		if (class_VMNetworkConfigInfo != NULL)
		{
			env->DeleteLocalRef(class_VMNetworkConfigInfo);
		}

		if (class_ArrayList != NULL)
		{
			env->DeleteLocalRef(class_ArrayList);		
		}

		// call into vsphere sdk
		jint jRet = (jint)env->CallObjectMethod(objectInstance, midSetVMNetworkConfigInfo, jArrayVMNetworkConfigInfo);
		nResult = jRet;
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}	

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) 
	{
		env->ExceptionDescribe();
	}
	DetachJVM();
	log.writeDebugLog(_T("End of SetvDSNetworkInfoEx operation. ret = %ld"), nResult);	
	return nResult;
}


BOOL NativeClass::isVMNameUsed(wchar_t* vmName)
{
	V2PNFlog log;
	jclass clsV2P = NULL;
	jmethodID midisVMNameUsed = NULL;
	string avmName;

	log.writeDebugLog(_T("Attempting to check if VMname %ls is used."),vmName);

	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	int iResult = -1;
	
#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return -1;
	}

	
	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return -1;
	}

	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		midisVMNameUsed  = env->GetMethodID(clsV2P,"isVMnameUsed","(Ljava/lang/String;)I");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midisVMNameUsed != NULL)
	{
		UnicodeToUTF8(vmName, avmName);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());		
		
		jint tempObj = (jint)env->CallObjectMethod(objectInstance,midisVMNameUsed,StringvmName);
		if(tempObj != NULL)
		{
			iResult = tempObj;
		}
		else
		{
			iResult = 0;
			log.writeDebugLog(_T("Call to Java API returned NULL."));
		}		
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	log.writeDebugLog(_T("Returning from isVMNameUsed with %d"),iResult);
	return iResult;
}

void NativeClass::logUserEvent(WCHAR* vmName, WCHAR* vmUUID, WCHAR *eventMessage)
{
	V2PNFlog log;
	jclass clsV2P = NULL;
	jmethodID midlogUserEvent = NULL;
	jmethodID midlogServerEvent = NULL;
	CHAR aeventMessage[MSG_SIZE] = {0};
	string avmName;
	string avmUUID;

	log.writeDebugLog(_T("Attempting to log user event."));

	log.writeDebugLog(_T("Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif
	int iResult = -1;

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return;
	}


	if(objectInstance==NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return;
	}

	clsV2P = env->GetObjectClass(objectInstance);

	if (clsV2P != NULL)
	{
		if(vmName == NULL || vmUUID == NULL)
			midlogServerEvent  = env->GetMethodID(clsV2P,"logServerEvent","(Ljava/lang/String;)V");

		else
			midlogUserEvent = env->GetMethodID(clsV2P,"logUserEvent","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class")); 
		goto invokejavavirtualmachine_error;
	}

	if(midlogServerEvent != NULL)
	{
		//UnicodeToAnsi(eventMessage, aeventMessage, 0);
		jstring StringevntMessage = _makeWCHARToJString(env, eventMessage);		
		env->CallObjectMethod(objectInstance,midlogServerEvent,StringevntMessage);
	}
	else if(midlogUserEvent != NULL)
	{
		//UnicodeToAnsi(eventMessage, aeventMessage, 0);
		jstring StringevntMessage = _makeWCHARToJString(env, eventMessage);		

		UnicodeToUTF8(vmName, avmName);
		jstring StringvmName = env->NewStringUTF(avmName.c_str());		
		
		UnicodeToUTF8(vmUUID, avmUUID);
		jstring StringvmUUID = env->NewStringUTF(avmUUID.c_str());		

		env->CallObjectMethod(objectInstance,midlogUserEvent,StringvmName,StringvmUUID,StringevntMessage);
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method.")); 
	}

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) {
		env->ExceptionDescribe();
	}
	DetachJVM();
	log.writeDebugLog(_T("Returning from isVMNameUsed with %d"),iResult);
}
INT64 NativeClass::GetESXVFlashResource(wchar_t* esxHost, wchar_t* esxDC)
{
	INT64 nResult = 0;
	V2PNFlog log;

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":esxHost = %s, esxDC = %s"), esxHost, esxDC);
	
	// attaching to JVM
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));

#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
		return -1;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to connect to ESX."));
		return -1;
	}

	jclass clsV2P = env->GetObjectClass(objectInstance);

	jmethodID midGetESXVFlashResource= NULL;
	if (clsV2P != NULL)
	{
		midGetESXVFlashResource  = env->GetMethodID(clsV2P,"getESXVFlashResource","(Ljava/lang/String;Ljava/lang/String;)J");
	}
	else
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class")); 
		nResult = -1;
		goto invokejavavirtualmachine_error;
	}

	if(midGetESXVFlashResource != NULL)
	{
		jstring jstrHost = WCHARToJString(env, esxHost);
		jstring jstrDC = WCHARToJString(env, esxDC);

		// call into vsphere sdk
		jlong jRet = (jlong)env->CallObjectMethod(objectInstance, midGetESXVFlashResource, jstrHost, jstrDC);

		if ( jstrHost!=NULL) env->DeleteLocalRef(jstrHost);
		if ( jstrDC!=NULL) env->DeleteLocalRef(jstrDC);
		nResult = jRet;
	}
	else
	{
		nResult = -1;
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested method.")); 
	}	

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) 
	{
		env->ExceptionDescribe();
	}
	DetachJVM();
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":End. ret = %ld"), nResult);	
	return nResult;
}

INT64 NativeClass::GetVMVFlashReadCache(wchar_t* configFilePath)
{
	INT64 nResult = 0;
	V2PNFlog log;

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":configFilePath = %s"), configFilePath);

	// attaching to JVM
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));

#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
		return -1;
	}

	if(objectInstance==NULL)
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to connect to ESX."));
		return -1;
	}

	jclass clsV2P = env->GetObjectClass(objectInstance);

	jmethodID midgetVMVFlashReadCache= NULL;
	if (clsV2P != NULL)
	{
		midgetVMVFlashReadCache  = env->GetMethodID(clsV2P,"getVMVFlashReadCache","(Ljava/lang/String;)J");
	}
	else
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class")); 
		nResult = -1;
		goto invokejavavirtualmachine_error;
	}

	if(midgetVMVFlashReadCache != NULL)
	{
		jstring jstr = WCHARToJString(env, configFilePath);

		// call into vsphere sdk
		jlong jRet = (jlong)env->CallObjectMethod(objectInstance, midgetVMVFlashReadCache, jstr);

		if ( jstr!=NULL) env->DeleteLocalRef(jstr);
		nResult = jRet;
	}
	else
	{
		nResult = -1;
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested method.")); 
	}	

invokejavavirtualmachine_error:
	if ( env->ExceptionOccurred()) 
	{
		env->ExceptionDescribe();
	}
	DetachJVM();
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":End. ret = %ld"), nResult);	
	return nResult;
}


wstring NativeClass::getThumbprint()
{
	wstring result;
	V2PNFlog log;

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(": begin"));

#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
		//break;
		return L"";
	}

	do 
	{
		// attaching to JVM
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));



		if (objectInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to connect to ESX."));
			break;
		}

		jclass clsV2P = env->GetObjectClass(objectInstance);

		jmethodID midgetThumbprint = NULL;
		if (clsV2P != NULL)
		{
			midgetThumbprint = env->GetMethodID(clsV2P, "getThumbprint", "()Ljava/lang/String;");
		}
		else
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class"));
			break;
		}

		if (midgetThumbprint != NULL)
		{
			jstring jRet = (jstring) env->CallObjectMethod(objectInstance, midgetThumbprint);
			result = JStringToWString(env, jRet);
		}
		else
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested method."));
		}

	} while (FALSE);

invokejavavirtualmachine_error:
	if (env->ExceptionOccurred())
	{
		env->ExceptionDescribe();
	}
	DetachJVM();

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":End. ret = %s"), result.c_str());
	return result;
}

int NativeClass::connectVCloud(const wchar_t* vcloudDirectorServerName, const wchar_t* username, const wchar_t* password, const wchar_t* protocol/* = L"https"*/, const int port/* = 443*/, const bool ignoreCert/* = true*/)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attempting to connect to vcloud %ls."), vcloudDirectorServerName);

	int result = 0;	
	do 
	{
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif

#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		jclass clsV2P = env->FindClass("com/ca/arcflash/ha/vcloudmanager/V2P_Export");
		if(clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to get the class of com/ca/arcflash/ha/vcloudmanager/V2P_Export"));
			result = -4;
			break;
		}

		jmethodID midConstructor = env->GetMethodID(clsV2P,"<init>","()V");
		if(midConstructor != NULL)
		{
			jobject obj = env->NewObject(clsV2P, midConstructor);
			if(obj == NULL)
			{
				log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to new the object of com/ca/arcflash/ha/vcloudmanager/V2P_Export"));
				objectInstance = NULL;
				result = -5;
				break;
			}
			// save to member field
			jvcloudInstance = obj;

			// connect
			jmethodID midConnect = env->GetMethodID(clsV2P, "connectVCloud", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IZ)I");
			if (midConnect == NULL)
			{
				result = -6;
				log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of connectVCloud()")); 
			}

			jstring jstrVCloud = WCHARToJString(env, vcloudDirectorServerName);
			jstring jstrUsername = WCHARToJString(env, username);
			jstring jstrPassword = WCHARToJString(env, password);
			jstring jstrProtocol = WCHARToJString(env, protocol);

			jint jRet = env->CallIntMethod(jvcloudInstance, midConnect, jstrVCloud, jstrUsername, jstrPassword, jstrProtocol, port, ignoreCert);

			if ( jstrVCloud != NULL) env->DeleteLocalRef(jstrVCloud);
			if ( jstrUsername != NULL) env->DeleteLocalRef(jstrUsername);
			if ( jstrPassword != NULL) env->DeleteLocalRef(jstrPassword);
			if ( jstrProtocol != NULL) env->DeleteLocalRef(jstrProtocol);

			if (clsV2P != NULL ) env->DeleteLocalRef(clsV2P);

			result = jRet;	
			
		}
	} while (false);

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Connected to vcloud %ls, ret = %d"), vcloudDirectorServerName, result);

	return result;
}

void NativeClass::disconnectVCloud()
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attempting to disconnect."));	

	do 
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			break;
		}

		if( jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if(clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class.")); 
			goto invokejavavirtualmachine_error;
		}

		jmethodID midDisconnect = env->GetMethodID(clsV2P, "disconnectVCloud", "()V");
		if(midDisconnect == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of disconnectVCloud")); 
			goto invokejavavirtualmachine_error;
		}

		// call disconnect
		env->CallVoidMethod(jvcloudInstance, midDisconnect);

invokejavavirtualmachine_error:
		if (clsV2P != NULL ) env->DeleteLocalRef(clsV2P);
		if ( env->ExceptionOccurred()) 
		{
			env->ExceptionDescribe();
		}				
	} while(false);

	DetachJVM();

	return;
}

int NativeClass::saveVAppInfo(const wchar_t* vAppId, const wchar_t* vAppInfoFilePath)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":vAppId = %s, vAppInfoFilePath = %s"), vAppId, vAppInfoFilePath);	

	int result = 0;
	do 
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if( jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if(clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class.")); 
			goto invokejavavirtualmachine_error;
		}

		jmethodID midSaveVAppInfo = env->GetMethodID(clsV2P, "saveVAppInfo", "(Ljava/lang/String;Ljava/lang/String;)I");
		if(midSaveVAppInfo == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of saveVAppInfo")); 
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrvAppId = WCHARToJString(env, vAppId);
		jstring jstrvAppInfoFilePath = WCHARToJString(env, vAppInfoFilePath);
		jint jRet = env->CallIntMethod(jvcloudInstance, midSaveVAppInfo, jstrvAppId, jstrvAppInfoFilePath);

		result = jRet;

		if ( jstrvAppId != NULL) env->DeleteLocalRef(jstrvAppId);
		if ( jstrvAppInfoFilePath != NULL) env->DeleteLocalRef(jstrvAppInfoFilePath);

		if (clsV2P != NULL ) env->DeleteLocalRef(clsV2P);

invokejavavirtualmachine_error:
		if ( env->ExceptionOccurred()) 
		{
			env->ExceptionDescribe();
		}				
	} while(false);

	DetachJVM();

	return result;
}

int NativeClass::cleanupHotAddedDisksAndConsolidateSnapshot(const wstring& esxHost, const vector<wstring>& proxyVMIPList, const vector<wstring>& proxyHotAddedDiskURLs, const wstring& protectedVMInstanceUUID)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":esxHost = %s, proxyVMIPList = %d, proxyHotAddedDiskURLs = %d, protectedVMInstanceUUID = %s"), esxHost.c_str(), proxyVMIPList.size(), proxyHotAddedDiskURLs.size(), protectedVMInstanceUUID.c_str());	

	int result = 0;
	do 
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if( objectInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(objectInstance);
		if(clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class.")); 
			goto invokejavavirtualmachine_error;
		}

		jmethodID midMethod = env->GetMethodID(clsV2P, "cleanupHotAddedDisksAndConsolidateSnapshot", "(Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/lang/String;)I");
		if(midMethod == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of saveVAppInfo")); 
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrEsxHost = WCHARToJString(env, esxHost.c_str());
		jstring jstrProtectedVMInstanceUUID = WCHARToJString(env, protectedVMInstanceUUID.c_str());

		jobject jArray_proxyVMIPList = VList2JList(env, proxyVMIPList);	
		jobject jArray_proxyHotAddedDiskURLs = VList2JList(env, proxyHotAddedDiskURLs);		

		jint jRet = env->CallIntMethod(objectInstance, midMethod, jstrEsxHost,jArray_proxyVMIPList, jArray_proxyHotAddedDiskURLs, jstrProtectedVMInstanceUUID);
		result = jRet;

		if ( jstrEsxHost != NULL) env->DeleteLocalRef(jstrEsxHost);
		if ( jstrProtectedVMInstanceUUID != NULL) env->DeleteLocalRef(jstrProtectedVMInstanceUUID);
		if ( jArray_proxyVMIPList != NULL) env->DeleteLocalRef(jArray_proxyVMIPList);
		if ( jArray_proxyHotAddedDiskURLs != NULL) env->DeleteLocalRef(jArray_proxyHotAddedDiskURLs);


		if (clsV2P != NULL ) env->DeleteLocalRef(clsV2P);

invokejavavirtualmachine_error:
		if ( env->ExceptionOccurred()) 
		{
			env->ExceptionDescribe();
		}				
	} while(false);

	DetachJVM();

	return result;
}


void convertVApp_J2C(JNIEnv *env, jobject jobj, VCloudVApp_Info& vAppInfo)
{
	V2PNFlog log;

	do
	{
		// vapp info
		jclass  class_vapp = env->FindClass("com/ca/arcflash/ha/vcloudmanager/objects/VCloudVApp");
		if (class_vapp == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the class of VCloudVApp"));
			break;
		}

		jfieldID field_id = env->GetFieldID(class_vapp, "name", "Ljava/lang/String;");
		wchar_t* temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vAppInfo.name, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vapp, "id", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vAppInfo.id, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vapp, "status", "I");
		vAppInfo.status = (jint) env->GetIntField(jobj, field_id);

		field_id = env->GetFieldID(class_vapp, "vCenter", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vAppInfo.vCenter, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vapp, "vCenterId", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vAppInfo.vCenterId, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vapp, "highestHWVersion", "I");
		vAppInfo.vmHighestHWVersion = (jint)env->GetIntField(jobj, field_id);

		field_id = env->GetFieldID(class_vapp, "vdcId", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vAppInfo.vdcId, temp);
		SAFE_FREE(temp);

		if (class_vapp != NULL) env->DeleteLocalRef(class_vapp);
	} while (false);
}

void convertVM_J2C(JNIEnv *env, jobject jobj, VCloudVM_Info& vmInfo)
{
	V2PNFlog log;

	do
	{
		// vm info
		jclass  class_vm = env->FindClass("com/ca/arcflash/ha/vcloudmanager/objects/VCloudVM");
		if (class_vm == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the class of VCloudVM"));
			break;
		}

		jfieldID field_id = env->GetFieldID(class_vm, "name", "Ljava/lang/String;");
		wchar_t* temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vmInfo.name, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vm, "id", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vmInfo.id, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vm, "moRef", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vmInfo.moRef, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vm, "vCenter", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vmInfo.vCenter, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vm, "vCenterId", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vmInfo.vCenterId, temp);
		SAFE_FREE(temp);


		field_id = env->GetFieldID(class_vm, "storageProfile", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vmInfo.storageProfile, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vm, "storageProfileId", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vmInfo.storageProfileId, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vm, "datastore", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vmInfo.datastore, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vm, "datastoreId", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vmInfo.datastoreId, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vm, "datastoreMoRef", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vmInfo.datastoreMoRef, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vm, "esxHost", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vmInfo.esxHost, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vm, "esxHostId", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vmInfo.esxHostId, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_vm, "esxHostMoRef", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vmInfo.esxHostMoRef, temp);
		SAFE_FREE(temp);

		if (class_vm != NULL) env->DeleteLocalRef(class_vm);

	} while (false);
}

void jList2List_VCloudVM(JNIEnv *env, jobject jlist, VCloudVM_Info** vmList, int* vmCount)
{
	jclass Class_ArrayList = env->GetObjectClass(jlist);
	jmethodID mid_size = env->GetMethodID(Class_ArrayList, "size", "()I");
	jmethodID mid_get = env->GetMethodID(Class_ArrayList, "get", "(I)Ljava/lang/Object;");

	jint size = env->CallIntMethod(jlist, mid_size);

	if (size > 0)
	{
		*vmList = new VCloudVM_Info[size];
		memset(*vmList, 0, size * sizeof(VCloudVM_Info));
		*vmCount = size;
	}

	for (jint i = 0; i < size; i++)
	{
		jobject jobj = (jstring) env->CallObjectMethod(jlist, mid_get, i);
		convertVM_J2C(env, jobj, (*vmList)[i]);
	}

	if (Class_ArrayList != NULL) env->DeleteLocalRef(Class_ArrayList);
}

int NativeClass::getVMListOfVApp(const wchar_t* vAppId, VCloudVM_Info** vmList, int* vmCount)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":vAppId = %s"), vAppId);

	int result = 0;
	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID midgetVMs = env->GetMethodID(clsV2P, "getVMs", "(Ljava/lang/String;Ljava/util/List;)I");
		if (midgetVMs == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of getVMs"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrvAppId = WCHARToJString(env, vAppId);

		jclass  class_ArrayList = env->FindClass("java/util/ArrayList");
		jmethodID arrayList_constructor = env->GetMethodID(class_ArrayList, "<init>", "()V");
		jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
		jobject jvmList = env->NewObject(class_ArrayList, arrayList_constructor);

		jint jRet = env->CallIntMethod(jvcloudInstance, midgetVMs, jstrvAppId, jvmList);
		result = jRet;

		jList2List_VCloudVM(env, jvmList, vmList, vmCount);

		if (jstrvAppId != NULL) env->DeleteLocalRef(jstrvAppId);
		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}

VM_Info* NativeClass::getVMInfoByMoId(WCHAR* vmMoId)
{
	V2PNFlog log;
	log.writeDebugLog(_T("Attempting to get vmInfo for vmMoId %ls."), vmMoId);

	VM_Info* result = NULL;;
	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			break;
		}

		if (objectInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(objectInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID midMethod = env->GetMethodID(clsV2P, "getVMInfoByMoId", "(Ljava/lang/String;)Lcom/ca/arcflash/ha/vmwaremanager/VM_Info;");
		if (midMethod == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of getVMInfoByMoId"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrVMMoId = WCHARToJString(env, vmMoId);

		jobject jobj = env->CallObjectMethod(objectInstance, midMethod, jstrVMMoId);

		if (jobj == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Cannot find the VM by moId"));
			goto invokejavavirtualmachine_error;
		}

		jclass clsVM = env->FindClass("com/ca/arcflash/ha/vmwaremanager/VM_Info");
		if (clsVM == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jfieldID fieldvmName = env->GetFieldID(clsVM, "vmName", "Ljava/lang/String;");
		jfieldID fieldvmUUID = env->GetFieldID(clsVM, "vmUUID", "Ljava/lang/String;");
		jfieldID fieldvmHost = env->GetFieldID(clsVM, "vmHostName", "Ljava/lang/String;");
		jfieldID fieldvmVMX = env->GetFieldID(clsVM, "vmVMX", "Ljava/lang/String;");
		jfieldID fieldvmInstUUID = env->GetFieldID(clsVM, "vmInstanceUUID", "Ljava/lang/String;");
		jfieldID fieldvmESXHost = env->GetFieldID(clsVM, "vmEsxHost", "Ljava/lang/String;");
		jfieldID fieldvmgstOS = env->GetFieldID(clsVM, "vmGuestOS", "Ljava/lang/String;");
		jfieldID fieldvmIP = env->GetFieldID(clsVM, "vmIP", "Ljava/lang/String;");
		jfieldID fieldvmState = env->GetFieldID(clsVM, "powerState", "Z");
		jfieldID fieldvmresPool = env->GetFieldID(clsVM, "vmresPool", "Ljava/lang/String;");
		jfieldID fieldVMMemInMB = env->GetFieldID(clsVM, "memoryMB", "I");//<huvfe01>2-14-7-14

		VM_Info* vmInf = new VM_Info;
		if (NULL == vmInf)
		{
			goto invokejavavirtualmachine_error;
		}
		ZeroMemory(vmInf, sizeof(VM_Info));

		wchar_t* temp = GetStringFromField(env, &jobj, fieldvmName);
		if (temp != NULL) wcscpy_s(vmInf->vmName, temp);
		SAFE_FREE(temp);

		temp = GetStringFromField(env, &jobj, fieldvmUUID);
		if (temp != NULL) wcscpy_s(vmInf->vmUUID, temp);
		SAFE_FREE(temp);

		temp = GetStringFromField(env, &jobj, fieldvmHost);
		if (temp != NULL) wcscpy_s(vmInf->vmHost, temp);
		SAFE_FREE(temp);

		temp = GetStringFromField(env, &jobj, fieldvmVMX);
		if (temp != NULL) wcscpy_s(vmInf->vmVMX, temp);
		SAFE_FREE(temp);

		temp = GetStringFromField(env, &jobj, fieldvmInstUUID);
		if (temp != NULL) wcscpy_s(vmInf->vmInstUUID, temp);
		SAFE_FREE(temp);

		temp = GetStringFromField(env, &jobj, fieldvmESXHost);
		if (temp != NULL) wcscpy_s(vmInf->vmESXHost, temp);
		SAFE_FREE(temp);

		temp = GetStringFromField(env, &jobj, fieldvmgstOS);
		if (temp != NULL) wcscpy_s(vmInf->vmGuestOS, temp);
		SAFE_FREE(temp);

		temp = GetStringFromField(env, &jobj, fieldvmIP);
		if (temp != NULL) wcscpy_s(vmInf->vmIP, temp);
		SAFE_FREE(temp);

		vmInf->powerState = (jboolean) env->GetBooleanField(jobj, fieldvmState);

		temp = GetStringFromField(env, &jobj, fieldvmresPool);
		if (temp != NULL) wcscpy_s(vmInf->vmresPool, temp);
		SAFE_FREE(temp);

		vmInf->dwVMMemInMB = (DWORD) (jint) env->GetIntField(jobj, fieldVMMemInMB);//<huvfe01>2-14-7-14

		result = vmInf;

		if (jstrVMMoId != NULL) env->DeleteLocalRef(jstrVMMoId);
		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;	
}

int NativeClass::SetVMDiskInfo(CONST Disk_Info* pVMDiskInfo, LONG Count)
{
	int nResult = 0;
	V2PNFlog log;

	log.writeDebugLog(_T("SetVMDiskInfo operation in progress"));

	// check parameters	
	if (!pVMDiskInfo || Count <= 0)
	{
		log.writeDebugLog(_T("No data to set"));
		return -2;
	}

	// attaching to JVM
	log.writeDebugLog(_T("Attaching to JVM..."));

#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return -1;
	}

	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return -1;
	}

	jclass clsV2P = env->GetObjectClass(objectInstance);

	jmethodID midSetVMDiskInfo = NULL;
	if (clsV2P != NULL)
	{
		midSetVMDiskInfo = env->GetMethodID(clsV2P, "setVMDiskInfo", "(Ljava/util/List;)I");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class"));
		goto invokejavavirtualmachine_error;
	}

	if (midSetVMDiskInfo != NULL)
	{
		// ArrayList
		jclass  class_ArrayList = env->FindClass("java/util/ArrayList");
		jmethodID arrayList_constructor = env->GetMethodID(class_ArrayList, "<init>", "()V");
		jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

		jobject jArrayVMDiskInfo = env->NewObject(class_ArrayList, arrayList_constructor);

		// Disk_Info
		jclass class_Disk_Info = env->FindClass("com/ca/arcflash/ha/vmwaremanager/Disk_Info");
		jmethodID mid_Disk_Info_constructor = env->GetMethodID(class_Disk_Info, "<init>", "()V");

		jfieldID field_diskURL = env->GetFieldID(class_Disk_Info, "diskURL", "Ljava/lang/String;");
		jfieldID field_diskProvisioning = env->GetFieldID(class_Disk_Info, "diskProvisioning", "I");

		for (int i = 0; i < Count; i++)
		{
			jobject jDiskInfo = env->NewObject(class_Disk_Info, mid_Disk_Info_constructor);

			jstring jstr = WCHARToJString(env, pVMDiskInfo[i].vmDisk);
			env->SetObjectField(jDiskInfo, field_diskURL, jstr);
			if (jstr != NULL) env->DeleteLocalRef(jstr);

			env->SetIntField(jDiskInfo, field_diskProvisioning, pVMDiskInfo[i].diskProvisioning);

			env->CallBooleanMethod(jArrayVMDiskInfo, id_ArrayList_add, jDiskInfo);

			if (jDiskInfo != NULL)
			{
				env->DeleteLocalRef(jDiskInfo);
			}
		}

		if (class_Disk_Info != NULL)
		{
			env->DeleteLocalRef(class_Disk_Info);
		}

		if (class_ArrayList != NULL)
		{
			env->DeleteLocalRef(class_ArrayList);
		}

		// call into vsphere sdk
		jint jRet = (jint) env->CallObjectMethod(objectInstance, midSetVMDiskInfo, jArrayVMDiskInfo);
		nResult = jRet;
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}

invokejavavirtualmachine_error:
	if (env->ExceptionOccurred())
	{
		env->ExceptionDescribe();
	}
	DetachJVM();
	log.writeDebugLog(_T("End of SetVMDiskInfo operation. ret = %d"), nResult);
	return nResult;
}

int NativeClass::consolidateVMDisks(const wchar_t* vmUuid)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":vmUUID = %s"), vmUuid);	

	int result = 0;
	do 
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if( objectInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(objectInstance);
		if(clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class.")); 
			goto invokejavavirtualmachine_error;
		}

		jmethodID midMethod = env->GetMethodID(clsV2P, "consolidateVMDisks", "(Ljava/lang/String;)I");
		if(midMethod == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of consolidateVMDisks")); 
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstr = WCHARToJString(env, vmUuid);
		jint jRet = env->CallIntMethod(objectInstance, midMethod, jstr);
		result = jRet;

		if ( jstr != NULL) env->DeleteLocalRef(jstr);
		if (clsV2P != NULL ) env->DeleteLocalRef(clsV2P);

invokejavavirtualmachine_error:
		if ( env->ExceptionOccurred()) 
		{
			env->ExceptionDescribe();
		}				
	} while(false);

	DetachJVM();

	return result;
}


int NativeClass::setVMCpuMemory(int numCPU, int numCoresPerSocket, long memoryMB)
{
	int nResult = 0;
	V2PNFlog log;

	log.writeDebugLog(_T("setVMCpuMemory operation in progress"));

	// attaching to JVM
	log.writeDebugLog(_T("Attaching to JVM..."));

#ifdef __OLD_JNI_ENV__
	BOOL bRet = AttachToJVM();
#else
	JNIEnv * env = AttachToJVMThread();
#endif

#ifdef __OLD_JNI_ENV__
	if (!bRet)
#else
	if (!env)
#endif
	{
		log.writeDebugLog(_T("Failed to attach to JVM."));
		return -1;
	}

	if (objectInstance == NULL)
	{
		log.writeDebugLog(_T("Failed to connect to ESX."));
		return -1;
	}

	jclass clsV2P = env->GetObjectClass(objectInstance);

	jmethodID midSetVMCpuMemory = NULL;
	if (clsV2P != NULL)
	{
		midSetVMCpuMemory = env->GetMethodID(clsV2P, "setVMCpuMemory", "(IIJ)I");
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested class"));
		goto invokejavavirtualmachine_error;
	}

	if (midSetVMCpuMemory != NULL)
	{

		// call into vsphere sdk
		jint jRet = (jint) env->CallObjectMethod(objectInstance, midSetVMCpuMemory, numCPU, numCoresPerSocket, memoryMB);
		nResult = jRet;
	}
	else
	{
		log.writeDebugLog(_T("Unable to find the requested method."));
	}

invokejavavirtualmachine_error:
	if (env->ExceptionOccurred())
	{
		env->ExceptionDescribe();
	}
	DetachJVM();
	log.writeDebugLog(_T("End of setVMCpuMemory operation. ret = %d"), nResult);
	return nResult;
}

jobject convertCreateVAppParams(JNIEnv *env, const VCloud_CreateVAppParams* createVAppParams)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":createVAppParams = %p"), createVAppParams);

	jobject result = NULL;

	do 
	{
		// params
		jclass  class_params = env->FindClass("com/ca/arcflash/ha/vcloudmanager/common/CreateVAppParams");
		if (class_params == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the class of CreateVAppParams"));
			break;
		}

		jmethodID params_constructor = env->GetMethodID(class_params, "<init>", "()V");
		jobject jParams = env->NewObject(class_params, params_constructor);

		if (jParams == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable constructor CreateVAppParams"));
			break;
		}

		jfieldID field_id = env->GetFieldID(class_params, "vAppInfoFilePath", "Ljava/lang/String;");
		jstring jstr = WCHARToJString(env, createVAppParams->vAppInfoFilePath);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		field_id = env->GetFieldID(class_params, "vAppName", "Ljava/lang/String;");
		jstr = WCHARToJString(env, createVAppParams->vAppName);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		field_id = env->GetFieldID(class_params, "vdcId", "Ljava/lang/String;");
		jstr = WCHARToJString(env, createVAppParams->vdcId);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		field_id = env->GetFieldID(class_params, "vAppSessionInfoXML", "Ljava/lang/String;");
		jstr = WCHARToJString(env, createVAppParams->pVAppConfigXML);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		if (class_params != NULL) env->DeleteLocalRef(class_params);

		result = jParams;
	} while (FALSE);
	

	return result;
}

void convertCreateVAppResult(JNIEnv *env, jobject createVAppResult, VCloud_CreateVAppResult* result)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":createVAppResult = %p"), createVAppResult);

	do
	{
		// result
		jclass  class_result = env->FindClass("com/ca/arcflash/ha/vcloudmanager/common/CreateVAppResult");
		if (class_result == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the class of CreateVAppResult"));
			break;
		}

		jfieldID field_id = env->GetFieldID(class_result, "errorMessage", "Ljava/lang/String;");
		jstring jstr = (jstring) env->GetObjectField(createVAppResult, field_id);
		wcscpy_s(result->erroMessage, JStringToWString(env, jstr).c_str());

		field_id = env->GetFieldID(class_result, "errorCode", "I");
		result->errorCode = env->GetIntField(createVAppResult, field_id);

		field_id = env->GetFieldID(class_result, "vApp", "Lcom/ca/arcflash/ha/vcloudmanager/objects/VCloudVApp;");
		jobject jobj = env->GetObjectField(createVAppResult, field_id);
		if (jobj != NULL)
		{
			convertVApp_J2C(env, jobj, result->vAppInfo);
		}

		if (class_result != NULL) env->DeleteLocalRef(class_result);

	} while (FALSE);
}

VCloud_CreateVAppResult* NativeClass::createVApp(const VCloud_CreateVAppParams* createVAppParams)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":createVAppParams = %p"), createVAppParams);

	VCloud_CreateVAppResult* result = new VCloud_CreateVAppResult;
	memset(result, 0, sizeof(VCloud_CreateVAppResult));
	do
	{
		if (createVAppParams == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":createVAppParams is NULL"));
			result->errorCode = -1;
			break;
		}

		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result->errorCode = -3;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result->errorCode = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			result->errorCode = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID mid = env->GetMethodID(clsV2P, "createVApp", "(Lcom/ca/arcflash/ha/vcloudmanager/common/CreateVAppParams;)Lcom/ca/arcflash/ha/vcloudmanager/common/CreateVAppResult;");
		if (mid == NULL)
		{
			result->errorCode = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of createVApp"));
			goto invokejavavirtualmachine_error;
		}

		jobject jParams = convertCreateVAppParams(env, createVAppParams);
		if (jParams == NULL)
		{
			result->errorCode = -7;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":failed to convertCreateVAppParams"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jobject jResult = env->CallObjectMethod(jvcloudInstance, mid, jParams);
		
		convertCreateVAppResult(env, jResult, result);
		if (result == NULL)
		{
			result->errorCode = -9;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":failed to convertCreateVAppResult"));
			goto invokejavavirtualmachine_error;
		}

		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}


jobject convertImportVMParams(JNIEnv *env, const VCloud_ImportVMParams* importVMParams)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":importVMParams = %p"), importVMParams);

	jobject result = NULL;

	do
	{
		// params
		jclass  class_params = env->FindClass("com/ca/arcflash/ha/vcloudmanager/common/ImportVMParams");
		if (class_params == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the class of ImportVMParams"));
			break;
		}

		jmethodID params_constructor = env->GetMethodID(class_params, "<init>", "()V");
		jobject jParams = env->NewObject(class_params, params_constructor);

		if (jParams == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable constructor ImportVMParams"));
			break;
		}

		jfieldID field_id = env->GetFieldID(class_params, "vCenterId", "Ljava/lang/String;");
		jstring jstr = WCHARToJString(env, importVMParams->vCenterId);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		field_id = env->GetFieldID(class_params, "vAppId", "Ljava/lang/String;");
		jstr = WCHARToJString(env, importVMParams->vAppId);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		field_id = env->GetFieldID(class_params, "vdcStorageProfileId", "Ljava/lang/String;");
		jstr = WCHARToJString(env, importVMParams->vdcStorageProfileId);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		field_id = env->GetFieldID(class_params, "vmName", "Ljava/lang/String;");
		jstr = WCHARToJString(env, importVMParams->vmName);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		field_id = env->GetFieldID(class_params, "vmMoRef", "Ljava/lang/String;");
		jstr = WCHARToJString(env, importVMParams->vmMoRef);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		field_id = env->GetFieldID(class_params, "vAppInfoFilePath", "Ljava/lang/String;");
		jstr = WCHARToJString(env, importVMParams->vAppInfoFilePath);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		field_id = env->GetFieldID(class_params, "vAppSessionInfoXML", "Ljava/lang/String;");
		jstr = WCHARToJString(env, importVMParams->pVAppConfigXML);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		if (class_params != NULL) env->DeleteLocalRef(class_params);

		result = jParams;

	} while (FALSE);


	return result;
}

void convertImportVMResult(JNIEnv *env, jobject importVMResult, VCloud_ImportVMResult* result)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":importVMResult = %p"), importVMResult);

	do
	{
		// result
		jclass  class_result = env->FindClass("com/ca/arcflash/ha/vcloudmanager/common/ImportVMResult");
		if (class_result == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the class of ImportVMResult"));
			break;
		}

		jfieldID field_id = env->GetFieldID(class_result, "errorMessage", "Ljava/lang/String;");
		jstring jstr = (jstring) env->GetObjectField(importVMResult, field_id);
		wcscpy_s(result->erroMessage, JStringToWString(env, jstr).c_str());

		field_id = env->GetFieldID(class_result, "errorCode", "I");
		result->errorCode = env->GetIntField(importVMResult, field_id);

		field_id = env->GetFieldID(class_result, "vm", "Lcom/ca/arcflash/ha/vcloudmanager/objects/VCloudVM;");
		jobject jobj = env->GetObjectField(importVMResult, field_id);

		if (jobj != NULL)
		{
			convertVM_J2C(env, jobj, result->vmInfo);
		}
		
		if (class_result != NULL) env->DeleteLocalRef(class_result);

	} while (FALSE);
}

VCloud_ImportVMResult* NativeClass::importVM(const VCloud_ImportVMParams* importVMParams)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":importVMParams = %p"), importVMParams);

	VCloud_ImportVMResult* result = new VCloud_ImportVMResult;
	memset(result, 0, sizeof(VCloud_ImportVMResult));

	do
	{
		if (importVMParams == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":importVMParams is NULL"));
			result->errorCode = -1;
			break;
		}

		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result->errorCode = -3;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result->errorCode = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			result->errorCode = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID mid = env->GetMethodID(clsV2P, "importVM", "(Lcom/ca/arcflash/ha/vcloudmanager/common/ImportVMParams;)Lcom/ca/arcflash/ha/vcloudmanager/common/ImportVMResult;");
		if (mid == NULL)
		{
			result->errorCode = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of importVM"));
			goto invokejavavirtualmachine_error;
		}

		jobject jParams = convertImportVMParams(env, importVMParams);
		if (jParams == NULL)
		{
			result->errorCode = -7;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":failed to convertImportVMParams"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jobject jResult = env->CallObjectMethod(jvcloudInstance, mid, jParams);

		convertImportVMResult(env, jResult, result);
		if (result == NULL)
		{
			result->errorCode = -9;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":failed to convertImportVMResult"));
			goto invokejavavirtualmachine_error;
		}

		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}


int NativeClass::deleteVApp(const wchar_t* vAppId)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":vAppId = %s"), vAppId);

	int result = 0;
	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID mid = env->GetMethodID(clsV2P, "deleteVApp", "(Ljava/lang/String;)I");
		if (mid == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of deleteVApp"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrvAppId = WCHARToJString(env, vAppId);
		jint jRet = env->CallIntMethod(jvcloudInstance, mid, jstrvAppId);
		result = jRet;

		if (jstrvAppId != NULL) env->DeleteLocalRef(jstrvAppId);

		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}


int NativeClass::powerOnVApp(const wchar_t* vAppId)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":vAppId = %s"), vAppId);

	int result = 0;
	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID mid = env->GetMethodID(clsV2P, "powerOnVApp", "(Ljava/lang/String;)I");
		if (mid == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of powerOnVApp"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrvAppId = WCHARToJString(env, vAppId);
		jint jRet = env->CallIntMethod(jvcloudInstance, mid, jstrvAppId);
		result = jRet;

		if (jstrvAppId != NULL) env->DeleteLocalRef(jstrvAppId);

		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}


int NativeClass::powerOffVApp(const wchar_t* vAppId)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":vAppId = %s"), vAppId);

	int result = 0;
	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID mid = env->GetMethodID(clsV2P, "powerOffVApp", "(Ljava/lang/String;)I");
		if (mid == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of powerOffVApp"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrvAppId = WCHARToJString(env, vAppId);
		jint jRet = env->CallIntMethod(jvcloudInstance, mid, jstrvAppId);
		result = jRet;

		if (jstrvAppId != NULL) env->DeleteLocalRef(jstrvAppId);

		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}

int NativeClass::renameVAppEx(const wchar_t* vAppId, const wchar_t* suffix, const bool append, const bool renameVM)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":vAppId = %s, suffix = %s, append = %d, renameVM = %d"), vAppId, suffix, append, renameVM);

	int result = 0;
	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID mid = env->GetMethodID(clsV2P, "renameVAppEx", "(Ljava/lang/String;Ljava/lang/String;ZZ)I");
		if (mid == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of renameVAppEx"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrvAppId = WCHARToJString(env, vAppId);
		jstring jstrSuffix = WCHARToJString(env, suffix);
		jint jRet = env->CallIntMethod(jvcloudInstance, mid, jstrvAppId, jstrSuffix, (jboolean) append, (jboolean) renameVM);
		result = jRet;

		if (jstrvAppId != NULL) env->DeleteLocalRef(jstrvAppId);
		if (jstrSuffix != NULL) env->DeleteLocalRef(jstrSuffix);

		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}


void jList2List_VCloudVApp(JNIEnv *env, jobject jlist, VCloudVApp_Info** vAppList, int* count)
{
	jclass Class_ArrayList = env->GetObjectClass(jlist);
	jmethodID mid_size = env->GetMethodID(Class_ArrayList, "size", "()I");
	jmethodID mid_get = env->GetMethodID(Class_ArrayList, "get", "(I)Ljava/lang/Object;");

	jint size = env->CallIntMethod(jlist, mid_size);

	if (size > 0)
	{
		*vAppList = new VCloudVApp_Info[size];
		memset(*vAppList, 0, size * sizeof(VCloudVApp_Info));
		*count = size;
	}

	for (jint i = 0; i < size; i++)
	{
		jobject jobj = (jstring) env->CallObjectMethod(jlist, mid_get, i);
		convertVApp_J2C(env, jobj, (*vAppList)[i]);
	}

	if (Class_ArrayList != NULL) env->DeleteLocalRef(Class_ArrayList);
}

int NativeClass::getVAppListOfVDC(const wchar_t* vdcId, VCloudVApp_Info** vAppList, int* count)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":vdcId = %s"), vdcId);

	int result = 0;
	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID mid = env->GetMethodID(clsV2P, "getVApps", "(Ljava/lang/String;Ljava/util/List;)I");
		if (mid == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of getVApps"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrVdcId = WCHARToJString(env, vdcId);

		jclass  class_ArrayList = env->FindClass("java/util/ArrayList");
		jmethodID arrayList_constructor = env->GetMethodID(class_ArrayList, "<init>", "()V");
		jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
		jobject jvappList = env->NewObject(class_ArrayList, arrayList_constructor);

		jint jRet = env->CallIntMethod(jvcloudInstance, mid, jstrVdcId, jvappList);
		result = jRet;

		jList2List_VCloudVApp(env, jvappList, vAppList, count);

		if (jstrVdcId != NULL) env->DeleteLocalRef(jstrVdcId);
		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}


int NativeClass::getVApp(const wchar_t* vAppId, VCloudVApp_Info* vAppInfo)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":vAppId = %s"), vAppId);

	int result = 0;
	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID mid = env->GetMethodID(clsV2P, "getVApp", "(Ljava/lang/String;Lcom/ca/arcflash/ha/vcloudmanager/objects/VCloudVApp;)I");
		if (mid == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of getVApp"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrVAppId = WCHARToJString(env, vAppId);

		jclass  class_vapp = env->FindClass("com/ca/arcflash/ha/vcloudmanager/objects/VCloudVApp");
		if (class_vapp == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the class of VCloudVApp"));
			break;
		}
		jmethodID vapp_constructor = env->GetMethodID(class_vapp, "<init>", "()V");
		jobject jvapp = env->NewObject(class_vapp, vapp_constructor);

		jint jRet = env->CallIntMethod(jvcloudInstance, mid, jstrVAppId, jvapp);
		result = jRet;

		convertVApp_J2C(env, jvapp, *vAppInfo);

		if (jstrVAppId != NULL) env->DeleteLocalRef(jstrVAppId);
		if (class_vapp != NULL) env->DeleteLocalRef(class_vapp);
		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}

int NativeClass::getVAppInOrg(const wchar_t* vdcId, const wchar_t* vAppName, VCloudVApp_Info* vAppInfo)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":vdcId = %s, vAppName = %s"), vdcId, vAppName);

	int result = 0;
	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID mid = env->GetMethodID(clsV2P, "getVAppInOrg", "(Ljava/lang/String;Ljava/lang/String;Lcom/ca/arcflash/ha/vcloudmanager/objects/VCloudVApp;)I");
		if (mid == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of getVApp"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrVdcId = WCHARToJString(env, vdcId);
		jstring jstrVAppName = WCHARToJString(env, vAppName);

		jclass  class_vapp = env->FindClass("com/ca/arcflash/ha/vcloudmanager/objects/VCloudVApp");
		if (class_vapp == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the class of VCloudVApp"));
			break;
		}
		jmethodID vapp_constructor = env->GetMethodID(class_vapp, "<init>", "()V");
		jobject jvapp = env->NewObject(class_vapp, vapp_constructor);

		jint jRet = env->CallIntMethod(jvcloudInstance, mid, jstrVdcId, jstrVAppName, jvapp);
		result = jRet;

		convertVApp_J2C(env, jvapp, *vAppInfo);

		if (jstrVdcId != NULL) env->DeleteLocalRef(jstrVdcId);
		if (jstrVAppName != NULL) env->DeleteLocalRef(jstrVAppName);
		if (class_vapp != NULL) env->DeleteLocalRef(class_vapp);
		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}


int NativeClass::getESXHostListByDatastoreMoRef(const wchar_t* datastoreMoRef, vector<wstring>& esxHostList)
{
	V2PNFlog log;
	log.writeDebugLog(_T("Attempting to getESXHostListByDatastoreMoRef for datastoreMoRef %ls."), datastoreMoRef);

	int result = 0;
	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			result = -1;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			break;
		}

		if (objectInstance == NULL)
		{
			result = -2;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(objectInstance);
		if (clsV2P == NULL)
		{
			result = -3;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID midMethod = env->GetMethodID(clsV2P, "getESXHostsByDatastoreMoRef", "(Ljava/lang/String;)Ljava/util/List;");
		if (midMethod == NULL)
		{
			result = -4;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of getESXHostsByDatastoreMoRef"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrDatastoreMoRef = WCHARToJString(env, datastoreMoRef);

		jobject jobj = env->CallObjectMethod(objectInstance, midMethod, jstrDatastoreMoRef);

		if (jobj == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Cannot getESXHostsByDatastoreMoRef by getESXHostsByDatastoreMoRef"));
			goto invokejavavirtualmachine_error;
		}

		jList2Vector(env, jobj, esxHostList);

		if (jstrDatastoreMoRef != NULL) env->DeleteLocalRef(jstrDatastoreMoRef);
		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}


void convertDatastore_J2C(JNIEnv *env, jobject jobj, VCloudDatastore_Info& datastoreInfo)
{
	V2PNFlog log;

	do
	{
		// vapp info
		jclass  class_datastore = env->FindClass("com/ca/arcflash/ha/vcloudmanager/objects/VCloudDatastore");
		if (class_datastore == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the class of VCloudDatastore"));
			break;
		}

		jfieldID field_id = env->GetFieldID(class_datastore, "name", "Ljava/lang/String;");
		wchar_t* temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(datastoreInfo.name, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_datastore, "id", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(datastoreInfo.id, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_datastore, "moRef", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(datastoreInfo.moRef, temp);
		SAFE_FREE(temp);

		if (class_datastore != NULL) env->DeleteLocalRef(class_datastore);
	} while (false);
}

int NativeClass::getDatastore(const wchar_t* datastoreId, VCloudDatastore_Info* datastoreInfo)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":datastoreId = %s"), datastoreId);

	int result = 0;
	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID mid = env->GetMethodID(clsV2P, "getDatastore", "(Ljava/lang/String;Lcom/ca/arcflash/ha/vcloudmanager/objects/VCloudDatastore;)I");
		if (mid == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of getDatastore"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrDatastoreId = WCHARToJString(env, datastoreId);

		jclass  class_datastore = env->FindClass("com/ca/arcflash/ha/vcloudmanager/objects/VCloudDatastore");
		if (class_datastore == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the class of VCloudDatastore"));
			break;
		}
		jmethodID datastore_constructor = env->GetMethodID(class_datastore, "<init>", "()V");
		jobject jdatastore = env->NewObject(class_datastore, datastore_constructor);

		jint jRet = env->CallIntMethod(jvcloudInstance, mid, jstrDatastoreId, jdatastore);
		result = jRet;

		convertDatastore_J2C(env, jdatastore, *datastoreInfo);

		if (jstrDatastoreId != NULL) env->DeleteLocalRef(jstrDatastoreId);
		if (class_datastore != NULL) env->DeleteLocalRef(class_datastore);
		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}


void convertESXHost_J2C(JNIEnv *env, jobject jobj, VCloudESXHost_Info& vCloudESXHostInfo)
{
	V2PNFlog log;

	do
	{
		// esx host info
		jclass  class_esxHost = env->FindClass("com/ca/arcflash/ha/vcloudmanager/objects/VCloudESXHost");
		if (class_esxHost == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the class of VCloudESXHost"));
			break;
		}

		jfieldID field_id = env->GetFieldID(class_esxHost, "name", "Ljava/lang/String;");
		wchar_t* temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vCloudESXHostInfo.name, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_esxHost, "id", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vCloudESXHostInfo.id, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_esxHost, "vCenter", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vCloudESXHostInfo.vCenter, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_esxHost, "vCenterId", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vCloudESXHostInfo.vCenterId, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_esxHost, "cpuType", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vCloudESXHostInfo.cpuType, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_esxHost, "vmMoRef", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vCloudESXHostInfo.moRef, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_esxHost, "numOfCpusPackages", "I");
		vCloudESXHostInfo.numOfCpusPackages = (jint) env->GetIntField(jobj, field_id);

		field_id = env->GetFieldID(class_esxHost, "numOfCpusLogical", "I");
		vCloudESXHostInfo.numOfCpusLogical = (jint) env->GetIntField(jobj, field_id);

		field_id = env->GetFieldID(class_esxHost, "cpuTotal", "J");
		vCloudESXHostInfo.cpuTotal = (jint) env->GetIntField(jobj, field_id);

		field_id = env->GetFieldID(class_esxHost, "memTotal", "J");
		vCloudESXHostInfo.memTotal = (jint) env->GetIntField(jobj, field_id);

		field_id = env->GetFieldID(class_esxHost, "hostOsName", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vCloudESXHostInfo.hostOsName, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_esxHost, "hostOsVersion", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(vCloudESXHostInfo.hostOsVersion, temp);
		SAFE_FREE(temp);

		if (class_esxHost != NULL) env->DeleteLocalRef(class_esxHost);
	} while (false);
}

void jList2List_VCloudESXHost(JNIEnv *env, jobject jlist, VCloudESXHost_Info** esxHostList, int* count)
{
	jclass Class_ArrayList = env->GetObjectClass(jlist);
	jmethodID mid_size = env->GetMethodID(Class_ArrayList, "size", "()I");
	jmethodID mid_get = env->GetMethodID(Class_ArrayList, "get", "(I)Ljava/lang/Object;");

	jint size = env->CallIntMethod(jlist, mid_size);

	if (size > 0)
	{
		*esxHostList = new VCloudESXHost_Info[size];
		memset(*esxHostList, 0, size * sizeof(VCloudESXHost_Info));
		*count = size;
	}

	for (jint i = 0; i < size; i++)
	{
		jobject jobj = (jstring) env->CallObjectMethod(jlist, mid_get, i);
		convertESXHost_J2C(env, jobj, (*esxHostList)[i]);
	}

	if (Class_ArrayList != NULL) env->DeleteLocalRef(Class_ArrayList);
}

int NativeClass::getESXHostListOfVDC(const wchar_t* vdcId, VCloudESXHost_Info** esxHostList, int* count)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":vdcId = %s"), vdcId);

	int result = 0;
	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID mid = env->GetMethodID(clsV2P, "getESXHostsOfVDC", "(Ljava/lang/String;Ljava/util/List;)I");
		if (mid == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of getESXHostsOfVDC"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrVdcId = WCHARToJString(env, vdcId);

		jclass  class_ArrayList = env->FindClass("java/util/ArrayList");
		jmethodID arrayList_constructor = env->GetMethodID(class_ArrayList, "<init>", "()V");
		jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
		jobject jEsxHostList = env->NewObject(class_ArrayList, arrayList_constructor);

		jint jRet = env->CallIntMethod(jvcloudInstance, mid, jstrVdcId, jEsxHostList);
		result = jRet;

		jList2List_VCloudESXHost(env, jEsxHostList, esxHostList, count);

		if (jstrVdcId != NULL) env->DeleteLocalRef(jstrVdcId);
		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}

void convertStorageProfile_J2C(JNIEnv *env, jobject jobj, VCloud_StorageProfile& storageProfile)
{
	V2PNFlog log;

	do
	{
		jclass  class_storageProfile = env->FindClass("com/ca/arcflash/ha/vcloudmanager/objects/VCloudStorageProfile");
		if (class_storageProfile == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the class of VCloudStorageProfile"));
			break;
		}

		jfieldID field_id = env->GetFieldID(class_storageProfile, "name", "Ljava/lang/String;");
		wchar_t* temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(storageProfile.name, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_storageProfile, "id", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(storageProfile.id, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_storageProfile, "moRef", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jobj, field_id);
		if (temp != NULL) wcscpy_s(storageProfile.moRef, temp);
		SAFE_FREE(temp);

		if (class_storageProfile != NULL) env->DeleteLocalRef(class_storageProfile);
	} while (false);
}
int NativeClass::getStorageProfileListOfVDC(const wchar_t* vdcId, VCloud_StorageProfile** storageProfileList, int* count)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":vdcId = %s"), vdcId);

	int result = 0;
	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID mid = env->GetMethodID(clsV2P, "getStorageProfiles", "(Ljava/lang/String;Ljava/util/List;)I");
		if (mid == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of getStorageProfiles"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jstring jstrVdcId = WCHARToJString(env, vdcId);

		jclass  class_ArrayList = env->FindClass("java/util/ArrayList");
		jmethodID arrayList_constructor = env->GetMethodID(class_ArrayList, "<init>", "()V");
		jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
		jobject jStorageProfileList = env->NewObject(class_ArrayList, arrayList_constructor);

		jint jRet = env->CallIntMethod(jvcloudInstance, mid, jstrVdcId, jStorageProfileList);
		result = jRet;

		jclass Class_ArrayList = env->GetObjectClass(jStorageProfileList);
		jmethodID mid_size = env->GetMethodID(Class_ArrayList, "size", "()I");
		jmethodID mid_get = env->GetMethodID(Class_ArrayList, "get", "(I)Ljava/lang/Object;");

		jint size = env->CallIntMethod(jStorageProfileList, mid_size);

		if (size > 0)
		{
			*storageProfileList = new VCloud_StorageProfile[size];
			memset(*storageProfileList, 0, size * sizeof(VCloud_StorageProfile));
			*count = size;
		}

		for (jint i = 0; i < size; i++)
		{
			jobject jobj = (jstring)env->CallObjectMethod(jStorageProfileList, mid_get, i);
			convertStorageProfile_J2C(env, jobj, (*storageProfileList)[i]);
		}

		if (Class_ArrayList != NULL) env->DeleteLocalRef(Class_ArrayList);

		if (jstrVdcId != NULL) env->DeleteLocalRef(jstrVdcId);
		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}

jobject convertVerifyVCloudInfo(JNIEnv *env, const VCloud_VerifyInfo* vCloudInfo)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":vCloudInfo = %p"), vCloudInfo);

	jobject result = NULL;

	do
	{
		// params
		jclass  class_params = env->FindClass("com/ca/arcflash/ha/vcloudmanager/common/VerifyVCloudInfo");
		if (class_params == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the class of VerifyVCloudInfo"));
			break;
		}

		jmethodID params_constructor = env->GetMethodID(class_params, "<init>", "()V");
		jobject jParams = env->NewObject(class_params, params_constructor);

		if (jParams == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable constructor VerifyVCloudInfo"));
			break;
		}

		jfieldID field_id = env->GetFieldID(class_params, "orgId", "Ljava/lang/String;");
		jstring jstr = WCHARToJString(env, vCloudInfo->orgId);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		field_id = env->GetFieldID(class_params, "orgName", "Ljava/lang/String;");
		jstr = WCHARToJString(env, vCloudInfo->orgName);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		field_id = env->GetFieldID(class_params, "orgVdcId", "Ljava/lang/String;");
		jstr = WCHARToJString(env, vCloudInfo->orgVdcId);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		field_id = env->GetFieldID(class_params, "orgVdcName", "Ljava/lang/String;");
		jstr = WCHARToJString(env, vCloudInfo->orgVdcName);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		field_id = env->GetFieldID(class_params, "vAppId", "Ljava/lang/String;");
		jstr = WCHARToJString(env, vCloudInfo->vAppId);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		field_id = env->GetFieldID(class_params, "vAppName", "Ljava/lang/String;");
		jstr = WCHARToJString(env, vCloudInfo->vAppName);
		env->SetObjectField(jParams, field_id, jstr);
		if (jstr != NULL) env->DeleteLocalRef(jstr);

		if (class_params != NULL) env->DeleteLocalRef(class_params);

		result = jParams;

	} while (FALSE);


	return result;
}
void getVerifyVCloudInfo(JNIEnv *env, jobject jParams, VCloud_VerifyInfo* vCloudInfo)
{
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":jParams = %p"), jParams);

	do
	{
		// result
		jclass  class_result = env->FindClass("com/ca/arcflash/ha/vcloudmanager/common/VerifyVCloudInfo");
		if (class_result == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the class of VerifyVCloudInfo"));
			break;
		}

		jfieldID field_id = env->GetFieldID(class_result, "orgId", "Ljava/lang/String;");
		wchar_t* temp = GetStringFromField(env, &jParams, field_id);
		if (temp != NULL) wcscpy_s(vCloudInfo->orgId, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_result, "orgName", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jParams, field_id);
		if (temp != NULL) wcscpy_s(vCloudInfo->orgName, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_result, "orgVdcId", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jParams, field_id);
		if (temp != NULL) wcscpy_s(vCloudInfo->orgVdcId, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_result, "orgVdcName", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jParams, field_id);
		if (temp != NULL) wcscpy_s(vCloudInfo->orgVdcName, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_result, "vAppId", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jParams, field_id);
		if (temp != NULL) wcscpy_s(vCloudInfo->vAppId, temp);
		SAFE_FREE(temp);

		field_id = env->GetFieldID(class_result, "vAppName", "Ljava/lang/String;");
		temp = GetStringFromField(env, &jParams, field_id);
		if (temp != NULL) wcscpy_s(vCloudInfo->vAppName, temp);
		SAFE_FREE(temp);

		if (class_result != NULL) env->DeleteLocalRef(class_result);

	} while (FALSE);
}
int NativeClass::verifyVCloudInfo(VCloud_VerifyInfo* vCloudInfo)
{
	V2PNFlog log;

	int result = 0;
	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			result = -3;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Not connected yet."));
			result = -4;
			goto invokejavavirtualmachine_error;
		}

		jclass clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			result = -5;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			goto invokejavavirtualmachine_error;
		}

		jmethodID mid = env->GetMethodID(clsV2P, "verifyVCloudInfo", "(Lcom/ca/arcflash/ha/vcloudmanager/common/VerifyVCloudInfo;)I");
		if (mid == NULL)
		{
			result = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of verifyVCloudInfo"));
			goto invokejavavirtualmachine_error;
		}

		jobject jParams = convertVerifyVCloudInfo(env, vCloudInfo);
		if (jParams == NULL)
		{
			result = -7;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":failed to convertVerifyVCloudInfo"));
			goto invokejavavirtualmachine_error;
		}

		// call method
		jint jRet = env->CallIntMethod(jvcloudInstance, mid, jParams);
		result = jRet;
		if (!result)
		{
			getVerifyVCloudInfo(env, jParams, vCloudInfo);
		}

		if (clsV2P != NULL) env->DeleteLocalRef(clsV2P);

	invokejavavirtualmachine_error:
		if (env->ExceptionOccurred())
		{
			env->ExceptionDescribe();
		}
	} while (false);

	DetachJVM();

	return result;
}

/// VIX is replaced by vSphere  -- begin --
int NativeClass::ClearVMResult(IN int vmHandle)
{
	VMResult_Iterator iter = m_mapVMResult.find(vmHandle);
	if (iter != m_mapVMResult.end())
	{
		if (NULL != iter->second.m_pGuestFileInfo)
		{
			delete iter->second.m_pGuestFileInfo;
		}
		if (NULL != iter->second.m_pGuestProcessInfo)
		{
			delete iter->second.m_pGuestProcessInfo;
		}
		iter->second.m_bResult = FALSE;
		iter->second.m_nListItemCount = 0;
	}
	return 0;
}

int NativeClass::ClearAllVMResult()
{
	VMResult_Iterator iter;
	for (iter = m_mapVMResult.begin(); iter != m_mapVMResult.end(); iter++)
	{
		if (NULL != iter->second.m_pGuestFileInfo)
		{
			delete iter->second.m_pGuestFileInfo;
		}
		if (NULL != iter->second.m_pGuestProcessInfo)
		{
			delete iter->second.m_pGuestProcessInfo;
		}
	}
	m_mapVMResult.clear();
	return 0;
}
int NativeClass::Vix_ConnectToHost(IN const char *pszHostName, IN int nHostPort, IN const char *pszUserName, IN const char *pszPassword,
	IN VixMgr_HostOptions nOptions, IN VixMgr_Handle hPropertyListHandle, IN VixEventProc *fnCallbackProc, IN void *pvClientData)
{
	int rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":connect to host. [%S]"), pszHostName );

	do
	{
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			rc = -3;
			break;
		}

		
		clsV2P = env->FindClass("com/ca/arcflash/ha/vmwaremanager/V2P_Export");
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to get the class V2P_Export"));
			rc = -4;
			break;
		}

		if( NULL == jvcloudInstance )
		{
			jmethodID midConstructor = env->GetMethodID(clsV2P, "<init>", "()V");
			if (midConstructor == NULL)
			{
				rc = -11;
				break;
			}
			jobject obj = env->NewObject(clsV2P, midConstructor);
			if (obj == NULL)
			{
				log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to new the object of V2P_Export"));
				objectInstance = NULL;
				rc = -5;
				break;
			}
			// save to member field
			jvcloudInstance = obj;
		}

		// public int connectToHost(String hostName, int port, String hostUser, String hostPassword)																			  
		jmethodID midConnect = env->GetMethodID(clsV2P, "connectToHost", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)I");
		if (midConnect == NULL)
		{
			rc = -6;
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method: connectToHost"));
			break;
		}

		jstring pJStrHost = CharToJString(env, pszHostName);
		jstring pJStrUsername = CharToJString(env, pszUserName);
		jstring pJStrPassword = CharToJString(env, pszPassword);

		// call connectToHost
		rc = env->CallIntMethod(jvcloudInstance, midConnect, pJStrHost, nHostPort, pJStrUsername, pJStrPassword);
		if (rc <= 0)
		{
			rc = -7;
		}

		SafeDeleteLocalRef(env, pJStrHost);
		SafeDeleteLocalRef(env, pJStrUsername);
		SafeDeleteLocalRef(env, pJStrPassword);
#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif
	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Connected to host [%S], rc=%d"), pszHostName, rc);

	return rc;
}

void NativeClass::Vix_DisconnectFromHost(IN int iHostHandle)
{
	jint rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":disconnect from host."));

	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
			if (!bRet)
#else
			if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			break;
		}

		// int disconnectFromHost(int iSess)
		jmethodID mid = env->GetMethodID(clsV2P, "disconnectFromHost", "(I)I");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of disconnectFromHost"));
			break;
		}

		// call disconnectFromHost
		rc = env->CallIntMethod(jvcloudInstance, mid, iHostHandle);
#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif
	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif

	DetachJVM();

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":disconnected from host. rc=%d"), rc);
}
int NativeClass::Vix_GetESXVersionVM(IN VixMgr_Handle hHostHandle, IN const char* pszVMName)
{
	int nVersion = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":get ESX version"));

	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			break;
		}

		// int getVMEsxVersion(int iEsxSess, String vmName)
		jmethodID mid = env->GetMethodID(clsV2P, "getVMEsxVersion", "(ILjava/lang/String;)I");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of getVMEsxVersion"));
			break;
		}

		jstring pJStrVMName = CharToJString(env, pszVMName);
		// call setVMName
		nVersion = env->CallIntMethod(jvcloudInstance, mid, hHostHandle, pJStrVMName);
		SafeDeleteLocalRef(env, pJStrVMName);
#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif
	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":get ESX version. ver=0x%08x"), nVersion);
	return nVersion;
}
int NativeClass::VixVM_Open(IN int iHostHandle, IN const char *pszVMName)
{//ACCEPT: [Storage1] sonmi01-002-d2d/sonmi01-002-d2d.vmx
	char szVMName[MAX_PATH] = "";

	do
	{
		const char* pszName = strstr(pszVMName, "] ");
		if (NULL == pszName)
		{
			strncpy_s( szVMName, _countof(szVMName), pszVMName, _TRUNCATE );
			break;
		}

		pszName += 2;
		const char* pszEnd = strchr(pszName, '/');
		if (NULL == pszEnd)
		{
			break;
		}

		size_t ccVMName = pszEnd - pszName;
		strncpy_s(szVMName, _countof(szVMName), pszName, ccVMName);
		szVMName[ccVMName] = '\0';
	} while (0);

	jint rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":set VM Name"));

	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			rc = -1;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			rc = -2;
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			rc = -3;
			break;
		}

		// int setVMName(int iEsxSess, String vmName)
		jmethodID mid = env->GetMethodID(clsV2P, "openVM", "(ILjava/lang/String;)I");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of loginVM"));
			rc = -4;
			break;
		}

		jstring pJStrVMName = CharToJString(env, szVMName);
		// call setVMName
		rc = env->CallIntMethod(jvcloudInstance, mid, iHostHandle, pJStrVMName);
		SafeDeleteLocalRef(env, pJStrVMName);
#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif
	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":set VM name. rc=%d"), rc);
	return rc;
}
int NativeClass::VixVM_LoginInGuest(IN int iHostHandle, IN const char *pszUserName, IN const char *pszPassword, IN int nOptions, IN VixEventProc *fnCallbackProc, void *pvClientData)
{
	jint rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":login guest OS"));

	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			rc = -1;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			rc = -2;
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			rc = -3;
			break;
		}

		// int loginVM(int iEsxSess, String vmName, String vmUser, String vmPassword, boolean onInteractiveSession)
		jmethodID mid = env->GetMethodID(clsV2P, "loginVM", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)I");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of loginVM"));
			rc = -4;
			break;
		}

		jstring pJStrUsername = CharToJString(env, pszUserName);
		jstring pJStrPassword = CharToJString(env, pszPassword);

		//VixMgr_LOGIN_IN_GUEST_REQUIRE_INTERACTIVE_ENVIRONMENT      = 0x08,
		// call loginVM
		rc = env->CallIntMethod(jvcloudInstance, mid, iHostHandle, NULL, pJStrUsername, pJStrPassword, nOptions == 8 ? true : false);
		SafeDeleteLocalRef(env, pJStrUsername);
		SafeDeleteLocalRef(env, pJStrPassword);

		if( rc > 0 )
		{
			VM_RESULT vmResult;
			memset( &vmResult, 0, sizeof( vmResult ) );
			m_mapVMResult.insert( VMResult_Pair( rc, vmResult ) );
		}
#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif
	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":login guest OS. rc=%d"), rc);
	return rc;
}
int NativeClass::VixVM_LogoutFromGuest(IN int vmHandle, IN VixEventProc *callbackProc, IN void *clientData)
{
	jint rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	//log.writeDebugLog(_TEXT(__FUNCTION__) _T(":vix logout guest OS."));

	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			rc = -1;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			rc = -2;
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			rc = -3;
			break;
		}

		// int int logoutVM(int iSess)
		jmethodID mid = env->GetMethodID(clsV2P, "logoutVM", "(I)I");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of logoutVM"));
			rc = -4;
			break;
		}

		// call logoutVM
		rc = env->CallIntMethod(jvcloudInstance, mid, vmHandle);

		VMResult_Iterator iter = m_mapVMResult.find(vmHandle);
		if (m_mapVMResult.end() != iter)
		{
			m_mapVMResult.erase(iter);
		}
#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif
	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":logout guest OS. rc=%d"), rc);
	return rc;
}

int NativeClass::VixVM_RunProgramInGuest(IN int vmHandle, IN const char *pszGuestProgramName, IN const char *pszCommandLineArgs,
	IN VixMgr_RunProgramOptions nOptions, IN VixMgr_Handle hPropertyListHandle, IN VixEventProc *fnCallbackProc, IN void *pvClientData)
{
	jint rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	//log.writeDebugLog(_TEXT(__FUNCTION__) _T(":run program in guest OS."));

	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			rc = -1;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			rc = -2;
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			rc = -3;
			break;
		}

		// int runProcessInGuest(int iSess, String exeName, String exeParam, boolean onInteractiveSession)
		jmethodID mid = env->GetMethodID(clsV2P, "runProcessInGuest", "(ILjava/lang/String;Ljava/lang/String;Z)I");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of runProcessInGuest"));
			rc = -4;
			break;
		}

		//VixMgr_RUNPROGRAM_ACTIVATE_WINDOW      = 0x0002,
		jstring pJStrExe = CharToJString(env, pszGuestProgramName);
		jstring pJStrParam = CharToJString(env, pszCommandLineArgs);
		// call runProcessInGuest
		rc = env->CallIntMethod(jvcloudInstance, mid, vmHandle, pJStrExe, pJStrParam, nOptions == 2 ? true : false);
		SafeDeleteLocalRef(env, pJStrExe);
		SafeDeleteLocalRef(env, pJStrParam);

#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif

	} while (false);

#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":run program in guest OS. rc=%d. command=[%S]"), rc, pszGuestProgramName );
	return rc;
}

int NativeClass::VixVM_CopyFileFromHostToGuest(IN int vmHandle, const char *hostPathName, const char *guestPathName, int options, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData)
{
	jint rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	//log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Copy file from host to guest."));

	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			rc = -1;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			rc = -2;
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			rc = -3;
			break;
		}

		// int uploadFileToGuest(int iSess, String localFilePath, String filePathInVM, boolean overwrite)
		jmethodID mid = env->GetMethodID(clsV2P, "uploadFileToGuest", "(ILjava/lang/String;Ljava/lang/String;Z)I");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of uploadFileToGuest"));
			rc = -4;
			break;
		}

		jstring pJStrLocal = CharToJString(env, hostPathName);
		jstring pJStrGuest = CharToJString(env, guestPathName);
		// call uploadFileToGuest
		rc = env->CallIntMethod(jvcloudInstance, mid, vmHandle, pJStrLocal, pJStrGuest, true);
		SafeDeleteLocalRef(env, pJStrLocal);
		SafeDeleteLocalRef(env, pJStrGuest);

#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif
	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Copy file from host to guest. rc=%d. [%S] >> [%S]"), rc, hostPathName, guestPathName );
	return rc;
}
int NativeClass::VixVM_CopyFileFromGuestToHost(IN int vmHandle, const char *guestPathName, const char *hostPathName, int options, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData)
{
	jint rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	//log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Copy file from guest to host."));

	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			rc = -1;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			rc = -2;
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			rc = -3;
			break;
		}

		// int downloadFileFromGuest(int iSess, String filePathInVM, String localFilePath )
		jmethodID mid = env->GetMethodID(clsV2P, "downloadFileFromGuest", "(ILjava/lang/String;Ljava/lang/String;)I");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of downloadFileFromGuest"));
			rc = -4;
			break;
		}

		jstring pJStrLocal = CharToJString(env, hostPathName);
		jstring pJStrGuest = CharToJString(env, guestPathName);
		// call downloadFileFromGuest
		rc = env->CallIntMethod(jvcloudInstance, mid, vmHandle, pJStrGuest, pJStrLocal);
		SafeDeleteLocalRef(env, pJStrLocal);
		SafeDeleteLocalRef(env, pJStrGuest);

#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif


	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Copy file from guest to host. rc=%d. [%S] >> [%S]"), rc, guestPathName, hostPathName);
	return rc;
}
int NativeClass::VixVM_DeleteFileInGuest(IN int vmHandle, const char *guestPathName, VixEventProc *callbackProc, void *clientData)
{
	jint rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	//log.writeDebugLog(_TEXT(__FUNCTION__) _T(":delete file in guest."));

	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			rc = -1;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			rc = -2;
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			rc = -3;
			break;
		}

		// int deleteFileInGuest(int iSess, String filePathInVM)
		jmethodID mid = env->GetMethodID(clsV2P, "deleteFileInGuest", "(ILjava/lang/String;)I");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of downloadFileFromGuest"));
			rc = -4;
			break;
		}

		jstring pJStrGuest = CharToJString(env, guestPathName);
		// call deleteFileInGuest
		rc = env->CallIntMethod(jvcloudInstance, mid, vmHandle, pJStrGuest);
		SafeDeleteLocalRef(env, pJStrGuest);
#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif
	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":delete file in guest. rc=%d [%S]"), rc, guestPathName);
	return rc;
}
int NativeClass::VixVM_FileExistsInGuest(IN int vmHandle, const char *guestPathName, VixEventProc *callbackProc, void *clientData)
{
	int rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	//log.writeDebugLog(_TEXT(__FUNCTION__) _T(":file exists in guest."));

	do
	{
		//log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			break;
		}

		// boolean fileExistInGuest(int iSess, String filePathInVM)
		jmethodID mid = env->GetMethodID(clsV2P, "fileExistInGuest", "(ILjava/lang/String;)Z");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of fileExistInGuest"));
			break;
		}

		jstring pJStrGuest = CharToJString(env, guestPathName);
		// call fileExistInGuest
		jboolean brc = env->CallBooleanMethod(jvcloudInstance, mid, vmHandle, pJStrGuest);
		SafeDeleteLocalRef(env, pJStrGuest);

		if (brc)
		{
			rc = 1;
		}

		VMResult_Iterator iter = m_mapVMResult.find(vmHandle);
		if (m_mapVMResult.end() != iter)
		{
			iter->second.m_bResult = brc != 0 ? TRUE : FALSE;
		}
#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif
	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":file exists in guest. rc=%d.[%S]"), rc, guestPathName);
	return rc;
}
PGUEST_FILE_INFO NativeClass::VixVM_ListFileInGuest(IN int vmHandle, const char *pathName, int options, VixEventProc *callbackProc, void *clientData)
{
	PGUEST_FILE_INFO pFileInfo = NULL;
	jclass clsV2P = NULL;
	jclass clsFileInfo = NULL;
	V2PNFlog log;
	//log.writeDebugLog(_TEXT(__FUNCTION__) _T(":list file in guest."));

	do
	{
		VMResult_Iterator iter = m_mapVMResult.find(vmHandle);
		if (m_mapVMResult.end() == iter)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":VM Result is not in map!") );
			break;
		}
		ClearVMResult( vmHandle );

		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		clsFileInfo = env->FindClass("com/ca/arcflash/ha/vmwaremanager/File_Info");
		if (clsV2P == NULL || NULL == clsFileInfo)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			break;
		}
		jfieldID fieldPath = env->GetFieldID(clsFileInfo, "m_strPathName", "Ljava/lang/String;");
		jfieldID fieldType = env->GetFieldID(clsFileInfo, "m_strType", "Ljava/lang/String;");
		jfieldID fieldLinkTarget = env->GetFieldID(clsFileInfo, "m_strLinkTarget", "Ljava/lang/String;");
		jfieldID fieldSize = env->GetFieldID(clsFileInfo, "m_nSize", "J");
		jfieldID fieldModifyTime = env->GetFieldID(clsFileInfo, "m_strModifyTime", "Ljava/lang/String;");
		jfieldID fieldLastAccessTime = env->GetFieldID(clsFileInfo, "m_strLastAccessTime", "Ljava/lang/String;");

		// public File_Info[] listFileInGuest(int iSess, String filePath, String pattern)
		jmethodID mid = env->GetMethodID(clsV2P, "listFileInGuest", "(ILjava/lang/String;Ljava/lang/String;)[Lcom/ca/arcflash/ha/vmwaremanager/File_Info;");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of listFileInGuest"));
			break;
		}

		jstring pJStrPath = CharToJString(env, pathName);
		jstring pJStrPattern = CharToJString(env, ".*");
		// call listFileInGuest
		jobjectArray pJObjFileInfo = (jobjectArray)env->CallBooleanMethod(jvcloudInstance, mid, vmHandle, pJStrPath, pJStrPattern);
		SafeDeleteLocalRef(env, pJStrPath);
		SafeDeleteLocalRef(env, pJStrPattern);

		if (NULL == pJObjFileInfo)
		{
			break;
		}

		jsize nFileCount = env->GetArrayLength(pJObjFileInfo);
		pFileInfo = (PGUEST_FILE_INFO)new GUEST_FILE_INFO[nFileCount];
		if (NULL == pFileInfo)
		{
			break;
		}
		PGUEST_FILE_INFO pFI = pFileInfo;
		for (jsize i = 0; i < nFileCount; i++, pFI++)
		{
			jobject tempObj = env->GetObjectArrayElement(pJObjFileInfo, i);
			jboolean isFileInfo = env->IsInstanceOf(tempObj, clsFileInfo);
			if (!isFileInfo)
			{
				log.writeDebugLog(_T("This is not a File_Info object!"));
				break;
			}
			jstring jstrPath = (jstring)env->GetObjectField(tempObj, fieldPath);
			jstring jstrType = (jstring)env->GetObjectField(tempObj, fieldType);
			jstring jstrLinkTarget = (jstring)env->GetObjectField(tempObj, fieldLinkTarget);
			jlong jnSize = (jlong)env->GetObjectField(tempObj, fieldSize);
			jstring jstrModifyTime = (jstring)env->GetObjectField(tempObj, fieldModifyTime);
			jstring jstrLastAccessTime = (jstring)env->GetObjectField(tempObj, fieldLastAccessTime);

			pFI->strPathName = JStringToWString(env,jstrPath);
			pFI->strType = JStringToWString(env, jstrType);
			pFI->strLinkTarget = JStringToWString(env, jstrLinkTarget);
			pFI->nSize = jnSize;
			pFI->tModify = JStringToTime64(env, jstrModifyTime);
			pFI->tLastAccess = JStringToTime64(env, jstrLastAccessTime);

			pFI->dwAttributes = 0;
			if (0 == wcscmp(pFI->strType.c_str(), L"directory"))
			{
				pFI->dwAttributes |= FILE_ATTRIBUTE_DIRECTORY;
			}
			if (pFI->strLinkTarget.size() > 0)
			{
				pFI->dwAttributes |= FILE_ATTRIBUTE_REPARSE_POINT;
			}
		}
		iter->second.m_pGuestFileInfo = pFileInfo;
		iter->second.m_nListItemCount = nFileCount;

#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif

	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":found all files in guest. [%S]"), pathName);
	return pFileInfo;
}
int NativeClass::VixVM_FilterOutFile(IN int vmHandle, IN bool bRemoveFile)
{
	VMResult_Iterator iter = m_mapVMResult.find(vmHandle);
	if (m_mapVMResult.end() != iter
		&& NULL != iter->second.m_pGuestFileInfo
		&& iter->second.m_nListItemCount > 0 )
	{
		int i;
		PGUEST_FILE_INFO pSrc = iter->second.m_pGuestFileInfo;
		PGUEST_FILE_INFO pDest = iter->second.m_pGuestFileInfo;
		int nNewCount = iter->second.m_nListItemCount;
		bool bRemoveIt = false;

		for( i = 0;i < iter->second.m_nListItemCount; i ++, pSrc ++ )
		{
			bRemoveIt = false;
			if( bRemoveFile )
			{
				if( 0 == (pSrc->dwAttributes & FILE_ATTRIBUTE_DIRECTORY ) )
				{// it is file
					bRemoveIt = true;
				}
			}
			else
			{
				if( 0 != (pSrc->dwAttributes & FILE_ATTRIBUTE_DIRECTORY ) )
				{// it is file
					bRemoveIt = true;
				}
			}
			if( bRemoveIt )
			{
				nNewCount --;
			}
			else
			{
				if( pDest != pSrc )
				{
					memcpy(pDest, pSrc, sizeof(GUEST_FILE_INFO) );
				}
				pDest ++;
			}
		}
		iter->second.m_nListItemCount = nNewCount;
	}
	else
	{
		iter->second.m_nListItemCount = 0;
	}

	return iter->second.m_nListItemCount;
}
int NativeClass::VixVM_CreateDirectoryInGuest(IN int vmHandle, const char *pathName, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData)
{
	jint rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	//log.writeDebugLog(_TEXT(__FUNCTION__) _T(":create directory in guest."));

	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			rc = -1;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			rc = -2;
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			rc = -3;
			break;
		}

		// int createDirectoryInGuest(int iSess, String pathInVM)
		jmethodID mid = env->GetMethodID(clsV2P, "createDirectoryInGuest", "(ILjava/lang/String;)I");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of createDirectoryInGuest"));
			rc = -4;
			break;
		}

		jstring pJStrGuest = CharToJString(env, pathName);
		// call createDirectoryInGuest
		rc = env->CallIntMethod(jvcloudInstance, mid, vmHandle, pJStrGuest);
		SafeDeleteLocalRef(env, pJStrGuest);
#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif
	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":create directory in guest. rc=%d [%S]"), rc, pathName);
	return rc;
}
int NativeClass::VixVM_DeleteDirectoryInGuest(IN int vmHandle, const char *pathName, int options, VixEventProc *callbackProc, void *clientData)
{
	jint rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	//log.writeDebugLog(_TEXT(__FUNCTION__) _T(":delete directory in guest."));

	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			rc = -1;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			rc = -2;
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			rc = -3;
			break;
		}

		// int deleteDirectoryInGuest(int iSess, String pathInVM)
		jmethodID mid = env->GetMethodID(clsV2P, "deleteDirectoryInGuest", "(ILjava/lang/String;)I");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of deleteDirectoryInGuest"));
			rc = -4;
			break;
		}

		jstring pJStrGuest = CharToJString(env, pathName);
		// call deleteDirectoryInGuest
		rc = env->CallIntMethod(jvcloudInstance, mid, vmHandle, pJStrGuest);
		SafeDeleteLocalRef(env, pJStrGuest);

#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif
	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":delete directory in guest. rc=%d [%S]"), rc, pathName );
	return rc;
}
int NativeClass::VixVM_DirectoryExistsInGuest(IN int vmHandle, const char *pathName, VixEventProc *callbackProc, void *clientData)
{
	int rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	//log.writeDebugLog(_TEXT(__FUNCTION__) _T(":directory exists in guest."));

	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			break;
		}

		// boolean directoryExistInGuest(int iSess, String pathInVM)
		jmethodID mid = env->GetMethodID(clsV2P, "directoryExistInGuest", "(ILjava/lang/String;)Z");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of directoryExistInGuest"));
			break;
		}

		jstring pJStrGuest = CharToJString(env, pathName);
		// call directoryExistInGuest
		jboolean brc = env->CallBooleanMethod(jvcloudInstance, mid, vmHandle, pJStrGuest);
		SafeDeleteLocalRef(env, pJStrGuest);

		if (brc)
		{
			rc = 1;
		}

		VMResult_Iterator iter = m_mapVMResult.find(vmHandle);
		if (m_mapVMResult.end() != iter)
		{
			iter->second.m_bResult = brc != 0 ? TRUE : FALSE;
		}
#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif
	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":directory exists in guest. rc=%d [%S]"), rc, pathName);
	return rc;
}
int NativeClass::VixJob_GetNumProperties(IN int vmHandle, int resultPropertyID)
{
	int rc = 0;
	VMResult_Iterator iter = m_mapVMResult.find(vmHandle);
	if (m_mapVMResult.end() != iter )
	{
		rc = iter->second.m_nListItemCount;
	}
	return rc;
}
int NativeClass::VixJob_GetNthProperties(IN int vmHandle, int index, int propertyID, va_list args)
{
	int nCount = 0;
	VMResult_Iterator iter = m_mapVMResult.find(vmHandle);
	if (m_mapVMResult.end() != iter )
	{
		nCount = iter->second.m_nListItemCount;
		if (index < nCount && index >= 0)
		{
			do
			{
				if (VixMgr_PROPERTY_JOB_RESULT_FILE_FLAGS == propertyID )
				{
					UINT64* pFlags = va_arg(args, UINT64*);
					*pFlags = 0;
					if (NULL != iter->second.m_pGuestFileInfo)
					{
						if ((iter->second.m_pGuestFileInfo + index)->dwAttributes & FILE_ATTRIBUTE_DIRECTORY)
						{
							*pFlags |= 1;// VixMgr_FILE_ATTRIBUTES_DIRECTORY = 0x0001
						}
						if ((iter->second.m_pGuestFileInfo + index)->dwAttributes & FILE_ATTRIBUTE_REPARSE_POINT)
						{
							*pFlags |= 2;// VixMgr_FILE_ATTRIBUTES_SYMLINK = 0x0002
						}
					}
				}
				else if (VixMgr_PROPERTY_JOB_RESULT_ITEM_NAME == propertyID )
				{
					char** ppPath = va_arg(args, char**);
					*ppPath = NULL;

					const wchar_t* pwszPath = NULL;
					if (NULL != iter->second.m_pGuestFileInfo)
					{
						pwszPath = (iter->second.m_pGuestFileInfo + index)->strPathName.c_str();
					}
					else if (NULL != iter->second.m_pGuestProcessInfo)
					{
						pwszPath = (iter->second.m_pGuestProcessInfo + index)->strName.c_str();
					}

					int ccPath = ::WideCharToMultiByte(CP_ACP, 0, pwszPath, -1, NULL, 0, NULL, NULL) + 1;

					*ppPath = (char*)malloc(ccPath);
					if (NULL != *ppPath)
					{
						::WideCharToMultiByte(CP_ACP, 0, pwszPath, -1, *ppPath, ccPath, NULL, NULL);
					}
				}
				else if (VixMgr_PROPERTY_JOB_RESULT_PROCESS_COMMAND == propertyID)
				{
					char** ppCmdline = va_arg(args, char**);
					*ppCmdline = NULL;

					const wchar_t* pwszCmdline = NULL;
					if (NULL != iter->second.m_pGuestProcessInfo)
					{
						pwszCmdline = (iter->second.m_pGuestProcessInfo + index)->cmdline.c_str();
					}

					int ccCmdline = ::WideCharToMultiByte(CP_ACP, 0, pwszCmdline, -1, NULL, 0, NULL, NULL) + 1;

					*ppCmdline = (char*)malloc(ccCmdline);
					if (NULL != *ppCmdline)
					{
						::WideCharToMultiByte(CP_ACP, 0, pwszCmdline, -1, *ppCmdline, ccCmdline, NULL, NULL);
					}
				}

				propertyID = va_arg(args, int);
			} while (VixMgr_PROPERTY_NONE != propertyID);
		}
	}
	return 0;
}
PGUEST_PROCESS_INFO NativeClass::VixVM_ListProcessesInGuest(IN int vmHandle, int options, VixEventProc *callbackProc, void *clientData)
{
	PGUEST_PROCESS_INFO pProcessInfo = NULL;
	jclass clsV2P = NULL;
	jclass clsProcessInfo = NULL;
	V2PNFlog log;
	//log.writeDebugLog(_TEXT(__FUNCTION__) _T(":list process in guest."));

	do
	{
		VMResult_Iterator iter = m_mapVMResult.find(vmHandle);
		if (m_mapVMResult.end() == iter )
		{
			break;
		}
		ClearVMResult( vmHandle );

		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		clsProcessInfo = env->FindClass("com/vmware/vim25/GuestProcessInfo;");
		if (clsV2P == NULL || NULL == clsProcessInfo)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			break;
		}
		jfieldID fieldName = env->GetFieldID(clsProcessInfo, "name", "Ljava/lang/String;");
		jfieldID fieldPid = env->GetFieldID(clsProcessInfo, "pid", "J");
		jfieldID fieldOwner = env->GetFieldID(clsProcessInfo, "owner", "Ljava/lang/String;");
		jfieldID fieldCmdLine = env->GetFieldID(clsProcessInfo, "cmdLine", "Ljava/lang/String;");
		jfieldID fieldExitCode = env->GetFieldID(clsProcessInfo, "exitCode", "Ljava/lang/Integer;");
		//jfieldID fieldStartTime = env->GetFieldID(clsProcessInfo, "startTime", "Ljavax/xml/datatype/XMLGregorianCalendar;");
		//jfieldID fieldEndTime = env->GetFieldID(clsProcessInfo, "endTime", "Ljavax/xml/datatype/XMLGregorianCalendar;");

		// public GuestProcessInfo[] listProcessInGuest(int iSess)
		jmethodID mid = env->GetMethodID(clsV2P, "listProcessInGuest", "(I)[Lcom/vmware/vim25/GuestProcessInfo;");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of listProcessInGuest"));
			break;
		}

		// call listProcessInGuest
		jobjectArray pJObjFileInfo = (jobjectArray)env->CallBooleanMethod(jvcloudInstance, mid, vmHandle);

		if (NULL == pJObjFileInfo)
		{
			break;
		}

		jsize nProcessCount = env->GetArrayLength(pJObjFileInfo);
		pProcessInfo = (PGUEST_PROCESS_INFO)new GUEST_PROCESS_INFO[nProcessCount];
		if (NULL == pProcessInfo)
		{
			break;
		}
		PGUEST_PROCESS_INFO pPI = pProcessInfo;
		for (jsize i = 0; i < nProcessCount; i++, pPI++)
		{
			jobject tempObj = env->GetObjectArrayElement(pJObjFileInfo, i);
			jboolean isProcessInfo = env->IsInstanceOf(tempObj, clsProcessInfo);
			if (!isProcessInfo)
			{
				log.writeDebugLog(_T("This is not a GuestProcessInfo object!"));
				break;
			}
			jstring jstrName = (jstring)env->GetObjectField(tempObj, fieldName);
			jlong jnPid = (jlong)env->GetObjectField(tempObj, fieldPid);
			jstring jstrOwner = (jstring)env->GetObjectField(tempObj, fieldOwner);
			jstring jstrCmdline = (jstring)env->GetObjectField(tempObj, fieldCmdLine);
			jint jnExitCode = (jint)env->GetObjectField(tempObj, fieldExitCode);
			//jstring jstrLastAccessTime = (jstring)env->GetObjectField(tempObj, )fieldStartTime;
			//jstring jstrLastAccessTime = (jstring)env->GetObjectField(tempObj, fieldEndTime);

			pPI->strName = JStringToWString(env, jstrName);
			pPI->pid = (DWORD)jnPid;
			pPI->owner = JStringToWString(env, jstrOwner);
			pPI->cmdline = JStringToWString(env, jstrCmdline);
			pPI->nExitCode = jnExitCode;
		}

		iter->second.m_pGuestProcessInfo = pProcessInfo;
		iter->second.m_nListItemCount = nProcessCount;

#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif

	} while (false);
#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":list all process in guest."));
	return pProcessInfo;
}

/// \fn VixMgr_Error VixJob_Wait( ... );
/// \brief
/// \param vmHandle that can be returned by calling function VixHost_Connect(), VixVM_Open(), VixVM_LoginInGuest(), ... any above function that return type is VixMgr_Handle.
int NativeClass::VixJob_Wait(IN int vmHandle, VixMgr_PropertyID firstPropertyID, ...)
{
	int rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	//log.writeDebugLog(_TEXT(__FUNCTION__) _T(":wait ..."));

	do
	{
		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			rc = -1;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			rc = -2;
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			rc = -3;
			break;
		}

		// int waitForProcessToExit(int iSess)
		jmethodID mid = env->GetMethodID(clsV2P, "waitForProcessToExit", "(I)I");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of waitForProcessToExit"));
			rc = -4;
			break;
		}

		// call directoryExistInGuest
		rc = env->CallIntMethod(jvcloudInstance, mid, vmHandle);

#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);
#endif
	} while (false);

#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif
	

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":wait. rc=%d"), rc);
	return rc;
}
int NativeClass::VixJob_CheckCompletion(IN int vmHandle, bool *complete)
{
	int rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;
	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":check program completion."));

	do
	{
		if (NULL == complete)
		{
			rc = -11;
			break;
		}
		*complete = false;

		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			rc = -1;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			rc = -2;
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			rc = -3;
			break;
		}

		// boolean isProcessExit(int iSess)
		jmethodID mid = env->GetMethodID(clsV2P, "isProcessExit", "(I)Z");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of isProcessExit"));
			rc = -4;
			break;
		}

		// call isProcessExit
		jboolean brc = env->CallBooleanMethod(jvcloudInstance, mid, vmHandle);
		if (brc)
		{
			*complete = true;
		}

#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);;
#endif


	} while (false);

#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);;
#else
	;
#endif
	

	log.writeDebugLog(_TEXT(__FUNCTION__) _T(":check program completion. rc=%d"), rc);
	return rc;
}
BOOL NativeClass::VixVM_GetResultBOOL(IN int vmHandle)
{
	BOOL rc = FALSE;
	VMResult_Iterator iter = m_mapVMResult.find(vmHandle);
	if (m_mapVMResult.end() != iter )
	{
		rc = iter->second.m_bResult;
	}
	return rc;
}
void NativeClass::VixVM_setResultBOOL(IN int vmHandle, IN BOOL bResult)
{
	VMResult_Iterator iter = m_mapVMResult.find(vmHandle);
	if (m_mapVMResult.end() != iter )
	{
		iter->second.m_bResult = bResult;
	}
}
int NativeClass::VixVM_GetLastError(IN int iHandle, OUT wchar_t* pszError, IN size_t ccError )
{
	int rc = 0;
	jclass clsV2P = NULL;
	V2PNFlog log;

	do
	{
		if (NULL == pszError
			|| ccError <= 0 )
		{
			rc = -11;
			break;
		}

		log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Attaching to JVM..."));
#ifdef __OLD_JNI_ENV__
		BOOL bRet = AttachToJVM();
#else
		JNIEnv * env = AttachToJVMThread();
#endif
#ifdef __OLD_JNI_ENV__
		if (!bRet)
#else
		if (!env)
#endif
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Failed to attach to JVM."));
			rc = -1;
			break;
		}

		if (jvcloudInstance == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T("Not connected yet."));
			rc = -2;
			break;
		}

		clsV2P = env->GetObjectClass(jvcloudInstance);
		if (clsV2P == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the requested class."));
			rc = -3;
			break;
		}

		// String getLastError(int iSess)
		jmethodID mid = env->GetMethodID(clsV2P, "getLastError", "(Ljava/lang/Object;)Ljava/lang/Object;");
		if (mid == NULL)
		{
			log.writeDebugLog(_TEXT(__FUNCTION__) _T(":Unable to find the method of getLastError"));
			rc = -4;
			break;
		}

		// call isProcessExit
		jstring jstrError = (jstring)env->CallObjectMethod(jvcloudInstance, mid, iHandle);
		const char *pszJsError = env->GetStringUTFChars(jstrError, NULL);
		::MultiByteToWideChar( CP_UTF8, 0, pszJsError, -1, pszError, ccError );
		env->ReleaseStringUTFChars(jstrError, pszJsError);
#ifdef __OLD_JNI_ENV__
		;
#else
		SafeDeleteLocalRef(env, clsV2P);;
#endif
	} while (false);

#ifdef __OLD_JNI_ENV__
	SafeDeleteLocalRef(env, clsV2P);
#else
	;
#endif
	

	return rc;
}
/// VIX is replaced by vSphere  -- end --

