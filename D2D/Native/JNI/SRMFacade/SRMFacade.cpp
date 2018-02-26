// SRMFacade.cpp : Defines the entry point for the DLL application.
//

#include "stdafx.h"
#include "SRM.h"
#include "Help.h"
#include "SRMAgent.h"
#include "SPLogger.h"


#ifdef _MANAGED
#pragma managed(push, off)
#endif

bool BuildAlertCfgStruct(JNIEnv *env, jobject obj, PKIAlertStruct *pkiAlertStruct, 
						 unsigned long *updateTime, SRMValidation *srmValidation);

const wstring c_module_log_name = L"srmfacade";
wstring g_module_dir;

BOOL APIENTRY DllMain( HMODULE hModule,
                       DWORD  ul_reason_for_call,
                       LPVOID lpReserved
					 )
{
    switch( ul_reason_for_call )
    {
    case DLL_PROCESS_ATTACH:
		{
			SET_SPLOGGER_MODULE(const_cast<wchar_t *>(c_module_log_name.c_str()));
			SET_SPLOGGER_LEVEL(LOG_LEVEL_INFO);

			WCHAR filePath[MAX_PATH] = L".";

			if ( GetModuleFileName(hModule, filePath, _countof(filePath)) == 0 )
			{
			    LOG_ERRORW(L"Failed to get SRMFacade.dll path!");
			}
			else
			{
			    PWCHAR pos = wcsrchr(filePath, L'\\');
				if ( pos != NULL )
				{
				    *(pos+1) = '\0';
				}
				g_module_dir.assign(filePath);
			}
            SetDllDirectory(filePath);
		}
        
        break;

    case DLL_THREAD_ATTACH:
        // Do thread-specific initialization.
        break;

    case DLL_THREAD_DETACH:
        // Do thread-specific cleanup.
        break;

    case DLL_PROCESS_DETACH:
        // Perform any necessary process-specific cleanup.
        break;
    }

    return TRUE;
}


/************************************************************************/
/* JNI Interface, collect system hardware information
/* RETURN:
/* If the function return not null, means invoke succeed; else invoke failed. 
/************************************************************************/
jstring JNICALL Java_com_ca_arcflash_webservice_edge_srmagent_SrmJniCaller_getSysHardwareInfo
(JNIEnv * env, jclass obj)
{
    wstring xmlBuf;
    int     xmlBufSize = 0;
    SRMAgent agent;
    if (agent.GetHardwareInfo(xmlBuf, &xmlBufSize)) {
        return (*env).NewString((jchar *)xmlBuf.c_str(), (int)xmlBuf.length());
    }
    return NULL;
}


/************************************************************************/
/* JNI Interface, collect system software information
/* RETURN:
/* If the function return not null, means invoke succeed; else invoke failed. 
/************************************************************************/
jstring JNICALL Java_com_ca_arcflash_webservice_edge_srmagent_SrmJniCaller_getSysSoftwareInfo
(JNIEnv *env, jclass obj)
{
    wstring xmlBuf;
    int     xmlBufSize = 0;
    SRMAgent agent;
    if (agent.GetSoftwareInfo(xmlBuf, &xmlBufSize)) {
        return (*env).NewString((jchar *)xmlBuf.c_str(), (int)xmlBuf.length());
    }
    return NULL;
}

/************************************************************************/
/* JNI Interface, collect system software information
/* ARGS:
/* intervalInHour - indicate collect data by each X hours 
/* RETURN:
/* If the function return not null, means invoke succeed; else invoke failed. 
/************************************************************************/
jstring JNICALL Java_com_ca_arcflash_webservice_edge_srmagent_SrmJniCaller_getServerPkiInfo
(JNIEnv * env, jclass obj, jint intervaInHour)
{
    wstring xmlBuf;
    int     xmlBufSize = 0;
    SRMAgent agent;
    if (agent.GetServerPkiInfo(xmlBuf, &xmlBufSize, intervaInHour)) {
        return (*env).NewString((jchar *)xmlBuf.c_str(), (int)xmlBuf.length());
    }
    return NULL;
}

/************************************************************************/
/* JNI Interface, check the environment whether installed ARCserve client
/* agent
/* RETURN:
/* If the function return true, means ARCserve client installed; else no
/* ARCserve client
/************************************************************************/
jboolean JNICALL Java_com_ca_arcflash_webservice_edge_srmagent_SrmJniCaller_isARCInstalled
(JNIEnv *, jclass)
{
    SRMAgent agent;
    return agent.IsARCInstalled();
}


/************************************************************************/
/* JNI Interface, start AgPkiMon.exe to monitor server performance data.
/* RETURN:
/* If the function return true, means AgPkiMon.exe start succeed; else
/* run AgPkiMon.exe failed.
/************************************************************************/
jboolean JNICALL Java_com_ca_arcflash_webservice_edge_srmagent_SrmJniCaller_startPkiMonitor
(JNIEnv *, jclass)
{
    SRMAgent agent;
    return agent.StartPkiMonitor();
}

/************************************************************************/
/* JNI Interface, stop AgPkiMon.exe process.
/* RETURN:
/* If the function return true, means AgPkiMon.exe is stopped; else
/* stop operation failed.
/************************************************************************/
jboolean JNICALL Java_com_ca_arcflash_webservice_edge_srmagent_SrmJniCaller_stopPkiMonitor
(JNIEnv *, jclass)
{
    SRMAgent agent;
    return agent.StopPkiMonitor();
}

/************************************************************************/
/* JNI Interface, stop AgPkiMon.exe process.
/* RETURN:
/* If the function return true, means AgPkiMon.exe is stopped; else
/* stop operation failed.
/************************************************************************/
jboolean JNICALL Java_com_ca_arcflash_webservice_edge_srmagent_SrmJniCaller_isSRMEnabled
(JNIEnv *, jclass)
{
    SRMAgent agent;
	return agent.IsSRMEnabled();
}

/************************************************************************/
/* JNI Interface, set pki alert configurations.
/* RETURN: 0 - success 
/*        <0 - failed
/************************************************************************/
jint JNICALL Java_com_ca_arcflash_webservice_edge_srmagent_SrmJniCaller_savePkiAlertPolicy
(JNIEnv *env, jclass cls, jobject obj)
{
	PKIAlertStruct pkiAlertStruct = {0};
	SRMValidation srmValidation = {0};
	unsigned long updateTime = 0;
	if ( BuildAlertCfgStruct(env, obj, &pkiAlertStruct, &updateTime, &srmValidation) )
	{
		SRMAgent agent;
		return agent.savePkiAlertSetting(&pkiAlertStruct, updateTime, &srmValidation);
	}
	
    return -1;
}

/************************************************************************/
/* JNI Interface, set pki alert configurations.
/* RETURN: >=0 - the real count of alert records gotten by this API
/*         <0 - failed
/************************************************************************/
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_edge_srmagent_SrmJniCaller_getAlertRecords
	(JNIEnv *env, jclass, jintArray alertTypes, jobjectArray alertHeaders, 
	jintArray alertThresholds, jintArray alertCurUtils, jint alertRecords)
{
	if ( alertRecords <= 0 )
		return 0;

	int ret = -1;
	SRMAgent agent;
	int *alertType = new int [alertRecords];
	int *threshold = new int[alertRecords];
	int *curUtil = new int[alertRecords];
	PWCHAR *alertHeader = new PWCHAR [alertRecords];

	if ( alertType == NULL || threshold == NULL || curUtil == NULL || alertHeaders == NULL )
	{
	    LOG_ERRORW(L"getAlertRecords - Failed to allocate memory!");
		goto Clear;
	}

	for ( int i = 0; i < alertRecords; ++i )
	{
	    *(alertHeader+i) = new WCHAR[MAX_PATH];
		if ( *(alertHeader+i) == NULL )
		{
		    LOG_ERRORW(L"getAlertRecords - Failed to allocate memory for alert header (%d)!", i);
		    goto Clear;
		}
	}

	ret = agent.GetAlertRecords(alertType, alertHeader, MAX_PATH, threshold, curUtil, alertRecords);

	if ( ret > 0 )
	{
		for ( int i = 0; i < ret; ++i )
		{
			env->SetIntArrayRegion( alertTypes, i, 1, (jint*)(alertType+i) );
			env->SetObjectArrayElement( alertHeaders, i, env->NewString((jchar*)(*(alertHeader+i)), (jsize)wcslen(*(alertHeader+i))) );
			env->SetIntArrayRegion(alertThresholds, i, 1, (jint*)(threshold+i));
			env->SetIntArrayRegion(alertCurUtils, i, 1, (jint*)(curUtil+i));
		}
	}
	
Clear:
	if ( alertType != NULL )
	    delete [] alertType;

	if ( threshold != NULL )
		delete [] threshold;

	if ( curUtil != NULL )
		delete [] curUtil;

	if ( alertHeader != NULL )
	{
	    for ( int i = 0; i < alertRecords; ++i )
		{
		   if ( *(alertHeader+i) != NULL )
			   delete [] *(alertHeader+i);
		}

		delete [] alertHeader;
	}
		
	return ret;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_edge_srmagent_SrmJniCaller_enableAlert
	(JNIEnv *env, jclass cls, jboolean enable)
{
    SRMAgent agent;
	return agent.EnableAlert(enable);
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_edge_srmagent_SrmJniCaller_enablePkiUtl
(JNIEnv *env, jclass cls, jboolean enable)
{
    SRMAgent agent;
    return agent.EnablePkiUtl(enable);
}

static bool BuildAlertCfgStruct(JNIEnv *env, jobject obj, 
								PKIAlertStruct *pkiAlertStruct, 
								unsigned long *updateTime, 
								SRMValidation *srmValidation)
{
	if ( env == NULL || obj == NULL )
	{
	    return false;
	}

	try 
	{
		jclass alertSetting = env->GetObjectClass(obj);

		if ( pkiAlertStruct != NULL )
		{
			//The fields for pkiAlertStruct
			jfieldID cpuinterval = env->GetFieldID(alertSetting, "cpuinterval", "I");
			jfieldID cputhreshold = env->GetFieldID(alertSetting, "cputhreshold", "I");
			jfieldID cpusampleamount = env->GetFieldID(alertSetting, "cpusampleamount", "I");
			jfieldID cpumaxalertnum = env->GetFieldID(alertSetting, "cpumaxalertnum", "I");

			jfieldID memoryinterval = env->GetFieldID(alertSetting, "memoryinterval", "I");
			jfieldID memorythreshold = env->GetFieldID(alertSetting, "memorythreshold", "I");
			jfieldID memorysampleamount = env->GetFieldID(alertSetting, "memorysampleamount", "I");
			jfieldID memorymaxalertnum = env->GetFieldID(alertSetting, "memorymaxalertnum", "I");

			jfieldID diskinterval = env->GetFieldID(alertSetting, "diskinterval", "I");
			jfieldID diskthreshold = env->GetFieldID(alertSetting, "diskthreshold", "I");
			jfieldID disksampleamount = env->GetFieldID(alertSetting, "disksampleamount", "I");
			jfieldID diskmaxalertnum = env->GetFieldID(alertSetting, "diskmaxalertnum", "I");

			jfieldID networkinterval = env->GetFieldID(alertSetting, "networkinterval", "I");
			jfieldID networkthreshold = env->GetFieldID(alertSetting, "networkthreshold", "I");
			jfieldID networksampleamount = env->GetFieldID(alertSetting, "networksampleamount", "I");
			jfieldID networkmaxalertnum = env->GetFieldID(alertSetting, "networkmaxalertnum", "I");

			pkiAlertStruct->nCPUInterval = env->GetIntField(obj, cpuinterval);
			pkiAlertStruct->nCPUThreshold = env->GetIntField(obj, cputhreshold);
			pkiAlertStruct->nCPUSamplingAmount = env->GetIntField(obj, cpusampleamount);
			pkiAlertStruct->nCPUMaxAlertNum = env->GetIntField(obj, cpumaxalertnum);

			pkiAlertStruct->nMemoryInterval = env->GetIntField(obj, memoryinterval);
			pkiAlertStruct->nMemoryThreshold = env->GetIntField(obj, memorythreshold);
			pkiAlertStruct->nMemorySamplingAmount = env->GetIntField(obj, memorysampleamount);
			pkiAlertStruct->nMemoryMaxAlertNum = env->GetIntField(obj, memorymaxalertnum);

			pkiAlertStruct->nDiskInterval = env->GetIntField(obj, diskinterval);
			pkiAlertStruct->nDiskThreshold = env->GetIntField(obj, diskthreshold);
			pkiAlertStruct->nDiskSamplingAmount = env->GetIntField(obj, disksampleamount);
			pkiAlertStruct->nDiskMaxAlertNum = env->GetIntField(obj, diskmaxalertnum);

			pkiAlertStruct->nNetworkInterval = env->GetIntField(obj, networkinterval);
			pkiAlertStruct->nNetworkThreshold = env->GetIntField(obj, networkthreshold);
			pkiAlertStruct->nNetworkSamplingAmount = env->GetIntField(obj, networksampleamount);
			pkiAlertStruct->nNetworkMaxAlertNum = env->GetIntField(obj, networkmaxalertnum);
		}

		if ( updateTime != NULL )
		{
			jfieldID updatetime = env->GetFieldID(alertSetting, "updatetime", "J");
			*updateTime = (ULONG)(env->GetLongField(obj, updatetime));
		}

		if ( srmValidation != NULL )
		{
			//The fields for SRMValidation
			jfieldID validsrm = env->GetFieldID(alertSetting, "validsrm", "Z");
			jfieldID validalert = env->GetFieldID(alertSetting, "validalert", "Z");
			jfieldID validpkiutl = env->GetFieldID(alertSetting, "validpkiutl", "Z");

			srmValidation->nAlertEnabled = (int)(env->GetBooleanField(obj, validalert));
			srmValidation->nPKIUtlEnabled = (int)(env->GetBooleanField(obj, validpkiutl));
			srmValidation->nSRMEnabled = (int)(env->GetBooleanField(obj, validsrm));
		}

		env->DeleteLocalRef(alertSetting);
		return true;
	}
	catch(exception e)
	{
	    return false;
	}
}

#ifdef _MANAGED
#pragma managed(pop)
#endif

